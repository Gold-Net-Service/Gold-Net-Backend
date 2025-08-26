package com.example.GOLDNet.service;

import com.example.GOLDNet.domain.JobPosting;
import com.example.GOLDNet.domain.Resume;
import com.example.GOLDNet.dto.JobRecommendationResponse;
import com.example.GOLDNet.repository.JobPostingRepository;
import com.example.GOLDNet.repository.ResumeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationService {
    
    private final ResumeRepository resumeRepository;
    private final JobPostingRepository jobPostingRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Value("${openai.api.key}")
    private String openaiApiKey;
    
    @Value("${openai.api.url:https://api.openai.com/v1/chat/completions}")
    private String openaiApiUrl;
    
    public List<JobRecommendationResponse> getJobRecommendations(Long memberId, int limit) {
        Resume resume = resumeRepository.findByMemberId(memberId)
                .orElseThrow(() -> new RuntimeException("Resume not found for member: " + memberId));
        
        List<JobPosting> allJobPostings = jobPostingRepository.findAll();
        
        return calculateRecommendations(resume, allJobPostings, limit);
    }
    
    public List<JobRecommendationResponse> refreshJobRecommendations(Long memberId, int limit) {
        return getJobRecommendations(memberId, limit);
    }
    
    private List<JobRecommendationResponse> calculateRecommendations(
            Resume resume, List<JobPosting> jobPostings, int limit) {
        
        try {
            List<Long> recommendedJobIds = getRecommendedJobIdsFromGPT(resume, jobPostings, limit);
            
            Map<Long, JobPosting> jobPostingMap = jobPostings.stream()
                    .collect(Collectors.toMap(JobPosting::getId, jp -> jp));
            
            List<JobRecommendationResponse> recommendations = new ArrayList<>();
            double baseScore = 95.0;
            
            for (Long jobId : recommendedJobIds) {
                JobPosting jobPosting = jobPostingMap.get(jobId);
                if (jobPosting != null) {
                    recommendations.add(JobRecommendationResponse.from(jobPosting, baseScore));
                    baseScore = Math.max(75.0, baseScore - 5.0);
                }
            }
            
            return recommendations;
            
        } catch (Exception e) {
            log.error("GPT recommendation failed, falling back to simple matching: ", e);
            return calculateSimpleRecommendations(resume, jobPostings, limit);
        }
    }
    
    private List<Long> getRecommendedJobIdsFromGPT(Resume resume, List<JobPosting> jobPostings, int limit) {
        String prompt = buildBatchRecommendationPrompt(resume, jobPostings, limit);
        String gptResponse = callGPTApi(prompt);
        return parseRecommendedJobIds(gptResponse);
    }
    
    private String buildBatchRecommendationPrompt(Resume resume, List<JobPosting> jobPostings, int limit) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("다음 이력서를 분석하고, 제공된 채용공고 목록에서 가장 적합한 ").append(limit).append("개를 추천해주세요.\n\n");
        
        prompt.append("[이력서]\n");
        prompt.append("- 학력: ").append(resume.getEducation()).append("\n");
        prompt.append("- 경력: ").append(resume.getExperience()).append("\n");
        prompt.append("- 기술: ").append(resume.getSkills()).append("\n");
        prompt.append("- 희망 조건: ").append(resume.getPreferredConditions()).append("\n");
        prompt.append("- 자격증: ").append(resume.getLicensesAbilities()).append("\n");
        prompt.append("- 강점: ").append(resume.getStrengths()).append("\n");
        prompt.append("- MBTI: ").append(resume.getMbti()).append("\n\n");
        
        prompt.append("[채용공고 목록]\n");
        for (JobPosting job : jobPostings) {
            prompt.append("ID: ").append(job.getId()).append("\n");
            prompt.append("- 직무: ").append(job.getTitle()).append("\n");
            prompt.append("- 분야: ").append(job.getCategory()).append("\n");
            prompt.append("- 업종: ").append(job.getIndustry()).append("\n");
            prompt.append("- 근무지: ").append(job.getLocation()).append("\n");
            prompt.append("- 급여: ").append(job.getSalaryInfo()).append("\n");
            prompt.append("- 근무일: ").append(job.getWorkDays()).append("\n");
            prompt.append("- 근무시간: ").append(job.getWorkHours()).append("\n");
            prompt.append("- 채용조건: ").append(truncateText(job.getRecruitmentConditions(), 200)).append("\n");
            prompt.append("---\n");
        }
        
        prompt.append("\n가장 적합한 순서대로 채용공고 ID를 콤마로 구분하여 답변해주세요. (예: 1,5,3,7,2)\n");
        prompt.append("ID만 답변하고 다른 설명은 하지 마세요.");
        
        return prompt.toString();
    }
    
    private String truncateText(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength) + "...";
    }
    
    private List<Long> parseRecommendedJobIds(String gptResponse) {
        try {
            String cleanedResponse = gptResponse.trim().replaceAll("[^0-9,]", "");
            String[] idStrings = cleanedResponse.split(",");
            List<Long> jobIds = new ArrayList<>();
            
            for (String idStr : idStrings) {
                if (!idStr.isEmpty()) {
                    jobIds.add(Long.parseLong(idStr.trim()));
                }
            }
            
            return jobIds;
        } catch (Exception e) {
            log.error("Failed to parse GPT response: " + gptResponse, e);
            throw new RuntimeException("Failed to parse GPT response", e);
        }
    }
    
    private List<JobRecommendationResponse> calculateSimpleRecommendations(
            Resume resume, List<JobPosting> jobPostings, int limit) {
        
        Map<JobPosting, Double> scoreMap = new HashMap<>();
        
        for (JobPosting jobPosting : jobPostings) {
            double score = calculateSimpleMatchingScore(resume, jobPosting);
            scoreMap.put(jobPosting, score);
        }
        
        return scoreMap.entrySet().stream()
                .sorted(Map.Entry.<JobPosting, Double>comparingByValue().reversed())
                .limit(limit)
                .map(entry -> JobRecommendationResponse.from(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }
    
    private String callGPTApi(String prompt) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(openaiApiKey);
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "gpt-3.5-turbo");
            requestBody.put("messages", Arrays.asList(
                Map.of("role", "system", "content", "당신은 채용 매칭 전문가입니다. 이력서와 채용공고를 분석하여 매칭 점수를 계산합니다."),
                Map.of("role", "user", "content", prompt)
            ));
            requestBody.put("temperature", 0.3);
            requestBody.put("max_tokens", 100);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            Map response = restTemplate.postForObject(openaiApiUrl, request, Map.class);
            List<Map> choices = (List<Map>) response.get("choices");
            Map message = (Map) choices.get(0).get("message");
            
            return (String) message.get("content");
        } catch (Exception e) {
            log.error("GPT API call failed: ", e);
            throw new RuntimeException("GPT API call failed", e);
        }
    }
    
    private double parseMatchingScore(String gptResponse) {
        try {
            String cleanedResponse = gptResponse.replaceAll("[^0-9.]", "");
            double score = Double.parseDouble(cleanedResponse);
            return Math.min(100, Math.max(0, score));
        } catch (NumberFormatException e) {
            log.warn("Failed to parse GPT score: " + gptResponse);
            return 50.0;
        }
    }
    
    private double calculateSimpleMatchingScore(Resume resume, JobPosting jobPosting) {
        double score = 50.0;
        
        if (resume.getSkills() != null && jobPosting.getRecruitmentConditions() != null) {
            String[] resumeSkills = resume.getSkills().toLowerCase().split(",");
            String jobConditions = jobPosting.getRecruitmentConditions().toLowerCase();
            
            for (String skill : resumeSkills) {
                if (jobConditions.contains(skill.trim())) {
                    score += 5;
                }
            }
        }
        
        if (resume.getPreferredConditions() != null && jobPosting.getLocation() != null) {
            if (resume.getPreferredConditions().contains(jobPosting.getLocation())) {
                score += 10;
            }
        }
        
        if (resume.getExperience() != null && !resume.getExperience().isEmpty()) {
            score += 10;
        }
        
        return Math.min(100, score);
    }
}
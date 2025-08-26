package com.example.GOLDNet.dto;

import com.example.GOLDNet.domain.JobPosting;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JobRecommendationResponse {
    private Long jobPostingId;
    private String title;
    private String category;
    private String brandName;
    private String location;
    private String salaryInfo;
    private String workDays;
    private String workHours;
    private String industry;
    private String recruitmentConditions;
    private String detailedDescription;
    private double matchingScore;
    private String matchingReason;
    
    public static JobRecommendationResponse from(JobPosting jobPosting, double matchingScore) {
        return JobRecommendationResponse.builder()
                .jobPostingId(jobPosting.getId())
                .title(jobPosting.getTitle())
                .category(jobPosting.getCategory())
                .brandName(jobPosting.getBrandName())
                .location(jobPosting.getLocation())
                .salaryInfo(jobPosting.getSalaryInfo())
                .workDays(jobPosting.getWorkDays())
                .workHours(jobPosting.getWorkHours())
                .industry(jobPosting.getIndustry())
                .recruitmentConditions(jobPosting.getRecruitmentConditions())
                .detailedDescription(jobPosting.getDetailedDescription())
                .matchingScore(matchingScore)
                .matchingReason(generateMatchingReason(matchingScore))
                .build();
    }
    
    private static String generateMatchingReason(double score) {
        if (score >= 80) {
            return "매우 적합한 공고입니다. 귀하의 경력과 기술이 이 포지션과 높은 매칭도를 보입니다.";
        } else if (score >= 60) {
            return "적합한 공고입니다. 주요 요구사항과 귀하의 프로필이 잘 맞습니다.";
        } else if (score >= 40) {
            return "고려해볼만한 공고입니다. 일부 요구사항이 맞지만 추가 준비가 필요할 수 있습니다.";
        } else {
            return "매칭도가 낮지만, 관심이 있다면 지원을 고려해보실 수 있습니다.";
        }
    }
}
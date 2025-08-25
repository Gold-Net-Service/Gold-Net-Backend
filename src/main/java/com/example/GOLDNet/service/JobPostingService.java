package com.example.GOLDNet.service;

import com.example.GOLDNet.domain.JobPosting;
import com.example.GOLDNet.repository.JobPostingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JobPostingService {

    private final JobPostingRepository jobPostingRepository;
    private final JobPostingAiParser aiParser; // AI 파서 주입


    @Transactional
    public void processAndSaveCrawledData(List<Map<String, String>> crawledDataList) {
        List<JobPosting> jobPostingsToSave = new ArrayList<>();

        for (Map<String, String> crawledData : crawledDataList) {
            try {
                // 각 데이터 조각을 AI 파서에 보내 JobPosting 객체로 변환
                JobPosting jobPosting = aiParser.parse(crawledData);
                jobPostingsToSave.add(jobPosting);
            } catch (Exception e) {
                // 특정 공고 파싱에 실패하더라도 전체 작업이 중단되지 않도록 로그만 남기고 계속 진행
                System.err.println("하나의 공고를 파싱하는 데 실패했습니다: " + crawledData.get("상세내용"));
                e.printStackTrace();
            }
        }

        // 성공적으로 파싱된 공고들을 DB에 저장
        if (!jobPostingsToSave.isEmpty()) {
            jobPostingRepository.saveAll(jobPostingsToSave);
            System.out.println(jobPostingsToSave.size() + "개의 공고가 성공적으로 저장되었습니다.");
        }
    }
}
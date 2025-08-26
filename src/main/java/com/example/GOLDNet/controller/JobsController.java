package com.example.GOLDNet.controller;

import com.example.GOLDNet.domain.JobPosting;
import com.example.GOLDNet.dto.JobPostingResponse;
import com.example.GOLDNet.service.CrawlerService;
import com.example.GOLDNet.service.JobPostingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/postings")
public class JobsController {
    private final CrawlerService crawlerService;
    private final JobPostingService jobPostingService;
    private final ObjectMapper objectMapper;

    @GetMapping("/crawl")
    public List<Map<String, String>> crawlJobs() {
        return crawlerService.crawlJobs();
    }

    @PostMapping("/crawl-and-save")
    public ResponseEntity<String> crawlAndSaveJobs() {
        try {
            System.out.println("크롤링을 시작합니다...");
            List<Map<String, String>> crawledResults = crawlerService.crawlJobs();

            System.out.println("크롤링된 데이터를 AI로 파싱하고 저장합니다...");
            jobPostingService.processAndSaveCrawledData(crawledResults);

            System.out.println("모든 작업이 완료되었습니다.");
            return ResponseEntity.ok("Crawling, AI parsing, and saving completed successfully.");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("An error occurred: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<JobPostingResponse>> getJobPostings(){
        return ResponseEntity.ok(jobPostingService.getJobPostings());
    }
}

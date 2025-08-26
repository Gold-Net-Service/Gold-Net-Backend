package com.example.GOLDNet.controller;

import com.example.GOLDNet.dto.JobRecommendationResponse;
import com.example.GOLDNet.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {
    
    private final RecommendationService recommendationService;
    
    @GetMapping("/jobs")
    public ResponseEntity<List<JobRecommendationResponse>> getJobRecommendations(
            @RequestParam Long memberId,
            @RequestParam(defaultValue = "10") int limit) {
        List<JobRecommendationResponse> recommendations = 
            recommendationService.getJobRecommendations(memberId, limit);
        return ResponseEntity.ok(recommendations);
    }
    
    @PostMapping("/jobs/refresh")
    public ResponseEntity<List<JobRecommendationResponse>> refreshJobRecommendations(
            @RequestParam Long memberId,
            @RequestParam(defaultValue = "10") int limit) {
        List<JobRecommendationResponse> recommendations = 
            recommendationService.refreshJobRecommendations(memberId, limit);
        return ResponseEntity.ok(recommendations);
    }
}
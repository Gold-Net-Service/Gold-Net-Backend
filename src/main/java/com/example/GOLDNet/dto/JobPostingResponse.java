package com.example.GOLDNet.dto;

public record JobPostingResponse(
        Long id,
        String title,
        String category,
        String salaryInfo,
        String location,
        String preferredAgeGroup,
        String industry,
        String brandName,
        String workDays,
        String workHours,
        String recruitmentConditions,
        String workRegion,
        String detailedDescription
) {
}

package com.example.GOLDNet.dto;

public record ResumeUpsertRequest(
        String education,
        String experience,
        String preferredConditions,
        String selfIntroduction,
        String skills,
        String strengths,
        String mbti,
        String licensesAbilities,
        String portfolioUrls,
        String preferentialTreatment
) {
}

package com.example.GOLDNet.dto;

import com.example.GOLDNet.domain.Resume;

public record ResumeResponse(
        Long memberId,
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
    // 이력서가 있을 때
    public static ResumeResponse from(Long memberId, Resume resume) {
        return new ResumeResponse(
                memberId,
                resume.getEducation(),
                resume.getExperience(),
                resume.getPreferredConditions(),
                resume.getSelfIntroduction(),
                resume.getSkills(),
                resume.getStrengths(),
                resume.getMbti(),
                resume.getLicensesAbilities(),
                resume.getPortfolioUrls(),
                resume.getPreferentialTreatment()
        );
    }

    // 이력서가 없을 때
    public static ResumeResponse empty(Long memberId) {
        return new ResumeResponse(
                memberId,
                null, null, null, null, null,
                null, null, null, null, null
        );
    }
}

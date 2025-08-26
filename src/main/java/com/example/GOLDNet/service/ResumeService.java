package com.example.GOLDNet.service;

import com.example.GOLDNet.domain.Member;
import com.example.GOLDNet.domain.Resume;
import com.example.GOLDNet.dto.ResumeUpsertRequest;
import com.example.GOLDNet.dto.ResumeResponse;
import com.example.GOLDNet.repository.MemberRepository;
import com.example.GOLDNet.repository.ResumeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ResumeService {
    private final ResumeRepository resumeRepository;
    private final MemberRepository memberRepository;

    public ResumeResponse getResume(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found: " + memberId));
        return resumeRepository.findResumeByMember(member)
                .map(resume -> ResumeResponse.from(memberId, resume))
                .orElse(ResumeResponse.empty(memberId));
    }

    public ResumeResponse upsertResume(Long memberId, ResumeUpsertRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found: " + memberId));

        Optional<Resume> existingResumeOpt = resumeRepository.findResumeByMember(member);

        Resume resume;
        if (existingResumeOpt.isPresent()) {
            // 기존 이력서 업데이트
            resume = existingResumeOpt.get();
            resume.updateResume(
                    request.education(),
                    request.experience(),
                    request.preferredConditions(),
                    request.selfIntroduction(),
                    request.skills(),
                    request.strengths(),
                    request.mbti(),
                    request.licensesAbilities(),
                    request.portfolioUrls(),
                    request.preferentialTreatment()
            );
        } else {
            // 새 이력서 생성
            resume = Resume.builder()
                    .member(member)
                    .education(request.education())
                    .experience(request.experience())
                    .preferredConditions(request.preferredConditions())
                    .selfIntroduction(request.selfIntroduction())
                    .skills(request.skills())
                    .strengths(request.strengths())
                    .mbti(request.mbti())
                    .licensesAbilities(request.licensesAbilities())
                    .portfolioUrls(request.portfolioUrls())
                    .preferentialTreatment(request.preferentialTreatment())
                    .build();
        }

        Resume savedResume = resumeRepository.save(resume);

        return ResumeResponse.from(memberId, savedResume);
    }
}

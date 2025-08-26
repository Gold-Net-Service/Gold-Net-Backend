package com.example.GOLDNet.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "resumes")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Resume {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resume_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(length = 255)
    private String education; //학력

    @Lob
    private String experience; //경험

    @Lob
    private String preferredConditions; //희망 근무 조건

    @Lob
    private String selfIntroduction; //자기소개

    @Lob
    private String skills; //스킬 (지게차 운전, 재고 관리 시스템 사용 )

    @Lob
    private String strengths; //성격 강점

    @Column(length = 10)
    private String mbti;

    @Lob
    private String licensesAbilities; //자등증

    @Lob
    private String portfolioUrls; //포트폴리오

    @Column(length = 255)
    private String preferentialTreatment; //취업 우대사항(국가유공자 자녀 등)


    @Builder
    public Resume(Member member, String education, String experience,
                  String preferredConditions, String selfIntroduction, String skills,
                  String strengths, String mbti, String licensesAbilities,
                  String portfolioUrls, String preferentialTreatment) {
        this.member = member;
        this.education = education;
        this.experience = experience;
        this.preferredConditions = preferredConditions;
        this.selfIntroduction = selfIntroduction;
        this.skills = skills;
        this.strengths = strengths;
        this.mbti = mbti;
        this.licensesAbilities = licensesAbilities;
        this.portfolioUrls = portfolioUrls;
        this.preferentialTreatment = preferentialTreatment;
    }

    // 업데이트 메서드
    public void updateResume(String education, String experience, String preferredConditions,
                             String selfIntroduction, String skills, String strengths,
                             String mbti, String licensesAbilities, String portfolioUrls,
                             String preferentialTreatment) {
        this.education = education;
        this.experience = experience;
        this.preferredConditions = preferredConditions;
        this.selfIntroduction = selfIntroduction;
        this.skills = skills;
        this.strengths = strengths;
        this.mbti = mbti;
        this.licensesAbilities = licensesAbilities;
        this.portfolioUrls = portfolioUrls;
        this.preferentialTreatment = preferentialTreatment;
    }
}

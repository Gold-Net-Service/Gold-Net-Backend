package com.example.GOLDNet.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
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
}

package com.example.GOLDNet.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
@AllArgsConstructor
@Table(name = "job_postings")
public class JobPosting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_posting_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String category;

    @Column(length = 100)
    private String salaryInfo;

    private String location; //구 단위

    @Column(length = 50)
    private String preferredAgeGroup;

    @OneToMany(mappedBy = "jobPosting", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SavedJobPosting> savedJobPostings = new ArrayList<>();

    // --- 근무 조건 ---
    @Column(length = 100)
    private String industry; //업직종

    @Column(length = 100)
    private String brandName; //브랜드

    @Column(length = 50)
    private String workDays; //근무요일

    @Column(length = 50)
    private String workHours; //근무시간

    // --- 모집 조건 ---
    @Lob
    private String recruitmentConditions; //모집 조건

    @Column(length = 100)
    private String workRegion; //근무 상세 지역

    @Lob
    private String detailedDescription; //상세 요강
}

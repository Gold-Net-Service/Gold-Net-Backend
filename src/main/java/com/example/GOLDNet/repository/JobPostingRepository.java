package com.example.GOLDNet.repository;

import com.example.GOLDNet.domain.JobPosting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobPostingRepository extends JpaRepository<JobPosting, Integer> {
}

package com.example.GOLDNet.repository;

import com.example.GOLDNet.domain.Member;
import com.example.GOLDNet.domain.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResumeRepository extends JpaRepository<Resume, Integer> {
    Optional<Resume> findResumeByMember(Member member);
}

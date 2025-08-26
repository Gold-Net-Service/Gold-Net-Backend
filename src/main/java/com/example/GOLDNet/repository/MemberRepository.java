package com.example.GOLDNet.repository;

import com.example.GOLDNet.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Integer> {
    Optional<Member> findById(Long id);
}

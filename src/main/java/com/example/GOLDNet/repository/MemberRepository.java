package com.example.GOLDNet.repository;

import com.example.GOLDNet.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Integer> {
}

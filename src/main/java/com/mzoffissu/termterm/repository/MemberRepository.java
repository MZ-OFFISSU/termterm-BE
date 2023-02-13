package com.mzoffissu.termterm.repository;

import com.mzoffissu.termterm.domain.auth.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    Optional<Member> findBySocialId(String socialId);
}


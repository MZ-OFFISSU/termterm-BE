package com.mzoffissu.termterm.repository;

import com.mzoffissu.termterm.domain.auth.Member;
import com.mzoffissu.termterm.domain.auth.SocialLoginType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmailAndSocialType(String email, SocialLoginType socialLoginType);
    Optional<Member> findById(Long id);
}


package com.mzoffissu.termterm.repository;

import com.mzoffissu.termterm.domain.auth.Member;

public interface MemberRepositoryCustom {
    boolean existsByNicknameCustom(String nickname);

    boolean existsByNicknameExceptMeCustom(Member member, String newNickname);
}

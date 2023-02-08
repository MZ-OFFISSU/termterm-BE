package com.mzoffissu.termterm.domain.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SocialLoginType {
    GOOGLE("google"),
    KAKAO("kakao"),
    APPLE("apple");

    private final String value;
}

package com.mzoffissu.termterm.service.auth;

import com.mzoffissu.termterm.dto.auth.TokenResponseDto;
import com.mzoffissu.termterm.dto.auth.MemberInfoDto;

public abstract class SocialAuthService {
    abstract public TokenResponseDto getToken(String code);
    abstract public MemberInfoDto getMemberInfo(TokenResponseDto dto);
    abstract public void signup(MemberInfoDto dto);
}

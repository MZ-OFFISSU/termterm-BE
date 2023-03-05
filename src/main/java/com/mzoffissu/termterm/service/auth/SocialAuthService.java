package com.mzoffissu.termterm.service.auth;

import com.mzoffissu.termterm.dto.auth.TokenResponseDto;
import com.mzoffissu.termterm.dto.auth.MemberInitialInfoDto;

public abstract class SocialAuthService {
    abstract public TokenResponseDto getToken(String code);
    abstract public MemberInitialInfoDto getMemberInfo(TokenResponseDto dto);
    abstract public void signup(MemberInitialInfoDto dto);
}

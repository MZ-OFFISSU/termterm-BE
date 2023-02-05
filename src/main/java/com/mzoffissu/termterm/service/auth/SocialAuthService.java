package com.mzoffissu.termterm.service.auth;

import com.mzoffissu.termterm.dto.auth.TokenResponseDto;
import com.mzoffissu.termterm.dto.auth.UserInfoDto;

public abstract class SocialAuthService {
    abstract public TokenResponseDto getToken(String code) throws Exception;
    abstract public UserInfoDto getUserInfo(TokenResponseDto dto) throws Exception;
    abstract public void signup(UserInfoDto dto);
}

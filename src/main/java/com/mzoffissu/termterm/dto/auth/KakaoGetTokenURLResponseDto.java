package com.mzoffissu.termterm.dto.auth;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class KakaoGetTokenURLResponseDto {
    private String token_type;
    private String access_token;
    private String expires_in;
    private String refresh_token;
    private String refresh_token_expires_in;
    private String scope;
}

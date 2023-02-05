package com.mzoffissu.termterm.controller.auth;

import com.mzoffissu.termterm.dto.auth.GoogleUserInfoDto;
import com.mzoffissu.termterm.dto.auth.KakaoUserInfoDto;
import com.mzoffissu.termterm.dto.auth.TokenResponseDto;
import com.mzoffissu.termterm.dto.auth.UserInfoDto;
import com.mzoffissu.termterm.service.auth.GoogleService;
import com.mzoffissu.termterm.service.auth.KakaoService;
import com.mzoffissu.termterm.service.auth.SocialAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final GoogleService googleService;
    private final KakaoService kakaoService;

    @PostMapping("/auth/{socialType}")
    public UserInfoDto login(@RequestBody Map<String, String> code, @PathVariable("socialType") String socialType) throws IOException {
        TokenResponseDto tokenResponse;
        UserInfoDto userInfo;
        String authorizationCode = code.get("code");
        SocialAuthService socialAuthService;

        try {
            if(socialType.equals("kakao")){
                socialAuthService = kakaoService;
            }else if(socialType.equals("google")){
                socialAuthService = googleService;
            }else{
                throw new Exception("wrong social type");
            }

            tokenResponse = socialAuthService.getToken(authorizationCode);
            userInfo = socialAuthService.getUserInfo(tokenResponse);
            socialAuthService.signup(userInfo);

            log.info("로그인 : {} ({})", userInfo.getEmail(), userInfo.getName());
            return userInfo;
        }
        catch (Exception e){
            log.error(e.getMessage());
            return GoogleUserInfoDto.builder().build();
        }
    }
}

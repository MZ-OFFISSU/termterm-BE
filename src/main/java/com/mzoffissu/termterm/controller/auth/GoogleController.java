package com.mzoffissu.termterm.controller.auth;

import com.mzoffissu.termterm.dto.auth.GoogleUserInfoDto;
import com.mzoffissu.termterm.dto.auth.TokenResponseDto;
import com.mzoffissu.termterm.service.auth.GoogleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class GoogleController {
    private final GoogleService googleService;

    @PostMapping("/auth/google")
    public GoogleUserInfoDto googleLogin(@RequestBody Map<String, String> code) throws IOException{
        TokenResponseDto tokenResponse = googleService.getToken(code.get("code"));
        GoogleUserInfoDto userInfo = googleService.getUserInfo(tokenResponse);
        googleService.googleSignup(userInfo);

        log.info("로그인 : {} ({})", userInfo.getEmail(), userInfo.getName());
        return userInfo;
    }

}

package com.mzoffissu.termterm.controller.auth;

import com.mzoffissu.termterm.dto.auth.KakaoUserInfoDto;
import com.mzoffissu.termterm.service.auth.KakaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class KakaoController {
    private final KakaoService kakaoService;

    @PostMapping("/auth/kakao")
    public KakaoUserInfoDto kakaoLogin(@RequestBody Map<String, String> code) throws IOException {
        String accessToken = kakaoService.getToken(code.get("code"));
        KakaoUserInfoDto userInfo = kakaoService.getUserInfo(accessToken);
        kakaoService.kakaoSignup(userInfo);

        return userInfo;
    }
}

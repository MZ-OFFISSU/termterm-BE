package com.mzoffissu.termterm.controller.auth;

import com.mzoffissu.termterm.domain.auth.Member;
import com.mzoffissu.termterm.dto.jwt.TokenDto;
import com.mzoffissu.termterm.response.exception.AuthorityExceptionType;
import com.mzoffissu.termterm.response.success.AuthSuccessType;
import com.mzoffissu.termterm.service.auth.*;
import com.mzoffissu.termterm.domain.auth.SocialLoginType;
import com.mzoffissu.termterm.response.DefaultResponse;
import com.mzoffissu.termterm.dto.auth.TokenResponseDto;
import com.mzoffissu.termterm.dto.auth.MemberInitialInfoDto;
import com.mzoffissu.termterm.response.exception.BizException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/v1")
public class AuthController {
    private final GoogleService googleService;
    private final KakaoService kakaoService;
    private final MemberService memberService;
    private final TokenService tokenService;

    @PostMapping("/auth/{socialType}")
    public ResponseEntity login(@RequestHeader(name = "auth-code", required = false) String authorizationCode, @PathVariable("socialType") String socialType){
        if(authorizationCode == null){
            throw new BizException(AuthorityExceptionType.NO_AUTHORIZATION_CODE);
        }

        TokenResponseDto tokenResponse;
        MemberInitialInfoDto memberInfo;
        SocialAuthService socialAuthService;
        TokenDto tokenDto;
        SocialLoginType socialLoginType;

        // api 경로 중 google, kakao, apple 여부 확인. 이외가 들어오면 exception
        if(socialType.equals(SocialLoginType.KAKAO.getValue())){
            socialLoginType = SocialLoginType.KAKAO;
            socialAuthService = kakaoService;
        }else if(socialType.equals(SocialLoginType.GOOGLE.getValue())){
            socialLoginType = SocialLoginType.GOOGLE;
            socialAuthService = googleService;
        }else if(socialType.equals(SocialLoginType.APPLE.getValue())){
            socialLoginType = SocialLoginType.APPLE;
//                socialAuthService = appleService;
            socialAuthService = kakaoService;
        }
        else{
            throw new BizException(AuthorityExceptionType.INVALID_SOCIAL_TYPE);
        }

        tokenResponse = socialAuthService.getToken(authorizationCode);
        memberInfo = socialAuthService.getMemberInfo(tokenResponse);
        socialAuthService.signup(memberInfo);
        Member member = memberService.findByEmailAndSocialType(memberInfo.getEmail(), socialLoginType);

        tokenDto = tokenService.createToken(member, memberInfo.getSocialId());

        log.info("로그인 : {} ({})", memberInfo.getEmail(), memberInfo.getName());
        return new ResponseEntity<>(DefaultResponse.create(AuthSuccessType.LOGIN_SUCCESS, tokenDto), AuthSuccessType.LOGIN_SUCCESS.getHttpStatus());
    }

    @GetMapping("/auth/reissue")
    public ResponseEntity reissue(@RequestHeader("Authorization") String refreshToken){
        TokenDto tokenDto = tokenService.reissue(refreshToken);
        return new ResponseEntity<>(DefaultResponse.create(AuthSuccessType.TOKEN_REISSUED, tokenDto), AuthSuccessType.TOKEN_REISSUED.getHttpStatus());
    }

    @GetMapping("/auth/logout")
    public ResponseEntity logout(@RequestHeader("Authorization") String accessToken){
        Member member = tokenService.getMemberByToken(accessToken);
        memberService.logout(member);
        return new ResponseEntity<>(DefaultResponse.create(AuthSuccessType.LOGOUT_SUCCESS), AuthSuccessType.LOGOUT_SUCCESS.getHttpStatus());
    }
}

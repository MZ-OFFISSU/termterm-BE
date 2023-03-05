package com.mzoffissu.termterm.controller.auth;

import com.mzoffissu.termterm.domain.auth.Member;
import com.mzoffissu.termterm.dto.jwt.TokenDto;
import com.mzoffissu.termterm.exception.AuthorityExceptionType;
import com.mzoffissu.termterm.service.auth.MemberService;
import com.mzoffissu.termterm.vo.ResponseMessage;
import com.mzoffissu.termterm.domain.auth.SocialLoginType;
import com.mzoffissu.termterm.dto.DefaultResponse;
import com.mzoffissu.termterm.dto.auth.TokenResponseDto;
import com.mzoffissu.termterm.dto.auth.MemberInitialInfoDto;
import com.mzoffissu.termterm.exception.BizException;
import com.mzoffissu.termterm.service.auth.GoogleService;
import com.mzoffissu.termterm.service.auth.KakaoService;
import com.mzoffissu.termterm.service.auth.SocialAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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

        tokenDto = memberService.createToken(member, memberInfo.getSocialId());

        log.info("로그인 : {} ({})", memberInfo.getEmail(), memberInfo.getName());
        return new ResponseEntity<>(DefaultResponse.create(HttpStatus.OK.value(), ResponseMessage.LOGIN_SUCCESS, tokenDto), HttpStatus.OK);
    }

    @GetMapping("/auth/reissue")
    public ResponseEntity reissue(@RequestHeader("Authorization") String refreshToken){
        TokenDto tokenDto = memberService.reissue(refreshToken);
        return new ResponseEntity<>(DefaultResponse.create(HttpStatus.OK.value(), ResponseMessage.TOKEN_REISSUED, tokenDto), HttpStatus.OK);
    }

    @GetMapping("/auth/logout")
    public ResponseEntity logout(@RequestHeader("Authorization") String accessToken){
            memberService.logout(accessToken);
            return new ResponseEntity<>(DefaultResponse.create(HttpStatus.NO_CONTENT.value(), ResponseMessage.LOGOUT_SUCCESS), HttpStatus.NO_CONTENT);
    }
}

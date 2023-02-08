package com.mzoffissu.termterm.controller.auth;

import com.mzoffissu.termterm.exception.AuthorityExceptionType;
import com.mzoffissu.termterm.exception.InternalServerExceptionType;
import com.mzoffissu.termterm.vo.ResponseMessage;
import com.mzoffissu.termterm.domain.auth.SocialLoginType;
import com.mzoffissu.termterm.dto.DefaultResponse;
import com.mzoffissu.termterm.dto.auth.TokenResponseDto;
import com.mzoffissu.termterm.dto.auth.UserInfoDto;
import com.mzoffissu.termterm.exception.BizException;
import com.mzoffissu.termterm.service.auth.GoogleService;
import com.mzoffissu.termterm.service.auth.KakaoService;
import com.mzoffissu.termterm.service.auth.SocialAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final GoogleService googleService;
    private final KakaoService kakaoService;

    @PostMapping("/auth/{socialType}")
    public ResponseEntity login(@RequestBody Map<String, String> code, @PathVariable("socialType") String socialType){
        TokenResponseDto tokenResponse;
        UserInfoDto userInfo;
        SocialAuthService socialAuthService;

        String authorizationCode = code.get("code");    // code 가 없을 경우 null 들어감
        if(authorizationCode == null){
            throw new BizException(AuthorityExceptionType.NO_AUTHORIZATION_CODE);
        }

        // api 경로 중 google, kakao, apple 여부 확인. 이외가 들어오면 exception
        if(socialType.equals(SocialLoginType.KAKAO.getValue())){
            socialAuthService = kakaoService;
        }else if(socialType.equals(SocialLoginType.GOOGLE.getValue())){
            socialAuthService = googleService;
        }else{
            throw new BizException(AuthorityExceptionType.INVALID_SOCIAL_TYPE);
        }

        try {
            tokenResponse = socialAuthService.getToken(authorizationCode);
            userInfo = socialAuthService.getUserInfo(tokenResponse);
            socialAuthService.signup(userInfo);

            log.info("로그인 : {} ({})", userInfo.getEmail(), userInfo.getName());
            return new ResponseEntity<>(DefaultResponse.create(HttpStatus.OK.value(), ResponseMessage.LOGIN_SUCCESS, userInfo), HttpStatus.OK);
        }
        catch (BizException e){
            log.error(e.getMessage());
            return new ResponseEntity<>(DefaultResponse.create(e.getBaseExceptionType().getHttpStatus().value(), e.getMessage()), e.getBaseExceptionType().getHttpStatus());
        }
        catch (Exception e){
            log.error(e.getMessage());
            return new ResponseEntity<>(DefaultResponse.create(HttpStatus.INTERNAL_SERVER_ERROR.value(), InternalServerExceptionType.INTERNAL_SERVER_ERROR.getMessage()), InternalServerExceptionType.INTERNAL_SERVER_ERROR.getHttpStatus());
        }
    }
}

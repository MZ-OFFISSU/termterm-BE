package com.mzoffissu.termterm.response.success;

import com.mzoffissu.termterm.response.BaseResponseType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthSuccessType implements BaseResponseType {
    LOGIN_SUCCESS(2001, "로그인 성공", HttpStatus.OK),
    TOKEN_REISSUED(2002, "토큰 재발급 완료", HttpStatus.OK),
    LOGOUT_SUCCESS(2003, "로그아웃 성공", HttpStatus.NO_CONTENT),
    ;

    private final Integer code;
    private final String message;
    private final HttpStatus httpStatus;
}

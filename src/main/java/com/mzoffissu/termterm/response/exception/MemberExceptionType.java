package com.mzoffissu.termterm.response.exception;

import com.mzoffissu.termterm.response.BaseResponseType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MemberExceptionType implements BaseResponseType {
    NOT_FOUND_USER(4021,"사용자를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),
    NOT_FOUND_AUTHENTICATION(4022,"인증 정보를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),
    DUPLICATE_USER(4023,"이미 존재하는 사용자입니다.", HttpStatus.BAD_REQUEST),
    NOT_FOUND_PASSWORD(4024,"비밀번호를 입력해주세요",HttpStatus.BAD_REQUEST),
    LOGOUT_MEMBER(4025,"로그아웃된 사용자입니다.",HttpStatus.BAD_REQUEST),
    DUPLICATE_NICKNAME(4026, "이미 사용중인 닉네임입니다.", HttpStatus.BAD_REQUEST),
    WRONG_PASSWORD(4027,"비밀번호를 잘못 입력하였습니다.", HttpStatus.UNAUTHORIZED),
    SESSION_EXPIRED(4028,"세션이 만료되었습니다.", HttpStatus.UNAUTHORIZED);

    private final Integer code;
    private final String message;
    private final HttpStatus httpStatus;

}

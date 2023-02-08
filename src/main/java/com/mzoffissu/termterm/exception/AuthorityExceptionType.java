package com.mzoffissu.termterm.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthorityExceptionType implements BaseExceptionType{
    NOT_FOUND_AUTHORITY("NOT_FOUND_AUTHORITY", "존재하지 않는 권한입니다.", HttpStatus.BAD_REQUEST),
    GOOGLE_CONNECTION_ERROR("GOOGLE_CONNECTION_ERROR", "구글 서버와의 연결에 실패하였습니다.", HttpStatus.GATEWAY_TIMEOUT),
    KAKAO_CONNECTION_ERROR("KAKAO_CONNECTION_ERROR", "카카오 서버와의 연결에 실패하였습니다.", HttpStatus.GATEWAY_TIMEOUT),
    NO_AUTHORIZATION_CODE("NO_AUTHORIZATION_CODE", "요청에 인가 코드가 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    INVALID_SOCIAL_TYPE("INVALID_SOCIAL_TYPE", "소셜 타입 경로가 유효하지 않습니다.", HttpStatus.BAD_REQUEST);

    private final String errorCode;
    private final String message;
    private final HttpStatus httpStatus;

}

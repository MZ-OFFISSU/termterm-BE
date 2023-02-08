package com.mzoffissu.termterm.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum InternalServerExceptionType implements BaseExceptionType{
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "서버 내부에 문제가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String errorCode;
    private final String message;
    private final HttpStatus httpStatus;

}

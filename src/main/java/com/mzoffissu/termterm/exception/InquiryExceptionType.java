package com.mzoffissu.termterm.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum InquiryExceptionType implements BaseExceptionType{
    INVALID_INQUIRY_TYPE("INVALID_INQUIRY_TYPE", "문의 유형이 존재하지 않습니다.", HttpStatus.BAD_REQUEST);

    private final String errorCode;
    private final String message;
    private final HttpStatus httpStatus;
}

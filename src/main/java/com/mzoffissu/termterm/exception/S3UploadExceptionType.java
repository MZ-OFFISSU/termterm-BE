package com.mzoffissu.termterm.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum S3UploadExceptionType implements BaseExceptionType{
    CANNOT_CONVERT_FILE("CANNOT_CONVERT_FILE", "파일을 변환할 수 없습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String errorCode;
    private final String message;
    private final HttpStatus httpStatus;
}

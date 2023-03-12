package com.mzoffissu.termterm.response.exception;

import com.mzoffissu.termterm.response.BaseResponseType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum S3UploadExceptionType implements BaseResponseType {
    CANNOT_CONVERT_FILE(5001, "파일을 변환할 수 없습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final Integer code;
    private final String message;
    private final HttpStatus httpStatus;
}

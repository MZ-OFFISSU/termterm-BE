package com.mzoffissu.termterm.response.exception;

import com.mzoffissu.termterm.response.BaseResponseType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum InquiryExceptionType implements BaseResponseType {
    INVALID_INQUIRY_TYPE(4044, "문의 유형이 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    INVALID_INQUIRY_ID(4045, "문의사항이 존재하지 않거나 삭제되었습니다.", HttpStatus.NOT_FOUND),
    ;

    private final Integer code;
    private final String message;
    private final HttpStatus httpStatus;
}

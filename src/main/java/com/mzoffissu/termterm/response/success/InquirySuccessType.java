package com.mzoffissu.termterm.response.success;

import com.mzoffissu.termterm.response.BaseResponseType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum InquirySuccessType implements BaseResponseType {
    INQUIRY_ACCEPTED(2011, "문의 접수 완료", HttpStatus.CREATED),
    INQUIRY_DELETED(2042, "문의 삭제 완료", HttpStatus.NO_CONTENT),
    INQUIRY_PROCEED(2043, "문의 처리 완료", HttpStatus.NO_CONTENT),
    INQUIRY_HOLD(2044, "문의 대기 중 변환 완료", HttpStatus.NO_CONTENT),
    ;


    private final Integer code;
    private final String message;
    private final HttpStatus httpStatus;
}

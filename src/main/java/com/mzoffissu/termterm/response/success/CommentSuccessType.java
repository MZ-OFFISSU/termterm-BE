package com.mzoffissu.termterm.response.success;

import com.mzoffissu.termterm.response.BaseResponseType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CommentSuccessType implements BaseResponseType {
    COMMENT_POST_SUCCESS(2061, "나만의 용어 설명 등록 완료", HttpStatus.CREATED),
    ;


    private final Integer code;
    private final String message;
    private final HttpStatus httpStatus;
}

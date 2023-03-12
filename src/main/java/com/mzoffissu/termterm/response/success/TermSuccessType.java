package com.mzoffissu.termterm.response.success;

import com.mzoffissu.termterm.response.BaseResponseType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum TermSuccessType implements BaseResponseType {
    TERM_SEARCH_SUCCESS(2001, "용어 검색 성공", HttpStatus.OK),
    TERM_SEARCH_NO_RESULT(2002, "검색어에 대한 검색 결과가 없습니다.", HttpStatus.OK),
    ;

    private final Integer code;
    private final String message;
    private final HttpStatus httpStatus;
}

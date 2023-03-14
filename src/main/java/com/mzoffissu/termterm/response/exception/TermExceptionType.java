package com.mzoffissu.termterm.response.exception;

import com.mzoffissu.termterm.response.BaseResponseType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum TermExceptionType implements BaseResponseType {
    TERM_SEARCH_NO_RESULT(4051, "검색어에 대한 검색 결과가 없습니다.", HttpStatus.BAD_REQUEST),
    TERM_ID_NO_RESULT(4052, "단어가 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    ;

    private final Integer code;
    private final String message;
    private final HttpStatus httpStatus;
}

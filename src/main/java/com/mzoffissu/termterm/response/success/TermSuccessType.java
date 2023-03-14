package com.mzoffissu.termterm.response.success;

import com.mzoffissu.termterm.response.BaseResponseType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum TermSuccessType implements BaseResponseType {
    TERM_SEARCH_SUCCESS(2051, "용어 검색 성공", HttpStatus.OK),
    TERM_DETAIL_SUCCESS(2052, "용어 상세 정보 조회 성공", HttpStatus.OK),
    ;

    private final Integer code;
    private final String message;
    private final HttpStatus httpStatus;
}

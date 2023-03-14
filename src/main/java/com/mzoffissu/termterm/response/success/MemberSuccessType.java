package com.mzoffissu.termterm.response.success;

import com.mzoffissu.termterm.response.BaseResponseType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MemberSuccessType implements BaseResponseType{
    LOGIN_SUCCESS(2021, "로그인 성공", HttpStatus.OK),
    MEMBER_INFO_GET_SUCCESS(2022, "사용자 정보 조회 성공", HttpStatus.OK),
    MEMBER_INFO_UPDATE_SUCCESS(2023, "사용자 정보 수정 성공", HttpStatus.OK),
    NOT_DUPLICATE_NICKNAME(2024, "사용 가능한 닉네임입니다.", HttpStatus.OK),
    PROFILE_IMAGE_UPDATE_SUCCESS(2025, "프로필 사진 수정 성공", HttpStatus.OK),
    MEMBER_CATEGORY_UPDATE_SUCCESS(2026, "사용자 관심사 업데이트 성공", HttpStatus.OK),
    ;

    private final Integer code;
    private final String message;
    private final HttpStatus httpStatus;
}

package com.mzoffissu.termterm.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MemberNicknameCheckResponseDto {
    private String isDuplicated;
}

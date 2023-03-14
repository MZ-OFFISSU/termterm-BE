package com.mzoffissu.termterm.dto.quiz;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class QuizDailyDto {
    private List<String> quiz; // 선지 3개
    private Long answer; // 정답 번호
    private String description; // 퀴즈 문제(용어 설명)
}

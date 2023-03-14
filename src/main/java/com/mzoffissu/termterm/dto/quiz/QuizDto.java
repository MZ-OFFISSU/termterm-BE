package com.mzoffissu.termterm.dto.quiz;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QuizDto {
    private Long id;
    private String name;
}

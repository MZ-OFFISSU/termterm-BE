package com.mzoffissu.termterm.dto.term;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class TermDetailResponseDto {
    private List<TermDto> terms = new ArrayList<>();
}

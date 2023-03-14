package com.mzoffissu.termterm.dto.term;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TermIdNameDto {
    private Long id;
    private String name;
}

package com.mzoffissu.termterm.dto.category;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CategoryDto {
    private Long id;
    private String name;
}

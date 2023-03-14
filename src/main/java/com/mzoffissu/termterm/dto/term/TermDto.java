package com.mzoffissu.termterm.dto.term;

import com.mzoffissu.termterm.dto.category.CategoryDto;
import com.mzoffissu.termterm.dto.comment.CommentDto;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class TermDto {
    private Long id;
    private String name;
    private String description;
    private String source;
    List<CategoryDto> categories = new ArrayList<>();
    List<CommentDto> comments = new ArrayList<>();
}

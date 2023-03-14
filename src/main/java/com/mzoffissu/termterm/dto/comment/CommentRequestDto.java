package com.mzoffissu.termterm.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentRequestDto {
    private Long termId;
    private String content;
    private String source;
}

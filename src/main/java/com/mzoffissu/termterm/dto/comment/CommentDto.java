package com.mzoffissu.termterm.dto.comment;

import com.mzoffissu.termterm.domain.comment.CommentStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommentDto {
    private Long id;
    private String content;
    private String source;
    private Integer likeCnt;
    private CommentStatus status;
}

package com.mzoffissu.termterm.domain.comment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommentStatus {
    ACCEPTED("A"),
    REPORTED("R"),
    ;

    private final String status;
}

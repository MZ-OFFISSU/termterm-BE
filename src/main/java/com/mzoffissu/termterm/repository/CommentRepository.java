package com.mzoffissu.termterm.repository;

import com.mzoffissu.termterm.domain.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}

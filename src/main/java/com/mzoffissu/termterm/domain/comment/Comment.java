package com.mzoffissu.termterm.domain.comment;

import com.mzoffissu.termterm.domain.BaseTimeEntity;
import com.mzoffissu.termterm.domain.term.Term;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.yaml.snakeyaml.comments.CommentType;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Comment extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COMMENT_ID")
    private Long id;

    private String content;
    private String source;
    private Integer likeCnt = 0;
    private CommentStatus status;

    @ManyToOne
    @JoinColumn(name = "TERM_ID")
    private Term term;


}

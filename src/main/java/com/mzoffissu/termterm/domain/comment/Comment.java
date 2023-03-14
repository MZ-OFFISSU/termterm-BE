package com.mzoffissu.termterm.domain.comment;

import com.mzoffissu.termterm.domain.BaseTimeEntity;
import com.mzoffissu.termterm.domain.auth.Member;
import com.mzoffissu.termterm.domain.term.Term;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Enumerated(EnumType.STRING)
    private CommentStatus status;

    @ManyToOne
    @JoinColumn(name = "TERM_ID")
    private Term term;

    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @Builder
    public Comment(String content, String source, CommentStatus status, Term term, Member member) {
        this.content = content;
        this.source = source;
        this.status = status;
        this.term = term;
        this.member = member;
    }
}

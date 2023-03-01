package com.mzoffissu.termterm.domain;

import com.mzoffissu.termterm.domain.auth.Member;
import lombok.Getter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CATEGORY_ID")
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "categories")
    private Set<Member> members;

    @ManyToMany(mappedBy = "categories")
    private Set<Term> terms;

}

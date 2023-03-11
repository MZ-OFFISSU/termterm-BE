package com.mzoffissu.termterm.domain.category;

import com.mzoffissu.termterm.domain.Term;
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
    private Set<Term> terms;

}

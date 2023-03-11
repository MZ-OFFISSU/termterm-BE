package com.mzoffissu.termterm.domain.term;

import com.mzoffissu.termterm.domain.category.Category;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
public class Term {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TERM_ID")
    private Long id;

    private String name;

    @Column(columnDefinition = "LONGTEXT")
    private String description;
    private String source;

    @ManyToMany
    @JoinTable(name = "TERM_CATEGORY",
            joinColumns = @JoinColumn(name = "TERM_ID"),
            inverseJoinColumns = @JoinColumn(name = "CATEGORY_ID"))
    private Set<Category> categories = new HashSet<>();

}

package com.mzoffissu.termterm.repository;


import com.mzoffissu.termterm.domain.term.Term;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TermRepository extends JpaRepository<Term, Long> {

    @Query("select t from Term t where lower(t.name) = lower(:name) or Function('replace', lower(t.name), ' ', '') like lower(concat('%', :name, '%'))")
    Optional<List<Term>> findByNameContainingIgnoreCase(@Param("name") String name);
}

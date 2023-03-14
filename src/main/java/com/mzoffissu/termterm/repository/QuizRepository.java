package com.mzoffissu.termterm.repository;

import com.mzoffissu.termterm.domain.term.Term;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface QuizRepository extends JpaRepository<Term, Long> {
    @Query(nativeQuery = true, value = "select * from Term t order by RAND() limit 3")
    List<Term> findRandomBy();
}

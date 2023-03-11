package com.mzoffissu.termterm.service.term;

import com.mzoffissu.termterm.domain.category.Category;
import com.mzoffissu.termterm.domain.term.Term;
import com.mzoffissu.termterm.dto.category.CategoryDto;
import com.mzoffissu.termterm.dto.term.TermDto;
import com.mzoffissu.termterm.dto.term.TermSearchResponseDto;
import com.mzoffissu.termterm.exception.BizException;
import com.mzoffissu.termterm.repository.TermRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TermService {
    private final TermRepository termRepository;
    public TermSearchResponseDto searchTerm(String name) {
        List<Term> termsResult = termRepository.findByNameContainingIgnoreCase(name)
                .orElseThrow(() -> new RuntimeException("부러ㅘ로라ㅗ라라"));

        List<TermDto> terms = new ArrayList<>();
        for(Term term : termsResult){
            List<CategoryDto> categories = new ArrayList<>();
            for(Category category : term.getCategories()){
                CategoryDto categoryDto = CategoryDto.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .build();

                categories.add(categoryDto);
            }

            TermDto termDto = TermDto.builder()
                    .id(term.getId())
                    .name(term.getName())
                    .description(term.getDescription())
                    .source(term.getSource())
                    .categories(categories)
                    .build();

            terms.add(termDto);
        }

        return TermSearchResponseDto.builder()
                .terms(terms)
                .build();
    }
}

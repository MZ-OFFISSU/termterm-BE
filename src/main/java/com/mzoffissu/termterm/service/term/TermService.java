package com.mzoffissu.termterm.service.term;

import com.mzoffissu.termterm.domain.category.Category;
import com.mzoffissu.termterm.domain.comment.Comment;
import com.mzoffissu.termterm.domain.comment.CommentStatus;
import com.mzoffissu.termterm.domain.term.Term;
import com.mzoffissu.termterm.dto.category.CategoryDto;
import com.mzoffissu.termterm.dto.comment.CommentDto;
import com.mzoffissu.termterm.dto.term.TermDto;
import com.mzoffissu.termterm.dto.term.TermIdNameDto;
import com.mzoffissu.termterm.dto.term.TermSearchResponseDto;
import com.mzoffissu.termterm.repository.TermRepository;
import com.mzoffissu.termterm.response.exception.BizException;
import com.mzoffissu.termterm.response.exception.TermExceptionType;
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
        List<Term> termsResult = termRepository.findByNameContainingIgnoreCase(name);
        if(termsResult.isEmpty()){
            throw new BizException(TermExceptionType.TERM_SEARCH_NO_RESULT);
        }

        List<TermIdNameDto> terms = new ArrayList<>();
        for(Term term : termsResult){
            TermIdNameDto termIdNameDto = TermIdNameDto.builder()
                    .id(term.getId())
                    .name(term.getName())
                    .build();

            terms.add(termIdNameDto);
        }

        return TermSearchResponseDto.builder()
                .terms(terms)
                .build();
    }

    public TermDto detailTerm(Long termId) {
        Term term = termRepository.findById(termId)
                .orElseThrow(() -> new BizException(TermExceptionType.TERM_ID_NO_RESULT));

        List<CategoryDto> categories = new ArrayList<>();
        for (Category category : term.getCategories()){
            CategoryDto categoryDto = CategoryDto.builder()
                    .id(category.getId())
                    .name(category.getName())
                    .build();

            categories.add(categoryDto);
        }

        List<CommentDto> comments = new ArrayList<>();
        for (Comment comment : term.getComments()){
            CommentDto commentDto = CommentDto.builder()
                    .id(comment.getId())
                    .content(comment.getContent())
                    .source(comment.getSource())
                    .likeCnt(comment.getLikeCnt())
                    .status(CommentStatus.ACCEPTED)
                    .build();

            comments.add(commentDto);
        }

        return TermDto.builder()
                .id(term.getId())
                .name(term.getName())
                .description(term.getDescription())
                .source(term.getSource())
                .categories(categories)
                .comments(comments)
                .build();
    }


}

package com.mzoffissu.termterm.controller.term;

import com.mzoffissu.termterm.domain.auth.Member;
import com.mzoffissu.termterm.dto.comment.CommentRequestDto;
import com.mzoffissu.termterm.dto.term.TermDto;
import com.mzoffissu.termterm.response.DefaultResponse;
import com.mzoffissu.termterm.dto.term.TermSearchResponseDto;
import com.mzoffissu.termterm.response.success.CommentSuccessType;
import com.mzoffissu.termterm.response.success.TermSuccessType;
import com.mzoffissu.termterm.service.auth.TokenService;
import com.mzoffissu.termterm.service.term.TermService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/v1")
public class TermController {
    private final TermService termService;
    private final TokenService tokenService;

    @GetMapping("/term/search")
    public ResponseEntity searchTerm(@RequestHeader(name = "Authorization") String accessToken, @RequestParam(name = "name") String name){
        tokenService.getMemberByToken(accessToken);
        TermSearchResponseDto termSearchResponseDto = termService.searchTerm(name);

        return new ResponseEntity<>(DefaultResponse.create(TermSuccessType.TERM_SEARCH_SUCCESS, termSearchResponseDto), TermSuccessType.TERM_SEARCH_SUCCESS.getHttpStatus());
    }

    @GetMapping("/term/detail")
    public ResponseEntity detailTerm(@RequestHeader(name = "Authorization") String accessToken, @RequestParam(name = "id") Long termId){
        tokenService.getMemberByToken(accessToken);
        TermDto termDto = termService.detailTerm(termId);

        return new ResponseEntity<>(DefaultResponse.create(TermSuccessType.TERM_DETAIL_SUCCESS, termDto), TermSuccessType.TERM_DETAIL_SUCCESS.getHttpStatus());
    }

    @PostMapping("/term/comment")
    public ResponseEntity postComment(@RequestHeader(name = "Authorization") String accessToken, @RequestBody CommentRequestDto commentRequestDto){
        Member member = tokenService.getMemberByToken(accessToken);
        termService.postComment(member, commentRequestDto);

        return new ResponseEntity<>(DefaultResponse.create(CommentSuccessType.COMMENT_POST_SUCCESS), CommentSuccessType.COMMENT_POST_SUCCESS.getHttpStatus());
    }
}

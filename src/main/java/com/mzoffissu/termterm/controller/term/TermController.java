package com.mzoffissu.termterm.controller.term;

import com.mzoffissu.termterm.response.DefaultResponse;
import com.mzoffissu.termterm.dto.term.TermSearchResponseDto;
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

}

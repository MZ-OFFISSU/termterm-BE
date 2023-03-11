package com.mzoffissu.termterm.controller.term;

import com.mzoffissu.termterm.dto.DefaultResponse;
import com.mzoffissu.termterm.dto.term.TermSearchResponseDto;
import com.mzoffissu.termterm.service.term.TermService;
import com.mzoffissu.termterm.vo.ResponseMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/v1")
public class TermController {
    private final TermService termService;

    @GetMapping("/term/search")
    public ResponseEntity searchTerm(@RequestParam(name = "name") String name){
        TermSearchResponseDto termSearchResponseDto = termService.searchTerm(name);
        return new ResponseEntity<>(DefaultResponse.create(HttpStatus.OK.value(), ResponseMessage.TERM_SEARCH_SUCCESS, termSearchResponseDto), HttpStatus.OK);
    }

}

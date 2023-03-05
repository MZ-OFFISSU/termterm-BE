package com.mzoffissu.termterm.controller.auth;

import com.mzoffissu.termterm.dto.DefaultResponse;
import com.mzoffissu.termterm.dto.auth.MemberInfoDto;
import com.mzoffissu.termterm.exception.BizException;
import com.mzoffissu.termterm.exception.InternalServerExceptionType;
import com.mzoffissu.termterm.service.auth.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/v1")
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/member/info")
    public ResponseEntity getMemberInfo(@RequestHeader(name = "Authorization") String accessToken){
        try{
            MemberInfoDto memberInfoDto = memberService.getMemberInfo(accessToken);
            return new ResponseEntity<>(DefaultResponse.create(HttpStatus.OK.value(), "", memberInfoDto), HttpStatus.OK);
        }
        catch (BizException e){
            log.error(e.getMessage());
            return new ResponseEntity<>(DefaultResponse.create(e.getBaseExceptionType().getHttpStatus().value(), e.getMessage()), e.getBaseExceptionType().getHttpStatus());
        }
        catch (Exception e){
            log.error(e.getMessage());
            return new ResponseEntity<>(DefaultResponse.create(HttpStatus.INTERNAL_SERVER_ERROR.value(), InternalServerExceptionType.INTERNAL_SERVER_ERROR.getMessage()), InternalServerExceptionType.INTERNAL_SERVER_ERROR.getHttpStatus());
        }
    }
}

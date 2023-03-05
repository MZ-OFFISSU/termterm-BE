package com.mzoffissu.termterm.controller.auth;

import com.mzoffissu.termterm.dto.DefaultResponse;
import com.mzoffissu.termterm.dto.auth.MemberInfoDto;
import com.mzoffissu.termterm.dto.member.MemberNicknameCheckResponseDto;
import com.mzoffissu.termterm.service.auth.MemberService;
import com.mzoffissu.termterm.vo.ResponseMessage;
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
        MemberInfoDto memberInfoDto = memberService.getMemberInfo(accessToken);
        return new ResponseEntity<>(DefaultResponse.create(HttpStatus.OK.value(), "", memberInfoDto), HttpStatus.OK);
    }

    @GetMapping("/member/nickname/check")
    public ResponseEntity isNicknameDuplicated(@RequestParam String nickname){
        boolean isDuplicated = memberService.isNicknameDuplicated(nickname);

        MemberNicknameCheckResponseDto responseDto;

        if(isDuplicated){
            responseDto = new MemberNicknameCheckResponseDto("true");
            return new ResponseEntity<>(DefaultResponse.create(HttpStatus.OK.value(), ResponseMessage.DUPLICATE_NICKNAME, responseDto), HttpStatus.OK);
        }
        else{
            responseDto = new MemberNicknameCheckResponseDto("false");
            return new ResponseEntity<>(DefaultResponse.create(HttpStatus.OK.value(), ResponseMessage.NOT_DUPLICATE_NICKNAME, responseDto), HttpStatus.OK);
        }
    }
}

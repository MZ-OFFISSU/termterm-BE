package com.mzoffissu.termterm.controller.auth;

import com.mzoffissu.termterm.dto.DefaultResponse;
import com.mzoffissu.termterm.dto.auth.MemberInfoDto;
import com.mzoffissu.termterm.dto.member.MemberInfoUpdateRequestDto;
import com.mzoffissu.termterm.dto.member.MemberCategoriesRequestDto;
import com.mzoffissu.termterm.dto.member.MemberNicknameCheckResponseDto;
import com.mzoffissu.termterm.exception.BizException;
import com.mzoffissu.termterm.exception.MemberExceptionType;
import com.mzoffissu.termterm.service.auth.MemberService;
import com.mzoffissu.termterm.vo.ResponseMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @PutMapping("/member/info")
    public ResponseEntity updateMemberInfo(@RequestHeader(name = "Authorization") String accessToken, @RequestBody MemberInfoUpdateRequestDto memberInfoUpdateRequestDto){
        MemberInfoDto memberInfoDto = memberService.updateMemberInfo(accessToken, memberInfoUpdateRequestDto.trimAll());
        return new ResponseEntity<>(DefaultResponse.create(HttpStatus.OK.value(), ResponseMessage.MEMBER_INFO_UPDATE_SUCCESS, memberInfoDto), HttpStatus.OK);
    }

    @GetMapping("/member/nickname/check")
    public ResponseEntity isNicknameDuplicated(@RequestParam String nickname){
        boolean isDuplicated = memberService.isNicknameDuplicated(nickname);

        MemberNicknameCheckResponseDto responseDto;

        if(isDuplicated){
            responseDto = new MemberNicknameCheckResponseDto("true");
            return new ResponseEntity<>(DefaultResponse.create(HttpStatus.BAD_REQUEST.value(), MemberExceptionType.DUPLICATE_NICKNAME.getMessage(), responseDto), HttpStatus.BAD_REQUEST);
        }
        else{
            responseDto = new MemberNicknameCheckResponseDto("false");
            return new ResponseEntity<>(DefaultResponse.create(HttpStatus.OK.value(), ResponseMessage.NOT_DUPLICATE_NICKNAME, responseDto), HttpStatus.OK);
        }
    }

    @PutMapping("/member/profile-image")
    public ResponseEntity updateProfileImage(@RequestHeader(name = "Authorization") String accessToken, @RequestBody MultipartFile imageFile){
        memberService.updateProfileImage(accessToken, imageFile);
        return new ResponseEntity<>(DefaultResponse.create(HttpStatus.OK.value(), ""), HttpStatus.OK);
    }

    @PutMapping("/member/category")
    public ResponseEntity updateCategories(@RequestHeader(name = "Authorization") String accessToken, @RequestBody MemberCategoriesRequestDto memberCategoriesRequestDto){
        memberService.updateCategories(accessToken, memberCategoriesRequestDto);
        return new ResponseEntity<>(DefaultResponse.create(HttpStatus.OK.value(), ResponseMessage.MEMBER_CATEGORY_UPDATE_SUCCESS), HttpStatus.OK);
    }
}

package com.mzoffissu.termterm.controller.auth;

import com.mzoffissu.termterm.response.DefaultResponse;
import com.mzoffissu.termterm.dto.auth.MemberInfoDto;
import com.mzoffissu.termterm.dto.member.MemberInfoUpdateRequestDto;
import com.mzoffissu.termterm.dto.member.MemberCategoriesRequestDto;
import com.mzoffissu.termterm.dto.member.MemberNicknameCheckResponseDto;
import com.mzoffissu.termterm.response.exception.MemberExceptionType;
import com.mzoffissu.termterm.response.success.MemberSuccessType;
import com.mzoffissu.termterm.service.auth.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        return new ResponseEntity<>(DefaultResponse.create(MemberSuccessType.MEMBER_INFO_GET_SUCCESS, memberInfoDto), MemberSuccessType.MEMBER_INFO_GET_SUCCESS.getHttpStatus());
    }

    @PutMapping("/member/info")
    public ResponseEntity updateMemberInfo(@RequestHeader(name = "Authorization") String accessToken, @RequestBody MemberInfoUpdateRequestDto memberInfoUpdateRequestDto){
        MemberInfoDto memberInfoDto = memberService.updateMemberInfo(accessToken, memberInfoUpdateRequestDto.trimAll());
        return new ResponseEntity<>(DefaultResponse.create(MemberSuccessType.MEMBER_INFO_UPDATE_SUCCESS, memberInfoDto), MemberSuccessType.MEMBER_INFO_UPDATE_SUCCESS.getHttpStatus());
    }

    @GetMapping("/member/nickname/check")
    public ResponseEntity isNicknameDuplicated(@RequestParam String nickname){
        boolean isDuplicated = memberService.isNicknameDuplicated(nickname);

        MemberNicknameCheckResponseDto responseDto;

        if(isDuplicated){
            responseDto = new MemberNicknameCheckResponseDto("true");
            return new ResponseEntity<>(DefaultResponse.create(MemberExceptionType.DUPLICATE_NICKNAME, responseDto), MemberExceptionType.DUPLICATE_NICKNAME.getHttpStatus());
        }
        else{
            responseDto = new MemberNicknameCheckResponseDto("false");
            return new ResponseEntity<>(DefaultResponse.create(MemberSuccessType.NOT_DUPLICATE_NICKNAME, responseDto), MemberSuccessType.NOT_DUPLICATE_NICKNAME.getHttpStatus());
        }
    }

    @PutMapping("/member/profile-image")
    public ResponseEntity updateProfileImage(@RequestHeader(name = "Authorization") String accessToken, @RequestBody MultipartFile imageFile){
        memberService.updateProfileImage(accessToken, imageFile);
        return new ResponseEntity<>(DefaultResponse.create(MemberSuccessType.PROFILE_IMAGE_UPDATE_SUCCESS), MemberSuccessType.PROFILE_IMAGE_UPDATE_SUCCESS.getHttpStatus());
    }

    @PutMapping("/member/category")
    public ResponseEntity updateCategories(@RequestHeader(name = "Authorization") String accessToken, @RequestBody MemberCategoriesRequestDto memberCategoriesRequestDto){
        memberService.updateCategories(accessToken, memberCategoriesRequestDto);
        return new ResponseEntity<>(DefaultResponse.create(MemberSuccessType.MEMBER_CATEGORY_UPDATE_SUCCESS), MemberSuccessType.MEMBER_CATEGORY_UPDATE_SUCCESS.getHttpStatus());
    }
}

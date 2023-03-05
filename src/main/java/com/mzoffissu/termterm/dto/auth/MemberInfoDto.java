package com.mzoffissu.termterm.dto.auth;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberInfoDto {
    private Long memberId;
    private String name;
    private String nickname;
    private String email;
    private String profileImage;
    private String job;
    private String domain;
    private String introduction;
    private Integer point;
    private Integer yearCareer;
}

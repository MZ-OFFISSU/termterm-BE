package com.mzoffissu.termterm.dto.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberInfoUpdateRequestDto {
    private String nickname;
    private String domain;
    private String job;
    private Integer yearCareer;
    private String introduction;

    public MemberInfoUpdateRequestDto trimAll(){
        this.nickname = nickname.trim();
        this.domain = domain.trim();
        this.job = job.trim();
        this.introduction = introduction.trim();

        return this;
    }
}

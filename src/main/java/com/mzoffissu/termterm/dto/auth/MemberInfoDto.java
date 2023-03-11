package com.mzoffissu.termterm.dto.auth;


import com.mzoffissu.termterm.dto.category.CategoryDto;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

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
    private List<CategoryDto> categories = new ArrayList<>();
}

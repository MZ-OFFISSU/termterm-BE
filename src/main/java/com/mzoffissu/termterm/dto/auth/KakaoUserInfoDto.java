package com.mzoffissu.termterm.dto.auth;

import lombok.Builder;
import lombok.Getter;

@Getter
public class KakaoUserInfoDto {
    private String socialId;
    private String name;
    private String email;
    private String nickname;
    private Boolean isDefaultImage;
    private String thumbnailImageUrl;

    @Builder

    public KakaoUserInfoDto(String socialId, String name, String email, String nickname, Boolean isDefaultImage, String thumbnailImageUrl) {
        this.socialId = socialId;
        this.name = name;
        this.email = email;
        this.nickname = nickname;
        this.isDefaultImage = isDefaultImage;
        this.thumbnailImageUrl = thumbnailImageUrl;
    }
}
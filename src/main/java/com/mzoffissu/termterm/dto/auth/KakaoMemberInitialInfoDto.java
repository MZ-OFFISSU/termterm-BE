package com.mzoffissu.termterm.dto.auth;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class KakaoMemberInitialInfoDto extends MemberInitialInfoDto {
//    private String socialId;
//    private String name;
//    private String email;
//    private String nickname;
    private Boolean isDefaultImage;
//    private String thumbnailImageUrl;
}

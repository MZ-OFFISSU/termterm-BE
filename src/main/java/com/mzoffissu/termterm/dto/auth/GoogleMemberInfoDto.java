package com.mzoffissu.termterm.dto.auth;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GoogleMemberInfoDto extends MemberInfoDto {
    private String iss;
//    private String sub;
    private String azp;
    private String aud;
    private String iat;
    private String exp;
    private String alg;
    private String kid;
    private String typ;
//    private String email;
    private String email_verified;
    private String at_hash;
//    private String name;
//    private String picture;
    private String given_name;
    private String family_name;
    private String locale;
}
package com.mzoffissu.termterm.dto.auth;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public abstract class MemberInfoDto {
    public String socialId;
    public String name;
    public String email;
    public String nickname;
    public String picture;
}

package com.mzoffissu.termterm.domain.auth;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name="authority")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Authority {
    @Id
    @Column(name="authority_name", length=50)
    @Enumerated(EnumType.STRING)
    private MemberAuth authorityName;

    public String getAuthorityName(){
        return this.authorityName.toString();
    }
}


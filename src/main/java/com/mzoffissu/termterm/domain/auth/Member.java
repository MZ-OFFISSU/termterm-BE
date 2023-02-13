package com.mzoffissu.termterm.domain.auth;

import com.mzoffissu.termterm.domain.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@Entity
public class Member extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String socialId;

    @Column
    @Size(min = 1, max = 30)
    private String name;

    @Column(nullable = false)
    @Email
    private String email;

    @Column(nullable = true)
    private String picture;

    @Column(unique = true)
    @Size(min = 2, max = 10)
    private String nickname;

    @Column
    @Size(max = 100)
    private String introduction;

    @Column
    private String job;

    @Column
    @PositiveOrZero
    private Integer yearCareer = 0;

    @Column
    @Size(max = 20)
    private String domain;

    @Column
    @PositiveOrZero
    private Integer point = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SocialLoginType socialType;

    @Builder
    public Member(String socialId, String name, String email, String picture, String nickname, SocialLoginType socialLoginType) {
        this.socialId = socialId;
        this.name = name;
        this.email = email;
        this.picture = picture;
        this.nickname = nickname;
        this.socialType = socialLoginType;
    }

    public Member updatePicture(String picture){
        this.picture = picture;

        return this;
    }

    public Member deletePicture(){
        this.picture = null;

        return this;
    }

    public Member updateNickname(String nickname){
        this.nickname = nickname;

        return this;
    }

    public Member updateIntroduction(String introduction){
        this.introduction = introduction;

        return this;
    }

    public Member updateJob(String job){
        this.job = job;

        return this;
    }

    public Member updateYearCareer(Integer yearCareer){
        this.yearCareer = yearCareer;

        return this;
    }

    public Member updateDomain(String domain){
        this.domain = domain;

        return this;
    }

    public Member addPoint(Integer point){
        this.point += point;

        return this;
    }

    public Member subPoint(Integer point){
        this.point -= point;

        return this;
    }

}
package com.mzoffissu.termterm.domain.auth;

import com.mzoffissu.termterm.domain.BaseTimeEntity;
import com.mzoffissu.termterm.domain.Category;
import com.mzoffissu.termterm.dto.member.MemberInfoUpdateRequestDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Getter
@NoArgsConstructor
@Entity
public class Member extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_ID")
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
    private String profileImg;

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

    @ManyToMany
    @JoinTable(name = "MEMBER_CATEGORY",
        joinColumns = @JoinColumn(name = "MEMBER_ID"),
            inverseJoinColumns = @JoinColumn(name = "CATEGORY_ID"))
    private Set<Category> categories = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "member_authority",
            joinColumns = {@JoinColumn(name = "member_id", referencedColumnName = "member_id")},
            inverseJoinColumns = {@JoinColumn(name = "authority_name", referencedColumnName = "authority_name")}
    )
    private Set<Authority> authorities = new HashSet<>();

    @Builder
    public Member(String socialId, String name, String email, String profileImg, String nickname, SocialLoginType socialLoginType, Set<Authority> authorities) {
        this.socialId = socialId;
        this.name = name;
        this.email = email;
        this.profileImg = profileImg;
        this.nickname = nickname;
        this.socialType = socialLoginType;
        this.authorities = authorities;
    }

    public Member updateProfileImg(String profileImg){
        this.profileImg = profileImg;

        return this;
    }

    public Member deleteProfileImg(){
        this.profileImg = null;

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

    public Member updateInfo(MemberInfoUpdateRequestDto memberInfoUpdateRequestDto){
        this.nickname = memberInfoUpdateRequestDto.getNickname();
        this.domain = memberInfoUpdateRequestDto.getDomain();
        this.job = memberInfoUpdateRequestDto.getJob();
        this.yearCareer = memberInfoUpdateRequestDto.getYearCareer();
        this.introduction = memberInfoUpdateRequestDto.getIntroduction();

        return this;
    }
}
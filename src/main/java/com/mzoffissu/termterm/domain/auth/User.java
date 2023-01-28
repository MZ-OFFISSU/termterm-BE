package com.mzoffissu.termterm.domain.auth;

import com.mzoffissu.termterm.domain.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;


/**
 * 컬럼은 임의로 설정하였습니다. 정책이 나오면 같이 회의하면서 수정해요
 */
@Getter
@NoArgsConstructor
@Entity
public class User extends BaseTimeEntity {
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

    @Column
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


    /**
     * JPA로 데이터베이스로 저장할 때 Enum 값을 어떤 형태로 저장할지를 결정한다.
     * 기본적으로는 int로 된 숫자가 저장된다.
     * 숫자로 저장되면 데이터베이스로 확인할 때 그 값이 무슨 코드를 의미하는지 알 수가 없다.
     * 그래서 문자열 (EnumType.STRING) 로 저장될 수 있도록 선언한다.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SocialLoginType socialType;

    @Builder
    public User(String socialId, String name, String email, String picture, String nickname, Role role, SocialLoginType socialLoginType) {
        this.socialId = socialId;
        this.name = name;
        this.email = email;
        this.picture = picture;
        this.nickname = nickname;
        this.role = role;
        this.socialType = socialLoginType;
    }

    public User updatePicture(String picture){
        this.picture = picture;
        setModifiedDate(LocalDateTime.now());

        return this;
    }

    public User deletePicture(){
        this.picture = null;
        setModifiedDate(LocalDateTime.now());

        return this;
    }

    public User updateNickname(String nickname){
        this.nickname = nickname;
        setModifiedDate(LocalDateTime.now());

        return this;
    }

    public User updateIntroduction(String introduction){
        this.introduction = introduction;
        setModifiedDate(LocalDateTime.now());

        return this;
    }

    public User updateJob(String job){
        this.job = job;
        setModifiedDate(LocalDateTime.now());

        return this;
    }

    public User updateYearCareer(Integer yearCareer){
        this.yearCareer = yearCareer;

        return this;
    }

    public User updateDomain(String domain){
        this.domain = domain;
        setModifiedDate(LocalDateTime.now());

        return this;
    }

    public User addPoint(Integer point){
        this.point += point;

        return this;
    }

    public User subPoint(Integer point){
        this.point -= point;

        return this;
    }

    public String getRoleKey(){
        return this.role.getKey();
    }
}
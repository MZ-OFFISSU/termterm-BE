package com.mzoffissu.termterm.domain.auth;

import com.mzoffissu.termterm.domain.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;


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
    @Size(min = 1, max = 20)
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
    private Integer yearCareer;

    @Column
    @Size(max = 20)
    private String domain;

    @Column
    @PositiveOrZero
    private Integer point;


    /**
     * JPA로 데이터베이스로 저장할 때 Enum 값을 어떤 형태로 저장할지를 결정한다.
     * 기본적으로는 int로 된 숫자가 저장된다.
     * 숫자로 저장되면 데이터베이스로 확인할 때 그 값이 무슨 코드를 의미하는지 알 수가 없다.
     * 그래서 문자열 (EnumType.STRING) 로 저장될 수 있도록 선언한다.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Builder
    public User(String socialId, String name, String email, String picture, String nickname, Role role) {
        this.socialId = socialId;
        this.name = name;
        this.email = email;
        this.picture = picture;
        this.nickname = nickname;
        this.role = role;
    }

    public User update(String name, String picture){
        this.name = name;
        this.picture = picture;

        return this;
    }

    public User updatePicture(String picture){
        this.picture = picture;

        return this;
    }

    public User deletePicture(){
        this.picture = null;

        return this;
    }

    public User updateNickname(String nickname){
        this.nickname = nickname;

        return this;
    }

    public User updateIntroduction(String introduction){
        this.introduction = introduction;

        return this;
    }

    public User updateJob(String job){
        this.job = job;

        return this;
    }

    public User updateYearCareer(Integer yearCareer){
        this.yearCareer = yearCareer;

        return this;
    }

    public User updateDomain(String domain){
        this.domain = domain;

        return this;
    }

    public String getRoleKey(){
        return this.role.getKey();
    }
}
package com.spring.martadmin.user.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_no")
    private int no; // 사용자 고유번호

    @Column(name = "user_email", length = 45, nullable = false, unique = true)
    private String email; // 사용자 이메일

    @Column(name = "user_pw", length = 70)
    private String password; // 사용자 비밀번호

    @Column(name = "user_temp_pw", columnDefinition = "boolean default false")
    private boolean tempPassword; // 사용자 임시비밀번호 여부

    @Column(name = "user_tel", length = 45, unique = true)
    private String tel; // 사용자 전화번호

    @Column(name = "user_name", length = 45)
    private String name; // 사용자 이름

    @Enumerated(EnumType.STRING)
    @Column(name = "user_sns_type", length = 45, nullable = false)
    private AuthProvider snsType; // 사용자 SNS 연동 타입

    @Column(name = "user_sns_key", length = 45, unique = true)
    private String snsKey; // 사용자 SNS 고유 key

    @Builder
    public User(String email, String password, boolean tempPassword, String tel, String name, AuthProvider snsType, String snsKey) {
        this.email = email;
        this.password = password;
        this.tempPassword = tempPassword;
        this.tel = tel;
        this.name = name;
        this.snsType = snsType;
        this.snsKey = snsKey;
    }

    // 비밀번호 변경
    public void setPassword(String password) {
        this.password = password;
    }

    // 임시 비밀번호 여부 True로 변경
    public void makeTrueTempPw() {
        if(!this.isTempPassword()) {
            this.tempPassword = true;
        }
    }

    // 임시 비밀번호 여부 False로 변경
    public void makeFalseTempPw() {
        if(this.isTempPassword()) {
            this.tempPassword = false;
        }
    }

    // 소셜 로그인 유저 이메일 변경
    public void setEmail(String email) {
        this.email = email;
    }

    // 소셜 로그인 유저 이름 변경
    public void setName(String name) {
        this.name = name;
    }

}

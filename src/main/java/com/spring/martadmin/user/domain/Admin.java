package com.spring.martadmin.user.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.spring.martadmin.advice.BaseTimeEntity;
import com.spring.martadmin.market.domain.Market;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "admin")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자 사용을 막음
public class Admin extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_no")
    private int no; // 관리자 고유번호

    @Column(name = "admin_id", length = 45, nullable = false, unique = true)
    private String id; //관리자 아이디

    @Column(name = "admin_pw", length = 70, nullable = false)
    private String password; //관리자 비밀번호

    public void setId(String id) {
        this.id = id;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Builder
    public Admin(String id, String password, List roles) {
        this.id = id;
        this.password = password;
    }
}

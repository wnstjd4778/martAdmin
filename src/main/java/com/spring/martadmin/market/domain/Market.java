package com.spring.martadmin.market.domain;

import com.spring.martadmin.advice.BaseTimeEntity;
import com.spring.martadmin.security.UserPrincipal;
import com.spring.martadmin.user.domain.Admin;
import com.spring.martadmin.user.domain.User;
import io.swagger.annotations.*;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;

import javax.persistence.*;
import java.text.ParseException;
import java.util.Date;

@Getter
@Entity
@Table(name = "market")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Market extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "market_no")
    private int no; // 마켓 고유번호

    @Column(name = "market_name", length = 45)
    private String name; // 마켓 이름

    @Column(name = "market_tel", length = 45, unique = true)
    private String tel; // 마켓 전화번호

    @Column(name = "market_open_time")
    private String openTime; // 마켓 오픈 시간

    @Column(name = "market_close_time")
    private String closeTime; // 마켓 클로스 시간

    @Column(name = "market_location")
    private String location; // 마켓 위치

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_no")
    private Admin admin; // 관리자 고유번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no")
    private User user; // 유저 고유번호

    public void setAdmin(Admin admin) {
        this.admin = admin;
        admin.getMarkets().add(this); // 양방향 연관관계 설정
    }

    @Builder
    public Market(String name, String tel, String openTime, String closeTime, String location, Admin admin, User user) {
        this.name = name;
        this.tel = tel;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.location = location;
        this.admin = admin;
        this.user = user;
    }

    public static Market createMarket(Admin admin, User user, String name, String tel, String openTime, String closeTime, String location) {
        Market market = Market.builder()
                .admin(admin)
                .name(name)
                .tel(tel)
                .openTime(openTime)
                .closeTime(closeTime)
                .location(location)
                .user(user)
                .build();

        return market;
    }

    public Market update(String name, String tel, String openTime, String closeTime, String location) throws ParseException {
        if (this.user != null && this.admin != null) { // 양방향 연관관계를 다시 생성하기위해 기존의 관계를 제거
            this.admin.getMarkets().remove(this);
            this.user.getMarkets().remove(this);
        }
        this.name = name;
        this.tel = tel;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.location = location;

        user.getMarkets().add(this); // 양방향 연관관계 설정
        admin.getMarkets().add(this); // 양방향 연관관계 설정

        return this;
    }


}

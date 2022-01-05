package com.spring.martadmin.market.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.spring.martadmin.advice.BaseTimeEntity;
import com.spring.martadmin.product.domain.Product;
import com.spring.martadmin.product.domain.ProductDetail;
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
import java.util.*;

@Getter
@Setter
@Entity
@Table(name = "market")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Market extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "market_no")
    private Integer no; // 마켓 고유번호

    @Column(name = "market_name", length = 45)
    private String name; // 마켓 이름

    @Column(name = "market_tel", length = 45)
    private String tel; // 마켓 전화번호

    @Column(name = "market_open_time")
    private String openTime; // 마켓 오픈 시간

    @Column(name = "market_close_time")
    private String closeTime; // 마켓 클로스 시간

    @Column(name = "market_location")
    private String location; // 마켓 위치

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_no")
    private Admin admin; // 관리자 고유번호


    @Builder
    public Market(String name, String tel, String openTime, String closeTime, String location, Admin admin) {
        this.name = name;
        this.tel = tel;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.location = location;
        this.admin = admin;
    }

    public static Market createMarket(Admin admin, String name, String tel, String openTime, String closeTime, String location) {
        Market market = Market.builder()
                .admin(admin)
                .name(name)
                .tel(tel)
                .openTime(openTime)
                .closeTime(closeTime)
                .location(location)
                .build();

        return market;
    }

    public Market update(String name, String tel, String openTime, String closeTime, String location) throws ParseException {

        this.name = name;
        this.tel = tel;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.location = location;

        return this;
    }

    public static Market subscribeMarket(Admin admin, String name, String tel, String openTime, String closeTime, String location) {
        Market market = Market.builder()
                .admin(admin)
                .name(name)
                .tel(tel)
                .openTime(openTime)
                .closeTime(closeTime)
                .location(location)
                .build();

        return market;
    }

}

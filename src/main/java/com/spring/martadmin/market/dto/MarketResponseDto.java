package com.spring.martadmin.market.dto;

import com.spring.martadmin.market.domain.Market;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


@Getter
@AllArgsConstructor
@Builder
public class MarketResponseDto {

    private Integer marketNo; // 마켓 고유번호
    private String name; // 마켓 이름
    private String tel; // 마켓 전화번호
    private String openTime; // 마켓 오픈 시간
    private String closeTime; // 마켓 클로스 시간
    private String location; // 마켓 위치

    public static MarketResponseDto of(Market market) {
        return MarketResponseDto.builder()
                .marketNo(market.getNo())
                .name(market.getName())
                .tel(market.getTel())
                .openTime(market.getOpenTime())
                .closeTime(market.getCloseTime())
                .location(market.getLocation())
                .build();
    }
}

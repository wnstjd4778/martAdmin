package com.spring.martadmin.market.dto;

import com.spring.martadmin.market.domain.Market;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class SubscriptionResponseDto {

    private List<Market> markets; // 3개의 마켓을 가져온다.

    public static SubscriptionResponseDto of(List<Market> markets) {
        return SubscriptionResponseDto.builder()
                .markets(markets)
                .build();
    }
}

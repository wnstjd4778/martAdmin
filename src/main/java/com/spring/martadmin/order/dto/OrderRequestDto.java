package com.spring.martadmin.order.dto;

import lombok.Data;

import javax.validation.constraints.PositiveOrZero;

@Data
public class OrderRequestDto {

    @PositiveOrZero(message = "주문할 카트의 고유번호를 입력해주세요")
    private int cartNo;
}

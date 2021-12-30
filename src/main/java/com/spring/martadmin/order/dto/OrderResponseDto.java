package com.spring.martadmin.order.dto;

import com.spring.martadmin.order.domain.Order;
import com.spring.martadmin.order.domain.OrderDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@Builder
public class OrderResponseDto {

    private int orderNo; // 주문 고유번호
    private String state; // 주문 상태
    private List<OrderDetailDto> orderDetails;
    private int totalPrice; // 결제금액

    public static OrderResponseDto of(Order order) {
        return OrderResponseDto.builder()
                .orderNo(order.getNo())
                .state(order.getState())
                .orderDetails(order.getOrderDetails().stream().map(orderDetail -> new OrderDetailDto(orderDetail)).collect(Collectors.toList()))
                .totalPrice(order.getTotalPrice())
                .build();
    }

    @Data
    static class OrderDetailDto {
        private int productNo; // 구매 제품 고유 번호
        private String productName; // 구매 상품명
        private int count; // 구매 수량
        private int price; // 수량 포함 제품 가격
        private String status; // 주문 상태

        public OrderDetailDto(OrderDetail orderDetail) {
            this.productNo = orderDetail.getProductDetail().getProduct().getNo();
            this.productName = orderDetail.getProductDetail().getProduct().getName();
            this.count = orderDetail.getCount();
            this.price = orderDetail.getPrice();
            this.status = orderDetail.getStatus();
        }
    }
}

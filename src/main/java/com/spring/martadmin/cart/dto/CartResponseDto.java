package com.spring.martadmin.cart.dto;

import com.spring.martadmin.cart.domain.Cart;
import com.spring.martadmin.cart.domain.CartItem;
import com.spring.martadmin.product.domain.ProductDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
public class CartResponseDto {

    private int cartNo; // 장바구니 번호
    private int totalPrice; // 장바구니 총 금액
    private List<CartItemDto> cartItems; // 장바구니에 담은 상품

    @Data
    static class CartItemDto {
        private int productNo; // 제품 고유번호
        private String productName; // 제품 이름
        private int productPrice; // 제품 판매가격
        private int count; // 장바구니에 담은 제픔 수량

        public CartItemDto(CartItem cartItem) {
            this.productNo = cartItem.getProductDetail().getProduct().getNo();
            this.productName = cartItem.getProductDetail().getProduct().getName();
            this.productPrice = cartItem.getProductDetail().getOnSalePrice();
            this.count = cartItem.getCount();
        }
    }

    public static CartResponseDto of(List<CartItem> cartItems, Cart cart) {
        return CartResponseDto.builder()
                .cartItems(cartItems.stream().map(cartItem -> new CartItemDto(cartItem)).collect(Collectors.toList()))
                .cartNo(cart.getNo())
                .build();
    }
}

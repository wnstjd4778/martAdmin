package com.spring.martadmin.cart.domain;

import com.spring.martadmin.advice.BaseTimeEntity;
import com.spring.martadmin.product.domain.Product;
import com.spring.martadmin.product.domain.ProductDetail;
import com.spring.martadmin.user.domain.User;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cart_item")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class CartItem extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_item_no")
    private int no; // 상세 장바구니 고유번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_no", nullable = false)
    private Cart cart; // 장바구니 고유번호

    @Column(name = "cart_item_count")
    private int count; // 담은 상품 수량

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_detail_no", nullable = false)
    private ProductDetail productDetail; // 제품 상세 고유번호

    public static CartItem createCartItem(Cart cart, ProductDetail productDetail) {
        CartItem cartItem = CartItem.builder()
                .cart(cart)
                .count(1)
                .productDetail(productDetail)
                .build();

        return cartItem;
    }

    public void addCartItem() {
        this.count++;
    }

    public void subCartItem() {
        this.count--;
    }

    // 카트에 담긴 물건 삭제
    public void removeCartItem() {
        this.cart = null;
        this.productDetail = null;
    }
}

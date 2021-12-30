package com.spring.martadmin.cart.domain;

import com.spring.martadmin.advice.BaseTimeEntity;
import com.spring.martadmin.user.domain.User;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cart")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cart extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_no")
    private int no; // 장바구니 고유번호

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no")
    private User user; // 고객 고유번호

    @OneToMany(mappedBy = "cart", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<CartItem> cartItems = new ArrayList<>();


    // 장바구니 생성
    public static Cart createCart(User user) {
        Cart cart = Cart.builder()
                .user(user)
                .build();

        return cart;
    }

    // 장바구니 삭제
    public void clear() {
        this.user = null;
    }

    //해당 유저의 장바구니중 특정 제품을 삭제
    public void removeCartItem(CartItem cartItem) {
        this.cartItems.remove(cartItem);
    }

    public int getTotalPrice(){
        int totalPrice = 0;
        for(CartItem cartItem : cartItems) {
            totalPrice += (cartItem.getCount() * cartItem.getProductDetail().getOnSalePrice());
        }
        return totalPrice;
    }


}

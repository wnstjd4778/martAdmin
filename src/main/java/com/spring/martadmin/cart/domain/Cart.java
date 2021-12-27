package com.spring.martadmin.cart.domain;

import com.spring.martadmin.advice.BaseTimeEntity;
import com.spring.martadmin.user.domain.User;
import lombok.*;

import javax.persistence.*;

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



}

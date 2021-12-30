package com.spring.martadmin.order.domain;

import com.spring.martadmin.advice.BaseTimeEntity;
import com.spring.martadmin.user.domain.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "order_product")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_product_no")
    private int no; // 주문 고유 번호

    @ManyToOne
    @JoinColumn(name = "user_no")
    private User user; // 고객 고유 번호

    @Column(name = "order_product_state", length = 45, nullable = false)
    private String state; // 주문 상태[ORDER, CANCEL]

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private List<OrderDetail> orderDetails = new ArrayList<>();

    public void setUser(User user) {
        this.user = user;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void addOrderDetail(OrderDetail orderDetail) {
        orderDetails.add(orderDetail);
        orderDetail.setOrder(this);
    }

    // 주문 금액 조회
    public int getTotalPrice() {
        int totalPrice = 0;
        for(OrderDetail orderDetail : orderDetails) {
            totalPrice += orderDetail.getPrice();
        }
        return totalPrice;
    }

    //주문 생성
    public static Order createOrder(User user, List<OrderDetail> orderDetails) {
        Order order = new Order(); // 주문 생성
        order.setUser(user);
        for(OrderDetail orderDetail: orderDetails) {
            order.addOrderDetail(orderDetail);
        }
        order.setState("ORDER");
        return order;
    }

    // 주문 취소
    public void cancel() {
        setState("CANCEL");
        for(OrderDetail orderDetail : orderDetails) {
            orderDetail.cancel();
        }
    }
}

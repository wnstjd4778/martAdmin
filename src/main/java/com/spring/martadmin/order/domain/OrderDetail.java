package com.spring.martadmin.order.domain;

import com.spring.martadmin.product.domain.ProductDetail;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "order_detail")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_detail_no")
    private int no; // 주문 세부정보 고유번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_product_no", nullable = false)
    private Order order; // 주문 고유번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_detail_no", nullable = false)
    private ProductDetail productDetail;

    @Column(name = "order_detail_count")
    private int count; // 구매수량

    @Column(name = "order_detail_price")
    private int price; // 수량 포함 제품 가격

    @Column(name = "order_detail_status")
    private String status; // 주문 제품 상태

    public void setOrder(Order order) {
        this.order = order;
    }

    public void setProductDetail(ProductDetail productDetail) {
        this.productDetail = productDetail;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setStatus(String status) { this.status = status; }

    // 주문 상품에 대한 정보 생성
    public static OrderDetail createOrderDetail(ProductDetail productDetail, int count) {
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setProductDetail(productDetail);
        orderDetail.setCount(count);
        orderDetail.setPrice(count * productDetail.getOnSalePrice());
        orderDetail.setStatus("ORDER");

        return orderDetail;
    }

    public void cancel() {
        setStatus("CANCEL");
    }
}

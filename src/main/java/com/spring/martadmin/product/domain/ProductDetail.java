package com.spring.martadmin.product.domain;

import com.spring.martadmin.advice.BaseTimeEntity;
import com.spring.martadmin.cart.domain.Cart;
import com.spring.martadmin.cart.domain.CartItem;
import com.spring.martadmin.market.domain.Market;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "product_detail")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductDetail extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_detail_no")
    private int no;

    @Column(name = "product_detail_is_sale")
    private int onSale; // 제품 세일하는지

    @Column(name = "product_detail_on_sale_price")
    private int onSalePrice; // 할인된 제품 가격

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "market_no")
    private Market market;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_no")
    private Product product;


    @Builder
    public ProductDetail(int no, int onSale, int onSalePrice, Market market, Product product) {
        this.no = no;
        this.onSale = onSale;
        this.onSalePrice = onSalePrice;
        this.market = market;
        this.product = product;
    }

    public static ProductDetail createProductDetail(int onSale, int onSalePrice, Market market, Product product) {
        ProductDetail productDetail = ProductDetail.builder()
                .onSale(onSale)
                .onSalePrice(onSalePrice)
                .market(market)
                .product(product)
                .build();

        return productDetail;
    }

    public ProductDetail updateProductDetail(int onSale, int onSalePrice) {

        this.onSale = onSale;
        this.onSalePrice = onSalePrice;

        return this;
    }


}

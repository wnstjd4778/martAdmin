package com.spring.martadmin.product.domain;

import com.spring.martadmin.advice.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "product")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseTimeEntity {

    @Id
    @Column(name = "product_no")
    private int no; // 제품 고유번호

    @Column(name = "product_name", length = 45, unique = true)
    private String name; // 제품 이름

    @Column(name = "product_price")
    private int price; // 제품 가격

    @Column(name = "product_img_url", length = 100)
    private String imgUrl; // 제품이미지 주소


    //제품 생성
    public static Product createProduct(int no, String name, int price) {

        Product product = Product.builder()
                .no(no)
                .name(name)
                .price(price)
                .build();

        return product;
    }

    //해당 제품 imgUrl수정
    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}

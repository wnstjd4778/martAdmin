package com.spring.martadmin.product.dto;

import com.spring.martadmin.market.domain.Market;
import com.spring.martadmin.product.domain.Product;
import com.spring.martadmin.product.domain.ProductDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;

@Getter
@Builder
@AllArgsConstructor
public class ProductResponseDto {

    private int no; // 제품 고유번호
    private String name; // 제품 이름
    private int price; // 제품 가격
    private String imgUrl; // 제품이미지 주소

    public static ProductResponseDto of(Product product) {
        return ProductResponseDto.builder()
                .name(product.getName())
                .price(product.getPrice())
                .imgUrl(product.getImgUrl())
                .build();
    }
}

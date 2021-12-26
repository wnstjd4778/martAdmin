package com.spring.martadmin.product.dto;

import com.spring.martadmin.product.domain.ProductDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Builder
@AllArgsConstructor
public class ProductDetailResponseDto {

    private int no;
    private int onSale; // 제품 세일하는지
    private int onSalePrice; // 할인된 제품 가격

    public static ProductDetailResponseDto of(ProductDetail productDetail) {
        return ProductDetailResponseDto.builder()
                .onSale(productDetail.getOnSale())
                .onSalePrice(productDetail.getOnSalePrice())
                .no(productDetail.getNo())
                .build();
    }
}

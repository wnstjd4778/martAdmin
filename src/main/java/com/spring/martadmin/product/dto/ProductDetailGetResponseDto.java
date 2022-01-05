package com.spring.martadmin.product.dto;

import com.spring.martadmin.product.domain.ProductDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class ProductDetailGetResponseDto {

    private List<ProductDetail> productDetails;

    public static ProductDetailGetResponseDto of(List<ProductDetail> productDetails) {
        return ProductDetailGetResponseDto.builder()
                .productDetails(productDetails)
                .build();
    }
}

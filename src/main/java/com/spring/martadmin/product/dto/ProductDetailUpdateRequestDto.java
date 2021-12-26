package com.spring.martadmin.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
public class ProductDetailUpdateRequestDto {

    private int productDetailNo;

    private int marketNO;

    private int productNo;

    //@NotBlank(message = "상품이 세일중인지 입력하시오(true,false)")
    private int onSale; // 제품 세일하는지

    //@NotBlank(message = "상품이 세일중이라면 세일중인 가격을 입력하시오")
    private int onSalePrice; // 할인된 제품 가격

}

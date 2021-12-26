package com.spring.martadmin.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;

@Data
@Builder
@AllArgsConstructor
public class ProductSaveRequestDto {

    //@NotBlank(message = "상품의 바코드 번호를 입력해주세요")
    private int no; // 상품 바코드 번호

    @NotBlank(message = "상품명을 입력해주세요")
    private String name; // 상품명

    @PositiveOrZero(message = "상품의 원가를 입력해주세요")
    private int price;

    private MultipartFile img; // 제품 이미지
}

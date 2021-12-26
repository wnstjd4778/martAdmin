package com.spring.martadmin.product.controller;


import com.spring.martadmin.product.dto.ProductResponseDto;
import com.spring.martadmin.product.dto.ProductSaveRequestDto;
import com.spring.martadmin.product.service.ProductService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Api(tags = "Product Management")
@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    @ApiOperation(value = "상품 추가", notes = "새로운 상품을 등록한다.", authorizations = { @Authorization(value = "jwtToken")})
    @ApiResponses({
            @ApiResponse(code = 201, message = "상품이 정상적으로 등록되었습니다."),
            @ApiResponse(code = 400, message = "유효한 입력값이 아닙니다."),
            @ApiResponse(code = 403, message = "상품 등록에 실패하였습니다.")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("")
    public ResponseEntity<ProductResponseDto> saveProduct(@ApiParam("등록할 상품 정보") @Valid @RequestBody ProductSaveRequestDto requestDto) throws Exception {
        ProductResponseDto productResponseDto = productService.saveProduct(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(productResponseDto);
    }
}

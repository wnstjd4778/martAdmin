package com.spring.martadmin.product.controller;


import com.spring.martadmin.product.dto.*;
import com.spring.martadmin.product.service.ProductDetailService;
import com.spring.martadmin.security.UserPrincipal;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/product_detail")
public class ProductDetailController {

    private final ProductDetailService productDetailService;

    @ApiOperation(value = "해당 마켓의 상품 추가", notes = "해당 마켓의 상품을 추가한다.", authorizations = { @Authorization(value = "jwtToken")})
    @ApiResponses({
            @ApiResponse(code = 201, message = "해당 마켓의 상품을 추가하였습니다."),
            @ApiResponse(code = 401, message = "유효한 입력값이 아닙니다."),
            @ApiResponse(code = 403, message = "상품 등록에 실패하였습니다.")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("")
    public ResponseEntity<ProductDetailResponseDto> saveProductDetail(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                                      @ApiParam("마켓에 등록할 상품의 정보") @Valid @RequestBody ProductDetailSaveRequestDto requestDto) throws Exception {
        ProductDetailResponseDto productDetailResponseDto = productDetailService.saveProductDetail(requestDto, userPrincipal.getNo());
        return ResponseEntity.status(HttpStatus.CREATED).body(productDetailResponseDto);
    }



    @ApiOperation(value = "해당 마켓의 상품 수정", notes = "해당 마켓의 상품을 수정한다.", authorizations = { @Authorization(value = "jwtToken")})
    @ApiResponses({
            @ApiResponse(code = 201, message = "해당 마켓의 상품을 수정하였습니다."),
            @ApiResponse(code = 401, message = "유효한 입력값이 아닙니다."),
            @ApiResponse(code = 403, message = "상품 수정에 실패하였습니다.")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("")
    public ResponseEntity<ProductDetailResponseDto> updateProductDetail(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                                        @ApiParam("마켓에 등록할 상품의 정보") @Valid @RequestBody ProductDetailUpdateRequestDto requestDto) throws Exception {
        ProductDetailResponseDto productDetailResponseDto = productDetailService.updateProductDetail(requestDto,userPrincipal.getNo());
        return ResponseEntity.status(HttpStatus.OK).body(productDetailResponseDto);
    }


    @ApiOperation(value = "해당 마켓의 상품 삭제", notes = "해당 마켓의 상품을 삭제한다.", authorizations = { @Authorization(value = "jwtToken")})
    @ApiResponses({
            @ApiResponse(code = 201, message = "해당 마켓의 상품을 삭제하였습니다."),
            @ApiResponse(code = 401, message = "유효한 입력값이 아닙니다."),
            @ApiResponse(code = 403, message = "상품 삭제에 실패하였습니다.")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("")
    public ResponseEntity<ProductDetailResponseDto> deleteProductDetail(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                                        @ApiParam("마켓에 등록할 상품의 정보") @Valid @RequestBody ProductDetailDeleteRequestDto requestDto) throws Exception {
        ProductDetailResponseDto productDetailResponseDto = productDetailService.deleteProductDetail(requestDto, userPrincipal.getNo());
        return ResponseEntity.status(HttpStatus.OK).body(productDetailResponseDto);
    }

    @ApiOperation(value = "해당 마켓의 상품을 모두 가져온다", notes = "해당 마켓의 상품을 모두 가져온다", authorizations = { @Authorization(value = "jwtToken")})
    @ApiResponses({
            @ApiResponse(code = 201, message = "해당 마켓의 상품을 모두 가져왔습니다."),
            @ApiResponse(code = 401, message = "유효한 입력값이 아닙니다."),
            @ApiResponse(code = 403, message = "상품 가져오기에 실패하였습니다.")
    })
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public ResponseEntity<ProductDetailGetResponseDto> getProductDetails(@ApiParam("마켓의 고유번호") @RequestParam(value = "marketNo") int marketNo) throws Exception {
        log.info("{}", marketNo);
        ProductDetailGetResponseDto productDetailGetResponseDto = productDetailService.getProductDetails(marketNo);
        return ResponseEntity.status(HttpStatus.OK).body(productDetailGetResponseDto);
    }
}

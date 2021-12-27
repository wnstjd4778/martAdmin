package com.spring.martadmin.cart.controller;


import com.spring.martadmin.advice.exception.SessionUnstableException;
import com.spring.martadmin.cart.dto.CartAddProductRequestDto;
import com.spring.martadmin.cart.dto.CartRemoveProductRequestDto;
import com.spring.martadmin.cart.dto.CartResponseDto;
import com.spring.martadmin.cart.service.CartService;
import com.spring.martadmin.security.UserPrincipal;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = {"Shopping cart"})
@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @ApiOperation(value = "장바구니 상품 추가", notes = "장바구니에 상품을 추가한다.", authorizations = { @Authorization(value = "jwtToken")})
    @ApiResponses({
            @ApiResponse(code = 201, message = "정상적으로 장바구니에 상품이 추가되었습니다."),
            @ApiResponse(code = 400, message = "유효한 입력이 아닙니다."),
            @ApiResponse(code = 403, message = "유저만 접근 가능"),
            @ApiResponse(code = 404, message = "해당 제품이 존재하지 않습니다.")
    })
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("")
    public ResponseEntity<Void> addProductInCart(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                 @ApiParam("상품 정보") @Valid @RequestBody CartAddProductRequestDto requestDto) throws SessionUnstableException {
        cartService.addProductInCart(userPrincipal.getNo(), requestDto.getProductDetailNo());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @ApiOperation(value = "장바구니 조회", notes = "장바구니에 상품을 조회한다..", authorizations = { @Authorization(value = "jwtToken")})
    @ApiResponses({
            @ApiResponse(code = 200, message = "정상적으로 장바구니에 상품이 조회되었습니다."),
            @ApiResponse(code = 400, message = "유효한 입력이 아닙니다."),
            @ApiResponse(code = 403, message = "유저만 접근 가능"),
            @ApiResponse(code = 404, message = "해당 유저의 장바구니가 존재하지 않습니다.")
    })
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public ResponseEntity<CartResponseDto> showUserCart(@AuthenticationPrincipal UserPrincipal userPrincipal) throws SessionUnstableException {
        CartResponseDto cartResponseDto = cartService.showUserCart(userPrincipal.getNo());
        return ResponseEntity.status(HttpStatus.OK).body(cartResponseDto);
    }


    @ApiOperation(value = "장바구니 상품 갯수 증가", notes = "+버튼을 누르면 장바구니 화면에서 상품 갯수가 하나 오른다.", authorizations = { @Authorization(value = "jwtToken")})
    @ApiResponses({
            @ApiResponse(code = 201, message = "정상적으로 장바구니에 상품 갯수가 증가되었습니다."),
            @ApiResponse(code = 400, message = "유효한 입력이 아닙니다."),
            @ApiResponse(code = 403, message = "유저만 접근 가능"),
            @ApiResponse(code = 404, message = "해당 상품을 찾을 수 없습니다.")
    })
    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping("/add/{productDetailNo}")
    public ResponseEntity<Void> addProductCount(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                @PathVariable int productDetailNo) throws SessionUnstableException {
        cartService.addProduct(userPrincipal.getNo(), productDetailNo);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @ApiOperation(value = "장바구니 상품 개수 감소", notes = "-버튼을 누르면 장바구니 화면에서 상품 개수가 1개 감소한다.", authorizations = { @Authorization(value = "jwtToken")})
    @ApiResponses({
            @ApiResponse(code = 201, message = "정상적으로 장바구니에 상품 갯수가 감소되었습니다."),
            @ApiResponse(code = 400, message = "유효한 입력이 아닙니다."),
            @ApiResponse(code = 403, message = "유저만 접근 가능"),
            @ApiResponse(code = 404, message = "해당 상품을 찾을 수 없습니다.")
    })
    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping("/sub/{productDetailNo}")
    public ResponseEntity<Void> subProductCount(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                @PathVariable int productDetailNo) throws SessionUnstableException {
        cartService.subProduct(userPrincipal.getNo(), productDetailNo);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @ApiOperation(value = "장바구니 상품 삭제", notes = "장바구니에서 상품을 삭제한다.", authorizations = { @Authorization(value = "jwtToken")})
    @ApiResponses({
            @ApiResponse(code = 204, message = "정상적으로 장바구니의 상품이 삭제되었습니다."),
            @ApiResponse(code = 400, message = "유효한 입력이 아닙니다."),
            @ApiResponse(code = 403, message = "유저만 접근 가능"),
            @ApiResponse(code = 404, message = "해당 상품을 찾을 수 없습니다.")
    })
    @PreAuthorize("hasRole('ROLE_USER')")
    @DeleteMapping("")
    public ResponseEntity<Void> deleteProduct(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                              @ApiParam("삭제하려는 상품의 고유번호") @Valid @RequestBody CartRemoveProductRequestDto requestDto) throws SessionUnstableException {
        cartService.takeProductOutOfCart(userPrincipal.getNo(), requestDto.getProductDetailNo());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

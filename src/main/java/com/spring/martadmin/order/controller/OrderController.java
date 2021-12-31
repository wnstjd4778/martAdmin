package com.spring.martadmin.order.controller;

import com.spring.martadmin.advice.exception.NotFoundDataException;
import com.spring.martadmin.cart.domain.Cart;
import com.spring.martadmin.cart.repository.CartRepository;
import com.spring.martadmin.order.dto.OrderRequestDto;
import com.spring.martadmin.order.dto.OrderResponseDto;
import com.spring.martadmin.order.dto.ResponseDataDto;
import com.spring.martadmin.order.service.OrderService;
import com.spring.martadmin.security.UserPrincipal;
import com.spring.martadmin.utility.HttpService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Api(tags = {"User Payment"})
@RestController
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;
    private final CartRepository cartRepository;
    private final HttpService httpService;

    @ApiOperation(value = "주문 하기", notes = "결제한 상품들을 주문한다.", authorizations = { @Authorization(value = "jwtToken")})
    @ApiResponses({
            @ApiResponse(code = 201, message = "정상적으로 주문되었습니다."),
            @ApiResponse(code = 400, message = "장바구니에 상품을 담아주세요"),
            @ApiResponse(code = 403, message = "유저만 접근 가능")
    })
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/purchase")
    public ResponseEntity<OrderResponseDto> order(@ApiParam("주문하는 고객 번호와 주문할 카트 번호") @AuthenticationPrincipal UserPrincipal userPrincipal,
                                                  @RequestBody @Valid OrderRequestDto requestDto) throws Exception {
        OrderResponseDto orderResponseDto = orderService.order(userPrincipal.getNo(), requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderResponseDto);
    }

    @ApiOperation(value = "주문 취소", notes = "해당 주문건을 취소한다.", authorizations = { @Authorization(value = "jwtToken")})
    @ApiImplicitParam(name = "orderNo", value = "취소할 주문 번호", required = true, dataType = "int", defaultValue = "1")
    @ApiResponses({
            @ApiResponse(code = 201, message = "해당 주문이 취소되었습니다."),
            @ApiResponse(code = 403, message = "관리자만 접근 가능"),
            @ApiResponse(code = 404, message = "취소할 주문이 존재하지 않습니다.")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/purchase/{orderNo}/cancel")
    public ResponseEntity<String> cancel(@PathVariable int orderNo) {
        orderService.cancelAll(orderNo);
        return ResponseEntity.status(HttpStatus.CREATED).body("해당 주문이 취소되었습니다.");
    }

}

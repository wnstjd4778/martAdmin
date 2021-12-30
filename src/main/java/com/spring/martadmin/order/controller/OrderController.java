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

//    @Value("${spring.iamPort.key}")
//    private String iamPortKey;
//
//    @Value("${spring.iamPort.secret")
//    private String iamPortSecret;

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

//    @ApiOperation(value = "결제정보 일치여부 확인", notes = "조회된 결제정보(status,amount)가 올바른지 체크한다.", authorizations = { @Authorization(value = "jwtToken")})
//    @ApiResponses({
//            @ApiResponse(code = 200, message = "조회된 결제정보가 일치하는지 확인된 결과를 true 또는 false로 응답합니다."),
//            @ApiResponse(code = 403, message = "유저만 접근 가능")
//    })
//    @PreAuthorize("hasRole('USER')")
//    @GetMapping("/payment/certification")
//    public ResponseEntity<Boolean> confirmPaymentInfo(@RequestParam String imp_uid, @RequestParam String merchant_uid,
//                                                      @RequestParam boolean imp_success, @RequestParam int cart_no) throws Exception {
//
//        String postUrl = "https://api.iamport.kr/users/getToken";
//
//        UriComponents postBuilder = UriComponentsBuilder.fromHttpUrl(postUrl).build(false);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//
//        params.add("imp_key", iamPortKey);
//        params.add("imp_secret", iamPortSecret);
//
//        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
//
//        ResponseDataDto postResult = httpService.post(postBuilder, request, ResponseDataDto.class);
//
//        String token = null;
//        for(Field field : postResult.getResponse().getClass().getDeclaredFields()) {
//            field.setAccessible(true);
//            String value = field.get(postResult.getResponse()).toString();
//            if(value.startsWith("access_token")) {
//                int idx = value.indexOf("=");
//                token = value.substring(idx + 1);
//                break;
//            }
//        }
//
//        // encode하면 제대로 요청이 되지 않는다.
//        String getUrl = "https://api.iamport.kr/payments/find/{merchant_uid}/paid";
//        UriComponents getBuilder = UriComponentsBuilder.fromHttpUrl(getUrl).build(false).expand(merchant_uid);
//        HttpHeaders httpHeaders = new HttpHeaders();
//        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
//        headers.add("X-ImpTokenHeader", token);
//
//        ResponseDataDto<Map<String, Object>> getResult = httpService.get(getBuilder, headers).getBody();
//        JSONObject jsonObject = httpService.MapConverterToJson(getResult.getResponse());
//
//        String status = (String) jsonObject.get("status");
//
//        if(status.equals("paid")) {
//            Integer amount = (Integer) jsonObject.get("amount");
//            Cart cart = cartRepository.findByNo(cart_no)
//                    .orElseThrow(() -> new NotFoundDataException("카트를 불러올 수 없습니다."));
//
//            if(amount == cart.getTotalPrice()) {
//                return ResponseEntity.status(HttpStatus.OK).body(false);
//            }
//        }
//
//        return ResponseEntity.status(HttpStatus.OK).body(false);
//    }
}

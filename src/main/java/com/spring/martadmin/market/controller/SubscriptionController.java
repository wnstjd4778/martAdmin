package com.spring.martadmin.market.controller;

import com.spring.martadmin.advice.exception.NotFoundDataException;
import com.spring.martadmin.market.dto.SubscriptionResponseDto;
import com.spring.martadmin.market.service.SubscriptionService;
import com.spring.martadmin.security.UserPrincipal;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@Api(tags = "구독")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/subscription")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @ApiOperation(value = "유저가 마켓을 구독", notes = "유저가 마트를 구독한다.", authorizations = { @Authorization(value = "jwtToken")})
    @ApiResponses({
            @ApiResponse(code = 200, message = "정상적으로 구독이 완료되었습니다."),
            @ApiResponse(code = 404, message = "해당 마켓을 찾을 수 없습니다.")
    })
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/{marketNo}")
    public ResponseEntity<String> subscribeMarket(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                  @ApiParam(value = "마켓 고유번호", example = "0") @PathVariable Integer marketNo) throws NotFoundDataException {
        log.info("{}", marketNo);
        subscriptionService.subscribeMarket(userPrincipal.getNo(), marketNo);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation(value = "유저가 마켓을 구독취소", notes = "유저가 마트를 구독을 취소한다.", authorizations = { @Authorization(value = "jwtToken")})
    @ApiResponses({
            @ApiResponse(code = 200, message = "정상적으로 구독 취소가 완료되었습니다."),
            @ApiResponse(code = 404, message = "해당 마켓을 찾을 수 없습니다.")
    })
    @PreAuthorize("hasRole('ROLE_USER')")
    @DeleteMapping("/{marketNo}")
    public ResponseEntity<String> unSubscribeMarket(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                  @ApiParam(value = "마켓 고유번호", example = "0") @PathVariable Integer marketNo) throws NotFoundDataException {
        log.info("{}", marketNo);
        subscriptionService.unSubscribeMarket(userPrincipal.getNo(), marketNo);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation(value = "유저가 구독한 마켓을 가져온다(3개)", notes = "유저가 구독한 마켓을 가져온다(3개)", authorizations = { @Authorization(value = "jwtToken")})
    @ApiResponses({
            @ApiResponse(code = 200, message = "정상적으로 마켓을 가져왔습니다."),
            @ApiResponse(code = 404, message = "해당 마켓을 찾을 수 없습니다.")
    })
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public ResponseEntity<SubscriptionResponseDto> getSubscribeMarket(@AuthenticationPrincipal UserPrincipal userPrincipal) throws NotFoundDataException {
        SubscriptionResponseDto subscriptionResponseDto = subscriptionService.getSubscribeMarket(userPrincipal.getNo());

        return ResponseEntity.status(HttpStatus.OK).body(subscriptionResponseDto);
    }


}

package com.spring.martadmin.market.controller;

import com.spring.martadmin.advice.exception.NotFoundDataException;
import com.spring.martadmin.market.dto.MarketResponseDto;
import com.spring.martadmin.market.dto.MarketSaveRequestDto;
import com.spring.martadmin.market.dto.MarketUpdateRequestDto;
import com.spring.martadmin.market.service.MarketService;
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

@Api(tags = "Market")
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/market")
public class MarketController {

    private final MarketService marketService;

    @ApiOperation(value = "마켓 추가", notes = "새로운 마켓을 추가한다.", authorizations = { @Authorization(value = "jwtToken")})
    @ApiResponses({
            @ApiResponse(code = 201, message = "마켓이 정상적으로 등록되었습니다."),
            @ApiResponse(code = 400, message = "유효한 입력값이 아닙니다."),
            @ApiResponse(code = 403, message = "마켓 등록에 실패하였습니다.")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("")
    public ResponseEntity<MarketResponseDto> saverMarket(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                         @ApiParam("등록할 마켓 정보") @Valid @RequestBody MarketSaveRequestDto requestDto) throws Exception {
        requestDto.setAdminNo(userPrincipal.getNo()); // userPrincipal에서 관리자 고유번호를 얻은후 넣는다.
        MarketResponseDto marketResponseDto = marketService.saveMarket(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(marketResponseDto);
    }

    @ApiOperation(value = "마켓 수정", notes = "마켓을 수정한다.", authorizations = { @Authorization(value = "jwtToken")})
    @ApiResponses({
            @ApiResponse(code = 201, message = "마켓이 정상적으로 수정되었습니다."),
            @ApiResponse(code = 400, message = "유효한 입력값이 아닙니다."),
            @ApiResponse(code = 403, message = "관리자만 접근가능"),
            @ApiResponse(code = 404, message = "수정할 마켓이 존재하지 않음.")
    })
    @PreAuthorize(("hasRole('ROLE_ADMIN')"))
    @PutMapping("/{marketNo}")
    public ResponseEntity<MarketResponseDto> updateMarket(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                          @ApiParam("수정할 마켓 정보") @Valid @RequestBody MarketUpdateRequestDto requestDto,
                                                          @ApiParam(value = "마켓 고유 번호", example = "0") @PathVariable Integer marketNo) throws Exception {
        requestDto.setMarketNo(marketNo);
        requestDto.setAdminNo(userPrincipal.getNo()); // userPrincipal에서 관리자 고유번호를 찾아서 넣는다.
        MarketResponseDto marketResponseDto = marketService.updateMarket(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(marketResponseDto);
    }

    @ApiOperation(value = "마켓 제거", notes = "마켓을 삭제한다.", authorizations = { @Authorization(value = "jwtToken")})
    @ApiResponses({
            @ApiResponse(code = 200, message = "마켓이 정상적으로 삭제되었습니다."),
            @ApiResponse(code = 401, message = "관리자가 달라 삭제할 수 없습니다."),
            @ApiResponse(code = 403, message = "상품 삭제를 실패하였습니다.(마켓 고유번호 오류)")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{marketNo}")
    public ResponseEntity<String> removeMarket(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                               @ApiParam(value = "마켓 고유번호", example = "0") @PathVariable Integer marketNo) {
        marketService.removeMarket(userPrincipal.getNo(), marketNo);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

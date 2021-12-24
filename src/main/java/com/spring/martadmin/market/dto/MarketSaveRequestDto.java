package com.spring.martadmin.market.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Date;

@Data
@Builder
public class MarketSaveRequestDto {

    private Integer adminNo;

    @NotBlank(message = "마켓이름을 입력해주세요.")
    private String name; // 마켓 이름

    @Pattern(regexp = "^01(?:0|1|[6-9])(\\d{3}|\\d{4})(\\d{4})$", message = "10 ~ 11 자리의 숫자만 입력해주세요.")
    private String tel; // 마켓 전화번호

    @NotBlank(message = "마켓 오픈시간을 입력해주세요.")
    private String openTime; // 마켓 오픈 시간

    @NotBlank(message = "마켓 클로스시간을 입력해주세요.")
    private String closeTime; // 마켓 클로스 시간

    @NotBlank(message = "마켓 위치를 입력해주세요.")
    private String location; // 마켓 위치

}

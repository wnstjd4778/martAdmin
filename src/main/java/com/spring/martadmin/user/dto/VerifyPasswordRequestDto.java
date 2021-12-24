package com.spring.martadmin.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyPasswordRequestDto {

    @NotBlank(message = "패스워드를 입력해주세요")
    private String password; // 사용자 패스워드
}

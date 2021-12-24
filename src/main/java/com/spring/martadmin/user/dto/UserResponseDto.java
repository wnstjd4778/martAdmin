package com.spring.martadmin.user.dto;

import com.spring.martadmin.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor
public class UserResponseDto {

    private String email;
    private String tel;
    private String name;

    public static UserResponseDto of(User user) {
        return UserResponseDto.builder()
                .email(user.getEmail())
                .tel(user.getTel())
                .name(user.getName())
                .build();
    }

    public static List<UserResponseDto> listOf(List<User> users) {
        return users.stream().map(UserResponseDto::of)
                .collect(Collectors.toList());
    }
}

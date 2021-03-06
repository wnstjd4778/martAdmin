package com.spring.martadmin.order.dto;

import lombok.Data;

@Data
public class ResponseDataDto<T> {

    private String code;
    private String message;
    private T response;
}

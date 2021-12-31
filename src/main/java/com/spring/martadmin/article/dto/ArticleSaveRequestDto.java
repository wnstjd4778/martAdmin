package com.spring.martadmin.article.dto;

import lombok.Data;

import javax.persistence.Column;

@Data
public class ArticleSaveRequestDto {

    private String title;
    private String content;


}

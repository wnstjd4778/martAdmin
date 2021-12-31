package com.spring.martadmin.article.dto;

import lombok.Data;

@Data
public class ArticleReplySaveRequestDto {

    private int articleNo;
    private String content;
}

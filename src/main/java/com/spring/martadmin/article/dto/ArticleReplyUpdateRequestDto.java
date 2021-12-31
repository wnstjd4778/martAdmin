package com.spring.martadmin.article.dto;

import lombok.Data;

@Data
public class ArticleReplyUpdateRequestDto {

    private int articleReplyNo;
    private int articleNo;
    private String content;
}

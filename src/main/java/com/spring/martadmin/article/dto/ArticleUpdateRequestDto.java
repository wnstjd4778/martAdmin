package com.spring.martadmin.article.dto;

import com.spring.martadmin.user.domain.User;
import lombok.Data;

@Data
public class ArticleUpdateRequestDto {

    private int articleNo;
    private String title;
    private String content;


}

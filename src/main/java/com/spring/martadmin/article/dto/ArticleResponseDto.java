package com.spring.martadmin.article.dto;

import com.spring.martadmin.article.domain.Article;
import com.spring.martadmin.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.persistence.Column;

@Data
@Builder
@AllArgsConstructor
public class ArticleResponseDto {

    private int no;

    private String title;

    private String content;

    private int viewCount;

    private String name;


    public static ArticleResponseDto of(Article article) {
        return ArticleResponseDto.builder()
                .no(article.getNo())
                .title(article.getTitle())
                .content(article.getContent())
                .viewCount(article.getViewCount())
                .name(article.getUser().getName())
                .build();
    }
}

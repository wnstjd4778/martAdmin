package com.spring.martadmin.article.dto;

import com.spring.martadmin.article.domain.Article;
import com.spring.martadmin.article.domain.ArticleReply;
import com.spring.martadmin.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ArticleReplyResponseDto {

    private int articleReplyNo;
    private String content;
    private User user;
    private Article article;

    public static ArticleReplyResponseDto of(ArticleReply articleReply) {
        return ArticleReplyResponseDto.builder()
                .articleReplyNo(articleReply.getNo())
                .content(articleReply.getContent())
                .user(articleReply.getUser())
                .article(articleReply.getArticle())
                .build();
    }
}

package com.spring.martadmin.article.domain;

import com.spring.martadmin.advice.BaseTimeEntity;
import com.spring.martadmin.user.domain.User;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "article_reply")
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArticleReply extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "article_reply_no")
    private int no;

    @Column(name = "article_reply_content")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_no")
    private Article article;

    public static ArticleReply createArticle(String content, User user, Article article) {
        return ArticleReply.builder()
                .content(content)
                .article(article)
                .user(user)
                .build();
    }

    public ArticleReply updateArticle(String content) {
        this.content = content;
        return this;
    }
}

package com.spring.martadmin.article.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.spring.martadmin.advice.BaseTimeEntity;
import com.spring.martadmin.user.domain.User;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "article")
@AllArgsConstructor
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Article extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "article_no")
    private int no;

    @Column(name = "article_title")
    private String title;

    @Column(name = "article_content")
    private String content;

    @Column(name = "article_view_count")
    private int viewCount;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no")
    private User user;

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public static Article createArticle(String title, String content, User user) {
        Article article = Article.builder()
                .title(title)
                .content(content)
                .viewCount(0)
                .user(user)
                .build();

        return article;
    }

    public Article updateArticle(String title, String content) {
        this.title = title;
        this.content = content;
        return this;
    }

    public Article plusViewCount(Article article) {
        article.setViewCount(article.getViewCount() + 1);
        return article;
    }
}

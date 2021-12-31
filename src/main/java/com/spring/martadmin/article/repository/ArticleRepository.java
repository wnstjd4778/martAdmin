package com.spring.martadmin.article.repository;

import com.spring.martadmin.article.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArticleRepository extends JpaRepository<Article, Integer> {

    Optional<Article> findByNo(int articleNo);
}

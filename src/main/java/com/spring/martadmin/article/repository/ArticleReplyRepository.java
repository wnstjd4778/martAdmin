package com.spring.martadmin.article.repository;

import com.spring.martadmin.article.domain.ArticleReply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArticleReplyRepository extends JpaRepository<ArticleReply, Integer> {

    Optional<ArticleReply> findByNo(int articleReplyNo);
}

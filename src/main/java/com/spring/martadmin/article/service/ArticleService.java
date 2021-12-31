package com.spring.martadmin.article.service;

import com.spring.martadmin.advice.exception.NotFoundDataException;
import com.spring.martadmin.advice.exception.SessionUnstableException;
import com.spring.martadmin.article.domain.Article;
import com.spring.martadmin.article.dto.ArticleDeleteRequestDto;
import com.spring.martadmin.article.dto.ArticleResponseDto;
import com.spring.martadmin.article.dto.ArticleSaveRequestDto;
import com.spring.martadmin.article.dto.ArticleUpdateRequestDto;
import com.spring.martadmin.article.repository.ArticleRepository;
import com.spring.martadmin.user.domain.User;
import com.spring.martadmin.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;

    // article을 db에 저장
    public ArticleResponseDto saveArticle(int userNo, ArticleSaveRequestDto requestDto) {
        User user = userRepository.findByNo(userNo)
                .orElseThrow(() -> new SessionUnstableException("해당 유저를 찾을 수 없습니디."));

        Article article = Article.createArticle(requestDto.getTitle(), requestDto.getContent(), user);
        articleRepository.save(article);
        return ArticleResponseDto.of(article);
    }

    // article을 수정
    @Transactional
    public ArticleResponseDto updateArticle(int userNo, ArticleUpdateRequestDto requestDto) {
        User user = userRepository.findByNo(userNo)
                .orElseThrow(() -> new SessionUnstableException("해당 유저를 찾을 수 없습니디."));

        Article article = articleRepository.findByNo(requestDto.getArticleNo())
                .orElseThrow(() -> new NotFoundDataException("해당 게시글을 찾을 수 없습니다."));

        if(user.getNo() == article.getUser().getNo()) {
            article = article.updateArticle(requestDto.getTitle(), requestDto.getContent());
            articleRepository.save(article);
        } else {
            throw new SessionUnstableException("게시글을 수정할 수 없습니다.");
        }

        return ArticleResponseDto.of(article);
    }

    // article삭제
    @Transactional
    public void deleteArticle(int userNo, ArticleDeleteRequestDto requestDto) {

        User user = userRepository.findByNo(userNo)
                .orElseThrow(() -> new SessionUnstableException("해당 유저를 찾을 수 없습니디."));

        Article article = articleRepository.findByNo(requestDto.getArticleNo())
                .orElseThrow(() -> new NotFoundDataException("해당 게시글을 찾을 수 없습니다."));

        if(user.getNo() == article.getUser().getNo()) {
            articleRepository.delete(article);
        } else {
            throw new SessionUnstableException("해당 게시글을 삭제할 수 없습니다.");
        }
    }

    // 게시글 조회
    @Transactional
    public ArticleResponseDto showArticle(int articleNo) {
        Article article = articleRepository.findByNo(articleNo)
                .orElseThrow(() -> new NotFoundDataException("해당 게시글을 찾을 수 없습니다."));
        article.plusViewCount(article);
        article = articleRepository.save(article);

        return ArticleResponseDto.of(article);
    }
}

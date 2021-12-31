package com.spring.martadmin.article.service;

import com.spring.martadmin.advice.exception.NotFoundDataException;
import com.spring.martadmin.advice.exception.SessionUnstableException;
import com.spring.martadmin.article.domain.Article;
import com.spring.martadmin.article.domain.ArticleReply;
import com.spring.martadmin.article.dto.*;
import com.spring.martadmin.article.repository.ArticleReplyRepository;
import com.spring.martadmin.article.repository.ArticleRepository;
import com.spring.martadmin.user.domain.User;
import com.spring.martadmin.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class ArticleReplyService {

    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final ArticleReplyRepository articleReplyRepository;

    // 댓글을 db에 저장
    @Transactional
    public ArticleReplyResponseDto saveArticleReply(int userNo, ArticleReplySaveRequestDto requestDto) {
        User user = userRepository.findByNo(userNo)
                .orElseThrow(() -> new SessionUnstableException("해당 유저를 찾을 수 없습니디."));

        Article article = articleRepository.findByNo(requestDto.getArticleNo())
                .orElseThrow(() -> new NotFoundDataException("해당 게시글을 찾을 수 없습니다."));
        ArticleReply articleReply = ArticleReply.createArticle(requestDto.getContent(), user, article);
        articleReplyRepository.save(articleReply);
        return ArticleReplyResponseDto.of(articleReply);
    }

    // 댓글을 수정
    @Transactional
    public ArticleReplyResponseDto updateArticleReply(int userNo, ArticleReplyUpdateRequestDto requestDto) {

        User user = userRepository.findByNo(userNo)
                .orElseThrow(() -> new SessionUnstableException("해당 유저를 찾을 수 없습니디."));

        Article article = articleRepository.findByNo(requestDto.getArticleNo())
                .orElseThrow(() -> new NotFoundDataException("해당 게시글을 찾을 수 없습니다."));

        ArticleReply articleReply = articleReplyRepository.findByNo(requestDto.getArticleReplyNo())
                .orElseThrow(() -> new NotFoundDataException("해당 댓글을 찾을 수 없습니다."));

        if(articleReply.getUser().getNo() == user.getNo()) {
            articleReply = articleReply.updateArticle(requestDto.getContent());
            articleReplyRepository.save(articleReply);
        } else {
            throw new SessionUnstableException("해당 댓글을 수정할 수 없습니다.");
        }
        return ArticleReplyResponseDto.of(articleReply);
    }

    // 댓글을 삭제
    @Transactional
    public void deleteArticleReply(int userNo, ArticleReplyDeleteRequestDto requestDto) {

        User user = userRepository.findByNo(userNo)
                .orElseThrow(() -> new SessionUnstableException("해당 유저를 찾을 수 없습니디."));

        Article article = articleRepository.findByNo(requestDto.getArticleNo())
                .orElseThrow(() -> new NotFoundDataException("해당 게시글을 찾을 수 없습니다."));

        ArticleReply articleReply = articleReplyRepository.findByNo(requestDto.getArticleReplyNo())
                .orElseThrow(() -> new NotFoundDataException("해당 댓글을 찾을 수 없습니다."));

        if(articleReply.getUser().getNo() == user.getNo()) {
            articleReplyRepository.delete(articleReply);
        } else {
            throw new SessionUnstableException("해당 댓글을 삭제할 수 없습니다.");
        }
    }

}

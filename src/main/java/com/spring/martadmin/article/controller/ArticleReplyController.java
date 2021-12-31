package com.spring.martadmin.article.controller;

import com.spring.martadmin.article.domain.ArticleReply;
import com.spring.martadmin.article.dto.ArticleReplyDeleteRequestDto;
import com.spring.martadmin.article.dto.ArticleReplyResponseDto;
import com.spring.martadmin.article.dto.ArticleReplySaveRequestDto;
import com.spring.martadmin.article.dto.ArticleReplyUpdateRequestDto;
import com.spring.martadmin.article.service.ArticleReplyService;
import com.spring.martadmin.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("article/reply")
public class ArticleReplyController {

    private final ArticleReplyService articleReplyService;

    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @PostMapping("")
    public ResponseEntity<ArticleReplyResponseDto> saveArticleReply(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                                      @RequestBody @Valid ArticleReplySaveRequestDto requestDto) throws Exception {
        ArticleReplyResponseDto articleReplyResponseDto = articleReplyService.saveArticleReply(userPrincipal.getNo(), requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(articleReplyResponseDto);
    }

    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @PutMapping("")
    public ResponseEntity<ArticleReplyResponseDto> updateArticleReply(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                                    @RequestBody @Valid ArticleReplyUpdateRequestDto requestDto) throws Exception {
        ArticleReplyResponseDto articleReplyResponseDto = articleReplyService.updateArticleReply(userPrincipal.getNo(), requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(articleReplyResponseDto);
    }

    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @DeleteMapping("")
    public ResponseEntity<String> deleteArticleReply(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                                    @RequestBody @Valid ArticleReplyDeleteRequestDto requestDto) throws Exception {
        articleReplyService.deleteArticleReply(userPrincipal.getNo(), requestDto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

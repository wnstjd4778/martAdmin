package com.spring.martadmin.article.controller;

import com.spring.martadmin.article.dto.ArticleDeleteRequestDto;
import com.spring.martadmin.article.dto.ArticleResponseDto;
import com.spring.martadmin.article.dto.ArticleSaveRequestDto;
import com.spring.martadmin.article.dto.ArticleUpdateRequestDto;
import com.spring.martadmin.article.service.ArticleService;
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
@RequestMapping("/article")
public class ArticleController {

    private final ArticleService articleService;

    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @PostMapping("")
    public ResponseEntity<ArticleResponseDto> saveArticle(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                          @RequestBody @Valid ArticleSaveRequestDto requestDto) throws Exception {
        ArticleResponseDto articleResponseDto = articleService.saveArticle(userPrincipal.getNo(), requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(articleResponseDto);
    }

    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @PutMapping("")
    public ResponseEntity<ArticleResponseDto> updateArticle(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                          @RequestBody @Valid ArticleUpdateRequestDto requestDto) throws Exception {
        ArticleResponseDto articleResponseDto = articleService.updateArticle(userPrincipal.getNo(), requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(articleResponseDto);
    }

    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @DeleteMapping("")
    public ResponseEntity<String> deleteArticle(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                @RequestBody @Valid ArticleDeleteRequestDto requestDto) throws Exception {
        articleService.deleteArticle(userPrincipal.getNo(), requestDto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{articleNo}")
    public ResponseEntity<ArticleResponseDto> showArticle(@PathVariable int articleNo) throws Exception {
        log.info("{}", articleNo);
        ArticleResponseDto articleResponseDto = articleService.showArticle(articleNo);
        return ResponseEntity.status(HttpStatus.OK).body(articleResponseDto);
    }
}

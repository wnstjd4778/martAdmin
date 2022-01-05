package com.spring.martadmin.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// 포론트 엔드에서 server의 api에 accsess 할 수 있도록 cors open
// 실제 실무에서는 각자 환경에 맞는 allowedOrigins를 정의해야한다.

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final long MAX_AGE_SECS = 3600;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // cor를 적용할 url 패턴
                .allowedOriginPatterns("*") // 자원을 공유를 허락할 origin을 설정
                .allowedMethods("GET", "POST", "PUT", "DELETE") // 요청 허용 메소드
                .allowedHeaders("*") // 요청을 허용하는 헤더
                .maxAge(MAX_AGE_SECS);
    }
}

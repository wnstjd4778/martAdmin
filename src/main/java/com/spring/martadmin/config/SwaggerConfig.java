package com.spring.martadmin.config;

import edu.emory.mathcs.backport.java.util.Arrays;
import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;;
import springfox.documentation.service.ApiKey;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static springfox.documentation.builders.RequestHandlerSelectors.withMethodAnnotation;

@Configuration
@EnableSwagger2 // Swagger 버전을 활성화
public class SwaggerConfig implements WebMvcConfigurer {
    private String version;
    private String title;

    @Bean
    public Docket restApi() {
        version = "Demo Version";
        title = "MartAdmin API" + version;
        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false) // 불필요한 응답코드(200, 401, 403, 404)와 메시지 제거
                .select()// ApiSelectorBuilder생성
                .apis(RequestHandlerSelectors.any()) // api 스펙이 작성되어 있는 패키지를 지정
                .paths(PathSelectors.any()) //apis()로 선택되어진 api중 특정 path 조건에 맞는 api를 다시 필러팅하여 문서화
                .build()
                .apiInfo(apiInfo(title, version)) // 제목, 설명 등 문서에 대한 정보들을 보여주기 위해 호출
                .ignoredParameterTypes(AuthenticationPrincipal.class)
                .select()
                    .apis(withMethodAnnotation(ApiOperation.class))
                .build();
   }

   // 제목 설명 등 문서에 대한 정보
    private ApiInfo apiInfo(String title, String version) {
        return new ApiInfoBuilder()
                .title(title)
                .description("마트들의 사이트")
                .version(version)
                .build();
    }

}

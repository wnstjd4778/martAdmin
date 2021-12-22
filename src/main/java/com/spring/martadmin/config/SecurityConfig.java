package com.spring.martadmin.config;

import com.spring.martadmin.security.CustomUserDetailsService;
import com.spring.martadmin.security.UserPrincipal;
import com.spring.martadmin.security.jwt.CustomAuthenticationEntryPoint;
import com.spring.martadmin.security.jwt.JwtBasicAuthenticationFilter;
import com.spring.martadmin.security.jwt.JwtCommonAuthorizationFilter;
import com.spring.martadmin.security.jwt.JwtTokenProvider;
import com.spring.martadmin.security.oauth2.CustomOAuth2UserService;
import com.spring.martadmin.user.repository.AdminRepository;
import com.spring.martadmin.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private JwtTokenProvider tokenProvider;

    // Authorization에 사용할 userDetailService와 password Encoder를 정의한다.
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder());
    }

    /*
     * 다른 AuthorizationServer나 ResourceServer가 참조할 수 있도록 오버라이딩 해서 빈으로 등록
     */
    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    // 스프링 시큐리티 필터를 통해 모든 필터링을 서버에서 처리
    protected void configure(HttpSecurity http) throws Exception {
        //로그인 성공 실패시 invoke할 Handler정의
        http
                .cors() // cors허용
                    .and()
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // jwt 토큰으로 인증하므로 세션 필요X
                    .and()
                .csrf().disable() // rest api이므로 csrf 보안 필요X
                .httpBasic().disable() // rest api이므로 기본설정 사용X. 기본 설정은 비인증시 로그인폼 화면으로 리다이렉트된다.
                .formLogin().disable()
                .addFilterBefore(new JwtBasicAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilter(new JwtCommonAuthorizationFilter(authenticationManager(), tokenProvider, userRepository, adminRepository))
                .authorizeRequests() // 요청에 대한 사용권한 체크
                    // 로그인 처리와 예외는 접근 가능
                    .antMatchers("/", "/*/signin/**", "/*/signup/**", "/test/token", "/*/find/**", "/oauth2/**", "/login/**", "/logout/**", "/error/**","/users").permitAll()
                    .antMatchers("/swagger-ui.html", "/swagger/**", "/swagger-resources/**", "/webjars/**", "/v2/api-docs").permitAll()
                    .antMatchers("/graphql").hasRole("ADMIN")
                    .anyRequest().authenticated() // 위에 주소 말고 모든 요청은 인증된 사용자만 접근 가능
                    .and()
                .exceptionHandling()
                    .authenticationEntryPoint(new CustomAuthenticationEntryPoint()) // 토큰이 만료되었을시 예외처리
                    .and()
                .oauth2Login() // oauth2 로그인 설정
                    .userInfoEndpoint() //로그인 시 사용할 User Service를 정의
                        .userService(customOAuth2UserService)
                        .and()
                    .successHandler(((request, response, authentication) -> {
                        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
                        String accessToken = tokenProvider.generateAccessToken(userPrincipal);
                        String refreshToken = tokenProvider.generateRefreshToken(userPrincipal); // redis 서버
                        response.addHeader("Authorization", "Bearer " + accessToken);
                        response.setStatus(HttpServletResponse.SC_OK);
                    }))
                    .failureHandler(((request, response, exception) -> {
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                    }));

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}

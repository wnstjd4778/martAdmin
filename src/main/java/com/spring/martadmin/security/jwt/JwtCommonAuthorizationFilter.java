package com.spring.martadmin.security.jwt;

import com.spring.martadmin.security.UserPrincipal;
import com.spring.martadmin.user.domain.Admin;
import com.spring.martadmin.user.domain.User;
import com.spring.martadmin.user.repository.AdminRepository;
import com.spring.martadmin.user.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

// HTTP 기본 인증 헤더를 처리하여 결과를 SecurityContextHolder에 저장한다.
@Slf4j
public class JwtCommonAuthorizationFilter extends BasicAuthenticationFilter {

    private UserRepository userRepository;
    private AdminRepository adminRepository;
    private JwtTokenProvider jwtTokenProvider;

    public JwtCommonAuthorizationFilter(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, UserRepository userRepository, AdminRepository adminRepository) {
        super(authenticationManager);
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
    }

    // request에서 Header(jwt token)을 획득 후, 해당 유저를 DB에서 찾아 인증을을 진행(Authentication 생성)
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String header = request.getHeader("Authorization"); // jwt token이 어디있는지 알기 위해, Authorization header를 찾는다.
        log.info("jwt토큰 이름 : " + header);
        // 만약 header에 Bearer가 포함되어 있지 않거나 header가 null이라면 작업을 끝낸다.
        if(header == null || !header.startsWith("Bearer")) {
            chain.doFilter(request, response);
            return;
        }

        // 토큰 인증을 위한 전처리
        HttpServletRequestWrapper myRequest = new HttpServletRequestWrapper((HttpServletRequest) request) {
            @Override
            public String getHeader(String name) {
                if(name.equals("Authorization")) {
                    String basic = request.getHeader("Authorization").replace("Bearer ", "");
                    return basic;
                }
                return super.getHeader(name);
            }
        };

        //만약 header가 존재한다면, DB로부터 user의 권한을 확인하고, authorization을 수행한다.
        Authentication authentication =
                getUsernamePasswordAuthentication(myRequest);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(myRequest, response); // filter 수행 계속함
    }

    // 헤더의 jwt token 내부에 있는 정보를 통해 DB와 일치하는 유저를 찾아 인증을 완료한다. (Userprincipal 객체 생성)
    private Authentication getUsernamePasswordAuthentication(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        String principal = null;
        UserPrincipal userPrincipal = null;


        try {
            principal = jwtTokenProvider.getPrincipal(token, JwtTokenProvider.TokenType.ACCESS_TOKEN);

            if(principal.contains("@")) { //User일 경우
                Optional<User> oUser = userRepository.findByEmail(principal);
                User user = oUser.get();
                userPrincipal = UserPrincipal.create(user);
            } else { // Admin일 경우
                Optional<Admin> oAdmin = adminRepository.findById(principal);
                Admin admin = oAdmin.get();
                userPrincipal = UserPrincipal.create(admin);
            }

            // OAuth인지 일반 로그인인지 구분할 필요가 없다. 왜냐하면 password를 Authentication이 가질 필요가 없기 때문이다.
            // JWT가 로그인 프로세스를 가로채서 인증을 다 해준다. (OAUTH2 일반로그인 둘다)

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return authentication;
        } catch (ExpiredJwtException e) {
            e.printStackTrace();
        }
        return null;
    }
}

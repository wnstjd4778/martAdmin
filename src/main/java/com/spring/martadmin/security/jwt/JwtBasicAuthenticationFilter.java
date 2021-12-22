package com.spring.martadmin.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.martadmin.security.CustomUserDetailsService;
import com.spring.martadmin.security.UserPrincipal;
import com.spring.martadmin.user.domain.User;
import com.spring.martadmin.user.dto.UserSignInRequestDto;
import com.spring.martadmin.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;


/**
 * id/pw를 통해 UsernamePasswordAuthenticationToken을 생성한 후,
 * AuthenticationManager가 AuthenticationProvider를 순회하며 인증 가능 여부를 체크한다.
 *      - 이때, AuthenticationProvider는 UserDetailService를 통해 사용자 정보 DB에서 조회
 * 인증이 완료되면 해당 authentication 객체를 SecurityContextHolder에 저장한 후 반환한다.
 */
public class JwtBasicAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        UserSignInRequestDto credentials = null;
        try {
            credentials = new ObjectMapper().readValue(request.getInputStream(), UserSignInRequestDto.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Optional<User> oUser = userRepository.findByEmail(credentials.getEmail());
        User user = oUser.get();
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getEmail());
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        credentials.getPassword()
                );
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }

    //인증이 성공하면 jwt토큰을 발급해준다.
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String accessToken = jwtTokenProvider.generateAccessToken(userPrincipal);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userPrincipal);
        response.addHeader("Authorization", "Bearer" + accessToken);
    }
}

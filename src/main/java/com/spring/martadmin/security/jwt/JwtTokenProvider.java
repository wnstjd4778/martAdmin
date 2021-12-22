package com.spring.martadmin.security.jwt;

import com.spring.martadmin.security.UserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;

@Service
@Slf4j
public class JwtTokenProvider {


    private String accessTokenSecret = "안녕";

    private String refreshTokenSecret = "안녕하";

    private static final long accessTokenExpiredMsc = 1000L * 60 * 60; // 1시간
    private static final long refreshTokenExpiredMsc = 1000L * 60 * 60 * 24 * 14; //2주

    private static final String HEADER_NAME = "Authorization";

    public enum TokenType {ACCESS_TOKEN, REFRESH_TOKEN}

    @PostConstruct
    protected void init() {
        accessTokenSecret = Base64.getEncoder().encodeToString(accessTokenSecret.getBytes());
        refreshTokenSecret = Base64.getEncoder().encodeToString(refreshTokenSecret.getBytes());
    }

    //Access 토큰 발급
    public String generateAccessToken(UserPrincipal userPrincipal) {
        return createToken(userPrincipal.getPrincipal(), TokenType.ACCESS_TOKEN);
    }

    //Refresh Token 발급
    public String generateRefreshToken(UserPrincipal userPrincipal) {
        return createToken(userPrincipal.getPrincipal(), TokenType.REFRESH_TOKEN);
    }

    //인증된 유저의 authentication에서 userPrincipal을 추출해 token을 생성
    public String createToken(String principal, TokenType tokenType) {

        String secretKey;
        long expireTime;

        if(tokenType == TokenType.ACCESS_TOKEN) {
            secretKey = accessTokenSecret;
            expireTime = accessTokenExpiredMsc;
        } else {
            secretKey = refreshTokenSecret;
            expireTime = refreshTokenExpiredMsc;
        }

        Claims claims = Jwts.claims().setSubject(principal);
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims) //데이터
                .setIssuedAt(now) // 토큰 발급일자
                .setExpiration(new Date(now.getTime() + expireTime)) // 토큰 만료시간
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    //Jwt 토큰 유효성 검사
    public boolean validateToken(String token) {
        String secretKey = accessTokenSecret;
        try {
            log.debug("validateToken's secretKey: " + secretKey);

            Jws<Claims> claims = Jwts.parser() // setSignKey를 통해 디지털 서명이 되었는지 확인
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token);

            boolean isNotExpire = !claims.getBody().getExpiration().before(new Date()); // 만료되면 false를 반환


            return isNotExpire;
        } catch (Exception e) {
            return false;
        }
    }

    // Jwt 토큰에서 회원 이메일 또는 관리자 아이디 추출
    public String getPrincipal(String token, TokenType tokenType) {
        String secretKey;

        if(tokenType == TokenType.ACCESS_TOKEN) {
            secretKey = accessTokenSecret;
        } else {
            secretKey = refreshTokenSecret;
        }
        return  Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Request의 Header에서 token 파싱
    public String extractToken(HttpServletRequest request) {
        return request.getHeader(HEADER_NAME);
    }

    public Date getExpirationDate(String token, TokenType tokenType) {
        String secretKey;

        if(tokenType == TokenType.ACCESS_TOKEN) {
            secretKey = accessTokenSecret;
        } else {
            secretKey = refreshTokenSecret;
        }

        log.debug("getExpirationDate's secretKey : " + secretKey);
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }
}

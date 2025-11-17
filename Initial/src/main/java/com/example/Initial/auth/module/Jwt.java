package com.example.Initial.auth.module;

import com.example.Initial.auth.config.JwtTokenConfigure;
import com.example.Initial.auth.config.JwtUserModel;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class Jwt {
    public final static String TOKEN_PREFIX = "Bearer ";
    // 토큰 유효시간24시간
    private final JwtTokenConfigure jwtTokenConfigure;

    // JWT 토큰 생성
    public String createToken(JwtUserModel user) {
        Claims claims = user.setupClaimsOnCreateToken();
        Date now = new Date();
        return Jwts.builder()
                .claims(claims) // 정보 저장
                .issuedAt(now) // 토큰 발행 시간 정보
                .expiration(new Date(now.getTime() + (jwtTokenConfigure.getExpiryMinutes() * 60000L))) // set Expire Time
                .signWith(Keys.hmacShaKeyFor(jwtTokenConfigure.getClientSecret().getBytes()))  // 사용할 암호화 알고리즘과 signature 에 들어갈 secret값 세팅
                .compact();
    }

    public Jws<Claims> parseToken(String token) {
        JwtParser parser = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(jwtTokenConfigure.getClientSecret().getBytes()))
                .build();
        return parser.parseSignedClaims(token);
    }
}

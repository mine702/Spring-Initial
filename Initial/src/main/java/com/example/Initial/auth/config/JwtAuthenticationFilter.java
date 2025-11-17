package com.example.Initial.auth.config;

import com.example.Initial.repository.token.TokenRepository;
import com.example.Initial.repository.user.UserRepository;
import com.google.gson.Gson;
import com.example.Initial.auth.module.Jwt;
import com.example.Initial.entity.auth.TokenEntity;
import com.example.Initial.entity.auth.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * JWT 로그인 커스터마이즈
 * 1. 디바이스로부터 accessToken 및 refreshToken 값이 있는지 확인한다.
 * 2. accessToken 만 받을 경우 accessToken 만 검사하여 인증처리를 진행한다.
 * 3. refreshToken 을 같이 받을 경우 refreshToken 을 검사하고 올바른 토큰이면 신규 accessToken 을 발급한다.
 * 4. refreshToken 토큰 검사 중 유효기간이 특정일 미만일 경우 refreshToken 도 신규 값을 발급 받아 DB에 저장한다.
 */

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilter {

    private final Gson gson;
    private final Jwt jwt;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final JwtTokenConfigure jwtTokenConfigure;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = ((HttpServletRequest) request);
        String uri = httpRequest.getRequestURI();

        Cookie[] cookies = httpRequest.getCookies();
        String accessToken = null;
        String refreshToken = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("accessToken")) accessToken = cookie.getValue();
                if (cookie.getName().equals("refreshToken")) refreshToken = cookie.getValue();
            }
        }

        Authentication auth = null;
        if (accessToken != null) {
            accessToken = accessToken.replace(Jwt.TOKEN_PREFIX, "");
            //파싱하면서 JWT 검사. 변조 확인시 excpetion이 발생함
            Jws<Claims> parsedToken;
            try {
                parsedToken = jwt.parseToken(accessToken);
                auth = new UserEntity().getAuthentication(parsedToken);
            } catch (ExpiredJwtException e) {
                //시간이 지난 경우에는 갱신 시도, 갱신 시도까지 실패하면 인증이 없는것으로 간주하고 필터 종료
                long id = Long.parseLong(e.getClaims().getSubject());

                //통과 조건 : 해당 유저가 DB에 존재할것
                Optional<UserEntity> optionalUser = userRepository.findById(id);
                if (!optionalUser.isPresent()) {
                    chain.doFilter(request, response);
                    return;
                }
                UserEntity user = optionalUser.get();

                //통과 조건 : refreshtoken이 발급되었을 것
                Optional<TokenEntity> optionalToken = tokenRepository.findByUserIdAndRefreshToken(id, refreshToken);
                if (!optionalToken.isPresent()) {
                    chain.doFilter(request, response);
                    return;
                }
                TokenEntity token = optionalToken.get();

                //refreshtoken이 만료되지 않았는지 검사
                LocalDateTime now = LocalDateTime.now();
                if (now.isAfter(token.getExpireDate())) {
                    tokenRepository.delete(token);
                    chain.doFilter(request, response);
                    return;
                }

                //refershtoken이 만료일 7일 이전일경우 refreshtoken 갱신
                if (now.isAfter(token.getExpireDate().minusDays(7))) {
                    token.setRefreshToken(UUID.randomUUID() + UUID.randomUUID().toString());
                    token.setExpireDate(now.plusDays(14));
                    tokenRepository.save(token);
                }

                //갱신된 access token과 refresh token을 헤더에 담아 전송
                accessToken = jwt.createToken(user);
                Jws<Claims> createdParsedToken = jwt.parseToken(accessToken);

                HttpServletResponse httpResponse = (HttpServletResponse) response;
                Cookie accessTokenCookie = new Cookie("accessToken", jwt.createToken(user));
                accessTokenCookie.setMaxAge(jwtTokenConfigure.getRefreshTokenExpiryDays() * 24 * 60 * 60);
                accessTokenCookie.setPath("/");
                httpResponse.addCookie(accessTokenCookie);
                Cookie refreshTokenCookie = new Cookie("refreshToken", token.getRefreshToken());
                refreshTokenCookie.setMaxAge(jwtTokenConfigure.getRefreshTokenExpiryDays() * 24 * 60 * 60);
                refreshTokenCookie.setPath("/");
                httpResponse.addCookie(refreshTokenCookie);

                auth = user.getAuthentication(createdParsedToken);

            } catch (Exception e) {
                //예외 발생시 인증이 없는것으로 간주하고 필터 종료
                chain.doFilter(request, response);
                return;
            }
        }

        // auth == null => access denied
        SecurityContextHolder.getContext().setAuthentication(auth);
        chain.doFilter(request, response);
    }
}

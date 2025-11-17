package com.example.Initial.auth.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.security.core.Authentication;

public interface JwtUserModel {
    /**
     * 이 메서드에 로그인/리프레시 할때 jwt에 등록할 클레임들을 저장합니다.
     */
    Claims setupClaimsOnCreateToken();

    /**
     * 이 메서드에 jwt에 등록된 클레임을 이용해 springSecurity에서 인식할 권한을 설정합니다.
     */
    Authentication getAuthentication(Jws<Claims> token);
}

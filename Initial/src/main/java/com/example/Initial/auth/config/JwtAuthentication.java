package com.example.Initial.auth.config;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.security.Principal;
import java.util.Collection;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class JwtAuthentication implements Authentication {
    protected long userId;
    protected String name;
    protected Jws<Claims> claims;
    protected Collection<GrantedAuthority> grantedAuthorities;

    public static Long getUserId(Principal principal) {
        return (Long) ((JwtAuthentication) principal).getPrincipal();
    }

    public static String getName(Principal principal) {
        return principal.getName();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return grantedAuthorities;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    /**
     * JWT 속성값 목록을 얻을 수 있습니다.
     */
    @Override
    public Object getDetails() {
        return claims;
    }

    /**
     * userId(Long)을 얻을 수 있습니다.
     */
    @Override
    public Object getPrincipal() {
        return userId;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
    }

    /**
     * JWT 속성값 목록을 얻을 수 있습니다.
     */
    @Override
    public String getName() {
        return name;
    }
}

package com.example.Initial.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jwt.token")
@Data
public class JwtTokenConfigure {
    private String header;
    private String issuer;
    private String clientSecret;
    private int expiryMinutes;
    private boolean allowMultiLogin;
    private boolean allowRefresh;
    private int refreshTokenExpiryDays;
}

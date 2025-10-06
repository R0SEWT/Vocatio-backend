package com.acme.vocatio.config;

import io.jsonwebtoken.security.Keys;
import java.time.Duration;
import java.util.Base64;
import javax.crypto.SecretKey;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
public class JwtProperties {

    private String secret;

    private Duration accessTokenTtl = Duration.ofMinutes(15);

    private Duration refreshTokenTtl = Duration.ofDays(7);

    private Duration rememberMeTtl = Duration.ofDays(30);

    public SecretKey getSigningKey() {
        if (!StringUtils.hasText(secret)) {
            throw new IllegalStateException("La propiedad jwt.secret no puede estar vacía");
        }
        byte[] decoded = Base64.getDecoder().decode(secret);
        return Keys.hmacShaKeyFor(decoded);
    }
}

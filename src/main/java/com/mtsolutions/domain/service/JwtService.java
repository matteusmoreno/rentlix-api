package com.mtsolutions.domain.service;

import com.mtsolutions.application.common.RequestParam;
import com.mtsolutions.application.component.DateComponent;
import com.mtsolutions.domain.entity.User;
import com.mtsolutions.domain.model.TokenResult;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.Duration;
import java.time.LocalDateTime;

@ApplicationScoped
@Slf4j
public class JwtService {

    private final String jwtIssuer;
    private final long tokenExpirationInHours;
    private final DateComponent dateComponent;

    public JwtService(@ConfigProperty(name = "mp.jwt.verify.issuer") String jwtIssuer,
                      @ConfigProperty(name = "app.auth.token-expiration-hours") long tokenExpirationInHours,
                      DateComponent dateComponent) {
        this.jwtIssuer = jwtIssuer;
        this.tokenExpirationInHours = tokenExpirationInHours;
        this.dateComponent = dateComponent;
    }

    public TokenResult generateToken(User user) {
        log.info("Generating JWT token for user: {}", user.getEmail());

        LocalDateTime expiresIn = this.dateComponent.now().plusHours(this.tokenExpirationInHours);
        String token = Jwt.issuer(this.jwtIssuer)
                .subject(user.getUserId().toString())
                .upn(user.getEmail())
                .claim(RequestParam.USER_ID, user.getUserId().toString())
                .claim("name", user.getName())
                .claim("emailVerified", user.getEmailVerified())
                .expiresIn(Duration.ofHours(this.tokenExpirationInHours))
                .sign();

        log.info("Generated JWT token for user: {}", user.getEmail());
        return new TokenResult(token, expiresIn);
    }
}

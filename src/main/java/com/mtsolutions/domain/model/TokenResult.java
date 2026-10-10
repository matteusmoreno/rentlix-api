package com.mtsolutions.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class TokenResult {
    private String token;
    private LocalDateTime expiresIn;
}

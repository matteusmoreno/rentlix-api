package com.mtsolutions.domain.dto.response;

import com.mtsolutions.domain.model.TokenResult;
import java.time.LocalDateTime;

public record LoginResponseDto (
        String token,
        LocalDateTime expiresIn
) {
    public LoginResponseDto(TokenResult tokenResult)
    {
        this(
                tokenResult.getToken(),
                tokenResult.getExpiresIn()
        );
    }
}

package com.mtsolutions.domain.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class PasswordService {

    public String encryptPassword(String password) {
        log.info("Encrypting password");
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }

    public boolean verifyPassword(String password, String hashedPassword) {
        log.info("Verifying password");
        BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), hashedPassword);
        return result.verified;
    }
}

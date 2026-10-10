package com.mtsolutions.domain.service;

import jakarta.enterprise.context.ApplicationScoped;

import java.security.SecureRandom;

@ApplicationScoped
public class UtilsService {

    private static final String VERIFICATION_CODE_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int VERIFICATION_CODE_LENGTH = 6;

    private final SecureRandom secureRandom = new SecureRandom();

    public String generateVerificationCode() {
        char[] code = new char[VERIFICATION_CODE_LENGTH];
        for (int index = 0; index < code.length; index++) {
            code[index] = VERIFICATION_CODE_ALPHABET.charAt(this.secureRandom.nextInt(VERIFICATION_CODE_ALPHABET.length()));
        }
        return new String(code);
    }
}

package com.mtsolutions.domain.constant;

import lombok.Getter;

@Getter
public enum UserRole {
    LESSOR("Lessor"), //LOCADOR
    LESSEE("Lessee"); //LOCATÁRIO

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }
}

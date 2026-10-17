package com.mtsolutions.application.component;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.security.Principal;
import java.util.UUID;

@RegisterForReflection
@Builder
@Getter
@EqualsAndHashCode
public class ContextUser implements Principal {

    private final UUID userId;
    private final String name;
    private final String email;
    private final Boolean emailVerified;
    private final Boolean admin;
}

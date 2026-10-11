package com.mtsolutions.application.component;

import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class ContextComponent {

    private final SecurityIdentity securityIdentity;

    public ContextComponent(SecurityIdentity securityIdentity) {
        this.securityIdentity = securityIdentity;
    }

    public ContextUser getCurrentUser() {
        if (this.securityIdentity.getPrincipal() instanceof ContextUser contextUser) {
            return contextUser;
        }
        return null;
    }
}

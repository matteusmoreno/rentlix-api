package com.mtsolutions.application.component;

import io.quarkus.security.identity.AuthenticationRequestContext;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.SecurityIdentityAugmentor;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.UUID;

@ApplicationScoped
public class ContextUserAugmentor implements SecurityIdentityAugmentor {

    @Override
    public Uni<SecurityIdentity> augment(SecurityIdentity identity, AuthenticationRequestContext context) {
        if (identity.isAnonymous() || !(identity.getPrincipal() instanceof JsonWebToken jwt) || jwt.getSubject() == null) {
            return Uni.createFrom().item(identity);
        }

        ContextUser contextUser = ContextUser.builder()
                .userId(UUID.fromString(jwt.getSubject()))
                .name(jwt.getClaim("name"))
                .email(jwt.getName())
                .emailVerified(jwt.getClaim("emailVerified"))
                .build();

        return Uni.createFrom().item(QuarkusSecurityIdentity.builder(identity)
                .setPrincipal(contextUser)
                .build());
    }
}

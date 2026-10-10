package com.mtsolutions.application.component;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.LocalDateTime;
import java.time.ZoneId;

@ApplicationScoped
@Slf4j
public class DateComponent {

    private final String serverTimezone;

    public DateComponent(@ConfigProperty(name = "app.server.timezone") String serverTimezone) {
        this.serverTimezone = serverTimezone;
    }

    public LocalDateTime now() {
        return LocalDateTime.now(ZoneId.of(serverTimezone));
    }

}


package com.mtsolutions.domain.service;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class EmailService {

    private final Mailer mailer;
    private final Template welcomeTemplate;
    private final Template verifyEmailTemplate;

    public EmailService(Mailer mailer,
                        @Location("emails/welcome.html") Template welcomeTemplate,
                        @Location("emails/verification.html") Template verifyEmailTemplate) {
        this.mailer = mailer;
        this.welcomeTemplate = welcomeTemplate;
        this.verifyEmailTemplate = verifyEmailTemplate;
    }

    public void sendWelcomeEmail(String name, String email) {
        log.info("Sending welcome email to: {}", email);
        try {
            String htmlBody = this.welcomeTemplate
                    .data("name", name)
                    .data("email", email)
                    .render();

            this.mailer.send(Mail.withHtml(email, "Bem-vindo à Rentlix", htmlBody));
            log.info("Welcome email sent to: {}", email);
        } catch (Exception exception) {
            log.error("Failed to send welcome email to {}: {}", email, exception.getMessage());
        }
    }

    public void sendVerificationEmail(String name, String email, String token, long expiresMinutes) {
        log.info("Sending verification email to: {}", email);
        try {
            String htmlBody = this.verifyEmailTemplate
                    .data("name", name)
                    .data("token", token)
                    .data("expiresMinutes", expiresMinutes)
                    .render();

            this.mailer.send(Mail.withHtml(email, "Confirme seu e-mail — Rentlix", htmlBody));
            log.info("Verification email sent to: {}", email);
        } catch (Exception exception) {
            log.error("Failed to send verification email to {}: {}", email, exception.getMessage());
        }
    }
}

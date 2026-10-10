package com.mtsolutions.domain.entity;

import com.mtsolutions.domain.constant.MaritalStatus;
import com.mtsolutions.domain.model.Address;
import com.mtsolutions.domain.model.UserDocument;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter @Setter
public class User {

    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private UUID userId;
    private String name;
    private String phone;
    private String cpf;
    private String rg;
    private String occupation;

    @Column(name = "marital_status")
    @Enumerated(EnumType.STRING)
    private MaritalStatus maritalStatus;

    @Embedded
    private Address address;
    private String email;
    @Column(name = "email_verified")
    private Boolean emailVerified;
    @Column(name = "email_verification_token")
    private String emailVerificationToken;
    @Column(name = "email_verification_token_expires_at")
    private LocalDateTime emailVerificationTokenExpiresAt;

    private String password;
    @Column(name = "password_reset_token")
    private String passwordResetToken;
    @Column(name = "password_reset_token_expires_at")
    private LocalDateTime passwordResetTokenExpiresAt;
    @Column(name = "picture_url")
    private String pictureUrl;

    @Embedded
    private UserDocument documents;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    private Boolean active;
}

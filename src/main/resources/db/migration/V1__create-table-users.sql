CREATE TABLE IF NOT EXISTS users (
    user_id                              UUID NOT NULL,
    name                                 VARCHAR(150) NOT NULL,
    phone                                VARCHAR(30) NOT NULL UNIQUE,
    cpf                                  VARCHAR(40) NOT NULL UNIQUE,
    rg                                   VARCHAR(40),
    occupation                           VARCHAR(150),
    marital_status                       VARCHAR(100),

    zip_code                             VARCHAR(50),
    street                               VARCHAR(190) NOT NULL,
    neighborhood                         VARCHAR(190) NOT NULL,
    city                                 VARCHAR(190) NOT NULL,
    state                                VARCHAR(190) NOT NULL,
    number                               VARCHAR(150),
    complement                           VARCHAR(150),

    email                                VARCHAR(200) NOT NULL UNIQUE,
    email_verified                       BOOLEAN,
    email_verification_token             VARCHAR(150),
    email_verification_token_expires_at  TIMESTAMP,

    password                             VARCHAR(255) NOT NULL,
    password_reset_token                 VARCHAR(150),
    password_reset_token_expires_at      TIMESTAMP,

    picture_url                          VARCHAR(255),
    cnh_url                              VARCHAR(255),
    cpf_url                              VARCHAR(255),
    rg_url                               VARCHAR(255),
    proof_of_residence_url               VARCHAR(255),
    criminal_record_url                  VARCHAR(255),
    passport_url                         VARCHAR(255),

    created_at                           TIMESTAMP,
    updated_at                           TIMESTAMP,
    active                               BOOLEAN,

    CONSTRAINT pk_users PRIMARY KEY (user_id)
);

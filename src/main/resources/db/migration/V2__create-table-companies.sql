CREATE TABLE IF NOT EXISTS companies (
    company_id   UUID NOT NULL,
    name         VARCHAR(150) NOT NULL,
    cnpj         VARCHAR(18) NOT NULL UNIQUE,
    plan         VARCHAR(30) NOT NULL,
    status       VARCHAR(30) NOT NULL,
    created_at   TIMESTAMP,
    updated_at   TIMESTAMP,

    CONSTRAINT pk_companies PRIMARY KEY (company_id)
);

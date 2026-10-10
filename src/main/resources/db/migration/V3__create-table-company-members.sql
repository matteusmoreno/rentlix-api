DROP TABLE IF EXISTS user_roles;

CREATE TABLE IF NOT EXISTS company_members (
    user_id    UUID NOT NULL,
    company_id UUID NOT NULL,
    role       VARCHAR(90) NOT NULL,

    CONSTRAINT pk_company_members PRIMARY KEY (user_id, company_id),
    CONSTRAINT fk_company_members_user FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
    CONSTRAINT fk_company_members_company FOREIGN KEY (company_id) REFERENCES companies (company_id) ON DELETE CASCADE
);

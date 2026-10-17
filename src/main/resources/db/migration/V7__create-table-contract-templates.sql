CREATE TABLE IF NOT EXISTS contract_templates (
    contract_template_id  UUID NOT NULL,
    company_id            UUID NOT NULL,
    category              VARCHAR(20) NOT NULL,
    content               TEXT NOT NULL,
    created_at            TIMESTAMP,
    updated_at            TIMESTAMP,

    CONSTRAINT pk_contract_templates PRIMARY KEY (contract_template_id),
    CONSTRAINT fk_contract_templates_company FOREIGN KEY (company_id) REFERENCES companies (company_id),
    CONSTRAINT uk_contract_templates_company_category UNIQUE (company_id, category)
);

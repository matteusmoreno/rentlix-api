CREATE TABLE IF NOT EXISTS financials (
    financial_id  UUID NOT NULL,
    company_id    UUID NOT NULL,
    asset_id      UUID,
    rental_id     UUID,
    direction     VARCHAR(20) NOT NULL,
    type          VARCHAR(30) NOT NULL,
    amount        NUMERIC(12, 2) NOT NULL,
    description   VARCHAR(255) NOT NULL,
    occurred_on   DATE NOT NULL,
    created_at    TIMESTAMP,
    updated_at    TIMESTAMP,

    CONSTRAINT pk_financials PRIMARY KEY (financial_id),
    CONSTRAINT fk_financials_company FOREIGN KEY (company_id) REFERENCES companies (company_id),
    CONSTRAINT fk_financials_asset FOREIGN KEY (asset_id) REFERENCES assets (asset_id),
    CONSTRAINT fk_financials_rental FOREIGN KEY (rental_id) REFERENCES rentals (rental_id)
);

CREATE INDEX IF NOT EXISTS idx_financials_company ON financials (company_id);

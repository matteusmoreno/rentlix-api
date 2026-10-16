CREATE TABLE IF NOT EXISTS rentals (
    rental_id     UUID NOT NULL,
    asset_id      UUID NOT NULL,
    lessee_id     UUID NOT NULL,
    start_date    DATE NOT NULL,
    end_date      DATE NOT NULL,
    price         NUMERIC(12, 2) NOT NULL,
    price_period  VARCHAR(20) NOT NULL,
    contract_url  VARCHAR(255),
    status        VARCHAR(30) NOT NULL,
    created_at    TIMESTAMP,
    updated_at    TIMESTAMP,

    CONSTRAINT pk_rentals PRIMARY KEY (rental_id),
    CONSTRAINT fk_rentals_asset FOREIGN KEY (asset_id) REFERENCES assets (asset_id),
    CONSTRAINT fk_rentals_lessee FOREIGN KEY (lessee_id) REFERENCES users (user_id)
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_rentals_active_asset ON rentals (asset_id) WHERE status = 'ACTIVE';

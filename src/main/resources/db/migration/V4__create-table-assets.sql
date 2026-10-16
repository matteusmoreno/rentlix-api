CREATE TABLE IF NOT EXISTS assets (
    asset_id      UUID NOT NULL,
    company_id    UUID NOT NULL,
    type          VARCHAR(30) NOT NULL,
    name          VARCHAR(150) NOT NULL,
    description   VARCHAR(1000),
    image_url     VARCHAR(255),
    status        VARCHAR(30) NOT NULL,
    price         NUMERIC(12, 2) NOT NULL,
    price_period  VARCHAR(20) NOT NULL,

    brand             VARCHAR(80),
    model             VARCHAR(80),
    manufacture_year  INTEGER,
    model_year        INTEGER,
    color             VARCHAR(40),
    plate             VARCHAR(10),
    renavam           VARCHAR(11),
    chassis           VARCHAR(17),
    engine_cc         INTEGER,
    mileage           INTEGER,

    zip_code              VARCHAR(50),
    street                VARCHAR(190),
    neighborhood          VARCHAR(190),
    city                  VARCHAR(190),
    state                 VARCHAR(190),
    number                VARCHAR(150),
    complement            VARCHAR(150),
    total_area            INTEGER,
    bedrooms              INTEGER,
    suites                INTEGER,
    bathrooms             INTEGER,
    parking_spaces        INTEGER,
    furnished             BOOLEAN,
    pet_friendly          BOOLEAN,
    has_elevator          BOOLEAN,
    has_pool              BOOLEAN,
    has_gym               BOOLEAN,
    has_barbecue          BOOLEAN,
    condo_fee             NUMERIC(12, 2),
    iptu                  NUMERIC(12, 2),
    registration_number   VARCHAR(40),

    created_at    TIMESTAMP,
    updated_at    TIMESTAMP,

    CONSTRAINT pk_assets PRIMARY KEY (asset_id),
    CONSTRAINT fk_assets_company FOREIGN KEY (company_id) REFERENCES companies (company_id)
);

CREATE TABLE IF NOT EXISTS asset_gallery (
    asset_id   UUID NOT NULL,
    position   INTEGER NOT NULL,
    image_url  VARCHAR(255) NOT NULL,

    CONSTRAINT pk_asset_gallery PRIMARY KEY (asset_id, position),
    CONSTRAINT fk_asset_gallery_asset FOREIGN KEY (asset_id) REFERENCES assets (asset_id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_assets_plate ON assets (plate) WHERE plate IS NOT NULL;
CREATE UNIQUE INDEX IF NOT EXISTS uk_assets_renavam ON assets (renavam) WHERE renavam IS NOT NULL;
CREATE UNIQUE INDEX IF NOT EXISTS uk_assets_chassis ON assets (chassis) WHERE chassis IS NOT NULL;

CREATE TABLE IF NOT EXISTS plans (
    plan_id    UUID NOT NULL,
    name       VARCHAR(150) NOT NULL,
    price      NUMERIC(12, 2) NOT NULL,
    fixed      BOOLEAN NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT pk_plans PRIMARY KEY (plan_id),
    CONSTRAINT uk_plans_name UNIQUE (name)
);

INSERT INTO plans (plan_id, name, price, fixed, created_at, updated_at)
SELECT gen_random_uuid(), 'FREE', 0, TRUE, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM plans WHERE name = 'FREE');

INSERT INTO plans (plan_id, name, price, fixed, created_at, updated_at)
SELECT gen_random_uuid(), company.plan, 0, FALSE, NOW(), NOW()
FROM companies company
WHERE company.plan <> 'FREE'
  AND NOT EXISTS (SELECT 1 FROM plans WHERE plans.name = company.plan)
GROUP BY company.plan;

ALTER TABLE companies ADD COLUMN IF NOT EXISTS plan_id UUID;

UPDATE companies company
SET plan_id = plan.plan_id
FROM plans plan
WHERE company.plan_id IS NULL
  AND plan.name = company.plan;

ALTER TABLE companies ALTER COLUMN plan_id SET NOT NULL;

ALTER TABLE companies DROP CONSTRAINT IF EXISTS fk_companies_plan;
ALTER TABLE companies
    ADD CONSTRAINT fk_companies_plan FOREIGN KEY (plan_id) REFERENCES plans (plan_id);

ALTER TABLE companies DROP COLUMN IF EXISTS plan;

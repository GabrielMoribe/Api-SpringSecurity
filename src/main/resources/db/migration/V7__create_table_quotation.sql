CREATE TABLE quotations (
    id SERIAL PRIMARY KEY,
    client_id INTEGER NOT NULL,
    health_plan_id INTEGER NOT NULL,
    final_price NUMERIC(19, 2) NOT NULL,
    beneficiaries_ages JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_quotation_client FOREIGN KEY (client_id) REFERENCES clients(id),
    CONSTRAINT fk_quotation_plan FOREIGN KEY (health_plan_id) REFERENCES health_plans(id)
);
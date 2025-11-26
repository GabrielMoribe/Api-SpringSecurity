CREATE TABLE health_plans (
    id serial PRIMARY KEY ,
    name VARCHAR(100) NOT NULL,             --nome do plano
    operator VARCHAR(100) NOT NULL,         --operadora (amil , bradesco ...)
    operator_code VARCHAR(50) NOT NULL,     --codigo da operadora
    base_price NUMERIC(12,2) NOT NULL,      --preço base do plano
    age_factor JSONB NOT NULL,              --precos por faixa etaria
    coverage VARCHAR(100) NOT NULL,         --tipo de cobertura (hospitalar, ambulatorial ...)
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

INSERT INTO health_plans (name, operator, operator_code, base_price, age_factor, coverage) VALUES
    ('Plano Essencial', 'Amil', 'AMIL001', 250.00,
    '{"0-18": 1.0, "19-23": 1.2, "24-28": 1.3, "29-33": 1.4, "34-38": 1.5, "39-43": 1.7, "44-48": 2.0, "49-53": 2.5, "54-58": 3.0, "59+": 4.0}',
    'Ambulatorial'),

    ('Plano Hospitalar Plus', 'Bradesco Saúde', 'BRAD002', 450.00,
    '{"0-18": 1.0, "19-23": 1.1, "24-28": 1.2, "29-33": 1.3, "34-38": 1.4, "39-43": 1.6, "44-48": 1.9, "49-53": 2.3, "54-58": 2.8, "59+": 3.8}',
    'Hospitalar'),

    ('Plano Completo', 'SulAmérica', 'SULA003', 680.00,
    '{"0-18": 1.0, "19-23": 1.15, "24-28": 1.25, "29-33": 1.35, "34-38": 1.45, "39-43": 1.65, "44-48": 1.95, "49-53": 2.4, "54-58": 2.9, "59+": 3.9}',
    'Hospitalar e Ambulatorial'),

    ('Plano Basic', 'Unimed', 'UNI004', 180.00,
    '{"0-18": 1.0, "19-23": 1.1, "24-28": 1.2, "29-33": 1.25, "34-38": 1.3, "39-43": 1.5, "44-48": 1.8, "49-53": 2.2, "54-58": 2.7, "59+": 3.5}',
    'Ambulatorial'),

    ('Plano Premium', 'NotreDame Intermédica', 'GNDI005', 890.00,
    '{"0-18": 1.0, "19-23": 1.2, "24-28": 1.3, "29-33": 1.4, "34-38": 1.5, "39-43": 1.7, "44-48": 2.1, "49-53": 2.6, "54-58": 3.1, "59+": 4.2}',
    'Hospitalar e Ambulatorial');

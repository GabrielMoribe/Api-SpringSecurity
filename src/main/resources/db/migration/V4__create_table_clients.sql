CREATE TABLE clients(
    id serial PRIMARY KEY ,
    name VARCHAR(100) NOT NULL ,
    email VARCHAR(100) NOT NULL ,
    phone VARCHAR(20) NOT NULL ,
    broker_id INTEGER NOT NULL , --ID do corretor dono do cliente
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ,
    CONSTRAINT fk_broker_client FOREIGN KEY (broker_id) REFERENCES users(id) -- Referencia a tabela de usuários (corretores)

);


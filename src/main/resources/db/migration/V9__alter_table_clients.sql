ALTER TABLE clients
DROP CONSTRAINT fk_broker_client,
ADD CONSTRAINT fk_broker_client
FOREIGN KEY (broker_id) REFERENCES users(id) ON DELETE CASCADE;
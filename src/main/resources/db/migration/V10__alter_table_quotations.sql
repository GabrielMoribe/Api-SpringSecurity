ALTER TABLE quotations
DROP CONSTRAINT fk_quotation_client,
ADD CONSTRAINT fk_quotation_client
FOREIGN KEY (client_id) REFERENCES clients(id) ON DELETE CASCADE;
ALTER TABLE users ADD COLUMN enabled BOOLEAN DEFAULT FALSE;
ALTER TABLE users ADD COLUMN verification_code VARCHAR(255);
ALTER TABLE users ADD COLUMN verification_expires_at TIMESTAMP;

UPDATE users SET enabled = TRUE WHERE enabled IS FALSE;
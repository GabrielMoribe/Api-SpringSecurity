ALTER TABLE users ADD COLUMN new_email_placeholder VARCHAR(255);
ALTER TABLE users ADD COLUMN new_email_token VARCHAR(255);
ALTER TABLE users ADD COLUMN new_email_token_expires_at TIMESTAMP
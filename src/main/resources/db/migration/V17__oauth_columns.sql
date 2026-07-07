ALTER TABLE users
  MODIFY password VARCHAR(255) NULL;

ALTER TABLE users
  ADD COLUMN auth_provider VARCHAR(20) NOT NULL DEFAULT 'LOCAL' AFTER password,
  ADD COLUMN provider_id VARCHAR(255) NULL AFTER auth_provider;

CREATE UNIQUE INDEX uq_users_provider ON users (auth_provider, provider_id);

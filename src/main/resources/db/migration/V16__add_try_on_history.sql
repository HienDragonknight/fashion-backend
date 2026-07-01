-- V16: Create try_on_history table for AI Virtual Try-On feature
CREATE TABLE IF NOT EXISTS try_on_history (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id             BIGINT       NOT NULL,
    product_id          BIGINT       NULL,
    product_name        VARCHAR(255) NULL,
    original_image_url  TEXT         NOT NULL,
    generated_image_url TEXT         NULL,
    status              VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    error_message       TEXT         NULL,
    created_at          DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT fk_try_on_user
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_try_on_history_user_id ON try_on_history (user_id);
CREATE INDEX idx_try_on_history_created_at ON try_on_history (created_at DESC);

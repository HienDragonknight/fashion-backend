-- ============================================================
-- Add is_collection column to products table
-- Version: V18
-- ============================================================

ALTER TABLE products
    ADD COLUMN is_collection TINYINT(1) NOT NULL DEFAULT 0 AFTER is_featured;

CREATE INDEX idx_products_is_collection ON products(is_collection);

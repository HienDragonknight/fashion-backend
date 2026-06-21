-- ============================================================
-- Add Default Variants for Products without any Variants
-- Version: V10
-- ============================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

INSERT INTO product_variants (product_id, size, color, color_hex, sku, stock_qty, price_adjustment)
SELECT 
    id, 
    'F' AS size, 
    'Mặc định' AS color, 
    '#FFFFFF' AS color_hex, 
    CONCAT(UPPER(slug), '-DEFAULT') AS sku, 
    100 AS stock_qty, 
    0.00 AS price_adjustment
FROM products
WHERE id NOT IN (SELECT DISTINCT product_id FROM product_variants);

SET FOREIGN_KEY_CHECKS = 1;

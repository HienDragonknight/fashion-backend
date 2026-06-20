-- ============================================================
-- Fix base_price / sale_price ordering for homepage products
-- In ecommerce: base_price = original price (higher), sale_price = discounted price (lower)
-- Version: V6
-- ============================================================

SET NAMES utf8mb4;

-- Fix Easy Office products (were accidentally swapped)
UPDATE products SET base_price = 399000, sale_price = 319200 WHERE slug = 'quan-au-nam';
UPDATE products SET base_price = 399000, sale_price = 319200 WHERE slug = 'chan-vay-pencil';

-- Fix Kids products
UPDATE products SET base_price = 249000, sale_price = 124500 WHERE slug = 'phong-adventure';
UPDATE products SET base_price = 169000, sale_price = 135200 WHERE slug = 'phong-tho';
UPDATE products SET base_price = 149000, sale_price = 119200 WHERE slug = 'polo-teen';
UPDATE products SET base_price = 199000, sale_price = 159200 WHERE slug = 'phong-summer';
UPDATE products SET base_price = 199000, sale_price = 159200 WHERE slug = 'phong-dream-team';
UPDATE products SET base_price = 199000, sale_price = 159200 WHERE slug = 'phong-wild';

-- Fix Sun Protection products
UPDATE products SET base_price = 799000, sale_price = 559300 WHERE slug = 'acn-toan-than';
UPDATE products SET base_price = 449000, sale_price = 314300 WHERE slug = 'acn-anti-uv';
UPDATE products SET base_price = 549000, sale_price = 439200 WHERE slug = 'acn-sieu-thoai';
UPDATE products SET base_price = 449000, sale_price = 314300 WHERE slug = 'acn-nu-6008';

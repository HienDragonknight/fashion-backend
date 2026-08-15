-- ============================================================
-- V21: Seed English names for categories, products, and brands
-- ============================================================

-- Categories
UPDATE categories SET name_en = 'Clothing' WHERE name LIKE '%Quần áo%' OR slug = 'quan-ao';
UPDATE categories SET name_en = 'Footwear' WHERE name LIKE '%Giày dép%' OR slug = 'giay-dep';
UPDATE categories SET name_en = 'Bags' WHERE name LIKE '%Túi xách%' OR slug = 'tui-xach';
UPDATE categories SET name_en = 'Watches' WHERE name LIKE '%Đồng hồ%' OR slug = 'dong-ho';
UPDATE categories SET name_en = 'Eyewear' WHERE name LIKE '%Kính mắt%' OR slug = 'kinh-mat';
UPDATE categories SET name_en = 'Accessories' WHERE name LIKE '%Phụ kiện%' OR slug = 'phu-kien';
UPDATE categories SET name_en = 'T-Shirts' WHERE name LIKE '%Áo thun%' OR slug = 'ao-thun';
UPDATE categories SET name_en = 'Shirts' WHERE name LIKE '%Áo sơ mi%' OR slug = 'ao-so-mi';
UPDATE categories SET name_en = 'Jeans' WHERE name LIKE '%Quần jeans%' OR slug = 'quan-jeans';
UPDATE categories SET name_en = 'Dresses' WHERE name LIKE '%Váy đầm%' OR slug = 'vay-dam';
UPDATE categories SET name_en = 'Sneakers' WHERE name LIKE '%Giày sneaker%' OR slug = 'giay-sneaker';
UPDATE categories SET name_en = 'High Heels' WHERE name LIKE '%Giày cao gót%' OR slug = 'giay-cao-got';
UPDATE categories SET name_en = 'Sandals' WHERE name LIKE '%Dép%' OR slug = 'dep';

-- Update products with English names
UPDATE products SET name_en = 'Red T-Shirt' WHERE name LIKE '%Áo Phông Đỏ%' OR name LIKE '%Ao Phong Do%';
UPDATE products SET name_en = 'White T-Shirt' WHERE name LIKE '%Áo Phông Trắng%' OR name LIKE '%Ao Phong Trang%';
UPDATE products SET name_en = 'Polo' WHERE name LIKE '%Polo%' AND (name_en IS NULL OR name_en = '');
UPDATE products SET name_en = 'Shorts' WHERE name LIKE '%Short%' AND (name_en IS NULL OR name_en = '');
UPDATE products SET name_en = 'Kun T-Shirt' WHERE name LIKE '%Kun%' AND (name_en IS NULL OR name_en = '');

-- ============================================================
-- Seed Data (MySQL)
-- Version: V2
-- ============================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ============================================================
-- CATEGORIES
-- ============================================================
INSERT INTO categories (name, slug, parent_id, is_active, sort_order) VALUES
('Quần áo',   'quan-ao',  NULL, 1, 1),
('Giày dép',  'giay-dep', NULL, 1, 2),
('Túi xách',  'tui-xach', NULL, 1, 3),
('Đồng hồ',   'dong-ho',  NULL, 1, 4),
('Kính mắt',  'kinh-mat', NULL, 1, 5),
('Phụ kiện',  'phu-kien', NULL, 1, 6);

INSERT INTO categories (name, slug, parent_id, is_active, sort_order) VALUES
('Áo thun',      'ao-thun',      1, 1, 1),
('Áo sơ mi',     'ao-so-mi',     1, 1, 2),
('Quần jeans',   'quan-jeans',   1, 1, 3),
('Váy đầm',      'vay-dam',      1, 1, 4),
('Giày sneaker', 'giay-sneaker', 2, 1, 1),
('Giày cao gót', 'giay-cao-got', 2, 1, 2),
('Dép',          'dep',          2, 1, 3);

-- ============================================================
-- BRANDS
-- ============================================================
INSERT INTO brands (name, slug, description, is_active) VALUES
('Nike',        'nike',        'Just Do It',                 1),
('Adidas',      'adidas',      'Impossible Is Nothing',      1),
('Yody',        'yody',        'Thương hiệu thời trang Việt',1),
('Zara',        'zara',        'Fast Fashion',               1),
('H&M',         'hm',          'Fashion and quality',        1),
('Local Brand', 'local-brand', 'Thương hiệu nội địa',        1);

-- ============================================================
-- BANNERS
-- ============================================================
INSERT INTO banners (title, image_url, link_url, position, is_active, sort_order) VALUES
('Summer Sale 2025', 'https://images.unsplash.com/photo-1490481651871-ab68de25d43d?w=1400&q=80', '/products', 'HOME_HERO', 1, 1),
('Bộ sưu tập Nike mới', 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=1400&q=80', '/products?brandId=1', 'HOME_HERO', 1, 2),
('Flash Sale Giày Sneaker', 'https://images.unsplash.com/photo-1552346154-21d32810aba3?w=1400&q=80', '/products?categoryId=2', 'HOME_HERO', 1, 3);

-- ============================================================
-- ADMIN USER (password: Admin@123)
-- BCrypt hash of "Admin@123"
-- ============================================================
INSERT INTO users (email, phone, password, full_name, role, is_active) VALUES
('admin@fashion.com', '0900000000',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LjTa6T8AfMi',
 'Admin Fashion', 'ROLE_ADMIN', 1);

-- CUSTOMER TEST (password: 123456)
INSERT INTO users (email, phone, password, full_name, role, is_active) VALUES
('customer@test.com', '0911111111',
 '$2a$10$slYQmyNdgTY18LREe68.QOuEAbTpkUFpSMGLQv6A2UfMPvb2BSBQO',
 'Nguyễn Văn A', 'ROLE_CUSTOMER', 1),
('test2@test.com', '0922222222',
 '$2a$10$slYQmyNdgTY18LREe68.QOuEAbTpkUFpSMGLQv6A2UfMPvb2BSBQO',
 'Trần Thị B', 'ROLE_CUSTOMER', 1);

-- ============================================================
-- PRODUCTS (12 sản phẩm mẫu)
-- ============================================================
INSERT INTO products (name, slug, description, brand_id, category_id, base_price, sale_price, thumbnail_url, is_active, is_featured, weight_grams) VALUES
('Áo thun Nike Dri-FIT Nam',
 'ao-thun-nike-dri-fit-nam',
 'Áo thun thể thao Nike Dri-FIT công nghệ thoát ẩm, thoáng khí tối đa. Phù hợp cho tập luyện và hoạt động ngoài trời.',
 1, 7, 450000, 379000,
 'https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=600&q=80',
 1, 1, 250),

('Áo Polo Yody Basic Nam',
 'ao-polo-yody-basic-nam',
 'Áo polo cổ bẻ chất liệu cotton cao cấp, thấm hút mồ hôi tốt, phù hợp đi làm và dạo phố.',
 3, 7, 299000, 249000,
 'https://images.unsplash.com/photo-1586363104862-3a5e2ab60d99?w=600&q=80',
 1, 1, 220),

('Quần Jeans Slim Fit Nam',
 'quan-jeans-slim-fit-nam',
 'Quần jeans denim co giãn 4 chiều, tôn dáng, phù hợp nhiều phong cách.',
 4, 9, 599000, 499000,
 'https://images.unsplash.com/photo-1542272604-787c3835535d?w=600&q=80',
 1, 0, 600),

('Giày Nike Air Max 270',
 'giay-nike-air-max-270',
 'Giày thể thao Nike Air Max 270 đệm khí lớn nhất từ trước đến nay, cực kỳ êm ái.',
 1, 11, 3200000, 2699000,
 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=600&q=80',
 1, 1, 800),

('Giày Adidas Ultraboost 22',
 'giay-adidas-ultraboost-22',
 'Giày chạy bộ đỉnh cao Adidas Ultraboost 22 với công nghệ Boost mang lại cảm giác nảy bật tối đa.',
 2, 11, 3800000, 3199000,
 'https://images.unsplash.com/photo-1608231387042-66d1773070a5?w=600&q=80',
 1, 1, 750),

('Túi Tote Vải Canvas Local Brand',
 'tui-tote-vai-canvas',
 'Túi tote vải canvas in họa tiết độc đáo, dung tích lớn, bền đẹp.',
 6, 3, 280000, NULL,
 'https://images.unsplash.com/photo-1544816155-12df9643f363?w=600&q=80',
 1, 0, 400),

('Váy Maxi Hoa Nữ',
 'vay-maxi-hoa-nu',
 'Váy maxi dáng dài họa tiết hoa nhẹ nhàng, nữ tính, phù hợp dã ngoại và du lịch.',
 4, 10, 520000, 420000,
 'https://images.unsplash.com/photo-1572804013309-59a88b7e92f1?w=600&q=80',
 1, 1, 350),

('Đồng Hồ Casio G-Shock',
 'dong-ho-casio-g-shock',
 'Đồng hồ G-Shock chống va đập, chống nước 200m, pin năng lượng mặt trời.',
 5, 4, 2500000, 2100000,
 'https://images.unsplash.com/photo-1524592094714-0f0654e20314?w=600&q=80',
 1, 1, 150),

('Kính Mát Rayban Aviator',
 'kinh-mat-rayban-aviator',
 'Kính mát Rayban Aviator tròng phân cực chống UV400, khung kim loại bền chắc.',
 5, 5, 1800000, 1500000,
 'https://images.unsplash.com/photo-1572635196237-14b3f281503f?w=600&q=80',
 1, 0, 100),

('Áo Sơ Mi Trắng Công Sở',
 'ao-so-mi-trang-cong-so',
 'Áo sơ mi trắng cotton lụa cao cấp không nhăn, phù hợp đi làm văn phòng.',
 3, 8, 350000, 299000,
 'https://images.unsplash.com/photo-1598033129183-c4f50c736f10?w=600&q=80',
 1, 0, 280),

('Sneaker Adidas Stan Smith',
 'sneaker-adidas-stan-smith',
 'Giày sneaker Adidas Stan Smith huyền thoại, da thật cao cấp, thiết kế classic bền với thời gian.',
 2, 11, 2200000, 1899000,
 'https://images.unsplash.com/photo-1552346154-21d32810aba3?w=600&q=80',
 1, 0, 700),

('Dép Quai Hậu Yody',
 'dep-quai-hau-yody',
 'Dép quai hậu đế cao su EVA siêu nhẹ, thoải mái cho mùa hè.',
 3, 13, 199000, NULL,
 'https://images.unsplash.com/photo-1603487742131-4160ec999306?w=600&q=80',
 1, 0, 300);

-- ============================================================
-- PRODUCT VARIANTS
-- ============================================================
-- Áo thun Nike (id=1)
INSERT INTO product_variants (product_id, size, color, color_hex, sku, stock_qty, price_adjustment) VALUES
(1,'S','Đen','#000000','NIKE-DRIFIT-BLK-S',50,0),
(1,'M','Đen','#000000','NIKE-DRIFIT-BLK-M',80,0),
(1,'L','Đen','#000000','NIKE-DRIFIT-BLK-L',60,0),
(1,'XL','Đen','#000000','NIKE-DRIFIT-BLK-XL',40,0),
(1,'M','Trắng','#FFFFFF','NIKE-DRIFIT-WHT-M',70,0),
(1,'L','Trắng','#FFFFFF','NIKE-DRIFIT-WHT-L',55,0);

-- Áo Polo Yody (id=2)
INSERT INTO product_variants (product_id, size, color, color_hex, sku, stock_qty, price_adjustment) VALUES
(2,'S','Xanh navy','#1B2A6B','YODY-POLO-NAV-S',40,0),
(2,'M','Xanh navy','#1B2A6B','YODY-POLO-NAV-M',60,0),
(2,'L','Xanh navy','#1B2A6B','YODY-POLO-NAV-L',50,0),
(2,'M','Trắng','#FFFFFF','YODY-POLO-WHT-M',45,0),
(2,'L','Trắng','#FFFFFF','YODY-POLO-WHT-L',35,0);

-- Quần Jeans (id=3)
INSERT INTO product_variants (product_id, size, color, color_hex, sku, stock_qty, price_adjustment) VALUES
(3,'28','Xanh','#4B6FA5','JEAN-SLIM-28',30,0),
(3,'30','Xanh','#4B6FA5','JEAN-SLIM-30',50,0),
(3,'32','Xanh','#4B6FA5','JEAN-SLIM-32',45,0),
(3,'34','Xanh','#4B6FA5','JEAN-SLIM-34',30,0);

-- Nike Air Max (id=4)
INSERT INTO product_variants (product_id, size, color, color_hex, sku, stock_qty, price_adjustment) VALUES
(4,'39','Đen/Trắng','#000000','NIKE-AM270-BW-39',15,0),
(4,'40','Đen/Trắng','#000000','NIKE-AM270-BW-40',20,0),
(4,'41','Đen/Trắng','#000000','NIKE-AM270-BW-41',25,0),
(4,'42','Đen/Trắng','#000000','NIKE-AM270-BW-42',20,0),
(4,'43','Đen/Trắng','#000000','NIKE-AM270-BW-43',10,0);

-- Adidas Ultraboost (id=5)
INSERT INTO product_variants (product_id, size, color, color_hex, sku, stock_qty, price_adjustment) VALUES
(5,'39','Trắng','#FFFFFF','ADS-UB22-WHT-39',10,0),
(5,'40','Trắng','#FFFFFF','ADS-UB22-WHT-40',15,0),
(5,'41','Trắng','#FFFFFF','ADS-UB22-WHT-41',20,0),
(5,'42','Trắng','#FFFFFF','ADS-UB22-WHT-42',15,0);

-- Túi Tote (id=6)
INSERT INTO product_variants (product_id, size, color, color_hex, sku, stock_qty, price_adjustment) VALUES
(6,'OneSize','Be','#D2B48C','TOTE-CANVAS-BEIGE',30,0),
(6,'OneSize','Đen','#000000','TOTE-CANVAS-BLACK',25,0);

-- Váy Maxi (id=7)
INSERT INTO product_variants (product_id, size, color, color_hex, sku, stock_qty, price_adjustment) VALUES
(7,'S','Hoa hồng','#FFB6C1','MAXI-HOA-S',20,0),
(7,'M','Hoa hồng','#FFB6C1','MAXI-HOA-M',30,0),
(7,'L','Hoa hồng','#FFB6C1','MAXI-HOA-L',20,0);

-- Casio G-Shock (id=8)
INSERT INTO product_variants (product_id, size, color, color_hex, sku, stock_qty, price_adjustment) VALUES
(8,'OneSize','Đen','#000000','CASIO-GSHOCK-BLK',20,0),
(8,'OneSize','Xanh lá','#228B22','CASIO-GSHOCK-GRN',15,200000);

-- Kính Rayban (id=9)
INSERT INTO product_variants (product_id, size, color, color_hex, sku, stock_qty, price_adjustment) VALUES
(9,'OneSize','Vàng/Xanh','#C8A420','RAYBAN-AVI-GLDGRN',15,0),
(9,'OneSize','Bạc/Xám','#C0C0C0','RAYBAN-AVI-SLVGRY',12,0);

-- Áo Sơ Mi (id=10)
INSERT INTO product_variants (product_id, size, color, color_hex, sku, stock_qty, price_adjustment) VALUES
(10,'S','Trắng','#FFFFFF','SOMI-TRANG-S',40,0),
(10,'M','Trắng','#FFFFFF','SOMI-TRANG-M',60,0),
(10,'L','Trắng','#FFFFFF','SOMI-TRANG-L',45,0),
(10,'XL','Trắng','#FFFFFF','SOMI-TRANG-XL',25,0);

-- Stan Smith (id=11)
INSERT INTO product_variants (product_id, size, color, color_hex, sku, stock_qty, price_adjustment) VALUES
(11,'39','Trắng/Xanh','#FFFFFF','ADS-STAN-WG-39',12,0),
(11,'40','Trắng/Xanh','#FFFFFF','ADS-STAN-WG-40',18,0),
(11,'41','Trắng/Xanh','#FFFFFF','ADS-STAN-WG-41',20,0),
(11,'42','Trắng/Xanh','#FFFFFF','ADS-STAN-WG-42',15,0);

-- Dép Yody (id=12)
INSERT INTO product_variants (product_id, size, color, color_hex, sku, stock_qty, price_adjustment) VALUES
(12,'36','Đen','#000000','YODY-DEP-BLK-36',30,0),
(12,'37','Đen','#000000','YODY-DEP-BLK-37',40,0),
(12,'38','Đen','#000000','YODY-DEP-BLK-38',35,0),
(12,'39','Đen','#000000','YODY-DEP-BLK-39',25,0),
(12,'40','Đen','#000000','YODY-DEP-BLK-40',20,0);

-- ============================================================
-- PRODUCT IMAGES (thêm ảnh phụ)
-- ============================================================
INSERT INTO product_images (product_id, image_url, sort_order) VALUES
(1,'https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=600&q=80',0),
(1,'https://images.unsplash.com/photo-1503342217505-b0a15ec3261c?w=600&q=80',1),
(4,'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=600&q=80',0),
(4,'https://images.unsplash.com/photo-1460353581641-37baddab0fa2?w=600&q=80',1),
(5,'https://images.unsplash.com/photo-1608231387042-66d1773070a5?w=600&q=80',0),
(7,'https://images.unsplash.com/photo-1572804013309-59a88b7e92f1?w=600&q=80',0),
(8,'https://images.unsplash.com/photo-1524592094714-0f0654e20314?w=600&q=80',0);

SET FOREIGN_KEY_CHECKS = 1;

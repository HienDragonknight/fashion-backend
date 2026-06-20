-- ============================================================
-- Homepage Seed Data
-- Version: V5
-- ============================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ============================================================
-- HERO BANNER SLIDES (3 slides)
-- ============================================================
-- Clear existing banners and reseed with full hero data
DELETE FROM banners WHERE position = 'HOME_HERO';

INSERT INTO banners (title, badge, badge_color, title_text, subtitle, cta_text, text_color, overlay_gradient, image_url, link_url, position, is_active, sort_order) VALUES
('POLO ALL-IN-1',
 'BST MỚI', '#FCCE00',
 'POLO ALL-IN-1',
 'Mặc đẹp, sống chất cùng YODY',
 'Khám phá ngay',
 '#ffffff',
 'linear-gradient(to right, rgba(0,0,0,0.8) 0%, rgba(0,0,0,0.4) 50%, transparent 100%)',
 '/images/banner-1.jpg',
 '/collection/POLO-ALL-IN-ONE',
 'HOME_HERO', 1, 1),

('Easy Wear Easy Office',
 'EASY OFFICE', '#fff',
 'Easy Wear\nEasy Office',
 'Mặc dễ dàng – Làm nhẹ nhàng',
 'Xem bộ sưu tập',
 '#ffffff',
 'linear-gradient(to right, rgba(20,30,40,0.85) 0%, rgba(20,30,40,0.4) 50%, transparent 100%)',
 '/images/banner-2.jpg',
 '/collection/EASYOFFICE',
 'HOME_HERO', 1, 2),

('Áo Chống Nắng UPF50+',
 'BẢO VỆ DA', '#2d6a4f',
 'ÁO CHỐNG NẮNG\nUPF50+',
 'Công nghệ Anti UV hiện đại – Mát mẻ cả ngày',
 'Xem ngay',
 '#ffffff',
 'linear-gradient(to right, rgba(10,40,20,0.85) 0%, rgba(10,40,20,0.4) 50%, transparent 100%)',
 '/images/banner-3.jpg',
 '/collection/ao-chong-nang',
 'HOME_HERO', 1, 3);

-- ============================================================
-- BLOG POSTS (6 bài)
-- ============================================================
INSERT INTO blog_posts (title, slug, date, image_url, is_active, sort_order) VALUES
('Khám phá BST EASYOFFICE mới nhất từ Yody – Giải pháp cho thời trang công sở năng động',
 'bst-easyoffice', '2026-05-20',
 'https://placehold.co/400x300/D4E6F1/1A1A1A?text=EasyOffice', 1, 1),

('Mua áo polo chính hãng ở đâu? 10 Áo polo được săn đón ở YODY',
 'ao-polo-chinh-hang', '2026-05-18',
 'https://placehold.co/400x300/D5E8D4/1A1A1A?text=Polo', 1, 2),

('Áo polo oversize nam: 10 mẫu đẹp & cách phối đồ sành điệu',
 'ao-polo-oversize-nam', '2026-05-15',
 'https://placehold.co/400x300/FDEBD0/1A1A1A?text=Oversize', 1, 3),

('Quốc tế Thiếu nhi ngày mấy? Gợi ý outfit cho bé đi chơi 1/6',
 'quoc-te-thieu-nhi-ngay-may', '2026-05-28',
 'https://placehold.co/400x300/FCE4EC/1A1A1A?text=Kid+1%2F6', 1, 4),

('Áo chống nắng nữ siêu thoáng - Cứu tinh giữa mùa hè ngột ngạt',
 'ao-chong-nang-nu', '2026-05-22',
 'https://placehold.co/400x300/E8F5E9/1A1A1A?text=Chống+Nắng', 1, 5),

('World Cup 2026 khi nào khai mạc? Gợi ý outfit xem World Cup cực chất',
 'world-cup-2026-khi-nao-khai-mac', '2026-06-01',
 'https://placehold.co/400x300/EBF5FB/1A1A1A?text=World+Cup', 1, 6);

-- ============================================================
-- HOMEPAGE PRODUCTS
-- Category IDs reference (from V4 inserts, IDs 14-27 approx):
-- We use slug lookups via subquery for safety
-- ============================================================

-- ---- EASY OFFICE section ----
INSERT INTO products (name, slug, description, brand_id, category_id, base_price, sale_price, thumbnail_url, is_active, is_featured, weight_grams) VALUES
('Áo Sơ-mi nam dài tay kẻ caro cao cấp',      'ao-so-mi-karo',     NULL, 3, (SELECT id FROM categories WHERE slug='easyoffice'),   299000, NULL,   '/images/model_office.png',   1, 0, 280),
('Áo Sơ-mi Regular Classic Good Essential',   'ao-so-mi-classic',  NULL, 3, (SELECT id FROM categories WHERE slug='easyoffice'),   599000, NULL,   '/images/model_office_2.png', 1, 0, 280),
('Áo Sơ-mi Regular Fit Cotton Wrinkle Less',  'ao-so-mi-cotton',   NULL, 3, (SELECT id FROM categories WHERE slug='easyoffice'),   429000, NULL,   '/images/model_office.png',   1, 0, 280),
('Áo Sơ-mi Nữ Relax Fit Cotton Blend',        'ao-so-mi-nu',       NULL, 3, (SELECT id FROM categories WHERE slug='easyoffice'),   499000, NULL,   '/images/model_office_2.png', 1, 0, 260),
('Áo Sơ-mi Nữ NANO Regular Essential',        'ao-so-mi-nano',     NULL, 3, (SELECT id FROM categories WHERE slug='easyoffice'),   599000, NULL,   '/images/model_office.png',   1, 0, 260),
('Quần Âu Nam Cạp Chun Siêu Phẳng',           'quan-au-nam',       NULL, 3, (SELECT id FROM categories WHERE slug='easyoffice'),   319200, 399000, '/images/model_office_2.png', 1, 0, 400),
('Quần Tây Nữ Straight Essential',             'quan-tay-nu',       NULL, 3, (SELECT id FROM categories WHERE slug='easyoffice'),   429000, NULL,   '/images/model_office.png',   1, 0, 380),
('Chân Váy Pencil Under The Knee Essential',   'chan-vay-pencil',   NULL, 3, (SELECT id FROM categories WHERE slug='easyoffice'),   319200, 399000, '/images/model_office_2.png', 1, 0, 300);

-- ---- POLO ALL-IN-ONE section ----
INSERT INTO products (name, slug, description, brand_id, category_id, base_price, sale_price, thumbnail_url, is_active, is_featured, weight_grams) VALUES
('Áo Polo Nam Regular Cổ Ép Có Xẻ Tà - Thoáng Khí', 'polo-nam-regular',  NULL, 3, (SELECT id FROM categories WHERE slug='polo-all-in-one'), 199000, NULL,   '/images/model_polo.png',   1, 1, 220),
('Áo Polo Nữ Regular Cổ Ép Có Xẻ Tà',               'polo-nu-regular',   NULL, 3, (SELECT id FROM categories WHERE slug='polo-all-in-one'), 199000, NULL,   '/images/model_polo_2.png', 1, 1, 200),
('Áo Polo Nam Slim Có Khóa Kéo',                     'polo-nam-slim',     NULL, 3, (SELECT id FROM categories WHERE slug='polo-all-in-one'), 329000, NULL,   '/images/model_polo.png',   1, 0, 230),
('Áo Polo Nam Hoạ Tiết In Tràn',                     'polo-hoa-tiet',     NULL, 3, (SELECT id FROM categories WHERE slug='polo-all-in-one'), 329000, NULL,   '/images/model_polo_2.png', 1, 0, 230),
('Áo Polo In Tràn Hoạ Tiết',                         'polo-in-tran',      NULL, 3, (SELECT id FROM categories WHERE slug='polo-all-in-one'), 499000, NULL,   '/images/model_polo.png',   1, 0, 230),
('Áo Polo Nam Slim In Thân Trước Có Xẻ Tà',          'polo-slim-print',   NULL, 3, (SELECT id FROM categories WHERE slug='polo-all-in-one'), 399000, NULL,   '/images/model_polo_2.png', 1, 0, 230),
('Áo Polo Nữ Slim In Thân Trước Có Xẻ Tà',           'polo-nu-slim',      NULL, 3, (SELECT id FROM categories WHERE slug='polo-all-in-one'), 349000, NULL,   '/images/model_polo.png',   1, 0, 210),
('Áo Polo Nam Phối Dây Dệt Năng Động',               'polo-day-det',      NULL, 3, (SELECT id FROM categories WHERE slug='polo-all-in-one'), 329000, NULL,   '/images/model_polo_2.png', 1, 0, 230);

-- ---- SALE section ----
INSERT INTO products (name, slug, description, brand_id, category_id, base_price, sale_price, thumbnail_url, is_active, is_featured, weight_grams) VALUES
('Áo Ba Lỗ Nữ Cotton Hack Dáng',                   'ao-ba-lo-nu',      NULL, 3, (SELECT id FROM categories WHERE slug='sale-homepage'),   49000, NULL,   '/images/model_office.png',   1, 0, 150),
('T-shirt Nữ Slimfit Thun Rib Tăm Nhỏ',            'tshirt-nu-slim',   NULL, 3, (SELECT id FROM categories WHERE slug='sale-homepage'),   79000, NULL,   '/images/model_office_2.png', 1, 0, 180),
('Áo Sơ Mi Nam Business Knit Phối Nép Giấu Cúc',   'so-mi-business',   NULL, 3, (SELECT id FROM categories WHERE slug='sale-homepage'),  199000, NULL,   '/images/model_office.png',   1, 0, 280),
('Áo sơ mi cộc tay sợi tre họa tiết',              'so-mi-soi-tre',    NULL, 3, (SELECT id FROM categories WHERE slug='sale-homepage'),  199000, NULL,   '/images/model_office_2.png', 1, 0, 260),
('Sơ Mi Dài Tay Nam Knit Tay Kiểu',                'so-mi-knit',       NULL, 3, (SELECT id FROM categories WHERE slug='sale-homepage'),  199000, NULL,   '/images/model_office.png',   1, 0, 270),
('Áo 2 Dây Nữ Cổ Rộng',                           'ao-2-day',         NULL, 3, (SELECT id FROM categories WHERE slug='sale-homepage'),   99000, NULL,   '/images/model_office_2.png', 1, 0, 140),
('Áo Phông Nữ Bổ Thân Gắn Tag Kim Loại',           'phong-nu-tag',     NULL, 3, (SELECT id FROM categories WHERE slug='sale-homepage'),   99000, NULL,   '/images/model_office.png',   1, 0, 160),
('Áo Polo Nữ Airycool Giữ Form Thoáng Mát',        'polo-airycool',    NULL, 3, (SELECT id FROM categories WHERE slug='sale-homepage'),  149000, NULL,   '/images/model_polo_2.png',   1, 0, 200);

-- ---- KIDS section ----
INSERT INTO products (name, slug, description, brand_id, category_id, base_price, sale_price, thumbnail_url, is_active, is_featured, weight_grams) VALUES
('Áo Phông Cá Sấu Lớn',              'phong-ca-sau',      NULL, 3, (SELECT id FROM categories WHERE slug='kid-homepage'),   99000,  NULL,   '/images/model_kids.png', 1, 0, 120),
('Áo phông adventure',               'phong-adventure',   NULL, 3, (SELECT id FROM categories WHERE slug='kid-homepage'),  124500,  249000, '/images/model_kids.png', 1, 0, 120),
('Áo Phông Bé Gái Ariel',            'phong-ariel',       NULL, 3, (SELECT id FROM categories WHERE slug='kid-homepage'),  149000,  NULL,   '/images/model_kids.png', 1, 0, 110),
('Áo Phông In Tràn Chú Thỏ',         'phong-tho',         NULL, 3, (SELECT id FROM categories WHERE slug='kid-homepage'),  135200,  169000, '/images/model_kids.png', 1, 0, 110),
('Áo Polo Teen Cổ Ép Có Xẻ Tà',      'polo-teen',         NULL, 3, (SELECT id FROM categories WHERE slug='kid-homepage'),  119200,  149000, '/images/model_kids.png', 1, 0, 130),
('Áo Phông Kid Summer Together',      'phong-summer',      NULL, 3, (SELECT id FROM categories WHERE slug='kid-homepage'),  159200,  199000, '/images/model_kids.png', 1, 0, 120),
('Áo Phông Trẻ Em Dream Team',        'phong-dream-team',  NULL, 3, (SELECT id FROM categories WHERE slug='kid-homepage'),  159200,  199000, '/images/model_kids.png', 1, 0, 120),
('Áo Phông Wild Discovery',           'phong-wild',        NULL, 3, (SELECT id FROM categories WHERE slug='kid-homepage'),  159200,  199000, '/images/model_kids.png', 1, 0, 120);

-- ---- SUN PROTECTION section ----
INSERT INTO products (name, slug, description, brand_id, category_id, base_price, sale_price, thumbnail_url, is_active, is_featured, weight_grams) VALUES
('Áo Khoác Chống Nắng Toàn Thân (ACN6002)',             'acn-toan-than',   NULL, 3, (SELECT id FROM categories WHERE slug='sun-protection'), 559300, 799000, '/images/model_office.png',   1, 0, 350),
('Áo Khoác Chống Nắng Nữ Đa Năng',                      'acn-nu-da-nang',  NULL, 3, (SELECT id FROM categories WHERE slug='sun-protection'), 369000, NULL,   '/images/model_office_2.png', 1, 0, 320),
('Áo Chống Nắng Nữ Đa Năng Anti UV - Versatile',        'acn-anti-uv',     NULL, 3, (SELECT id FROM categories WHERE slug='sun-protection'), 314300, 449000, '/images/model_office.png',   1, 0, 300),
('Áo Chống Nắng Nam Có Mũ',                             'acn-nam-mu',      NULL, 3, (SELECT id FROM categories WHERE slug='sun-protection'), 599000, NULL,   '/images/model_office_2.png', 1, 0, 380),
('Áo Khoác Chống Nắng Thời Trang Mũ Liền',              'acn-mu-lien',     NULL, 3, (SELECT id FROM categories WHERE slug='sun-protection'), 629000, NULL,   '/images/model_office.png',   1, 0, 400),
('Găng Tay Chống Nắng Anti UV',                         'gang-tay-acn',    NULL, 3, (SELECT id FROM categories WHERE slug='sun-protection'), 129000, NULL,   '/images/model_office_2.png', 1, 0, 80),
('Áo Khoác Chống Nắng Siêu Thoải Mái (ACN7004)',        'acn-sieu-thoai',  NULL, 3, (SELECT id FROM categories WHERE slug='sun-protection'), 439200, 549000, '/images/model_office.png',   1, 0, 360),
('Áo Khoác Chống Nắng Nữ Đa Năng (ACN6008)',            'acn-nu-6008',     NULL, 3, (SELECT id FROM categories WHERE slug='sun-protection'), 314300, 449000, '/images/model_office_2.png', 1, 0, 310);

-- ---- JEANS section ----
INSERT INTO products (name, slug, description, brand_id, category_id, base_price, sale_price, thumbnail_url, is_active, is_featured, weight_grams) VALUES
('Quần Jeans Nữ Straight Cắt Cúp',              'jeans-nu-straight',  NULL, 3, (SELECT id FROM categories WHERE slug='jean-flex'), 629000, NULL,   '/images/model_jeans.png', 1, 0, 600),
('Quần Jeans Nữ Baggy Xếp Ly',                  'jeans-nu-baggy',     NULL, 3, (SELECT id FROM categories WHERE slug='jean-flex'), 399000, NULL,   '/images/model_jeans.png', 1, 0, 580),
('Quần Jeans Nữ Straight Thêu Túi Hậu',         'jeans-nu-theu',      NULL, 3, (SELECT id FROM categories WHERE slug='jean-flex'), 569000, NULL,   '/images/model_jeans.png', 1, 0, 590),
('Quần Jeans Nam Regular Siêu Nhẹ Co Giãn',     'jeans-nam-nhe',      NULL, 3, (SELECT id FROM categories WHERE slug='jean-flex'), 699000, NULL,   '/images/model_jeans.png', 1, 0, 620),
('Quần Jeans Nữ Barrel Light Weight',            'jeans-nu-barrel',    NULL, 3, (SELECT id FROM categories WHERE slug='jean-flex'), 499000, NULL,   '/images/model_jeans.png', 1, 0, 570),
('Quần Jeans Nam Regular Can Túi',               'jeans-nam-can',      NULL, 3, (SELECT id FROM categories WHERE slug='jean-flex'), 399000, NULL,   '/images/model_jeans.png', 1, 0, 600),
('Quần Jeans Nam Slim Denim Like (QJM6041)',     'jeans-nam-slim',     NULL, 3, (SELECT id FROM categories WHERE slug='jean-flex'), 599000, NULL,   '/images/model_jeans.png', 1, 0, 610),
('Quần Jeans Nam Regular Coolmax Black Titan',   'jeans-nam-coolmax',  NULL, 3, (SELECT id FROM categories WHERE slug='jean-flex'), 649000, NULL,   '/images/model_jeans.png', 1, 0, 630);

-- ---- UPDATE CATEGORIES image_url for CollectionTabs ----
UPDATE categories SET image_url = 'https://placehold.co/64x64/1A1A1A/FCCE00?text=DT'  WHERE slug = 'dream-team-winner';
UPDATE categories SET image_url = 'https://placehold.co/64x64/2d6a4f/ffffff?text=UV'  WHERE slug = 'ao-chong-nang';
UPDATE categories SET image_url = 'https://placehold.co/64x64/9B59B6/ffffff?text=SÊ'  WHERE slug = 'bst-sip-emmm';
UPDATE categories SET image_url = 'https://placehold.co/64x64/E53E3E/ffffff?text=XH'  WHERE slug = 'ao-giu-nhiet-xtra-heat';
UPDATE categories SET image_url = 'https://placehold.co/64x64/1565C0/ffffff?text=JF'  WHERE slug = 'jeans-collection';
UPDATE categories SET image_url = 'https://placehold.co/64x64/37474F/ffffff?text=BC'  WHERE slug = 'bst-business-casual';
UPDATE categories SET image_url = 'https://placehold.co/64x64/F5A623/ffffff?text=SP'  WHERE slug = 'yody-sport-nhe-tenh';
UPDATE categories SET image_url = 'https://placehold.co/64x64/555555/ffffff?text=EB'  WHERE slug = 'everyday-basics';

SET FOREIGN_KEY_CHECKS = 1;

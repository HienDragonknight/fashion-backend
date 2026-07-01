-- V14: Seed thêm sản phẩm SALE >= 50% và KIDS
-- ============================================================

-- ── SALE >= 50% products (nhiều loại gender) ──────────────────
INSERT INTO products (name, slug, description, base_price, sale_price, thumbnail_url, is_active, is_featured, weight_grams, gender_tags, category_id, brand_id, created_at)
SELECT
  'Áo Thun Oversized Basic Sale 50%', 'ao-thun-oversized-sale', 'Áo thun form rộng basic, chất liệu cotton 100%, thoáng mát.',
  299000, 149000,
  'https://images.unsplash.com/photo-1576566588028-4147f3842f27?w=600&q=80&fit=crop',
  true, true, 200, 'MALE,FEMALE,UNISEX',
  (SELECT id FROM categories WHERE slug='ao-thun' LIMIT 1),
  (SELECT id FROM brands WHERE slug='yody' LIMIT 1),
  NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE slug='ao-thun-oversized-sale');

INSERT INTO products (name, slug, description, base_price, sale_price, thumbnail_url, is_active, is_featured, weight_grams, gender_tags, category_id, brand_id, created_at)
SELECT
  'Áo Polo Classic Giảm 60%', 'ao-polo-classic-sale-60', 'Áo polo cổ bẻ chất liệu cotton pique cao cấp.',
  399000, 159000,
  'https://images.unsplash.com/photo-1586790170083-2f9ceadc732d?w=600&q=80&fit=crop',
  true, true, 220, 'MALE,UNISEX',
  (SELECT id FROM categories WHERE slug='polo-all-in-one' LIMIT 1),
  (SELECT id FROM brands WHERE slug='yody' LIMIT 1),
  NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE slug='ao-polo-classic-sale-60');

INSERT INTO products (name, slug, description, base_price, sale_price, thumbnail_url, is_active, is_featured, weight_grams, gender_tags, category_id, brand_id, created_at)
SELECT
  'Áo Khoác Dù Nhẹ Sale 55%', 'ao-khoac-du-sale-55', 'Áo khoác dù siêu nhẹ, chống gió, chống nước nhẹ.',
  599000, 269000,
  'https://images.unsplash.com/photo-1551028719-00167b16eac5?w=600&q=80&fit=crop',
  true, false, 350, 'MALE,FEMALE',
  (SELECT id FROM categories WHERE slug='ao-khoac' LIMIT 1),
  (SELECT id FROM brands WHERE slug='yody' LIMIT 1),
  NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE slug='ao-khoac-du-sale-55');

INSERT INTO products (name, slug, description, base_price, sale_price, thumbnail_url, is_active, is_featured, weight_grams, gender_tags, category_id, brand_id, created_at)
SELECT
  'Quần Short Thể Thao Sale 50%', 'quan-short-thethao-sale', 'Quần short thể thao thoáng khí, co giãn 4 chiều.',
  249000, 124000,
  'https://images.unsplash.com/photo-1620012253295-c15cc3e65df4?w=600&q=80&fit=crop',
  true, false, 180, 'MALE,UNISEX',
  (SELECT id FROM categories WHERE slug='quan-short' LIMIT 1),
  (SELECT id FROM brands WHERE slug='yody' LIMIT 1),
  NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE slug='quan-short-thethao-sale');

INSERT INTO products (name, slug, description, base_price, sale_price, thumbnail_url, is_active, is_featured, weight_grams, gender_tags, category_id, brand_id, created_at)
SELECT
  'Áo Sơ Mi Nữ Linen Sale 50%', 'ao-so-mi-nu-linen-sale', 'Áo sơ mi nữ chất linen nhẹ, thoáng, phong cách đơn giản tinh tế.',
  450000, 225000,
  'https://images.unsplash.com/photo-1594938298603-c8148c4b4dac?w=600&q=80&fit=crop',
  true, true, 200, 'FEMALE',
  (SELECT id FROM categories WHERE slug='easyoffice' LIMIT 1),
  (SELECT id FROM brands WHERE slug='yody' LIMIT 1),
  NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE slug='ao-so-mi-nu-linen-sale');

INSERT INTO products (name, slug, description, base_price, sale_price, thumbnail_url, is_active, is_featured, weight_grams, gender_tags, category_id, brand_id, created_at)
SELECT
  'Đầm Midi Hoa Sale 60%', 'dam-midi-hoa-sale-60', 'Đầm midi hoạ tiết hoa nhẹ nhàng, thanh lịch cho mùa hè.',
  650000, 260000,
  'https://images.unsplash.com/photo-1612336307429-8a898d10e223?w=600&q=80&fit=crop',
  true, true, 250, 'FEMALE',
  (SELECT id FROM categories WHERE slug='easyoffice' LIMIT 1),
  (SELECT id FROM brands WHERE slug='yody' LIMIT 1),
  NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE slug='dam-midi-hoa-sale-60');

INSERT INTO products (name, slug, description, base_price, sale_price, thumbnail_url, is_active, is_featured, weight_grams, gender_tags, category_id, brand_id, created_at)
SELECT
  'Áo Len Crop Nữ Sale 55%', 'ao-len-crop-nu-sale', 'Áo len crop tay dài, giữ ấm, phối đồ linh hoạt.',
  380000, 171000,
  'https://images.unsplash.com/photo-1434389677669-e08b4cac3105?w=600&q=80&fit=crop',
  true, false, 300, 'FEMALE',
  (SELECT id FROM categories WHERE slug='easyoffice' LIMIT 1),
  (SELECT id FROM brands WHERE slug='yody' LIMIT 1),
  NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE slug='ao-len-crop-nu-sale');

INSERT INTO products (name, slug, description, base_price, sale_price, thumbnail_url, is_active, is_featured, weight_grams, gender_tags, category_id, brand_id, created_at)
SELECT
  'Quần Jeans Skinny Sale 50%', 'quan-jeans-skinny-sale', 'Quần jeans skinny co giãn, ôm vừa, bền màu.',
  490000, 245000,
  'https://images.unsplash.com/photo-1582552938357-32b906df40cb?w=600&q=80&fit=crop',
  true, false, 400, 'MALE,FEMALE',
  (SELECT id FROM categories WHERE slug='quan-jeans' LIMIT 1),
  (SELECT id FROM brands WHERE slug='yody' LIMIT 1),
  NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE slug='quan-jeans-skinny-sale');

INSERT INTO products (name, slug, description, base_price, sale_price, thumbnail_url, is_active, is_featured, weight_grams, gender_tags, category_id, brand_id, created_at)
SELECT
  'Áo Thun In Hình Giảm 70%', 'ao-thun-in-hinh-sale-70', 'Áo thun cotton in hình độc đáo, màu sắc đa dạng.',
  199000, 59000,
  'https://images.unsplash.com/photo-1503341455253-b2e723bb3dbb?w=600&q=80&fit=crop',
  true, true, 180, 'MALE,UNISEX',
  (SELECT id FROM categories WHERE slug='ao-thun' LIMIT 1),
  (SELECT id FROM brands WHERE slug='yody' LIMIT 1),
  NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE slug='ao-thun-in-hinh-sale-70');

INSERT INTO products (name, slug, description, base_price, sale_price, thumbnail_url, is_active, is_featured, weight_grams, gender_tags, category_id, brand_id, created_at)
SELECT
  'Áo Chống Nắng Unisex Sale 50%', 'ao-chong-nang-unisex-sale', 'Áo chống nắng mỏng nhẹ, UPF50+, phù hợp cả nam lẫn nữ.',
  350000, 175000,
  'https://images.unsplash.com/photo-1523381210434-271e8be1f52b?w=600&q=80&fit=crop',
  true, true, 220, 'MALE,FEMALE,UNISEX',
  (SELECT id FROM categories WHERE slug='sun-protection' LIMIT 1),
  (SELECT id FROM brands WHERE slug='yody' LIMIT 1),
  NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE slug='ao-chong-nang-unisex-sale');

-- ── KIDS products (thêm cho đủ ≥ 20 sản phẩm) ────────────────
INSERT INTO products (name, slug, description, base_price, sale_price, thumbnail_url, is_active, is_featured, weight_grams, gender_tags, category_id, brand_id, created_at)
SELECT
  'Áo Thun Trẻ Em In Khủng Long', 'ao-thun-khung-long', 'Áo thun trẻ em cotton mềm mại, in hình khủng long dễ thương.',
  129000, NULL,
  'https://images.unsplash.com/photo-1519238263530-99bdd11df2ea?w=600&q=80&fit=crop',
  true, false, 110, 'KIDS',
  (SELECT id FROM categories WHERE slug='kid-homepage' LIMIT 1),
  (SELECT id FROM brands WHERE slug='yody' LIMIT 1),
  NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE slug='ao-thun-khung-long');

INSERT INTO products (name, slug, description, base_price, sale_price, thumbnail_url, is_active, is_featured, weight_grams, gender_tags, category_id, brand_id, created_at)
SELECT
  'Bộ Đồ Bơi Trẻ Em', 'bo-do-boi-tre-em', 'Bộ đồ bơi trẻ em chất liệu nhanh khô, màu sắc rực rỡ.',
  199000, 99000,
  'https://images.unsplash.com/photo-1560185007-c5ca9d2c014d?w=600&q=80&fit=crop',
  true, true, 130, 'KIDS',
  (SELECT id FROM categories WHERE slug='kid-homepage' LIMIT 1),
  (SELECT id FROM brands WHERE slug='yody' LIMIT 1),
  NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE slug='bo-do-boi-tre-em');

INSERT INTO products (name, slug, description, base_price, sale_price, thumbnail_url, is_active, is_featured, weight_grams, gender_tags, category_id, brand_id, created_at)
SELECT
  'Quần Short Kẻ Sọc Trẻ Em', 'quan-short-ke-soc-kids', 'Quần short kẻ sọc thoáng mát cho bé trai và bé gái.',
  119000, NULL,
  'https://images.unsplash.com/photo-1471286174890-9c112ffca5b4?w=600&q=80&fit=crop',
  true, false, 100, 'KIDS',
  (SELECT id FROM categories WHERE slug='kid-homepage' LIMIT 1),
  (SELECT id FROM brands WHERE slug='yody' LIMIT 1),
  NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE slug='quan-short-ke-soc-kids');

INSERT INTO products (name, slug, description, base_price, sale_price, thumbnail_url, is_active, is_featured, weight_grams, gender_tags, category_id, brand_id, created_at)
SELECT
  'Áo Khoác Siêu Nhẹ Trẻ Em', 'ao-khoac-sieu-nhe-kids', 'Áo khoác siêu nhẹ chống gió cho bé, nhiều màu.',
  249000, NULL,
  'https://images.unsplash.com/photo-1503919545889-aef636e10ad4?w=600&q=80&fit=crop',
  true, true, 160, 'KIDS',
  (SELECT id FROM categories WHERE slug='kid-homepage' LIMIT 1),
  (SELECT id FROM brands WHERE slug='yody' LIMIT 1),
  NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE slug='ao-khoac-sieu-nhe-kids');

INSERT INTO products (name, slug, description, base_price, sale_price, thumbnail_url, is_active, is_featured, weight_grams, gender_tags, category_id, brand_id, created_at)
SELECT
  'Váy Bé Gái Hoạ Tiết Hoa', 'vay-be-gai-hoa-tiet', 'Váy bé gái chất liệu cotton nhẹ, hoạ tiết hoa dễ thương.',
  169000, 84000,
  'https://images.unsplash.com/photo-1518831959646-742c3a14ebf7?w=600&q=80&fit=crop',
  true, true, 120, 'KIDS',
  (SELECT id FROM categories WHERE slug='kid-homepage' LIMIT 1),
  (SELECT id FROM brands WHERE slug='yody' LIMIT 1),
  NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE slug='vay-be-gai-hoa-tiet');

INSERT INTO products (name, slug, description, base_price, sale_price, thumbnail_url, is_active, is_featured, weight_grams, gender_tags, category_id, brand_id, created_at)
SELECT
  'Bộ Thể Thao Trẻ Em Năng Động', 'bo-the-thao-kids', 'Bộ thể thao áo + quần cho bé, vải co giãn thoáng khí.',
  279000, NULL,
  'https://images.unsplash.com/photo-1476234251651-f353703a034d?w=600&q=80&fit=crop',
  true, false, 200, 'KIDS',
  (SELECT id FROM categories WHERE slug='kid-homepage' LIMIT 1),
  (SELECT id FROM brands WHERE slug='yody' LIMIT 1),
  NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE slug='bo-the-thao-kids');

INSERT INTO products (name, slug, description, base_price, sale_price, thumbnail_url, is_active, is_featured, weight_grams, gender_tags, category_id, brand_id, created_at)
SELECT
  'Áo Phông Bé Trai In Xe Đua', 'ao-phong-xe-dua-kids', 'Áo phông bé trai in hình xe đua, chất cotton mềm.',
  99000, NULL,
  'https://images.unsplash.com/photo-1519238263530-99bdd11df2ea?w=600&q=80&fit=crop',
  true, false, 100, 'KIDS',
  (SELECT id FROM categories WHERE slug='kid-homepage' LIMIT 1),
  (SELECT id FROM brands WHERE slug='yody' LIMIT 1),
  NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE slug='ao-phong-xe-dua-kids');

INSERT INTO products (name, slug, description, base_price, sale_price, thumbnail_url, is_active, is_featured, weight_grams, gender_tags, category_id, brand_id, created_at)
SELECT
  'Quần Dài Kaki Trẻ Em', 'quan-dai-kaki-kids', 'Quần dài kaki cho bé, bền đẹp phù hợp đi học.',
  179000, NULL,
  'https://images.unsplash.com/photo-1471286174890-9c112ffca5b4?w=600&q=80&fit=crop',
  true, false, 180, 'KIDS',
  (SELECT id FROM categories WHERE slug='kid-homepage' LIMIT 1),
  (SELECT id FROM brands WHERE slug='yody' LIMIT 1),
  NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE slug='quan-dai-kaki-kids');

INSERT INTO products (name, slug, description, base_price, sale_price, thumbnail_url, is_active, is_featured, weight_grams, gender_tags, category_id, brand_id, created_at)
SELECT
  'Áo Hoodie Nỉ Trẻ Em', 'ao-hoodie-ni-kids', 'Áo hoodie nỉ ấm áp, có mũ trùm đầu, phong cách năng động.',
  299000, 149000,
  'https://images.unsplash.com/photo-1503919545889-aef636e10ad4?w=600&q=80&fit=crop',
  true, true, 280, 'KIDS',
  (SELECT id FROM categories WHERE slug='kid-homepage' LIMIT 1),
  (SELECT id FROM brands WHERE slug='yody' LIMIT 1),
  NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE slug='ao-hoodie-ni-kids');

INSERT INTO products (name, slug, description, base_price, sale_price, thumbnail_url, is_active, is_featured, weight_grams, gender_tags, category_id, brand_id, created_at)
SELECT
  'Đồ Ngủ Bé Gái Unicorn', 'do-ngu-unicorn-kids', 'Bộ đồ ngủ bé gái hoạ tiết kỳ lân, chất flannel mềm mịn.',
  219000, NULL,
  'https://images.unsplash.com/photo-1518831959646-742c3a14ebf7?w=600&q=80&fit=crop',
  true, false, 220, 'KIDS',
  (SELECT id FROM categories WHERE slug='kid-homepage' LIMIT 1),
  (SELECT id FROM brands WHERE slug='yody' LIMIT 1),
  NOW()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE slug='do-ngu-unicorn-kids');

-- Seed variants cho tất cả sản phẩm mới (size F mặc định)
INSERT INTO product_variants (product_id, size, color, color_hex, sku, stock_qty, price_adjustment)
SELECT p.id, 'F', 'Mặc định', '#FFFFFF',
       UPPER(REPLACE(REPLACE(p.slug, '-', '_'), '__', '_')) AS sku,
       100, 0
FROM products p
WHERE p.slug IN (
  'ao-thun-oversized-sale','ao-polo-classic-sale-60','ao-khoac-du-sale-55',
  'quan-short-thethao-sale','ao-so-mi-nu-linen-sale','dam-midi-hoa-sale-60',
  'ao-len-crop-nu-sale','quan-jeans-skinny-sale','ao-thun-in-hinh-sale-70',
  'ao-chong-nang-unisex-sale',
  'ao-thun-khung-long','bo-do-boi-tre-em','quan-short-ke-soc-kids',
  'ao-khoac-sieu-nhe-kids','vay-be-gai-hoa-tiet','bo-the-thao-kids',
  'ao-phong-xe-dua-kids','quan-dai-kaki-kids','ao-hoodie-ni-kids',
  'do-ngu-unicorn-kids'
)
AND NOT EXISTS (
  SELECT 1 FROM product_variants pv WHERE pv.product_id = p.id
);

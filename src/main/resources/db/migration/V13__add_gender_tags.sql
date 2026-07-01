-- ============================================================
-- V13: Add gender_tags column to products
-- gender_tags: comma-separated values of MALE, FEMALE, UNISEX, KIDS
-- A product can belong to multiple genders e.g. 'MALE,UNISEX'
-- ============================================================

ALTER TABLE products
    ADD COLUMN gender_tags VARCHAR(100) NULL DEFAULT NULL
        COMMENT 'Comma-separated gender tags: MALE, FEMALE, UNISEX, KIDS';

CREATE INDEX idx_products_gender ON products(gender_tags);

-- ── Seed gender_tags for existing products (V2 seed data) ──────────────────

-- id=1  Áo thun Nike Dri-FIT Nam
UPDATE products SET gender_tags = 'MALE' WHERE slug = 'ao-thun-nike-dri-fit-nam';

-- id=2  Áo Polo Yody Basic Nam
UPDATE products SET gender_tags = 'MALE' WHERE slug = 'ao-polo-yody-basic-nam';

-- id=3  Quần Jeans Slim Fit Nam
UPDATE products SET gender_tags = 'MALE' WHERE slug = 'quan-jeans-slim-fit-nam';

-- id=4  Giày Nike Air Max 270 (unisex sneaker)
UPDATE products SET gender_tags = 'MALE,FEMALE,UNISEX' WHERE slug = 'giay-nike-air-max-270';

-- id=5  Giày Adidas Ultraboost 22 (unisex running)
UPDATE products SET gender_tags = 'MALE,FEMALE,UNISEX' WHERE slug = 'giay-adidas-ultraboost-22';

-- id=6  Túi Tote Vải Canvas (unisex)
UPDATE products SET gender_tags = 'MALE,FEMALE,UNISEX' WHERE slug = 'tui-tote-vai-canvas';

-- id=7  Váy Maxi Hoa Nữ
UPDATE products SET gender_tags = 'FEMALE' WHERE slug = 'vay-maxi-hoa-nu';

-- id=8  Đồng Hồ Casio G-Shock (unisex)
UPDATE products SET gender_tags = 'MALE,FEMALE,UNISEX' WHERE slug = 'dong-ho-casio-g-shock';

-- id=9  Kính Mát Rayban Aviator (unisex)
UPDATE products SET gender_tags = 'MALE,FEMALE,UNISEX' WHERE slug = 'kinh-mat-rayban-aviator';

-- id=10 Áo Sơ Mi Trắng Công Sở (unisex)
UPDATE products SET gender_tags = 'MALE,FEMALE,UNISEX' WHERE slug = 'ao-so-mi-trang-cong-so';

-- id=11 Sneaker Adidas Stan Smith (unisex)
UPDATE products SET gender_tags = 'MALE,FEMALE,UNISEX' WHERE slug = 'sneaker-adidas-stan-smith';

-- id=12 Dép Quai Hậu Yody (unisex)
UPDATE products SET gender_tags = 'MALE,FEMALE,UNISEX' WHERE slug = 'dep-quai-hau-yody';

-- ── Seed gender_tags for V5 homepage seed products ─────────────────────────
-- Easy Office section (eo*)
UPDATE products SET gender_tags = 'MALE'   WHERE slug = 'ao-so-mi-karo';
UPDATE products SET gender_tags = 'MALE'   WHERE slug = 'ao-so-mi-classic';
UPDATE products SET gender_tags = 'MALE'   WHERE slug = 'ao-so-mi-cotton';
UPDATE products SET gender_tags = 'FEMALE' WHERE slug = 'ao-so-mi-nu';
UPDATE products SET gender_tags = 'FEMALE' WHERE slug = 'ao-so-mi-nano';
UPDATE products SET gender_tags = 'MALE'   WHERE slug = 'quan-au-nam';
UPDATE products SET gender_tags = 'FEMALE' WHERE slug = 'quan-tay-nu';
UPDATE products SET gender_tags = 'FEMALE' WHERE slug = 'chan-vay-pencil';

-- Polo section (p*)
UPDATE products SET gender_tags = 'MALE'   WHERE slug = 'polo-nam-regular';
UPDATE products SET gender_tags = 'FEMALE' WHERE slug = 'polo-nu-regular';
UPDATE products SET gender_tags = 'MALE'   WHERE slug = 'polo-nam-slim';
UPDATE products SET gender_tags = 'MALE,UNISEX' WHERE slug = 'polo-hoa-tiet';
UPDATE products SET gender_tags = 'MALE,UNISEX' WHERE slug = 'polo-in-tran';
UPDATE products SET gender_tags = 'MALE'   WHERE slug = 'polo-slim-print';
UPDATE products SET gender_tags = 'FEMALE' WHERE slug = 'polo-nu-slim';
UPDATE products SET gender_tags = 'MALE'   WHERE slug = 'polo-day-det';

-- Sale section (s*)
UPDATE products SET gender_tags = 'FEMALE' WHERE slug = 'ao-ba-lo-nu';
UPDATE products SET gender_tags = 'FEMALE' WHERE slug = 'tshirt-nu-slim';
UPDATE products SET gender_tags = 'MALE'   WHERE slug = 'so-mi-business';
UPDATE products SET gender_tags = 'MALE,UNISEX' WHERE slug = 'so-mi-soi-tre';
UPDATE products SET gender_tags = 'MALE'   WHERE slug = 'so-mi-knit';
UPDATE products SET gender_tags = 'FEMALE' WHERE slug = 'ao-2-day';
UPDATE products SET gender_tags = 'FEMALE' WHERE slug = 'phong-nu-tag';
UPDATE products SET gender_tags = 'FEMALE' WHERE slug = 'polo-airycool';

-- Kids section (k*)
UPDATE products SET gender_tags = 'KIDS' WHERE slug = 'phong-ca-sau';
UPDATE products SET gender_tags = 'KIDS' WHERE slug = 'phong-adventure';
UPDATE products SET gender_tags = 'KIDS' WHERE slug = 'phong-ariel';
UPDATE products SET gender_tags = 'KIDS' WHERE slug = 'phong-tho';
UPDATE products SET gender_tags = 'KIDS' WHERE slug = 'polo-teen';
UPDATE products SET gender_tags = 'KIDS' WHERE slug = 'phong-summer';
UPDATE products SET gender_tags = 'KIDS' WHERE slug = 'phong-dream-team';
UPDATE products SET gender_tags = 'KIDS' WHERE slug = 'phong-wild';

-- Sun protection section (sp*)
UPDATE products SET gender_tags = 'MALE,FEMALE,UNISEX' WHERE slug = 'acn-toan-than';
UPDATE products SET gender_tags = 'FEMALE' WHERE slug = 'acn-nu-da-nang';
UPDATE products SET gender_tags = 'FEMALE' WHERE slug = 'acn-anti-uv';
UPDATE products SET gender_tags = 'MALE'   WHERE slug = 'acn-nam-mu';
UPDATE products SET gender_tags = 'MALE,FEMALE,UNISEX' WHERE slug = 'acn-mu-lien';
UPDATE products SET gender_tags = 'MALE,FEMALE,UNISEX' WHERE slug = 'gang-tay-acn';
UPDATE products SET gender_tags = 'MALE,FEMALE,UNISEX' WHERE slug = 'acn-sieu-thoai';
UPDATE products SET gender_tags = 'FEMALE' WHERE slug = 'acn-nu-6008';

-- Jeans section (j*)
UPDATE products SET gender_tags = 'FEMALE' WHERE slug = 'jeans-nu-straight';
UPDATE products SET gender_tags = 'FEMALE' WHERE slug = 'jeans-nu-baggy';
UPDATE products SET gender_tags = 'FEMALE' WHERE slug = 'jeans-nu-theu';
UPDATE products SET gender_tags = 'MALE'   WHERE slug = 'jeans-nam-nhe';
UPDATE products SET gender_tags = 'FEMALE' WHERE slug = 'jeans-nu-barrel';
UPDATE products SET gender_tags = 'MALE'   WHERE slug = 'jeans-nam-can';
UPDATE products SET gender_tags = 'MALE'   WHERE slug = 'jeans-nam-slim';
UPDATE products SET gender_tags = 'MALE'   WHERE slug = 'jeans-nam-coolmax';

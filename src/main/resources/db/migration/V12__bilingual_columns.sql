-- ============================================================
-- V12: Add bilingual (English) columns to content tables
-- Affects: products, categories, brands, blog_posts, banners
-- Zero impact on orders, cart, users, reviews, variants
-- ============================================================

-- ── PRODUCTS ─────────────────────────────────────────────────
ALTER TABLE products
    ADD COLUMN name_en        VARCHAR(500) DEFAULT NULL AFTER name,
    ADD COLUMN description_en TEXT         DEFAULT NULL AFTER description;

-- Full-text index for English product search
ALTER TABLE products
    ADD FULLTEXT INDEX idx_products_name_en_ft (name_en);

-- ── CATEGORIES ───────────────────────────────────────────────
ALTER TABLE categories
    ADD COLUMN name_en VARCHAR(255) DEFAULT NULL AFTER name;

-- ── BRANDS ───────────────────────────────────────────────────
ALTER TABLE brands
    ADD COLUMN name_en        VARCHAR(255) DEFAULT NULL AFTER name,
    ADD COLUMN description_en TEXT         DEFAULT NULL AFTER description;

-- ── BLOG POSTS ───────────────────────────────────────────────
-- First add missing content columns (blog was sparse)
ALTER TABLE blog_posts
    ADD COLUMN excerpt    TEXT     DEFAULT NULL AFTER title,
    ADD COLUMN content    LONGTEXT DEFAULT NULL AFTER excerpt,
    ADD COLUMN title_en   VARCHAR(500) DEFAULT NULL AFTER title,
    ADD COLUMN excerpt_en TEXT         DEFAULT NULL AFTER excerpt,
    ADD COLUMN content_en LONGTEXT     DEFAULT NULL AFTER content;

-- ── BANNERS ──────────────────────────────────────────────────
ALTER TABLE banners
    ADD COLUMN title_en    VARCHAR(255) DEFAULT NULL AFTER title,
    ADD COLUMN subtitle_en VARCHAR(500) DEFAULT NULL AFTER subtitle,
    ADD COLUMN cta_text_en VARCHAR(100) DEFAULT NULL AFTER cta_text,
    ADD COLUMN badge_en    VARCHAR(100) DEFAULT NULL AFTER badge;

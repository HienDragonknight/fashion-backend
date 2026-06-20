-- ============================================================
-- Homepage Schema Additions
-- Version: V4
-- ============================================================

SET NAMES utf8mb4;

-- ============================================================
-- Extend BANNERS table for hero slides
-- ============================================================
ALTER TABLE banners
    ADD COLUMN badge          VARCHAR(100)  DEFAULT NULL AFTER title,
    ADD COLUMN badge_color    VARCHAR(20)   DEFAULT '#FCCE00' AFTER badge,
    ADD COLUMN title_text     VARCHAR(255)  DEFAULT NULL AFTER badge_color,
    ADD COLUMN subtitle       VARCHAR(500)  DEFAULT NULL AFTER title_text,
    ADD COLUMN cta_text       VARCHAR(100)  DEFAULT 'Khám phá ngay' AFTER subtitle,
    ADD COLUMN text_color     VARCHAR(20)   DEFAULT '#ffffff' AFTER cta_text,
    ADD COLUMN overlay_gradient TEXT         DEFAULT NULL AFTER text_color;

-- ============================================================
-- BLOG POSTS
-- ============================================================
CREATE TABLE IF NOT EXISTS blog_posts (
    id         BIGINT        AUTO_INCREMENT PRIMARY KEY,
    title      VARCHAR(500)  NOT NULL,
    slug       VARCHAR(500)  NOT NULL UNIQUE,
    date       DATE          NOT NULL,
    image_url  TEXT          NOT NULL,
    is_active  TINYINT(1)    NOT NULL DEFAULT 1,
    sort_order INT           NOT NULL DEFAULT 0,
    created_at DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_blog_posts_slug ON blog_posts(slug);
CREATE INDEX idx_blog_posts_active ON blog_posts(is_active);

-- ============================================================
-- HOMEPAGE PRODUCT SECTIONS
-- Categories cho các section trên homepage
-- ============================================================
INSERT INTO categories (name, slug, parent_id, is_active, sort_order) VALUES
('Easy Office',        'easyoffice',               1, 1, 10),
('Polo All-In-One',    'polo-all-in-one',           1, 1, 11),
('Sale',               'sale-homepage',             1, 1, 12),
('Trẻ Em',             'kid-homepage',              1, 1, 13),
('Áo Chống Nắng',      'sun-protection',            1, 1, 14),
('Jean Flex',          'jean-flex',                 1, 1, 15),
('BST Dream Team Winner', 'dream-team-winner',      1, 1, 16),
('BST Áo Chống Nắng',  'ao-chong-nang',            1, 1, 17),
('BST Sịp Êm',         'bst-sip-emmm',             1, 1, 18),
('Áo Giữ Nhiệt Xtra-Heat', 'ao-giu-nhiet-xtra-heat', 1, 1, 19),
('BST Jean Flex',      'jeans-collection',          1, 1, 20),
('BST Business Casual','bst-business-casual',       1, 1, 21),
('BST Sport Nhẹ Tênh', 'yody-sport-nhe-tenh',      1, 1, 22),
('BST Everyday Basic', 'everyday-basics',           1, 1, 23);

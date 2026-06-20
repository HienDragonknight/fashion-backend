-- ============================================================
-- Fashion E-Commerce Database Schema (MySQL)
-- Version: V1
-- ============================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ============================================================
-- USERS
-- ============================================================
CREATE TABLE IF NOT EXISTS users (
    id            BIGINT       AUTO_INCREMENT PRIMARY KEY,
    email         VARCHAR(255) UNIQUE,
    phone         VARCHAR(20)  UNIQUE,
    password      VARCHAR(255) NOT NULL,
    full_name     VARCHAR(255) NOT NULL,
    avatar_url    TEXT,
    role          VARCHAR(20)  NOT NULL DEFAULT 'ROLE_CUSTOMER',
    is_active     TINYINT(1)   NOT NULL DEFAULT 1,
    refresh_token TEXT,
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_phone ON users(phone);

-- ============================================================
-- PASSWORD RESET TOKENS
-- ============================================================
CREATE TABLE IF NOT EXISTS password_reset_tokens (
    id         BIGINT       AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT       NOT NULL,
    token      VARCHAR(255) NOT NULL UNIQUE,
    expires_at DATETIME     NOT NULL,
    used       TINYINT(1)   NOT NULL DEFAULT 0,
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_prt_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_prt_token ON password_reset_tokens(token);

-- ============================================================
-- ADDRESSES
-- ============================================================
CREATE TABLE IF NOT EXISTS addresses (
    id           BIGINT       AUTO_INCREMENT PRIMARY KEY,
    user_id      BIGINT       NOT NULL,
    full_name    VARCHAR(255) NOT NULL,
    phone        VARCHAR(20)  NOT NULL,
    province_id  INT          NOT NULL DEFAULT 0,
    province     VARCHAR(255) NOT NULL,
    district_id  INT          NOT NULL DEFAULT 0,
    district     VARCHAR(255) NOT NULL,
    ward_code    VARCHAR(20)  NOT NULL DEFAULT '',
    ward         VARCHAR(255) NOT NULL,
    detail       TEXT         NOT NULL,
    is_default   TINYINT(1)   NOT NULL DEFAULT 0,
    created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_addr_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_addresses_user ON addresses(user_id);

-- ============================================================
-- CATEGORIES
-- ============================================================
CREATE TABLE IF NOT EXISTS categories (
    id         BIGINT       AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    slug       VARCHAR(255) NOT NULL UNIQUE,
    parent_id  BIGINT       DEFAULT NULL,
    image_url  TEXT,
    is_active  TINYINT(1)   NOT NULL DEFAULT 1,
    sort_order INT          NOT NULL DEFAULT 0,
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cat_parent FOREIGN KEY (parent_id) REFERENCES categories(id) ON DELETE SET NULL
);

CREATE INDEX idx_categories_slug   ON categories(slug);
CREATE INDEX idx_categories_parent ON categories(parent_id);

-- ============================================================
-- BRANDS
-- ============================================================
CREATE TABLE IF NOT EXISTS brands (
    id          BIGINT       AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    slug        VARCHAR(255) NOT NULL UNIQUE,
    logo_url    TEXT,
    description TEXT,
    is_active   TINYINT(1)   NOT NULL DEFAULT 1,
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_brands_slug ON brands(slug);

-- ============================================================
-- PRODUCTS
-- ============================================================
CREATE TABLE IF NOT EXISTS products (
    id            BIGINT        AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(500)  NOT NULL,
    slug          VARCHAR(500)  NOT NULL UNIQUE,
    description   TEXT,
    brand_id      BIGINT        DEFAULT NULL,
    category_id   BIGINT        DEFAULT NULL,
    base_price    DECIMAL(15,2) NOT NULL,
    sale_price    DECIMAL(15,2) DEFAULT NULL,
    thumbnail_url TEXT,
    is_active     TINYINT(1)    NOT NULL DEFAULT 1,
    is_featured   TINYINT(1)    NOT NULL DEFAULT 0,
    weight_grams  INT           NOT NULL DEFAULT 300,
    sold_count    INT           NOT NULL DEFAULT 0,
    view_count    INT           NOT NULL DEFAULT 0,
    created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_prod_brand    FOREIGN KEY (brand_id)    REFERENCES brands(id)     ON DELETE SET NULL,
    CONSTRAINT fk_prod_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL
);

CREATE INDEX idx_products_slug        ON products(slug);
CREATE INDEX idx_products_category    ON products(category_id);
CREATE INDEX idx_products_brand       ON products(brand_id);
CREATE INDEX idx_products_is_active   ON products(is_active);
CREATE INDEX idx_products_is_featured ON products(is_featured);
CREATE FULLTEXT INDEX idx_products_name_ft ON products(name);

-- ============================================================
-- PRODUCT IMAGES
-- ============================================================
CREATE TABLE IF NOT EXISTS product_images (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    image_url  TEXT   NOT NULL,
    sort_order INT    NOT NULL DEFAULT 0,
    CONSTRAINT fk_img_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

CREATE INDEX idx_product_images_product ON product_images(product_id);

-- ============================================================
-- PRODUCT VARIANTS
-- ============================================================
CREATE TABLE IF NOT EXISTS product_variants (
    id               BIGINT        AUTO_INCREMENT PRIMARY KEY,
    product_id       BIGINT        NOT NULL,
    size             VARCHAR(20)   DEFAULT NULL,
    color            VARCHAR(100)  DEFAULT NULL,
    color_hex        VARCHAR(10)   DEFAULT NULL,
    sku              VARCHAR(255)  NOT NULL UNIQUE,
    stock_qty        INT           NOT NULL DEFAULT 0,
    price_adjustment DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    CONSTRAINT fk_var_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    CONSTRAINT chk_stock CHECK (stock_qty >= 0)
);

CREATE INDEX idx_variants_product ON product_variants(product_id);
CREATE INDEX idx_variants_sku     ON product_variants(sku);

-- ============================================================
-- CART ITEMS
-- ============================================================
CREATE TABLE IF NOT EXISTS cart_items (
    id         BIGINT   AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT   NOT NULL,
    variant_id BIGINT   NOT NULL,
    quantity   INT      NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cart_user    FOREIGN KEY (user_id)    REFERENCES users(id)            ON DELETE CASCADE,
    CONSTRAINT fk_cart_variant FOREIGN KEY (variant_id) REFERENCES product_variants(id) ON DELETE CASCADE,
    UNIQUE KEY uq_cart_user_variant (user_id, variant_id),
    CONSTRAINT chk_cart_qty CHECK (quantity > 0)
);

CREATE INDEX idx_cart_user ON cart_items(user_id);

-- ============================================================
-- WISHLISTS
-- ============================================================
CREATE TABLE IF NOT EXISTS wishlists (
    id         BIGINT   AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT   NOT NULL,
    product_id BIGINT   NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_wish_user    FOREIGN KEY (user_id)    REFERENCES users(id)    ON DELETE CASCADE,
    CONSTRAINT fk_wish_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    UNIQUE KEY uq_wishlist (user_id, product_id)
);

CREATE INDEX idx_wishlist_user ON wishlists(user_id);

-- ============================================================
-- ORDERS
-- ============================================================
CREATE TABLE IF NOT EXISTS orders (
    id               BIGINT        AUTO_INCREMENT PRIMARY KEY,
    user_id          BIGINT        NOT NULL,
    address_id       BIGINT        DEFAULT NULL,
    status           VARCHAR(50)   NOT NULL DEFAULT 'PENDING',
    payment_method   VARCHAR(50)   NOT NULL DEFAULT 'COD',
    payment_status   VARCHAR(50)   NOT NULL DEFAULT 'UNPAID',
    shipping_fee     DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    subtotal         DECIMAL(15,2) NOT NULL,
    discount         DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    total            DECIMAL(15,2) NOT NULL,
    ghn_order_code   VARCHAR(100)  DEFAULT NULL,
    vnpay_txn_ref    VARCHAR(100)  DEFAULT NULL,
    note             TEXT,
    snapshot_address TEXT,
    created_at       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_order_user    FOREIGN KEY (user_id)    REFERENCES users(id)     ON DELETE RESTRICT,
    CONSTRAINT fk_order_address FOREIGN KEY (address_id) REFERENCES addresses(id) ON DELETE SET NULL
);

CREATE INDEX idx_orders_user    ON orders(user_id);
CREATE INDEX idx_orders_status  ON orders(status);
CREATE INDEX idx_orders_created ON orders(created_at);

-- ============================================================
-- ORDER ITEMS
-- ============================================================
CREATE TABLE IF NOT EXISTS order_items (
    id            BIGINT        AUTO_INCREMENT PRIMARY KEY,
    order_id      BIGINT        NOT NULL,
    variant_id    BIGINT        DEFAULT NULL,
    product_id    BIGINT        DEFAULT NULL,
    product_name  VARCHAR(500)  NOT NULL,
    variant_label VARCHAR(255)  DEFAULT NULL,
    thumbnail_url TEXT,
    price         DECIMAL(15,2) NOT NULL,
    quantity      INT           NOT NULL,
    CONSTRAINT fk_oi_order   FOREIGN KEY (order_id)   REFERENCES orders(id)           ON DELETE CASCADE,
    CONSTRAINT fk_oi_variant FOREIGN KEY (variant_id) REFERENCES product_variants(id) ON DELETE SET NULL,
    CONSTRAINT fk_oi_product FOREIGN KEY (product_id) REFERENCES products(id)         ON DELETE SET NULL,
    CONSTRAINT chk_oi_qty CHECK (quantity > 0)
);

CREATE INDEX idx_order_items_order ON order_items(order_id);

-- ============================================================
-- REVIEWS
-- ============================================================
CREATE TABLE IF NOT EXISTS reviews (
    id          BIGINT     AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT     NOT NULL,
    product_id  BIGINT     NOT NULL,
    order_id    BIGINT     DEFAULT NULL,
    rating      INT        NOT NULL,
    comment     TEXT,
    images      JSON,
    is_approved TINYINT(1) NOT NULL DEFAULT 0,
    created_at  DATETIME   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_rev_user    FOREIGN KEY (user_id)    REFERENCES users(id)    ON DELETE CASCADE,
    CONSTRAINT fk_rev_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    CONSTRAINT fk_rev_order   FOREIGN KEY (order_id)   REFERENCES orders(id)   ON DELETE SET NULL,
    UNIQUE KEY uq_review (user_id, product_id, order_id),
    CONSTRAINT chk_rating CHECK (rating BETWEEN 1 AND 5)
);

CREATE INDEX idx_reviews_product ON reviews(product_id);
CREATE INDEX idx_reviews_user    ON reviews(user_id);

-- ============================================================
-- BANNERS
-- ============================================================
CREATE TABLE IF NOT EXISTS banners (
    id         BIGINT       AUTO_INCREMENT PRIMARY KEY,
    title      VARCHAR(255) NOT NULL,
    image_url  TEXT         NOT NULL,
    link_url   TEXT,
    position   VARCHAR(50)  NOT NULL DEFAULT 'HOME_HERO',
    is_active  TINYINT(1)   NOT NULL DEFAULT 1,
    sort_order INT          NOT NULL DEFAULT 0,
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

SET FOREIGN_KEY_CHECKS = 1;

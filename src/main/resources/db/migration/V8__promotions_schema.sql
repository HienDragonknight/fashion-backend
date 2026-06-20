-- V8: Promotions (Coupon) table
CREATE TABLE IF NOT EXISTS promotions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500),
    discount_type ENUM('PERCENTAGE', 'FIXED') NOT NULL DEFAULT 'PERCENTAGE',
    discount_value DECIMAL(15, 2) NOT NULL,
    min_order_value DECIMAL(15, 2) NOT NULL DEFAULT 0,
    max_discount DECIMAL(15, 2) NULL,
    usage_limit INT NOT NULL DEFAULT 100,
    used_count INT NOT NULL DEFAULT 0,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Seed sample promotions
INSERT INTO promotions (code, description, discount_type, discount_value, min_order_value, max_discount, usage_limit, used_count, start_date, end_date, is_active)
VALUES
    ('SUMMER2026', 'Giảm giá mùa hè', 'PERCENTAGE', 20, 300000, 100000, 500, 234, '2026-06-01', '2026-08-31', TRUE),
    ('FREESHIP50K', 'Freeship đơn từ 200K', 'FIXED', 50000, 200000, NULL, 1000, 876, '2026-01-01', '2026-12-31', TRUE),
    ('NEWUSER30', 'Khách hàng mới giảm 30%', 'PERCENTAGE', 30, 150000, 150000, 200, 200, '2026-05-01', '2026-05-31', FALSE),
    ('SALE100K', 'Giảm 100K mọi đơn từ 500K', 'FIXED', 100000, 500000, NULL, 300, 87, '2026-06-15', '2026-07-15', TRUE),
    ('BIRTHDAY15', 'Ưu đãi sinh nhật 15%', 'PERCENTAGE', 15, 0, 200000, 100, 42, '2026-06-17', '2026-06-30', TRUE);

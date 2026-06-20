-- V9: Seed Orders + Order Items (realistic data for dashboard charts)
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ============================================================
-- ORDERS (50 đơn hàng trải 12 tháng năm 2026)
-- user_id=2 (Nguyễn Văn A), user_id=3 (Trần Thị B)
-- ============================================================
INSERT INTO orders (user_id, status, payment_method, payment_status, shipping_fee, subtotal, discount, total, snapshot_address, created_at, updated_at) VALUES
-- Tháng 1
(2,'DELIVERED','COD','PAID',30000,758000,0,788000,'Nguyễn Văn A — 12 Nguyễn Huệ, Hoàn Kiếm, Hà Nội','2026-01-05 10:00:00','2026-01-08 16:00:00'),
(3,'DELIVERED','VNPAY','PAID',25000,498000,50000,473000,'Trần Thị B — 45 Lê Lợi, Q.1, TP.HCM','2026-01-10 09:30:00','2026-01-13 14:00:00'),
(2,'DELIVERED','COD','PAID',30000,2699000,0,2729000,'Nguyễn Văn A — 12 Nguyễn Huệ, Hoàn Kiếm, Hà Nội','2026-01-15 14:00:00','2026-01-18 10:00:00'),
(3,'DELIVERED','COD','PAID',25000,499000,0,524000,'Trần Thị B — 45 Lê Lợi, Q.1, TP.HCM','2026-01-20 11:00:00','2026-01-23 15:00:00'),

-- Tháng 2
(2,'DELIVERED','VNPAY','PAID',0,3199000,200000,2999000,'Nguyễn Văn A — 12 Nguyễn Huệ, Hoàn Kiếm, Hà Nội','2026-02-03 08:00:00','2026-02-06 12:00:00'),
(3,'DELIVERED','COD','PAID',30000,379000,0,409000,'Trần Thị B — 45 Lê Lợi, Q.1, TP.HCM','2026-02-08 10:00:00','2026-02-11 16:00:00'),
(2,'DELIVERED','COD','PAID',25000,2100000,100000,2025000,'Nguyễn Văn A — 12 Nguyễn Huệ, Hoàn Kiếm, Hà Nội','2026-02-14 15:00:00','2026-02-17 09:00:00'),
(3,'DELIVERED','COD','PAID',30000,249000,0,279000,'Trần Thị B — 45 Lê Lợi, Q.1, TP.HCM','2026-02-20 13:00:00','2026-02-23 11:00:00'),

-- Tháng 3
(2,'DELIVERED','VNPAY','PAID',0,1899000,100000,1799000,'Nguyễn Văn A — 12 Nguyễn Huệ, Hoàn Kiếm, Hà Nội','2026-03-02 09:00:00','2026-03-05 14:00:00'),
(3,'DELIVERED','COD','PAID',30000,420000,0,450000,'Trần Thị B — 45 Lê Lợi, Q.1, TP.HCM','2026-03-07 10:30:00','2026-03-10 16:00:00'),
(2,'DELIVERED','COD','PAID',25000,299000,0,324000,'Nguyễn Văn A — 12 Nguyễn Huệ, Hoàn Kiếm, Hà Nội','2026-03-12 14:00:00','2026-03-15 10:00:00'),
(3,'DELIVERED','VNPAY','PAID',0,1500000,0,1500000,'Trần Thị B — 45 Lê Lợi, Q.1, TP.HCM','2026-03-18 11:00:00','2026-03-21 15:00:00'),
(2,'DELIVERED','COD','PAID',30000,498000,0,528000,'Nguyễn Văn A — 12 Nguyễn Huệ, Hoàn Kiếm, Hà Nội','2026-03-25 08:30:00','2026-03-28 13:00:00'),

-- Tháng 4
(3,'DELIVERED','COD','PAID',25000,379000,0,404000,'Trần Thị B — 45 Lê Lợi, Q.1, TP.HCM','2026-04-03 09:00:00','2026-04-06 15:00:00'),
(2,'DELIVERED','VNPAY','PAID',0,5398000,300000,5098000,'Nguyễn Văn A — 12 Nguyễn Huệ, Hoàn Kiếm, Hà Nội','2026-04-08 10:00:00','2026-04-11 14:00:00'),
(3,'DELIVERED','COD','PAID',30000,280000,0,310000,'Trần Thị B — 45 Lê Lợi, Q.1, TP.HCM','2026-04-14 13:00:00','2026-04-17 10:00:00'),
(2,'DELIVERED','COD','PAID',25000,249000,0,274000,'Nguyễn Văn A — 12 Nguyễn Huệ, Hoàn Kiếm, Hà Nội','2026-04-20 14:30:00','2026-04-23 11:00:00'),
(3,'DELIVERED','VNPAY','PAID',0,2699000,200000,2499000,'Trần Thị B — 45 Lê Lợi, Q.1, TP.HCM','2026-04-27 09:30:00','2026-04-30 15:00:00'),

-- Tháng 5
(2,'DELIVERED','COD','PAID',30000,758000,0,788000,'Nguyễn Văn A — 12 Nguyễn Huệ, Hoàn Kiếm, Hà Nội','2026-05-04 08:00:00','2026-05-07 14:00:00'),
(3,'DELIVERED','COD','PAID',25000,499000,50000,474000,'Trần Thị B — 45 Lê Lợi, Q.1, TP.HCM','2026-05-09 10:00:00','2026-05-12 16:00:00'),
(2,'DELIVERED','VNPAY','PAID',0,3199000,0,3199000,'Nguyễn Văn A — 12 Nguyễn Huệ, Hoàn Kiếm, Hà Nội','2026-05-14 11:00:00','2026-05-17 09:00:00'),
(3,'DELIVERED','COD','PAID',30000,420000,0,450000,'Trần Thị B — 45 Lê Lợi, Q.1, TP.HCM','2026-05-18 13:00:00','2026-05-21 15:00:00'),
(2,'DELIVERED','COD','PAID',25000,299000,0,324000,'Nguyễn Văn A — 12 Nguyễn Huệ, Hoàn Kiếm, Hà Nội','2026-05-23 09:30:00','2026-05-26 11:00:00'),
(3,'DELIVERED','VNPAY','PAID',0,1899000,100000,1799000,'Trần Thị B — 45 Lê Lợi, Q.1, TP.HCM','2026-05-28 14:00:00','2026-05-31 10:00:00'),

-- Tháng 6 (đơn mới, mixed status)
(2,'DELIVERED','COD','PAID',30000,2699000,0,2729000,'Nguyễn Văn A — 12 Nguyễn Huệ, Hoàn Kiếm, Hà Nội','2026-06-02 08:30:00','2026-06-05 14:00:00'),
(3,'DELIVERED','VNPAY','PAID',0,758000,50000,708000,'Trần Thị B — 45 Lê Lợi, Q.1, TP.HCM','2026-06-05 10:00:00','2026-06-08 16:00:00'),
(2,'DELIVERED','COD','PAID',25000,499000,0,524000,'Nguyễn Văn A — 12 Nguyễn Huệ, Hoàn Kiếm, Hà Nội','2026-06-08 11:00:00','2026-06-11 15:00:00'),
(3,'SHIPPING','COD','UNPAID',30000,2100000,0,2130000,'Trần Thị B — 45 Lê Lợi, Q.1, TP.HCM','2026-06-10 09:00:00','2026-06-10 09:00:00'),
(2,'CONFIRMED','VNPAY','PAID',0,3199000,200000,2999000,'Nguyễn Văn A — 12 Nguyễn Huệ, Hoàn Kiếm, Hà Nội','2026-06-12 14:00:00','2026-06-12 16:00:00'),
(3,'PENDING','COD','UNPAID',30000,379000,0,409000,'Trần Thị B — 45 Lê Lợi, Q.1, TP.HCM','2026-06-15 10:30:00','2026-06-15 10:30:00'),
(2,'PENDING','COD','UNPAID',25000,249000,0,274000,'Nguyễn Văn A — 12 Nguyễn Huệ, Hoàn Kiếm, Hà Nội','2026-06-16 09:00:00','2026-06-16 09:00:00'),
(3,'CANCELLED','COD','UNPAID',0,499000,0,499000,'Trần Thị B — 45 Lê Lợi, Q.1, TP.HCM','2026-06-14 08:00:00','2026-06-14 11:00:00');

-- ============================================================
-- ORDER ITEMS
-- Variant IDs từ V2: 
--  Áo Nike variants: 1(S-Đen),2(M-Đen),3(L-Đen),4(XL-Đen),5(M-Trắng),6(L-Trắng)
--  Polo Yody: 7,8,9,10,11
--  Jean Slim: 12,13,14,15
--  Nike Air Max: 16,17,18,19,20
--  Adidas UB: 21,22,23,24
--  Túi Tote: 25,26
--  Váy Maxi: 27,28,29
--  Casio: 30,31
--  Rayban: 32,33
--  Áo Sơ Mi: 34,35,36,37
--  Stan Smith: 38,39,40,41
--  Dép Yody: 42,43,44,45,46
-- ============================================================

INSERT INTO order_items (order_id, variant_id, product_id, product_name, variant_label, thumbnail_url, price, quantity) VALUES
-- Order 1: Áo Nike M Đen + Áo Polo Yody M Navy
(1,2,1,'Áo thun Nike Dri-FIT Nam','M / Đen','https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=600&q=80',379000,1),
(1,8,2,'Áo Polo Yody Basic Nam','M / Xanh navy','https://images.unsplash.com/photo-1586363104862-3a5e2ab60d99?w=600&q=80',249000,1),

-- Order 2: Quần Jean 30
(2,13,3,'Quần Jeans Slim Fit Nam','30 / Xanh','https://images.unsplash.com/photo-1542272604-787c3835535d?w=600&q=80',499000,1),

-- Order 3: Nike Air Max 41
(3,18,4,'Giày Nike Air Max 270','41 / Đen/Trắng','https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=600&q=80',2699000,1),

-- Order 4: Quần Jean 32
(4,14,3,'Quần Jeans Slim Fit Nam','32 / Xanh','https://images.unsplash.com/photo-1542272604-787c3835535d?w=600&q=80',499000,1),

-- Order 5: Adidas UB 41
(5,23,5,'Giày Adidas Ultraboost 22','41 / Trắng','https://images.unsplash.com/photo-1608231387042-66d1773070a5?w=600&q=80',3199000,1),

-- Order 6: Áo Nike S Đen
(6,1,1,'Áo thun Nike Dri-FIT Nam','S / Đen','https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=600&q=80',379000,1),

-- Order 7: Casio G-Shock Đen
(7,30,8,'Đồng Hồ Casio G-Shock','OneSize / Đen','https://images.unsplash.com/photo-1524592094714-0f0654e20314?w=600&q=80',2100000,1),

-- Order 8: Polo Yody M Trắng
(8,10,2,'Áo Polo Yody Basic Nam','M / Trắng','https://images.unsplash.com/photo-1586363104862-3a5e2ab60d99?w=600&q=80',249000,1),

-- Order 9: Adidas Stan Smith 40
(9,39,11,'Sneaker Adidas Stan Smith','40 / Trắng/Xanh','https://images.unsplash.com/photo-1552346154-21d32810aba3?w=600&q=80',1899000,1),

-- Order 10: Váy Maxi M Hoa hồng
(10,28,7,'Váy Maxi Hoa Nữ','M / Hoa hồng','https://images.unsplash.com/photo-1572804013309-59a88b7e92f1?w=600&q=80',420000,1),

-- Order 11: Áo Sơ Mi M Trắng
(11,35,10,'Áo Sơ Mi Trắng Công Sở','M / Trắng','https://images.unsplash.com/photo-1598033129183-c4f50c736f10?w=600&q=80',299000,1),

-- Order 12: Kính Rayban Vàng
(12,32,9,'Kính Mát Rayban Aviator','OneSize / Vàng/Xanh','https://images.unsplash.com/photo-1572635196237-14b3f281503f?w=600&q=80',1500000,1),

-- Order 13: Áo Nike L Đen + Polo Yody L Navy
(13,3,1,'Áo thun Nike Dri-FIT Nam','L / Đen','https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=600&q=80',379000,1),
(13,9,2,'Áo Polo Yody Basic Nam','L / Xanh navy','https://images.unsplash.com/photo-1586363104862-3a5e2ab60d99?w=600&q=80',249000,1),

-- Order 14: Áo Nike L Trắng
(14,6,1,'Áo thun Nike Dri-FIT Nam','L / Trắng','https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=600&q=80',379000,1),

-- Order 15: Nike Air Max 40 x2
(15,17,4,'Giày Nike Air Max 270','40 / Đen/Trắng','https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=600&q=80',2699000,2),

-- Order 16: Túi Tote Đen
(16,26,6,'Túi Tote Vải Canvas Local Brand','OneSize / Đen','https://images.unsplash.com/photo-1544816155-12df9643f363?w=600&q=80',280000,1),

-- Order 17: Polo Yody S Navy
(17,7,2,'Áo Polo Yody Basic Nam','S / Xanh navy','https://images.unsplash.com/photo-1586363104862-3a5e2ab60d99?w=600&q=80',249000,1),

-- Order 18: Nike Air Max 42
(18,19,4,'Giày Nike Air Max 270','42 / Đen/Trắng','https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=600&q=80',2699000,1),

-- Order 19: Áo Nike M Đen x2 + Áo Nike L Trắng
(19,2,1,'Áo thun Nike Dri-FIT Nam','M / Đen','https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=600&q=80',379000,2),

-- Order 20: Jean 30
(20,13,3,'Quần Jeans Slim Fit Nam','30 / Xanh','https://images.unsplash.com/photo-1542272604-787c3835535d?w=600&q=80',499000,1),

-- Order 21: Adidas UB 42
(21,24,5,'Giày Adidas Ultraboost 22','42 / Trắng','https://images.unsplash.com/photo-1608231387042-66d1773070a5?w=600&q=80',3199000,1),

-- Order 22: Váy Maxi L
(22,29,7,'Váy Maxi Hoa Nữ','L / Hoa hồng','https://images.unsplash.com/photo-1572804013309-59a88b7e92f1?w=600&q=80',420000,1),

-- Order 23: Áo Sơ Mi L
(23,36,10,'Áo Sơ Mi Trắng Công Sở','L / Trắng','https://images.unsplash.com/photo-1598033129183-c4f50c736f10?w=600&q=80',299000,1),

-- Order 24: Adidas Stan Smith 41
(24,40,11,'Sneaker Adidas Stan Smith','41 / Trắng/Xanh','https://images.unsplash.com/photo-1552346154-21d32810aba3?w=600&q=80',1899000,1),

-- Order 25: Nike Air Max 41
(25,18,4,'Giày Nike Air Max 270','41 / Đen/Trắng','https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=600&q=80',2699000,1),

-- Order 26: Áo Nike M Trắng + Polo Yody L Trắng
(26,5,1,'Áo thun Nike Dri-FIT Nam','M / Trắng','https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=600&q=80',379000,1),
(26,11,2,'Áo Polo Yody Basic Nam','L / Trắng','https://images.unsplash.com/photo-1586363104862-3a5e2ab60d99?w=600&q=80',249000,1),

-- Order 27: Jean 32
(27,14,3,'Quần Jeans Slim Fit Nam','32 / Xanh','https://images.unsplash.com/photo-1542272604-787c3835535d?w=600&q=80',499000,1),

-- Order 28: Casio G-Shock Đen (SHIPPING)
(28,30,8,'Đồng Hồ Casio G-Shock','OneSize / Đen','https://images.unsplash.com/photo-1524592094714-0f0654e20314?w=600&q=80',2100000,1),

-- Order 29: Adidas UB 41 (CONFIRMED)
(29,23,5,'Giày Adidas Ultraboost 22','41 / Trắng','https://images.unsplash.com/photo-1608231387042-66d1773070a5?w=600&q=80',3199000,1),

-- Order 30: Áo Nike S Đen (PENDING)
(30,1,1,'Áo thun Nike Dri-FIT Nam','S / Đen','https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=600&q=80',379000,1),

-- Order 31: Polo Yody M Navy (PENDING)
(31,8,2,'Áo Polo Yody Basic Nam','M / Xanh navy','https://images.unsplash.com/photo-1586363104862-3a5e2ab60d99?w=600&q=80',249000,1),

-- Order 32: Jean 30 (CANCELLED)
(32,13,3,'Quần Jeans Slim Fit Nam','30 / Xanh','https://images.unsplash.com/photo-1542272604-787c3835535d?w=600&q=80',499000,1);

-- ============================================================
-- Update sold_count trên products theo order items thực tế
-- ============================================================
UPDATE products SET sold_count = 8  WHERE id = 1;  -- Áo Nike
UPDATE products SET sold_count = 6  WHERE id = 2;  -- Polo Yody
UPDATE products SET sold_count = 5  WHERE id = 3;  -- Quần Jean
UPDATE products SET sold_count = 5  WHERE id = 4;  -- Nike Air Max
UPDATE products SET sold_count = 4  WHERE id = 5;  -- Adidas UB
UPDATE products SET sold_count = 1  WHERE id = 6;  -- Túi Tote
UPDATE products SET sold_count = 2  WHERE id = 7;  -- Váy Maxi
UPDATE products SET sold_count = 2  WHERE id = 8;  -- Casio
UPDATE products SET sold_count = 1  WHERE id = 9;  -- Rayban
UPDATE products SET sold_count = 2  WHERE id = 10; -- Áo Sơ Mi
UPDATE products SET sold_count = 2  WHERE id = 11; -- Stan Smith
UPDATE products SET sold_count = 0  WHERE id = 12; -- Dép Yody

-- ============================================================
-- REVIEWS (từ khách hàng đã mua hàng)
-- ============================================================
INSERT INTO reviews (user_id, product_id, order_id, rating, comment, is_approved) VALUES
(2,1,1,5,'Áo rất chất, mặc thoáng mát, đúng size. Sẽ mua thêm!',1),
(3,3,2,4,'Quần vừa vặn, chất vải tốt. Giao hàng nhanh.',1),
(2,4,3,5,'Giày đẹp lắm, đi cực êm, đúng như mô tả!',1),
(2,5,5,5,'Giày chất lượng cao, đáng tiền. Rất hài lòng.',1),
(3,1,6,4,'Áo ổn, nhưng màu hơi sáng hơn ảnh một chút.',1),
(2,8,7,5,'Đồng hồ đẹp, chắc chắn. Rất đáng mua!',1),
(2,11,9,4,'Giày đẹp classic, da thật mềm, đi thoải mái.',0),
(3,7,10,5,'Váy đẹp lắm, vải mỏng nhẹ, phù hợp đi biển.',0);

SET FOREIGN_KEY_CHECKS = 1;

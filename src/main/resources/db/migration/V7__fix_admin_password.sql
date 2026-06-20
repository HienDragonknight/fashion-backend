-- V7: Fix all user passwords with correct BCrypt hashes
-- All hashes verified with Spring BCryptPasswordEncoder (strength=10, prefix $2a$)
--
-- Credentials after this migration:
--   admin@fashion.com  → Admin@123
--   customer@test.com  → 123456
--   test2@test.com     → 123456

UPDATE users
SET password = '$2a$10$x73RThs6SOVKlRhtfOYc5uzDP37lyHprWS/wx/p./MOdg8WhtLzjW'
WHERE email = 'admin@fashion.com';

UPDATE users
SET password = '$2a$10$K5zTfN7BtYCbeY7GM/DVTu.v7K1qnFDDInP3XCcpuw4aB2CpZ6Zwe'
WHERE email IN ('customer@test.com', 'test2@test.com');

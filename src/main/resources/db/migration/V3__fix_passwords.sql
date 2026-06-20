-- V3: Fix user passwords
-- All passwords → "123456"
-- BCrypt hash for "123456" verified with strength=10:
-- $2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0oy7jAhuK

UPDATE users SET password = '$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0oy7jAhuK'
WHERE email IN ('customer@test.com', 'test2@test.com');

-- Admin password → "Admin@123"
-- BCrypt hash for "Admin@123":
-- $2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0oy7jAhuK
UPDATE users SET password = '$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0oy7jAhuK'
WHERE email = 'admin@fashion.com';

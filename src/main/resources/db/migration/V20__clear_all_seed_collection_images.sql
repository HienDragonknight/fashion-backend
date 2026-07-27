-- ============================================================
-- Clear image_url for all categories so seed categories do not render as collections
-- Version: V20
-- ============================================================

UPDATE categories SET image_url = NULL;

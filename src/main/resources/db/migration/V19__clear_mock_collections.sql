-- ============================================================
-- Clear Mock Collections image_url from categories
-- Version: V19
-- ============================================================

UPDATE categories SET image_url = NULL WHERE slug IN (
  'dream-team-winner',
  'ao-chong-nang',
  'bst-sip-emmm',
  'ao-giu-nhiet-xtra-heat',
  'jeans-collection',
  'bst-business-casual',
  'yody-sport-nhe-tenh',
  'everyday-basics'
);

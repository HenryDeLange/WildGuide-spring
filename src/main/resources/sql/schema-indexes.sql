-- USERS
CREATE INDEX IF NOT EXISTS idx_users_username ON "users"(username);

-- GUIDES
CREATE INDEX IF NOT EXISTS idx_guides_visibility ON "guides"(visibility);

-- GUIDE ENTRIES
CREATE INDEX IF NOT EXISTS idx_guides_name ON "guide_entries"(name);
CREATE INDEX IF NOT EXISTS idx_guides_scientific_name ON "guide_entries"(scientificName);

-- USERS
CREATE INDEX IF NOT EXISTS idx_users_username ON "users"(username);

-- GUIDES
CREATE INDEX IF NOT EXISTS idx_guides_visibility ON "guides"(visibility);

-- USERS
CREATE TABLE IF NOT EXISTS "users" (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(64) NOT NULL UNIQUE,
    password VARCHAR(256) NOT NULL,
    created_by BIGINT,
    created_date TIMESTAMP,
    last_modified_by BIGINT,
    last_modified_date TIMESTAMP
);

-- GUIDES
CREATE TABLE IF NOT EXISTS "guides" (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(64) NOT NULL UNIQUE,
    description TEXT,
    visibility VARCHAR(12) NOT NULL,
    created_by BIGINT,
    created_date TIMESTAMP,
    last_modified_by BIGINT,
    last_modified_date TIMESTAMP
);

-- GUIDE_OWNERS
CREATE TABLE IF NOT EXISTS "guide_owners" (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    guide_id BIGINT,
    user_id BIGINT,
    FOREIGN KEY (guide_id) REFERENCES "guides"(id),
    FOREIGN KEY (user_id) REFERENCES "users"(id)
);

-- GUIDE_MEMBERS
CREATE TABLE IF NOT EXISTS "guide_members" (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    guide_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (guide_id) REFERENCES "guides"(id),
    FOREIGN KEY (user_id) REFERENCES "users"(id)
);

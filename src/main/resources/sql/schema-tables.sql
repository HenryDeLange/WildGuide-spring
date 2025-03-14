-- USERS
CREATE TABLE IF NOT EXISTS "users" (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(64) NOT NULL UNIQUE,
    password VARCHAR(60) NOT NULL,
    created_by BIGINT,
    created_date TIMESTAMP,
    last_modified_by BIGINT,
    last_modified_date TIMESTAMP
);

-- GUIDES
CREATE TABLE IF NOT EXISTS "guides" (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(128) NOT NULL UNIQUE,
    summary VARCHAR(256),
    description TEXT,
    visibility VARCHAR(12) NOT NULL,
    inaturalist_criteria VARCHAR(512),
    created_by BIGINT,
    created_date TIMESTAMP,
    last_modified_by BIGINT,
    last_modified_date TIMESTAMP
);

-- GUIDE OWNERS
CREATE TABLE IF NOT EXISTS "guide_owners" (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    guide_id BIGINT,
    user_id BIGINT,
    created_by BIGINT,
    created_date TIMESTAMP,
    last_modified_by BIGINT,
    last_modified_date TIMESTAMP,
    FOREIGN KEY (guide_id) REFERENCES "guides"(id),
    FOREIGN KEY (user_id) REFERENCES "users"(id),
    UNIQUE (guide_id, user_id)
);

-- GUIDE MEMBERS
CREATE TABLE IF NOT EXISTS "guide_members" (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    guide_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    created_by BIGINT,
    created_date TIMESTAMP,
    last_modified_by BIGINT,
    last_modified_date TIMESTAMP,
    FOREIGN KEY (guide_id) REFERENCES "guides"(id),
    FOREIGN KEY (user_id) REFERENCES "users"(id),
    UNIQUE (guide_id, user_id)
);

-- GUIDE ENTRIES
CREATE TABLE IF NOT EXISTS "guide_entries" (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    guide_id BIGINT NOT NULL,
    name VARCHAR(128) NOT NULL,
    scientific_name VARCHAR(128) NOT NULL,
    scientific_rank VARCHAR(24) NOT NULL,
    summary VARCHAR(256),
    description TEXT,
    inaturalist_taxon BIGINT,
    created_by BIGINT,
    created_date TIMESTAMP,
    last_modified_by BIGINT,
    last_modified_date TIMESTAMP,
    FOREIGN KEY (guide_id) REFERENCES "guides"(id),
    UNIQUE (guide_id, name),
    UNIQUE (guide_id, scientific_name),
    UNIQUE (guide_id, inaturalist_taxon)
);

-- STARS
CREATE TABLE IF NOT EXISTS "guide_stars" (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    guide_id BIGINT NOT NULL,
    created_by BIGINT,
    created_date TIMESTAMP,
    last_modified_by BIGINT,
    last_modified_date TIMESTAMP,
    FOREIGN KEY (guide_id) REFERENCES "guides"(id),
    UNIQUE (user_id, guide_id)
);

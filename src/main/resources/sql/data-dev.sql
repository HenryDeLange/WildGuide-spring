-- Insert users
INSERT INTO "users" (id, username, password, created_by, created_date, last_modified_by, last_modified_date)
SELECT 1, 'test', '$2a$10$eAMJP3aId.GfC6.Ka44NjeMRTMT0ir3bvnrQk27NTHI5339VVjOiq', 1, CURRENT_TIMESTAMP(), 1, CURRENT_TIMESTAMP();

-- Loop using temporary tables
CREATE TEMPORARY TABLE temp_guide_ids (i INT);
INSERT INTO temp_guide_ids (i) SELECT x FROM SYSTEM_RANGE(1, 500);
CREATE TEMPORARY TABLE temp_entry_ids (i INT, j INT);
INSERT INTO temp_entry_ids (i, j) SELECT i, x FROM temp_guide_ids, SYSTEM_RANGE(1, 500);

-- Insert guides
INSERT INTO "guides" (name, summary, description, visibility, inaturalist_criteria, created_by, created_date, last_modified_by, last_modified_date)
SELECT CONCAT('Guide ', i), 'Summary for guide', 'Description for guide', 'PUBLIC', null, 1, CURRENT_TIMESTAMP(), 1, CURRENT_TIMESTAMP()
FROM temp_guide_ids;

-- Insert guide owners
INSERT INTO "guide_owners" (guide_id, user_id, created_by, created_date, last_modified_by, last_modified_date)
SELECT i, 1, 1, CURRENT_TIMESTAMP(), 1, CURRENT_TIMESTAMP()
FROM temp_guide_ids;

-- Star guides stars
INSERT INTO "guide_stars" (user_id, guide_id, created_by, created_date, last_modified_by, last_modified_date)
SELECT 1, i, 1, CURRENT_TIMESTAMP(), 1, CURRENT_TIMESTAMP()
FROM temp_guide_ids
WHERE i % 30 = 0;

-- Insert entries
INSERT INTO "guide_entries" (guide_id, name, scientific_name, scientific_rank, summary, description, inaturalist_taxon, created_by, created_date, last_modified_by, last_modified_date)
SELECT i, CONCAT('Entry ', j, ' for guide ', i), CONCAT('Species ', j), 'SPECIES', 'Summary for entry', 'Description for entry', null, 1, CURRENT_TIMESTAMP(), 1, CURRENT_TIMESTAMP()
FROM temp_entry_ids;

-- Clean up temporary tables
DROP TABLE temp_guide_ids;
DROP TABLE temp_entry_ids;

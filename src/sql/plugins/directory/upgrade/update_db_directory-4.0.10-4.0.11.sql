-- 
-- Trim the title of all existing entry
-- 
UPDATE directory_entry SET title = TRIM(title);

--
-- Dumping data in table directory_entry_parameter
--
INSERT INTO directory_entry_parameter (parameter_key, parameter_value) VALUES ('indexed_as_title', '0');
INSERT INTO directory_entry_parameter (parameter_key, parameter_value) VALUES ('indexed_as_summary', '0');


--
-- Add column in table directory_entry
--
ALTER TABLE directory_entry ADD COLUMN is_indexed_as_summary SMALLINT DEFAULT 0 AFTER is_indexed;
ALTER TABLE directory_entry ADD COLUMN is_indexed_as_title SMALLINT DEFAULT 0 AFTER is_indexed;
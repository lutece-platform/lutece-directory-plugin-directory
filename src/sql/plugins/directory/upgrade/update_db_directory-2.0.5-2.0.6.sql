--
-- Dumping data in table directory_directory_parameter
--
INSERT INTO directory_directory_parameter (parameter_key, parameter_value) VALUES ('is_indexed', '1');


--
-- Add column in table directory_directory
--
ALTER TABLE directory_directory ADD COLUMN is_indexed SMALLINT DEFAULT 1 AFTER is_directory_record_activated;

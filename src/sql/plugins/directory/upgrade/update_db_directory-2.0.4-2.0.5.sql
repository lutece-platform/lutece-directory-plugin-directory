--
-- Dumping data in table directory_directory_parameter
--
INSERT INTO directory_directory_parameter (parameter_key, parameter_value) VALUES ('unavailability_message', 'Annuaire indisponible.');
INSERT INTO directory_directory_parameter (parameter_key, parameter_value) VALUES ('activate_directory_record', '1');

--
-- Add column in table directory_directory
--
ALTER TABLE directory_directory ADD COLUMN is_directory_record_activated SMALLINT DEFAULT 1 AFTER is_ascending_sort;

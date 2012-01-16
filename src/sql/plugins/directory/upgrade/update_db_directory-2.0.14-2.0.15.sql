--
-- DIRECTORY-150 : Add the possibility to upload multiple files
--
INSERT INTO directory_entry_parameter (parameter_key, parameter_value) VALUES ('file_max_size', '5242880');
INSERT INTO directory_entry_parameter (parameter_key, parameter_value) VALUES ('max_files', '1');

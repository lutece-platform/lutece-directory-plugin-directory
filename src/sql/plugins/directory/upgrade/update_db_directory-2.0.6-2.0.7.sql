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

--
-- Add new lines for table directory_field for the EntryTypeMyLutece
--

-- First, the primary key is set to AUTO_INCREMENT, so that new records will automatically set a new id_field
ALTER TABLE directory_field CHANGE id_field id_field INT NOT NULL AUTO_INCREMENT;
-- Insert new lines by fetching the id_entry of the EntryTypeMyLutece (19 is the id_type of EntryTypeMyLutece) 
INSERT INTO directory_field (id_entry) SELECT id_entry FROM directory_entry WHERE id_type = 19;
-- Revert the attribute of the primary_key id_field
ALTER TABLE directory_field CHANGE id_field id_field INT DEFAULT 0 NOT NULL;

--
-- Add new entry type EntryTypeDownloadUrl
--
INSERT INTO directory_entry_type(id_type,title_key,is_group,is_comment,is_mylutece_user,class_name) VALUES (20,'directory.entry_type_download_url.title',FALSE,FALSE,FALSE,'fr.paris.lutece.plugins.directory.business.EntryTypeDownloadUrl');

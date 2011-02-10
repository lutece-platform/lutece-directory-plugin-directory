--
-- Update table form_entry_type 
--
ALTER TABLE directory_entry_type ADD COLUMN is_mylutece_user SMALLINT default 0 AFTER is_comment;

--
-- Dumping data in table directory_entry_type
--
insert into directory_entry_type(id_type,title_key,is_group,is_comment,is_mylutece_user,class_name) values(19,'directory.entry_type_mylutece_user.title',FALSE,FALSE,TRUE,'fr.paris.lutece.plugins.directory.business.EntryTypeMyLuteceUser');

--
-- Update table directory_entry_type
--
UPDATE directory_entry_type SET is_mylutece_user = false WHERE id_type = 1;
UPDATE directory_entry_type SET is_mylutece_user = false WHERE id_type = 2;
UPDATE directory_entry_type SET is_mylutece_user = false WHERE id_type = 3;
UPDATE directory_entry_type SET is_mylutece_user = false WHERE id_type = 4;
UPDATE directory_entry_type SET is_mylutece_user = false WHERE id_type = 5;
UPDATE directory_entry_type SET is_mylutece_user = false WHERE id_type = 6;
UPDATE directory_entry_type SET is_mylutece_user = false WHERE id_type = 7;
UPDATE directory_entry_type SET is_mylutece_user = false WHERE id_type = 8;
UPDATE directory_entry_type SET is_mylutece_user = false WHERE id_type = 9;
UPDATE directory_entry_type SET is_mylutece_user = false WHERE id_type = 10;
UPDATE directory_entry_type SET is_mylutece_user = false WHERE id_type = 11;
UPDATE directory_entry_type SET is_mylutece_user = false WHERE id_type = 12;
UPDATE directory_entry_type SET is_mylutece_user = false WHERE id_type = 13;
UPDATE directory_entry_type SET is_mylutece_user = false WHERE id_type = 14;
UPDATE directory_entry_type SET is_mylutece_user = false WHERE id_type = 15;
UPDATE directory_entry_type SET is_mylutece_user = false WHERE id_type = 16;
UPDATE directory_entry_type SET is_mylutece_user = false WHERE id_type = 17;
UPDATE directory_entry_type SET is_mylutece_user = false WHERE id_type = 18;

--
-- Update table directory_entry
--
UPDATE directory_entry  SET is_shown_in_export = 0 WHERE id_type = 10;

--
-- Update table directory_directory 
--
ALTER TABLE directory_directory ADD COLUMN is_ascending_sort SMALLINT default 1 AFTER is_date_shown_in_export;
ALTER TABLE directory_directory ADD COLUMN id_sort_entry VARCHAR(50) DEFAULT NULL AFTER is_ascending_sort;

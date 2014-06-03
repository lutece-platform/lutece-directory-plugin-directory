INSERT INTO directory_entry_type(id_type,title_key,is_group,is_comment,is_mylutece_user,class_name) 
values
(22,'directory.entry_type_array.title',FALSE,FALSE,FALSE,'fr.paris.lutece.plugins.directory.business.EntryTypeArray');

ALTER TABLE directory_entry ADD COLUMN num_row SMALLINT DEFAULT 0;
ALTER TABLE directory_entry ADD COLUMN num_column SMALLINT DEFAULT 0;
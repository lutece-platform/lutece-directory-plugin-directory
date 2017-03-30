insert into directory_entry_type(id_type,title_key,is_group,is_comment,is_mylutece_user,class_name) values(23,'directory.entry_type_camera.title',0,0,0,'fr.paris.lutece.plugins.directory.business.EntryTypeCamera');
ALTER TABLE directory_field ADD COLUMN  image_type varchar(50) DEFAULT NULL;
ALTER TABLE directory_file ADD COLUMN extension varchar(50) DEFAULT NULL;

UPDATE directory_action SET icon_url = 'edit' WHERE id_action = 1;
UPDATE directory_action SET icon_url = 'edit' WHERE id_action = 2;
UPDATE directory_action SET icon_url = 'folder-open' WHERE id_action = 3;
UPDATE directory_action SET icon_url = 'folder-open' WHERE id_action = 4;
UPDATE directory_action SET icon_url = 'upload' WHERE id_action = 5;
UPDATE directory_action SET icon_url = 'upload' WHERE id_action = 6;
UPDATE directory_action SET icon_url = 'trash-o' WHERE id_action = 7;
UPDATE directory_action SET icon_url = 'trash-o' WHERE id_action = 8;
UPDATE directory_action SET icon_url = 'remove' WHERE id_action = 9;
UPDATE directory_action SET icon_url = 'check' WHERE id_action = 10;
UPDATE directory_action SET icon_url = 'clone' WHERE id_action = 11;
UPDATE directory_action SET icon_url = 'clone' WHERE id_action = 12;
UPDATE directory_action SET icon_url = 'trash' WHERE id_action = 13;

UPDATE directory_record_action SET icon_url = 'edit' WHERE id_action = 1;
UPDATE directory_record_action SET icon_url = 'edit' WHERE id_action = 2;
UPDATE directory_record_action SET icon_url = 'clone' WHERE id_action = 3;
UPDATE directory_record_action SET icon_url = 'clone' WHERE id_action = 4;
UPDATE directory_record_action SET icon_url = 'trash' WHERE id_action = 5;
UPDATE directory_record_action SET icon_url = 'remove' WHERE id_action = 6;
UPDATE directory_record_action SET icon_url = 'check' WHERE id_action = 7;
UPDATE directory_record_action SET icon_url = 'trash' WHERE id_action = 8;
UPDATE directory_record_action SET icon_url = 'list' WHERE id_action = 9;
UPDATE directory_record_action SET icon_url = 'list' WHERE id_action = 10;
UPDATE directory_record_action SET icon_url = 'eye' WHERE id_action = 11;
UPDATE directory_record_action SET icon_url = 'eye' WHERE id_action = 12;

UPDATE directory_xsl_action SET icon_url = 'trash' WHERE id_action = 1;
UPDATE directory_xsl_action SET icon_url = 'edit' WHERE id_action = 2;
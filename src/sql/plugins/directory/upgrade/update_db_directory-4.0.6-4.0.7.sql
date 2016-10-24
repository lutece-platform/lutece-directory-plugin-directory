insert into directory_entry_type(id_type,title_key,is_group,is_comment,is_mylutece_user,class_name) values(23,'directory.entry_type_camera.title',0,0,0,'fr.paris.lutece.plugins.directory.business.EntryTypeCamera');
ALTER TABLE directory_field ADD COLUMN  image_type varchar(50) DEFAULT NULL;
ALTER TABLE directory_file ADD COLUMN extension varchar(50) DEFAULT NULL;
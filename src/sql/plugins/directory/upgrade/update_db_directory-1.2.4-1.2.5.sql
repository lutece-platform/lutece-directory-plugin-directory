-- INSERT INTO directory_entry_type(id_type,title_key,is_group,is_comment,class_name) VALUES (16,'directory.entry_type_geolocation.title',FALSE,FALSE,'fr.paris.lutece.plugins.directory.business.EntryTypeGeolocation');
insert into directory_entry_type(id_type,title_key,is_group,is_comment,class_name) values(17,'directory.entry_type_internallink.title',FALSE,FALSE,'fr.paris.lutece.plugins.directory.business.EntryTypeInternalLink');
insert into directory_entry_type(id_type,title_key,is_group,is_comment,class_name) values(18,'directory.entry_type_richtext.title',FALSE,FALSE,'fr.paris.lutece.plugins.directory.business.EntryTypeRichText');

ALTER TABLE directory_entry ADD COLUMN map_provider VARCHAR(45) DEFAULT NULL;
ALTER TABLE directory_entry ADD COLUMN  is_autocomplete_entry SMALLINT DEFAULT 0;
DROP TABLE IF EXISTS directory_directory CASCADE;
DROP TABLE IF EXISTS directory_entry CASCADE;
DROP TABLE IF EXISTS directory_field CASCADE;
DROP TABLE IF EXISTS directory_entry_type CASCADE;
DROP TABLE IF EXISTS directory_xsl CASCADE;
DROP TABLE IF EXISTS directory_category CASCADE;
DROP TABLE IF EXISTS directory_record CASCADE;
DROP TABLE IF EXISTS directory_template CASCADE;
DROP TABLE IF EXISTS directory_record_field CASCADE;
DROP TABLE IF EXISTS directory_verify_by CASCADE;
DROP TABLE IF EXISTS directory_action CASCADE;
DROP TABLE IF EXISTS directory_record_action CASCADE;
DROP TABLE IF EXISTS directory_file CASCADE;
DROP TABLE IF EXISTS directory_physical_file CASCADE;
DROP TABLE IF EXISTS directory_indexer_action CASCADE;
DROP TABLE IF EXISTS directory_directory_parameter CASCADE;
DROP TABLE IF EXISTS directory_entry_parameter CASCADE;
DROP TABLE IF EXISTS directory_xsl_action CASCADE;
DROP TABLE IF EXISTS directory_rss_cf CASCADE;
DROP TABLE IF EXISTS directory_directory_attribute CASCADE;

/*==============================================================*/
/* Table structure for table directory_directory				*/
/*==============================================================*/
CREATE TABLE directory_directory (
  id_directory INT DEFAULT 0 NOT NULL,
  title VARCHAR(255) DEFAULT NULL,
  description LONG VARCHAR DEFAULT NULL,
  unavailability_message LONG VARCHAR DEFAULT NULL ,
  workgroup_key VARCHAR(255) DEFAULT NULL,
  role_key VARCHAR(50) DEFAULT NULL,
  is_enabled SMALLINT DEFAULT 0,
  date_creation timestamp DEFAULT NULL NULL,
  id_result_list_template INT DEFAULT NULL,
  id_result_record_template INT DEFAULT NULL,
  id_form_search_template INT DEFAULT NULL,
  number_record_per_page INT DEFAULT NULL,
  id_workflow INT DEFAULT NULL,
  is_search_wf_state SMALLINT DEFAULT 0, 
  is_search_comp_wf_state SMALLINT DEFAULT 0,
  is_ascending_sort SMALLINT DEFAULT 1,
  is_directory_record_activated SMALLINT DEFAULT 1,
  is_indexed SMALLINT DEFAULT 1,
  id_sort_entry VARCHAR(50) DEFAULT NULL,
  id_sort_entry_front VARCHAR(50) DEFAULT NULL,
  is_ascending_sort_front SMALLINT DEFAULT 1,
  front_office_title VARCHAR(255) DEFAULT NULL,
  automatic_record_removal_workflow_state INT DEFAULT -1,
  PRIMARY KEY  (id_directory)
  );

CREATE INDEX id_result_list_template_fk ON directory_directory (id_result_list_template);
CREATE INDEX id_result_record_template_fk ON directory_directory (id_result_record_template);
CREATE INDEX id_form_search_template_fk ON directory_directory (id_form_search_template);


/*==============================================================*/
/* Table structure for table directory_entry					*/
/*==============================================================*/
CREATE TABLE directory_entry (
  id_entry INT DEFAULT 0 NOT NULL,
  id_entry_parent INT DEFAULT NULL,
  id_directory INT DEFAULT NULL,
  id_type INT DEFAULT NULL,
  title LONG VARCHAR DEFAULT NULL,
  help_message LONG VARCHAR DEFAULT NULL,
  help_message_search LONG VARCHAR DEFAULT NULL,
  entry_comment LONG VARCHAR DEFAULT NULL,
  is_mandatory SMALLINT DEFAULT 0,
  is_indexed SMALLINT DEFAULT 0,
  is_indexed_as_title SMALLINT DEFAULT 0,
  is_indexed_as_summary SMALLINT DEFAULT 0,
  is_shown_in_search SMALLINT DEFAULT 0,
  is_shown_in_result_list SMALLINT DEFAULT 0,
  is_shown_in_result_record SMALLINT DEFAULT 0,
  is_fields_in_line SMALLINT DEFAULT 0,
  entry_position INT DEFAULT 0,
  display_height INT DEFAULT NULL,
  display_width INT DEFAULT NULL,
  is_role_associated SMALLINT DEFAULT 0,
  is_workgroup_associated SMALLINT DEFAULT 0,
  is_multiple_search_fields SMALLINT DEFAULT 0,
  is_shown_in_history SMALLINT DEFAULT 0,
  id_entry_associate INT DEFAULT NULL,
  request_sql LONG VARCHAR DEFAULT NULL,
  is_add_value_search_all SMALLINT DEFAULT 0,
  label_value_search_all LONG VARCHAR DEFAULT NULL,
  map_provider VARCHAR(45) DEFAULT NULL,
  is_autocomplete_entry SMALLINT DEFAULT 0,
  is_shown_in_export SMALLINT DEFAULT 0,
  is_shown_in_completeness SMALLINT DEFAULT 0,
  anonymize SMALLINT DEFAULT NULL,
  num_row SMALLINT DEFAULT 0,
  num_column SMALLINT DEFAULT 0,
  PRIMARY KEY  (id_entry)
  ) ;

CREATE INDEX  id_directory_fk ON directory_entry (id_directory);
CREATE INDEX  id_type_fk ON directory_entry (id_type);
/*==============================================================*/
/* Table structure for table directory_field					*/
/*==============================================================*/
CREATE TABLE directory_field (
  id_field INT DEFAULT 0 NOT NULL,
  id_entry INT DEFAULT NULL,
  title VARCHAR(255) DEFAULT NULL,
  DEFAULT_value LONG VARCHAR DEFAULT NULL,
  height INT DEFAULT NULL,
  width INT DEFAULT NULL,
  is_DEFAULT_value SMALLINT DEFAULT 0,
  max_size_enter INT DEFAULT NULL,
  field_position INT DEFAULT NULL,
  value_type_date date DEFAULT NULL,
  role_key VARCHAR(50) DEFAULT NULL,
  workgroup_key VARCHAR(255) DEFAULT NULL,
  is_shown_in_result_list SMALLINT DEFAULT 0,
  is_shown_in_result_record SMALLINT DEFAULT 0,
  PRIMARY KEY  (id_field)
) ;

CREATE INDEX  id_entry_fk ON directory_field (id_entry);

/*==============================================================*/
/*  Table structure for table directory_category			*/
/*==============================================================*/
CREATE TABLE directory_category (
  id_category INT DEFAULT 0 NOT NULL,
  title_key VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY  (id_category)
);

/*==============================================================*/
/*  Table structure for table directory_entry_type			*/
/*==============================================================*/
CREATE TABLE directory_entry_type (
  id_type INT DEFAULT 0 NOT NULL,
  title_key VARCHAR(255) DEFAULT NULL,
  is_group SMALLINT DEFAULT 0,
  is_comment SMALLINT DEFAULT 0,
  is_mylutece_user SMALLINT DEFAULT 0,
  class_name VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY  (id_type)
);


/*==============================================================*/
/* Table structure for table directory_xsl			*/
/*==============================================================*/
CREATE TABLE directory_xsl (
  id_directory_xsl INT DEFAULT 0 NOT NULL,
  title VARCHAR(255) DEFAULT NULL,
  description VARCHAR(255) DEFAULT NULL ,
  extension VARCHAR(255) DEFAULT NULL,
  id_file INT DEFAULT NULL,
  id_category INT DEFAULT NULL,
  PRIMARY KEY  (id_directory_xsl)
);


/*==============================================================*/
/* Table structure for table directory_record					*/
/*==============================================================*/
CREATE TABLE directory_record (
  id_record INT DEFAULT 0 NOT NULL,
  date_creation timestamp DEFAULT NULL NULL,
  date_modification timestamp DEFAULT NULL NULL,
  id_directory INT DEFAULT NULL,
  is_enabled SMALLINT DEFAULT 0,
  role_key VARCHAR(50) DEFAULT NULL,
  workgroup_key VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY  (id_record)
  );

CREATE INDEX  id_directory_fk_re ON directory_record (id_directory);

/*==============================================================*/
/* Table structure for table directory_record_field			*/
/*==============================================================*/
CREATE TABLE directory_record_field (
  id_record_field INT DEFAULT 0 NOT NULL,
  id_record INT DEFAULT NULL,
  record_field_value LONG VARCHAR DEFAULT NULL,
  id_entry INT DEFAULT NULL,
  id_field INT DEFAULT NULL,
  id_file INT DEFAULT NULL,
  PRIMARY KEY  (id_record_field)
 );

CREATE INDEX  id_entry_fk_ref ON directory_record_field (id_entry);
CREATE INDEX  id_record ON directory_record_field (id_record);

/*==============================================================*/
/* Table structure for table directory_file						*/
/*==============================================================*/
CREATE TABLE directory_file (
  id_file INT DEFAULT 0 NOT NULL,
  title LONG VARCHAR DEFAULT NULL, 
  id_physical_file INT DEFAULT NULL,  
  file_size  INT DEFAULT NULL,
  mime_type VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY  (id_file)
 );


/*==============================================================*/
/* Table structure for table directory_physical_file						*/
/*==============================================================*/
CREATE TABLE directory_physical_file (
  id_physical_file INT DEFAULT 0 NOT NULL,
  file_value LONG VARBINARY,  
  PRIMARY KEY  (id_physical_file)
 );



/*==============================================================*/
/* Table structure for table directory_verify_by				*/
/*==============================================================*/
CREATE TABLE directory_verify_by (
  id_field INT DEFAULT 0 NOT NULL,
  id_expression INT DEFAULT 0 NOT NULL,
  PRIMARY KEY  (id_field,id_expression)
 ) ;



/*==============================================================*/
/*Table structure for table directory_action					*/
/*==============================================================*/
CREATE TABLE directory_action (
  id_action INT DEFAULT 0 NOT NULL,
  name_key VARCHAR(100) DEFAULT NULL ,
  description_key VARCHAR(100) DEFAULT NULL  ,
  action_url VARCHAR(255) DEFAULT NULL ,
  icon_url VARCHAR(255) DEFAULT NULL,
  action_permission VARCHAR(255) DEFAULT NULL,
  directory_state SMALLINT DEFAULT 0,
  PRIMARY KEY (id_action)
);

/*==============================================================*/
/*Table structure for table directory_record_action				*/
/*==============================================================*/
CREATE TABLE directory_record_action (
  id_action INT DEFAULT 0 NOT NULL,
  name_key VARCHAR(100) DEFAULT NULL ,
  description_key VARCHAR(100) DEFAULT NULL  ,
  action_url VARCHAR(255) DEFAULT NULL ,
  icon_url VARCHAR(255) DEFAULT NULL,
  action_permission VARCHAR(255) DEFAULT NULL,
  directory_state SMALLINT DEFAULT 0,
  PRIMARY KEY (id_action)
);

/*==============================================================*/
/* Table structure for table directory_indexer_action					*/
/*==============================================================*/
CREATE TABLE directory_indexer_action (
  id_action INT DEFAULT 0 NOT NULL,
  id_record INT DEFAULT 0 NOT NULL,
  id_task INT DEFAULT 0 NOT NULL ,
  PRIMARY KEY (id_action)
  );

/*==============================================================*/
/*Table structure for table directory_xsl_action					*/
/*==============================================================*/
CREATE TABLE directory_xsl_action (
  id_action INT DEFAULT 0 NOT NULL,
  name_key VARCHAR(100) DEFAULT NULL ,
  description_key VARCHAR(100) DEFAULT NULL  ,
  action_url VARCHAR(255) DEFAULT NULL ,
  icon_url VARCHAR(255) DEFAULT NULL,
  action_permission VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (id_action)
);


/*==============================================================*/
/*Table structure for table directory_rss_cf					*/
/*==============================================================*/
CREATE TABLE directory_rss_cf (
  id_rss INT DEFAULT 0 NOT NULL,
  id_directory INT DEFAULT 0 NOT NULL,
  id_entry_title INT DEFAULT 0 NOT NULL,
  id_entry_description INT DEFAULT 0 NOT NULL,
  id_entry_image INT DEFAULT -1 NOT NULL,
  id_entry_link INT DEFAULT -1 NOT NULL,
  id_entry_filter_1 INT DEFAULT -1 NOT NULL,
  value_filter_1 LONG VARCHAR DEFAULT NULL,
  id_entry_filter_2 INT DEFAULT -1 NOT NULL,
  value_filter_2 LONG VARCHAR DEFAULT NULL,
  id_workflow_state INT DEFAULT -1 NOT NULL,
  PRIMARY KEY (id_rss)
);

/*==============================================================*/
/*Table structure for table directory_directory_parameter		*/
/*==============================================================*/
CREATE TABLE directory_directory_parameter (
	parameter_key varchar(100) NOT NULL,
	parameter_value varchar(100) NOT NULL,
	PRIMARY KEY (parameter_key)
);

/*==============================================================*/
/*Table structure for table directory_entry_parameter			*/
/*==============================================================*/
CREATE TABLE directory_entry_parameter (
	parameter_key varchar(100) NOT NULL,
	parameter_value varchar(100) NOT NULL,
	PRIMARY KEY (parameter_key)
);

CREATE INDEX  id_indexer_task ON directory_indexer_action (id_task);

/*==============================================================*/
/*Table structure for table directory_directory_attribute		*/
/*==============================================================*/
CREATE TABLE directory_directory_attribute (
	id_directory INT DEFAULT 0 NOT NULL,
	attribute_key varchar(255) NOT NULL,
	attribute_value varchar(255) NOT NULL,
	PRIMARY KEY (id_directory, attribute_key)
);

ALTER TABLE directory_directory ADD CONSTRAINT fk_id_result_list_template FOREIGN KEY (id_result_list_template)
     REFERENCES directory_xsl (id_directory_xsl)  ON DELETE RESTRICT ON UPDATE RESTRICT ;

ALTER TABLE directory_directory ADD CONSTRAINT fk_id_result_record_template FOREIGN KEY (id_result_record_template)
      REFERENCES directory_xsl (id_directory_xsl)  ON DELETE RESTRICT ON UPDATE RESTRICT ;

ALTER TABLE directory_directory ADD CONSTRAINT fk_id_form_search_template FOREIGN KEY (id_form_search_template)
      REFERENCES directory_xsl (id_directory_xsl)  ON DELETE RESTRICT ON UPDATE RESTRICT ;

ALTER TABLE directory_entry ADD CONSTRAINT fk_id_directory FOREIGN KEY (id_directory)
      REFERENCES directory_directory (id_directory)  ON DELETE RESTRICT ON UPDATE RESTRICT ;

ALTER TABLE directory_record ADD CONSTRAINT fk_re_id_directory FOREIGN KEY (id_directory)
      REFERENCES directory_directory (id_directory)  ON DELETE RESTRICT ON UPDATE RESTRICT ;

ALTER TABLE directory_field ADD CONSTRAINT fk_id_entry FOREIGN KEY (id_entry)
      REFERENCES directory_entry (id_entry)  ON DELETE RESTRICT ON UPDATE RESTRICT ;

ALTER TABLE directory_record_field ADD CONSTRAINT fk_ref_id_entry FOREIGN KEY (id_entry)
      REFERENCES directory_entry (id_entry)  ON DELETE RESTRICT ON UPDATE RESTRICT ;

ALTER TABLE directory_entry ADD CONSTRAINT fk_id_type FOREIGN KEY (id_type)
      REFERENCES directory_entry_type (id_type)  ON DELETE RESTRICT ON UPDATE RESTRICT ;

ALTER TABLE directory_record_field ADD CONSTRAINT fk_id_record FOREIGN KEY (id_record)
      REFERENCES directory_record (id_record)  ON DELETE RESTRICT ON UPDATE RESTRICT ;

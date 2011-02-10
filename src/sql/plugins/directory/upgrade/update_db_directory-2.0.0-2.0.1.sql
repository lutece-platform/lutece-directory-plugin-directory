--add date_creation visibility
ALTER TABLE directory_directory ADD COLUMN is_date_shown_in_result_list SMALLINT DEFAULT 0;
ALTER TABLE directory_directory ADD COLUMN is_date_shown_in_result_record SMALLINT DEFAULT 0;
ALTER TABLE directory_directory ADD COLUMN is_date_shown_in_history SMALLINT DEFAULT 0;
ALTER TABLE directory_directory ADD COLUMN is_date_shown_in_search SMALLINT DEFAULT 0;
ALTER TABLE directory_directory ADD COLUMN is_date_shown_in_advanced_search SMALLINT DEFAULT 0;
ALTER TABLE directory_directory ADD COLUMN is_date_shown_in_multi_search SMALLINT DEFAULT 0;
   
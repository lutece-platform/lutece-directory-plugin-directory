ALTER TABLE directory_directory ADD COLUMN automatic_record_removal_workflow_state INT DEFAULT -1;
UPDATE directory_directory SET automatic_record_removal_workflow_state = -1;

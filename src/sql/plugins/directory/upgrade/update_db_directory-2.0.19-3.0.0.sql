-- Dumping data for table lutece.directory_action: 13 rows
DELETE FROM directory_action;
INSERT INTO directory_action (id_action, name_key, description_key, action_url, icon_url, action_permission, directory_state) VALUES
	(1, 'directory.action.modify.name', 'directory.action.modify.description', 'jsp/admin/plugins/directory/ModifyDirectory.jsp', 'icon-edit', 'MODIFY', 0),
	(2, 'directory.action.modify.name', 'directory.action.modify.description', 'jsp/admin/plugins/directory/ModifyDirectory.jsp', 'icon-edit', 'MODIFY', 1),
	(3, 'directory.action.manage_record.name', 'directory.action.manage_record.description', 'jsp/admin/plugins/directory/ManageDirectoryRecord.jsp', ' icon-folder-open', 'MANAGE_RECORD', 0),
	(4, 'directory.action.manage_record.name', 'directory.action.manage_record.description', 'jsp/admin/plugins/directory/ManageDirectoryRecord.jsp', ' icon-folder-open', 'MANAGE_RECORD', 1),
	(5, 'directory.action.import_record.name', 'directory.action.import_record.description', 'jsp/admin/plugins/directory/ImportDirectoryRecord.jsp', 'icon-upload', 'IMPORT_RECORD', 0),
	(6, 'directory.action.import_record.name', 'directory.action.import_record.description', 'jsp/admin/plugins/directory/ImportDirectoryRecord.jsp', 'icon-upload', 'IMPORT_RECORD', 1),
	(7, 'directory.action.delete_all_directory_record.name', 'directory.action.delete_all_directory_record.description', 'jsp/admin/plugins/directory/ConfirmRemoveAllDirectoryRecord.jsp', 'icon-ban-circle', 'DELETE_ALL_RECORD', 0),
	(8, 'directory.action.delete_all_directory_record.name', 'directory.action.delete_all_directory_record.description', 'jsp/admin/plugins/directory/ConfirmRemoveAllDirectoryRecord.jsp', 'icon-ban-circle', 'DELETE_ALL_RECORD', 1),
	(9, 'directory.action.disable.name', 'directory.action.disable.description', 'jsp/admin/plugins/directory/ConfirmDisableDirectory.jsp', 'icon-remove', 'CHANGE_STATE', 1),
	(10, 'directory.action.enable.name', 'directory.action.enable.description', 'jsp/admin/plugins/directory/DoEnableDirectory.jsp', 'icon-ok', 'CHANGE_STATE', 0),
	(11, 'directory.action.copy.name', 'directory.action.copy.description', 'jsp/admin/plugins/directory/DoCopyDirectory.jsp', 'icon-plus-sign', 'COPY', 0),
	(12, 'directory.action.copy.name', 'directory.action.copy.description', 'jsp/admin/plugins/directory/DoCopyDirectory.jsp', 'icon-plus-sign', 'COPY', 1),
	(13, 'directory.action.delete.name', 'directory.action.delete.description', 'jsp/admin/plugins/directory/ConfirmRemoveDirectory.jsp', 'icon-trash', 'DELETE', 0);

-- Dumping data for table lutece.directory_record_action: 12 rows
DELETE FROM directory_record_action;
INSERT INTO directory_record_action (id_action, name_key, description_key, action_url, icon_url, action_permission, directory_state) VALUES
	(1, 'directory.action.modify.name', 'directory.action.modify.description.record', 'jsp/admin/plugins/directory/ModifyDirectoryRecord.jsp', 'icon-edit', 'MODIFY_RECORD', 0),
	(2, 'directory.action.modify.name', 'directory.action.modify.description.record', 'jsp/admin/plugins/directory/ModifyDirectoryRecord.jsp', 'icon-edit', 'MODIFY_RECORD', 1),
	(3, 'directory.action.copy.name', 'directory.action.copy.description', 'jsp/admin/plugins/directory/DoCopyDirectoryRecord.jsp', 'icon-plus-sign', 'COPY_RECORD', 0),
	(4, 'directory.action.copy.name', 'directory.action.copy.description', 'jsp/admin/plugins/directory/DoCopyDirectoryRecord.jsp', 'icon-plus-sign', 'COPY_RECORD', 1),
	(5, 'directory.action.delete.name', 'directory.action.delete.description', 'jsp/admin/plugins/directory/ConfirmRemoveDirectoryRecord.jsp', 'icon-trash', 'DELETE_RECORD', 0),
	(6, 'directory.action.disable.name', 'directory.action.disable.description', 'jsp/admin/plugins/directory/ConfirmDisableDirectoryRecord.jsp', 'icon-remove', 'CHANGE_STATE_RECORD', 1),
	(7, 'directory.action.enable.name', 'directory.action.enable.description', 'jsp/admin/plugins/directory/DoEnableDirectoryRecord.jsp', 'icon-ok', 'CHANGE_STATE_RECORD', 0),
	(8, 'directory.action.delete.name', 'directory.action.delete.description', 'jsp/admin/plugins/directory/ConfirmRemoveDirectoryRecord.jsp', 'icon-trash', 'DELETE_RECORD', 1),
	(9, 'directory.action.history.name', 'directory.action.history.description', 'jsp/admin/plugins/directory/ResourceHistory.jsp', 'icon-list', 'HISTORY_RECORD', 0),
	(10, 'directory.action.history.name', 'directory.action.history.description', 'jsp/admin/plugins/directory/ResourceHistory.jsp', 'icon-list', 'HISTORY_RECORD', 1),
	(11, 'directory.actions.title.visualisation', 'directory.actions.title.visualisation', 'jsp/admin/plugins/directory/DoVisualisationRecord.jsp', 'icon-eye-open', 'VISUALISATION_RECORD', 0),
	(12, 'directory.actions.title.visualisation', 'directory.actions.title.visualisation', 'jsp/admin/plugins/directory/DoVisualisationRecord.jsp', 'icon-eye-open', 'VISUALISATION_RECORD', 1);
-- Dumping data for table lutece.directory_xsl_action: 2 rows
DELETE FROM directory_xsl_action;
INSERT INTO directory_xsl_action (id_action, name_key, description_key, action_url, icon_url, action_permission) VALUES
	(2, 'directory.manage_directory_xsl.title_modify', 'directory.manage_directory_xsl.title_modify', 'jsp/admin/plugins/directory/ModifyDirectoryXsl.jsp', 'icon-edit', 'MODIFY'),
	(1, 'directory.manage_directory_xsl.title_delete', 'directory.manage_directory_xsl.title_delete', 'jsp/admin/plugins/directory/ConfirmRemoveDirectoryXsl.jsp', 'icon-trash', 'DELETE');

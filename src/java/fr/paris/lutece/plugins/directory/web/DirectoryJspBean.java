/*
 * Copyright (c) 2002-2009, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.directory.web;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;

import au.com.bytecode.opencsv.CSVReader;
import fr.paris.lutece.plugins.directory.business.Category;
import fr.paris.lutece.plugins.directory.business.Directory;
import fr.paris.lutece.plugins.directory.business.DirectoryAction;
import fr.paris.lutece.plugins.directory.business.DirectoryActionHome;
import fr.paris.lutece.plugins.directory.business.DirectoryFilter;
import fr.paris.lutece.plugins.directory.business.DirectoryHome;
import fr.paris.lutece.plugins.directory.business.DirectoryRemovalListenerService;
import fr.paris.lutece.plugins.directory.business.DirectoryXsl;
import fr.paris.lutece.plugins.directory.business.DirectoryXslFilter;
import fr.paris.lutece.plugins.directory.business.DirectoryXslHome;
import fr.paris.lutece.plugins.directory.business.Entry;
import fr.paris.lutece.plugins.directory.business.EntryFilter;
import fr.paris.lutece.plugins.directory.business.EntryHome;
import fr.paris.lutece.plugins.directory.business.EntryRemovalListenerService;
import fr.paris.lutece.plugins.directory.business.EntryTypeHome;
import fr.paris.lutece.plugins.directory.business.Field;
import fr.paris.lutece.plugins.directory.business.FieldHome;
import fr.paris.lutece.plugins.directory.business.File;
import fr.paris.lutece.plugins.directory.business.FileHome;
import fr.paris.lutece.plugins.directory.business.IEntry;
import fr.paris.lutece.plugins.directory.business.PhysicalFile;
import fr.paris.lutece.plugins.directory.business.PhysicalFileHome;
import fr.paris.lutece.plugins.directory.business.Record;
import fr.paris.lutece.plugins.directory.business.RecordField;
import fr.paris.lutece.plugins.directory.business.RecordFieldFilter;
import fr.paris.lutece.plugins.directory.business.RecordFieldHome;
import fr.paris.lutece.plugins.directory.business.RecordHome;
import fr.paris.lutece.plugins.directory.business.parameter.DirectoryParameterHome;
import fr.paris.lutece.plugins.directory.business.parameter.EntryParameterHome;
import fr.paris.lutece.plugins.directory.service.DirectoryResourceIdService;
import fr.paris.lutece.plugins.directory.service.DirectoryService;
import fr.paris.lutece.plugins.directory.service.RecordRemovalListenerService;
import fr.paris.lutece.plugins.directory.service.directorysearch.DirectorySearchService;
import fr.paris.lutece.plugins.directory.utils.DirectoryErrorException;
import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.portal.business.rbac.RBAC;
import fr.paris.lutece.portal.business.role.RoleHome;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.business.workflow.Action;
import fr.paris.lutece.portal.business.workflow.State;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.fileupload.FileUploadService;
import fr.paris.lutece.portal.service.html.XmlTransformerService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.mailinglist.AdminMailingListService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupService;
import fr.paris.lutece.portal.web.admin.PluginAdminPageJspBean;
import fr.paris.lutece.portal.web.constants.Parameters;
import fr.paris.lutece.portal.web.upload.MultipartHttpServletRequest;
import fr.paris.lutece.portal.web.util.LocalizedPaginator;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.UniqueIDGenerator;
import fr.paris.lutece.util.filesystem.FileSystemUtil;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.string.StringUtil;
import fr.paris.lutece.util.url.UrlItem;
import fr.paris.lutece.util.xml.XmlUtil;


/**
 * This class provides the user interface to manage form features ( manage,
 * create, modify, remove)
 */
public class DirectoryJspBean extends PluginAdminPageJspBean
{
    // Templates
    private static final String TEMPLATE_MANAGE_DIRECTORY = "admin/plugins/directory/manage_directory.html";
    private static final String TEMPLATE_MANAGE_DIRECTORY_RECORD = "admin/plugins/directory/manage_directory_record.html";
    private static final String TEMPLATE_CREATE_DIRECTORY_RECORD = "admin/plugins/directory/create_directory_record.html";
    private static final String TEMPLATE_MODIFY_DIRECTORY_RECORD = "admin/plugins/directory/modify_directory_record.html";
    private static final String TEMPLATE_CREATE_DIRECTORY = "admin/plugins/directory/create_directory.html";
    private static final String TEMPLATE_MODIFY_DIRECTORY = "admin/plugins/directory/modify_directory.html";
    private static final String TEMPLATE_CREATE_FIELD = "admin/plugins/directory/create_field.html";
    private static final String TEMPLATE_MOVE_ENTRY = "admin/plugins/directory/move_entry.html";
    private static final String TEMPLATE_MODIFY_FIELD = "admin/plugins/directory/modify_field.html";
    private static final String TEMPLATE_IMPORT_DIRECTORY_RECORD = "admin/plugins/directory/import_directory_record.html";
    private static final String TEMPLATE_INDEX_ALL_DIRECTORY = "admin/plugins/directory/index_all_directory.html";
    private static final String TEMPLATE_TASKS_FORM_WORKFLOW = "admin/plugins/directory/tasks_form_workflow.html";
    private static final String TEMPLATE_RESOURCE_HISTORY = "admin/plugins/directory/resource_history.html";
    private static final String TEMPLATE_VIEW_DIRECTORY_RECORD = "admin/plugins/directory/view_directory_record.html";
    private static final String TEMPLATE_MANAGE_MASS_PRINT = "admin/plugins/directory/select_mass_print.html";
    private static final String TEMPLATE_DISPLAY_MASS_PRINT = "admin/plugins/directory/display_mass_print.html";
    private static final String TEMPLATE_MANAGE_ADVANCED_PARAMETERS = "admin/plugins/directory/manage_advanced_parameters.html";
    private static final String TEMPLATE_VIEW_MYLUTECE_USER = "admin/plugins/directory/view_mylutece_user.html";
    private static final String TEMPLATE_IMPORT_FIELD = "admin/plugins/directory/import_field.html";

    // Messages (I18n keys)
    private static final String MESSAGE_CONFIRM_REMOVE_DIRECTORY = "directory.message.confirm_remove_directory";
    private static final String MESSAGE_CONFIRM_REMOVE_DIRECTORY_RECORD = "directory.message.confirm_remove_directory_record";
    private static final String MESSAGE_CONFIRM_REMOVE_ALL_DIRECTORY_RECORD = "directory.message.confirm_remove_all_directory_record";
    private static final String MESSAGE_CONFIRM_REMOVE_DIRECTORY_WITH_RECORD = "directory.message.confirm_remove_directory_with_record";
    private static final String MESSAGE_CONFIRM_DISABLE_DIRECTORY = "directory.message.confirm_disable_directory";
    private static final String MESSAGE_CONFIRM_DISABLE_DIRECTORY_RECORD = "directory.message.confirm_disable_directory_record";
    private static final String MESSAGE_CONFIRM_REMOVE_ENTRY = "directory.message.confirm_remove_entry";
    private static final String MESSAGE_CONFIRM_REMOVE_FIELD = "directory.message.confirm_remove_field";
    private static final String MESSAGE_CONFIRM_REMOVE_GROUP_WITH_ANY_ENTRY = "directory.message.confirm_remove_group_with_any_entry";
    private static final String MESSAGE_CONFIRM_REMOVE_GROUP_WITH_ENTRY = "directory.message.confirm_remove_group_whith_entry";
    private static final String MESSAGE_MANDATORY_FIELD = "directory.message.mandatory.field";
    private static final String MESSAGE_FIELD_VALUE_FIELD = "directory.message.field_value_field";
    private static final String MESSAGE_DIRECTORY_ERROR = "directory.message.directory_error";
    private static final String MESSAGE_DIRECTORY_ERROR_MANDATORY_FIELD = "directory.message.directory_error.mandatory.field";
    private static final String MESSAGE_SELECT_GROUP = "directory.message.select_group";
    private static final String MESSAGE_NUMERIC_FIELD = "directory.message.numeric.field";
    private static final String MESSAGE_ERROR_CSV_FILE_IMPORT = "directory.message.error_csv_file_import";
    private static final String MESSAGE_ERROR_CSV_NUMBER_SEPARATOR = "directory.message.error_csv_number_delimiter";
    private static final String MESSAGE_CANNOT_REMOVE_DIRECTORY = "directory.message.can_not_remove_directory";
    private static final String MESSAGE_CANNOT_REMOVE_RECORD = "directory.message.can_not_remove_record";
    private static final String MESSAGE_CANNOT_REMOVE_ENTRY = "directory.message.can_not_remove_entry";
    private static final String MESSAGE_WORKFLOW_CHANGE = "directory.message.workflow_change";

    //private static final String MESSAGE_CANNOT_CREATE_ENTRY_DIRECTORY_IS_NOT_EMPTY = "directory.message.can_not_create_entry_directory_is_not_empty";
    private static final String MESSAGE_CANNOT_REMOVE_ENTRY_DIRECTORY_IS_NOT_EMPTY = "directory.message.can_not_remove_entry_directory_is_not_empty";
    private static final String MESSAGE_CANNOT_REMOVE_FIELD_DIRECTORY_IS_NOT_EMPTY = "directory.message.can_not_remove_field_directory_is_not_empty";
    private static final String MESSAGE_CONFIRM_INDEX_ALL_DIRECTORY = "directory.message.confirm_index_all_directory";
    private static final String MESSAGE_ERROR_NOT_SELECTED_STATE = "directory.message.not_selected_state";
    private static final String MESSAGE_ERROR_NO_RECORD = "directory.message.no_record";
    private static final String FIELD_TITLE = "directory.create_directory.label_title";
    private static final String FIELD_DESCRIPTION = "directory.create_directory.label_description";
    private static final String FIELD_TITLE_FIELD = "directory.create_field.label_title";
    private static final String FIELD_VALUE_FIELD = "directory.create_field.label_value";
    private static final String FIELD_UNAVAILABILITY_MESSAGE = "directory.create_directory.label_unavailability_message";
    private static final String FIELD_ID_FORM_SEARCH_TEMPLATE = "directory.create_directory.label_form_search_template";
    private static final String FIELD_ID_RESULT_LIST_TEMPLATE = "directory.create_directory.label_result_list_template";
    private static final String FIELD_ID_RESULT_RECORD_TEMPLATE = "directory.create_directory.label_result_record_template";
    private static final String FIELD_NUMBER_RECORD_PER_PAGE = "directory.create_directory.label_number_record_per_page";
    private static final String FIELD_FILE_IMPORT = "directory.import_directory_record.label_file";
    private static final String FIELD_THUMBNAIL = "little_thumbnail";
    private static final String FIELD_BIG_THUMBNAIL = "big_thumbnail";
    private static final String FIELD_IMAGE = "image_full_size";

    //properties
    private static final String PROPERTY_ALL = "directory.manage_directory.select.all";
    private static final String PROPERTY_YES = "directory.manage_directory.select.yes";
    private static final String PROPERTY_NO = "directory.manage_directory.select.no";
    private static final String PROPERTY_NOTHING = "directory.create_directory.select.nothing";
    private static final String PROPERTY_MODIFY_DIRECTORY_TITLE = "directory.modify_directory.title";
    private static final String PROPERTY_MANAGE_DIRECTORY_RECORD_PAGE_TITLE = "directory.manage_directory_record.page_title";
    private static final String PROPERTY_CREATE_DIRECTORY_RECORD_PAGE_TITLE = "directory.create_directory_record.page_title";
    private static final String PROPERTY_MODIFY_DIRECTORY_RECORD_PAGE_TITLE = "directory.modify_directory_record.page_title";
    private static final String PROPERTY_IMPORT_DIRECTORY_RECORD_PAGE_TITLE = "directory.import_directory_record.page_title";
    private static final String PROPERTY_INDEX_ALL_DIRECTORY_PAGE_TITLE = "directory.index_all_directory.page_title";
    private static final String PROPERTY_MANAGE_DIRECTORY_PAGE_TITLE = "directory.manage_directory.page_title";
    private static final String PROPERTY_CREATE_DIRECTORY_PAGE_TITLE = "directory.create_directory.page_title";
    private static final String PROPERTY_CREATE_ENTRY_COMMENT_PAGE_TITLE = "directory.create_entry.page_title_comment";
    private static final String PROPERTY_CREATE_ENTRY_FIELD_PAGE_TITLE = "directory.create_entry.page_title_field";
    private static final String PROPERTY_MODIFY_ENTRY_COMMENT_PAGE_TITLE = "directory.modify_entry.page_title_comment";
    private static final String PROPERTY_MODIFY_ENTRY_FIELD_PAGE_TITLE = "directory.modify_entry.page_title_field";
    private static final String PROPERTY_MODIFY_ENTRY_GROUP_PAGE_TITLE = "directory.modify_entry.page_title_group";
    private static final String PROPERTY_CREATE_FIELD_PAGE_TITLE = "directory.create_field.page_title";
    private static final String PROPERTY_MODIFY_FIELD_PAGE_TITLE = "directory.modify_field.page_title";
    private static final String PROPERTY_COPY_DIRECTORY_TITLE = "directory.copy_directory.title";
    private static final String PROPERTY_COPY_ENTRY_TITLE = "directory.copy_entry.title";
    private static final String PROPERTY_IMPORT_CSV_DELIMITER = "directory.import.csv.delimiter";
    private static final String PROPERTY_LINE = "directory.import_directory_record.line";
    private static final String PROPERTY_ITEM_PER_PAGE = "directory.itemsPerPage";
    private static final String PROPERTY_TASKS_FORM_WORKFLOW_PAGE_TITLE = "directory.tasks_form_workflow.page_title";
    private static final String PROPERTY_RESOURCE_HISTORY_PAGE_TITLE = "directory.resource_history.page_title";
    private static final String PROPERTY_MASS_PRINT_PAGE_TITLE = "directory.mass_print.page_title";
    private static final String PROPERTY_ENTRY_AUTORIZE_FOR_ENTRY_DIRECTORY = "directory.entry_type_directory.entry_autorize";
    private static final String PROPERTY_PATH_TMP = "path.tmp";
    private static final String PROPERTY_ENTRY_TYPE_DIRECTORY = "directory.entry_type.directory";
    private static final String PROPERTY_ENTRY_TYPE_GEOLOCATION = "directory.entry_type.geolocation";
    private static final String PROPERTY_ENTRY_TYPE_IMAGE = "directory.resource_rss.entry_type_image";
    private static final String PROPERTY_ENTRY_TYPE_MYLUTECE_USER = "directory.entry_type.mylutece_user";
    private static final String PROPERTY_ENTRY_TYPE_DATE_CREATION_TITLE = "directory.entry_type_date_creation.title";
    private static final String PROPERTY_IMPORT_FIELD_PAGE_TITLE = "directory.import_field.page_title";

    //Markers
    private static final String MARK_HISTORY_LIST = "history_list";
    private static final String MARK_ID_DIRECTORY = "idDirectory";
    private static final String MARK_ID_STATE = "idState";
    private static final String MARK_WEBAPP_URL = "webapp_url";
    private static final String MARK_LOCALE = "locale";
    private static final String MARK_PAGINATOR = "paginator";
    private static final String MARK_USER_WORKGROUP_REF_LIST = "user_workgroup_list";
    private static final String MARK_USER_WORKGROUP_SELECTED = "user_workgroup_selected";
    private static final String MARK_ACTIVE_REF_LIST = "active_list";
    private static final String MARK_ACTIVE_SELECTED = "active_selected";
    private static final String MARK_ENTRY_TYPE_LIST = "entry_type_list";
    private static final String MARK_REGULAR_EXPRESSION_LIST_REF_LIST = "regular_expression_list";
    private static final String MARK_ENTRY = "entry";
    private static final String MARK_FIELD = "field";
    private static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";
    private static final String MARK_DIRECTORY_LIST = "directory_list";
    private static final String MARK_ENTRY_LIST_GEOLOCATION = "entry_list_geolocation";
    private static final String MARK_IS_ACTIVE_MYLUTECE_AUTHENTIFICATION = "is_active_mylutece_authentification";
    private static final String MARK_MYLUTECE_USER_INFOS_LIST = "mylutece_user_infos_list";
    private static final String MARK_MYLUTECE_USER_LOGIN = "mylutece_user_login";

    //private static final String MARK_DIRECTORY_RECORD_LIST = "directory_record_list";
    private static final String MARK_DIRECTORY = "directory";
    private static final String MARK_DIRECTORY_RECORD = "directory_record";
    private static final String MARK_PERMISSION_CREATE_DIRECTORY = "permission_create_directory";
    private static final String MARK_ENTRY_LIST = "entry_list";
    private static final String MARK_ENTRY_LIST_FORM_MAIN_SEARCH = "entry_list_form_main_search";
    private static final String MARK_ENTRY_LIST_FORM_COMPLEMENTARY_SEARCH = "entry_list_form_complementary_search";
    private static final String MARK_ENTRY_LIST_SEARCH_RESULT = "entry_list_search_result";
    private static final String MARK_LIST = "list";
    private static final String MARK_FORM_SEARCH_TEMPLATE_LIST = "form_search_template_list";
    private static final String MARK_RESULT_LIST_TEMPLATE_LIST = "result_list_template_list";
    private static final String MARK_RESULT_RECORD_TEMPLATE_LIST = "result_record_template_list";
    private static final String MARK_XSL_EXPORT_LIST = "xsl_export_list";
    private static final String MARK_NUMBER_FIELD = "number_field";
    private static final String MARK_NUMBER_ITEMS = "number_items";
    private static final String MARK_NUMBER_RECORD = "number_record";
    private static final String MARK_ROLE_REF_LIST = "role_list";
    private static final String MARK_MAP_ID_ENTRY_LIST_RECORD_FIELD = "map_id_entry_list_record_field";
    private static final String MARK_ID_ENTRY_TYPE_DIRECTORY = "id_entry_type_directory";
    private static final String MARK_ID_ENTRY_TYPE_GEOLOCATION = "id_entry_type_geolocation";
    private static final String MARK_ID_ENTRY_TYPE_IMAGE = "id_entry_type_image";
    private static final String MARK_ID_ENTRY_TYPE_MYLUTECE_USER = "id_entry_type_mylutece_user";
    private static final String MARK_SHOW_DATE_CREATION_RECORD = "show_date_creation_record";
    private static final String MARK_SHOW_DATE_CREATION_RESULT = "show_date_creation_result";
    private static final String MARK_RECORD_DATE_CREATION = "date_creation";
    private static final String MARK_DATE_CREATION_SEARCH = "date_creation_search";
    private static final String MARK_DATE_CREATION_BEGIN_SEARCH = "date_creation_begin_search";
    private static final String MARK_DATE_CREATION_END_SEARCH = "date_creation_end_search";
    private static final String MARK_PERMISSION_MANAGE_ADVANCED_PARAMETERS = "permission_manage_advanced_parameters";
    private static final String MARK_LIST_PARAM_DEFAULT_VALUES = "list_param_default_values";

    //private static final String MARK_URL_ACTION = "url_action";
    private static final String MARK_STR_ERROR = "str_error";
    private static final String MARK_NUMBER_LINES_IMPORTED = "number_lines_imported";
    private static final String MARK_NUMBER_LINES_ERROR = "number_lines_error";
    private static final String MARK_FINISH_IMPORT = "finish_import";
    private static final String MARK_WORKFLOW_REF_LIST = "workflow_list";
    private static final String MARK_WORKFLOW_SELECTED = "workflow_selected";
    private static final String MARK_WORKFLOW_ACTION_LIST = "workflow_action_list";
    private static final String MARK_WORKFLOW_STATE = "workflow_state";
    private static final String MARK_RECORD = "record";
    private static final String MARK_RESOURCE_ACTIONS_LIST = "resource_actions_list";
    private static final String MARK_TASKS_FORM = "tasks_form";
    private static final String MARK_ID_ACTION = "id_action";
    private static final String MARK_ID_RECORD = "id_record";
    private static final String MARK_RESOURCE_HISTORY = "resource_history";
    private static final String MARK_HISTORY_WORKFLOW_ENABLED = "history_workflow";
    private static final String MARK_PERMISSION_CREATE_RECORD = "permission_create_record";
    private static final String MARK_PERMISSION_MASS_PRINT = "permission_mass_print";
    private static final String MARK_PERMISSION_VISUALISATION_MYLUTECE_USER = "permission_visualisation_mylutece_user";
    private static final String MARK_IS_WORKFLOW_ENABLED = "is_workflow_enabled";
    private static final String MARK_IS_ASSOCIATION_ENTRY_WORKGROUP = "is_association_entry_workgroup";
    private static final String MARK_IS_ASSOCIATION_ENTRY_ROLE = "is_association_entry_role";
    private static final String MARK_IS_AUTHENTIFICATION_ENABLED = "is_authentification_enabled";
    private static final String MARK_WORKFLOW_STATE_SEARCH = "workflow_state_filter_search";
    private static final String MARK_WORKFLOW_STATE_SEARCH_SELECTED = "workflow_state_filter_search_selected";
    private static final String MARK_SEARCH_STATE_WORKFLOW = "search_state_workflow";
    private static final String MARK_WORKFLOW_STATE_SEARCH_DEFAULT = "search_state_workflow_default";
    private static final String MARK_STATE_LIST = "state_list";
    private static final String MARK_ENTRY_LIST_ASSOCIATE = "entry_list_associate";
    private static final String MARK_DIRECTORY_LIST_ASSOCIATE = "directory_list_associate";
    private static final String MARK_DIRECTORY_ENTRY_LIST_ASSOCIATE = "directory_entry_list_associate";
    private static final String MARK_DIRECTORY_ASSOCIATE = "id_directory_associate";
    private static final String MARK_THUMBNAIL_FIELD = "thumbnail_field";
    private static final String MARK_BIG_THUMBNAIL_FIELD = "big_thumbnail_field";
    private static final String MARK_IMAGE_FIELD = "image_field";
    private static final String MARK_HAS_THUMBNAIL = "has_thumbnail";
    private static final String MARK_HAS_BIG_THUMBNAIL = "has_big_thumbnail";

    // JSP URL
    private static final String JSP_DO_DISABLE_DIRECTORY = "jsp/admin/plugins/directory/DoDisableDirectory.jsp";
    private static final String JSP_DO_DISABLE_DIRECTORY_RECORD = "jsp/admin/plugins/directory/DoDisableDirectoryRecord.jsp";
    private static final String JSP_DO_REMOVE_DIRECTORY = "jsp/admin/plugins/directory/DoRemoveDirectory.jsp";
    private static final String JSP_DO_REMOVE_ALL_DIRECTORY_RECORD = "jsp/admin/plugins/directory/DoRemoveAllDirectoryRecord.jsp";
    private static final String JSP_DO_REMOVE_DIRECTORY_RECORD = "jsp/admin/plugins/directory/DoRemoveDirectoryRecord.jsp";
    private static final String JSP_DO_INDEX_ALL_DIRECTORY = "jsp/admin/plugins/directory/DoIndexAllDirectory.jsp";
    private static final String JSP_DO_REMOVE_FIELD = "jsp/admin/plugins/directory/DoRemoveField.jsp";
    private static final String JSP_DO_REMOVE_ENTRY = "jsp/admin/plugins/directory/DoRemoveEntry.jsp";
    private static final String JSP_MANAGE_DIRECTORY = "jsp/admin/plugins/directory/ManageDirectory.jsp";
    private static final String JSP_MANAGE_DIRECTORY_RECORD = "jsp/admin/plugins/directory/ManageDirectoryRecord.jsp";
    private static final String JSP_IMPORT_DIRECTORY_RECORD = "jsp/admin/plugins/directory/ImportDirectoryRecord.jsp";
    private static final String JSP_IMPORT_FIELD = "jsp/admin/plugins/directory/ImportField.jsp";
    private static final String JSP_MODIFY_DIRECTORY = "jsp/admin/plugins/directory/ModifyDirectory.jsp";
    private static final String JSP_MODIFY_ENTRY = "jsp/admin/plugins/directory/ModifyEntry.jsp";
    private static final String JSP_MODIFY_FIELD = "jsp/admin/plugins/directory/ModifyField.jsp";
    private static final String JSP_TASKS_FORM_WORKFLOW = "jsp/admin/plugins/directory/TasksFormWorkflow.jsp";
    private static final String JSP_DISPLAY_PRINT_HISTORY = "jsp/admin/plugins/directory/DisplayMassPrint.jsp";
    private static final String JSP_MANAGE_ADVANCED_PARAMETERS = "jsp/admin/plugins/directory/ManageAdvancedParameters.jsp";

    //Parameters
    private static final String PARAMETER_ID_DIRECTORY = "id_directory";
    private static final String PARAMETER_ID_DIRECTORY_RECORD = "id_directory_record";
    private static final String PARAMETER_TITLE = "title";
    private static final String PARAMETER_DESCRIPTION = "description";
    private static final String PARAMETER_UNAVAILABILITY_MESSAGE = "unavailability_message";
    private static final String PARAMETER_ACTIVE = "active";
    private static final String PARAMETER_WORKGROUP = "workgroup";
    private static final String PARAMETER_ROLE_KEY = "role_key";
    private static final String PARAMETER_ID_FORM_SEARCH_TEMPLATE = "id_form_search_template";
    private static final String PARAMETER_ID_RESULT_LIST_TEMPLATE = "id_result_list_template";
    private static final String PARAMETER_ID_RESULT_RECORD_TEMPLATE = "id_result_record_template";
    private static final String PARAMETER_NUMBER_RECORD_PER_PAGE = "number_record_per_page";
    private static final String PARAMETER_PAGE_INDEX = "page_index";
    private static final String PARAMETER_ID_ENTRY = "id_entry";
    private static final String PARAMETER_ID_FIELD = "id_field";
    private static final String PARAMETER_ID_EXPRESSION = "id_expression";
    private static final String PARAMETER_CANCEL = "cancel";
    private static final String PARAMETER_APPLY = "apply";
    private static final String PARAMETER_VALUE = "value";
    private static final String PARAMETER_SEARCH = "search";
    private static final String PARAMETER_DEFAULT_VALUE = "default_value";
    private static final String PARAMETER_SESSION = "session";
    private static final String PARAMETER_EXPORT_SEARCH_RESULT = "export_search_result";
    private static final String PARAMETER_DATE_SHOWN_IN_RESULT_LIST = "date_shown_in_result_list";
    private static final String PARAMETER_DATE_SHOWN_IN_RESULT_RECORD = "date_shown_in_result_record";
    private static final String PARAMETER_DATE_SHOWN_IN_HISTORY = "date_shown_in_history";
    private static final String PARAMETER_DATE_SHOWN_IN_SEARCH = "date_shown_in_search";
    private static final String PARAMETER_DATE_SHOWN_IN_ADVANCED_SEARCH = "date_shown_in_advanced_search";
    private static final String PARAMETER_DATE_SHOWN_IN_MULTI_SEARCH = "date_shown_in_multi_search";
    private static final String PARAMETER_DATE_SHOWN_IN_EXPORT = "date_shown_in_export";
    private static final String PARAMETER_DATE_BEGIN_CREATION = "date_begin_creation";
    private static final String PARAMETER_DATE_CREATION = "date_creation";
    private static final String PARAMETER_DATE_END_CREATION = "date_end_creation";
    private static final String PARAMETER_DATECREATION = "dateCreation";
    private static final String PARAMETER_ID_SORT_ENTRY = "id_sort_entry";
    private static final String PARAMETER_ASC_SORT = "asc_sort";
    private static final String PARAMETER_ACTIVATE_DIRECTORY_RECORD = "activate_directory_record";
    private static final String PARAMETER_IS_INDEXED = "is_indexed";

    //private static final String PARAMETER_EXPORT_ALL_RESULT = "export_all_result";
    private static final String PARAMETER_ID_DIRECTORY_XSL = "id_directory_xsl";

    //private static final String PARAMETER_NUMBER_LINES_IMPORTED = "number_lines_imported";
    //private static final String PARAMETER_NUMBER_LINES_ERROR = "number_lines_error";
    private static final String PARAMETER_FILE_IMPORT = "file_import";
    private static final String PARAMETER_WORKFLOW = "id_workflow_list";
    private static final String PARAMETER_WORKFLOW_STATE_SEARCH = "workflow_state_filter_search";
    private static final String PARAMETER_WORKFLOW_STATE_SELECTED = "search_state_workflow";
    private static final String PARAMETER_ID_ACTION = "id_action";
    private static final String PARAMETER_ID_RECORD = "id_record";
    private static final String PARAMETER_ID_STATE = "id_state";
    private static final String IS_DISPLAY_STATE_SEARCH = "1";
    private static final String IS_DISPLAY_STATE_SEARCH_COMPLEMENTARY = "2";
    private static final String IS_NOT_DISPLAY_STATE_SEARCH = "3";
    private static final String TAG_STATUS = "status";
    private static final String TAG_DISPLAY = "display";
    private static final String TAG_YES = "yes";
    private static final String TAG_NO = "no";
    private static final String ZERO = "0";

    // Misc
    private static final String CONSTANT_EXTENSION_CSV_FILE = ".csv";
    private static final String CONSTANT_MIME_TYPE_CSV = "application/csv";
    private static final String CONSTANT_MIME_TYPE_TEXT_CSV = "text/csv";
    private static final String CONSTANT_MIME_TYPE_OCTETSTREAM = "application/octet-stream";
    private static final String XSL_UNIQUE_PREFIX_ID = UniqueIDGenerator.getNewId(  ) + "directory-";
    private static final String MYLUTECE_PLUGIN = "mylutece";

    // Export
    private static final int EXPORT_STRINGBUFFER_MAX_CONTENT_SIZE = 500000;
    private static final int EXPORT_STRINGBUFFER_INITIAL_SIZE = 600000;
    private static final int EXPORT_RECORD_STEP = 100;
    private static final String EXPORT_TMPFILE_PREFIX = "exportDirectory";
    private static final String EXPORT_TMPFILE_SUFIX = ".part";
    private static final String EXPORT_XSL_BEGIN_PARTIAL_EXPORT = "<partialexport>";
    private static final String EXPORT_XSL_END_PARTIAL_EXPORT = "</partialexport>";
    private static final String EXPORT_XSL_BEGIN_LIST_RECORD = "<list-record>";
    private static final String EXPORT_XSL_END_LIST_RECORD = "</list-record>";
    private static final String EXPORT_XSL_EMPTY_LIST_RECORD = "<list-record/>";
    private static final String EXPORT_XSL_END_DIRECTORY = "</directory>";
    private static final String EXPORT_XSL_NEW_LINE = "\r\n";
    private static final String EXPORT_CSV_EXT = "csv";
    
    // Import
    private static final int IMPORT_FIELD_NB_COLUMN_MAX = 2; 

    //defaults
    private String DEFAULT_TYPE_IMAGE = "10";

    //session fields
    private int _nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( PROPERTY_ITEM_PER_PAGE, 50 );
    private String _strCurrentPageIndexDirectory;
    private int _nItemsPerPageDirectory;
    private String _strCurrentPageIndexEntry;
    private int _nItemsPerPageEntry;
    private String _strCurrentPageIndexPrintMass;

    //private String _nItemsPerPagePrintMass;
    private int _nItemsPerPagePrintMass;
    private String _strCurrentPageIndex;
    private String _strCurrentPageIndexDirectoryRecord;
    private int _nItemsPerPageDirectoryRecord;
    private int _nItemsPerPage;
    private int _nIdActive = -1;
    private String _strWorkGroup = AdminWorkgroupService.ALL_GROUPS;
    private int _nIdDirectory = -1;
    private int _nIdEntry = -1;
    private int _nCountLine;
    private int _nCountLineFailure;
    private int _nIdWorkflowSate = -1;
    private StringBuffer _strError;
    private HashMap<String, List<RecordField>> _mapQuery;
    private Date _dateCreationBeginRecord;
    private Date _dateCreationEndRecord;
    private Date _dateCreationRecord;

    /*-------------------------------MANAGEMENT  DIRECTORY-----------------------------*/

    /**
     * Return management directory ( list of directory )
     * @param request The Http request
     * @return Html directory
     */
    public String getManageDirectory( HttpServletRequest request )
    {
        List<DirectoryAction> listActionsForDirectoryEnable;
        List<DirectoryAction> listActionsForDirectoryDisable;
        List<DirectoryAction> listActions;

        String strWorkGroup = request.getParameter( PARAMETER_WORKGROUP );
        String strActive = request.getParameter( PARAMETER_ACTIVE );
        _strCurrentPageIndexDirectory = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX,
                _strCurrentPageIndexDirectory );
        _nItemsPerPageDirectory = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE,
                _nItemsPerPageDirectory, _nDefaultItemsPerPage );

        if ( ( strActive != null ) && !strActive.equals( DirectoryUtils.EMPTY_STRING ) )
        {
            _nIdActive = DirectoryUtils.convertStringToInt( strActive );
        }

        if ( ( strWorkGroup != null ) && !strWorkGroup.equals( DirectoryUtils.EMPTY_STRING ) )
        {
            _strWorkGroup = strWorkGroup;
        }

        //build Filter
        DirectoryFilter filter = new DirectoryFilter(  );
        filter.setIsDisabled( _nIdActive );
        filter.setWorkGroup( _strWorkGroup );

        List<Directory> listDirectory = DirectoryHome.getDirectoryList( filter, getPlugin(  ) );
        listDirectory = (List) AdminWorkgroupService.getAuthorizedCollection( listDirectory, getUser(  ) );

        Map<String, Object> model = new HashMap<String, Object>(  );
        LocalizedPaginator paginator = new LocalizedPaginator( listDirectory, _nItemsPerPageDirectory,
                getJspManageDirectory( request ), PARAMETER_PAGE_INDEX, _strCurrentPageIndexDirectory, getLocale(  ) );

        listActionsForDirectoryEnable = DirectoryActionHome.selectActionsByFormState( Directory.STATE_ENABLE,
                getPlugin(  ), getLocale(  ) );
        listActionsForDirectoryDisable = DirectoryActionHome.selectActionsByFormState( Directory.STATE_DISABLE,
                getPlugin(  ), getLocale(  ) );

        for ( Directory directory : (List<Directory>) paginator.getPageItems(  ) )
        {
            if ( directory.isEnabled(  ) )
            {
                listActions = listActionsForDirectoryEnable;
            }
            else
            {
                listActions = listActionsForDirectoryDisable;
            }

            listActions = (List<DirectoryAction>) RBACService.getAuthorizedActionsCollection( listActions, directory,
                    getUser(  ) );
            directory.setActions( listActions );
        }

        boolean bPermissionAdvancedParameter = RBACService.isAuthorized( Directory.RESOURCE_TYPE,
                RBAC.WILDCARD_RESOURCES_ID, DirectoryResourceIdService.PERMISSION_MANAGE_ADVANCED_PARAMETERS,
                getUser(  ) );

        model.put( MARK_PAGINATOR, paginator );
        model.put( MARK_NB_ITEMS_PER_PAGE, DirectoryUtils.EMPTY_STRING + _nItemsPerPageDirectory );
        model.put( MARK_USER_WORKGROUP_REF_LIST, AdminWorkgroupService.getUserWorkgroups( getUser(  ), getLocale(  ) ) );
        model.put( MARK_USER_WORKGROUP_SELECTED, _strWorkGroup );
        model.put( MARK_ACTIVE_REF_LIST, getRefListActive( getLocale(  ) ) );
        model.put( MARK_ACTIVE_SELECTED, _nIdActive );
        model.put( MARK_DIRECTORY_LIST, paginator.getPageItems(  ) );
        model.put( MARK_PERMISSION_CREATE_DIRECTORY,
            RBACService.isAuthorized( Directory.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID,
                DirectoryResourceIdService.PERMISSION_CREATE, getUser(  ) ) );
        model.put( MARK_PERMISSION_MANAGE_ADVANCED_PARAMETERS, bPermissionAdvancedParameter );

        setPageTitleProperty( PROPERTY_MANAGE_DIRECTORY_PAGE_TITLE );

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_MANAGE_DIRECTORY, getLocale(  ), model );

        return getAdminPage( templateList.getHtml(  ) );
    }

    /**
     * Get the request data and if there is no error insert the data in the directory specified in parameter.
     * return null if there is no error or else return the error page url
     * @param request the request
     * @param directory directory
     * @param locale the locale
     * @return null if there is no error or else return the error page url
     */
    private String getDirectoryData( HttpServletRequest request, Directory directory, Locale locale )
    {
        String strTitle = request.getParameter( PARAMETER_TITLE );
        String strDescription = request.getParameter( PARAMETER_DESCRIPTION );
        String strUnavailabilityMessage = request.getParameter( PARAMETER_UNAVAILABILITY_MESSAGE );
        String strWorkgroup = request.getParameter( PARAMETER_WORKGROUP );
        String strRoleKey = request.getParameter( PARAMETER_ROLE_KEY );

        String strIdFormSearchTemplate = request.getParameter( PARAMETER_ID_FORM_SEARCH_TEMPLATE );
        String strIdResultListTemplate = request.getParameter( PARAMETER_ID_RESULT_LIST_TEMPLATE );
        String strIdResultRecordTemplate = request.getParameter( PARAMETER_ID_RESULT_RECORD_TEMPLATE );
        String strNumberRecordPerPage = request.getParameter( PARAMETER_NUMBER_RECORD_PER_PAGE );
        String strWorkflow = request.getParameter( PARAMETER_WORKFLOW );
        String strDisplaySearchStateWorkflow = request.getParameter( PARAMETER_WORKFLOW_STATE_SEARCH );

        String strIdSortEntry = request.getParameter( PARAMETER_ID_SORT_ENTRY );
        String strAscSort = request.getParameter( PARAMETER_ASC_SORT );
        String strRecordActivated = request.getParameter( PARAMETER_ACTIVATE_DIRECTORY_RECORD );
        String strIsIndexed = request.getParameter( PARAMETER_IS_INDEXED );

        //creation date field
        String strShowDateInResultList = request.getParameter( PARAMETER_DATE_SHOWN_IN_RESULT_LIST );
        String strShowDateInResultRecord = request.getParameter( PARAMETER_DATE_SHOWN_IN_RESULT_RECORD );
        String strShowDateInHistory = request.getParameter( PARAMETER_DATE_SHOWN_IN_HISTORY );
        String strShowDateInSearch = request.getParameter( PARAMETER_DATE_SHOWN_IN_SEARCH );
        String strShowDateInAdvancedSearch = request.getParameter( PARAMETER_DATE_SHOWN_IN_ADVANCED_SEARCH );
        String strShowDateInMultiSearch = request.getParameter( PARAMETER_DATE_SHOWN_IN_MULTI_SEARCH );
        String strShowDateInExport = request.getParameter( PARAMETER_DATE_SHOWN_IN_EXPORT );

        int nIdResultListTemplate = DirectoryUtils.convertStringToInt( strIdResultListTemplate );
        int nIdResultRecordTemplate = DirectoryUtils.convertStringToInt( strIdResultRecordTemplate );
        int nIdFormSearchTemplate = DirectoryUtils.convertStringToInt( strIdFormSearchTemplate );
        int nNumberRecordPerPage = DirectoryUtils.convertStringToInt( strNumberRecordPerPage );
        int nIdWorkflow = DirectoryUtils.convertStringToInt( strWorkflow );

        String strFieldError = DirectoryUtils.EMPTY_STRING;

        if ( ( strTitle == null ) || strTitle.trim(  ).equals( DirectoryUtils.EMPTY_STRING ) )
        {
            strFieldError = FIELD_TITLE;
        }

        else if ( ( strDescription == null ) || strDescription.trim(  ).equals( DirectoryUtils.EMPTY_STRING ) )
        {
            strFieldError = FIELD_DESCRIPTION;
        }

        else if ( ( strUnavailabilityMessage == null ) ||
                strUnavailabilityMessage.trim(  ).equals( DirectoryUtils.EMPTY_STRING ) )
        {
            strFieldError = FIELD_UNAVAILABILITY_MESSAGE;
        }
        else if ( nIdFormSearchTemplate == DirectoryUtils.CONSTANT_ID_NULL )
        {
            strFieldError = FIELD_ID_FORM_SEARCH_TEMPLATE;
        }

        else if ( nIdResultListTemplate == DirectoryUtils.CONSTANT_ID_NULL )
        {
            strFieldError = FIELD_ID_RESULT_LIST_TEMPLATE;
        }
        else if ( nIdResultRecordTemplate == DirectoryUtils.CONSTANT_ID_NULL )
        {
            strFieldError = FIELD_ID_RESULT_RECORD_TEMPLATE;
        }
        else if ( ( strNumberRecordPerPage == null ) ||
                strNumberRecordPerPage.trim(  ).equals( DirectoryUtils.EMPTY_STRING ) )
        {
            strFieldError = FIELD_NUMBER_RECORD_PER_PAGE;
        }

        if ( !strFieldError.equals( DirectoryUtils.EMPTY_STRING ) )
        {
            Object[] tabRequiredFields = { I18nService.getLocalizedString( strFieldError, getLocale(  ) ) };

            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields,
                AdminMessage.TYPE_STOP );
        }

        if ( nNumberRecordPerPage == -1 )
        {
            strFieldError = FIELD_NUMBER_RECORD_PER_PAGE;
        }

        if ( !strFieldError.equals( DirectoryUtils.EMPTY_STRING ) )
        {
            Object[] tabRequiredFields = { I18nService.getLocalizedString( strFieldError, locale ) };

            return AdminMessageService.getMessageUrl( request, MESSAGE_NUMERIC_FIELD, tabRequiredFields,
                AdminMessage.TYPE_STOP );
        }

        RecordFieldFilter recordFilter = new RecordFieldFilter(  );
        recordFilter.setIdDirectory( directory.getIdDirectory(  ) );

        int nCountRecord = RecordHome.getCountRecord( recordFilter, getPlugin(  ) );

        if ( ( directory.getIdWorkflow(  ) != nIdWorkflow ) && ( nCountRecord != 0 ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_WORKFLOW_CHANGE, AdminMessage.TYPE_STOP );
        }

        directory.setTitle( strTitle );
        directory.setDescription( strDescription );
        directory.setUnavailabilityMessage( strUnavailabilityMessage );
        directory.setWorkgroup( strWorkgroup );
        directory.setRoleKey( strRoleKey );
        directory.setIdFormSearchTemplate( nIdFormSearchTemplate );
        directory.setIdResultListTemplate( nIdResultListTemplate );
        directory.setIdResultRecordTemplate( nIdResultRecordTemplate );
        directory.setNumberRecordPerPage( nNumberRecordPerPage );
        directory.setIdWorkflow( nIdWorkflow );

        if ( ( strDisplaySearchStateWorkflow != null ) &&
                strDisplaySearchStateWorkflow.equals( IS_DISPLAY_STATE_SEARCH ) )
        {
            directory.setDisplayComplementarySearchState( false );
            directory.setDisplaySearchState( true );
        }
        else if ( ( strDisplaySearchStateWorkflow != null ) &&
                strDisplaySearchStateWorkflow.equals( IS_DISPLAY_STATE_SEARCH_COMPLEMENTARY ) )
        {
            directory.setDisplayComplementarySearchState( true );
            directory.setDisplaySearchState( false );
        }
        else if ( ( strDisplaySearchStateWorkflow != null ) &&
                strDisplaySearchStateWorkflow.equals( IS_NOT_DISPLAY_STATE_SEARCH ) )
        {
            directory.setDisplayComplementarySearchState( false );
            directory.setDisplaySearchState( false );
        }

        directory.setDateShownInResultList( strShowDateInResultList != null );
        directory.setDateShownInResultRecord( strShowDateInResultRecord != null );
        directory.setDateShownInHistory( strShowDateInHistory != null );
        directory.setDateShownInSearch( strShowDateInSearch != null );
        directory.setDateShownInAdvancedSearch( strShowDateInAdvancedSearch != null );
        directory.setDateShownInMultiSearch( strShowDateInMultiSearch != null );
        directory.setDateShownInExport( strShowDateInExport != null );

        if ( ( strIdSortEntry != null ) && ( !strIdSortEntry.equals( DirectoryUtils.EMPTY_STRING ) ) )
        {
            directory.setIdSortEntry( strIdSortEntry );
        }
        else
        {
            directory.setIdSortEntry( null );
        }

        directory.setAscendingSort( strAscSort != null );
        directory.setRecordActivated( strRecordActivated != null );
        directory.setIndexed( strIsIndexed != null );

        return null; // No error
    }

    /**
     * Gets the directory creation page
     * @param request The HTTP request
     * @throws AccessDeniedException the {@link AccessDeniedException}
     * @return The directory creation page
     */
    public String getCreateDirectory( HttpServletRequest request )
        throws AccessDeniedException
    {
        AdminUser adminUser = getUser(  );
        Locale locale = getLocale(  );

        if ( !RBACService.isAuthorized( Directory.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID,
                    DirectoryResourceIdService.PERMISSION_CREATE, adminUser ) )
        {
            throw new AccessDeniedException(  );
        }

        DirectoryXslFilter filter = new DirectoryXslFilter(  );

        filter.setIdCategory( Category.ID_CATEGORY_STYLE_FORM_SEARCH );

        ReferenceList refListStyleFormSearch = DirectoryXslHome.getRefList( filter, getPlugin(  ) );

        filter.setIdCategory( Category.ID_CATEGORY_STYLE_RESULT_LIST );

        ReferenceList refListStyleResultList = DirectoryXslHome.getRefList( filter, getPlugin(  ) );

        filter.setIdCategory( Category.ID_CATEGORY_STYLE_RESULT_RECORD );

        ReferenceList refListStyleResultRecord = DirectoryXslHome.getRefList( filter, getPlugin(  ) );

        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_USER_WORKGROUP_REF_LIST, AdminWorkgroupService.getUserWorkgroups( adminUser, locale ) );

        if ( WorkflowService.getInstance(  ).isAvailable(  ) )
        {
            model.put( MARK_WORKFLOW_REF_LIST, WorkflowService.getInstance(  ).getWorkflowsEnabled( adminUser, locale ) );
            model.put( MARK_WORKFLOW_STATE_SEARCH_SELECTED, IS_NOT_DISPLAY_STATE_SEARCH );
        }

        if ( SecurityService.isAuthenticationEnable(  ) )
        {
            model.put( MARK_ROLE_REF_LIST, RoleHome.getRolesList(  ) );
        }

        // Default Values
        ReferenceList listParamDefaultValues = DirectoryParameterHome.findAll( getPlugin(  ) );

        model.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
        model.put( MARK_LOCALE, AdminUserService.getLocale( request ).getLanguage(  ) );
        model.put( MARK_FORM_SEARCH_TEMPLATE_LIST, refListStyleFormSearch );
        model.put( MARK_RESULT_LIST_TEMPLATE_LIST, refListStyleResultList );
        model.put( MARK_RESULT_RECORD_TEMPLATE_LIST, refListStyleResultRecord );
        model.put( MARK_LIST_PARAM_DEFAULT_VALUES, listParamDefaultValues );

        setPageTitleProperty( PROPERTY_CREATE_DIRECTORY_PAGE_TITLE );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CREATE_DIRECTORY, locale, model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Perform the directory creation
     * @param request The HTTP request
     * @throws AccessDeniedException the {@link AccessDeniedException}
     * @return The URL to go after performing the action
     */
    public String doCreateDirectory( HttpServletRequest request )
        throws AccessDeniedException
    {
        if ( !RBACService.isAuthorized( Directory.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID,
                    DirectoryResourceIdService.PERMISSION_CREATE, getUser(  ) ) )
        {
            throw new AccessDeniedException(  );
        }

        if ( ( request.getParameter( PARAMETER_CANCEL ) == null ) )
        {
            Plugin plugin = getPlugin(  );
            Directory directory = new Directory(  );
            String strError = getDirectoryData( request, directory, getLocale(  ) );

            if ( strError != null )
            {
                return strError;
            }

            directory.setDateCreation( DirectoryUtils.getCurrentTimestamp(  ) );
            DirectoryHome.create( directory, plugin );
        }

        return getJspManageDirectory( request );
    }

    /**
     * Gets the directory modification page
     * @param request The HTTP request
     * @throws AccessDeniedException the {@link AccessDeniedException}
     * @return The directory modification page
     */
    public String getModifyDirectory( HttpServletRequest request )
        throws AccessDeniedException
    {
        List<IEntry> listEntry = new ArrayList<IEntry>(  );
        List<IEntry> listEntryFirstLevel;
        int nNumberField;
        EntryFilter filter;
        String strIdDirectory = request.getParameter( PARAMETER_ID_DIRECTORY );
        int nIdDirectory = DirectoryUtils.convertStringToInt( strIdDirectory );
        Directory directory = null;

        if ( nIdDirectory != -1 )
        {
            directory = DirectoryHome.findByPrimaryKey( nIdDirectory, getPlugin(  ) );
            _nIdDirectory = nIdDirectory;
        }

        if ( ( directory == null ) ||
                !RBACService.isAuthorized( Directory.RESOURCE_TYPE, strIdDirectory,
                    DirectoryResourceIdService.PERMISSION_MODIFY, getUser(  ) ) )
        {
            throw new AccessDeniedException(  );
        }

        filter = new EntryFilter(  );

        filter.setIdDirectory( nIdDirectory );
        filter.setIsEntryParentNull( EntryFilter.FILTER_TRUE );
        listEntryFirstLevel = EntryHome.getEntryList( filter, getPlugin(  ) );

        filter.setIsEntryParentNull( EntryFilter.ALL_INT );
        filter.setIsComment( EntryFilter.FILTER_FALSE );
        filter.setIsGroup( EntryFilter.FILTER_FALSE );

        nNumberField = EntryHome.getNumberEntryByFilter( filter, getPlugin(  ) );

        if ( listEntryFirstLevel.size(  ) != 0 )
        {
            listEntryFirstLevel.get( 0 ).setFirstInTheList( true );
            listEntryFirstLevel.get( listEntryFirstLevel.size(  ) - 1 ).setLastInTheList( true );
        }

        for ( IEntry entry : listEntryFirstLevel )
        {
            listEntry.add( entry );

            if ( entry.getEntryType(  ).getGroup(  ) )
            {
                filter = new EntryFilter(  );
                filter.setIdEntryParent( entry.getIdEntry(  ) );
                entry.setChildren( EntryHome.getEntryList( filter, getPlugin(  ) ) );

                if ( !entry.getChildren(  ).isEmpty(  ) )
                {
                    entry.getChildren(  ).get( 0 ).setFirstInTheList( true );
                    entry.getChildren(  ).get( entry.getChildren(  ).size(  ) - 1 ).setLastInTheList( true );
                }

                for ( IEntry entryChild : entry.getChildren(  ) )
                {
                    listEntry.add( entryChild );
                }
            }
        }

        _strCurrentPageIndexEntry = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX,
                _strCurrentPageIndexEntry );
        _nItemsPerPageEntry = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE,
                _nItemsPerPageEntry, _nDefaultItemsPerPage );

        LocalizedPaginator paginator = new LocalizedPaginator( listEntry, _nItemsPerPageEntry,
                AppPathService.getBaseUrl( request ) + JSP_MODIFY_DIRECTORY + "?id_directory=" + nIdDirectory,
                PARAMETER_PAGE_INDEX, _strCurrentPageIndexEntry, getLocale(  ) );

        AdminUser adminUser = getUser(  );

        Locale locale = getLocale(  );
        ReferenceList refListWorkGroups;
        ReferenceList refMailingList;

        refListWorkGroups = AdminWorkgroupService.getUserWorkgroups( adminUser, locale );

        refMailingList = new ReferenceList(  );

        String strNothing = I18nService.getLocalizedString( PROPERTY_NOTHING, locale );
        refMailingList.addItem( -1, strNothing );
        refMailingList.addAll( AdminMailingListService.getMailingLists( adminUser ) );

        DirectoryXslFilter directoryXslFilter = new DirectoryXslFilter(  );

        directoryXslFilter.setIdCategory( Category.ID_CATEGORY_STYLE_FORM_SEARCH );

        ReferenceList refListStyleFormSearch = DirectoryXslHome.getRefList( directoryXslFilter, getPlugin(  ) );

        directoryXslFilter.setIdCategory( Category.ID_CATEGORY_STYLE_RESULT_LIST );

        ReferenceList refListStyleResultList = DirectoryXslHome.getRefList( directoryXslFilter, getPlugin(  ) );

        directoryXslFilter.setIdCategory( Category.ID_CATEGORY_STYLE_RESULT_RECORD );

        ReferenceList refListStyleResultRecord = DirectoryXslHome.getRefList( directoryXslFilter, getPlugin(  ) );

        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_PAGINATOR, paginator );
        model.put( MARK_NB_ITEMS_PER_PAGE, DirectoryUtils.EMPTY_STRING + _nItemsPerPageEntry );
        model.put( MARK_USER_WORKGROUP_REF_LIST, refListWorkGroups );

        if ( SecurityService.isAuthenticationEnable(  ) )
        {
            model.put( MARK_ROLE_REF_LIST, RoleHome.getRolesList(  ) );
        }

        if ( WorkflowService.getInstance(  ).isAvailable(  ) )
        {
            ReferenceList referenceList = WorkflowService.getInstance(  ).getWorkflowsEnabled( adminUser, locale );
            model.put( MARK_WORKFLOW_REF_LIST, referenceList );
            model.put( MARK_WORKFLOW_SELECTED, directory.getIdWorkflow(  ) );

            if ( !referenceList.isEmpty(  ) )
            {
                model.put( MARK_WORKFLOW_STATE_SEARCH, true );

                /*ReferenceList referenceList=new ReferenceList();
                    referenceList.addItem(1, strName)*/
                if ( directory.isDisplaySearchState(  ) )
                {
                    model.put( MARK_WORKFLOW_STATE_SEARCH_SELECTED, IS_DISPLAY_STATE_SEARCH );
                }
                else if ( directory.isDisplayComplementarySearchState(  ) )
                {
                    model.put( MARK_WORKFLOW_STATE_SEARCH_SELECTED, IS_DISPLAY_STATE_SEARCH_COMPLEMENTARY );
                }
                else
                {
                    model.put( MARK_WORKFLOW_STATE_SEARCH_SELECTED, IS_NOT_DISPLAY_STATE_SEARCH );
                }
            }
        }

        model.put( MARK_FORM_SEARCH_TEMPLATE_LIST, refListStyleFormSearch );
        model.put( MARK_RESULT_LIST_TEMPLATE_LIST, refListStyleResultList );
        model.put( MARK_RESULT_RECORD_TEMPLATE_LIST, refListStyleResultRecord );
        model.put( MARK_ENTRY_TYPE_LIST, EntryTypeHome.getList( getPlugin(  ) ) );
        model.put( MARK_DIRECTORY, directory );
        model.put( MARK_ENTRY_LIST, paginator.getPageItems(  ) );
        model.put( MARK_NUMBER_FIELD, nNumberField );
        model.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
        model.put( MARK_LOCALE, AdminUserService.getLocale( request ).getLanguage(  ) );
        model.put( MARK_IS_ACTIVE_MYLUTECE_AUTHENTIFICATION, PluginService.isPluginEnable( MYLUTECE_PLUGIN ) );
        setPageTitleProperty( PROPERTY_MODIFY_DIRECTORY_TITLE );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MODIFY_DIRECTORY, locale, model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Perform the directory modification
     * @param request The HTTP request
     * @throws AccessDeniedException the {@link AccessDeniedException}
     * @return The URL to go after performing the action
     */
    public String doModifyDirectory( HttpServletRequest request )
        throws AccessDeniedException
    {
        if ( request.getParameter( PARAMETER_CANCEL ) == null )
        {
            String strIdDirectory = request.getParameter( PARAMETER_ID_DIRECTORY );
            int nIdDirectory = DirectoryUtils.convertStringToInt( strIdDirectory );
            Directory directory;

            if ( nIdDirectory != -1 )
            {
                directory = DirectoryHome.findByPrimaryKey( nIdDirectory, getPlugin(  ) );

                if ( ( directory == null ) ||
                        !RBACService.isAuthorized( Directory.RESOURCE_TYPE, strIdDirectory,
                            DirectoryResourceIdService.PERMISSION_MODIFY, getUser(  ) ) )
                {
                    throw new AccessDeniedException(  );
                }

                String strError = getDirectoryData( request, directory, getLocale(  ) );

                if ( strError != null )
                {
                    return strError;
                }

                directory.setIdDirectory( nIdDirectory );
                DirectoryHome.update( directory, getPlugin(  ) );

                if ( request.getParameter( PARAMETER_APPLY ) != null )
                {
                    return getJspModifyDirectory( request, nIdDirectory );
                }
            }
        }

        return getJspManageDirectory( request );
    }

    /**
     * Gets the confirmation page of delete directory
     * @param request The HTTP request
     * @throws AccessDeniedException the {@link AccessDeniedException}
     * @return the confirmation page of delete directory
     */
    public String getConfirmRemoveDirectory( HttpServletRequest request )
        throws AccessDeniedException
    {
        String strIdDirectory = request.getParameter( PARAMETER_ID_DIRECTORY );
        String strMessage;
        int nIdDirectory = DirectoryUtils.convertStringToInt( strIdDirectory );

        if ( ( strIdDirectory == null ) ||
                !RBACService.isAuthorized( Directory.RESOURCE_TYPE, strIdDirectory,
                    DirectoryResourceIdService.PERMISSION_DELETE, getUser(  ) ) )
        {
            throw new AccessDeniedException(  );
        }

        RecordFieldFilter recordFieldFilter = new RecordFieldFilter(  );
        recordFieldFilter.setIdDirectory( nIdDirectory );

        int nNumberRecord = RecordHome.getCountRecord( recordFieldFilter, getPlugin(  ) );
        strMessage = ( nNumberRecord == 0 ) ? MESSAGE_CONFIRM_REMOVE_DIRECTORY
                                            : MESSAGE_CONFIRM_REMOVE_DIRECTORY_WITH_RECORD;

        UrlItem url = new UrlItem( JSP_DO_REMOVE_DIRECTORY );
        url.addParameter( PARAMETER_ID_DIRECTORY, strIdDirectory );

        return AdminMessageService.getMessageUrl( request, strMessage, url.getUrl(  ), AdminMessage.TYPE_CONFIRMATION );
    }

    /**
     * Perform the directory suppression
     * @param request The HTTP request
     * @throws AccessDeniedException the {@link AccessDeniedException}
     * @return The URL to go after performing the action
     */
    public String doRemoveDirectory( HttpServletRequest request )
        throws AccessDeniedException
    {
        String strIdDirectory = request.getParameter( PARAMETER_ID_DIRECTORY );
        ArrayList<String> listErrors = new ArrayList<String>(  );

        int nIdDirectory = DirectoryUtils.convertStringToInt( strIdDirectory );

        if ( !RBACService.isAuthorized( Directory.RESOURCE_TYPE, strIdDirectory,
                    DirectoryResourceIdService.PERMISSION_DELETE, getUser(  ) ) )
        {
            throw new AccessDeniedException(  );
        }

        if ( !DirectoryRemovalListenerService.getService(  ).checkForRemoval( strIdDirectory, listErrors, getLocale(  ) ) )
        {
            String strCause = AdminMessageService.getFormattedList( listErrors, getLocale(  ) );
            Object[] args = { strCause };

            return AdminMessageService.getMessageUrl( request, MESSAGE_CANNOT_REMOVE_DIRECTORY, args,
                AdminMessage.TYPE_STOP );
        }

        DirectoryHome.remove( nIdDirectory, getPlugin(  ) );

        return getJspManageDirectory( request );
    }

    /**
     * Gets the confirmation page of remove all Directory Record
     * @param request The HTTP request
     * @throws AccessDeniedException the {@link AccessDeniedException}
     * @return the confirmation page of delete all Directory Record
     */
    public String getConfirmRemoveAllDirectoryRecord( HttpServletRequest request )
        throws AccessDeniedException
    {
        String strIdDirectory = request.getParameter( PARAMETER_ID_DIRECTORY );

        if ( !RBACService.isAuthorized( Directory.RESOURCE_TYPE, strIdDirectory,
                    DirectoryResourceIdService.PERMISSION_DELETE_ALL_RECORD, getUser(  ) ) )
        {
            throw new AccessDeniedException(  );
        }

        UrlItem url = new UrlItem( JSP_DO_REMOVE_ALL_DIRECTORY_RECORD );
        url.addParameter( PARAMETER_ID_DIRECTORY, strIdDirectory );

        return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_ALL_DIRECTORY_RECORD, url.getUrl(  ),
            AdminMessage.TYPE_CONFIRMATION );
    }

    /**
     * Remove all directory record of the directory
     * @param request The HTTP request
     * @throws AccessDeniedException the {@link AccessDeniedException}
     * @return The URL to go after performing the action
     */
    public String doRemoveAllDirectoryRecord( HttpServletRequest request )
        throws AccessDeniedException
    {
        String strIdDirectory = request.getParameter( PARAMETER_ID_DIRECTORY );
        Plugin plugin = getPlugin(  );
        int nIdDirectory = DirectoryUtils.convertStringToInt( strIdDirectory );

        if ( !RBACService.isAuthorized( Directory.RESOURCE_TYPE, strIdDirectory,
                    DirectoryResourceIdService.PERMISSION_DELETE_ALL_RECORD, getUser(  ) ) )
        {
            throw new AccessDeniedException(  );
        }

        /*
        RecordFieldFilter recordFilter = new RecordFieldFilter(  );
        recordFilter.setIdDirectory( nIdDirectory );
        
        
        for ( Integer nRecordId : RecordHome.getListRecordId( recordFilter, plugin ) )
        {
            RecordHome.remove( nRecordId, plugin );
        }
        */
        RecordHome.removeByIdDirectory( nIdDirectory, plugin );

        return getJspManageDirectory( request );
    }

    /**
     * copy the directory whose key is specified in the Http request
     * @param request The HTTP request
     * @throws AccessDeniedException the {@link AccessDeniedException}
     * @return The URL to go after performing the action
     */
    public String doCopyDirectory( HttpServletRequest request )
        throws AccessDeniedException
    {
        Plugin plugin = getPlugin(  );
        Directory directory;
        String strIdDirectory = request.getParameter( PARAMETER_ID_DIRECTORY );
        int nIdDirectory = DirectoryUtils.convertStringToInt( strIdDirectory );
        directory = DirectoryHome.findByPrimaryKey( nIdDirectory, plugin );

        if ( ( directory == null ) ||
                !RBACService.isAuthorized( Directory.RESOURCE_TYPE, strIdDirectory,
                    DirectoryResourceIdService.PERMISSION_COPY, getUser(  ) ) )
        {
            throw new AccessDeniedException(  );
        }

        Object[] tabFormTitleCopy = { directory.getTitle(  ) };
        String strTitleCopyForm = I18nService.getLocalizedString( PROPERTY_COPY_DIRECTORY_TITLE, tabFormTitleCopy,
                getLocale(  ) );

        if ( strTitleCopyForm != null )
        {
            directory.setTitle( strTitleCopyForm );
        }

        DirectoryHome.copy( directory, plugin );

        return getJspManageDirectory( request );
    }

    /**
    * Gets the entry creation page
    * @param request The HTTP request
    * @throws AccessDeniedException the {@link AccessDeniedException}
    * @return The  entry creation page
    */
    public String getCreateEntry( HttpServletRequest request )
        throws AccessDeniedException
    {
        Directory directory;
        Plugin plugin = getPlugin(  );
        IEntry entry;
        entry = DirectoryUtils.createEntryByType( request, plugin );

        boolean bAssociationEntryWorkgroup;
        boolean bAssociationEntryRole;

        if ( ( entry == null ) ||
                !RBACService.isAuthorized( Directory.RESOURCE_TYPE, DirectoryUtils.EMPTY_STRING + _nIdDirectory,
                    DirectoryResourceIdService.PERMISSION_MODIFY, getUser(  ) ) )
        {
            throw new AccessDeniedException(  );
        }

        directory = DirectoryHome.findByPrimaryKey( _nIdDirectory, plugin );
        entry.setDirectory( directory );

        //test if an entry is already asoociated with a role or a workgroup
        EntryFilter filter = new EntryFilter(  );
        filter.setIdDirectory( _nIdDirectory );
        filter.setIsRoleAssociated( EntryFilter.FILTER_TRUE );
        bAssociationEntryRole = ( EntryHome.getNumberEntryByFilter( filter, plugin ) != 0 );
        filter.setIsRoleAssociated( EntryFilter.ALL_INT );
        filter.setIsWorkgroupAssociated( EntryFilter.FILTER_TRUE );
        bAssociationEntryWorkgroup = ( EntryHome.getNumberEntryByFilter( filter, plugin ) != 0 );

        Map<String, Object> model = new HashMap<String, Object>(  );

        //For Entry Type Directory
        String strAutorizeEntryType = AppPropertiesService.getProperty( PROPERTY_ENTRY_AUTORIZE_FOR_ENTRY_DIRECTORY );
        String[] strTabAutorizeEntryType = strAutorizeEntryType.split( "," );

        ReferenceList listEntryAssociateWithoutJavascript = new ReferenceList(  );
        List<List> listEntryWithJavascript = new ArrayList<List>(  );

        for ( ReferenceItem item : DirectoryHome.getDirectoryList( plugin ) )
        {
            List<IEntry> listEntry = new ArrayList<IEntry>(  );
            Directory directoryTmp = DirectoryHome.findByPrimaryKey( DirectoryUtils.convertStringToInt( 
                        item.getCode(  ) ), plugin );
            EntryFilter entryFilter = new EntryFilter(  );
            entryFilter.setIdDirectory( directoryTmp.getIdDirectory(  ) );

            for ( IEntry entryTmp : EntryHome.getEntryList( entryFilter, plugin ) )
            {
                boolean bEntryAutorize = false;

                for ( int i = 0; ( i < strTabAutorizeEntryType.length ) && !bEntryAutorize; i++ )
                {
                    if ( entryTmp.getEntryType(  ).getIdType(  ) == DirectoryUtils.convertStringToInt( 
                                strTabAutorizeEntryType[i] ) )
                    {
                        bEntryAutorize = true;
                    }
                }

                if ( bEntryAutorize )
                {
                    listEntryAssociateWithoutJavascript.addItem( entryTmp.getIdEntry(  ),
                        directoryTmp.getTitle(  ) + " - " + entryTmp.getTitle(  ) );
                    listEntry.add( entryTmp );
                }
            }

            listEntryWithJavascript.add( listEntry );
        }

        // Default Values
        ReferenceList listParamDefaultValues = EntryParameterHome.findAll( getPlugin(  ) );

        model.put( MARK_DIRECTORY_ENTRY_LIST_ASSOCIATE, listEntryAssociateWithoutJavascript );
        model.put( MARK_DIRECTORY_LIST_ASSOCIATE, DirectoryHome.getDirectoryList( plugin ) );
        model.put( MARK_ENTRY_LIST_ASSOCIATE, listEntryWithJavascript );

        model.put( MARK_ENTRY, entry );
        model.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
        model.put( MARK_LOCALE, AdminUserService.getLocale( request ).getLanguage(  ) );
        model.put( MARK_IS_ASSOCIATION_ENTRY_WORKGROUP, bAssociationEntryWorkgroup );
        model.put( MARK_IS_ASSOCIATION_ENTRY_ROLE, bAssociationEntryRole );
        model.put( MARK_IS_AUTHENTIFICATION_ENABLED, SecurityService.isAuthenticationEnable(  ) );
        model.put( MARK_LIST_PARAM_DEFAULT_VALUES, listParamDefaultValues );

        if ( entry.getEntryType(  ).getComment(  ) )
        {
            setPageTitleProperty( PROPERTY_CREATE_ENTRY_COMMENT_PAGE_TITLE );
        }
        else
        {
            setPageTitleProperty( PROPERTY_CREATE_ENTRY_FIELD_PAGE_TITLE );
        }

        HtmlTemplate template = AppTemplateService.getTemplate( entry.getTemplateCreate(  ), getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Perform the entry creation
     * @param request The HTTP request
     * @throws AccessDeniedException the {@link AccessDeniedException}
     * @return The URL to go after performing the action
     */
    public String doCreateEntry( HttpServletRequest request )
        throws AccessDeniedException
    {
        IEntry entry;
        Directory directory;

        if ( !RBACService.isAuthorized( Directory.RESOURCE_TYPE, DirectoryUtils.EMPTY_STRING + _nIdDirectory,
                    DirectoryResourceIdService.PERMISSION_MODIFY, getUser(  ) ) )
        {
            throw new AccessDeniedException(  );
        }

        if ( ( request.getParameter( PARAMETER_CANCEL ) == null ) )
        {
            entry = DirectoryUtils.createEntryByType( request, getPlugin(  ) );

            if ( entry == null )
            {
                return getJspManageDirectory( request );
            }

            String strError = entry.getEntryData( request, getLocale(  ) );

            if ( strError != null )
            {
                return strError;
            }

            directory = new Directory(  );
            directory.setIdDirectory( _nIdDirectory );
            entry.setDirectory( directory );
            entry.setIdEntry( EntryHome.create( entry, getPlugin(  ) ) );

            if ( entry.getFields(  ) != null )
            {
                for ( Field field : entry.getFields(  ) )
                {
                    field.setEntry( entry );
                    FieldHome.create( field, getPlugin(  ) );
                }
            }

            if ( request.getParameter( PARAMETER_APPLY ) != null )
            {
                return getJspModifyEntry( request, entry.getIdEntry(  ) );
            }
        }

        return getJspModifyDirectory( request, _nIdDirectory );
    }

    /**
     * Gets the entry modification page
     * @param request The HTTP request
     * @throws AccessDeniedException the {@link AccessDeniedException}
     * @return The  entry modification page
     */
    public String getModifyEntry( HttpServletRequest request )
        throws AccessDeniedException
    {
        boolean bAssociationEntryWorkgroup;
        boolean bAssociationEntryRole;
        Plugin plugin = getPlugin(  );
        IEntry entry;
        ReferenceList refListRegularExpression;
        String strIdEntry = request.getParameter( PARAMETER_ID_ENTRY );
        int nIdEntry = DirectoryUtils.convertStringToInt( strIdEntry );
        entry = EntryHome.findByPrimaryKey( nIdEntry, plugin );

        if ( ( entry == null ) ||
                !RBACService.isAuthorized( Directory.RESOURCE_TYPE, DirectoryUtils.EMPTY_STRING + _nIdDirectory,
                    DirectoryResourceIdService.PERMISSION_MODIFY, getUser(  ) ) )
        {
            throw new AccessDeniedException(  );
        }

        _nIdEntry = nIdEntry;

        List<Field> listField = new ArrayList<Field>(  );

        for ( Field field : entry.getFields(  ) )
        {
            field = FieldHome.findByPrimaryKey( field.getIdField(  ), plugin );
            listField.add( field );
        }

        entry.setFields( listField );

        HashMap<String, Object> model = new HashMap<String, Object>(  );

        int nIdTypeImage = DirectoryUtils.convertStringToInt( AppPropertiesService.getProperty( 
                    PROPERTY_ENTRY_TYPE_IMAGE, DEFAULT_TYPE_IMAGE ) );

        if ( entry.getEntryType(  ).getIdType(  ) == nIdTypeImage )
        {
            for ( Field field : entry.getFields(  ) )
            {
                if ( ( field.getValue(  ) != null ) && ( field.getValue(  ).equals( FIELD_THUMBNAIL ) ) )
                {
                    model.put( MARK_THUMBNAIL_FIELD, field );
                    model.put( MARK_HAS_THUMBNAIL, true );
                }

                else if ( ( field.getValue(  ) != null ) && ( field.getValue(  ).equals( FIELD_BIG_THUMBNAIL ) ) )
                {
                    model.put( MARK_BIG_THUMBNAIL_FIELD, field );
                    model.put( MARK_HAS_BIG_THUMBNAIL, true );
                }

                else if ( ( field.getValue(  ) != null ) && ( field.getValue(  ).equals( FIELD_IMAGE ) ) )
                {
                    model.put( MARK_IMAGE_FIELD, field );
                }
            }
        }

        model.put( MARK_ENTRY, entry );
        _strCurrentPageIndex = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex );
        _nItemsPerPage = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPage,
                _nDefaultItemsPerPage );

        LocalizedPaginator paginator = entry.getPaginator( _nItemsPerPage,
                AppPathService.getBaseUrl( request ) + JSP_MODIFY_ENTRY + "?id_entry=" + nIdEntry,
                PARAMETER_PAGE_INDEX, _strCurrentPageIndex, getLocale(  ) );

        if ( paginator != null )
        {
            model.put( MARK_NB_ITEMS_PER_PAGE, DirectoryUtils.EMPTY_STRING + _nItemsPerPage );
            model.put( MARK_NUMBER_ITEMS, paginator.getItemsCount(  ) );
            model.put( MARK_LIST, paginator.getPageItems(  ) );
            model.put( MARK_PAGINATOR, paginator );
        }

        refListRegularExpression = entry.getReferenceListRegularExpression( entry, plugin );

        if ( refListRegularExpression != null )
        {
            model.put( MARK_REGULAR_EXPRESSION_LIST_REF_LIST, refListRegularExpression );
        }

        model.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
        model.put( MARK_LOCALE, AdminUserService.getLocale( request ).getLanguage(  ) );

        if ( entry.getEntryType(  ).getComment(  ) )
        {
            setPageTitleProperty( PROPERTY_MODIFY_ENTRY_COMMENT_PAGE_TITLE );
        }
        else if ( entry.getEntryType(  ).getGroup(  ) )
        {
            setPageTitleProperty( PROPERTY_MODIFY_ENTRY_GROUP_PAGE_TITLE );
        }
        else
        {
            setPageTitleProperty( PROPERTY_MODIFY_ENTRY_FIELD_PAGE_TITLE );
        }

        //test if an entry is already asoociated with a role or a workgroup
        EntryFilter filter = new EntryFilter(  );
        filter.setIdDirectory( _nIdDirectory );
        filter.setIsRoleAssociated( EntryFilter.FILTER_TRUE );
        bAssociationEntryRole = ( EntryHome.getNumberEntryByFilter( filter, plugin ) != 0 );
        filter.setIsRoleAssociated( EntryFilter.ALL_INT );
        filter.setIsWorkgroupAssociated( EntryFilter.FILTER_TRUE );
        bAssociationEntryWorkgroup = ( EntryHome.getNumberEntryByFilter( filter, plugin ) != 0 );

        //For Entry Type Directory
        String strAutorizeEntryType = AppPropertiesService.getProperty( PROPERTY_ENTRY_AUTORIZE_FOR_ENTRY_DIRECTORY );
        String[] strTabAutorizeEntryType = strAutorizeEntryType.split( "," );

        ReferenceList listEntryAssociateWithoutJavascript = new ReferenceList(  );
        List<List> listEntryWithJavascript = new ArrayList<List>(  );

        for ( ReferenceItem item : DirectoryHome.getDirectoryList( plugin ) )
        {
            List<IEntry> listEntry = new ArrayList<IEntry>(  );
            Directory directoryTmp = DirectoryHome.findByPrimaryKey( DirectoryUtils.convertStringToInt( 
                        item.getCode(  ) ), plugin );
            EntryFilter entryFilter = new EntryFilter(  );
            entryFilter.setIdDirectory( directoryTmp.getIdDirectory(  ) );

            for ( IEntry entryTmp : EntryHome.getEntryList( entryFilter, plugin ) )
            {
                boolean bEntryAutorize = false;

                for ( int i = 0; ( i < strTabAutorizeEntryType.length ) && !bEntryAutorize; i++ )
                {
                    if ( entryTmp.getEntryType(  ).getIdType(  ) == DirectoryUtils.convertStringToInt( 
                                strTabAutorizeEntryType[i] ) )
                    {
                        bEntryAutorize = true;
                    }
                }

                if ( bEntryAutorize )
                {
                    listEntryAssociateWithoutJavascript.addItem( entryTmp.getIdEntry(  ),
                        directoryTmp.getTitle(  ) + " - " + entryTmp.getTitle(  ) );
                    listEntry.add( entryTmp );
                }
            }

            listEntryWithJavascript.add( listEntry );
        }

        model.put( MARK_DIRECTORY_ENTRY_LIST_ASSOCIATE, listEntryAssociateWithoutJavascript );
        model.put( MARK_DIRECTORY_LIST_ASSOCIATE, DirectoryHome.getDirectoryList( plugin ) );

        if ( ( entry.getEntryAssociate(  ) != DirectoryUtils.CONSTANT_ID_NULL ) &&
                ( EntryHome.findByPrimaryKey( entry.getEntryAssociate(  ), plugin ) != null ) )
        {
            model.put( MARK_DIRECTORY_ASSOCIATE,
                EntryHome.findByPrimaryKey( entry.getEntryAssociate(  ), plugin ).getDirectory(  ).getIdDirectory(  ) );
        }

        model.put( MARK_ENTRY_LIST_ASSOCIATE, listEntryWithJavascript );
        model.put( MARK_IS_ASSOCIATION_ENTRY_WORKGROUP, bAssociationEntryWorkgroup );
        model.put( MARK_IS_ASSOCIATION_ENTRY_ROLE, bAssociationEntryRole );
        model.put( MARK_IS_AUTHENTIFICATION_ENABLED, SecurityService.isAuthenticationEnable(  ) );
        model.put( MARK_ID_DIRECTORY, _nIdDirectory );

        HtmlTemplate template = AppTemplateService.getTemplate( entry.getTemplateModify(  ), getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Perform the entry modification
     * @param request The HTTP request
     * @throws AccessDeniedException the {@link AccessDeniedException}
     * @return The URL to go after performing the action
     */
    public String doModifyEntry( HttpServletRequest request )
        throws AccessDeniedException
    {
        Plugin plugin = getPlugin(  );
        IEntry entry;
        String strIdEntry = request.getParameter( PARAMETER_ID_ENTRY );
        int nIdEntry = DirectoryUtils.convertStringToInt( strIdEntry );

        if ( ( nIdEntry == -1 ) ||
                !RBACService.isAuthorized( Directory.RESOURCE_TYPE, DirectoryUtils.EMPTY_STRING + _nIdDirectory,
                    DirectoryResourceIdService.PERMISSION_MODIFY, getUser(  ) ) )
        {
            throw new AccessDeniedException(  );
        }

        entry = EntryHome.findByPrimaryKey( nIdEntry, plugin );

        if ( request.getParameter( PARAMETER_CANCEL ) == null )
        {
            String strError = entry.getEntryData( request, getLocale(  ) );

            if ( strError != null )
            {
                return strError;
            }

            EntryHome.update( entry, plugin );

            if ( entry.getFields(  ) != null )
            {
                for ( Field field : entry.getFields(  ) )
                {
                    FieldHome.update( field, plugin );
                }
            }
        }

        if ( request.getParameter( PARAMETER_APPLY ) == null )
        {
            return getJspModifyDirectory( request, _nIdDirectory );
        }
        else
        {
            return getJspModifyEntry( request, nIdEntry );
        }
    }

    /**
     * Gets the confirmation page of delete entry
     * @param request The HTTP request
     * @throws AccessDeniedException the {@link AccessDeniedException}
     * @return the confirmation page of delete entry
     */
    public String getConfirmRemoveEntry( HttpServletRequest request )
        throws AccessDeniedException
    {
        IEntry entry;
        Plugin plugin = getPlugin(  );
        String strIdEntry = request.getParameter( PARAMETER_ID_ENTRY );
        String strMessage;
        int nIdEntry = DirectoryUtils.convertStringToInt( strIdEntry );

        if ( ( nIdEntry == -1 ) ||
                !RBACService.isAuthorized( Directory.RESOURCE_TYPE, DirectoryUtils.EMPTY_STRING + _nIdDirectory,
                    DirectoryResourceIdService.PERMISSION_MODIFY, getUser(  ) ) )
        {
            throw new AccessDeniedException(  );
        }

        entry = EntryHome.findByPrimaryKey( nIdEntry, plugin );

        if ( entry.getEntryType(  ).getGroup(  ) )
        {
            strMessage = ( !entry.getChildren(  ).isEmpty(  ) ) ? MESSAGE_CONFIRM_REMOVE_GROUP_WITH_ENTRY
                                                                : MESSAGE_CONFIRM_REMOVE_GROUP_WITH_ANY_ENTRY;
        }
        else
        {
            strMessage = MESSAGE_CONFIRM_REMOVE_ENTRY;
        }

        UrlItem url = new UrlItem( JSP_DO_REMOVE_ENTRY );
        url.addParameter( PARAMETER_ID_ENTRY, strIdEntry + "#list" );

        return AdminMessageService.getMessageUrl( request, strMessage, url.getUrl(  ), AdminMessage.TYPE_CONFIRMATION );
    }

    /**
     * Perform the entry supression
     * @param request The HTTP request
     * @throws AccessDeniedException the {@link AccessDeniedException}
     * @return The URL to go after performing the action
     */
    public String doRemoveEntry( HttpServletRequest request )
        throws AccessDeniedException
    {
        Plugin plugin = getPlugin(  );
        String strIdEntry = request.getParameter( PARAMETER_ID_ENTRY );
        ArrayList<String> listErrors = new ArrayList<String>(  );

        int nIdEntry = DirectoryUtils.convertStringToInt( strIdEntry );
        IEntry entry = EntryHome.findByPrimaryKey( nIdEntry, getPlugin(  ) );

        if ( ( entry == null ) ||
                !RBACService.isAuthorized( Directory.RESOURCE_TYPE, DirectoryUtils.EMPTY_STRING + _nIdDirectory,
                    DirectoryResourceIdService.PERMISSION_MODIFY, getUser(  ) ) )
        {
            throw new AccessDeniedException(  );
        }

        RecordFieldFilter recordFieldFilter = new RecordFieldFilter(  );
        recordFieldFilter.setIdDirectory( _nIdDirectory );

        if ( !EntryRemovalListenerService.getService(  ).checkForRemoval( strIdEntry, listErrors, getLocale(  ) ) )
        {
            String strCause = AdminMessageService.getFormattedList( listErrors, getLocale(  ) );
            Object[] args = { strCause };

            return AdminMessageService.getMessageUrl( request, MESSAGE_CANNOT_REMOVE_ENTRY, args, AdminMessage.TYPE_STOP );
        }

        //remove all recordField associated
        RecordFieldFilter filter = new RecordFieldFilter(  );
        filter.setIdEntry( nIdEntry );
        RecordFieldHome.removeByFilter( filter, plugin );
        //remove entry
        EntryHome.remove( nIdEntry, plugin );

        return getJspModifyDirectory( request, _nIdDirectory );
    }

    /**
     * copy the entry whose key is specified in the Http request
     * @param request The HTTP request
     * @throws AccessDeniedException the {@link AccessDeniedException}
     * @return The URL to go after performing the action
     */
    public String doCopyEntry( HttpServletRequest request )
        throws AccessDeniedException
    {
        Plugin plugin = getPlugin(  );
        String strIdEntry = request.getParameter( PARAMETER_ID_ENTRY );
        IEntry entry;
        int nIdEntry = DirectoryUtils.convertStringToInt( strIdEntry );
        entry = EntryHome.findByPrimaryKey( nIdEntry, plugin );

        if ( ( entry == null ) ||
                !RBACService.isAuthorized( Directory.RESOURCE_TYPE, DirectoryUtils.EMPTY_STRING + _nIdDirectory,
                    DirectoryResourceIdService.PERMISSION_MODIFY, getUser(  ) ) )
        {
            throw new AccessDeniedException(  );
        }

        RecordFieldFilter recordFieldFilter = new RecordFieldFilter(  );
        recordFieldFilter.setIdDirectory( _nIdDirectory );

        if ( !entry.getEntryType(  ).getComment(  ) && !entry.getEntryType(  ).getGroup(  ) &&
                ( RecordHome.getCountRecord( recordFieldFilter, getPlugin(  ) ) != 0 ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_CANNOT_REMOVE_ENTRY_DIRECTORY_IS_NOT_EMPTY,
                AdminMessage.TYPE_STOP );
        }

        Object[] tabEntryTitleCopy = { entry.getTitle(  ) };
        String strTitleCopyEntry = I18nService.getLocalizedString( PROPERTY_COPY_ENTRY_TITLE, tabEntryTitleCopy,
                getLocale(  ) );

        if ( strTitleCopyEntry != null )
        {
            entry.setTitle( strTitleCopyEntry );
        }

        EntryHome.copy( entry, plugin );

        return getJspModifyDirectory( request, _nIdDirectory );
    }

    /**
     * Gets the list of questions group
     * @param request The HTTP request
     * @throws AccessDeniedException the {@link AccessDeniedException}
     * @return the list of questions group
     */
    public String getMoveEntry( HttpServletRequest request )
        throws AccessDeniedException
    {
        Plugin plugin = getPlugin(  );
        IEntry entry;
        List<IEntry> listGroup;
        EntryFilter filter;
        String strIdEntry = request.getParameter( PARAMETER_ID_ENTRY );
        int nIdEntry = DirectoryUtils.convertStringToInt( strIdEntry );
        entry = EntryHome.findByPrimaryKey( nIdEntry, plugin );

        if ( ( entry == null ) ||
                !RBACService.isAuthorized( Directory.RESOURCE_TYPE, DirectoryUtils.EMPTY_STRING + _nIdDirectory,
                    DirectoryResourceIdService.PERMISSION_MODIFY, getUser(  ) ) )
        {
            throw new AccessDeniedException(  );
        }

        _nIdEntry = nIdEntry;

        //recup group
        filter = new EntryFilter(  );
        filter.setIdDirectory( entry.getDirectory(  ).getIdDirectory(  ) );
        filter.setIsGroup( EntryFilter.FILTER_TRUE );
        listGroup = EntryHome.getEntryList( filter, plugin );

        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_ENTRY, entry );
        model.put( MARK_ENTRY_LIST, listGroup );

        setPageTitleProperty( DirectoryUtils.EMPTY_STRING );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MOVE_ENTRY, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Move the entry in the questions group specified in parameter
     * @param request The HTTP request
     * @throws AccessDeniedException the {@link AccessDeniedException}
     * @return The URL to go after performing the action
     */
    public String doMoveEntry( HttpServletRequest request )
        throws AccessDeniedException
    {
        Plugin plugin = getPlugin(  );
        IEntry entryToMove;
        IEntry entryGroup;
        String strIdEntryGroup = request.getParameter( PARAMETER_ID_ENTRY );
        int nIdEntryGroup = DirectoryUtils.convertStringToInt( strIdEntryGroup );

        if ( !RBACService.isAuthorized( Directory.RESOURCE_TYPE, DirectoryUtils.EMPTY_STRING + _nIdDirectory,
                    DirectoryResourceIdService.PERMISSION_MODIFY, getUser(  ) ) )
        {
            throw new AccessDeniedException(  );
        }

        int nPosition = 1;
        entryToMove = EntryHome.findByPrimaryKey( _nIdEntry, plugin );
        entryGroup = EntryHome.findByPrimaryKey( nIdEntryGroup, plugin );

        if ( ( entryToMove == null ) || ( entryGroup == null ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_SELECT_GROUP, AdminMessage.TYPE_STOP );
        }

        if ( ( entryGroup.getChildren(  ) != null ) && ( !entryGroup.getChildren(  ).isEmpty(  ) ) )
        {
            nPosition = entryGroup.getChildren(  ).get( entryGroup.getChildren(  ).size(  ) - 1 ).getPosition(  ) + 1;
            entryToMove.setPosition( nPosition );
        }

        entryToMove.setParent( entryGroup );

        EntryHome.update( entryToMove, plugin );

        return getJspModifyDirectory( request, _nIdDirectory );
    }

    /**
     * Move up the entry
     * @param request The HTTP request
     * @throws AccessDeniedException the {@link AccessDeniedException}
     * @return The URL to go after performing the action
     */
    public String doMoveUpEntry( HttpServletRequest request )
        throws AccessDeniedException
    {
        Plugin plugin = getPlugin(  );
        IEntry entry;

        String strIdEntry = request.getParameter( PARAMETER_ID_ENTRY );
        int nIdEntry = DirectoryUtils.convertStringToInt( strIdEntry );
        entry = EntryHome.findByPrimaryKey( nIdEntry, plugin );

        if ( ( entry == null ) ||
                !RBACService.isAuthorized( Directory.RESOURCE_TYPE, DirectoryUtils.EMPTY_STRING + _nIdDirectory,
                    DirectoryResourceIdService.PERMISSION_MODIFY, getUser(  ) ) )
        {
            throw new AccessDeniedException(  );
        }

        List<IEntry> listEntry;
        EntryFilter filter = new EntryFilter(  );
        filter.setIdDirectory( entry.getDirectory(  ).getIdDirectory(  ) );

        if ( entry.getParent(  ) != null )
        {
            filter.setIdEntryParent( entry.getParent(  ).getIdEntry(  ) );
        }
        else
        {
            filter.setIsEntryParentNull( EntryFilter.FILTER_TRUE );
        }

        listEntry = EntryHome.getEntryList( filter, plugin );

        int nIndexEntry = DirectoryUtils.getIndexEntryInTheEntryList( nIdEntry, listEntry );

        if ( nIndexEntry != 0 )
        {
            int nNewPosition;
            IEntry entryToInversePosition;
            entryToInversePosition = listEntry.get( nIndexEntry - 1 );
            entryToInversePosition = EntryHome.findByPrimaryKey( entryToInversePosition.getIdEntry(  ), plugin );

            nNewPosition = entryToInversePosition.getPosition(  );
            entryToInversePosition.setPosition( entry.getPosition(  ) );
            entry.setPosition( nNewPosition );
            EntryHome.update( entry, plugin );
            EntryHome.update( entryToInversePosition, plugin );
        }

        return getJspModifyDirectory( request, _nIdDirectory );
    }

    /**
     * Move down the entry
     * @param request The HTTP request
     * @throws AccessDeniedException the {@link AccessDeniedException}
     * @return The URL to go after performing the action
     */
    public String doMoveDownEntry( HttpServletRequest request )
        throws AccessDeniedException
    {
        Plugin plugin = getPlugin(  );
        IEntry entry;

        String strIdEntry = request.getParameter( PARAMETER_ID_ENTRY );
        int nIdEntry = DirectoryUtils.convertStringToInt( strIdEntry );
        entry = EntryHome.findByPrimaryKey( nIdEntry, plugin );

        if ( ( entry == null ) ||
                !RBACService.isAuthorized( Directory.RESOURCE_TYPE, DirectoryUtils.EMPTY_STRING + _nIdDirectory,
                    DirectoryResourceIdService.PERMISSION_MODIFY, getUser(  ) ) )
        {
            throw new AccessDeniedException(  );
        }

        List<IEntry> listEntry;
        EntryFilter filter = new EntryFilter(  );
        filter.setIdDirectory( entry.getDirectory(  ).getIdDirectory(  ) );

        if ( entry.getParent(  ) != null )
        {
            filter.setIdEntryParent( entry.getParent(  ).getIdEntry(  ) );
        }
        else
        {
            filter.setIsEntryParentNull( EntryFilter.FILTER_TRUE );
        }

        listEntry = EntryHome.getEntryList( filter, plugin );

        int nIndexEntry = DirectoryUtils.getIndexEntryInTheEntryList( nIdEntry, listEntry );

        if ( nIndexEntry != ( listEntry.size(  ) - 1 ) )
        {
            int nNewPosition;
            IEntry entryToInversePosition;
            entryToInversePosition = listEntry.get( nIndexEntry + 1 );
            entryToInversePosition = EntryHome.findByPrimaryKey( entryToInversePosition.getIdEntry(  ), plugin );

            nNewPosition = entryToInversePosition.getPosition(  );
            entryToInversePosition.setPosition( entry.getPosition(  ) );
            entry.setPosition( nNewPosition );
            EntryHome.update( entry, plugin );
            EntryHome.update( entryToInversePosition, plugin );
        }

        return getJspModifyDirectory( request, _nIdDirectory );
    }

    /**
     * Move out the entry
     * @param request The HTTP request
     * @throws AccessDeniedException the {@link AccessDeniedException}
     * @return The URL to go after performing the action
     */
    public String doMoveOutEntry( HttpServletRequest request )
        throws AccessDeniedException
    {
        Plugin plugin = getPlugin(  );
        IEntry entry;
        String strIdEntry = request.getParameter( PARAMETER_ID_ENTRY );
        int nIdEntry = DirectoryUtils.convertStringToInt( strIdEntry );
        entry = EntryHome.findByPrimaryKey( nIdEntry, plugin );

        if ( ( entry == null ) ||
                !RBACService.isAuthorized( Directory.RESOURCE_TYPE, DirectoryUtils.EMPTY_STRING + _nIdDirectory,
                    DirectoryResourceIdService.PERMISSION_MODIFY, getUser(  ) ) )
        {
            throw new AccessDeniedException(  );
        }

        entry.setParent( null );

        List<IEntry> listEntry;
        EntryFilter filter = new EntryFilter(  );
        filter.setIsEntryParentNull( EntryFilter.FILTER_TRUE );
        filter.setIdDirectory( entry.getDirectory(  ).getIdDirectory(  ) );
        listEntry = EntryHome.getEntryList( filter, plugin );
        entry.setPosition( listEntry.get( listEntry.size(  ) - 1 ).getPosition(  ) + 1 );
        EntryHome.update( entry, plugin );

        return getJspModifyDirectory( request, _nIdDirectory );
    }

    /**
     * Gets the confirmation page of disable directory
     * @param request The HTTP request
     * @throws AccessDeniedException the {@link AccessDeniedException}
     * @return the confirmation page of disable directory
     */
    public String getConfirmDisableDirectory( HttpServletRequest request )
        throws AccessDeniedException
    {
        String strIdDirectory = request.getParameter( PARAMETER_ID_DIRECTORY );
        int nIdDirectory = DirectoryUtils.convertStringToInt( strIdDirectory );
        String strMessage;

        if ( ( nIdDirectory == DirectoryUtils.CONSTANT_ID_NULL ) ||
                !RBACService.isAuthorized( Directory.RESOURCE_TYPE, strIdDirectory,
                    DirectoryResourceIdService.PERMISSION_CHANGE_STATE, getUser(  ) ) )
        {
            throw new AccessDeniedException(  );
        }

        strMessage = MESSAGE_CONFIRM_DISABLE_DIRECTORY;

        UrlItem url = new UrlItem( JSP_DO_DISABLE_DIRECTORY );
        url.addParameter( PARAMETER_ID_DIRECTORY, strIdDirectory );

        return AdminMessageService.getMessageUrl( request, strMessage, url.getUrl(  ), AdminMessage.TYPE_CONFIRMATION );
    }

    /**
     * Perform disable directory
     * @param request The HTTP request
     * @throws AccessDeniedException the {@link AccessDeniedException}
     * @return The URL to go after performing the action
     */
    public String doDisableDirectory( HttpServletRequest request )
        throws AccessDeniedException
    {
        Directory directory;
        Plugin plugin = getPlugin(  );
        String strIdDirectory = request.getParameter( PARAMETER_ID_DIRECTORY );
        int nIdDirectory = DirectoryUtils.convertStringToInt( strIdDirectory );
        directory = DirectoryHome.findByPrimaryKey( nIdDirectory, plugin );

        if ( ( directory == null ) ||
                !RBACService.isAuthorized( Directory.RESOURCE_TYPE, strIdDirectory,
                    DirectoryResourceIdService.PERMISSION_CHANGE_STATE, getUser(  ) ) )
        {
            throw new AccessDeniedException(  );
        }

        directory.setEnabled( false );
        DirectoryHome.update( directory, getPlugin(  ) );

        return getJspManageDirectory( request );
    }

    /**
     * Perform enable directory
     * @param request The HTTP request
     * @throws AccessDeniedException the {@link AccessDeniedException}
     * @return The URL to go after performing the action
     */
    public String doEnableDirectory( HttpServletRequest request )
        throws AccessDeniedException
    {
        Directory directory;
        Plugin plugin = getPlugin(  );
        String strIdDirectory = request.getParameter( PARAMETER_ID_DIRECTORY );
        int nIdDirectory = DirectoryUtils.convertStringToInt( strIdDirectory );
        directory = DirectoryHome.findByPrimaryKey( nIdDirectory, plugin );

        if ( ( directory == null ) ||
                !RBACService.isAuthorized( Directory.RESOURCE_TYPE, strIdDirectory,
                    DirectoryResourceIdService.PERMISSION_CHANGE_STATE, getUser(  ) ) )
        {
            throw new AccessDeniedException(  );
        }

        directory.setEnabled( true );
        DirectoryHome.update( directory, getPlugin(  ) );

        return getJspManageDirectory( request );
    }

    /**
     * Gets the field creation page
     * @param request The HTTP request
     * @throws AccessDeniedException the {@link AccessDeniedException}
     * @return the field creation page
     */
    public String getCreateField( HttpServletRequest request )
        throws AccessDeniedException
    {
        Field field = new Field(  );
        IEntry entry = EntryHome.findByPrimaryKey( _nIdEntry, getPlugin(  ) );

        if ( ( entry == null ) ||
                !RBACService.isAuthorized( Directory.RESOURCE_TYPE, DirectoryUtils.EMPTY_STRING + _nIdDirectory,
                    DirectoryResourceIdService.PERMISSION_MODIFY, getUser(  ) ) )
        {
            throw new AccessDeniedException(  );
        }

        field.setEntry( entry );

        HashMap model = new HashMap(  );
        Locale locale = getLocale(  );
        model.put( MARK_FIELD, field );
        model.put( MARK_USER_WORKGROUP_REF_LIST, AdminWorkgroupService.getUserWorkgroups( getUser(  ), locale ) );

        if ( SecurityService.isAuthenticationEnable(  ) )
        {
            model.put( MARK_ROLE_REF_LIST, RoleHome.getRolesList(  ) );
        }

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CREATE_FIELD, locale, model );
        setPageTitleProperty( PROPERTY_CREATE_FIELD_PAGE_TITLE );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Gets the field modification page
     * @param request The HTTP request
     * @param bWithConditionalQuestion true if the field is associate to conditionals questions
     * @throws AccessDeniedException the {@link AccessDeniedException}
     * @return the field modification page
     */
    public String getModifyField( HttpServletRequest request, boolean bWithConditionalQuestion )
        throws AccessDeniedException
    {
        Field field = null;
        IEntry entry = null;
        Plugin plugin = getPlugin(  );
        String strIdField = request.getParameter( PARAMETER_ID_FIELD );
        int nIdField = DirectoryUtils.convertStringToInt( strIdField );
        field = FieldHome.findByPrimaryKey( nIdField, getPlugin(  ) );

        if ( ( field == null ) ||
                !RBACService.isAuthorized( Directory.RESOURCE_TYPE, DirectoryUtils.EMPTY_STRING + _nIdDirectory,
                    DirectoryResourceIdService.PERMISSION_MODIFY, getUser(  ) ) )
        {
            throw new AccessDeniedException(  );
        }

        entry = EntryHome.findByPrimaryKey( field.getEntry(  ).getIdEntry(  ), plugin );

        field.setEntry( entry );

        Map<String, Object> model = new HashMap<String, Object>(  );
        Locale locale = getLocale(  );
        model.put( MARK_FIELD, field );
        model.put( MARK_USER_WORKGROUP_REF_LIST, AdminWorkgroupService.getUserWorkgroups( getUser(  ), locale ) );

        if ( SecurityService.isAuthenticationEnable(  ) )
        {
            model.put( MARK_ROLE_REF_LIST, RoleHome.getRolesList(  ) );
        }

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MODIFY_FIELD, locale, model );
        setPageTitleProperty( PROPERTY_MODIFY_FIELD_PAGE_TITLE );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Perform creation field
     * @param request The HTTP request
     * @throws AccessDeniedException the {@link AccessDeniedException}
     * @return The URL to go after performing the action
     */
    public String doCreateField( HttpServletRequest request )
        throws AccessDeniedException
    {
        if ( !RBACService.isAuthorized( Directory.RESOURCE_TYPE, DirectoryUtils.EMPTY_STRING + _nIdDirectory,
                    DirectoryResourceIdService.PERMISSION_MODIFY, getUser(  ) ) )
        {
            throw new AccessDeniedException(  );
        }

        if ( request.getParameter( PARAMETER_CANCEL ) == null )
        {
            IEntry entry = new Entry(  );
            entry.setIdEntry( _nIdEntry );

            Field field = new Field(  );
            field.setEntry( entry );

            String strError = getFieldData( request, field );

            if ( strError != null )
            {
                return strError;
            }

            FieldHome.create( field, getPlugin(  ) );
        }

        return getJspModifyEntry( request, _nIdEntry );
    }

    /**
     * Perform modification field
     * @param request The HTTP request
     * @throws AccessDeniedException the {@link AccessDeniedException}
     * @return The URL to go after performing the action
     */
    public String doModifyField( HttpServletRequest request )
        throws AccessDeniedException
    {
        Plugin plugin = getPlugin(  );
        Field field = null;
        String strIdField = request.getParameter( PARAMETER_ID_FIELD );
        int nIdField = DirectoryUtils.convertStringToInt( strIdField );
        field = FieldHome.findByPrimaryKey( nIdField, plugin );

        if ( ( field == null ) ||
                !RBACService.isAuthorized( Directory.RESOURCE_TYPE, DirectoryUtils.EMPTY_STRING + _nIdDirectory,
                    DirectoryResourceIdService.PERMISSION_MODIFY, getUser(  ) ) )
        {
            throw new AccessDeniedException(  );
        }

        if ( request.getParameter( PARAMETER_CANCEL ) == null )
        {
            String strError = getFieldData( request, field );

            if ( strError != null )
            {
                return strError;
            }

            FieldHome.update( field, getPlugin(  ) );
        }

        if ( request.getParameter( PARAMETER_APPLY ) == null )
        {
            return getJspModifyEntry( request, field.getEntry(  ).getIdEntry(  ) );
        }
        else
        {
            return getJspModifyField( request, nIdField );
        }
    }

    /**
     * Get the request data and if there is no error insert the data in the field specified in parameter.
     * return null if there is no error or else return the error page url
     * @param request the request
     * @param field field
     * @return null if there is no error or else return the error page url
     */
    private String getFieldData( HttpServletRequest request, Field field )
    {
        String strTitle = request.getParameter( PARAMETER_TITLE );
        String strValue = request.getParameter( PARAMETER_VALUE );
        String strDefaultValue = request.getParameter( PARAMETER_DEFAULT_VALUE );

        String strFieldError = DirectoryUtils.EMPTY_STRING;

        if ( ( strTitle == null ) || DirectoryUtils.EMPTY_STRING.equals( strTitle ) )
        {
            strFieldError = FIELD_TITLE_FIELD;
        }
        else if ( ( strValue == null ) || DirectoryUtils.EMPTY_STRING.equals( strValue ) )
        {
            strFieldError = FIELD_VALUE_FIELD;
        }
        else if ( !StringUtil.checkCodeKey( strValue ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_FIELD_VALUE_FIELD, AdminMessage.TYPE_STOP );
        }

        String strRoleKey = request.getParameter( PARAMETER_ROLE_KEY );
        String strWorkgroupKey = request.getParameter( PARAMETER_WORKGROUP );

        if ( !strFieldError.equals( DirectoryUtils.EMPTY_STRING ) )
        {
            Object[] tabRequiredFields = { I18nService.getLocalizedString( strFieldError, getLocale(  ) ) };

            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields,
                AdminMessage.TYPE_STOP );
        }

        field.setTitle( strTitle );
        field.setValue( strValue );
        field.setDefaultValue( strDefaultValue != null );
        field.setRoleKey( strRoleKey );
        field.setWorkgroup( strWorkgroupKey );

        return null; // No error
    }

    /**
     * Gets the confirmation page of delete field
     * @param request The HTTP request
     * @throws AccessDeniedException the {@link AccessDeniedException}
     * @return the confirmation page of delete field
     */
    public String getConfirmRemoveField( HttpServletRequest request )
        throws AccessDeniedException
    {
        if ( ( request.getParameter( PARAMETER_ID_FIELD ) == null ) ||
                !RBACService.isAuthorized( Directory.RESOURCE_TYPE, DirectoryUtils.EMPTY_STRING + _nIdDirectory,
                    DirectoryResourceIdService.PERMISSION_MODIFY, getUser(  ) ) )
        {
            throw new AccessDeniedException(  );
        }

        String strIdField = request.getParameter( PARAMETER_ID_FIELD );
        UrlItem url = new UrlItem( JSP_DO_REMOVE_FIELD );
        url.addParameter( PARAMETER_ID_FIELD, strIdField + "#list" );

        return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_FIELD, url.getUrl(  ),
            AdminMessage.TYPE_CONFIRMATION );
    }

    /**
     * Perform suppression field
     * @param request The HTTP request
     * @throws AccessDeniedException the {@link AccessDeniedException}
     * @return The URL to go after performing the action
     */
    public String doRemoveField( HttpServletRequest request )
        throws AccessDeniedException
    {
        String strIdField = request.getParameter( PARAMETER_ID_FIELD );
        int nIdField = DirectoryUtils.convertStringToInt( strIdField );

        if ( ( nIdField == -1 ) ||
                !RBACService.isAuthorized( Directory.RESOURCE_TYPE, DirectoryUtils.EMPTY_STRING + _nIdDirectory,
                    DirectoryResourceIdService.PERMISSION_MODIFY, getUser(  ) ) )
        {
            throw new AccessDeniedException(  );
        }

        RecordFieldFilter recordFieldFilter = new RecordFieldFilter(  );
        recordFieldFilter.setIdField( nIdField );

        if ( ( RecordFieldHome.getCountRecordField( recordFieldFilter, getPlugin(  ) ) != 0 ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_CANNOT_REMOVE_FIELD_DIRECTORY_IS_NOT_EMPTY,
                AdminMessage.TYPE_STOP );
        }

        FieldHome.remove( nIdField, getPlugin(  ) );

        return getJspModifyEntry( request, _nIdEntry );
    }

    /**
     * Move up the field
     * @param request The HTTP request
     * @throws AccessDeniedException the {@link AccessDeniedException}
     * @return The URL to go after performing the action
     */
    public String doMoveUpField( HttpServletRequest request )
        throws AccessDeniedException
    {
        Plugin plugin = getPlugin(  );
        List<Field> listField;
        Field field;
        String strIdField = request.getParameter( PARAMETER_ID_FIELD );
        int nIdField = DirectoryUtils.convertStringToInt( strIdField );
        field = FieldHome.findByPrimaryKey( nIdField, plugin );

        if ( ( field == null ) ||
                !RBACService.isAuthorized( Directory.RESOURCE_TYPE, DirectoryUtils.EMPTY_STRING + _nIdDirectory,
                    DirectoryResourceIdService.PERMISSION_MODIFY, getUser(  ) ) )
        {
            throw new AccessDeniedException(  );
        }

        listField = FieldHome.getFieldListByIdEntry( field.getEntry(  ).getIdEntry(  ), plugin );

        int nIndexField = DirectoryUtils.getIndexFieldInTheFieldList( nIdField, listField );

        if ( nIndexField != 0 )
        {
            int nNewPosition;
            Field fieldToInversePosition;
            fieldToInversePosition = listField.get( nIndexField - 1 );
            nNewPosition = fieldToInversePosition.getPosition(  );
            fieldToInversePosition.setPosition( field.getPosition(  ) );
            field.setPosition( nNewPosition );
            FieldHome.update( field, plugin );
            FieldHome.update( fieldToInversePosition, plugin );
        }

        return getJspModifyEntry( request, _nIdEntry );
    }

    /**
     * Move down the field
     * @param request The HTTP request
     * @throws AccessDeniedException the {@link AccessDeniedException}
     * @return The URL to go after performing the action
     */
    public String doMoveDownField( HttpServletRequest request )
        throws AccessDeniedException
    {
        Plugin plugin = getPlugin(  );
        List<Field> listField;
        Field field;
        String strIdField = request.getParameter( PARAMETER_ID_FIELD );
        int nIdField = DirectoryUtils.convertStringToInt( strIdField );
        field = FieldHome.findByPrimaryKey( nIdField, plugin );

        if ( ( field == null ) ||
                !RBACService.isAuthorized( Directory.RESOURCE_TYPE, DirectoryUtils.EMPTY_STRING + _nIdDirectory,
                    DirectoryResourceIdService.PERMISSION_MODIFY, getUser(  ) ) )
        {
            throw new AccessDeniedException(  );
        }

        listField = FieldHome.getFieldListByIdEntry( field.getEntry(  ).getIdEntry(  ), plugin );

        int nIndexField = DirectoryUtils.getIndexFieldInTheFieldList( nIdField, listField );

        if ( nIndexField != ( listField.size(  ) - 1 ) )
        {
            int nNewPosition;
            Field fieldToInversePosition;
            fieldToInversePosition = listField.get( nIndexField + 1 );
            nNewPosition = fieldToInversePosition.getPosition(  );
            fieldToInversePosition.setPosition( field.getPosition(  ) );
            field.setPosition( nNewPosition );
            FieldHome.update( field, plugin );
            FieldHome.update( fieldToInversePosition, plugin );
        }

        return getJspModifyEntry( request, _nIdEntry );
    }

    /**
     * Delete association between  field and  regular expression
     * @param request the Http Request
     * @throws AccessDeniedException the {@link AccessDeniedException}
     * @return The URL to go after performing the action
     */
    public String doRemoveRegularExpression( HttpServletRequest request )
        throws AccessDeniedException
    {
        String strIdExpression = request.getParameter( PARAMETER_ID_EXPRESSION );
        String strIdField = request.getParameter( PARAMETER_ID_FIELD );
        int nIdField = DirectoryUtils.convertStringToInt( strIdField );
        int nIdExpression = DirectoryUtils.convertStringToInt( strIdExpression );

        if ( ( nIdExpression == DirectoryUtils.CONSTANT_ID_NULL ) || ( nIdField == DirectoryUtils.CONSTANT_ID_NULL ) ||
                ( !RBACService.isAuthorized( Directory.RESOURCE_TYPE, DirectoryUtils.EMPTY_STRING + _nIdDirectory,
                    DirectoryResourceIdService.PERMISSION_MODIFY, getUser(  ) ) ) )
        {
            throw new AccessDeniedException(  );
        }

        FieldHome.removeVerifyBy( nIdField, nIdExpression, getPlugin(  ) );

        return getJspModifyEntry( request, _nIdEntry );
    }

    /**
     * insert association between  field and  regular expression
     * @param request the Http Request
     * @throws AccessDeniedException the {@link AccessDeniedException}
     * @return The URL to go after performing the action
     */
    public String doInsertRegularExpression( HttpServletRequest request )
        throws AccessDeniedException
    {
        String strIdExpression = request.getParameter( PARAMETER_ID_EXPRESSION );
        String strIdField = request.getParameter( PARAMETER_ID_FIELD );
        int nIdField = DirectoryUtils.convertStringToInt( strIdField );
        int nIdExpression = DirectoryUtils.convertStringToInt( strIdExpression );

        if ( ( nIdExpression == DirectoryUtils.CONSTANT_ID_NULL ) || ( nIdField == DirectoryUtils.CONSTANT_ID_NULL ) ||
                ( !RBACService.isAuthorized( Directory.RESOURCE_TYPE, DirectoryUtils.EMPTY_STRING + _nIdDirectory,
                    DirectoryResourceIdService.PERMISSION_MODIFY, getUser(  ) ) ) )
        {
            throw new AccessDeniedException(  );
        }

        FieldHome.createVerifyBy( nIdField, nIdExpression, getPlugin(  ) );

        return getJspModifyEntry( request, _nIdEntry );
    }

    /**
     * Get the map query and return on manage directory record
     * @param request the Http Request
     * @throws AccessDeniedException the {@link AccessDeniedException}
     * @return The URL to go after performing the action
     */
    public String doSearchDirectoryRecord( HttpServletRequest request )
        throws AccessDeniedException
    {
        String strIdDirectory = request.getParameter( PARAMETER_ID_DIRECTORY );
        int nIdDirectory = DirectoryUtils.convertStringToInt( strIdDirectory );
        _nIdWorkflowSate = DirectoryUtils.convertStringToInt( request.getParameter( PARAMETER_WORKFLOW_STATE_SELECTED ) );

        try
        {
            //get search filter
            _mapQuery = DirectoryUtils.getSearchRecordData( request, nIdDirectory, getPlugin(  ), getLocale(  ) );
            _dateCreationBeginRecord = DirectoryUtils.getSearchRecordDateCreationFromRequest( request,
                    PARAMETER_DATE_BEGIN_CREATION, getLocale(  ) );
            _dateCreationEndRecord = DirectoryUtils.getSearchRecordDateCreationFromRequest( request,
                    PARAMETER_DATE_END_CREATION, getLocale(  ) );
            _dateCreationRecord = DirectoryUtils.getSearchRecordDateCreationFromRequest( request,
                    PARAMETER_DATE_CREATION, getLocale(  ) );
        }
        catch ( DirectoryErrorException error )
        {
            String strErrorMessage = DirectoryUtils.EMPTY_STRING;

            if ( error.isMandatoryError(  ) )
            {
                Object[] tabRequiredFields = { error.getTitleField(  ) };
                strErrorMessage = AdminMessageService.getMessageUrl( request, MESSAGE_DIRECTORY_ERROR_MANDATORY_FIELD,
                        tabRequiredFields, AdminMessage.TYPE_STOP );
            }
            else
            {
                Object[] tabRequiredFields = { error.getTitleField(  ), error.getErrorMessage(  ) };
                strErrorMessage = AdminMessageService.getMessageUrl( request, MESSAGE_DIRECTORY_ERROR,
                        tabRequiredFields, AdminMessage.TYPE_STOP );
            }

            return strErrorMessage;
        }

        return getJspManageDirectoryRecord( request, nIdDirectory ) + "&" + PARAMETER_SEARCH + "=" + PARAMETER_SEARCH;
    }

    /**
     * Get the result list according to queries
     * @param request The {@link HttpServletRequest}
     * @param directory The {@link Directory}
     * @param bWorkflowServiceEnable true if the WorkflowService is enabled
     * @return The list of id records
     * @throws AccessDeniedException
     */
    private List<Integer> getListResults( HttpServletRequest request, Directory directory,
        boolean bWorkflowServiceEnable, boolean bUseFilterDirectory, IEntry sortEntry, int nSortOrder )
        throws AccessDeniedException
    {
        //call search service
        RecordFieldFilter filter = new RecordFieldFilter(  );
        filter.setIdDirectory( directory.getIdDirectory(  ) );

        List<Integer> listResultRecordId = null;

        //filter by workgroup 
        filter.setWorkgroupKeyList( AdminWorkgroupService.getUserWorkgroups( getUser(  ), getLocale(  ) ) );
  
        // sort filter
        filter.setSortEntry( sortEntry );
        filter.setSortOrder( nSortOrder );

        // If workflow active, filter by workflow state
        if ( ( directory.getIdWorkflow(  ) != DirectoryUtils.CONSTANT_ID_NULL ) &&
                bWorkflowServiceEnable /* &&
            ( _nIdWorkflowSate != DirectoryUtils.CONSTANT_ID_NULL ) */ )
        {
            if ( bUseFilterDirectory )
            {
                listResultRecordId = DirectorySearchService.getInstance(  )
                                                           .getSearchResults( directory, _mapQuery,
                        _dateCreationRecord, _dateCreationBeginRecord, _dateCreationEndRecord, filter, getPlugin(  ) );
            }
            else
            {
                listResultRecordId = DirectorySearchService.getInstance(  )
                                                           .getSearchResults( directory, null, null, null, null,
                        filter, getPlugin(  ) );
            }

            List<Integer> listTmpResultRecordId = WorkflowService.getInstance(  )
                                                                 .getAuthorizedResourceList( Record.WORKFLOW_RESOURCE_TYPE,
                    directory.getIdWorkflow(  ), _nIdWorkflowSate, Integer.valueOf( directory.getIdDirectory(  ) ),
                    getUser(  ) );

            listResultRecordId = DirectoryUtils.retainAllIdsKeepingFirstOrder( listResultRecordId, listTmpResultRecordId );
        }
        else
        {
            if ( bUseFilterDirectory )
            {
                listResultRecordId = DirectorySearchService.getInstance(  )
                                                           .getSearchResults( directory, _mapQuery,
                        _dateCreationRecord, _dateCreationBeginRecord, _dateCreationEndRecord, filter, getPlugin(  ) );
            }
            else
            {
                listResultRecordId = DirectorySearchService.getInstance(  )
                                                           .getSearchResults( directory, null, null, null, null,
                        filter, getPlugin(  ) );
            }
        }

        return listResultRecordId;
    }

    /**
     * Return management of directory record ( list of directory record )
     * @param request The Http request
     * @throws AccessDeniedException the {@link AccessDeniedException}
     * @return Html
     */
    @SuppressWarnings( "unchecked" )
    public String getManageDirectoryRecord( HttpServletRequest request )
        throws AccessDeniedException
    {
        String strIdDirectory = request.getParameter( PARAMETER_ID_DIRECTORY );
        int nIdDirectory = DirectoryUtils.convertStringToInt( strIdDirectory );
        boolean bWorkflowServiceEnable = WorkflowService.getInstance(  ).isAvailable(  );
        AdminUser adminUser = getUser(  );
        Directory directory = DirectoryHome.findByPrimaryKey( nIdDirectory, getPlugin(  ) );

        if ( ( directory == null ) ||
                !RBACService.isAuthorized( Directory.RESOURCE_TYPE, strIdDirectory,
                    DirectoryResourceIdService.PERMISSION_MANAGE_RECORD, getUser(  ) ) )
        {
            throw new AccessDeniedException(  );
        }

        if ( request.getParameter( PARAMETER_SESSION ) == null )
        {
            reInitDirectoryRecordFilter(  );
        }

        _strCurrentPageIndexDirectoryRecord = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX,
                _strCurrentPageIndexDirectoryRecord );
        _nItemsPerPageDirectoryRecord = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE,
                _nItemsPerPageDirectoryRecord, _nDefaultItemsPerPage );

        //build entryFilter
        EntryFilter entryFilter = new EntryFilter(  );
        entryFilter.setIdDirectory( directory.getIdDirectory(  ) );
        entryFilter.setIsGroup( EntryFilter.FILTER_FALSE );
        entryFilter.setIsComment( EntryFilter.FILTER_FALSE );

        List<IEntry> listEntryFormMainSearch = new ArrayList<IEntry>(  );
        List<IEntry> listEntryFormComplementarySearch = new ArrayList<IEntry>(  );
        List<IEntry> listEntryResultSearch = new ArrayList<IEntry>(  );
        List<IEntry> listEntryGeolocation = new ArrayList<IEntry>(  );

        for ( IEntry entry : EntryHome.getEntryList( entryFilter, getPlugin(  ) ) )
        {
            IEntry entryTmp = EntryHome.findByPrimaryKey( entry.getIdEntry(  ), getPlugin(  ) );

            if ( entryTmp.isWorkgroupAssociated(  ) )
            {
                entryTmp.setFields( DirectoryUtils.getAuthorizedFieldsByWorkgroup( entryTmp.getFields(  ), getUser(  ) ) );
            }

            if ( entryTmp.isIndexed(  ) )
            {
                if ( !entryTmp.isShownInAdvancedSearch(  ) )
                {
                    listEntryFormMainSearch.add( entryTmp );
                }
                else
                {
                    listEntryFormComplementarySearch.add( entryTmp );
                }
            }

            if ( entry.isShownInResultList(  ) )
            {
                listEntryResultSearch.add( entryTmp );

                // add geolocation entries
                if ( entry.getEntryType(  ).getIdType(  ) == AppPropertiesService.getPropertyInt( 
                            PROPERTY_ENTRY_TYPE_GEOLOCATION, 16 ) )
                {
                    listEntryGeolocation.add( entry );
                }
            }
        }

        UrlItem url = new UrlItem( getJspManageDirectoryRecord( request, nIdDirectory ) );
        String strSortedAttributeName = request.getParameter( Parameters.SORTED_ATTRIBUTE_NAME );
        String strAscSort = null;
        List<Record> lRecord = null;
        
        IEntry sortEntry = null;
        int nSortOrder = RecordFieldFilter.ORDER_NONE;

        if ( ( strSortedAttributeName != null ) || ( directory.getIdSortEntry(  ) != null ) )
        {
            if ( strSortedAttributeName == null )
            {
                strSortedAttributeName = directory.getIdSortEntry(  );
            }

            if ( strSortedAttributeName.equals( PARAMETER_DATECREATION ) )
            {
            	// IMPORTANT : date creation is default filter
            }
            else
            {
                int nSortedEntryId = Integer.parseInt( strSortedAttributeName );
                sortEntry = EntryHome.findByPrimaryKey( nSortedEntryId, getPlugin(  ) );
            }

            strAscSort = request.getParameter( Parameters.SORTED_ASC );
	
            boolean bIsAscSort;

            if ( strAscSort != null )
            {
                bIsAscSort = Boolean.parseBoolean( strAscSort );
            }
            else
            {
                bIsAscSort = directory.isAscendingSort(  );
            }
            
            if ( bIsAscSort )
            {
            	nSortOrder = RecordFieldFilter.ORDER_ASC;
            }
            else
            {
            	nSortOrder = RecordFieldFilter.ORDER_DESC;
            }

            url.addParameter( Parameters.SORTED_ATTRIBUTE_NAME, strSortedAttributeName );
            url.addParameter( Parameters.SORTED_ASC, strAscSort );
        }
        
        List<Integer> listResultRecordId = getListResults( request, directory, bWorkflowServiceEnable, true, sortEntry, nSortOrder );
        
        // HACK : We copy the list so workflow does not clear the paginator list.
        LocalizedPaginator<Integer> paginator = new LocalizedPaginator<Integer>( new ArrayList<Integer>( listResultRecordId ), _nItemsPerPageDirectoryRecord,
                url.getUrl(  ), PARAMETER_PAGE_INDEX, _strCurrentPageIndexDirectoryRecord, getLocale(  ) );
        
        // get only record for page items.
        lRecord = RecordHome.loadListByListId( paginator.getPageItems(), getPlugin(  ) );
        
        boolean bHistoryEnabled = false;
        RecordFieldFilter recordFieldFilter = new RecordFieldFilter(  );
        recordFieldFilter.setIsEntryShownInResultList( RecordFieldFilter.FILTER_TRUE );

        bWorkflowServiceEnable = ( directory.getIdWorkflow(  ) != DirectoryUtils.CONSTANT_ID_NULL ) &&
            bWorkflowServiceEnable;

        WorkflowService workflowService = null;
        HashMap<Integer, List<Action>> lListActions = null;

        if ( bWorkflowServiceEnable )
        {
            bHistoryEnabled = true;
            workflowService = WorkflowService.getInstance(  );
            lListActions = workflowService.getActions( (List<Integer>) listResultRecordId,
                    Record.WORKFLOW_RESOURCE_TYPE, Integer.valueOf( directory.getIdDirectory(  ) ),
                    directory.getIdWorkflow(  ), adminUser );
        }

        List<HashMap> listResourceActions = new ArrayList<HashMap>( lRecord.size(  ) );

        List<DirectoryAction> listActionsForDirectoryEnable = DirectoryActionHome.selectActionsRecordByFormState( Directory.STATE_ENABLE,
                getPlugin(  ), getLocale(  ) );
        List<DirectoryAction> listActionsForDirectoryDisable = DirectoryActionHome.selectActionsRecordByFormState( Directory.STATE_DISABLE,
                getPlugin(  ), getLocale(  ) );

        listActionsForDirectoryEnable = (List<DirectoryAction>) RBACService.getAuthorizedActionsCollection( listActionsForDirectoryEnable,
                directory, getUser(  ) );
        listActionsForDirectoryDisable = (List<DirectoryAction>) RBACService.getAuthorizedActionsCollection( listActionsForDirectoryDisable,
                directory, getUser(  ) );

        for ( Record record : lRecord )
        {
            if ( record.isEnabled(  ) )
            {
                record.setActions( listActionsForDirectoryEnable );
            }
            else
            {
                record.setActions( listActionsForDirectoryDisable );
            }

            //workflow service
            HashMap resourceActions = new HashMap(  );
            resourceActions.put( MARK_RECORD, record );
            resourceActions.put( MARK_MAP_ID_ENTRY_LIST_RECORD_FIELD,
                DirectoryUtils.getMapIdEntryListRecordField( listEntryResultSearch, record.getIdRecord(  ),
                    getPlugin(  ) ) );

            if ( bWorkflowServiceEnable )
            {
                State state = workflowService.getState( record.getIdRecord(  ), Record.WORKFLOW_RESOURCE_TYPE,
                        directory.getIdWorkflow(  ), Integer.valueOf( directory.getIdDirectory(  ) ), adminUser );
                resourceActions.put( MARK_WORKFLOW_STATE, state );

                if ( bHistoryEnabled )
                {
                    //State state = workflowService.getState( record.getIdRecord(  ), Record.WORKFLOW_RESOURCE_TYPE,
                    //        directory.getIdWorkflow(  ), Integer.valueOf( directory.getIdDirectory(  ) ), adminUser );
                    resourceActions.put( MARK_WORKFLOW_ACTION_LIST, lListActions.get( record.getIdRecord(  ) ) );
                }
            }

            listResourceActions.add( resourceActions );
        }

        //add xslExport
        DirectoryXslFilter directoryXslFilter = new DirectoryXslFilter(  );

        directoryXslFilter.setIdCategory( Category.ID_CATEGORY_EXPORT );

        ReferenceList refListXslExport = DirectoryXslHome.getRefList( directoryXslFilter, getPlugin(  ) );

        Map<String, Object> model = new HashMap<String, Object>(  );

        model.put( MARK_SHOW_DATE_CREATION_RESULT, directory.isDateShownInResultList(  ) );
        model.put( MARK_ID_ENTRY_TYPE_IMAGE, AppPropertiesService.getPropertyInt( PROPERTY_ENTRY_TYPE_IMAGE, 10 ) );
        model.put( MARK_ID_ENTRY_TYPE_DIRECTORY,
            AppPropertiesService.getPropertyInt( PROPERTY_ENTRY_TYPE_DIRECTORY, 12 ) );
        model.put( MARK_ID_ENTRY_TYPE_GEOLOCATION,
            AppPropertiesService.getPropertyInt( PROPERTY_ENTRY_TYPE_GEOLOCATION, 16 ) );
        model.put( MARK_ID_ENTRY_TYPE_MYLUTECE_USER,
            AppPropertiesService.getPropertyInt( PROPERTY_ENTRY_TYPE_MYLUTECE_USER, 19 ) );
        model.put( MARK_ENTRY_LIST_GEOLOCATION, listEntryGeolocation );
        model.put( MARK_WORKFLOW_STATE_SEARCH_DEFAULT, _nIdWorkflowSate );
        model.put( MARK_PAGINATOR, paginator );
        model.put( MARK_NB_ITEMS_PER_PAGE, DirectoryUtils.EMPTY_STRING + _nItemsPerPageDirectoryRecord );
        model.put( MARK_ENTRY_LIST_FORM_MAIN_SEARCH, listEntryFormMainSearch );
        model.put( MARK_ENTRY_LIST_FORM_COMPLEMENTARY_SEARCH, listEntryFormComplementarySearch );
        model.put( MARK_ENTRY_LIST_SEARCH_RESULT, listEntryResultSearch );

        model.put( MARK_XSL_EXPORT_LIST, refListXslExport );
        model.put( MARK_MAP_ID_ENTRY_LIST_RECORD_FIELD, _mapQuery );
        model.put( MARK_DATE_CREATION_SEARCH, _dateCreationRecord );
        model.put( MARK_DATE_CREATION_BEGIN_SEARCH, _dateCreationBeginRecord );
        model.put( MARK_DATE_CREATION_END_SEARCH, _dateCreationEndRecord );

        model.put( MARK_DIRECTORY, directory );
        //model.put( MARK_DIRECTORY_RECORD_LIST, listRecordResult );
        //model.put( MARK_NUMBER_RECORD, paginator.getItemsCount(  ) );
        model.put( MARK_NUMBER_RECORD, listResultRecordId.size(  ) );
        model.put( MARK_RESOURCE_ACTIONS_LIST, listResourceActions );
        model.put( MARK_HISTORY_WORKFLOW_ENABLED, bHistoryEnabled );
        model.put( MARK_PERMISSION_CREATE_RECORD,
            RBACService.isAuthorized( Directory.RESOURCE_TYPE, strIdDirectory,
                DirectoryResourceIdService.PERMISSION_CREATE_RECORD, getUser(  ) ) );
        model.put( MARK_PERMISSION_MASS_PRINT,
            RBACService.isAuthorized( Directory.RESOURCE_TYPE, strIdDirectory,
                DirectoryResourceIdService.PERMISSION_MASS_PRINT, getUser(  ) ) );
        model.put( MARK_PERMISSION_VISUALISATION_MYLUTECE_USER,
            RBACService.isAuthorized( Directory.RESOURCE_TYPE, strIdDirectory,
                DirectoryResourceIdService.PERMISSION_VISUALISATION_MYLUTECE_USER, getUser(  ) ) );

        model.put( MARK_LOCALE, getLocale(  ) );
        model.put( MARK_IS_WORKFLOW_ENABLED, bWorkflowServiceEnable );

        if ( directory.isDisplayComplementarySearchState(  ) || directory.isDisplaySearchState(  ) )
        {
            ReferenceList referenceList = new ReferenceList(  );
            referenceList.addItem( -1, "" );

            Collection<State> colState = WorkflowService.getInstance(  ).getAllStateByWorkflow( directory.getIdWorkflow(  ), adminUser );
            if( colState != null)
            {
	            for ( State stateWorkflow : colState )
	            {
	                referenceList.addItem( stateWorkflow.getId(  ), stateWorkflow.getName(  ) );
	            }
            }

            model.put( MARK_SEARCH_STATE_WORKFLOW, referenceList );
        }

        setPageTitleProperty( PROPERTY_MANAGE_DIRECTORY_RECORD_PAGE_TITLE );

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_MANAGE_DIRECTORY_RECORD, getLocale(  ),
                model );

        return getAdminPage( templateList.getHtml(  ) );
    }

    /**
     * Append partial export result to temporary file if need
     * @param strBufferListRecordXml The partial XML content
     * @param bufferedWriter The bufferedWriter used to append content to temporary file
     * @param physicalFile The XSL physical File
     * @param bIsCsvExport is CSV export
     * @param strXslId The XSL unique ID
     * @param nXmlHeaderLength XML header length
     * @param xmlTransformerService he XmlTransformer service
     */
    private StringBuffer appendPartialContent( StringBuffer strBufferListRecordXml, BufferedWriter bufferedWriter,
        PhysicalFile physicalFile, boolean bIsCsvExport, String strXslId, int nXmlHeaderLength,
        XmlTransformerService xmlTransformerService )
    {
        if ( strBufferListRecordXml.length(  ) > EXPORT_STRINGBUFFER_MAX_CONTENT_SIZE )
        {
            strBufferListRecordXml.insert( 0, EXPORT_XSL_BEGIN_PARTIAL_EXPORT );
            strBufferListRecordXml.insert( 0, XmlUtil.getXmlHeader(  ) );
            strBufferListRecordXml.append( EXPORT_XSL_END_PARTIAL_EXPORT );

            String strFileOutPut = xmlTransformerService.transformBySourceWithXslCache( strBufferListRecordXml.toString(  ),
                    physicalFile.getValue(  ), strXslId, null, null );

            try
            {
                if ( bIsCsvExport )
                {
                    bufferedWriter.write( strFileOutPut );
                }
                else
                {
                    bufferedWriter.write( strFileOutPut.substring( nXmlHeaderLength ) );
                }
            }
            catch ( IOException e )
            {
                AppLogService.error( e );
            }
            finally
            {
                strBufferListRecordXml = new StringBuffer( EXPORT_STRINGBUFFER_INITIAL_SIZE );
            }
        }

        return strBufferListRecordXml;
    }

    /**
     * Export Directory record
     * @param request the Http Request
     * @param response the Http response
     * @throws AccessDeniedException the {@link AccessDeniedException}
     */
    public void doExportDirectoryRecord( HttpServletRequest request, HttpServletResponse response )
        throws AccessDeniedException
    {
        String strIdDirectory = request.getParameter( PARAMETER_ID_DIRECTORY );
        int nIdDirectory = DirectoryUtils.convertStringToInt( strIdDirectory );
        Directory directory = DirectoryHome.findByPrimaryKey( nIdDirectory, getPlugin(  ) );
        String strIdDirectoryXsl = request.getParameter( PARAMETER_ID_DIRECTORY_XSL );
        int nIdDirectoryXsl = DirectoryUtils.convertStringToInt( strIdDirectoryXsl );
        WorkflowService workflowService = WorkflowService.getInstance(  );
        boolean bWorkflowServiceEnable = workflowService.isAvailable(  );
        String strShotExportFinalOutPut = null;

        // -----------------------------------------------------------------------
        DirectoryXsl directoryXsl = DirectoryXslHome.findByPrimaryKey( nIdDirectoryXsl, getPlugin(  ) );
        String strFileExtension = directoryXsl.getExtension(  );
        String strFileName = directory.getTitle(  ) + "." + strFileExtension;
        strFileName = StringUtil.replaceAccent( strFileName ).replace( " ", "_" );

        boolean bIsCsvExport = strFileExtension.equals( EXPORT_CSV_EXT );
        boolean bDisplayDateCreation = directory.isDateShownInExport(  );

        if ( ( directory == null ) || ( directoryXsl == null ) ||
                !RBACService.isAuthorized( Directory.RESOURCE_TYPE, strIdDirectory,
                    DirectoryResourceIdService.PERMISSION_MANAGE_RECORD, getUser(  ) ) )
        {
            throw new AccessDeniedException(  );
        }

        List<Integer> listResultRecordId = new ArrayList<Integer>(  );

        if ( request.getParameter( PARAMETER_EXPORT_SEARCH_RESULT ) != null )
        {
        	// sort order and sort entry are not needed in export
            listResultRecordId = getListResults( request, directory, bWorkflowServiceEnable, true, null, RecordFieldFilter.ORDER_NONE );
        }
        else
        {
        	// sort order and sort entry are not needed in export
            listResultRecordId = getListResults( request, directory, bWorkflowServiceEnable, false, null, RecordFieldFilter.ORDER_NONE );
        }

        EntryFilter entryFilter = new EntryFilter(  );
        entryFilter.setIdDirectory( directory.getIdDirectory(  ) );
        entryFilter.setIsGroup( EntryFilter.FILTER_FALSE );
        entryFilter.setIsComment( EntryFilter.FILTER_FALSE );
        entryFilter.setIsShownInExport( EntryFilter.FILTER_TRUE );

        List<IEntry> listEntryResultSearch = EntryHome.getEntryList( entryFilter, getPlugin(  ) );
        StringBuffer strBufferListRecordXml = null;

        java.io.File tmpFile = null;
        BufferedWriter bufferedWriter = null;

        File fileTemplate = null;
        String strFileOutPut = DirectoryUtils.EMPTY_STRING;

        if ( directoryXsl.getFile(  ) != null )
        {
            fileTemplate = FileHome.findByPrimaryKey( directoryXsl.getFile(  ).getIdFile(  ), getPlugin(  ) );
        }

        XmlTransformerService xmlTransformerService = null;
        PhysicalFile physicalFile = null;
        String strXslId = null;

        if ( ( fileTemplate != null ) && ( fileTemplate.getPhysicalFile(  ) != null ) )
        {
            fileTemplate.setPhysicalFile( PhysicalFileHome.findByPrimaryKey( 
                    fileTemplate.getPhysicalFile(  ).getIdPhysicalFile(  ), getPlugin(  ) ) );

            xmlTransformerService = new XmlTransformerService(  );
            physicalFile = fileTemplate.getPhysicalFile(  );
            strXslId = XSL_UNIQUE_PREFIX_ID + physicalFile.getIdPhysicalFile(  );
        }

        int nSize = listResultRecordId.size(  );
        boolean bIsBigExport = ( nSize > EXPORT_RECORD_STEP );

        if ( bIsBigExport )
        {
            try
            {
                String strPath = AppPathService.getWebAppPath(  ) +
                    AppPropertiesService.getProperty( PROPERTY_PATH_TMP );
                java.io.File tmpDir = new java.io.File( strPath );
                tmpFile = java.io.File.createTempFile( EXPORT_TMPFILE_PREFIX, EXPORT_TMPFILE_SUFIX, tmpDir );
            }
            catch ( IOException e )
            {
                AppLogService.error( "Unable to create temp file in webapp tmp dir" );

                try
                {
                    tmpFile = java.io.File.createTempFile( EXPORT_TMPFILE_PREFIX, EXPORT_TMPFILE_SUFIX );
                }
                catch ( IOException e1 )
                {
                    AppLogService.error( e1 );
                }
            }

            try
            {
                tmpFile.deleteOnExit(  );
                bufferedWriter = new BufferedWriter( new FileWriter( tmpFile, true ) );
            }
            catch ( IOException e )
            {
                AppLogService.error( e );
            }
        }

        Plugin plugin = this.getPlugin(  );
        Locale locale = request.getLocale(  );

        // ---------------------------------------------------------------------
        StringBuffer strBufferListEntryXml = new StringBuffer(  );

        if ( bDisplayDateCreation && bIsCsvExport )
        {
            HashMap<String, String> model = new HashMap<String, String>(  );
            model.put( Entry.ATTRIBUTE_ENTRY_ID, "0" );
            XmlUtil.beginElement( strBufferListEntryXml, Entry.TAG_ENTRY, model );

            String strDateCreation = I18nService.getLocalizedString( PROPERTY_ENTRY_TYPE_DATE_CREATION_TITLE, locale );
            XmlUtil.addElementHtml( strBufferListEntryXml, Entry.TAG_TITLE, strDateCreation );
            XmlUtil.endElement( strBufferListEntryXml, Entry.TAG_ENTRY );
        }

        for ( IEntry entry : listEntryResultSearch )
        {
            entry.getXml( plugin, locale, strBufferListEntryXml );
        }

        HashMap<String, String> model = new HashMap<String, String>(  );

        if ( ( directory.getIdWorkflow(  ) != DirectoryUtils.CONSTANT_ID_NULL ) && bWorkflowServiceEnable )
        {
            model.put( TAG_DISPLAY, TAG_YES );
        }
        else
        {
            model.put( TAG_DISPLAY, TAG_NO );
        }

        XmlUtil.addEmptyElement( strBufferListEntryXml, TAG_STATUS, model );

        StringBuilder strBufferDirectoryXml = new StringBuilder(  );
        strBufferDirectoryXml.append( XmlUtil.getXmlHeader(  ) );

        if ( bIsBigExport )
        {
            strBufferDirectoryXml.append( directory.getXml( plugin, locale, new StringBuffer(  ), strBufferListEntryXml ) );

            strBufferListRecordXml = new StringBuffer( EXPORT_STRINGBUFFER_INITIAL_SIZE );

            strFileOutPut = xmlTransformerService.transformBySourceWithXslCache( strBufferDirectoryXml.toString(  ),
                    physicalFile.getValue(  ), strXslId, null, null );

            String strFinalOutPut = null;

            if ( !bIsCsvExport )
            {
                int pos = strFileOutPut.indexOf( EXPORT_XSL_EMPTY_LIST_RECORD );
                strFinalOutPut = strFileOutPut.substring( 0, pos ) + EXPORT_XSL_BEGIN_LIST_RECORD;
            }
            else
            {
                strFinalOutPut = strFileOutPut;
            }

            try
            {
                bufferedWriter.write( strFinalOutPut );
            }
            catch ( IOException e )
            {
                AppLogService.error( e );
            }
        }
        else
        {
            strBufferListRecordXml = new StringBuffer(  );
        }

        // -----------------------------------------------------------------------
        List<Integer> nTmpListId = new ArrayList<Integer>(  );
        int idWorflow = directory.getIdWorkflow(  );

        if ( bIsBigExport )
        {
            int nXmlHeaderLength = XmlUtil.getXmlHeader(  ).length(  ) - 1;
            int max = nSize / EXPORT_RECORD_STEP;
            int max1 = nSize - EXPORT_RECORD_STEP;

            for ( int i = 0; i < max1; i += EXPORT_RECORD_STEP )
            {
                AppLogService.debug( "Directory export progress : " + ( ( (float) i / nSize ) * 100 ) + "%" );

                nTmpListId = new ArrayList<Integer>(  );

                int k = i + EXPORT_RECORD_STEP;

                for ( int j = i; j < k; j++ )
                {
                    nTmpListId.add( listResultRecordId.get( j ) );
                }

                List<Record> nTmpListRecords = RecordHome.loadListByListId( nTmpListId, plugin );

                for ( Record record : nTmpListRecords )
                {
                    State state = workflowService.getState( record.getIdRecord(  ), Record.WORKFLOW_RESOURCE_TYPE,
                            idWorflow, Integer.valueOf( directory.getIdDirectory(  ) ), null );
                    strBufferListRecordXml.append( record.getXml( plugin, locale, false, state, listEntryResultSearch,
                            false, false, true, bDisplayDateCreation ) );
                }

                strBufferListRecordXml = this.appendPartialContent( strBufferListRecordXml, bufferedWriter,
                        physicalFile, bIsCsvExport, strXslId, nXmlHeaderLength, xmlTransformerService );
            }

            // -----------------------------------------------------------------------
            int max2 = EXPORT_RECORD_STEP * max;
            nTmpListId = new ArrayList<Integer>(  );

            for ( int i = max2; i < nSize; i++ )
            {
                nTmpListId.add( listResultRecordId.get( ( i ) ) );
            }

            List<Record> nTmpListRecords = RecordHome.loadListByListId( nTmpListId, plugin );

            for ( Record record : nTmpListRecords )
            {
                State state = workflowService.getState( record.getIdRecord(  ), Record.WORKFLOW_RESOURCE_TYPE,
                        idWorflow, Integer.valueOf( directory.getIdDirectory(  ) ), null );

                if ( bIsCsvExport )
                {
                    strBufferListRecordXml.append( record.getXmlForCsvExport( plugin, locale, false, state,
                            listEntryResultSearch, false, false, true, bDisplayDateCreation ) );
                }
                else
                {
                    strBufferListRecordXml.append( record.getXml( plugin, locale, false, state, listEntryResultSearch,
                            false, false, true, bDisplayDateCreation ) );
                }
            }

            strBufferListRecordXml = this.appendPartialContent( strBufferListRecordXml, bufferedWriter, physicalFile,
                    bIsCsvExport, strXslId, nXmlHeaderLength, xmlTransformerService );

            strBufferListRecordXml.insert( 0, EXPORT_XSL_BEGIN_PARTIAL_EXPORT );
            strBufferListRecordXml.insert( 0, XmlUtil.getXmlHeader(  ) );
            strBufferListRecordXml.append( EXPORT_XSL_END_PARTIAL_EXPORT );
            strFileOutPut = xmlTransformerService.transformBySourceWithXslCache( strBufferListRecordXml.toString(  ),
                    physicalFile.getValue(  ), strXslId, null, null );

            try
            {
                if ( bIsCsvExport )
                {
                    bufferedWriter.write( strFileOutPut );
                }
                else
                {
                    bufferedWriter.write( strFileOutPut.substring( nXmlHeaderLength ) );
                    bufferedWriter.write( EXPORT_XSL_END_LIST_RECORD + EXPORT_XSL_NEW_LINE + EXPORT_XSL_END_DIRECTORY );
                }

                bufferedWriter.flush(  );
                bufferedWriter.close(  );
            }
            catch ( IOException e )
            {
                AppLogService.error( e );
            }
        }
        else
        {
            List<Record> nTmpListRecords = RecordHome.loadListByListId( listResultRecordId, plugin );

            for ( Record record : nTmpListRecords )
            {
                State state = workflowService.getState( record.getIdRecord(  ), Record.WORKFLOW_RESOURCE_TYPE,
                        idWorflow, Integer.valueOf( directory.getIdDirectory(  ) ), null );

                if ( bIsCsvExport )
                {
                    strBufferListRecordXml.append( record.getXmlForCsvExport( plugin, locale, false, state,
                            listEntryResultSearch, false, false, true, bDisplayDateCreation ) );
                }
                else
                {
                    strBufferListRecordXml.append( record.getXml( plugin, locale, false, state, listEntryResultSearch,
                            false, false, true, bDisplayDateCreation ) );
                }
            }

            strBufferDirectoryXml.append( directory.getXml( plugin, locale, strBufferListRecordXml,
                    strBufferListEntryXml ) );
            strShotExportFinalOutPut = xmlTransformerService.transformBySourceWithXslCache( strBufferDirectoryXml.toString(  ),
                    physicalFile.getValue(  ), strXslId, null, null );
        }

        // ----------------------------------------------------------------------- 
        DirectoryUtils.addHeaderResponse( request, response, strFileName );

        if ( bIsCsvExport )
        {
            response.setContentType( CONSTANT_MIME_TYPE_CSV );
        }
        else
        {
            String strMimeType = FileSystemUtil.getMIMEType( strFileName );

            if ( strMimeType != null )
            {
                response.setContentType( strMimeType );
            }
            else
            {
                response.setContentType( CONSTANT_MIME_TYPE_OCTETSTREAM );
            }
        }

        if ( bIsBigExport )
        {
            FileChannel in = null;
            WritableByteChannel writeChannelOut = null;

            try
            {
                in = new FileInputStream( tmpFile ).getChannel(  );
                writeChannelOut = Channels.newChannel( response.getOutputStream(  ) );
                response.setContentLength( Long.valueOf( in.size(  ) ).intValue(  ) );
                in.transferTo( 0, in.size(  ), writeChannelOut );
                response.getOutputStream(  ).close(  );
            }
            catch ( IOException e )
            {
                AppLogService.error( e );
            }
            finally
            {
                if ( in != null )
                {
                    try
                    {
                        in.close(  );
                    }
                    catch ( IOException e )
                    {
                    }
                }

                tmpFile.delete(  );
            }
        }
        else
        {
            byte[] bResult = strShotExportFinalOutPut.getBytes(  );

            try
            {
                response.setContentLength( bResult.length );
                response.getOutputStream(  ).write( bResult );
                response.getOutputStream(  ).close(  );
            }
            catch ( IOException e )
            {
                AppLogService.error( e );
            }
        }
    }

    /**
     * Return the interface for import directory record
     * @param request The Http request
     * @throws AccessDeniedException the {@link AccessDeniedException}
     * @return Html directory
     *
     */
    public String getImportDirectoryRecord( HttpServletRequest request )
        throws AccessDeniedException
    {
        String strIdDirectory = request.getParameter( PARAMETER_ID_DIRECTORY );

        int nIdDirectory = DirectoryUtils.convertStringToInt( strIdDirectory );
        Directory directory = DirectoryHome.findByPrimaryKey( nIdDirectory, getPlugin(  ) );

        if ( ( directory == null ) ||
                !RBACService.isAuthorized( Directory.RESOURCE_TYPE, strIdDirectory,
                    DirectoryResourceIdService.PERMISSION_IMPORT_RECORD, getUser(  ) ) )
        {
            throw new AccessDeniedException(  );
        }

        HashMap model = new HashMap(  );

        if ( request.getParameter( PARAMETER_SESSION ) != null )
        {
            if ( _strError != null )
            {
                model.put( MARK_STR_ERROR, _strError.toString(  ) );
            }

            model.put( MARK_NUMBER_LINES_ERROR, _nCountLineFailure );
            model.put( MARK_NUMBER_LINES_IMPORTED, _nCountLine - _nCountLineFailure );
            model.put( MARK_FINISH_IMPORT, true );
            _nCountLine = 0;
            _nCountLineFailure = 0;
            _strError = null;

            _strError = null;
        }

        model.put( MARK_LOCALE, getLocale(  ) );
        model.put( MARK_DIRECTORY, directory );
        setPageTitleProperty( PROPERTY_IMPORT_DIRECTORY_RECORD_PAGE_TITLE );

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_IMPORT_DIRECTORY_RECORD, getLocale(  ),
                model );

        return getAdminPage( templateList.getHtml(  ) );
    }

    /**
     * ImportDirectory record
     * @param request the Http Request
     * @throws AccessDeniedException the {@link AccessDeniedException}
     * @return The URL to go after performing the action
     */
    public String doImportDirectoryRecord( HttpServletRequest request )
        throws AccessDeniedException
    {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        FileItem fileItem = multipartRequest.getFile( PARAMETER_FILE_IMPORT );
        String strMimeType = FileSystemUtil.getMIMEType( FileUploadService.getFileNameOnly( fileItem ) );

        if ( ( fileItem == null ) || ( fileItem.getName(  ) == null ) ||
                DirectoryUtils.EMPTY_STRING.equals( fileItem.getName(  ) ) )
        {
            Object[] tabRequiredFields = { I18nService.getLocalizedString( FIELD_FILE_IMPORT, getLocale(  ) ) };

            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields,
                AdminMessage.TYPE_STOP );
        }

        if ( ( !strMimeType.equals( CONSTANT_MIME_TYPE_CSV ) && !strMimeType.equals( CONSTANT_MIME_TYPE_OCTETSTREAM ) &&
                !strMimeType.equals( CONSTANT_MIME_TYPE_TEXT_CSV ) ) ||
                !fileItem.getName(  ).toLowerCase(  ).endsWith( CONSTANT_EXTENSION_CSV_FILE ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_CSV_FILE_IMPORT, AdminMessage.TYPE_STOP );
        }

        String strIdDirectory = request.getParameter( PARAMETER_ID_DIRECTORY );
        int nIdDirectory = DirectoryUtils.convertStringToInt( strIdDirectory );
        Directory directory = DirectoryHome.findByPrimaryKey( nIdDirectory, getPlugin(  ) );

        if ( ( directory == null ) ||
                !RBACService.isAuthorized( Directory.RESOURCE_TYPE, strIdDirectory,
                    DirectoryResourceIdService.PERMISSION_MANAGE_RECORD, getUser(  ) ) )
        {
            throw new AccessDeniedException(  );
        }

        Character strCsvSeparator = AppPropertiesService.getProperty( PROPERTY_IMPORT_CSV_DELIMITER ).charAt( 0 );
        _strError = new StringBuffer(  );

        EntryFilter filter = new EntryFilter(  );
        filter.setIdDirectory( nIdDirectory );
        filter.setIsComment( EntryFilter.FILTER_FALSE );
        filter.setIsEntryParentNull( EntryFilter.FILTER_TRUE );

        List<IEntry> listEntry = new ArrayList<IEntry>(  );

        List<IEntry> listEntryFirstLevel = EntryHome.getEntryList( filter, getPlugin(  ) );

        filter.setIsEntryParentNull( EntryFilter.ALL_INT );

        for ( IEntry entry : listEntryFirstLevel )
        {
            if ( !entry.getEntryType(  ).getGroup(  ) )
            {
                listEntry.add( EntryHome.findByPrimaryKey( entry.getIdEntry(  ), getPlugin(  ) ) );
            }

            filter.setIdEntryParent( entry.getIdEntry(  ) );

            List<IEntry> listChildren = EntryHome.getEntryList( filter, getPlugin(  ) );

            for ( IEntry entryChild : listChildren )
            {
                listEntry.add( EntryHome.findByPrimaryKey( entryChild.getIdEntry(  ), getPlugin(  ) ) );
            }
        }

        Object[] tabEntry = listEntry.toArray(  );

        try
        {
            InputStreamReader inputStreamReader = new InputStreamReader( fileItem.getInputStream(  ) );
            CSVReader csvReader = new CSVReader( inputStreamReader, strCsvSeparator, '\"' );

            String[] nextLine;

            _nCountLine = 0;
            _nCountLineFailure = 0;

            while ( ( nextLine = csvReader.readNext(  ) ) != null )
            {
                _nCountLine++;

                if ( nextLine.length != tabEntry.length )
                {
                    _strError.append( I18nService.getLocalizedString( PROPERTY_LINE, getLocale(  ) ) );
                    _strError.append( _nCountLine );
                    _strError.append( " > " );
                    _strError.append( I18nService.getLocalizedString( MESSAGE_ERROR_CSV_NUMBER_SEPARATOR, getLocale(  ) ) );
                    _strError.append( "<br/>" );
                    _nCountLineFailure++;
                }
                else
                {
                    Record record = new Record(  );
                    record.setDirectory( directory );

                    List<RecordField> listRecordField = new ArrayList<RecordField>(  );

                    try
                    {
                        for ( int i = 0; i < nextLine.length; i++ )
                        {
                            ( (IEntry) tabEntry[i] ).getImportRecordFieldData( record, nextLine[i], true,
                                listRecordField, getLocale(  ) );
                        }

                        record.setListRecordField( listRecordField );
                        record.setDateCreation( DirectoryUtils.getCurrentTimestamp(  ) );
                        //Autopublication
                        record.setEnabled( true );
                        RecordHome.create( record, getPlugin(  ) );
                    }
                    catch ( DirectoryErrorException error )
                    {
                        _strError.append( I18nService.getLocalizedString( PROPERTY_LINE, getLocale(  ) ) );
                        _strError.append( _nCountLine );
                        _strError.append( " > " );

                        if ( error.isMandatoryError(  ) )
                        {
                            Object[] tabRequiredFields = { error.getTitleField(  ) };
                            _strError.append( I18nService.getLocalizedString( MESSAGE_DIRECTORY_ERROR_MANDATORY_FIELD,
                                    tabRequiredFields, getLocale(  ) ) );
                        }
                        else
                        {
                            Object[] tabRequiredFields = { error.getTitleField(  ), error.getErrorMessage(  ) };
                            _strError.append( I18nService.getLocalizedString( MESSAGE_DIRECTORY_ERROR,
                                    tabRequiredFields, getLocale(  ) ) );
                        }

                        _strError.append( "<br/>" );
                        _nCountLineFailure++;
                    }
                }
            }
        }

        catch ( IOException e )
        {
            AppLogService.error( e );
        }

        return getJspImportDirectoryRecord( request, nIdDirectory );
    }

    /**
     * Return the interface for index all directory
     * @param request The Http request
     * @throws AccessDeniedException the {@link AccessDeniedException}
     * @return Html directory
     */
    public String getIndexAllDirectory( HttpServletRequest request )
        throws AccessDeniedException
    {
        if ( !RBACService.isAuthorized( Directory.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID,
                    DirectoryResourceIdService.PERMISSION_INDEX_ALL_DIRECTORY, getUser(  ) ) )
        {
            throw new AccessDeniedException(  );
        }

        Map<String, Object> model = new HashMap<String, Object>(  );

        model.put( MARK_LOCALE, getLocale(  ) );

        setPageTitleProperty( PROPERTY_INDEX_ALL_DIRECTORY_PAGE_TITLE );

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_INDEX_ALL_DIRECTORY, getLocale(  ), model );

        return getAdminPage( templateList.getHtml(  ) );
    }

    /**
     * Gets the confirmation page of indexing all directory
     * @param request The HTTP request
     * @throws AccessDeniedException the {@link AccessDeniedException}
     * @return the confirmation page of delete directory record
     */
    public String getConfirmIndexAllDirectory( HttpServletRequest request )
        throws AccessDeniedException
    {
        if ( !RBACService.isAuthorized( Directory.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID,
                    DirectoryResourceIdService.PERMISSION_INDEX_ALL_DIRECTORY, getUser(  ) ) )
        {
            throw new AccessDeniedException(  );
        }

        UrlItem url = new UrlItem( JSP_DO_INDEX_ALL_DIRECTORY );

        return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_INDEX_ALL_DIRECTORY, url.getUrl(  ),
            AdminMessage.TYPE_CONFIRMATION );
    }

    /**
     * Start indexing
     * @param request The HTTP request
     * @throws AccessDeniedException the {@link AccessDeniedException}
     * @return The URL to go after performing the action
     */
    public String doIndexAllDirectory( HttpServletRequest request )
        throws AccessDeniedException
    {
        if ( !RBACService.isAuthorized( Directory.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID,
                    DirectoryResourceIdService.PERMISSION_INDEX_ALL_DIRECTORY, getUser(  ) ) )
        {
            throw new AccessDeniedException(  );
        }

        if ( request.getParameter( PARAMETER_CANCEL ) == null )
        {
            DirectorySearchService.getInstance(  ).processIndexing( true );
        }

        return getHomeUrl( request );
    }

    /**
     * Return management of directory record ( list of directory record )
     * @param request The Http request
     * @throws AccessDeniedException the {@link AccessDeniedException}
     * @return Html directory
     */
    public String getCreateDirectoryRecord( HttpServletRequest request )
        throws AccessDeniedException
    {
        String strIdDirectory = request.getParameter( PARAMETER_ID_DIRECTORY );
        int nIdDirectory = DirectoryUtils.convertStringToInt( strIdDirectory );
        Directory directory = DirectoryHome.findByPrimaryKey( nIdDirectory, getPlugin(  ) );

        if ( ( directory == null ) ||
                !RBACService.isAuthorized( Directory.RESOURCE_TYPE, strIdDirectory,
                    DirectoryResourceIdService.PERMISSION_CREATE_RECORD, getUser(  ) ) )
        {
            throw new AccessDeniedException(  );
        }

        Map<String, Object> model = new HashMap(  );

        List<IEntry> listEntry = DirectoryUtils.getFormEntries( nIdDirectory, getPlugin(  ), getUser(  ) );
        model.put( MARK_ENTRY_LIST, listEntry );

        if ( SecurityService.isAuthenticationEnable(  ) )
        {
            model.put( MARK_ROLE_REF_LIST, RoleHome.getRolesList(  ) );
        }

        model.put( MARK_DIRECTORY, directory );
        model.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
        model.put( MARK_LOCALE, getLocale(  ) );
        setPageTitleProperty( PROPERTY_CREATE_DIRECTORY_RECORD_PAGE_TITLE );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CREATE_DIRECTORY_RECORD, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Perform the directory record creation
     * @param request The HTTP request
     * @throws AccessDeniedException the {@link AccessDeniedException}
     * @return The URL to go after performing the action
     */
    public String doCreateDirectoryRecord( HttpServletRequest request )
        throws AccessDeniedException
    {
        String strIdDirectory = request.getParameter( PARAMETER_ID_DIRECTORY );
        int nIdDirectory = DirectoryUtils.convertStringToInt( strIdDirectory );
        Directory directory = DirectoryHome.findByPrimaryKey( nIdDirectory, getPlugin(  ) );

        if ( ( directory == null ) ||
                !RBACService.isAuthorized( Directory.RESOURCE_TYPE, strIdDirectory,
                    DirectoryResourceIdService.PERMISSION_CREATE_RECORD, getUser(  ) ) )
        {
            throw new AccessDeniedException(  );
        }

        if ( request.getParameter( PARAMETER_CANCEL ) == null )
        {
            Record record = new Record(  );
            record.setDirectory( directory );

            try
            {
                DirectoryUtils.getDirectoryRecordData( request, record, getPlugin(  ), getLocale(  ) );
            }
            catch ( DirectoryErrorException error )
            {
                String strErrorMessage = DirectoryUtils.EMPTY_STRING;

                if ( error.isMandatoryError(  ) )
                {
                    Object[] tabRequiredFields = { error.getTitleField(  ) };
                    strErrorMessage = AdminMessageService.getMessageUrl( request,
                            MESSAGE_DIRECTORY_ERROR_MANDATORY_FIELD, tabRequiredFields, AdminMessage.TYPE_STOP );
                }
                else
                {
                    Object[] tabRequiredFields = { error.getTitleField(  ), error.getErrorMessage(  ) };
                    strErrorMessage = AdminMessageService.getMessageUrl( request, MESSAGE_DIRECTORY_ERROR,
                            tabRequiredFields, AdminMessage.TYPE_STOP );
                }

                return strErrorMessage;
            }

            record.setDateCreation( DirectoryUtils.getCurrentTimestamp(  ) );
            //Autopublication
            record.setEnabled( directory.isRecordActivated(  ) );
            RecordHome.create( record, getPlugin(  ) );

            if ( WorkflowService.getInstance(  ).isAvailable(  ) &&
                    ( directory.getIdWorkflow(  ) != DirectoryUtils.CONSTANT_ID_NULL ) )
            {
                WorkflowService.getInstance(  )
                               .getState( record.getIdRecord(  ), Record.WORKFLOW_RESOURCE_TYPE,
                    directory.getIdWorkflow(  ), Integer.valueOf( directory.getIdDirectory(  ) ), getUser(  ) );
                WorkflowService.getInstance(  )
                               .executeActionAutomatic( record.getIdRecord(  ), Record.WORKFLOW_RESOURCE_TYPE,
                    directory.getIdWorkflow(  ), Integer.valueOf( directory.getIdDirectory(  ) ) );
            }
        }

        return getJspManageDirectoryRecord( request, nIdDirectory );
    }

    /**
     * Return management of directory record ( list of directory record )
     * @param request The Http request
     * @throws AccessDeniedException the {@link AccessDeniedException}
     * @return Html directory
     */
    public String getModifyDirectoryRecord( HttpServletRequest request )
        throws AccessDeniedException
    {
        String strIdDirectoryRecord = request.getParameter( PARAMETER_ID_DIRECTORY_RECORD );
        int nIdDirectoryRecord = DirectoryUtils.convertStringToInt( strIdDirectoryRecord );
        Record record = RecordHome.findByPrimaryKey( nIdDirectoryRecord, getPlugin(  ) );

        if ( ( record == null ) ||
                !RBACService.isAuthorized( Directory.RESOURCE_TYPE,
                    record.getDirectory(  ).getIdDirectory(  ) + DirectoryUtils.EMPTY_STRING,
                    DirectoryResourceIdService.PERMISSION_MODIFY_RECORD, getUser(  ) ) )
        {
            throw new AccessDeniedException(  );
        }

        Directory directory = DirectoryHome.findByPrimaryKey( record.getDirectory(  ).getIdDirectory(  ), getPlugin(  ) );

        Map<String, Object> model = new HashMap<String, Object>(  );

        List<IEntry> listEntry = DirectoryUtils.getFormEntries( record.getDirectory(  ).getIdDirectory(  ),
                getPlugin(  ), getUser(  ) );

        model.put( MARK_ENTRY_LIST, listEntry );
        model.put( MARK_MAP_ID_ENTRY_LIST_RECORD_FIELD,
            DirectoryUtils.getMapIdEntryListRecordField( listEntry, nIdDirectoryRecord, getPlugin(  ) ) );
        model.put( MARK_DIRECTORY, directory );

        if ( SecurityService.isAuthenticationEnable(  ) )
        {
            model.put( MARK_ROLE_REF_LIST, RoleHome.getRolesList(  ) );
        }

        model.put( MARK_DIRECTORY_RECORD, record );
        model.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
        model.put( MARK_LOCALE, getLocale(  ) );
        setPageTitleProperty( PROPERTY_MODIFY_DIRECTORY_RECORD_PAGE_TITLE );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MODIFY_DIRECTORY_RECORD, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Perform the directory record creation
     * @param request The HTTP request
     * @throws AccessDeniedException the {@link AccessDeniedException}
     * @return The URL to go after performing the action
     */
    public String doModifyDirectoryRecord( HttpServletRequest request )
        throws AccessDeniedException
    {
        String strIdDirectoryRecord = request.getParameter( PARAMETER_ID_DIRECTORY_RECORD );
        int nIdDirectoryRecord = DirectoryUtils.convertStringToInt( strIdDirectoryRecord );
        Record record = RecordHome.findByPrimaryKey( nIdDirectoryRecord, getPlugin(  ) );

        if ( ( record == null ) ||
                !RBACService.isAuthorized( Directory.RESOURCE_TYPE,
                    record.getDirectory(  ).getIdDirectory(  ) + DirectoryUtils.EMPTY_STRING,
                    DirectoryResourceIdService.PERMISSION_MODIFY_RECORD, getUser(  ) ) )
        {
            throw new AccessDeniedException(  );
        }

        if ( request.getParameter( PARAMETER_CANCEL ) == null )
        {
            try
            {
                DirectoryUtils.getDirectoryRecordData( request, record, getPlugin(  ), getLocale(  ) );
            }
            catch ( DirectoryErrorException error )
            {
                String strErrorMessage = DirectoryUtils.EMPTY_STRING;

                if ( error.isMandatoryError(  ) )
                {
                    Object[] tabRequiredFields = { error.getTitleField(  ) };
                    strErrorMessage = AdminMessageService.getMessageUrl( request,
                            MESSAGE_DIRECTORY_ERROR_MANDATORY_FIELD, tabRequiredFields, AdminMessage.TYPE_STOP );
                }
                else
                {
                    Object[] tabRequiredFields = { error.getTitleField(  ), error.getErrorMessage(  ) };
                    strErrorMessage = AdminMessageService.getMessageUrl( request, MESSAGE_DIRECTORY_ERROR,
                            tabRequiredFields, AdminMessage.TYPE_STOP );
                }

                return strErrorMessage;
            }

            RecordHome.updateWidthRecordField( record, getPlugin(  ) );
        }

        return getJspManageDirectoryRecord( request, record.getDirectory(  ).getIdDirectory(  ) );
    }

    /**
     * Gets the confirmation page of delete directory record
     * @param request The HTTP request
     * @throws AccessDeniedException the {@link AccessDeniedException}
     * @return the confirmation page of delete directory record
     */
    public String getConfirmRemoveDirectoryRecord( HttpServletRequest request )
        throws AccessDeniedException
    {
        String strIdDirectoryRecord = request.getParameter( PARAMETER_ID_DIRECTORY_RECORD );
        int nIdDirectoryRecord = DirectoryUtils.convertStringToInt( strIdDirectoryRecord );
        Record record = RecordHome.findByPrimaryKey( nIdDirectoryRecord, getPlugin(  ) );

        if ( ( record == null ) ||
                !RBACService.isAuthorized( Directory.RESOURCE_TYPE,
                    Integer.toString( record.getDirectory(  ).getIdDirectory(  ) ),
                    DirectoryResourceIdService.PERMISSION_DELETE_RECORD, getUser(  ) ) )
        {
            throw new AccessDeniedException(  );
        }

        UrlItem url = new UrlItem( JSP_DO_REMOVE_DIRECTORY_RECORD );
        url.addParameter( PARAMETER_ID_DIRECTORY_RECORD, nIdDirectoryRecord );

        return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_DIRECTORY_RECORD, url.getUrl(  ),
            AdminMessage.TYPE_CONFIRMATION );
    }

    /**
     * Perform the directory record supression
     * @param request The HTTP request
     * @throws AccessDeniedException the {@link AccessDeniedException}
     * @return The URL to go after performing the action
     */
    public String doRemoveDirectoryRecord( HttpServletRequest request )
        throws AccessDeniedException
    {
        String strIdDirectoryRecord = request.getParameter( PARAMETER_ID_DIRECTORY_RECORD );
        ArrayList<String> listErrors = new ArrayList<String>(  );
        int nIdDirectoryRecord = DirectoryUtils.convertStringToInt( strIdDirectoryRecord );
        Record record = RecordHome.findByPrimaryKey( nIdDirectoryRecord, getPlugin(  ) );

        if ( ( record == null ) ||
                !RBACService.isAuthorized( Directory.RESOURCE_TYPE,
                    Integer.toString( record.getDirectory(  ).getIdDirectory(  ) ),
                    DirectoryResourceIdService.PERMISSION_DELETE_RECORD, getUser(  ) ) )
        {
            throw new AccessDeniedException(  );
        }

        if ( !RecordRemovalListenerService.getService(  )
                                              .checkForRemoval( strIdDirectoryRecord, listErrors, getLocale(  ) ) )
        {
            String strCause = AdminMessageService.getFormattedList( listErrors, getLocale(  ) );
            Object[] args = { strCause };

            return AdminMessageService.getMessageUrl( request, MESSAGE_CANNOT_REMOVE_RECORD, args,
                AdminMessage.TYPE_STOP );
        }

        RecordHome.remove( nIdDirectoryRecord, getPlugin(  ) );
        WorkflowService.getInstance(  ).doRemoveWorkFlowResource( nIdDirectoryRecord, Record.WORKFLOW_RESOURCE_TYPE );

        return getJspManageDirectoryRecord( request, record.getDirectory(  ).getIdDirectory(  ) );
    }

    /**
     * copy the directory whose key is specified in the Http request
     * @param request The HTTP request
     * @throws AccessDeniedException the {@link AccessDeniedException}
     * @return The URL to go after performing the action
     */
    public String doCopyDirectoryRecord( HttpServletRequest request )
        throws AccessDeniedException
    {
        String strIdDirectoryRecord = request.getParameter( PARAMETER_ID_DIRECTORY_RECORD );
        int nIdDirectoryRecord = DirectoryUtils.convertStringToInt( strIdDirectoryRecord );
        Record record = RecordHome.findByPrimaryKey( nIdDirectoryRecord, getPlugin(  ) );

        if ( ( record == null ) ||
                !RBACService.isAuthorized( Directory.RESOURCE_TYPE,
                    Integer.toString( record.getDirectory(  ).getIdDirectory(  ) ),
                    DirectoryResourceIdService.PERMISSION_COPY_RECORD, getUser(  ) ) )
        {
            throw new AccessDeniedException(  );
        }

        record.setDateCreation( DirectoryUtils.getCurrentTimestamp(  ) );
        RecordHome.copy( record, getPlugin(  ) );

        Directory directory = DirectoryHome.findByPrimaryKey( record.getDirectory(  ).getIdDirectory(  ), getPlugin(  ) );

        if ( WorkflowService.getInstance(  ).isAvailable(  ) &&
                ( directory.getIdWorkflow(  ) != DirectoryUtils.CONSTANT_ID_NULL ) )
        {
            WorkflowService.getInstance(  )
                           .getState( record.getIdRecord(  ), Record.WORKFLOW_RESOURCE_TYPE,
                directory.getIdWorkflow(  ), Integer.valueOf( directory.getIdDirectory(  ) ), getUser(  ) );
            WorkflowService.getInstance(  )
                           .executeActionAutomatic( record.getIdRecord(  ), Record.WORKFLOW_RESOURCE_TYPE,
                directory.getIdWorkflow(  ), Integer.valueOf( directory.getIdDirectory(  ) ) );
        }

        return getJspManageDirectoryRecord( request, record.getDirectory(  ).getIdDirectory(  ) );
    }

    /**
     * Gets the confirmation page of disable directory record
     * @param request The HTTP request
     * @throws AccessDeniedException the {@link AccessDeniedException}
     * @return the confirmation page of disable directory record
     */
    public String getConfirmDisableDirectoryRecord( HttpServletRequest request )
        throws AccessDeniedException
    {
        String strIdDirectoryRecord = request.getParameter( PARAMETER_ID_DIRECTORY_RECORD );
        int nIdDirectoryRecord = DirectoryUtils.convertStringToInt( strIdDirectoryRecord );
        Record record = RecordHome.findByPrimaryKey( nIdDirectoryRecord, getPlugin(  ) );
        String strMessage;

        if ( ( record == null ) ||
                !RBACService.isAuthorized( Directory.RESOURCE_TYPE,
                    Integer.toString( record.getDirectory(  ).getIdDirectory(  ) ),
                    DirectoryResourceIdService.PERMISSION_CHANGE_STATE_RECORD, getUser(  ) ) )
        {
            throw new AccessDeniedException(  );
        }

        strMessage = MESSAGE_CONFIRM_DISABLE_DIRECTORY_RECORD;

        UrlItem url = new UrlItem( JSP_DO_DISABLE_DIRECTORY_RECORD );
        url.addParameter( PARAMETER_ID_DIRECTORY_RECORD, strIdDirectoryRecord );

        return AdminMessageService.getMessageUrl( request, strMessage, url.getUrl(  ), AdminMessage.TYPE_CONFIRMATION );
    }

    /**
     * Perform disable directory record
     * @param request The HTTP request
     * @throws AccessDeniedException the {@link AccessDeniedException}
     * @return The URL to go after performing the action
     */
    public String doDisableDirectoryRecord( HttpServletRequest request )
        throws AccessDeniedException
    {
        String strIdDirectoryRecord = request.getParameter( PARAMETER_ID_DIRECTORY_RECORD );
        int nIdDirectoryRecord = DirectoryUtils.convertStringToInt( strIdDirectoryRecord );
        Record record = RecordHome.findByPrimaryKey( nIdDirectoryRecord, getPlugin(  ) );

        if ( ( record == null ) ||
                !RBACService.isAuthorized( Directory.RESOURCE_TYPE,
                    Integer.toString( record.getDirectory(  ).getIdDirectory(  ) ),
                    DirectoryResourceIdService.PERMISSION_CHANGE_STATE_RECORD, getUser(  ) ) )
        {
            throw new AccessDeniedException(  );
        }

        record.setEnabled( false );
        RecordHome.update( record, getPlugin(  ) );

        return getJspManageDirectoryRecord( request, record.getDirectory(  ).getIdDirectory(  ) );
    }

    /**
     * Perform enable directory record
     * @param request The HTTP request
     * @throws AccessDeniedException the {@link AccessDeniedException}
     * @return The URL to go after performing the action
     */
    public String doEnableDirectoryRecord( HttpServletRequest request )
        throws AccessDeniedException
    {
        String strIdDirectoryRecord = request.getParameter( PARAMETER_ID_DIRECTORY_RECORD );
        int nIdDirectoryRecord = DirectoryUtils.convertStringToInt( strIdDirectoryRecord );
        Record record = RecordHome.findByPrimaryKey( nIdDirectoryRecord, getPlugin(  ) );

        if ( ( record == null ) ||
                !RBACService.isAuthorized( Directory.RESOURCE_TYPE,
                    Integer.toString( record.getDirectory(  ).getIdDirectory(  ) ),
                    DirectoryResourceIdService.PERMISSION_CHANGE_STATE_RECORD, getUser(  ) ) )
        {
            throw new AccessDeniedException(  );
        }

        record.setEnabled( true );
        RecordHome.update( record, getPlugin(  ) );

        return getJspManageDirectoryRecord( request, record.getDirectory(  ).getIdDirectory(  ) );
    }

    /**
     * return the tasks form
     * @param request the request
     * @return the tasks form
     */
    public String getTasksForm( HttpServletRequest request )
    {
        String strIdRecord = request.getParameter( PARAMETER_ID_RECORD );
        String strIdAction = request.getParameter( PARAMETER_ID_ACTION );
        int nIdRecord = DirectoryUtils.convertStringToInt( strIdRecord );
        int nIdAction = DirectoryUtils.convertStringToInt( strIdAction );

        HashMap model = new HashMap(  );

        model.put( MARK_TASKS_FORM,
            WorkflowService.getInstance(  )
                           .getDisplayTasksForm( nIdRecord, Record.WORKFLOW_RESOURCE_TYPE, nIdAction, request,
                getLocale(  ) ) );
        model.put( MARK_ID_ACTION, nIdAction );
        model.put( MARK_ID_RECORD, nIdRecord );

        setPageTitleProperty( PROPERTY_TASKS_FORM_WORKFLOW_PAGE_TITLE );

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_TASKS_FORM_WORKFLOW, getLocale(  ), model );

        return getAdminPage( templateList.getHtml(  ) );
    }

    /**
     * save the tasks form
     * @param request the httpRequest
     * @return The URL to go after performing the action
     */
    public String doSaveTasksForm( HttpServletRequest request )
    {
        String strIdRecord = request.getParameter( PARAMETER_ID_RECORD );
        String strIdAction = request.getParameter( PARAMETER_ID_ACTION );
        int nIdRecord = DirectoryUtils.convertStringToInt( strIdRecord );
        int nIdAction = DirectoryUtils.convertStringToInt( strIdAction );

        Record record = RecordHome.findByPrimaryKey( nIdRecord, getPlugin(  ) );
        int nIdDirectory = record.getDirectory(  ).getIdDirectory(  );

        if ( request.getParameter( PARAMETER_CANCEL ) == null )
        {
            String strError = WorkflowService.getInstance(  )
                                             .doSaveTasksForm( nIdRecord, Record.WORKFLOW_RESOURCE_TYPE, nIdAction,
                    Integer.valueOf( nIdDirectory ), request, getLocale(  ) );

            if ( strError != null )
            {
                return strError;
            }
        }

        return getJspManageDirectoryRecord( request, nIdDirectory );
    }

    /**
     * return the resource history
     * @param request the httpRequest
     * @return the resource history
     * @throws AccessDeniedException
     */
    public String getResourceHistory( HttpServletRequest request )
        throws AccessDeniedException
    {
        String strIdRecord = request.getParameter( PARAMETER_ID_DIRECTORY_RECORD );
        int nIdRecord = DirectoryUtils.convertStringToInt( strIdRecord );

        Record record = RecordHome.findByPrimaryKey( nIdRecord, getPlugin(  ) );
        int nIdDirectory = record.getDirectory(  ).getIdDirectory(  );
        int nIdWorkflow = ( DirectoryHome.findByPrimaryKey( nIdDirectory, getPlugin(  ) ) ).getIdWorkflow(  );
        Map<String, Object> model = new HashMap<String, Object>(  );

        if ( ( record == null ) ||
                !RBACService.isAuthorized( Directory.RESOURCE_TYPE,
                    Integer.toString( record.getDirectory(  ).getIdDirectory(  ) ),
                    DirectoryResourceIdService.PERMISSION_HISTORY_RECORD, getUser(  ) ) )
        {
            throw new AccessDeniedException(  );
        }

        EntryFilter filter;
        filter = new EntryFilter(  );
        filter.setIdDirectory( record.getDirectory(  ).getIdDirectory(  ) );
        filter.setIsShownInHistory( EntryFilter.FILTER_TRUE );

        List<IEntry> listEntry = EntryHome.getEntryList( filter, getPlugin(  ) );

        Directory directory = DirectoryHome.findByPrimaryKey( nIdDirectory, getPlugin(  ) );

        if ( ( directory != null ) && ( directory.isDateShownInHistory(  ) ) )
        {
            model.put( MARK_RECORD_DATE_CREATION, record.getDateCreation(  ) );
        }

        model.put( MARK_ENTRY_LIST, listEntry );
        model.put( MARK_MAP_ID_ENTRY_LIST_RECORD_FIELD,
            DirectoryUtils.getMapIdEntryListRecordField( listEntry, nIdRecord, getPlugin(  ) ) );

        model.put( MARK_RESOURCE_HISTORY,
            WorkflowService.getInstance(  )
                           .getDisplayDocumentHistory( nIdRecord, Record.WORKFLOW_RESOURCE_TYPE, nIdWorkflow, request,
                getLocale(  ) ) );

        setPageTitleProperty( PROPERTY_RESOURCE_HISTORY_PAGE_TITLE );

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_RESOURCE_HISTORY, getLocale(  ), model );

        return getAdminPage( templateList.getHtml(  ) );
    }

    /**
     * return the record visualisation
     * @param request
     * @return the record visualisation
     * @throws AccessDeniedException
     */
    public String getRecordVisualisation( HttpServletRequest request )
        throws AccessDeniedException
    {
        String strIdRecord = request.getParameter( PARAMETER_ID_DIRECTORY_RECORD );
        int nIdRecord = DirectoryUtils.convertStringToInt( strIdRecord );
        EntryFilter filter;

        Record record = RecordHome.findByPrimaryKey( nIdRecord, getPlugin(  ) );

        if ( ( record == null ) ||
                !RBACService.isAuthorized( Directory.RESOURCE_TYPE,
                    Integer.toString( record.getDirectory(  ).getIdDirectory(  ) ),
                    DirectoryResourceIdService.PERMISSION_VISUALISATION_RECORD, getUser(  ) ) )
        {
            throw new AccessDeniedException(  );
        }

        filter = new EntryFilter(  );
        filter.setIdDirectory( record.getDirectory(  ).getIdDirectory(  ) );
        filter.setIsGroup( EntryFilter.FILTER_TRUE );

        List<IEntry> listEntry = DirectoryUtils.getFormEntries( record.getDirectory(  ).getIdDirectory(  ),
                getPlugin(  ), getUser(  ) );
        int nIdDirectory = record.getDirectory(  ).getIdDirectory(  );
        Directory directory = DirectoryHome.findByPrimaryKey( nIdDirectory, getPlugin(  ) );

        Map<String, Object> model = new HashMap<String, Object>(  );

        model.put( MARK_RECORD, record );
        model.put( MARK_ENTRY_LIST, listEntry );
        model.put( MARK_DIRECTORY, directory );
        model.put( MARK_LOCALE, getLocale(  ) );
        model.put( MARK_ID_ENTRY_TYPE_IMAGE, AppPropertiesService.getPropertyInt( PROPERTY_ENTRY_TYPE_IMAGE, 10 ) );
        model.put( MARK_ID_ENTRY_TYPE_MYLUTECE_USER,
                AppPropertiesService.getPropertyInt( PROPERTY_ENTRY_TYPE_MYLUTECE_USER, 19 ) );
        model.put( MARK_PERMISSION_VISUALISATION_MYLUTECE_USER,
                RBACService.isAuthorized( Directory.RESOURCE_TYPE, Integer.toString( nIdDirectory ),
                    DirectoryResourceIdService.PERMISSION_VISUALISATION_MYLUTECE_USER, getUser(  ) ) );
        model.put( MARK_MAP_ID_ENTRY_LIST_RECORD_FIELD,
            DirectoryUtils.getMapIdEntryListRecordField( listEntry, nIdRecord, getPlugin(  ) ) );

        model.put( MARK_SHOW_DATE_CREATION_RECORD, directory.isDateShownInResultRecord(  ) );

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_VIEW_DIRECTORY_RECORD, getLocale(  ), model );

        return getAdminPage( templateList.getHtml(  ) );
    }

    /**
     * return url of the jsp manage directory
     * @param request The HTTP request
     * @return url of the jsp manage directory
     */
    private String getJspManageDirectory( HttpServletRequest request )
    {
        return AppPathService.getBaseUrl( request ) + JSP_MANAGE_DIRECTORY;
    }

    /**
     * return url of the jsp modify directory
     * @param request The HTTP request
     * @param nIdDirectory the key of directory to modify
     * @return return url of the jsp modify directorys
     */
    private String getJspModifyDirectory( HttpServletRequest request, int nIdDirectory )
    {
        return AppPathService.getBaseUrl( request ) + JSP_MODIFY_DIRECTORY + "?" + PARAMETER_ID_DIRECTORY + "=" +
        nIdDirectory;
    }

    /**
     * return url of the jsp modify entry
     * @param request The HTTP request
     * @param nIdEntry the key of the entry to modify
     * @return return url of the jsp modify entry
     */
    private String getJspModifyEntry( HttpServletRequest request, int nIdEntry )
    {
        return AppPathService.getBaseUrl( request ) + JSP_MODIFY_ENTRY + "?" + PARAMETER_ID_ENTRY + "=" + nIdEntry;
    }

    /**
     * return url of the jsp modify field
     * @param request The HTTP request
     * @param nIdField the key of the field to modify
     * @return return url of the jsp modify field
     */
    private String getJspModifyField( HttpServletRequest request, int nIdField )
    {
        return AppPathService.getBaseUrl( request ) + JSP_MODIFY_FIELD + "?" + PARAMETER_ID_FIELD + "=" + nIdField;
    }

    /**
     * return url of the jsp manage directory record
     * @param request The HTTP request
     * @param nIdDirectory the directory id
     * @return url of the jsp manage directory record
     */
    private String getJspManageDirectoryRecord( HttpServletRequest request, int nIdDirectory )
    {
        return AppPathService.getBaseUrl( request ) + JSP_MANAGE_DIRECTORY_RECORD + "?" + PARAMETER_ID_DIRECTORY + "=" +
        nIdDirectory + "&" + PARAMETER_SESSION + "=" + PARAMETER_SESSION;
    }

    /**
     * return url of the jsp print mass
     * @param request The HTTP request
     * @param nIdDirectory the directory id
     * @return url of the jsp print mass
     */
    private String getJspPrintMass( HttpServletRequest request, int nIdDirectory, String strIdStateList )
    {
        return AppPathService.getBaseUrl( request ) + JSP_DISPLAY_PRINT_HISTORY + "?" + PARAMETER_ID_DIRECTORY + "=" +
        nIdDirectory + "&" + PARAMETER_ID_STATE + "=" + strIdStateList;
    }

    /**
     * return url of the jsp import directory record
     * @param request The HTTP request
     * @param nIdDirectory the directory id
     * @return url of the jsp import directory record
     */
    private String getJspImportDirectoryRecord( HttpServletRequest request, int nIdDirectory )
    {
        return AppPathService.getBaseUrl( request ) + JSP_IMPORT_DIRECTORY_RECORD + "?" + PARAMETER_ID_DIRECTORY + "=" +
        nIdDirectory + "&" + PARAMETER_SESSION + "=" + PARAMETER_SESSION;
    }

    /**
     * return url of the jsp manage directory
     * @param request The HTTP request
     * @return url of the jsp manage directory
     */
    private String getJspManageAdvancedParameters( HttpServletRequest request )
    {
        return AppPathService.getBaseUrl( request ) + JSP_MANAGE_ADVANCED_PARAMETERS;
    }

    /**
     * return a reference list wich contains the different state of directory
     * @param locale the locale
     * @return reference list of directory state
     */
    private ReferenceList getRefListActive( Locale locale )
    {
        ReferenceList refListState = new ReferenceList(  );
        String strAll = I18nService.getLocalizedString( PROPERTY_ALL, locale );
        String strYes = I18nService.getLocalizedString( PROPERTY_YES, locale );
        String strNo = I18nService.getLocalizedString( PROPERTY_NO, locale );

        refListState.addItem( -1, strAll );
        refListState.addItem( 1, strYes );
        refListState.addItem( 0, strNo );

        return refListState;
    }

    /**
     * reinit directory recordFilter
     */
    private void reInitDirectoryRecordFilter(  )
    {
        _nItemsPerPageDirectoryRecord = 0;
        _strCurrentPageIndexDirectory = null;
        _mapQuery = null;
    }

    public String doProcessAction( HttpServletRequest request )
    {
        String strIdRecord = request.getParameter( PARAMETER_ID_RECORD );
        String strIdAction = request.getParameter( PARAMETER_ID_ACTION );
        int nIdRecord = DirectoryUtils.convertStringToInt( strIdRecord );
        int nIdAction = DirectoryUtils.convertStringToInt( strIdAction );

        Record record = RecordHome.findByPrimaryKey( nIdRecord, getPlugin(  ) );
        int nIdDirectory = record.getDirectory(  ).getIdDirectory(  );

        if ( WorkflowService.getInstance(  ).isDisplayTasksForm( nIdAction, getLocale(  ) ) )
        {
            return getJspTasksFormTest( request, nIdRecord, nIdAction );
        }

        WorkflowService.getInstance(  )
                       .doProcessAction( nIdRecord, Record.WORKFLOW_RESOURCE_TYPE, nIdAction,
            Integer.valueOf( nIdDirectory ), request, getLocale(  ), false );

        return getJspManageDirectoryRecord( request, nIdDirectory );
    }

    /**
     * return url of the jsp manage commentaire
     * @param request The HTTP request
     * @return url of the jsp manage commentaire
     */
    private String getJspTasksFormTest( HttpServletRequest request, int nIdTestResource, int nIdAction )
    {
        return AppPathService.getBaseUrl( request ) + JSP_TASKS_FORM_WORKFLOW + "?" + PARAMETER_ID_RECORD + "=" +
        nIdTestResource + "&" + PARAMETER_ID_ACTION + "=" + nIdAction;
    }

    /**
     * Display the states for print mass
     * @param request la requete
     * @return The URL to go after performing the action
     */
    public String getMassPrint( HttpServletRequest request )
        throws AccessDeniedException
    {
        String strIdDirectory = request.getParameter( PARAMETER_ID_DIRECTORY );
        int nIdDirectory = DirectoryUtils.convertStringToInt( strIdDirectory );
        Directory directory = DirectoryHome.findByPrimaryKey( nIdDirectory, getPlugin(  ) );
        Map<String, Object> model = new HashMap<String, Object>(  );

        if ( ( directory.getIdWorkflow(  ) != DirectoryUtils.CONSTANT_ID_NULL ) &&
                WorkflowService.getInstance(  ).isAvailable(  ) )
        {
            int nIdWorkflow = directory.getIdWorkflow(  );
            Collection<State> listState = WorkflowService.getInstance(  )
                                                         .getAllStateByWorkflow( directory.getIdWorkflow(  ),
                    AdminUserService.getAdminUser( request ) );
            model.put( MARK_STATE_LIST, listState );
        }

        model.put( MARK_DIRECTORY, directory );
        setPageTitleProperty( PROPERTY_MASS_PRINT_PAGE_TITLE );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MANAGE_MASS_PRINT, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Verify Print Mass
     * @param request la requete
     * @return The URL to go after performing the action
     */
    public String doMassPrint( HttpServletRequest request )
        throws AccessDeniedException
    {
        String strIdDirectory = request.getParameter( PARAMETER_ID_DIRECTORY );
        int nIdDirectory = DirectoryUtils.convertStringToInt( strIdDirectory );
        Directory directory = DirectoryHome.findByPrimaryKey( nIdDirectory, getPlugin(  ) );
        Map mapIdState = request.getParameterMap(  );
        WorkflowService workflowService = WorkflowService.getInstance(  );

        List<State> listAllState = (List<State>) workflowService.getAllStateByWorkflow( directory.getIdWorkflow(  ),
                AdminUserService.getAdminUser( request ) );

        List<State> listState = new ArrayList<State>(  );

        for ( State state : listAllState )
        {
            if ( mapIdState.containsKey( Integer.toString( state.getId(  ) ) ) )
            {
                listState.add( state );
            }
        }

        if ( mapIdState.isEmpty(  ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_NOT_SELECTED_STATE, AdminMessage.TYPE_STOP );
        }
        else
        {
            Iterator<State> it = listState.iterator(  );
            boolean bFind = false;
            Integer nIntIdDirectory = Integer.valueOf( directory.getIdDirectory(  ) );

            while ( !bFind && it.hasNext(  ) )
            {
                bFind = ( workflowService.getAuthorizedResourceList( Record.WORKFLOW_RESOURCE_TYPE,
                        directory.getIdWorkflow(  ), it.next(  ).getId(  ), nIntIdDirectory, getUser(  ) ).size(  ) > 0 );
            }

            if ( !bFind )
            {
                return AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_NO_RECORD, AdminMessage.TYPE_STOP );
            }

            return getJspPrintHistory( request, listState, nIdDirectory );
        }
    }

    /**
     * return url of the jsp
     * @param request The HTTP request
     * @param listState A state list
     * @param nIdDirectory The directory id
     * @return url of the jsp
     */
    private String getJspPrintHistory( HttpServletRequest request, List<State> listState, int nIdDirectory )
    {
        String strIdState = new String(  );

        for ( State state : listState )
        {
            strIdState = strIdState.concat( state.getId(  ) + "," );
        }

        if ( strIdState.length(  ) > 0 )
        {
            strIdState = strIdState.substring( 0, strIdState.length(  ) - 1 );
        }

        return AppPathService.getBaseUrl( request ) + JSP_DISPLAY_PRINT_HISTORY + "?" + PARAMETER_ID_DIRECTORY + "=" +
        nIdDirectory + "&" + PARAMETER_ID_STATE + "=" + strIdState;
    }

    /**
     * Display Print Mass
     * @param request la requete
     * @return The URL to go after performing the action
     */
    public String getDisplayMassPrint( HttpServletRequest request )
        throws AccessDeniedException
    {
        String strIdDirectory = request.getParameter( PARAMETER_ID_DIRECTORY );
        int nIdDirectory = DirectoryUtils.convertStringToInt( strIdDirectory );
        Directory directory = DirectoryHome.findByPrimaryKey( nIdDirectory, getPlugin(  ) );
        String strIdState = (String) request.getParameter( PARAMETER_ID_STATE );
        String[] tabIdState = strIdState.split( "," );

        WorkflowService workflowService = WorkflowService.getInstance(  );

        HashMap model = new HashMap(  );
        List<Integer> recordList = new ArrayList<Integer>(  );

        List<String> listStrIdState = Arrays.asList( tabIdState );

        List<State> listAllState = (List<State>) workflowService.getAllStateByWorkflow( directory.getIdWorkflow(  ),
                AdminUserService.getAdminUser( request ) );

        List<Integer> listIdState = new ArrayList<Integer>(  );

        for ( State state : listAllState )
        {
            if ( listStrIdState.contains( Integer.toString( state.getId(  ) ) ) )
            {
                listIdState.add( Integer.valueOf( state.getId(  ) ) );
            }
        }

        RecordFieldFilter recordFieldFilter = new RecordFieldFilter(  );
        recordFieldFilter.setIdDirectory( directory.getIdDirectory(  ) );

        List<Integer> listResultRecordId = DirectorySearchService.getInstance(  )
                                                                 .getSearchResults( directory, _mapQuery,
                _dateCreationRecord, _dateCreationBeginRecord, _dateCreationEndRecord, recordFieldFilter, getPlugin(  ) );

        List<Integer> listTmpResultRecordId = workflowService.getAuthorizedResourceList( Record.WORKFLOW_RESOURCE_TYPE,
                directory.getIdWorkflow(  ), listIdState, Integer.valueOf( directory.getIdDirectory(  ) ), getUser(  ) );
        List<Integer> lListResult = DirectoryUtils.retainAll( listResultRecordId, listTmpResultRecordId );

        _strCurrentPageIndexPrintMass = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX,
                _strCurrentPageIndexPrintMass );
        _nItemsPerPagePrintMass = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE,
                _nItemsPerPagePrintMass, _nDefaultItemsPerPage );

        LocalizedPaginator paginator = new LocalizedPaginator( lListResult, _nItemsPerPagePrintMass,
                getJspPrintMass( request, nIdDirectory, strIdState ), PARAMETER_PAGE_INDEX,
                _strCurrentPageIndexPrintMass, getLocale(  ) );

        recordList = (List<Integer>) paginator.getPageItems(  );

        EntryFilter filter;
        filter = new EntryFilter(  );
        filter.setIdDirectory( nIdDirectory );
        filter.setIsShownInHistory( EntryFilter.FILTER_TRUE );

        List<IEntry> listEntry = EntryHome.getEntryList( filter, getPlugin(  ) );

        List<HashMap> listRecordHistory = new ArrayList<HashMap>(  );

        for ( Integer nIdRecord : recordList )
        {
            HashMap resource = new HashMap(  );
            resource.put( MARK_MAP_ID_ENTRY_LIST_RECORD_FIELD,
                DirectoryUtils.getMapIdEntryListRecordField( listEntry, nIdRecord, getPlugin(  ) ) );

            resource.put( MARK_RESOURCE_HISTORY,
                WorkflowService.getInstance(  )
                               .getDisplayDocumentHistory( nIdRecord, Record.WORKFLOW_RESOURCE_TYPE,
                    directory.getIdWorkflow(  ), request, getLocale(  ) ) );
            listRecordHistory.add( resource );
        }

        model.put( MARK_HISTORY_LIST, listRecordHistory );
        model.put( MARK_ENTRY_LIST, listEntry );

        model.put( MARK_ID_DIRECTORY, strIdDirectory );
        model.put( MARK_ID_STATE, strIdState );

        model.put( MARK_PAGINATOR, paginator );
        model.put( MARK_NB_ITEMS_PER_PAGE, DirectoryUtils.EMPTY_STRING + _nItemsPerPagePrintMass );

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_DISPLAY_MASS_PRINT, getLocale(  ), model );

        return getAdminPage( templateList.getHtml(  ) );
    }

    /**
     * Returns advanced parameters form
     *
     * @param request The Http request
     * @return Html form
     */
    public String getManageAdvancedParameters( HttpServletRequest request )
    {
        if ( !RBACService.isAuthorized( Directory.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID,
                    DirectoryResourceIdService.PERMISSION_MANAGE_ADVANCED_PARAMETERS, getUser(  ) ) )
        {
            return getManageDirectory( request );
        }

        Map<String, Object> model = DirectoryService.getInstance(  ).getManageAdvancedParameters( getUser(  ) );
        
        model.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
        model.put( MARK_LOCALE, AdminUserService.getLocale( request ).getLanguage(  ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MANAGE_ADVANCED_PARAMETERS, getLocale(  ),
                model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Modify directory parameter default values
     * @param request HttpServletRequest
     * @return JSP return
     * @throws AccessDeniedException
     */
    public String doModifyDirectoryParameterDefaultValues( HttpServletRequest request )
        throws AccessDeniedException
    {
        if ( !RBACService.isAuthorized( Directory.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID,
                    DirectoryResourceIdService.PERMISSION_MANAGE_ADVANCED_PARAMETERS, getUser(  ) ) )
        {
            throw new AccessDeniedException(  );
        }

        ReferenceList listParams = DirectoryParameterHome.findAll( getPlugin(  ) );

        for ( ReferenceItem param : listParams )
        {
            String strParamValue = request.getParameter( param.getCode(  ) );

            if ( StringUtils.isBlank( strParamValue ) )
            {
                strParamValue = ZERO;
            }

            param.setName( strParamValue );
            DirectoryParameterHome.update( param, getPlugin(  ) );
        }

        return getJspManageAdvancedParameters( request );
    }

    /**
     * Modify entry parameter default values
     * @param request HttpServletRequest
     * @return JSP return
     * @throws AccessDeniedException
     */
    public String doModifyEntryParameterDefaultValues( HttpServletRequest request )
        throws AccessDeniedException
    {
        if ( !RBACService.isAuthorized( Directory.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID,
                    DirectoryResourceIdService.PERMISSION_MANAGE_ADVANCED_PARAMETERS, getUser(  ) ) )
        {
            throw new AccessDeniedException(  );
        }

        ReferenceList listParams = EntryParameterHome.findAll( getPlugin(  ) );

        for ( ReferenceItem param : listParams )
        {
            String strParamValue = request.getParameter( param.getCode(  ) );

            if ( StringUtils.isBlank( strParamValue ) )
            {
                strParamValue = ZERO;
            }

            param.setName( strParamValue );
            EntryParameterHome.update( param, getPlugin(  ) );
        }

        return getJspManageAdvancedParameters( request );
    }

    /**
     * return the record visualisation
     * @param request
     * @return the record visualisation
     * @throws AccessDeniedException
     */
    public String getMyLuteceUserVisualisation( HttpServletRequest request )
        throws AccessDeniedException
    {
        String strIdRecord = request.getParameter( PARAMETER_ID_DIRECTORY_RECORD );
        int nIdRecord = DirectoryUtils.convertStringToInt( strIdRecord );
        Record record = RecordHome.findByPrimaryKey( nIdRecord, getPlugin(  ) );

        if ( ( record == null ) ||
                !RBACService.isAuthorized( Directory.RESOURCE_TYPE,
                    Integer.toString( record.getDirectory(  ).getIdDirectory(  ) ),
                    DirectoryResourceIdService.PERMISSION_VISUALISATION_MYLUTECE_USER, getUser(  ) ) )
        {
            throw new AccessDeniedException(  );
        }

        LuteceUser user = DirectoryService.getInstance(  ).getUserFromIdDirectoryRecord( nIdRecord );
        Map<String, Object> model = new HashMap<String, Object>(  );
        if ( user != null )
        {
        	ReferenceList listUserInfos = DirectoryService.getInstance(  ).getUserInfo( user );

            model.put( MARK_MYLUTECE_USER_LOGIN, user.getName(  ) );
            model.put( MARK_MYLUTECE_USER_INFOS_LIST, listUserInfos );
        }

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_VIEW_MYLUTECE_USER, getLocale(  ), model );

        return getAdminPage( templateList.getHtml(  ) );
    }

    /**
     * Return the interface for import field
     * @param request The Http request
     * @throws AccessDeniedException the {@link AccessDeniedException}
     * @return Html directory
     */
    public String getImportField( HttpServletRequest request )
        throws AccessDeniedException
    {
        String strIdDirectory = request.getParameter( PARAMETER_ID_DIRECTORY );
        int nIdDirectory = DirectoryUtils.convertStringToInt( strIdDirectory );
        Directory directory = DirectoryHome.findByPrimaryKey( nIdDirectory, getPlugin(  ) );
        String strIdEntry = request.getParameter( PARAMETER_ID_ENTRY );
        int nIdEntry = DirectoryUtils.convertStringToInt( strIdEntry );
        IEntry entry = EntryHome.findByPrimaryKey( nIdEntry, getPlugin(  ) );

        if ( ( directory == null ) || ( entry == null ) ||
                !RBACService.isAuthorized( Directory.RESOURCE_TYPE, strIdDirectory,
                    DirectoryResourceIdService.PERMISSION_IMPORT_FIELD, getUser(  ) ) )
        {
            throw new AccessDeniedException(  );
        }

        Map<String, Object> model = new HashMap<String, Object>(  );

        if ( request.getParameter( PARAMETER_SESSION ) != null )
        {
            if ( _strError != null )
            {
                model.put( MARK_STR_ERROR, _strError.toString(  ) );
            }

            model.put( MARK_NUMBER_LINES_ERROR, _nCountLineFailure );
            model.put( MARK_NUMBER_LINES_IMPORTED, _nCountLine - _nCountLineFailure );
            model.put( MARK_FINISH_IMPORT, true );
            _nCountLine = 0;
            _nCountLineFailure = 0;
            _strError = null;

            _strError = null;
        }

        model.put( MARK_LOCALE, getLocale(  ) );
        model.put( MARK_DIRECTORY, directory );
        model.put( MARK_ENTRY, entry );
        setPageTitleProperty( PROPERTY_IMPORT_FIELD_PAGE_TITLE );

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_IMPORT_FIELD, getLocale(  ),
                model );

        return getAdminPage( templateList.getHtml(  ) );
    }
    
    /**
     * ImportDirectory record
     * @param request the Http Request
     * @throws AccessDeniedException the {@link AccessDeniedException}
     * @return The URL to go after performing the action
     */
    public String doImportField( HttpServletRequest request )
        throws AccessDeniedException
    {
        MultipartHttpServletRequest multipartRequest = ( MultipartHttpServletRequest ) request;
        FileItem fileItem = multipartRequest.getFile( PARAMETER_FILE_IMPORT );
        String strMimeType = FileSystemUtil.getMIMEType( FileUploadService.getFileNameOnly( fileItem ) );

        if ( ( fileItem == null ) || ( fileItem.getName(  ) == null ) ||
                DirectoryUtils.EMPTY_STRING.equals( fileItem.getName(  ) ) )
        {
            Object[] tabRequiredFields = { I18nService.getLocalizedString( FIELD_FILE_IMPORT, getLocale(  ) ) };

            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields,
                AdminMessage.TYPE_STOP );
        }

        if ( ( !strMimeType.equals( CONSTANT_MIME_TYPE_CSV ) && !strMimeType.equals( CONSTANT_MIME_TYPE_OCTETSTREAM ) &&
                !strMimeType.equals( CONSTANT_MIME_TYPE_TEXT_CSV ) ) ||
                !fileItem.getName(  ).toLowerCase(  ).endsWith( CONSTANT_EXTENSION_CSV_FILE ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_CSV_FILE_IMPORT, AdminMessage.TYPE_STOP );
        }

        String strIdEntry = request.getParameter( PARAMETER_ID_ENTRY );
        int nIdEntry = DirectoryUtils.convertStringToInt( strIdEntry );
        IEntry entry = EntryHome.findByPrimaryKey( nIdEntry, getPlugin(  ) );
        String strIdDirectory = request.getParameter( PARAMETER_ID_DIRECTORY );
        int nIdDirectory = DirectoryUtils.convertStringToInt( strIdDirectory );
        Directory directory = DirectoryHome.findByPrimaryKey( nIdDirectory, getPlugin(  ) );

        if ( ( entry == null ) || ( directory == null ) ||
        		!RBACService.isAuthorized( Directory.RESOURCE_TYPE, strIdDirectory,
                        DirectoryResourceIdService.PERMISSION_MODIFY, getUser(  ) ) )
        {
            throw new AccessDeniedException(  );
        }

        Character strCsvSeparator = AppPropertiesService.getProperty( PROPERTY_IMPORT_CSV_DELIMITER ).charAt( 0 );
        _strError = new StringBuffer(  );
        try
        {
            InputStreamReader inputStreamReader = new InputStreamReader( fileItem.getInputStream(  ) );
            CSVReader csvReader = new CSVReader( inputStreamReader, strCsvSeparator, '\"' );

            String[] nextLine;

            _nCountLine = 0;
            _nCountLineFailure = 0;

            while ( ( nextLine = csvReader.readNext(  ) ) != null )
            {
                _nCountLine++;

                if ( nextLine.length != IMPORT_FIELD_NB_COLUMN_MAX )
                {
                    _strError.append( I18nService.getLocalizedString( PROPERTY_LINE, getLocale(  ) ) );
                    _strError.append( _nCountLine );
                    _strError.append( " > " );
                    _strError.append( I18nService.getLocalizedString( MESSAGE_ERROR_CSV_NUMBER_SEPARATOR, getLocale(  ) ) );
                    _strError.append( "<br/>" );
                    _nCountLineFailure++;
                }
                else
                {
                	Field field = new Field(  );
                    field.setEntry( entry );

                    try
                    {
                    	getImportFieldData( request, field, nextLine );
                    	FieldHome.create( field, getPlugin(  ) );
                    }
                    catch ( DirectoryErrorException error )
                    {
                    	_strError.append( I18nService.getLocalizedString( PROPERTY_LINE, getLocale(  ) ) );
                        _strError.append( _nCountLine );
                        _strError.append( " > " );

                        if ( error.isMandatoryError(  ) )
                        {
                            Object[] tabRequiredFields = { error.getTitleField(  ) };
                            _strError.append( I18nService.getLocalizedString( MESSAGE_DIRECTORY_ERROR_MANDATORY_FIELD,
                                    tabRequiredFields, getLocale(  ) ) );
                        }
                        else
                        {
                            Object[] tabRequiredFields = { error.getTitleField(  ), error.getErrorMessage(  ) };
                            _strError.append( I18nService.getLocalizedString( MESSAGE_DIRECTORY_ERROR,
                                    tabRequiredFields, getLocale(  ) ) );
                        }

                        _strError.append( "<br/>" );
                        _nCountLineFailure++;
                    }
                }
            }
        }

        catch ( IOException e )
        {
            AppLogService.error( e );
        }

    	return AppPathService.getBaseUrl( request ) + JSP_IMPORT_FIELD + DirectoryUtils.CONSTANT_INTERROGATION_MARK + PARAMETER_ID_DIRECTORY + 
	        	DirectoryUtils.CONSTANT_EQUAL + nIdDirectory + DirectoryUtils.CONSTANT_AMPERSAND + PARAMETER_ID_ENTRY + 
	        	DirectoryUtils.CONSTANT_EQUAL + nIdEntry + DirectoryUtils.CONSTANT_AMPERSAND + PARAMETER_SESSION + "=" + PARAMETER_SESSION;
    }
    
    /**
     * Get the request data and if there is no error insert the data in the field specified in parameter.
     * return null if there is no error or else return the error page url
     * @param request the request
     * @param field field
     * @return null if there is no error or else return the error page url
     */
    private void getImportFieldData( HttpServletRequest request, Field field, String[] listImportValue )
    	throws DirectoryErrorException
    {
        String strTitle = listImportValue[0];
        String strValue = listImportValue[1];

        String strFieldError = DirectoryUtils.EMPTY_STRING;

        if ( ( strTitle == null ) || DirectoryUtils.EMPTY_STRING.equals( strTitle ) )
        {
        	throw new DirectoryErrorException( I18nService.getLocalizedString( FIELD_TITLE_FIELD, getLocale(  )  ) );
        }
        else if ( ( strValue == null ) || DirectoryUtils.EMPTY_STRING.equals( strValue ) )
        {
        	throw new DirectoryErrorException( I18nService.getLocalizedString( FIELD_VALUE_FIELD, getLocale(  ) ) );
        }
        else if ( !StringUtil.checkCodeKey( strValue ) )
        {
        	throw new DirectoryErrorException( I18nService.getLocalizedString( FIELD_VALUE_FIELD, getLocale(  ) ), 
        			I18nService.getLocalizedString( MESSAGE_FIELD_VALUE_FIELD, getLocale(  ) ) );
        }

        field.setTitle( strTitle );
        field.setValue( strValue );
        field.setDefaultValue( false );
    }
}

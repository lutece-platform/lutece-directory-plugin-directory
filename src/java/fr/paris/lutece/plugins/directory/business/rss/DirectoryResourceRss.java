/*
 * Copyright (c) 2002-2012, Mairie de Paris
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
package fr.paris.lutece.plugins.directory.business.rss;

import fr.paris.lutece.plugins.directory.business.Directory;
import fr.paris.lutece.plugins.directory.business.DirectoryHome;
import fr.paris.lutece.plugins.directory.business.EntryFilter;
import fr.paris.lutece.plugins.directory.business.EntryHome;
import fr.paris.lutece.plugins.directory.business.Field;
import fr.paris.lutece.plugins.directory.business.FieldHome;
import fr.paris.lutece.plugins.directory.business.IEntry;
import fr.paris.lutece.plugins.directory.business.Record;
import fr.paris.lutece.plugins.directory.business.RecordField;
import fr.paris.lutece.plugins.directory.business.RecordFieldFilter;
import fr.paris.lutece.plugins.directory.business.RecordFieldHome;
import fr.paris.lutece.plugins.directory.service.DirectoryPlugin;
import fr.paris.lutece.plugins.directory.service.directorysearch.DirectorySearchService;
import fr.paris.lutece.plugins.directory.service.record.IRecordService;
import fr.paris.lutece.plugins.directory.service.record.RecordService;
import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.portal.business.rss.FeedResource;
import fr.paris.lutece.portal.business.rss.FeedResourceImage;
import fr.paris.lutece.portal.business.rss.FeedResourceItem;
import fr.paris.lutece.portal.business.rss.IFeedResource;
import fr.paris.lutece.portal.business.rss.IFeedResourceImage;
import fr.paris.lutece.portal.business.rss.IFeedResourceItem;
import fr.paris.lutece.portal.business.rss.ResourceRss;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.url.UrlItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 *
 * DirectoryResourceRss
 */
public class DirectoryResourceRss extends ResourceRss
{
    //templates
    private static final String TEMPLATE_TASK_EVALUATION_CREATE_CONFIG = "admin/plugins/directory/rss/resource_create_config.html";
    private static final String TEMPLATE_TASK_EVALUATION_MODIFY_CONFIG = "admin/plugins/directory/rss/resource_modify_config.html";
    private static final String TEMPLATE_RSS_IMAGE = "admin/plugins/directory/rss/rss_image.html";

    //	Markers
    private static final String MARK_DIRECTORY_LIST = "directory_list";
    private static final String MARK_DIRECTORY_LIST_DEFAULT_ITEM = "directory_list_default_item";
    private static final String MARK_RSS_SITE_ID_DIRECTORY = "id_directory";
    private static final String MARK_RSS_SITE_ID_RECORD = "id_record";
    private static final String MARK_ENTRY_LIST = "entry_list";
    private static final String MARK_ENTRY_LIST_TITLE_DEFAULT_ITEM = "entry_list_title_default_item";
    private static final String MARK_ENTRY_LIST_DESCRIPTION_DEFAULT_ITEM = "entry_list_description_default_item";
    private static final String MARK_ENTRY_LIST_IMAGE_DEFAULT_ITEM = "entry_list_image_default_item";
    private static final String MARK_ENTRY_LIST_LINK_DEFAULT_ITEM = "entry_list_link_default_item";
    private static final String MARK_ENTRY_LIST_IMAGE = "entry_list_image";
    private static final String MARK_ENTRY_LIST_FILTER = "entry_list_filter";
    private static final String MARK_ENTRY_LIST_LINK = "entry_list_link";
    private static final String MARK_ENTRY_FILTER_1 = "entry_filter_1";
    private static final String MARK_ENTRY_FILTER_2 = "entry_filter_2";
    private static final String MARK_WORKFLOW_STATE_LIST = "workflow_state_list";
    private static final String MARK_WORKFLOW_STATE_LIST_DEFAULT_ITEM = "workflow_state_list_default_item";
    private static final String MARK_RSS_SITE_TITLE = "entry_title";
    private static final String MARK_RSS_SITE_DESCRIPTION_ITEM = "entry_description";
    private static final String MARK_RSS_SITE_LINK_ITEM = "entry_link";
    private static final String MARK_RSS_SITE_IMAGE_ITEM = "entry_image";
    private static final String MARK_RSS_SITE_DATE = "date";
    private static final String MARK_RSS_SITE_DATE_MODIFICATION = "date_modification";
    private static final String MARK_LOCALE = "locale";
    private static final String MARK_RECORD_FIELD_FILTER_1 = "recordField_filter_1";
    private static final String MARK_RECORD_FIELD_FILTER_2 = "recordField_filter_2";
    private static final String MARK_RSS_SITE_IMAGE_HEIGHT_ITEM = "entry_image_height";
    private static final String MARK_RSS_SITE_IMAGE_WIDTH_ITEM = "entry_image_width";

    //Parameters
    private static final String PARAMETER_ID_DIRECTORY = "id_directory";
    private static final String PARAMETER_ID_ENTRY_TITLE = "id_entry_title";
    private static final String PARAMETER_ID_ENTRY_DESCRIPTION = "id_entry_description";
    private static final String PARAMETER_ID_WORKFLOW_STATE = "id_workflow_state";
    private static final String PARAMETER_ID_ENTRY_IMAGE = "id_entry_image";
    private static final String PARAMETER_ID_ENTRY_LINK = "id_entry_link";
    private static final String PARAMETER_ID_ENTRY_FILTER_1 = "id_entry_filter_1";
    private static final String PARAMETER_ID_ENTRY_FILTER_2 = "id_entry_filter_2";
    private static final String PARAMETER_PAGE = "page";
    private static final String PARAMETER_ID_DIRECTORY_RECORD = "id_directory_record";
    private static final String PARAMETER_VIEW_DIRECTORY_RECORD = "view_directory_record";

    //Properties
    private static final String FIELD_ID_DIRECTORY = "directory.resource_rss.label_directory";
    private static final String FIELD_ID_ENTRY_TITLE = "directory.resource_rss.label_entry_title";
    private static final String FIELD_ID_ENTRY_DESCRIPTION = "directory.resource_rss.label_entry_description";

    //Messages
    private static final String MESSAGE_MANDATORY_FIELD = "directory.message.mandatory.field";
    private static final String MESSAGE_NO_REFRESH = "directory.message.no_refresh";
    private static final String MESSAGE_NO_REFRESH_FILTER = "directory.message.no_refresh_filter";
    public static final String PROPERTY_RSS_STORAGE_FOLDER_PATH = "rss.storage.folder.path";
    public static final String PROPERTY_STORAGE_DIRECTORY_NAME = "rss.storage.directory.name";
    private static final String TEMPLATE_PUSH_RSS_XML = "admin/plugins/directory/rss/rss_xml.html";
    private static final String MARK_ITEM_LIST = "itemList";
    private static final String MARK_RSS_SITE_NAME = "site_name";
    private static final String MARK_RSS_FILE_LANGUAGE = "file_language";
    private static final String MARK_RSS_SITE_URL = "site_url";
    private static final String MARK_RSS_SITE_DESCRIPTION = "site_description";
    private static final String PROPERTY_SITE_LANGUAGE = "rss.language";
    private static final String PROPERTY_WEBAPP_PROD_URL = "lutece.prod.url";
    private static final String PROPERTY_ACCEPT_DIRECTORY_TYPE = "directory.resource_rss.entry_accept";
    private static final String PROPERTY_ACCEPT_DIRECTORY_TYPE_FOR_LINK = "directory.resource_rss.entry_accept_for_link";
    private static final String PROPERTY_ENTRY_DIRECTORY_TYPE_IMAGE = "directory.resource_rss.entry_type_image";
    private static final String CONSTANT_DIRECTORY = "directory";
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String EMPTY_STRING = "";

    public boolean contentResourceRss(  )
    {
        Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );

        if ( DirectoryHome.getDirectoryList( pluginDirectory ) != null )
        {
            return true;
        }

        return false;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.portal.business.rss.IResourceRss#doSaveConfig( javax.servlet.http.HttpServletRequest, java.util.Locale )
     */
    public void doSaveConfig( HttpServletRequest request, Locale locale )
    {
        Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );

        String idDirectory = request.getParameter( PARAMETER_ID_DIRECTORY );
        String idEntryDescription = request.getParameter( PARAMETER_ID_ENTRY_DESCRIPTION );
        String idEntryTitle = request.getParameter( PARAMETER_ID_ENTRY_TITLE );
        String idWorkflowState = request.getParameter( PARAMETER_ID_WORKFLOW_STATE );
        String idEntryImage = request.getParameter( PARAMETER_ID_ENTRY_IMAGE );
        String idEntryLink = request.getParameter( PARAMETER_ID_ENTRY_LINK );
        String idEntryFilter1 = request.getParameter( PARAMETER_ID_ENTRY_FILTER_1 );
        String idEntryFilter2 = request.getParameter( PARAMETER_ID_ENTRY_FILTER_2 );
        String strValueFilter1 = request.getParameter( idEntryFilter1 );
        String strValueFilter2 = request.getParameter( idEntryFilter2 );

        DirectoryResourceRssConfig config = new DirectoryResourceRssConfig(  );
        config.setIdRss( this.getId(  ) );
        config.setIdDirectory( DirectoryUtils.convertStringToInt( idDirectory ) );
        config.setIdEntryTitle( DirectoryUtils.convertStringToInt( idEntryTitle ) );
        config.setIdEntryDescription( DirectoryUtils.convertStringToInt( idEntryDescription ) );
        config.setIdEntryImage( DirectoryUtils.convertStringToInt( idEntryImage ) );
        config.setIdEntryLink( DirectoryUtils.convertStringToInt( idEntryLink ) );
        config.setIdEntryFilter1( DirectoryUtils.convertStringToInt( idEntryFilter1 ) );
        config.setIdEntryFilter2( DirectoryUtils.convertStringToInt( idEntryFilter2 ) );
        config.setValueFilter1( strValueFilter1 );
        config.setValueFilter2( strValueFilter2 );
        config.setIdWorkflowState( DirectoryUtils.convertStringToInt( idWorkflowState ) );

        DirectoryResourceRssConfigHome.create( config, pluginDirectory );
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.portal.business.rss.IResourceRss#doSaveConfig( javax.servlet.http.HttpServletRequest, java.util.Locale )
     */
    public void doUpdateConfig( HttpServletRequest request, Locale locale )
    {
        Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );

        String idDirectory = request.getParameter( PARAMETER_ID_DIRECTORY );
        String idEntryDescription = request.getParameter( PARAMETER_ID_ENTRY_DESCRIPTION );
        String idEntryTitle = request.getParameter( PARAMETER_ID_ENTRY_TITLE );
        String idWorkflowState = request.getParameter( PARAMETER_ID_WORKFLOW_STATE );
        String idEntryImage = request.getParameter( PARAMETER_ID_ENTRY_IMAGE );
        String idEntryLink = request.getParameter( PARAMETER_ID_ENTRY_LINK );
        String idEntryFilter1 = request.getParameter( PARAMETER_ID_ENTRY_FILTER_1 );
        String idEntryFilter2 = request.getParameter( PARAMETER_ID_ENTRY_FILTER_2 );
        String strValueFilter1 = request.getParameter( idEntryFilter1 );
        String strValueFilter2 = request.getParameter( idEntryFilter2 );

        DirectoryResourceRssConfig config = new DirectoryResourceRssConfig(  );
        config.setIdRss( this.getId(  ) );
        config.setIdDirectory( DirectoryUtils.convertStringToInt( idDirectory ) );
        config.setIdEntryTitle( DirectoryUtils.convertStringToInt( idEntryTitle ) );
        config.setIdEntryDescription( DirectoryUtils.convertStringToInt( idEntryDescription ) );
        config.setIdEntryImage( DirectoryUtils.convertStringToInt( idEntryImage ) );
        config.setIdEntryLink( DirectoryUtils.convertStringToInt( idEntryLink ) );
        config.setIdEntryFilter1( DirectoryUtils.convertStringToInt( idEntryFilter1 ) );
        config.setIdEntryFilter2( DirectoryUtils.convertStringToInt( idEntryFilter2 ) );
        config.setValueFilter1( strValueFilter1 );
        config.setValueFilter2( strValueFilter2 );
        config.setIdWorkflowState( DirectoryUtils.convertStringToInt( idWorkflowState ) );

        DirectoryResourceRssConfigHome.update( config, pluginDirectory );
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.portal.business.rss.IResourceRss#doValidateTask(javax.servlet.http.HttpServletRequest, java.util.Locale)
     */
    public String doValidateConfigForm( HttpServletRequest request, Locale locale )
    {
        Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
        String strError = DirectoryUtils.EMPTY_STRING;
        String idDirectory = request.getParameter( PARAMETER_ID_DIRECTORY );
        String idEntryDescription = request.getParameter( PARAMETER_ID_ENTRY_DESCRIPTION );
        String idEntryTitle = request.getParameter( PARAMETER_ID_ENTRY_TITLE );
        String idEntryFilter1 = request.getParameter( PARAMETER_ID_ENTRY_FILTER_1 );
        String idEntryFilter2 = request.getParameter( PARAMETER_ID_ENTRY_FILTER_2 );

        if ( idDirectory == null )
        {
            strError = FIELD_ID_DIRECTORY;
        }
        else if ( idEntryTitle == null )
        {
            strError = FIELD_ID_ENTRY_TITLE;
        }
        else if ( idEntryDescription == null )
        {
            strError = FIELD_ID_ENTRY_DESCRIPTION;
        }

        if ( !strError.equals( DirectoryUtils.EMPTY_STRING ) )
        {
            Object[] tabRequiredFields = { I18nService.getLocalizedString( strError, locale ) };

            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields,
                AdminMessage.TYPE_STOP );
        }

        IEntry entryTitle = EntryHome.findByPrimaryKey( DirectoryUtils.convertStringToInt( idEntryTitle ),
                pluginDirectory );
        IEntry entryDescription = EntryHome.findByPrimaryKey( DirectoryUtils.convertStringToInt( idEntryDescription ),
                pluginDirectory );

        if ( ( entryTitle != null ) && ( entryDescription != null ) &&
                ( entryTitle.getDirectory(  ).getIdDirectory(  ) != DirectoryUtils.convertStringToInt( idDirectory ) ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_NO_REFRESH, AdminMessage.TYPE_STOP );
        }
        else if ( entryDescription.getDirectory(  ).getIdDirectory(  ) != DirectoryUtils.convertStringToInt( 
                    idDirectory ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_NO_REFRESH, AdminMessage.TYPE_STOP );
        }
        else if ( ( DirectoryUtils.convertStringToInt( idEntryFilter1 ) != DirectoryUtils.CONSTANT_ID_NULL ) &&
                ( request.getParameter( idEntryFilter1 ) == null ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_NO_REFRESH_FILTER, AdminMessage.TYPE_STOP );
        }
        else if ( ( DirectoryUtils.convertStringToInt( idEntryFilter2 ) != DirectoryUtils.CONSTANT_ID_NULL ) &&
                ( request.getParameter( idEntryFilter2 ) == null ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_NO_REFRESH_FILTER, AdminMessage.TYPE_STOP );
        }

        Directory directory = DirectoryHome.findByPrimaryKey( DirectoryUtils.convertStringToInt( idDirectory ),
                pluginDirectory );
        this.setName( directory.getTitle(  ) );
        this.setDescription( directory.getDescription(  ) );

        return null;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.portal.business.rss.IResourceRss#getDisplayCreateConfigForm( javax.servlet.http.HttpServletRequest,java.util.Locale)
     */
    public String getDisplayCreateConfigForm( HttpServletRequest request, Locale locale )
    {
        Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
        ReferenceList directoryList = DirectoryHome.getDirectoryList( pluginDirectory );
        HashMap<String, Object> model = new HashMap<String, Object>(  );

        String strEntryTypeImage = AppPropertiesService.getProperty( PROPERTY_ENTRY_DIRECTORY_TYPE_IMAGE );
        int entryTypeImage = DirectoryUtils.convertStringToInt( strEntryTypeImage );
        String strAcceptEntryType = AppPropertiesService.getProperty( PROPERTY_ACCEPT_DIRECTORY_TYPE );
        String[] strTabAcceptEntryType = strAcceptEntryType.split( "," );
        String strAcceptEntryTypeForLink = AppPropertiesService.getProperty( PROPERTY_ACCEPT_DIRECTORY_TYPE_FOR_LINK );
        String[] strTabAcceptEntryTypeForLink = strAcceptEntryTypeForLink.split( "," );

        int nIdDirectory = -1;

        if ( request.getParameter( PARAMETER_ID_DIRECTORY ) != null )
        {
            nIdDirectory = DirectoryUtils.convertStringToInt( request.getParameter( PARAMETER_ID_DIRECTORY ) );
            model.put( MARK_ENTRY_LIST_IMAGE_DEFAULT_ITEM,
                DirectoryUtils.convertStringToInt( request.getParameter( PARAMETER_ID_ENTRY_IMAGE ) ) );
            model.put( MARK_ENTRY_LIST_TITLE_DEFAULT_ITEM,
                DirectoryUtils.convertStringToInt( request.getParameter( PARAMETER_ID_ENTRY_TITLE ) ) );
            model.put( MARK_ENTRY_LIST_DESCRIPTION_DEFAULT_ITEM,
                DirectoryUtils.convertStringToInt( request.getParameter( PARAMETER_ID_ENTRY_DESCRIPTION ) ) );
            model.put( MARK_ENTRY_LIST_LINK_DEFAULT_ITEM,
                DirectoryUtils.convertStringToInt( request.getParameter( PARAMETER_ID_ENTRY_LINK ) ) );
            model.put( MARK_WORKFLOW_STATE_LIST_DEFAULT_ITEM,
                DirectoryUtils.convertStringToInt( request.getParameter( PARAMETER_ID_WORKFLOW_STATE ) ) );
        }
        else if ( !directoryList.isEmpty(  ) && ( directoryList.get( 0 ) != null ) )
        {
            nIdDirectory = DirectoryUtils.convertStringToInt( directoryList.get( 0 ).getCode(  ) );
            model.put( MARK_ENTRY_LIST_IMAGE_DEFAULT_ITEM, DirectoryUtils.CONSTANT_ID_NULL );
            model.put( MARK_ENTRY_LIST_TITLE_DEFAULT_ITEM, DirectoryUtils.CONSTANT_ID_NULL );
            model.put( MARK_ENTRY_LIST_DESCRIPTION_DEFAULT_ITEM, DirectoryUtils.CONSTANT_ID_NULL );
            model.put( MARK_ENTRY_LIST_LINK_DEFAULT_ITEM, DirectoryUtils.CONSTANT_ID_NULL );
            model.put( MARK_WORKFLOW_STATE_LIST_DEFAULT_ITEM, DirectoryUtils.CONSTANT_ID_NULL );
        }

        if ( nIdDirectory != DirectoryUtils.CONSTANT_ID_NULL )
        {
            ReferenceList referenceEntryLink = new ReferenceList(  );
            ReferenceList referenceEntryImage = new ReferenceList(  );
            ReferenceList referenceEntry = new ReferenceList(  );
            ReferenceList referenceEntryFilter = new ReferenceList(  );
            EntryFilter filter = new EntryFilter(  );
            filter.setIdDirectory( nIdDirectory );
            model.put( MARK_DIRECTORY_LIST_DEFAULT_ITEM, String.valueOf( nIdDirectory ) );

            List<IEntry> entryList = EntryHome.getEntryList( filter, pluginDirectory );

            for ( IEntry entry : entryList )
            {
                for ( int i = 0; i < strTabAcceptEntryType.length; i++ )
                {
                    if ( entry.getEntryType(  ).getIdType(  ) == Integer.parseInt( strTabAcceptEntryType[i] ) )
                    {
                        if ( entry.isMandatory(  ) )
                        {
                            referenceEntry.addItem( entry.getIdEntry(  ), entry.getTitle(  ) );
                        }

                        referenceEntryFilter.addItem( entry.getIdEntry(  ), entry.getTitle(  ) );
                    }
                }

                for ( int i = 0; i < strTabAcceptEntryTypeForLink.length; i++ )
                {
                    if ( entry.getEntryType(  ).getIdType(  ) == Integer.parseInt( strTabAcceptEntryTypeForLink[i] ) )
                    {
                        referenceEntryLink.addItem( entry.getIdEntry(  ), entry.getTitle(  ) );
                    }
                }

                if ( entry.getEntryType(  ).getIdType(  ) == entryTypeImage )
                {
                    referenceEntryImage.addItem( entry.getIdEntry(  ), entry.getTitle(  ) );
                }
            }

            if ( !referenceEntryLink.isEmpty(  ) )
            {
                referenceEntryLink.addItem( -1, "" );
                model.put( MARK_ENTRY_LIST_LINK, referenceEntryLink );
            }

            if ( !referenceEntryImage.isEmpty(  ) )
            {
                referenceEntryImage.addItem( -1, "" );
                model.put( MARK_ENTRY_LIST_IMAGE, referenceEntryImage );
            }

            if ( !referenceEntryFilter.isEmpty(  ) )
            {
                referenceEntryFilter.addItem( -1, "" );
                model.put( MARK_ENTRY_LIST_FILTER, referenceEntryFilter );
                model.put( MARK_LOCALE, locale );

                if ( request.getParameter( PARAMETER_ID_ENTRY_FILTER_1 ) != null )
                {
                    IEntry entry = EntryHome.findByPrimaryKey( DirectoryUtils.convertStringToInt( request.getParameter( 
                                    PARAMETER_ID_ENTRY_FILTER_1 ) ), pluginDirectory );
                    model.put( MARK_ENTRY_FILTER_1, entry );
                }

                if ( request.getParameter( PARAMETER_ID_ENTRY_FILTER_2 ) != null )
                {
                    IEntry entry = EntryHome.findByPrimaryKey( DirectoryUtils.convertStringToInt( request.getParameter( 
                                    PARAMETER_ID_ENTRY_FILTER_2 ) ), pluginDirectory );
                    model.put( MARK_ENTRY_FILTER_2, entry );
                }
            }

            model.put( MARK_ENTRY_LIST, referenceEntry );

            Directory directory = DirectoryHome.findByPrimaryKey( nIdDirectory, pluginDirectory );

            if ( WorkflowService.getInstance(  ).isAvailable(  ) && ( directory != null ) &&
                    ( directory.getIdWorkflow(  ) != DirectoryUtils.CONSTANT_ID_NULL ) )
            {
                Collection<State> listState = WorkflowService.getInstance(  )
                                                             .getAllStateByWorkflow( directory.getIdWorkflow(  ),
                        AdminUserService.getAdminUser( request ) );
                ReferenceList referenceListState = ReferenceList.convert( listState, ID, NAME, true );
                referenceListState.addItem( -1, "" );
                model.put( MARK_WORKFLOW_STATE_LIST, referenceListState );
            }
        }
        else
        {
            model.put( MARK_ENTRY_LIST_IMAGE_DEFAULT_ITEM, DirectoryUtils.CONSTANT_ID_NULL );
            model.put( MARK_ENTRY_LIST_TITLE_DEFAULT_ITEM, DirectoryUtils.CONSTANT_ID_NULL );
            model.put( MARK_ENTRY_LIST_DESCRIPTION_DEFAULT_ITEM, DirectoryUtils.CONSTANT_ID_NULL );
            model.put( MARK_ENTRY_LIST_LINK_DEFAULT_ITEM, DirectoryUtils.CONSTANT_ID_NULL );
            model.put( MARK_WORKFLOW_STATE_LIST_DEFAULT_ITEM, DirectoryUtils.CONSTANT_ID_NULL );
            model.put( MARK_DIRECTORY_LIST_DEFAULT_ITEM, DirectoryUtils.CONSTANT_ID_NULL );
            model.put( MARK_ENTRY_LIST, new ArrayList(  ) );
        }

        model.put( MARK_DIRECTORY_LIST, directoryList );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_EVALUATION_CREATE_CONFIG, locale, model );

        return template.getHtml(  );
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.portal.business.rss.IResourceRss#getDisplayModifyConfigForm( javax.servlet.http.HttpServletRequest , java.util.Locale)
     */
    public String getDisplayModifyConfigForm( HttpServletRequest request, Locale locale )
    {
        String strEntryTypeImage = AppPropertiesService.getProperty( PROPERTY_ENTRY_DIRECTORY_TYPE_IMAGE );
        int entryTypeImage = DirectoryUtils.convertStringToInt( strEntryTypeImage );

        String strAcceptEntryType = AppPropertiesService.getProperty( PROPERTY_ACCEPT_DIRECTORY_TYPE );
        String[] strTabAcceptEntryType = strAcceptEntryType.split( "," );

        String strAcceptEntryTypeForLink = AppPropertiesService.getProperty( PROPERTY_ACCEPT_DIRECTORY_TYPE_FOR_LINK );
        String[] strTabAcceptEntryTypeForLink = strAcceptEntryTypeForLink.split( "," );

        Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
        ReferenceList directoryList = DirectoryHome.getDirectoryList( pluginDirectory );

        DirectoryResourceRssConfig directoryResourceRssConfig = DirectoryResourceRssConfigHome.findByPrimaryKey( this.getId(  ),
                pluginDirectory );
        HashMap<String, Object> model = new HashMap<String, Object>(  );

        ReferenceList referenceEntryImage = new ReferenceList(  );
        ReferenceList referenceEntry = new ReferenceList(  );
        ReferenceList referenceEntryLink = new ReferenceList(  );
        ReferenceList referenceEntryFilter = new ReferenceList(  );
        EntryFilter filter = new EntryFilter(  );

        if ( request.getParameter( PARAMETER_ID_DIRECTORY ) != null )
        {
            int idDirectory = DirectoryUtils.convertStringToInt( request.getParameter( PARAMETER_ID_DIRECTORY ) );
            filter.setIdDirectory( idDirectory );
            model.put( MARK_DIRECTORY_LIST_DEFAULT_ITEM, idDirectory );

            Directory directory = DirectoryHome.findByPrimaryKey( idDirectory, pluginDirectory );

            if ( WorkflowService.getInstance(  ).isAvailable(  ) && ( directory != null ) &&
                    ( directory.getIdWorkflow(  ) != DirectoryUtils.CONSTANT_ID_NULL ) )
            {
                Collection<State> listState = WorkflowService.getInstance(  )
                                                             .getAllStateByWorkflow( directory.getIdWorkflow(  ),
                        AdminUserService.getAdminUser( request ) );
                ReferenceList referenceListState = ReferenceList.convert( listState, ID, NAME, true );
                referenceListState.addItem( -1, "" );
                model.put( MARK_WORKFLOW_STATE_LIST, referenceListState );
            }
        }
        else
        {
            Directory directory = DirectoryHome.findByPrimaryKey( directoryResourceRssConfig.getIdDirectory(  ),
                    pluginDirectory );
            filter.setIdDirectory( directory.getIdDirectory(  ) );
            model.put( MARK_DIRECTORY_LIST_DEFAULT_ITEM, directoryResourceRssConfig.getIdDirectory(  ) );

            if ( WorkflowService.getInstance(  ).isAvailable(  ) && ( directory != null ) &&
                    ( directory.getIdWorkflow(  ) != DirectoryUtils.CONSTANT_ID_NULL ) )
            {
                Collection<State> listState = WorkflowService.getInstance(  )
                                                             .getAllStateByWorkflow( directory.getIdWorkflow(  ),
                        AdminUserService.getAdminUser( request ) );
                ReferenceList referenceListState = ReferenceList.convert( listState, ID, NAME, true );
                referenceListState.addItem( -1, "" );
                model.put( MARK_WORKFLOW_STATE_LIST, referenceListState );
            }
        }

        List<IEntry> entryList = EntryHome.getEntryList( filter, pluginDirectory );

        for ( IEntry entry : entryList )
        {
            for ( int i = 0; i < strTabAcceptEntryType.length; i++ )
            {
                if ( entry.getEntryType(  ).getIdType(  ) == Integer.parseInt( strTabAcceptEntryType[i] ) )
                {
                    if ( entry.isMandatory(  ) )
                    {
                        referenceEntry.addItem( entry.getIdEntry(  ), entry.getTitle(  ) );
                    }

                    referenceEntryFilter.addItem( entry.getIdEntry(  ), entry.getTitle(  ) );
                }
            }

            for ( int i = 0; i < strTabAcceptEntryTypeForLink.length; i++ )
            {
                if ( entry.getEntryType(  ).getIdType(  ) == Integer.parseInt( strTabAcceptEntryTypeForLink[i] ) )
                {
                    referenceEntryLink.addItem( entry.getIdEntry(  ), entry.getTitle(  ) );
                }
            }

            if ( entry.getEntryType(  ).getIdType(  ) == entryTypeImage )
            {
                referenceEntryImage.addItem( entry.getIdEntry(  ), entry.getTitle(  ) );
            }
        }

        if ( !referenceEntryLink.isEmpty(  ) )
        {
            referenceEntryLink.addItem( -1, "" );
            model.put( MARK_ENTRY_LIST_LINK, referenceEntryLink );
        }

        if ( !referenceEntryImage.isEmpty(  ) )
        {
            referenceEntryImage.addItem( -1, "" );
            model.put( MARK_ENTRY_LIST_IMAGE, referenceEntryImage );
        }

        if ( !referenceEntryFilter.isEmpty(  ) )
        {
            referenceEntryFilter.addItem( -1, "" );
            model.put( MARK_ENTRY_LIST_FILTER, referenceEntryFilter );
            model.put( MARK_LOCALE, locale );
        }

        if ( request.getParameter( PARAMETER_ID_DIRECTORY ) != null )
        {
            model.put( MARK_ENTRY_LIST_IMAGE_DEFAULT_ITEM,
                DirectoryUtils.convertStringToInt( request.getParameter( PARAMETER_ID_ENTRY_IMAGE ) ) );
            model.put( MARK_ENTRY_LIST_TITLE_DEFAULT_ITEM,
                DirectoryUtils.convertStringToInt( request.getParameter( PARAMETER_ID_ENTRY_TITLE ) ) );
            model.put( MARK_ENTRY_LIST_DESCRIPTION_DEFAULT_ITEM,
                DirectoryUtils.convertStringToInt( request.getParameter( PARAMETER_ID_ENTRY_DESCRIPTION ) ) );
            model.put( MARK_ENTRY_LIST_LINK_DEFAULT_ITEM,
                DirectoryUtils.convertStringToInt( request.getParameter( PARAMETER_ID_ENTRY_LINK ) ) );
            model.put( MARK_WORKFLOW_STATE_LIST_DEFAULT_ITEM,
                DirectoryUtils.convertStringToInt( request.getParameter( PARAMETER_ID_WORKFLOW_STATE ) ) );

            if ( request.getParameter( PARAMETER_ID_ENTRY_FILTER_1 ) != null )
            {
                IEntry entry = EntryHome.findByPrimaryKey( DirectoryUtils.convertStringToInt( request.getParameter( 
                                PARAMETER_ID_ENTRY_FILTER_1 ) ), pluginDirectory );
                model.put( MARK_ENTRY_FILTER_1, entry );
            }

            if ( request.getParameter( PARAMETER_ID_ENTRY_FILTER_2 ) != null )
            {
                IEntry entry = EntryHome.findByPrimaryKey( DirectoryUtils.convertStringToInt( request.getParameter( 
                                PARAMETER_ID_ENTRY_FILTER_2 ) ), pluginDirectory );
                model.put( MARK_ENTRY_FILTER_2, entry );
            }
        }
        else if ( directoryResourceRssConfig != null )
        {
            model.put( MARK_ENTRY_LIST_IMAGE_DEFAULT_ITEM, directoryResourceRssConfig.getIdEntryImage(  ) );
            model.put( MARK_ENTRY_LIST_TITLE_DEFAULT_ITEM, directoryResourceRssConfig.getIdEntryTitle(  ) );
            model.put( MARK_ENTRY_LIST_DESCRIPTION_DEFAULT_ITEM, directoryResourceRssConfig.getIdEntryDescription(  ) );
            model.put( MARK_WORKFLOW_STATE_LIST_DEFAULT_ITEM, directoryResourceRssConfig.getIdWorkflowState(  ) );
            model.put( MARK_ENTRY_LIST_LINK_DEFAULT_ITEM, directoryResourceRssConfig.getIdEntryLink(  ) );

            if ( directoryResourceRssConfig.getIdEntryFilter1(  ) != DirectoryUtils.CONSTANT_ID_NULL )
            {
                IEntry entry = EntryHome.findByPrimaryKey( directoryResourceRssConfig.getIdEntryFilter1(  ),
                        pluginDirectory );
                model.put( MARK_ENTRY_FILTER_1, entry );

                RecordField recordField = new RecordField(  );
                recordField.setEntry( entry );

                if ( DirectoryUtils.convertStringToInt( directoryResourceRssConfig.getValueFilter1(  ) ) != DirectoryUtils.CONSTANT_ID_NULL )
                {
                    Field field = FieldHome.findByPrimaryKey( DirectoryUtils.convertStringToInt( 
                                directoryResourceRssConfig.getValueFilter1(  ) ), pluginDirectory );
                    recordField.setField( field );
                }

                recordField.setValue( directoryResourceRssConfig.getValueFilter1(  ) );

                List<RecordField> recordFieldList = new ArrayList<RecordField>(  );
                recordFieldList.add( recordField );
                model.put( MARK_RECORD_FIELD_FILTER_1, recordFieldList );
            }

            if ( directoryResourceRssConfig.getIdEntryFilter2(  ) != DirectoryUtils.CONSTANT_ID_NULL )
            {
                IEntry entry = EntryHome.findByPrimaryKey( directoryResourceRssConfig.getIdEntryFilter2(  ),
                        pluginDirectory );
                model.put( MARK_ENTRY_FILTER_2, entry );

                RecordField recordField = new RecordField(  );
                recordField.setEntry( entry );

                if ( DirectoryUtils.convertStringToInt( directoryResourceRssConfig.getValueFilter2(  ) ) != DirectoryUtils.CONSTANT_ID_NULL )
                {
                    Field field = FieldHome.findByPrimaryKey( DirectoryUtils.convertStringToInt( 
                                directoryResourceRssConfig.getValueFilter2(  ) ), pluginDirectory );
                    recordField.setField( field );
                }

                recordField.setValue( directoryResourceRssConfig.getValueFilter2(  ) );

                List<RecordField> recordFieldList = new ArrayList<RecordField>(  );
                recordFieldList.add( recordField );
                model.put( MARK_RECORD_FIELD_FILTER_2, recordFieldList );
            }
        }
        else
        {
            model.put( MARK_ENTRY_LIST_IMAGE_DEFAULT_ITEM, DirectoryUtils.CONSTANT_ID_NULL );
            model.put( MARK_DIRECTORY_LIST_DEFAULT_ITEM, DirectoryUtils.CONSTANT_ID_NULL );
            model.put( MARK_ENTRY_LIST_TITLE_DEFAULT_ITEM, DirectoryUtils.CONSTANT_ID_NULL );
            model.put( MARK_ENTRY_LIST_DESCRIPTION_DEFAULT_ITEM, DirectoryUtils.CONSTANT_ID_NULL );
            model.put( MARK_ENTRY_LIST_LINK_DEFAULT_ITEM, DirectoryUtils.CONSTANT_ID_NULL );
        }

        model.put( MARK_DIRECTORY_LIST, directoryList );
        model.put( MARK_ENTRY_LIST, referenceEntry );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_EVALUATION_MODIFY_CONFIG, locale, model );

        return template.getHtml(  );
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.portal.business.rss.IResourceRss#createHtmlRss( )
     */
    @Deprecated
    public String createHtmlRss(  )
    {
        HashMap<String, Object> model = new HashMap<String, Object>(  );
        Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
        DirectoryResourceRssConfig config = DirectoryResourceRssConfigHome.findByPrimaryKey( this.getId(  ),
                pluginDirectory );
        Directory directory = DirectoryHome.findByPrimaryKey( config.getIdDirectory(  ), pluginDirectory );

        // Update the head of the document
        String strRssFileLanguage = AppPropertiesService.getProperty( PROPERTY_SITE_LANGUAGE );

        String strWebAppUrl = AppPropertiesService.getProperty( PROPERTY_WEBAPP_PROD_URL );
        String strSiteUrl = strWebAppUrl;
        model.put( MARK_RSS_SITE_NAME, directory.getTitle(  ) );
        model.put( MARK_RSS_FILE_LANGUAGE, strRssFileLanguage );
        model.put( MARK_RSS_SITE_URL, strSiteUrl );
        model.put( MARK_RSS_SITE_DESCRIPTION, directory.getDescription(  ) );

        Locale locale = new Locale( strRssFileLanguage );

        RecordFieldFilter filter = new RecordFieldFilter(  );
        filter.setIdDirectory( directory.getIdDirectory(  ) );

        HashMap<String, List<RecordField>> mapSearchQuery = new HashMap<String, List<RecordField>>(  );

        if ( config.getIdEntryFilter1(  ) != DirectoryUtils.CONSTANT_ID_NULL )
        {
            IEntry entry = EntryHome.findByPrimaryKey( config.getIdEntryFilter1(  ), pluginDirectory );
            RecordField recordField = new RecordField(  );
            recordField.setEntry( entry );

            if ( DirectoryUtils.convertStringToInt( config.getValueFilter1(  ) ) != DirectoryUtils.CONSTANT_ID_NULL )
            {
                Field field = FieldHome.findByPrimaryKey( DirectoryUtils.convertStringToInt( config.getValueFilter1(  ) ),
                        pluginDirectory );
                recordField.setField( field );
            }

            recordField.setValue( config.getValueFilter1(  ) );

            List<RecordField> recordFieldList = new ArrayList<RecordField>(  );
            recordFieldList.add( recordField );

            mapSearchQuery.put( Integer.toString( config.getIdEntryFilter1(  ) ), recordFieldList );
        }

        if ( config.getIdEntryFilter2(  ) != DirectoryUtils.CONSTANT_ID_NULL )
        {
            IEntry entry = EntryHome.findByPrimaryKey( config.getIdEntryFilter2(  ), pluginDirectory );
            RecordField recordField = new RecordField(  );
            recordField.setEntry( entry );

            if ( DirectoryUtils.convertStringToInt( config.getValueFilter2(  ) ) != DirectoryUtils.CONSTANT_ID_NULL )
            {
                Field field = FieldHome.findByPrimaryKey( DirectoryUtils.convertStringToInt( config.getValueFilter2(  ) ),
                        pluginDirectory );
                recordField.setField( field );
            }

            recordField.setValue( config.getValueFilter2(  ) );

            List<RecordField> recordFieldList = new ArrayList<RecordField>(  );
            recordFieldList.add( recordField );

            mapSearchQuery.put( Integer.toString( config.getIdEntryFilter2(  ) ), recordFieldList );
        }

        List<Integer> listResultRecordId = DirectorySearchService.getInstance(  )
                                                                 .getSearchResults( directory, mapSearchQuery, null,
                null, null, filter, pluginDirectory );

        if ( ( directory.getIdWorkflow(  ) != DirectoryUtils.CONSTANT_ID_NULL ) &&
                ( config.getIdWorkflowState(  ) != DirectoryUtils.CONSTANT_ID_NULL ) &&
                WorkflowService.getInstance(  ).isAvailable(  ) )
        {
            List<Integer> listTmpResultRecordId = WorkflowService.getInstance(  )
                                                                 .getAuthorizedResourceList( Record.WORKFLOW_RESOURCE_TYPE,
                    directory.getIdWorkflow(  ), config.getIdWorkflowState(  ),
                    Integer.valueOf( directory.getIdDirectory(  ) ), null );

            listResultRecordId = DirectoryUtils.retainAll( listResultRecordId, listTmpResultRecordId );
        }

        List<HashMap> listItem = new ArrayList<HashMap>(  );
        IRecordService recordService = SpringContextService.getBean( RecordService.BEAN_SERVICE );

        for ( Integer idRecord : listResultRecordId )
        {
            Record record = recordService.findByPrimaryKey( idRecord, pluginDirectory );
            RecordFieldFilter recordFieldFilter = new RecordFieldFilter(  );
            recordFieldFilter.setIdDirectory( config.getIdDirectory(  ) );
            recordFieldFilter.setIdRecord( idRecord );

            recordFieldFilter.setIdEntry( config.getIdEntryTitle(  ) );

            List<RecordField> recordFieldList = RecordFieldHome.getRecordFieldList( recordFieldFilter, pluginDirectory );
            RecordField recordFieldTitle = recordFieldList.get( 0 );

            recordFieldFilter.setIdEntry( config.getIdEntryDescription(  ) );

            List<RecordField> recordFieldList2 = RecordFieldHome.getRecordFieldList( recordFieldFilter, pluginDirectory );
            RecordField recordFieldDescription = recordFieldList2.get( 0 );

            recordFieldFilter.setIdEntry( config.getIdEntryImage(  ) );

            List<RecordField> recordFieldListImage = RecordFieldHome.getRecordFieldList( recordFieldFilter,
                    pluginDirectory );
            RecordField recordFieldImage = null;

            if ( !recordFieldListImage.isEmpty(  ) )
            {
                recordFieldImage = recordFieldListImage.get( 0 );
            }

            recordFieldFilter.setIdEntry( config.getIdEntryLink(  ) );

            List<RecordField> recordFieldListLink = RecordFieldHome.getRecordFieldList( recordFieldFilter,
                    pluginDirectory );
            RecordField recordFieldLink = null;

            if ( !recordFieldListLink.isEmpty(  ) )
            {
                recordFieldLink = recordFieldListLink.get( 0 );
            }

            if ( ( recordFieldTitle != null ) && ( recordFieldDescription != null ) )
            {
                HashMap<String, Object> item = new HashMap<String, Object>(  );
                item.put( MARK_RSS_SITE_ID_RECORD, idRecord );

                if ( recordFieldTitle.getValue(  ) != null )
                {
                    item.put( MARK_RSS_SITE_TITLE, recordFieldTitle.getValue(  ) );
                }
                else
                {
                    item.put( MARK_RSS_SITE_TITLE, recordFieldTitle.getField(  ).getValue(  ) );
                }

                if ( recordFieldDescription.getValue(  ) != null )
                {
                    item.put( MARK_RSS_SITE_DESCRIPTION_ITEM, recordFieldDescription.getValue(  ) );
                }
                else
                {
                    item.put( MARK_RSS_SITE_DESCRIPTION_ITEM, recordFieldDescription.getField(  ).getValue(  ) );
                }

                if ( ( recordFieldImage != null ) && ( recordFieldImage.getFile(  ) != null ) )
                {
                    item.put( MARK_RSS_SITE_IMAGE_ITEM, recordFieldImage.getFile(  ).getIdFile(  ) );

                    if ( ( recordFieldImage.getEntry(  ) != null ) &&
                            ( recordFieldImage.getEntry(  ).getDisplayHeight(  ) != -1 ) )
                    {
                        item.put( MARK_RSS_SITE_IMAGE_HEIGHT_ITEM, recordFieldImage.getEntry(  ).getDisplayHeight(  ) );
                    }

                    if ( ( recordFieldImage.getEntry(  ) != null ) &&
                            ( recordFieldImage.getEntry(  ).getDisplayWidth(  ) != -1 ) )
                    {
                        item.put( MARK_RSS_SITE_IMAGE_WIDTH_ITEM, recordFieldImage.getEntry(  ).getDisplayWidth(  ) );
                    }
                }

                if ( ( recordFieldLink != null ) && ( recordFieldLink.getValue(  ) != null ) )
                {
                    item.put( MARK_RSS_SITE_LINK_ITEM, recordFieldLink.getValue(  ) );
                }

                item.put( MARK_RSS_SITE_DATE, record.getDateCreation(  ) );
                item.put( MARK_RSS_SITE_DATE_MODIFICATION, record.getDateModification(  ) );
                listItem.add( item );
            }
        }

        model.put( MARK_ITEM_LIST, listItem );
        model.put( MARK_RSS_SITE_ID_DIRECTORY, config.getIdDirectory(  ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_PUSH_RSS_XML, locale, model );

        return template.getHtml(  );
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.portal.business.rss.IResourceRss#deleteResourceRssConfig( int )
     */
    public void deleteResourceRssConfig( int idResourceRss )
    {
        Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
        DirectoryResourceRssConfigHome.remove( idResourceRss, pluginDirectory );
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.portal.business.rss.IResourceRss#getParameterToApply( javax.servlet.http.HttpServletRequest )
     */
    public Map<String, String> getParameterToApply( HttpServletRequest request )
    {
        Map<String, String> map = new HashMap<String, String>(  );

        map.put( PARAMETER_ID_DIRECTORY, request.getParameter( PARAMETER_ID_DIRECTORY ) );
        map.put( PARAMETER_ID_ENTRY_FILTER_1, request.getParameter( PARAMETER_ID_ENTRY_FILTER_1 ) );
        map.put( PARAMETER_ID_ENTRY_FILTER_2, request.getParameter( PARAMETER_ID_ENTRY_FILTER_2 ) );
        map.put( PARAMETER_ID_ENTRY_TITLE, request.getParameter( PARAMETER_ID_ENTRY_TITLE ) );
        map.put( PARAMETER_ID_ENTRY_DESCRIPTION, request.getParameter( PARAMETER_ID_ENTRY_DESCRIPTION ) );
        map.put( PARAMETER_ID_ENTRY_IMAGE, request.getParameter( PARAMETER_ID_ENTRY_IMAGE ) );
        map.put( PARAMETER_ID_ENTRY_LINK, request.getParameter( PARAMETER_ID_ENTRY_LINK ) );
        map.put( PARAMETER_ID_WORKFLOW_STATE, request.getParameter( PARAMETER_ID_WORKFLOW_STATE ) );

        return map;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.portal.business.rss.IResourceRss#checkResource(  )
     */
    public boolean checkResource(  )
    {
        Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
        DirectoryResourceRssConfig config = DirectoryResourceRssConfigHome.findByPrimaryKey( this.getId(  ),
                pluginDirectory );
        Directory directory = DirectoryHome.findByPrimaryKey( config.getIdDirectory(  ), pluginDirectory );
        IEntry entryTitle = EntryHome.findByPrimaryKey( config.getIdEntryTitle(  ), pluginDirectory );
        IEntry entryDescription = EntryHome.findByPrimaryKey( config.getIdEntryDescription(  ), pluginDirectory );

        return ( ( directory != null ) && ( entryDescription != null ) && ( entryTitle != null ) );
    }

    /**
     *
     *{@inheritDoc}
     */
    @Override
    public IFeedResource getFeed(  )
    {
        // Update the head of the document
        String strRssFileLanguage = AppPropertiesService.getProperty( PROPERTY_SITE_LANGUAGE );

        String strWebAppUrl = AppPropertiesService.getProperty( PROPERTY_WEBAPP_PROD_URL );
        String strSiteUrl = strWebAppUrl;

        Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
        DirectoryResourceRssConfig config = DirectoryResourceRssConfigHome.findByPrimaryKey( this.getId(  ),
                pluginDirectory );
        Directory directory = DirectoryHome.findByPrimaryKey( config.getIdDirectory(  ), pluginDirectory );

        IFeedResource resource = new FeedResource(  );
        resource.setTitle( directory.getTitle(  ) );
        resource.setDescription( directory.getDescription(  ) );
        resource.setLink( strSiteUrl );
        resource.setLanguage( strRssFileLanguage );

        IFeedResourceImage image = new FeedResourceImage(  );
        image.setUrl( strSiteUrl + "/images/local/skin/valid-rss.png" );
        image.setLink( strSiteUrl );
        image.setTitle( directory.getTitle(  ) );

        resource.setImage( image );

        Locale locale = new Locale( strRssFileLanguage );

        RecordFieldFilter filter = new RecordFieldFilter(  );
        filter.setIdDirectory( directory.getIdDirectory(  ) );
        filter.setSortOrder( RecordFieldFilter.ORDER_DESC );

        HashMap<String, List<RecordField>> mapSearchQuery = new HashMap<String, List<RecordField>>(  );

        if ( config.getIdEntryFilter1(  ) != DirectoryUtils.CONSTANT_ID_NULL )
        {
            IEntry entry = EntryHome.findByPrimaryKey( config.getIdEntryFilter1(  ), pluginDirectory );
            RecordField recordField = new RecordField(  );
            recordField.setEntry( entry );

            if ( DirectoryUtils.convertStringToInt( config.getValueFilter1(  ) ) != DirectoryUtils.CONSTANT_ID_NULL )
            {
                Field field = FieldHome.findByPrimaryKey( DirectoryUtils.convertStringToInt( config.getValueFilter1(  ) ),
                        pluginDirectory );
                recordField.setField( field );
            }

            recordField.setValue( config.getValueFilter1(  ) );

            List<RecordField> recordFieldList = new ArrayList<RecordField>(  );
            recordFieldList.add( recordField );

            mapSearchQuery.put( Integer.toString( config.getIdEntryFilter1(  ) ), recordFieldList );
        }

        if ( config.getIdEntryFilter2(  ) != DirectoryUtils.CONSTANT_ID_NULL )
        {
            IEntry entry = EntryHome.findByPrimaryKey( config.getIdEntryFilter2(  ), pluginDirectory );
            RecordField recordField = new RecordField(  );
            recordField.setEntry( entry );

            if ( DirectoryUtils.convertStringToInt( config.getValueFilter2(  ) ) != DirectoryUtils.CONSTANT_ID_NULL )
            {
                Field field = FieldHome.findByPrimaryKey( DirectoryUtils.convertStringToInt( config.getValueFilter2(  ) ),
                        pluginDirectory );
                recordField.setField( field );
            }

            recordField.setValue( config.getValueFilter2(  ) );

            List<RecordField> recordFieldList = new ArrayList<RecordField>(  );
            recordFieldList.add( recordField );

            mapSearchQuery.put( Integer.toString( config.getIdEntryFilter2(  ) ), recordFieldList );
        }

        List<Integer> listResultRecordId = DirectorySearchService.getInstance(  )
                                                                 .getSearchResults( directory, mapSearchQuery, null,
                null, null, filter, pluginDirectory );

        if ( ( directory.getIdWorkflow(  ) != DirectoryUtils.CONSTANT_ID_NULL ) &&
                ( config.getIdWorkflowState(  ) != DirectoryUtils.CONSTANT_ID_NULL ) &&
                WorkflowService.getInstance(  ).isAvailable(  ) )
        {
            List<Integer> listTmpResultRecordId = WorkflowService.getInstance(  )
                                                                 .getAuthorizedResourceList( Record.WORKFLOW_RESOURCE_TYPE,
                    directory.getIdWorkflow(  ), config.getIdWorkflowState(  ),
                    Integer.valueOf( directory.getIdDirectory(  ) ), null );

            listResultRecordId = DirectoryUtils.retainAll( listResultRecordId, listTmpResultRecordId );
        }

        List<IFeedResourceItem> listItems = new ArrayList<IFeedResourceItem>(  );
        IRecordService recordService = SpringContextService.getBean( RecordService.BEAN_SERVICE );

        for ( Integer idRecord : listResultRecordId )
        {
            Record record = recordService.findByPrimaryKey( idRecord, pluginDirectory );
            RecordFieldFilter recordFieldFilter = new RecordFieldFilter(  );
            recordFieldFilter.setIdDirectory( config.getIdDirectory(  ) );
            recordFieldFilter.setIdRecord( idRecord );

            recordFieldFilter.setIdEntry( config.getIdEntryTitle(  ) );

            List<RecordField> recordFieldList = RecordFieldHome.getRecordFieldList( recordFieldFilter, pluginDirectory );
            RecordField recordFieldTitle = recordFieldList.get( 0 );

            recordFieldFilter.setIdEntry( config.getIdEntryDescription(  ) );

            List<RecordField> recordFieldList2 = RecordFieldHome.getRecordFieldList( recordFieldFilter, pluginDirectory );
            RecordField recordFieldDescription = recordFieldList2.get( 0 );

            recordFieldFilter.setIdEntry( config.getIdEntryImage(  ) );

            List<RecordField> recordFieldListImage = RecordFieldHome.getRecordFieldList( recordFieldFilter,
                    pluginDirectory );
            RecordField recordFieldImage = null;

            if ( !recordFieldListImage.isEmpty(  ) )
            {
                recordFieldImage = recordFieldListImage.get( 0 );
            }

            recordFieldFilter.setIdEntry( config.getIdEntryLink(  ) );

            List<RecordField> recordFieldListLink = RecordFieldHome.getRecordFieldList( recordFieldFilter,
                    pluginDirectory );
            RecordField recordFieldLink = null;

            if ( !recordFieldListLink.isEmpty(  ) )
            {
                recordFieldLink = recordFieldListLink.get( 0 );
            }

            if ( ( recordFieldTitle != null ) && ( recordFieldDescription != null ) )
            {
                IFeedResourceItem item = new FeedResourceItem(  );

                // image handling
                // the image is put right before the description
                String strImageDescription;

                if ( ( recordFieldImage != null ) && ( recordFieldImage.getFile(  ) != null ) )
                {
                    Map<String, Object> model = new HashMap<String, Object>(  );
                    model.put( MARK_RSS_SITE_IMAGE_ITEM, recordFieldImage.getFile(  ).getIdFile(  ) );
                    model.put( MARK_RSS_SITE_URL, strSiteUrl );

                    if ( ( recordFieldImage.getEntry(  ) != null ) &&
                            ( recordFieldImage.getEntry(  ).getDisplayHeight(  ) != -1 ) )
                    {
                        model.put( MARK_RSS_SITE_IMAGE_HEIGHT_ITEM, recordFieldImage.getEntry(  ).getDisplayHeight(  ) );
                    }

                    if ( ( recordFieldImage.getEntry(  ) != null ) &&
                            ( recordFieldImage.getEntry(  ).getDisplayWidth(  ) != -1 ) )
                    {
                        model.put( MARK_RSS_SITE_IMAGE_WIDTH_ITEM, recordFieldImage.getEntry(  ).getDisplayWidth(  ) );
                    }

                    HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_RSS_IMAGE, locale, model );
                    strImageDescription = template.getHtml(  );
                }
                else
                {
                    strImageDescription = EMPTY_STRING;
                }

                if ( recordFieldTitle.getValue(  ) != null )
                {
                    item.setTitle( recordFieldTitle.getValue(  ) );
                }
                else
                {
                    item.setTitle( recordFieldTitle.getField(  ).getValue(  ) );
                }

                if ( recordFieldDescription.getValue(  ) != null )
                {
                    item.setDescription( strImageDescription + recordFieldDescription.getValue(  ) );
                }
                else
                {
                    item.setDescription( strImageDescription + recordFieldDescription.getField(  ).getValue(  ) );
                }

                if ( ( recordFieldLink != null ) && ( recordFieldLink.getValue(  ) != null ) )
                {
                    item.setLink( recordFieldLink.getValue(  ) );
                }
                else
                {
                    UrlItem urlItem = new UrlItem( strSiteUrl + "/jsp/site/Portal.jsp" );
                    urlItem.addParameter( PARAMETER_PAGE, CONSTANT_DIRECTORY );
                    urlItem.addParameter( PARAMETER_ID_DIRECTORY_RECORD, record.getIdRecord(  ) );
                    urlItem.addParameter( PARAMETER_VIEW_DIRECTORY_RECORD, directory.getIdDirectory(  ) );

                    item.setLink( urlItem.getUrl(  ) );
                }

                item.setGUID( item.getLink(  ) );

                item.setDate( record.getDateCreation(  ) );

                listItems.add( item );
            }
        }

        resource.setItems( listItems );

        return resource;
    }
}

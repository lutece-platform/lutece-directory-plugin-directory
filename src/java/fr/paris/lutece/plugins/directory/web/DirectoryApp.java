/*
 * Copyright (c) 2002-2014, Mairie de Paris
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.directory.business.Directory;
import fr.paris.lutece.plugins.directory.business.DirectoryFilter;
import fr.paris.lutece.plugins.directory.business.DirectoryHome;
import fr.paris.lutece.plugins.directory.business.DirectoryXsl;
import fr.paris.lutece.plugins.directory.business.DirectoryXslHome;
import fr.paris.lutece.plugins.directory.business.EntryFilter;
import fr.paris.lutece.plugins.directory.business.EntryHome;
import fr.paris.lutece.plugins.directory.business.File;
import fr.paris.lutece.plugins.directory.business.FileHome;
import fr.paris.lutece.plugins.directory.business.IEntry;
import fr.paris.lutece.plugins.directory.business.PhysicalFile;
import fr.paris.lutece.plugins.directory.business.PhysicalFileHome;
import fr.paris.lutece.plugins.directory.business.Record;
import fr.paris.lutece.plugins.directory.business.RecordField;
import fr.paris.lutece.plugins.directory.business.RecordFieldFilter;
import fr.paris.lutece.plugins.directory.business.RecordFieldHome;
import fr.paris.lutece.plugins.directory.service.record.IRecordService;
import fr.paris.lutece.plugins.directory.service.record.RecordService;
import fr.paris.lutece.plugins.directory.utils.DirectoryErrorException;
import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.plugins.directory.web.action.DirectorySiteSearchFields;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.portal.service.content.XPageAppService;
import fr.paris.lutece.portal.service.html.XmlTransformerService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.SiteMessage;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.message.SiteMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.portal.PortalService;
import fr.paris.lutece.portal.service.search.SearchEngine;
import fr.paris.lutece.portal.service.search.SearchResult;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.security.UserNotSignedException;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
import fr.paris.lutece.portal.web.xpages.XPage;
import fr.paris.lutece.portal.web.xpages.XPageApplication;
import fr.paris.lutece.util.UniqueIDGenerator;
import fr.paris.lutece.util.date.DateUtil;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.http.SecurityUtil;
import fr.paris.lutece.util.url.UrlItem;
import fr.paris.lutece.util.xml.XmlUtil;


/**
 * This class manages DirectoryApp page.
 */
public class DirectoryApp implements XPageApplication
{
    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 3679144166666894465L;

    // templates
    private static final String TEMPLATE_XPAGE_FRAME_DIRECTORY = "skin/plugins/directory/directory_frame.html";
    private static final String TEMPLATE_XPAGE_VIEW_ALL_DIRECTORIES = "skin/plugins/directory/view_all_directories.html";
    private static final String TEMPLATE_XML_FORM_SEARCH = "skin/plugins/directory/xml_form_search.html";

    // properties for page titles and path label
    private static final String PROPERTY_XPAGE_PAGETITLE = "directory.xpage.pagetitle";
    private static final String PROPERTY_XPAGE_PATHLABEL = "directory.xpage.pathlabel";
    private static final String PROPERTY_PAGE_APPLICATION_ID = "directory.xpage.applicationId";
    private static final String PROPERTY_DIRECTORY_FRAME_TITLE_DESCRIPTIVE = "directory.directory_frame.title_descriptive";
    private static final String PROPERTY_DIRECTORY_FRAME_TITLE_BACK_SEARCH = "directory.directory_frame.title_back_search";
    private static final String PROPERTY_DIRECTORY_FRAME_LABEL_BACK_SEARCH = "directory.directory_frame.label_back_search";
    private static final String PROPERTY_DIRECTORY_FRAME_TITLE_BACK_RECORD = "directory.directory_frame.title_back_record";
    private static final String PROPERTY_DIRECTORY_FRAME_LABEL_BACK_RECORD = "directory.directory_frame.label_back_record";
    private static final String PROPERTY_DIRECTORY_RESULT_TITLE_SORT_ASC = "directory.directory_result_list.tiltle_sort_asc";
    private static final String PROPERTY_DIRECTORY_RESULT_TITLE_SORT_DESC = "directory.directory_result_list.tiltle_sort_desc";
    private static final String PROPERTY_ENTRY_TYPE_GEOLOCATION = "directory.entry_type.geolocation";
    private static final String PROPERTY_DISPLAY_ONE_RESULT_DIRECTLY = "directory.display_one_result_directly";

    // request parameters
    private static final String PARAMETER_VIEW_DIRECTORY_RECORD = "view_directory_record";
    private static final String PARAMETER_ID_DIRECTORY = "id_directory";
    private static final String PARAMETER_SEARCH = "search";
    private static final String PARAMETER_ID_DIRECTORY_RECORD = "id_directory_record";
    private static final String INIT_MAP_QUERY = "init_map_query";
    private static final String PARAMETER_DATE_BEGIN = "date_after";
    private static final String PARAMETER_DATE_END = "date_before";
    private static final String PARAMETER_OPERATOR = "default_operator";
    private static final String PARAMETER_QUERY = "query";

    // message
    private static final String MESSAGE_ERROR = "directory.message.Error";
    private static final String MESSAGE_ACCESS_DENIED = "directory.message.accessDenied";
    private static final String MESSAGE_INVALID_SEARCH_TERMS = "directory.message.invalidSearchTerms";
    private static final String MESSAGE_SEARCH_DATE_VALIDITY = "directory.message.dateValidity";
    private static final String MESSAGE_SEARCH_OPERATOR_VALIDITY = "directory.message.operatorValidity";

    // Markers
    private static final String MARK_UNAVAILABILITY_MESSAGE = "unavailability_message";
    private static final String MARK_ENTRY_LIST_GEOLOCATION = "entry_list_geolocation";
    private static final String MARK_ENTRY_LIST_FORM_MAIN_SEARCH = "entry_list_form_main_search";
    private static final String MARK_ENTRY_LIST_FORM_COMPLEMENTARY_SEARCH = "entry_list_form_complementary_search";
    private static final String MARK_MAP_ID_ENTRY_LIST_RECORD_FIELD = "map_id_entry_list_record_field";
    private static final String MARK_DIRECTORY = "directory";
    private static final String MARK_STR_FORM_SEARCH = "str_form_search";
    private static final String MARK_STR_RESULT_LIST = "str_result_list";
    private static final String MARK_STR_RESULT_RECORD = "str_result_record";
    private static final String MARK_LOCALE = "locale";
    private static final String MARK_PAGINATOR = "paginator";
    private static final String MARK_DIRECTORY_LIST = "directory_list";
    private static final String MARK_ONE_RESULT = "one_result";
    private static final String MARK_ONE_SESSION_ID = "one_session_id";
    private static final String MARK_NEW_SEARCH = "new_search";
    private static final String MARK_DATE_BEGIN = "date_after";
    private static final String MARK_DATE_END = "date_before";
    private static final String MARK_OPERATOR = "operator";
    private static final String MARK_QUERY = "query";
    private static final String MARK_RESULT_LIST = "result_list";
    private static final String MARK_IS_EXTEND_INSTALLED = "isExtendInstalled";

    // Markers XSL
    private static final String MARK_TITLE_DESCRIPTIVE = "title-descriptive";
    private static final String MARK_TITLE_BACK_SEARCH = "title-back-search";
    private static final String MARK_LABEL_BACK_SEARCH = "label-back-search";
    private static final String MARK_ID_DIRECTORY = "id-directory";
    private static final String MARK_TITLE_BACK_RECORD = "title-back-record";
    private static final String MARK_LABEL_BACK_RECORD = "label-back-record";
    private static final String MARK_ID_LAST_RECORD = "id-last-record";
    private static final String MARK_ID_LAST_DIRECTORY = "id-last-directory";
    private static final String MARK_TITLE_SORT_ASC = "title-sort-asc";
    private static final String MARK_TITLE_SORT_DESC = "title-sort-desc";

    // session filter
    private static final String SESSION_ONE_RECORD_ID = "one_record_id";
    private static final String SESSION_ID_LAST_RECORD = "id_last_record";
    private static final String SESSION_ID_LAST_DIRECTORY = "id_last_directory";
    private static final String SESSION_DIRECTORY_SITE_SEARCH_FIELDS = "search_fields";

    // message
    private static final String MESSAGE_DIRECTORY_ERROR = "directory.message.directory_error";
    private static final String MESSAGE_DIRECTORY_ERROR_MANDATORY_FIELD = "directory.message.directory_error.mandatory.field";

    // Properties
    private static final String XSL_UNIQUE_PREFIX_ID = UniqueIDGenerator.getNewId( ) + "directory-";
    private static final String OPERATOR_AND = "AND";
    private static final String OPERATOR_OR = "OR";
    private static final String BEAN_SEARCH_ENGINE = "searchEngine";

    // Tag
    private static final String TAG_DISPLAY = "display";
    private static final String TAG_YES = "yes";
    private static final String TAG_NO = "no";
    private static final String TAG_STATUS = "status";

    /**
     * Returns the Directory XPage result content depending on the request
     * parameters and the current mode.
     * 
     * @param request
     *            The HTTP request.
     * @param nMode
     *            The current mode.
     * @param plugin
     *            The Plugin.
     * @return The page content.
     * @throws SiteMessageException
     *             the SiteMessageException
     * @throws UserNotSignedException
     *             the UserNotSignedException
     */
    @Override
    public XPage getPage( HttpServletRequest request, int nMode, Plugin plugin ) throws SiteMessageException,
            UserNotSignedException
    {
        XPage page = new XPage( );

        page.setTitle( I18nService.getLocalizedString( PROPERTY_XPAGE_PAGETITLE, request.getLocale( ) ) );
        page.setPathLabel( I18nService.getLocalizedString( PROPERTY_XPAGE_PATHLABEL, request.getLocale( ) ) );

        Map<String, Object> model = new HashMap<String, Object>( );

        String strIdDirectory = request.getParameter( PARAMETER_ID_DIRECTORY );
        String strIdDirectoryRecord = request.getParameter( PARAMETER_ID_DIRECTORY_RECORD );

        List<Record> listRecord = new ArrayList<Record>( );
        List<Integer> listIdDirectory = new ArrayList<Integer>( );

        Boolean bSingleResult = null;

        if ( ( strIdDirectory == null ) && ( strIdDirectoryRecord == null ) )
        {
            page.setContent( getSearchPage( request, plugin ) );
        }
        else
        {
            Directory directory;
            HttpSession session = request.getSession( );
            int nIdDirectory = DirectoryUtils.convertStringToInt( strIdDirectory );
            directory = DirectoryHome.findByPrimaryKey( nIdDirectory, plugin );

            DirectorySiteSearchFields searchFields = ( session.getAttribute( SESSION_DIRECTORY_SITE_SEARCH_FIELDS ) != null ) ? (DirectorySiteSearchFields) session
                    .getAttribute( SESSION_DIRECTORY_SITE_SEARCH_FIELDS ) : getInitDirectorySearchField( );

            model.put( MARK_DIRECTORY, directory );

            IRecordService recordService = SpringContextService.getBean( RecordService.BEAN_SERVICE );

            int nIdDirectoryRecord = DirectoryUtils.convertStringToInt( strIdDirectoryRecord );
            Record record = null;

            if ( request.getParameter( PARAMETER_VIEW_DIRECTORY_RECORD ) != null )
            {
                record = recordService.findByPrimaryKey( nIdDirectoryRecord, plugin );

                if ( ( record != null ) && ( record.getDirectory( ) != null ) )
                {
                    directory = DirectoryHome.findByPrimaryKey( record.getDirectory( ).getIdDirectory( ), plugin );

                    listRecord = (List<Record>) session.getAttribute( SESSION_ID_LAST_RECORD );

                    if ( ( listRecord != null ) && ( listRecord.size( ) > 0 )
                            && ( listRecord.get( listRecord.size( ) - 1 ).getIdRecord( ) != record.getIdRecord( ) ) )
                    {
                        listRecord.add( record );
                    }
                    else if ( listRecord == null )
                    {
                        listRecord = new ArrayList<Record>( );
                        listRecord.add( record );
                    }

                    session.setAttribute( SESSION_ID_LAST_RECORD, listRecord );

                    listIdDirectory = (List<Integer>) session.getAttribute( SESSION_ID_LAST_DIRECTORY );

                    if ( ( listIdDirectory != null )
                            && ( listIdDirectory.size( ) > 0 )
                            && !listIdDirectory.get( listIdDirectory.size( ) - 1 ).equals(
                                    record.getDirectory( ).getIdDirectory( ) ) )
                    {
                        listIdDirectory.add( record.getDirectory( ).getIdDirectory( ) );
                    }
                    else if ( listIdDirectory == null )
                    {
                        listIdDirectory = new ArrayList<Integer>( );
                        listIdDirectory.add( record.getDirectory( ).getIdDirectory( ) );
                    }

                    session.setAttribute( SESSION_ID_LAST_DIRECTORY, listIdDirectory );
                }
            }

            String strPortalUrl = AppPathService.getPortalUrl( );
            UrlItem urlDirectoryXpage = new UrlItem( strPortalUrl );
            urlDirectoryXpage.addParameter( XPageAppService.PARAM_XPAGE_APP,
                    AppPropertiesService.getProperty( PROPERTY_PAGE_APPLICATION_ID ) );
            urlDirectoryXpage.addParameter( PARAMETER_ID_DIRECTORY, strIdDirectory );

            if ( directory == null )
            {
                SiteMessageService.setMessage( request, MESSAGE_ERROR, SiteMessage.TYPE_STOP );

                return null;
            }

            if ( ( directory.getRoleKey( ) != null ) && !directory.getRoleKey( ).equals( Directory.ROLE_NONE )
                    && SecurityService.isAuthenticationEnable( )
                    && !SecurityService.getInstance( ).isUserInRole( request, directory.getRoleKey( ) ) )
            {
                SiteMessageService.setMessage( request, MESSAGE_ACCESS_DENIED, SiteMessage.TYPE_STOP );
            }

            if ( directory.isEnabled( ) )
            {
                if ( request.getParameter( PARAMETER_VIEW_DIRECTORY_RECORD ) != null )
                {
                    if ( ( record == null )
                            || ( ( record.getRoleKey( ) != null ) && !record.getRoleKey( ).equals( Directory.ROLE_NONE )
                                    && SecurityService.isAuthenticationEnable( ) && !SecurityService.getInstance( )
                                    .isUserInRole( request, record.getRoleKey( ) ) ) )
                    {
                        SiteMessageService.setMessage( request, MESSAGE_ACCESS_DENIED, SiteMessage.TYPE_STOP );

                        return null;
                    }

                    record.setDirectory( directory );

                    bSingleResult = true;

                    String strDirectoryRecord = getHtmlResultRecord( directory, record, request.getLocale( ), plugin,
                            session );
                    model.put( MARK_STR_RESULT_RECORD, strDirectoryRecord );
                }
                else
                {
                    searchFields.setCurrentPageIndex( Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX,
                            searchFields.getCurrentPageIndex( ) ) );
                    searchFields.setItemsPerPage( directory.getNumberRecordPerPage( ) );
                    // Init Map query if requested
                    initMapQuery( request, searchFields, directory );

                    if ( request.getParameter( PARAMETER_SEARCH ) != null )
                    {
                        // get search filter
                        try
                        {
                            HashMap<String, List<RecordField>> mapQuery = DirectoryUtils.getSearchRecordData( request,
                                    directory.getIdDirectory( ), plugin, request.getLocale( ) );

                            searchFields.setMapQuery( mapQuery );
                            searchFields.setIdDirectory( directory.getIdDirectory( ) );
                        }
                        catch ( DirectoryErrorException error )
                        {
                            if ( error.isMandatoryError( ) )
                            {
                                Object[] tabRequiredFields = { error.getTitleField( ) };
                                SiteMessageService.setMessage( request, MESSAGE_DIRECTORY_ERROR_MANDATORY_FIELD,
                                        tabRequiredFields, SiteMessage.TYPE_STOP );
                            }
                            else
                            {
                                Object[] tabRequiredFields = { error.getTitleField( ), error.getErrorMessage( ) };
                                SiteMessageService.setMessage( request, MESSAGE_DIRECTORY_ERROR, tabRequiredFields,
                                        SiteMessage.TYPE_STOP );
                            }
                        }
                    }

                    if ( searchFields.getMapQuery( ) != null )
                    {
                        // call search service
                        searchFields.setIsDisabled( RecordFieldFilter.FILTER_TRUE );

                        List<Integer> listResultRecordId = new ArrayList<Integer>( );

                        if ( SecurityService.isAuthenticationEnable( ) )
                        {
                            SecurityService securityService = SecurityService.getInstance( );
                            LuteceUser user = securityService.getRegisteredUser( request );
                            List<String> roleKeyList = new ArrayList<String>( );

                            if ( user != null )
                            {
                                String[] lRoles = securityService.getRolesByUser( user );
                                roleKeyList = new ArrayList<String>( Arrays.asList( lRoles ) );
                            }

                            searchFields.setRoleKeyList( roleKeyList );
                            searchFields.setIncludeRoleNone( true );
                            searchFields.setIncludeRoleNull( true );
                        }

                        //sort parameters
                        searchFields.setSortParameters( request, directory, plugin );

                        listResultRecordId = DirectoryUtils.getListResults( request, directory, false, true,
                                searchFields, null, request.getLocale( ) );

                        boolean bIsDisplayedDirectly = Boolean.parseBoolean( AppPropertiesService
                                .getProperty( PROPERTY_DISPLAY_ONE_RESULT_DIRECTLY ) );

                        if ( bIsDisplayedDirectly && ( listResultRecordId.size( ) == 1 )
                                && ( session.getAttribute( SESSION_ONE_RECORD_ID ) == null ) )
                        {
                            record = recordService.findByPrimaryKey( listResultRecordId.get( 0 ), plugin );

                            if ( ( record != null ) && ( record.getDirectory( ) != null ) )
                            {
                                directory = DirectoryHome.findByPrimaryKey( record.getDirectory( ).getIdDirectory( ),
                                        plugin );
                            }

                            if ( ( record == null )
                                    || ( ( record.getRoleKey( ) != null )
                                            && !record.getRoleKey( ).equals( Directory.ROLE_NONE )
                                            && SecurityService.isAuthenticationEnable( ) && !SecurityService
                                            .getInstance( ).isUserInRole( request, record.getRoleKey( ) ) ) )
                            {
                                SiteMessageService.setMessage( request, MESSAGE_ACCESS_DENIED, SiteMessage.TYPE_STOP );

                                return null;
                            }

                            record.setDirectory( directory );

                            String strDirectoryRecord = getHtmlResultRecord( directory, record, request.getLocale( ),
                                    plugin, session );
                            model.put( MARK_STR_RESULT_RECORD, strDirectoryRecord );
                            bSingleResult = true;
                            model.put( MARK_ONE_RESULT, true );

                            session.setAttribute( SESSION_ONE_RECORD_ID, record.getIdRecord( ) );

                            listRecord = (List<Record>) session.getAttribute( SESSION_ID_LAST_RECORD );

                            if ( ( listRecord != null )
                                    && ( listRecord.size( ) > 0 )
                                    && ( listRecord.get( listRecord.size( ) - 1 ).getIdRecord( ) != record
                                            .getIdRecord( ) ) )
                            {
                                listRecord.add( record );
                            }
                            else if ( listRecord == null )
                            {
                                listRecord = new ArrayList<Record>( );
                                listRecord.add( record );
                            }

                            session.setAttribute( SESSION_ID_LAST_RECORD, listRecord );

                            listIdDirectory = (List<Integer>) session.getAttribute( SESSION_ID_LAST_DIRECTORY );

                            if ( ( listIdDirectory != null )
                                    && ( listIdDirectory.size( ) > 0 )
                                    && !listIdDirectory.get( listIdDirectory.size( ) - 1 ).equals(
                                            record.getDirectory( ).getIdDirectory( ) ) )
                            {
                                listIdDirectory.add( record.getDirectory( ).getIdDirectory( ) );
                            }
                            else if ( listIdDirectory == null )
                            {
                                listIdDirectory = new ArrayList<Integer>( );
                                listIdDirectory.add( record.getDirectory( ).getIdDirectory( ) );
                            }

                            session.setAttribute( SESSION_ID_LAST_DIRECTORY, listIdDirectory );
                        }
                        else
                        {
                            session.setAttribute( SESSION_ONE_RECORD_ID, null );
                        }

                        if ( ( listResultRecordId.size( ) != 1 ) || !bIsDisplayedDirectly )
                        {
                            bSingleResult = false;

                            Paginator<Integer> paginator = new Paginator<Integer>( listResultRecordId,
                                    searchFields.getItemsPerPage( ), urlDirectoryXpage.getUrl( ),
                                    Paginator.PARAMETER_PAGE_INDEX, searchFields.getCurrentPageIndex( ) );

                            model.put( MARK_PAGINATOR, paginator );

                            List<Record> lRecord = recordService.loadListByListId( paginator.getPageItems( ), plugin );

                            if ( lRecord.size( ) > 0 )
                            {
                                String strResultList = getHtmlResultList( directory, lRecord, request.getLocale( ),
                                        plugin );
                                model.put( MARK_STR_RESULT_LIST, strResultList );
                            }
                        }
                    }
                    else
                    {
                        // if map_query is null, indicate that
                        model.put( MARK_NEW_SEARCH, Integer.valueOf( 1 ) );
                    }

                    String strFormSearch = getHtmlFormSearch( directory, searchFields.getMapQuery( ), request, plugin );
                    model.put( MARK_STR_FORM_SEARCH, strFormSearch );
                }

                model.put( MARK_LOCALE, request.getLocale( ) );
            }
            else
            {
                model.put( MARK_UNAVAILABILITY_MESSAGE, directory.getUnavailabilityMessage( ) );
            }

            EntryFilter filterGeolocation = new EntryFilter( );
            filterGeolocation.setIdDirectory( directory.getIdDirectory( ) );
            filterGeolocation.setIdType( AppPropertiesService.getPropertyInt( PROPERTY_ENTRY_TYPE_GEOLOCATION, 16 ) );

            if ( bSingleResult != null )
            {
                if ( bSingleResult )
                {
                    filterGeolocation.setIsShownInResultRecord( 1 );
                }
                else
                {
                    filterGeolocation.setIsShownInResultList( 1 );
                }
            }

            List<IEntry> entriesGeolocationList = EntryHome.getEntryList( filterGeolocation, plugin );
            model.put( MARK_ENTRY_LIST_GEOLOCATION, entriesGeolocationList );

            HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_XPAGE_FRAME_DIRECTORY,
                    request.getLocale( ), model );
            page.setContent( template.getHtml( ) );
            session.setAttribute( SESSION_DIRECTORY_SITE_SEARCH_FIELDS, searchFields );
        }

        return page;
    }

    /**
     * return the HTML form search
     * 
     * @param directory
     *            the directory
     * @param mapQuery
     *            the mapQuerySearch
     * @param request
     *            the HttpServletRequesr
     * @param plugin
     *            the plugin
     * @return the html form search
     */
    private String getHtmlFormSearch( Directory directory, HashMap<String, List<RecordField>> mapQuery,
            HttpServletRequest request, Plugin plugin )
    {
        // build entryFilter
        EntryFilter entryFilter = new EntryFilter( );
        entryFilter.setIdDirectory( directory.getIdDirectory( ) );
        entryFilter.setIsGroup( EntryFilter.FILTER_FALSE );
        entryFilter.setIsComment( EntryFilter.FILTER_FALSE );
        entryFilter.setIsIndexed( EntryFilter.FILTER_TRUE );

        List<IEntry> listEntryFormMainSearch = new ArrayList<IEntry>( );
        List<IEntry> listEntryFormComplementarySearch = new ArrayList<IEntry>( );
        IEntry entryStore;

        for ( IEntry entry : EntryHome.getEntryList( entryFilter, plugin ) )
        {
            entryStore = EntryHome.findByPrimaryKey( entry.getIdEntry( ), plugin );

            if ( entryStore.isRoleAssociated( ) )
            {
                entryStore.setFields( DirectoryUtils.getAuthorizedFieldsByRole( request, entryStore.getFields( ) ) );
            }

            if ( !entryStore.isShownInAdvancedSearch( ) )
            {
                listEntryFormMainSearch.add( entryStore );
            }
            else
            {
                listEntryFormComplementarySearch.add( entryStore );
            }
        }

        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( MARK_ENTRY_LIST_FORM_MAIN_SEARCH, listEntryFormMainSearch );
        model.put( MARK_ENTRY_LIST_FORM_COMPLEMENTARY_SEARCH, listEntryFormComplementarySearch );
        model.put( MARK_MAP_ID_ENTRY_LIST_RECORD_FIELD, mapQuery );
        model.put( MARK_DIRECTORY, directory );
        model.put( MARK_LOCALE, request.getLocale( ) );

        HttpSession session = request.getSession( );

        if ( session.getAttribute( SESSION_ONE_RECORD_ID ) != null )
        {
            model.put( MARK_ONE_SESSION_ID, session.getAttribute( SESSION_ONE_RECORD_ID ) );
        }

        HtmlTemplate templateXmlFormSearch = AppTemplateService.getTemplate( TEMPLATE_XML_FORM_SEARCH,
                request.getLocale( ), model );

        File fileTemplate = null;
        DirectoryXsl directoryXsl = DirectoryXslHome.findByPrimaryKey( directory.getIdFormSearchTemplate( ), plugin );

        if ( directoryXsl.getFile( ) != null )
        {
            fileTemplate = FileHome.findByPrimaryKey( directoryXsl.getFile( ).getIdFile( ), plugin );
        }

        if ( ( fileTemplate != null ) && ( fileTemplate.getPhysicalFile( ) != null ) )
        {
            fileTemplate.setPhysicalFile( PhysicalFileHome.findByPrimaryKey( fileTemplate.getPhysicalFile( )
                    .getIdPhysicalFile( ), plugin ) );

            XmlTransformerService xmlTransformerService = new XmlTransformerService( );
            PhysicalFile physicalFile = fileTemplate.getPhysicalFile( );
            String strXslId = XSL_UNIQUE_PREFIX_ID + physicalFile.getIdPhysicalFile( );
            String strResult = xmlTransformerService.transformBySourceWithXslCache( templateXmlFormSearch.getHtml( ),
                    physicalFile.getValue( ), strXslId, null, null );

            return strResult;
        }

        return DirectoryUtils.EMPTY_STRING;
    }

    /**
     * return the HTML result list
     * 
     * @param directory
     *            the directory
     * @param listRecord
     *            the list of record
     * @param listEntry
     *            the list of entry
     * @param locale
     *            the locale
     * @param plugin
     *            the plugin
     * @return the HTML result list
     */
    private String getHtmlResultList( Directory directory, List<Record> listRecord, Locale locale, Plugin plugin )
    {
        StringBuffer strBufferListRecordXml = new StringBuffer( );
        StringBuffer strBufferListEntryXml = new StringBuffer( );

        // get directory Entry
        EntryFilter entryFilter = new EntryFilter( );
        entryFilter.setIdDirectory( directory.getIdDirectory( ) );
        entryFilter.setIsGroup( EntryFilter.FILTER_FALSE );
        entryFilter.setIsComment( EntryFilter.FILTER_FALSE );
        entryFilter.setIsShownInResultList( EntryFilter.FILTER_TRUE );

        List<IEntry> listEntrySearchResult = EntryHome.getEntryList( entryFilter, plugin );

        RecordFieldFilter recordFieldFilter = new RecordFieldFilter( );

        for ( Record record : listRecord )
        {
            recordFieldFilter.setIdRecord( record.getIdRecord( ) );
            recordFieldFilter.setIsEntryShownInResultList( RecordFieldFilter.FILTER_TRUE );
            record.setListRecordField( RecordFieldHome.getRecordFieldList( recordFieldFilter, plugin ) );

            State state = null;

            if ( WorkflowService.getInstance( ).isAvailable( ) )
            {
                state = WorkflowService.getInstance( ).getState( record.getIdRecord( ), Record.WORKFLOW_RESOURCE_TYPE,
                        directory.getIdWorkflow( ), Integer.valueOf( directory.getIdDirectory( ) ) );
            }

            strBufferListRecordXml.append( record.getXml( plugin, locale, false, state, listEntrySearchResult, true,
                    true, false, true ) );
        }

        for ( IEntry entry : listEntrySearchResult )
        {
            entry.getXml( plugin, locale, strBufferListEntryXml );
        }

        HashMap<String, String> model = new HashMap<String, String>( );

        if ( ( directory.getIdWorkflow( ) != DirectoryUtils.CONSTANT_ID_NULL )
                && WorkflowService.getInstance( ).isAvailable( ) )
        {
            model.put( TAG_DISPLAY, TAG_YES );
        }
        else
        {
            model.put( TAG_DISPLAY, TAG_NO );
        }

        XmlUtil.addEmptyElement( strBufferListEntryXml, TAG_STATUS, model );

        StringBuilder strBufferXml = new StringBuilder( );
        strBufferXml.append( XmlUtil.getXmlHeader( ) );
        strBufferXml.append( directory.getXml( plugin, locale, strBufferListRecordXml, strBufferListEntryXml ) );

        File fileTemplate = null;
        DirectoryXsl directoryXsl = DirectoryXslHome.findByPrimaryKey( directory.getIdResultListTemplate( ), plugin );

        if ( directoryXsl.getFile( ) != null )
        {
            fileTemplate = FileHome.findByPrimaryKey( directoryXsl.getFile( ).getIdFile( ), plugin );
        }

        if ( ( fileTemplate != null ) && ( fileTemplate.getPhysicalFile( ) != null ) )
        {
            fileTemplate.setPhysicalFile( PhysicalFileHome.findByPrimaryKey( fileTemplate.getPhysicalFile( )
                    .getIdPhysicalFile( ), plugin ) );

            HashMap<String, String> params = new HashMap<String, String>( );
            String strParamTitleDescriptive = I18nService.getLocalizedString(
                    PROPERTY_DIRECTORY_FRAME_TITLE_DESCRIPTIVE, locale );
            String strParamTitleSortAsc = I18nService.getLocalizedString( PROPERTY_DIRECTORY_RESULT_TITLE_SORT_ASC,
                    locale );
            String strParamTitleSortDesc = I18nService.getLocalizedString( PROPERTY_DIRECTORY_RESULT_TITLE_SORT_DESC,
                    locale );

            params.put( MARK_TITLE_SORT_ASC, strParamTitleSortAsc );
            params.put( MARK_TITLE_SORT_DESC, strParamTitleSortDesc );
            params.put( MARK_TITLE_DESCRIPTIVE, strParamTitleDescriptive );

            XmlTransformerService xmlTransformerService = new XmlTransformerService( );
            PhysicalFile physicalFile = fileTemplate.getPhysicalFile( );
            String strXslId = XSL_UNIQUE_PREFIX_ID + physicalFile.getIdPhysicalFile( );
            String strResult = xmlTransformerService.transformBySourceWithXslCache( strBufferXml.toString( ),
                    physicalFile.getValue( ), strXslId, params, null );

            return strResult;
        }

        return DirectoryUtils.EMPTY_STRING;
    }

    /**
     * return the HTML result lrcord
     * 
     * @param directory
     *            the directory
     * @param record
     *            the record
     * @param locale
     *            the locale
     * @param plugin
     *            the plugin
     * @param session The session
     * @return the Html result record
     */
    private String getHtmlResultRecord( Directory directory, Record record, Locale locale, Plugin plugin,
            HttpSession session )
    {
        RecordFieldFilter filter = new RecordFieldFilter( );
        filter.setIdRecord( record.getIdRecord( ) );
        filter.setIsEntryShownInResultRecord( RecordFieldFilter.FILTER_TRUE );

        List<RecordField> listRecordField = RecordFieldHome.getRecordFieldList( filter, plugin );
        record.setListRecordField( listRecordField );

        StringBuffer strBufferListEntryXml = new StringBuffer( );

        // get directory Entry
        EntryFilter entryFilter = new EntryFilter( );
        entryFilter.setIdDirectory( record.getDirectory( ).getIdDirectory( ) );

        List<IEntry> listEntry = DirectoryUtils.getFormEntriesByFilter( entryFilter, plugin );

        for ( IEntry entry : listEntry )
        {
            entry.getXml( plugin, locale, strBufferListEntryXml );
        }

        StringBuffer strBufferListRecordXml = new StringBuffer( );
        strBufferListRecordXml
                .append( record.getXml( plugin, locale, false, null, listEntry, true, true, false, true ) );

        StringBuilder strBufferXml = new StringBuilder( );
        strBufferXml.append( XmlUtil.getXmlHeader( ) );
        strBufferXml.append( directory.getXml( plugin, locale, strBufferListRecordXml, strBufferListEntryXml ) );

        File fileTemplate = null;
        DirectoryXsl directoryXsl = DirectoryXslHome.findByPrimaryKey( record.getDirectory( )
                .getIdResultRecordTemplate( ), plugin );

        if ( directoryXsl.getFile( ) != null )
        {
            fileTemplate = FileHome.findByPrimaryKey( directoryXsl.getFile( ).getIdFile( ), plugin );
        }

        if ( ( fileTemplate != null ) && ( fileTemplate.getPhysicalFile( ) != null ) )
        {
            fileTemplate.setPhysicalFile( PhysicalFileHome.findByPrimaryKey( fileTemplate.getPhysicalFile( )
                    .getIdPhysicalFile( ), plugin ) );

            HashMap<String, String> params = new HashMap<String, String>( );
            String strParamTitleBackSearch = I18nService.getLocalizedString(
                    PROPERTY_DIRECTORY_FRAME_TITLE_BACK_SEARCH, locale );
            String strParamLabelBackSearch = I18nService.getLocalizedString(
                    PROPERTY_DIRECTORY_FRAME_LABEL_BACK_SEARCH, locale );

            params.put( MARK_TITLE_BACK_SEARCH, strParamTitleBackSearch );
            params.put( MARK_LABEL_BACK_SEARCH, strParamLabelBackSearch );
            params.put( MARK_ID_DIRECTORY, Integer.toString( record.getDirectory( ).getIdDirectory( ) ) );

            // Params linked with last record
            if ( session.getAttribute( SESSION_ID_LAST_RECORD ) != null )
            {
                List<Record> listRecord = (List<Record>) session.getAttribute( SESSION_ID_LAST_RECORD );
                String strParamTitleBackRecord = I18nService.getLocalizedString(
                        PROPERTY_DIRECTORY_FRAME_TITLE_BACK_RECORD, locale );
                String strParamLabelBackRecord = I18nService.getLocalizedString(
                        PROPERTY_DIRECTORY_FRAME_LABEL_BACK_RECORD, locale );
                params.put( MARK_TITLE_BACK_RECORD, strParamTitleBackRecord );
                params.put( MARK_LABEL_BACK_RECORD, strParamLabelBackRecord );
                params.put( MARK_IS_EXTEND_INSTALLED, Boolean.toString( PortalService.isExtendActivated( ) ) );

                if ( ( listRecord != null ) && ( listRecord.size( ) > 1 ) )
                {
                    Record lastRecord = listRecord.get( listRecord.size( ) - 2 );
                    params.put( MARK_ID_LAST_RECORD, "" + lastRecord.getIdRecord( ) );
                }
            }

            if ( session.getAttribute( SESSION_ID_LAST_DIRECTORY ) != null )
            {
                List<Integer> listIdDirectory = (List<Integer>) session.getAttribute( SESSION_ID_LAST_DIRECTORY );

                if ( ( listIdDirectory != null ) && ( listIdDirectory.size( ) > 1 ) )
                {
                    Integer lastIdDirectory = listIdDirectory.get( listIdDirectory.size( ) - 2 );
                    params.put( MARK_ID_LAST_DIRECTORY, "" + lastIdDirectory );
                }
            }

            XmlTransformerService xmlTransformerService = new XmlTransformerService( );
            PhysicalFile physicalFile = fileTemplate.getPhysicalFile( );
            String strXslId = XSL_UNIQUE_PREFIX_ID + physicalFile.getIdPhysicalFile( );
            String strResult = xmlTransformerService.transformBySourceWithXslCache( strBufferXml.toString( ),
                    physicalFile.getValue( ), strXslId, params, null );

            return strResult;
        }

        return DirectoryUtils.EMPTY_STRING;
    }

    /**
     * Returns the html code of the search page
     * 
     * @param request
     *            the http request
     * @param plugin
     *            the plugin
     * @return the html page
     * @throws SiteMessageException
     *             a exception that triggers a site message
     */
    public String getSearchPage( HttpServletRequest request, Plugin plugin ) throws SiteMessageException
    {
        String strQuery = request.getParameter( PARAMETER_QUERY );

        HashMap<String, Object> model = new HashMap<String, Object>( );

        if ( StringUtils.isNotBlank( strQuery ) )
        {
            Date dateBegin = null;
            Date dateEnd = null;

            String strOperator = request.getParameter( PARAMETER_OPERATOR );
            String strDateBegin = request.getParameter( PARAMETER_DATE_BEGIN );
            String strDateEnd = request.getParameter( PARAMETER_DATE_END );

            // Mandatory fields
            if ( StringUtils.isBlank( strOperator ) )
            {
                Object[] tabRequiredFields = { PARAMETER_OPERATOR };
                SiteMessageService.setMessage( request, MESSAGE_DIRECTORY_ERROR_MANDATORY_FIELD, tabRequiredFields,
                        SiteMessage.TYPE_STOP );
            }

            // Safety checks
            if ( StringUtils.isNotBlank( strDateBegin ) )
            {
                dateBegin = DateUtil.formatDate( strDateBegin, request.getLocale( ) );

                if ( dateBegin == null )
                {
                    SiteMessageService.setMessage( request, MESSAGE_SEARCH_DATE_VALIDITY, SiteMessage.TYPE_STOP );
                }
            }

            if ( StringUtils.isNotBlank( strDateEnd ) )
            {
                dateEnd = DateUtil.formatDate( strDateEnd, request.getLocale( ) );

                if ( dateEnd == null )
                {
                    SiteMessageService.setMessage( request, MESSAGE_SEARCH_DATE_VALIDITY, SiteMessage.TYPE_STOP );
                }
            }

            if ( !strOperator.equalsIgnoreCase( OPERATOR_AND ) && !strOperator.equalsIgnoreCase( OPERATOR_OR ) )
            {
                SiteMessageService.setMessage( request, MESSAGE_SEARCH_OPERATOR_VALIDITY, SiteMessage.TYPE_STOP );
            }

            // Check XSS characters
            if ( SecurityUtil.containsXssCharacters( request, strQuery ) )
            {
                SiteMessageService.setMessage( request, MESSAGE_INVALID_SEARCH_TERMS, SiteMessage.TYPE_STOP );
            }

            // Use LuceneSearchEngine
            SearchEngine engine = (SearchEngine) SpringContextService.getBean( BEAN_SEARCH_ENGINE );
            List<SearchResult> listResults = engine.getSearchResults( strQuery, request );

            model.put( MARK_RESULT_LIST, listResults );

            // re-populate search parameters
            model.put( MARK_QUERY, strQuery );
            model.put( MARK_OPERATOR, strOperator );
            model.put( MARK_DATE_BEGIN, strDateBegin );
            model.put( MARK_DATE_END, strDateEnd );
        }

        // Display the list of all Directory
        DirectoryFilter filter = new DirectoryFilter( );
        filter.setIsDisabled( DirectoryFilter.FILTER_TRUE );

        List<Directory> listDirectory = DirectoryHome.getDirectoryList( filter, plugin );
        List<Directory> listDirectoryAuthorized;

        if ( SecurityService.isAuthenticationEnable( ) )
        {
            listDirectoryAuthorized = new ArrayList<Directory>( );

            for ( Directory directory : listDirectory )
            {
                if ( ( directory.getRoleKey( ) == null ) || directory.getRoleKey( ).equals( Directory.ROLE_NONE )
                        || SecurityService.getInstance( ).isUserInRole( request, directory.getRoleKey( ) ) )
                {
                    listDirectoryAuthorized.add( directory );
                }
            }
        }
        else
        {
            listDirectoryAuthorized = listDirectory;
        }

        model.put( MARK_DIRECTORY_LIST, listDirectoryAuthorized );

        model.put( MARK_LOCALE, request.getLocale( ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_XPAGE_VIEW_ALL_DIRECTORIES,
                request.getLocale( ), model );

        return template.getHtml( );
    }

    /**
     * re init the map query used for searching
     * @param request the HttpServletRequest
     * @param searchFields the searchFields
     * @param directory the directory
     */
    private static void initMapQuery( HttpServletRequest request, DirectorySiteSearchFields searchFields,
            Directory directory )
    {
        if ( ( request.getParameter( INIT_MAP_QUERY ) != null )
                || ( ( request.getParameter( PARAMETER_SEARCH ) == null ) && ( searchFields.getIdDirectory( ) != directory
                        .getIdDirectory( ) ) ) )
        {
            searchFields.setMapQuery( null );
        }
    }

    /**
     * return a init searchField
     * @param directory the directory
     * @return the DirectorySiteSearchFields
     */
    private DirectorySiteSearchFields getInitDirectorySearchField( )
    {
        DirectorySiteSearchFields searchFields = new DirectorySiteSearchFields( );

        return searchFields;
    }
}

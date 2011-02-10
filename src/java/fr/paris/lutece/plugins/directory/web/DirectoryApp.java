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
import fr.paris.lutece.plugins.directory.business.RecordHome;
import fr.paris.lutece.plugins.directory.service.directorysearch.DirectorySearchService;
import fr.paris.lutece.plugins.directory.utils.DirectoryErrorException;
import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.portal.business.workflow.State;
import fr.paris.lutece.portal.service.content.XPageAppService;
import fr.paris.lutece.portal.service.html.XmlTransformerService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.SiteMessage;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.message.SiteMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.security.UserNotSignedException;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
import fr.paris.lutece.portal.web.xpages.XPage;
import fr.paris.lutece.portal.web.xpages.XPageApplication;
import fr.paris.lutece.util.UniqueIDGenerator;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.url.UrlItem;
import fr.paris.lutece.util.xml.XmlUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


/**
 * This class manages DirectoryApp page.
 */
public class DirectoryApp implements XPageApplication
{
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
    private static final String PROPERTY_ENTRY_TYPE_GEOLOCATION = "directory.entry_type.geolocation";
    private static final String PROPERTY_DISPLAY_ONE_RESULT_DIRECTLY = "directory.display_one_result_directly";

    // request parameters
    private static final String PARAMETER_VIEW_DIRECTORY_RECORD = "view_directory_record";
    private static final String PARAMETER_ID_DIRECTORY = "id_directory";
    private static final String PARAMETER_SEARCH = "search";
    private static final String PARAMETER_ID_DIRECTORY_RECORD = "id_directory_record";
    private static final String PARAMETER_FILTER_PAGE_INDEX = "filter_page_index";
    private static final String INIT_MAP_QUERY = "init_map_query";

    //message
    private static final String MESSAGE_ERROR = "directory.message.Error";
    private static final String MESSAGE_ACCESS_DENIED = "directory.message.accessDenied";

    //Markers
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

    //Markers XSL
    private static final String MARK_TITLE_DESCRIPTIVE = "title-descriptive";
    private static final String MARK_TITLE_BACK_SEARCH = "title-back-search";
    private static final String MARK_LABEL_BACK_SEARCH = "label-back-search";
    private static final String MARK_ID_DIRECTORY = "id-directory";
    private static final String MARK_TITLE_BACK_RECORD = "title-back-record";
    private static final String MARK_LABEL_BACK_RECORD = "label-back-record";
    private static final String MARK_ID_LAST_RECORD = "id-last-record";
    private static final String MARK_ID_LAST_DIRECTORY = "id-last-directory";

    //session filter
    private static final String SESSION_FILTER_PAGE_INDEX = "directory_filter_page_index";
    private static final String SESSION_FILTER_MAP_QUERY = "directory_map_query";
    private static final String SESSION_FILTER_ID_DIRECTORY = "directory_id_directory";
    private static final String SESSION_ONE_RECORD_ID = "one_record_id";
    private static final String SESSION_ID_LAST_RECORD = "id_last_record";
    private static final String SESSION_ID_LAST_DIRECTORY = "id_last_directory";

    //message
    private static final String MESSAGE_DIRECTORY_ERROR = "directory.message.directory_error";
    private static final String MESSAGE_DIRECTORY_ERROR_MANDATORY_FIELD = "directory.message.directory_error.mandatory.field";

    // Properties
    private static final String XSL_UNIQUE_PREFIX_ID = UniqueIDGenerator.getNewId(  ) + "directory-";

    //Tag
    private static final String TAG_DISPLAY = "display";
    private static final String TAG_YES = "yes";
    private static final String TAG_NO = "no";
    private static final String TAG_STATUS = "status";

    /**
     * Returns the Directory XPage result content depending on the request parameters and the current mode.
     *
     * @param request The HTTP request.
     * @param nMode The current mode.
     * @param plugin The Plugin.
     * @return The page content.
     * @throws SiteMessageException the SiteMessageException
     * @throws UserNotSignedException the UserNotSignedException
     */
    @SuppressWarnings( "unchecked" )
    public XPage getPage( HttpServletRequest request, int nMode, Plugin plugin )
        throws SiteMessageException, UserNotSignedException
    {
        XPage page = new XPage(  );

        page.setTitle( I18nService.getLocalizedString( PROPERTY_XPAGE_PAGETITLE, request.getLocale(  ) ) );
        page.setPathLabel( I18nService.getLocalizedString( PROPERTY_XPAGE_PATHLABEL, request.getLocale(  ) ) );

        Map<String, Object> model = new HashMap<String, Object>(  );

        String strIdDirectory = request.getParameter( PARAMETER_ID_DIRECTORY );
        String strIdDirectoryRecord = request.getParameter( PARAMETER_ID_DIRECTORY_RECORD );

        List<Record> listRecord = new ArrayList<Record>(  );
        List<Integer> listIdDirectory = new ArrayList<Integer>(  );

        Boolean bSingleResult = null;

        if ( ( strIdDirectory == null ) && ( strIdDirectoryRecord == null ) )
        {
            //Display the list of all Directory
            DirectoryFilter filter = new DirectoryFilter(  );
            filter.setIsDisabled( DirectoryFilter.FILTER_TRUE );

            List<Directory> listDirectory = DirectoryHome.getDirectoryList( filter, plugin );
            List<Directory> listDirectoryAuthorized;

            if ( SecurityService.isAuthenticationEnable(  ) )
            {
                listDirectoryAuthorized = new ArrayList<Directory>(  );

                for ( Directory directory : listDirectory )
                {
                    if ( ( directory.getRoleKey(  ) == null ) ||
                            directory.getRoleKey(  ).equals( Directory.ROLE_NONE ) ||
                            SecurityService.getInstance(  ).isUserInRole( request, directory.getRoleKey(  ) ) )
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

            HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_XPAGE_VIEW_ALL_DIRECTORIES,
                    request.getLocale(  ), model );
            page.setContent( template.getHtml(  ) );
        }
        else
        {
            Directory directory;
            int nIdDirectory = DirectoryUtils.convertStringToInt( strIdDirectory );
            directory = DirectoryHome.findByPrimaryKey( nIdDirectory, plugin );

            model.put( MARK_DIRECTORY, directory );

            int nIdDirectoryRecord = DirectoryUtils.convertStringToInt( strIdDirectoryRecord );
            Record record = null;

            if ( request.getParameter( PARAMETER_VIEW_DIRECTORY_RECORD ) != null )
            {
                record = RecordHome.findByPrimaryKey( nIdDirectoryRecord, plugin );

                if ( ( record != null ) && ( record.getDirectory(  ) != null ) )
                {
                    directory = DirectoryHome.findByPrimaryKey( record.getDirectory(  ).getIdDirectory(  ), plugin );

                    HttpSession session = request.getSession(  );

                    listRecord = (List<Record>) session.getAttribute( SESSION_ID_LAST_RECORD );

                    if ( ( listRecord != null ) && ( listRecord.size(  ) > 0 ) &&
                            ( listRecord.get( listRecord.size(  ) - 1 ).getIdRecord(  ) != record.getIdRecord(  ) ) )
                    {
                        listRecord.add( record );
                    }
                    else if ( listRecord == null )
                    {
                        listRecord = new ArrayList<Record>(  );
                        listRecord.add( record );
                    }

                    session.setAttribute( SESSION_ID_LAST_RECORD, listRecord );

                    listIdDirectory = (List<Integer>) session.getAttribute( SESSION_ID_LAST_DIRECTORY );

                    if ( ( listIdDirectory != null ) && ( listIdDirectory.size(  ) > 0 ) &&
                            !listIdDirectory.get( listIdDirectory.size(  ) - 1 )
                                                .equals( record.getDirectory(  ).getIdDirectory(  ) ) )
                    {
                        listIdDirectory.add( record.getDirectory(  ).getIdDirectory(  ) );
                    }
                    else if ( listIdDirectory == null )
                    {
                        listIdDirectory = new ArrayList<Integer>(  );
                        listIdDirectory.add( record.getDirectory(  ).getIdDirectory(  ) );
                    }

                    session.setAttribute( SESSION_ID_LAST_DIRECTORY, listIdDirectory );
                }
            }

            String strPortalUrl = AppPathService.getPortalUrl(  );
            UrlItem urlDirectoryXpage = new UrlItem( strPortalUrl );
            urlDirectoryXpage.addParameter( XPageAppService.PARAM_XPAGE_APP,
                AppPropertiesService.getProperty( PROPERTY_PAGE_APPLICATION_ID ) );
            urlDirectoryXpage.addParameter( PARAMETER_ID_DIRECTORY, strIdDirectory );

            if ( directory == null )
            {
                SiteMessageService.setMessage( request, MESSAGE_ERROR, SiteMessage.TYPE_STOP );
            }

            if ( ( directory.getRoleKey(  ) != null ) && !directory.getRoleKey(  ).equals( Directory.ROLE_NONE ) &&
                    SecurityService.isAuthenticationEnable(  ) &&
                    !SecurityService.getInstance(  ).isUserInRole( request, directory.getRoleKey(  ) ) )
            {
                SiteMessageService.setMessage( request, MESSAGE_ACCESS_DENIED, SiteMessage.TYPE_STOP );
            }

            if ( directory.isEnabled(  ) )
            {
                if ( request.getParameter( PARAMETER_VIEW_DIRECTORY_RECORD ) != null )
                {
                    if ( ( record == null ) ||
                            ( ( record.getRoleKey(  ) != null ) &&
                            !record.getRoleKey(  ).equals( Directory.ROLE_NONE ) &&
                            SecurityService.isAuthenticationEnable(  ) &&
                            !SecurityService.getInstance(  ).isUserInRole( request, record.getRoleKey(  ) ) ) )
                    {
                        SiteMessageService.setMessage( request, MESSAGE_ACCESS_DENIED, SiteMessage.TYPE_STOP );
                    }

                    HttpSession session = request.getSession(  );
                    record.setDirectory( directory );

                    bSingleResult = true;

                    String strDirectoryRecord = getHtmlResultRecord( directory, record, request.getLocale(  ), plugin,
                            session );
                    model.put( MARK_STR_RESULT_RECORD, strDirectoryRecord );
                }
                else
                {
                    HttpSession session = request.getSession(  );
                    String strFilterPageIndex = request.getParameter( PARAMETER_FILTER_PAGE_INDEX );

                    if ( ( strFilterPageIndex == null ) && ( session.getAttribute( SESSION_FILTER_PAGE_INDEX ) != null ) )
                    {
                        strFilterPageIndex = (String) session.getAttribute( SESSION_FILTER_PAGE_INDEX );
                    }
                    else
                    {
                        session.setAttribute( SESSION_FILTER_PAGE_INDEX, strFilterPageIndex );
                    }

                    // Init Map query if requested
                    initMapQuery( request );

                    HashMap<String, List<RecordField>> mapQuery = null;

                    if ( ( request.getParameter( PARAMETER_SEARCH ) == null ) &&
                            ( session.getAttribute( SESSION_FILTER_MAP_QUERY ) != null ) &&
                            ( session.getAttribute( SESSION_FILTER_ID_DIRECTORY ) != null ) &&
                            ( (Integer) session.getAttribute( SESSION_FILTER_ID_DIRECTORY ) == directory.getIdDirectory(  ) ) )
                    {
                        mapQuery = (HashMap<String, List<RecordField>>) session.getAttribute( SESSION_FILTER_MAP_QUERY );
                    }
                    else if ( request.getParameter( PARAMETER_SEARCH ) != null )
                    {
                        //get search filter
                        try
                        {
                            mapQuery = DirectoryUtils.getSearchRecordData( request, directory.getIdDirectory(  ),
                                    plugin, request.getLocale(  ) );

                            session.setAttribute( SESSION_FILTER_MAP_QUERY, mapQuery );
                            session.setAttribute( SESSION_FILTER_ID_DIRECTORY, directory.getIdDirectory(  ) );
                        }
                        catch ( DirectoryErrorException error )
                        {
                            if ( error.isMandatoryError(  ) )
                            {
                                Object[] tabRequiredFields = { error.getTitleField(  ) };
                                SiteMessageService.setMessage( request, MESSAGE_DIRECTORY_ERROR_MANDATORY_FIELD,
                                    tabRequiredFields, SiteMessage.TYPE_STOP );
                            }
                            else
                            {
                                Object[] tabRequiredFields = { error.getTitleField(  ), error.getErrorMessage(  ) };
                                SiteMessageService.setMessage( request, MESSAGE_DIRECTORY_ERROR, tabRequiredFields,
                                    SiteMessage.TYPE_STOP );
                            }
                        }
                    }

                    if ( mapQuery != null )
                    {
                        //call search service
                        RecordFieldFilter filter = new RecordFieldFilter(  );
                        filter.setIdDirectory( directory.getIdDirectory(  ) );
                        filter.setIsDisabled( RecordFieldFilter.FILTER_TRUE );

                        List<Integer> listResultRecordId = new ArrayList<Integer>(  );

                        if ( SecurityService.isAuthenticationEnable(  ) )
                        {
                            SecurityService securityService = SecurityService.getInstance(  );
                            LuteceUser user = securityService.getRegisteredUser( request );
                            List<String> roleKeyList = new ArrayList<String>(  );

                            if ( user != null )
                            {
                                String[] lRoles = securityService.getRolesByUser( user );
                                roleKeyList = new ArrayList<String>( Arrays.asList( lRoles ) );
                            }

                            filter.setRoleKeyList( roleKeyList, true, true );
                        }

                        listResultRecordId = DirectorySearchService.getInstance(  )
                                                                   .getSearchResults( directory, mapQuery, null, null,
                                null, filter, plugin );

                        boolean bIsDisplayedDirectly = Boolean.parseBoolean( AppPropertiesService.getProperty( 
                                    PROPERTY_DISPLAY_ONE_RESULT_DIRECTLY ) );

                        if ( bIsDisplayedDirectly && ( listResultRecordId.size(  ) == 1 ) &&
                                ( session.getAttribute( SESSION_ONE_RECORD_ID ) == null ) )
                        {
                            record = RecordHome.findByPrimaryKey( listResultRecordId.get( 0 ), plugin );

                            if ( ( record != null ) && ( record.getDirectory(  ) != null ) )
                            {
                                directory = DirectoryHome.findByPrimaryKey( record.getDirectory(  ).getIdDirectory(  ),
                                        plugin );
                            }

                            if ( ( record == null ) ||
                                    ( ( record.getRoleKey(  ) != null ) &&
                                    !record.getRoleKey(  ).equals( Directory.ROLE_NONE ) &&
                                    SecurityService.isAuthenticationEnable(  ) &&
                                    !SecurityService.getInstance(  ).isUserInRole( request, record.getRoleKey(  ) ) ) )
                            {
                                SiteMessageService.setMessage( request, MESSAGE_ACCESS_DENIED, SiteMessage.TYPE_STOP );
                            }

                            record.setDirectory( directory );

                            String strDirectoryRecord = getHtmlResultRecord( directory, record, request.getLocale(  ),
                                    plugin, session );
                            model.put( MARK_STR_RESULT_RECORD, strDirectoryRecord );
                            bSingleResult = true;
                            model.put( MARK_ONE_RESULT, true );

                            session.setAttribute( SESSION_ONE_RECORD_ID, record.getIdRecord(  ) );

                            listRecord = (List<Record>) session.getAttribute( SESSION_ID_LAST_RECORD );

                            if ( ( listRecord != null ) && ( listRecord.size(  ) > 0 ) &&
                                    ( listRecord.get( listRecord.size(  ) - 1 ).getIdRecord(  ) != record.getIdRecord(  ) ) )
                            {
                                listRecord.add( record );
                            }
                            else if ( listRecord == null )
                            {
                                listRecord = new ArrayList<Record>(  );
                                listRecord.add( record );
                            }

                            session.setAttribute( SESSION_ID_LAST_RECORD, listRecord );

                            listIdDirectory = (List<Integer>) session.getAttribute( SESSION_ID_LAST_DIRECTORY );

                            if ( ( listIdDirectory != null ) && ( listIdDirectory.size(  ) > 0 ) &&
                                    !listIdDirectory.get( listIdDirectory.size(  ) - 1 )
                                                        .equals( record.getDirectory(  ).getIdDirectory(  ) ) )
                            {
                                listIdDirectory.add( record.getDirectory(  ).getIdDirectory(  ) );
                            }
                            else if ( listIdDirectory == null )
                            {
                                listIdDirectory = new ArrayList<Integer>(  );
                                listIdDirectory.add( record.getDirectory(  ).getIdDirectory(  ) );
                            }

                            session.setAttribute( SESSION_ID_LAST_DIRECTORY, listIdDirectory );
                        }
                        else
                        {
                            session.setAttribute( SESSION_ONE_RECORD_ID, null );
                        }

                        if ( ( listResultRecordId.size(  ) != 1 ) || !bIsDisplayedDirectly )
                        {
                            bSingleResult = false;

                            Paginator paginator = new Paginator( listResultRecordId,
                                    directory.getNumberRecordPerPage(  ), urlDirectoryXpage.getUrl(  ),
                                    PARAMETER_FILTER_PAGE_INDEX, strFilterPageIndex );

                            model.put( MARK_PAGINATOR, paginator );

                            List<Record> lRecord = RecordHome.loadListByListId( (List<Integer>) paginator.getPageItems(  ),
                                    plugin );

                            if ( lRecord.size(  ) > 0 )
                            {
                                String strResultList = getHtmlResultList( directory, lRecord, request.getLocale(  ),
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

                    String strFormSearch = getHtmlFormSearch( directory, mapQuery, request, plugin );
                    model.put( MARK_STR_FORM_SEARCH, strFormSearch );
                }

                model.put( MARK_LOCALE, request.getLocale(  ) );
            }
            else
            {
                model.put( MARK_UNAVAILABILITY_MESSAGE, directory.getUnavailabilityMessage(  ) );
            }

            EntryFilter filterGeolocation = new EntryFilter(  );
            filterGeolocation.setIdDirectory( directory.getIdDirectory(  ) );
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
                    request.getLocale(  ), model );
            page.setContent( template.getHtml(  ) );
        }

        return page;
    }

    /**
     * return the HTML form search
     * @param directory the directory
     * @param mapQuery the mapQuerySearch
     * @param request the HttpServletRequesr
     * @param plugin  the plugin
     * @return the html form search
     */
    private String getHtmlFormSearch( Directory directory, HashMap<String, List<RecordField>> mapQuery,
        HttpServletRequest request, Plugin plugin )
    {
        //build entryFilter
        EntryFilter entryFilter = new EntryFilter(  );
        entryFilter.setIdDirectory( directory.getIdDirectory(  ) );
        entryFilter.setIsGroup( EntryFilter.FILTER_FALSE );
        entryFilter.setIsComment( EntryFilter.FILTER_FALSE );
        entryFilter.setIsIndexed( EntryFilter.FILTER_TRUE );

        List<IEntry> listEntryFormMainSearch = new ArrayList<IEntry>(  );
        List<IEntry> listEntryFormComplementarySearch = new ArrayList<IEntry>(  );
        IEntry entryStore;

        for ( IEntry entry : EntryHome.getEntryList( entryFilter, plugin ) )
        {
            entryStore = EntryHome.findByPrimaryKey( entry.getIdEntry(  ), plugin );

            if ( entryStore.isRoleAssociated(  ) )
            {
                entryStore.setFields( DirectoryUtils.getAuthorizedFieldsByRole( request, entryStore.getFields(  ) ) );
            }

            if ( !entryStore.isShownInAdvancedSearch(  ) )
            {
                listEntryFormMainSearch.add( entryStore );
            }
            else
            {
                listEntryFormComplementarySearch.add( entryStore );
            }
        }

        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_ENTRY_LIST_FORM_MAIN_SEARCH, listEntryFormMainSearch );
        model.put( MARK_ENTRY_LIST_FORM_COMPLEMENTARY_SEARCH, listEntryFormComplementarySearch );
        model.put( MARK_MAP_ID_ENTRY_LIST_RECORD_FIELD, mapQuery );
        model.put( MARK_DIRECTORY, directory );
        model.put( MARK_LOCALE, request.getLocale(  ) );

        HttpSession session = request.getSession(  );

        if ( session.getAttribute( SESSION_ONE_RECORD_ID ) != null )
        {
            model.put( MARK_ONE_SESSION_ID, session.getAttribute( SESSION_ONE_RECORD_ID ) );
        }

        HtmlTemplate templateXmlFormSearch = AppTemplateService.getTemplate( TEMPLATE_XML_FORM_SEARCH,
                request.getLocale(  ), model );

        File fileTemplate = null;
        DirectoryXsl directoryXsl = DirectoryXslHome.findByPrimaryKey( directory.getIdFormSearchTemplate(  ), plugin );

        if ( directoryXsl.getFile(  ) != null )
        {
            fileTemplate = FileHome.findByPrimaryKey( directoryXsl.getFile(  ).getIdFile(  ), plugin );
        }

        if ( ( fileTemplate != null ) && ( fileTemplate.getPhysicalFile(  ) != null ) )
        {
            fileTemplate.setPhysicalFile( PhysicalFileHome.findByPrimaryKey( 
                    fileTemplate.getPhysicalFile(  ).getIdPhysicalFile(  ), plugin ) );

            XmlTransformerService xmlTransformerService = new XmlTransformerService(  );
            PhysicalFile physicalFile = fileTemplate.getPhysicalFile(  );
            String strXslId = XSL_UNIQUE_PREFIX_ID + physicalFile.getIdPhysicalFile(  );
            String strResult = xmlTransformerService.transformBySourceWithXslCache( templateXmlFormSearch.getHtml(  ),
                    physicalFile.getValue(  ), strXslId, null, null );

            return strResult;
        }

        return DirectoryUtils.EMPTY_STRING;
    }

    /**
     * return the HTML result list
     * @param directory the directory
     * @param listRecord the list of record
     * @param listEntry the list of entry
     * @param locale the locale
     * @param plugin the plugin
     * @return the HTML result list
     */
    private String getHtmlResultList( Directory directory, List<Record> listRecord, Locale locale, Plugin plugin )
    {
        StringBuffer strBufferListRecordXml = new StringBuffer(  );
        StringBuffer strBufferListEntryXml = new StringBuffer(  );

        //get directory Entry
        EntryFilter entryFilter = new EntryFilter(  );
        entryFilter.setIdDirectory( directory.getIdDirectory(  ) );
        entryFilter.setIsGroup( EntryFilter.FILTER_FALSE );
        entryFilter.setIsComment( EntryFilter.FILTER_FALSE );
        entryFilter.setIsShownInResultList( EntryFilter.FILTER_TRUE );

        List<IEntry> listEntrySearchResult = EntryHome.getEntryList( entryFilter, plugin );

        RecordFieldFilter recordFieldFilter = new RecordFieldFilter(  );

        for ( Record record : listRecord )
        {
            recordFieldFilter.setIdRecord( record.getIdRecord(  ) );
            recordFieldFilter.setIsEntryShownInResultList( RecordFieldFilter.FILTER_TRUE );
            record.setListRecordField( RecordFieldHome.getRecordFieldList( recordFieldFilter, plugin ) );

            State state = null;

            if ( WorkflowService.getInstance(  ).isAvailable(  ) )
            {
                state = WorkflowService.getInstance(  )
                                       .getState( record.getIdRecord(  ), Record.WORKFLOW_RESOURCE_TYPE,
                        directory.getIdWorkflow(  ), Integer.valueOf( directory.getIdDirectory(  ) ), null );
            }

            strBufferListRecordXml.append( record.getXml( plugin, locale, false, state, listEntrySearchResult, true,
                    true, false, true ) );
        }

        for ( IEntry entry : listEntrySearchResult )
        {
            entry.getXml( plugin, locale, strBufferListEntryXml );
        }

        HashMap<String, String> model = new HashMap<String, String>(  );

        if ( ( directory.getIdWorkflow(  ) != DirectoryUtils.CONSTANT_ID_NULL ) &&
                WorkflowService.getInstance(  ).isAvailable(  ) )
        {
            model.put( TAG_DISPLAY, TAG_YES );
        }
        else
        {
            model.put( TAG_DISPLAY, TAG_NO );
        }

        XmlUtil.addEmptyElement( strBufferListEntryXml, TAG_STATUS, model );

        StringBuilder strBufferXml = new StringBuilder(  );
        strBufferXml.append( XmlUtil.getXmlHeader(  ) );
        strBufferXml.append( directory.getXml( plugin, locale, strBufferListRecordXml, strBufferListEntryXml ) );

        File fileTemplate = null;
        DirectoryXsl directoryXsl = DirectoryXslHome.findByPrimaryKey( directory.getIdResultListTemplate(  ), plugin );

        if ( directoryXsl.getFile(  ) != null )
        {
            fileTemplate = FileHome.findByPrimaryKey( directoryXsl.getFile(  ).getIdFile(  ), plugin );
        }

        if ( ( fileTemplate != null ) && ( fileTemplate.getPhysicalFile(  ) != null ) )
        {
            fileTemplate.setPhysicalFile( PhysicalFileHome.findByPrimaryKey( 
                    fileTemplate.getPhysicalFile(  ).getIdPhysicalFile(  ), plugin ) );

            HashMap<String, String> params = new HashMap<String, String>(  );
            String strParamTitleDescriptive = I18nService.getLocalizedString( PROPERTY_DIRECTORY_FRAME_TITLE_DESCRIPTIVE,
                    locale );

            params.put( MARK_TITLE_DESCRIPTIVE, strParamTitleDescriptive );

            XmlTransformerService xmlTransformerService = new XmlTransformerService(  );
            PhysicalFile physicalFile = fileTemplate.getPhysicalFile(  );
            String strXslId = XSL_UNIQUE_PREFIX_ID + physicalFile.getIdPhysicalFile(  );
            String strResult = xmlTransformerService.transformBySourceWithXslCache( strBufferXml.toString(  ),
                    physicalFile.getValue(  ), strXslId, params, null );

            return strResult;
        }

        return DirectoryUtils.EMPTY_STRING;
    }

    /**
     * return the HTML result lrcord
     * @param directory the directory
     * @param record the record
     * @param locale the locale
     * @param plugin the plugin
     * @return the Html result record
     */
    private String getHtmlResultRecord( Directory directory, Record record, Locale locale, Plugin plugin,
        HttpSession session )
    {
        RecordFieldFilter filter = new RecordFieldFilter(  );
        filter.setIdRecord( record.getIdRecord(  ) );
        filter.setIsEntryShownInResultRecord( RecordFieldFilter.FILTER_TRUE );

        List<RecordField> listRecordField = RecordFieldHome.getRecordFieldList( filter, plugin );
        record.setListRecordField( listRecordField );

        StringBuffer strBufferListEntryXml = new StringBuffer(  );

        //get directory Entry
        EntryFilter entryFilter = new EntryFilter(  );
        entryFilter.setIdDirectory( record.getDirectory(  ).getIdDirectory(  ) );
        entryFilter.setIsComment( EntryFilter.FILTER_FALSE );
        entryFilter.setIsShownInResultRecord( EntryFilter.FILTER_TRUE );

        List<IEntry> listEntry = DirectoryUtils.getFormEntriesByFilter( entryFilter, plugin );

        for ( IEntry entry : listEntry )
        {
            entry.getXml( plugin, locale, strBufferListEntryXml );
        }

        StringBuffer strBufferListRecordXml = new StringBuffer(  );
        strBufferListRecordXml.append( record.getXml( plugin, locale, false, null, listEntry, true, true, false, true ) );

        StringBuilder strBufferXml = new StringBuilder(  );
        strBufferXml.append( XmlUtil.getXmlHeader(  ) );
        strBufferXml.append( directory.getXml( plugin, locale, strBufferListRecordXml, strBufferListEntryXml ) );

        File fileTemplate = null;
        DirectoryXsl directoryXsl = DirectoryXslHome.findByPrimaryKey( record.getDirectory(  )
                                                                             .getIdResultRecordTemplate(  ), plugin );

        if ( directoryXsl.getFile(  ) != null )
        {
            fileTemplate = FileHome.findByPrimaryKey( directoryXsl.getFile(  ).getIdFile(  ), plugin );
        }

        if ( ( fileTemplate != null ) && ( fileTemplate.getPhysicalFile(  ) != null ) )
        {
            fileTemplate.setPhysicalFile( PhysicalFileHome.findByPrimaryKey( 
                    fileTemplate.getPhysicalFile(  ).getIdPhysicalFile(  ), plugin ) );

            HashMap<String, String> params = new HashMap<String, String>(  );
            String strParamTitleBackSearch = I18nService.getLocalizedString( PROPERTY_DIRECTORY_FRAME_TITLE_BACK_SEARCH,
                    locale );
            String strParamLabelBackSearch = I18nService.getLocalizedString( PROPERTY_DIRECTORY_FRAME_LABEL_BACK_SEARCH,
                    locale );

            params.put( MARK_TITLE_BACK_SEARCH, strParamTitleBackSearch );
            params.put( MARK_LABEL_BACK_SEARCH, strParamLabelBackSearch );
            params.put( MARK_ID_DIRECTORY, Integer.toString( record.getDirectory(  ).getIdDirectory(  ) ) );

            //Params linked with last record
            if ( session.getAttribute( SESSION_ID_LAST_RECORD ) != null )
            {
                List<Record> listRecord = (List<Record>) session.getAttribute( SESSION_ID_LAST_RECORD );
                String strParamTitleBackRecord = I18nService.getLocalizedString( PROPERTY_DIRECTORY_FRAME_TITLE_BACK_RECORD,
                        locale );
                String strParamLabelBackRecord = I18nService.getLocalizedString( PROPERTY_DIRECTORY_FRAME_LABEL_BACK_RECORD,
                        locale );
                params.put( MARK_TITLE_BACK_RECORD, strParamTitleBackRecord );
                params.put( MARK_LABEL_BACK_RECORD, strParamLabelBackRecord );

                if ( ( listRecord != null ) && ( listRecord.size(  ) > 1 ) )
                {
                    Record lastRecord = listRecord.get( listRecord.size(  ) - 2 );
                    params.put( MARK_ID_LAST_RECORD, "" + lastRecord.getIdRecord(  ) );
                }
            }

            if ( session.getAttribute( SESSION_ID_LAST_DIRECTORY ) != null )
            {
                List<Integer> listIdDirectory = (List<Integer>) session.getAttribute( SESSION_ID_LAST_DIRECTORY );

                if ( ( listIdDirectory != null ) && ( listIdDirectory.size(  ) > 1 ) )
                {
                    Integer lastIdDirectory = listIdDirectory.get( listIdDirectory.size(  ) - 2 );
                    params.put( MARK_ID_LAST_DIRECTORY, "" + lastIdDirectory );
                }
            }

            XmlTransformerService xmlTransformerService = new XmlTransformerService(  );
            PhysicalFile physicalFile = fileTemplate.getPhysicalFile(  );
            String strXslId = XSL_UNIQUE_PREFIX_ID + physicalFile.getIdPhysicalFile(  );
            String strResult = xmlTransformerService.transformBySourceWithXslCache( strBufferXml.toString(  ),
                    physicalFile.getValue(  ), strXslId, params, null );

            return strResult;
        }

        return DirectoryUtils.EMPTY_STRING;
    }

    /**
     * Init the SESSION_FILTER_MAP_QUERY session attribute
     * @param request HttpServletRequest
     */
    private static void initMapQuery( HttpServletRequest request )
    {
        if ( request.getParameter( INIT_MAP_QUERY ) != null )
        {
            request.getSession(  ).removeAttribute( SESSION_FILTER_MAP_QUERY );
        }
    }
}

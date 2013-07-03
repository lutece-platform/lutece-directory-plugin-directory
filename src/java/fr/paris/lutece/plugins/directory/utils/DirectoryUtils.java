/*
 * Copyright (c) 2002-2013, Mairie de Paris
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
package fr.paris.lutece.plugins.directory.utils;

import fr.paris.lutece.plugins.directory.business.Directory;
import fr.paris.lutece.plugins.directory.business.DirectoryHome;
import fr.paris.lutece.plugins.directory.business.EntryFilter;
import fr.paris.lutece.plugins.directory.business.EntryHome;
import fr.paris.lutece.plugins.directory.business.EntryType;
import fr.paris.lutece.plugins.directory.business.EntryTypeDownloadUrl;
import fr.paris.lutece.plugins.directory.business.EntryTypeHome;
import fr.paris.lutece.plugins.directory.business.Field;
import fr.paris.lutece.plugins.directory.business.FieldHome;
import fr.paris.lutece.plugins.directory.business.File;
import fr.paris.lutece.plugins.directory.business.IEntry;
import fr.paris.lutece.plugins.directory.business.PhysicalFile;
import fr.paris.lutece.plugins.directory.business.Record;
import fr.paris.lutece.plugins.directory.business.RecordField;
import fr.paris.lutece.plugins.directory.business.RecordFieldFilter;
import fr.paris.lutece.plugins.directory.business.RecordFieldHome;
import fr.paris.lutece.plugins.directory.business.attribute.DirectoryAttribute;
import fr.paris.lutece.plugins.directory.service.DirectoryPlugin;
import fr.paris.lutece.plugins.directory.service.directorysearch.DirectorySearchService;
import fr.paris.lutece.plugins.directory.service.upload.DirectoryAsynchronousUploadHandler;
import fr.paris.lutece.plugins.directory.web.action.DirectoryAdminSearchFields;
import fr.paris.lutece.plugins.directory.web.action.DirectorySiteSearchFields;
import fr.paris.lutece.plugins.directory.web.action.IDirectorySearchFields;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.blobstore.BlobStoreClientException;
import fr.paris.lutece.portal.service.fileupload.FileUploadService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupService;
import fr.paris.lutece.portal.web.constants.Parameters;
import fr.paris.lutece.portal.web.upload.MultipartHttpServletRequest;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.date.DateUtil;
import fr.paris.lutece.util.filesystem.FileSystemUtil;
import fr.paris.lutece.util.string.StringUtil;
import fr.paris.lutece.util.url.UrlItem;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.ReflectionUtils;


/**
 * 
 * class DirectoryUtils
 * 
 */
public final class DirectoryUtils
{
    // other constants
    public static final String CONSTANT_WHERE = " WHERE ";
    public static final String CONSTANT_AND = " AND ";
    public static final String CONSTANT_OR = " OR ";
    public static final String CONSTANT_EQUAL = "=";
    public static final String CONSTANT_COMA = ",";
    public static final String CONSTANT_INTERROGATION_MARK = "?";
    public static final String CONSTANT_AMPERSAND = "&";
    public static final int CONSTANT_ID_NULL = -1;
    public static final int CONSTANT_ID_ZERO = 0;
    public static final String EMPTY_STRING = "";
    public static final String CONSTANT_ID = "id";
    public static final String CONSTANT_NAME = "name";
    public static final String CONSTANT_TRUE = "true";
    public static final String CONSTANT_DOT = ".";
    public static final String CONSTANT_UNDERSCORE = "_";

    // TEMPLATES
    public static final String TEMPLATE_FORM_DIRECTORY_RECORD = "admin/plugins/directory/html_code_form_directory_record.html";
    public static final String TEMPLATE_FORM_SEARCH_DIRECTORY_RECORD = "admin/plugins/directory/html_code_form_search_directory_record.html";

    // MESSAGES
    public static final String MESSAGE_DIRECTORY_ERROR_MANDATORY_FIELD = "directory.message.directory_error.mandatory.field";
    public static final String MESSAGE_DIRECTORY_ERROR = "directory.message.directory_error";
    public static final String MESSAGE_DIRECTORY_ERROR_MIME_TYPE = "directory.message.directory_error.mime_type";
    public static final String MESSAGE_SELECT_RECORDS = "directory.message.select_records";
    public static final String MESSAGE_RECORD_INVALID_STATE = "directory.message.record.invalid_state";

    // PARAMETERS
    public static final String PARAMETER_ID_DIRECTORY = "id_directory";
    public static final String PARAMETER_ID_DIRECTORY_RECORD = "id_directory_record";
    public static final String PARAMETER_ID_ENTRY = "id_entry";
    public static final String PARAMETER_SESSION = "session";
    public static final String PARAMETER_DELETE_PREFIX = "delete_";
    public static final String PARAMETER_UPLOAD_SUBMIT = "_directory_upload_submit_";
    public static final String PARAMETER_ID_FILE = "id_file";
    public static final String PARAMETER_ID_ACTION = "id_action";
    public static final String PARAMETER_SHOW_ACTION_RESULT = "show_action_result";
    public static final String PARAMETER_DATECREATION = "dateCreation";
    public static final String PARAMETER_DATEMODIFICATION = "dateModification";

    // JSP
    public static final String JSP_MANAGE_DIRECTORY_RECORD = "jsp/admin/plugins/directory/ManageDirectoryRecord.jsp";

    // SESSION
    public static final String SESSION_DIRECTORY_LIST_SUBMITTED_RECORD_FIELDS = "directory_list_submitted_record_fields";
    public static final String SESSION_DIRECTORY_TASKS_SUBMITTED_RECORD_FIELDS = "directory_tasks_submitted_record_fields";

    // PROPERTIES
    public static final String PROPERTY_LUTECE_BASE_URL = "lutece.base.url";
    public static final String PROPERTY_LUTECE_PROD_URL = "lutece.prod.url";
    private static final String PARAMETER_ID_ENTRY_TYPE = "id_type";
    private static final String CONSTANT_CHARACTER_DOUBLE_QUOTE = "\"";
    private static final String CONSTANT_CHARACTER_SIMPLE_QUOTE = "'";
    private static final String CONSTANTE_CHARACTERNEW_LINE = "\n";
    private static final String CONSTANTE_CHARACTER_RETURN = "\r";
    private static final String REGEX_ID = "^[\\d]+$";

    /**
     * DirectoryUtils
     * 
     */
    private DirectoryUtils( )
    {
    }

    /**
     * return current Timestamp
     * 
     * @return return current Timestamp
     */
    public static Timestamp getCurrentTimestamp( )
    {
        return new Timestamp( GregorianCalendar.getInstance( ).getTimeInMillis( ) );
    }

    /**
     * return an instance of IEntry function of type entry
     * 
     * @param request
     *            the request
     * @param plugin
     *            the plugin
     * @return an instance of IEntry function of type entry
     */
    public static IEntry createEntryByType( HttpServletRequest request, Plugin plugin )
    {
        String strIdType = request.getParameter( PARAMETER_ID_ENTRY_TYPE );
        int nIdType = convertStringToInt( strIdType );

        return createEntryByType( nIdType, plugin );
    }

    /**
     * return an instance of IEntry function of type entry
     * 
     * @param nIdType
     *            the type id
     * @param plugin
     *            the plugin
     * @return an instance of IEntry function of type entry
     */
    public static IEntry createEntryByType( int nIdType, Plugin plugin )
    {
        IEntry entry = null;
        EntryType entryType;
        entryType = EntryTypeHome.findByPrimaryKey( nIdType, plugin );

        if ( entryType != null )
        {
            try
            {
                entry = (IEntry) Class.forName( entryType.getClassName( ) ).newInstance( );
                entry.setEntryType( entryType );
            }
            catch ( ClassNotFoundException e )
            {
                // class doesn't exist
                AppLogService.error( e );
            }
            catch ( InstantiationException e )
            {
                // Class is abstract or is an interface or haven't accessible
                // builder
                AppLogService.error( e );
            }
            catch ( IllegalAccessException e )
            {
                // can't access to rhe class
                AppLogService.error( e );
            }
        }

        return entry;
    }

    /**
     * return the index in the list of the entry whose key is specified in
     * parameter
     * 
     * @param nIdEntry
     *            the key of the entry
     * @param listEntry
     *            the list of the entry
     * @return the index in the list of the entry whose key is specified in
     *         parameter
     */
    public static int getIndexEntryInTheEntryList( int nIdEntry, List<IEntry> listEntry )
    {
        int nIndex = 0;

        for ( IEntry entry : listEntry )
        {
            if ( entry.getIdEntry( ) == nIdEntry )
            {
                return nIndex;
            }

            nIndex++;
        }

        return nIndex;
    }

    /**
     * return the index in the list of the field whose key is specified in
     * parameter
     * 
     * @param nIdField
     *            the key of the field
     * @param listField
     *            the list of field
     * @return the index in the list of the field whose key is specified in
     *         parameter
     */
    public static int getIndexFieldInTheFieldList( int nIdField, List<Field> listField )
    {
        int nIndex = 0;

        for ( Field field : listField )
        {
            if ( field.getIdField( ) == nIdField )
            {
                return nIndex;
            }

            nIndex++;
        }

        return nIndex;
    }

    /**
     * return all entry associate to the directory
     * 
     * @param nIdDirectory
     *            the id of the directory
     * @param plugin
     *            the plugin
     * @param user
     *            the AdminUser
     * @return list of entry
     */
    public static List<IEntry> getFormEntries( int nIdDirectory, Plugin plugin, AdminUser user )
    {
        IEntry entryFistLevel;
        EntryFilter filter = new EntryFilter( );
        filter.setIdDirectory( nIdDirectory );
        filter.setIsEntryParentNull( EntryFilter.FILTER_TRUE );

        List<IEntry> listEntryFirstLevel = EntryHome.getEntryList( filter, plugin );
        List<IEntry> listEntryChildren;
        List<IEntry> listEntryImbricate = new ArrayList<IEntry>( );

        for ( IEntry entry : listEntryFirstLevel )
        {
            entryFistLevel = EntryHome.findByPrimaryKey( entry.getIdEntry( ), plugin );

            if ( entryFistLevel.isWorkgroupAssociated( ) )
            {
                entryFistLevel.setFields( DirectoryUtils.getAuthorizedFieldsByWorkgroup( entryFistLevel.getFields( ),
                        user ) );
            }

            if ( entryFistLevel.getEntryType( ).getGroup( ) )
            {
                filter = new EntryFilter( );
                filter.setIdEntryParent( entryFistLevel.getIdEntry( ) );
                listEntryChildren = new ArrayList<IEntry>( );

                for ( IEntry entryChildren : EntryHome.getEntryList( filter, plugin ) )
                {
                    IEntry entryTmp = EntryHome.findByPrimaryKey( entryChildren.getIdEntry( ), plugin );

                    if ( entryTmp.isWorkgroupAssociated( ) )
                    {
                        entryTmp.setFields( DirectoryUtils.getAuthorizedFieldsByWorkgroup( entryFistLevel.getFields( ),
                                user ) );
                    }

                    listEntryChildren.add( entryTmp );
                }

                entryFistLevel.setChildren( listEntryChildren );
            }

            listEntryImbricate.add( entryFistLevel );
        }

        return listEntryImbricate;
    }

    /**
     * return all entry associate to the directory
     * 
     * @param filter
     *            entry filter
     * @param plugin
     *            the plugin
     * @return list of entry
     */
    public static List<IEntry> getFormEntriesByFilter( EntryFilter filter, Plugin plugin )
    {
        IEntry entryFistLevel;
        filter.setIsEntryParentNull( EntryFilter.FILTER_TRUE );

        List<IEntry> listEntryFirstLevel = EntryHome.getEntryList( filter, plugin );
        List<IEntry> listEntryChildren;
        List<IEntry> listEntryImbricate = new ArrayList<IEntry>( );

        for ( IEntry entry : listEntryFirstLevel )
        {
            entryFistLevel = EntryHome.findByPrimaryKey( entry.getIdEntry( ), plugin );

            if ( entryFistLevel.getEntryType( ).getGroup( ) )
            {
                filter = new EntryFilter( );
                filter.setIdEntryParent( entryFistLevel.getIdEntry( ) );
                listEntryChildren = new ArrayList<IEntry>( );

                for ( IEntry entryChildren : EntryHome.getEntryList( filter, plugin ) )
                {
                    IEntry entryTmp = EntryHome.findByPrimaryKey( entryChildren.getIdEntry( ), plugin );
                    listEntryChildren.add( entryTmp );
                }

                entryFistLevel.setChildren( listEntryChildren );
            }

            listEntryImbricate.add( entryFistLevel );
        }

        return listEntryImbricate;
    }

    /**
     * get a Map which contains for each entry the list of recordField object
     * associated
     * 
     * @param lisEntry
     *            the list of entry associate to the record
     * @param nIdRecord
     *            the id of the record
     * @param plugin
     *            plugin
     * @return a map
     */
    public static Map<String, List<RecordField>> getMapIdEntryListRecordField( List<IEntry> lisEntry, int nIdRecord,
            Plugin plugin )
    {
        return getMapIdEntryListRecordField( lisEntry, nIdRecord, plugin, true );
    }

    /**
     * get a Map which contains for each entry the list of recordField object
     * associated
     * 
     * @param lisEntry
     *            the list of entry associate to the record
     * @param nIdRecord
     *            the id of the record
     * @param plugin
     *            plugin
     * @param bGetFileName
     *            true if it must get the file name, false otherwise <br />
     *            Warning : The file name is fetch by a webservice call. Beware
     *            of performance.
     * @return a map
     */
    public static Map<String, List<RecordField>> getMapIdEntryListRecordField( List<IEntry> lisEntry, int nIdRecord,
            Plugin plugin, boolean bGetFileName )
    {
        Map<String, List<RecordField>> map = new HashMap<String, List<RecordField>>( );

        RecordFieldFilter filter = new RecordFieldFilter( );
        filter.setIdRecord( nIdRecord );

        for ( IEntry entryFistLevel : lisEntry )
        {
            if ( entryFistLevel.getChildren( ) != null )
            {
                for ( IEntry child : entryFistLevel.getChildren( ) )
                {
                    buildMapIdEntryListRecordField( map, child, filter, plugin, bGetFileName );
                }
            }

            buildMapIdEntryListRecordField( map, entryFistLevel, filter, plugin, bGetFileName );
        }

        return map;
    }

    /**
     * Build the map of <idEntry, RecordFields> from a given record
     * @param record the record
     * @return the map of <idEntry, RecordFields>
     */
    public static Map<String, List<RecordField>> buildMapIdEntryListRecordField( Record record )
    {
        Map<String, List<RecordField>> map = new HashMap<String, List<RecordField>>( );

        for ( RecordField recordField : record.getListRecordField( ) )
        {
            if ( ( recordField != null ) && ( recordField.getEntry( ) != null ) )
            {
                recordField.setRecord( record );

                String strIdEntry = Integer.toString( recordField.getEntry( ).getIdEntry( ) );
                List<RecordField> listRecordFields = map.get( strIdEntry );

                if ( listRecordFields == null )
                {
                    listRecordFields = new ArrayList<RecordField>( );
                }

                listRecordFields.add( recordField );
                map.put( strIdEntry, listRecordFields );
            }
        }

        return map;
    }

    /**
     * Gets all {@link RecordField} for the entry
     * 
     * @param entry
     *            the entry
     * @param nIdRecord
     *            the record id
     * @param plugin
     *            the plugin
     * @return the list
     */
    public static List<RecordField> getListRecordField( IEntry entry, int nIdRecord, Plugin plugin )
    {
        RecordFieldFilter filter = new RecordFieldFilter( );
        filter.setIdRecord( nIdRecord );

        filter.setIdEntry( entry.getIdEntry( ) );

        return RecordFieldHome.getRecordFieldList( filter, plugin );
    }

    /**
     * get a Map which contains for each entry the list of recordField object
     * associated
     * 
     * @param lisEntry
     *            the list of entry associate to the record
     * @param nIdRecord
     *            the id of the record
     * @param plugin
     *            plugin
     * @return a map
     */
    public static Map<String, List<RecordField>> getSpecificMapIdEntryListRecordField( List<IEntry> lisEntry,
            int nIdRecord, Plugin plugin )
    {
        Map<String, List<RecordField>> map = new HashMap<String, List<RecordField>>( );

        List<Integer> listIdEntry = new ArrayList<Integer>( );

        for ( IEntry entryFistLevel : lisEntry )
        {
            listIdEntry.add( entryFistLevel.getIdEntry( ) );

            if ( entryFistLevel.getChildren( ) != null )
            {
                for ( IEntry child : entryFistLevel.getChildren( ) )
                {
                    listIdEntry.add( child.getIdEntry( ) );
                }
            }
        }

        List<RecordField> lRF = RecordFieldHome.getRecordFieldSpecificList( listIdEntry, nIdRecord, plugin );
        Map<Integer, List<RecordField>> tt = new HashMap<Integer, List<RecordField>>( );

        for ( RecordField rf : lRF )
        {
            Integer nIdEntry = Integer.valueOf( rf.getEntry( ).getIdEntry( ) );

            if ( tt.containsKey( nIdEntry ) )
            {
                tt.get( nIdEntry ).add( rf );
            }
            else
            {
                List<RecordField> lRFTmp = new ArrayList<RecordField>( );
                lRFTmp.add( rf );
                tt.put( nIdEntry, lRFTmp );
            }
        }

        Iterator<Entry<Integer, List<RecordField>>> it = tt.entrySet( ).iterator( );

        while ( it.hasNext( ) )
        {
            Entry<Integer, List<RecordField>> ent = it.next( );
            map.put( ent.getKey( ).toString( ), ent.getValue( ) );
        }

        return map;
    }

    /**
     * Get the request data and if there is no error insert the data in the
     * record specified in parameter. return null if there is no error or else
     * return a DirectoryError object
     * 
     * @param request
     *            the request
     * @param record
     *            the record
     * @param plugin
     *            the plugin
     * @param locale
     *            the locale
     * @throws DirectoryErrorException If an error occurs
     */
    public static void getDirectoryRecordData( HttpServletRequest request, Record record, Plugin plugin, Locale locale )
            throws DirectoryErrorException
    {
        boolean bTestDirectoryError = true;
        String strUploadAction = DirectoryAsynchronousUploadHandler.getHandler( ).getUploadAction( request );

        if ( StringUtils.isNotBlank( strUploadAction ) )
        {
            bTestDirectoryError = false;
        }

        List<RecordField> listRecordFieldResult = new ArrayList<RecordField>( );
        EntryFilter filter = new EntryFilter( );
        filter.setIdDirectory( record.getDirectory( ).getIdDirectory( ) );
        filter.setIsComment( EntryFilter.FILTER_FALSE );
        filter.setIsEntryParentNull( EntryFilter.FILTER_TRUE );

        List<IEntry> listEntryFirstLevel = EntryHome.getEntryList( filter, plugin );

        for ( IEntry entry : listEntryFirstLevel )
        {
            DirectoryUtils.getDirectoryRecordFieldData( record, request, entry.getIdEntry( ), bTestDirectoryError,
                    listRecordFieldResult, plugin, locale );
        }

        record.setListRecordField( listRecordFieldResult );
    }

    /**
     * Get the request data and return a Map which contains for each entry the
     * list of recordField object associated
     * 
     * @param request
     *            the request
     * @param nIdDirectory
     *            the id of the directory
     * @param plugin
     *            the plugin
     * @param locale
     *            the locale
     * @return a map
     * @throws DirectoryErrorException If an error occurs
     */
    public static HashMap<String, List<RecordField>> getSearchRecordData( HttpServletRequest request, int nIdDirectory,
            Plugin plugin, Locale locale ) throws DirectoryErrorException
    {
        HashMap<String, List<RecordField>> mapSearchQuery = new HashMap<String, List<RecordField>>( );
        List<RecordField> listRecordFieldTmp;
        EntryFilter filter = new EntryFilter( );
        filter.setIdDirectory( nIdDirectory );
        filter.setIsComment( EntryFilter.FILTER_FALSE );
        filter.setIsGroup( EntryFilter.FILTER_FALSE );
        filter.setIsIndexed( EntryFilter.FILTER_TRUE );

        List<IEntry> listEntry = EntryHome.getEntryList( filter, plugin );

        for ( IEntry entry : listEntry )
        {
            listRecordFieldTmp = new ArrayList<RecordField>( );
            DirectoryUtils.getDirectoryRecordFieldData( null, request, entry.getIdEntry( ), false, listRecordFieldTmp,
                    plugin, locale );

            mapSearchQuery.put( Integer.toString( entry.getIdEntry( ) ), listRecordFieldTmp );
        }

        return mapSearchQuery;
    }

    /**
     * perform in the record field list the record field associates with a entry
     * specify in parameter. return null if there is no error in the response
     * else return a DirectoryError Object
     * 
     * @param request
     *            the request
     * @param nIdEntry
     *            the key of the entry
     * @param bTestDirectoryError
     *            true if we must test the validity of user input
     * @param listRecordFieldResult
     *            the list of record field result
     * @param plugin
     *            the plugin
     * @param locale
     *            the locale
     * @throws DirectoryErrorException If an error occurs
     */
    public static void getDirectoryRecordFieldData( Record record, HttpServletRequest request, int nIdEntry,
            boolean bTestDirectoryError, List<RecordField> listRecordFieldResult, Plugin plugin, Locale locale )
            throws DirectoryErrorException
    {
        IEntry entry = null;

        entry = EntryHome.findByPrimaryKey( nIdEntry, plugin );

        List<Field> listField = new ArrayList<Field>( );

        for ( Field field : entry.getFields( ) )
        {
            field = FieldHome.findByPrimaryKey( field.getIdField( ), plugin );
            listField.add( field );
        }

        entry.setFields( listField );

        if ( entry.getEntryType( ).getGroup( ) )
        {
            for ( IEntry entryChild : entry.getChildren( ) )
            {
                getDirectoryRecordFieldData( record, request, entryChild.getIdEntry( ), bTestDirectoryError,
                        listRecordFieldResult, plugin, locale );
            }
        }
        else if ( !entry.getEntryType( ).getComment( ) )
        {
            entry.getRecordFieldData( record, request, bTestDirectoryError, false, listRecordFieldResult, locale );
        }
    }

    /**
     * return the field which key is specified in parameter
     * 
     * @param nIdField
     *            the id of the field who is search
     * @param listField
     *            the list of field
     * @return the field which key is specified in parameter
     */
    public static Field findFieldByIdInTheList( int nIdField, List<Field> listField )
    {
        if ( ( listField != null ) && !listField.isEmpty( ) )
        {
            for ( Field field : listField )
            {
                if ( field.getIdField( ) == nIdField )
                {
                    return field;
                }
            }
        }

        return null;
    }

    /**
     * return the field which value is specified in parameter
     * 
     * @param strFieldValue
     *            the value of the field who is search
     * @param listField
     *            the list of field
     * @return the field which key is specified in parameter
     */
    public static Field findFieldByValueInTheList( String strFieldValue, List<Field> listField )
    {
        if ( ( strFieldValue != null ) && ( listField != null ) && !listField.isEmpty( ) )
        {
            for ( Field field : listField )
            {
                if ( ( field.getValue( ) != null ) && field.getValue( ).trim( ).equals( strFieldValue.trim( ) ) )
                {
                    return field;
                }
            }
        }

        return null;
    }

    /**
     * Return the field which title is specified in parameter
     * @param strTitle the title
     * @param listFields the list of fields
     * @return the field which title is specified in parameter
     */
    public static Field findFieldByTitleInTheList( String strTitle, List<Field> listFields )
    {
        if ( ( listFields == null ) || listFields.isEmpty( ) )
        {
            return null;
        }

        for ( Field field : listFields )
        {
            if ( StringUtils.isNotBlank( strTitle ) )
            {
                if ( trim( strTitle ).equals( trim( field.getTitle( ) ) ) )
                {
                    return field;
                }
            }
            else if ( StringUtils.isBlank( field.getTitle( ) ) )
            {
                return field;
            }
        }

        return null;
    }

    /**
     * return true if the field which key is specified in parameter is in the
     * response list
     * 
     * @param nIdField
     *            the id of the field who is search
     * @param listRecordField
     *            the list of object Response
     * @return true if the field which key is specified in parameter is in the
     *         response list
     */
    public static Boolean isFieldInTheRecordFieldList( int nIdField, List<RecordField> listRecordField )
    {
        for ( RecordField recordField : listRecordField )
        {
            if ( ( recordField.getField( ) != null ) && ( recordField.getField( ).getIdField( ) == nIdField ) )
            {
                return true;
            }
        }

        return false;
    }

    /**
     * write the http header in the response
     * 
     * @param request
     *            the httpServletRequest
     * @param response
     *            the http response
     * @param strFileName
     *            the name of the file who must insert in the response
     * 
     */
    public static void addHeaderResponse( HttpServletRequest request, HttpServletResponse response, String strFileName )
    {
        response.setHeader( "Content-Disposition", "attachment ;filename=\"" + strFileName + "\"" );
        response.setHeader( "Pragma", "public" );
        response.setHeader( "Expires", "0" );
        response.setHeader( "Cache-Control", "must-revalidate,post-check=0,pre-check=0" );
    }

    /**
     * convert a string to int
     * 
     * @param strParameter
     *            the string parameter to convert
     * @return the conversion
     */
    public static int convertStringToInt( String strParameter )
    {
        int nIdParameter = -1;

        try
        {
            if ( ( strParameter != null ) && strParameter.matches( REGEX_ID ) )
            {
                nIdParameter = Integer.parseInt( strParameter );
            }
        }
        catch ( NumberFormatException ne )
        {
            AppLogService.error( ne );
        }

        return nIdParameter;
    }

    /**
     * Returns a copy of the string , with leading and trailing whitespace
     * omitted.
     * 
     * @param strParameter
     *            the string parameter to convert
     * @return null if the strParameter is null other return with leading and
     *         trailing whitespace omitted.
     */
    public static String trim( String strParameter )
    {
        if ( strParameter != null )
        {
            return strParameter.trim( );
        }

        return strParameter;
    }

    /**
     * Get the file contains in the request from the name of the input file
     * 
     * @param strFileInputName
     *            le name of the input file file
     * @param request
     *            the request
     * @return file the file contains in the request from the name of the input
     *         file
     */
    public static File getFileData( String strFileInputName, HttpServletRequest request )
    {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        FileItem fileItem = multipartRequest.getFile( strFileInputName );

        if ( ( fileItem != null ) && ( fileItem.getName( ) != null ) && !EMPTY_STRING.equals( fileItem.getName( ) ) )
        {
            File file = new File( );
            PhysicalFile physicalFile = new PhysicalFile( );
            physicalFile.setValue( fileItem.get( ) );
            file.setTitle( FileUploadService.getFileNameOnly( fileItem ) );
            file.setSize( (int) fileItem.getSize( ) );
            file.setPhysicalFile( physicalFile );
            file.setMimeType( FileSystemUtil.getMIMEType( FileUploadService.getFileNameOnly( fileItem ) ) );

            return file;
        }

        return null;
    }

    /**
     * Builds a query with filters placed in parameters. Consider using
     * {@link #buildQueryWithFilter(StringBuilder, List, String)} instead.
     * 
     * @param strSelect
     *            the select of the query
     * @param listStrFilter
     *            the list of filter to add in the query
     * @param strOrder
     *            the order by of the query
     * @return a query
     */
    public static String buildRequetteWithFilter( String strSelect, List<String> listStrFilter, String strOrder )
    {
        return buildQueryWithFilter( new StringBuilder( strSelect ), listStrFilter, strOrder );
    }

    /**
     * Builds a query with filters placed in parameters
     * 
     * @param sbSQL
     *            the beginning of the query
     * @param listFilter
     *            the list of filter to add in the query
     * @param strOrder
     *            the order by of the query
     * @return a query
     */
    public static String buildQueryWithFilter( StringBuilder sbSQL, List<String> listFilter, String strOrder )
    {
        int nCount = 0;

        for ( String strFilter : listFilter )
        {
            if ( ++nCount == 1 )
            {
                sbSQL.append( CONSTANT_WHERE );
            }

            sbSQL.append( strFilter );

            if ( nCount != listFilter.size( ) )
            {
                sbSQL.append( CONSTANT_AND );
            }
        }

        if ( strOrder != null )
        {
            sbSQL.append( strOrder );
        }

        return sbSQL.toString( );
    }

    /**
     * replace special characters in the string passed as a parameter
     * 
     * @param strSource
     *            the string
     * @return substitute special in the string passed as a parameter
     */
    public static String substituteSpecialCaractersForExport( String strSource )
    {
        String strResult = EMPTY_STRING;

        if ( strSource != null )
        {
            strResult = strSource;
        }

        strResult = StringUtil.substitute( strResult, CONSTANT_CHARACTER_SIMPLE_QUOTE, CONSTANT_CHARACTER_DOUBLE_QUOTE );
        strResult = StringUtil.substitute( strResult, EMPTY_STRING, CONSTANTE_CHARACTER_RETURN );
        strResult = StringUtil.substitute( strResult, EMPTY_STRING, CONSTANTE_CHARACTERNEW_LINE );

        return strResult;
    }

    /**
     * Filter a list of field for a given user
     * 
     * @param listField
     *            a list of field
     * @param user
     *            an adminUser
     * @return a field list
     */
    public static List<Field> getAuthorizedFieldsByWorkgroup( List<Field> listField, AdminUser user )
    {
        List<Field> listFieldAuthorized = new ArrayList<Field>( );

        for ( Field field : listField )
        {
            // filter by workgroup
            if ( AdminWorkgroupService.isAuthorized( field, user ) )
            {
                listFieldAuthorized.add( field );
            }
        }

        return listFieldAuthorized;
    }

    /**
     * Filter a list of field for a given user
     * 
     * @param listField a list of field
     * @param request The http request
     * @return a field list
     */
    public static List<Field> getAuthorizedFieldsByRole( HttpServletRequest request, List<Field> listField )
    {
        List<Field> listFieldAuthorized = new ArrayList<Field>( );

        for ( Field field : listField )
        {
            // filter by workgroup
            if ( ( !SecurityService.isAuthenticationEnable( ) ) || ( field.getRoleKey( ) == null )
                    || field.getRoleKey( ).equals( Directory.ROLE_NONE )
                    || SecurityService.getInstance( ).isUserInRole( request, field.getRoleKey( ) ) )
            {
                listFieldAuthorized.add( field );
            }
        }

        return listFieldAuthorized;
    }

    /**
     * Removes from list all the elements that are not contained in the other
     * list Faster than classic "List.retainAll" because each id is unique
     * 
     * @param list1
     *            input list 1
     * @param list2
     *            input list 2
     * @return the result list
     */
    public static List<Integer> retainAll( List<Integer> list1, List<Integer> list2 )
    {
        List<Integer> lresult = null;

        if ( list1.size( ) < list2.size( ) )
        {
            Set<Integer> ts1 = new TreeSet<Integer>( list2 );

            Iterator<Integer> it = list1.iterator( );

            while ( it.hasNext( ) )
            {
                if ( !ts1.contains( it.next( ) ) )
                {
                    it.remove( );
                }
            }

            lresult = list1;
        }
        else
        {
            Set<Integer> ts1 = new TreeSet<Integer>( list1 );

            Iterator<Integer> it = list2.iterator( );

            while ( it.hasNext( ) )
            {
                if ( !ts1.contains( it.next( ) ) )
                {
                    it.remove( );
                }
            }

            lresult = list2;
        }

        return lresult;
    }

    /**
     * Like {@link List#retainAll(java.util.Collection)}, keeping first list
     * order. This method is based on the fact that list1 and list2 have unique
     * elements.
     * 
     * @param list1
     *            the first list
     * @param list2
     *            the other list
     * @return first list
     */
    public static List<Integer> retainAllIdsKeepingFirstOrder( List<Integer> list1, List<Integer> list2 )
    {
        Iterator<Integer> it = list1.iterator( );

        // makes contains quicker
        TreeSet<Integer> ts = new TreeSet<Integer>( list2 );

        while ( it.hasNext( ) )
        {
            if ( !ts.contains( it.next( ) ) )
            {
                it.remove( );
            }
        }

        return list1;
    }

    /**
     * Get the search record creation date from the request
     * @param request The request
     * @param dateTypeParameter The date type parameter
     * @param locale The locale
     * @return The date formation into the given locale, or null if no date was
     *         found
     */
    public static Date getSearchRecordDateCreationFromRequest( HttpServletRequest request, String dateTypeParameter,
            Locale locale )
    {
        String strDate = request.getParameter( dateTypeParameter );

        if ( strDate != null )
        {
            return DateUtil.formatDate( strDate, locale );
        }
        return null;
    }

    /**
     * Get the result list according to queries
     * 
     * @param request
     *            The {@link HttpServletRequest}
     * @param directory
     *            The {@link Directory}
     * @param bWorkflowServiceEnable
     *            true if the WorkflowService is enabled
     * @param bUseFilterDirectory True to use filter directory
     * @param searchFields The search field
     * @param adminUser The admin user
     * @param locale The locale
     * @return The list of id records
     */
    public static List<Integer> getListResults( HttpServletRequest request, Directory directory,
            boolean bWorkflowServiceEnable, boolean bUseFilterDirectory, IDirectorySearchFields searchFields,
            AdminUser adminUser, Locale locale )
    {
        return getListResults( request, directory, bWorkflowServiceEnable, bUseFilterDirectory, null,
                RecordFieldFilter.ORDER_NONE, searchFields, adminUser, locale );
    }

    /**
     * Get the result list according to queries
     * 
     * @param request
     *            The {@link HttpServletRequest}
     * @param directory
     *            The {@link Directory}
     * @param bWorkflowServiceEnable
     *            true if the WorkflowService is enabled
     * @param bUseFilterDirectory True to use filter directory
     * @param sortEntry The entry to sort results by
     * @param nSortOrder The sort order
     * @param searchFields The search field
     * @param adminUser The admin user
     * @param locale The locale
     * @return The list of id records
     */
    public static List<Integer> getListResults( HttpServletRequest request, Directory directory,
            boolean bWorkflowServiceEnable, boolean bUseFilterDirectory, IEntry sortEntry, int nSortOrder,
            IDirectorySearchFields searchFields, AdminUser adminUser, Locale locale )
    {
        // call search service
        RecordFieldFilter filter = new RecordFieldFilter( );
        filter.setIdDirectory( directory.getIdDirectory( ) );

        List<Integer> listResultRecordId = null;

        // filter by record state
        filter.setIsDisabled( searchFields.getIsDisabled( ) );

        // filter by role
        if ( searchFields instanceof DirectorySiteSearchFields
                && ( ( (DirectorySiteSearchFields) searchFields ).getRoleKeyList( ) != null ) )
        {
            filter.setRoleKeyList( ( (DirectorySiteSearchFields) searchFields ).getRoleKeyList( ),
                    ( (DirectorySiteSearchFields) searchFields ).isIncludeRoleNone( ),
                    ( (DirectorySiteSearchFields) searchFields ).isIncludeRoleNull( ) );
        }

        // filter by workgroup
        if ( adminUser != null )
        {
            filter.setWorkgroupKeyList( AdminWorkgroupService.getUserWorkgroups( adminUser, locale ) );
        }

        // sort filter
        if ( sortEntry == null )
        {
            filter.setSortEntry( searchFields.getSortEntry( ) );
        }
        else
        {
            filter.setSortEntry( sortEntry );
        }

        if ( nSortOrder == RecordFieldFilter.ORDER_NONE )
        {
            filter.setSortOrder( searchFields.getSortOrder( ) );
        }
        else
        {
            filter.setSortOrder( nSortOrder );
        }

        filter.setOrderByDateModification( searchFields.isSortByDateModification( ) );

        // If workflow active, filter by workflow state
        if ( ( directory.getIdWorkflow( ) != DirectoryUtils.CONSTANT_ID_NULL ) && bWorkflowServiceEnable )
        {
            if ( bUseFilterDirectory )
            {
                listResultRecordId = DirectorySearchService.getInstance( ).getSearchResults( directory,
                        searchFields.getMapQuery( ), searchFields.getDateCreationRecord( ),
                        searchFields.getDateCreationBeginRecord( ), searchFields.getDateCreationEndRecord( ),
                        searchFields.getDateModificationRecord( ), searchFields.getDateModificationBeginRecord( ),
                        searchFields.getDateModificationEndRecord( ), filter, getPlugin( ) );
            }
            else
            {
                listResultRecordId = DirectorySearchService.getInstance( ).getSearchResults( directory, null, null,
                        null, null, filter, getPlugin( ) );
            }

            List<Integer> listTmpResultRecordId = WorkflowService.getInstance( ).getAuthorizedResourceList(
                    Record.WORKFLOW_RESOURCE_TYPE, directory.getIdWorkflow( ),
                    ( (DirectoryAdminSearchFields) searchFields ).get_nIdWorkflowSate( ),
                    Integer.valueOf( directory.getIdDirectory( ) ), adminUser );

            listResultRecordId = DirectoryUtils.retainAllIdsKeepingFirstOrder( listResultRecordId,
                    listTmpResultRecordId );
        }
        else
        {
            if ( bUseFilterDirectory )
            {
                listResultRecordId = DirectorySearchService.getInstance( ).getSearchResults( directory,
                        searchFields.getMapQuery( ), searchFields.getDateCreationRecord( ),
                        searchFields.getDateCreationBeginRecord( ), searchFields.getDateCreationEndRecord( ),
                        searchFields.getDateModificationRecord( ), searchFields.getDateModificationBeginRecord( ),
                        searchFields.getDateModificationEndRecord( ), filter, getPlugin( ) );
            }
            else
            {
                listResultRecordId = DirectorySearchService.getInstance( ).getSearchResults( directory, null, null,
                        null, null, filter, getPlugin( ) );
            }
        }

        return listResultRecordId;
    }

    /**
     * Gets the plugin
     * 
     * @return the plugin
     */
    public static Plugin getPlugin( )
    {
        return PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
    }

    /**
     * return url of the jsp manage directory record
     * 
     * @param request
     *            The HTTP request
     * @param nIdDirectory
     *            the directory id
     * @return url of the jsp manage directory record
     */
    public static String getJspManageDirectoryRecord( HttpServletRequest request, int nIdDirectory )
    {
        UrlItem urlItem = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_MANAGE_DIRECTORY_RECORD );
        urlItem.addParameter( PARAMETER_ID_DIRECTORY, nIdDirectory );
        urlItem.addParameter( PARAMETER_SESSION, PARAMETER_SESSION );

        String strSortedAttributeName = request.getParameter( Parameters.SORTED_ATTRIBUTE_NAME );
        String strAscSort = null;
        Directory directory = DirectoryHome.findByPrimaryKey( nIdDirectory, getPlugin( ) );

        if ( ( directory != null ) && ( ( strSortedAttributeName != null ) || ( directory.getIdSortEntry( ) != null ) ) )
        {
            if ( strSortedAttributeName == null )
            {
                strSortedAttributeName = directory.getIdSortEntry( );
            }

            strAscSort = request.getParameter( Parameters.SORTED_ASC );

            urlItem.addParameter( Parameters.SORTED_ATTRIBUTE_NAME, strSortedAttributeName );
            urlItem.addParameter( Parameters.SORTED_ASC, strAscSort );
        }

        return urlItem.getUrl( );
    }

    /**
     * Convert a map of ( String, String ) into a {@link ReferenceList}
     * 
     * @param map
     *            the map to convert
     * @return a {@link ReferenceList}
     */
    public static ReferenceList convertMapToReferenceList( Map<String, String> map )
    {
        ReferenceList ref = new ReferenceList( );

        if ( map != null )
        {
            for ( Entry<String, String> userInfo : map.entrySet( ) )
            {
                ref.addItem( userInfo.getKey( ), userInfo.getValue( ) );
            }
        }

        return ref;
    }

    /**
     * Get the file name of a file from the url
     * 
     * @param strUrl
     *            the url of the file
     * @return the file name
     */
    public static String getFileName( String strUrl )
    {
        String strFileName = StringUtils.EMPTY;
        DirectoryAsynchronousUploadHandler handler = DirectoryAsynchronousUploadHandler.getHandler( );

        try
        {
            strFileName = handler.getFileName( strUrl );
        }
        catch ( BlobStoreClientException e )
        {
            AppLogService.error( e );
        }

        return strFileName;
    }

    /**
     * Do download a file
     * 
     * @param strUrl
     *            the url of the file to download
     * @param strFilePath
     *            the file path to download the file
     */
    public static void doDownloadFile( String strUrl, String strFilePath )
    {
        DirectoryAsynchronousUploadHandler handler = DirectoryAsynchronousUploadHandler.getHandler( );

        try
        {
            handler.doDownloadFile( strUrl, strFilePath );
        }
        catch ( BlobStoreClientException e )
        {
            AppLogService.error( e );
        }
    }

    /**
     * Do download a file
     * 
     * @param strUrl
     *            the url of the file to download
     * @return a {@link FileItem}
     */
    public static File doDownloadFile( String strUrl )
    {
        FileItem fileItem = null;
        File file = null;
        DirectoryAsynchronousUploadHandler handler = DirectoryAsynchronousUploadHandler.getHandler( );

        try
        {
            fileItem = handler.doDownloadFile( strUrl );
        }
        catch ( BlobStoreClientException e )
        {
            AppLogService.error( e );
        }

        if ( fileItem != null )
        {
            if ( fileItem.getSize( ) < Integer.MAX_VALUE )
            {
                PhysicalFile physicalFile = new PhysicalFile( );
                physicalFile.setValue( fileItem.get( ) );

                String strFileName = fileItem.getName( );

                if ( StringUtils.isNotBlank( strFileName ) )
                {
                    String strExtension = StringUtils.EMPTY;
                    int nLastIndexOfDot = strFileName.lastIndexOf( CONSTANT_DOT );

                    if ( nLastIndexOfDot != DirectoryUtils.CONSTANT_ID_NULL )
                    {
                        strExtension = strFileName.substring( nLastIndexOfDot + 1 );
                    }

                    file = new File( );
                    file.setPhysicalFile( physicalFile );
                    file.setSize( (int) fileItem.getSize( ) );
                    file.setTitle( strFileName );

                    if ( StringUtils.isNotBlank( fileItem.getContentType( ) ) )
                    {
                        file.setMimeType( fileItem.getContentType( ) );
                    }
                    else
                    {
                        file.setMimeType( FileSystemUtil.getMIMEType( strFileName ) );
                    }

                    file.setExtension( strExtension );
                }
            }
            else
            {
                AppLogService
                        .error( "DirectoryUtils : File too big ! fr.paris.lutece.plugins.directory.business.File.setSize "
                                + "must have Integer parameter, in other words a size lower than '"
                                + Integer.MAX_VALUE
                                + "'" );
            }
        }

        return file;
    }

    /**
     * Build the map id entry - list record field
     * 
     * @param map
     *            the map
     * @param entry
     *            the entry
     * @param filter
     *            the filter
     * @param plugin
     *            the plugin
     * @param bGetFileName
     *            true if it must get the file name, false otherwise <br />
     *            Warning : The file name is fetch by a webservice call. Beware
     *            of performance.
     */
    private static void buildMapIdEntryListRecordField( Map<String, List<RecordField>> map, IEntry entry,
            RecordFieldFilter filter, Plugin plugin, boolean bGetFileName )
    {
        filter.setIdEntry( entry.getIdEntry( ) );

        List<RecordField> listRecordFields = RecordFieldHome.getRecordFieldList( filter, plugin );

        // If entry is type download url, then fetch the file name
        if ( entry instanceof EntryTypeDownloadUrl && bGetFileName )
        {
            if ( ( listRecordFields != null ) && !listRecordFields.isEmpty( ) )
            {
                for ( RecordField recordField : listRecordFields )
                {
                    if ( ( recordField != null ) && StringUtils.isNotBlank( recordField.getValue( ) ) )
                    {
                        recordField.setFileName( getFileName( recordField.getValue( ) ) );
                    }
                }
            }
        }

        map.put( Integer.toString( entry.getIdEntry( ) ), listRecordFields );
    }

    /**
     * Get the base url
     * 
     * @param request
     *            the HTTP request
     * @return the base url
     */
    public static String getBaseUrl( HttpServletRequest request )
    {
        String strBaseUrl = StringUtils.EMPTY;

        if ( request != null )
        {
            strBaseUrl = AppPathService.getBaseUrl( request );
        }
        else
        {
            strBaseUrl = AppPropertiesService.getProperty( PROPERTY_LUTECE_BASE_URL );

            if ( StringUtils.isBlank( strBaseUrl ) )
            {
                strBaseUrl = AppPropertiesService.getProperty( PROPERTY_LUTECE_PROD_URL );
            }
        }

        return strBaseUrl;
    }

    /**
     * Depopulate the directory into a map of key - value
     * @param directory the directory
     * @return a map of key - value
     */
    public static Map<String, Object> depopulate( Directory directory )
    {
        Map<String, Object> mapAttributes = new HashMap<String, Object>( );

        for ( java.lang.reflect.Field field : Directory.class.getDeclaredFields( ) )
        {
            DirectoryAttribute attribute = field.getAnnotation( DirectoryAttribute.class );

            if ( attribute != null )
            {
                String strAttributeKey = attribute.value( );

                try
                {
                    field.setAccessible( true );

                    Object attributeValue = ReflectionUtils.getField( field, directory );
                    mapAttributes.put( strAttributeKey, attributeValue );
                }
                catch ( SecurityException e )
                {
                    AppLogService.error( e );
                }
            }
        }

        return mapAttributes;
    }
}

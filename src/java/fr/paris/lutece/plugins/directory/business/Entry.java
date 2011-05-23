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
package fr.paris.lutece.plugins.directory.business;

import fr.paris.lutece.plugins.directory.service.directorysearch.DirectorySearchItem;
import fr.paris.lutece.plugins.directory.utils.DirectoryErrorException;
import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.web.util.LocalizedPaginator;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.xml.XmlUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 *
 * class Entry
 *
 */
public class Entry implements IEntry
{
    public static final String TAG_ENTRY = "entry";
    public static final String ATTRIBUTE_ENTRY_ID = "id";
    public static final String TAG_TITLE = "title";
    public static final String ATTRIBUTE_TITLE = "title";
    public static final String ATTRIBUTE_ENTRY_ID_TYPE = "id-type";
    public static final String TAG_LIST_ENTRY = "list-entry";

    //	parameters Entry 
    protected static final String PARAMETER_TITLE = "title";
    protected static final String PARAMETER_HELP_MESSAGE = "help_message";
    protected static final String PARAMETER_HELP_MESSAGE_SEARCH = "help_message_search";
    protected static final String PARAMETER_COMMENT = "comment";
    protected static final String PARAMETER_MANDATORY = "mandatory";
    protected static final String PARAMETER_INDEXED = "indexed";
    protected static final String PARAMETER_INDEXED_AS_TITLE = "indexed_as_title";
    protected static final String PARAMETER_INDEXED_AS_SUMMARY = "indexed_as_summary";
    protected static final String PARAMETER_MULTIPLE_SEARCH_FIELDS = "multiple_search_fields";
    protected static final String PARAMETER_SHOWN_IN_ADVANCED_SEARCH = "shown_in_advanced_search";
    protected static final String PARAMETER_SHOWN_IN_RESULT_LIST = "shown_in_result_list";
    protected static final String PARAMETER_SHOWN_IN_RESULT_RECORD = "shown_in_result_record";
    protected static final String PARAMETER_SHOWN_IN_HISTORY = "shown_in_history";
    protected static final String PARAMETER_AUTOCOMPLETE = "autocomplete_entry";
    protected static final String PARAMETER_FIELD_IN_LINE = "field_in_line";
    protected static final String PARAMETER_HEIGHT = "height";
    protected static final String PARAMETER_WIDTH = "width";
    protected static final String PARAMETER_DISPLAY_HEIGHT = "display_height";
    protected static final String PARAMETER_DISPLAY_WIDTH = "display_width";
    protected static final String PARAMETER_VALUE = "value";
    protected static final String PARAMETER_MAX_SIZE_ENTER = "max_size_enter";
    protected static final String PARAMETER_ID_DIRECTORY_RECORD = "id_directory_record";
    protected static final String PARAMETER_UPDATE_ENTRY = "update_entry";
    protected static final String PARAMETER_WORKGROUP_ASSOCIATED = "workgroup_associated";
    protected static final String PARAMETER_ROLE_ASSOCIATED = "role_associated";
    protected static final String PARAMETER_SEARCH_ACTION = "search_action";
    protected static final String PARAMETER_DATE_BEGIN = "date_begin";
    protected static final String PARAMETER_DATE_END = "date_end";
    protected static final String PARAMETER_DIRECTORY_ASSOCIATE = "id_directory_associate";
    protected static final String PARAMETER_ENTRY_ASSOCIATE = "id_entry_associate";
    protected static final String PARAMETER_REQUEST_SQL = "request_sql";
    protected static final String PARAMETER_IS_ADD_VALUE_SEARCH_ALL = "is_all_search";
    protected static final String PARAMETER_LABEL_VALUE_SEARCH_ALL = "label_all_search";
    protected static final String PARAMETER_SHOWN_IN_EXPORT = "shown_in_export";
    protected static final String PARAMETER_SHOW_ALL_INFO = "show_all_info";

    //	message
    protected static final String MESSAGE_MANDATORY_FIELD = "directory.message.mandatory.field";
    protected static final String MESSAGE_NUMERIC_FIELD = "directory.message.numeric.field";
    protected static final String FIELD_TITLE = "directory.create_entry.label_title";
    protected static final String FIELD_INSERT_GROUP = "directory.modify_directory.manage_entry.label_insert_group";
    protected static final String FIELD_HELP_MESSAGE = "directory.create_entry.label_help_message";
    protected static final String FIELD_HELP_MESSAGE_SEARCH = "directory.create_entry.label_help_message_search";
    protected static final String FIELD_COMMENT = "directory.create_entry.label_comment";
    protected static final String FIELD_VALUE = "directory.create_entry.label_value";
    protected static final String FIELD_PRESENTATION = "directory.create_entry.label_presentation";
    protected static final String FIELD_MANDATORY = "directory.create_entry.label_mandatory";
    protected static final String FIELD_WIDTH = "directory.create_entry.label_width";
    protected static final String FIELD_HEIGHT = "directory.create_entry.label_height";
    protected static final String FIELD_WIDTH_DISPLAY = "directory.create_entry.label_width_display";
    protected static final String FIELD_HEIGHT_DISPLAY = "directory.create_entry.label_height_display";
    protected static final String FIELD_MAX_SIZE_ENTER = "directory.create_entry.label_max_size_enter";
    protected static final String FIELD_ENTRY_ASSOCIATE = "directory.create_entry.label_entry";
    protected static final String FIELD_REQUEST_SQL = "directory.create_entry.label_request_sql";
    protected static final String FIELD_LABEL_ALL_SEARCH = "directory.create_entry.label_label_all_search";

    //  Jsp Definition
    protected static final String JSP_DOWNLOAD_FILE = "jsp/admin/plugins/directory/DoDownloadFile.jsp";
    
    // SQL
    private static final String SQL_JOIN_DIRECTORY_RECORD_FIELD = " LEFT JOIN directory_record_field drf ON drf.id_record = dr.id_record AND drf.id_entry = ? ";
    private static final String SQL_ORDER_BY_RECORD_FIELD_VALUE = " ORDER BY drf.record_field_value ";

    //MARK
    protected static final String MARK_ENTRY = "entry";
    protected static final String MARK_LOCALE = "locale";
    protected static final String MARK_RECORD_FIELD = "record_field";
    protected static final String MARK_DEFAULT_VALUES = "default_values";
    protected static final String MARK_SHOW_ALL_INFO = "show_all_info";
    protected static final String MARK_MYLUTECE_USER_INFOS_LIST = "mylutece_user_infos_list";
    protected static final String MARK_MYLUTECE_USER_LOGIN = "mylutece_user_login";

    //PROPERTIES
    protected static final String PROPERTY_IMPORT_MULTIPLE_VALUE_DELIMITER = "directory.import.multiple_value.delimiter";
    private int _nIdEntry;
    private Directory _directory;
    private String _strTitle;
    private String _strHelpMessage;
    private String _strHelpMessageSearch;
    private String _strComment;
    private int _nDisplayHeight;
    private int _nDisplayWidth;
    private boolean _bMandatory;
    private boolean _bFieldInLine;
    private boolean _bShownInAdvancedSearch;
    private boolean _bShownInResultList;
    private boolean _bShownInResultRecord;
    private boolean _bShownInHistory;
    private boolean _bShownInExport;
    private boolean _bWorkgroupAssociated;
    private boolean _bRoleAssociated;
    private boolean _bIndexed;
    private boolean _bIndexedAsTitle;
    private boolean _bIndexedAsSummary;
    private int _nPosition;
    private boolean _bMultipleSearchFields;
    private EntryType _entryType;
    private List<Field> _listFields;
    private boolean _nFirstInTheList;
    private boolean _nLastInTheList;
    private IEntry _entryParent;
    private List<IEntry> _listEntryChildren;
    private int _nIdEntryAssociate; // For entry type directory
    private String _strRequestSQL; // For entry type SQL
    private boolean _bAddValueAllSearch; // For entries type directory and select
    private String _strLabelValueAllSearch; // For entries type directory and select
    private IMapProvider _mapProvider; // For entries type Geolocation
    private boolean _bIsAutocompleEntry; // For autocomplete entries

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#getIdEntry()
     */
    public int getIdEntry(  )
    {
        return _nIdEntry;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#setIdEntry(int)
     */
    public void setIdEntry( int idEntry )
    {
        _nIdEntry = idEntry;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#getDirectory()
     */
    public Directory getDirectory(  )
    {
        return _directory;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#setDirectory(fr.paris.lutece.plugins.directory.business.Directory)
     */
    public void setDirectory( Directory directory )
    {
        this._directory = directory;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#getTitle()
     */
    public String getTitle(  )
    {
        return _strTitle;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#setTitle(java.lang.String)
     */
    public void setTitle( String title )
    {
        _strTitle = title;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#getHelpMessage()
     */
    public String getHelpMessage(  )
    {
        return _strHelpMessage;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#setHelpMessage(java.lang.String)
     */
    public void setHelpMessage( String helpMessage )
    {
        _strHelpMessage = helpMessage;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#getHelpMessageSearch()
     */
    public String getHelpMessageSearch(  )
    {
        return _strHelpMessageSearch;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#setHelpMessageSearch(java.lang.String)
     */
    public void setHelpMessageSearch( String helpMessage )
    {
        _strHelpMessageSearch = helpMessage;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#getComment()
     */
    public String getComment(  )
    {
        return _strComment;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#setComment(java.lang.String)
     */
    public void setComment( String comment )
    {
        _strComment = comment;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#isMandatory()
     */
    public boolean isMandatory(  )
    {
        return _bMandatory;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#setMandatory(boolean)
     */
    public void setMandatory( boolean mandatory )
    {
        _bMandatory = mandatory;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#isFieldInLine()
     */
    public boolean isFieldInLine(  )
    {
        return _bFieldInLine;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#setFieldInLine(boolean)
     */
    public void setFieldInLine( boolean fieldInLine )
    {
        _bFieldInLine = fieldInLine;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#isShownInFormMainSearch()
     */
    public boolean isShownInAdvancedSearch(  )
    {
        return _bShownInAdvancedSearch;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#setShownInFormMainSearch(boolean)
     */
    public void setShownInAdvancedSearch( boolean shown )
    {
        _bShownInAdvancedSearch = shown;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#isShownInResultList()
     */
    public boolean isShownInResultList(  )
    {
        return _bShownInResultList;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#setShownInResultList(boolean)
     */
    public void setShownInResultList( boolean shown )
    {
        _bShownInResultList = shown;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#isShownInResultRecord()
     */
    public boolean isShownInResultRecord(  )
    {
        return _bShownInResultRecord;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#setShownInResultRecord(boolean)
     */
    public void setShownInResultRecord( boolean shown )
    {
        _bShownInResultRecord = shown;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#isShownInResultRecord()
     */
    public boolean isShownInHistory(  )
    {
        return _bShownInHistory;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#setShownInExport(boolean)
     */
    public void setShownInHistory( boolean shown )
    {
        _bShownInHistory = shown;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#isShownInExport()
     */
    public boolean isShownInExport(  )
    {
        return _bShownInExport;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#setShownInResultRecord(boolean)
     */
    public void setShownInExport( boolean shown )
    {
        _bShownInExport = shown;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#isIndexed()
     */
    public boolean isIndexed(  )
    {
        return _bIndexed;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#setIndexed(boolean)
     */
    public void setIndexed( boolean indexed )
    {
        _bIndexed = indexed;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#getPosition()
     */
    public int getPosition(  )
    {
        return _nPosition;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#setPosition(int)
     */
    public void setPosition( int position )
    {
        _nPosition = position;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#getEntryType()
     */
    public EntryType getEntryType(  )
    {
        return _entryType;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#setEntryType(fr.paris.lutece.plugins.directory.business.EntryType)
     */
    public void setEntryType( EntryType entryType )
    {
        _entryType = entryType;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#getFields()
     */
    public List<Field> getFields(  )
    {
        return _listFields;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#setFields(java.util.List)
     */
    public void setFields( List<Field> fields )
    {
        _listFields = fields;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#getParent()
     */
    public IEntry getParent(  )
    {
        return _entryParent;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#setParent(fr.paris.lutece.plugins.directory.business.IEntry)
     */
    public void setParent( IEntry parent )
    {
        _entryParent = parent;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#getChildren()
     */
    public List<IEntry> getChildren(  )
    {
        return _listEntryChildren;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#setChildren(java.util.List)
     */
    public void setChildren( List<IEntry> children )
    {
        _listEntryChildren = children;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#isLastInTheList()
     */
    public boolean isLastInTheList(  )
    {
        return _nLastInTheList;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#setLastInTheList(boolean)
     */
    public void setLastInTheList( boolean lastInTheList )
    {
        _nLastInTheList = lastInTheList;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#isFirstInTheList()
     */
    public boolean isFirstInTheList(  )
    {
        return _nFirstInTheList;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#setFirstInTheList(boolean)
     */
    public void setFirstInTheList( boolean firstInTheList )
    {
        _nFirstInTheList = firstInTheList;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#getDisplayWidth()
     */
    public int getDisplayWidth(  )
    {
        return _nDisplayWidth;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#setDisplayWidth(int)
     */
    public void setDisplayWidth( int width )
    {
        _nDisplayWidth = width;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#getDisplayHeight()
     */
    public int getDisplayHeight(  )
    {
        return _nDisplayHeight;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#setDisplayHeight(int)
     */
    public void setDisplayHeight( int height )
    {
        _nDisplayHeight = height;
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#isRoleAssociated()
     */
    public boolean isRoleAssociated(  )
    {
        return _bRoleAssociated;
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#setRoleAssociated(boolean)
     */
    public void setRoleAssociated( boolean bRoleAssociated )
    {
        _bRoleAssociated = bRoleAssociated;
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#isWorkgroupAssociated()
     */
    public boolean isWorkgroupAssociated(  )
    {
        return _bWorkgroupAssociated;
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#setWorkgroupAssociated(boolean)
     */
    public void setWorkgroupAssociated( boolean bWorkGroupAssociated )
    {
        _bWorkgroupAssociated = bWorkGroupAssociated;
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#isMultipleSearchFields()
     */
    public boolean isMultipleSearchFields(  )
    {
        return _bMultipleSearchFields;
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#setMultipleSearchFields(boolean)
     */
    public void setMultipleSearchFields( boolean multipleSearchFields )
    {
        _bMultipleSearchFields = multipleSearchFields;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#getEntryAssociate()
     */
    public int getEntryAssociate(  )
    {
        return _nIdEntryAssociate;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#setEntryAssociate(int)
     */
    public void setEntryAssociate( int idEntryAssociate )
    {
        _nIdEntryAssociate = idEntryAssociate;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#getComment()
     */
    public String getRequestSQL(  )
    {
        return _strRequestSQL;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#setComment(java.lang.String)
     */
    public void setRequestSQL( String strRequestSQL )
    {
        _strRequestSQL = strRequestSQL;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#getComment()
     */
    public boolean isAddValueAllSearch(  )
    {
        return _bAddValueAllSearch;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#setComment(java.lang.String)
     */
    public void setAddValueAllSearch( boolean bAddValueAllSearch )
    {
        _bAddValueAllSearch = bAddValueAllSearch;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#isAutocompleteEntry()
     */
    public boolean isAutocompleteEntry(  )
    {
        return _bIsAutocompleEntry;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#setAutocompleteEntry(boolean)
     */
    public void setAutocompleteEntry( boolean bIsAutocompleEntry )
    {
        _bIsAutocompleEntry = bIsAutocompleEntry;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#getComment()
     */
    public String getLabelValueAllSearch(  )
    {
        return _strLabelValueAllSearch;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#setComment(java.lang.String)
     */
    public void setLabelValueAllSearch( String strLabelValueAllSearch )
    {
        _strLabelValueAllSearch = strLabelValueAllSearch;
    }

    /**
     * Get the url of the template wich contains the Html code used in entry form
         * @param true if display for front office or false if display for back office
     * @return the template url
     *
     * */
    public String getTemplateHtmlFormEntry( boolean isDisplayFront )
    {
        return null;
    }

    /**
     * Get the url of the template wich contains the Html code used in search form
         * @param true if display for front office or false if display for back office
     * @return the template url
     *
     * */
    public String getTemplateHtmlFormSearchEntry( boolean isDisplayFront )
    {
        return null;
    }

    /**
     * Get the url of the template wich contains the Html code used in display records
         * @param true if display for front office or false if display for back office
     * @return the template url
     *
     * */
    public String getTemplateHtmlRecordFieldValue( boolean isDisplayFront )
    {
        return null;
    }

    /* (non-Javadoc)
    * @see fr.paris.lutece.plugins.directory.business.IEntry#getEntryData(javax.servlet.http.HttpServletRequest, java.util.Locale)
    */
    public String getEntryData( HttpServletRequest request, Locale locale )
    {
        return null;
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#getRecordFieldData(fr.paris.lutece.plugins.directory.business.Record, javax.servlet.http.HttpServletRequest, boolean, java.util.List, java.util.Locale)
     */
    public void getRecordFieldData( Record record, HttpServletRequest request, boolean bTestDirectoryError,
        boolean bAddNewValue, List<RecordField> listRecordField, Locale locale )
        throws DirectoryErrorException
    {
        List<String> lstValue = new ArrayList<String>(  );
        String strValueEntry = ( request.getParameter( DirectoryUtils.EMPTY_STRING + this.getIdEntry(  ) ) != null )
            ? request.getParameter( DirectoryUtils.EMPTY_STRING + this.getIdEntry(  ) ).trim(  ) : null;
        lstValue.add( strValueEntry );
        getRecordFieldData( record, lstValue, bTestDirectoryError, bAddNewValue, listRecordField, locale );
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#getImportRecordFieldData(fr.paris.lutece.plugins.directory.business.Record, java.lang.String, boolean, java.util.List, java.util.Locale)
     */
    public void getImportRecordFieldData( Record record, String strImportValue, boolean bTestDirectoryError,
        List<RecordField> listRecordField, Locale locale )
        throws DirectoryErrorException
    {
        List<String> lstValue = new ArrayList<String>(  );
        lstValue.add( strImportValue );
        getRecordFieldData( record, lstValue, bTestDirectoryError, false, listRecordField, locale );
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#getRecordFieldData(fr.paris.lutece.plugins.directory.business.Record, java.util.List, boolean, java.util.List, java.util.Locale)
     */
    public void getRecordFieldData( Record record, List<String> lstValue, boolean bTestDirectoryError,
        boolean bAddNewValue, List<RecordField> listRecordField, Locale locale )
        throws DirectoryErrorException
    {
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#getTemplateCreate()
     */
    public String getTemplateCreate(  )
    {
        return null;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#getTemplateModify()
     */
    public String getTemplateModify(  )
    {
        return null;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#getPaginator(int, java.lang.String, java.lang.String, java.lang.String)
     */
    public Paginator getPaginator( int nItemPerPage, String strBaseUrl, String strPageIndexParameterName,
        String strPageIndex )
    {
        return null;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#getReferenceListRegularExpression(fr.paris.lutece.plugins.directory.business.IEntry, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public ReferenceList getReferenceListRegularExpression( IEntry entry, Plugin plugin )
    {
        return null;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#getHtmlFormEntry(java.util.Locale)
     */
    public String getHtmlFormEntry( Locale locale, boolean isDisplayFront )
    {
        if ( getTemplateHtmlFormEntry( isDisplayFront ) != null )
        {
            Map<String, Object> model = new HashMap<String, Object>(  );
            model.put( MARK_ENTRY, this );
            model.put( MARK_LOCALE, locale );

            HtmlTemplate template = AppTemplateService.getTemplate( getTemplateHtmlFormEntry( isDisplayFront ), locale,
                    model );

            return template.getHtml(  );
        }

        return null;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#getHtmlFormEntry(java.util.Locale, java.util.List)
     */
    public String getHtmlFormEntry( Locale locale, List<RecordField> defaultValues, boolean isDisplayFront )
    {
        if ( getTemplateHtmlFormEntry( isDisplayFront ) != null )
        {
            Map<String, Object> model = new HashMap<String, Object>(  );
            model.put( MARK_ENTRY, this );
            model.put( MARK_LOCALE, locale );
            model.put( MARK_DEFAULT_VALUES, defaultValues );

            HtmlTemplate template = AppTemplateService.getTemplate( getTemplateHtmlFormEntry( isDisplayFront ), locale,
                    model );

            return template.getHtml(  );
        }

        return null;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#getHtmlFormSearchEntry(java.util.Locale)
     */
    public String getHtmlFormSearchEntry( Locale locale, boolean isDisplayFront )
    {
        if ( getTemplateHtmlFormSearchEntry( isDisplayFront ) != null )
        {
            Map<String, Object> model = new HashMap<String, Object>(  );
            model.put( MARK_ENTRY, this );
            model.put( MARK_LOCALE, locale );

            HtmlTemplate template = AppTemplateService.getTemplate( getTemplateHtmlFormSearchEntry( isDisplayFront ),
                    locale, model );

            return template.getHtml(  );
        }

        return null;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#getHtmlFormSearchEntry(java.util.Locale, java.util.List)
     */
    public String getHtmlFormSearchEntry( Locale locale, List<RecordField> defaultValues, boolean isDisplayFront )
    {
        if ( getTemplateHtmlFormSearchEntry( isDisplayFront ) != null )
        {
            Map<String, Object> model = new HashMap<String, Object>(  );
            model.put( MARK_ENTRY, this );
            model.put( MARK_LOCALE, locale );
            model.put( MARK_DEFAULT_VALUES, defaultValues );

            HtmlTemplate template = AppTemplateService.getTemplate( getTemplateHtmlFormSearchEntry( isDisplayFront ),
                    locale, model );

            return template.getHtml(  );
        }

        return null;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#getHtmlRecordFieldValue(java.util.Locale, fr.paris.lutece.plugins.directory.business.RecordField)
     */
    public String getHtmlRecordFieldValue( Locale locale, RecordField recordField, boolean isDisplayFront )
    {
        if ( getTemplateHtmlRecordFieldValue( isDisplayFront ) != null )
        {
            Map<String, Object> model = new HashMap<String, Object>(  );
            model.put( MARK_ENTRY, this );
            model.put( MARK_RECORD_FIELD, recordField );
            model.put( MARK_LOCALE, locale );

            HtmlTemplate template = AppTemplateService.getTemplate( getTemplateHtmlRecordFieldValue( isDisplayFront ),
                    locale, model );

            return template.getHtml(  );
        }

        return null;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#convertRecordFieldValueToString(fr.paris.lutece.plugins.directory.business.RecordField, java.util.Locale)
     */
    public String convertRecordFieldValueToString( RecordField recordField, Locale locale, boolean bDisplayFront,
        boolean bExportDirectory )
    {
        if ( recordField.getValue(  ) != null )
        {
            if ( recordField.getField(  ) != null )
            {
                if ( ( recordField.getField(  ).getValue(  ) != null ) &&
                        !recordField.getField(  ).getValue(  ).equals( DirectoryUtils.EMPTY_STRING ) )
                {
                    return recordField.getField(  ).getValue(  );
                }

                return recordField.getValue(  );
            }

            return recordField.getValue(  );
        }

        return DirectoryUtils.EMPTY_STRING;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#convertRecordFieldTitleToString(fr.paris.lutece.plugins.directory.business.RecordField, java.util.Locale)
     */
    public String convertRecordFieldTitleToString( RecordField recordField, Locale locale, boolean bDisplayFront )
    {
        if ( recordField.getValue(  ) != null )
        {
            if ( recordField.getField(  ) != null )
            {
                if ( ( recordField.getField(  ).getTitle(  ) != null ) &&
                        !recordField.getField(  ).getTitle(  ).equals( DirectoryUtils.EMPTY_STRING ) )
                {
                    return recordField.getField(  ).getTitle(  );
                }

                return recordField.getValue(  );
            }

            return recordField.getValue(  );
        }

        return DirectoryUtils.EMPTY_STRING;
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#addSearchCriteria(java.util.HashMap, fr.paris.lutece.plugins.directory.business.RecordField)
     */
    public void addSearchCriteria( HashMap<String, Object> mapSearchItem, RecordField recordField )
    {
        String strValue;

        if ( recordField.getValue(  ) != null )
        {
            strValue = new String( recordField.getValue(  ) );

            if ( !strValue.trim(  ).equals( DirectoryUtils.EMPTY_STRING ) )
            {
                mapSearchItem.put( DirectorySearchItem.FIELD_CONTENTS, strValue );
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#getXml(fr.paris.lutece.portal.service.plugin.Plugin, java.util.Locale, java.lang.StringBuffer)
     */
    public void getXml( Plugin plugin, Locale locale, StringBuffer strXml )
    {
        if ( !this.getEntryType(  ).getComment(  ) )
        {
            HashMap<String, String> model = new HashMap<String, String>(  );
            model.put( ATTRIBUTE_ENTRY_ID, String.valueOf( this.getIdEntry(  ) ) );
            model.put( ATTRIBUTE_ENTRY_ID_TYPE, String.valueOf( this.getEntryType(  ).getIdType(  ) ) );
            XmlUtil.beginElement( strXml, TAG_ENTRY, model );
            XmlUtil.addElementHtml( strXml, TAG_TITLE, this.getTitle(  ) );

            if ( this.getEntryType(  ).getGroup(  ) && ( this.getChildren(  ) != null ) )
            {
                XmlUtil.beginElement( strXml, TAG_LIST_ENTRY, model );

                for ( IEntry entry : this.getChildren(  ) )
                {
                    entry.getXml( plugin, locale, strXml );
                }

                XmlUtil.endElement( strXml, TAG_LIST_ENTRY );
            }

            XmlUtil.endElement( strXml, TAG_ENTRY );
        }
    }

    public void getImportRecordFieldData( Record record, byte[] decodedBytes, String nomFile, boolean b,
        List<RecordField> listRecordField, Locale locale )
        throws DirectoryErrorException
    {
        // TODO Auto-generated method stub
    }

    /**
     *
     * {@inheritDoc}
     */
    public void setMapProvider( IMapProvider mapProvider )
    {
        _mapProvider = mapProvider;
    }

    /**
     *
     * {@inheritDoc}
     */
    public IMapProvider getMapProvider(  )
    {
        return _mapProvider;
    }

    /**
     * Check if the entry is sortable
     * @return true if it is sortable, false otherwise
     */
    public boolean isSortable(  )
    {
        return false;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IEntry#getPaginator(int, java.lang.String, java.lang.String, java.lang.String, java.util.Locale)
     */
    public LocalizedPaginator getPaginator( int nItemPerPage, String strBaseUrl, String strPageIndexParameterName,
        String strPageIndex, Locale locale )
    {
        return null;
    }

    /**
     * 
     * Provide default join on directory_record_field
     */
	public String getSQLJoin()
	{
		return SQL_JOIN_DIRECTORY_RECORD_FIELD;
	}

	/**
     * 
     * Provides default order by on directory_record_field.record_field_value
     */
	public String getSQLOrderBy()
	{
		return SQL_ORDER_BY_RECORD_FIELD_VALUE;
	}
	
	/**
	 * 
	 * Returns the entry id
	 */
	public List<Object> getSQLParametersValues()
	{
		return Collections.<Object>singletonList( Integer.valueOf( getIdEntry() ) );
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isIndexedAsTitle(  )
	{
		return _bIndexedAsTitle;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void setIndexedAsTitle(  boolean indexedAsTitle  )
	{
		_bIndexedAsTitle = indexedAsTitle;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isIndexedAsSummary(  )
	{
		return _bIndexedAsSummary;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setIndexedAsSummary( boolean indexedAsSummary )
	{
		_bIndexedAsSummary = indexedAsSummary;
		
	}

}

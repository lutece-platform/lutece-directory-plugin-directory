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
package fr.paris.lutece.plugins.directory.business;

import fr.paris.lutece.plugins.directory.service.DirectoryService;
import fr.paris.lutece.plugins.directory.service.FileImgService;
import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.resource.IExtendableResource;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupResource;
import fr.paris.lutece.util.date.DateUtil;
import fr.paris.lutece.util.xml.XmlUtil;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;


/**
 * 
 * class Record
 * 
 */
public class Record implements AdminWorkgroupResource, IExtendableResource
{
    public static final String WORKFLOW_RESOURCE_TYPE = "DIRECTORY_RECORD";
    public static final String EXTENDABLE_RESOURCE_TYPE = WORKFLOW_RESOURCE_TYPE;
    public static final String ATTRIBUTE_TITLE = "title";
    public static final String TAG_LIST_RECORD_FIELD = "list-record-field";
    public static final String TAG_RECORD_FIELD = "record-field";
    public static final String TAG_RECORD_FIELD_VALUE = "record-field-value";
    public static final String TAG_CREATION_DATE = "creation-date";
    public static final String TAG_MODIFICATION_DATE = "modification-date";
    public static final String TAG_STATUS = "status";
    public static final String ATTRIBUTE_ICON = "icon";

    private static final String TAG_RECORD = "record";
    private static final String ATTRIBUTE_RECORD_ID = "id";
    private static final String ATTRIBUTE_TYPE_ENTRY = "type-entry";
    private static final String ATTRIBUTE_SHOW_IN_RECORD = "isShownInRecord";
    private static final String ATTRIBUTE_SHOW_IN_LIST = "isShownInList";

    private int _nIdRecord;
    private Timestamp _tDateCreation;
    private Timestamp _tDateModification;
    private Directory _directory;
    private List<RecordField> _listRecordField;
    private List<DirectoryAction> _listAction;
    private boolean _bIsEnabled;
    private String _strRoleKey;
    private String _strWorkgroupKey;

    /**
     * 
     * @return the directory associate to the record
     */
    public Directory getDirectory( )
    {
        return _directory;
    }

    /**
     * 
     * set the directory associate to the record
     * @param directory the directory associate to the record
     */
    public void setDirectory( Directory directory )
    {
        this._directory = directory;
    }

    /**
     * return the id of the record
     * @return the id of the record
     */
    public int getIdRecord( )
    {
        return _nIdRecord;
    }

    /**
     * set the id of the record
     * @param idRecord the id of the record
     */
    public void setIdRecord( int idRecord )
    {
        _nIdRecord = idRecord;
    }

    /**
     * return the date creation
     * @return the date creation
     */
    public Timestamp getDateCreation( )
    {
        return _tDateCreation;
    }

    /**
     * set the date creation
     * @param dateCreation date creation
     */
    public void setDateCreation( Timestamp dateCreation )
    {
        _tDateCreation = dateCreation;
    }

    /**
     * return the date Modification
     * @return the date Modification
     */
    public Timestamp getDateModification( )
    {
        return _tDateModification;
    }

    /**
     * set the date Modification
     * @param dateModification date Modification
     */
    public void setDateModification( Timestamp dateModification )
    {
        _tDateModification = dateModification;
    }

    /**
     * 
     * @return the list of field associate to the record
     */
    public List<RecordField> getListRecordField( )
    {
        return _listRecordField;
    }

    /**
     * set the list of record field associate to the record
     * @param listRecordField the list of response associate to the form submit
     */
    public void setActions( List<DirectoryAction> listAction )
    {
        _listAction = listAction;
    }

    /**
     * 
     * @return the list of field associate to the record
     */
    public List<DirectoryAction> getActions( )
    {
        return _listAction;
    }

    /**
     * set the list of record field associate to the record
     * @param listRecordField the list of response associate to the form submit
     */
    public void setListRecordField( List<RecordField> listRecordField )
    {
        _listRecordField = listRecordField;
    }

    /**
     * 
     * @return true if the record is enabled
     */
    public boolean isEnabled( )
    {
        return _bIsEnabled;
    }

    /**
     * set true if the record is enabled
     * @param enable true if the record is enabled
     */
    public void setEnabled( boolean enable )
    {
        _bIsEnabled = enable;
    }

    /**
     * Gets the record role
     * @return recors's role as a String
     * 
     */
    public String getRoleKey( )
    {
        return _strRoleKey;
    }

    /**
     * Sets the directory's role
     * @param strRole The role
     * 
     */
    public void setRoleKey( String strRole )
    {
        _strRoleKey = strRole;
    }

    /**
     * 
     * @return the work group associate to the record
     */
    public String getWorkgroup( )
    {
        return _strWorkgroupKey;
    }

    /**
     * set the work group associate to the record
     * @param workGroup the work group associate to the record
     */
    public void setWorkgroup( String workGroup )
    {
        _strWorkgroupKey = workGroup;
    }

    /**
     * The Xml of the record
     * @param record the record
     * @param plugin the plugin
     * @param locale the locale
     * @param bWithHtmlCode true if the xml must contain html code like <img src
     *            ="">
     * @param state the state of the record
     * @param listEntryResultSearch the list of entry to display
     * @param bDisplayTitleEntryTypeSelect true if template front or false if
     *            template back
     * @param bDisplayFront true if template front or false if template back
     * @param bDisplayExport true if it must be displayed in export
     * @param bDisplayDateCreation true if the date creation must be displayed
     * @return xml
     */
    public StringBuffer getXml( Plugin plugin, Locale locale, boolean bWithHtmlCode, State state,
            List<IEntry> listEntryResultSearch, boolean bDisplayTitleEntryTypeSelect, boolean bDisplayFront,
            boolean bDisplayExport, boolean bDisplayDateCreation )
    {
        return getXml( plugin, locale, bWithHtmlCode, state, listEntryResultSearch, bDisplayTitleEntryTypeSelect,
                bDisplayFront, bDisplayExport, bDisplayDateCreation, false );
    }

    /**
     * The Xml of the record
     * @param record the record
     * @param plugin the plugin
     * @param locale the locale
     * @param bWithHtmlCode true if the xml must contain html code like <img src
     *            ="">
     * @param state the state of the record
     * @param listEntryResultSearch the list of entry to display
     * @param bDisplayTitleEntryTypeSelect true if template front or false if
     *            template back
     * @param bDisplayFront true if template front or false if template back
     * @param bDisplayExport true if it must be displayed in export
     * @param bDisplayDateCreation true if the date creation must be displayed
     * @param bDisplayDateModification true if the modification date must be
     *            displayed, false otherwise
     * @return xml
     */
    public StringBuffer getXml( Plugin plugin, Locale locale, boolean bWithHtmlCode, State state,
            List<IEntry> listEntryResultSearch, boolean bDisplayTitleEntryTypeSelect, boolean bDisplayFront,
            boolean bDisplayExport, boolean bDisplayDateCreation, boolean bDisplayDateModification )
    {
        StringBuffer strXml = new StringBuffer( );
        Map<String, String> model = new HashMap<String, String>( );
        model.put( ATTRIBUTE_RECORD_ID, String.valueOf( this.getIdRecord( ) ) );
        XmlUtil.beginElement( strXml, TAG_RECORD, model );

        if ( bDisplayDateCreation )
        {
            XmlUtil.addElement( strXml, TAG_CREATION_DATE, DateUtil.getDateString( this.getDateCreation( ), locale ) );
        }

        if ( bDisplayDateModification )
        {
            XmlUtil.addElement( strXml, TAG_MODIFICATION_DATE,
                    DateUtil.getDateString( this.getDateModification( ), locale ) );
        }

        if ( state != null )
        {
            Map<String, String> stateAttributes = new HashMap<String, String>( );
            stateAttributes.put( ATTRIBUTE_ICON, Integer.toString( state.getIcon( ).getId( ) ) );
            XmlUtil.addElement( strXml, TAG_STATUS, state.getName( ), stateAttributes );
        }

        XmlUtil.beginElement( strXml, Entry.TAG_LIST_ENTRY );

        Map<String, List<RecordField>> mapEntryRecordFields = DirectoryUtils.getSpecificMapIdEntryListRecordField(
                listEntryResultSearch, this.getIdRecord( ), plugin );

        for ( IEntry entry : listEntryResultSearch )
        {
            if ( entry.getEntryType( ).getGroup( ) && ( entry.getChildren( ) != null ) )
            {
                for ( IEntry entryChildren : entry.getChildren( ) )
                {
                    if ( !entryChildren.getEntryType( ).getComment( ) )
                    {
                        getXmlListRecordField( entryChildren, strXml, plugin, locale, bWithHtmlCode,
                                mapEntryRecordFields.get( Integer.toString( entryChildren.getIdEntry( ) ) ),
                                bDisplayTitleEntryTypeSelect, bDisplayFront, bDisplayExport );
                    }
                }
            }
            else if ( !entry.getEntryType( ).getComment( ) )
            {
                getXmlListRecordField( entry, strXml, plugin, locale, bWithHtmlCode,
                        mapEntryRecordFields.get( Integer.toString( entry.getIdEntry( ) ) ),
                        bDisplayTitleEntryTypeSelect, bDisplayFront, bDisplayExport );
            }
        }

        XmlUtil.endElement( strXml, Entry.TAG_LIST_ENTRY );
        XmlUtil.endElement( strXml, TAG_RECORD );

        return strXml;
    }

    /**
     * The Xml of the record field list
     * @param entry the entry
     * @param strXml the xml buffer
     * @param plugin the plugin
     * @param locale the locale
     * @param bWithHtmlCode true if the xml must contain html code like <img src
     *            ="">
     * @param listRecordField the list of recordField to display
     * @param true if template front or false if template back
     */
    private void getXmlListRecordField( IEntry entry, StringBuffer strXml, Plugin plugin, Locale locale,
            boolean bWithHtmlCode, List<RecordField> listRecordField, boolean bDisplayTitleEntryTypeSelect,
            boolean bDisplayFront, boolean bDisplayExport )
    {
        Map<String, String> model = DirectoryService.getInstance( ).getModelForEntryForXml( entry );
        XmlUtil.beginElement( strXml, Entry.TAG_ENTRY, model );

        Map<String, String> modelListRecordField = new HashMap<String, String>( );
        boolean bIsEntryTypeGeolocation = ( entry instanceof EntryTypeGeolocation ) ? true : false;
        boolean bIsEntryTypeNumbering = ( entry instanceof EntryTypeNumbering ) ? true : false;
        modelListRecordField.put( RecordField.ATTRIBUTE_GEOLOCATION, Boolean.toString( bIsEntryTypeGeolocation ) );
        XmlUtil.beginElement( strXml, TAG_LIST_RECORD_FIELD, modelListRecordField );

        if ( listRecordField != null )
        {
            for ( RecordField recordField : listRecordField )
            {
                Map<String, String> modelRecordField = new HashMap<String, String>( );
                Field field = recordField.getField( );

                if ( ( field != null ) && StringUtils.isNotBlank( recordField.getField( ).getTitle( ) ) )
                {
                    modelRecordField.put( Record.ATTRIBUTE_TITLE, recordField.getField( ).getTitle( ) );
                }

                if ( entry.getEntryType( ) != null )
                {
                    modelRecordField.put( ATTRIBUTE_TYPE_ENTRY, String.valueOf( entry.getEntryType( ).getIdType( ) ) );
                }

                if ( field != null )
                {
                    modelRecordField.put( ATTRIBUTE_SHOW_IN_RECORD, String.valueOf( field.isShownInResultRecord( ) ) );
                    modelRecordField.put( ATTRIBUTE_SHOW_IN_LIST, String.valueOf( field.isShownInResultList( ) ) );
                }

                XmlUtil.beginElement( strXml, TAG_RECORD_FIELD, modelRecordField );

                if ( bWithHtmlCode )
                {
                    Map<String, String> modelRecordFieldValue = new HashMap<String, String>( );

                    if ( field != null )
                    {
                        modelRecordFieldValue.put( Field.ATTRIBUTE_FIELD_ID, Integer.toString( field.getIdField( ) ) );

                        if ( StringUtils.isNotBlank( field.getTitle( ) ) )
                        {
                            modelRecordFieldValue
                                    .put( Field.ATTRIBUTE_FIELD_TITLE, recordField.getField( ).getTitle( ) );
                        }
                    }

                    XmlUtil.addElementHtml( strXml, TAG_RECORD_FIELD_VALUE, recordField.getEntry( )
                            .getHtmlRecordFieldValue( locale, recordField, bDisplayFront ), modelRecordFieldValue );
                }
                else
                {
                    if ( recordField.getFile( ) != null )
                    {
                        IEntry entryFile = EntryHome.findByPrimaryKey( recordField.getEntry( ).getIdEntry( ), plugin );

                        XmlUtil.addElementHtml( strXml, TAG_RECORD_FIELD_VALUE, entry.convertRecordFieldValueToString(
                                recordField, locale, bDisplayFront, bDisplayExport ) );
                        strXml.append( recordField.getFile( ).getXml( plugin, locale,
                                entry.getEntryType( ).getIdType( ), entryFile.getDisplayWidth( ),
                                entryFile.getDisplayHeight( ) ) );
                    }
                    else
                    {
                        if ( bDisplayTitleEntryTypeSelect && !bIsEntryTypeNumbering )
                        {
                            XmlUtil.addElementHtml( strXml, TAG_RECORD_FIELD_VALUE, DirectoryUtils
                                    .substituteSpecialCaractersForExport( recordField.getEntry( )
                                            .convertRecordFieldTitleToString( recordField, locale, bDisplayFront ) ) );
                        }
                        else
                        {
                            Map<String, String> modelRecordFieldValue = new HashMap<String, String>( );

                            if ( recordField.getField( ) != null )
                            {
                                modelRecordFieldValue.put( Field.ATTRIBUTE_FIELD_ID,
                                        Integer.toString( recordField.getField( ).getIdField( ) ) );

                                if ( StringUtils.isNotBlank( recordField.getField( ).getTitle( ) ) )
                                {
                                    modelRecordFieldValue.put( Field.ATTRIBUTE_FIELD_TITLE, recordField.getField( )
                                            .getTitle( ) );
                                }
                            }

                            XmlUtil.addElementHtml( strXml, TAG_RECORD_FIELD_VALUE, DirectoryUtils
                                    .substituteSpecialCaractersForExport( recordField.getEntry( )
                                            .convertRecordFieldValueToString( recordField, locale, bDisplayFront,
                                                    bDisplayExport ) ), modelRecordFieldValue );
                        }

                        XmlUtil.addEmptyElement( strXml, File.TAG_FILE, null );
                    }
                }

                XmlUtil.endElement( strXml, TAG_RECORD_FIELD );
            }
        }

        XmlUtil.endElement( strXml, TAG_LIST_RECORD_FIELD );
        XmlUtil.endElement( strXml, Entry.TAG_ENTRY );
    }

    /**
     * The Xml of the record
     * @param plugin the plugin
     * @param locale the locale
     * @param bWithHtmlCode true if the xml must contain html code like <img src
     *            ="">
     * @param state the state of the record
     * @param listEntryResultSearch the list of entry to display
     * @param bDisplayTitleEntryTypeSelect true if the title must be display for
     *            entry type select
     * @param bDisplayFront true if template front or false if template back
     * @param bDisplayExport true if it must be displayed in export
     * @param bDisplayDateCreation true if the date creation must be displayed
     * @return xml
     */
    public StringBuffer getXmlForCsvExport( Plugin plugin, Locale locale, boolean bWithHtmlCode, State state,
            List<IEntry> listEntryResultSearch, boolean bDisplayTitleEntryTypeSelect, boolean bDisplayFront,
            boolean bDisplayExport, boolean bDisplayDateCreation )
    {
        return getXmlForCsvExport( plugin, locale, bWithHtmlCode, state, listEntryResultSearch,
                bDisplayTitleEntryTypeSelect, bDisplayFront, bDisplayExport, bDisplayDateCreation, false );
    }

    /**
     * The Xml of the record
     * @param plugin the plugin
     * @param locale the locale
     * @param bWithHtmlCode true if the xml must contain html code like <img src
     *            ="">
     * @param state the state of the record
     * @param listEntryResultSearch the list of entry to display
     * @param bDisplayTitleEntryTypeSelect true if the title must be display for
     *            entry type select
     * @param bDisplayFront true if template front or false if template back
     * @param bDisplayExport true if it must be displayed in export
     * @param bDisplayDateCreation true if the date creation must be displayed
     * @param bDisplayDateModification true if the modification date must be
     *            displayed, false otherwise
     * @return xml
     */
    public StringBuffer getXmlForCsvExport( Plugin plugin, Locale locale, boolean bWithHtmlCode, State state,
            List<IEntry> listEntryResultSearch, boolean bDisplayTitleEntryTypeSelect, boolean bDisplayFront,
            boolean bDisplayExport, boolean bDisplayDateCreation, boolean bDisplayDateModification )
    {
        StringBuffer strXml = new StringBuffer( );
        Map<String, String> model = new HashMap<String, String>( );
        model.put( ATTRIBUTE_RECORD_ID, String.valueOf( this.getIdRecord( ) ) );
        XmlUtil.beginElement( strXml, TAG_RECORD, model );

        if ( state != null )
        {
            Map<String, String> stateAttributes = new HashMap<String, String>( );
            stateAttributes.put( ATTRIBUTE_ICON, Integer.toString( state.getIcon( ).getId( ) ) );
            XmlUtil.addElement( strXml, TAG_STATUS, state.getName( ), stateAttributes );
        }

        XmlUtil.beginElement( strXml, Entry.TAG_LIST_ENTRY );

        Map<String, List<RecordField>> mapEntryRecordFields = DirectoryUtils.getSpecificMapIdEntryListRecordField(
                listEntryResultSearch, this.getIdRecord( ), plugin );

        if ( bDisplayDateCreation )
        {
            HashMap<String, String> modelCreationDate = new HashMap<String, String>( );
            modelCreationDate.put( Entry.ATTRIBUTE_ENTRY_ID, "0" );
            XmlUtil.beginElement( strXml, Entry.TAG_ENTRY, modelCreationDate );
            XmlUtil.beginElement( strXml, TAG_LIST_RECORD_FIELD );
            XmlUtil.beginElement( strXml, TAG_RECORD_FIELD );
            XmlUtil.addElementHtml( strXml, TAG_RECORD_FIELD_VALUE,
                    DateUtil.getDateString( this.getDateCreation( ), locale ) );
            XmlUtil.endElement( strXml, TAG_RECORD_FIELD );
            XmlUtil.endElement( strXml, TAG_LIST_RECORD_FIELD );
            XmlUtil.endElement( strXml, Entry.TAG_ENTRY );
        }

        if ( bDisplayDateModification )
        {
            HashMap<String, String> modelModificationDate = new HashMap<String, String>( );
            modelModificationDate.put( Entry.ATTRIBUTE_ENTRY_ID, "0" );
            XmlUtil.beginElement( strXml, Entry.TAG_ENTRY, modelModificationDate );
            XmlUtil.beginElement( strXml, TAG_LIST_RECORD_FIELD );
            XmlUtil.beginElement( strXml, TAG_RECORD_FIELD );
            XmlUtil.addElementHtml( strXml, TAG_RECORD_FIELD_VALUE,
                    DateUtil.getDateString( this.getDateModification( ), locale ) );
            XmlUtil.endElement( strXml, TAG_RECORD_FIELD );
            XmlUtil.endElement( strXml, TAG_LIST_RECORD_FIELD );
            XmlUtil.endElement( strXml, Entry.TAG_ENTRY );
        }

        for ( IEntry entry : listEntryResultSearch )
        {
            if ( entry.getEntryType( ).getGroup( ) && ( entry.getChildren( ) != null ) )
            {
                for ( IEntry entryChildren : entry.getChildren( ) )
                {
                    if ( !entryChildren.getEntryType( ).getComment( ) )
                    {
                        getXmlListRecordField( entryChildren, strXml, plugin, locale, bWithHtmlCode,
                                mapEntryRecordFields.get( Integer.toString( entryChildren.getIdEntry( ) ) ),
                                bDisplayTitleEntryTypeSelect, bDisplayFront, bDisplayExport );
                    }
                }
            }
            else if ( !entry.getEntryType( ).getComment( ) )
            {
                getXmlListRecordField( entry, strXml, plugin, locale, bWithHtmlCode,
                        mapEntryRecordFields.get( Integer.toString( entry.getIdEntry( ) ) ),
                        bDisplayTitleEntryTypeSelect, bDisplayFront, bDisplayExport );
            }
        }

        XmlUtil.endElement( strXml, Entry.TAG_LIST_ENTRY );
        XmlUtil.endElement( strXml, TAG_RECORD );

        return strXml;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getIdExtendableResource( )
    {
        return Integer.toString( getIdRecord( ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getExtendableResourceType( )
    {
        return EXTENDABLE_RESOURCE_TYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getExtendableResourceName( )
    {
        return Integer.toString( getIdRecord( ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getExtendableResourceDescription( )
    {
        return Integer.toString( getIdRecord( ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getExtendableResourceImageUrl( )
    {
        if ( _listRecordField != null && _listRecordField.size( ) > 0 )
        {
            RecordField recordFieldImage = null;
            for ( RecordField recordField : _listRecordField )
            {
                if ( recordField.isBigThumbnail( ) )
                {
                    // If we find a big thumbnail, we return its URL
                    recordFieldImage = recordField;
                    break;
                }
                else if ( recordField.isLittleThumbnail( ) )
                {
                    // If we find a little thumbnail, we return its URL unless we find a big thumbnail
                    recordFieldImage = recordField;
                }
            }
            if ( recordFieldImage != null )
            {
                return FileImgService
                        .getResourceImageEntryUrlWhitoutEntities( recordFieldImage.getFile( ).getIdFile( ) );
            }
        }
        return null;
    }
}

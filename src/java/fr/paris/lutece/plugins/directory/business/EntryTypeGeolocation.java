/*
 * Copyright (c) 2002-2017, Mairie de Paris
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
import fr.paris.lutece.plugins.directory.service.directorysearch.DirectorySearchItem;
import fr.paris.lutece.plugins.directory.utils.DirectoryErrorException;
import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.xml.XmlUtil;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * EntryTypeGeolocation
 *
 */
public class EntryTypeGeolocation extends Entry
{
    private Entry _entryAdditionalAddress;

    // PARAMETERS
    public static final String PARAMETER_ID_ENTRY = "idEntry";
    public static final String PARAMETER_ID_DIRECTORY = "idDirectory";
    public static final String PARAMETER_MAP_PROVIDER = "map_provider";
    public static final String PARAMETER_SHOWXY = "showxy";
    public static final String PARAMETER_SUFFIX_X = "_x";
    public static final String PARAMETER_SUFFIX_Y = "_y";
    public static final String PARAMETER_SUFFIX_MAP_PROVIDER = "_map_provider";
    public static final String PARAMETER_SUFFIX_ADDRESS = "_address";

    // PARAMETERS OF GISMAP ATTRIBUTS
    public static final String PARAMETER_EDIT_MODE = "edit_mode";
    public static final String PARAMETER_VIEW_NUMBER_ATT = "view_number_att";
    public static final String PARAMETER_VIEW_NUMBER_GES = "view_number_ges";
    // public static final String PARAMETER_SUFFIX_ADDITIONAL_ADDRESS = "_additional_address";
    public static final String PARAMETER_SUFFIX_GEOMETRY = "_geometry";

    public static final String PARAMETER_EDIT_MODE_LIST = "gismap.edit.mode.list";

    // PUBLIC COSNTANTS
    public static final String CONSTANT_ADDRESS = "address";
    public static final String CONSTANT_X = "X";
    public static final String CONSTANT_Y = "Y";
    public static final String CONSTANT_PROVIDER = "provider";
    public static final String CONSTANT_SHOWXY = "showxy";
    // PUBLIC COSNTANTS OF GISMAP ATTRIBUTS
    public static final String CONSTANT_EDIT_MODE = "editMode";
    public static final String CONSTANT_VIEW_NUMBER_ATT = "viewNumberAtt";
    public static final String CONSTANT_VIEW_NUMBER_GES = "viewNumberGes";
    // public static final String CONSTANT_ADDITIONAL_ADDRESS = "additionalAddress";
    public static final String CONSTANT_GEOMETRY = "geometry";

    // PRIVATE CONSTANTS
    private static final int CONSTANT_POSITION_X = 0;
    private static final int CONSTANT_POSITION_Y = 1;
    private static final int CONSTANT_POSITION_MAP_PROVIDER = 2;
    private static final int CONSTANT_POSITION_ADDRESS = 3;
    // private static final int CONSTANT_POSITION_ADDITIONAL_ADDRESS = 4;
    private static final int CONSTANT_POSITION_GEOMETRY = 4;
    private static final int CONSTANT_FIELDS_COUNT = 5;

    // TEMPLATES
    private static final String TEMPLATE_CREATE = "admin/plugins/directory/entrytypegeolocation/create_entry_type_geolocation.html";
    private static final String TEMPLATE_MODIFY = "admin/plugins/directory/entrytypegeolocation/modify_entry_type_geolocation.html";
    private static final String TEMPLATE_HTML_CODE_FORM_ENTRY = "admin/plugins/directory/entrytypegeolocation/html_code_form_entry_type_geolocation.html";
    private static final String TEMPLATE_HTML_CODE_ENTRY_VALUE = "admin/plugins/directory/entrytypegeolocation/html_code_entry_value_type_geolocation.html";
    private static final String TEMPLATE_HTML_CODE_FORM_SEARCH_ENTRY = "admin/plugins/directory/entrytypegeolocation/html_code_form_search_entry_type_geolocation.html";
    private static final String TEMPLATE_HTML_FRONT_CODE_FORM_ENTRY = "skin/plugins/directory/entrytypegeolocation/html_code_form_entry_type_geolocation.html";
    private static final String TEMPLATE_HTML_FRONT_CODE_ENTRY_VALUE = "skin/plugins/directory/entrytypegeolocation/html_code_entry_value_type_geolocation.html";
    private static final String TEMPLATE_HTML_FRONT_CODE_FORM_SEARCH_ENTRY = "skin/plugins/directory/entrytypegeolocation/html_code_form_search_entry_type_geolocation.html";

    // SQL
    private static final String SQL_JOIN_DIRECTORY_RECORD_FIELD = " JOIN directory_record_field drf ON drf.id_record = dr.id_record AND drf.id_entry = ? JOIN directory_field df ON df.id_entry = drf.id_entry AND df.id_field = drf.id_field AND title=? ";

    /**
     * @return the _entryAdditionalAddress
     */
    public Entry getEntryAdditionalAddress( )
    {
        return _entryAdditionalAddress;
    }

    /**
     * @param entryAdditionalAddress
     *            the entryAdditionalAddress to set
     */
    public void setEntryAdditionalAddress( Entry entryAdditionalAddress )
    {
        this._entryAdditionalAddress = entryAdditionalAddress;
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public String getTemplateCreate( )
    {
        return TEMPLATE_CREATE;
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public String getTemplateModify( )
    {
        return TEMPLATE_MODIFY;
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public String getTemplateHtmlFormEntry( boolean isDisplayFront )
    {
        if ( isDisplayFront )
        {
            return TEMPLATE_HTML_FRONT_CODE_FORM_ENTRY;
        }

        return TEMPLATE_HTML_CODE_FORM_ENTRY;
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public String getTemplateHtmlFormSearchEntry( boolean isDisplayFront )
    {
        if ( isDisplayFront )
        {
            return TEMPLATE_HTML_FRONT_CODE_FORM_SEARCH_ENTRY;
        }

        return TEMPLATE_HTML_CODE_FORM_SEARCH_ENTRY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTemplateHtmlRecordFieldValue( boolean isDisplayFront )
    {
        if ( isDisplayFront )
        {
            return TEMPLATE_HTML_FRONT_CODE_ENTRY_VALUE;
        }

        return TEMPLATE_HTML_CODE_ENTRY_VALUE;
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public String getEntryData( HttpServletRequest request, Locale locale )
    {
        String strTitle = request.getParameter( PARAMETER_TITLE );
        String strHelpMessage = ( request.getParameter( PARAMETER_HELP_MESSAGE ) != null ) ? request.getParameter( PARAMETER_HELP_MESSAGE ).trim( ) : null;
        String strHelpMessageSearch = ( request.getParameter( PARAMETER_HELP_MESSAGE_SEARCH ) != null ) ? request.getParameter( PARAMETER_HELP_MESSAGE_SEARCH )
                .trim( ) : null;
        String strComment = request.getParameter( PARAMETER_COMMENT );
        String strMandatory = request.getParameter( PARAMETER_MANDATORY );
        String strIndexed = request.getParameter( PARAMETER_INDEXED );
        String strIndexedAsTitle = request.getParameter( PARAMETER_INDEXED_AS_TITLE );
        String strIndexedAsSummary = request.getParameter( PARAMETER_INDEXED_AS_SUMMARY );
        String strShowInAdvancedSearch = request.getParameter( PARAMETER_SHOWN_IN_ADVANCED_SEARCH );
        String strShowInResultList = request.getParameter( PARAMETER_SHOWN_IN_RESULT_LIST );
        String strShowInResultRecord = request.getParameter( PARAMETER_SHOWN_IN_RESULT_RECORD );
        String strShowInHistory = request.getParameter( PARAMETER_SHOWN_IN_HISTORY );
        String strMapProvider = request.getParameter( PARAMETER_MAP_PROVIDER );
        String strShowInExport = request.getParameter( PARAMETER_SHOWN_IN_EXPORT );
        String strShowInCompleteness = request.getParameter( PARAMETER_SHOWN_IN_COMPLETENESS );
        String strShowXY = request.getParameter( PARAMETER_SHOWXY );
        String strEditMode = request.getParameter( PARAMETER_EDIT_MODE );
        String strViewNumberAtt = request.getParameter( PARAMETER_VIEW_NUMBER_ATT );
        String strViewNumberGes = request.getParameter( PARAMETER_VIEW_NUMBER_GES );

        String strIdEntry = request.getParameter( PARAMETER_ID_ENTRY );
        int nIdEntry = Integer.parseInt( strIdEntry );

        String strIdDirectory = request.getParameter( PARAMETER_ID_DIRECTORY );
        int nIdDirectory = Integer.parseInt( strIdDirectory );

        String strFieldError = DirectoryUtils.EMPTY_STRING;

        if ( ( strTitle == null ) || strTitle.trim( ).equals( DirectoryUtils.EMPTY_STRING ) )
        {
            strFieldError = FIELD_TITLE;
        }

        if ( !strFieldError.equals( DirectoryUtils.EMPTY_STRING ) )
        {
            Object [ ] tabRequiredFields = {
                I18nService.getLocalizedString( strFieldError, locale )
            };

            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields, AdminMessage.TYPE_STOP );
        }

        // for don't update fields listFields=null
        Field xField = findField( CONSTANT_X, getFields( ) );

        if ( xField == null )
        {
            xField = new Field( );
        }

        xField.setEntry( this );
        xField.setTitle( CONSTANT_X );
        xField.setValue( CONSTANT_X );

        Field yField = findField( CONSTANT_Y, getFields( ) );

        if ( yField == null )
        {
            yField = new Field( );
        }

        yField.setEntry( this );
        yField.setTitle( CONSTANT_Y );
        yField.setValue( CONSTANT_Y );

        Field mapProviderField = findField( CONSTANT_PROVIDER, getFields( ) );

        if ( mapProviderField == null )
        {
            mapProviderField = new Field( );
        }

        mapProviderField.setEntry( this );
        mapProviderField.setTitle( CONSTANT_PROVIDER );
        mapProviderField.setValue( strMapProvider );

        Field addressField = findField( CONSTANT_ADDRESS, getFields( ) );

        if ( addressField == null )
        {
            addressField = new Field( );
        }

        addressField.setEntry( this );
        addressField.setTitle( CONSTANT_ADDRESS );

        /*
         * Field additionalAddressField = findField( CONSTANT_ADDITIONAL_ADDRESS, getFields( ) );
         * 
         * if ( additionalAddressField == null ) { additionalAddressField = new Field( ); }
         * 
         * additionalAddressField.setEntry( this ); additionalAddressField.setTitle( CONSTANT_ADDITIONAL_ADDRESS );
         */

        Field geometryField = findField( CONSTANT_GEOMETRY, getFields( ) );

        if ( geometryField == null )
        {
            geometryField = new Field( );
        }

        geometryField.setEntry( this );
        geometryField.setTitle( CONSTANT_GEOMETRY );

        Field showXYField = findField( CONSTANT_SHOWXY, getFields( ) );

        if ( showXYField == null )
        {
            showXYField = new Field( );
        }

        showXYField.setEntry( this );
        showXYField.setTitle( CONSTANT_SHOWXY );
        showXYField.setValue( StringUtils.isNotBlank( strShowXY ) ? Boolean.TRUE.toString( ) : Boolean.FALSE.toString( ) );

        Field editModeField = findField( CONSTANT_EDIT_MODE, getFields( ) );

        if ( editModeField == null )
        {
            editModeField = new Field( );
        }

        editModeField.setEntry( this );
        editModeField.setTitle( CONSTANT_EDIT_MODE );
        editModeField.setValue( strEditMode );

        Field viewNumberAttField = findField( CONSTANT_VIEW_NUMBER_ATT, getFields( ) );

        if ( viewNumberAttField == null )
        {
            viewNumberAttField = new Field( );
        }

        viewNumberAttField.setEntry( this );
        viewNumberAttField.setTitle( CONSTANT_VIEW_NUMBER_ATT );
        viewNumberAttField.setValue( strViewNumberAtt );

        Field viewNumberGesField = findField( CONSTANT_VIEW_NUMBER_GES, getFields( ) );

        if ( viewNumberGesField == null )
        {
            viewNumberGesField = new Field( );
        }

        viewNumberGesField.setEntry( this );
        viewNumberGesField.setTitle( CONSTANT_VIEW_NUMBER_GES );
        viewNumberGesField.setValue( strViewNumberGes );

        List<Field> listFields = new ArrayList<Field>( );
        listFields.add( xField );
        listFields.add( yField );
        listFields.add( mapProviderField );
        listFields.add( addressField );
        // listFields.add( additionalAddressField );
        listFields.add( geometryField );
        listFields.add( showXYField );
        listFields.add( editModeField );
        listFields.add( viewNumberAttField );
        listFields.add( viewNumberGesField );
        this.setFields( listFields );

        this.setTitle( strTitle );
        this.setHelpMessage( strHelpMessage );
        this.setHelpMessageSearch( strHelpMessageSearch );
        this.setComment( strComment );
        this.setMandatory( strMandatory != null );
        this.setIndexed( strIndexed != null );
        this.setIndexedAsTitle( strIndexedAsTitle != null );
        this.setIndexedAsSummary( strIndexedAsSummary != null );
        this.setShownInAdvancedSearch( strShowInAdvancedSearch != null );
        this.setShownInResultList( strShowInResultList != null );
        this.setShownInResultRecord( strShowInResultRecord != null );
        this.setFieldInLine( false );
        this.setShownInHistory( strShowInHistory != null );
        this.setMapProvider( MapProviderManager.getMapProvider( strMapProvider ) );
        this.setShownInExport( strShowInExport != null );
        this.setShownInCompleteness( strShowInCompleteness != null );

        IEntry entry = EntryHome.findByPrimaryKey( nIdEntry, DirectoryUtils.getPlugin( ) );
        boolean update = false;
        if ( entry != null )
        {
            update = true;
            getAdditionalAddressEntry( 6, entry, locale, update );
        }
        else
        {
            entry = new Entry( );
            Directory directory = DirectoryHome.findByPrimaryKey( nIdDirectory, DirectoryUtils.getPlugin( ) );
            entry.setDirectory( directory );
            getAdditionalAddressEntry( 6, entry, locale, update );
        }

        return null;
    }

    /**
     * Builds the {@link ReferenceList} of all available map providers
     * 
     * @return the {@link ReferenceList}
     */
    public ReferenceList getMapProvidersRefList( )
    {
        ReferenceList refList = new ReferenceList( );

        refList.addItem( DirectoryUtils.EMPTY_STRING, DirectoryUtils.EMPTY_STRING );

        for ( IMapProvider mapProvider : MapProviderManager.getMapProvidersList( ) )
        {
            refList.add( mapProvider.toRefItem( ) );
        }

        return refList;
    }

    /**
     * Builds the {@link ReferenceList} of all available edit modes
     * 
     * @return the {@link ReferenceList}
     */
    public ReferenceList getEditModesRefList( )
    {
        String strEditModeListProperty = AppPropertiesService.getProperty( PARAMETER_EDIT_MODE_LIST );
        ReferenceList refList = new ReferenceList( );
        refList.addItem( StringUtils.EMPTY, StringUtils.EMPTY );
        if ( strEditModeListProperty != null )
        {
            String [ ] strEditModeListPropertyArray = strEditModeListProperty.split( "," );

            for ( int i = 0; i < strEditModeListPropertyArray.length; i++ )
            {
                refList.addItem( strEditModeListPropertyArray [i], strEditModeListPropertyArray [i] );
            }
        }

        return refList;
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public void getRecordFieldData( Record record, HttpServletRequest request, boolean bTestDirectoryError, boolean addNewValue,
            List<RecordField> listRecordField, Locale locale ) throws DirectoryErrorException
    {
        List<String> listValue = new ArrayList<String>( );
        String strXValue = request.getParameter( this.getIdEntry( ) + PARAMETER_SUFFIX_X );
        String strYValue = request.getParameter( this.getIdEntry( ) + PARAMETER_SUFFIX_Y );
        String strMapProviderValue = request.getParameter( this.getIdEntry( ) + PARAMETER_SUFFIX_MAP_PROVIDER );
        String strAddressValue = request.getParameter( this.getIdEntry( ) + PARAMETER_SUFFIX_ADDRESS );
        // String strAdditionalAddressValue = request.getParameter( this.getIdEntry( ) + PARAMETER_SUFFIX_ADDITIONAL_ADDRESS );
        String strGeometryValue = request.getParameter( this.getIdEntry( ) + PARAMETER_SUFFIX_GEOMETRY );
        listValue.add( strXValue );
        listValue.add( strYValue );
        listValue.add( strMapProviderValue );
        listValue.add( strAddressValue );
        // listValue.add( strAdditionalAddressValue );
        listValue.add( strGeometryValue );

        getRecordFieldData( record, listValue, bTestDirectoryError, addNewValue, listRecordField, locale );
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public void addSearchCriteria( HashMap<String, Object> mapSearchItem, RecordField recordField )
    {
        String strValue;

        if ( recordField.getValue( ) != null )
        {
            if ( !CONSTANT_PROVIDER.equals( recordField.getField( ).getTitle( ) ) )
            {
                strValue = new String( recordField.getValue( ) );

                if ( !strValue.trim( ).equals( DirectoryUtils.EMPTY_STRING ) )
                {
                    mapSearchItem.put( DirectorySearchItem.FIELD_CONTENTS, strValue );
                }
            }
        }
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public void getRecordFieldData( Record record, List<String> lstValue, boolean bTestDirectoryError, boolean addNewValue, List<RecordField> listRecordField,
            Locale locale ) throws DirectoryErrorException
    {
        if ( lstValue.size( ) != CONSTANT_FIELDS_COUNT )
        {
            throw new DirectoryErrorException( this.getTitle( ) );
        }

        String strXValue = lstValue.get( CONSTANT_POSITION_X );
        String strYValue = lstValue.get( CONSTANT_POSITION_Y );
        String strMapProviderValue = lstValue.get( CONSTANT_POSITION_MAP_PROVIDER );
        String strAddressValue = lstValue.get( CONSTANT_POSITION_ADDRESS );
        // String strAdditionalAddressValue = lstValue.get( CONSTANT_POSITION_ADDITIONAL_ADDRESS );
        String strGeometryValue = lstValue.get( CONSTANT_POSITION_GEOMETRY );

        Field xField = findField( CONSTANT_X, getFields( ) );
        Field yField = findField( CONSTANT_Y, getFields( ) );

        if ( bTestDirectoryError )
        {
            if ( this.isMandatory( ) )
            {
                if ( ( StringUtils.isBlank( strXValue ) || StringUtils.isBlank( strYValue ) ) && StringUtils.isBlank( strAddressValue ) )
                {
                    throw new DirectoryErrorException( this.getTitle( ) );
                }
            }
            else
                if ( ( StringUtils.isBlank( strXValue ) && StringUtils.isNotBlank( strYValue ) )
                        || ( StringUtils.isNotBlank( strXValue ) && StringUtils.isBlank( strYValue ) ) )
                {
                    throw new DirectoryErrorException( this.getTitle( ) );
                }
        }

        RecordField recordFieldX = new RecordField( );
        recordFieldX.setEntry( this );
        recordFieldX.setValue( strXValue );
        recordFieldX.setField( xField );
        listRecordField.add( recordFieldX );

        RecordField recordFieldY = new RecordField( );
        recordFieldY.setEntry( this );
        recordFieldY.setValue( strYValue );
        recordFieldY.setField( yField );
        listRecordField.add( recordFieldY );

        IMapProvider mapProvider = MapProviderManager.getMapProvider( strMapProviderValue );
        Field mapProviderField = findField( CONSTANT_PROVIDER, getFields( ) );

        RecordField recordFieldMapProvider = new RecordField( );
        recordFieldMapProvider.setEntry( this );

        if ( mapProvider == null )
        {
            strMapProviderValue = mapProviderField.getValue( );
        }

        recordFieldMapProvider.setValue( strMapProviderValue );
        recordFieldMapProvider.setField( mapProviderField );
        listRecordField.add( recordFieldMapProvider );

        RecordField recordFieldAddress = new RecordField( );
        Field addressField = findField( CONSTANT_ADDRESS, getFields( ) );

        recordFieldAddress.setEntry( this );
        recordFieldAddress.setValue( strAddressValue );
        recordFieldAddress.setField( addressField );
        listRecordField.add( recordFieldAddress );

        /*
         * RecordField recordFieldAddtionalAddress = new RecordField( ); Field additionalAddressField = findField( CONSTANT_ADDITIONAL_ADDRESS, getFields( ) );
         * 
         * recordFieldAddtionalAddress.setEntry( this ); recordFieldAddtionalAddress.setValue( strAdditionalAddressValue );
         * recordFieldAddtionalAddress.setField( additionalAddressField ); listRecordField.add( recordFieldAddtionalAddress );
         */

        RecordField recordFieldGeometry = new RecordField( );
        Field geometryField = findField( CONSTANT_GEOMETRY, getFields( ) );

        recordFieldGeometry.setEntry( this );
        recordFieldGeometry.setValue( strGeometryValue );
        recordFieldGeometry.setField( geometryField );
        listRecordField.add( recordFieldGeometry );
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public void getImportRecordFieldData( Record record, byte [ ] decodedBytes, String nomFile, boolean b, List<RecordField> listRecordField, Locale locale )
            throws DirectoryErrorException
    {
        super.getImportRecordFieldData( record, decodedBytes, nomFile, b, listRecordField, locale );
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public void getImportRecordFieldData( Record record, String strImportValue, boolean bTestDirectoryError, List<RecordField> listRecordField, Locale locale )
            throws DirectoryErrorException
    {
        List<String> lstValue = splitGeolocationValues( strImportValue );

        getRecordFieldData( record, lstValue, bTestDirectoryError, false, listRecordField, locale );
    }

    /**
     * Split the import value to X, Y, provider and address
     * 
     * @param strNotSplittedValue
     *            the import value
     * @return a list of string
     */
    private List<String> splitGeolocationValues( String strNotSplittedValue )
    {
        Field showXYField = findField( CONSTANT_SHOWXY, getFields( ) );

        // if ( !strNotSplittedValue.contains( DirectoryUtils.CONSTANT_COMA ) )
        if ( !Boolean.parseBoolean( showXYField.getValue( ) ) )
        {
            List<String> lstValue = new ArrayList<String>( );

            for ( int nCpt = 0; nCpt < CONSTANT_FIELDS_COUNT; nCpt++ )
            {
                lstValue.add( StringUtils.EMPTY );
            }

            if ( StringUtils.isNotBlank( strNotSplittedValue ) )
            {
                lstValue.remove( CONSTANT_POSITION_ADDRESS );
                lstValue.add( CONSTANT_POSITION_ADDRESS, strNotSplittedValue );
            }

            return lstValue;
        }

        /** DIRECTORY-70 : The X, Y and provider are not displayed in the export */
        /*
         * // first, split coma "," if ( StringUtils.isBlank( strNotSplittedValue ) ) { List<String> listEmptyValues = new ArrayList<String>( );
         * 
         * for ( int nCpt = 0; nCpt < CONSTANT_FIELDS_COUNT; nCpt++ ) { listEmptyValues.add( DirectoryUtils.EMPTY_STRING ); }
         * 
         * return listEmptyValues; }
         * 
         * String[] comaSplitted = strNotSplittedValue.split( DirectoryUtils.CONSTANT_COMA );
         * 
         * if ( ( comaSplitted == null ) || ( comaSplitted.length < 3 ) ) { throw new DirectoryErrorException( this.getTitle( ) ); }
         */
        String [ ] comaSplitted = strNotSplittedValue.split( DirectoryUtils.CONSTANT_COMA );

        List<String> listGeolocValues = new ArrayList<String>( 4 );
        StringBuilder sbAddress = new StringBuilder( );

        for ( int i = 0; i < comaSplitted.length; i++ )
        {
            if ( i < CONSTANT_POSITION_ADDRESS )
            {
                listGeolocValues.add( comaSplitted [i] );
            }
            else
                if ( i == CONSTANT_POSITION_ADDRESS )
                {
                    sbAddress.append( comaSplitted [i] );
                }
                else
                {
                    sbAddress.append( DirectoryUtils.CONSTANT_COMA );
                    sbAddress.append( comaSplitted [i] );
                }
        }

        listGeolocValues.add( sbAddress.toString( ) );

        return listGeolocValues;

        // for ( String strValue : comaSplitted )
        // {
        // if ( strValue.startsWith( CONSTANT_X + DirectoryUtils.CONSTANT_EQUAL ) )
        // {
        // tabGeolocationValues[CONSTANT_POSITION_X] = strValue.substring( ( CONSTANT_X +
        // DirectoryUtils.CONSTANT_EQUAL ).length( ) );
        // }
        // else if ( strValue.startsWith( CONSTANT_Y + DirectoryUtils.CONSTANT_EQUAL ) )
        // {
        // tabGeolocationValues[CONSTANT_POSITION_Y] = strValue.substring( ( CONSTANT_Y +
        // DirectoryUtils.CONSTANT_EQUAL ).length( ) );
        // }
        // else if ( strValue.startsWith( CONSTANT_PROVIDER + DirectoryUtils.CONSTANT_EQUAL ) )
        // {
        // tabGeolocationValues[CONSTANT_POSITION_MAP_PROVIDER] = strValue.substring( ( CONSTANT_PROVIDER +
        // DirectoryUtils.CONSTANT_EQUAL ).length( ) );
        // }
        // else if ( strValue.startsWith( CONSTANT_ADDRESS + DirectoryUtils.CONSTANT_EQUAL ) )
        // {
        // tabGeolocationValues[CONSTANT_POSITION_ADDRESS] = strValue.substring( ( CONSTANT_ADDRESS +
        // DirectoryUtils.CONSTANT_EQUAL ).length( ) );
        // }
        // else
        // {
        // if ( StringUtils.isNotBlank( tabGeolocationValues[CONSTANT_POSITION_ADDRESS] ) )
        // {
        // tabGeolocationValues[CONSTANT_POSITION_ADDRESS] += strValue;
        // }
        // else
        // {
        // throw new DirectoryErrorException( this.getTitle( ) );
        // }
        // }
        // }
        //
        // return Arrays.asList( tabGeolocationValues );
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public String toString( )
    {
        return "Entry Type Geolocation " + this.getFields( ) + " " + this.getMapProvider( );
    }

    /**
     * Finds a field according to its title
     * 
     * @param fieldName
     *            the title
     * @param fieldList
     *            the list
     * @return the found field, <code>null</code> otherwise.
     */
    private Field findField( String fieldName, List<Field> fieldList )
    {
        if ( StringUtils.isBlank( fieldName ) || ( fieldList == null ) || ( fieldList.size( ) == 0 ) )
        {
            return null;
        }

        for ( Field field : fieldList )
        {
            if ( fieldName.equals( field.getTitle( ) ) )
            {
                return field;
            }
        }

        return null;
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public String convertRecordFieldTitleToString( RecordField recordField, Locale locale, boolean displayFront )
    {
        if ( ( recordField != null ) && ( recordField.getField( ) != null ) )
        {
            return recordField.getValue( );
        }

        /*
         * if ( displayFront ) { if ( recordField != null && recordField.getField( ) != null && recordField.getField( ).getTitle( ) != null ) { if (
         * CONSTANT_X.equals( recordField.getField( ).getTitle( ) ) ) { return CONSTANT_X + DirectoryUtils.CONSTANT_EQUAL + recordField.getValue( ); } else if (
         * CONSTANT_Y.equals( recordField.getField( ).getTitle( ) ) ) { return CONSTANT_Y + DirectoryUtils.CONSTANT_EQUAL + recordField.getValue( ); } else if (
         * CONSTANT_PROVIDER.equals( recordField.getField( ).getTitle( ) ) ) { return DirectoryUtils.EMPTY_STRING; } } }
         */
        return super.convertRecordFieldTitleToString( recordField, locale, displayFront );
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public String convertRecordFieldValueToString( RecordField recordField, Locale locale, boolean bDisplayFront, boolean bExportDirectory )
    {
        if ( ( recordField != null ) && ( recordField.getField( ) != null ) && ( recordField.getField( ).getTitle( ) != null ) )
        {
            if ( CONSTANT_X.equals( recordField.getField( ).getTitle( ) ) || CONSTANT_Y.equals( recordField.getField( ).getTitle( ) )
                    || CONSTANT_PROVIDER.equals( recordField.getField( ).getTitle( ) ) || CONSTANT_ADDRESS.equals( recordField.getField( ).getTitle( ) ) )
            {
                return recordField.getValue( );
            }

            return StringUtils.EMPTY;
        }

        return super.convertRecordFieldValueToString( recordField, locale, bDisplayFront, bExportDirectory );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSortable( )
    {
        return true;
    }

    /**
     * Join that get only the address.
     */
    @Override
    public String getSQLJoin( )
    {
        return SQL_JOIN_DIRECTORY_RECORD_FIELD;
    }

    /**
     *
     * Returns the entry id and field title parameter
     */
    @Override
    public List<Object> getSQLParametersValues( )
    {
        List<Object> listParameters = new ArrayList<Object>( );
        listParameters.add( Integer.valueOf( getIdEntry( ) ) );
        listParameters.add( CONSTANT_ADDRESS );

        return listParameters;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getXml( Plugin plugin, Locale locale, StringBuffer strXml )
    {
        Map<String, String> model = new HashMap<String, String>( );
        model.put( ATTRIBUTE_ENTRY_ID, String.valueOf( this.getIdEntry( ) ) );
        model.put( ATTRIBUTE_ENTRY_ID_TYPE, String.valueOf( this.getEntryType( ).getIdType( ) ) );
        model.put( CONSTANT_SHOWXY, Boolean.toString( DirectoryService.getInstance( ).showXY( this ) ) );

        XmlUtil.beginElement( strXml, TAG_ENTRY, model );
        XmlUtil.addElementHtml( strXml, TAG_TITLE, this.getTitle( ) );
        XmlUtil.endElement( strXml, TAG_ENTRY );
    }

    public void getAdditionalAddressEntry( int nIdEntryType, IEntry entry, Locale locale, boolean update )
    {
        EntryType entryType = EntryTypeHome.findByPrimaryKey( nIdEntryType, DirectoryUtils.getPlugin( ) );
        IEntry entryAdditionalAddress = DirectoryUtils.createEntryByType( nIdEntryType, DirectoryUtils.getPlugin( ) );

        if ( entryAdditionalAddress != null )
        {
            entryAdditionalAddress.setEntryType( entryType );

            entryAdditionalAddress.setDirectory( entry.getDirectory( ) );

            entryAdditionalAddress.setTitle( I18nService.getLocalizedString( "directory.additional_address.title", locale ) );
            entryAdditionalAddress.setHelpMessage( "" );
            entryAdditionalAddress.setHelpMessageSearch( "" );
            entryAdditionalAddress.setComment( "" );

            if ( entryAdditionalAddress.getFields( ) == null )
            {
                ArrayList<Field> listFields = new ArrayList<Field>( );
                Field field = new Field( );
                field.setTitle( "additionalAddress" );
                field.setValue( "" );
                // FieldHome.create(field, pluginDirectory);
                listFields.add( field );
                entryAdditionalAddress.setFields( listFields );
            }

            entryAdditionalAddress.getFields( ).get( 0 ).setWidth( 50 );
            entryAdditionalAddress.getFields( ).get( 0 ).setMaxSizeEnter( 50 );
            entryAdditionalAddress.setMandatory( false );
            entryAdditionalAddress.setIndexed( false );
            entryAdditionalAddress.setIndexedAsTitle( false );
            entryAdditionalAddress.setIndexedAsSummary( false );
            entryAdditionalAddress.setShownInAdvancedSearch( false );
            entryAdditionalAddress.setShownInResultList( true );
            entryAdditionalAddress.setShownInResultRecord( true );
            entryAdditionalAddress.setShownInHistory( true );
            entryAdditionalAddress.setAutocompleteEntry( false );
            entryAdditionalAddress.setShownInExport( true );
            entryAdditionalAddress.setShownInCompleteness( false );

            if ( update )
            {
                EntryHome.update( entryAdditionalAddress, DirectoryUtils.getPlugin( ) );

                if ( entryAdditionalAddress.getFields( ) != null )
                {
                    for ( Field field : entryAdditionalAddress.getFields( ) )
                    {
                        field.setEntry( entryAdditionalAddress );
                        FieldHome.update( field, DirectoryUtils.getPlugin( ) );
                    }
                }
            }
            else
            {
                EntryHome.create( entryAdditionalAddress, DirectoryUtils.getPlugin( ) );

                if ( entryAdditionalAddress.getFields( ) != null )
                {
                    for ( Field field : entryAdditionalAddress.getFields( ) )
                    {
                        field.setEntry( entryAdditionalAddress );
                        FieldHome.create( field, DirectoryUtils.getPlugin( ) );
                    }
                }
            }

        }
    }
}

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

import fr.paris.lutece.plugins.directory.service.DirectoryPlugin;
import fr.paris.lutece.plugins.directory.service.directorysearch.DirectorySearchItem;
import fr.paris.lutece.plugins.directory.utils.DirectoryErrorException;
import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.PluginService;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * class EntryTypeDate
 *
 */
public class EntryTypeArray extends Entry
{
    private final String _template_create = "admin/plugins/directory/entrytypearray/create_entry_type_array.html";
    private final String _template_modify = "admin/plugins/directory/entrytypearray/modify_entry_type_array.html";
    private final String _template_html_code_form_entry = "admin/plugins/directory/entrytypearray/html_code_form_entry_type_array.html";
    private final String _template_html_code_form_search_entry = "admin/plugins/directory/entrytypearray/html_code_form_search_entry_type_array.html";
    private final String _template_html_code_entry_value = "admin/plugins/directory/entrytypearray/html_code_entry_value_type_array.html";
    private final String _template_html_front_code_form_entry = "skin/plugins/directory/entrytypearray/html_code_form_entry_type_array.html";
    private final String _template_html_front_code_form_search_entry = "skin/plugins/directory/entrytypearray/html_code_form_search_entry_type_array.html";
    private final String _template_html_front_code_entry_value = "skin/plugins/directory/entrytypearray/html_code_entry_value_type_array.html";

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTemplateHtmlFormEntry( boolean isDisplayFront )
    {
        if ( isDisplayFront )
        {
            return _template_html_front_code_form_entry;
        }

        return _template_html_code_form_entry;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTemplateHtmlRecordFieldValue( boolean isDisplayFront )
    {
        if ( isDisplayFront )
        {
            return _template_html_front_code_entry_value;
        }

        return _template_html_code_entry_value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTemplateHtmlFormSearchEntry( boolean isDisplayFront )
    {
        if ( isDisplayFront )
        {
            return _template_html_front_code_form_search_entry;
        }

        return _template_html_code_form_search_entry;
    }

    /**
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
        String strIndexed = request.getParameter( PARAMETER_INDEXED );
        String strIndexedAsTitle = request.getParameter( PARAMETER_INDEXED_AS_TITLE );
        String strIndexedAsSummary = request.getParameter( PARAMETER_INDEXED_AS_SUMMARY );
        String strShowInAdvancedSearch = request.getParameter( PARAMETER_SHOWN_IN_ADVANCED_SEARCH );
        String strShowInResultList = request.getParameter( PARAMETER_SHOWN_IN_RESULT_LIST );
        String strShowInResultRecord = request.getParameter( PARAMETER_SHOWN_IN_RESULT_RECORD );
        String strMultipleSearchFields = request.getParameter( PARAMETER_MULTIPLE_SEARCH_FIELDS );
        String strShowInHistory = request.getParameter( PARAMETER_SHOWN_IN_HISTORY );
        String strFieldError = DirectoryUtils.EMPTY_STRING;
        String strShowInExport = request.getParameter( PARAMETER_SHOWN_IN_EXPORT );
        String strShowInCompleteness = request.getParameter( PARAMETER_SHOWN_IN_COMPLETENESS );
        String strNumberRows = request.getParameter( PARAMETER_NUMBER_ROWS );
        String strNumberColumns = request.getParameter( PARAMETER_NUMBER_COLUMNS );

        if ( ( strTitle == null ) || strTitle.trim( ).equals( DirectoryUtils.EMPTY_STRING ) )
        {
            strFieldError = FIELD_TITLE;
        }
        else
            if ( StringUtils.isEmpty( strNumberRows ) )
            {
                strFieldError = FIELD_NUMBER_ROWS;
            }
            else
                if ( StringUtils.isEmpty( strNumberColumns ) )
                {
                    strFieldError = FIELD_NUMBER_COLUMNS;
                }

        if ( !strFieldError.equals( DirectoryUtils.EMPTY_STRING ) )
        {
            Object [ ] tabRequiredFields = {
                I18nService.getLocalizedString( strFieldError, locale )
            };

            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields, AdminMessage.TYPE_STOP );
        }
        else
            if ( !isValid( strNumberRows ) )
            {
                Object [ ] tabRequiredFields = {
                    I18nService.getLocalizedString( FIELD_NUMBER_ROWS, locale )
                };

                return AdminMessageService.getMessageUrl( request, MESSAGE_NUMERIC_FIELD, tabRequiredFields, AdminMessage.TYPE_STOP );
            }
            else
                if ( !isValid( strNumberColumns ) )
                {
                    Object [ ] tabRequiredFields = {
                        I18nService.getLocalizedString( FIELD_NUMBER_COLUMNS, locale )
                    };

                    return AdminMessageService.getMessageUrl( request, MESSAGE_NUMERIC_FIELD, tabRequiredFields, AdminMessage.TYPE_STOP );
                }

        int row = Integer.valueOf( strNumberRows );
        int column = Integer.valueOf( strNumberColumns );

        this.setTitle( strTitle );
        this.setHelpMessage( strHelpMessage );
        this.setHelpMessageSearch( strHelpMessageSearch );
        this.setComment( strComment );
        this.setMandatory( false );
        this.setIndexed( strIndexed != null );
        this.setIndexedAsTitle( strIndexedAsTitle != null );
        this.setIndexedAsSummary( strIndexedAsSummary != null );
        this.setShownInAdvancedSearch( strShowInAdvancedSearch != null );
        this.setShownInResultList( strShowInResultList != null );
        this.setShownInResultRecord( strShowInResultRecord != null );
        this.setMultipleSearchFields( strMultipleSearchFields != null );
        this.setShownInHistory( strShowInHistory != null );
        this.setShownInExport( strShowInExport != null );
        this.setShownInCompleteness( strShowInCompleteness != null );
        this.setNumberColumn( column );
        this.setNumberRow( row );

        ArrayList<Field> listFields = new ArrayList<Field>( );
        List<Field> fields = FieldHome.getFieldListByIdEntry( this.getIdEntry( ), PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME ) );

        for ( int i = 1; i <= ( row + 1 ); i++ )
        {
            for ( int j = 1; j <= ( column + 1 ); j++ )
            {
                Field existingFields = null;

                for ( Field f : fields )
                {
                    if ( f.getValue( ).equals( i + "_" + j ) )
                    {
                        existingFields = f;

                        break;
                    }
                }

                String strTitleRow = request.getParameter( "field_" + i + "_" + j );

                if ( ( i == 1 ) && ( j != 1 ) )
                {
                    Field field = new Field( );

                    if ( existingFields != null )
                    {
                        field = existingFields;
                    }

                    field.setEntry( this );
                    field.setValue( i + "_" + j );
                    field.setTitle( StringUtils.defaultString( strTitleRow ) );
                    listFields.add( field );
                }
                else
                    if ( ( i != 1 ) && ( j == 1 ) )
                    {
                        Field field = new Field( );

                        if ( existingFields != null )
                        {
                            field = existingFields;
                        }

                        field.setEntry( this );
                        field.setValue( i + "_" + j );
                        field.setTitle( StringUtils.defaultString( strTitleRow ) );
                        listFields.add( field );
                    }
                    else
                    {
                        Field field = new Field( );
                        field.setEntry( this );
                        field.setValue( i + "_" + j );
                        listFields.add( field );
                    }
            }
        }

        this.setFields( listFields );

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTemplateCreate( )
    {
        return _template_create;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTemplateModify( )
    {
        return _template_modify;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getRecordFieldData( Record record, HttpServletRequest request, boolean bTestDirectoryError, boolean bAddNewValue,
            List<RecordField> listRecordField, Locale locale ) throws DirectoryErrorException
    {
        List<Field> fields = FieldHome.getFieldListByIdEntry( this.getIdEntry( ), PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME ) );

        for ( int i = 1; i <= ( this.getNumberRow( ) + 1 ); i++ )
        {
            for ( int j = 1; j <= ( this.getNumberColumn( ) + 1 ); j++ )
            {
                Field existingFields = null;

                for ( Field f : fields )
                {
                    if ( f.getValue( ).equals( i + "_" + j ) )
                    {
                        existingFields = f;

                        break;
                    }
                }

                String strValueEntry = request.getParameter( this.getIdEntry( ) + "_field_" + i + "_" + j );

                if ( ( i != 1 ) && ( j != 1 ) )
                {
                    RecordField recordField = new RecordField( );
                    recordField.setEntry( this );
                    recordField.setValue( strValueEntry );
                    recordField.setField( existingFields );
                    listRecordField.add( recordField );
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getRecordFieldData( Record record, List<String> lstValue, boolean bTestDirectoryError, boolean bAddNewValue, List<RecordField> listRecordField,
            Locale locale ) throws DirectoryErrorException
    {
        // Do nothing
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String convertRecordFieldValueToString( RecordField recordField, Locale locale, boolean bDisplayFront, boolean bDisplayExport )
    {
        Field field = recordField.getField( );

        String [ ] fieldValues = field.getValue( ).split( "_" );

        int nRow = Integer.valueOf( fieldValues [0] );
        int nColumn = Integer.valueOf( fieldValues [1] );

        Field fieldRow = FieldHome.findByValue( field.getEntry( ).getIdEntry( ), nRow + "_1", PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME ) );
        Field fieldColumn = FieldHome.findByValue( field.getEntry( ).getIdEntry( ), "1_" + nColumn, PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME ) );

        return fieldRow.getTitle( ) + "/" + fieldColumn.getTitle( ) + " : " + recordField.getValue( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String convertRecordFieldTitleToString( RecordField recordField, Locale locale, boolean bDisplayFront )
    {
        return convertRecordFieldValueToString( recordField, locale, bDisplayFront, false );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addSearchCriteria( HashMap<String, Object> mapSearchItem, RecordField recordField )
    {
        if ( !recordField.getValue( ).equals( DirectoryUtils.EMPTY_STRING ) && ( recordField.getField( ) != null )
                && ( recordField.getField( ).getValue( ) != null ) && !recordField.getField( ).getValue( ).equals( DirectoryUtils.EMPTY_STRING ) )
        {
            List<Integer> listIdField = (List<Integer>) mapSearchItem.get( DirectorySearchItem.FIELD_ID_DIRECTORY_FIELD );

            if ( listIdField == null )
            {
                listIdField = new ArrayList<Integer>( );
            }

            listIdField.add( recordField.getField( ).getIdField( ) );
            mapSearchItem.put( DirectorySearchItem.FIELD_ID_DIRECTORY_FIELD, listIdField );

            if ( recordField.getValue( ) != null )
            {
                String strValue = new String( recordField.getValue( ) );

                if ( !strValue.trim( ).equals( DirectoryUtils.EMPTY_STRING ) )
                {
                    mapSearchItem.put( DirectorySearchItem.FIELD_CONTENTS, strValue );
                }
            }
        }
    }

    /**
     * Check if param is a valid integer
     * 
     * @param strValue
     *            the value to check
     * @return true if valid, false otherwise
     */
    private boolean isValid( String strValue )
    {
        if ( !StringUtils.isNumeric( strValue ) )
        {
            return false;
        }
        else
            if ( Integer.valueOf( strValue ) <= 0 )
            {
                return false;
            }

        return true;
    }
}

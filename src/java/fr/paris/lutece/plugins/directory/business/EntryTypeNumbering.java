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
package fr.paris.lutece.plugins.directory.business;

import fr.paris.lutece.plugins.directory.service.DirectoryPlugin;
import fr.paris.lutece.plugins.directory.service.DirectoryService;
import fr.paris.lutece.plugins.directory.service.record.IRecordService;
import fr.paris.lutece.plugins.directory.service.record.RecordService;
import fr.paris.lutece.plugins.directory.utils.DirectoryErrorException;
import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.html.HtmlTemplate;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * class EntryTypeNumbering
 *
 */
public class EntryTypeNumbering extends Entry
{
    private static final String PARAMETER_PREFIX = "prefix";

    // MARKS
    private static final String MARK_MAX_NUMBER = "max_number";

    // SQL
    private static final String SQL_ORDER_BY_RECORD_FIELD_VALUE = " ORDER BY CAST(drf.record_field_value AS DECIMAL) ";
    private final String _template_create = "admin/plugins/directory/entrytypenumbering/create_entry_type_numbering.html";
    private final String _template_modify = "admin/plugins/directory/entrytypenumbering/modify_entry_type_numbering.html";
    private final String _template_html_code_form_entry = "admin/plugins/directory/entrytypenumbering/html_code_form_entry_type_numbering.html";
    private final String _template_html_code_form_search_entry = "admin/plugins/directory/entrytypenumbering/html_code_form_search_entry_type_numbering.html";
    private final String _template_html_code_entry_value = "admin/plugins/directory/entrytypenumbering/html_code_entry_value_type_numbering.html";
    private final String _template_html_front_code_form_entry = "skin/plugins/directory/entrytypenumbering/html_code_form_entry_type_numbering.html";
    private final String _template_html_front_code_form_search_entry = "skin/plugins/directory/entrytypenumbering/html_code_form_search_entry_type_numbering.html";
    private final String _template_html_front_code_entry_value = "skin/plugins/directory/entrytypenumbering/html_code_entry_value_type_numbering.html";

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
        else
        {
            return _template_html_code_form_entry;
        }
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
    public String getHtmlFormEntry( Locale locale, boolean isDisplayFront )
    {
        if ( getTemplateHtmlFormEntry( isDisplayFront ) != null )
        {
            Map<String, Object> model = new HashMap<String, Object>( );
            model.put( MARK_ENTRY, this );
            model.put( MARK_LOCALE, locale );
            model.put( MARK_MAX_NUMBER, DirectoryService.getInstance( ).getMaxNumber( this ) );

            HtmlTemplate template = AppTemplateService.getTemplate( getTemplateHtmlFormEntry( isDisplayFront ), locale, model );

            return template.getHtml( );
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEntryData( HttpServletRequest request, Locale locale )
    {
        String strTitle = request.getParameter( PARAMETER_TITLE );
        String strHelpMessageSearch = ( request.getParameter( PARAMETER_HELP_MESSAGE_SEARCH ) != null ) ? request.getParameter( PARAMETER_HELP_MESSAGE_SEARCH )
                .trim( ) : null;
        String strComment = request.getParameter( PARAMETER_COMMENT );

        String strIndexed = request.getParameter( PARAMETER_INDEXED );
        String strIndexedAsTitle = request.getParameter( PARAMETER_INDEXED_AS_TITLE );
        String strIndexedAsSummary = request.getParameter( PARAMETER_INDEXED_AS_SUMMARY );
        String strShowInAdvancedSearch = request.getParameter( PARAMETER_SHOWN_IN_ADVANCED_SEARCH );
        String strShowInResultList = request.getParameter( PARAMETER_SHOWN_IN_RESULT_LIST );
        String strShowInResultRecord = request.getParameter( PARAMETER_SHOWN_IN_RESULT_RECORD );
        String strShowInHistory = request.getParameter( PARAMETER_SHOWN_IN_HISTORY );
        String strShowInExport = request.getParameter( PARAMETER_SHOWN_IN_EXPORT );
        String strPrefix = request.getParameter( PARAMETER_PREFIX );

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

        this.setTitle( strTitle );
        this.setHelpMessageSearch( strHelpMessageSearch );
        this.setComment( strComment );

        if ( this.getFields( ) == null )
        {
            ArrayList<Field> listFields = new ArrayList<Field>( );
            Field field = new Field( );
            listFields.add( field );
            this.setFields( listFields );
        }

        this.getFields( ).get( 0 ).setValue( "1" );
        this.getFields( ).get( 0 ).setTitle( StringUtils.isNotEmpty( strPrefix ) ? strPrefix : StringUtils.EMPTY );
        this.setIndexed( strIndexed != null );
        this.setIndexedAsTitle( strIndexedAsTitle != null );
        this.setIndexedAsSummary( strIndexedAsSummary != null );
        this.setShownInAdvancedSearch( strShowInAdvancedSearch != null );
        this.setShownInResultList( strShowInResultList != null );
        this.setShownInResultRecord( strShowInResultRecord != null );
        this.setShownInHistory( strShowInHistory != null );
        this.setShownInExport( strShowInExport != null );
        // This entry cannot be shown in completeness
        this.setShownInCompleteness( false );

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
    public void getRecordFieldData( Record record, List<String> lstValue, boolean bTestDirectoryError, boolean bAddNewValue, List<RecordField> listRecordField,
            Locale locale ) throws DirectoryErrorException
    {
        /*
         * This method is called from several operations : 1) Creating a record 2) Updating a record 3) Search from BO 4) Search from FO
         */
        Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
        RecordField recordField = new RecordField( );
        recordField.setEntry( this );

        Record recordOld = null;

        if ( record != null )
        {
            /*
             * CASES 1 AND 2 : (The record is not null for cases 1) and 2))
             */
            IRecordService recordService = SpringContextService.getBean( RecordService.BEAN_SERVICE );
            recordOld = recordService.findByPrimaryKey( record.getIdRecord( ), pluginDirectory );
        }

        String strValueEntry = ( ( lstValue != null ) && ( lstValue.size( ) > 0 ) ) ? lstValue.get( 0 ) : null;

        if ( recordOld != null )
        {
            /*
             * CASE 2 : (The record old is not null for case 2))
             */
            recordField.setValue( strValueEntry );
        }
        else
            if ( record == null )
            {
                /*
                 * CASES 3 AND 4 : (The record is null for cases 3) and 4))
                 */
                if ( bTestDirectoryError && this.isMandatory( ) && StringUtils.isBlank( strValueEntry ) )
                {
                    throw new DirectoryErrorException( this.getTitle( ) );
                }

                if ( StringUtils.isNotBlank( strValueEntry ) )
                {
                    recordField.setValue( strValueEntry );
                }
            }
            else
            {
                /*
                 * CASE 1 : (Create the record, thus fetch the max number)
                 */
                int numbering = DirectoryService.getInstance( ).getMaxNumber( this );
                this.getFields( ).get( 0 ).setValue( String.valueOf( numbering + 1 ) );
                FieldHome.update( this.getFields( ).get( 0 ), pluginDirectory );

                recordField.setValue( String.valueOf( numbering ) );
            }

        if ( recordField.getValue( ) != null )
        {
            /*
             * In cases 3 and 4, if the strValueEntry is null, then that means that the user did not use the search function and the user only wants to display
             * every records or did not want to filter his/her search for this entry.
             */
            listRecordField.add( recordField );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getImportRecordFieldData( Record record, String strImportValue, boolean bTestDirectoryError, List<RecordField> listRecordField, Locale locale )
            throws DirectoryErrorException
    {
        int numbering = DirectoryService.getInstance( ).getNumber( this, strImportValue );

        if ( numbering != DirectoryUtils.CONSTANT_ID_NULL )
        {
            RecordField recordField = new RecordField( );
            recordField.setEntry( this );
            recordField.setValue( Integer.toString( numbering ) );
            listRecordField.add( recordField );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String convertRecordFieldValueToString( RecordField recordField, Locale locale, boolean bDisplayFront, boolean bExportDirectory )
    {
        Field field = null;

        if ( recordField.getField( ) == null )
        {
            if ( getFields( ) == null )
            {
                Plugin plugin = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
                setFields( FieldHome.getFieldListByIdEntry( getIdEntry( ), plugin ) );
            }

            if ( ( getFields( ) != null ) && ( getFields( ).size( ) > 0 ) )
            {
                field = getFields( ).get( 0 );
            }
        }
        else
        {
            field = recordField.getField( );
        }

        if ( ( field != null ) && StringUtils.isNotBlank( field.getTitle( ) ) )
        {
            return field.getTitle( ) + recordField.getValue( );
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
     * {@inheritDoc}
     */
    @Override
    public String getSQLOrderBy( )
    {
        // Special query in order to sort numerically and not alphabetically (thus avoiding list like 1, 10, 11, 2, ... instead of 1, 2, ..., 10, 11)
        return SQL_ORDER_BY_RECORD_FIELD_VALUE;
    }
}

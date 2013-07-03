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

import fr.paris.lutece.plugins.directory.service.DirectoryPlugin;
import fr.paris.lutece.plugins.directory.utils.DirectoryErrorException;
import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;


/**
 * 
 * class EntryTypeSQL
 * 
 */
public class EntryTypeSQL extends Entry
{
    private final String _template_create = "admin/plugins/directory/entrytypeSQL/create_entry_type_SQL.html";
    private final String _template_modify = "admin/plugins/directory/entrytypeSQL/modify_entry_type_SQL.html";
    private final String _template_html_code_form_entry = "admin/plugins/directory/entrytypeSQL/html_code_form_entry_type_SQL.html";
    private final String _template_html_code_form_search_entry = "admin/plugins/directory/entrytypeSQL/html_code_form_search_entry_type_SQL.html";
    private final String _template_html_code_entry_value = "admin/plugins/directory/entrytypeSQL/html_code_entry_value_type_SQL.html";
    private final String _template_html_front_code_form_entry = "skin/plugins/directory/entrytypeSQL/html_code_form_entry_type_SQL.html";
    private final String _template_html_front_code_form_search_entry = "skin/plugins/directory/entrytypeSQL/html_code_form_search_entry_type_SQL.html";
    private final String _template_html_front_code_entry_value = "skin/plugins/directory/entrytypeSQL/html_code_entry_value_type_SQL.html";

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

    @Override
    public String getTemplateHtmlRecordFieldValue( boolean isDisplayFront )
    {
        if ( isDisplayFront )
        {
            return _template_html_front_code_entry_value;
        }
        else
        {
            return _template_html_code_entry_value;
        }
    }

    @Override
    public String getTemplateHtmlFormSearchEntry( boolean isDisplayFront )
    {
        if ( isDisplayFront )
        {
            return _template_html_front_code_form_search_entry;
        }
        else
        {
            return _template_html_code_form_search_entry;
        }
    }

    @Override
    public String getEntryData( HttpServletRequest request, Locale locale )
    {
        String strTitle = request.getParameter( PARAMETER_TITLE );
        String strHelpMessage = ( request.getParameter( PARAMETER_HELP_MESSAGE ) != null ) ? request.getParameter(
                PARAMETER_HELP_MESSAGE ).trim( ) : null;
        String strHelpMessageSearch = ( request.getParameter( PARAMETER_HELP_MESSAGE_SEARCH ) != null ) ? request
                .getParameter( PARAMETER_HELP_MESSAGE_SEARCH ).trim( ) : null;
        String strComment = request.getParameter( PARAMETER_COMMENT );
        String strRequestSQL = request.getParameter( PARAMETER_REQUEST_SQL );
        String strMandatory = request.getParameter( PARAMETER_MANDATORY );
        String strIndexed = request.getParameter( PARAMETER_INDEXED );
        String strDocumentTitle = request.getParameter( PARAMETER_INDEXED_AS_TITLE );
        String strDocumentSummary = request.getParameter( PARAMETER_INDEXED_AS_SUMMARY );
        String strShowInAdvancedSearch = request.getParameter( PARAMETER_SHOWN_IN_ADVANCED_SEARCH );
        String strShowInResultList = request.getParameter( PARAMETER_SHOWN_IN_RESULT_LIST );
        String strShowInResultRecord = request.getParameter( PARAMETER_SHOWN_IN_RESULT_RECORD );
        String strShowInHistory = request.getParameter( PARAMETER_SHOWN_IN_HISTORY );
        String strShowInExport = request.getParameter( PARAMETER_SHOWN_IN_EXPORT );
        String strShowInCompleteness = request.getParameter( PARAMETER_SHOWN_IN_COMPLETENESS );

        String strFieldError = DirectoryUtils.EMPTY_STRING;

        if ( ( strTitle == null ) || strTitle.trim( ).equals( DirectoryUtils.EMPTY_STRING ) )
        {
            strFieldError = FIELD_TITLE;
        }
        else if ( ( strRequestSQL == null ) || strRequestSQL.trim( ).equals( DirectoryUtils.EMPTY_STRING ) )
        {
            strFieldError = FIELD_REQUEST_SQL;
        }

        if ( !strFieldError.equals( DirectoryUtils.EMPTY_STRING ) )
        {
            Object[] tabRequiredFields = { I18nService.getLocalizedString( strFieldError, locale ) };

            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields,
                    AdminMessage.TYPE_STOP );
        }

        if ( !strFieldError.equals( DirectoryUtils.EMPTY_STRING ) )
        {
            Object[] tabRequiredFields = { I18nService.getLocalizedString( strFieldError, locale ) };

            return AdminMessageService.getMessageUrl( request, MESSAGE_NUMERIC_FIELD, tabRequiredFields,
                    AdminMessage.TYPE_STOP );
        }

        this.setTitle( strTitle );
        this.setHelpMessage( strHelpMessage );
        this.setHelpMessageSearch( strHelpMessageSearch );
        this.setComment( strComment );
        this.setRequestSQL( strRequestSQL );
        this.setMandatory( strMandatory != null );
        this.setIndexed( strIndexed != null );
        this.setIndexedAsTitle( strDocumentTitle != null );
        this.setIndexedAsSummary( strDocumentSummary != null );
        this.setShownInAdvancedSearch( strShowInAdvancedSearch != null );
        this.setShownInResultList( strShowInResultList != null );
        this.setShownInResultRecord( strShowInResultRecord != null );
        this.setShownInHistory( strShowInHistory != null );
        this.setShownInExport( strShowInExport != null );
        this.setShownInCompleteness( strShowInCompleteness != null );

        return null;
    }

    @Override
    public String getTemplateCreate( )
    {
        return _template_create;
    }

    @Override
    public String getTemplateModify( )
    {
        return _template_modify;
    }

    @Override
    public Paginator getPaginator( int nItemPerPage, String strBaseUrl, String strPageIndexParameterName,
            String strPageIndex )
    {
        return new Paginator( this.getFields( ).get( 0 ).getRegularExpressionList( ), nItemPerPage, strBaseUrl,
                strPageIndexParameterName, strPageIndex );
    }

    @Override
    public void getRecordFieldData( Record record, List<String> lstValue, boolean bTestDirectoryError,
            boolean bAddNewValue, List<RecordField> listRecordField, Locale locale ) throws DirectoryErrorException
    {
        Plugin plugin = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );

        String strValueEntry = ( ( lstValue != null ) && ( lstValue.size( ) > 0 ) ) ? lstValue.get( 0 ) : null;
        RecordField recordField = new RecordField( );
        recordField.setEntry( this );

        if ( ( record != null ) && bAddNewValue )
        {
            RecordFieldFilter recordFieldFilter = new RecordFieldFilter( );
            recordFieldFilter.setIdDirectory( record.getDirectory( ).getIdDirectory( ) );
            recordFieldFilter.setIdEntry( this.getIdEntry( ) );
            recordFieldFilter.setIdRecord( record.getIdRecord( ) );

            List<RecordField> recordFieldList = RecordFieldHome.getRecordFieldList( recordFieldFilter, plugin );

            if ( ( recordFieldList != null ) && !recordFieldList.isEmpty( )
                    && !recordFieldList.get( 0 ).getValue( ).equals( "" ) )
            {
                strValueEntry = recordFieldList.get( 0 ).getValue( ) + ", " + strValueEntry;
            }
        }

        if ( strValueEntry != null )
        {
            if ( bTestDirectoryError && this.isMandatory( ) && strValueEntry.equals( DirectoryUtils.EMPTY_STRING ) )
            {
                throw new DirectoryErrorException( this.getTitle( ) );
            }

            recordField.setValue( strValueEntry );
        }

        listRecordField.add( recordField );
    }

    /**
     * Return fields from a SQL query
     * @return A list of fields
     */
    public ReferenceList getSqlQueryFields( )
    {
        ReferenceList list = new ReferenceList( );
        String strSQL = this.getRequestSQL( );
        DAOUtil daoUtil = new DAOUtil( strSQL );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            list.addItem( daoUtil.getInt( 1 ), daoUtil.getString( 2 ) );
        }

        daoUtil.free( );

        return list;
    }

    public String convertRecordFieldValueToString( RecordField recordField, Locale locale, boolean bDisplayFront,
            boolean bDisplayExport )
    {
        Plugin plugin = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );

        if ( recordField.getValue( ) != null )
        {
            if ( bDisplayExport )
            {
                return recordField.getValue( );
            }
            IEntry entry = EntryHome.findByPrimaryKey( this.getIdEntry( ), plugin );
            DAOUtil daoUtil = new DAOUtil( entry.getRequestSQL( ) );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                if ( daoUtil.getString( 1 ).equals( recordField.getValue( ) ) )
                {
                    return daoUtil.getString( 2 );
                }
            }
        }

        return DirectoryUtils.EMPTY_STRING;
    }

    public String convertRecordFieldTitleToString( RecordField recordField, Locale locale, boolean bDisplayFront )
    {
        return convertRecordFieldValueToString( recordField, locale, bDisplayFront, false );
    }
}

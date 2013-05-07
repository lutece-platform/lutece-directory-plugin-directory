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

import fr.paris.lutece.plugins.directory.service.directorysearch.DirectorySearchItem;
import fr.paris.lutece.plugins.directory.utils.DirectoryErrorException;
import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.date.DateUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;


/**
 *
 * class EntryTypeDate
 *
 */
public class EntryTypeDate extends Entry
{
    private static final String _message_illogical_date = "directory.message.illogical_date";
    private static final String _message_illogical_date_begin = "directory.message.illogical_date_begin";
    private static final String _message_illogical_date_end = "directory.message.illogical_date_end";
    private static final String _message_illogical_date_end_before_date_begin = "directory.message.illogical_date_end_before_date_begin";
    private static final String _message_mandatory_date_begin_date_end = "directory.message.mandatory_date_begin_date_end";
    private static final int _nIdFieldDateBegin = -2;
    private static final int _nIdFieldDateEnd = -3;
    private final String _template_create = "admin/plugins/directory/entrytypedate/create_entry_type_date.html";
    private final String _template_modify = "admin/plugins/directory/entrytypedate/modify_entry_type_date.html";
    private final String _template_html_code_form_entry = "admin/plugins/directory/entrytypedate/html_code_form_entry_type_date.html";
    private final String _template_html_code_form_search_entry = "admin/plugins/directory/entrytypedate/html_code_form_search_entry_type_date.html";
    private final String _template_html_code_entry_value = "admin/plugins/directory/entrytypedate/html_code_entry_value_type_date.html";
    private final String _template_html_front_code_form_entry = "skin/plugins/directory/entrytypedate/html_code_form_entry_type_date.html";
    private final String _template_html_front_code_form_search_entry = "skin/plugins/directory/entrytypedate/html_code_form_search_entry_type_date.html";
    private final String _template_html_front_code_entry_value = "skin/plugins/directory/entrytypedate/html_code_entry_value_type_date.html";

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
        String strHelpMessage = ( request.getParameter( PARAMETER_HELP_MESSAGE ) != null )
            ? request.getParameter( PARAMETER_HELP_MESSAGE ).trim(  ) : null;
        String strHelpMessageSearch = ( request.getParameter( PARAMETER_HELP_MESSAGE_SEARCH ) != null )
            ? request.getParameter( PARAMETER_HELP_MESSAGE_SEARCH ).trim(  ) : null;
        String strComment = request.getParameter( PARAMETER_COMMENT );
        String strValue = request.getParameter( PARAMETER_VALUE );
        String strMandatory = request.getParameter( PARAMETER_MANDATORY );
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

        if ( ( strTitle == null ) || strTitle.trim(  ).equals( DirectoryUtils.EMPTY_STRING ) )
        {
            strFieldError = FIELD_TITLE;
        }

        if ( !strFieldError.equals( DirectoryUtils.EMPTY_STRING ) )
        {
            Object[] tabRequiredFields = { I18nService.getLocalizedString( strFieldError, locale ) };

            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields,
                AdminMessage.TYPE_STOP );
        }

        Date dDateValue = null;

        if ( ( strValue != null ) && !strValue.equals( DirectoryUtils.EMPTY_STRING ) )
        {
            dDateValue = DateUtil.formatDate( strValue, locale );

            if ( dDateValue == null )
            {
                return AdminMessageService.getMessageUrl( request, _message_illogical_date, AdminMessage.TYPE_STOP );
            }
        }

        this.setTitle( strTitle );
        this.setHelpMessage( strHelpMessage );
        this.setHelpMessageSearch( strHelpMessageSearch );
        this.setComment( strComment );

        if ( this.getFields(  ) == null )
        {
            ArrayList<Field> listFields = new ArrayList<Field>(  );
            Field field = new Field(  );
            listFields.add( field );
            this.setFields( listFields );
        }

        this.getFields(  ).get( 0 ).setValueTypeDate( dDateValue );
        this.setMandatory( strMandatory != null );
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

        return null;
    }

    @Override
    public String getTemplateCreate(  )
    {
        return _template_create;
    }

    @Override
    public String getTemplateModify(  )
    {
        return _template_modify;
    }

    @Override
    public void getRecordFieldData( Record record, HttpServletRequest request, boolean bTestDirectoryError,
        boolean bAddNewValue, List<RecordField> listRecordField, Locale locale )
        throws DirectoryErrorException
    {
        if ( !this.isMultipleSearchFields(  ) || ( request.getParameterValues( PARAMETER_SEARCH_ACTION ) == null ) )
        {
            super.getRecordFieldData( record, request, bTestDirectoryError, bAddNewValue, listRecordField, locale );
        }
        else
        {
            List<String> lstValue = new ArrayList<String>(  );
            String strDateBegin = request.getParameter( PARAMETER_DATE_BEGIN + "_" + this.getIdEntry(  ) );
            String strDateEnd = request.getParameter( PARAMETER_DATE_END + "_" + this.getIdEntry(  ) );
            lstValue.add( strDateBegin );
            lstValue.add( strDateEnd );
            getRecordFieldData( record, lstValue, bTestDirectoryError, bAddNewValue, listRecordField, locale );
        }
    }

    @Override
    public void getRecordFieldData( Record record, List<String> lstValue, boolean bTestDirectoryError,
        boolean bAddNewValue, List<RecordField> listRecordField, Locale locale )
        throws DirectoryErrorException
    {
        RecordField recordField;

        if ( this.isMultipleSearchFields(  ) && ( ( lstValue != null ) && ( lstValue.size(  ) > 1 ) ) )
        {
            String strDateBegin = lstValue.get( 0 );
            String strDateEnd = lstValue.get( 1 );
            Date tDateBegin = null;
            Date tDateEnd = null;
            String strError = null;

            if ( ( !strDateBegin.equals( DirectoryUtils.EMPTY_STRING ) &&
                    strDateEnd.equals( DirectoryUtils.EMPTY_STRING ) ) ||
                    ( !strDateEnd.equals( DirectoryUtils.EMPTY_STRING ) &&
                    strDateBegin.equals( DirectoryUtils.EMPTY_STRING ) ) )
            {
                strError = _message_mandatory_date_begin_date_end;
            }

            if ( ( strError == null ) && !strDateBegin.equals( DirectoryUtils.EMPTY_STRING ) )
            {
                tDateBegin = DateUtil.formatDate( strDateBegin, locale );

                if ( tDateBegin == null )
                {
                    strError = _message_illogical_date_begin;
                }
            }

            if ( ( strError == null ) && !strDateEnd.equals( DirectoryUtils.EMPTY_STRING ) )
            {
                tDateEnd = DateUtil.formatDate( strDateEnd, locale );

                if ( tDateEnd == null )
                {
                    strError = _message_illogical_date_end;
                }
            }

            if ( ( strError == null ) && ( tDateBegin != null ) && ( tDateEnd != null ) &&
                    tDateEnd.before( tDateBegin ) )
            {
                strError = _message_illogical_date_end_before_date_begin;
            }

            if ( strError != null )
            {
                strError = I18nService.getLocalizedString( strError, locale );

                throw new DirectoryErrorException( this.getTitle(  ), strError );
            }

            if ( tDateBegin != null )
            {
                recordField = new RecordField(  );

                Field mockFieldDateBegin = new Field(  );
                mockFieldDateBegin.setIdField( _nIdFieldDateBegin );
                recordField.setValue( ( DirectoryUtils.EMPTY_STRING + tDateBegin.getTime(  ) ) );
                recordField.setField( mockFieldDateBegin );
                recordField.setEntry( this );
                listRecordField.add( recordField );
            }

            if ( tDateEnd != null )
            {
                recordField = new RecordField(  );

                Field mockFieldDateEnd = new Field(  );
                mockFieldDateEnd.setIdField( _nIdFieldDateEnd );
                recordField.setValue( ( DirectoryUtils.EMPTY_STRING + tDateEnd.getTime(  ) ) );
                recordField.setField( mockFieldDateEnd );
                recordField.setEntry( this );
                listRecordField.add( recordField );
            }
        }
        else
        {
            recordField = new RecordField(  );
            recordField.setEntry( this );

            String strValueEntry = ( ( lstValue != null ) && ( lstValue.size(  ) > 0 ) ) ? lstValue.get( 0 ) : null;

            if ( strValueEntry != null )
            {
                if ( bTestDirectoryError && this.isMandatory(  ) &&
                        strValueEntry.equals( DirectoryUtils.EMPTY_STRING ) )
                {
                    throw new DirectoryErrorException( this.getTitle(  ) );
                }

                if ( !strValueEntry.equals( DirectoryUtils.EMPTY_STRING ) )
                {
                    Date tDateValue = DateUtil.formatDate( strValueEntry, locale );

                    if ( tDateValue == null )
                    {
                        String strError = I18nService.getLocalizedString( _message_illogical_date, locale );

                        throw new DirectoryErrorException( this.getTitle(  ), strError );
                    }

                    recordField.setValue( ( DirectoryUtils.EMPTY_STRING + tDateValue.getTime(  ) ) );
                }
            }

            listRecordField.add( recordField );
        }
    }

    @Override
    public String convertRecordFieldValueToString( RecordField recordField, Locale locale, boolean bDisplayFront,
        boolean bDisplayExport )
    {
        if ( recordField.getValue(  ) != null )
        {
            try
            {
                Long lTime = Long.parseLong( recordField.getValue(  ) );
                Date date = new Date( lTime );

                return DateUtil.getDateString( date, locale );
            }
            catch ( Exception e )
            {
                AppLogService.error( e );
            }
        }

        return DirectoryUtils.EMPTY_STRING;
    }

    public String convertRecordFieldTitleToString( RecordField recordField, Locale locale, boolean bDisplayFront )
    {
        return convertRecordFieldValueToString( recordField, locale, bDisplayFront, false );
    }

    @Override
    public void addSearchCriteria( HashMap<String, Object> mapSearchItem, RecordField recordField )
    {
        if ( recordField.getValue(  ) != null )
        {
            try
            {
                Long lTime = Long.parseLong( new String( recordField.getValue(  ) ) );
                Date date = new Date( lTime );

                if ( recordField.getField(  ) != null )
                {
                    if ( recordField.getField(  ).getIdField(  ) == _nIdFieldDateBegin )
                    {
                        mapSearchItem.put( DirectorySearchItem.FIELD_DATE_BEGIN, date );
                    }
                    else if ( recordField.getField(  ).getIdField(  ) == _nIdFieldDateEnd )
                    {
                        mapSearchItem.put( DirectorySearchItem.FIELD_DATE_END, date );
                    }
                }
                else
                {
                    mapSearchItem.put( DirectorySearchItem.FIELD_DATE, date );
                }
            }
            catch ( Exception e )
            {
                AppLogService.error( e );
            }
        }
    }

    /**
     *
     * {@inheritDoc}
     */
    public boolean isSortable(  )
    {
        return true;
    }
}

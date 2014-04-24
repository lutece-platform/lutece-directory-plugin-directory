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

import fr.paris.lutece.plugins.directory.service.directorysearch.DirectorySearchItem;
import fr.paris.lutece.plugins.directory.utils.DirectoryErrorException;
import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.web.util.LocalizedPaginator;
import fr.paris.lutece.util.html.Paginator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;


/**
 *
 * class EntryTypeCheckBox
 *
 */
public class EntryTypeCheckBox extends Entry
{
    private final String _template_create = "admin/plugins/directory/entrytypecheckbox/create_entry_type_check_box.html";
    private final String _template_modify = "admin/plugins/directory/entrytypecheckbox/modify_entry_type_check_box.html";
    private final String _template_html_code_form_entry = "admin/plugins/directory/entrytypecheckbox/html_code_form_entry_type_check_box.html";
    private final String _template_html_code_entry_value = "admin/plugins/directory/entrytypecheckbox/html_code_entry_value_type_check_box.html";
    private final String _template_html_code_form_search_entry = "admin/plugins/directory/entrytypecheckbox/html_code_form_search_entry_type_check_box.html";
    private final String _template_html_front_code_form_entry = "skin/plugins/directory/entrytypecheckbox/html_code_form_entry_type_check_box.html";
    private final String _template_html_front_code_entry_value = "skin/plugins/directory/entrytypecheckbox/html_code_entry_value_type_check_box.html";
    private final String _template_html_front_code_form_search_entry = "skin/plugins/directory/entrytypecheckbox/html_code_form_search_entry_type_check_box.html";

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
    public String getEntryData( HttpServletRequest request, Locale locale )
    {
        String strTitle = request.getParameter( PARAMETER_TITLE );
        String strHelpMessage = ( request.getParameter( PARAMETER_HELP_MESSAGE ) != null )
            ? request.getParameter( PARAMETER_HELP_MESSAGE ).trim(  ) : null;
        String strHelpMessageSearch = ( request.getParameter( PARAMETER_HELP_MESSAGE_SEARCH ) != null )
            ? request.getParameter( PARAMETER_HELP_MESSAGE_SEARCH ).trim(  ) : null;
        String strComment = request.getParameter( PARAMETER_COMMENT );
        String strMandatory = request.getParameter( PARAMETER_MANDATORY );
        String strIndexed = request.getParameter( PARAMETER_INDEXED );
        String strIndexedAsTitle = request.getParameter( PARAMETER_INDEXED_AS_TITLE );
        String strIndexedAsSummary = request.getParameter( PARAMETER_INDEXED_AS_SUMMARY );
        String strShowInAdvancedSearch = request.getParameter( PARAMETER_SHOWN_IN_ADVANCED_SEARCH );
        String strShowInResultList = request.getParameter( PARAMETER_SHOWN_IN_RESULT_LIST );
        String strShowInResultRecord = request.getParameter( PARAMETER_SHOWN_IN_RESULT_RECORD );
        String strShowInHistory = request.getParameter( PARAMETER_SHOWN_IN_HISTORY );
        String strFieldInLine = request.getParameter( PARAMETER_FIELD_IN_LINE );
        String strShowInExport = request.getParameter( PARAMETER_SHOWN_IN_EXPORT );
        String strShowInCompleteness = request.getParameter( PARAMETER_SHOWN_IN_COMPLETENESS );
        int nFieldInLine = DirectoryUtils.convertStringToInt( strFieldInLine );

        String strFieldError = DirectoryUtils.EMPTY_STRING;

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

        // for don't update fields listFields=null
        this.setFields( null );

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
        this.setFieldInLine( nFieldInLine == 1 );
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
    public Paginator getPaginator( int nItemPerPage, String strBaseUrl, String strPageIndexParameterName,
        String strPageIndex )
    {
        return new Paginator( this.getFields(  ), nItemPerPage, strBaseUrl, strPageIndexParameterName, strPageIndex );
    }

    @Override
    public void getRecordFieldData( Record record, HttpServletRequest request, boolean bTestDirectoryError,
        boolean bAddNewValue, List<RecordField> listRecordField, Locale locale )
        throws DirectoryErrorException
    {
        List<String> lstValue = new ArrayList<String>(  );
        String[] strTabIdField = request.getParameterValues( DirectoryUtils.EMPTY_STRING + this.getIdEntry(  ) );

        if ( strTabIdField != null )
        {
            for ( String strIdField : strTabIdField )
            {
                lstValue.add( strIdField );
            }
        }

        getRecordFieldData( record, lstValue, bTestDirectoryError, bAddNewValue, listRecordField, locale );
    }

    @Override
    public void getImportRecordFieldData( Record record, String strImportValue, boolean bTestDirectoryError,
        List<RecordField> listRecordField, Locale locale )
        throws DirectoryErrorException
    {
        String strMultipleValueSeparator = AppPropertiesService.getProperty( PROPERTY_IMPORT_MULTIPLE_VALUE_DELIMITER );
        List<String> lstValue = new ArrayList<String>(  );
        String[] tabStrValue = strImportValue.split( strMultipleValueSeparator );
        Field field;

        for ( String strValue : tabStrValue )
        {
            field = DirectoryUtils.findFieldByValueInTheList( strValue, this.getFields(  ) );

            if ( field != null )
            {
                lstValue.add( Integer.toString( field.getIdField(  ) ) );
            }
        }

        getRecordFieldData( record, lstValue, bTestDirectoryError, false, listRecordField, locale );
    }

    @Override
    public void getRecordFieldData( Record record, List<String> lstValue, boolean bTestDirectoryError,
        boolean bAddNewValue, List<RecordField> listRecordField, Locale locale )
        throws DirectoryErrorException
    {
        List<Field> listFieldInResponse = new ArrayList<Field>(  );
        int nIdField = -1;
        Field field = null;
        RecordField recordField;

        if ( lstValue != null )
        {
            for ( String strIdField : lstValue )
            {
                nIdField = DirectoryUtils.convertStringToInt( strIdField );
                field = DirectoryUtils.findFieldByIdInTheList( nIdField, this.getFields(  ) );

                if ( field != null )
                {
                    listFieldInResponse.add( field );
                }
            }
        }

        if ( bTestDirectoryError && this.isMandatory(  ) )
        {
            boolean bAllFieldEmpty = true;

            for ( Field fieldInResponse : listFieldInResponse )
            {
                if ( !fieldInResponse.getValue(  ).equals( DirectoryUtils.EMPTY_STRING ) )
                {
                    bAllFieldEmpty = false;
                }
            }

            if ( bAllFieldEmpty )
            {
                throw new DirectoryErrorException( this.getTitle(  ) );
            }
        }

        if ( listFieldInResponse.size(  ) == 0 )
        {
            recordField = new RecordField(  );
            recordField.setEntry( this );
            listRecordField.add( recordField );
        }
        else
        {
            for ( Field fieldInResponse : listFieldInResponse )
            {
                recordField = new RecordField(  );
                recordField.setEntry( this );
                recordField.setValue( fieldInResponse.getValue(  ) );
                recordField.setField( fieldInResponse );
                listRecordField.add( recordField );
            }
        }
    }

    @Override
    public void addSearchCriteria( HashMap<String, Object> mapSearchItem, RecordField recordField )
    {
        if ( ( recordField.getField(  ) != null ) && ( recordField.getField(  ).getValue(  ) != null ) &&
                !recordField.getField(  ).getValue(  ).equals( DirectoryUtils.EMPTY_STRING ) )
        {
            List<Integer> listIdField = (List<Integer>) mapSearchItem.get( DirectorySearchItem.FIELD_ID_DIRECTORY_FIELD );

            if ( listIdField == null )
            {
                listIdField = new ArrayList<Integer>(  );
            }

            listIdField.add( recordField.getField(  ).getIdField(  ) );
            mapSearchItem.put( DirectorySearchItem.FIELD_ID_DIRECTORY_FIELD, listIdField );
        }
    }

    /**
     *
     * {@inheritDoc}
     */
    public boolean isSortable(  )
    {
        return false;
    }

    @Override
    public LocalizedPaginator getPaginator( int nItemPerPage, String strBaseUrl, String strPageIndexParameterName,
        String strPageIndex, Locale locale )
    {
        return new LocalizedPaginator( this.getFields(  ), nItemPerPage, strBaseUrl, strPageIndexParameterName,
            strPageIndex, locale );
    }
}

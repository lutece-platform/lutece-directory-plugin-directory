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
import fr.paris.lutece.portal.web.util.LocalizedPaginator;
import fr.paris.lutece.util.html.Paginator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;


/**
 *
 * class EntryTypeSelect
 *
 */
public class EntryTypeSelect extends Entry
{
    private static final String SQL_JOIN_DIRECTORY_RECORD_FIELD = " LEFT JOIN directory_record_field drf ON drf.id_record = dr.id_record AND drf.id_entry = ? LEFT JOIN directory_field df ON df.id_entry = drf.id_entry AND drf.id_field = df.id_field ";
    private static final String SQL_ORDER_BY_TITLE = " ORDER BY df.title ";
    private final String _template_create = "admin/plugins/directory/entrytypeselect/create_entry_type_select.html";
    private final String _template_modify = "admin/plugins/directory/entrytypeselect/modify_entry_type_select.html";
    private final String _template_html_code_form_entry = "admin/plugins/directory/entrytypeselect/html_code_form_entry_type_select.html";
    private final String _template_html_code_form_search_entry = "admin/plugins/directory/entrytypeselect/html_code_form_search_entry_type_select.html";
    private final String _template_html_code_entry_value = "admin/plugins/directory/entrytypeselect/html_code_entry_value_type_select.html";
    private final String _template_html_front_code_form_entry = "skin/plugins/directory/entrytypeselect/html_code_form_entry_type_select.html";
    private final String _template_html_front_code_form_search_entry = "skin/plugins/directory/entrytypeselect/html_code_form_search_entry_type_select.html";
    private final String _template_html_front_code_entry_value = "skin/plugins/directory/entrytypeselect/html_code_entry_value_type_select.html";

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
        String strMandatory = request.getParameter( PARAMETER_MANDATORY );
        String strIndexed = request.getParameter( PARAMETER_INDEXED );
        String strIndexedAsTitle = request.getParameter( PARAMETER_INDEXED_AS_TITLE );
        String strIndexedAsSummary = request.getParameter( PARAMETER_INDEXED_AS_SUMMARY );
        String strShowInAdvancedSearch = request.getParameter( PARAMETER_SHOWN_IN_ADVANCED_SEARCH );
        String strShowInResultList = request.getParameter( PARAMETER_SHOWN_IN_RESULT_LIST );
        String strShowInResultRecord = request.getParameter( PARAMETER_SHOWN_IN_RESULT_RECORD );
        String strWorkgroupAssociated = request.getParameter( PARAMETER_WORKGROUP_ASSOCIATED );
        String strRoleAssociated = request.getParameter( PARAMETER_ROLE_ASSOCIATED );
        String strShowInHistory = request.getParameter( PARAMETER_SHOWN_IN_HISTORY );
        String strIsAllSearch = request.getParameter( PARAMETER_IS_ADD_VALUE_SEARCH_ALL );
        String strLabelValueAllSearch = request.getParameter( PARAMETER_LABEL_VALUE_SEARCH_ALL );
        String strShowInExport = request.getParameter( PARAMETER_SHOWN_IN_EXPORT );
        String strShowInCompleteness = request.getParameter( PARAMETER_SHOWN_IN_COMPLETENESS );
        String strFieldError = DirectoryUtils.EMPTY_STRING;

        if ( ( strTitle == null ) || strTitle.trim(  ).equals( DirectoryUtils.EMPTY_STRING ) )
        {
            strFieldError = FIELD_TITLE;
        }
        else if ( ( strIsAllSearch != null ) && ( strLabelValueAllSearch == null ) )
        {
            strFieldError = FIELD_LABEL_ALL_SEARCH;
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
        this.setWorkgroupAssociated( strWorkgroupAssociated != null );
        this.setRoleAssociated( strRoleAssociated != null );
        this.setShownInHistory( strShowInHistory != null );
        this.setShownInExport( strShowInExport != null );
        this.setShownInCompleteness( strShowInCompleteness != null );
        this.setAddValueAllSearch( strIsAllSearch != null );

        if ( strIsAllSearch != null )
        {
            this.setLabelValueAllSearch( strLabelValueAllSearch );
        }
        else
        {
            this.setLabelValueAllSearch( null );
        }

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
    public void getImportRecordFieldData( Record record, String strImportValue, boolean bTestDirectoryError,
        List<RecordField> listRecordField, Locale locale )
        throws DirectoryErrorException
    {
        List<String> lstValue = new ArrayList<String>(  );
        Field field;
        field = DirectoryUtils.findFieldByValueInTheList( strImportValue, this.getFields(  ) );

        if ( field != null )
        {
            lstValue.add( Integer.toString( field.getIdField(  ) ) );
        }

        getRecordFieldData( record, lstValue, bTestDirectoryError, false, listRecordField, locale );
    }

    @Override
    public void getRecordFieldData( Record record, List<String> lstValue, boolean bTestDirectoryError,
        boolean bAddNewValue, List<RecordField> listRecordField, Locale locale )
        throws DirectoryErrorException
    {
        String strIdField = ( ( lstValue != null ) && ( lstValue.size(  ) > 0 ) ) ? lstValue.get( 0 ) : null;
        int nIdField = DirectoryUtils.convertStringToInt( strIdField );
        Field field = null;
        RecordField recordField = new RecordField(  );
        recordField.setEntry( this );

        if ( nIdField != DirectoryUtils.CONSTANT_ID_NULL )
        {
            field = DirectoryUtils.findFieldByIdInTheList( nIdField, this.getFields(  ) );
        }

        if ( bTestDirectoryError && this.isMandatory(  ) )
        {
            if ( ( field == null ) || field.getValue(  ).equals( DirectoryUtils.EMPTY_STRING ) )
            {
                throw new DirectoryErrorException( this.getTitle(  ) );
            }
        }

        if ( field != null )
        {
            recordField.setValue( field.getValue(  ) );
            recordField.setField( field );

            //set in the record the  workgroup and the role associated to the field  
            if ( record != null )
            {
                if ( this.isRoleAssociated(  ) )
                {
                    record.setRoleKey( field.getRoleKey(  ) );
                }

                if ( this.isWorkgroupAssociated(  ) )
                {
                    record.setWorkgroup( field.getWorkgroup(  ) );
                }
            }
        }

        listRecordField.add( recordField );
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
        return true;
    }

    @Override
    public LocalizedPaginator getPaginator( int nItemPerPage, String strBaseUrl, String strPageIndexParameterName,
        String strPageIndex, Locale locale )
    {
        return new LocalizedPaginator( this.getFields(  ), nItemPerPage, strBaseUrl, strPageIndexParameterName,
            strPageIndex, locale );
    }

    @Override
    public String getSQLJoin(  )
    {
        return SQL_JOIN_DIRECTORY_RECORD_FIELD;
    }

    @Override
    public String getSQLOrderBy(  )
    {
        return SQL_ORDER_BY_TITLE;
    }

    @Override
    public List<Object> getSQLParametersValues(  )
    {
        return Collections.<Object>singletonList( Integer.valueOf( getIdEntry(  ) ) );
    }
}

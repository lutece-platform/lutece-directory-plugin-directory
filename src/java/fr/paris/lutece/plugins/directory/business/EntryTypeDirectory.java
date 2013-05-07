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
import fr.paris.lutece.portal.service.content.XPageAppService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.web.util.LocalizedPaginator;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.sort.AttributeComparator;
import fr.paris.lutece.util.url.UrlItem;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;


/**
 *
 * class EntryTypeText
 *
 */
public class EntryTypeDirectory extends Entry
{
    // Parameter
    private static final String PARAMETER_VIEW_DIRECTORY_RECORD = "view_directory_record";

    // Property
    private static final String PROPERTY_PAGE_APPLICATION_ID = "directory.xpage.applicationId";

    // JSP
    private static final String JSP_DO_VISUALISATION_RECORD = "jsp/admin/plugins/directory/DoVisualisationRecord.jsp";

    // HTML constants
    private static final String HTML_LINK_OPEN_BEGIN = "<a href=\"";
    private static final String HTML_LINK_OPEN_END = "\">";
    private static final String HTML_LINK_CLOSE = "</a>";

    // Templates
    private final String _template_create = "admin/plugins/directory/entrytypedirectory/create_entry_type_directory.html";
    private final String _template_modify = "admin/plugins/directory/entrytypedirectory/modify_entry_type_directory.html";
    private final String _template_html_code_form_entry = "admin/plugins/directory/entrytypedirectory/html_code_form_entry_type_directory.html";
    private final String _template_html_code_form_search_entry = "admin/plugins/directory/entrytypedirectory/html_code_form_search_entry_type_directory.html";
    private final String _template_html_code_entry_value = "admin/plugins/directory/entrytypedirectory/html_code_entry_value_type_directory.html";
    private final String _template_html_front_code_form_entry = "skin/plugins/directory/entrytypedirectory/html_code_form_entry_type_directory.html";
    private final String _template_html_front_code_form_search_entry = "skin/plugins/directory/entrytypedirectory/html_code_form_search_entry_type_directory.html";
    private final String _template_html_front_code_entry_value = "skin/plugins/directory/entrytypedirectory/html_code_entry_value_type_directory.html";

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
        String strMandatory = request.getParameter( PARAMETER_MANDATORY );
        String strIndexed = request.getParameter( PARAMETER_INDEXED );
        String strIndexedAsTitle = request.getParameter( PARAMETER_INDEXED_AS_TITLE );
        String strIndexedAsSummary = request.getParameter( PARAMETER_INDEXED_AS_SUMMARY );
        String strShowInAdvancedSearch = request.getParameter( PARAMETER_SHOWN_IN_ADVANCED_SEARCH );
        String strShowInResultList = request.getParameter( PARAMETER_SHOWN_IN_RESULT_LIST );
        String strShowInResultRecord = request.getParameter( PARAMETER_SHOWN_IN_RESULT_RECORD );
        String strShowInHistory = request.getParameter( PARAMETER_SHOWN_IN_HISTORY );
        String strIsAllSearch = request.getParameter( PARAMETER_IS_ADD_VALUE_SEARCH_ALL );
        String strLabelValueAllSearch = request.getParameter( PARAMETER_LABEL_VALUE_SEARCH_ALL );
        String strIdEntryAssociate = request.getParameter( PARAMETER_ENTRY_ASSOCIATE );
        String strShowInExport = request.getParameter( PARAMETER_SHOWN_IN_EXPORT );
        String strShowInCompleteness = request.getParameter( PARAMETER_SHOWN_IN_COMPLETENESS );

        int idEntryAssociate = DirectoryUtils.convertStringToInt( strIdEntryAssociate );

        String strFieldError = DirectoryUtils.EMPTY_STRING;

        if ( ( strTitle == null ) || strTitle.trim(  ).equals( DirectoryUtils.EMPTY_STRING ) )
        {
            strFieldError = FIELD_TITLE;
        }
        else if ( idEntryAssociate == -1 )
        {
            strFieldError = FIELD_ENTRY_ASSOCIATE;
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

        this.setTitle( strTitle );
        this.setHelpMessage( strHelpMessage );
        this.setHelpMessageSearch( strHelpMessageSearch );
        this.setComment( strComment );

        // for don't update fields listFields=null
        this.setFields( null );

        this.setMandatory( strMandatory != null );
        this.setIndexed( strIndexed != null );
        this.setIndexedAsTitle( strIndexedAsTitle != null );
        this.setIndexedAsSummary( strIndexedAsSummary != null );
        this.setShownInAdvancedSearch( strShowInAdvancedSearch != null );
        this.setShownInResultList( strShowInResultList != null );
        this.setShownInResultRecord( strShowInResultRecord != null );
        this.setShownInHistory( strShowInHistory != null );
        this.setEntryAssociate( idEntryAssociate );
        this.setAddValueAllSearch( strIsAllSearch != null );
        this.setShownInExport( strShowInExport != null );
        this.setShownInCompleteness( strShowInCompleteness != null );

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
    public void getRecordFieldData( Record record, List<String> lstValue, boolean bTestDirectoryError,
        boolean bAddNewValue, List<RecordField> listRecordField, Locale locale )
        throws DirectoryErrorException
    {
        String strValueEntry = ( ( lstValue != null ) && ( lstValue.size(  ) > 0 ) ) ? lstValue.get( 0 ) : null;
        RecordField response = new RecordField(  );
        response.setEntry( this );

        if ( strValueEntry != null )
        {
            if ( bTestDirectoryError && this.isMandatory(  ) &&
                    ( strValueEntry.equals( DirectoryUtils.EMPTY_STRING ) ||
                    strValueEntry.equals( String.valueOf( DirectoryUtils.CONSTANT_ID_NULL ) ) ) )
            {
                throw new DirectoryErrorException( this.getTitle(  ) );
            }

            response.setValue( strValueEntry );
        }

        listRecordField.add( response );
    }

    public ReferenceList getSelectListRecordAssociate( boolean bDisplayFormSearch )
    {
        Plugin plugin = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
        ReferenceList referenceList = new ReferenceList(  );

        if ( ( bDisplayFormSearch || !this.isMandatory(  ) ) && this.isAddValueAllSearch(  ) )
        {
            referenceList.addItem( DirectoryUtils.EMPTY_STRING, this.getLabelValueAllSearch(  ) );
        }

        RecordFieldFilter recordFieldFilter = new RecordFieldFilter(  );
        //recordFieldFilter.setIdDirectory(this.getDirectoryAssociate());
        recordFieldFilter.setIdEntry( this.getEntryAssociate(  ) );

        List<RecordField> listRecordField = RecordFieldHome.getRecordFieldList( recordFieldFilter, plugin );
        Collections.sort( listRecordField, new AttributeComparator( "value", true ) );

        for ( RecordField recordField : listRecordField )
        {
            String title;

            if ( recordField.getFile(  ) != null )
            {
                title = recordField.getFile(  ).getTitle(  );
            }
            else if ( recordField.getField(  ) != null )
            {
                title = recordField.getField(  ).getTitle(  );
            }
            else
            {
                title = recordField.getValue(  );
            }

            if ( title != null )
            {
                referenceList.addItem( recordField.getRecord(  ).getIdRecord(  ), title );
            }
        }

        return referenceList;
    }

    public String convertRecordFieldValueToString( RecordField recordField, Locale locale, boolean bDisplayFront,
        boolean bExportDirectory )
    {
        String value = DirectoryUtils.EMPTY_STRING;
        Plugin plugin = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
        int idDirectory = -1;

        if ( recordField.getValue(  ) != null )
        {
            if ( !bExportDirectory )
            {
                RecordFieldFilter recordFieldFilter = new RecordFieldFilter(  );
                IEntry entry = EntryHome.findByPrimaryKey( this.getIdEntry(  ), plugin );
                idDirectory = entry.getDirectory(  ).getIdDirectory(  );

                recordFieldFilter.setIdEntry( entry.getEntryAssociate(  ) );
                recordFieldFilter.setIdRecord( DirectoryUtils.convertStringToInt( recordField.getValue(  ) ) );

                List<RecordField> listRecordField = RecordFieldHome.getRecordFieldList( recordFieldFilter, plugin );

                if ( ( listRecordField != null ) && !listRecordField.isEmpty(  ) )
                {
                    RecordField recordFieldResult = listRecordField.get( 0 );

                    if ( recordFieldResult != null )
                    {
                        if ( recordFieldResult.getFile(  ) != null )
                        {
                            value = recordFieldResult.getFile(  ).getTitle(  );
                        }
                        else if ( recordFieldResult.getField(  ) != null )
                        {
                            value = recordFieldResult.getField(  ).getTitle(  );
                        }
                        else
                        {
                            value = recordFieldResult.getValue(  );
                        }
                    }

                    if ( bDisplayFront && !value.equals( DirectoryUtils.EMPTY_STRING ) )
                    {
                        UrlItem url = new UrlItem( AppPathService.getPortalUrl(  ) );
                        url.addParameter( XPageAppService.PARAM_XPAGE_APP,
                            AppPropertiesService.getProperty( PROPERTY_PAGE_APPLICATION_ID ) );
                        url.addParameter( PARAMETER_ID_DIRECTORY_RECORD, recordField.getValue(  ) );
                        url.addParameter( PARAMETER_VIEW_DIRECTORY_RECORD, idDirectory );

                        return HTML_LINK_OPEN_BEGIN + url.getUrl(  ) + HTML_LINK_OPEN_END + value + HTML_LINK_CLOSE;
                    }
                    else if ( !bExportDirectory && !value.equals( DirectoryUtils.EMPTY_STRING ) )
                    {
                        UrlItem url = new UrlItem( JSP_DO_VISUALISATION_RECORD );
                        url.addParameter( PARAMETER_ID_DIRECTORY_RECORD, recordField.getValue(  ) );

                        return HTML_LINK_OPEN_BEGIN + url.getUrl(  ) + HTML_LINK_OPEN_END + value + HTML_LINK_CLOSE;
                    }
                }
                else
                {
                    return recordField.getValue(  );
                }
            }
            else
            {
                return recordField.getValue(  );
            }
        }
        else
        {
            return DirectoryUtils.EMPTY_STRING;
        }

        return DirectoryUtils.EMPTY_STRING;
    }

    public String convertRecordFieldTitleToString( RecordField recordField, Locale locale, boolean bDisplayFront )
    {
        return convertRecordFieldValueToString( recordField, locale, bDisplayFront, false );
    }

    @Override
    public LocalizedPaginator getPaginator( int nItemPerPage, String strBaseUrl, String strPageIndexParameterName,
        String strPageIndex, Locale locale )
    {
        return new LocalizedPaginator( this.getFields(  ), nItemPerPage, strBaseUrl, strPageIndexParameterName,
            strPageIndex, locale );
    }
}

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

import fr.paris.lutece.plugins.directory.service.DirectoryPlugin;
import fr.paris.lutece.plugins.directory.utils.DirectoryErrorException;
import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.url.UrlItem;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;


/**
 *
 * class EntryTypeMyLuteceUser
 *
 */
public class EntryTypeMyLuteceUser extends Entry
{
    // CONSTANTS
    private static final int CONSTANT_POSITION_MYLUTECE_USER_LOGIN = 0;
    private static final String SPACE = " ";
    private static final String OPEN_BRACKET = "(";
    private static final String CLOSED_BRACKET = ")";

    // HTML constants
    private static final String HTML_LINK_OPEN_BEGIN = "<a href=\"";
    private static final String HTML_LINK_OPEN_END = "\">";
    private static final String HTML_LINK_CLOSE = "</a>";

    // JSP
    private static final String JSP_DO_VISUALISATION_MYLUTECE_USER = "jsp/admin/plugins/directory/DoVisualisationMyLuteceUser.jsp";

    // TEMPLATES
    private static final String TEMPLATE_CREATE = "admin/plugins/directory/entrytypemyluteceuser/create_entry_type_mylutece_user.html";
    private static final String TEMPLATE_MODIFY = "admin/plugins/directory/entrytypemyluteceuser/modify_entry_type_mylutece_user.html";
    private static final String TEMPLATE_HTML_FRONT_CODE_FORM_ENTRY = "skin/plugins/directory/entrytypemyluteceuser/html_code_form_entry_type_mylutece_user.html";
    private static final String TEMPLATE_HTML_FRONT_CODE_ENTRY_VALUE = "skin/plugins/directory/entrytypemyluteceuser/html_code_entry_value_type_mylutece_user.html";
    private static final String TEMPLATE_HTML_FRONT_CODE_FORM_SEARCH_ENTRY = "skin/plugins/directory/entrytypemyluteceuser/html_code_form_search_entry_type_mylutece_user.html";
    private static final String TEMPLATE_HTML_CODE_FORM_ENTRY = "admin/plugins/directory/entrytypemyluteceuser/html_code_form_entry_type_mylutece_user.html";
    private static final String TEMPLATE_HTML_CODE_ENTRY_VALUE = "admin/plugins/directory/entrytypemyluteceuser/html_code_entry_value_type_mylutece_user.html";
    private static final String TEMPLATE_HTML_CODE_FORM_SEARCH_ENTRY = "admin/plugins/directory/entrytypemyluteceuser/html_code_form_search_entry_type_mylutece_user.html";

    /**
    *
    * {@inheritDoc}
    */
    @Override
    public String getTemplateCreate(  )
    {
        return TEMPLATE_CREATE;
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public String getTemplateModify(  )
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
    public String getTemplateHtmlFormSearchEntry( boolean isDisplayFront )
    {
        if ( isDisplayFront )
        {
            return TEMPLATE_HTML_FRONT_CODE_FORM_SEARCH_ENTRY;
        }

        return TEMPLATE_HTML_CODE_FORM_SEARCH_ENTRY;
    }

    /**
    *
    * {@inheritDoc}
    */
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

        String strShowInExport = request.getParameter( PARAMETER_SHOWN_IN_EXPORT );

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
        this.setShownInExport( strShowInExport != null );
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

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public void getRecordFieldData( Record record, List<String> lstValue, boolean bTestDirectoryError,
        boolean bAddNewValue, List<RecordField> listRecordField, Locale locale )
        throws DirectoryErrorException
    {
        if ( ( lstValue != null ) && ( lstValue.size(  ) > 0 ) )
        {
            String strUserLogin = lstValue.get( CONSTANT_POSITION_MYLUTECE_USER_LOGIN );
            LuteceUser user = SecurityService.getInstance(  ).getUser( strUserLogin );

            if ( user != null )
            {
                RecordField recordField = new RecordField(  );
                recordField.setEntry( this );
                recordField.setValue( strUserLogin );
                listRecordField.add( recordField );
            }
        }
    }

    /**
     * Get all mylutece users
     * @return mylutece users
     */
    public ReferenceList getMyLuteceUsers(  )
    {
        ReferenceList listMyLuteceUsers = new ReferenceList(  );
        Collection<LuteceUser> listUsers = SecurityService.getInstance(  ).getUsers(  );

        for ( LuteceUser user : listUsers )
        {
            StringBuilder sbUser = new StringBuilder(  );
            String strFamilyName = user.getUserInfo( LuteceUser.NAME_FAMILY );
            String strGivenName = user.getUserInfo( LuteceUser.NAME_GIVEN );

            if ( ( strFamilyName != null ) && !strFamilyName.equals( DirectoryUtils.EMPTY_STRING ) )
            {
                sbUser.append( strFamilyName + SPACE );
            }

            if ( ( strGivenName != null ) && !strGivenName.equals( DirectoryUtils.EMPTY_STRING ) )
            {
                sbUser.append( strGivenName + SPACE );
            }

            sbUser.append( OPEN_BRACKET + user.getName(  ) + CLOSED_BRACKET );
            listMyLuteceUsers.addItem( user.getName(  ), sbUser.toString(  ) );
        }

        return listMyLuteceUsers;
    }

    /**
     * Convert the record field value to string
     * @param recordField the record field
     * @param locale Locale
     * @param bDisplayFront true if it is displayed in front
     * @param bExportDirectory true if it is exported
     */
    public String convertRecordFieldValueToString( RecordField recordField, Locale locale, boolean bDisplayFront,
        boolean bExportDirectory )
    {
        String value = DirectoryUtils.EMPTY_STRING;
        Plugin plugin = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );

        if ( recordField.getValue(  ) != null )
        {
            if ( !bExportDirectory && !bDisplayFront )
            {
                RecordFieldFilter recordFieldFilter = new RecordFieldFilter(  );
                IEntry entry = EntryHome.findByPrimaryKey( this.getIdEntry(  ), plugin );

                recordFieldFilter.setIdEntry( entry.getIdEntry(  ) );
                recordFieldFilter.setIdRecord( recordField.getRecord(  ).getIdRecord(  ) );

                List<RecordField> listRecordField = RecordFieldHome.getRecordFieldList( recordFieldFilter, plugin );

                if ( ( listRecordField != null ) && !listRecordField.isEmpty(  ) )
                {
                    RecordField recordFieldResult = listRecordField.get( CONSTANT_POSITION_MYLUTECE_USER_LOGIN );

                    if ( recordFieldResult != null )
                    {
                        value = recordFieldResult.getValue(  );
                    }

                    if ( !value.equals( DirectoryUtils.EMPTY_STRING ) )
                    {
                        UrlItem url = new UrlItem( JSP_DO_VISUALISATION_MYLUTECE_USER );
                        url.addParameter( PARAMETER_ID_DIRECTORY_RECORD, recordField.getRecord(  ).getIdRecord(  ) );

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

    /**
     * Convert the record field value to string
     * @param recordField the record field
     * @param locale Locale
     * @param bDisplayFront true if it is displayed in front
     */
    public String convertRecordFieldTitleToString( RecordField recordField, Locale locale, boolean bDisplayFront )
    {
        return convertRecordFieldValueToString( recordField, locale, bDisplayFront, false );
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

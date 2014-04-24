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
import fr.paris.lutece.plugins.directory.utils.DirectoryErrorException;
import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;
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
 * class EntryTypeRemoteMyLuteceUser
 *
 */
public class EntryTypeRemoteMyLuteceUser extends Entry
{
    // CONSTANTS
    private static final int CONSTANT_POSITION_MYLUTECE_USER_LOGIN = 0;
    private static final String SPACE = " ";
    private static final String TWO_POINTS = ":";
    private static final String COMMA = ",";

    // PROPERTIES
    private static final String PROPERTY_USER_LOGIN = "directory.viewing_mylutece_user.labelLogin";
    private static final String PROPERTY_USER_INFO_PREFIX = "portal.security.";

    // TEMPLATES
    private static final String TEMPLATE_CREATE = "admin/plugins/directory/entrytyperemotemyluteceuser/create_entry_type_remote_mylutece_user.html";
    private static final String TEMPLATE_MODIFY = "admin/plugins/directory/entrytyperemotemyluteceuser/modify_entry_type_remote_mylutece_user.html";
    private static final String TEMPLATE_HTML_FRONT_CODE_FORM_ENTRY = "skin/plugins/directory/entrytyperemotemyluteceuser/html_code_form_entry_type_remote_mylutece_user.html";
    private static final String TEMPLATE_HTML_FRONT_CODE_ENTRY_VALUE = "skin/plugins/directory/entrytyperemotemyluteceuser/html_code_entry_value_type_remote_mylutece_user.html";
    private static final String TEMPLATE_HTML_FRONT_CODE_FORM_SEARCH_ENTRY = "skin/plugins/directory/entrytyperemotemyluteceuser/html_code_form_search_entry_type_remote_mylutece_user.html";
    private static final String TEMPLATE_HTML_CODE_FORM_ENTRY = "admin/plugins/directory/entrytyperemotemyluteceuser/html_code_form_entry_type_remote_mylutece_user.html";
    private static final String TEMPLATE_HTML_CODE_ENTRY_VALUE = "admin/plugins/directory/entrytyperemotemyluteceuser/html_code_entry_value_type_remote_mylutece_user.html";
    private static final String TEMPLATE_HTML_CODE_FORM_SEARCH_ENTRY = "admin/plugins/directory/entrytyperemotemyluteceuser/html_code_form_search_entry_type_remote_mylutece_user.html";

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTemplateCreate(  )
    {
        return TEMPLATE_CREATE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTemplateModify(  )
    {
        return TEMPLATE_MODIFY;
    }

    /**
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

        // Check if we have to show every information of the users or not
        String strShowFullInfo = request.getParameter( PARAMETER_SHOW_ALL_INFO );

        String strShowInExport = request.getParameter( PARAMETER_SHOWN_IN_EXPORT );
        String strShowInCompleteness = request.getParameter( PARAMETER_SHOWN_IN_COMPLETENESS );

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

        if ( ( this.getFields(  ) == null ) || ( this.getFields(  ).size(  ) == 0 ) )
        {
            List<Field> listFields = new ArrayList<Field>(  );
            Field field = new Field(  );
            field.setEntry( this );
            listFields.add( field );
            this.setFields( listFields );
        }

        // IMPORTANT !!!
        // The flag to show every information is stored in the database directory_field.DEFAULT_value
        this.getFields(  ).get( 0 ).setDefaultValue( strShowFullInfo != null );

        return null;
    }

    /**
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

            RecordField recordField = new RecordField(  );
            recordField.setEntry( this );
            recordField.setValue( strUserLogin );
            listRecordField.add( recordField );
        }
    }

    /**
         * {@inheritDoc}
         */
    @Override
    public String getHtmlRecordFieldValue( Locale locale, RecordField recordField, boolean isDisplayFront )
    {
        if ( getTemplateHtmlRecordFieldValue( isDisplayFront ) != null )
        {
            Map<String, Object> model = new HashMap<String, Object>(  );
            model.put( MARK_ENTRY, this );
            model.put( MARK_RECORD_FIELD, recordField );
            model.put( MARK_LOCALE, locale );

            if ( showAllInfo(  ) )
            {
                int nIdRecord = recordField.getRecord(  ).getIdRecord(  );
                String strUserGuid = DirectoryService.getInstance(  ).getUserGuid( nIdRecord, getIdEntry(  ) );
                ReferenceList listUserInfos = DirectoryService.getInstance(  ).getUserInfos( strUserGuid, getIdEntry(  ) );

                model.put( MARK_MYLUTECE_USER_INFOS_LIST, listUserInfos );
                model.put( MARK_MYLUTECE_USER_LOGIN, strUserGuid );
            }

            HtmlTemplate template = AppTemplateService.getTemplate( getTemplateHtmlRecordFieldValue( isDisplayFront ),
                    locale, model );

            return template.getHtml(  );
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String convertRecordFieldValueToString( RecordField recordField, Locale locale, boolean bDisplayFront,
        boolean bExportDirectory )
    {
        String strValue = StringUtils.EMPTY;

        if ( ( recordField != null ) && StringUtils.isNotBlank( recordField.getValue(  ) ) &&
                ( recordField.getRecord(  ) != null ) )
        {
            if ( bExportDirectory )
            {
                int nIdRecord = recordField.getRecord(  ).getIdRecord(  );
                String strUserGuid = DirectoryService.getInstance(  ).getUserGuid( nIdRecord, getIdEntry(  ) );
                ReferenceList listUserInfos = DirectoryService.getInstance(  ).getUserInfos( strUserGuid, getIdEntry(  ) );

                if ( showAllInfo(  ) && StringUtils.isNotBlank( strUserGuid ) && ( listUserInfos != null ) )
                {
                    StringBuilder sbValue = new StringBuilder(  );
                    sbValue.append( I18nService.getLocalizedString( PROPERTY_USER_LOGIN, locale ) + SPACE + TWO_POINTS +
                        SPACE + strUserGuid );

                    for ( ReferenceItem userInfo : listUserInfos )
                    {
                        sbValue.append( COMMA );
                        sbValue.append( I18nService.getLocalizedString( PROPERTY_USER_INFO_PREFIX +
                                userInfo.getCode(  ), locale ) );
                        sbValue.append( SPACE + TWO_POINTS + SPACE );
                        sbValue.append( userInfo.getName(  ) );
                    }

                    strValue = sbValue.toString(  );
                }
            }

            if ( StringUtils.isBlank( strValue ) )
            {
                strValue = recordField.getValue(  );
            }
        }

        return strValue;
    }

    /**
     * {@inheritDocs}
     */
    @Override
    public String convertRecordFieldTitleToString( RecordField recordField, Locale locale, boolean bDisplayFront )
    {
        return convertRecordFieldValueToString( recordField, locale, bDisplayFront, false );
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public boolean isSortable(  )
    {
        return true;
    }

    /**
     * Check if it must show every information of the user or not
     * @return true if it must show every information, false otherwise
     */
    private boolean showAllInfo(  )
    {
        boolean bShow = false;
        List<Field> listFields = getFields(  );

        if ( ( listFields == null ) || listFields.isEmpty(  ) )
        {
            Plugin plugin = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
            listFields = FieldHome.getFieldListByIdEntry( getIdEntry(  ), plugin );
        }

        if ( listFields != null )
        {
            Field field = listFields.get( 0 );

            if ( field != null )
            {
                bShow = field.isDefaultValue(  );
            }
        }

        return bShow;
    }
}

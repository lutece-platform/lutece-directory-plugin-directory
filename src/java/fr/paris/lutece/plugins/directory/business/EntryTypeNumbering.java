/*
 * Copyright (c) 2002-2011, Mairie de Paris
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
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.html.HtmlTemplate;

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
    private final String _template_create = "admin/plugins/directory/entrytypenumbering/create_entry_type_numbering.html";
    private final String _template_modify = "admin/plugins/directory/entrytypenumbering/modify_entry_type_numbering.html";
    private final String _template_html_code_form_entry = "admin/plugins/directory/entrytypenumbering/html_code_form_entry_type_numbering.html";
    private final String _template_html_code_form_search_entry = "admin/plugins/directory/entrytypenumbering/html_code_form_search_entry_type_numbering.html";
    private final String _template_html_code_entry_value = "admin/plugins/directory/entrytypenumbering/html_code_entry_value_type_numbering.html";
    private final String _template_html_front_code_form_entry = "skin/plugins/directory/entrytypenumbering/html_code_form_entry_type_numbering.html";
    private final String _template_html_front_code_form_search_entry = "skin/plugins/directory/entrytypenumbering/html_code_form_search_entry_type_numbering.html";
    private final String _template_html_front_code_entry_value = "skin/plugins/directory/entrytypenumbering/html_code_entry_value_type_numbering.html";
    
    // MARKS
    private static final String MARK_MAX_NUMBER = "max_number";
    
    // SQL
    private static final String SQL_ORDER_BY_RECORD_FIELD_VALUE = " ORDER BY 0 + drf.record_field_value ";
    
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
    public String getHtmlFormEntry( Locale locale, boolean isDisplayFront )
    {
        if ( getTemplateHtmlFormEntry( isDisplayFront ) != null )
        {
        	Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
            Map<String, Object> model = new HashMap<String, Object>(  );
            model.put( MARK_ENTRY, this );
            model.put( MARK_LOCALE, locale );
            model.put( MARK_MAX_NUMBER, RecordFieldHome.findMaxNumber( getEntryType(  ).getIdType(  ), 
            		getDirectory(  ).getIdDirectory(  ), pluginDirectory ) );

            HtmlTemplate template = AppTemplateService.getTemplate( getTemplateHtmlFormEntry( isDisplayFront ), locale,
                    model );

            return template.getHtml(  );
        }

        return null;
    }

    @Override
    public String getEntryData( HttpServletRequest request, Locale locale )
    {
        String strTitle = request.getParameter( PARAMETER_TITLE );
        String strHelpMessageSearch = ( request.getParameter( PARAMETER_HELP_MESSAGE_SEARCH ) != null )
            ? request.getParameter( PARAMETER_HELP_MESSAGE_SEARCH ).trim(  ) : null;
        String strComment = request.getParameter( PARAMETER_COMMENT );

        String strIndexed = request.getParameter( PARAMETER_INDEXED );
        String strIndexedAsTitle = request.getParameter( PARAMETER_INDEXED_AS_TITLE );
        String strIndexedAsSummary = request.getParameter( PARAMETER_INDEXED_AS_SUMMARY );
        String strShowInAdvancedSearch = request.getParameter( PARAMETER_SHOWN_IN_ADVANCED_SEARCH );
        String strShowInResultList = request.getParameter( PARAMETER_SHOWN_IN_RESULT_LIST );
        String strShowInResultRecord = request.getParameter( PARAMETER_SHOWN_IN_RESULT_RECORD );
        String strShowInHistory = request.getParameter( PARAMETER_SHOWN_IN_HISTORY );
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
        this.setHelpMessageSearch( strHelpMessageSearch );
        this.setComment( strComment );

        if ( this.getFields(  ) == null )
        {
            ArrayList<Field> listFields = new ArrayList<Field>(  );
            Field field = new Field(  );
            listFields.add( field );
            this.setFields( listFields );
        }

        this.getFields(  ).get( 0 ).setValue( "1" );
        this.setIndexed( strIndexed != null );
        this.setIndexedAsTitle( strIndexedAsTitle != null );
        this.setIndexedAsSummary( strIndexedAsSummary != null );
        this.setShownInAdvancedSearch( strShowInAdvancedSearch != null );
        this.setShownInResultList( strShowInResultList != null );
        this.setShownInResultRecord( strShowInResultRecord != null );
        this.setShownInHistory( strShowInHistory != null );
        this.setShownInExport( strShowInExport != null );

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
    public void getRecordFieldData( Record record, List<String> lstValue, boolean bTestDirectoryError,
        boolean bAddNewValue, List<RecordField> listRecordField, Locale locale )
        throws DirectoryErrorException
    {
        //String strValueEntry = ( ( lstValue != null ) && ( lstValue.size(  ) > 0 ) ) ? lstValue.get( 0 ) : null;
        Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
        RecordField recordField = new RecordField(  );
        recordField.setEntry( this );

        Record recordOld = null;

        if ( record != null )
        {
            recordOld = RecordHome.findByPrimaryKey( record.getIdRecord(  ), pluginDirectory );
        }

        String strValueEntry = ( ( lstValue != null ) && ( lstValue.size(  ) > 0 ) ) ? lstValue.get( 0 ) : null;

        if ( recordOld != null )
        {
            recordField.setValue( strValueEntry );
        }
        else if ( ( strValueEntry != null ) && ( record == null ) )
        {
            if ( bTestDirectoryError && this.isMandatory(  ) && strValueEntry.equals( DirectoryUtils.EMPTY_STRING ) )
            {
                throw new DirectoryErrorException( this.getTitle(  ) );
            }

            recordField.setValue( strValueEntry );
        }
        else
        {
        	
            //int numbering = DirectoryUtils.convertStringToInt( this.getFields(  ).get( 0 ).getValue(  ) );
        	int numbering = RecordFieldHome.findMaxNumber( getEntryType(  ).getIdType(  ), 
            		getDirectory(  ).getIdDirectory(  ), pluginDirectory );
            this.getFields(  ).get( 0 ).setValue( String.valueOf( numbering + 1 ) );
            FieldHome.update( this.getFields(  ).get( 0 ), pluginDirectory );
        	
            recordField.setValue( String.valueOf( numbering ) );
        }

        if ( bTestDirectoryError && this.isMandatory(  ) )
        {
            throw new DirectoryErrorException( this.getTitle(  ) );
        }

        listRecordField.add( recordField );
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
    public String getSQLOrderBy(  )
    {
    	// Special query in order to sort numerically and not alphabetically (thus avoiding list like 1, 10, 11, 2, ... instead of 1, 2, ..., 10, 11)
    	return SQL_ORDER_BY_RECORD_FIELD_VALUE;
    }
}

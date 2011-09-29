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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.directory.service.DirectoryPlugin;
import fr.paris.lutece.plugins.directory.utils.DirectoryErrorException;
import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.portal.business.regularexpression.RegularExpression;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.regularexpression.RegularExpressionService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.web.util.LocalizedPaginator;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.url.UrlItem;


/**
 *
 * class EntryTypeFile
 *
 */
public class EntryTypeFile extends Entry
{
    private final String _template_create = "admin/plugins/directory/entrytypefile/create_entry_type_file.html";
    private final String _template_modify = "admin/plugins/directory/entrytypefile/modify_entry_type_file.html";
    private final String _template_html_code_form_entry = "admin/plugins/directory/entrytypefile/html_code_form_entry_type_file.html";
    private final String _template_html_code_entry_value = "admin/plugins/directory/entrytypefile/html_code_entry_value_type_file.html";
    private final String _template_html_front_code_form_entry = "skin/plugins/directory/entrytypefile/html_code_form_entry_type_file.html";
    private final String _template_html_front_code_entry_value = "skin/plugins/directory/entrytypefile/html_code_entry_value_type_file.html";
    
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
        else
        {
            return _template_html_code_entry_value;
        }
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
        String strComment = request.getParameter( PARAMETER_COMMENT );
        String strMandatory = request.getParameter( PARAMETER_MANDATORY );
        String strIndexed = request.getParameter( PARAMETER_INDEXED );
        String strIndexedAsTitle = request.getParameter( PARAMETER_INDEXED_AS_TITLE );
        String strIndexedAsSummary = request.getParameter( PARAMETER_INDEXED_AS_SUMMARY);
        String strWidth = request.getParameter( PARAMETER_WIDTH );
        String strShowInFormMainSearch = request.getParameter( PARAMETER_SHOWN_IN_ADVANCED_SEARCH );
        String strShowInResultList = request.getParameter( PARAMETER_SHOWN_IN_RESULT_LIST );
        String strShowInResultRecord = request.getParameter( PARAMETER_SHOWN_IN_RESULT_RECORD );
        String strShowInHistory = request.getParameter( PARAMETER_SHOWN_IN_HISTORY );
        String strFieldError = DirectoryUtils.EMPTY_STRING;
        int nWidth = DirectoryUtils.convertStringToInt( strWidth );
        String strShowInExport = request.getParameter( PARAMETER_SHOWN_IN_EXPORT );
        String strShowInCompleteness = request.getParameter( PARAMETER_SHOWN_IN_COMPLETENESS );

        if ( ( strTitle == null ) || strTitle.trim(  ).equals( DirectoryUtils.EMPTY_STRING ) )
        {
            strFieldError = FIELD_TITLE;
        }
        else if ( ( strWidth == null ) || strWidth.trim(  ).equals( DirectoryUtils.EMPTY_STRING ) )
        {
            strFieldError = FIELD_WIDTH;
        }

        if ( !strFieldError.equals( DirectoryUtils.EMPTY_STRING ) )
        {
            Object[] tabRequiredFields = { I18nService.getLocalizedString( strFieldError, locale ) };

            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields,
                AdminMessage.TYPE_STOP );
        }

        if ( nWidth == -1 )
        {
            strFieldError = FIELD_WIDTH;
        }

        if ( !strFieldError.equals( DirectoryUtils.EMPTY_STRING ) )
        {
            Object[] tabRequiredFields = { I18nService.getLocalizedString( strFieldError, locale ) };

            return AdminMessageService.getMessageUrl( request, MESSAGE_NUMERIC_FIELD, tabRequiredFields,
                AdminMessage.TYPE_STOP );
        }

        this.setTitle( strTitle );
        this.setHelpMessage( strHelpMessage );
        this.setComment( strComment );

        if ( this.getFields(  ) == null )
        {
            ArrayList<Field> listFields = new ArrayList<Field>(  );
            Field field = new Field(  );
            listFields.add( field );
            this.setFields( listFields );
        }

        this.getFields(  ).get( 0 ).setWidth( nWidth );
        this.setMandatory( strMandatory != null );
        this.setIndexed( strIndexed != null );
        this.setIndexedAsTitle( strIndexedAsTitle != null );
        this.setIndexedAsSummary( strIndexedAsSummary != null );
        this.setShownInAdvancedSearch( strShowInFormMainSearch != null );
        this.setShownInResultList( strShowInResultList != null );
        this.setShownInResultRecord( strShowInResultRecord != null );
        this.setShownInHistory( strShowInHistory != null );
        this.setShownInExport( strShowInExport != null );
        this.setShownInCompleteness( strShowInCompleteness != null );

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTemplateCreate(  )
    {
        return _template_create;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTemplateModify(  )
    {
        return _template_modify;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getRecordFieldData( Record record, List<String> lstValue, boolean bTestDirectoryError,
        boolean bAddNewValue, List<RecordField> listRecordField, Locale locale )
        throws DirectoryErrorException
    {
        // Add Empty recordField(Use for data import)
        RecordField recordField = new RecordField(  );
        recordField.setEntry( this );
        listRecordField.add( recordField );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getImportRecordFieldData( Record record, byte[] strImportValue, String nomFile,
        boolean bTestDirectoryError, List<RecordField> listRecordField, Locale locale )
        throws DirectoryErrorException
    {
        // Create a file with the data of the pdf file, the file will then be imported
        RecordField recordField = new RecordField(  );
        recordField.setEntry( this );

        File file2 = new File(  );
        file2.setExtension( "pdf" );
        file2.setMimeType( "application/pdf" );
        file2.setTitle( nomFile );
        file2.setSize( strImportValue.length );

        PhysicalFile ph = new PhysicalFile(  );
        ph.setValue( strImportValue );
        file2.setPhysicalFile( ph );
        recordField.setFile( file2 );
        listRecordField.add( recordField );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getRecordFieldData( Record record, HttpServletRequest request, boolean bTestDirectoryError,
        boolean bAddNewValue, List<RecordField> listRecordField, Locale locale )
        throws DirectoryErrorException
    {
        String strUpdateFile = request.getParameter( PARAMETER_UPDATE_ENTRY + "_" + this.getIdEntry(  ) );
        String strIdDirectoryRecord = request.getParameter( PARAMETER_ID_DIRECTORY_RECORD );

        File fileSource = DirectoryUtils.getFileData( DirectoryUtils.EMPTY_STRING + this.getIdEntry(  ), request );
        List<RegularExpression> listRegularExpression = this.getFields(  ).get( 0 ).getRegularExpressionList(  );

        RecordField recordField = new RecordField(  );
        recordField.setEntry( this );

        if ( this.isMandatory(  ) && ( fileSource == null ) &&
                ( ( strIdDirectoryRecord == null ) || ( ( strIdDirectoryRecord != null ) && ( strUpdateFile != null ) ) ) )
        {
            throw new DirectoryErrorException( this.getTitle(  ) );
        }

        if ( ( fileSource != null ) && ( listRegularExpression != null ) && ( listRegularExpression.size(  ) != 0 ) &&
                RegularExpressionService.getInstance(  ).isAvailable(  ) )
        {
            for ( RegularExpression regularExpression : listRegularExpression )
            {
                if ( !RegularExpressionService.getInstance(  ).isMatches( fileSource.getMimeType(  ), regularExpression ) )
                {
                    throw new DirectoryErrorException( this.getTitle(  ), regularExpression.getErrorMessage(  ) );
                }
            }
        }

        if ( ( fileSource != null ) &&
                ( ( strIdDirectoryRecord == null ) || ( ( strIdDirectoryRecord != null ) && ( strUpdateFile != null ) ) ) )
        {
            recordField.setFile( fileSource );
        }
        else if ( ( fileSource == null ) && ( strUpdateFile == null ) && ( strIdDirectoryRecord != null ) )
        {
            // Get the default file
            RecordFieldFilter filter = new RecordFieldFilter(  );
            filter.setIdEntry( this.getIdEntry(  ) );
            filter.setIdRecord( DirectoryUtils.convertStringToInt( strIdDirectoryRecord ) );

            List<RecordField> listRecordFieldStore = RecordFieldHome.getRecordFieldList( filter,
                    PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME ) );

            for ( RecordField recordFieldBase : listRecordFieldStore )
            {
                recordField = recordFieldBase;

                if ( recordField.getFile(  ) != null )
                {
                    recordField.getFile(  )
                               .setPhysicalFile( PhysicalFileHome.findByPrimaryKey( 
                            recordField.getFile(  ).getPhysicalFile(  ).getIdPhysicalFile(  ),
                            PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME ) ) );
                }
            }
        }
        
        // Put the download url in the record field value
        String strDownloadUrl = StringUtils.EMPTY;
        if ( recordField.getFile(  ) != null )
        {
        	UrlItem url = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_DOWNLOAD_FILE );
        	url.addParameter( DirectoryUtils.PARAMETER_ID_DIRECTORY_RECORD, strIdDirectoryRecord );
        	url.addParameter( DirectoryUtils.PARAMETER_ID_ENTRY, getIdEntry(  ) );
        	strDownloadUrl = url.getUrl(  );
        }
        
        recordField.setValue( strDownloadUrl );

        listRecordField.add( recordField );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Paginator<RegularExpression> getPaginator( int nItemPerPage, String strBaseUrl, String strPageIndexParameterName,
        String strPageIndex )
    {
        return new Paginator<RegularExpression>( this.getFields(  ).get( 0 ).getRegularExpressionList(  ), nItemPerPage, strBaseUrl,
            strPageIndexParameterName, strPageIndex );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReferenceList getReferenceListRegularExpression( IEntry entry, Plugin plugin )
    {
        ReferenceList refListRegularExpression = null;

        if ( RegularExpressionService.getInstance(  ).isAvailable(  ) )
        {
            refListRegularExpression = new ReferenceList(  );

            List<RegularExpression> listRegularExpression = RegularExpressionService.getInstance(  )
                                                                                    .getAllRegularExpression(  );

            for ( RegularExpression regularExpression : listRegularExpression )
            {
                if ( !entry.getFields(  ).get( 0 ).getRegularExpressionList(  ).contains( regularExpression ) )
                {
                    refListRegularExpression.addItem( regularExpression.getIdExpression(  ),
                        regularExpression.getTitle(  ) );
                }
            }
        }

        return refListRegularExpression;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalizedPaginator<RegularExpression> getPaginator( int nItemPerPage, String strBaseUrl, String strPageIndexParameterName,
        String strPageIndex, Locale locale )
    {
        return new LocalizedPaginator<RegularExpression>( this.getFields(  ).get( 0 ).getRegularExpressionList(  ), nItemPerPage,
            strBaseUrl, strPageIndexParameterName, strPageIndex, locale );
    }
}

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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
import fr.paris.lutece.portal.web.util.LocalizedPaginator;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.xml.XmlUtil;


/**
 *
 * class EntryTypeDownloadUrl
 *
 */
public class EntryTypeDownloadUrl extends Entry
{
	// PARAMETERS
	public static final String PARAMETER_WS_REST_URL = "ws_rest_url";
	public static final String PARAMETER_BLOBSTORE = "blobstore";
	
	// CONSTANTS
	public static final String CONSTANT_OPTION = "option";
	public static final String CONSTANT_WS_REST_URL = "ws_rest_url";
	public static final String CONSTANT_BLOBSTORE = "blobstore";
	
    // TAGS
    private static final String TAG_A = "a";
    private static final String TAG_IMG = "img";
    
    // ATTRIBUTES
    private static final String ATTRIBUTE_HREF = "href";
    private static final String ATTRIBUTE_SRC = "src";
    private static final String ATTRIBUTE_TITLE = "title";
    private static final String ATTRIBUTE_ALT = "alt";
    
    // URL
    private static final String URL_IMG_DOWNLOAD = "images/local/skin/plugins/directory/download.png";
    
    // PROPERTIES
    private static final String PROPERTY_LABEL_DOWNLOAD = "directory.viewing_directory_record.download";

    // TEMPLATES
    private final String _template_create = "admin/plugins/directory/entrytypedownloadurl/create_entry_type_download_url.html";
    private final String _template_modify = "admin/plugins/directory/entrytypedownloadurl/modify_entry_type_download_url.html";
    private final String _template_html_code_form_entry = "admin/plugins/directory/entrytypedownloadurl/html_code_form_entry_type_download_url.html";
    private final String _template_html_code_form_search_entry = "admin/plugins/directory/entrytypedownloadurl/html_code_form_search_entry_type_download_url.html";
    private final String _template_html_code_entry_value = "admin/plugins/directory/entrytypedownloadurl/html_code_entry_value_type_download_url.html";
    private final String _template_html_front_code_form_entry = "skin/plugins/directory/entrytypedownloadurl/html_code_form_entry_type_download_url.html";
    private final String _template_html_front_code_form_search_entry = "skin/plugins/directory/entrytypedownloadurl/html_code_form_search_entry_type_download_url.html";
    private final String _template_html_front_code_entry_value = "skin/plugins/directory/entrytypedownloadurl/html_code_entry_value_type_download_url.html";

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
        String strValue = request.getParameter( PARAMETER_VALUE );
        String strMandatory = request.getParameter( PARAMETER_MANDATORY );
        String strIndexed = request.getParameter( PARAMETER_INDEXED );
        String strDocumentTitle = request.getParameter( PARAMETER_INDEXED_AS_TITLE );
        String strDocumentSummary = request.getParameter( PARAMETER_INDEXED_AS_SUMMARY );
        String strShowInAdvancedSearch = request.getParameter( PARAMETER_SHOWN_IN_ADVANCED_SEARCH );
        String strShowInResultList = request.getParameter( PARAMETER_SHOWN_IN_RESULT_LIST );
        String strShowInResultRecord = request.getParameter( PARAMETER_SHOWN_IN_RESULT_RECORD );
        String strWidth = request.getParameter( PARAMETER_WIDTH );
        String strMaxSizeEnter = request.getParameter( PARAMETER_MAX_SIZE_ENTER );
        String strShowInHistory = request.getParameter( PARAMETER_SHOWN_IN_HISTORY );
        String strShowInExport = request.getParameter( PARAMETER_SHOWN_IN_EXPORT );
        String strShowInCompleteness = request.getParameter( PARAMETER_SHOWN_IN_COMPLETENESS );
        String strWSRestUrl = request.getParameter( PARAMETER_WS_REST_URL );
        String strBlobStore = request.getParameter( PARAMETER_BLOBSTORE );

        int nWidth = DirectoryUtils.convertStringToInt( strWidth );
        int nMaxSizeEnter = DirectoryUtils.convertStringToInt( strMaxSizeEnter );

        String strFieldError = DirectoryUtils.EMPTY_STRING;

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
        else if ( ( strMaxSizeEnter != null ) && !strMaxSizeEnter.trim(  ).equals( DirectoryUtils.EMPTY_STRING ) &&
                ( nMaxSizeEnter == -1 ) )
        {
            strFieldError = FIELD_MAX_SIZE_ENTER;
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

        // Field option
        Field fieldOption = findFieldOption( getFields(  ) );
        if ( fieldOption == null )
        {
        	fieldOption = new Field(  );
        }
        fieldOption.setEntry( this );
        fieldOption.setTitle( CONSTANT_OPTION );
        fieldOption.setValue( strValue );
        fieldOption.setWidth( nWidth );
        fieldOption.setMaxSizeEnter( nMaxSizeEnter );
        
        // Field WS Rest url
        Field fieldWSRestUrl = findField( CONSTANT_WS_REST_URL, getFields(  ) );
        if ( fieldWSRestUrl == null )
        {
        	fieldWSRestUrl = new Field(  );
        }
        fieldWSRestUrl.setEntry( this );
        fieldWSRestUrl.setTitle( CONSTANT_WS_REST_URL );
        fieldWSRestUrl.setValue( strWSRestUrl );
        
        // Field BlobStore
        Field fieldBlobStore = findField( CONSTANT_BLOBSTORE, getFields(  ) );
        if ( fieldBlobStore == null )
        {
        	fieldBlobStore = new Field(  );
        }
        fieldBlobStore.setEntry( this );
        fieldBlobStore.setTitle( CONSTANT_BLOBSTORE );
        fieldBlobStore.setValue( strBlobStore );
        
        List<Field> listFields = new ArrayList<Field>(  );
        listFields.add( fieldOption );
        listFields.add( fieldWSRestUrl );
        listFields.add( fieldBlobStore );
        
        this.setFields( listFields );

        this.setMandatory( strMandatory != null );
        this.setIndexed( strIndexed != null );
        this.setIndexedAsTitle( strDocumentTitle != null );
        this.setIndexedAsSummary( strDocumentSummary!= null );
        this.setShownInAdvancedSearch( strShowInAdvancedSearch != null );
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
    public void getRecordFieldData( Record record, List<String> lstValue, boolean bTestDirectoryError,
        boolean bAddNewValue, List<RecordField> listRecordField, Locale locale )
        throws DirectoryErrorException
    {
        Plugin plugin = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );

        String strValueEntry = ( ( lstValue != null ) && ( lstValue.size(  ) > 0 ) ) ? lstValue.get( 0 ) : null;
        List<RegularExpression> listRegularExpression = this.getFields(  ).get( 0 ).getRegularExpressionList(  );
        RecordField response = new RecordField(  );
        response.setEntry( this );

        if ( ( record != null ) && bAddNewValue )
        {
            RecordFieldFilter recordFieldFilter = new RecordFieldFilter(  );
            recordFieldFilter.setIdDirectory( record.getDirectory(  ).getIdDirectory(  ) );
            recordFieldFilter.setIdEntry( this.getIdEntry(  ) );
            recordFieldFilter.setIdRecord( record.getIdRecord(  ) );

            List<RecordField> recordFieldList = RecordFieldHome.getRecordFieldList( recordFieldFilter, plugin );

            if ( ( recordFieldList != null ) && !recordFieldList.isEmpty(  ) &&
            		StringUtils.isNotBlank( recordFieldList.get( 0 ).getValue(  ) ) )
            {
                strValueEntry = recordFieldList.get( 0 ).getValue(  ) + ", " + strValueEntry;
            }
        }

        if ( strValueEntry != null )
        {
            if ( bTestDirectoryError && this.isMandatory(  ) && strValueEntry.equals( DirectoryUtils.EMPTY_STRING ) )
            {
                throw new DirectoryErrorException( this.getTitle(  ) );
            }

            if ( bTestDirectoryError && ( !strValueEntry.equals( DirectoryUtils.EMPTY_STRING ) ) &&
                    ( listRegularExpression != null ) && ( listRegularExpression.size(  ) != 0 ) &&
                    RegularExpressionService.getInstance(  ).isAvailable(  ) )
            {
                for ( RegularExpression regularExpression : listRegularExpression )
                {
                    if ( !RegularExpressionService.getInstance(  ).isMatches( strValueEntry, regularExpression ) )
                    {
                        throw new DirectoryErrorException( this.getTitle(  ), regularExpression.getErrorMessage(  ) );
                    }
                }
            }

            response.setValue( strValueEntry );
        }

        listRecordField.add( response );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String convertRecordFieldTitleToString( RecordField recordField, Locale locale, boolean bDisplayFront )
    {
        String strTitle = super.convertRecordFieldTitleToString( recordField, locale, bDisplayFront );

        if ( StringUtils.isNotBlank( strTitle ) && bDisplayFront )
        {
        	// Display as an image
        	StringBuffer sbHtml = new StringBuffer(  );
        	
        	Map<String, String> mapParamTagA = new HashMap<String, String>(  );
        	mapParamTagA.put( ATTRIBUTE_HREF, strTitle );
        	
        	String strAlt = I18nService.getLocalizedString( PROPERTY_LABEL_DOWNLOAD, locale );
        	Map<String, String> mapParamTagImg = new HashMap<String, String>(  );
        	mapParamTagImg.put( ATTRIBUTE_SRC, URL_IMG_DOWNLOAD );
        	mapParamTagImg.put( ATTRIBUTE_TITLE, strAlt );
        	mapParamTagImg.put( ATTRIBUTE_ALT, strAlt );
        	
        	XmlUtil.beginElement( sbHtml, TAG_A, mapParamTagA );
        	XmlUtil.addEmptyElement( sbHtml, TAG_IMG, mapParamTagImg );
        	XmlUtil.endElement( sbHtml, TAG_A );
        	
            strTitle = sbHtml.toString(  );
        }

        return strTitle;
    }

    /**
     *
     * {@inheritDoc}
     */
    public boolean isSortable(  )
    {
        return true;
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
    
    /**
     * Finds a field according to its title
     * @param fieldName the title
     * @param listFields the list
     * @return the found field, <code>null</code> otherwise.
     */
    private Field findField( String strFieldName, List<Field> listFields )
    {
        if ( StringUtils.isBlank( strFieldName ) || listFields == null || listFields.size(  ) == 0 )
        {
            return null;
        }

        for ( Field field : listFields )
        {
            if ( strFieldName.equals( field.getTitle(  ) ) )
            {
                return field;
            }
        }

        return null;
    }
    
    /**
     * Find option field
     * @param listFields the list
     * @return the found field, <code>null</code> otherwise.
     */
    private Field findFieldOption( List<Field> listFields )
    {
    	if ( listFields == null || listFields.size(  ) == 0 )
        {
            return null;
        }
    	
    	for ( Field field : listFields )
        {
    		if ( StringUtils.isBlank( field.getTitle(  ) ) || CONSTANT_OPTION.equals( field.getTitle(  ) ) )
    		{
    			return field;
    		}
        }
    	return null;
    }
}

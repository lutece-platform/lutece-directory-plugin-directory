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

import fr.paris.lutece.plugins.blobstore.service.BlobStoreClientException;
import fr.paris.lutece.plugins.directory.service.upload.DirectoryAsynchronousUploadHandler;
import fr.paris.lutece.plugins.directory.utils.DirectoryErrorException;
import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.portal.business.regularexpression.RegularExpression;
import fr.paris.lutece.portal.service.fileupload.FileUploadService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.regularexpression.RegularExpressionService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.web.upload.MultipartHttpServletRequest;
import fr.paris.lutece.portal.web.util.LocalizedPaginator;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.filesystem.FileSystemUtil;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.xml.XmlUtil;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 *
 * class EntryTypeDownloadUrl
 *
 */
public class EntryTypeDownloadUrl extends AbstractEntryTypeUpload
{
    // CONSTANTS
    public static final String CONSTANT_WS_REST_URL = "ws_rest_url";
    public static final String CONSTANT_BLOBSTORE = "blobstore";
    public static final String CONSTANT_OPTION = "option";

    // PARAMETERS
    private static final String PARAMETER_WS_REST_URL = "ws_rest_url";
    private static final String PARAMETER_BLOBSTORE = "blobstore";

    // TAGS
    private static final String TAG_A = "a";
    private static final String TAG_IMG = "img";

    // ATTRIBUTES
    private static final String ATTRIBUTE_HREF = "href";
    private static final String ATTRIBUTE_SRC = "src";
    private static final String ATTRIBUTE_TITLE = "title";
    private static final String ATTRIBUTE_ALT = "alt";

    // FIELDS
    private static final String FIELD_WS_REST_URL = "directory.create_entry.label_ws_rest_url";
    private static final String FIELD_BLOBSTORE = "directory.create_entry.label_blobstore";

    // URL
    private static final String URL_IMG_DOWNLOAD = "images/local/skin/plugins/directory/download.png";

    // PROPERTIES
    private static final String PROPERTY_LABEL_DOWNLOAD = "directory.viewing_directory_record.download";
    private static final String MESSAGE_ENTRY_NOT_WELL_CONFIGURED = "directory.message.error.entry_not_well_configured";

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

        return _template_html_code_form_entry;
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

        return _template_html_code_entry_value;
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

        return _template_html_code_form_search_entry;
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
        String strDocumentTitle = request.getParameter( PARAMETER_INDEXED_AS_TITLE );
        String strDocumentSummary = request.getParameter( PARAMETER_INDEXED_AS_SUMMARY );
        String strShowInAdvancedSearch = request.getParameter( PARAMETER_SHOWN_IN_ADVANCED_SEARCH );
        String strShowInResultList = request.getParameter( PARAMETER_SHOWN_IN_RESULT_LIST );
        String strShowInResultRecord = request.getParameter( PARAMETER_SHOWN_IN_RESULT_RECORD );
        String strShowInHistory = request.getParameter( PARAMETER_SHOWN_IN_HISTORY );
        String strShowInExport = request.getParameter( PARAMETER_SHOWN_IN_EXPORT );
        String strShowInCompleteness = request.getParameter( PARAMETER_SHOWN_IN_COMPLETENESS );

        String strFieldError = checkEntryData( request, locale );

        if ( StringUtils.isNotBlank( strFieldError ) )
        {
            return strFieldError;
        }

        this.setTitle( strTitle );
        this.setHelpMessage( strHelpMessage );
        this.setHelpMessageSearch( strHelpMessageSearch );
        this.setComment( strComment );

        this.setFields( request );

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
    public Paginator<RegularExpression> getPaginator( int nItemPerPage, String strBaseUrl,
        String strPageIndexParameterName, String strPageIndex )
    {
        return new Paginator<RegularExpression>( this.getFields(  ).get( 0 ).getRegularExpressionList(  ),
            nItemPerPage, strBaseUrl, strPageIndexParameterName, strPageIndex );
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
    public void getRecordFieldData( Record record, HttpServletRequest request, boolean bTestDirectoryError,
        boolean bAddNewValue, List<RecordField> listRecordField, Locale locale )
        throws DirectoryErrorException
    {
        if ( request instanceof MultipartHttpServletRequest )
        {
            // Check if the BlobStoreClientService is available
            DirectoryAsynchronousUploadHandler handler = DirectoryAsynchronousUploadHandler.getHandler(  );

            if ( !handler.isBlobStoreClientServiceAvailable(  ) )
            {
                String strErrorMessage = I18nService.getLocalizedString( MESSAGE_BLOBSTORE_CLIENT_SERVICE_UNAVAILABLE,
                        locale );
                throw new DirectoryErrorException( this.getTitle(  ), strErrorMessage );
            }

            // Get entry properties
            Field fieldWSRestUrl = DirectoryUtils.findFieldByTitleInTheList( CONSTANT_WS_REST_URL, getFields(  ) );
            Field fieldBlobStore = DirectoryUtils.findFieldByTitleInTheList( CONSTANT_BLOBSTORE, getFields(  ) );
            String strWSRestUrl = fieldWSRestUrl.getValue(  );
            String strBlobStore = fieldBlobStore.getValue(  );

            if ( bTestDirectoryError && ( StringUtils.isBlank( strWSRestUrl ) || StringUtils.isBlank( strBlobStore ) ) )
            {
                String strErrorMessage = I18nService.getLocalizedString( MESSAGE_ENTRY_NOT_WELL_CONFIGURED, locale );
                throw new DirectoryErrorException( this.getTitle(  ), strErrorMessage );
            }

            /**
             * 1) Get the files from the session
             * 2) Check if the user is uploading a file or not
             * 2A) If the user is uploading a file, the file should not be
             * stored in the blobstore yet
             * 2B) Otherwise, upload the file in blobstore :
             * 2B-1) Delete files from the blobstore
             * 2B-2) Upload files to the blostore
             */

            /** 1) Get the files from the session */
            List<FileItem> asynchronousFileItems = getFileSources( request );

            if ( ( asynchronousFileItems != null ) && !asynchronousFileItems.isEmpty(  ) )
            {
                /** 2) Check if the user is uploading a file or not */
                String strUploadAction = DirectoryAsynchronousUploadHandler.getHandler(  ).getUploadAction( request );

                if ( StringUtils.isNotBlank( strUploadAction ) )
                {
                    /**
                     * 2A) If the user is uploading a file, the file should not
                     * be stored in the blobstore yet
                     */
                    for ( FileItem fileItem : asynchronousFileItems )
                    {
                        RecordField recordField = new RecordField(  );
                        recordField.setEntry( this );
                        recordField.setFileName( fileItem.getName(  ) );
                        recordField.setFileExtension( fileItem.getContentType(  ) );
                        listRecordField.add( recordField );
                    }
                }
                else
                {
                    /** 2B) Otherwise, upload the file in blobstore : */
                    // Checks
                    if ( bTestDirectoryError )
                    {
                        this.checkRecordFieldData( asynchronousFileItems, locale );
                    }

                    /** 2B-1) Delete files from the blobstore */
                    try
                    {
                        handler.doRemoveFile( record, this, strWSRestUrl );
                    }
                    catch ( BlobStoreClientException e )
                    {
                        AppLogService.debug( e );
                    }

                    /** 2B-2) Upload files to the blostore */
                    for ( FileItem fileItem : asynchronousFileItems )
                    {
                        String strDownloadFileUrl = StringUtils.EMPTY;

                        try
                        {
                            if ( fileItem != null )
                            {
                                // Store the uploaded file in the blobstore webapp
                                String strBlobKey = handler.doUploadFile( strWSRestUrl, fileItem, strBlobStore );
                                strDownloadFileUrl = handler.getFileUrl( strWSRestUrl, strBlobStore, strBlobKey );
                            }
                        }
                        catch ( Exception e )
                        {
                            throw new DirectoryErrorException( this.getTitle(  ), e.getMessage(  ) );
                        }

                        // Add record field
                        RecordField recordField = new RecordField(  );
                        recordField.setEntry( this );
                        recordField.setValue( strDownloadFileUrl );
                        listRecordField.add( recordField );
                    }
                }
            }
            else
            {
                // No uploaded files
                if ( bTestDirectoryError && this.isMandatory(  ) )
                {
                    throw new DirectoryErrorException( this.getTitle(  ) );
                }

                // Delete files from blobstore
                try
                {
                    handler.doRemoveFile( record, this, strWSRestUrl );
                }
                catch ( BlobStoreClientException e )
                {
                    AppLogService.debug( e );
                }
            }
        }
        else
        {
            // Case if we get directly the url of the blob
            String[] listDownloadFileUrls = request.getParameterValues( Integer.toString( getIdEntry(  ) ) );

            if ( ( listDownloadFileUrls != null ) && ( listDownloadFileUrls.length > 0 ) )
            {
                for ( String strDownloadFileUrl : listDownloadFileUrls )
                {
                    RecordField recordField = new RecordField(  );
                    recordField.setEntry( this );
                    recordField.setValue( StringUtils.isNotBlank( strDownloadFileUrl ) ? strDownloadFileUrl
                                                                                       : StringUtils.EMPTY );
                    listRecordField.add( recordField );
                }
            }
        }
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
    @Override
    public boolean isSortable(  )
    {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalizedPaginator<RegularExpression> getPaginator( int nItemPerPage, String strBaseUrl,
        String strPageIndexParameterName, String strPageIndex, Locale locale )
    {
        return new LocalizedPaginator<RegularExpression>( this.getFields(  ).get( 0 ).getRegularExpressionList(  ),
            nItemPerPage, strBaseUrl, strPageIndexParameterName, strPageIndex, locale );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setFields( HttpServletRequest request, List<Field> listFields )
    {
        listFields.add( buildFieldOption( request ) );
        listFields.add( buildFieldWSRestUrl( request ) );
        listFields.add( buildFieldBlobStore( request ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String checkEntryData( HttpServletRequest request, Locale locale )
    {
        String strError = super.checkEntryData( request, locale );

        if ( StringUtils.isBlank( strError ) )
        {
            DirectoryAsynchronousUploadHandler handler = DirectoryAsynchronousUploadHandler.getHandler(  );

            if ( !handler.isBlobStoreClientServiceAvailable(  ) )
            {
                return AdminMessageService.getMessageUrl( request, MESSAGE_BLOBSTORE_CLIENT_SERVICE_UNAVAILABLE,
                    AdminMessage.TYPE_STOP );
            }

            String strFieldError = StringUtils.EMPTY;
            String strWSRestUrl = request.getParameter( PARAMETER_WS_REST_URL );
            String strBlobStore = request.getParameter( PARAMETER_BLOBSTORE );

            if ( StringUtils.isBlank( strWSRestUrl ) )
            {
                strFieldError = FIELD_WS_REST_URL;
            }

            if ( StringUtils.isBlank( strBlobStore ) )
            {
                strFieldError = FIELD_BLOBSTORE;
            }

            if ( StringUtils.isNotBlank( strFieldError ) )
            {
                Object[] tabRequiredFields = { I18nService.getLocalizedString( strFieldError, locale ) };

                return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields,
                    AdminMessage.TYPE_STOP );
            }
        }

        return strError;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void checkRecordFieldData( FileItem fileItem, Locale locale )
        throws DirectoryErrorException
    {
        // Check if the BlobStoreClientService is available
        DirectoryAsynchronousUploadHandler handler = DirectoryAsynchronousUploadHandler.getHandler(  );

        if ( !handler.isBlobStoreClientServiceAvailable(  ) )
        {
            String strErrorMessage = I18nService.getLocalizedString( MESSAGE_BLOBSTORE_CLIENT_SERVICE_UNAVAILABLE,
                    locale );
            throw new DirectoryErrorException( this.getTitle(  ), strErrorMessage );
        }

        String strFilename = ( fileItem != null ) ? FileUploadService.getFileNameOnly( fileItem ) : StringUtils.EMPTY;
        String strMimeType = FileSystemUtil.getMIMEType( strFilename );

        // Check mime type with option
        Field fieldOption = DirectoryUtils.findFieldByTitleInTheList( CONSTANT_OPTION, getFields(  ) );

        if ( ( fieldOption == null ) || StringUtils.isBlank( fieldOption.getValue(  ) ) )
        {
            throw new DirectoryErrorException( getTitle(  ) );
        }

        if ( StringUtils.isNotBlank( strFilename ) && StringUtils.isNotBlank( strMimeType ) &&
                StringUtils.isNotBlank( fieldOption.getValue(  ) ) && !ALL.equals( fieldOption.getValue(  ) ) )
        {
            String[] listAuthorizedFileExt = fieldOption.getValue(  ).split( COMMA );

            if ( ( listAuthorizedFileExt != null ) && ( listAuthorizedFileExt.length > 0 ) )
            {
                boolean bIsAuthorized = false;

                for ( String strAuthorizedFileExt : listAuthorizedFileExt )
                {
                    String strAuthorizedMimeType = FileSystemUtil.getMIMEType( DirectoryUtils.CONSTANT_DOT +
                            strAuthorizedFileExt );

                    if ( StringUtils.isNotBlank( strAuthorizedMimeType ) &&
                            strAuthorizedMimeType.equals( strMimeType ) )
                    {
                        bIsAuthorized = true;

                        break;
                    }
                }

                if ( !bIsAuthorized )
                {
                    Object[] param = { fieldOption.getValue(  ) };
                    String strErrorMessage = I18nService.getLocalizedString( DirectoryUtils.MESSAGE_DIRECTORY_ERROR_MIME_TYPE,
                            param, locale );
                    throw new DirectoryErrorException( getTitle(  ), strErrorMessage );
                }
            }
        }
    }

    // PRIVATE METHODS

    /**
     * Build the field for option
     * @param request the HTTP request
     * @return the field
     */
    private Field buildFieldOption( HttpServletRequest request )
    {
        String strValue = request.getParameter( PARAMETER_VALUE );
        String strWidth = request.getParameter( PARAMETER_WIDTH );

        /**
         * The width is used to store the max size of the files for version
         * 2.0.13 and below
         */
        int nWidth = DirectoryUtils.convertStringToInt( strWidth );

        // Field option
        Field fieldOption = DirectoryUtils.findFieldByTitleInTheList( CONSTANT_OPTION, getFields(  ) );

        if ( fieldOption == null )
        {
            fieldOption = new Field(  );
        }

        fieldOption.setEntry( this );
        fieldOption.setTitle( CONSTANT_OPTION );
        fieldOption.setValue( strValue );
        fieldOption.setWidth( nWidth );

        return fieldOption;
    }

    /**
     * Build the field for ws rest url
     * @param request the HTTP request
     * @return the field
     */
    private Field buildFieldWSRestUrl( HttpServletRequest request )
    {
        String strWSRestUrl = request.getParameter( PARAMETER_WS_REST_URL );
        Field fieldWSRestUrl = DirectoryUtils.findFieldByTitleInTheList( CONSTANT_WS_REST_URL, getFields(  ) );

        if ( fieldWSRestUrl == null )
        {
            fieldWSRestUrl = new Field(  );
        }

        fieldWSRestUrl.setEntry( this );
        fieldWSRestUrl.setTitle( CONSTANT_WS_REST_URL );
        fieldWSRestUrl.setValue( strWSRestUrl );

        return fieldWSRestUrl;
    }

    /**
     * Build the field for blobstore
     * @param request the HTTP request
     * @return the field
     */
    private Field buildFieldBlobStore( HttpServletRequest request )
    {
        String strBlobStore = request.getParameter( PARAMETER_BLOBSTORE );
        Field fieldBlobStore = DirectoryUtils.findFieldByTitleInTheList( CONSTANT_BLOBSTORE, getFields(  ) );

        if ( fieldBlobStore == null )
        {
            fieldBlobStore = new Field(  );
        }

        fieldBlobStore.setEntry( this );
        fieldBlobStore.setTitle( CONSTANT_BLOBSTORE );
        fieldBlobStore.setValue( strBlobStore );

        return fieldBlobStore;
    }
}

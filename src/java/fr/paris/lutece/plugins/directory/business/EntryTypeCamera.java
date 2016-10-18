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

import fr.paris.lutece.plugins.directory.utils.DirectoryErrorException;
import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.portal.business.regularexpression.RegularExpression;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.regularexpression.RegularExpressionService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.web.upload.MultipartHttpServletRequest;
import fr.paris.lutece.portal.web.util.LocalizedPaginator;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.filesystem.FileSystemUtil;
import fr.paris.lutece.util.html.Paginator;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;


/**
 *
 * class EntryTypeCamera
 *
 */
public class EntryTypeCamera extends AbstractEntryTypeUpload
{
	private String PROPERTY_IMAGE_TITLE = AppPropertiesService.getProperty( "directory.image.prefix.title",
            "default" );
    protected static final String FIELD_THUMBNAIL_WIDTH = "directory.create_entry.label_width";
    protected static final String FIELD_THUMBNAIL_HEIGHT = "directory.create_entry.label_width";
    protected static final String FIELD_BIG_THUMBNAIL_WIDTH = "directory.create_entry.label_width";
    protected static final String FIELD_BIG_THUMBNAIL_HEIGHT = "directory.create_entry.label_width";
    protected static final String ERROR_FIELD_THUMBNAIL = "directory.create_entry.label_error_thumbnail";
    protected static final String ERROR_FIELD_BIG_THUMBNAIL = "directory.create_entry.label_error_big_thumbnail";
    private static final String PARAMETER_IMAGE_SHOWN_IN_RESULT_LIST = "image_shown_in_result_list";
    private static final String PARAMETER_IMAGE_SHOWN_IN_RESULT_RECORD = "image_shown_in_result_record";
    private static final String PARAMETER_IMAGE_TYPE = "image_type";
    private static final String MESSAGE_ERROR_NOT_AN_IMAGE = "directory.message.error.notAnImage";
    private static final String FIELD_IMAGE = "image_full_size";
    private final String PREFIX_ENTRY_ID = "directory_";
    private final String _template_create = "admin/plugins/directory/entrytypecamera/create_entry_type_camera.html";
    private final String _template_modify = "admin/plugins/directory/entrytypecamera/modify_entry_type_camera.html";
    private final String _template_html_code_form_entry = "admin/plugins/directory/entrytypecamera/html_code_form_entry_type_camera.html";
    private final String _template_html_code_entry_value = "admin/plugins/directory/entrytypecamera/html_code_entry_value_type_camera.html";
    private final String _template_html_front_code_form_entry = "skin/plugins/directory/entrytypecamera/html_code_form_entry_type_camera.html";
    private final String _template_html_front_code_entry_value = "skin/plugins/directory/entrytypecamera/html_code_entry_value_type_camera.html";

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
    public String getEntryData( HttpServletRequest request, Locale locale )
    {
        String strTitle = request.getParameter( PARAMETER_TITLE );
        String strHelpMessage = ( request.getParameter( PARAMETER_HELP_MESSAGE ) != null )
            ? request.getParameter( PARAMETER_HELP_MESSAGE ).trim(  ) : null;
        String strComment = request.getParameter( PARAMETER_COMMENT );
        String strMandatory = request.getParameter( PARAMETER_MANDATORY );
        String strIndexed = request.getParameter( PARAMETER_INDEXED );
        String strIndexedAsTitle = request.getParameter( PARAMETER_INDEXED_AS_TITLE );
        String strIndexedAsSummary = request.getParameter( PARAMETER_INDEXED_AS_SUMMARY );

        String strWidth = request.getParameter( PARAMETER_WIDTH );
        String strHeight = request.getParameter( PARAMETER_HEIGHT );

        int nWidth = DirectoryUtils.convertStringToInt( strWidth );
        int nHeight = DirectoryUtils.convertStringToInt( strHeight );

        String strShowInFormMainSearch = request.getParameter( PARAMETER_SHOWN_IN_ADVANCED_SEARCH );
        String strShowInResultList = request.getParameter( PARAMETER_SHOWN_IN_RESULT_LIST );
        String strShowInResultRecord = request.getParameter( PARAMETER_SHOWN_IN_RESULT_RECORD );
        String strShowInHistory = request.getParameter( PARAMETER_SHOWN_IN_HISTORY );
        String strShowInCompleteness = request.getParameter( PARAMETER_SHOWN_IN_COMPLETENESS );

        String strError = this.checkEntryData( request, locale );

        if ( StringUtils.isNotBlank( strError ) )
        {
            return strError;
        }

        this.setTitle( strTitle );
        this.setHelpMessage( strHelpMessage );
        this.setComment( strComment );

        this.setFields( request );

        this.setDisplayWidth( nWidth );
        this.setDisplayHeight( nHeight );

        this.setMandatory( strMandatory != null );
        this.setIndexed( strIndexed != null );
        this.setIndexedAsTitle( strIndexedAsTitle != null );
        this.setIndexedAsSummary( strIndexedAsSummary != null );
        this.setShownInAdvancedSearch( strShowInFormMainSearch != null );
        this.setShownInResultList( strShowInResultList != null );
        this.setShownInResultRecord( strShowInResultRecord != null );
        this.setShownInHistory( strShowInHistory != null );
        this.setShownInCompleteness( strShowInCompleteness != null );
        //image can't be exported in csv
        this.setShownInExport( false );

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
        //add Empty recordField(Use for data import)
        RecordField recordField = new RecordField(  );
        recordField.setEntry( this );
        listRecordField.add( recordField );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getImportRecordFieldData( Record record, String strImportValue, boolean bTestDirectoryError,
        List<RecordField> listRecordField, Locale locale )
        throws DirectoryErrorException
    {
        // add Empty recordField
        RecordField recordField = new RecordField(  );
        recordField.setEntry( this );
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
        if ( request instanceof MultipartHttpServletRequest )
        {
        	
            String sourceImage = request.getParameter( PREFIX_ENTRY_ID + this.getIdEntry(  ) );

            if ( ( sourceImage != null ) && StringUtils.isNotEmpty( sourceImage ) )
            {
                // Checks
                if ( bTestDirectoryError )
                {
                    this.checkRecordFieldData( sourceImage, locale );
                }

                File file = new File(  );
                Calendar c = Calendar.getInstance(  );
                String fileName = request.getParameter( PROPERTY_IMAGE_TITLE );
               
                if( fileName != null ){
                	
                	file.setTitle( fileName + "_" + c.getTime(  ) );
                
                }else{
                
                	file.setTitle( this.getTitle( ) + "_" + c.getTime(  ) );
                }
                PhysicalFile physicalFile = new PhysicalFile(  );
                String base64Image = sourceImage.split( "," )[1];
                byte[] imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary( base64Image );
                physicalFile.setValue( imageBytes );
                file.setPhysicalFile( physicalFile );
                file.setTitle( this.getFields().get(2).getImageType( )!=null?file.getTitle(  )+
                		"."+this.getFields().get(2).getImageType( ):file.getTitle(  )+"" );
                file.setMimeType( FileSystemUtil.getMIMEType( file.getTitle(  ) ) );

                ByteArrayInputStream bis = new ByteArrayInputStream( imageBytes );
                ByteArrayOutputStream tmp = new ByteArrayOutputStream(  );

                try
                {
                    BufferedImage image = ImageIO.read( bis );
                    ImageIO.write( image, this.getFields().get(2).getImageType( )!=null?this.getFields().get(2).getImageType( ):"png", tmp );
                    bis.close(  );
                    tmp.close(  );
                    file.setSize( tmp.size(  ) );
                }
                catch ( IOException e )
                {
                    AppLogService.error( e );
                    throw new DirectoryErrorException( this.getTitle(  ) );
                }

                //Add the image to the record fields list
                RecordField recordField = new RecordField(  );
                recordField.setEntry( this );
                recordField.setValue( FIELD_IMAGE + DirectoryUtils.CONSTANT_UNDERSCORE + 1 );
                recordField.setFile( file );
                listRecordField.add( recordField );
            }

            if ( bTestDirectoryError && this.isMandatory(  ) &&
                    ( ( sourceImage == null ) || StringUtils.isEmpty( sourceImage ) ) )
            {
                RecordField recordField = new RecordField(  );
                recordField.setEntry( this );
                recordField.setValue( FIELD_IMAGE );
                listRecordField.add( recordField );

                throw new DirectoryErrorException( this.getTitle(  ) );
            }
        }
        else if ( bTestDirectoryError )
        {
            throw new DirectoryErrorException( this.getTitle(  ) );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Paginator getPaginator( int nItemPerPage, String strBaseUrl, String strPageIndexParameterName,
        String strPageIndex )
    {
        return new Paginator( this.getFields(  ).get( 0 ).getRegularExpressionList(  ), nItemPerPage, strBaseUrl,
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
    public LocalizedPaginator getPaginator( int nItemPerPage, String strBaseUrl, String strPageIndexParameterName,
        String strPageIndex, Locale locale )
    {
        return new LocalizedPaginator( this.getFields(  ).get( 0 ).getRegularExpressionList(  ), nItemPerPage,
            strBaseUrl, strPageIndexParameterName, strPageIndex, locale );
    }

    // PROTECTED METHODS

    /**
     * {@inheritDoc}
     */
    @Override
    protected String checkEntryData( HttpServletRequest request, Locale locale )
    {
        String strError = super.checkEntryData( request, locale );

        if ( StringUtils.isBlank( strError ) )
        {
            String strFieldError = StringUtils.EMPTY;
            String strTitle = request.getParameter( PARAMETER_TITLE );

            String strWidth = request.getParameter( PARAMETER_WIDTH );
            String strHeight = request.getParameter( PARAMETER_HEIGHT );

            int nWidth = DirectoryUtils.convertStringToInt( strWidth );
            int nHeight = DirectoryUtils.convertStringToInt( strHeight );

            if ( StringUtils.isBlank( strTitle ) )
            {
                strFieldError = FIELD_TITLE;
            }

            if ( !strFieldError.equals( DirectoryUtils.EMPTY_STRING ) )
            {
                Object[] tabRequiredFields = { I18nService.getLocalizedString( strFieldError, locale ) };

                return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields,
                    AdminMessage.TYPE_STOP );
            }

            if ( ( strWidth != null ) && ( !strWidth.trim(  ).equals( DirectoryUtils.EMPTY_STRING ) ) &&
                    ( nWidth == DirectoryUtils.CONSTANT_ID_NULL ) )
            {
                strFieldError = FIELD_WIDTH;
            }
            else if ( ( strHeight != null ) && ( !strHeight.trim(  ).equals( DirectoryUtils.EMPTY_STRING ) ) &&
                    ( nHeight == DirectoryUtils.CONSTANT_ID_NULL ) )
            {
                strFieldError = FIELD_HEIGHT;
            }

            if ( !strFieldError.equals( DirectoryUtils.EMPTY_STRING ) )
            {
                Object[] tabRequiredFields = { I18nService.getLocalizedString( strFieldError, locale ) };

                return AdminMessageService.getMessageUrl( request, MESSAGE_NUMERIC_FIELD, tabRequiredFields,
                    AdminMessage.TYPE_STOP );
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
    protected void setFields( HttpServletRequest request, List<Field> listFields )
    {
        listFields.add( buildFieldFullSize( request ) );
    }

    private void checkRecordFieldData( String imageSource, Locale locale )
        throws DirectoryErrorException
    {
        BufferedImage image = null;

        if ( ( imageSource != null ) && ( imageSource.split( "," ).length > 1 ) )
        {
            String base64Image = imageSource.split( "," )[1];
            byte[] imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary( base64Image );
            ByteArrayInputStream bis = new ByteArrayInputStream( imageBytes );

            try
            {
                image = ImageIO.read( bis );
                bis.close(  );
            }
            catch ( IOException e )
            {
                String strErrorMessage = I18nService.getLocalizedString( MESSAGE_ERROR_NOT_AN_IMAGE, locale );
                throw new DirectoryErrorException( this.getTitle(  ), strErrorMessage );
            }
        }

        if ( ( image == null ) && StringUtils.isNotBlank( imageSource ) )
        {
            String strErrorMessage = I18nService.getLocalizedString( MESSAGE_ERROR_NOT_AN_IMAGE, locale );
            throw new DirectoryErrorException( this.getTitle(  ), strErrorMessage );
        }
    }

    // PRIVATE METHODS

    /**
     * Build the field full size
     * @param request the HTTP request
     * @return the field
     */
    private Field buildFieldFullSize( HttpServletRequest request )
    {
        Field fieldFullImage = DirectoryUtils.findFieldByValueInTheList( FIELD_IMAGE, getFields(  ) );

        if ( fieldFullImage == null )
        {
            fieldFullImage = new Field(  );
        }

        if ( request.getParameter( PARAMETER_IMAGE_SHOWN_IN_RESULT_LIST ) != null )
        {
            fieldFullImage.setShownInResultList( true );
        }
        else
        {
            fieldFullImage.setShownInResultList( false );
        }

        if ( request.getParameter( PARAMETER_IMAGE_SHOWN_IN_RESULT_RECORD ) != null )
        {
            fieldFullImage.setShownInResultRecord( true );
        }
        else
        {
            fieldFullImage.setShownInResultRecord( false );
        }
        if(request.getParameter( PARAMETER_IMAGE_TYPE ) != null){
        	fieldFullImage.setImageType(request.getParameter( PARAMETER_IMAGE_TYPE ));
        }
        fieldFullImage.setEntry( this );
        fieldFullImage.setValue( FIELD_IMAGE );
        

        return fieldFullImage;
    }

    @Override
    protected void checkRecordFieldData( FileItem fileItem, Locale locale )
        throws DirectoryErrorException
    {
    }
}

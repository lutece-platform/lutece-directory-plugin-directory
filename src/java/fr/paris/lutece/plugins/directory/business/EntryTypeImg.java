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

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.directory.service.DirectoryPlugin;
import fr.paris.lutece.plugins.directory.utils.DirectoryErrorException;
import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.portal.business.regularexpression.RegularExpression;
import fr.paris.lutece.portal.service.fileupload.FileUploadService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.regularexpression.RegularExpressionService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.web.upload.MultipartHttpServletRequest;
import fr.paris.lutece.portal.web.util.LocalizedPaginator;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.filesystem.FileSystemUtil;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.image.ImageUtil;


/**
 *
 * class EntryTypeImg
 *
 */
public class EntryTypeImg extends AbstractEntryTypeUpload
{
    private static final String PARAMETER_THUMBNAIL_HEIGHT = "thumbnail_height";
    private static final String PARAMETER_THUMBNAIL_WIDTH = "thumbnail_width";
    private static final String PARAMETER_BIG_THUMBNAIL_WIDTH = "thumbnail_big_width";
    private static final String PARAMETER_BIG_THUMBNAIL_HEIGHT = "thumbnail_big_height";
    private static final String PARAMETER_IMAGE_SHOWN_IN_RESULT_LIST = "image_shown_in_result_list";
    private static final String PARAMETER_IMAGE_SHOWN_IN_RESULT_RECORD = "image_shown_in_result_record";
    private static final String PARAMETER_THUMBNAIL_SHOWN_IN_RESULT_LIST = "thbnail_shown_in_result_list";
    private static final String PARAMETER_THUMBNAIL_SHOWN_IN_RESULT_RECORD = "thbnail_shown_in_result_record";
    private static final String PARAMETER_BIG_THUMBNAIL_SHOWN_IN_RESULT_LIST = "big_thbnail_shown_in_result_list";
    private static final String PARAMETER_BIG_THUMBNAIL_SHOWN_IN_RESULT_RECORD = "big_thbnail_shown_in_result_record";
    private static final String PARAMETER_CREATE_THUMBNAIL = "create_thumbnail";
    private static final String PARAMETER_CREATE_BIG_THUMBNAIL = "create_big_thumbnail";
    protected static final String FIELD_THUMBNAIL_WIDTH = "directory.create_entry.label_width";
    protected static final String FIELD_THUMBNAIL_HEIGHT = "directory.create_entry.label_width";
    protected static final String FIELD_BIG_THUMBNAIL_WIDTH = "directory.create_entry.label_width";
    protected static final String FIELD_BIG_THUMBNAIL_HEIGHT = "directory.create_entry.label_width";
    protected static final String ERROR_FIELD_THUMBNAIL = "directory.create_entry.label_error_thumbnail";
    protected static final String ERROR_FIELD_BIG_THUMBNAIL = "directory.create_entry.label_error_big_thumbnail";
    private static final String MESSAGE_ERROR_NOT_AN_IMAGE = "directory.message.error.notAnImage";
    private static final String FIELD_IMAGE = "image_full_size";
    private static final String FIELD_THUMBNAIL = "little_thumbnail";
    private static final String FIELD_BIG_THUMBNAIL = "big_thumbnail";
    private static final int INTEGER_QUALITY_MAXIMUM = 1;
    private final String _template_create = "admin/plugins/directory/entrytypeimg/create_entry_type_img.html";
    private final String _template_modify = "admin/plugins/directory/entrytypeimg/modify_entry_type_img.html";
    private final String _template_html_code_form_entry = "admin/plugins/directory/entrytypeimg/html_code_form_entry_type_img.html";
    private final String _template_html_code_entry_value = "admin/plugins/directory/entrytypeimg/html_code_entry_value_type_img.html";
    private final String _template_html_front_code_form_entry = "skin/plugins/directory/entrytypeimg/html_code_form_entry_type_img.html";
    private final String _template_html_front_code_entry_value = "skin/plugins/directory/entrytypeimg/html_code_entry_value_type_img.html";

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
        String strIndexedAsSummary = request.getParameter( PARAMETER_INDEXED_AS_SUMMARY );

        String strDisplayWidth = request.getParameter( PARAMETER_DISPLAY_WIDTH );
        String strDisplayHeight = request.getParameter( PARAMETER_DISPLAY_HEIGHT );

        //used for display image       
        int nDisplayWidth = DirectoryUtils.convertStringToInt( strDisplayWidth );
        int nDisplayHeight = DirectoryUtils.convertStringToInt( strDisplayHeight );

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

        this.setDisplayWidth( nDisplayWidth );
        this.setDisplayHeight( nDisplayHeight );

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
			List<FileItem> asynchronousFileItem = getFileSources( request );
    		
    		if ( asynchronousFileItem != null && !asynchronousFileItem.isEmpty(  ) )
            {
    			// Checks
				if ( bTestDirectoryError )
				{
					this.checkRecordFieldData( asynchronousFileItem, locale );
				}
				// The index is used to distinguish the thumbnails of one image from another
				int nIndex = 0;
            	for ( FileItem fileItem : asynchronousFileItem )
            	{
            		String strFilename = fileItem != null ? FileUploadService.getFileNameOnly( fileItem ) : StringUtils.EMPTY;
            		
            		if ( fileItem != null && fileItem.get(  ) != null && fileItem.getSize(  ) < Integer.MAX_VALUE )
            		{
            			PhysicalFile physicalFile = new PhysicalFile(  );
            			physicalFile.setValue( fileItem.get(  ) );
            			
            			File file = new File(  );
            			file.setPhysicalFile( physicalFile );
            			file.setTitle( strFilename );
            			file.setSize( (int) fileItem.getSize(  ) );
            			file.setMimeType( FileSystemUtil.getMIMEType( strFilename ) );
            			
            			//Add the image to the record fields list
            			RecordField recordField = new RecordField(  );
            	        recordField.setEntry( this );
            	        recordField.setValue( FIELD_IMAGE + DirectoryUtils.CONSTANT_UNDERSCORE + nIndex );
            	        recordField.setFile( file );
            	        
        	            Field fullsizedField = FieldHome.findByValue( this.getIdEntry(  ), FIELD_IMAGE,
        	                    PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME ) );

        	            if ( fullsizedField != null )
        	            {
        	                recordField.setField( fullsizedField );
        	            }

        	            listRecordField.add( recordField );

        	            //Create thumbnails records
        	            File imageFile = recordField.getFile(  );
        	            Field thumbnailField = FieldHome.findByValue( this.getIdEntry(  ), FIELD_THUMBNAIL,
        	                    PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME ) );

        	            if ( thumbnailField != null )
        	            {
        	                byte[] resizedImage = ImageUtil.resizeImage( imageFile.getPhysicalFile(  ).getValue(  ),
        	                        String.valueOf( thumbnailField.getWidth(  ) ), String.valueOf( thumbnailField.getHeight(  ) ),
        	                        INTEGER_QUALITY_MAXIMUM );

        	                RecordField thbnailRecordField = new RecordField(  );
        	                thbnailRecordField.setEntry( this );

        	                PhysicalFile thbnailPhysicalFile = new PhysicalFile(  );
        	                thbnailPhysicalFile.setValue( resizedImage );

        	                File thbnailFile = new File(  );
        	                thbnailFile.setTitle( imageFile.getTitle(  ) );
        	                thbnailFile.setExtension( imageFile.getExtension(  ) );

        	                if ( ( imageFile.getExtension(  ) != null ) && ( imageFile.getTitle(  ) != null ) )
        	                {
        	                    thbnailFile.setMimeType( FileSystemUtil.getMIMEType( imageFile.getTitle(  ) ) );
        	                }

        	                thbnailFile.setPhysicalFile( thbnailPhysicalFile );
        	                thbnailFile.setSize( resizedImage.length );

        	                thbnailRecordField.setFile( thbnailFile );

        	                thbnailRecordField.setRecord( record );
        	                thbnailRecordField.setValue( FIELD_THUMBNAIL +  DirectoryUtils.CONSTANT_UNDERSCORE + nIndex );
        	                thbnailRecordField.setField( thumbnailField );
        	                listRecordField.add( thbnailRecordField );
        	            }

        	            Field bigThumbnailField = FieldHome.findByValue( this.getIdEntry(  ), FIELD_BIG_THUMBNAIL,
        	                    PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME ) );

        	            if ( bigThumbnailField != null )
        	            {
        	                byte[] resizedImage = ImageUtil.resizeImage( imageFile.getPhysicalFile(  ).getValue(  ),
        	                        String.valueOf( bigThumbnailField.getWidth(  ) ),
        	                        String.valueOf( bigThumbnailField.getHeight(  ) ), INTEGER_QUALITY_MAXIMUM );

        	                RecordField bigThbnailRecordField = new RecordField(  );
        	                bigThbnailRecordField.setEntry( this );

        	                PhysicalFile thbnailPhysicalFile = new PhysicalFile(  );
        	                thbnailPhysicalFile.setValue( resizedImage );

        	                File thbnailFile = new File(  );
        	                thbnailFile.setTitle( imageFile.getTitle(  ) );
        	                thbnailFile.setExtension( imageFile.getExtension(  ) );

        	                if ( ( imageFile.getExtension(  ) != null ) && ( imageFile.getTitle(  ) != null ) )
        	                {
        	                    thbnailFile.setMimeType( FileSystemUtil.getMIMEType( imageFile.getTitle(  ) ) );
        	                }

        	                thbnailFile.setPhysicalFile( thbnailPhysicalFile );
        	                thbnailFile.setSize( resizedImage.length );

        	                bigThbnailRecordField.setFile( thbnailFile );

        	                bigThbnailRecordField.setRecord( record );
        	                bigThbnailRecordField.setValue( FIELD_BIG_THUMBNAIL + DirectoryUtils.CONSTANT_UNDERSCORE + nIndex );
        	                bigThbnailRecordField.setField( bigThumbnailField );
        	                listRecordField.add( bigThbnailRecordField );
        	            }
            		}
            		nIndex++;
            	}
            }
    		if ( bTestDirectoryError && this.isMandatory(  ) && ( asynchronousFileItem == null || asynchronousFileItem.isEmpty(  ) ) )
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
    		String strCreateThumbnail = request.getParameter( PARAMETER_CREATE_THUMBNAIL );
    		String strCreateBigThumbnail = request.getParameter( PARAMETER_CREATE_BIG_THUMBNAIL );
    		
    		String strDisplayWidth = request.getParameter( PARAMETER_DISPLAY_WIDTH );
    		String strDisplayHeight = request.getParameter( PARAMETER_DISPLAY_HEIGHT );
    		String strThumbnailWidth = request.getParameter( PARAMETER_THUMBNAIL_WIDTH );
    		String strThumbnailHeight = request.getParameter( PARAMETER_THUMBNAIL_HEIGHT );
    		String strBigThumbnailWidth = request.getParameter( PARAMETER_BIG_THUMBNAIL_WIDTH );
    		String strBigThumbnailHeight = request.getParameter( PARAMETER_BIG_THUMBNAIL_HEIGHT );
    		
    		int nDisplayWidth = DirectoryUtils.convertStringToInt( strDisplayWidth );
    		int nDisplayHeight = DirectoryUtils.convertStringToInt( strDisplayHeight );
    		int nThumbnailWidth = DirectoryUtils.CONSTANT_ID_NULL;
    		int nThumbnailHeight = DirectoryUtils.CONSTANT_ID_NULL;
    		int nBigThumbnailHeight = DirectoryUtils.CONSTANT_ID_NULL;
    		int nBigThumbnailWidth = DirectoryUtils.CONSTANT_ID_NULL;
    		
    		if ( strThumbnailWidth != null )
    		{
    			nThumbnailWidth = DirectoryUtils.convertStringToInt( strThumbnailWidth );
    		}
    		
    		if ( strThumbnailHeight != null )
    		{
    			nThumbnailHeight = DirectoryUtils.convertStringToInt( strThumbnailHeight );
    		}
    		
    		if ( strBigThumbnailWidth != null )
    		{
    			nBigThumbnailWidth = DirectoryUtils.convertStringToInt( strBigThumbnailWidth );
    		}
    		
    		if ( strBigThumbnailHeight != null )
    		{
    			nBigThumbnailHeight = DirectoryUtils.convertStringToInt( strBigThumbnailHeight );
    		}
    		
    		if ( StringUtils.isBlank( strTitle ) )
    		{
    			strFieldError = FIELD_TITLE;
    		}
    		else if ( ( strCreateThumbnail != null ) && ( strThumbnailWidth == null ) )
    		{
    			strFieldError = FIELD_THUMBNAIL_WIDTH;
    		}
    		else if ( ( strCreateThumbnail != null ) && ( strThumbnailHeight == null ) )
    		{
    			strFieldError = FIELD_THUMBNAIL_HEIGHT;
    		}
    		else if ( ( strCreateBigThumbnail != null ) && ( strThumbnailHeight == null ) )
    		{
    			strFieldError = FIELD_BIG_THUMBNAIL_HEIGHT;
    		}
    		else if ( ( strCreateBigThumbnail != null ) && ( strThumbnailWidth == null ) )
    		{
    			strFieldError = FIELD_BIG_THUMBNAIL_WIDTH;
    		}
    		
    		if ( !strFieldError.equals( DirectoryUtils.EMPTY_STRING ) )
    		{
    			Object[] tabRequiredFields = { I18nService.getLocalizedString( strFieldError, locale ) };
    			
    			return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields,
    					AdminMessage.TYPE_STOP );
    		}
    		
    		if ( ( strDisplayWidth != null ) && ( !strDisplayWidth.trim(  ).equals( DirectoryUtils.EMPTY_STRING ) ) &&
    				( nDisplayWidth == DirectoryUtils.CONSTANT_ID_NULL ) )
    		{
    			strFieldError = FIELD_WIDTH_DISPLAY;
    		}
    		else if ( ( strDisplayHeight != null ) && ( !strDisplayHeight.trim(  ).equals( DirectoryUtils.EMPTY_STRING ) ) &&
    				( nDisplayHeight == DirectoryUtils.CONSTANT_ID_NULL ) )
    		{
    			strFieldError = FIELD_HEIGHT_DISPLAY;
    		}
    		
    		if ( !strFieldError.equals( DirectoryUtils.EMPTY_STRING ) )
    		{
    			Object[] tabRequiredFields = { I18nService.getLocalizedString( strFieldError, locale ) };
    			
    			return AdminMessageService.getMessageUrl( request, MESSAGE_NUMERIC_FIELD, tabRequiredFields,
    					AdminMessage.TYPE_STOP );
    		}
    		
    		if ( ( strCreateThumbnail != null ) && ( ( nThumbnailWidth <= 0 ) || ( nThumbnailHeight <= 0 ) ) )
    		{
    			strError = AdminMessageService.getMessageUrl( request, ERROR_FIELD_THUMBNAIL, AdminMessage.TYPE_STOP );
    		}
    		
    		if ( ( strCreateBigThumbnail != null ) && ( ( nBigThumbnailWidth <= 0 ) || ( nBigThumbnailHeight <= 0 ) ) )
    		{
    			strError = AdminMessageService.getMessageUrl( request, ERROR_FIELD_BIG_THUMBNAIL, AdminMessage.TYPE_STOP );
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
		String strCreateThumbnail = request.getParameter( PARAMETER_CREATE_THUMBNAIL );
		String strCreateBigThumbnail = request.getParameter( PARAMETER_CREATE_BIG_THUMBNAIL );
        listFields.add( buildFieldFullSize( request ) );

        if ( strCreateThumbnail != null )
        {
        	// If the checkbox to create a thumbnail is checked, then add the associated field
        	listFields.add( buildFieldThumbnail( request ) );
        }
        else
        {
        	// Otherwise, remove from db
        	Field fieldThumbnail = DirectoryUtils.findFieldByValueInTheList( FIELD_THUMBNAIL, getFields(  ) );
    		if ( fieldThumbnail != null )
    		{
    			RecordFieldFilter filter = new RecordFieldFilter(  );
                filter.setIdField( fieldThumbnail.getIdField(  ) );
    			FieldHome.remove( fieldThumbnail.getIdField(  ), DirectoryUtils.getPlugin(  ) );
                RecordFieldHome.removeByFilter( filter, DirectoryUtils.getPlugin(  ) );
    		}
        }

        if ( strCreateBigThumbnail != null )
        {
        	// If the checkbox to create a big thumbnail is checked, then add the associated field
            listFields.add( buildFieldBigThumbnail( request ) );
        }
        else
        {
        	// Otherwise, remove from db
        	Field fieldBigThumbnail = DirectoryUtils.findFieldByValueInTheList( FIELD_BIG_THUMBNAIL, getFields(  ) );
    		if ( fieldBigThumbnail != null )
    		{
    			RecordFieldFilter filter = new RecordFieldFilter(  );
                filter.setIdField( fieldBigThumbnail.getIdField(  ) );
    			FieldHome.remove( fieldBigThumbnail.getIdField(  ), DirectoryUtils.getPlugin(  ) );
                RecordFieldHome.removeByFilter( filter, DirectoryUtils.getPlugin(  ) );
    		}
        }
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void checkRecordFieldData( FileItem fileItem, Locale locale )
		throws DirectoryErrorException
	{
		String strFilename = fileItem != null ? FileUploadService.getFileNameOnly( fileItem ) : StringUtils.EMPTY;
		BufferedImage image = null;
		try
		{
			if( fileItem != null && fileItem.get(  ) != null )
			{
				image = ImageIO.read( new ByteArrayInputStream( fileItem.get(  ) ) );
			}
		} 
		catch ( IOException e )
		{			
			AppLogService.error( e );
		}
		
		if ( ( image == null ) &&  StringUtils.isNotBlank( strFilename ) )
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

        if ( request.getParameter( PARAMETER_IMAGE_SHOWN_IN_RESULT_RECORD ) != null )
        {
        	fieldFullImage.setShownInResultRecord( true );
        }

        fieldFullImage.setEntry( this );
        fieldFullImage.setValue( FIELD_IMAGE );
        
        return fieldFullImage;
	}
	
	/**
	 * Build the field thumbnail
	 * @param request the HTTP request
	 * @return the field
	 */
	private Field buildFieldThumbnail( HttpServletRequest request )
	{
		String strThumbnailWidth = request.getParameter( PARAMETER_THUMBNAIL_WIDTH );
		String strThumbnailHeight = request.getParameter( PARAMETER_THUMBNAIL_HEIGHT );
		int nThumbnailWidth = DirectoryUtils.CONSTANT_ID_NULL;
		int nThumbnailHeight = DirectoryUtils.CONSTANT_ID_NULL;
		if ( strThumbnailWidth != null )
        {
            nThumbnailWidth = DirectoryUtils.convertStringToInt( strThumbnailWidth );
        }

        if ( strThumbnailHeight != null )
        {
            nThumbnailHeight = DirectoryUtils.convertStringToInt( strThumbnailHeight );
        }
		
		Field fieldThumbnail = DirectoryUtils.findFieldByValueInTheList( FIELD_THUMBNAIL, getFields(  ) );
		if ( fieldThumbnail == null )
		{
			fieldThumbnail = new Field(  );
		}
		fieldThumbnail.setWidth( nThumbnailWidth );
		fieldThumbnail.setHeight( nThumbnailHeight );
		fieldThumbnail.setValue( FIELD_THUMBNAIL );
		fieldThumbnail.setEntry( this );
    	fieldThumbnail.setShownInResultList( request.getParameter( PARAMETER_THUMBNAIL_SHOWN_IN_RESULT_LIST ) != null );
    	fieldThumbnail.setShownInResultRecord( request.getParameter( PARAMETER_THUMBNAIL_SHOWN_IN_RESULT_RECORD ) != null );
        
        return fieldThumbnail;
	}
	
	/**
	 * Build the field for big thumbnail
	 * @param request the HTTP request
	 * @return the field
	 */
	private Field buildFieldBigThumbnail( HttpServletRequest request )
	{
		String strBigThumbnailWidth = request.getParameter( PARAMETER_BIG_THUMBNAIL_WIDTH );
		String strBigThumbnailHeight = request.getParameter( PARAMETER_BIG_THUMBNAIL_HEIGHT );
		
		int nBigThumbnailHeight = DirectoryUtils.CONSTANT_ID_NULL;
		int nBigThumbnailWidth = DirectoryUtils.CONSTANT_ID_NULL;

        if ( strBigThumbnailWidth != null )
        {
            nBigThumbnailWidth = DirectoryUtils.convertStringToInt( strBigThumbnailWidth );
        }

        if ( strBigThumbnailHeight != null )
        {
            nBigThumbnailHeight = DirectoryUtils.convertStringToInt( strBigThumbnailHeight );
        }
        
		Field bigThumbnailField = DirectoryUtils.findFieldByValueInTheList( FIELD_BIG_THUMBNAIL, getFields(  ) );
		if ( bigThumbnailField == null )
		{
			bigThumbnailField = new Field(  );
		}
        bigThumbnailField.setWidth( nBigThumbnailWidth );
        bigThumbnailField.setHeight( nBigThumbnailHeight );
        bigThumbnailField.setValue( FIELD_BIG_THUMBNAIL );
        bigThumbnailField.setEntry( this );
        bigThumbnailField.setShownInResultList( request.getParameter( PARAMETER_BIG_THUMBNAIL_SHOWN_IN_RESULT_LIST ) != null );
        bigThumbnailField.setShownInResultRecord( request.getParameter( PARAMETER_BIG_THUMBNAIL_SHOWN_IN_RESULT_RECORD ) != null );
        
        return bigThumbnailField;
	}
}

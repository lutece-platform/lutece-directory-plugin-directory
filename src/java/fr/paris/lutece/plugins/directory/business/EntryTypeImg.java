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
import fr.paris.lutece.portal.business.regularexpression.RegularExpression;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.regularexpression.RegularExpressionService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.web.util.LocalizedPaginator;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.filesystem.FileSystemUtil;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.image.ImageUtil;

import java.io.ByteArrayInputStream;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.imageio.ImageIO;

import javax.servlet.http.HttpServletRequest;


/**
 *
 * class EntryTypeImg
 *
 */
public class EntryTypeImg extends Entry
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
    public String getEntryData( HttpServletRequest request, Locale locale )
    {
        String strTitle = request.getParameter( PARAMETER_TITLE );
        String strHelpMessage = ( request.getParameter( PARAMETER_HELP_MESSAGE ) != null )
            ? request.getParameter( PARAMETER_HELP_MESSAGE ).trim(  ) : null;
        String strComment = request.getParameter( PARAMETER_COMMENT );
        String strMandatory = request.getParameter( PARAMETER_MANDATORY );
        String strIndexed = request.getParameter( PARAMETER_INDEXED );
        String strCreateThumbnail = request.getParameter( PARAMETER_CREATE_THUMBNAIL );
        String strCreateBigThumbnail = request.getParameter( PARAMETER_CREATE_BIG_THUMBNAIL );

        String strDisplayWidth = request.getParameter( PARAMETER_DISPLAY_WIDTH );
        String strDisplayHeight = request.getParameter( PARAMETER_DISPLAY_HEIGHT );
        String strThumbnailWidth = request.getParameter( PARAMETER_THUMBNAIL_WIDTH );
        String strThumbnailHeight = request.getParameter( PARAMETER_THUMBNAIL_HEIGHT );
        String strBigThumbnailWidth = request.getParameter( PARAMETER_BIG_THUMBNAIL_WIDTH );
        String strBigThumbnailHeight = request.getParameter( PARAMETER_BIG_THUMBNAIL_HEIGHT );

        //used for display image       
        int nDisplayWidth = DirectoryUtils.convertStringToInt( strDisplayWidth );
        int nDisplayHeight = DirectoryUtils.convertStringToInt( strDisplayHeight );
        int nThumbnailWidth = -1;
        int nThumbnailHeight = -1;
        int nBigThumbnailHeight = -1;
        int nBigThumbnailWidth = -1;

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

        String strShowInFormMainSearch = request.getParameter( PARAMETER_SHOWN_IN_ADVANCED_SEARCH );
        String strShowInResultList = request.getParameter( PARAMETER_SHOWN_IN_RESULT_LIST );
        String strShowInResultRecord = request.getParameter( PARAMETER_SHOWN_IN_RESULT_RECORD );
        String strShowInHistory = request.getParameter( PARAMETER_SHOWN_IN_HISTORY );      
        String strFieldError = DirectoryUtils.EMPTY_STRING;

        if ( ( strTitle == null ) || strTitle.trim(  ).equals( DirectoryUtils.EMPTY_STRING ) )
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
                ( nDisplayWidth == -1 ) )
        {
            strFieldError = FIELD_WIDTH_DISPLAY;
        }
        else if ( ( strDisplayHeight != null ) && ( !strDisplayHeight.trim(  ).equals( DirectoryUtils.EMPTY_STRING ) ) &&
                ( nDisplayHeight == -1 ) )
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
            return AdminMessageService.getMessageUrl( request, ERROR_FIELD_THUMBNAIL, AdminMessage.TYPE_STOP );
        }

        if ( ( strCreateBigThumbnail != null ) && ( ( nBigThumbnailWidth <= 0 ) || ( nBigThumbnailHeight <= 0 ) ) )
        {
            return AdminMessageService.getMessageUrl( request, ERROR_FIELD_BIG_THUMBNAIL, AdminMessage.TYPE_STOP );
        }

        this.setTitle( strTitle );
        this.setHelpMessage( strHelpMessage );
        this.setComment( strComment );

        if ( this.getFields(  ) == null )
        {
            ArrayList<Field> listFields = new ArrayList<Field>(  );
            Field field = new Field(  );

            if ( request.getParameter( PARAMETER_IMAGE_SHOWN_IN_RESULT_LIST ) != null )
            {
                field.setShownInResultList( true );
            }

            if ( request.getParameter( PARAMETER_IMAGE_SHOWN_IN_RESULT_RECORD ) != null )
            {
                field.setShownInResultRecord( true );
            }

            field.setEntry( this );
            field.setValue( FIELD_IMAGE );
            listFields.add( field );

            if ( strCreateThumbnail != null )
            {
                Field thumbnailField = new Field(  );
                thumbnailField.setWidth( nThumbnailWidth );
                thumbnailField.setHeight( nThumbnailHeight );
                thumbnailField.setValue( FIELD_THUMBNAIL );
                thumbnailField.setEntry( this );

                if ( request.getParameter( PARAMETER_THUMBNAIL_SHOWN_IN_RESULT_LIST ) != null )
                {
                    thumbnailField.setShownInResultList( true );
                }

                if ( request.getParameter( PARAMETER_THUMBNAIL_SHOWN_IN_RESULT_RECORD ) != null )
                {
                    thumbnailField.setShownInResultRecord( true );
                }

                listFields.add( thumbnailField );
            }

            if ( strCreateBigThumbnail != null )
            {
                Field bigThumbnailField = new Field(  );
                bigThumbnailField.setWidth( nBigThumbnailWidth );
                bigThumbnailField.setHeight( nBigThumbnailHeight );
                bigThumbnailField.setValue( FIELD_BIG_THUMBNAIL );
                bigThumbnailField.setEntry( this );

                if ( request.getParameter( PARAMETER_BIG_THUMBNAIL_SHOWN_IN_RESULT_LIST ) != null )
                {
                    bigThumbnailField.setShownInResultList( true );
                }

                if ( request.getParameter( PARAMETER_BIG_THUMBNAIL_SHOWN_IN_RESULT_RECORD ) != null )
                {
                    bigThumbnailField.setShownInResultRecord( true );
                }

                listFields.add( bigThumbnailField );
            }

            this.setFields( listFields );
        }

        boolean hasThumbnail = false;
        boolean hasBigThumbnail = false;

        for ( Field field : this.getFields(  ) )
        {
            RecordFieldFilter filter = new RecordFieldFilter(  );
            filter.setIdField( field.getIdField(  ) );

            if ( ( field.getValue(  ) != null ) && ( field.getValue(  ).equals( FIELD_THUMBNAIL ) ) )
            {
                field.setWidth( nThumbnailWidth );
                field.setHeight( nThumbnailHeight );

                if ( request.getParameter( PARAMETER_THUMBNAIL_SHOWN_IN_RESULT_LIST ) != null )
                {
                    field.setShownInResultList( true );
                }
                else
                {
                    field.setShownInResultList( false );
                }

                if ( request.getParameter( PARAMETER_THUMBNAIL_SHOWN_IN_RESULT_RECORD ) != null )
                {
                    field.setShownInResultRecord( true );
                }
                else
                {
                    field.setShownInResultRecord( false );
                }

                if ( strCreateThumbnail == null )
                {
                    FieldHome.remove( field.getIdField(  ), PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME ) );

                    RecordFieldHome.removeByFilter( filter, PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME ) );
                }

                hasThumbnail = true;
            }

            else if ( ( field.getValue(  ) != null ) && ( field.getValue(  ).equals( FIELD_BIG_THUMBNAIL ) ) )
            {
                field.setWidth( nBigThumbnailWidth );
                field.setHeight( nBigThumbnailHeight );

                if ( request.getParameter( PARAMETER_BIG_THUMBNAIL_SHOWN_IN_RESULT_LIST ) != null )
                {
                    field.setShownInResultList( true );
                }
                else
                {
                    field.setShownInResultList( false );
                }

                if ( request.getParameter( PARAMETER_BIG_THUMBNAIL_SHOWN_IN_RESULT_RECORD ) != null )
                {
                    field.setShownInResultRecord( true );
                }
                else
                {
                    field.setShownInResultRecord( false );
                }

                if ( strCreateBigThumbnail == null )
                {
                    FieldHome.remove( field.getIdField(  ), PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME ) );

                    RecordFieldHome.removeByFilter( filter, PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME ) );
                }

                hasBigThumbnail = true;
            }
            else if ( ( field.getValue(  ) != null ) && ( field.getValue(  ).equals( FIELD_IMAGE ) ) )
            {
                if ( request.getParameter( PARAMETER_IMAGE_SHOWN_IN_RESULT_LIST ) != null )
                {
                    field.setShownInResultList( true );
                }
                else
                {
                    field.setShownInResultList( false );
                }

                if ( request.getParameter( PARAMETER_IMAGE_SHOWN_IN_RESULT_RECORD ) != null )
                {
                    field.setShownInResultRecord( true );
                }
                else
                {
                    field.setShownInResultRecord( false );
                }
            }
        }

        if ( ( ( strCreateThumbnail ) != null ) && ( !hasThumbnail ) )
        {
            Field thumbnailField = new Field(  );
            thumbnailField.setWidth( nThumbnailWidth );
            thumbnailField.setHeight( nThumbnailHeight );
            thumbnailField.setValue( FIELD_THUMBNAIL );
            thumbnailField.setEntry( this );

            if ( request.getParameter( PARAMETER_THUMBNAIL_SHOWN_IN_RESULT_LIST ) != null )
            {
                thumbnailField.setShownInResultList( true );
            }

            if ( request.getParameter( PARAMETER_THUMBNAIL_SHOWN_IN_RESULT_RECORD ) != null )
            {
                thumbnailField.setShownInResultRecord( true );
            }

            FieldHome.create( thumbnailField, PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME ) );
        }

        if ( ( strCreateBigThumbnail != null ) && ( !hasBigThumbnail ) )
        {
            Field bigThumbnailField = new Field(  );
            bigThumbnailField.setWidth( nBigThumbnailWidth );
            bigThumbnailField.setHeight( nBigThumbnailHeight );
            bigThumbnailField.setValue( FIELD_BIG_THUMBNAIL );
            bigThumbnailField.setEntry( this );

            if ( request.getParameter( PARAMETER_BIG_THUMBNAIL_SHOWN_IN_RESULT_LIST ) != null )
            {
                bigThumbnailField.setShownInResultList( true );
            }

            if ( request.getParameter( PARAMETER_BIG_THUMBNAIL_SHOWN_IN_RESULT_RECORD ) != null )
            {
                bigThumbnailField.setShownInResultRecord( true );
            }

            FieldHome.create( bigThumbnailField, PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME ) );
        }

        this.setDisplayWidth( nDisplayWidth );
        this.setDisplayHeight( nDisplayHeight );

        this.setMandatory( strMandatory != null );
        this.setIndexed( strIndexed != null );
        this.setShownInAdvancedSearch( strShowInFormMainSearch != null );
        this.setShownInResultList( strShowInResultList != null );
        this.setShownInResultRecord( strShowInResultRecord != null );
        this.setShownInHistory( strShowInHistory != null );
        //image can't be exported in csv
        this.setShownInExport( false );

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
        //add Empty recordField(Use for data import)
        RecordField recordField = new RecordField(  );
        recordField.setEntry( this );
        listRecordField.add( recordField );
    }

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
        recordField.setValue( FIELD_IMAGE );

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

        try
        {
            if ( ( fileSource != null ) &&
                    ( ( strIdDirectoryRecord == null ) ||
                    ( ( strIdDirectoryRecord != null ) && ( strUpdateFile != null ) ) ) )
            {
                //verify that the file is an image
                ImageIO.read( new ByteArrayInputStream( fileSource.getPhysicalFile(  ).getValue(  ) ) );

                recordField.setFile( fileSource );
            }
            else if ( ( fileSource == null ) && ( strUpdateFile == null ) )
            {
                //get the default file
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
                thbnailRecordField.setValue( FIELD_THUMBNAIL );
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
                bigThbnailRecordField.setValue( FIELD_BIG_THUMBNAIL );
                bigThbnailRecordField.setField( bigThumbnailField );
                listRecordField.add( bigThbnailRecordField );
            }
        }
        catch ( Exception e )
        {
            AppLogService.error( e );
        }
    }

    @Override
    public Paginator getPaginator( int nItemPerPage, String strBaseUrl, String strPageIndexParameterName,
        String strPageIndex )
    {
        return new Paginator( this.getFields(  ).get( 0 ).getRegularExpressionList(  ), nItemPerPage, strBaseUrl,
            strPageIndexParameterName, strPageIndex );
    }

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

    @Override
    public LocalizedPaginator getPaginator( int nItemPerPage, String strBaseUrl, String strPageIndexParameterName,
        String strPageIndex, Locale locale )
    {
        return new LocalizedPaginator( this.getFields(  ).get( 0 ).getRegularExpressionList(  ), nItemPerPage,
            strBaseUrl, strPageIndexParameterName, strPageIndex, locale );
    }
}

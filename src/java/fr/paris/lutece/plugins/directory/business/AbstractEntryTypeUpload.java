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

import fr.paris.lutece.plugins.directory.service.upload.DirectoryAsynchronousUploadHandler;
import fr.paris.lutece.plugins.directory.utils.DirectoryErrorException;
import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.portal.business.regularexpression.RegularExpression;
import fr.paris.lutece.portal.service.fileupload.FileUploadService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.regularexpression.RegularExpressionService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.filesystem.FileSystemUtil;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


/**
 *
 * AbstractEntryTypeUpload
 *
 */
public abstract class AbstractEntryTypeUpload extends Entry
{
    // PARAMETERS
    protected static final String PARAMETER_MAX_FILES = "max_files";
    protected static final String PARAMETER_FILE_MAX_SIZE = "file_max_size";

    // CONSTANTS
    protected static final String CONSTANT_MAX_FILES = "max_files";
    protected static final String CONSTANT_FILE_MAX_SIZE = "file_max_size";
    protected static final String ALL = "*";
    protected static final String COMMA = ",";

    // PROPERTIES
    private static final String PROPERTY_MESSAGE_ERROR_UPLOADING_FILE_MAX_FILES = "directory.message.error.uploading_file.max_files";
    private static final String PROPERTY_MESSAGE_ERROR_UPLOADING_FILE_FILE_MAX_SIZE = "directory.message.error.uploading_file.file_max_size";
    private static final String PROPERTY_UPLOAD_FILE_DEFAULT_MAX_SIZE = "directory.upload.file.default_max_size";

    // FIELDS
    private static final String FIELD_MAX_FILES = "directory.create_entry.label_max_files";
    private static final String FIELD_FILE_MAX_SIZE = "directory.create_entry.label_file_max_size";

    /**
     * Set the fields
     * @param request the HTTP request
     * @param listFields the list of fields to set
     */
    protected abstract void setFields( HttpServletRequest request, List<Field> listFields );

    /**
     * Check the record field data for a single file item
     * @param fileItem the file item
     * @param locale the locale
     * @throws DirectoryErrorException exception if there is an error
     */
    protected abstract void checkRecordFieldData( FileItem fileItem, Locale locale )
        throws DirectoryErrorException;

    /**
     * {@inheritDoc}
     */
    @Override
    public void canUploadFiles( List<FileItem> listUploadedFileItems, List<FileItem> listFileItemsToUpload,
        Locale locale ) throws DirectoryErrorException
    {
        /** 1) Check max files */
        Field fieldMaxFiles = DirectoryUtils.findFieldByTitleInTheList( CONSTANT_MAX_FILES, getFields(  ) );

        // By default, max file is set at 1
        int nMaxFiles = 1;

        if ( ( fieldMaxFiles != null ) && StringUtils.isNotBlank( fieldMaxFiles.getValue(  ) ) &&
                StringUtils.isNumeric( fieldMaxFiles.getValue(  ) ) )
        {
            nMaxFiles = DirectoryUtils.convertStringToInt( fieldMaxFiles.getValue(  ) );
        }

        if ( ( listUploadedFileItems != null ) && ( listFileItemsToUpload != null ) )
        {
            int nNbFiles = listUploadedFileItems.size(  ) + listFileItemsToUpload.size(  );

            if ( nNbFiles > nMaxFiles )
            {
                Object[] params = { nMaxFiles };
                String strMessage = I18nService.getLocalizedString( PROPERTY_MESSAGE_ERROR_UPLOADING_FILE_MAX_FILES,
                        params, locale );
                throw new DirectoryErrorException( this.getTitle(  ), strMessage );
            }
        }

        /** 2) Check files size */
        Field fieldFileMaxSize = DirectoryUtils.findFieldByTitleInTheList( CONSTANT_FILE_MAX_SIZE, getFields(  ) );
        int nMaxSize = DirectoryUtils.CONSTANT_ID_NULL;

        if ( ( fieldFileMaxSize != null ) && StringUtils.isNotBlank( fieldFileMaxSize.getValue(  ) ) &&
                StringUtils.isNumeric( fieldFileMaxSize.getValue(  ) ) )
        {
            nMaxSize = DirectoryUtils.convertStringToInt( fieldFileMaxSize.getValue(  ) );
        }
        else
        {
            // For version 2.0.13 and below, the max size was stored in the width of the field "option" for EntryTypeDownloadUrl
            Field fieldOption = DirectoryUtils.findFieldByTitleInTheList( EntryTypeDownloadUrl.CONSTANT_OPTION,
                    getFields(  ) );

            if ( fieldOption != null )
            {
                nMaxSize = fieldOption.getWidth(  );
            }
        }

        // If no max size defined in the db, then fetch if from the directory.properties file
        if ( nMaxSize == DirectoryUtils.CONSTANT_ID_NULL )
        {
            nMaxSize = AppPropertiesService.getPropertyInt( PROPERTY_UPLOAD_FILE_DEFAULT_MAX_SIZE, 5242880 );
        }

        // If nMaxSize == -1, then no size limit
        if ( ( nMaxSize != DirectoryUtils.CONSTANT_ID_NULL ) && ( listFileItemsToUpload != null ) &&
                !listFileItemsToUpload.isEmpty(  ) )
        {
            for ( FileItem fileItem : listFileItemsToUpload )
            {
                if ( fileItem.getSize(  ) > nMaxSize )
                {
                    Object[] params = { nMaxSize };
                    String strMessage = I18nService.getLocalizedString( PROPERTY_MESSAGE_ERROR_UPLOADING_FILE_FILE_MAX_SIZE,
                            params, locale );
                    throw new DirectoryErrorException( this.getTitle(  ), strMessage );
                }
            }
        }
    }

    // CHECKS

    /**
     * Check the entry data
     * @param request the HTTP request
     * @param locale the locale
     * @return the error message url if there is an error, an empty string
     *         otherwise
     */
    protected String checkEntryData( HttpServletRequest request, Locale locale )
    {
        String strTitle = request.getParameter( PARAMETER_TITLE );
        String strMaxFiles = request.getParameter( PARAMETER_MAX_FILES );
        String strFileMaxSize = request.getParameter( PARAMETER_FILE_MAX_SIZE );
        String strFieldError = DirectoryUtils.EMPTY_STRING;

        if ( StringUtils.isBlank( strTitle ) )
        {
            strFieldError = FIELD_TITLE;
        }
        else if ( StringUtils.isBlank( strMaxFiles ) )
        {
            strFieldError = FIELD_MAX_FILES;
        }
        else if ( StringUtils.isBlank( strFileMaxSize ) )
        {
            strFieldError = FIELD_FILE_MAX_SIZE;
        }

        if ( !strFieldError.equals( DirectoryUtils.EMPTY_STRING ) )
        {
            Object[] tabRequiredFields = { I18nService.getLocalizedString( strFieldError, locale ) };

            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields,
                AdminMessage.TYPE_STOP );
        }

        if ( !StringUtils.isNumeric( strMaxFiles ) )
        {
            strFieldError = FIELD_MAX_FILES;
        }
        else if ( !StringUtils.isNumeric( strFileMaxSize ) )
        {
            strFieldError = FIELD_FILE_MAX_SIZE;
        }

        if ( !strFieldError.equals( DirectoryUtils.EMPTY_STRING ) )
        {
            Object[] tabRequiredFields = { I18nService.getLocalizedString( strFieldError, locale ) };

            return AdminMessageService.getMessageUrl( request, MESSAGE_NUMERIC_FIELD, tabRequiredFields,
                AdminMessage.TYPE_STOP );
        }

        return StringUtils.EMPTY;
    }

    /**
     * Check the record field data
     * @param listFilesSource the file source to upload
     * @param locale the locale
     * @throws DirectoryErrorException exception if there is an error
     */
    protected void checkRecordFieldData( List<FileItem> listFilesSource, Locale locale )
        throws DirectoryErrorException
    {
        for ( FileItem fileSource : listFilesSource )
        {
            // Check mandatory attribute
            String strFilename = ( fileSource != null ) ? FileUploadService.getFileNameOnly( fileSource )
                                                        : StringUtils.EMPTY;

            if ( isMandatory(  ) && StringUtils.isBlank( strFilename ) )
            {
                throw new DirectoryErrorException( getTitle(  ) );
            }

            String strMimeType = FileSystemUtil.getMIMEType( strFilename );

            // Check mime type with regular expressions
            List<RegularExpression> listRegularExpression = this.getFields(  ).get( 0 ).getRegularExpressionList(  );

            if ( StringUtils.isNotBlank( strFilename ) && ( listRegularExpression != null ) &&
                    !listRegularExpression.isEmpty(  ) && RegularExpressionService.getInstance(  ).isAvailable(  ) )
            {
                for ( RegularExpression regularExpression : listRegularExpression )
                {
                    if ( !RegularExpressionService.getInstance(  ).isMatches( strMimeType, regularExpression ) )
                    {
                        throw new DirectoryErrorException( getTitle(  ), regularExpression.getErrorMessage(  ) );
                    }
                }
            }

            // Specific Check from each entry types
            checkRecordFieldData( fileSource, locale );
        }
    }

    // FINDERS

    /**
     * Get the file source from the session
     * @param request the HttpServletRequest
     * @return the file item
     */
    protected List<FileItem> getFileSources( HttpServletRequest request )
    {
        HttpSession session = request.getSession( false );

        if ( session != null )
        {
            // check the file in session - it might no be deleted
            return DirectoryAsynchronousUploadHandler.getHandler(  )
                                                     .getFileItems( Integer.toString( getIdEntry(  ) ),
                session.getId(  ) );
        }

        return null;
    }

    // SET

    /**
     * Set the list of fields
     * @param request the HTTP request
     */
    protected void setFields( HttpServletRequest request )
    {
        List<Field> listFields = new ArrayList<Field>(  );
        listFields.add( buildFieldMaxFiles( request ) );
        listFields.add( buildFieldFileMaxSize( request ) );

        setFields( request, listFields );

        this.setFields( listFields );
    }

    // PRIVATE METHODS

    /**
     * Build the field for max files
     * @param request the HTTP request
     * @return the field
     */
    private Field buildFieldMaxFiles( HttpServletRequest request )
    {
        String strMaxFiles = request.getParameter( PARAMETER_MAX_FILES );
        int nMaxFiles = DirectoryUtils.convertStringToInt( strMaxFiles );
        Field fieldMaxFiles = DirectoryUtils.findFieldByTitleInTheList( CONSTANT_MAX_FILES, getFields(  ) );

        if ( fieldMaxFiles == null )
        {
            fieldMaxFiles = new Field(  );
        }

        fieldMaxFiles.setEntry( this );
        fieldMaxFiles.setTitle( CONSTANT_MAX_FILES );
        fieldMaxFiles.setValue( Integer.toString( nMaxFiles ) );

        return fieldMaxFiles;
    }

    /**
     * Build the field for file max size
     * @param request the HTTP request
     * @return the field
     */
    private Field buildFieldFileMaxSize( HttpServletRequest request )
    {
        String strFileMaxSize = request.getParameter( PARAMETER_FILE_MAX_SIZE );
        int nFileMaxSize = DirectoryUtils.convertStringToInt( strFileMaxSize );
        Field fieldMaxFiles = DirectoryUtils.findFieldByTitleInTheList( CONSTANT_FILE_MAX_SIZE, getFields(  ) );

        if ( fieldMaxFiles == null )
        {
            fieldMaxFiles = new Field(  );
        }

        fieldMaxFiles.setEntry( this );
        fieldMaxFiles.setTitle( CONSTANT_FILE_MAX_SIZE );
        fieldMaxFiles.setValue( Integer.toString( nFileMaxSize ) );

        return fieldMaxFiles;
    }
}

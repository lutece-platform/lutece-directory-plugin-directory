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
package fr.paris.lutece.plugins.directory.service.upload;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.blobstoreclient.service.BlobStoreClientWebService;
import fr.paris.lutece.plugins.directory.business.IEntry;
import fr.paris.lutece.plugins.directory.business.Record;
import fr.paris.lutece.plugins.directory.business.RecordField;
import fr.paris.lutece.plugins.directory.business.RecordFieldFilter;
import fr.paris.lutece.plugins.directory.business.RecordFieldHome;
import fr.paris.lutece.plugins.directory.service.DirectoryPlugin;
import fr.paris.lutece.plugins.directory.utils.UrlUtils;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.web.upload.IAsynchronousUploadHandler;
import fr.paris.lutece.util.httpaccess.HttpAccessException;


/**
 * Handler for asynchronous uploads.
 * Files are stored using {@link SubForm#addFileItem(String, String, FileItem)}.
 * The <code>jessionid</code> parameter should be the <strong>REAL</strong> session id,
 * not the flash player one.
 * The uploaded files are deleted by SubForm when filling fields.
 *
 */
public class DirectoryAsynchronousUploadHandler implements IAsynchronousUploadHandler
{
	private static final String BEAN_DIRECTORY_ASYNCHRONOUS_UPLOAD_HANDLER = "directory.asynchronousUploadHandler";
	
	// PARAMETERS
    private static final String PARAMETER_BLOB_KEY = "blob_key";
    private static final String PARAMETER_BLOBSTORE = "blobstore";
	private static final String PARAMETER_PLUGIN_NAME = "plugin_name";
	private static final String PARAMETER_FIELD_NAME = "field_name";
	private static final String PARAMETER_JSESSION_ID = "jsessionid";
	
	// JSON
	private static final String JSON_KEY_FIELD_NAME = "field_name";
    private static final String JSON_KEY_ERROR = "error";
	
	// MESSAGES
	private static final String MESSAGE_ERROR_UPLOAD = "directory.message.error.upload";
    
    /** contains uploaded file items */
    public static Map<String, Map<String, FileItem>> _mapAsynchronousUpload = new ConcurrentHashMap<String, Map<String,FileItem>>(  );

    private BlobStoreClientWebService _blobStoreClientWS;
    
    /**
     * Private constructor
     */
    private DirectoryAsynchronousUploadHandler(  )
    {
    }
    
    /**
     * Get the handler
     * @return the handler
     */
    public static DirectoryAsynchronousUploadHandler getHandler(  )
    {
    	return (DirectoryAsynchronousUploadHandler) SpringContextService.getPluginBean( DirectoryPlugin.PLUGIN_NAME, 
    			BEAN_DIRECTORY_ASYNCHRONOUS_UPLOAD_HANDLER );
    }
    
    /**
     * Set the blobstore client web service
     * @param blobStoreClientWebService the blob store client web service
     */
    public void setBlobStoreClientWebService( BlobStoreClientWebService blobStoreClientWebService )
    {
        _blobStoreClientWS = blobStoreClientWebService;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isInvoked( HttpServletRequest request )
    {
        return DirectoryPlugin.PLUGIN_NAME.equals( request.getParameter( PARAMETER_PLUGIN_NAME ) );
    }

    /**
     * {@inheritDoc}
     */
    public void process( HttpServletRequest request, HttpServletResponse response, JSONObject mainObject,
        List<FileItem> listFileItems )
    {
    	// prevent 0 or multiple uploads for the same field
		if ( listFileItems == null || listFileItems.isEmpty(  ) )
		{
			throw new AppException( "No file uploaded" );
		}
		if ( listFileItems.size(  ) > 1 )
		{
			throw new AppException( "Upload multiple files for Directory is not supported" );
		}
		
		String strIdSession = request.getParameter( PARAMETER_JSESSION_ID );
		if ( StringUtils.isNotBlank( strIdSession ) )
		{
			String strIdEntry = request.getParameter( PARAMETER_FIELD_NAME );
			if ( StringUtils.isBlank( strIdEntry ) )
			{
				throw new AppException( "id entry is not provided for the current file upload" );
			}
			
			// find session-related files in the map
			Map<String, FileItem> mapFileItemsSession = _mapAsynchronousUpload.get( strIdSession );
			
			// create map if not exists
			if ( mapFileItemsSession == null )
			{
				synchronized ( _mapAsynchronousUpload )
				{
					if ( _mapAsynchronousUpload.get( strIdSession ) == null )
					{
						mapFileItemsSession = new ConcurrentHashMap<String, FileItem>(  );
						_mapAsynchronousUpload.put( strIdSession, mapFileItemsSession );
					}
				}
			}
			
			// put entry id -> fileItem : we don't want more than one file per entry --> overwrite existing file
			mapFileItemsSession.put( strIdEntry, listFileItems.get( 0 ) );
			
			// add entry id to json
			mainObject.element( JSON_KEY_FIELD_NAME, strIdEntry );
		}
		else
		{
			AppLogService.error( DirectoryAsynchronousUploadHandler.class.getName(  ) + " : Session does not exists" );
            mainObject.accumulate( JSON_KEY_ERROR,
                I18nService.getLocalizedString( MESSAGE_ERROR_UPLOAD, request.getLocale(  ) ) );
		}
    }
    
    /**
     * Do upload a file in the blobstore webapp
     * @param strBaseUrl the base url
     * @param fileItem the file
     * @param strBlobStore the blobstore service name
     * @return the blob key of the uploaded file
     * @throws HttpAccessException Exception if there is an HTTP issue
     */
    public String doUploadFile( String strBaseUrl, FileItem fileItem, String strBlobStore )
        throws HttpAccessException
    {
        return _blobStoreClientWS.doUploadFile( strBaseUrl, fileItem, strBlobStore );
    }
    
    /**
     * Do remove a file from a given record and entry
     * @param record the record
     * @param entry the entry
     * @param strWSRestUrl the url of the WS rest
     * @throws HttpAccessException Exception if there is an HTTP issue
     */
    public void doRemoveFile( Record record, IEntry entry, String strWSRestUrl )
        throws HttpAccessException
    {
    	Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
        RecordFieldFilter recordFieldFilter = new RecordFieldFilter(  );
        recordFieldFilter.setIdDirectory( record.getDirectory(  ).getIdDirectory(  ) );
        recordFieldFilter.setIdEntry( entry.getIdEntry(  ) );
        recordFieldFilter.setIdRecord( record.getIdRecord(  ) );

        List<RecordField> listRecordFields = RecordFieldHome.getRecordFieldList( recordFieldFilter, pluginDirectory );
        if ( listRecordFields != null && !listRecordFields.isEmpty(  ) )
        {
        	RecordField recordField = listRecordFields.get( 0 );
        	doRemoveFile( recordField, entry, strWSRestUrl );
        }
    }
    
    /**
     * Do remove a file from a given record field
     * @param recordField the record field
     * @param entry the entry
     * @param strWSRestUrl the url of the WS rest
     * @throws HttpAccessException Exception if there is an HTTP issue
     */
    public void doRemoveFile( RecordField recordField, IEntry entry, String strWSRestUrl )
    	throws HttpAccessException
    {
    	if ( recordField != null )
    	{
    		// Get the download file url
            String strDownloadFileUrl = entry.convertRecordFieldTitleToString( recordField, null, false );

            if ( StringUtils.isNotBlank( strDownloadFileUrl ) )
            {
                // Parse the download file url to fetch the parameters
                Map<String, List<String>> mapParameters = UrlUtils.getMapParametersFromUrl( strDownloadFileUrl );
                List<String> parameterBlobKey = mapParameters.get( PARAMETER_BLOB_KEY );
                List<String> parameterBlobStore = mapParameters.get( PARAMETER_BLOBSTORE );

                if ( ( parameterBlobKey != null ) && !parameterBlobKey.isEmpty(  ) && ( parameterBlobStore != null ) &&
                        !parameterBlobStore.isEmpty(  ) )
                {
                    String strBlobKey = parameterBlobKey.get( 0 );
                    String strBlobStore = parameterBlobStore.get( 0 );
                    _blobStoreClientWS.doDeleteFile( strWSRestUrl, strBlobStore, strBlobKey );
                }
            }
    	}
    }
    
    /**
     * Get the file url
     * @param strBaseUrl the base url
     * @param strBlobKey the blob key
     * @param strBlobStore the blobstore service name
     * @return the file url
     * @throws HttpAccessException Exception if there is an HTTP issue
     */
    public String getFileUrl( String strBaseUrl, String strBlobKey, String strBlobStore )
        throws HttpAccessException
    {
        return _blobStoreClientWS.getFileUrl( strBaseUrl, strBlobKey, strBlobStore );
    }
    
    /**
     * Get the file name from a given url
     * @param strUrl the url
     * @return the file name
     * @throws HttpAccessException Exception if there is an HTTP issue
     */
    public String getFileName( String strUrl ) throws HttpAccessException
    {
        return _blobStoreClientWS.getFileName( strUrl );
    }
    
    /**
	 * Gets the fileItem for the entry and the given session.
	 * @param strIdEntry the entry
	 * @param strSessionId the session id
	 * @return the fileItem found, <code>null</code> otherwise.
	 */
	public static FileItem getFileItem( String strIdEntry, String strSessionId )
	{
		FileItem fileItem;
		Map<String, FileItem> mapFileItemsSession = _mapAsynchronousUpload.get( strSessionId );
		if ( mapFileItemsSession != null )
		{
			fileItem = mapFileItemsSession.get( strIdEntry );
		}
		else
		{
			fileItem = null;
		}
		
		return fileItem;
	}
	
	/**
	 * Removes the file from the list.
	 * @param strIdEntry the entry id
	 * @param strSessionId the session id
	 */
	public static void removeFileItem( String strIdEntry, String strSessionId )
	{
		Map<String, FileItem> mapFileItemsSession = _mapAsynchronousUpload.get( strSessionId );
		if ( mapFileItemsSession != null && strIdEntry != null )
		{
			mapFileItemsSession.remove( strIdEntry );
		}
	}
	
	/**
	 * Removes all files associated to the session
	 * @param strSessionId the session id
	 */
	public static void removeSessionFiles( String strSessionId )
	{
		_mapAsynchronousUpload.remove( strSessionId );
	}
}

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
package fr.paris.lutece.plugins.directory.web;

import fr.paris.lutece.plugins.directory.business.File;
import fr.paris.lutece.plugins.directory.business.FileHome;
import fr.paris.lutece.plugins.directory.business.PhysicalFile;
import fr.paris.lutece.plugins.directory.business.PhysicalFileHome;
import fr.paris.lutece.plugins.directory.business.RecordField;
import fr.paris.lutece.plugins.directory.business.RecordFieldFilter;
import fr.paris.lutece.plugins.directory.business.RecordFieldHome;
import fr.paris.lutece.plugins.directory.service.DirectoryPlugin;
import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.filesystem.FileSystemUtil;

import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.OutputStream;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 *
 * class DoDownloadGraph
 *
 */
public final class DoDownloadFile
{
    private static final String PARAMETER_ID_FILE = "id_file";
    private static final String MESSAGE_ERROR_DURING_DOWNLOAD_FILE = "directory.message.error_during_download_file";

    /**
     * Private constructor
     */
    private DoDownloadFile(  )
    {
    }

    /**
     * Write in the http response the file to upload
     * @param request the http request
     * @param response The http response
     * @return Error Message
     *
     */
    public static String doDownloadFile( HttpServletRequest request, HttpServletResponse response )
    {
        Plugin plugin = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
        String strIdFile = request.getParameter( PARAMETER_ID_FILE );
        int nIdFile = DirectoryUtils.CONSTANT_ID_NULL;

        if ( StringUtils.isBlank( strIdFile ) || !StringUtils.isNumeric( strIdFile ) )
        {
            String strIdDirectoryRecord = request.getParameter( DirectoryUtils.PARAMETER_ID_DIRECTORY_RECORD );
            String strIdEntry = request.getParameter( DirectoryUtils.PARAMETER_ID_ENTRY );

            if ( ( StringUtils.isBlank( strIdDirectoryRecord ) || !StringUtils.isNumeric( strIdDirectoryRecord ) ) &&
                    ( StringUtils.isBlank( strIdEntry ) || !StringUtils.isNumeric( strIdEntry ) ) )
            {
                return AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_DURING_DOWNLOAD_FILE,
                    AdminMessage.TYPE_STOP );
            }

            int nIdDirectoryRecord = DirectoryUtils.convertStringToInt( strIdDirectoryRecord );
            int nIdEntry = DirectoryUtils.convertStringToInt( strIdEntry );
            RecordFieldFilter rfFilter = new RecordFieldFilter(  );
            rfFilter.setIdRecord( nIdDirectoryRecord );
            rfFilter.setIdEntry( nIdEntry );

            List<RecordField> listRecordFields = RecordFieldHome.getRecordFieldList( rfFilter, plugin );

            if ( ( listRecordFields != null ) && !listRecordFields.isEmpty(  ) )
            {
                RecordField recordField = listRecordFields.get( 0 );

                if ( ( recordField != null ) && ( recordField.getFile(  ) != null ) )
                {
                    nIdFile = recordField.getFile(  ).getIdFile(  );
                }
            }

            if ( ( nIdFile == DirectoryUtils.CONSTANT_ID_NULL ) || ( nIdFile == DirectoryUtils.CONSTANT_ID_ZERO ) )
            {
                return AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_DURING_DOWNLOAD_FILE,
                    AdminMessage.TYPE_STOP );
            }
        }
        else
        {
            nIdFile = DirectoryUtils.convertStringToInt( strIdFile );
        }

        File file = FileHome.findByPrimaryKey( nIdFile, plugin );
        PhysicalFile physicalFile = ( file != null )
            ? PhysicalFileHome.findByPrimaryKey( file.getPhysicalFile(  ).getIdPhysicalFile(  ), plugin ) : null;

        if ( physicalFile != null )
        {
            try
            {
                byte[] byteFileOutPut = physicalFile.getValue(  );
                DirectoryUtils.addHeaderResponse( request, response, file.getTitle(  ) );

                String strMimeType = file.getMimeType(  );

                if ( strMimeType == null )
                {
                    strMimeType = FileSystemUtil.getMIMEType( file.getTitle(  ) );
                }

                response.setContentType( strMimeType );
                response.setContentLength( byteFileOutPut.length );

                OutputStream os = response.getOutputStream(  );
                os.write( byteFileOutPut );
                os.close(  );
            }
            catch ( IOException e )
            {
                AppLogService.error( e );
            }
        }

        return AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_DURING_DOWNLOAD_FILE, AdminMessage.TYPE_STOP );
    }
}

/*
 * Copyright (c) 2002-2018, Mairie de Paris
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
package fr.paris.lutece.plugins.directory.service.file;

import fr.paris.lutece.plugins.directory.business.File;
import fr.paris.lutece.plugins.directory.business.FileHome;
import fr.paris.lutece.plugins.directory.business.PhysicalFileHome;
import fr.paris.lutece.plugins.directory.business.RecordField;
import fr.paris.lutece.plugins.directory.business.RecordFieldFilter;
import fr.paris.lutece.plugins.directory.business.RecordFieldHome;
import fr.paris.lutece.plugins.directory.service.DirectoryPlugin;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.sql.TransactionManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DirectoryFileService
{

    private static final String MESSAGE_PURGE_FILE_TITLE = "directory.file.purge.fileTitle";

    /**
     * Purge the given file
     * 
     * @param file
     *            The file
     */
    public void purge( File file )
    {
        try
        {
            TransactionManager.beginTransaction( PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME ) );
            file.setTitle( I18nService.getLocalizedString( MESSAGE_PURGE_FILE_TITLE, Locale.getDefault( ) ) );
            FileHome.purge( file, PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME ) );
            PhysicalFileHome.purge( file.getPhysicalFile( ), PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME ) );
            TransactionManager.commitTransaction( PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME ) );
        }
        catch( Exception e )
        {
            TransactionManager.rollBack( null );
            AppLogService.error( "Unable to purge file with id " + file.getIdFile( ), e );
            throw e;
        }
    }

    /**
     * Get the files list
     * 
     * @return the file list
     */
    public List<File> getFilesList( )
    {
        return FileHome.getFilesList( PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME ) );
    }

    /**
     * Update the file
     * 
     * @param file
     *            the file to update
     */
    public void update( File file )
    {
        FileHome.update( file, PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME ) );
    }

    /**
     * Get the associated files of a given resource
     * 
     * @param nIdResource
     * @return the associated files of a given resource
     */
    public List<File> getAssociatedFiles( int nIdResource )
    {
        List<File> listFiles = new ArrayList<>( );

        // Set an entry filter
        RecordFieldFilter filter = new RecordFieldFilter( );
        filter.setIdRecord( nIdResource );
        filter.setContainsFile( true );
        List<RecordField> listRecordFields = RecordFieldHome.getRecordFieldList( filter, PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME ) );

        for ( RecordField recordField : listRecordFields )
        {
            listFiles.add( recordField.getFile( ) );
        }
        return listFiles;
    }

    /**
     * Get the file from the given file id
     * 
     * @param strFileId
     *            the file id
     * @return the file corresponding to the given file id
     */
    public File getFile( String strFileId )
    {
        int nIdFile = Integer.parseInt( strFileId );
        return FileHome.findByPrimaryKey( nIdFile, PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME ) );
    }

}

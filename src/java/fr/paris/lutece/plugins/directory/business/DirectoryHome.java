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

import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.util.ReferenceList;

import java.util.List;


/**
 * This class provides instances management methods (create, find, ...) for Directory objects
 */
public final class DirectoryHome
{
    // Static variable pointed at the DAO instance
    private static IDirectoryDAO _dao = (IDirectoryDAO) SpringContextService.getPluginBean( "directory", "directoryDAO" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private DirectoryHome(  )
    {
    }

    /**
     * Creation of an instance of directory
     *
     * @param directory The instance of the Directory which contains the informations to store
     * @param plugin the Plugin
     * @return The primary key of the new directory.
     */
    public static int create( Directory directory, Plugin plugin )
    {
        return _dao.insert( directory, plugin );
    }

    /**
     * Copy of an instance of directory
     *
     * @param directory The instance of the directory who must copy
     * @param plugin the Plugin
     *
     */
    public static void copy( Directory directory, Plugin plugin )
    {
        List<IEntry> listEntry;
        EntryFilter filter = new EntryFilter(  );
        filter.setIdDirectory( directory.getIdDirectory(  ) );
        filter.setIsEntryParentNull( EntryFilter.FILTER_TRUE );
        listEntry = EntryHome.getEntryList( filter, plugin );

        directory.setDateCreation( DirectoryUtils.getCurrentTimestamp(  ) );
        directory.setIdDirectory( create( directory, plugin ) );

        for ( IEntry entry : listEntry )
        {
            entry = EntryHome.findByPrimaryKey( entry.getIdEntry(  ), plugin );
            entry.setDirectory( directory );
            EntryHome.copy( entry, plugin );
        }
    }

    /**
     * Update of the directory which is specified in parameter
     *
     * @param directory The instance of the directory which contains the informations to update
     * @param plugin the Plugin
     *
     */
    public static void update( Directory directory, Plugin plugin )
    {
        _dao.store( directory, plugin );
    }

    /**
     * Remove the directory whose identifier is specified in parameter
     *
     * @param nIdDirectory The directory Id
     * @param plugin the Plugin
     */
    public static void remove( int nIdDirectory, Plugin plugin )
    {
        Directory directory = findByPrimaryKey( nIdDirectory, plugin );
        List<IEntry> listEntry;
        List<Record> listRecord;

        RecordFieldFilter recordFilter = new RecordFieldFilter(  );
        recordFilter.setIdDirectory( nIdDirectory );
        listRecord = RecordHome.getListRecord( recordFilter, plugin );

        for ( Record record : listRecord )
        {
            RecordHome.remove( record.getIdRecord(  ), plugin );
        }

        EntryFilter entryFilter = new EntryFilter(  );
        entryFilter.setIdDirectory( directory.getIdDirectory(  ) );
        listEntry = EntryHome.getEntryList( entryFilter, plugin );

        for ( IEntry entry : listEntry )
        {
            EntryHome.remove( entry.getIdEntry(  ), plugin );
        }

        _dao.delete( nIdDirectory, plugin );
    }

    ///////////////////////////////////////////////////////////////////////////
    // Finders
    /**
     * Returns an instance of a directory whose identifier is specified in parameter
     *
     * @param nKey The entry primary key
     * @param plugin the Plugin
     * @return an instance of directory
     */
    public static Directory findByPrimaryKey( int nKey, Plugin plugin )
    {
        return _dao.load( nKey, plugin );
    }

    /**
         * Load the data of all the directory who verify the filter and returns them in a  list
         * @param filter the filter
         * @param plugin the plugin
         * @return  the list of form
         */
    public static List<Directory> getDirectoryList( DirectoryFilter filter, Plugin plugin )
    {
        return _dao.selectDirectoryList( filter, plugin );
    }

    /**
         * Load the data of all enable directory  returns them in a  reference list
         * @param plugin the plugin
         * @return  a  reference list of enable directory
         */
    public static ReferenceList getDirectoryList( Plugin plugin )
    {
        return _dao.getEnableDirectoryList( plugin );
    }
}

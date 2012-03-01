/*
 * Copyright (c) 2002-2012, Mairie de Paris
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

import fr.paris.lutece.portal.service.html.XmlTransformerService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;

import java.util.List;


/**
 * This class provides instances management methods (create, find, ...) for ExportFormat objects
 */
public final class DirectoryXslHome
{
    // Static variable pointed at the DAO instance
    private static IDirectoryXslDAO _dao = (IDirectoryXslDAO) SpringContextService.getPluginBean( "directory",
            "directoryXslDAO" );
    private static final String CSV = "csv";

    /**
     * Private constructor - this class need not be instantiated
     */
    private DirectoryXslHome(  )
    {
    }

    /**
     * Creation of an instance of Directory Xsl
     *
     * @param directoryXsl The instance of the directoryXsl which contains the informations to store
     * @param plugin the Plugin
     *
     */
    public static void create( DirectoryXsl directoryXsl, Plugin plugin )
    {
        _dao.insert( directoryXsl, plugin );
    }

    /**
     * Update of the DirectoryXsl which is specified in parameter
     *
     * @param directoryXsl The instance of the directoryXsl which contains the informations to update
     * @param plugin the Plugin
     *
     */
    public static void update( DirectoryXsl directoryXsl, Plugin plugin )
    {
        _dao.store( directoryXsl, plugin );
        XmlTransformerService.clearXslCache(  );
    }

    /**
     * Remove the DirectoryXsl whose identifier is specified in parameter
     *
     * @param nIdDirectoryXsl The DirectoryXsl Id
     * @param plugin the Plugin
     */
    public static void remove( int nIdDirectoryXsl, Plugin plugin )
    {
        _dao.delete( nIdDirectoryXsl, plugin );
        XmlTransformerService.clearXslCache(  );
    }

    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Returns an instance of a DirectoryXsl whose identifier is specified in parameter
     *
     * @param nKey The directoryXsl primary key
     * @param plugin the Plugin
     * @return an instance of DirectoryXsl
     */
    public static DirectoryXsl findByPrimaryKey( int nKey, Plugin plugin )
    {
        DirectoryXsl directoryXsl = _dao.load( nKey, plugin );

        if ( ( directoryXsl != null ) && ( directoryXsl.getFile(  ) != null ) )
        {
            directoryXsl.setFile( FileHome.findByPrimaryKey( directoryXsl.getFile(  ).getIdFile(  ), plugin ) );
        }

        return directoryXsl;
    }

    /**
     * Loads the data of all the DirectoryXsl who verify the filter and returns them in a list
     * @param filter the filter
     * @param plugin the Plugin
     * @return the list which contains the data of all the Directory Xsl
     */
    public static List<DirectoryXsl> getList( DirectoryXslFilter filter, Plugin plugin )
    {
        return _dao.selectList( filter, plugin );
    }

    /**
     * Loads in the reference list the data of all the DirectoryXsl who verify the filter and returns them in a list
     * @param filter the filter
     * @param plugin the Plugin
     * @return the list which contains the data of all the Directory Xsl
     */
    public static ReferenceList getRefList( DirectoryXslFilter filter, Plugin plugin )
    {
        ReferenceList refList = new ReferenceList(  );

        List<DirectoryXsl> xslList = getList( filter, plugin );
        int index = -1;

        for ( DirectoryXsl directoryXsl : xslList )
        {
            if ( directoryXsl.getExtension(  ).equals( CSV ) )
            {
                index = xslList.indexOf( directoryXsl );
            }
            else
            {
                refList.addItem( directoryXsl.getIdDirectoryXsl(  ), directoryXsl.getTitle(  ) );
            }
        }

        if ( index != -1 )
        {
            ReferenceItem referenceItem = new ReferenceItem(  );
            referenceItem.setCode( String.valueOf( xslList.get( index ).getIdDirectoryXsl(  ) ) );
            referenceItem.setName( xslList.get( index ).getTitle(  ) );
            refList.add( 0, referenceItem );
        }

        return refList;
    }
}

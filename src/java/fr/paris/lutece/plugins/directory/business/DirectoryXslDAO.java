/*
 * Copyright (c) 2002-2017, Mairie de Paris
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
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * This class provides Data Access methods for Directory XSL objects
 */
public final class DirectoryXslDAO implements IDirectoryXslDAO
{
    // Constants
    private static final String SQL_QUERY_NEW_PK = "SELECT max( id_directory_xsl ) FROM directory_xsl";
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = "SELECT id_directory_xsl,title,description,extension,id_file,id_category"
            + " FROM directory_xsl WHERE id_directory_xsl = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO directory_xsl( id_directory_xsl,title,description,extension,id_file,id_category)"
            + " VALUES(?,?,?,?,?,?)";
    private static final String SQL_QUERY_DELETE = "DELETE FROM directory_xsl WHERE id_directory_xsl = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE directory_xsl SET id_directory_xsl=?,title=?,description=?,extension=?,id_file=?,id_category=? WHERE id_directory_xsl = ? ";
    private static final String SQL_QUERY_SELECT = "SELECT id_directory_xsl,title,description,extension,id_file,id_category" + " FROM directory_xsl ";
    private static final String SQL_FILTER_ID_CATEGORY = " id_category = ? ";
    private static final String SQL_ORDER_BY_ID_CATEGORY = " ORDER BY id_category ";

    // Security on files
    private static final String SQL_QUERY_FIND_BY_FILE = "SELECT id_directory_xsl,title,description,extension,id_file,id_category"
            + " FROM directory_xsl WHERE id_file = ?";

    /**
     * {@inheritDoc}
     */
    @Override
    public int newPrimaryKey( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_PK, plugin );
        daoUtil.executeQuery( );

        int nKey;

        if ( !daoUtil.next( ) )
        {
            // if the table is empty
            nKey = 1;
        }

        nKey = daoUtil.getInt( 1 ) + 1;
        daoUtil.free( );

        return nKey;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void insert( DirectoryXsl directoryXsl, Plugin plugin )
    {
        directoryXsl.setIdDirectoryXsl( newPrimaryKey( plugin ) );

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );
        daoUtil.setInt( 1, directoryXsl.getIdDirectoryXsl( ) );
        daoUtil.setString( 2, directoryXsl.getTitle( ) );
        daoUtil.setString( 3, directoryXsl.getDescription( ) );
        daoUtil.setString( 4, directoryXsl.getExtension( ) );

        if ( directoryXsl.getFile( ) != null )
        {
            daoUtil.setInt( 5, directoryXsl.getFile( ).getIdFile( ) );
        }
        else
        {
            daoUtil.setIntNull( 5 );
        }

        if ( directoryXsl.getCategory( ) != null )
        {
            daoUtil.setInt( 6, directoryXsl.getCategory( ).getIdCategory( ) );
        }
        else
        {
            daoUtil.setIntNull( 6 );
        }

        daoUtil.executeUpdate( );

        daoUtil.free( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DirectoryXsl load( int nId, Plugin plugin )
    {
        return load( nId, SQL_QUERY_FIND_BY_PRIMARY_KEY, plugin );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DirectoryXsl loadByFile( int nIdFile, Plugin plugin )
    {
        return load( nIdFile, SQL_QUERY_FIND_BY_FILE, plugin );
    }

    private DirectoryXsl load( int nId, String strSQL, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( strSQL, plugin );
        daoUtil.setInt( 1, nId );
        daoUtil.executeQuery( );

        DirectoryXsl directoryXsl = null;
        Category category = null;
        File file = null;

        if ( daoUtil.next( ) )
        {
            directoryXsl = new DirectoryXsl( );
            directoryXsl.setIdDirectoryXsl( daoUtil.getInt( 1 ) );
            directoryXsl.setTitle( daoUtil.getString( 2 ) );
            directoryXsl.setDescription( daoUtil.getString( 3 ) );
            directoryXsl.setExtension( daoUtil.getString( 4 ) );

            if ( daoUtil.getObject( 5 ) != null )
            {
                file = new File( );
                file.setIdFile( daoUtil.getInt( 5 ) );
                directoryXsl.setFile( file );
            }

            if ( daoUtil.getObject( 6 ) != null )
            {
                category = new Category( );
                category.setIdCategory( daoUtil.getInt( 6 ) );
                directoryXsl.setCategory( category );
            }
        }

        daoUtil.free( );

        return directoryXsl;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete( int nIdDirectoryXsl, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nIdDirectoryXsl );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void store( DirectoryXsl directoryXsl, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );
        daoUtil.setInt( 1, directoryXsl.getIdDirectoryXsl( ) );
        daoUtil.setString( 2, directoryXsl.getTitle( ) );
        daoUtil.setString( 3, directoryXsl.getDescription( ) );
        daoUtil.setString( 4, directoryXsl.getExtension( ) );

        if ( directoryXsl.getFile( ) != null )
        {
            daoUtil.setInt( 5, directoryXsl.getFile( ).getIdFile( ) );
        }
        else
        {
            daoUtil.setIntNull( 5 );
        }

        if ( directoryXsl.getCategory( ) != null )
        {
            daoUtil.setInt( 6, directoryXsl.getCategory( ).getIdCategory( ) );
        }
        else
        {
            daoUtil.setIntNull( 6 );
        }

        daoUtil.setInt( 7, directoryXsl.getIdDirectoryXsl( ) );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<DirectoryXsl> selectList( DirectoryXslFilter filter, Plugin plugin )
    {
        List<DirectoryXsl> directoryXslList = new ArrayList<DirectoryXsl>( );
        List<String> listStrFilter = new ArrayList<String>( );
        DirectoryXsl directoryXsl = null;
        Category category = null;
        File file = null;

        if ( filter.containsIdCategory( ) )
        {
            listStrFilter.add( SQL_FILTER_ID_CATEGORY );
        }

        String strSQL = DirectoryUtils.buildRequetteWithFilter( SQL_QUERY_SELECT, listStrFilter, SQL_ORDER_BY_ID_CATEGORY );

        DAOUtil daoUtil = new DAOUtil( strSQL, plugin );

        int nIndex = 1;

        if ( filter.containsIdCategory( ) )
        {
            daoUtil.setInt( nIndex, filter.getIdCategory( ) );
            nIndex++;
        }

        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            directoryXsl = new DirectoryXsl( );
            directoryXsl.setIdDirectoryXsl( daoUtil.getInt( 1 ) );
            directoryXsl.setTitle( daoUtil.getString( 2 ) );
            directoryXsl.setDescription( daoUtil.getString( 3 ) );
            directoryXsl.setExtension( daoUtil.getString( 4 ) );

            if ( daoUtil.getObject( 5 ) != null )
            {
                file = new File( );
                file.setIdFile( daoUtil.getInt( 5 ) );
                directoryXsl.setFile( file );
            }

            if ( daoUtil.getObject( 6 ) != null )
            {
                category = new Category( );
                category.setIdCategory( daoUtil.getInt( 6 ) );
                directoryXsl.setCategory( category );
            }

            directoryXslList.add( directoryXsl );
        }

        daoUtil.free( );

        return directoryXslList;
    }
}

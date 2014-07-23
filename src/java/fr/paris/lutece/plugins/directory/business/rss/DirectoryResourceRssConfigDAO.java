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
package fr.paris.lutece.plugins.directory.business.rss;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.List;


/**
 *
 * class DirectoryResourceRssConfigDAO
 *
 */
public class DirectoryResourceRssConfigDAO implements IDirectoryResourceRssConfigDAO
{
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = "SELECT id_rss,id_directory,id_entry_title,id_entry_description,id_entry_image,id_entry_link,id_entry_filter_1,value_filter_1,id_entry_filter_2,value_filter_2,id_workflow_state " +
        "FROM directory_rss_cf  WHERE id_rss=?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO directory_rss_cf( " +
        "id_rss,id_directory,id_entry_title,id_entry_description,id_entry_image,id_entry_link,id_entry_filter_1,value_filter_1,id_entry_filter_2,value_filter_2,id_workflow_state)" +
        "VALUES (?,?,?,?,?,?,?,?,?,?,?)";
    private static final String SQL_QUERY_UPDATE = "UPDATE directory_rss_cf " +
        "SET id_rss=?,id_directory=?,id_entry_title=?,id_entry_description=?,id_entry_image=?,id_entry_link=?,id_entry_filter_1=?,value_filter_1=?,id_entry_filter_2=?,value_filter_2=?,id_workflow_state=? " +
        " WHERE id_rss=?";
    private static final String SQL_QUERY_DELETE = "DELETE FROM directory_rss_cf WHERE id_rss=? ";
    private static final String SQL_QUERY_FIND_ALL = "SELECT id_rss,id_directory,id_entry_title,id_entry_description,id_entry_image,id_entry_link,id_workflow_state,id_entry_filter_1,value_filter_1,id_entry_filter_2,value_filter_2 " +
        "FROM directory_rss_cf";

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void insert( DirectoryResourceRssConfig config, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );

        int nPos = 0;

        daoUtil.setInt( ++nPos, config.getIdRss(  ) );
        daoUtil.setInt( ++nPos, config.getIdDirectory(  ) );
        daoUtil.setInt( ++nPos, config.getIdEntryTitle(  ) );
        daoUtil.setInt( ++nPos, config.getIdEntryDescription(  ) );
        daoUtil.setInt( ++nPos, config.getIdEntryImage(  ) );
        daoUtil.setInt( ++nPos, config.getIdEntryLink(  ) );
        daoUtil.setInt( ++nPos, config.getIdEntryFilter1(  ) );
        daoUtil.setString( ++nPos, config.getValueFilter1(  ) );
        daoUtil.setInt( ++nPos, config.getIdEntryFilter2(  ) );
        daoUtil.setString( ++nPos, config.getValueFilter2(  ) );
        daoUtil.setInt( ++nPos, config.getIdWorkflowState(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void store( DirectoryResourceRssConfig config, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );

        int nPos = 0;

        daoUtil.setInt( ++nPos, config.getIdRss(  ) );
        daoUtil.setInt( ++nPos, config.getIdDirectory(  ) );
        daoUtil.setInt( ++nPos, config.getIdEntryTitle(  ) );
        daoUtil.setInt( ++nPos, config.getIdEntryDescription(  ) );
        daoUtil.setInt( ++nPos, config.getIdEntryImage(  ) );
        daoUtil.setInt( ++nPos, config.getIdEntryLink(  ) );
        daoUtil.setInt( ++nPos, config.getIdEntryFilter1(  ) );
        daoUtil.setString( ++nPos, config.getValueFilter1(  ) );
        daoUtil.setInt( ++nPos, config.getIdEntryFilter2(  ) );
        daoUtil.setString( ++nPos, config.getValueFilter2(  ) );
        daoUtil.setInt( ++nPos, config.getIdWorkflowState(  ) );

        daoUtil.setInt( ++nPos, config.getIdRss(  ) );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DirectoryResourceRssConfig load( int nIdRss, Plugin plugin )
    {
        DirectoryResourceRssConfig config = null;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, plugin );

        daoUtil.setInt( 1, nIdRss );

        daoUtil.executeQuery(  );

        int nPos = 0;

        if ( daoUtil.next(  ) )
        {
            config = new DirectoryResourceRssConfig(  );
            config.setIdRss( daoUtil.getInt( ++nPos ) );
            config.setIdDirectory( daoUtil.getInt( ++nPos ) );
            config.setIdEntryTitle( daoUtil.getInt( ++nPos ) );
            config.setIdEntryDescription( daoUtil.getInt( ++nPos ) );
            config.setIdEntryImage( daoUtil.getInt( ++nPos ) );
            config.setIdEntryLink( daoUtil.getInt( ++nPos ) );
            config.setIdEntryFilter1( daoUtil.getInt( ++nPos ) );
            config.setValueFilter1( daoUtil.getString( ++nPos ) );
            config.setIdEntryFilter2( daoUtil.getInt( ++nPos ) );
            config.setValueFilter2( daoUtil.getString( ++nPos ) );
            config.setIdWorkflowState( daoUtil.getInt( ++nPos ) );
        }

        daoUtil.free(  );

        return config;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete( int nIdRss, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );

        daoUtil.setInt( 1, nIdRss );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<DirectoryResourceRssConfig> loadAll( Plugin plugin )
    {
        List<DirectoryResourceRssConfig> configList = new ArrayList<DirectoryResourceRssConfig>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_ALL, plugin );

        daoUtil.executeQuery(  );

        int nPos = 0;

        if ( daoUtil.next(  ) )
        {
            DirectoryResourceRssConfig config = new DirectoryResourceRssConfig(  );
            config.setIdRss( daoUtil.getInt( ++nPos ) );
            config.setIdDirectory( daoUtil.getInt( ++nPos ) );
            config.setIdEntryTitle( daoUtil.getInt( ++nPos ) );
            config.setIdEntryDescription( daoUtil.getInt( ++nPos ) );
            config.setIdEntryImage( daoUtil.getInt( ++nPos ) );
            config.setIdEntryLink( daoUtil.getInt( ++nPos ) );
            config.setIdEntryFilter1( daoUtil.getInt( ++nPos ) );
            config.setValueFilter1( daoUtil.getString( ++nPos ) );
            config.setIdEntryFilter2( daoUtil.getInt( ++nPos ) );
            config.setValueFilter2( daoUtil.getString( ++nPos ) );
            config.setIdWorkflowState( daoUtil.getInt( ++nPos ) );

            configList.add( config );
        }

        daoUtil.free(  );

        return configList;
    }
}

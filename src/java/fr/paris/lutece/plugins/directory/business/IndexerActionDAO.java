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

import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * This class provides Data Access methods for Indexer Action objects
 */
public final class IndexerActionDAO implements IIndexerActionDAO
{
    // Constants
    private static final String SQL_QUERY_NEW_PK = "SELECT max( id_action ) FROM directory_indexer_action";
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = "SELECT id_action,id_record,id_task" +
        " FROM directory_indexer_action WHERE id_action = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO directory_indexer_action( id_action,id_record,id_task)" +
        " VALUES(?,?,?)";
    private static final String SQL_QUERY_DELETE = "DELETE FROM directory_indexer_action WHERE id_action = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE directory_indexer_action SET id_action=?,id_record=?,id_task=? WHERE id_action = ? ";
    private static final String SQL_QUERY_SELECT = "SELECT id_action,id_record,id_task" +
        " FROM directory_indexer_action  ";
    private static final String SQL_FILTER_ID_TASK = " id_task = ? ";

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.service.directorysearch.IIndexerActionDAO#newPrimaryKey(fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public int newPrimaryKey( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_PK, plugin );
        daoUtil.executeQuery(  );

        int nKey;

        if ( !daoUtil.next(  ) )
        {
            // if the table is empty
            nKey = 1;
        }

        nKey = daoUtil.getInt( 1 ) + 1;
        daoUtil.free(  );

        return nKey;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.service.directorysearch.IIndexerActionDAO#insert(fr.paris.lutece.plugins.directory.service.directorysearch.IndexerAction, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public synchronized void insert( IndexerAction indexerAction, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );
        daoUtil.setInt( 2, indexerAction.getIdRecord(  ) );
        daoUtil.setInt( 3, indexerAction.getIdTask(  ) );

        indexerAction.setIdAction( newPrimaryKey( plugin ) );
        daoUtil.setInt( 1, indexerAction.getIdAction(  ) );

        daoUtil.executeUpdate(  );

        daoUtil.free(  );
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.service.directorysearch.IIndexerActionDAO#load(int, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public IndexerAction load( int nId, Plugin plugin )
    {
        IndexerAction indexerAction = null;

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, plugin );
        daoUtil.setInt( 1, nId );
        daoUtil.executeQuery(  );

        if ( daoUtil.next(  ) )
        {
            indexerAction = new IndexerAction(  );
            indexerAction.setIdAction( daoUtil.getInt( 1 ) );
            indexerAction.setIdRecord( daoUtil.getInt( 2 ) );
            indexerAction.setIdTask( daoUtil.getInt( 3 ) );
        }

        daoUtil.free(  );

        return indexerAction;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.service.directorysearch.IIndexerActionDAO#delete(int, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public void delete( int nId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nId );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.service.directorysearch.IIndexerActionDAO#store(fr.paris.lutece.plugins.directory.service.directorysearch.IndexerAction, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public void store( IndexerAction indexerAction, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );
        daoUtil.setInt( 1, indexerAction.getIdAction(  ) );
        daoUtil.setInt( 2, indexerAction.getIdRecord(  ) );
        daoUtil.setInt( 3, indexerAction.getIdTask(  ) );

        daoUtil.setInt( 4, indexerAction.getIdAction(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.service.directorysearch.IIndexerActionDAO#selectList(fr.paris.lutece.plugins.directory.service.directorysearch.IndexerActionlFilter, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public List<IndexerAction> selectList( IndexerActionFilter filter, Plugin plugin )
    {
        List<IndexerAction> indexerActionList = new ArrayList<IndexerAction>(  );
        IndexerAction indexerAction = null;
        List<String> listStrFilter = new ArrayList<String>(  );

        if ( filter.containsIdTask(  ) )
        {
            listStrFilter.add( SQL_FILTER_ID_TASK );
        }

        String strSQL = DirectoryUtils.buildRequetteWithFilter( SQL_QUERY_SELECT, listStrFilter, null );

        DAOUtil daoUtil = new DAOUtil( strSQL, plugin );

        int nIndex = 1;

        if ( filter.containsIdTask(  ) )
        {
            daoUtil.setInt( nIndex, filter.getIdTask(  ) );
            nIndex++;
        }

        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            indexerAction = new IndexerAction(  );
            indexerAction.setIdAction( daoUtil.getInt( 1 ) );
            indexerAction.setIdRecord( daoUtil.getInt( 2 ) );
            indexerAction.setIdTask( daoUtil.getInt( 3 ) );

            indexerActionList.add( indexerAction );
        }

        daoUtil.free(  );

        return indexerActionList;
    }
}

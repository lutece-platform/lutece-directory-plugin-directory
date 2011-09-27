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

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * DirectoryActionDAO
 */
public class DirectoryActionDAO implements IDirectoryActionDAO
{
    private static final String SQL_QUERY_SELECT_ACTIONS = "SELECT a.name_key, a.description_key, a.action_url, a.icon_url, a.action_permission ,a.directory_state" +
        " FROM directory_action a  where a.directory_state=? ";
    private static final String SQL_QUERY_SELECT_ACTIONS_RECORDS = "SELECT a.name_key, a.description_key, a.action_url, a.icon_url, a.action_permission ,a.directory_state" +
        " FROM directory_record_action a  where a.directory_state=? ";
    private static final String SQL_QUERY_SELECT_ACTIONS_XSL = "SELECT a.name_key, a.description_key, a.action_url, a.icon_url, a.action_permission " +
        " FROM directory_xsl_action a ";
    private static final String SQL_QUERY_SELECT_MAX_ACTION_RECORD = "SELECT max(id_action) FROM directory_record_action";
    private static final String SQL_QUERY_ADD_ACTION_RECORD = "INSERT INTO directory_record_action (id_action,name_key,description_key,action_url,icon_url,action_permission,directory_state) VALUES ( ? , ? , ? , ? , ? , ? , ? );";
    private static final String SQL_QUERY_CHECK_ACTION_RECORD = "SELECT id_action FROM directory_record_action WHERE name_key = ? AND description_key = ? AND action_url = ? AND icon_url = ? AND action_permission = ? AND directory_state = ? ;";
    private static final String SQL_QUERY_DELETE_ACTION_RECORD = "DELETE FROM directory_record_action WHERE name_key = ? AND description_key = ? AND action_url = ? AND icon_url = ? AND action_permission = ? AND directory_state = ? ;";

    /**
     * Load the list of actions for a all directory by directory state
     * @param nState the state of the form
     * @param plugin the plugin
     * @return The Collection of actions
     */
    public List<DirectoryAction> selectActionsByDirectoryState( int nState, Plugin plugin )
    {
        List<DirectoryAction> listActions = new ArrayList<DirectoryAction>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_ACTIONS, plugin );
        daoUtil.setInt( 1, nState );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            DirectoryAction action = new DirectoryAction(  );
            action.setNameKey( daoUtil.getString( 1 ) );
            action.setDescriptionKey( daoUtil.getString( 2 ) );
            action.setUrl( daoUtil.getString( 3 ) );
            action.setIconUrl( daoUtil.getString( 4 ) );
            action.setPermission( daoUtil.getString( 5 ) );
            action.setFormState( daoUtil.getInt( 6 ) );
            listActions.add( action );
        }

        daoUtil.free(  );

        return listActions;
    }

    /**
     * Load the list of actions for a all directory by directory state
     * @param nState the state of the form
     * @param plugin the plugin
     * @return The Collection of actions
     */
    public List<DirectoryAction> selectActionsByDirectoryRecordState( int nState, Plugin plugin )
    {
        List<DirectoryAction> listActions = new ArrayList<DirectoryAction>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_ACTIONS_RECORDS, plugin );
        daoUtil.setInt( 1, nState );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            DirectoryAction action = new DirectoryAction(  );
            action.setNameKey( daoUtil.getString( 1 ) );
            action.setDescriptionKey( daoUtil.getString( 2 ) );
            action.setUrl( daoUtil.getString( 3 ) );
            action.setIconUrl( daoUtil.getString( 4 ) );
            action.setPermission( daoUtil.getString( 5 ) );
            action.setFormState( daoUtil.getInt( 6 ) );
            listActions.add( action );
        }

        daoUtil.free(  );

        return listActions;
    }

    /**
     * Add a new action for directory record for module which uses plugin-directory
     * @param directoryAction The action builded in module which uses plugin-directory
     * @param plugin the plugin
     */
    public void addNewActionInDirectoryRecordAction( DirectoryAction directoryAction, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_MAX_ACTION_RECORD, plugin );
        daoUtil.executeQuery(  );

        int nId = 1;

        while ( daoUtil.next(  ) )
        {
            nId = daoUtil.getInt( 1 ) + 1;
        }
        daoUtil.free(  );
        daoUtil = new DAOUtil( SQL_QUERY_ADD_ACTION_RECORD, plugin );
        daoUtil.setInt( 1, nId );
        daoUtil.setString( 2, directoryAction.getNameKey(  ) );
        daoUtil.setString( 3, directoryAction.getDescriptionKey(  ) );
        daoUtil.setString( 4, directoryAction.getUrl(  ) );
        daoUtil.setString( 5, directoryAction.getIconUrl(  ) );
        daoUtil.setString( 6, directoryAction.getPermission(  ) );
        daoUtil.setInt( 7, directoryAction.getFormState(  ) );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    public boolean checkActionsDirectoryRecord( DirectoryAction directoryAction, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_CHECK_ACTION_RECORD, plugin );
        daoUtil.setString( 1, directoryAction.getNameKey(  ) );
        daoUtil.setString( 2, directoryAction.getDescriptionKey(  ) );
        daoUtil.setString( 3, directoryAction.getUrl(  ) );
        daoUtil.setString( 4, directoryAction.getIconUrl(  ) );
        daoUtil.setString( 5, directoryAction.getPermission(  ) );
        daoUtil.setInt( 6, directoryAction.getFormState(  ) );
        daoUtil.executeQuery(  );

        boolean bCheckAction = daoUtil.next(  );
        daoUtil.free(  );

        return bCheckAction;
    }

    /**
     * {@inheritDoc}
     */
    public void deleteActionsDirectoryRecord( DirectoryAction directoryAction, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_ACTION_RECORD, plugin );
        daoUtil.setString( 1, directoryAction.getNameKey(  ) );
        daoUtil.setString( 2, directoryAction.getDescriptionKey(  ) );
        daoUtil.setString( 3, directoryAction.getUrl(  ) );
        daoUtil.setString( 4, directoryAction.getIconUrl(  ) );
        daoUtil.setString( 5, directoryAction.getPermission(  ) );
        daoUtil.setInt( 6, directoryAction.getFormState(  ) );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Load the list of actions for a all directory by directory state
     * @param nState the state of the form
     * @param plugin the plugin
     * @return The Collection of actions
     */
    public List<DirectoryAction> selectActionsByDirectoryXsl( Plugin plugin )
    {
        List<DirectoryAction> listActions = new ArrayList<DirectoryAction>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_ACTIONS_XSL, plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            DirectoryAction action = new DirectoryAction(  );
            action.setNameKey( daoUtil.getString( 1 ) );
            action.setDescriptionKey( daoUtil.getString( 2 ) );
            action.setUrl( daoUtil.getString( 3 ) );
            action.setIconUrl( daoUtil.getString( 4 ) );
            action.setPermission( daoUtil.getString( 5 ) );
            listActions.add( action );
        }

        daoUtil.free(  );

        return listActions;
    }
}

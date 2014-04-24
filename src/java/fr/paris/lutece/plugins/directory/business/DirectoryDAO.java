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

import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;


/**
 * class DirectoryDAO
 */
public final class DirectoryDAO implements IDirectoryDAO
{
    // Constants
    private static final String SQL_QUERY_NEW_PK = "SELECT max( id_directory ) FROM directory_directory";
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = "SELECT id_directory,title,description,"
            + " unavailability_message,workgroup_key,role_key,"
            + " is_enabled,date_creation,id_result_list_template,id_result_record_template,id_form_search_template,number_record_per_page,"
            + " id_workflow, is_search_wf_state, is_search_comp_wf_state, "
            + " is_ascending_sort, is_directory_record_activated, id_sort_entry, is_indexed,id_sort_entry_front,is_ascending_sort_front, front_office_title, automatic_record_removal_workflow_state "
            + " FROM directory_directory WHERE id_directory = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO directory_directory (id_directory,title,description,"
            + " unavailability_message,workgroup_key,role_key,"
            + " is_enabled,date_creation,id_result_list_template,id_result_record_template,id_form_search_template,number_record_per_page,"
            + " id_workflow, is_search_wf_state, is_search_comp_wf_state, is_directory_record_activated, is_indexed, front_office_title, automatic_record_removal_workflow_state) "
            + " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String SQL_QUERY_DELETE = "DELETE FROM directory_directory  WHERE id_directory = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE directory_directory  SET id_directory=?,title=?,description=?,"
            + "unavailability_message=?,workgroup_key=?,role_key=?,"
            + "is_enabled=?,id_result_list_template=?,id_result_record_template=? ,id_form_search_template=? ,number_record_per_page=?,"
            + "id_workflow=?, is_search_wf_state=?, is_search_comp_wf_state=?, "
            + "is_ascending_sort=?, is_directory_record_activated=?, id_sort_entry=?, is_indexed=?,id_sort_entry_front=?,is_ascending_sort_front=?, "
            + "front_office_title=?, automatic_record_removal_workflow_state=? WHERE id_directory=?";
    private static final String SQL_QUERY_SELECT_DIRECTORY_BY_FILTER = "SELECT id_directory,title,description,"
            + "unavailability_message,workgroup_key,role_key,"
            + "is_enabled,date_creation,id_result_list_template,id_result_record_template,id_form_search_template,number_record_per_page"
            + ",id_workflow,is_search_wf_state, is_search_comp_wf_state, is_ascending_sort, "
            + " is_directory_record_activated, id_sort_entry, is_indexed,id_sort_entry_front,is_ascending_sort_front, front_office_title, automatic_record_removal_workflow_state "
            + "FROM directory_directory ";
    private static final String SQL_FILTER_WORKGROUP = " workgroup_key = ? ";
    private static final String SQL_FILTER_IS_ENABLED = " is_enabled = ? ";
    private static final String SQL_FILTER_WORKFLOW = " id_workflow = ? ";
    private static final String SQL_FILTER_IS_INDEXED = " is_indexed = ? ";
    private static final String SQL_ORDER_BY_DATE_CREATION = " ORDER BY date_creation DESC ";
    private static final String SQL_ORDER_BY_TITLE_DESC = " ORDER BY title DESC ";
    private static final String SQL_ORDER_BY_TITLE_ASC = " ORDER BY title ASC ";

    /**
     * Generates a new primary key
     * 
     * @param plugin the plugin
     * @return The new primary key
     */
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
     * Insert a new record in the table.
     * 
     * @param directory instance of the Directory to insert
     * @param plugin the plugin
     * @return the new directory create
     */
    public synchronized int insert( Directory directory, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );
        daoUtil.setString( 2, directory.getTitle( ) );
        daoUtil.setString( 3, directory.getDescription( ) );
        daoUtil.setString( 4, directory.getUnavailabilityMessage( ) );
        daoUtil.setString( 5, directory.getWorkgroup( ) );
        daoUtil.setString( 6, directory.getRoleKey( ) );
        daoUtil.setBoolean( 7, directory.isEnabled( ) );
        daoUtil.setTimestamp( 8, directory.getDateCreation( ) );

        if ( directory.getIdResultListTemplate( ) != DirectoryUtils.CONSTANT_ID_NULL )
        {
            daoUtil.setInt( 9, directory.getIdResultListTemplate( ) );
        }
        else
        {
            daoUtil.setIntNull( 9 );
        }

        if ( directory.getIdResultRecordTemplate( ) != DirectoryUtils.CONSTANT_ID_NULL )
        {
            daoUtil.setInt( 10, directory.getIdResultRecordTemplate( ) );
        }
        else
        {
            daoUtil.setIntNull( 10 );
        }

        if ( directory.getIdFormSearchTemplate( ) != DirectoryUtils.CONSTANT_ID_NULL )
        {
            daoUtil.setInt( 11, directory.getIdFormSearchTemplate( ) );
        }
        else
        {
            daoUtil.setIntNull( 11 );
        }

        daoUtil.setInt( 12, directory.getNumberRecordPerPage( ) );

        if ( directory.getIdWorkflow( ) != DirectoryUtils.CONSTANT_ID_NULL )
        {
            daoUtil.setInt( 13, directory.getIdWorkflow( ) );
        }
        else
        {
            daoUtil.setIntNull( 13 );
        }

        daoUtil.setBoolean( 14, directory.isDisplaySearchState( ) );
        daoUtil.setBoolean( 15, directory.isDisplayComplementarySearchState( ) );
        daoUtil.setBoolean( 16, directory.isRecordActivated( ) );
        daoUtil.setBoolean( 17, directory.isIndexed( ) );
        daoUtil.setString( 18, directory.getFrontOfficeTitle( ) );
        daoUtil.setInt( 19, directory.getIdWorkflowStateToRemove( ) );

        directory.setIdDirectory( newPrimaryKey( plugin ) );
        daoUtil.setInt( 1, directory.getIdDirectory( ) );

        daoUtil.executeUpdate( );
        daoUtil.free( );

        return directory.getIdDirectory( );
    }

    /**
     * Load the data of the Directory from the table
     * 
     * @param nId The identifier of the directory
     * @param plugin the plugin
     * @return the instance of the Directory
     */
    public Directory load( int nId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, plugin );
        daoUtil.setInt( 1, nId );
        daoUtil.executeQuery( );

        Directory directory = null;

        if ( daoUtil.next( ) )
        {
            directory = new Directory( );
            directory.setIdDirectory( daoUtil.getInt( 1 ) );
            directory.setTitle( daoUtil.getString( 2 ) );
            directory.setDescription( daoUtil.getString( 3 ) );
            directory.setUnavailabilityMessage( daoUtil.getString( 4 ) );
            directory.setWorkgroup( daoUtil.getString( 5 ) );
            directory.setRoleKey( daoUtil.getString( 6 ) );
            directory.setEnabled( daoUtil.getBoolean( 7 ) );
            directory.setDateCreation( daoUtil.getTimestamp( 8 ) );

            if ( daoUtil.getObject( 9 ) != null )
            {
                directory.setIdResultListTemplate( daoUtil.getInt( 9 ) );
            }

            if ( daoUtil.getObject( 10 ) != null )
            {
                directory.setIdResultRecordTemplate( daoUtil.getInt( 10 ) );
            }

            if ( daoUtil.getObject( 11 ) != null )
            {
                directory.setIdFormSearchTemplate( daoUtil.getInt( 11 ) );
            }

            directory.setNumberRecordPerPage( daoUtil.getInt( 12 ) );

            if ( daoUtil.getObject( 13 ) != null )
            {
                directory.setIdWorkflow( daoUtil.getInt( 13 ) );
            }

            directory.setDisplaySearchState( daoUtil.getBoolean( 14 ) );
            directory.setDisplayComplementarySearchState( daoUtil.getBoolean( 15 ) );

            directory.setAscendingSort( daoUtil.getBoolean( 16 ) );
            directory.setRecordActivated( daoUtil.getBoolean( 17 ) );
            directory.setIdSortEntry( daoUtil.getString( 18 ) );
            directory.setIndexed( daoUtil.getBoolean( 19 ) );
            directory.setIdSortEntryFront( daoUtil.getString( 20 ) );
            directory.setAscendingSortFront( daoUtil.getBoolean( 21 ) );
            directory.setFrontOfficeTitle( daoUtil.getString( 22 ) );
            directory.setIdWorkflowStateToRemove( daoUtil.getInt( 23 ) );
        }

        daoUtil.free( );

        return directory;
    }

    /**
     * Delete a record from the table
     * 
     * @param nIdDirectory The identifier of the directory
     * @param plugin the plugin
     */
    public void delete( int nIdDirectory, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nIdDirectory );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * Update the directory in the table
     * 
     * @param directory instance of the Directory object to update
     * @param plugin the plugin
     */
    public void store( Directory directory, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );

        daoUtil.setInt( 1, directory.getIdDirectory( ) );
        daoUtil.setString( 2, directory.getTitle( ) );
        daoUtil.setString( 3, directory.getDescription( ) );
        daoUtil.setString( 4, directory.getUnavailabilityMessage( ) );
        daoUtil.setString( 5, directory.getWorkgroup( ) );
        daoUtil.setString( 6, directory.getRoleKey( ) );
        daoUtil.setBoolean( 7, directory.isEnabled( ) );

        if ( directory.getIdResultListTemplate( ) != DirectoryUtils.CONSTANT_ID_NULL )
        {
            daoUtil.setInt( 8, directory.getIdResultListTemplate( ) );
        }
        else
        {
            daoUtil.setIntNull( 8 );
        }

        if ( directory.getIdResultRecordTemplate( ) != DirectoryUtils.CONSTANT_ID_NULL )
        {
            daoUtil.setInt( 9, directory.getIdResultRecordTemplate( ) );
        }
        else
        {
            daoUtil.setIntNull( 9 );
        }

        if ( directory.getIdFormSearchTemplate( ) != DirectoryUtils.CONSTANT_ID_NULL )
        {
            daoUtil.setInt( 10, directory.getIdFormSearchTemplate( ) );
        }
        else
        {
            daoUtil.setIntNull( 10 );
        }

        daoUtil.setInt( 11, directory.getNumberRecordPerPage( ) );

        if ( directory.getIdWorkflow( ) != DirectoryUtils.CONSTANT_ID_NULL )
        {
            daoUtil.setInt( 12, directory.getIdWorkflow( ) );
        }
        else
        {
            daoUtil.setIntNull( 12 );
        }

        daoUtil.setBoolean( 13, directory.isDisplaySearchState( ) );
        daoUtil.setBoolean( 14, directory.isDisplayComplementarySearchState( ) );

        daoUtil.setBoolean( 15, directory.isAscendingSort( ) );
        daoUtil.setBoolean( 16, directory.isRecordActivated( ) );
        daoUtil.setString( 17, directory.getIdSortEntry( ) );
        daoUtil.setBoolean( 18, directory.isIndexed( ) );
        daoUtil.setString( 19, directory.getIdSortEntryFront( ) );
        daoUtil.setBoolean( 20, directory.isAscendingSortFront( ) );
        daoUtil.setString( 21, directory.getFrontOfficeTitle( ) );
        daoUtil.setInt( 22, directory.getIdWorkflowStateToRemove( ) );

        daoUtil.setInt( 23, directory.getIdDirectory( ) );

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * Load the data of all the directory who verify the filter and returns them
     * in a list
     * @param filter the filter
     * @param plugin the plugin
     * @return the list of form
     */
    public List<Directory> selectDirectoryList( DirectoryFilter filter, Plugin plugin )
    {
        List<Directory> directoryList = new ArrayList<Directory>( );
        Directory directory = null;
        List<String> listStrFilter = new ArrayList<String>( );

        if ( filter.containsWorkgroupCriteria( ) )
        {
            listStrFilter.add( SQL_FILTER_WORKGROUP );
        }

        if ( filter.containsIsDisabled( ) )
        {
            listStrFilter.add( SQL_FILTER_IS_ENABLED );
        }

        if ( filter.containsIdWorkflow( ) )
        {
            listStrFilter.add( SQL_FILTER_WORKFLOW );
        }

        if ( filter.containsIsIndexed( ) )
        {
            listStrFilter.add( SQL_FILTER_IS_INDEXED );
        }

        String strOrderBy = SQL_ORDER_BY_DATE_CREATION;

        if ( StringUtils.isNotBlank( filter.getOrder( ) ) )
        {
            if ( Boolean.TRUE.toString( ).equals( filter.getOrder( ) ) )
            {
                strOrderBy = SQL_ORDER_BY_TITLE_ASC;
            }
            else
            {
                strOrderBy = SQL_ORDER_BY_TITLE_DESC;
            }
        }

        String strSQL = DirectoryUtils.buildRequetteWithFilter( SQL_QUERY_SELECT_DIRECTORY_BY_FILTER, listStrFilter,
                strOrderBy );
        DAOUtil daoUtil = new DAOUtil( strSQL, plugin );
        int nIndex = 1;

        if ( filter.containsWorkgroupCriteria( ) )
        {
            daoUtil.setString( nIndex, filter.getWorkgroup( ) );
            nIndex++;
        }

        if ( filter.containsIsDisabled( ) )
        {
            daoUtil.setInt( nIndex, filter.getIsDisabled( ) );
            nIndex++;
        }

        if ( filter.containsIdWorkflow( ) )
        {
            daoUtil.setInt( nIndex, filter.getIdWorkflow( ) );
            nIndex++;
        }

        if ( filter.containsIsIndexed( ) )
        {
            daoUtil.setInt( nIndex, filter.getIsIndexed( ) );
            nIndex++;
        }

        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            directory = new Directory( );
            directory.setIdDirectory( daoUtil.getInt( 1 ) );
            directory.setTitle( daoUtil.getString( 2 ) );
            directory.setDescription( daoUtil.getString( 3 ) );
            directory.setUnavailabilityMessage( daoUtil.getString( 4 ) );
            directory.setWorkgroup( daoUtil.getString( 5 ) );
            directory.setRoleKey( daoUtil.getString( 6 ) );
            directory.setEnabled( daoUtil.getBoolean( 7 ) );
            directory.setDateCreation( daoUtil.getTimestamp( 8 ) );

            if ( daoUtil.getObject( 9 ) != null )
            {
                directory.setIdResultListTemplate( daoUtil.getInt( 9 ) );
            }

            if ( daoUtil.getObject( 10 ) != null )
            {
                directory.setIdResultRecordTemplate( daoUtil.getInt( 10 ) );
            }

            if ( daoUtil.getObject( 11 ) != null )
            {
                directory.setIdFormSearchTemplate( daoUtil.getInt( 11 ) );
            }

            directory.setNumberRecordPerPage( daoUtil.getInt( 12 ) );
            directory.setIdWorkflow( daoUtil.getInt( 13 ) );
            directory.setDisplaySearchState( daoUtil.getBoolean( 14 ) );
            directory.setDisplayComplementarySearchState( daoUtil.getBoolean( 15 ) );
            directory.setAscendingSort( daoUtil.getBoolean( 16 ) );
            directory.setRecordActivated( daoUtil.getBoolean( 17 ) );
            directory.setIdSortEntry( daoUtil.getString( 18 ) );
            directory.setIndexed( daoUtil.getBoolean( 19 ) );
            directory.setIdSortEntryFront( daoUtil.getString( 20 ) );
            directory.setAscendingSortFront( daoUtil.getBoolean( 21 ) );
            directory.setFrontOfficeTitle( daoUtil.getString( 22 ) );
            directory.setIdWorkflowStateToRemove( daoUtil.getInt( 23 ) );

            directoryList.add( directory );
        }

        daoUtil.free( );

        return directoryList;
    }

    /**
     * Load the data of all enable directory returns them in a reference list
     * @param plugin the plugin
     * @return a reference list of directory
     */
    public ReferenceList getEnableDirectoryList( Plugin plugin )
    {
        ReferenceList listDirectory = new ReferenceList( );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_DIRECTORY_BY_FILTER + SQL_ORDER_BY_DATE_CREATION, plugin );
        daoUtil.executeQuery( );

        Directory directory;

        while ( daoUtil.next( ) )
        {
            directory = new Directory( );
            directory.setIdDirectory( daoUtil.getInt( 1 ) );
            directory.setTitle( daoUtil.getString( 2 ) );
            listDirectory.addItem( directory.getIdDirectory( ), directory.getTitle( ) );
        }

        daoUtil.free( );

        return listDirectory;
    }
}

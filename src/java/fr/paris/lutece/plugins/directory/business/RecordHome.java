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

import fr.paris.lutece.plugins.directory.service.DirectoryService;
import fr.paris.lutece.plugins.directory.service.directorysearch.DirectoryIndexer;
import fr.paris.lutece.plugins.directory.service.directorysearch.DirectorySearchService;
import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;

import java.util.List;


/**
 *class RecordHome
 */
public final class RecordHome
{
    // Static variable pointed at the DAO instance
    private static IRecordDAO _dao = (IRecordDAO) SpringContextService.getPluginBean( "directory", "directoryRecordDAO" );
    private static final int STEP_DELETE = 50;

    /**
     * Private constructor - this class need not be instantiated
     */
    private RecordHome(  )
    {
    }

    /**
     * Creation of an instance of record
     *
     * @param record The instance of the record which contains the informations to store
     * @param plugin the Plugin
     * @return the id of the new record
     *
     */
    public static int create( Record record, Plugin plugin )
    {
        record.setDateModification( DirectoryUtils.getCurrentTimestamp(  ) );
        record.setIdRecord( _dao.insert( record, plugin ) );

        DirectorySearchService.getInstance(  )
                              .addIndexerAction( record.getIdRecord(  ), IndexerAction.TASK_CREATE, plugin );

        for ( RecordField recordField : record.getListRecordField(  ) )
        {
            recordField.setRecord( record );
            RecordFieldHome.create( recordField, plugin );
        }

        return record.getIdRecord(  );
    }

    /**
     * Copy an instance of record
     *
     * @param record The instance of the record who must copy
     * @param plugin the Plugin
     * @return the id of the record
     *
     */
    public static int copy( Record record, Plugin plugin )
    {
        record.setDateModification( DirectoryUtils.getCurrentTimestamp(  ) );

        RecordFieldFilter filter = new RecordFieldFilter(  );
        filter.setIdRecord( record.getIdRecord(  ) );
        record.setListRecordField( RecordFieldHome.getRecordFieldList( filter, plugin ) );
        record.setIdRecord( _dao.insert( record, plugin ) );

        DirectorySearchService.getInstance(  )
                              .addIndexerAction( record.getIdRecord(  ), IndexerAction.TASK_CREATE, plugin );

        for ( RecordField recordField : record.getListRecordField(  ) )
        {
            recordField.setRecord( record );

            //we don't copy numbering entry
            if ( !recordField.getEntry(  ).getEntryType(  ).getClassName(  ).equals( EntryTypeNumbering.class.getName(  ) ) )
            {
                RecordFieldHome.copy( recordField, plugin );
            }
            else
            {
                //update the number
                IEntry entryNumbering = EntryHome.findByPrimaryKey( recordField.getEntry(  ).getIdEntry(  ), plugin );
                int numbering = DirectoryService.getInstance(  ).getMaxNumber( entryNumbering );

                if ( numbering != DirectoryUtils.CONSTANT_ID_NULL )
                {
                    entryNumbering.getFields(  ).get( 0 ).setValue( String.valueOf( numbering + 1 ) );
                    FieldHome.update( entryNumbering.getFields(  ).get( 0 ), plugin );
                    recordField.setValue( String.valueOf( numbering ) );
                    RecordFieldHome.create( recordField, plugin );
                }
            }
        }

        return record.getIdRecord(  );
    }

    /**
     * Update of the record which is specified in parameter
     *
     * @param record The instance of the record which contains the informations to update
     * @param plugin the Plugin
     *
     */
    public static void updateWidthRecordField( Record record, Plugin plugin )
    {
        record.setDateModification( DirectoryUtils.getCurrentTimestamp(  ) );
        DirectorySearchService.getInstance(  )
                              .addIndexerAction( record.getIdRecord(  ), IndexerAction.TASK_MODIFY, plugin );

        _dao.store( record, plugin );

        //delete all record field in database associate
        RecordFieldFilter filter = new RecordFieldFilter(  );
        filter.setIdRecord( record.getIdRecord(  ) );
        RecordFieldHome.removeByFilter( filter, plugin );

        //insert the new record Field
        for ( RecordField recordField : record.getListRecordField(  ) )
        {
            recordField.setRecord( record );
            RecordFieldHome.create( recordField, plugin );
        }
    }

    /**
     * Update of the record
     *
     * @param record The instance of the record which contains the informations to update
     * @param plugin the Plugin
     *
     */
    public static void update( Record record, Plugin plugin )
    {
        record.setDateModification( DirectoryUtils.getCurrentTimestamp(  ) );
        DirectorySearchService.getInstance(  )
                              .addIndexerAction( record.getIdRecord(  ), IndexerAction.TASK_MODIFY, plugin );

        _dao.store( record, plugin );
    }

    /**
     * Remove the record whose identifier is specified in parameter
     *
     * @param nIdRecord The recordId
     * @param plugin the Plugin
     */
    public static void remove( int nIdRecord, Plugin plugin )
    {
        DirectorySearchService.getInstance(  ).addIndexerAction( nIdRecord, IndexerAction.TASK_DELETE, plugin );
        WorkflowService.getInstance(  ).doRemoveWorkFlowResource( nIdRecord, Record.WORKFLOW_RESOURCE_TYPE );

        //delete all record field in database associate
        RecordFieldFilter filter = new RecordFieldFilter(  );
        filter.setIdRecord( nIdRecord );
        RecordFieldHome.removeByFilter( filter, true, plugin );
        _dao.delete( nIdRecord, plugin );
    }

    /**
     * Remove directory and workflow record by directory Id
     * @param nIdDirectory The directory id
     * @param plugin The plugin
     * @deprecated This function does not remove the associated files
     */
    public static void removeByIdDirectory( Integer nIdDirectory, Plugin plugin )
    {
        WorkflowService workflowService = WorkflowService.getInstance(  );
        boolean nWorkFlowServiceIsAvaible = workflowService.isAvailable(  );

        Directory directory = DirectoryHome.findByPrimaryKey( nIdDirectory, plugin );

        RecordFieldFilter recordFilter = new RecordFieldFilter(  );
        recordFilter.setIdDirectory( nIdDirectory );

        List<Integer> listRecordId = RecordHome.getListRecordId( recordFilter, plugin );

        // --- Suppress record fields & workflow resources ---
        int nListRecordIdSize = listRecordId.size(  );

        if ( nListRecordIdSize > STEP_DELETE )
        {
            int nMax = nListRecordIdSize - STEP_DELETE;
            int nIndex = 0;
            List<Integer> subList;

            for ( int i = 0; i < nMax; i += STEP_DELETE )
            {
                subList = listRecordId.subList( i, i + STEP_DELETE );
                RecordFieldHome.removeByListRecordId( subList, plugin );

                if ( nWorkFlowServiceIsAvaible )
                {
                    workflowService.doRemoveWorkFlowResourceByListId( subList, Record.WORKFLOW_RESOURCE_TYPE,
                        directory.getIdWorkflow(  ) );
                }

                nIndex = i;
            }

            subList = listRecordId.subList( nIndex, nListRecordIdSize );
            RecordFieldHome.removeByListRecordId( subList, plugin );

            if ( nWorkFlowServiceIsAvaible )
            {
                workflowService.doRemoveWorkFlowResourceByListId( subList, Record.WORKFLOW_RESOURCE_TYPE,
                    directory.getIdWorkflow(  ) );
            }
        }
        else
        {
            RecordFieldHome.removeByListRecordId( listRecordId, plugin );

            if ( nWorkFlowServiceIsAvaible )
            {
                workflowService.doRemoveWorkFlowResourceByListId( listRecordId, Record.WORKFLOW_RESOURCE_TYPE,
                    directory.getIdWorkflow(  ) );
            }
        }

        // --- Suppress records ---
        _dao.deleteRecordByDirectoryId( nIdDirectory, plugin );

        // --- Update index ---
        // Hack to bypass problem of primary key violation on table "directory_indexer_action"
        // when inserting many records
        // TODO : fixe me
        DirectoryIndexer.appendListRecordToDelete( listRecordId );
    }

    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Returns an instance of a recordwhose identifier is specified in parameter
     *
     * @param nKey The formResponse primary key
     * @param plugin the Plugin
     * @return an instance of FormResponse
     */
    public static Record findByPrimaryKey( int nKey, Plugin plugin )
    {
        return _dao.load( nKey, plugin );
    }

    /**
     * Test if the given directory record list has a worflow
     * @param nIdDirectory directory Id
     * @param plugin the plugin
     * @return true if has at least one
     */
    public static Boolean direcytoryRecordListHasWorkflow( int nIdDirectory, Plugin plugin )
    {
        return _dao.direcytoryRecordListHasWorkflow( nIdDirectory, plugin );
    }

    /**
     * Load a list of record
     * @param listId list of record id
     * @param plugin the plugin
     * @return list of Record
     */
    public static List<Record> loadListByListId( List<Integer> lIdList, Plugin plugin )
    {
        return _dao.loadList( lIdList, plugin );
    }

    /**
        * Load the data of all the record who verify the filter and returns them in a  list
        * @param filter the filter
        * @param plugin the plugin
        * @return  the list of record
        */
    public static List<Record> getListRecord( RecordFieldFilter filter, Plugin plugin )
    {
        return _dao.selectListByFilter( filter, plugin );
    }

    /**
     * Count record who verify the filter
     * @param filter the filter
     * @param plugin the plugin
     * @return  the number of record
     */
    public static int getCountRecord( RecordFieldFilter filter, Plugin plugin )
    {
        return _dao.selectCountByFilter( filter, plugin );
    }

    /**
     * Load the data of all the record who verify the filter and returns them in a  list
     * @param filter the filter
     * @param plugin the plugin
     * @return  the list of record
     */
    public static List<Integer> getListRecordId( RecordFieldFilter filter, Plugin plugin )
    {
        return _dao.selectListIdByFilter( filter, plugin );
    }

    /**
     * Get directory id by by record id
     * @param nRecordId the record id
     * @param plugin the plugin
     * @return the directory id
     */
    public static Integer getDirectoryIdByRecordId( Integer nRecordId, Plugin plugin )
    {
        return _dao.getDirectoryIdByRecordId( nRecordId, plugin );
    }
}

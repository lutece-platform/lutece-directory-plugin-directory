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
package fr.paris.lutece.plugins.directory.service;

import fr.paris.lutece.plugins.directory.business.Directory;
import fr.paris.lutece.plugins.directory.business.DirectoryFilter;
import fr.paris.lutece.plugins.directory.business.DirectoryHome;
import fr.paris.lutece.plugins.directory.business.Record;
import fr.paris.lutece.plugins.directory.service.record.IRecordService;
import fr.paris.lutece.plugins.directory.service.record.RecordService;
import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.portal.service.daemon.Daemon;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;

import java.util.List;

/**
 * Daemon to remove entries of directories that are in a given state
 */
public class DirectoryWorkflowRecordRemovalDaemon extends Daemon
{
    private IRecordService _recordService;

    /**
     * {@inheritDoc}
     */
    @Override
    public void run( )
    {
        WorkflowService workflowService = WorkflowService.getInstance( );

        if ( workflowService.isAvailable( ) )
        {
            DirectoryFilter filter = new DirectoryFilter( );
            Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
            List<Directory> listDirectory = DirectoryHome.getDirectoryList( filter, PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME ) );
            IRecordService recordService = getRecordService( );
            int nRemovedRecords = 0;

            for ( Directory directory : listDirectory )
            {
                if ( ( directory.getIdWorkflow( ) > DirectoryUtils.CONSTANT_ID_NULL )
                        && ( directory.getIdWorkflowStateToRemove( ) != DirectoryUtils.CONSTANT_ID_NULL ) )
                {
                    List<Integer> listRecordId = workflowService.getResourceIdListByIdState( directory.getIdWorkflowStateToRemove( ),
                            Record.WORKFLOW_RESOURCE_TYPE );

                    if ( ( listRecordId != null ) && ( listRecordId.size( ) > 0 ) )
                    {
                        for ( int nRecordId : listRecordId )
                        {
                            recordService.remove( nRecordId, pluginDirectory );
                        }

                        nRemovedRecords += listRecordId.size( );
                    }
                }
            }

            setLastRunLogs( nRemovedRecords + " record(s) have been removed" );
        }
        else
        {
            setLastRunLogs( "No avaiable workflow service found" );
        }
    }

    /**
     * Get the instance of the record service
     * 
     * @return The instance of the record service
     */
    private IRecordService getRecordService( )
    {
        if ( _recordService == null )
        {
            _recordService = SpringContextService.getBean( RecordService.BEAN_SERVICE );
        }

        return _recordService;
    }
}

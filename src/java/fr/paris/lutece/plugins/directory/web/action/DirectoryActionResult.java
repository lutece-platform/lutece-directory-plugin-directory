/*
 * Copyright (c) 2002-2013, Mairie de Paris
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
package fr.paris.lutece.plugins.directory.web.action;

import fr.paris.lutece.plugins.directory.business.Directory;
import fr.paris.lutece.plugins.directory.business.IEntry;
import fr.paris.lutece.plugins.directory.business.Record;
import fr.paris.lutece.plugins.directory.service.record.IRecordService;
import fr.paris.lutece.plugins.directory.service.record.RecordService;
import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.service.action.ActionService;
import fr.paris.lutece.plugins.workflowcore.service.action.IActionService;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 *
 * DirectoryActionResult
 *
 */
public class DirectoryActionResult
{
    // MARKS
    private static final String MARK_RECORD = "record";
    private static final String MARK_WORKFLOW_STATE = "workflow_state";
    private static final String MARK_MAP_ID_ENTRY_LIST_RECORD_FIELD = "map_id_entry_list_record_field";
    private static final String MARK_LIST_SUCCESS_RECORDS = "list_success_records";
    private static final String MARK_LIST_FAIL_RECORDS = "list_fail_records";
    private static final String MARK_MAP_FAIL_RECORD_CAUSES = "map_fail_record_causes";

    // VARIABLES
    private List<Integer> _listIdsSuccessRecord;
    private Map<String, String> _mapFailRecords;

    /**
     * Do process the workflow action
     * @param nIdDirectory the id directory
     * @param nIdAction the id action
     * @param listIdsDirectoryRecord the list of ids record to execute the action
     * @param plugin the plugin
     * @param locale the locale
     * @param request the HTTP request
     */
    public void doProcessAction( int nIdDirectory, int nIdAction, String[] listIdsDirectoryRecord, Plugin plugin,
        Locale locale, HttpServletRequest request )
    {
        _listIdsSuccessRecord = new ArrayList<Integer>(  );
        _mapFailRecords = new HashMap<String, String>(  );

        for ( String strIdDirectoryRecord : listIdsDirectoryRecord )
        {
            int nIdRecord = DirectoryUtils.convertStringToInt( strIdDirectoryRecord );

            // Get the id action to execute : it may not be the one given in the parameter, but one
            // of the linked actions
            int nIdActionToExecute = getIdActionToExecute( nIdDirectory, nIdAction, nIdRecord, request );

            // If nIdActionToExecute == -1, then there are no actions that can be executed to the record
            if ( nIdActionToExecute != DirectoryUtils.CONSTANT_ID_NULL )
            {
                IRecordService recordService = SpringContextService.getBean( RecordService.BEAN_SERVICE );
                boolean bHasSucceed = false;

                try
                {
                    WorkflowService.getInstance(  )
                                   .doProcessAction( nIdRecord, Record.WORKFLOW_RESOURCE_TYPE, nIdActionToExecute,
                        nIdDirectory, request, locale, false );
                    bHasSucceed = true;
                }
                catch ( Exception e )
                {
                    AppLogService.error( "Error processing action for id record '" + nIdRecord + "' - cause : " +
                        e.getMessage(  ), e );
                    _mapFailRecords.put( strIdDirectoryRecord, e.getMessage(  ) );
                }

                if ( bHasSucceed )
                {
                    _listIdsSuccessRecord.add( nIdRecord );

                    // Update record modification date
                    Record record = recordService.findByPrimaryKey( nIdRecord, plugin );
                    recordService.update( record, plugin );
                }
            }
            else
            {
                String strMessage = I18nService.getLocalizedString( DirectoryUtils.MESSAGE_RECORD_INVALID_STATE, locale );
                _mapFailRecords.put( strIdDirectoryRecord, strMessage );
            }
        }
    }

    /**
     * Do save the task form
     * @param nIdDirectory the id directory
     * @param nIdAction the id action
     * @param listIdsDirectoryRecord the list of id records
     * @param plugin the plugin
     * @param locale the locale
     * @param request the HTTP request
     * @return an error message if there is an error, null otherwise
     */
    public String doSaveTaskForm( int nIdDirectory, int nIdAction, String[] listIdsDirectoryRecord, Plugin plugin,
        Locale locale, HttpServletRequest request )
    {
        _listIdsSuccessRecord = new ArrayList<Integer>(  );
        _mapFailRecords = new HashMap<String, String>(  );

        for ( String strIdDirectoryRecord : listIdsDirectoryRecord )
        {
            int nIdRecord = DirectoryUtils.convertStringToInt( strIdDirectoryRecord );

            if ( WorkflowService.getInstance(  )
                                    .canProcessAction( nIdRecord, Record.WORKFLOW_RESOURCE_TYPE, nIdAction,
                        nIdDirectory, request, false ) )
            {
                boolean bHasSucceed = false;

                try
                {
                    String strError = WorkflowService.getInstance(  )
                                                     .doSaveTasksForm( nIdRecord, Record.WORKFLOW_RESOURCE_TYPE,
                            nIdAction, nIdDirectory, request, locale );

                    if ( strError != null )
                    {
                        return strError;
                    }

                    bHasSucceed = true;
                }
                catch ( Exception e )
                {
                    String strExceptionError = buildErrorMessage( e );
                    AppLogService.error( "Error processing action for id record '" + nIdRecord + "' - cause : " +
                        strExceptionError, e );
                    _mapFailRecords.put( strIdDirectoryRecord, strExceptionError );
                }

                if ( bHasSucceed )
                {
                    _listIdsSuccessRecord.add( nIdRecord );

                    IRecordService recordService = SpringContextService.getBean( RecordService.BEAN_SERVICE );

                    // Update record modification date
                    Record record = recordService.findByPrimaryKey( nIdRecord, plugin );
                    recordService.update( record, plugin );
                }
            }
            else
            {
                String strMessage = I18nService.getLocalizedString( DirectoryUtils.MESSAGE_RECORD_INVALID_STATE, locale );
                _mapFailRecords.put( strIdDirectoryRecord, strMessage );
            }
        }

        return null;
    }

    /**
     * Fill the model with the success and fail records
     * @param model the model to fill
     * @param listEntries the list of entries
     * @param plugin the plugin
     * @param user the current user
     * @param directory the directory
     */
    public void fillModel( Map<String, Object> model, List<IEntry> listEntries, Plugin plugin, AdminUser user,
        Directory directory )
    {
        IRecordService recordService = SpringContextService.getBean( RecordService.BEAN_SERVICE );

        // Add the success records to the model
        if ( ( _listIdsSuccessRecord != null ) && !_listIdsSuccessRecord.isEmpty(  ) )
        {
            List<Record> listRecords = recordService.loadListByListId( _listIdsSuccessRecord, plugin );
            List<Map<String, Object>> listMapRecords = new ArrayList<Map<String, Object>>( _listIdsSuccessRecord.size(  ) );

            for ( Record record : listRecords )
            {
                Map<String, Object> mapRecord = new HashMap<String, Object>(  );
                mapRecord.put( MARK_RECORD, record );
                mapRecord.put( MARK_MAP_ID_ENTRY_LIST_RECORD_FIELD,
                    DirectoryUtils.getMapIdEntryListRecordField( listEntries, record.getIdRecord(  ), plugin, false ) );

                WorkflowService workflowService = WorkflowService.getInstance(  );
                State state = workflowService.getState( record.getIdRecord(  ), Record.WORKFLOW_RESOURCE_TYPE,
                        directory.getIdWorkflow(  ), Integer.valueOf( directory.getIdDirectory(  ) ) );
                mapRecord.put( MARK_WORKFLOW_STATE, state );
                listMapRecords.add( mapRecord );
            }

            model.put( MARK_LIST_SUCCESS_RECORDS, listMapRecords );
        }

        // Add the fail records to the model
        if ( ( _mapFailRecords != null ) && !_mapFailRecords.isEmpty(  ) )
        {
            List<Integer> listIdsFailRecord = new ArrayList<Integer>(  );

            for ( String strIdRecord : _mapFailRecords.keySet(  ) )
            {
                listIdsFailRecord.add( DirectoryUtils.convertStringToInt( strIdRecord ) );
            }

            List<Record> listRecords = recordService.loadListByListId( listIdsFailRecord, plugin );
            List<Map<String, Object>> listMapRecords = new ArrayList<Map<String, Object>>( listIdsFailRecord.size(  ) );

            for ( Record record : listRecords )
            {
                Map<String, Object> mapRecord = new HashMap<String, Object>(  );
                mapRecord.put( MARK_RECORD, record );
                mapRecord.put( MARK_MAP_ID_ENTRY_LIST_RECORD_FIELD,
                    DirectoryUtils.getMapIdEntryListRecordField( listEntries, record.getIdRecord(  ), plugin, false ) );

                WorkflowService workflowService = WorkflowService.getInstance(  );
                State state = workflowService.getState( record.getIdRecord(  ), Record.WORKFLOW_RESOURCE_TYPE,
                        directory.getIdWorkflow(  ), Integer.valueOf( directory.getIdDirectory(  ) ) );
                mapRecord.put( MARK_WORKFLOW_STATE, state );
                listMapRecords.add( mapRecord );
            }

            model.put( MARK_LIST_FAIL_RECORDS, listMapRecords );
        }

        model.put( MARK_MAP_FAIL_RECORD_CAUSES, _mapFailRecords );
    }

    /**
     * Build the error message for action results
     * @param e the exception
     * @return the error message
     */
    private String buildErrorMessage( Exception e )
    {
        String strError = e.getMessage(  );

        if ( StringUtils.isBlank( strError ) && ( e.getStackTrace(  ) != null ) && ( e.getStackTrace(  ).length > 0 ) )
        {
            StringBuilder sbError = new StringBuilder(  );

            for ( StackTraceElement ele : e.getStackTrace(  ) )
            {
                sbError.append( ele.toString(  ) );
                sbError.append( "\n" );
            }

            strError = sbError.toString(  );
        }

        return strError;
    }

    /**
     * Get the ID action to execute. It may not be the one given in
     * the parameters, but one of the linked actions of the given ID action.
     * @param nIdDirectory the id directory
     * @param nIdAction the id action
     * @param nIdRecord the id record
     * @param request the HTTP request
     * @return the ID action to execute
     */
    private int getIdActionToExecute( int nIdDirectory, int nIdAction, int nIdRecord, HttpServletRequest request )
    {
        if ( WorkflowService.getInstance(  )
                                .canProcessAction( nIdRecord, Record.WORKFLOW_RESOURCE_TYPE, nIdAction, nIdDirectory,
                    request, false ) )
        {
            return nIdAction;
        }

        IActionService actionService = SpringContextService.getBean( ActionService.BEAN_SERVICE );
        Collection<Integer> listIdsLinkedAction = actionService.getListIdsLinkedAction( nIdAction );

        if ( ( listIdsLinkedAction != null ) && !listIdsLinkedAction.isEmpty(  ) )
        {
            for ( int nIdLinkedAction : listIdsLinkedAction )
            {
                if ( WorkflowService.getInstance(  )
                                        .canProcessAction( nIdRecord, Record.WORKFLOW_RESOURCE_TYPE, nIdLinkedAction,
                            nIdDirectory, request, false ) )
                {
                    return nIdLinkedAction;
                }
            }
        }

        return DirectoryUtils.CONSTANT_ID_NULL;
    }
}

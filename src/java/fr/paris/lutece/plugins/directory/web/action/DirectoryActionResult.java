package fr.paris.lutece.plugins.directory.web.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.directory.business.Directory;
import fr.paris.lutece.plugins.directory.business.IEntry;
import fr.paris.lutece.plugins.directory.business.Record;
import fr.paris.lutece.plugins.directory.business.RecordHome;
import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.business.workflow.State;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;

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
	public void doProcessAction( int nIdDirectory, int nIdAction, String[] listIdsDirectoryRecord, 
			Plugin plugin, Locale locale, HttpServletRequest request )
	{
        _listIdsSuccessRecord = new ArrayList<Integer>(  );
        _mapFailRecords = new HashMap<String, String>(  );
        
        for ( String strIdDirectoryRecord : listIdsDirectoryRecord )
        {
        	int nIdRecord = DirectoryUtils.convertStringToInt( strIdDirectoryRecord );
        	if ( WorkflowService.getInstance(  ).canProcessAction( nIdRecord, Record.WORKFLOW_RESOURCE_TYPE, 
        			nIdAction, nIdDirectory, request, false ) )
        	{
        		boolean bHasSucceed = false;
        		try
        		{
        			WorkflowService.getInstance(  )
        			.doProcessAction( nIdRecord, Record.WORKFLOW_RESOURCE_TYPE, nIdAction,
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
        			Record record = RecordHome.findByPrimaryKey( nIdRecord, plugin );
        			RecordHome.update( record, plugin );
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
	public String doSaveTaskForm( int nIdDirectory, int nIdAction, String[] listIdsDirectoryRecord, 
			Plugin plugin, Locale locale, HttpServletRequest request )
	{
		_listIdsSuccessRecord = new ArrayList<Integer>(  );
        _mapFailRecords = new HashMap<String, String>(  );
		for ( String strIdDirectoryRecord : listIdsDirectoryRecord )
    	{
    		int nIdRecord = DirectoryUtils.convertStringToInt( strIdDirectoryRecord );
    		
    		if ( WorkflowService.getInstance(  ).canProcessAction( nIdRecord, Record.WORKFLOW_RESOURCE_TYPE, 
        			nIdAction, nIdDirectory, request, false ) )
        	{
    			boolean bHasSucceed = false;
    			try
    			{
    				String strError = WorkflowService.getInstance(  )
    				.doSaveTasksForm( nIdRecord, Record.WORKFLOW_RESOURCE_TYPE, nIdAction,
    						nIdDirectory, request, locale );
    				if ( strError != null )
        			{
        				return strError;
        			}
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
        			Record record = RecordHome.findByPrimaryKey( nIdRecord, plugin );
        			RecordHome.update( record, plugin );
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
	public void fillModel( Map<String, Object> model, List<IEntry> listEntries, Plugin plugin, AdminUser user, Directory directory )
	{
		// Add the success records to the model
		if ( _listIdsSuccessRecord != null && !_listIdsSuccessRecord.isEmpty(  ) )
		{
			List<Record> listRecords = RecordHome.loadListByListId( _listIdsSuccessRecord, plugin );
			List<Map<String, Object>> listMapRecords = new ArrayList<Map<String, Object>>( _listIdsSuccessRecord.size(  ) );
			for ( Record record : listRecords )
	        {
	            Map<String, Object> mapRecord = new HashMap<String, Object>(  );
	            mapRecord.put( MARK_RECORD, record );
	            mapRecord.put( MARK_MAP_ID_ENTRY_LIST_RECORD_FIELD,
	                DirectoryUtils.getMapIdEntryListRecordField( listEntries, record.getIdRecord(  ),
	                    plugin, false ) );
	        	WorkflowService workflowService = WorkflowService.getInstance(  );
	            State state = workflowService.getState( record.getIdRecord(  ), Record.WORKFLOW_RESOURCE_TYPE,
	                    directory.getIdWorkflow(  ), Integer.valueOf( directory.getIdDirectory(  ) ), user );
	            mapRecord.put( MARK_WORKFLOW_STATE, state );
    	        listMapRecords.add( mapRecord );
	        }
			model.put( MARK_LIST_SUCCESS_RECORDS, listMapRecords );
		}
		
		// Add the fail records to the model
		if ( _mapFailRecords != null && !_mapFailRecords.isEmpty(  ) )
		{
			List<Integer> listIdsFailRecord = new ArrayList<Integer>(  );
			for ( String strIdRecord : _mapFailRecords.keySet(  ) )
			{
				listIdsFailRecord.add( DirectoryUtils.convertStringToInt( strIdRecord ) );
			}
			List<Record> listRecords = RecordHome.loadListByListId( listIdsFailRecord, plugin );
			List<Map<String, Object>> listMapRecords = new ArrayList<Map<String, Object>>( listIdsFailRecord.size(  ) );
			for ( Record record : listRecords )
	        {
	            Map<String, Object> mapRecord = new HashMap<String, Object>(  );
	            mapRecord.put( MARK_RECORD, record );
	            mapRecord.put( MARK_MAP_ID_ENTRY_LIST_RECORD_FIELD,
	                DirectoryUtils.getMapIdEntryListRecordField( listEntries, record.getIdRecord(  ),
	                    plugin, false ) );
	        	WorkflowService workflowService = WorkflowService.getInstance(  );
	            State state = workflowService.getState( record.getIdRecord(  ), Record.WORKFLOW_RESOURCE_TYPE,
	                    directory.getIdWorkflow(  ), Integer.valueOf( directory.getIdDirectory(  ) ), user );
	            mapRecord.put( MARK_WORKFLOW_STATE, state );
	            listMapRecords.add( mapRecord );
	        }
			model.put( MARK_LIST_FAIL_RECORDS, listMapRecords );
		}
		model.put( MARK_MAP_FAIL_RECORD_CAUSES, _mapFailRecords );
	}
}

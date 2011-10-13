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
package fr.paris.lutece.plugins.directory.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.directory.business.Directory;
import fr.paris.lutece.plugins.directory.business.DirectoryAction;
import fr.paris.lutece.plugins.directory.business.DirectoryXsl;
import fr.paris.lutece.plugins.directory.business.Entry;
import fr.paris.lutece.plugins.directory.business.EntryType;
import fr.paris.lutece.plugins.directory.business.EntryTypeDownloadUrl;
import fr.paris.lutece.plugins.directory.business.EntryTypeGeolocation;
import fr.paris.lutece.plugins.directory.business.EntryTypeHome;
import fr.paris.lutece.plugins.directory.business.Field;
import fr.paris.lutece.plugins.directory.business.FieldHome;
import fr.paris.lutece.plugins.directory.business.IEntry;
import fr.paris.lutece.plugins.directory.business.Record;
import fr.paris.lutece.plugins.directory.business.RecordField;
import fr.paris.lutece.plugins.directory.business.RecordFieldFilter;
import fr.paris.lutece.plugins.directory.business.RecordFieldHome;
import fr.paris.lutece.plugins.directory.business.RecordHome;
import fr.paris.lutece.plugins.directory.business.parameter.DirectoryParameterHome;
import fr.paris.lutece.plugins.directory.business.parameter.EntryParameterHome;
import fr.paris.lutece.plugins.directory.service.directorysearch.DirectorySearchService;
import fr.paris.lutece.plugins.directory.service.upload.DirectoryAsynchronousUploadHandler;
import fr.paris.lutece.plugins.directory.utils.DirectoryErrorException;
import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.portal.business.rbac.RBAC;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.business.workflow.Action;
import fr.paris.lutece.portal.business.workflow.State;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupService;
import fr.paris.lutece.util.ReferenceList;


/**
 *
 * FormService
 *
 */
public class DirectoryService
{
    // MARKS
    private static final String MARK_LIST_DIRECTORY_PARAM_DEFAULT_VALUES = "list_directory_param_default_values";
    private static final String MARK_LIST_ENTRY_PARAM_DEFAULT_VALUES = "list_entry_param_default_values";
    private static final String MARK_PERMISSION_INDEX_ALL_DIRECTORY = "permission_index_all_directory";
    private static final String MARK_PERMISSION_XSL = "right_xsl";
    private static final String MARK_RECORD = "record";
    private static final String MARK_MAP_ID_ENTRY_LIST_RECORD_FIELD = "map_id_entry_list_record_field";
    private static final String MARK_WORKFLOW_STATE = "workflow_state";
    private static final String MARK_WORKFLOW_ACTION_LIST = "workflow_action_list";
    private static DirectoryService _singleton;

    /**
    * Initialize the Form service
    *
    */
    public void init(  )
    {
        Directory.init(  );
    }

    /**
     * Returns the instance of the singleton
     *
     * @return The instance of the singleton
     */
    public static DirectoryService getInstance(  )
    {
        if ( _singleton == null )
        {
            _singleton = new DirectoryService(  );
        }

        return _singleton;
    }

    /**
     * Build the advanced parameters management
     * @param user the current user
     * @return The model for the advanced parameters
     */
    public Map<String, Object> getManageAdvancedParameters( AdminUser user )
    {
        Map<String, Object> model = new HashMap<String, Object>(  );
        Plugin plugin = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );

        if ( RBACService.isAuthorized( Directory.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID,
                    DirectoryResourceIdService.PERMISSION_MANAGE_ADVANCED_PARAMETERS, user ) )
        {
            ReferenceList listDirectoryParamDefaultValues = DirectoryParameterHome.findAll( plugin );
            ReferenceList listEntryParamDefaultValues = EntryParameterHome.findAll( plugin );

            model.put( MARK_LIST_DIRECTORY_PARAM_DEFAULT_VALUES, listDirectoryParamDefaultValues );
            model.put( MARK_LIST_ENTRY_PARAM_DEFAULT_VALUES, listEntryParamDefaultValues );
        }

        if ( RBACService.isAuthorized( DirectoryXsl.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID,
                    DirectoryXslResourceIdService.PERMISSION_CREATE, user ) )
        {
            model.put( MARK_PERMISSION_XSL, true );
        }

        if ( RBACService.isAuthorized( Directory.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID,
                    DirectoryResourceIdService.PERMISSION_INDEX_ALL_DIRECTORY, user ) )
        {
            model.put( MARK_PERMISSION_INDEX_ALL_DIRECTORY, true );
        }

        return model;
    }
    
    /**
     * Get the records count
     * @param directory the {@link Directory}
     * @param user the {@link AdminUser}
     * @return the record count
     */
    public int getRecordsCount( Directory directory, AdminUser user )
    {
    	Plugin plugin = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
    	int nNbRecords = 0;
    	boolean bWorkflowServiceEnable = WorkflowService.getInstance(  ).isAvailable(  );
    	RecordFieldFilter filter = new RecordFieldFilter(  );
    	filter.setIdDirectory( directory.getIdDirectory(  ) );
        filter.setWorkgroupKeyList( AdminWorkgroupService.getUserWorkgroups( user, user.getLocale(  ) ) );
    	
    	if ( ( directory.getIdWorkflow(  ) != DirectoryUtils.CONSTANT_ID_NULL ) &&
                bWorkflowServiceEnable )
        {
    		List<Integer> listResultRecordIds = DirectorySearchService.getInstance(  ).
    							getSearchResults( directory, null, null, null, null, filter, plugin );
    		List<Integer> listTmpResultRecordIds = WorkflowService.getInstance(  )
			            		.getAuthorizedResourceList( Record.WORKFLOW_RESOURCE_TYPE, directory.getIdWorkflow(  ), 
			            		DirectoryUtils.CONSTANT_ID_NULL, Integer.valueOf( directory.getIdDirectory(  ) ), user );
			listResultRecordIds = DirectoryUtils.retainAllIdsKeepingFirstOrder( listResultRecordIds, listTmpResultRecordIds );
			nNbRecords = listResultRecordIds.size(  );
        }
    	else
    	{
        	nNbRecords = RecordHome.getCountRecord( filter, plugin );
    	}
    	
    	return nNbRecords;
    }
    
    /**
     * Get the user from the id directory record
     * @param nIdDirectoryRecord the id directory record
     * @return a {@link LuteceUser}, null if the directory record is not a MyLutece entry
     */
    public LuteceUser getUserFromIdDirectoryRecord( int nIdDirectoryRecord )
    {
    	LuteceUser user = null;
        RecordFieldFilter recordFieldFilter = new RecordFieldFilter(  );
        recordFieldFilter.setIdRecord( nIdDirectoryRecord );
        Plugin plugin = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );

        List<RecordField> listRecordFields = RecordFieldHome.getRecordFieldList( recordFieldFilter, plugin );
        String strUserLogin = DirectoryUtils.EMPTY_STRING;

        for ( RecordField recordField : listRecordFields )
        {
            EntryType entryType = EntryTypeHome.findByPrimaryKey( recordField.getEntry(  ).getEntryType(  ).getIdType(  ),
            		plugin );

            if ( entryType.getMyLuteceUser(  ) )
            {
                strUserLogin = recordField.getValue(  );
                user = SecurityService.getInstance(  ).getUser( strUserLogin );

                break;
            }
        }
    	return user;
    }
    
    /**
     * Get the user info
     * @param user {@link LuteceUser}
     * @return a list of info of the user
     */
    public ReferenceList getUserInfo( LuteceUser user )
    {
    	ReferenceList listUserInfos = new ReferenceList(  );

        if ( user != null )
        {
            for ( Map.Entry<String, String> userInfo : user.getUserInfos(  ).entrySet(  ) )
            {
                listUserInfos.addItem( userInfo.getKey(  ), userInfo.getValue(  ) );
            }
        }
        return listUserInfos;
    }
    
    /**
     * Get the max number
     * @param nIdEntryTypeNumbering the id entry type numbering
     * @param nIdDirectory the id directory
     * @return the max number
     */
    public int getMaxNumber( IEntry entry )
    {
    	int nMaxNumber = 1;
    	if ( entry instanceof fr.paris.lutece.plugins.directory.business.EntryTypeNumbering && 
    			entry != null && entry.getEntryType(  ) != null && entry.getDirectory(  ) != null )
    	{
    		Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
    		nMaxNumber = RecordFieldHome.findMaxNumber( entry.getEntryType(  ).getIdType(  ), 
    				entry.getDirectory(  ).getIdDirectory(  ), pluginDirectory );
    	}
    	return nMaxNumber;
    }

    /**
     * Get the number to insert to the entry type numbering. 
     * @param entry the entry type numbering
     * @param strNumber the number to insert
     * @return the number
     * @throws DirectoryErrorException exception if the number already exists on an another record field
     */
    public int getNumber( IEntry entry, String strNumber ) throws DirectoryErrorException
    {
    	int nNumber = DirectoryUtils.CONSTANT_ID_NULL;
    	if ( entry instanceof fr.paris.lutece.plugins.directory.business.EntryTypeNumbering &&
    			entry != null && entry.getFields(  ) != null && entry.getFields(  ).size(  ) > 0 && 
    			entry.getEntryType(  ) != null && entry.getDirectory(  ) != null )
    	{
			nNumber = buildNumber( entry, strNumber );
			if ( nNumber != DirectoryUtils.CONSTANT_ID_NULL )
			{
				Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
	    		if ( RecordFieldHome.isNumberOnARecordField( entry.getEntryType(  ).getIdType(  ), 
	    				entry.getDirectory(  ).getIdDirectory(  ), nNumber, pluginDirectory ) )
	    		{
	    			throw new DirectoryErrorException( entry.getTitle(  ), "Directory Error - The number already exists in an " +
	    					"another record field." );
	    		}
			}
    	}
    	
    	return nNumber;
    }

    /**
     * Get the list of fields from a given id entry
     * @param nIdEntry the id entry
     * @return a list of fields
     */
    public List<Field> getFieldsListFromIdEntry( int nIdEntry )
    {
    	Plugin plugin = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
		return FieldHome.getFieldListByIdEntry( nIdEntry, plugin );
    }

    /**
     * Get the model for entry for xml
     * @param entry the entry
     * @return the model
     */
    public Map<String, String> getModelForEntryForXml( IEntry entry )
    {
    	Map<String, String> model = new HashMap<String, String>(  );
        model.put( Entry.ATTRIBUTE_ENTRY_ID, String.valueOf( entry.getIdEntry(  ) ) );
        model.put( Entry.ATTRIBUTE_TITLE, entry.getTitle(  ) );
        if ( entry instanceof EntryTypeGeolocation )
        {
        	model.put( Entry.ATTRIBUTE_SHOWXY, Boolean.toString( showXY( entry ) ) );
        }
        return model;
    }
    
    /**
     * Check if the entry must show the X and Y or not
     * @return true if it must show, false otherwise
     */
    public boolean showXY( IEntry entry )
    {
    	boolean bShowXY = false;
    	if ( entry instanceof EntryTypeGeolocation )
        {
        	if ( entry.getFields(  ) == null || entry.getFields().size(  ) == 0 )
        	{
        		entry.setFields( getFieldsListFromIdEntry( entry.getIdEntry(  ) ) );
        	}
        	for ( Field field : entry.getFields(  ) )
        	{
        		if ( EntryTypeGeolocation.CONSTANT_SHOWXY.equals( field.getTitle(  ) ) )
        		{
        			bShowXY = Boolean.valueOf( field.getValue(  ) );
        			break;
        		}
        	}
        }
    	return bShowXY;
    }
    
    /**
     * Remove asynchronous file
     * @param recordField the record field
     * @param plugin the plugin
     */
    public void removeAsynchronousFile( RecordField recordField, Plugin plugin )
    {
    	if ( recordField != null && recordField.getEntry(  ) != null )
    	{
    		IEntry entry = recordField.getEntry(  );
    		String strWSRestUrl = getWSRestUrl( entry, plugin );
    		if ( StringUtils.isNotBlank( strWSRestUrl ) )
    		{
    			try
				{
					DirectoryAsynchronousUploadHandler.getHandler(  ).doRemoveFile( 
							recordField, entry, strWSRestUrl );
				}
				catch ( Exception e )
				{
					AppLogService.error( e );
				}
    		}
    	}
    }
    
    /**
     * Get the WS rest url from a given entry
     * @param entry the entry
     * @param plugin the plugin
     * @return the ws rest url
     */
    public String getWSRestUrl( IEntry entry, Plugin plugin )
    {
    	String strWSRestUrl = StringUtils.EMPTY;
    	if ( entry != null && entry instanceof EntryTypeDownloadUrl )
    	{
    		if ( entry.getFields(  ) == null )
    		{
    			entry.setFields( FieldHome.getFieldListByIdEntry( entry.getIdEntry(  ), plugin ) );
    		}
    		
    		if ( entry.getFields(  ) != null && !entry.getFields(  ).isEmpty(  ) )
    		{
    			for ( Field field : entry.getFields(  ) )
    			{
    				if ( EntryTypeDownloadUrl.CONSTANT_WS_REST_URL.equals( field.getTitle(  ) ) )
    				{
    					strWSRestUrl = field.getValue(  );
    				}
    			}
    		}
    	}
		
		return strWSRestUrl;
    }

    /**
     * Get the resource action for a record
     * @param record the record
     * @param directory the directory
     * @param listEntryResultSearch the list of entry
     * @param locale the locale
     * @param adminUser the AdminUser
     * @param plugin the plugin
     * @return a map of string - object
     */
    public Map<String, Object> getResourceAction( Record record, Directory directory, List<IEntry> listEntryResultSearch,
    		Locale locale, AdminUser adminUser, List<DirectoryAction> listActionsForDirectoryEnable, 
    		List<DirectoryAction> listActionsForDirectoryDisable, boolean bGetFileName, Plugin plugin )
    {        
        if ( record.isEnabled(  ) )
        {
            record.setActions( listActionsForDirectoryEnable );
        }
        else
        {
            record.setActions( listActionsForDirectoryDisable );
        }
        
        //workflow service
        Map<String, Object> resourceActions = new HashMap<String, Object>(  );
        resourceActions.put( MARK_RECORD, record );
        resourceActions.put( MARK_MAP_ID_ENTRY_LIST_RECORD_FIELD,
            DirectoryUtils.getMapIdEntryListRecordField( listEntryResultSearch, record.getIdRecord(  ),
                plugin, bGetFileName ) );
        
        boolean bWorkflowServiceEnable = WorkflowService.getInstance(  ).isAvailable(  ) && 
        	( directory.getIdWorkflow(  ) != DirectoryUtils.CONSTANT_ID_NULL );

        if ( bWorkflowServiceEnable )
        {
        	WorkflowService workflowService = WorkflowService.getInstance(  );
        	Collection<Action> lListActions = workflowService.getActions( record.getIdRecord(  ),
                    Record.WORKFLOW_RESOURCE_TYPE, directory.getIdWorkflow(  ), adminUser );
            State state = workflowService.getState( record.getIdRecord(  ), Record.WORKFLOW_RESOURCE_TYPE,
                    directory.getIdWorkflow(  ), Integer.valueOf( directory.getIdDirectory(  ) ), adminUser );
            resourceActions.put( MARK_WORKFLOW_STATE, state );
            resourceActions.put( MARK_WORKFLOW_ACTION_LIST, lListActions );
        }
        
        return resourceActions;
    }

    /**
     * Build the number from a given number. This methods first checks if the number is not a type
     * numerical (without the prefix of the entry), or checks if the number already exists on 
     * an another record field or not.
     * @param entry the entry numbering
     * @param strNumber the number to build
     * @return the number
     * @throws DirectoryErrorException exception if the directory entry type numbering does not have the same prefix as the number
     */
    private int buildNumber( IEntry entry, String strNumber ) throws DirectoryErrorException
    {
    	int nNumber = DirectoryUtils.CONSTANT_ID_NULL;
    	
    	Field field = entry.getFields(  ).get( 0 );
		String strPrefix = field.getTitle(  );
		
		String strNumberTmp = strNumber;
		
		if ( StringUtils.isNotBlank( strPrefix )  )
		{
			if ( StringUtils.isNotBlank( strNumber ) && strPrefix.length(  ) < strNumber.length(  ) )
			{
				strNumberTmp = strNumber.substring( strPrefix.length(  ), strNumber.length(  ) );
			}
			else
			{
				throw new DirectoryErrorException( entry.getTitle(  ), "Directory Error - The prefix of the entry type numbering to " +
						"insert is not correct." );
			}
		}
		
		if ( StringUtils.isNotBlank( strNumberTmp ) && StringUtils.isNumeric( strNumberTmp ) )
		{
			nNumber = Integer.parseInt( strNumberTmp );
		}
		else
		{
			throw new DirectoryErrorException( entry.getTitle(  ), "Directory Error - The prefix of the entry type numbering to " +
				"insert is not correct." );
		}
		
		return nNumber;
    }
}

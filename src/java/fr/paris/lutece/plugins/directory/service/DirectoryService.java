/*
 * Copyright (c) 2002-2009, Mairie de Paris
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.paris.lutece.plugins.directory.business.Directory;
import fr.paris.lutece.plugins.directory.business.DirectoryXsl;
import fr.paris.lutece.plugins.directory.business.Record;
import fr.paris.lutece.plugins.directory.business.RecordFieldFilter;
import fr.paris.lutece.plugins.directory.business.RecordHome;
import fr.paris.lutece.plugins.directory.business.parameter.DirectoryParameterHome;
import fr.paris.lutece.plugins.directory.business.parameter.EntryParameterHome;
import fr.paris.lutece.plugins.directory.service.directorysearch.DirectorySearchService;
import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.portal.business.rbac.RBAC;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.rbac.RBACService;
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
}

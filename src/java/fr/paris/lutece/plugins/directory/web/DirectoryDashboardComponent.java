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
package fr.paris.lutece.plugins.directory.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.directory.business.Directory;
import fr.paris.lutece.plugins.directory.business.DirectoryAction;
import fr.paris.lutece.plugins.directory.business.DirectoryActionHome;
import fr.paris.lutece.plugins.directory.business.DirectoryFilter;
import fr.paris.lutece.plugins.directory.business.DirectoryHome;
import fr.paris.lutece.plugins.directory.service.DirectoryPlugin;
import fr.paris.lutece.plugins.directory.service.DirectoryResourceIdService;
import fr.paris.lutece.plugins.directory.service.DirectoryService;
import fr.paris.lutece.portal.business.rbac.RBAC;
import fr.paris.lutece.portal.business.right.Right;
import fr.paris.lutece.portal.business.right.RightHome;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.dashboard.DashboardComponent;
import fr.paris.lutece.portal.service.database.AppConnectionService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupService;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.url.UrlItem;


/**
 * Calendar Dashboard Component
 * This component displays directories
 */
public class DirectoryDashboardComponent extends DashboardComponent
{
	// MARKS
    public static final String MARK_URL = "url";
    public static final String MARK_ICON = "icon";
    public static final String MARK_DIRECTORY_LIST = "directory_list";
    public static final String MARK_RECORD_COUNT_LIST = "record_count_list";
    public static final String MARK_AUTHORIZED_DIRECTORY_MODIFICATION_LIST = "authorized_directory_modification_list";
    public static final String MARK_PERMISSION_CREATE = "permission_create";
    
    // CONSTANTS
    private static final int ZONE_1 = 1;
    private static final String EMPTY_STRING = "";
    
    // TEMPALTES
    private static final String TEMPLATE_DASHBOARD_ZONE_1 = "/admin/plugins/directory/directory_dashboard_zone_1.html";
    private static final String TEMPLATE_DASHBOARD_OTHER_ZONE = "/admin/plugins/directory/directory_dashboard_other_zone.html";

    /**
     * The HTML code of the component
     * @param user The Admin User
	 * @param request HttpServletRequest
     * @return The dashboard component
     */
    public String getDashboardData( AdminUser user, HttpServletRequest request )
    {
        Right right = RightHome.findByPrimaryKey( getRight(  ) );
        Plugin plugin = PluginService.getPlugin( right.getPluginName(  ) );
        List<DirectoryAction> listActions;
        List<DirectoryAction> listActionsForDirectoryEnable;
        List<DirectoryAction> listActionsForDirectoryDisable;

        if ( !( ( plugin.getDbPoolName(  ) != null ) &&
                !AppConnectionService.NO_POOL_DEFINED.equals( plugin.getDbPoolName(  ) ) ) )
        {
            return EMPTY_STRING;
        }

        UrlItem url = new UrlItem( right.getUrl(  ) );
        url.addParameter( DirectoryPlugin.PLUGIN_NAME, right.getPluginName(  ) );

        Map<String, Object> model = new HashMap<String, Object>(  );
        List<Directory> directoryList = getDirectoryList( user, plugin );

        listActionsForDirectoryEnable = DirectoryActionHome.selectActionsByFormState( Directory.STATE_ENABLE,
                getPlugin(  ), user.getLocale(  ) );
        listActionsForDirectoryDisable = DirectoryActionHome.selectActionsByFormState( Directory.STATE_DISABLE,
                getPlugin(  ), user.getLocale(  ) );

        Map<String, Object> recordCountMap = new HashMap<String, Object>(  );
        List<Integer> nAuthorizedModificationList = new ArrayList<Integer>(  );

        for ( Directory directory : directoryList )
        {
            if ( directory.isEnabled(  ) )
            {
                listActions = listActionsForDirectoryEnable;
            }
            else
            {
                listActions = listActionsForDirectoryDisable;
            }

            listActions = (List<DirectoryAction>) RBACService.getAuthorizedActionsCollection( listActions, directory,
                    user );
            directory.setActions( listActions );

            if ( RBACService.isAuthorized( directory, DirectoryResourceIdService.PERMISSION_MODIFY, user ) )
            {
                nAuthorizedModificationList.add( directory.getIdDirectory(  ) );
            }

            //count records
            recordCountMap.put( Integer.toString( directory.getIdDirectory(  ) ),
                DirectoryService.getInstance(  ).getRecordsCount( directory, user ) );
        }

        model.put( MARK_DIRECTORY_LIST, directoryList );
        model.put( MARK_RECORD_COUNT_LIST, recordCountMap );
        model.put( MARK_AUTHORIZED_DIRECTORY_MODIFICATION_LIST, nAuthorizedModificationList );

        model.put( MARK_URL, url.getUrl(  ) );
        model.put( MARK_ICON, plugin.getIconUrl(  ) );
        model.put( MARK_PERMISSION_CREATE, RBACService.isAuthorized( Directory.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID,
                DirectoryResourceIdService.PERMISSION_CREATE, user ) );

        HtmlTemplate t = AppTemplateService.getTemplate( getTemplateDashboard(  ), user.getLocale(  ), model );

        return t.getHtml(  );
    }

    /**
     * Get the list of directories
     * @param user the current user
     * @param plugin Plugin
     * @return the list of calendars
     */
    private List<Directory> getDirectoryList( AdminUser user, Plugin plugin )
    {
        DirectoryFilter filter = new DirectoryFilter(  );
        List<Directory> directoryList = DirectoryHome.getDirectoryList( filter, getPlugin(  ) );

        return (List) AdminWorkgroupService.getAuthorizedCollection( directoryList, user );
    }

    /**
     * Get the template
     * @return the template
     */
    private String getTemplateDashboard(  )
    {
        if ( getZone(  ) == ZONE_1 )
        {
            return TEMPLATE_DASHBOARD_ZONE_1;
        }

        return TEMPLATE_DASHBOARD_OTHER_ZONE;
    }
}

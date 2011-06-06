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

import fr.paris.lutece.plugins.directory.business.Directory;
import fr.paris.lutece.plugins.directory.business.DirectoryXsl;
import fr.paris.lutece.plugins.directory.business.DirectoryXslFilter;
import fr.paris.lutece.plugins.directory.business.DirectoryXslHome;
import fr.paris.lutece.plugins.directory.service.DirectoryResourceIdService;
import fr.paris.lutece.plugins.directory.service.DirectoryXslResourceIdService;
import fr.paris.lutece.portal.business.rbac.RBAC;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.web.admin.PluginAdminPageJspBean;
import fr.paris.lutece.util.html.HtmlTemplate;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;


/**
 * class ManageDirectoryJspBean
 */
public class ManageDirectoryJspBean extends PluginAdminPageJspBean
{
    public static final String RIGHT_MANAGE_DIRECTORY = "DIRECTORY_MANAGEMENT";

    //templates
    private static final String TEMPLATE_MANAGE_PLUGIN_DIRECTORY = "admin/plugins/directory/manage_plugin_directory.html";

    // other constants
    private static final String EMPTY_STRING = "";

    //MARK 
    private static final String MARK_PERMISSION_INDEX_ALL_DIRECTORY = "permission_index_all_directory";
    private static final String MARK_PERMISSION_XSL = "right_xsl";

    /*-------------------------------MANAGEMENT  FORM-----------------------------*/

    /**
     * Return management page of plugin directory
     * @param request The Http request
     * @return Html management page of plugin directory
     */
    public String getManageDirectory( HttpServletRequest request )
    {
        setPageTitleProperty( EMPTY_STRING );

        HashMap model = new HashMap(  );

        model.put( MARK_PERMISSION_INDEX_ALL_DIRECTORY,
            RBACService.isAuthorized( Directory.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID,
                DirectoryResourceIdService.PERMISSION_INDEX_ALL_DIRECTORY, getUser(  ) ) );

        List<DirectoryXsl> listDirectoryXsl = DirectoryXslHome.getList( new DirectoryXslFilter(  ), getPlugin(  ) );
        boolean right = false;

        for ( DirectoryXsl directoryXsl : listDirectoryXsl )
        {
            if ( RBACService.isAuthorized( DirectoryXsl.RESOURCE_TYPE,
                        Integer.toString( directoryXsl.getIdDirectoryXsl(  ) ),
                        DirectoryXslResourceIdService.PERMISSION_DELETE, getUser(  ) ) ||
                    RBACService.isAuthorized( DirectoryXsl.RESOURCE_TYPE,
                        Integer.toString( directoryXsl.getIdDirectoryXsl(  ) ),
                        DirectoryXslResourceIdService.PERMISSION_MODIFY, getUser(  ) ) )
            {
                right = true;
            }
        }

        if ( RBACService.isAuthorized( DirectoryXsl.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID,
                    DirectoryXslResourceIdService.PERMISSION_CREATE, getUser(  ) ) )
        {
            right = true;
        }

        model.put( MARK_PERMISSION_XSL, right );

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_MANAGE_PLUGIN_DIRECTORY, getLocale(  ),
                model );

        return getAdminPage( templateList.getHtml(  ) );
    }
}

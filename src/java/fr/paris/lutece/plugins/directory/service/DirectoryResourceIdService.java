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
package fr.paris.lutece.plugins.directory.service;

import fr.paris.lutece.plugins.directory.business.Directory;
import fr.paris.lutece.plugins.directory.business.DirectoryHome;
import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.rbac.Permission;
import fr.paris.lutece.portal.service.rbac.ResourceIdService;
import fr.paris.lutece.portal.service.rbac.ResourceType;
import fr.paris.lutece.portal.service.rbac.ResourceTypeManager;
import fr.paris.lutece.util.ReferenceList;

import java.util.Locale;


/**
 *
 * class DirectoryResourceIdService
 *
 */
public class DirectoryResourceIdService extends ResourceIdService
{
    /** Permission for creating a directory */
    public static final String PERMISSION_CREATE = "CREATE";

    /** Permission for creating a directory */
    public static final String PERMISSION_CREATE_RECORD = "CREATE_RECORD";

    /** Permission for managing record */
    public static final String PERMISSION_MANAGE_RECORD = "MANAGE_RECORD";

    /** Permission for import record */
    public static final String PERMISSION_IMPORT_RECORD = "IMPORT_RECORD";

    /** Permission for deleting all directory record */
    public static final String PERMISSION_DELETE_ALL_RECORD = "DELETE_ALL_RECORD";

    /** Permission for deleting a directory */
    public static final String PERMISSION_DELETE = "DELETE";

    /** Permission for deleting a directory */
    public static final String PERMISSION_DELETE_RECORD = "DELETE_RECORD";

    /** Permission for modifying a directory */
    public static final String PERMISSION_MODIFY = "MODIFY";

    /** Permission for modifying a directory */
    public static final String PERMISSION_MODIFY_RECORD = "MODIFY_RECORD";

    /** Permission for copying a directory*/
    public static final String PERMISSION_COPY = "COPY";

    /** Permission for copying a directory*/
    public static final String PERMISSION_COPY_RECORD = "COPY_RECORD";

    /** Permission for indexing all directory */
    public static final String PERMISSION_INDEX_ALL_DIRECTORY = "INDEX_ALL_DIRECTORY";

    /** Permission for enable or disable a directory */
    public static final String PERMISSION_CHANGE_STATE = "CHANGE_STATE";

    /** Permission for enable or disable a directory */
    public static final String PERMISSION_CHANGE_STATE_RECORD = "CHANGE_STATE_RECORD";

    /** Permission for enable history */
    public static final String PERMISSION_HISTORY_RECORD = "HISTORY_RECORD";

    /** Permission for mass print */
    public static final String PERMISSION_MASS_PRINT = "MASS_PRINT";

    /** Permission for managing advanced parameters */
    public static final String PERMISSION_MANAGE_ADVANCED_PARAMETERS = "MANAGE_ADVANCED_PARAMETERS";

    /** Permission for record visualisation */
    public static final String PERMISSION_VISUALISATION_RECORD = "VISUALISATION_RECORD";

    /** Permission for import record */
    public static final String PERMISSION_IMPORT_FIELD = "IMPORT_FIELD";

    /** Permission for mylutece user visualisation */
    public static final String PERMISSION_VISUALISATION_MYLUTECE_USER = "VISUALISATION_MYLUTECE_USER";
    public static final String PROPERTY_LABEL_RESOURCE_TYPE = "directory.permission.label.resource_type_directory";
    private static final String PROPERTY_LABEL_CREATE = "directory.permission.label.create_directory";
    private static final String PROPERTY_LABEL_MANAGE_RECORD = "directory.permission.label.manage_directory_record";
    private static final String PROPERTY_LABEL_DELETE_ALL_RECORD = "directory.permission.label.delete_all_directory_record";
    private static final String PROPERTY_LABEL_DELETE = "directory.permission.label.delete_directory";
    private static final String PROPERTY_LABEL_MODIFY = "directory.permission.label.modify_directory";
    private static final String PROPERTY_LABEL_COPY = "directory.permission.label.copy_directory";
    private static final String PROPERTY_LABEL_CHANGE_STATE = "directory.permission.label.change_state_directory";
    private static final String PROPERTY_LABEL_INDEX_ALL_DIRECTORY = "directory.permission.label.index_all_directory";
    private static final String PROPERTY_LABEL_CREATE_RECORD = "directory.permission.label.create_directory_record";
    private static final String PROPERTY_LABEL_DELETE_RECORD = "directory.permission.label.delete_directory_record";
    private static final String PROPERTY_LABEL_MODIFY_RECORD = "directory.permission.label.modify_directory_record";
    private static final String PROPERTY_LABEL_COPY_RECORD = "directory.permission.label.copy_directory_record";
    private static final String PROPERTY_LABEL_CHANGE_STATE_RECORD = "directory.permission.label.change_state_directory_record";
    private static final String PROPERTY_LABEL_HISTORY_RECORD = "directory.permission.label.change_history_record";
    private static final String PROPERTY_LABEL_VISUALISATION_RECORD = "directory.permission.label.visualisation_record";
    private static final String PROPERTY_LABEL_IMPORT_RECORD = "directory.permission.label.import_record";
    private static final String PROPERTY_LABEL_MASS_PRINT = "directory.permission.label.mass_print";
    private static final String PROPERTY_LABEL_MANAGE_ADVANCED_PARAMETERS = "directory.permission.label.manage_advanced_parameters";
    private static final String PROPERTY_LABEL_VISUALISATION_MYLUTECE_USER = "directory.permission.label.visualisation_mylutece_user";
    private static final String PROPERTY_LABEL_IMPORT_FIELD = "directory.permission.label.import_field";

    /** Creates a new instance of DocumentTypeResourceIdService */
    public DirectoryResourceIdService(  )
    {
        setPluginName( DirectoryPlugin.PLUGIN_NAME );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void register(  )
    {
        // Override the resource type DIRECTORY_DIRECTORY_TYPE
        ResourceType rt = ResourceTypeManager.getResourceType( Directory.RESOURCE_TYPE );

        if ( rt == null )
        {
            rt = new ResourceType(  );
            rt.setResourceIdServiceClass( DirectoryResourceIdService.class.getName(  ) );
            rt.setPluginName( DirectoryPlugin.PLUGIN_NAME );
            rt.setResourceTypeKey( Directory.RESOURCE_TYPE );
            rt.setResourceTypeLabelKey( PROPERTY_LABEL_RESOURCE_TYPE );
        }

        Permission p = new Permission(  );
        p.setPermissionKey( PERMISSION_CREATE );
        p.setPermissionTitleKey( PROPERTY_LABEL_CREATE );
        rt.registerPermission( p );

        p = new Permission(  );
        p.setPermissionKey( PERMISSION_MODIFY );
        p.setPermissionTitleKey( PROPERTY_LABEL_MODIFY );
        rt.registerPermission( p );

        p = new Permission(  );
        p.setPermissionKey( PERMISSION_COPY );
        p.setPermissionTitleKey( PROPERTY_LABEL_COPY );
        rt.registerPermission( p );

        p = new Permission(  );
        p.setPermissionKey( PERMISSION_MANAGE_RECORD );
        p.setPermissionTitleKey( PROPERTY_LABEL_MANAGE_RECORD );
        rt.registerPermission( p );

        p = new Permission(  );
        p.setPermissionKey( PERMISSION_DELETE_ALL_RECORD );
        p.setPermissionTitleKey( PROPERTY_LABEL_DELETE_ALL_RECORD );
        rt.registerPermission( p );

        p = new Permission(  );
        p.setPermissionKey( PERMISSION_CHANGE_STATE );
        p.setPermissionTitleKey( PROPERTY_LABEL_CHANGE_STATE );
        rt.registerPermission( p );

        p = new Permission(  );
        p.setPermissionKey( PERMISSION_DELETE );
        p.setPermissionTitleKey( PROPERTY_LABEL_DELETE );
        rt.registerPermission( p );

        p = new Permission(  );
        p.setPermissionKey( PERMISSION_INDEX_ALL_DIRECTORY );
        p.setPermissionTitleKey( PROPERTY_LABEL_INDEX_ALL_DIRECTORY );
        rt.registerPermission( p );

        p = new Permission(  );
        p.setPermissionKey( PERMISSION_CREATE_RECORD );
        p.setPermissionTitleKey( PROPERTY_LABEL_CREATE_RECORD );
        rt.registerPermission( p );

        p = new Permission(  );
        p.setPermissionKey( PERMISSION_DELETE_RECORD );
        p.setPermissionTitleKey( PROPERTY_LABEL_DELETE_RECORD );
        rt.registerPermission( p );

        p = new Permission(  );
        p.setPermissionKey( PERMISSION_MODIFY_RECORD );
        p.setPermissionTitleKey( PROPERTY_LABEL_MODIFY_RECORD );
        rt.registerPermission( p );

        p = new Permission(  );
        p.setPermissionKey( PERMISSION_COPY_RECORD );
        p.setPermissionTitleKey( PROPERTY_LABEL_COPY_RECORD );
        rt.registerPermission( p );

        p = new Permission(  );
        p.setPermissionKey( PERMISSION_CHANGE_STATE_RECORD );
        p.setPermissionTitleKey( PROPERTY_LABEL_CHANGE_STATE_RECORD );
        rt.registerPermission( p );

        p = new Permission(  );
        p.setPermissionKey( PERMISSION_HISTORY_RECORD );
        p.setPermissionTitleKey( PROPERTY_LABEL_HISTORY_RECORD );
        rt.registerPermission( p );

        p = new Permission(  );
        p.setPermissionKey( PERMISSION_VISUALISATION_RECORD );
        p.setPermissionTitleKey( PROPERTY_LABEL_VISUALISATION_RECORD );
        rt.registerPermission( p );

        p = new Permission(  );
        p.setPermissionKey( PERMISSION_IMPORT_RECORD );
        p.setPermissionTitleKey( PROPERTY_LABEL_IMPORT_RECORD );
        rt.registerPermission( p );

        p = new Permission(  );
        p.setPermissionKey( PERMISSION_MASS_PRINT );
        p.setPermissionTitleKey( PROPERTY_LABEL_MASS_PRINT );
        rt.registerPermission( p );

        p = new Permission(  );
        p.setPermissionKey( PERMISSION_MANAGE_ADVANCED_PARAMETERS );
        p.setPermissionTitleKey( PROPERTY_LABEL_MANAGE_ADVANCED_PARAMETERS );
        rt.registerPermission( p );

        p = new Permission(  );
        p.setPermissionKey( PERMISSION_VISUALISATION_MYLUTECE_USER );
        p.setPermissionTitleKey( PROPERTY_LABEL_VISUALISATION_MYLUTECE_USER );
        rt.registerPermission( p );

        p = new Permission(  );
        p.setPermissionKey( PERMISSION_IMPORT_FIELD );
        p.setPermissionTitleKey( PROPERTY_LABEL_IMPORT_FIELD );
        rt.registerPermission( p );

        ResourceTypeManager.registerResourceType( rt );
    }

    /**
     * Returns a list of directory resource ids
     * @param locale The current locale
     * @return A list of resource ids
     */
    @Override
    public ReferenceList getResourceIdList( Locale locale )
    {
        return DirectoryHome.getDirectoryList( PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitle( String strId, Locale locale )
    {
        int nIdDirectory = DirectoryUtils.convertStringToInt( strId );
        Directory directory = DirectoryHome.findByPrimaryKey( nIdDirectory,
                PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME ) );

        return ( directory != null ) ? directory.getTitle(  ) : null;
    }
}

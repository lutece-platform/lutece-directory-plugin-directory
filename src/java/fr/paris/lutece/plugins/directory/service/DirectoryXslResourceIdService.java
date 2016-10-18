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

import fr.paris.lutece.plugins.directory.business.DirectoryXsl;
import fr.paris.lutece.plugins.directory.business.DirectoryXslFilter;
import fr.paris.lutece.plugins.directory.business.DirectoryXslHome;
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
 * class ExportFormatResourceIdService
 *
 */
public class DirectoryXslResourceIdService extends ResourceIdService
{
    /** Permission for creating a directory */
    public static final String PERMISSION_CREATE = "CREATE";

    /** Permission for deleting a directory */
    public static final String PERMISSION_DELETE = "DELETE";

    /** Permission for modifying a directory */
    public static final String PERMISSION_MODIFY = "MODIFY";
    private static final String PROPERTY_LABEL_RESOURCE_TYPE = "directory.permission.label.resource_type_directory_xsl";
    private static final String PROPERTY_LABEL_CREATE = "directory.permission.label.create_directory_xsl";
    private static final String PROPERTY_LABEL_DELETE = "directory.permission.label.delete_directory_xsl";
    private static final String PROPERTY_LABEL_MODIFY = "directory.permission.label.modify_directory_xsl";

    /** Creates a new instance of DocumentTypeResourceIdService */
    public DirectoryXslResourceIdService( )
    {
        setPluginName( DirectoryPlugin.PLUGIN_NAME );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void register( )
    {
        ResourceType rt = new ResourceType( );
        rt.setResourceIdServiceClass( DirectoryXslResourceIdService.class.getName( ) );
        rt.setPluginName( DirectoryPlugin.PLUGIN_NAME );
        rt.setResourceTypeKey( DirectoryXsl.RESOURCE_TYPE );
        rt.setResourceTypeLabelKey( PROPERTY_LABEL_RESOURCE_TYPE );

        Permission p = new Permission( );
        p.setPermissionKey( PERMISSION_CREATE );
        p.setPermissionTitleKey( PROPERTY_LABEL_CREATE );
        rt.registerPermission( p );

        p = new Permission( );
        p.setPermissionKey( PERMISSION_MODIFY );
        p.setPermissionTitleKey( PROPERTY_LABEL_MODIFY );
        rt.registerPermission( p );

        p = new Permission( );
        p.setPermissionKey( PERMISSION_DELETE );
        p.setPermissionTitleKey( PROPERTY_LABEL_DELETE );
        rt.registerPermission( p );

        ResourceTypeManager.registerResourceType( rt );
    }

    /**
     * Returns a list of export format resource ids
     * 
     * @param locale
     *            The current locale
     * @return A list of resource ids
     */
    @Override
    public ReferenceList getResourceIdList( Locale locale )
    {
        return DirectoryXslHome.getRefList( new DirectoryXslFilter( ), PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitle( String strId, Locale locale )
    {
        int nIdExport = DirectoryUtils.convertStringToInt( strId );
        DirectoryXsl export = DirectoryXslHome.findByPrimaryKey( nIdExport, PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME ) );

        return ( export != null ) ? export.getTitle( ) : null;
    }
}

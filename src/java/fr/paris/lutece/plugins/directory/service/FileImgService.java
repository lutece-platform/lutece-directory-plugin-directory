/*
 * Copyright (c) 2002-2017, Mairie de Paris
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

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.directory.business.EntryTypeUrl;
import fr.paris.lutece.plugins.directory.business.File;
import fr.paris.lutece.plugins.directory.business.FileHome;
import fr.paris.lutece.plugins.directory.business.PhysicalFile;
import fr.paris.lutece.plugins.directory.business.PhysicalFileHome;
import fr.paris.lutece.plugins.directory.service.record.IRecordService;
import fr.paris.lutece.plugins.directory.service.record.RecordService;
import fr.paris.lutece.portal.service.image.ImageResource;
import fr.paris.lutece.portal.service.image.ImageResourceManager;
import fr.paris.lutece.portal.service.image.ImageResourceProvider;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.web.LocalVariables;
import fr.paris.lutece.portal.web.constants.Parameters;
import fr.paris.lutece.util.file.FileUtil;
import fr.paris.lutece.util.url.UrlItem;

/**
 * Service for Url entry types. Provide ImageResource managemenent
 *
 */
public class FileImgService implements ImageResourceProvider
{
    private static FileImgService _singleton = new FileImgService( );
    private static final String IMAGE_RESOURCE_TYPE_ID = "directory_entry_img";

    /**
     * Creates a new instance of FileImgService
     */
    FileImgService( )
    {
    }

    /**
     * Initializes the service
     */
    public void register( )
    {
        ImageResourceManager.registerProvider( this );
    }

    /**
     * Get the unique instance of the service
     *
     * @return The unique instance
     */
    public static FileImgService getInstance( )
    {
        return _singleton;
    }

    /**
     * Return the Resource id
     * 
     * @param nIdResource
     *            The resource identifier
     * @return The Resource Image
     */
    @Override
    public ImageResource getImageResource( int nIdResource )
    {
        //When using an older core version (before 5.1.5), the local variables will not
        //have been set by the image servlet. So we can get null or a request from another thread.
        //We could try to detect this by checking request.getServletPath( ) (or maybe other things?)
        //but it would break if we decide to expose this provider through another entrypoint.
        //Also, on tomcat (tested 8.5.5), it seems like the request object is reused just like
        //the thread, so that even if the local variables were set in another request,
        //the object we get here is the correct one (with the corect LuteceUser or AdminUser etc).
        //Also, Portal.jsp, the main entry point of the webapp, does clean up the local variables.
        //Note that the other request could even have run code from another webapp (not even a lutece webapp)
        //Also, we could log a warning here when request is null, but then it would prevent from using
        //this function from code not associated with a request. So no warnings.
        HttpServletRequest request = LocalVariables.getRequest();

        Plugin plugin = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
        File file = FileHome.findByPrimaryKey( nIdResource, plugin );
        if ( ( file != null ) && ( file.getPhysicalFile( ) != null ) && FileUtil.hasImageExtension( file.getTitle( ) ) ) {
            IRecordService recordService = SpringContextService.getBean( RecordService.BEAN_SERVICE );
            if ( request == null || recordService.isFileAuthorized( nIdResource, request, plugin ) ) {
                PhysicalFile physicalFile = PhysicalFileHome.findByPrimaryKey( file.getPhysicalFile( ).getIdPhysicalFile( ), plugin ) ;
                if ( physicalFile != null )
                {
                    ImageResource imageResource = new ImageResource( );
                    imageResource.setImage( physicalFile.getValue( ) );
                    imageResource.setMimeType( file.getMimeType( ) );

                    return imageResource;
                }
            }
        }

        return null;
    }

    /**
     * Return the Resource Type id
     * 
     * @return The Resource Type Id
     */
    @Override
    public String getResourceTypeId( )
    {
        return IMAGE_RESOURCE_TYPE_ID;
    }

    /**
     * Management of the image associated to the {@link EntryTypeUrl}
     * 
     * @param nEntryUrl
     *            The {@link EntryTypeUrl} identifier
     * @return The url of the resource without HTML escape characters
     */
    public static String getResourceImageEntryUrlWhitoutEntities( int nEntryUrl )
    {
        return getResourceImageEntryUrl( nEntryUrl, false );
    }

    /**
     * Management of the image associated to the {@link EntryUrl}
     * 
     * @param nEntryUrl
     *            The {@link EntryUrl} identifier
     * @param bWithEntities
     *            True to get the URL with HTML escape characters, false otherwise
     * @return The url of the resource
     */
    private static String getResourceImageEntryUrl( int nEntryUrl, boolean bWithEntities )
    {
        String strResourceType = FileImgService.getInstance( ).getResourceTypeId( );
        UrlItem url = new UrlItem( Parameters.IMAGE_SERVLET );
        url.addParameter( Parameters.RESOURCE_TYPE, strResourceType );
        url.addParameter( Parameters.RESOURCE_ID, Integer.toString( nEntryUrl ) );

        return bWithEntities ? url.getUrlWithEntity( ) : url.getUrl( );
    }
}

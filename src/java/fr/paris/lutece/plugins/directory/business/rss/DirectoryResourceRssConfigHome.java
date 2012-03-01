/*
 * Copyright (c) 2002-2012, Mairie de Paris
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
package fr.paris.lutece.plugins.directory.business.rss;

import fr.paris.lutece.plugins.directory.service.DirectoryPlugin;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.List;


/**
 * DirectoryResourceRssConfigHome
 */
public final class DirectoryResourceRssConfigHome
{
    // Static variable pointed at the DAO instance
    private static IDirectoryResourceRssConfigDAO _dao = (IDirectoryResourceRssConfigDAO) SpringContextService.getPluginBean( DirectoryPlugin.PLUGIN_NAME,
            "resourceRssDirectoryConfigDAO" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private DirectoryResourceRssConfigHome(  )
    {
    }

    /**
     * Insert new configuration
     *
     * @param config object configuration
     * @param plugin the plugin
     */
    public static void create( DirectoryResourceRssConfig config, Plugin plugin )
    {
        _dao.insert( config, plugin );
    }

    /**
     * Update a configuration
     *
     * @param config object configuration
     * @param plugin the plugin
     */
    public static void update( DirectoryResourceRssConfig config, Plugin plugin )
    {
        _dao.store( config, plugin );
    }

    /**
     * Delete a configuration
     * @param nIdDirectoryResourceRssConfig id DirectoryResourceRssConfig
     * @param plugin the plugin
     */
    public static void remove( int nIdDirectoryResourceRssConfig, Plugin plugin )
    {
        _dao.delete( nIdDirectoryResourceRssConfig, plugin );
    }

    /**
     * Delete a configuration
     * @param nIdDirectoryResourceRssConfig id task
     * @param plugin the plugin
     * @return a configuration
     *
     */
    public static DirectoryResourceRssConfig findByPrimaryKey( int nIdDirectoryResourceRssConfig, Plugin plugin )
    {
        DirectoryResourceRssConfig taskNotifyDirectoryConfig = _dao.load( nIdDirectoryResourceRssConfig, plugin );

        return taskNotifyDirectoryConfig;
    }

    /**
     * Load All DirectoryResourceRssConfig
     * @param plugin the plugin
     * @return a list of DirectoryResourceRssConfig
     *
     */
    public static List<DirectoryResourceRssConfig> getAll( Plugin plugin )
    {
        List<DirectoryResourceRssConfig> listTaskNotifyDirectoryConfig = _dao.loadAll( plugin );

        return listTaskNotifyDirectoryConfig;
    }
}

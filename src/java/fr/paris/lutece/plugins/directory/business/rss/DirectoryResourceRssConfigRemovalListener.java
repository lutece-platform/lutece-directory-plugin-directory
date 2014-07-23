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
package fr.paris.lutece.plugins.directory.business.rss;

import fr.paris.lutece.plugins.directory.service.DirectoryPlugin;
import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.util.RemovalListener;

import java.util.List;
import java.util.Locale;


/**
 * class DirectoryResourceRssConfigRemovalListener
 */
public class DirectoryResourceRssConfigRemovalListener implements RemovalListener
{
    private static final String PROPERTY_DIRECTORY_RESOURCE_RSS_CONFIG_CANNOT_BE_REMOVED = "directory.message.directory_resource_rss_config_can_not_be_removed";

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canBeRemoved( String strId )
    {
        Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
        int nIdEntry = DirectoryUtils.convertStringToInt( strId );

        if ( nIdEntry == DirectoryUtils.CONSTANT_ID_NULL )
        {
            return true;
        }

        if ( pluginDirectory.isInstalled(  ) )
        {
            List<DirectoryResourceRssConfig> listResourceRssConfig = DirectoryResourceRssConfigHome.getAll( pluginDirectory );

            for ( DirectoryResourceRssConfig directoryResourceRss : listResourceRssConfig )
            {
                if ( ( directoryResourceRss.getIdEntryDescription(  ) == nIdEntry ) ||
                        ( directoryResourceRss.getIdEntryTitle(  ) == nIdEntry ) ||
                        ( directoryResourceRss.getIdEntryImage(  ) == nIdEntry ) ||
                        ( directoryResourceRss.getIdEntryLink(  ) == nIdEntry ) ||
                        ( directoryResourceRss.getIdEntryFilter1(  ) == nIdEntry ) ||
                        ( directoryResourceRss.getIdEntryFilter2(  ) == nIdEntry ) )
                {
                    return false;
                }
            }

            return true;
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRemovalRefusedMessage( String strId, Locale locale )
    {
        // Build a message 
        return I18nService.getLocalizedString( PROPERTY_DIRECTORY_RESOURCE_RSS_CONFIG_CANNOT_BE_REMOVED, locale );
    }
}

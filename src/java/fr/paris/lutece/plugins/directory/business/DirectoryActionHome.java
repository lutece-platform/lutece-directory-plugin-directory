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
package fr.paris.lutece.plugins.directory.business;

import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.List;
import java.util.Locale;


/**
 * This class provides instances management methods (create, find, ...) for DirectortyAction objects
 */
public final class DirectoryActionHome
{
    // Static variable pointed at the DAO instance
    private static IDirectoryActionDAO _dao = SpringContextService.getBean( "directoryActionDAO" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private DirectoryActionHome(  )
    {
    }

    /**
     * Load the list of actions by directory state
     * @param nState the state of the directory
     * @param locale the locale
     * @param plugin the plugin
     * @return The Collection of actions
     */
    public static List<DirectoryAction> selectActionsByFormState( int nState, Plugin plugin, Locale locale )
    {
        List<DirectoryAction> listFormActions = _dao.selectActionsByDirectoryState( nState, plugin );

        return I18nService.localizeCollection( listFormActions, locale );
    }

    /**
     * Load the list of actions by directory state
     * @param nState the state of the directory
     * @param locale the locale
     * @param plugin the plugin
     * @return The Collection of actions
     */
    public static List<DirectoryAction> selectActionsRecordByFormState( int nState, Plugin plugin, Locale locale )
    {
        List<DirectoryAction> listFormActions = _dao.selectActionsByDirectoryRecordState( nState, plugin );

        return I18nService.localizeCollection( listFormActions, locale );
    }

    /**
     * Add a new action for directory record for module which uses plugin-directory
     * @param directoryAction The action builded in module which uses plugin-directory
     * @param plugin the plugin
     */
    public static void addNewActionInDirectoryRecordAction( DirectoryAction directoryAction, Plugin plugin )
    {
        _dao.addNewActionInDirectoryRecordAction( directoryAction, plugin );
    }

    /**
     * Delete a directory record action
     * @param plugin plugin
     * @param directoryAction The action to delete
     */
    public static void deleteActionsDirectoryRecord( DirectoryAction directoryAction, Plugin plugin )
    {
        _dao.deleteActionsDirectoryRecord( directoryAction, plugin );
    }

    /**
     * This method add new actions for directory record
     * @param plugin plugin
     * @param directoryAction action to check
     * @return True if the action exists, false otherwise
     */
    public static boolean checkActionsDirectoryRecord( DirectoryAction directoryAction, Plugin plugin )
    {
        return _dao.checkActionsDirectoryRecord( directoryAction, plugin );
    }

    /**
     * Load the list of actions for directory Xsl
     * @param locale the locale
     * @param plugin the plugin
     * @return The Collection of actions
     */
    public static List<DirectoryAction> selectActionsByDirectoryXsl( Plugin plugin, Locale locale )
    {
        List<DirectoryAction> listActions = _dao.selectActionsByDirectoryXsl( plugin );

        return I18nService.localizeCollection( listActions, locale );
    }
}

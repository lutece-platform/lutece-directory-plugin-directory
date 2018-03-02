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
package fr.paris.lutece.plugins.directory.web.action;

import fr.paris.lutece.plugins.directory.utils.DirectoryErrorException;
import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.web.pluginaction.AbstractPluginAction;
import fr.paris.lutece.portal.web.pluginaction.DefaultPluginActionResult;
import fr.paris.lutece.portal.web.pluginaction.IPluginActionResult;

import org.apache.commons.lang.StringUtils;

import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Directory record search
 *
 */
public class SearchRecordDirectoryAction extends AbstractPluginAction<DirectoryAdminSearchFields> implements IDirectoryAction
{
    private static final String PARAMETER_BUTTON_SEARCH = "search";
    private static final String ACTION_NAME = "Search Directory";
    private static final String PARAMETER_DATE_BEGIN_CREATION = "date_begin_creation";
    private static final String PARAMETER_DATE_CREATION = "date_creation";
    private static final String PARAMETER_DATE_END_CREATION = "date_end_creation";
    private static final String PARAMETER_DATE_BEGIN_MODIFICATION = "date_begin_modification";
    private static final String PARAMETER_DATE_MODIFICATION = "date_modification";
    private static final String PARAMETER_DATE_END_MODIFICATION = "date_end_modification";
    private static final String PARAMETER_WORKFLOW_STATE_SELECTED = "search_state_workflow";

    /**
     * {@inheritDoc}
     */
    public void fillModel( HttpServletRequest request, AdminUser adminUser, Map<String, Object> model )
    {
        // nothing to fill, the model is already search friendly
    }

    /**
     * Returns an empty string - nothing to print
     */
    public String getButtonTemplate( )
    {
        return StringUtils.EMPTY;
    }

    /**
     *
     */
    public String getName( )
    {
        return ACTION_NAME;
    }

    /**
     * @see #PARAMETER_BUTTON_SEARCH
     */
    public boolean isInvoked( HttpServletRequest request )
    {
        return request.getParameter( PARAMETER_BUTTON_SEARCH ) != null;
    }

    /**
     * {@inheritDoc}
     */
    public IPluginActionResult process( HttpServletRequest request, HttpServletResponse response, AdminUser adminUser, DirectoryAdminSearchFields searchFields )
            throws AccessDeniedException
    {
        DefaultPluginActionResult result = new DefaultPluginActionResult( );
        String strIdDirectory = request.getParameter( DirectoryUtils.PARAMETER_ID_DIRECTORY );
        int nIdDirectory = DirectoryUtils.convertStringToInt( strIdDirectory );

        searchFields.setIdWorkflowSate( DirectoryUtils.convertStringToInt( request.getParameter( PARAMETER_WORKFLOW_STATE_SELECTED ) ) );

        try
        {
            Locale locale = adminUser.getLocale( );
            // get search filter
            searchFields.setMapQuery( DirectoryUtils.getSearchRecordData( request, nIdDirectory, DirectoryUtils.getPlugin( ), locale ) );
            searchFields.setDateCreationBeginRecord( DirectoryUtils.getSearchRecordDateCreationFromRequest( request, PARAMETER_DATE_BEGIN_CREATION, locale ) );
            searchFields.setDateCreationEndRecord( DirectoryUtils.getSearchRecordDateCreationFromRequest( request, PARAMETER_DATE_END_CREATION, locale ) );
            searchFields.setDateCreationRecord( DirectoryUtils.getSearchRecordDateCreationFromRequest( request, PARAMETER_DATE_CREATION, locale ) );
            searchFields.setDateModificationBeginRecord( DirectoryUtils.getSearchRecordDateCreationFromRequest( request, PARAMETER_DATE_BEGIN_MODIFICATION,
                    locale ) );
            searchFields
                    .setDateModificationEndRecord( DirectoryUtils.getSearchRecordDateCreationFromRequest( request, PARAMETER_DATE_END_MODIFICATION, locale ) );
            searchFields.setDateModificationRecord( DirectoryUtils.getSearchRecordDateCreationFromRequest( request, PARAMETER_DATE_MODIFICATION, locale ) );

            // build redirect url
            result.setRedirect( DirectoryUtils.getJspManageDirectoryRecord( request, nIdDirectory ) ); // + "&" + PARAMETER_SEARCH + "=" + PARAMETER_SEARCH );
        }
        catch( DirectoryErrorException error )
        {
            String strErrorMessage = DirectoryUtils.EMPTY_STRING;

            if ( error.isMandatoryError( ) )
            {
                Object [ ] tabRequiredFields = {
                    error.getTitleField( )
                };
                strErrorMessage = AdminMessageService.getMessageUrl( request, DirectoryUtils.MESSAGE_DIRECTORY_ERROR_MANDATORY_FIELD, tabRequiredFields,
                        AdminMessage.TYPE_STOP );
            }
            else
            {
                Object [ ] tabRequiredFields = {
                        error.getTitleField( ), error.getErrorMessage( )
                };
                strErrorMessage = AdminMessageService
                        .getMessageUrl( request, DirectoryUtils.MESSAGE_DIRECTORY_ERROR, tabRequiredFields, AdminMessage.TYPE_STOP );
            }

            result.setRedirect( strErrorMessage );
        }

        return result;
    }
}

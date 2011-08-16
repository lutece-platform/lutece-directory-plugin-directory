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
package fr.paris.lutece.plugins.directory.web.action;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.spring.SpringContextService;

/**
 * Manages directory action.<br>
 * {@link IDirectoryAction} are detected by {@link SpringContextService#getBeansOfType(Class)}
 *
 */
public final class DirectoryActionManager 
{
	private static final String MARK_DIRECTORY_ACTIONS = "directory_actions";
	/**
	 * Gets the list of {@link IDirectoryAction}
	 * @return the list
	 */
	public static List<IDirectoryAction> getListDirectoryAction(  )
	{
		return SpringContextService.getBeansOfType( IDirectoryAction.class );
	}
	
	/**
	 * Gets the {@link IDirectoryAction} for the request.
	 * @param request the reuqest
	 * @return the invoked {@link IDirectoryAction}, <code>null</code> otherwise.
	 * @see IDirectoryAction#isInvoked(HttpServletRequest)
	 */
	public static IDirectoryAction getDirectoryAction( HttpServletRequest request )
	{
		for ( IDirectoryAction action : getListDirectoryAction(  ) )
		{
			if ( action.isInvoked( request ) )
			{
				return action;
			}
		}
		
		return null;
	}
	
	/**
	 * Fills the model with all actions and adds the list
	 * @param request the request
	 * @param adminUser the admin user
	 * @param model the model
	 */
	public static void fillModel( HttpServletRequest request, AdminUser adminUser, Map<String, Object> model )
	{
		for ( IDirectoryAction action : getListDirectoryAction(  ) )
		{
			action.fillModel( request, adminUser, model );
		}
		
		// add the action list
		model.put( MARK_DIRECTORY_ACTIONS, getListDirectoryAction(  ) );
	}

}

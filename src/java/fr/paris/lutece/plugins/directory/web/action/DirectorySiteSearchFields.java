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

import fr.paris.lutece.plugins.directory.business.Directory;

import java.util.List;

/**
 * implementation of IDirectorySearchFields for storing in session the search filter and sort filter used in DirectoryApp
 * @author merlinfe
 *
 */
public class DirectorySiteSearchFields extends DefaultDirectorySearchFields
{
    /**
     *
     */
    private static final long serialVersionUID = -3832842315618063894L;
    private List<String> _roleKeyList;
    private boolean _bIncludeRoleNull;
    private boolean _bIncludeRoleNone;
    
    /**
     * 	
     * @return true if the filter must included the role none
     */
    public boolean isIncludeRoleNone(  )
    {
        return _bIncludeRoleNone;
    }
    /**
     *  used if the record must be filter by role
     * @param bIncludeRoleNone true if the filter must included the role none
     */
    public void setIncludeRoleNone( boolean bIncludeRoleNone )
    {
        _bIncludeRoleNone = bIncludeRoleNone;
    }

    /**
     * a list of key role
     * @param roleKeyList  a list of key role
     */
    public void setRoleKeyList( List<String> roleKeyList )
    {
        _roleKeyList = roleKeyList;
    }
    /**
     * a list of key role
     * @return  a list of key role
     */
    public List<String> getRoleKeyList(  )
    {
        return _roleKeyList;
    }
    /**
     * used if the record must be filter by role
     * @param _bIncludeRoleNull true if the filter must included the role null
     */
    public void setIncludeRoleNull( boolean _bIncludeRoleNull )
    {
        this._bIncludeRoleNull = _bIncludeRoleNull;
    }
    /**
     * 
     * @return true if the filter must included the role null
     */
    public boolean isIncludeRoleNull(  )
    {
        return _bIncludeRoleNull;
    }
    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.web.action.DefaultDirectorySearchFields#getDefaultIdSortEntry(fr.paris.lutece.plugins.directory.business.Directory)
     */
    public String getDefaultIdSortEntry( Directory directory )
    {
        return directory.getIdSortEntryFront(  );
    }

	  /*
	   * (non-Javadoc)
	   * @see fr.paris.lutece.plugins.directory.web.action.DefaultDirectorySearchFields#isDefaultAscendingSort(fr.paris.lutece.plugins.directory.business.Directory)
	   */
	    public boolean isDefaultAscendingSort( Directory directory )
    {
        return directory.isAscendingSort(  );
    }
}

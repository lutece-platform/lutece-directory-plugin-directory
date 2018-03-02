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
package fr.paris.lutece.plugins.directory.business.parameter;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * DirectoryParameterFilter
 *
 */
public class DirectoryParameterFilter
{
    private boolean _bExcludeParameterKeys;
    private List<String> _listParameterKeys;

    /**
     * Constructor
     */
    public DirectoryParameterFilter( )
    {
        _bExcludeParameterKeys = false;
    }

    /**
     * Check if the filter contains a list of parameters keys
     * 
     * @return true if it contains a list of parameter keys
     */
    public boolean containsListParameterKeys( )
    {
        return _listParameterKeys != null;
    }

    /**
     * Add a parameter key to the list to filter
     * 
     * @param strParameterKey
     *            the parameter key
     */
    public void addParameterKey( String strParameterKey )
    {
        if ( _listParameterKeys == null )
        {
            _listParameterKeys = new ArrayList<String>( );
        }

        _listParameterKeys.add( strParameterKey );
    }

    /**
     * Get the list of parameter keys
     * 
     * @return the list of parameter keys
     */
    public List<String> getListParameterKeys( )
    {
        return _listParameterKeys;
    }

    /**
     * Check if the filter must exclude the list of parameter keys
     * 
     * @return true if the filter must exclude the list of parameter keys, false otherwise
     */
    public boolean excludeParameterKeys( )
    {
        return _bExcludeParameterKeys;
    }

    /**
     * Set true if the filter must exclude the list of parameter keys, false otherwise
     * 
     * @param bExcludeParameterKeys
     *            true if the filter must exclude the list of parameter keys, false otherwise
     */
    public void setExcludeParameterKeys( boolean bExcludeParameterKeys )
    {
        _bExcludeParameterKeys = bExcludeParameterKeys;
    }
}

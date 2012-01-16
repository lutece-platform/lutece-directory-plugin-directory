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
package fr.paris.lutece.plugins.directory.business;


/**
 *
 * class DirectoryFilter
 *
 */
public class DirectoryFilter
{
    public static final String ALL_STRING = "all";
    public static final int ALL_INT = -1;
    public static final int FILTER_FALSE = 0;
    public static final int FILTER_TRUE = 1;
    private int _nIsDisabled = ALL_INT;
    private String _strWorkgroup = ALL_STRING;
    private int _nIdWorkflow = ALL_INT;
    private int _nIsIndexed = ALL_INT;

    /**
     *
     * @return 1 if the directories return must be enabled
     *                    0 if the forms return must be disabled
     */
    public int getIsDisabled(  )
    {
        return _nIsDisabled;
    }

    /**
     * Set 1 if the directories return must be enabled
     *            0 if the forms return must be disabled
     * @param idState  1 if the forms return must be enabled
     *                                      0 if the directories return must be disabled
     */
    public void setIsDisabled( int idState )
    {
        _nIsDisabled = idState;
    }

    /**
     * Check if the filter contains isDisabled state
     * @return true if the filter contain isDisabled state
     */
    public boolean containsIsDisabled(  )
    {
        return ( _nIsDisabled != ALL_INT );
    }

    /**
         *
         * @return the workgroup of the search forms
         */
    public String getWorkgroup(  )
    {
        return _strWorkgroup;
    }

    /**
     * set the workgroup of the search forms
     * @param workgroup the workgroup of the workflow
     */
    public void setWorkGroup( String workgroup )
    {
        _strWorkgroup = workgroup;
    }

    /**
     *
     * @return true if the filter contain workgroup criteria
     */
    public boolean containsWorkgroupCriteria(  )
    {
        return ( !_strWorkgroup.equals( ALL_STRING ) );
    }

    /**
    *
    * @return 1 if the forms have workflow
    *                    0 if the forms have not workflow
    */
    public int getIdWorkflow(  )
    {
        return _nIdWorkflow;
    }

    /**
     * Set 1 if the forms have workflow
     *            0 if the forms have not workflow
     * @param idState  1 if the forms return must be enabled
     *                                      0 if the forms return must be disabled
     */
    public void setIdWorkflow( int idWorkflow )
    {
        _nIdWorkflow = idWorkflow;
    }

    /**
     *
     * @return true if the filter contain form state
     */
    public boolean containsIdWorkflow(  )
    {
        return ( _nIdWorkflow != ALL_INT );
    }

    /**
     *
    * @return 1 if the directories return must be Indexed
    *                    0 if the forms return must not be Indexed
    */
    public int getIsIndexed(  )
    {
        return _nIsIndexed;
    }

    /**
     * Set 1 if the directories return must be indexed
     *            0 if the directories return must be indexed
     * @param idState  1 if the directories return must be indexed
     *                                      0 if the forms return must not be indexed
     */
    public void setIsIndexed( int idState )
    {
        _nIsIndexed = idState;
    }

    /**
    * Check if the filter contains isIndexed state
    * @return true if the filter contains isIndexed state
    */
    public boolean containsIsIndexed(  )
    {
        return ( _nIsIndexed != ALL_INT );
    }
}

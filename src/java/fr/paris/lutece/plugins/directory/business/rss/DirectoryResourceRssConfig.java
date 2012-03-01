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


/**
 *
 * DirectoryResourceRssConfig
 *
 */
public class DirectoryResourceRssConfig
{
    private int _nIdRss;
    private int _nIdDirectory;
    private int _nIdEntryTitle;
    private int _nIdEntryDescription;
    private int _nIdEntryImage;
    private int _nIdEntryLink;
    private int _nIdEntryFilter1;
    private String _strValueFilter1;
    private int _nIdEntryFilter2;
    private String _strValueFilter2;
    private int _nIdWorkflowState;

    /**
    *
    * @return id Rss
    */
    public int getIdRss(  )
    {
        return _nIdRss;
    }

    /**
     * set id Rss
     * @param idRss id Rss
     */
    public void setIdRss( int idRss )
    {
        _nIdRss = idRss;
    }

    /**
    *
    * @return id directory
    */
    public int getIdDirectory(  )
    {
        return _nIdDirectory;
    }

    /**
     * set id directory
     * @param idDirectory id directory
     */
    public void setIdDirectory( int idDirectory )
    {
        _nIdDirectory = idDirectory;
    }

    /**
     * Return id entry of directory used as title
     * @return id entry of directory
     */
    public int getIdEntryTitle(  )
    {
        return _nIdEntryTitle;
    }

    /**
     * set id entry of directory used as title
     * @param idEntryTitle id of Entry directory
     */
    public void setIdEntryTitle( int idEntryTitle )
    {
        _nIdEntryTitle = idEntryTitle;
    }

    /**
     * Return id entry of directory used as description
     * @return id entry of directory
     */
    public int getIdEntryDescription(  )
    {
        return _nIdEntryDescription;
    }

    /**
     * set id of Entry directory used as description
     * @param idEntryDescription id of Entry directory
     */
    public void setIdEntryDescription( int idEntryDescription )
    {
        _nIdEntryDescription = idEntryDescription;
    }

    /**
     * Return id entry of directory used as image
     * @return id entry of directory
     */
    public int getIdEntryImage(  )
    {
        return _nIdEntryImage;
    }

    /**
     * set id of Entry directory used as image
     * @param idEntryImage id of Entry directory
     */
    public void setIdEntryImage( int idEntryImage )
    {
        _nIdEntryImage = idEntryImage;
    }

    /**
     * Return id entry of directory used as Link
     * @return id entry of directory
     */
    public int getIdEntryLink(  )
    {
        return _nIdEntryLink;
    }

    /**
     * set id of Entry directory used as Link
     * @param idEntryLink id of Entry directory
     */
    public void setIdEntryLink( int idEntryLink )
    {
        _nIdEntryLink = idEntryLink;
    }

    /**
     * Return id entry of directory used as filter 1
     * @return id entry of directory
     */
    public int getIdEntryFilter1(  )
    {
        return _nIdEntryFilter1;
    }

    /**
     * set id of Entry directory used as filter 1
     * @param idEntryFilter1 id of Entry directory
     */
    public void setIdEntryFilter1( int idEntryFilter1 )
    {
        _nIdEntryFilter1 = idEntryFilter1;
    }

    /**
     * Return value of entry of directory used as filter 1
     * @return id entry of directory
     */
    public String getValueFilter1(  )
    {
        return _strValueFilter1;
    }

    /**
     * set value of Entry directory used as filter 1
     * @param idEntryFilter1 id of Entry directory
     */
    public void setValueFilter1( String strValueFilter1 )
    {
        _strValueFilter1 = strValueFilter1;
    }

    /**
     * Return id entry of directory used as filter 1
     * @return id entry of directory
     */
    public int getIdEntryFilter2(  )
    {
        return _nIdEntryFilter2;
    }

    /**
     * set id of Entry directory used as filter 1
     * @param idEntryFilter1 id of Entry directory
     */
    public void setIdEntryFilter2( int idEntryFilter2 )
    {
        _nIdEntryFilter2 = idEntryFilter2;
    }

    /**
     * Return value of entry of directory used as filter 2
     * @return id entry of directory
     */
    public String getValueFilter2(  )
    {
        return _strValueFilter2;
    }

    /**
     * set value of Entry directory used as filter 2
     * @param idEntryFilter2 id of Entry directory
     */
    public void setValueFilter2( String strValueFilter2 )
    {
        _strValueFilter2 = strValueFilter2;
    }

    /**
     * Return id state of workflow
     * @return id state of workflow
     */
    public int getIdWorkflowState(  )
    {
        return _nIdWorkflowState;
    }

    /**
     * set id state of workflow
     * @param idWorkflowState id state of workflow
     */
    public void setIdWorkflowState( int idWorkflowState )
    {
        _nIdWorkflowState = idWorkflowState;
    }
}

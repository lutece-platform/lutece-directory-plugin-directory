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
package fr.paris.lutece.plugins.directory.web.action;

import fr.paris.lutece.plugins.directory.business.Directory;
import fr.paris.lutece.plugins.directory.business.IEntry;
import fr.paris.lutece.plugins.directory.business.RecordField;
import fr.paris.lutece.portal.service.plugin.Plugin;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * Visualization of all needed for searched record.
 * 
 * @author merlinfe
 *
 */
public interface IDirectorySearchFields
{
    /**
     * Default items per page
     *
     * @return default items per page
     */
    int getDefaultItemsPerPage( );

    /**
     * Default items per page
     *
     * @param nDefaultItemsPerPage
     *            default items per page
     */
    void setDefaultItemsPerPage( int nDefaultItemsPerPage );

    /**
     * Current page index
     *
     * @return current page index
     */
    String getCurrentPageIndex( );

    /**
     * Current page index
     *
     * @param strCurrentPageIndex
     *            current page index
     */
    void setCurrentPageIndex( String strCurrentPageIndex );

    /**
     * Id directory
     *
     * @return id directory
     */
    int getIdDirectory( );

    /**
     * Id directory
     *
     * @param nIdDirectory
     *            id directory
     */
    void setIdDirectory( int nIdDirectory );

    /**
     * Id entry
     *
     * @return id entry
     */
    int getIdEntry( );

    /**
     * Id entry
     *
     * @param nIdEntry
     *            id entry
     */
    void setIdEntry( int nIdEntry );

    /**
     * Map query
     *
     * @return map query
     */
    HashMap<String, List<RecordField>> getMapQuery( );

    /**
     * Map query
     *
     * @param mapQuery
     *            map query
     */
    void setMapQuery( HashMap<String, List<RecordField>> mapQuery );

    /**
     * Date creation begin record
     *
     * @return date creation begin record
     */
    Date getDateCreationBeginRecord( );

    /**
     * Date creation begin record
     *
     * @param dateCreationBeginRecord
     *            date creation begin record
     */
    void setDateCreationBeginRecord( Date dateCreationBeginRecord );

    /**
     * Date creation end record
     *
     * @return date creation end record
     */
    Date getDateCreationEndRecord( );

    /**
     * Date creation end record
     *
     * @param dateCreationEndRecord
     *            date creation end record
     */
    void setDateCreationEndRecord( Date dateCreationEndRecord );

    /**
     * Date creation record
     *
     * @return date creation record
     */
    Date getDateCreationRecord( );

    /**
     * Date creation record
     *
     * @param dateCreationRecord
     *            date creation record
     */
    void setDateCreationRecord( Date dateCreationRecord );

    /**
     * Set the sort entry
     *
     * @param sortEntry
     *            the sort entry
     */
    void setSortEntry( IEntry sortEntry );

    /**
     * Get the sort entry
     *
     * @return the sort entry
     */
    IEntry getSortEntry( );

    /**
     * Set the sort order
     *
     * @param nSortOrder
     *            the sort order
     */
    void setSortOrder( int nSortOrder );

    /**
     * Get the sort order
     *
     * @return the sort order
     */
    int getSortOrder( );

    /**
     * Set the sort parameters
     *
     * @param request
     *            the HTTP request
     * @param directory
     *            the directory
     * @param plugin
     *            the plugin
     */
    void setSortParameters( HttpServletRequest request, Directory directory, Plugin plugin );

    /**
     * return the default entry used for sorting the records
     * 
     * @param directory
     *            the directory
     * @return the default entry used for sorting the records
     */
    String getDefaultIdSortEntry( Directory directory );

    /**
     * return true if the record must be sorted by ascending sort
     * 
     * @param directory
     *            the directory
     * @return true if the record must be sorted by ascending sort
     */
    boolean isDefaultAscendingSort( Directory directory );

    /**
     * Items per page
     * 
     * @return items per page
     */
    int getItemsPerPage( );

    /**
     * Items per page
     * 
     * @param nItemsPerPage
     *            items per page
     */
    void setItemsPerPage( int nItemsPerPage );

    /**
     *
     * @return true if the records must be disabled
     */
    int getIsDisabled( );

    /***
     * set true if the records displayed must be disabled
     * 
     * @param nIsDisabled
     *            true if the records displayed must be disabled
     */
    void setIsDisabled( int nIsDisabled );

    /**
     * Date modification begin record
     * 
     * @return date modification begin record
     */
    Date getDateModificationBeginRecord( );

    /**
     * Date modification begin record
     * 
     * @param dateModificationBeginRecord
     *            date modification begin record
     */
    void setDateModificationBeginRecord( Date dateModificationBeginRecord );

    /**
     * Date modification end record
     * 
     * @return date modification end record
     */
    Date getDateModificationEndRecord( );

    /**
     * Date modification end record
     * 
     * @param dateModificationEndRecord
     *            date modification end record
     */
    void setDateModificationEndRecord( Date dateModificationEndRecord );

    /**
     * Date modification record
     * 
     * @return date modification record
     */
    Date getDateModificationRecord( );

    /**
     * Date modification record
     * 
     * @param dateModificationRecord
     *            date modification record
     */
    void setDateModificationRecord( Date dateModificationRecord );

    /**
     * Set true if it must be sort by date modification
     * 
     * @param bSortByDateModification
     *            true if it must be sort by date modification
     */
    void setSortByDateModification( boolean bSortByDateModification );

    /**
     * Check if it must be sort by date modification
     * 
     * @return true if it must be sort by date modification, false otherwise
     */
    boolean isSortByDateModification( );
}

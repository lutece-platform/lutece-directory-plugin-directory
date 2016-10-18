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

import fr.paris.lutece.util.ReferenceList;

import java.sql.Timestamp;

import java.util.List;

/**
 *
 * class RecordFieldFilter
 *
 */
public class RecordFieldFilter
{
    public static final int ALL_INT = -1;
    public static final int FILTER_FALSE = 0;
    public static final int FILTER_TRUE = 1;
    public static final int ORDER_NONE = -1;
    public static final int ORDER_ASC = 0;
    public static final int ORDER_DESC = 1;
    private int _nIdDirectory = ALL_INT;
    private int _nIdRecord = ALL_INT;
    private int _nIdField = ALL_INT;
    private int _nIdEntry = ALL_INT;
    private Timestamp _tDateFirst;
    private Timestamp _tDateLast;
    private int _nIsEntryShownInResultList = ALL_INT;
    private int _nIsEntryShownInResultRecord = ALL_INT;
    private int _nIsDisabled = ALL_INT;
    private ReferenceList _workgroupKeyList;
    private List<String> _roleKeyList;
    private boolean _bIncludeRoleNull;
    private IEntry _sortEntry;
    private int _nSortOrder = ORDER_NONE;
    private boolean _bOrderByDateModification = false;

    /**
     * Gets the entry that should be used to sort the results
     * 
     * @return the entry that should be used to sort the results
     */
    public IEntry getSortEntry( )
    {
        return _sortEntry;
    }

    /**
     * Set the entry that should be used to sort the results
     * 
     * @param sortEntry
     *            the entry to use to sort the results, <code>null</code> to use default sort (creation date).
     */
    public void setSortEntry( IEntry sortEntry )
    {
        this._sortEntry = sortEntry;
    }

    /**
     * <code>true</code> if an entry should be used to sort the results, <code>false</code> otherwise
     * 
     * @return <code>true</code> if an entry should be used to sort the results, <code>false</code> otherwise.
     */
    public boolean containsSortEntry( )
    {
        return _sortEntry != null;
    }

    /**
     * Gets the sort order. Default is {@link #ORDER_NONE}
     * 
     * @return the sort order
     * @see #ORDER_ASC
     * @see #ORDER_DESC
     * @see #ORDER_NONE
     */
    public int getSortOrder( )
    {
        return _nSortOrder;
    }

    /**
     * Sets the sort order. Use it with {@link #setSortEntry(IEntry)}
     * 
     * @param nSortOrder
     *            the sort order.
     * @see #ORDER_ASC
     * @see #ORDER_DESC
     * @see #ORDER_NONE
     */
    public void setSortOrder( int nSortOrder )
    {
        this._nSortOrder = nSortOrder;
    }

    /**
     * Return <code>true</code> if sort order is not {@link #ORDER_NONE}
     * 
     * @return <code>true</code> if sort order is not {@link #ORDER_NONE}, <code>false</code> otherwise.
     */
    public boolean containsSortOrder( )
    {
        return this._nSortOrder != ORDER_NONE;
    }

    /**
     *
     * @return the id of directory insert in the filter
     */
    public int getIdDirectory( )
    {
        return _nIdDirectory;
    }

    /**
     * set the id of directory in the filter
     * 
     * @param idDirectory
     *            the id of directory to insert in the filter
     */
    public void setIdDirectory( int idDirectory )
    {
        _nIdDirectory = idDirectory;
    }

    /**
     *
     * @return true if the filter contain an id of directory
     *
     */
    public boolean containsIdDirectory( )
    {
        return ( _nIdDirectory != ALL_INT );
    }

    /**
     *
     * @return the id of record insert in the filter
     */
    public int getIdRecord( )
    {
        return _nIdRecord;
    }

    /**
     * set the id of record in the filter
     * 
     * @param idRecord
     *            the id of the record to insert in the filter
     */
    public void setIdRecord( int idRecord )
    {
        _nIdRecord = idRecord;
    }

    /**
     *
     * @return true if the filter contain an id of record
     *
     */
    public boolean containsIdRecord( )
    {
        return ( _nIdRecord != ALL_INT );
    }

    /**
     *
     * @return the id of field insert in the filter
     */
    public int getIdField( )
    {
        return _nIdField;
    }

    /**
     * set the id of field depend in the filter
     * 
     * @param idField
     *            the id of field depend to insert in the filter
     */
    public void setIdField( int idField )
    {
        _nIdField = idField;
    }

    /**
     *
     * @return true if the filter contain an id of field depend
     */
    public boolean containsIdField( )
    {
        return ( _nIdField != ALL_INT );
    }

    /**
     *
     * @return the id of entry insert in the filter
     */
    public int getIdEntry( )
    {
        return _nIdEntry;
    }

    /**
     * set the id of entry depend in the filter
     * 
     * @param idEntry
     *            the id of entry depend to insert in the filter
     */
    public void setIdEntry( int idEntry )
    {
        _nIdEntry = idEntry;
    }

    /**
     *
     * @return true if the filter contain an id of entry depend
     */
    public boolean containsIdEntry( )
    {
        return ( _nIdEntry != ALL_INT );
    }

    /**
     *
     * @return date of the first submit
     */
    public Timestamp getDateFirst( )
    {
        return _tDateFirst;
    }

    /**
     * set the date of the first submit
     * 
     * @param begin
     *            date of the first submit
     */
    public void setDateFirst( Timestamp begin )
    {
        _tDateFirst = begin;
    }

    /**
     *
     * @return true if the filter contain the date of the first submit
     */
    public boolean containsDateFirst( )
    {
        return ( _tDateFirst != null );
    }

    /**
     *
     * @return date of the last submit
     */
    public Timestamp getDateLast( )
    {
        return _tDateLast;
    }

    /**
     * set the date of the last submit
     * 
     * @param end
     *            the date of the last submit
     */
    public void setDateLast( Timestamp end )
    {
        _tDateLast = end;
    }

    /**
     *
     * @return true if the filter contain the date of the last submit
     */
    public boolean containsDateLast( )
    {
        return ( _tDateLast != null );
    }

    /**
     *
     * @return 1 if the entry associate to the record field must be in the result list, O if the entry associate to the record field must not be in the result
     *         list
     */
    public int getIsEntryShownInResultList( )
    {
        return _nIsEntryShownInResultList;
    }

    /**
     * set 1 if the entry associate to the record field must be in the result list, O if the entry associate to the record field must not be in the result list
     * 
     * @param nIsShown
     *            1 if the entry associate to the record field must be in the result list
     */
    public void setIsEntryShownInResultList( int nIsShown )
    {
        _nIsEntryShownInResultList = nIsShown;
    }

    /**
     *
     * @return true if the filter is initialized
     */
    public boolean containsIsEntryShownInResultList( )
    {
        return ( _nIsEntryShownInResultList != ALL_INT );
    }

    /**
     *
     * @return 1 if the entry associate to the record field must be in the result record, O if the entry associate to the record field must not be in the result
     *         record
     */
    public int getIsEntryShownInResultRecord( )
    {
        return _nIsEntryShownInResultRecord;
    }

    /**
     * set 1 if the entry associate to the record field must be in the result record, O if the entry associate to the record field must not be in the result
     * record
     * 
     * @param nIsShown
     *            1 if the entry associate to the record field must be in the result record
     */
    public void setIsEntryShownInResultRecord( int nIsShown )
    {
        _nIsEntryShownInResultRecord = nIsShown;
    }

    /**
     *
     * @return true if the filter is initialized
     */
    public boolean containsIsEntryShownInResultRecord( )
    {
        return ( _nIsEntryShownInResultRecord != ALL_INT );
    }

    /**
     *
     * @return 1 if the record return must be enabled 0 if the record return must be disabled
     */
    public int getIsDisabled( )
    {
        return _nIsDisabled;
    }

    /**
     * Set 1 if the recors return must be enabled 0 if the record return must be disabled
     * 
     * @param idState
     *            1 if the record return must be enabled 0 if the record return must be disabled
     */
    public void setIsDisabled( int idState )
    {
        _nIsDisabled = idState;
    }

    /**
     *
     * @return true if the filter contain form state
     */
    public boolean containsIsDisabled( )
    {
        return ( _nIsDisabled != ALL_INT );
    }

    /**
     * The workgroup key list to set
     * 
     * @param workgroupKeyList
     *            The workgroup key list
     */
    public void setWorkgroupKeyList( ReferenceList workgroupKeyList )
    {
        this._workgroupKeyList = workgroupKeyList;
    }

    /**
     * Set the role key list
     * 
     * @param roleKeyList
     *            The role key list
     * @param bIncludeRoleNone
     *            include role set to "none"
     * @param bIncludeRoleNull
     *            include role set to "null"
     */
    public void setRoleKeyList( List<String> roleKeyList, boolean bIncludeRoleNone, boolean bIncludeRoleNull )
    {
        this._roleKeyList = roleKeyList;

        if ( bIncludeRoleNone )
        {
            roleKeyList.add( Directory.ROLE_NONE );
        }

        this._bIncludeRoleNull = bIncludeRoleNull;
    }

    /**
     * Test if the filter contains at least one role key
     * 
     * @return true if the filter contain role key list
     */
    public boolean containsRoleKeyList( )
    {
        return ( ( _roleKeyList != null ) && ( _roleKeyList.size( ) > 0 ) );
    }

    /**
     * Get the role key list
     * 
     * @return the role key list
     */
    public List<String> getRoleKeyList( )
    {
        return _roleKeyList;
    }

    /**
     * @return the _bIncludeRoleNull
     */
    public boolean includeRoleNull( )
    {
        return _bIncludeRoleNull;
    }

    /**
     * Get workgroup key list
     * 
     * @return The workgroup key list
     */
    public ReferenceList getWorkgroupKeyList( )
    {
        return _workgroupKeyList;
    }

    /**
     *
     * @return true if the filter contain workgroup key list
     */
    public boolean containsWorkgroupKeyList( )
    {
        return ( ( _workgroupKeyList != null ) && ( _workgroupKeyList.size( ) != 0 ) );
    }

    /**
     * Set order by date modification
     * 
     * @param bOrderByDateModification
     *            true if filter by date modification
     */
    public void setOrderByDateModification( boolean bOrderByDateModification )
    {
        _bOrderByDateModification = bOrderByDateModification;
    }

    /**
     * Order by date modification
     * 
     * @return order by date modification
     */
    public boolean isOrderByDateModification( )
    {
        return _bOrderByDateModification;
    }
}

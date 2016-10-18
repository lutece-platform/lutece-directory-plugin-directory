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
import fr.paris.lutece.plugins.directory.business.EntryHome;
import fr.paris.lutece.plugins.directory.business.IEntry;
import fr.paris.lutece.plugins.directory.business.RecordField;
import fr.paris.lutece.plugins.directory.business.RecordFieldFilter;
import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.web.constants.Parameters;

import java.io.Serializable;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * DefaultDirectorySearchFields
 */
public abstract class DefaultDirectorySearchFields implements IDirectorySearchFields, Serializable
{
    public static final int ALL_INT = -1;

    /**
     * Generated serial version UID
     */
    private static final long serialVersionUID = -2839610597359957115L;
    private static final String PROPERTY_ITEM_PER_PAGE = "directory.itemsPerPage";
    private int _nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( PROPERTY_ITEM_PER_PAGE, 50 );
    private int _nItemsPerPage;
    private int _nIdDirectory = ALL_INT;
    private int _nIdEntry = ALL_INT;
    private int _nIsDisabled = ALL_INT;
    private HashMap<String, List<RecordField>> _mapQuery;
    private Date _dateCreationBeginRecord;
    private Date _dateCreationEndRecord;
    private Date _dateCreationRecord;
    private Date _dateModificationBeginRecord;
    private Date _dateModificationEndRecord;
    private Date _dateModificationRecord;
    private boolean _bIsSortByDateModification;
    private IEntry _sortEntry;
    private int _nSortOrder = RecordFieldFilter.ORDER_NONE;
    private String _strCurrentPageIndex;

    /*
     * (non-Javadoc)
     * 
     * @see fr.paris.lutece.plugins.directory.web.action.IDirectorySearchFields# getCurrentPageIndex()
     */
    public String getCurrentPageIndex( )
    {
        return _strCurrentPageIndex;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.paris.lutece.plugins.directory.web.action.IDirectorySearchFields# getDateCreationBeginRecord()
     */
    public Date getDateCreationBeginRecord( )
    {
        return _dateCreationBeginRecord;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.paris.lutece.plugins.directory.web.action.IDirectorySearchFields# getDateCreationEndRecord()
     */
    public Date getDateCreationEndRecord( )
    {
        return _dateCreationEndRecord;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.paris.lutece.plugins.directory.web.action.IDirectorySearchFields# getDateCreationRecord()
     */
    public Date getDateCreationRecord( )
    {
        return _dateCreationRecord;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.paris.lutece.plugins.directory.web.action.IDirectorySearchFields# getDefaultItemsPerPage()
     */
    public int getDefaultItemsPerPage( )
    {
        return _nDefaultItemsPerPage;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.paris.lutece.plugins.directory.web.action.IDirectorySearchFields# getIdDirectory()
     */
    public int getIdDirectory( )
    {
        return _nIdDirectory;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.paris.lutece.plugins.directory.web.action.IDirectorySearchFields# getIdEntry()
     */
    public int getIdEntry( )
    {
        return _nIdEntry;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.paris.lutece.plugins.directory.web.action.IDirectorySearchFields# getMapQuery()
     */
    public HashMap<String, List<RecordField>> getMapQuery( )
    {
        return _mapQuery;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.paris.lutece.plugins.directory.web.action.IDirectorySearchFields# getSortEntry()
     */
    public IEntry getSortEntry( )
    {
        return _sortEntry;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.paris.lutece.plugins.directory.web.action.IDirectorySearchFields# getSortOrder()
     */
    public int getSortOrder( )
    {
        return _nSortOrder;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.paris.lutece.plugins.directory.web.action.IDirectorySearchFields# setCurrentPageIndex(java.lang.String)
     */
    public void setCurrentPageIndex( String strCurrentPageIndex )
    {
        _strCurrentPageIndex = strCurrentPageIndex;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.paris.lutece.plugins.directory.web.action.IDirectorySearchFields# setDateCreationBeginRecord(java.util.Date)
     */
    public void setDateCreationBeginRecord( Date dateCreationBeginRecord )
    {
        _dateCreationBeginRecord = dateCreationBeginRecord;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.paris.lutece.plugins.directory.web.action.IDirectorySearchFields# setDateCreationEndRecord(java.util.Date)
     */
    public void setDateCreationEndRecord( Date dateCreationEndRecord )
    {
        _dateCreationEndRecord = dateCreationEndRecord;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.paris.lutece.plugins.directory.web.action.IDirectorySearchFields# setDateCreationRecord(java.util.Date)
     */
    public void setDateCreationRecord( Date dateCreationRecord )
    {
        _dateCreationRecord = dateCreationRecord;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.paris.lutece.plugins.directory.web.action.IDirectorySearchFields# setDefaultItemsPerPage(int)
     */
    public void setDefaultItemsPerPage( int nDefaultItemsPerPage )
    {
        _nDefaultItemsPerPage = nDefaultItemsPerPage;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.paris.lutece.plugins.directory.web.action.IDirectorySearchFields# setIdDirectory(int)
     */
    public void setIdDirectory( int nIdDirectory )
    {
        _nIdDirectory = nIdDirectory;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.paris.lutece.plugins.directory.web.action.IDirectorySearchFields# setIdEntry(int)
     */
    public void setIdEntry( int nIdEntry )
    {
        _nIdEntry = nIdEntry;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.paris.lutece.plugins.directory.web.action.IDirectorySearchFields# setMapQuery(java.util.HashMap)
     */
    public void setMapQuery( HashMap<String, List<RecordField>> mapQuery )
    {
        _mapQuery = mapQuery;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.paris.lutece.plugins.directory.web.action.IDirectorySearchFields# setSortEntry(fr.paris.lutece.plugins.directory.business.IEntry)
     */
    public void setSortEntry( IEntry sortEntry )
    {
        _sortEntry = sortEntry;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.paris.lutece.plugins.directory.web.action.IDirectorySearchFields# setSortOrder(int)
     */
    public void setSortOrder( int nSortOrder )
    {
        _nSortOrder = nSortOrder;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.paris.lutece.plugins.directory.web.action.IDirectorySearchFields# setSortParameters(javax.servlet.http.HttpServletRequest,
     * fr.paris.lutece.plugins.directory.business.Directory, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public void setSortParameters( HttpServletRequest request, Directory directory, Plugin plugin )
    {
        String strSortedAttributeName = request.getParameter( Parameters.SORTED_ATTRIBUTE_NAME );
        String strAscSort = null;

        if ( ( strSortedAttributeName != null ) || ( getDefaultIdSortEntry( directory ) != null ) )
        {
            if ( strSortedAttributeName == null )
            {
                strSortedAttributeName = getDefaultIdSortEntry( directory );
            }

            if ( DirectoryUtils.PARAMETER_DATECREATION.equals( strSortedAttributeName ) )
            {
                // IMPORTANT : date creation is default filter
            }
            else
                if ( DirectoryUtils.PARAMETER_DATEMODIFICATION.equals( strSortedAttributeName ) )
                {
                    _bIsSortByDateModification = true;
                }
                else
                {
                    int nSortedEntryId = Integer.parseInt( strSortedAttributeName );
                    _sortEntry = EntryHome.findByPrimaryKey( nSortedEntryId, plugin );
                }

            strAscSort = request.getParameter( Parameters.SORTED_ASC );

            boolean bIsAscSort;

            if ( strAscSort != null )
            {
                bIsAscSort = Boolean.parseBoolean( strAscSort );
            }
            else
            {
                bIsAscSort = isDefaultAscendingSort( directory );
            }

            if ( bIsAscSort )
            {
                _nSortOrder = RecordFieldFilter.ORDER_ASC;
            }
            else
            {
                _nSortOrder = RecordFieldFilter.ORDER_DESC;
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.paris.lutece.plugins.directory.web.action.IDirectorySearchFields# getItemsPerPage()
     */
    public int getItemsPerPage( )
    {
        return _nItemsPerPage;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.paris.lutece.plugins.directory.web.action.IDirectorySearchFields# setItemsPerPage(int)
     */
    public void setItemsPerPage( int nItemsPerPage )
    {
        _nItemsPerPage = nItemsPerPage;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.paris.lutece.plugins.directory.web.action.IDirectorySearchFields# setIsDisabled(int)
     */
    public void setIsDisabled( int nIdIsDisabled )
    {
        _nIsDisabled = nIdIsDisabled;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.paris.lutece.plugins.directory.web.action.IDirectorySearchFields# getIsDisabled()
     */
    public int getIsDisabled( )
    {
        return _nIsDisabled;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.paris.lutece.plugins.directory.web.action.IDirectorySearchFields# getDefaultIdSortEntry (fr.paris.lutece.plugins.directory.business.Directory)
     */
    public abstract String getDefaultIdSortEntry( Directory directory );

    /*
     * (non-Javadoc)
     * 
     * @see fr.paris.lutece.plugins.directory.web.action.IDirectorySearchFields# isDefaultAscendingSort (fr.paris.lutece.plugins.directory.business.Directory)
     */
    public abstract boolean isDefaultAscendingSort( Directory directory );

    /**
     * {@inheritDoc}
     */
    public void setDateModificationBeginRecord( Date dateModificationBeginRecord )
    {
        _dateModificationBeginRecord = dateModificationBeginRecord;
    }

    /**
     * {@inheritDoc}
     */
    public Date getDateModificationBeginRecord( )
    {
        return _dateModificationBeginRecord;
    }

    /**
     * {@inheritDoc}
     */
    public void setDateModificationEndRecord( Date dateModificationEndRecord )
    {
        _dateModificationEndRecord = dateModificationEndRecord;
    }

    /**
     * {@inheritDoc}
     */
    public Date getDateModificationEndRecord( )
    {
        return _dateModificationEndRecord;
    }

    /**
     * {@inheritDoc}
     */
    public void setDateModificationRecord( Date dateModificationRecord )
    {
        _dateModificationRecord = dateModificationRecord;
    }

    /**
     * {@inheritDoc}
     */
    public Date getDateModificationRecord( )
    {
        return _dateModificationRecord;
    }

    /**
     * {@inheritDoc}
     */
    public void setSortByDateModification( boolean bIsSortByDateModification )
    {
        _bIsSortByDateModification = bIsSortByDateModification;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isSortByDateModification( )
    {
        return _bIsSortByDateModification;
    }
}

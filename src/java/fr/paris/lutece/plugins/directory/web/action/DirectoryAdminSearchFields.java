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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.directory.business.IEntry;
import fr.paris.lutece.plugins.directory.business.RecordField;
import fr.paris.lutece.plugins.directory.business.RecordFieldFilter;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupService;
import fr.paris.lutece.util.html.ItemNavigator;
import fr.paris.lutece.util.url.UrlItem;

/**
 * Visualization of all needed session values.
 * Many features depends on search result or paginator.
 * Those fields may be required for actions.
 *
 */
public final class DirectoryAdminSearchFields implements Serializable
{
	private static final long serialVersionUID = 1L;

	private static final String PROPERTY_ITEM_PER_PAGE = "directory.itemsPerPage";

    //session fields
    private int _nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( PROPERTY_ITEM_PER_PAGE, 50 );
    private String _strCurrentPageIndexDirectory;
    private int _nItemsPerPageDirectory;
    private String _strCurrentPageIndexEntry;
    private int _nItemsPerPageEntry;
    private String _strCurrentPageIndexPrintMass;

    //private String _nItemsPerPagePrintMass;
    private int _nItemsPerPagePrintMass;
    private String _strCurrentPageIndex;
    private String _strCurrentPageIndexDirectoryRecord;
    private int _nItemsPerPageDirectoryRecord;
    private int _nItemsPerPage;
    private int _nIdActive = -1;
    private String _strWorkGroup = AdminWorkgroupService.ALL_GROUPS;
    private int _nIdDirectory = -1;
    private int _nIdEntry = -1;
    private int _nCountLine;
    private int _nCountLineFailure;
    private int _nIdWorkflowSate = -1;
    private StringBuffer _strError;
    private HashMap<String, List<RecordField>> _mapQuery;
    private Date _dateCreationBeginRecord;
    private Date _dateCreationEndRecord;
    private Date _dateCreationRecord;
    private List<String> _listSelectedRecords;
    private List<Integer> _listIdsResultRecord;
    private ItemNavigator _itemNavigatorViewRecords;
    private ItemNavigator _itemNavigatorHistory;
    private String _strRedirectUrl;
    private IEntry _sortEntry;
    private int _nSortOrder = RecordFieldFilter.ORDER_NONE;
    
    /**
     * Gets the selected records
     * @return the selected records
     */
    public List<String> getSelectedRecords(  )
    {
    	return _listSelectedRecords;
    }
    
    /**
     * Sets the selected records
     * @param listSelectedRecords the selected records
     */
    public void setSelectedRecords( List<String> listSelectedRecords )
    {
    	_listSelectedRecords = listSelectedRecords;
    }
    
    /**
     * Default items per page
     * @return default items per page
     */
	public int getDefaultItemsPerPage(  )
	{
		return _nDefaultItemsPerPage;
	}
	
	/**
	 * Default items per page 
	 * @param nDefaultItemsPerPage default items per page
	 */
	public void setDefaultItemsPerPage( int nDefaultItemsPerPage )
	{
		_nDefaultItemsPerPage = nDefaultItemsPerPage;
	}
	
	/**
	 * Current page index directory
	 * @return current page index directory
	 */
	public String getCurrentPageIndexDirectory(  )
	{
		return _strCurrentPageIndexDirectory;
	}
	
	/**
	 * Current page index directory
	 * @param strCurrentPageIndexDirectory current page index directory
	 */
	public void setCurrentPageIndexDirectory( String strCurrentPageIndexDirectory )
	{
		_strCurrentPageIndexDirectory = strCurrentPageIndexDirectory;
	}
	
	/**
	 * Items per page directory
	 * @return item per page directory
	 */
	public int getItemsPerPageDirectory(  )
	{
		return _nItemsPerPageDirectory;
	}
	
	/**
	 * Items per page directory
	 * @param nItemsPerPageDirectory items per page directory
	 */
	public void setItemsPerPageDirectory( int nItemsPerPageDirectory )
	{
		_nItemsPerPageDirectory = nItemsPerPageDirectory;
	}
	
	/**
	 * Current page index entry
	 * @return current page index entry
	 */
	public String getCurrentPageIndexEntry(  )
	{
		return _strCurrentPageIndexEntry;
	}
	
	/**
	 * Current page index entry
	 * @param strCurrentPageIndexEntry current page index entry
	 */
	public void setCurrentPageIndexEntry( String strCurrentPageIndexEntry )
	{
		_strCurrentPageIndexEntry = strCurrentPageIndexEntry;
	}
	
	/**
	 * Items per page entry
	 * @return items per page entry
	 */
	public int getItemsPerPageEntry(  )
	{
		return _nItemsPerPageEntry;
	}
	
	/**
	 * Items per page entry
	 * @param nItemsPerPageEntry items per page entry
	 */
	public void setItemsPerPageEntry( int nItemsPerPageEntry )
	{
		_nItemsPerPageEntry = nItemsPerPageEntry;
	}
	
	/**
	 * Current page index print mass
	 * @return current page index print mass
	 */
	public String getCurrentPageIndexPrintMass(  )
	{
		return _strCurrentPageIndexPrintMass;
	}
	
	/**
	 * Current page index print mass
	 * @param strCurrentPageIndexPrintMass current page index print mass
	 */
	public void setCurrentPageIndexPrintMass( String strCurrentPageIndexPrintMass )
	{
		_strCurrentPageIndexPrintMass = strCurrentPageIndexPrintMass;
	}
	
	/**
	 * Items per page print mass
	 * @return items per page print mass
	 */
	public int getItemsPerPagePrintMass(  )
	{
		return _nItemsPerPagePrintMass;
	}
	
	/**
	 * Items per page print mass
	 * @param nItemsPerPagePrintMass items per page print mass
	 */
	public void setItemsPerPagePrintMass( int nItemsPerPagePrintMass )
	{
		_nItemsPerPagePrintMass = nItemsPerPagePrintMass;
	}
	
	/**
	 * Current page index
	 * @return current page index
	 */
	public String getCurrentPageIndex(  )
	{
		return _strCurrentPageIndex;
	}
	
	/**
	 * Current page index
	 * @param strCurrentPageIndex current page index
	 */
	public void setCurrentPageIndex( String strCurrentPageIndex )
	{
		_strCurrentPageIndex = strCurrentPageIndex;
	}
	
	/**
	 * Current page index directory record
	 * @return current page index directory record
	 */
	public String getCurrentPageIndexDirectoryRecord() {
		return _strCurrentPageIndexDirectoryRecord;
	}
	
	/**
	 * Current page index directory record
	 * @param strCurrentPageIndexDirectoryRecord current page index directory record
	 */
	public void setCurrentPageIndexDirectoryRecord( String strCurrentPageIndexDirectoryRecord )
	{
		_strCurrentPageIndexDirectoryRecord = strCurrentPageIndexDirectoryRecord;
	}
	
	/**
	 * Items per page directory record
	 * @return items per page directory record
	 */
	public int getItemsPerPageDirectoryRecord(  )
	{
		return _nItemsPerPageDirectoryRecord;
	}
	
	/**
	 * Items per page directory record
	 * @param nItemsPerPageDirectoryRecord items per page directory record
	 */
	public void setItemsPerPageDirectoryRecord( int nItemsPerPageDirectoryRecord )
	{
		_nItemsPerPageDirectoryRecord = nItemsPerPageDirectoryRecord;
	}
	
	/**
	 * Items per page
	 * @return items per page
	 */
	public int getItemsPerPage(  )
	{
		return _nItemsPerPage;
	}
	
	/**
	 * Items per page
	 * @param nItemsPerPage items per page
	 */
	public void setItemsPerPage( int nItemsPerPage )
	{
		_nItemsPerPage = nItemsPerPage;
	}
	
	/**
	 * Id active
	 * @return id active
	 */
	public int getIdActive(  )
	{
		return _nIdActive;
	}
	
	/**
	 * Id active
	 * @param nIdActive id active
	 */
	public void setIdActive( int nIdActive )
	{
		_nIdActive = nIdActive;
	}
	
	/**
	 * Workgroup
	 * @return workgroup
	 */
	public String getWorkGroup(  )
	{
		return _strWorkGroup;
	}
	
	/**
	 * Workgroup
	 * @param strWorkGroup workgroup
	 */
	public void setWorkGroup( String strWorkGroup)
	{
		_strWorkGroup = strWorkGroup;
	}
	
	/**
	 * Id directory
	 * @return id directory
	 */
	public int getIdDirectory(  )
	{
		return _nIdDirectory;
	}
	
	/**
	 * Id directory
	 * @param nIdDirectory id directory
	 */
	public void setIdDirectory( int nIdDirectory )
	{
		_nIdDirectory = nIdDirectory;
	}
	
	/**
	 * Id entry
	 * @return id entry
	 */
	public int getIdEntry(  )
	{
		return _nIdEntry;
	}
	
	/**
	 * Id entry
	 * @param nIdEntry id entry
	 */
	public void setIdEntry( int nIdEntry )
	{
		_nIdEntry = nIdEntry;
	}
	
	/**
	 * Count line
	 * @return count line
	 */
	public int getCountLine(  )
	{
		return _nCountLine;
	}
	
	/**
	 * Count line
	 * @param nCountLine count line
	 */
	public void setCountLine( int nCountLine )
	{
		_nCountLine = nCountLine;
	}
	
	/**
	 * Count line failure
	 * @return count line failure
	 */
	public int getCountLineFailure(  )
	{
		return _nCountLineFailure;
	}
	
	/**
	 * Count line failure
	 * @param nCountLineFailure count line failure
	 */
	public void setCountLineFailure( int nCountLineFailure )
	{
		_nCountLineFailure = nCountLineFailure;
	}
	
	/**
	 * Id workfow state
	 * @return id workflow state
	 */
	public int get_nIdWorkflowSate(  )
	{
		return _nIdWorkflowSate;
	}
	
	/**
	 * Id workflow state
	 * @param nIdWorkflowSate id workflow state
	 */
	public void setIdWorkflowSate( int nIdWorkflowSate )
	{
		_nIdWorkflowSate = nIdWorkflowSate;
	}
	
	/**
	 * Error
	 * @return error
	 */
	public StringBuffer getError(  )
	{
		return _strError;
	}
	
	/**
	 * Error
	 * @param strError error
	 */
	public void setError( StringBuffer strError )
	{
		_strError = strError;
	}
	
	/**
	 * Map query
	 * @return map query
	 */
	public HashMap<String, List<RecordField>> getMapQuery(  )
	{
		return _mapQuery;
	}
	
	/**
	 * Map query
	 * @param mapQuery map query
	 */
	public void setMapQuery( HashMap<String, List<RecordField>> mapQuery )
	{
		_mapQuery = mapQuery;
	}
	
	/**
	 * Date creation begin record
	 * @return date creation begin record
	 */
	public Date getDateCreationBeginRecord(  )
	{
		return _dateCreationBeginRecord;
	}
	
	/**
	 * Date creation begin record
	 * @param dateCreationBeginRecord date creation begin record
	 */
	public void setDateCreationBeginRecord( Date dateCreationBeginRecord )
	{
		_dateCreationBeginRecord = dateCreationBeginRecord;
	}
	
	/**
	 * Date creation end record
	 * @return date creation end record
	 */
	public Date getDateCreationEndRecord(  )
	{
		return _dateCreationEndRecord;
	}
	
	/**
	 * Date creation end record
	 * @param dateCreationEndRecord date creation end record
	 */
	public void setDateCreationEndRecord( Date dateCreationEndRecord )
	{
		_dateCreationEndRecord = dateCreationEndRecord;
	}
	
	/**
	 * Date creation record
	 * @return date creation record
	 */
	public Date getDateCreationRecord(  )
	{
		return _dateCreationRecord;
	}
	
	/**
	 * Date creation record
	 * @param dateCreationRecord date creation record
	 */
	public void setDateCreationRecord( Date dateCreationRecord )
	{
		_dateCreationRecord = dateCreationRecord;
	}

	/**
	 * Set the list of ids result record
	 * @param listIdsResultRecord the list of id result record
	 */
	public void setListIdsResultRecord( List<Integer> listIdsResultRecord )
	{
		_listIdsResultRecord = listIdsResultRecord;
	}

	/**
	 * Get the list of ids result record
	 * @return the list of ids result record
	 */
	public List<Integer> getListIdsResultRecord(  )
	{
		return _listIdsResultRecord;
	}

	/**
	 * Set the item navigator for records
	 * @param nCurrentIdRecord the current id record
	 * @param strUrl the url
	 * @param strParameterName the parameter name
	 */
	public void setItemNavigatorViewRecords( int nCurrentIdRecord, String strUrl, String strParameterName )
	{
		if ( _itemNavigatorViewRecords == null  )
		{
			if ( _listIdsResultRecord != null && !_listIdsResultRecord.isEmpty(  ) )
			{
				List<String> listIds = new ArrayList<String>( _listIdsResultRecord.size(  ) );
				int nCurrentItemId = 0;
				int nIndex = 0;
				for ( int nIdRecord : _listIdsResultRecord )
				{
					listIds.add( Integer.toString( nIdRecord ) );
					if ( nIdRecord == nCurrentIdRecord )
					{
						nCurrentItemId = nIndex;
					}
					nIndex++;
				}
				_itemNavigatorViewRecords = new ItemNavigator( listIds, nCurrentItemId, strUrl, strParameterName );
			}
		}
		else
		{
			_itemNavigatorViewRecords.setCurrentItemId( Integer.toString( nCurrentIdRecord ) );
		}
	}

	/**
	 * Set the item navigator
	 * @param itemNavigator the item navigator
	 */
	public void setItemNavigatorViewRecords( ItemNavigator itemNavigator )
	{
		_itemNavigatorViewRecords = itemNavigator;
	}

	/**
	 * Get the item navigator
	 * @return
	 */
	public ItemNavigator getItemNavigatorViewRecords(  )
	{
		return _itemNavigatorViewRecords;
	}

	/**
	 * Set the item navigator for records
	 * @param nCurrentIdRecord the current id record
	 * @param strUrl the url
	 * @param strParameterName the parameter name
	 */
	public void setItemNavigatorHistory( int nCurrentIdRecord, String strUrl, String strParameterName )
	{
		if ( _itemNavigatorHistory == null  )
		{
			if ( _listIdsResultRecord != null && !_listIdsResultRecord.isEmpty(  ) )
			{
				List<String> listIds = new ArrayList<String>( _listIdsResultRecord.size(  ) );
				int nCurrentItemId = 0;
				int nIndex = 0;
				for ( int nIdRecord : _listIdsResultRecord )
				{
					listIds.add( Integer.toString( nIdRecord ) );
					if ( nIdRecord == nCurrentIdRecord )
					{
						nCurrentItemId = nIndex;
					}
					nIndex++;
				}
				_itemNavigatorHistory = new ItemNavigator( listIds, nCurrentItemId, strUrl, strParameterName );
			}
		}
		else
		{
			_itemNavigatorHistory.setCurrentItemId( Integer.toString( nCurrentIdRecord ) );
		}
	}

	/**
	 * Set the item navigator
	 * @param itemNavigator the item navigator
	 */
	public void setItemNavigatorHistory( ItemNavigator itemNavigator )
	{
		_itemNavigatorHistory = itemNavigator;
	}

	/**
	 * Get the item navigator
	 * @return
	 */
	public ItemNavigator getItemNavigatorHistory(  )
	{
		return _itemNavigatorHistory;
	}

	/**
	 * Set the redirect url
	 * @param _strRedirectUrl
	 */
	public void setRedirectUrl( HttpServletRequest request )
	{
		String strNextUrl = request.getRequestURI(  );
        UrlItem url = new UrlItem( strNextUrl );
        Enumeration enumParams = request.getParameterNames(  );

        while ( enumParams.hasMoreElements(  ) )
        {
            String strParamName = (String) enumParams.nextElement(  );
            url.addParameter( strParamName, request.getParameter( strParamName ) );
        }
        
        _strRedirectUrl = url.getUrl(  );
	}

	/**
	 * Get the redirect url
	 * @return the redirect url
	 */
	public String getRedirectUrl(  )
	{
		return _strRedirectUrl;
	}

	/**
	 * Set the sort entry
	 * @param sortEntry the sort entry
	 */
	public void setSortEntry( IEntry sortEntry )
	{
		_sortEntry = sortEntry;
	}

	/**
	 * Get the sort entry
	 * @return the sort entry
	 */
	public IEntry getSortEntry(  )
	{
		return _sortEntry;
	}

	/**
	 * Set the sort order
	 * @param nSortOrder the sort order
	 */
	public void setSortOrder( int nSortOrder )
	{
		_nSortOrder = nSortOrder;
	}

	/**
	 * Get the sort order
	 * @return the sort order
	 */
	public int getSortOrder(  )
	{
		return _nSortOrder;
	}
}

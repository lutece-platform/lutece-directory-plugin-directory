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

import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupService;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.sql.DAOUtil;

import org.apache.commons.lang.StringUtils;

import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * This class provides Data Access methods for record objects
 */
public final class RecordDAO implements IRecordDAO
{
    // Constants
    private static final String SQL_QUERY_NEW_PK = "SELECT MAX( id_record ) FROM directory_record";
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = "SELECT id_record,date_creation,id_directory,is_enabled,role_key,workgroup_key,date_modification " +
        "FROM directory_record WHERE id_record=? ";
    private static final String SQL_QUERY_FIND_BY_LIST_PRIMARY_KEY = "SELECT id_record,date_creation,id_directory,is_enabled,role_key,workgroup_key,date_modification " +
        "FROM directory_record WHERE id_record IN ( ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO directory_record ( " +
        "id_record,date_creation,id_directory,is_enabled,role_key,workgroup_key,date_modification ) VALUES(?,?,?,?,?,?,?)";
    private static final String SQL_QUERY_DELETE = "DELETE FROM directory_record WHERE id_record = ? ";
    private static final String SQL_QUERY_DELETE_BY_ID_DIRECTORY = "DELETE FROM directory_record WHERE id_directory = ?";
    private static final String SQL_QUERY_UPDATE = "UPDATE directory_record SET " +
        "id_record=?,date_creation=?,id_directory=?,is_enabled=?,role_key=?,workgroup_key=?,date_modification=? WHERE id_record=?";
    private static final String SQL_QUERY_SELECT_RECORD_BY_FILTER = "SELECT dr.id_record,dr.date_creation,dr.id_directory,dr.is_enabled,dr.role_key,dr.workgroup_key,dr.date_modification " +
        "FROM directory_record dr ";
    private static final String SQL_QUERY_SELECT_RECORD_ID_BY_FILTER = "SELECT dr.id_record FROM directory_record dr ";
    private static final String SQL_QUERY_SELECT_COUNT_BY_FILTER = "SELECT COUNT(dr.id_record) " +
        "FROM directory_record dr  ";
    private static final String SQL_QUERY_SELECT_DIRECTORY_ID = "SELECT id_directory FROM directory_record WHERE id_record=?";
    private static final String SQL_QUERY_SELECT_COUNT_DIRECYTORY_RECORD_HAS_WORKFLOW = "SELECT COUNT(*) FROM directory_record WHERE id_directory = ? AND workgroup_key IS NOT NULL";
    private static final String SQL_FILTER_ID_DIRECTORY = "	 dr.id_directory = ? ";
    private static final String SQL_FILTER_IS_ENABLED = " dr.is_enabled = ? ";
    private static final String SQL_FILTER_OR = " OR ";
    private static final String SQL_FILTER_OPEN_PARENTHESIS = " ( ";
    private static final String SQL_FILTER_CLOSE_PARENTHESIS = " ) ";
    private static final String SQL_FILTER_WORKGROUP_LIST = " dr.workgroup_key IN ( ? ";
    private static final String SQL_FILTER_ROLE_LIST = " dr.role_key IN ( ? ";
    private static final String SQL_ADITIONAL_PARAMETER = ",?";
    private static final String SQL_FILTER_WORKGROUP_IS_NULL = " dr.workgroup_key IS NULL ";
    private static final String SQL_FILTER_ROLE_IS_NULL = " dr.role_key IS NULL ";
    private static final String SQL_ORDER_BY_DEFAULT = " ORDER BY dr.date_creation ";
    private static final String SQL_ORDER_BY_DATE_MODIFICATION = " ORDER BY dr.date_modification ";
    private static final String SQL_ORDER_ASC = " ASC";
    private static final String SQL_ORDER_DESC = " DESC ";
    private static final String SQL_ORDER_BY_DEFAULT_ASC = SQL_ORDER_BY_DEFAULT + SQL_ORDER_ASC;

    /**
     * Generates a new primary key
     *
     * @param plugin the plugin
     * @return The new primary key
     */
    private int newPrimaryKey( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_PK, plugin );
        daoUtil.executeQuery(  );

        int nKey;

        if ( !daoUtil.next(  ) )
        {
            // if the table is empty
            nKey = 1;
        }

        nKey = daoUtil.getInt( 1 ) + 1;
        daoUtil.free(  );

        return nKey;
    }

    /**
     * Insert a new record in the table.
     * @param record instance of the record  object to insert
     * @param plugin the plugin
     * @return the id of the record
     */
    public synchronized int insert( Record record, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );

        daoUtil.setTimestamp( 2, record.getDateCreation(  ) );
        daoUtil.setInt( 3, record.getDirectory(  ).getIdDirectory(  ) );
        daoUtil.setBoolean( 4, record.isEnabled(  ) );
        daoUtil.setString( 5, record.getRoleKey(  ) );
        daoUtil.setString( 6, record.getWorkgroup(  ) );
        daoUtil.setTimestamp( 7, record.getDateModification(  ) );

        record.setIdRecord( newPrimaryKey( plugin ) );
        daoUtil.setInt( 1, record.getIdRecord(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );

        return record.getIdRecord(  );
    }

    /**
     * Load the data of the record from the table
     *
     * @param nIdRecord The identifier of the record
     * @param plugin the plugin
     * @return the instance of the record
     */
    public Record load( int nIdRecord, Plugin plugin )
    {
        Record record = null;
        Directory directory;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, plugin );
        daoUtil.setInt( 1, nIdRecord );
        daoUtil.executeQuery(  );

        if ( daoUtil.next(  ) )
        {
            record = new Record(  );
            record.setIdRecord( daoUtil.getInt( 1 ) );
            record.setDateCreation( daoUtil.getTimestamp( 2 ) );
            directory = new Directory(  );
            directory.setIdDirectory( daoUtil.getInt( 3 ) );
            record.setDirectory( directory );
            record.setEnabled( daoUtil.getBoolean( 4 ) );
            record.setRoleKey( daoUtil.getString( 5 ) );
            record.setWorkgroup( daoUtil.getString( 6 ) );
            record.setDateModification( daoUtil.getTimestamp( 7 ) );
        }

        daoUtil.free(  );

        return record;
    }

    /**
     * Test if directory one or more directory record has a workflow
     * @param nIdDirectory The id of the directory
     * @param plugin the plugin
     * @return true if one or more record has a worflow
     */
    public Boolean direcytoryRecordListHasWorkflow( int nIdDirectory, Plugin plugin )
    {
        Boolean bResult = null;

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_COUNT_DIRECYTORY_RECORD_HAS_WORKFLOW, plugin );
        daoUtil.setInt( 1, nIdDirectory );

        daoUtil.executeQuery(  );

        if ( daoUtil.next(  ) )
        {
            int nNb = daoUtil.getInt( 1 );

            if ( nNb > 0 )
            {
                bResult = Boolean.TRUE;
            }
            else
            {
                bResult = Boolean.FALSE;
            }
        }

        daoUtil.free(  );

        return bResult;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IRecordDAO#loadList(java.util.List, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public List<Record> loadList( List<Integer> listId, Plugin plugin )
    {
        List<Record> lRecord;
        int nSize = listId.size(  );

        if ( nSize > 0 )
        {
            // array to keep order from listId
            // because we have no way to keep it with a query
            Record[] tabRecords = new Record[nSize];

            Directory directory;

            StringBuilder sb = new StringBuilder( SQL_QUERY_FIND_BY_LIST_PRIMARY_KEY );

            for ( int i = 1; i < nSize; i++ )
            {
                sb.append( SQL_ADITIONAL_PARAMETER );
            }

            sb.append( SQL_FILTER_CLOSE_PARENTHESIS );

            DAOUtil daoUtil = new DAOUtil( sb.toString(  ), plugin );

            for ( int i = 0; i < nSize; i++ )
            {
                daoUtil.setInt( 1 + i, listId.get( i ) );
            }

            daoUtil.executeQuery(  );

            while ( daoUtil.next(  ) )
            {
                Record record = new Record(  );
                record.setIdRecord( daoUtil.getInt( 1 ) );

                if ( daoUtil.getTimestamp( 2 ) == null )
                {
                    record.setDateCreation( new Timestamp( 0 ) );
                }
                else
                {
                    record.setDateCreation( daoUtil.getTimestamp( 2 ) );
                }

                directory = new Directory(  );
                directory.setIdDirectory( daoUtil.getInt( 3 ) );
                record.setDirectory( directory );
                record.setEnabled( daoUtil.getBoolean( 4 ) );
                record.setRoleKey( daoUtil.getString( 5 ) );
                record.setWorkgroup( daoUtil.getString( 6 ) );

                if ( daoUtil.getTimestamp( 7 ) == null )
                {
                    record.setDateModification( new Timestamp( 0 ) );
                }
                else
                {
                    record.setDateModification( daoUtil.getTimestamp( 7 ) );
                }

                // keep id order
                tabRecords[listId.indexOf( record.getIdRecord(  ) )] = record;
            }

            daoUtil.free(  );

            // get list from array
            lRecord = Arrays.asList( tabRecords );
        }
        else
        {
            lRecord = new ArrayList<Record>(  );
        }

        return lRecord;
    }

    /**
     *  Delete a record from the table
     *
     * @param nIdRecord The identifier of the record
     * @param plugin the plugin
     */
    public void delete( int nIdRecord, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nIdRecord );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IRecordDAO#deleteRecordByDirectoryId(java.lang.Integer, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public void deleteRecordByDirectoryId( Integer nDirectoryId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_BY_ID_DIRECTORY, plugin );
        daoUtil.setInt( 1, nDirectoryId );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Update the record in the table
     *
     * @param record instance of the record object to update
     * @param plugin the plugin
     */
    public void store( Record record, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );
        daoUtil.setInt( 1, record.getIdRecord(  ) );
        daoUtil.setTimestamp( 2, record.getDateCreation(  ) );
        daoUtil.setInt( 3, record.getDirectory(  ).getIdDirectory(  ) );
        daoUtil.setBoolean( 4, record.isEnabled(  ) );
        daoUtil.setString( 5, record.getRoleKey(  ) );
        daoUtil.setString( 6, record.getWorkgroup(  ) );
        daoUtil.setTimestamp( 7, record.getDateModification(  ) );

        daoUtil.setInt( 8, record.getIdRecord(  ) );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Load the data of all the record who verify the filter and returns them in a  list
     * @param filter the filter
     * @param plugin the plugin
     * @return  the list of record
     */
    public List<Record> selectListByFilter( RecordFieldFilter filter, Plugin plugin )
    {
        List<Record> recordList = new ArrayList<Record>(  );
        Record record;
        Directory directory;

        List<String> listStrFilter = buildFilterQueryHeader( filter );

        String strSQL = DirectoryUtils.buildRequetteWithFilter( SQL_QUERY_SELECT_RECORD_BY_FILTER, listStrFilter,
                SQL_ORDER_BY_DEFAULT_ASC );

        DAOUtil daoUtil = new DAOUtil( strSQL, plugin );

        daoUtil = buildFilterQueryFooter( daoUtil, filter, 1 );

        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            record = new Record(  );
            record.setIdRecord( daoUtil.getInt( 1 ) );
            record.setDateCreation( daoUtil.getTimestamp( 2 ) );
            directory = new Directory(  );
            directory.setIdDirectory( daoUtil.getInt( 3 ) );
            record.setDirectory( directory );
            record.setEnabled( daoUtil.getBoolean( 4 ) );
            record.setRoleKey( daoUtil.getString( 5 ) );
            record.setWorkgroup( daoUtil.getString( 6 ) );
            record.setDateModification( daoUtil.getTimestamp( 7 ) );

            recordList.add( record );
        }

        daoUtil.free(  );

        return recordList;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IRecordDAO#getDirectoryIdByRecordId(java.lang.Integer, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public Integer getDirectoryIdByRecordId( Integer nRecordId, Plugin plugin )
    {
        Integer nResult = null;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_DIRECTORY_ID, plugin );

        daoUtil.setInt( 1, nRecordId );
        daoUtil.executeQuery(  );

        if ( daoUtil.next(  ) )
        {
            nResult = daoUtil.getInt( 1 );
        }

        daoUtil.free(  );

        return nResult;
    }

    /**
     * Count record who verify the filter
     * @param filter the filter
     * @param plugin the plugin
     * @return  the number of record
     */
    public int selectCountByFilter( RecordFieldFilter filter, Plugin plugin )
    {
        int nIdCount = 0;
        List<String> listStrFilter = buildFilterQueryHeader( filter );

        String strSQL = DirectoryUtils.buildRequetteWithFilter( SQL_QUERY_SELECT_COUNT_BY_FILTER, listStrFilter, null );

        DAOUtil daoUtil = new DAOUtil( strSQL, plugin );

        daoUtil = buildFilterQueryFooter( daoUtil, filter, 1 );

        daoUtil.executeQuery(  );

        if ( daoUtil.next(  ) )
        {
            nIdCount = daoUtil.getInt( 1 );
        }

        daoUtil.free(  );

        return nIdCount;
    }

    /**
     * Load the data of all the record id who verify the filter and returns them in a  list
     * @param filter the filter
     * @param plugin the plugin
     * @return  the list of id record
     */
    public List<Integer> selectListIdByFilter( RecordFieldFilter filter, Plugin plugin )
    {
        List<Integer> recordList = new ArrayList<Integer>(  );
        List<String> listFilter = buildFilterQueryHeader( filter );

        StringBuilder sbSQL = new StringBuilder( SQL_QUERY_SELECT_RECORD_ID_BY_FILTER );

        // check if filter contains entry to sort the result with.
        if ( filter.containsSortEntry(  ) )
        {
            sbSQL.append( filter.getSortEntry(  ).getSQLJoin(  ) );
        }

        String strOrderBy = StringUtils.EMPTY;

        if ( filter.isOrderByDateModification(  ) )
        {
            strOrderBy = getOrderByQuery( filter, SQL_ORDER_BY_DATE_MODIFICATION );
        }
        else
        {
            strOrderBy = getOrderByQuery( filter, SQL_ORDER_BY_DEFAULT );
        }

        String strSQL = DirectoryUtils.buildQueryWithFilter( sbSQL, listFilter, strOrderBy );

        DAOUtil daoUtil = new DAOUtil( strSQL, plugin );

        daoUtil = buildFilterQueryFooter( daoUtil, filter, 1 );

        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            recordList.add( daoUtil.getInt( 1 ) );
        }

        daoUtil.free(  );

        return recordList;
    }

    /**
     * Builds an order by query using the given filter.
     * Uses the default order query if filter does not contains order data.
     * @param filter the filter
     * @param strDefaultOrderQuery the default order query
     * @return the order by query
     */
    private String getOrderByQuery( RecordFieldFilter filter, String strDefaultOrderQuery )
    {
        String strOrderBy;

        if ( filter.containsSortEntry(  ) )
        {
            strOrderBy = filter.getSortEntry(  ).getSQLOrderBy(  );
        }
        else
        {
            // default sort is date_creation
            strOrderBy = strDefaultOrderQuery;
        }

        if ( filter.getSortOrder(  ) == RecordFieldFilter.ORDER_ASC )
        {
            strOrderBy += SQL_ORDER_ASC;
        }
        else
        {
            strOrderBy += SQL_ORDER_DESC;
        }

        return strOrderBy;
    }

    /**
     * Build beginning SQL query by given filter
     * @param filter the filter to apply
     * @return List of string to add to the SQL query
     */
    private List<String> buildFilterQueryHeader( RecordFieldFilter filter )
    {
        List<String> listStrFilter = new ArrayList<String>(  );

        if ( filter.containsIdDirectory(  ) )
        {
            listStrFilter.add( SQL_FILTER_ID_DIRECTORY );
        }

        if ( filter.containsIsDisabled(  ) )
        {
            listStrFilter.add( SQL_FILTER_IS_ENABLED );
        }

        if ( filter.containsWorkgroupKeyList(  ) )
        {
            StringBuffer sbWorkgroupFilter = new StringBuffer(  );

            boolean bContaintsAll = false;
            List<String> lWorkgroupKey = new ArrayList<String>(  );

            for ( ReferenceItem rWorkgroupKey : filter.getWorkgroupKeyList(  ) )
            {
                //FIXME : Hack for workgroup filter
                if ( AdminWorkgroupService.ALL_GROUPS.equals( rWorkgroupKey.getCode(  ) ) )
                {
                    bContaintsAll = true;
                }
                else
                {
                    lWorkgroupKey.add( rWorkgroupKey.getCode(  ) );
                }
            }

            int nWorkgroupKeySize = lWorkgroupKey.size(  );

            if ( bContaintsAll )
            {
                if ( nWorkgroupKeySize > 0 )
                {
                    sbWorkgroupFilter.append( SQL_FILTER_OPEN_PARENTHESIS + SQL_FILTER_WORKGROUP_IS_NULL +
                        SQL_FILTER_OR );
                }
                else
                {
                    sbWorkgroupFilter.append( SQL_FILTER_WORKGROUP_IS_NULL );
                }
            }

            if ( nWorkgroupKeySize > 0 )
            {
                for ( int i = 0; i < nWorkgroupKeySize; i++ )
                {
                    if ( i < 1 )
                    {
                        sbWorkgroupFilter.append( SQL_FILTER_WORKGROUP_LIST );
                    }
                    else
                    {
                        sbWorkgroupFilter.append( SQL_ADITIONAL_PARAMETER );
                    }
                }

                sbWorkgroupFilter.append( SQL_FILTER_CLOSE_PARENTHESIS );
            }

            if ( bContaintsAll && ( nWorkgroupKeySize > 0 ) )
            {
                sbWorkgroupFilter.append( SQL_FILTER_CLOSE_PARENTHESIS );
            }

            listStrFilter.add( sbWorkgroupFilter.toString(  ) );
        }

        if ( filter.containsRoleKeyList(  ) )
        {
            List<String> lRoleKeyList = filter.getRoleKeyList(  );
            int nSize = lRoleKeyList.size(  );

            StringBuffer sbRoleKeyFilter = new StringBuffer(  );

            if ( filter.includeRoleNull(  ) )
            {
                if ( nSize > 0 )
                {
                    sbRoleKeyFilter.append( SQL_FILTER_OPEN_PARENTHESIS + SQL_FILTER_ROLE_IS_NULL + SQL_FILTER_OR );
                }
                else
                {
                    sbRoleKeyFilter.append( SQL_FILTER_ROLE_IS_NULL );
                }
            }

            if ( nSize > 0 )
            {
                for ( int i = 0; i < nSize; i++ )
                {
                    if ( i < 1 )
                    {
                        sbRoleKeyFilter.append( SQL_FILTER_ROLE_LIST );
                    }
                    else
                    {
                        sbRoleKeyFilter.append( SQL_ADITIONAL_PARAMETER );
                    }
                }

                sbRoleKeyFilter.append( SQL_FILTER_CLOSE_PARENTHESIS );
            }

            if ( filter.includeRoleNull(  ) && ( nSize > 0 ) )
            {
                sbRoleKeyFilter.append( SQL_FILTER_CLOSE_PARENTHESIS );
            }

            listStrFilter.add( sbRoleKeyFilter.toString(  ) );
        }

        return listStrFilter;
    }

    /**
     * Build ending SQL query by given filter
     * @param filter the filter to apply
     * @param nIndex
     * @return List of string to add to the SQL query
     */
    private DAOUtil buildFilterQueryFooter( DAOUtil daoUtil, RecordFieldFilter filter, int nIndex )
    {
        DAOUtil result = daoUtil;

        // sort parameters
        if ( filter.containsSortEntry(  ) )
        {
            for ( Object oValue : filter.getSortEntry(  ).getSQLParametersValues(  ) )
            {
                // try to use setInt if possible, use setString otherwise.
                if ( oValue instanceof Integer )
                {
                    result.setInt( nIndex++, (Integer) oValue );
                }
                else
                {
                    result.setString( nIndex++, oValue.toString(  ) );
                }
            }
        }

        if ( filter.containsIdDirectory(  ) )
        {
            result.setInt( nIndex, filter.getIdDirectory(  ) );
            nIndex++;
        }

        if ( filter.containsIsDisabled(  ) )
        {
            result.setInt( nIndex, filter.getIsDisabled(  ) );
            nIndex++;
        }

        if ( filter.containsWorkgroupKeyList(  ) )
        {
            for ( ReferenceItem rWorkgroupKey : filter.getWorkgroupKeyList(  ) )
            {
                //FIXME : Hack for workgroup filter
                if ( !AdminWorkgroupService.ALL_GROUPS.equals( rWorkgroupKey.getCode(  ) ) )
                {
                    result.setString( nIndex, rWorkgroupKey.getCode(  ) );
                    nIndex++;
                }
            }
        }

        if ( filter.containsRoleKeyList(  ) )
        {
            for ( String strRole : filter.getRoleKeyList(  ) )
            {
                result.setString( nIndex, strRole );
                nIndex++;
            }
        }

        return result;
    }
}

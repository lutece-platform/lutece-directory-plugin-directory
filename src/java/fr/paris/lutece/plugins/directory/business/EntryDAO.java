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
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * This class provides Data Access methods for Entry objects
 */
public final class EntryDAO implements IEntryDAO
{
    // Constants
    private static final String SQL_QUERY_NEW_PK = "SELECT MAX( id_entry ) FROM directory_entry";
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = "SELECT ent.id_type,typ.title_key,typ.is_group," +
        "typ.is_comment,typ.is_mylutece_user,typ.class_name,ent.id_entry,ent.id_directory,directory.title,ent.id_entry_parent,ent.title," +
        "ent.help_message,ent.help_message_search,ent.entry_comment,ent.is_mandatory,ent.is_indexed,ent.is_indexed_as_title,ent.is_indexed_as_summary," +
        "ent.is_shown_in_search,ent.is_shown_in_result_list,ent.is_shown_in_result_record,ent.is_fields_in_line,ent.entry_position," +
        "ent.display_width,ent.display_height,ent.is_role_associated,ent.is_workgroup_associated,ent.is_multiple_search_fields,ent.is_shown_in_history,ent.id_entry_associate,ent.request_sql,ent.is_add_value_search_all,ent.label_value_search_all,ent.map_provider,ent.is_autocomplete_entry,ent.is_shown_in_export,ent.is_shown_in_completeness, ent.num_row, ent.num_column " +
        "FROM directory_entry ent,directory_entry_type typ,directory_directory directory WHERE ent.id_entry = ? and ent.id_type=typ.id_type and " +
        "ent.id_directory=directory.id_directory";
    private static final String SQL_QUERY_INSERT = "INSERT INTO directory_entry ( " +
        "id_entry,id_entry_parent,id_directory,id_type,title,help_message,help_message_search,entry_comment,is_mandatory," +
        "is_indexed,is_indexed_as_title,is_indexed_as_summary,is_shown_in_search,is_shown_in_result_list,is_shown_in_result_record,is_fields_in_line,entry_position,display_width,display_height " +
        ",is_role_associated,is_workgroup_associated,is_multiple_search_fields,is_shown_in_history,id_entry_associate,request_sql,is_add_value_search_all,label_value_search_all,map_provider,is_autocomplete_entry,is_shown_in_export,is_shown_in_completeness, num_row, num_column )VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String SQL_QUERY_DELETE = "DELETE FROM directory_entry WHERE id_entry = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE  directory_entry SET " +
        "id_entry=?,id_entry_parent=?,id_directory=?,id_type=?,title=?,help_message=?,help_message_search=?," +
        "entry_comment=?,is_mandatory=?,is_indexed=?,is_indexed_as_title=?,is_indexed_as_summary=?,is_shown_in_search=?,is_shown_in_result_list=?," +
        "is_shown_in_result_record=?,is_fields_in_line=?,entry_position=? ,display_width=?,display_height=?," +
        "is_role_associated=?,is_workgroup_associated=?,is_multiple_search_fields=?,is_shown_in_history=?,id_entry_associate=?,request_sql=?,is_add_value_search_all=?,label_value_search_all=?, map_provider=?, is_autocomplete_entry=?, is_shown_in_export=?, is_shown_in_completeness=?, num_row = ?, num_column = ? WHERE id_entry=?";
    private static final String SQL_QUERY_SELECT_ENTRY_BY_FILTER = "SELECT ent.id_type,typ.title_key,typ.is_group," +
        "typ.is_comment,typ.is_mylutece_user,typ.class_name,ent.id_entry,ent.id_directory," +
        "ent.id_entry_parent,ent.title,ent.help_message,ent.help_message_search," +
        "ent.entry_comment,ent.is_mandatory,ent.is_indexed,ent.is_indexed_as_title,ent.is_indexed_as_summary,ent.is_shown_in_search,ent.is_shown_in_result_list,ent.is_shown_in_result_record," +
        "ent.is_fields_in_line,ent.entry_position,ent.display_width,ent.display_height,ent.is_role_associated,ent.is_workgroup_associated, " +
        "ent.is_multiple_search_fields,ent.is_shown_in_history,ent.id_entry_associate ,ent.request_sql,ent.is_add_value_search_all,ent.label_value_search_all,ent.map_provider,ent.is_autocomplete_entry,ent.is_shown_in_export,ent.is_shown_in_completeness " +
        "FROM directory_entry ent,directory_entry_type typ  ";
    private static final String SQL_QUERY_SELECT_ENTRY_ANONYMIZE = "SELECT ent.id_entry, ent.title, ent.anonymize, det.class_name, det.title_key FROM directory_entry ent INNER JOIN directory_entry_type det ON (ent.id_type=det.id_type) ";
    private static final String SQL_QUERY_UPDATE_ENTRY_ANONYMIZE = "UPDATE directory_entry SET anonymize = ? WHERE id_entry = ?";
    private static final String SQL_QUERY_SELECT_NUMBER_ENTRY_BY_FILTER = "SELECT COUNT(ent.id_entry) " +
        "FROM directory_entry ent,directory_entry_type typ ";
    private static final String SQL_QUERY_NEW_POSITION = "SELECT MAX(entry_position) " +
        "FROM directory_entry WHERE id_directory = ? ";
    private static final String SQL_FILTER_ID_DIRECTORY = "  ent.id_directory = ? ";
    private static final String SQL_FILTER_POSITION = "  ent.entry_position = ? ";
    private static final String SQL_FILTER_ID_PARENT = " ent.id_entry_parent = ? ";
    private static final String SQL_FILTER_ID_PARENT_IS_NULL = "  ent.id_entry_parent IS NULL ";
    private static final String SQL_FILTER_IS_GROUP = "  typ.is_group = ? ";
    private static final String SQL_FILTER_IS_COMMENT = " typ.is_comment = ? ";
    private static final String SQL_FILTER_IS_MYLUTECE_USER = " typ.is_mylutece_user = ? ";
    private static final String SQL_FILTER_IS_SHOWN_IN_RESULT_LIST = "  ent.is_shown_in_result_list=?";
    private static final String SQL_FILTER_IS_SHOWN_IN_RESULT_RECORD = "  ent.is_shown_in_result_record=?";
    private static final String SQL_FILTER_IS_SHOWN_IN_HISTORY = "  ent.is_shown_in_history=?";
    private static final String SQL_FILTER_IS_INDEXED = "  ent.is_indexed=?";
    private static final String SQL_FILTER_IS_INDEXED_AS_TITLE = "  ent.is_indexed_as_title=?";
    private static final String SQL_FILTER_IS_INDEXED_AS_SUMMARY = "  ent.is_indexed_as_summary=?";
    private static final String SQL_FILTER_IS_ROLE_ASSOCIATED = "  ent.is_role_associated=?";
    private static final String SQL_FILTER_IS_WORKGROUP_ASSOCIATED = "  ent.is_workgroup_associated=?";
    private static final String SQL_FILTER_ASSOCIATION_ON_ID_TYPE = " ent.id_type=typ.id_type";
    private static final String SQL_ORDER_BY_POSITION = " ORDER BY ent.entry_position ";
    private static final String SQL_FILTER_ID_TYPE = "  typ.id_type = ? ";
    private static final String SQL_FILTER_ID_ENTRY_ASSOCIATE = "  ent.id_entry_associate = ? ";
    private static final String SQL_FILTER_IS_AUTOCOMPLETE_ENTRY = "  ent.is_autocomplete_entry = ? ";
    private static final String SQL_FILTER_IS_SHOWN_IN_EXPORT = " ent.is_shown_in_export = ? ";
    private static final String SQL_FILTER_IS_SHOWN_IN_COMPLETENESS = " ent.is_shown_in_completeness = ? ";
    private static final String CONSTANT_PARENTHESIS_LEFT = " ( ";
    private static final String CONSTANT_PARENTHESIS_RIGHT = " ) ";
    private static final String SQL_QUERY_SELECT_ENTRIES_WITHOUT_PARENT = "SELECT ent.id_type,typ.title_key,typ.is_group," +
        "typ.is_comment,typ.is_mylutece_user,typ.class_name,ent.id_entry,ent.id_directory," +
        "ent.id_entry_parent,ent.title,ent.help_message,ent.help_message_search," +
        "ent.entry_comment,ent.is_mandatory,ent.is_indexed,ent.is_indexed_as_title,ent.is_indexed_as_summary,ent.is_shown_in_search,ent.is_shown_in_result_list,ent.is_shown_in_result_record," +
        "ent.is_fields_in_line,ent.entry_position,ent.display_width,ent.display_height,ent.is_role_associated,ent.is_workgroup_associated, " +
        "ent.is_multiple_search_fields,ent.is_shown_in_history,ent.id_entry_associate ,ent.request_sql,ent.is_add_value_search_all,ent.label_value_search_all,ent.map_provider,ent.is_autocomplete_entry,ent.is_shown_in_export,ent.is_shown_in_completeness " +
        "FROM directory_entry ent,directory_entry_type typ WHERE ent.id_type = typ.id_type AND id_entry_parent IS NULL AND id_directory=? ORDER BY ent.entry_position ";

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
     * Generates a new entry position
     * @param plugin the plugin
     * @param nIdDirectory the id of the directory
     * @return the new entry position
     */
    private int newPosition( int nIdDirectory, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_POSITION, plugin );
        daoUtil.setInt( 1, nIdDirectory );
        daoUtil.executeQuery(  );

        int nPos;

        if ( !daoUtil.next(  ) )
        {
            // if the table is empty
            nPos = 1;
        }

        nPos = daoUtil.getInt( 1 ) + 1;
        daoUtil.free(  );

        return nPos;
    }

    /**
     * Insert a new record in the table.
     *
     * @param entry instance of the Entry object to insert
     * @param plugin the plugin
     * @return the id of the new entry
     */
    public synchronized int insert( IEntry entry, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );

        if ( entry.getParent(  ) != null )
        {
            daoUtil.setInt( 2, entry.getParent(  ).getIdEntry(  ) );
        }
        else
        {
            daoUtil.setIntNull( 2 );
        }

        daoUtil.setInt( 3, entry.getDirectory(  ).getIdDirectory(  ) );
        daoUtil.setInt( 4, entry.getEntryType(  ).getIdType(  ) );
        daoUtil.setString( 5, entry.getTitle(  ) );
        daoUtil.setString( 6, entry.getHelpMessage(  ) );
        daoUtil.setString( 7, entry.getHelpMessageSearch(  ) );
        daoUtil.setString( 8, entry.getComment(  ) );
        daoUtil.setBoolean( 9, entry.isMandatory(  ) );
        daoUtil.setBoolean( 10, entry.isIndexed(  ) );
        daoUtil.setBoolean( 11, entry.isIndexedAsTitle(  ) );
        daoUtil.setBoolean( 12, entry.isIndexedAsSummary(  ) );
        daoUtil.setBoolean( 13, entry.isShownInAdvancedSearch(  ) );
        daoUtil.setBoolean( 14, entry.isShownInResultList(  ) );
        daoUtil.setBoolean( 15, entry.isShownInResultRecord(  ) );
        daoUtil.setBoolean( 16, entry.isFieldInLine(  ) );
        daoUtil.setInt( 17, newPosition( entry.getDirectory(  ).getIdDirectory(  ), plugin ) );
        daoUtil.setInt( 18, entry.getDisplayWidth(  ) );
        daoUtil.setInt( 19, entry.getDisplayHeight(  ) );
        daoUtil.setBoolean( 20, entry.isRoleAssociated(  ) );
        daoUtil.setBoolean( 21, entry.isWorkgroupAssociated(  ) );
        daoUtil.setBoolean( 22, entry.isMultipleSearchFields(  ) );
        daoUtil.setBoolean( 23, entry.isShownInHistory(  ) );
        daoUtil.setInt( 24, entry.getEntryAssociate(  ) );
        daoUtil.setString( 25, entry.getRequestSQL(  ) );
        daoUtil.setBoolean( 26, entry.isAddValueAllSearch(  ) );
        daoUtil.setString( 27, entry.getLabelValueAllSearch(  ) );

        // map provider
        String strMapProvider = ( entry.getMapProvider(  ) == null ) ? DirectoryUtils.EMPTY_STRING
                                                                     : entry.getMapProvider(  ).getKey(  );
        daoUtil.setString( 28, strMapProvider );
        daoUtil.setBoolean( 29, entry.isAutocompleteEntry(  ) );
        daoUtil.setBoolean( 30, entry.isShownInExport(  ) );
        daoUtil.setBoolean( 31, entry.isShownInCompleteness(  ) );
        daoUtil.setInt( 32, entry.getNumberRow(  ) );
        daoUtil.setInt( 33, entry.getNumberColumn(  ) );

        entry.setIdEntry( newPrimaryKey( plugin ) );
        daoUtil.setInt( 1, entry.getIdEntry(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );

        return entry.getIdEntry(  );
    }

    /**
     * Load the data of the entry from the table
     *
     * @param nId The identifier of the entry
     * @param plugin the plugin
     * @return the instance of the Entry
     */
    public IEntry load( int nId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, plugin );
        daoUtil.setInt( 1, nId );
        daoUtil.executeQuery(  );

        boolean bException = false;
        IEntry entry = null;
        EntryType entryType = null;
        IEntry entryParent = null;
        Directory directory = null;

        if ( daoUtil.next(  ) )
        {
            entryType = new EntryType(  );
            entryType.setIdType( daoUtil.getInt( 1 ) );
            entryType.setTitleI18nKey( daoUtil.getString( 2 ) );
            entryType.setGroup( daoUtil.getBoolean( 3 ) );
            entryType.setComment( daoUtil.getBoolean( 4 ) );
            entryType.setMyLuteceUser( daoUtil.getBoolean( 5 ) );
            entryType.setClassName( daoUtil.getString( 6 ) );

            try
            {
                entry = (IEntry) Class.forName( entryType.getClassName(  ) ).newInstance(  );
            }
            catch ( ClassNotFoundException e )
            {
                //  class doesn't exist
                AppLogService.error( e );
                bException = true;
            }
            catch ( InstantiationException e )
            {
                // Class is abstract or is an  interface or haven't accessible builder
                AppLogService.error( e );
                bException = true;
            }
            catch ( IllegalAccessException e )
            {
                // can't access to rhe class
                AppLogService.error( e );
                bException = true;
            }

            if ( bException )
            {
                daoUtil.free(  );

                return null;
            }

            entry.setEntryType( entryType );
            entry.setIdEntry( daoUtil.getInt( 7 ) );
            // insert directory
            directory = new Directory(  );
            directory.setIdDirectory( daoUtil.getInt( 8 ) );
            directory.setTitle( daoUtil.getString( 9 ) );
            entry.setDirectory( directory );

            if ( daoUtil.getObject( 10 ) != null )
            {
                entryParent = new Entry(  );
                entryParent.setIdEntry( daoUtil.getInt( 10 ) );
                entry.setParent( entryParent );
            }

            entry.setTitle( daoUtil.getString( 11 ) );
            entry.setHelpMessage( daoUtil.getString( 12 ) );
            entry.setHelpMessageSearch( daoUtil.getString( 13 ) );
            entry.setComment( daoUtil.getString( 14 ) );
            entry.setMandatory( daoUtil.getBoolean( 15 ) );
            entry.setIndexed( daoUtil.getBoolean( 16 ) );
            entry.setIndexedAsTitle( daoUtil.getBoolean( 17 ) );
            entry.setIndexedAsSummary( daoUtil.getBoolean( 18 ) );
            entry.setShownInAdvancedSearch( daoUtil.getBoolean( 19 ) );
            entry.setShownInResultList( daoUtil.getBoolean( 20 ) );
            entry.setShownInResultRecord( daoUtil.getBoolean( 21 ) );
            entry.setFieldInLine( daoUtil.getBoolean( 22 ) );
            entry.setPosition( daoUtil.getInt( 23 ) );
            entry.setDisplayWidth( daoUtil.getInt( 24 ) );
            entry.setDisplayHeight( daoUtil.getInt( 25 ) );
            entry.setRoleAssociated( daoUtil.getBoolean( 26 ) );
            entry.setWorkgroupAssociated( daoUtil.getBoolean( 27 ) );
            entry.setMultipleSearchFields( daoUtil.getBoolean( 28 ) );
            entry.setShownInHistory( daoUtil.getBoolean( 29 ) );
            entry.setEntryAssociate( daoUtil.getInt( 30 ) );
            entry.setRequestSQL( daoUtil.getString( 31 ) );
            entry.setAddValueAllSearch( daoUtil.getBoolean( 32 ) );
            entry.setLabelValueAllSearch( daoUtil.getString( 33 ) );
            entry.setMapProvider( MapProviderManager.getMapProvider( daoUtil.getString( 34 ) ) );
            entry.setAutocompleteEntry( daoUtil.getBoolean( 35 ) );
            entry.setShownInExport( daoUtil.getBoolean( 36 ) );
            entry.setShownInCompleteness( daoUtil.getBoolean( 37 ) );
            entry.setNumberRow( daoUtil.getInt( 38 ) );
            entry.setNumberColumn( daoUtil.getInt( 39 ) );
        }

        daoUtil.free(  );

        return entry;
    }

    /**
     * Delete a record from the table
     *
     * @param nIdEntry The identifier of the entry
     * @param plugin the plugin
     */
    public void delete( int nIdEntry, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nIdEntry );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Update the entry in the table
     *
     * @param entry instance of the Entry object to update
     * @param plugin the plugin
     */
    public void store( IEntry entry, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );

        daoUtil.setInt( 1, entry.getIdEntry(  ) );

        if ( entry.getParent(  ) != null )
        {
            daoUtil.setInt( 2, entry.getParent(  ).getIdEntry(  ) );
        }
        else
        {
            daoUtil.setIntNull( 2 );
        }

        daoUtil.setInt( 3, entry.getDirectory(  ).getIdDirectory(  ) );
        daoUtil.setInt( 4, entry.getEntryType(  ).getIdType(  ) );
        daoUtil.setString( 5, entry.getTitle(  ) );
        daoUtil.setString( 6, entry.getHelpMessage(  ) );
        daoUtil.setString( 7, entry.getHelpMessageSearch(  ) );
        daoUtil.setString( 8, entry.getComment(  ) );
        daoUtil.setBoolean( 9, entry.isMandatory(  ) );
        daoUtil.setBoolean( 10, entry.isIndexed(  ) );
        daoUtil.setBoolean( 11, entry.isIndexedAsTitle(  ) );
        daoUtil.setBoolean( 12, entry.isIndexedAsSummary(  ) );
        daoUtil.setBoolean( 13, entry.isShownInAdvancedSearch(  ) );
        daoUtil.setBoolean( 14, entry.isShownInResultList(  ) );
        daoUtil.setBoolean( 15, entry.isShownInResultRecord(  ) );
        daoUtil.setBoolean( 16, entry.isFieldInLine(  ) );
        daoUtil.setInt( 17, entry.getPosition(  ) );
        daoUtil.setInt( 18, entry.getDisplayWidth(  ) );
        daoUtil.setInt( 19, entry.getDisplayHeight(  ) );
        daoUtil.setBoolean( 20, entry.isRoleAssociated(  ) );
        daoUtil.setBoolean( 21, entry.isWorkgroupAssociated(  ) );
        daoUtil.setBoolean( 22, entry.isMultipleSearchFields(  ) );
        daoUtil.setBoolean( 23, entry.isShownInHistory(  ) );
        daoUtil.setInt( 24, entry.getEntryAssociate(  ) );
        daoUtil.setString( 25, entry.getRequestSQL(  ) );
        daoUtil.setBoolean( 26, entry.isAddValueAllSearch(  ) );
        daoUtil.setString( 27, entry.getLabelValueAllSearch(  ) );

        // map provider
        String strMapProvider = ( entry.getMapProvider(  ) == null ) ? DirectoryUtils.EMPTY_STRING
                                                                     : entry.getMapProvider(  ).getKey(  );
        daoUtil.setString( 28, strMapProvider );

        daoUtil.setBoolean( 29, entry.isAutocompleteEntry(  ) );
        daoUtil.setBoolean( 30, entry.isShownInExport(  ) );
        daoUtil.setBoolean( 31, entry.isShownInCompleteness(  ) );
        daoUtil.setInt( 32, entry.getNumberRow(  ) );
        daoUtil.setInt( 33, entry.getNumberColumn(  ) );

        daoUtil.setInt( 34, entry.getIdEntry(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    private List<String> setSelectFilter( EntryFilter filter )
    {
        List<String> listStrFilter = new ArrayList<String>(  );

        listStrFilter.add( SQL_FILTER_ASSOCIATION_ON_ID_TYPE );

        if ( filter.containsPosition(  ) )
        {
            listStrFilter.add( SQL_FILTER_POSITION );
        }

        if ( filter.containsIdDirectory(  ) )
        {
            listStrFilter.add( SQL_FILTER_ID_DIRECTORY );
        }

        if ( filter.containsIdEntryParent(  ) )
        {
            listStrFilter.add( SQL_FILTER_ID_PARENT );
        }

        if ( filter.containsIsEntryParentNull(  ) )
        {
            listStrFilter.add( SQL_FILTER_ID_PARENT_IS_NULL );
        }

        if ( filter.containsIsGroup(  ) )
        {
            listStrFilter.add( SQL_FILTER_IS_GROUP );
        }

        if ( filter.containsIsShownInResultList(  ) )
        {
            listStrFilter.add( SQL_FILTER_IS_SHOWN_IN_RESULT_LIST );
        }

        if ( filter.containsIsShownInResultRecord(  ) )
        {
            listStrFilter.add( SQL_FILTER_IS_SHOWN_IN_RESULT_RECORD );
        }

        if ( filter.containsIsShownInHistory(  ) )
        {
            listStrFilter.add( SQL_FILTER_IS_SHOWN_IN_HISTORY );
        }

        if ( filter.containsIsIndexed(  ) )
        {
            listStrFilter.add( SQL_FILTER_IS_INDEXED );
        }

        if ( filter.containsIsIndexedAsTitle(  ) )
        {
            listStrFilter.add( SQL_FILTER_IS_INDEXED_AS_TITLE );
        }

        if ( filter.containsIsIndexedAsSummary(  ) )
        {
            listStrFilter.add( SQL_FILTER_IS_INDEXED_AS_SUMMARY );
        }

        if ( filter.containsIsComment(  ) )
        {
            listStrFilter.add( SQL_FILTER_IS_COMMENT );
        }

        if ( filter.containsIsMyLuteceUser(  ) )
        {
            listStrFilter.add( SQL_FILTER_IS_MYLUTECE_USER );
        }

        if ( filter.containsIsWorkgroupAssociated(  ) )
        {
            listStrFilter.add( SQL_FILTER_IS_WORKGROUP_ASSOCIATED );
        }

        if ( filter.containsIsRoleAssociated(  ) )
        {
            listStrFilter.add( SQL_FILTER_IS_ROLE_ASSOCIATED );
        }

        if ( filter.containsIdType(  ) )
        {
            listStrFilter.add( SQL_FILTER_ID_TYPE );
        }

        if ( filter.containsIdEntryAssociate(  ) )
        {
            listStrFilter.add( SQL_FILTER_ID_ENTRY_ASSOCIATE );
        }

        if ( filter.containsIsAutocompleteEntry(  ) )
        {
            listStrFilter.add( SQL_FILTER_IS_AUTOCOMPLETE_ENTRY );
        }

        if ( filter.containsIsShownInExport(  ) )
        {
            listStrFilter.add( SQL_FILTER_IS_SHOWN_IN_EXPORT );
        }

        if ( filter.containsIsShownInCompleteness(  ) )
        {
            listStrFilter.add( SQL_FILTER_IS_SHOWN_IN_COMPLETENESS );
        }

        return listStrFilter;
    }

    private int setSelectFilterValues( EntryFilter filter, DAOUtil daoUtil, int index )
    {
        int nIndex = index;

        if ( filter.containsPosition(  ) )
        {
            daoUtil.setInt( nIndex, filter.getPosition(  ) );
            nIndex++;
        }

        if ( filter.containsIdDirectory(  ) )
        {
            daoUtil.setInt( nIndex, filter.getIdDirectory(  ) );
            nIndex++;
        }

        if ( filter.containsIdEntryParent(  ) )
        {
            daoUtil.setInt( nIndex, filter.getIdEntryParent(  ) );
            nIndex++;
        }

        if ( filter.containsIsGroup(  ) )
        {
            daoUtil.setBoolean( nIndex, filter.getIsGroup(  ) == EntryFilter.FILTER_TRUE );
            nIndex++;
        }

        if ( filter.containsIsShownInResultList(  ) )
        {
            daoUtil.setBoolean( nIndex, filter.getIsShownInResultList(  ) == EntryFilter.FILTER_TRUE );
            nIndex++;
        }

        if ( filter.containsIsShownInResultRecord(  ) )
        {
            daoUtil.setBoolean( nIndex, filter.getIsShownInResultRecord(  ) == EntryFilter.FILTER_TRUE );
            nIndex++;
        }

        if ( filter.containsIsShownInHistory(  ) )
        {
            daoUtil.setBoolean( nIndex, filter.getIsShownInHistory(  ) == EntryFilter.FILTER_TRUE );
            nIndex++;
        }

        if ( filter.containsIsIndexed(  ) )
        {
            daoUtil.setBoolean( nIndex, filter.getIsIndexed(  ) == EntryFilter.FILTER_TRUE );
            nIndex++;
        }

        if ( filter.containsIsIndexedAsTitle(  ) )
        {
            daoUtil.setBoolean( nIndex, filter.getIsIndexedAsTitle(  ) == EntryFilter.FILTER_TRUE );
            nIndex++;
        }

        if ( filter.containsIsIndexedAsSummary(  ) )
        {
            daoUtil.setBoolean( nIndex, filter.getIsIndexedAsSummary(  ) == EntryFilter.FILTER_TRUE );
            nIndex++;
        }

        if ( filter.containsIsComment(  ) )
        {
            daoUtil.setBoolean( nIndex, filter.getIsComment(  ) == EntryFilter.FILTER_TRUE );
            nIndex++;
        }

        if ( filter.containsIsMyLuteceUser(  ) )
        {
            daoUtil.setBoolean( nIndex, filter.getIsMyLuteceUser(  ) == EntryFilter.FILTER_TRUE );
            nIndex++;
        }

        if ( filter.containsIsWorkgroupAssociated(  ) )
        {
            daoUtil.setBoolean( nIndex, filter.getIsWorkgroupAssociated(  ) == EntryFilter.FILTER_TRUE );
            nIndex++;
        }

        if ( filter.containsIsRoleAssociated(  ) )
        {
            daoUtil.setBoolean( nIndex, filter.getIsRoleAssociated(  ) == EntryFilter.FILTER_TRUE );
            nIndex++;
        }

        if ( filter.containsIdType(  ) )
        {
            daoUtil.setInt( nIndex, filter.getIdType(  ) );
            nIndex++;
        }

        if ( filter.containsIdEntryAssociate(  ) )
        {
            daoUtil.setInt( nIndex, filter.getIdEntryAssociate(  ) );
            nIndex++;
        }

        if ( filter.containsIsAutocompleteEntry(  ) )
        {
            daoUtil.setBoolean( nIndex, filter.getIsAutocompleteEntry(  ) == EntryFilter.FILTER_TRUE );
            nIndex++;
        }

        if ( filter.containsIsShownInExport(  ) )
        {
            daoUtil.setBoolean( nIndex, filter.getIsShownInExport(  ) == EntryFilter.FILTER_TRUE );
            nIndex++;
        }

        if ( filter.containsIsShownInCompleteness(  ) )
        {
            daoUtil.setBoolean( nIndex, filter.getIsShownInCompleteness(  ) == EntryFilter.FILTER_TRUE );
            nIndex++;
        }

        return nIndex;
    }

    private IEntry getValuesFromQuery( DAOUtil daoUtil )
    {
        EntryType entryType = null;
        IEntry entryParent = null;
        IEntry entry = null;
        Directory directory = null;
        boolean bException = false;

        entryType = new EntryType(  );
        entryType.setIdType( daoUtil.getInt( 1 ) );
        entryType.setTitleI18nKey( daoUtil.getString( 2 ) );
        entryType.setGroup( daoUtil.getBoolean( 3 ) );
        entryType.setComment( daoUtil.getBoolean( 4 ) );
        entryType.setMyLuteceUser( daoUtil.getBoolean( 5 ) );
        entryType.setClassName( daoUtil.getString( 6 ) );

        try
        {
            entry = (IEntry) Class.forName( entryType.getClassName(  ) ).newInstance(  );
        }
        catch ( ClassNotFoundException e )
        {
            //  class doesn't exist
            AppLogService.error( e );
            bException = true;
        }
        catch ( InstantiationException e )
        {
            // Class is abstract or is an  interface or haven't accessible builder
            AppLogService.error( e );
            bException = true;
        }
        catch ( IllegalAccessException e )
        {
            // can't access to rhe class
            AppLogService.error( e );
            bException = true;
        }

        if ( bException )
        {
            daoUtil.free(  );

            return null;
        }

        entry.setEntryType( entryType );
        entry.setIdEntry( daoUtil.getInt( 7 ) );
        // insert form
        directory = new Directory(  );
        directory.setIdDirectory( daoUtil.getInt( 8 ) );
        entry.setDirectory( directory );

        if ( daoUtil.getObject( 9 ) != null )
        {
            entryParent = new Entry(  );
            entryParent.setIdEntry( daoUtil.getInt( 9 ) );
            entry.setParent( entryParent );
        }

        entry.setTitle( daoUtil.getString( 10 ) );
        entry.setHelpMessage( daoUtil.getString( 11 ) );
        entry.setHelpMessageSearch( daoUtil.getString( 12 ) );
        entry.setComment( daoUtil.getString( 13 ) );
        entry.setMandatory( daoUtil.getBoolean( 14 ) );
        entry.setIndexed( daoUtil.getBoolean( 15 ) );
        entry.setIndexedAsTitle( daoUtil.getBoolean( 16 ) );
        entry.setIndexedAsSummary( daoUtil.getBoolean( 17 ) );
        entry.setShownInAdvancedSearch( daoUtil.getBoolean( 18 ) );
        entry.setShownInResultList( daoUtil.getBoolean( 19 ) );
        entry.setShownInResultRecord( daoUtil.getBoolean( 20 ) );
        entry.setFieldInLine( daoUtil.getBoolean( 21 ) );
        entry.setPosition( daoUtil.getInt( 22 ) );
        entry.setDisplayWidth( daoUtil.getInt( 23 ) );
        entry.setDisplayHeight( daoUtil.getInt( 24 ) );
        entry.setRoleAssociated( daoUtil.getBoolean( 25 ) );
        entry.setWorkgroupAssociated( daoUtil.getBoolean( 26 ) );
        entry.setMultipleSearchFields( daoUtil.getBoolean( 27 ) );
        entry.setShownInHistory( daoUtil.getBoolean( 28 ) );
        entry.setEntryAssociate( daoUtil.getInt( 29 ) );
        entry.setRequestSQL( daoUtil.getString( 30 ) );
        entry.setAddValueAllSearch( daoUtil.getBoolean( 31 ) );
        entry.setLabelValueAllSearch( daoUtil.getString( 32 ) );
        entry.setMapProvider( MapProviderManager.getMapProvider( daoUtil.getString( 33 ) ) );
        entry.setAutocompleteEntry( daoUtil.getBoolean( 34 ) );
        entry.setShownInExport( daoUtil.getBoolean( 35 ) );
        entry.setShownInCompleteness( daoUtil.getBoolean( 36 ) );

        return entry;
    }

    /**
     * Load the data of all the entry who verify the filter and returns them in
     * a list
     * @param filter the filter
     * @param plugin the plugin
     * @return the list of entry
     */
    public List<IEntry> selectEntryListByFilter( EntryFilter filter, Plugin plugin )
    {
        List<IEntry> entryList = new ArrayList<IEntry>(  );
        IEntry entry = null;

        List<String> listStrFilter = setSelectFilter( filter );

        String strSQL = DirectoryUtils.buildRequetteWithFilter( SQL_QUERY_SELECT_ENTRY_BY_FILTER, listStrFilter,
                SQL_ORDER_BY_POSITION );
        DAOUtil daoUtil = new DAOUtil( strSQL, plugin );
        int nIndex = 1;
        nIndex = setSelectFilterValues( filter, daoUtil, nIndex );

        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            entry = getValuesFromQuery( daoUtil );

            if ( entry == null )
            {
                return null;
            }

            entryList.add( entry );
        }

        daoUtil.free(  );

        return entryList;
    }

    /**
     * Return the number of entry who verify the filter
     * @param filter the filter
     * @param plugin the plugin
     * @return the number of entry who verify the filter
     */
    public int selectNumberEntryByFilter( EntryFilter filter, Plugin plugin )
    {
        int nNumberEntry = 0;
        List<String> listStrFilter = new ArrayList<String>(  );

        listStrFilter.add( SQL_FILTER_ASSOCIATION_ON_ID_TYPE );

        if ( filter.containsIdDirectory(  ) )
        {
            listStrFilter.add( SQL_FILTER_ID_DIRECTORY );
        }

        if ( filter.containsIdEntryParent(  ) )
        {
            listStrFilter.add( SQL_FILTER_ID_PARENT );
        }

        if ( filter.containsIsEntryParentNull(  ) )
        {
            listStrFilter.add( SQL_FILTER_ID_PARENT_IS_NULL );
        }

        if ( filter.containsIsGroup(  ) )
        {
            listStrFilter.add( SQL_FILTER_IS_GROUP );
        }

        if ( filter.containsIsShownInResultList(  ) )
        {
            listStrFilter.add( SQL_FILTER_IS_SHOWN_IN_RESULT_LIST );
        }

        if ( filter.containsIsIndexed(  ) )
        {
            listStrFilter.add( SQL_FILTER_IS_INDEXED );
        }

        if ( filter.containsIsIndexedAsTitle(  ) )
        {
            listStrFilter.add( SQL_FILTER_IS_INDEXED_AS_TITLE );
        }

        if ( filter.containsIsIndexedAsSummary(  ) )
        {
            listStrFilter.add( SQL_FILTER_IS_INDEXED_AS_SUMMARY );
        }

        if ( filter.containsIsComment(  ) )
        {
            listStrFilter.add( SQL_FILTER_IS_COMMENT );
        }

        if ( filter.containsIsMyLuteceUser(  ) )
        {
            listStrFilter.add( SQL_FILTER_IS_MYLUTECE_USER );
        }

        if ( filter.containsIsWorkgroupAssociated(  ) )
        {
            listStrFilter.add( SQL_FILTER_IS_WORKGROUP_ASSOCIATED );
        }

        if ( filter.containsIsRoleAssociated(  ) )
        {
            listStrFilter.add( SQL_FILTER_IS_ROLE_ASSOCIATED );
        }

        if ( filter.containsIdType(  ) )
        {
            listStrFilter.add( SQL_FILTER_ID_TYPE );
        }

        if ( filter.containsIdEntryAssociate(  ) )
        {
            listStrFilter.add( SQL_FILTER_ID_ENTRY_ASSOCIATE );
        }

        if ( filter.containsIsAutocompleteEntry(  ) )
        {
            listStrFilter.add( SQL_FILTER_IS_AUTOCOMPLETE_ENTRY );
        }

        if ( filter.containsIsShownInExport(  ) )
        {
            listStrFilter.add( SQL_FILTER_IS_SHOWN_IN_EXPORT );
        }

        if ( filter.containsIsShownInCompleteness(  ) )
        {
            listStrFilter.add( SQL_FILTER_IS_SHOWN_IN_COMPLETENESS );
        }

        String strSQL = DirectoryUtils.buildRequetteWithFilter( SQL_QUERY_SELECT_NUMBER_ENTRY_BY_FILTER, listStrFilter,
                null );

        DAOUtil daoUtil = new DAOUtil( strSQL, plugin );
        int nIndex = 1;

        if ( filter.containsIdDirectory(  ) )
        {
            daoUtil.setInt( nIndex, filter.getIdDirectory(  ) );
            nIndex++;
        }

        if ( filter.containsIdEntryParent(  ) )
        {
            daoUtil.setInt( nIndex, filter.getIdEntryParent(  ) );
            nIndex++;
        }

        if ( filter.containsIsGroup(  ) )
        {
            daoUtil.setBoolean( nIndex, filter.getIsGroup(  ) == EntryFilter.FILTER_TRUE );
            nIndex++;
        }

        if ( filter.containsIsShownInResultList(  ) )
        {
            daoUtil.setBoolean( nIndex, filter.getIsShownInResultList(  ) == EntryFilter.FILTER_TRUE );
            nIndex++;
        }

        if ( filter.containsIsIndexed(  ) )
        {
            daoUtil.setBoolean( nIndex, filter.getIsIndexed(  ) == EntryFilter.FILTER_TRUE );
            nIndex++;
        }

        if ( filter.containsIsIndexedAsTitle(  ) )
        {
            daoUtil.setBoolean( nIndex, filter.getIsIndexedAsTitle(  ) == EntryFilter.FILTER_TRUE );
            nIndex++;
        }

        if ( filter.containsIsIndexedAsSummary(  ) )
        {
            daoUtil.setBoolean( nIndex, filter.getIsIndexedAsSummary(  ) == EntryFilter.FILTER_TRUE );
            nIndex++;
        }

        if ( filter.containsIsComment(  ) )
        {
            daoUtil.setBoolean( nIndex, filter.getIsComment(  ) == EntryFilter.FILTER_TRUE );
            nIndex++;
        }

        if ( filter.containsIsMyLuteceUser(  ) )
        {
            daoUtil.setBoolean( nIndex, filter.getIsMyLuteceUser(  ) == EntryFilter.FILTER_TRUE );
            nIndex++;
        }

        if ( filter.containsIsWorkgroupAssociated(  ) )
        {
            daoUtil.setBoolean( nIndex, filter.getIsWorkgroupAssociated(  ) == EntryFilter.FILTER_TRUE );
            nIndex++;
        }

        if ( filter.containsIsRoleAssociated(  ) )
        {
            daoUtil.setBoolean( nIndex, filter.getIsRoleAssociated(  ) == EntryFilter.FILTER_TRUE );
            nIndex++;
        }

        if ( filter.containsIdType(  ) )
        {
            daoUtil.setInt( nIndex, filter.getIdType(  ) );
            nIndex++;
        }

        if ( filter.containsIdEntryAssociate(  ) )
        {
            daoUtil.setInt( nIndex, filter.getIdEntryAssociate(  ) );
            nIndex++;
        }

        if ( filter.containsIsAutocompleteEntry(  ) )
        {
            daoUtil.setBoolean( nIndex, filter.getIsAutocompleteEntry(  ) == EntryFilter.FILTER_TRUE );
            nIndex++;
        }

        if ( filter.containsIsShownInExport(  ) )
        {
            daoUtil.setBoolean( nIndex, filter.getIsShownInExport(  ) == EntryFilter.FILTER_TRUE );
            nIndex++;
        }

        if ( filter.containsIsShownInCompleteness(  ) )
        {
            daoUtil.setBoolean( nIndex, filter.getIsShownInCompleteness(  ) == EntryFilter.FILTER_TRUE );
            nIndex++;
        }

        daoUtil.executeQuery(  );

        if ( daoUtil.next(  ) )
        {
            nNumberEntry = daoUtil.getInt( 1 );
        }

        daoUtil.free(  );

        return nNumberEntry;
    }

    public List<IEntry> selectEntryListByTypeByFilter( List<Integer> idTypeEntryList, EntryFilter filter, Plugin plugin )
    {
        List<IEntry> entryList = new ArrayList<IEntry>(  );
        IEntry entry = null;

        List<String> listStrFilter = setSelectFilter( filter );

        String strSQL = DirectoryUtils.buildRequetteWithFilter( SQL_QUERY_SELECT_ENTRY_BY_FILTER, listStrFilter, null );

        StringBuffer strBuffer = new StringBuffer(  );
        strBuffer.append( strSQL );

        if ( ( idTypeEntryList != null ) && ( idTypeEntryList.size(  ) > 0 ) )
        {
            if ( listStrFilter.size(  ) > 0 )
            {
                strBuffer.append( DirectoryUtils.CONSTANT_AND );
                strBuffer.append( CONSTANT_PARENTHESIS_LEFT );
            }
            else
            {
                strBuffer.append( DirectoryUtils.CONSTANT_WHERE );
            }

            for ( int i = 0; i < idTypeEntryList.size(  ); i++ )
            {
                if ( i != 0 )
                {
                    strBuffer.append( DirectoryUtils.CONSTANT_OR );
                }

                strBuffer.append( SQL_FILTER_ID_TYPE );
            }

            strBuffer.append( CONSTANT_PARENTHESIS_RIGHT );
        }
        else
        {
            return entryList;
        }

        strSQL = strBuffer.toString(  );

        DAOUtil daoUtil = new DAOUtil( strSQL, plugin );
        int nIndex = 1;

        nIndex = setSelectFilterValues( filter, daoUtil, nIndex );

        for ( int idType : idTypeEntryList )
        {
            daoUtil.setInt( nIndex, idType );
            nIndex++;
        }

        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            entry = getValuesFromQuery( daoUtil );

            if ( entry == null )
            {
                return null;
            }

            entryList.add( entry );
        }

        daoUtil.free(  );

        return entryList;
    }

    /**
     * Get the list of entries whith their titles, their ids and their
     * anonymisation status. Also get the class name of the entry type.
     * @return A list of entries with their titles, their ids and their
     *         anonymisation status.
     */
    public List<IEntry> getEntryListAnonymizeStatus( Plugin plugin )
    {
        List<IEntry> entryList = new ArrayList<IEntry>(  );
        IEntry entry = null;

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_ENTRY_ANONYMIZE, plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            entry = new Entry(  );
            entry.setIdEntry( daoUtil.getInt( 1 ) );
            entry.setTitle( daoUtil.getString( 2 ) );
            entry.setAnonymize( daoUtil.getBoolean( 3 ) );

            EntryType entryType = new EntryType(  );
            entryType.setClassName( daoUtil.getString( 4 ) );
            entryType.setTitleI18nKey( daoUtil.getString( 5 ) );
            entry.setEntryType( entryType );
            entryList.add( entry );
        }

        daoUtil.free(  );

        return entryList;
    }

    /**
     * Update an entry anonymization status
     * @param nEntryId Id of the entry
     * @param plugin the plugin
     * @param bAnonymize True if the entry should be anonymize, false otherwise
     */
    public void updateEntryAnonymizeStatus( Integer nEntryId, Boolean bAnonymize, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE_ENTRY_ANONYMIZE, plugin );
        daoUtil.setBoolean( 1, bAnonymize );
        daoUtil.setInt( 2, nEntryId );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<IEntry> findEntriesWithoutParent( Plugin plugin, int nIdDirectory )
    {
        List<IEntry> listResult = new ArrayList<IEntry>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_ENTRIES_WITHOUT_PARENT );
        daoUtil.setInt( 1, nIdDirectory );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            IEntry entry = getValuesFromQuery( daoUtil );

            if ( entry == null )
            {
                return null;
            }

            listResult.add( entry );
        }

        daoUtil.free(  );

        return listResult;
    }
}

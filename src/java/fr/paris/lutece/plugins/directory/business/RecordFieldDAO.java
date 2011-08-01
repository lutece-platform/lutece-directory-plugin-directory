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

import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * This class provides Data Access methods for Response objects
 */
public final class RecordFieldDAO implements IRecordFieldDAO
{
    // Constants
    private static final String SQL_QUERY_NEW_PK = "SELECT MAX( id_record_field ) FROM directory_record_field";
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = "SELECT " +
        "drf.id_record_field,drf.id_record,drf.record_field_value,type.class_name,ent.id_entry,ent.title,ent.display_width,ent.display_height, " +
        "drf.id_field,drf.id_file FROM directory_record_field drf,directory_entry ent,directory_entry_type type  " +
        "WHERE drf.id_record_field=? and drf.id_entry =ent.id_entry and ent.id_type=type.id_type ";
    private static final String SQL_QUERY_INSERT = "INSERT INTO directory_record_field( " +
        "id_record_field,id_record,record_field_value,id_entry,id_field,id_file) VALUES(?,?,?,?,?,?)";
    private static final String SQL_QUERY_DELETE = "DELETE FROM directory_record_field WHERE id_record_field = ? ";
    private static final String SQL_QUERY_DELETE_BY_LIST_RECORD_ID = "DELETE FROM directory_record_field WHERE id_record IN ( ?";
    private static final String SQL_QUERY_UPDATE = "UPDATE  directory_record_field SET " +
        "id_record_field=?,id_record=?,record_field_value=?,id_entry=?,id_field=?,id_file=? WHERE id_record_field=?";
    private static final String SQL_QUERY_SELECT_RECORD_FIELD_BY_FILTER = "SELECT " +
        "drf.id_record_field,drf.id_record,drf.record_field_value,type.class_name,type.id_type,ent.id_entry,ent.title,ent.display_width" +
        ",ent.display_height,drf.id_field,drf.id_file " +
        "FROM directory_record_field drf,directory_entry ent,directory_entry_type type ";
    private static final String SQL_QUERY_SELECT_FULL_RECORD_FIELD_LIST = "SELECT drf.id_record_field,drf.id_record,drf.record_field_value,type.class_name,ent.id_entry,ent.title,ent.display_width,ent.display_height," +
        " fil.id_file,fil.title,fil.id_physical_file,fil.file_size,fil.mime_type," +
        " dfield.id_field,dfield.id_entry,dfield.title,dfield.default_value,dfield.height,dfield.width,dfield.is_default_value,dfield.max_size_enter,dfield.field_position,dfield.value_type_date,dfield.role_key,dfield.workgroup_key" +
        " FROM directory_record_field drf " + " INNER JOIN directory_entry ent ON (drf.id_entry=ent.id_entry)" +
        " INNER JOIN directory_entry_type type ON (ent.id_type=type.id_type) " +
        " LEFT JOIN directory_file fil ON (drf.id_file=fil.id_file)" +
        " LEFT JOIN directory_field dfield ON (drf.id_field=dfield.id_field) ";
    private static final String SQL_QUERY_SELECT_FULL_RECORD_FIELD_LIST_WITH_RECORD = "SELECT drf.id_record_field,drf.id_record,drf.record_field_value,type.class_name,ent.id_entry,ent.title,ent.display_width,ent.display_height," +
        " fil.id_file,fil.title,fil.id_physical_file,fil.file_size,fil.mime_type," +
        " dfield.id_field,dfield.id_entry,dfield.title,dfield.default_value,dfield.height,dfield.width,dfield.is_default_value,dfield.max_size_enter,dfield.field_position,dfield.value_type_date,dfield.role_key,dfield.workgroup_key," +
        " dr.date_creation, dr.id_directory, dr.is_enabled, dr.role_key, dr.workgroup_key " +
        " FROM directory_record_field drf " + " INNER JOIN directory_entry ent ON (drf.id_entry=ent.id_entry)" +
        " INNER JOIN directory_entry_type type ON (ent.id_type=type.id_type) " +
        " INNER JOIN directory_record dr ON (dr.id_record = drf.id_record) " +
        " LEFT JOIN directory_file fil ON (drf.id_file=fil.id_file)" +
        " LEFT JOIN directory_field dfield ON (drf.id_field=dfield.id_field) ";
    private static final String SQL_QUERY_COUNT_RECORD_FIELD_BY_FILTER = "SELECT COUNT(drf.id_record_field) " +
        "FROM directory_record_field drf,directory_entry ent,directory_entry_type type ";
    // Special query in order to sort numerically and not alphabetically (thus avoiding list like 1, 10, 11, 2, ... instead of 1, 2, ..., 10, 11)
    private static final String SQL_QUERY_SELECT_MAX_NUMBER = " SELECT drf.record_field_value FROM directory_record_field drf " + 
    	" INNER JOIN directory_record dr ON drf.id_record = dr.id_record " +
    	" INNER JOIN directory_entry ent ON drf.id_entry = ent.id_entry " +
    	" WHERE ent.id_type = ? AND dr.id_directory = ? ORDER BY 0 + drf.record_field_value DESC LIMIT 1 ";
    private static final String SQL_QUERY_SELECT_BY_RECORD_FIELD_VALUE = " SELECT drf.id_record_field FROM directory_record_field drf " + 
		" INNER JOIN directory_record dr ON drf.id_record = dr.id_record " +
		" INNER JOIN directory_entry ent ON drf.id_entry = ent.id_entry " +
		" WHERE ent.id_type = ? AND dr.id_directory = ? AND drf.record_field_value = ? "; 
    private static final String SQL_FILTER_ID_RECORD = " drf.id_record = ? ";
    private static final String SQL_FILTER_ID_RECORD_IN = " drf.id_record IN ( ? ";
    private static final String SQL_FILTER_ID_FIELD = " drf.id_field = ? ";
    private static final String SQL_FILTER_ID_ENTRY = "  drf.id_entry = ? ";
    private static final String SQL_FILTER_ID_ENTRY_IN = " AND drf.id_entry IN ( ?";
    private static final String SQL_FILTER_ADITIONAL_PARAMETER = ",?";
    private static final String SQL_FILTER_CLOSE_PARENTHESIS = " ) ";
    private static final String SQL_FILTER_IS_ENTRY_SHOWN_IN_RESULT_LIST = "  ent.is_shown_in_result_list=?";
    private static final String SQL_FILTER_IS_ENTRY_SHOWN_IN_RESULT_RECORD = "  ent.is_shown_in_result_record=?";
    private static final String SQL_ORDER_BY_ID_RECORD_FIELD = " ORDER BY ent.entry_position ";
    private static final String SQL_FILTER_ASSOCIATION_ON_ID_ENTRY = " drf.id_entry =ent.id_entry ";
    private static final String SQL_FILTER_ASSOCIATION_ON_ID_TYPE = " ent.id_type=type.id_type ";
    private static final String SQL_WHERE = " WHERE ";

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

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IRecordFieldDAO#insert(fr.paris.lutece.plugins.directory.business.RecordField, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public synchronized void insert( RecordField recordField, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );

        daoUtil.setInt( 2, recordField.getRecord(  ).getIdRecord(  ) );
        daoUtil.setString( 3, recordField.getValue(  ) );
        // daoUtil.setBytes( 3 , recordField.getValue().getBytes() );
        daoUtil.setInt( 4, recordField.getEntry(  ).getIdEntry(  ) );

        if ( recordField.getField(  ) != null )
        {
            daoUtil.setInt( 5, recordField.getField(  ).getIdField(  ) );
        }
        else
        {
            daoUtil.setIntNull( 5 );
        }

        if ( recordField.getFile(  ) != null )
        {
            daoUtil.setInt( 6, recordField.getFile(  ).getIdFile(  ) );
        }
        else
        {
            daoUtil.setIntNull( 6 );
        }

        recordField.setIdRecordField( newPrimaryKey( plugin ) );
        daoUtil.setInt( 1, recordField.getIdRecordField(  ) );

        daoUtil.executeUpdate(  );

        daoUtil.free(  );
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IRecordFieldDAO#load(int, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public RecordField load( int nIdRecordField, Plugin plugin )
    {
        boolean bException = false;
        RecordField recordField = null;
        File file = null;
        IEntry entry = null;
        EntryType entryType = null;
        Field field = null;
        Record record = null;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, plugin );
        daoUtil.setInt( 1, nIdRecordField );
        daoUtil.executeQuery(  );

        if ( daoUtil.next(  ) )
        {
            recordField = new RecordField(  );
            recordField.setIdRecordField( daoUtil.getInt( 1 ) );
            record = new Record(  );
            record.setIdRecord( daoUtil.getInt( 2 ) );
            recordField.setRecord( record );
            recordField.setValue( daoUtil.getString( 3 ) );

            /**
            if( daoUtil.getBytes( 3 ) != null )
                {
                    recordField.setValue( new String( daoUtil.getBytes( 3 ) ) );
                }
                **/
            entryType = new EntryType(  );
            entryType.setClassName( daoUtil.getString( 4 ) );

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
                // can't access to the class
                AppLogService.error( e );
                bException = true;
            }

            if ( bException )
            {
                daoUtil.free(  );

                return null;
            }

            entry.setEntryType( entryType );
            entry.setIdEntry( daoUtil.getInt( 5 ) );
            entry.setTitle( daoUtil.getString( 6 ) );
            entry.setDisplayWidth( daoUtil.getInt( 7 ) );
            entry.setDisplayHeight( daoUtil.getInt( 8 ) );

            entry.setIdEntry( daoUtil.getInt( 7 ) );

            recordField.setEntry( entry );

            if ( daoUtil.getObject( 9 ) != null )
            {
                field = new Field(  );
                field.setIdField( daoUtil.getInt( 9 ) );
                recordField.setField( field );
            }

            if ( daoUtil.getObject( 10 ) != null )
            {
                file = new File(  );
                file.setIdFile( daoUtil.getInt( 10 ) );
                recordField.setFile( file );
            }
        }

        daoUtil.free(  );

        return recordField;
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IRecordFieldDAO#delete(int, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public void delete( int nIdRecordField, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nIdRecordField );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IRecordFieldDAO#deleteByListRecordId(java.util.List, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public void deleteByListRecordId( List<Integer> lListRecordId, Plugin plugin )
    {
        int nListIdSize = lListRecordId.size(  );

        if ( nListIdSize > 0 )
        {
            StringBuffer sbSQL = new StringBuffer( SQL_QUERY_DELETE_BY_LIST_RECORD_ID );

            for ( int i = 1; i < nListIdSize; i++ )
            {
                sbSQL.append( SQL_FILTER_ADITIONAL_PARAMETER );
            }

            sbSQL.append( SQL_FILTER_CLOSE_PARENTHESIS );

            DAOUtil daoUtil = new DAOUtil( sbSQL.toString(  ), plugin );

            for ( int i = 0; i < nListIdSize; i++ )
            {
                daoUtil.setInt( i + 1, lListRecordId.get( i ) );
            }

            daoUtil.executeUpdate(  );
            daoUtil.free(  );
        }
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IRecordFieldDAO#store(fr.paris.lutece.plugins.directory.business.RecordField, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public void store( RecordField recordField, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );

        daoUtil.setInt( 1, recordField.getIdRecordField(  ) );
        daoUtil.setInt( 2, recordField.getRecord(  ).getIdRecord(  ) );
        daoUtil.setString( 3, recordField.getValue(  ) );

        //daoUtil.setBytes( 3 , recordField.getValue().getBytes() );
        daoUtil.setInt( 4, recordField.getEntry(  ).getIdEntry(  ) );

        if ( recordField.getField(  ) != null )
        {
            daoUtil.setInt( 5, recordField.getField(  ).getIdField(  ) );
        }
        else
        {
            daoUtil.setIntNull( 5 );
        }

        if ( recordField.getFile(  ) != null )
        {
            daoUtil.setInt( 6, recordField.getFile(  ).getIdFile(  ) );
        }
        else
        {
            daoUtil.setIntNull( 6 );
        }

        daoUtil.setInt( 7, recordField.getIdRecordField(  ) );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IRecordFieldDAO#getRecordFieldListByRecordId(java.lang.Integer, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public List<RecordField> getRecordFieldListByRecordIdList( List<Integer> lIdRecordList, Plugin plugin )
    {
        boolean bException = false;
        List<RecordField> recordFieldList = new ArrayList<RecordField>(  );
        int nIdRecordListSize = lIdRecordList.size(  );

        if ( nIdRecordListSize > 0 )
        {
            RecordField recordField;
            IEntry entry = null;
            EntryType entryType = null;
            Field field = null;
            File file = null;
            Record record = null;
            Directory directory = null;

            StringBuffer sbSQL = new StringBuffer( SQL_QUERY_SELECT_FULL_RECORD_FIELD_LIST_WITH_RECORD );

            sbSQL.append( SQL_WHERE );

            for ( int i = 0; i < nIdRecordListSize; i++ )
            {
                if ( i < 1 )
                {
                    sbSQL.append( SQL_FILTER_ID_RECORD_IN );
                }
                else
                {
                    sbSQL.append( SQL_FILTER_ADITIONAL_PARAMETER );
                }
            }

            sbSQL.append( SQL_FILTER_CLOSE_PARENTHESIS + SQL_ORDER_BY_ID_RECORD_FIELD );

            DAOUtil daoUtil = new DAOUtil( sbSQL.toString(  ), plugin );

            for ( int i = 0; i < nIdRecordListSize; i++ )
            {
                daoUtil.setInt( i + 1, lIdRecordList.get( i ) );
            }

            daoUtil.executeQuery(  );

            while ( daoUtil.next(  ) )
            {
                recordField = new RecordField(  );
                recordField.setIdRecordField( daoUtil.getInt( 1 ) ); // drf.id_record_field
                record = new Record(  );
                record.setIdRecord( daoUtil.getInt( 2 ) ); // drf.id_record

                record.setDateCreation( daoUtil.getTimestamp( 26 ) ); // dr.date_creation 
                directory = new Directory(  );
                directory.setIdDirectory( daoUtil.getInt( 27 ) ); // dr.id_directory 
                record.setDirectory( directory );
                record.setEnabled( daoUtil.getBoolean( 28 ) ); // dr.is_enabled
                record.setRoleKey( daoUtil.getString( 29 ) ); // dr.role_key
                record.setWorkgroup( daoUtil.getString( 30 ) ); // dr.workgroup_key

                recordField.setRecord( record );
                recordField.setValue( daoUtil.getString( 3 ) ); // drf.record_field_value

                entryType = new EntryType(  );
                entryType.setClassName( daoUtil.getString( 4 ) ); // type.class_name

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
                entry.setIdEntry( daoUtil.getInt( 5 ) ); // ent.id_entry
                entry.setTitle( daoUtil.getString( 6 ) ); // ent.title
                entry.setDisplayWidth( daoUtil.getInt( 7 ) ); // ent.display_width
                entry.setDisplayHeight( daoUtil.getInt( 8 ) ); // ent.display_height
                recordField.setEntry( entry );

                if ( daoUtil.getObject( 14 ) != null ) // field.id_field
                {
                    field = new Field(  );
                    field.setIdField( daoUtil.getInt( 14 ) ); // field.id_field

                    Entry entryField = new Entry(  );
                    entryField.setIdEntry( daoUtil.getInt( 15 ) ); // field.id_entry
                    field.setEntry( entryField );

                    field.setTitle( daoUtil.getString( 16 ) ); // field.id_entry
                    field.setValue( daoUtil.getString( 17 ) ); // field.default_value
                    field.setHeight( daoUtil.getInt( 18 ) ); // field.height
                    field.setWidth( daoUtil.getInt( 19 ) ); // field.width
                    field.setDefaultValue( daoUtil.getBoolean( 20 ) ); // field.default_value
                    field.setMaxSizeEnter( daoUtil.getInt( 21 ) ); // field.max_size_enter
                    field.setPosition( daoUtil.getInt( 22 ) ); // field.field_position
                    field.setValueTypeDate( daoUtil.getDate( 23 ) ); // field.value_type_date
                    field.setRoleKey( daoUtil.getString( 24 ) ); // field.role_key
                    field.setWorkgroup( daoUtil.getString( 25 ) ); // field.workgroup_key

                    recordField.setField( field );
                }

                if ( daoUtil.getObject( 9 ) != null ) // fil.id_file
                {
                    file = new File(  );
                    file.setIdFile( daoUtil.getInt( 9 ) ); // fil.id_file
                    file.setTitle( daoUtil.getString( 10 ) ); // fil.title

                    PhysicalFile pf = new PhysicalFile(  );
                    pf.setIdPhysicalFile( daoUtil.getInt( 11 ) ); //fil.id_physical_file
                    file.setPhysicalFile( pf );
                    file.setSize( daoUtil.getInt( 12 ) ); // fil.file_size
                    file.setMimeType( daoUtil.getString( 13 ) ); //fil.mime_type
                    recordField.setFile( file );
                }

                recordFieldList.add( recordField );
            }

            daoUtil.free(  );
        }

        return recordFieldList;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IRecordFieldDAO#selectSpecificList(java.util.List, java.lang.Integer, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public List<RecordField> selectSpecificList( List<Integer> lEntryId, Integer nIdRecord, Plugin plugin )
    {
        boolean bException = false;
        List<RecordField> recordFieldList = new ArrayList<RecordField>(  );
        RecordField recordField;
        IEntry entry = null;
        EntryType entryType = null;
        Field field = null;
        File file = null;
        Record record = null;

        StringBuffer sbSQL = new StringBuffer( SQL_QUERY_SELECT_FULL_RECORD_FIELD_LIST );

        sbSQL.append( SQL_WHERE + SQL_FILTER_ID_RECORD );

        int nListEntryIdSize = lEntryId.size(  );

        if ( nListEntryIdSize > 0 )
        {
            for ( int i = 0; i < nListEntryIdSize; i++ )
            {
                if ( i < 1 )
                {
                    sbSQL.append( SQL_FILTER_ID_ENTRY_IN );
                }
                else
                {
                    sbSQL.append( SQL_FILTER_ADITIONAL_PARAMETER );
                }
            }

            sbSQL.append( SQL_FILTER_CLOSE_PARENTHESIS );
        }

        sbSQL.append( SQL_ORDER_BY_ID_RECORD_FIELD );

        DAOUtil daoUtil = new DAOUtil( sbSQL.toString(  ), plugin );
        daoUtil.setInt( 1, nIdRecord );

        if ( nListEntryIdSize > 0 )
        {
            for ( int i = 0; i < nListEntryIdSize; i++ )
            {
                daoUtil.setInt( i + 2, lEntryId.get( i ) );
            }
        }

        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            recordField = new RecordField(  );
            recordField.setIdRecordField( daoUtil.getInt( 1 ) ); // drf.id_record_field
            record = new Record(  );
            record.setIdRecord( daoUtil.getInt( 2 ) ); // drf.id_record
            recordField.setRecord( record );
            recordField.setValue( daoUtil.getString( 3 ) ); // drf.record_field_value

            entryType = new EntryType(  );
            entryType.setClassName( daoUtil.getString( 4 ) ); // type.class_name

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
            entry.setIdEntry( daoUtil.getInt( 5 ) ); // ent.id_entry
            entry.setTitle( daoUtil.getString( 6 ) ); // ent.title
            entry.setDisplayWidth( daoUtil.getInt( 7 ) ); // ent.display_width
            entry.setDisplayHeight( daoUtil.getInt( 8 ) ); // ent.display_height
            recordField.setEntry( entry );

            if ( daoUtil.getObject( 14 ) != null ) // field.id_field
            {
                field = new Field(  );
                field.setIdField( daoUtil.getInt( 14 ) ); // field.id_field

                Entry entryField = new Entry(  );
                entryField.setIdEntry( daoUtil.getInt( 15 ) ); // field.id_entry
                field.setEntry( entryField );

                field.setTitle( daoUtil.getString( 16 ) ); // field.id_entry
                field.setValue( daoUtil.getString( 17 ) ); // field.default_value
                field.setHeight( daoUtil.getInt( 18 ) ); // field.height
                field.setWidth( daoUtil.getInt( 19 ) ); // field.width
                field.setDefaultValue( daoUtil.getBoolean( 20 ) ); // field.default_value
                field.setMaxSizeEnter( daoUtil.getInt( 21 ) ); // field.max_size_enter
                field.setPosition( daoUtil.getInt( 22 ) ); // field.field_position
                field.setValueTypeDate( daoUtil.getDate( 23 ) ); // field.value_type_date
                field.setRoleKey( daoUtil.getString( 24 ) ); // field.role_key
                field.setWorkgroup( daoUtil.getString( 25 ) ); // field.workgroup_key

                recordField.setField( field );
            }

            if ( daoUtil.getObject( 9 ) != null ) // fil.id_file
            {
                file = new File(  );
                file.setIdFile( daoUtil.getInt( 9 ) ); // fil.id_file
                file.setTitle( daoUtil.getString( 10 ) ); // fil.title

                PhysicalFile pf = new PhysicalFile(  );
                pf.setIdPhysicalFile( daoUtil.getInt( 11 ) ); //fil.id_physical_file
                file.setPhysicalFile( pf );
                file.setSize( daoUtil.getInt( 12 ) ); // fil.file_size
                file.setMimeType( daoUtil.getString( 13 ) ); //fil.mime_type
                recordField.setFile( file );
            }

            recordFieldList.add( recordField );
        }

        daoUtil.free(  );

        return recordFieldList;
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IRecordFieldDAO#selectListByFilter(fr.paris.lutece.plugins.directory.business.RecordFieldFilter, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public List<RecordField> selectListByFilter( RecordFieldFilter filter, Plugin plugin )
    {
        boolean bException = false;
        List<RecordField> recordFieldList = new ArrayList<RecordField>(  );
        RecordField recordField;
        IEntry entry = null;
        EntryType entryType = null;
        Field field = null;
        File file = null;
        Record record = null;

        List<String> listStrFilter = new ArrayList<String>(  );
        listStrFilter.add( SQL_FILTER_ASSOCIATION_ON_ID_ENTRY );
        listStrFilter.add( SQL_FILTER_ASSOCIATION_ON_ID_TYPE );

        if ( filter.containsIdRecord(  ) )
        {
            listStrFilter.add( SQL_FILTER_ID_RECORD );
        }

        if ( filter.containsIdField(  ) )
        {
            listStrFilter.add( SQL_FILTER_ID_FIELD );
        }

        if ( filter.containsIdEntry(  ) )
        {
            listStrFilter.add( SQL_FILTER_ID_ENTRY );
        }

        if ( filter.containsIsEntryShownInResultList(  ) )
        {
            listStrFilter.add( SQL_FILTER_IS_ENTRY_SHOWN_IN_RESULT_LIST );
        }

        if ( filter.containsIsEntryShownInResultRecord(  ) )
        {
            listStrFilter.add( SQL_FILTER_IS_ENTRY_SHOWN_IN_RESULT_RECORD );
        }

        String strSQL = DirectoryUtils.buildRequetteWithFilter( SQL_QUERY_SELECT_RECORD_FIELD_BY_FILTER, listStrFilter,
                SQL_ORDER_BY_ID_RECORD_FIELD );

        DAOUtil daoUtil = new DAOUtil( strSQL, plugin );
        int nIndex = 1;

        if ( filter.containsIdRecord(  ) )
        {
            daoUtil.setInt( nIndex, filter.getIdRecord(  ) );
            nIndex++;
        }

        if ( filter.containsIdField(  ) )
        {
            daoUtil.setInt( nIndex, filter.getIdField(  ) );
            nIndex++;
        }

        if ( filter.containsIdEntry(  ) )
        {
            daoUtil.setInt( nIndex, filter.getIdEntry(  ) );
            nIndex++;
        }

        if ( filter.containsIsEntryShownInResultList(  ) )
        {
            daoUtil.setBoolean( nIndex, filter.getIsEntryShownInResultList(  ) == RecordFieldFilter.FILTER_TRUE );
            nIndex++;
        }

        if ( filter.containsIsEntryShownInResultRecord(  ) )
        {
            daoUtil.setBoolean( nIndex, filter.getIsEntryShownInResultRecord(  ) == RecordFieldFilter.FILTER_TRUE );
            nIndex++;
        }

        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            recordField = new RecordField(  );
            recordField.setIdRecordField( daoUtil.getInt( 1 ) );
            record = new Record(  );
            record.setIdRecord( daoUtil.getInt( 2 ) );
            recordField.setRecord( record );
            recordField.setValue( daoUtil.getString( 3 ) );
            /**
            if( daoUtil.getBytes( 3 ) != null )
                {
                    recordField.setValue( new String( daoUtil.getBytes( 3 ) ) );
                }
            **/
            entryType = new EntryType(  );
            entryType.setClassName( daoUtil.getString( 4 ) );

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

            entryType.setIdType( daoUtil.getInt( 5 ) );
            entry.setEntryType( entryType );
            entry.setIdEntry( daoUtil.getInt( 6 ) );
            entry.setTitle( daoUtil.getString( 7 ) );
            entry.setDisplayWidth( daoUtil.getInt( 8 ) );
            entry.setDisplayHeight( daoUtil.getInt( 9 ) );
            recordField.setEntry( entry );

            if ( daoUtil.getObject( 10 ) != null )
            {
                field = new Field(  );
                field.setIdField( daoUtil.getInt( 10 ) );
                recordField.setField( field );
            }

            if ( daoUtil.getObject( 11 ) != null )
            {
                file = new File(  );
                file.setIdFile( daoUtil.getInt( 11 ) );
                recordField.setFile( file );
            }

            recordFieldList.add( recordField );
        }

        daoUtil.free(  );

        return recordFieldList;
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IRecordFieldDAO#getCountByFilter(fr.paris.lutece.plugins.directory.business.RecordFieldFilter, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public int getCountByFilter( RecordFieldFilter filter, Plugin plugin )
    {
        int nCount = 0;

        List<String> listStrFilter = new ArrayList<String>(  );
        listStrFilter.add( SQL_FILTER_ASSOCIATION_ON_ID_ENTRY );
        listStrFilter.add( SQL_FILTER_ASSOCIATION_ON_ID_TYPE );

        if ( filter.containsIdRecord(  ) )
        {
            listStrFilter.add( SQL_FILTER_ID_RECORD );
        }

        if ( filter.containsIdField(  ) )
        {
            listStrFilter.add( SQL_FILTER_ID_FIELD );
        }

        if ( filter.containsIdEntry(  ) )
        {
            listStrFilter.add( SQL_FILTER_ID_ENTRY );
        }

        if ( filter.containsIsEntryShownInResultList(  ) )
        {
            listStrFilter.add( SQL_FILTER_IS_ENTRY_SHOWN_IN_RESULT_LIST );
        }

        if ( filter.containsIsEntryShownInResultRecord(  ) )
        {
            listStrFilter.add( SQL_FILTER_IS_ENTRY_SHOWN_IN_RESULT_RECORD );
        }

        String strSQL = DirectoryUtils.buildRequetteWithFilter( SQL_QUERY_COUNT_RECORD_FIELD_BY_FILTER, listStrFilter,
                null );

        DAOUtil daoUtil = new DAOUtil( strSQL, plugin );
        int nIndex = 1;

        if ( filter.containsIdRecord(  ) )
        {
            daoUtil.setInt( nIndex, filter.getIdRecord(  ) );
            nIndex++;
        }

        if ( filter.containsIdField(  ) )
        {
            daoUtil.setInt( nIndex, filter.getIdField(  ) );
            nIndex++;
        }

        if ( filter.containsIdEntry(  ) )
        {
            daoUtil.setInt( nIndex, filter.getIdEntry(  ) );
            nIndex++;
        }

        if ( filter.containsIsEntryShownInResultList(  ) )
        {
            daoUtil.setBoolean( nIndex, filter.getIsEntryShownInResultList(  ) == RecordFieldFilter.FILTER_TRUE );
            nIndex++;
        }

        if ( filter.containsIsEntryShownInResultRecord(  ) )
        {
            daoUtil.setBoolean( nIndex, filter.getIsEntryShownInResultRecord(  ) == RecordFieldFilter.FILTER_TRUE );
            nIndex++;
        }

        daoUtil.executeQuery(  );

        if ( daoUtil.next(  ) )
        {
            nCount = daoUtil.getInt( 1 );
        }

        daoUtil.free(  );

        return nCount;
    }

    /**
     * {@inheritDoc}
     */
    public int getMaxNumber( int nIdEntryTypeNumbering, int nIdDirectory, Plugin plugin )
    {
    	int nIndex = 1;
    	DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_MAX_NUMBER, plugin );
    	daoUtil.setInt( nIndex++, nIdEntryTypeNumbering );
    	daoUtil.setInt( nIndex++, nIdDirectory );
        daoUtil.executeQuery(  );

        int nKey = 1;

        if ( daoUtil.next(  ) )
        {
        	nKey = daoUtil.getInt( 1 ) + 1;
        }
        
        daoUtil.free(  );

        return nKey;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isNumberOnARecordField( int nIdEntryTypeNumbering, int nIdDirectory, int nNumber, Plugin plugin )
    {
    	int nIndex = 1;
    	DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_RECORD_FIELD_VALUE, plugin );
    	daoUtil.setInt( nIndex++, nIdEntryTypeNumbering );
    	daoUtil.setInt( nIndex++, nIdDirectory );
    	daoUtil.setInt( nIndex++, nNumber );
        daoUtil.executeQuery(  );
        
        boolean isOn = false;

        if ( daoUtil.next(  ) )
        {
        	isOn = true;
        }
        
        daoUtil.free(  );

        return isOn;
    }
}

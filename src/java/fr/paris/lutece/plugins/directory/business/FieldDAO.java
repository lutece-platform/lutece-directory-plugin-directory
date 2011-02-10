/*
 * Copyright (c) 2002-2009, Mairie de Paris
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

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

import java.sql.Date;

import java.util.ArrayList;
import java.util.List;


/**
 * This class provides Data Access methods for Field objects
 */
public final class FieldDAO implements IFieldDAO
{
    // Constants
    private static final String SQL_QUERY_NEW_PK = "SELECT max( id_field ) FROM directory_field";
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = "SELECT id_field,id_entry,title,default_value,height,width," +
        "is_default_value,max_size_enter,field_position,value_type_date,role_key,workgroup_key" +
        ",is_shown_in_result_list,is_shown_in_result_record " + " FROM directory_field WHERE id_field = ? ";
    private static final String SQL_QUERY_FIND_BY_VALUE = "SELECT id_field,id_entry,title,default_value,height,width," +
        "is_default_value,max_size_enter,field_position,value_type_date,role_key,workgroup_key" +
        ",is_shown_in_result_list,is_shown_in_result_record " +
        " FROM directory_field WHERE id_entry=? and default_value = ? ";
    private static final String SQL_QUERY_INSERT = "INSERT INTO directory_field(id_field,id_entry,title,default_value,height," +
        "width,is_default_value,max_size_enter,field_position,value_type_date,role_key,workgroup_key,is_shown_in_result_list," +
        "is_shown_in_result_record )" + " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String SQL_QUERY_DELETE = "DELETE FROM directory_field WHERE id_field = ? ";
    private static final String SQL_QUERY_INSERT_VERIF_BY = "INSERT INTO directory_verify_by(id_field,id_expression) VALUES(?,?) ";
    private static final String SQL_QUERY_DELETE_VERIF_BY = "DELETE FROM directory_verify_by WHERE id_field = ? and id_expression= ?";
    private static final String SQL_QUERY_UPDATE = "UPDATE directory_field SET " +
        "id_field=?,id_entry=?,title=?,default_value=?,height=?,width=?,is_default_value=?,max_size_enter=?, " +
        "field_position=?,value_type_date=?,role_key=?,workgroup_key=?,is_shown_in_result_list=?,is_shown_in_result_record=?" +
        "  WHERE id_field = ?";
    private static final String SQL_QUERY_SELECT_FIELD_BY_ID_ENTRY = "SELECT id_field,id_entry,title,default_value,height,width,is_default_value," +
        "max_size_enter,field_position,value_type_date,role_key,workgroup_key,is_shown_in_result_list" +
        ",is_shown_in_result_record FROM directory_field  WHERE id_entry = ? ORDER BY field_position";
    private static final String SQL_QUERY_NEW_POSITION = "SELECT MAX(field_position)" + " FROM directory_field ";
    private static final String SQL_QUERY_SELECT_REGULAR_EXPRESSION_BY_ID_FIELD = "SELECT id_expression " +
        " FROM directory_verify_by where id_field=?";
    private static final String SQL_QUERY_COUNT_FIELD_BY_ID_REGULAR_EXPRESSION = "SELECT COUNT(id_field) " +
        " FROM directory_verify_by where id_expression = ?";

    /**
     * Generates a new primary key
     *
     * @param plugin the plugin
     * @return The new primary key
     */
    public int newPrimaryKey( Plugin plugin )
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
     * Generates a new field position
     * @param plugin the plugin
     * @return the new entry position
     */
    private int newPosition( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_POSITION, plugin );
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

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IFieldDAO#insert(fr.paris.lutece.plugins.directory.business.Field, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public synchronized int insert( Field field, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );
        daoUtil.setInt( 2, field.getEntry(  ).getIdEntry(  ) );
        daoUtil.setString( 3, field.getTitle(  ) );
        daoUtil.setString( 4, field.getValue(  ) );
        daoUtil.setInt( 5, field.getHeight(  ) );
        daoUtil.setInt( 6, field.getWidth(  ) );
        daoUtil.setBoolean( 7, field.isDefaultValue(  ) );
        daoUtil.setInt( 8, field.getMaxSizeEnter(  ) );
        daoUtil.setInt( 9, newPosition( plugin ) );
        daoUtil.setDate( 10,
            ( field.getValueTypeDate(  ) == null ) ? null : new Date( field.getValueTypeDate(  ).getTime(  ) ) );

        daoUtil.setString( 11, field.getRoleKey(  ) );
        daoUtil.setString( 12, field.getWorkgroup(  ) );
        daoUtil.setBoolean( 13, field.isShownInResultList(  ) );
        daoUtil.setBoolean( 14, field.isShownInResultRecord(  ) );

        field.setIdField( newPrimaryKey( plugin ) );
        daoUtil.setInt( 1, field.getIdField(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );

        return field.getIdField(  );
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IFieldDAO#load(int, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public Field load( int nId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, plugin );
        daoUtil.setInt( 1, nId );
        daoUtil.executeQuery(  );

        Field field = null;
        IEntry entry = null;

        if ( daoUtil.next(  ) )
        {
            field = new Field(  );
            field.setIdField( daoUtil.getInt( 1 ) );
            //parent entry
            entry = new Entry(  );
            entry.setIdEntry( daoUtil.getInt( 2 ) );
            field.setEntry( entry );
            field.setTitle( daoUtil.getString( 3 ) );
            field.setValue( daoUtil.getString( 4 ) );
            field.setHeight( daoUtil.getInt( 5 ) );
            field.setWidth( daoUtil.getInt( 6 ) );
            field.setDefaultValue( daoUtil.getBoolean( 7 ) );
            field.setMaxSizeEnter( daoUtil.getInt( 8 ) );
            field.setPosition( daoUtil.getInt( 9 ) );
            field.setValueTypeDate( daoUtil.getDate( 10 ) );
            field.setRoleKey( daoUtil.getString( 11 ) );
            field.setWorkgroup( daoUtil.getString( 12 ) );
            field.setShownInResultList( daoUtil.getBoolean( 13 ) );
            field.setShownInResultRecord( daoUtil.getBoolean( 14 ) );
        }

        daoUtil.free(  );

        return field;
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IFieldDAO#loadByValue(int, java.lang.String, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public Field loadByValue( int nIdEntry, String strValue, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_VALUE, plugin );
        daoUtil.setInt( 1, nIdEntry );
        daoUtil.setString( 2, strValue );
        daoUtil.executeQuery(  );

        Field field = null;
        IEntry entry = null;

        if ( daoUtil.next(  ) )
        {
            field = new Field(  );
            field.setIdField( daoUtil.getInt( 1 ) );
            //parent entry
            entry = new Entry(  );
            entry.setIdEntry( daoUtil.getInt( 2 ) );
            field.setEntry( entry );
            field.setTitle( daoUtil.getString( 3 ) );
            field.setValue( daoUtil.getString( 4 ) );
            field.setHeight( daoUtil.getInt( 5 ) );
            field.setWidth( daoUtil.getInt( 6 ) );
            field.setDefaultValue( daoUtil.getBoolean( 7 ) );
            field.setMaxSizeEnter( daoUtil.getInt( 8 ) );
            field.setPosition( daoUtil.getInt( 9 ) );
            field.setValueTypeDate( daoUtil.getDate( 10 ) );
            field.setRoleKey( daoUtil.getString( 11 ) );
            field.setWorkgroup( daoUtil.getString( 12 ) );
            field.setShownInResultList( daoUtil.getBoolean( 13 ) );
            field.setShownInResultRecord( daoUtil.getBoolean( 14 ) );
        }

        daoUtil.free(  );

        return field;
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IFieldDAO#delete(int, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public void delete( int nIdField, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nIdField );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IFieldDAO#store(fr.paris.lutece.plugins.directory.business.Field, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public void store( Field field, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );
        daoUtil.setInt( 1, field.getIdField(  ) );
        daoUtil.setInt( 2, field.getEntry(  ).getIdEntry(  ) );
        daoUtil.setString( 3, field.getTitle(  ) );
        daoUtil.setString( 4, field.getValue(  ) );
        daoUtil.setInt( 5, field.getHeight(  ) );
        daoUtil.setInt( 6, field.getWidth(  ) );
        daoUtil.setBoolean( 7, field.isDefaultValue(  ) );
        daoUtil.setInt( 8, field.getMaxSizeEnter(  ) );
        daoUtil.setInt( 9, field.getPosition(  ) );
        daoUtil.setDate( 10,
            ( field.getValueTypeDate(  ) == null ) ? null : new Date( field.getValueTypeDate(  ).getTime(  ) ) );
        daoUtil.setString( 11, field.getRoleKey(  ) );
        daoUtil.setString( 12, field.getWorkgroup(  ) );
        daoUtil.setBoolean( 13, field.isShownInResultList(  ) );
        daoUtil.setBoolean( 14, field.isShownInResultRecord(  ) );
        daoUtil.setInt( 15, field.getIdField(  ) );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IFieldDAO#selectFieldListByIdEntry(int, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public List<Field> selectFieldListByIdEntry( int nIdEntry, Plugin plugin )
    {
        List<Field> fieldList = new ArrayList<Field>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_FIELD_BY_ID_ENTRY, plugin );
        daoUtil.setInt( 1, nIdEntry );
        daoUtil.executeQuery(  );

        Field field = null;
        IEntry entry = null;

        while ( daoUtil.next(  ) )
        {
            field = new Field(  );
            field.setIdField( daoUtil.getInt( 1 ) );
            //parent entry
            entry = new Entry(  );
            entry.setIdEntry( daoUtil.getInt( 2 ) );
            field.setEntry( entry );
            field.setTitle( daoUtil.getString( 3 ) );
            field.setValue( daoUtil.getString( 4 ) );
            field.setHeight( daoUtil.getInt( 5 ) );
            field.setWidth( daoUtil.getInt( 6 ) );
            field.setDefaultValue( daoUtil.getBoolean( 7 ) );
            field.setMaxSizeEnter( daoUtil.getInt( 8 ) );
            field.setPosition( daoUtil.getInt( 9 ) );
            field.setValueTypeDate( daoUtil.getDate( 10 ) );
            field.setRoleKey( daoUtil.getString( 11 ) );
            field.setWorkgroup( daoUtil.getString( 12 ) );
            field.setShownInResultList( daoUtil.getBoolean( 13 ) );
            field.setShownInResultRecord( daoUtil.getBoolean( 14 ) );

            fieldList.add( field );
        }

        daoUtil.free(  );

        return fieldList;
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IFieldDAO#deleteVerifyBy(int, int, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public void deleteVerifyBy( int nIdField, int nIdExpression, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_VERIF_BY, plugin );
        daoUtil.setInt( 1, nIdField );
        daoUtil.setInt( 2, nIdExpression );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IFieldDAO#insertVerifyBy(int, int, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public void insertVerifyBy( int nIdField, int nIdExpression, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT_VERIF_BY, plugin );
        daoUtil.setInt( 1, nIdField );
        daoUtil.setInt( 2, nIdExpression );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IFieldDAO#selectListRegularExpressionKeyByIdField(int, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public List<Integer> selectListRegularExpressionKeyByIdField( int nIdField, Plugin plugin )
    {
        List<Integer> regularExpressionList = new ArrayList<Integer>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_REGULAR_EXPRESSION_BY_ID_FIELD, plugin );
        daoUtil.setInt( 1, nIdField );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            regularExpressionList.add( daoUtil.getInt( 1 ) );
        }

        daoUtil.free(  );

        return regularExpressionList;
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.business.IFieldDAO#isRegularExpressionIsUse(int, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public boolean isRegularExpressionIsUse( int nIdExpression, Plugin plugin )
    {
        int nNumberEntry = 0;

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_COUNT_FIELD_BY_ID_REGULAR_EXPRESSION, plugin );
        daoUtil.setInt( 1, nIdExpression );
        daoUtil.executeQuery(  );

        if ( daoUtil.next(  ) )
        {
            nNumberEntry = daoUtil.getInt( 1 );
        }

        daoUtil.free(  );

        return nNumberEntry != 0;
    }
}

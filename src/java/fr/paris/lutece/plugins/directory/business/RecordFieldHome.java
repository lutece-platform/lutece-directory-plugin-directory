/*
 * Copyright (c) 2002-2017, Mairie de Paris
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

import fr.paris.lutece.plugins.directory.service.DirectoryService;
import fr.paris.lutece.plugins.directory.service.record.IRecordService;
import fr.paris.lutece.plugins.directory.service.record.RecordService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.List;
import java.util.Map;

/**
 * This class provides instances management methods (create, find, ...) for Record field objects
 */
public final class RecordFieldHome
{
    // Static variable pointed at the DAO instance
    private static IRecordFieldDAO _dao = SpringContextService.getBean( "directoryRecordFieldDAO" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private RecordFieldHome( )
    {
    }

    /**
     * Creation of an instance of record field
     *
     * @param recordField
     *            The instance of the record field which contains the informations to store
     * @param plugin
     *            the Plugin
     *
     */
    public static void create( RecordField recordField, Plugin plugin )
    {
        if ( recordField.getFile( ) != null )
        {
            recordField.getFile( ).setIdFile( FileHome.create( recordField.getFile( ), plugin ) );
        }

        _dao.insert( recordField, plugin );
    }

    /**
     * Copy of an instance of record field
     *
     * @param recordField
     *            The instance of the record field which contains the informations to store
     * @param plugin
     *            the Plugin
     *
     */
    public static void copy( RecordField recordField, Plugin plugin )
    {
        if ( recordField.getFile( ) != null )
        {
            File fileCopy = FileHome.findByPrimaryKey( recordField.getFile( ).getIdFile( ), plugin );

            if ( ( fileCopy != null ) && ( fileCopy.getPhysicalFile( ) != null ) )
            {
                fileCopy.setPhysicalFile( PhysicalFileHome.findByPrimaryKey( fileCopy.getPhysicalFile( ).getIdPhysicalFile( ), plugin ) );
            }

            recordField.getFile( ).setIdFile( FileHome.create( fileCopy, plugin ) );
        }

        _dao.insert( recordField, plugin );
    }

    /**
     * Update of the record field which is specified in parameter
     *
     * @param recordField
     *            The instance of the record fields which contains the informations to update
     * @param plugin
     *            the Plugin
     *
     */
    public static void update( RecordField recordField, Plugin plugin )
    {
        if ( recordField.getFile( ) != null )
        {
            FileHome.update( recordField.getFile( ), plugin );
        }

        _dao.store( recordField, plugin );
    }

    /**
     * Delete the record field whose identifier is specified in parameter
     *
     * @param nIdRecordField
     *            The identifier of the record field
     * @param plugin
     *            the Plugin
     */
    public static void remove( int nIdRecordField, Plugin plugin )
    {
        remove( nIdRecordField, false, plugin );
    }

    /**
     * Delete the record field whose identifier is specified in parameter
     *
     * @param nIdRecordField
     *            The identifier of the record field
     * @param bRemoveAsynchronousFiles
     *            true if it must remove the asynchronous files, false otherwise
     * @param plugin
     *            the Plugin
     */
    public static void remove( int nIdRecordField, boolean bRemoveAsynchronousFiles, Plugin plugin )
    {
        RecordField recordField = findByPrimaryKey( nIdRecordField, plugin );

        if ( ( recordField != null ) && ( recordField.getFile( ) != null ) )
        {
            FileHome.remove( recordField.getFile( ).getIdFile( ), plugin );
        }

        if ( bRemoveAsynchronousFiles )
        {
            DirectoryService.getInstance( ).removeAsynchronousFile( recordField, plugin );
        }

        _dao.delete( nIdRecordField, plugin );
    }

    // /////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Returns an instance of a Record field whose identifier is specified in parameter
     *
     * @param nKey
     *            The entry primary key
     * @param plugin
     *            the Plugin
     * @return an instance of Record field
     */
    public static RecordField findByPrimaryKey( int nKey, Plugin plugin )
    {
        RecordField recordField = _dao.load( nKey, plugin );

        loadSubObjects( recordField, plugin );

        return recordField;
    }

    /**
     * Returns an instance of a Record field whose file identifier is specified in parameter
     *
     * @param nIdFile
     *            The entry primary key
     * @param plugin
     *            the Plugin
     * @return an instance of Record field
     */
    public static RecordField findByFile( int nIdFile, Plugin plugin )
    {
        RecordField recordField = _dao.loadByFile( nIdFile, plugin );

        loadSubObjects( recordField, plugin );
        return recordField;
    }

    /**
     * Replace "empty" sub object containing only an Id with "full" objects
     *
     * @param recordField
     *            The recordField
     * @param plugin
     *            the Plugin
     */
    private static void loadSubObjects( RecordField recordField, Plugin plugin )
    {
        if ( ( recordField != null ) && ( recordField.getFile( ) != null ) )
        {
            recordField.setFile( FileHome.findByPrimaryKey( recordField.getFile( ).getIdFile( ), plugin ) );
        }

        if ( ( recordField != null ) && ( recordField.getField( ) != null ) )
        {
            recordField.setField( FieldHome.findByPrimaryKey( recordField.getField( ).getIdField( ), plugin ) );
        }
    }

    /**
     * remove all record field who verify the filter
     * 
     * @param filter
     *            the filter
     * @param plugin
     *            the plugin
     *
     */
    public static void removeByFilter( RecordFieldFilter filter, Plugin plugin )
    {
        removeByFilter( filter, false, plugin );
    }

    /**
     * remove all record field who verify the filter
     * 
     * @param filter
     *            the filter
     * @param bRemoveByAsynchronousFiles
     *            True to remove by asynchronous files
     * @param plugin
     *            the plugin
     *
     */
    public static void removeByFilter( RecordFieldFilter filter, boolean bRemoveByAsynchronousFiles, Plugin plugin )
    {
        List<RecordField> listRecordField = _dao.selectListByFilter( filter, plugin );

        for ( RecordField recordField : listRecordField )
        {
            remove( recordField.getIdRecordField( ), bRemoveByAsynchronousFiles, plugin );
        }
    }

    /**
     * Remove list of record field by list of record id
     * 
     * @param lListRecordId
     *            the list of record id
     * @param plugin
     *            the plugin
     * @deprecated This function does not remove the associated files
     */
    public static void removeByListRecordId( List<Integer> lListRecordId, Plugin plugin )
    {
        _dao.deleteByListRecordId( lListRecordId, plugin );
    }

    /**
     * Load the data of all the record field who verify the filter and returns them in a list
     * 
     * @param filter
     *            the filter
     * @param plugin
     *            the plugin
     * @return the list of record fields
     */
    public static List<RecordField> getRecordFieldList( RecordFieldFilter filter, Plugin plugin )
    {
        return getRecordFieldList(  filter, true, true, true,  plugin );
    }

    /**
     * Load the data of all the record field who verify the filter and returns them in a list
     *
     * @param filter
     *            the filter
     * @param withFile
     *            if the recordField must be loaded with his file (if exists)
     * @param withField
     *            if the recordField must be loaded with his field (if exists)
     * @param withRecord
     *            if the recordField must be loaded with his record (if exists)
     * @param plugin
     *            the plugin
     * @return the list of record fields
     */
    public static List<RecordField> getRecordFieldList( RecordFieldFilter filter, boolean withFile, boolean withField, boolean withRecord, Plugin plugin )
    {
        List<RecordField> listRecordField = _dao.selectListByFilter( filter, plugin );
        IRecordService recordService = SpringContextService.getBean( RecordService.BEAN_SERVICE );

        if ( withFile || withField || withRecord )
        {
	        for ( RecordField recordField : listRecordField )
	        {
	            if ( withFile && recordField.getFile( ) != null )
	            {
	                recordField.setFile( FileHome.findByPrimaryKey( recordField.getFile( ).getIdFile( ), plugin ) );
	            }

	            if ( withField && recordField.getField( ) != null )
	            {
	                recordField.setField( FieldHome.findByPrimaryKey( recordField.getField( ).getIdField( ), plugin ) );
	            }

	            if ( withRecord && recordField.getRecord( ) != null )
	            {
	                recordField.setRecord( recordService.findByPrimaryKey( recordField.getRecord( ).getIdRecord( ), plugin ) );
	            }
	        }
        }

        return listRecordField;
    }

    /**
     * Load full record field data (except binary file data) of given list of Record id * /!\ include record data
     * 
     * @param lIdRecordList
     *            the list of record id
     * @param plugin
     *            the plugin
     * @return list of record
     */
    public static List<RecordField> getRecordFieldListByRecordIdList( List<Integer> lIdRecordList, Plugin plugin )
    {
        return _dao.getRecordFieldListByRecordIdList( lIdRecordList, plugin );
    }

    /**
     * Load full record field data (except binary file data) /!\ record data is NOT load, only the id
     * 
     * @param lEntryId
     *            List entry to load
     * @param nIdRecord
     *            the record Id
     * @param plugin
     *            the plugin
     * @param mapFieldEntry
     *            a map containing all fields associated to the list of entry
     * @return list of record
     */
    public static List<RecordField> getRecordFieldSpecificList( List<Integer> lEntryId, Integer nIdRecord, Plugin plugin, Map<Integer, Field> mapFieldEntry )
    {
        List<RecordField> listRecordField = _dao.selectSpecificList( lEntryId, nIdRecord, plugin );

        for ( RecordField recordField : listRecordField )
        {
            if ( recordField.getField( ) != null )
            {
                recordField.setField( mapFieldEntry.get( recordField.getField( ).getIdField( ) ) );
            }
        }

        return listRecordField;
    }

    /**
     * return the number of record field who verify the filter
     * 
     * @param filter
     *            the filter
     * @param plugin
     *            the plugin
     * @return the number of record field who verify the filter
     */
    public static int getCountRecordField( RecordFieldFilter filter, Plugin plugin )
    {
        return _dao.getCountByFilter( filter, plugin );
    }

    /**
     * Get the max number from a given id directory
     * 
     * @param nIdEntry
     *            the id of the entry
     * @param nIdDirectory
     *            the id directory
     * @param plugin
     *            {@link Plugin}
     * @return the max number
     */
    public static int findMaxNumber( int nIdEntry, int nIdDirectory, Plugin plugin )
    {
        return _dao.getMaxNumber( nIdEntry, nIdDirectory, plugin );
    }

    /**
     * Check if the given number is already on a record field or not. <br>
     * In other words, this method serves the purpose of checking the given number before creating a new record field since the entry type numbering should have
     * unique number.
     * 
     * @param nIdEntry
     *            the id entry
     * @param nIdDirectory
     *            the id directory
     * @param nNumber
     *            the number to check
     * @param plugin
     *            {@link Plugin}
     * @return true if it is already on, false otherwise
     */
    public static boolean isNumberOnARecordField( int nIdEntry, int nIdDirectory, int nNumber, Plugin plugin )
    {
        return _dao.isNumberOnARecordField( nIdEntry, nIdDirectory, nNumber, plugin );
    }

    /**
     * Load values of record field
     * 
     * @param lEntryId
     *            List entry to load
     * @param nIdRecord
     *            The record Id
     * @param plugin
     *            The plugin
     * @return list of record
     */
    public static List<RecordField> selectValuesList( List<Integer> lEntryId, Integer nIdRecord, Plugin plugin )
    {
        return _dao.selectValuesList( lEntryId, nIdRecord, plugin );
    }

    /**
     * Update the value of a record field
     * 
     * @param strNewValue
     *            The new value
     * @param nIdRecordField
     *            The id of the record field to update
     * @param plugin
     *            The plugin
     */
    public static void updateValue( String strNewValue, Integer nIdRecordField, Plugin plugin )
    {
        _dao.updateValue( strNewValue, nIdRecordField, plugin );
    }
}

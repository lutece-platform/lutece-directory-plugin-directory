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
package fr.paris.lutece.plugins.directory.business;

import fr.paris.lutece.portal.service.plugin.Plugin;

import java.util.List;


/**
 *
 * interface IRecordFieldDAO
 *
 */
public interface IRecordFieldDAO
{
    /**
     * Insert a new record field in the table.
     *
     * @param recordField instance of the RecordField object to insert
     * @param plugin the plugin
     */
    void insert( RecordField recordField, Plugin plugin );

    /**
     * Load the data of the record field from the table
     *
     * @param nIdRecordField The identifier of the entry
     * @param plugin the plugin
     * @return the instance of the Record Field
     */
    RecordField load( int nIdRecordField, Plugin plugin );

    /**
     * Delete the record field whose identifier is specified in parameter
     *
     * @param nIdRecordField The identifier of the record field
     * @param plugin the plugin
     */
    void delete( int nIdRecordField, Plugin plugin );

    /**
     * Delete list of record fields by list of record id
     * @param lListRecordId list of record id
     * @param plugin the plugin
     */
    void deleteByListRecordId( List<Integer> lListRecordId, Plugin plugin );

    /**
     * Update the record field in the table
     *
     * @param recordField instance of the record field object to update
     * @param plugin the plugin
     */
    void store( RecordField recordField, Plugin plugin );

    /**
     * Load the data of all the record field who verify the filter and returns them in a  list
     * @param filter the filter
     * @param plugin the plugin
     * @return  the list of record field
     */
    List<RecordField> selectListByFilter( RecordFieldFilter filter, Plugin plugin );

    /**
     * Load full record field data (except binary file data) of given list of Record id
     * * /!\ include record data
     * @param nIdRecord the Record id
     * @param plugin the plugin
     * @return list of record
     */
    List<RecordField> getRecordFieldListByRecordIdList( List<Integer> lIdRecordList, Plugin plugin );

    /**
     * Load full record field data (except binary file data)
     * /!\ record data is NOT load, only the id
     * @param lEntryId List entry to load
     * @param nIdRecord the record Id
     * @param plugin the plugin
     * @return list of record
     */
    List<RecordField> selectSpecificList( List<Integer> lEntryId, Integer nIdRecord, Plugin plugin );

    /**
     *  return the number of record field who verify the filter
     * @param filter  the filter
     * @param plugin the plugin
     * @return the number of record field who verify the filter
     */
    int getCountByFilter( RecordFieldFilter filter, Plugin plugin );

    /**
     * Get the max number from a given id directory
     * @param nIdEntryTypeNumbering the id of the entry type numbering
     * @param nIdDirectory the id directory
     * @param plugin {@link Plugin}
     * @return the max number
     */
    int getMaxNumber( int nIdEntryTypeNumbering, int nIdDirectory, Plugin plugin );

    /**
     * Check if the given number is already on a record field or not.
     * <br />
     * In other words, this method serves the purpose of checking the given number
     * before creating a new record field since the entry type numbering should
     * have unique number.
     * @param nIdEntryTypeNumbering the id entry type numbering
     * @param nIdDirectory the id directory
     * @param nNumber the number to check
     * @param plugin {@link Plugin}
     * @return true if it is already on, false otherwise
     */
    boolean isNumberOnARecordField( int nIdEntryTypeNumbering, int nIdDirectory, int nNumber, Plugin plugin );
}

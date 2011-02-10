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

import java.util.List;


/**
 *
 *  Interface IRecordDAO
 *
 */
public interface IRecordDAO
{
    /**
     * Insert a new record in the table.
     *
     * @param record instance of the Record object to insert
     * @param plugin the plugin
     * @return the key of the new record
     */
    int insert( Record record, Plugin plugin );

    /**
     * Load the data of the record from the table
     *
     * @param nIdRecord The identifier of the id record
     * @param plugin the plugin
     * @return the instance of the Record
     */
    Record load( int nIdRecord, Plugin plugin );

    /**
     * Test if the given directory record list as a worflow
     * @param nIdDirectory directory Id
     * @param plugin the plugin
     * @return true if has at least one
     */
    Boolean direcytoryRecordListHasWorkflow( int nIdDirectory, Plugin plugin );

    /**
     * Load a list of record
     * @param listId list of record id
     * @param plugin the plugin
     * @return list of Record
     */
    List<Record> loadList( List<Integer> listId, Plugin plugin );

    /**
     * Delete a record from the table
     *
     * @param nIdRecord The identifier of the record
     * @param plugin the plugin
     */
    void delete( int nIdRecord, Plugin plugin );

    /**
     * Delete list of record by directory id
     * @param nDirectoryId the directory id
     * @param plugin the plugin
     */
    void deleteRecordByDirectoryId( Integer nDirectoryId, Plugin plugin );

    /**
     * Update the the record in the table
     * @param record the record to update
     * @param plugin the plugin
     */
    void store( Record record, Plugin plugin );

    /**
     * Load the data of all the record who verify the filter and returns them in a  list
     * @param filter the filter
     * @param plugin the plugin
     * @return  the list of record
     */
    List<Record> selectListByFilter( RecordFieldFilter filter, Plugin plugin );

    /**
     * Count record who verify the filter
     * @param filter the filter
     * @param plugin the plugin
     * @return  the number of record
     */
    int selectCountByFilter( RecordFieldFilter filter, Plugin plugin );

    /**
     * Load the data of all the record id who verify the filter and returns them in a  list
     * @param filter the filter
     * @param plugin the plugin
     * @return  the list of record id
     */
    List<Integer> selectListIdByFilter( RecordFieldFilter filter, Plugin plugin );

    /**
     * Get directory id by record id
     * @param nRecordId the record id
     * @param plugin the plugin
     * @return directory id
     */
    Integer getDirectoryIdByRecordId( Integer nRecordId, Plugin plugin );
}

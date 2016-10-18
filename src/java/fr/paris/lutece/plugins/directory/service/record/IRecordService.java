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
package fr.paris.lutece.plugins.directory.service.record;

import fr.paris.lutece.plugins.directory.business.Record;
import fr.paris.lutece.plugins.directory.business.RecordFieldFilter;
import fr.paris.lutece.portal.service.plugin.Plugin;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 *
 * IRecordService
 *
 */
public interface IRecordService
{
    /**
     * Creation of an instance of record
     *
     * @param record
     *            The instance of the record which contains the informations to store
     * @param plugin
     *            the Plugin
     * @return the id of the new record
     */
    @Transactional( "directory.transactionManager" )
    int create( Record record, Plugin plugin );

    /**
     * Copy an instance of record
     *
     * @param record
     *            The instance of the record who must copy
     * @param plugin
     *            the Plugin
     * @return the id of the record
     */
    @Transactional( "directory.transactionManager" )
    int copy( Record record, Plugin plugin );

    /**
     * Update of the record which is specified in parameter
     *
     * @param record
     *            The instance of the record which contains the informations to update
     * @param plugin
     *            the Plugin
     */
    @Transactional( "directory.transactionManager" )
    void updateWidthRecordField( Record record, Plugin plugin );

    /**
     * Update of the record
     *
     * @param record
     *            The instance of the record which contains the informations to update
     * @param plugin
     *            the Plugin
     */
    @Transactional( "directory.transactionManager" )
    void update( Record record, Plugin plugin );

    /**
     * Remove the record whose identifier is specified in parameter
     *
     * @param nIdRecord
     *            The recordId
     * @param plugin
     *            the Plugin
     */
    @Transactional( "directory.transactionManager" )
    void remove( int nIdRecord, Plugin plugin );

    // /////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Returns an instance of a recordwhose identifier is specified in parameter
     *
     * @param nKey
     *            The formResponse primary key
     * @param plugin
     *            the Plugin
     * @return an instance of FormResponse
     */
    Record findByPrimaryKey( int nKey, Plugin plugin );

    /**
     * Test if the given directory record list has a worflow
     * 
     * @param nIdDirectory
     *            directory Id
     * @param plugin
     *            the plugin
     * @return true if has at least one
     */
    Boolean directoryRecordListHasWorkflow( int nIdDirectory, Plugin plugin );

    /**
     * Load a list of record
     * 
     * @param lIdList
     *            list of record id
     * @param plugin
     *            the plugin
     * @return list of Record
     */
    List<Record> loadListByListId( List<Integer> lIdList, Plugin plugin );

    /**
     * Load the data of all the record who verify the filter and returns them in a list
     * 
     * @param filter
     *            the filter
     * @param plugin
     *            the plugin
     * @return the list of record
     */
    List<Record> getListRecord( RecordFieldFilter filter, Plugin plugin );

    /**
     * Count record who verify the filter
     * 
     * @param filter
     *            the filter
     * @param plugin
     *            the plugin
     * @return the number of record
     */
    int getCountRecord( RecordFieldFilter filter, Plugin plugin );

    /**
     * Load the data of all the record who verify the filter and returns them in a list
     * 
     * @param filter
     *            the filter
     * @param plugin
     *            the plugin
     * @return the list of record
     */
    List<Integer> getListRecordId( RecordFieldFilter filter, Plugin plugin );

    /**
     * Get directory id by by record id
     * 
     * @param nRecordId
     *            the record id
     * @param plugin
     *            the plugin
     * @return the directory id
     */
    Integer getDirectoryIdByRecordId( Integer nRecordId, Plugin plugin );
}

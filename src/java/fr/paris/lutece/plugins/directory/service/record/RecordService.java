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
import fr.paris.lutece.plugins.directory.business.RecordHome;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.resource.ExtendableResourceRemovalListenerService;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 *
 * RecordService
 *
 */
public class RecordService implements IRecordService
{
    /**
     * Name of the bean of this service
     */
    public static final String BEAN_SERVICE = "directory.recordService";

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( "directory.transactionManager" )
    public int copy( Record record, Plugin plugin )
    {
        return RecordHome.copy( record, plugin );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( "directory.transactionManager" )
    public int create( Record record, Plugin plugin )
    {
        return RecordHome.create( record, plugin );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( "directory.transactionManager" )
    public void remove( int nIdRecord, Plugin plugin )
    {
        ExtendableResourceRemovalListenerService.doRemoveResourceExtentions( Record.EXTENDABLE_RESOURCE_TYPE, Integer.toString( nIdRecord ) );
        RecordHome.remove( nIdRecord, plugin );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( "directory.transactionManager" )
    public void update( Record record, Plugin plugin )
    {
        RecordHome.update( record, plugin );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( "directory.transactionManager" )
    public void updateWidthRecordField( Record record, Plugin plugin )
    {
        RecordHome.updateWidthRecordField( record, plugin );
    }

    // FINDERS

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean directoryRecordListHasWorkflow( int nIdDirectory, Plugin plugin )
    {
        return RecordHome.direcytoryRecordListHasWorkflow( nIdDirectory, plugin );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Record findByPrimaryKey( int nKey, Plugin plugin )
    {
        return RecordHome.findByPrimaryKey( nKey, plugin );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getCountRecord( RecordFieldFilter filter, Plugin plugin )
    {
        return RecordHome.getCountRecord( filter, plugin );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getDirectoryIdByRecordId( Integer nRecordId, Plugin plugin )
    {
        return RecordHome.getDirectoryIdByRecordId( nRecordId, plugin );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Record> getListRecord( RecordFieldFilter filter, Plugin plugin )
    {
        return RecordHome.getListRecord( filter, plugin );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Integer> getListRecordId( RecordFieldFilter filter, Plugin plugin )
    {
        return RecordHome.getListRecordId( filter, plugin );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Record> loadListByListId( List<Integer> lIdList, Plugin plugin )
    {
        return RecordHome.loadListByListId( lIdList, plugin );
    }
}

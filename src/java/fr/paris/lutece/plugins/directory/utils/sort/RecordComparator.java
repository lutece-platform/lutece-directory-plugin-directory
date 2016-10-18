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
package fr.paris.lutece.plugins.directory.utils.sort;

import fr.paris.lutece.plugins.directory.business.EntryTypeGeolocation;
import fr.paris.lutece.plugins.directory.business.IEntry;
import fr.paris.lutece.plugins.directory.business.Record;
import fr.paris.lutece.plugins.directory.business.RecordField;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.sort.AttributeComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 *
 * RecordComparator
 *
 */
public class RecordComparator implements Comparator<Record>
{
    private static final String CONSTANT_VALUE = "value";
    private static final String EMPTY_STRING = "";
    private IEntry _entry;
    private boolean _bIsAscSort;

    /**
     * Constructor
     * 
     * @param entry
     *            the entry to compare
     * @param bIsAscSort
     *            true if it is sorted asc, false otherwise
     */
    public RecordComparator( IEntry entry, boolean bIsAscSort )
    {
        _entry = entry;
        _bIsAscSort = bIsAscSort;
    }

    /**
     * Compare two records
     * 
     * @param r1
     *            Record 1
     * @param r2
     *            Record 2
     * @return < 0 if r1 is before r2 in the alphabetical order 0 if r1 equals r2 > 0 if r1 is after r2
     */
    @Override
    public int compare( Record r1, Record r2 )
    {
        int nStatus = 0;
        RecordField rf1 = getRecordFieldToCompare( r1 );
        RecordField rf2 = getRecordFieldToCompare( r2 );

        if ( ( rf1 != null ) && ( rf2 != null ) )
        {
            if ( _entry instanceof fr.paris.lutece.plugins.directory.business.EntryTypeDate )
            {
                try
                {
                    Long lTime1 = Long.parseLong( rf1.getValue( ) );
                    Date date1 = new Date( lTime1 );
                    Long lTime2 = Long.parseLong( rf2.getValue( ) );
                    Date date2 = new Date( lTime2 );
                    nStatus = date1.compareTo( date2 );
                }
                catch( Exception e )
                {
                    AppLogService.error( e );
                }
            }
            else
                if ( _entry instanceof fr.paris.lutece.plugins.directory.business.EntryTypeNumbering )
                {
                    try
                    {
                        nStatus = Integer.parseInt( rf1.getValue( ) ) - Integer.parseInt( rf2.getValue( ) );
                    }
                    catch( Exception e )
                    {
                        AppLogService.error( e );
                    }
                }
                else
                {
                    if ( ( rf1.getValue( ) == null ) && ( rf2.getValue( ) != null ) )
                    {
                        nStatus = -1;
                    }
                    else
                        if ( ( rf1.getValue( ) != null ) && ( rf2.getValue( ) == null ) )
                        {
                            nStatus = 1;
                        }
                        else
                            if ( ( rf1.getValue( ) != null ) && ( rf2.getValue( ) != null ) )
                            {
                                nStatus = rf1.getValue( ).compareToIgnoreCase( rf2.getValue( ) );
                            }
                }
        }

        if ( !_bIsAscSort )
        {
            nStatus = nStatus * ( -1 );
        }

        return nStatus;
    }

    /**
     * Get the record field
     * 
     * @param r
     *            Record
     * @return RecordField
     */
    private RecordField getRecordFieldToCompare( Record r )
    {
        RecordField rf = null;

        if ( _entry instanceof fr.paris.lutece.plugins.directory.business.EntryTypeCheckBox
                || _entry instanceof fr.paris.lutece.plugins.directory.business.EntryTypeSelect )
        {
            List<RecordField> listRecordFields = new ArrayList<RecordField>( );

            for ( RecordField recordField : r.getListRecordField( ) )
            {
                if ( recordField.getEntry( ).getIdEntry( ) == _entry.getIdEntry( ) )
                {
                    if ( recordField.getValue( ) == null )
                    {
                        recordField.setValue( EMPTY_STRING );
                    }

                    listRecordFields.add( recordField );
                }
            }

            Collections.sort( listRecordFields, new AttributeComparator( CONSTANT_VALUE, _bIsAscSort ) );

            if ( listRecordFields.size( ) > 0 )
            {
                rf = listRecordFields.get( 0 );
            }
        }
        else
            if ( _entry instanceof fr.paris.lutece.plugins.directory.business.EntryTypeGeolocation )
            {
                for ( RecordField recordField : r.getListRecordField( ) )
                {
                    if ( ( recordField.getEntry( ).getIdEntry( ) == _entry.getIdEntry( ) )
                            && recordField.getField( ).getTitle( ).equals( EntryTypeGeolocation.CONSTANT_ADDRESS ) )
                    {
                        rf = recordField;
                    }
                }
            }
            else
                if ( _entry instanceof fr.paris.lutece.plugins.directory.business.EntryTypeDate
                        || _entry instanceof fr.paris.lutece.plugins.directory.business.EntryTypeDirectory
                        || _entry instanceof fr.paris.lutece.plugins.directory.business.EntryTypeMail
                        || _entry instanceof fr.paris.lutece.plugins.directory.business.EntryTypeUrl
                        || _entry instanceof fr.paris.lutece.plugins.directory.business.EntryTypeNumbering
                        || _entry instanceof fr.paris.lutece.plugins.directory.business.EntryTypeSQL
                        || _entry instanceof fr.paris.lutece.plugins.directory.business.EntryTypeText
                        || _entry instanceof fr.paris.lutece.plugins.directory.business.EntryTypeTextArea
                        || _entry instanceof fr.paris.lutece.plugins.directory.business.EntryTypeRadioButton
                        || _entry instanceof fr.paris.lutece.plugins.directory.business.EntryTypeRichText
                        || _entry instanceof fr.paris.lutece.plugins.directory.business.EntryTypeInternalLink
                        || _entry instanceof fr.paris.lutece.plugins.directory.business.EntryTypeMyLuteceUser )
                {
                    for ( RecordField recordField : r.getListRecordField( ) )
                    {
                        if ( recordField.getEntry( ).getIdEntry( ) == _entry.getIdEntry( ) )
                        {
                            rf = recordField;
                        }
                    }
                }

        return rf;
    }
}

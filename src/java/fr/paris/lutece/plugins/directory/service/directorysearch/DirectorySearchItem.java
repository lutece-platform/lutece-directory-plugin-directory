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
package fr.paris.lutece.plugins.directory.service.directorysearch;

import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;

import org.apache.lucene.document.Document;


/**
 * DirectorySearchItem
 */
public class DirectorySearchItem
{
    public static final String FIELD_ID_DIRECTORY = "id_directory";
    public static final String FIELD_ID_DIRECTORY_RECORD = "id_directory_record";
    public static final String FIELD_ID_DIRECTORY_ENTRY = "id_directory_entry";
    public static final String FIELD_ID_DIRECTORY_FIELD = "id_directory_field";
    public static final String FIELD_CONTENTS = "contents";
    public static final String FIELD_DATE = "date";
    public static final String FIELD_DATE_BEGIN = "date_begin";
    public static final String FIELD_DATE_END = "date_end";
    public static final String FIELD_WORKGROUP_KEY = "workgroup_key";
    public static final String FIELD_ROLE_KEY = "role_key";
    public static final String FIELD_DATE_CREATION = "date_creation";
    public static final String FIELD_DATE_CREATION_BEGIN = "date_creation_begin";
    public static final String FIELD_DATE_CREATION_END = "date_creation_end";
    public static final String FIELD_DATE_MODIFICATION = "date_modification";
    public static final String FIELD_DATE_MODIFICATION_BEGIN = "date_modification_begin";
    public static final String FIELD_DATE_MODIFICATION_END = "date_modification_end";

    // Variables declarations
    private int _nIdDirectoryRecord;

    /**
     * Create DirectorySearchItem object
     * @param document lucene document
     */
    public DirectorySearchItem( Document document )
    {
        setIdDirectoryRecord( DirectoryUtils.convertStringToInt( document.get( FIELD_ID_DIRECTORY_RECORD ) ) );
    }

    /**
     * @return the id of the directory record
     */
    public int getIdDirectoryRecord(  )
    {
        return _nIdDirectoryRecord;
    }

    /**
     * @param idDirectoryRecord the id of the directory record
     */
    public void setIdDirectoryRecord( int idDirectoryRecord )
    {
        _nIdDirectoryRecord = idDirectoryRecord;
    }
}

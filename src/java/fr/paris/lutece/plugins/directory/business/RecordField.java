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

import org.apache.commons.lang.StringUtils;


/**
 *
 * class RecordField
 *
 */
public class RecordField
{
    public static final String ATTRIBUTE_GEOLOCATION = "geolocation";
    private static final String CONSTANT_LITTLE_THUMBNAIL = "little_thumbnail";
    private static final String CONSTANT_BIG_THUMBNAIL = "big_thumbnail";
    private int _nIdRecordField;
    private String _strValue;
    private IEntry _entry;
    private Field _field;
    private File _file;
    private Record _record;
    /**
     * This attribute is used for EntryTypeDownloadURL
     * @see {@link EntryTypeDownloadUrl}
     */
    private String _strFileName;
    /**
     * This attribute is used for EntryTypeDownloadURL
     * @see {@link EntryTypeDownloadUrl}
     */
    private String _strFileExtension;

    /**
     *
     * @return the record associate to the field
     */
    public Record getRecord(  )
    {
        return _record;
    }

    /**
     * the record associate to the field
     * @param record the record
     */
    public void setRecord( Record record )
    {
        _record = record;
    }

    /**
    *
    * @return the entry associate to the field
    */
    public IEntry getEntry(  )
    {
        return _entry;
    }

    /**
     * set the entry associate to the field
     * @param entry the entry associate to the field
     */
    public void setEntry( IEntry entry )
    {
        _entry = entry;
    }

    /**
     *
     * @return the id of the response
     */
    public int getIdRecordField(  )
    {
        return _nIdRecordField;
    }

    /**
     * set the id of the record field
     * @param idRecordField the id of the record field
     */
    public void setIdRecordField( int idRecordField )
    {
        _nIdRecordField = idRecordField;
    }

    /**
     *
     * @return the value of the record field
     */
    public String getValue(  )
    {
        return _strValue;
    }

    /**
     * set the value of the record field
     * @param valueRecordField the value of the record field
     */
    public void setValue( String valueRecordField )
    {
        _strValue = valueRecordField;
    }

    /**
     * get the field associate to the response
     * @return the field associate to the response
     */
    public Field getField(  )
    {
        return _field;
    }

    /**
     * set the field associate to the response
     * @param field field
     */
    public void setField( Field field )
    {
        this._field = field;
    }

    /**
     *
     * @return the file associate to the record field
     */
    public File getFile(  )
    {
        return _file;
    }

    /**
     * set the file associate to the record field
     * @param file the file
     */
    public void setFile( File file )
    {
        _file = file;
    }

    public boolean isLittleThumbnail(  )
    {
        if ( StringUtils.isNotBlank( _strValue ) && ( _strValue.startsWith( CONSTANT_LITTLE_THUMBNAIL ) ) )
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean isBigThumbnail(  )
    {
        if ( StringUtils.isNotBlank( _strValue ) && ( _strValue.startsWith( CONSTANT_BIG_THUMBNAIL ) ) )
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     *
     * @return the value of the recordField
     */
    public String toString(  )
    {
        return _strValue;
    }
    
    /**
     * Get the file extensions
     * This attribute is used for EntryTypeDownloadURL
     * @see {@link EntryTypeDownloadUrl}
     * @return the file extension if the response value is a file
     */
    public String getFileExtension(  )
	{
    	return _strFileExtension;
	}

	/**
     * Set the file extension if the response value is a file
     * This attribute is used for EntryTypeDownloadURL
     * @see {@link EntryTypeDownloadUrl}
     * @param fileExtension the file extension if the response value is a file
     */
    public void setFileExtension( String fileExtension )
    {
    	_strFileExtension = fileExtension;
	}

	/**
	 * The file name if the response value is a file
	 * This attribute is used for EntryTypeDownloadURL
     * @see {@link EntryTypeDownloadUrl}
	 * @return the file name if the response value is a file
	 */
	public String getFileName(  )
	{
		return _strFileName;
	}

	/**
	 * The file name if the response value is a file
	 * This attribute is used for EntryTypeDownloadURL
     * @see {@link EntryTypeDownloadUrl}
	 * @param fileName the file name if the response value is a file
     */
	public void setFileName( String fileName )
	{
		_strFileName = fileName;
	}
}

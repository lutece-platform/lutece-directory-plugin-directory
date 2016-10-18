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
package fr.paris.lutece.plugins.directory.utils;

import fr.paris.lutece.plugins.directory.service.upload.DirectoryAsynchronousUploadHandler;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * JSONUtils
 *
 */
public final class JSONUtils
{
    // JSON
    private static final String JSON_KEY_FORM_ERROR = "directory_error";
    private static final String JSON_KEY_SUCCESS = "success";
    private static final String JSON_KEY_FIELD_NAME = "field_name";
    private static final String JSON_KEY_UPLOADED_FILES = "uploadedFiles";
    private static final String JSON_KEY_UPLOADED_FILES_SIZE = "uploadedFilesSize";
    private static final String JSON_KEY_FILE_COUNT = "fileCount";
    private static final String KEY_USER_ATTRIBUTES = "user-attributes";

    /**
     * Private constructor
     */
    private JSONUtils( )
    {
    }

    /**
     * Builds a json object with the error message.
     * 
     * @param strMessage
     *            the error message
     * @return the json object.
     */
    public static JSONObject buildJsonError( String strMessage )
    {
        JSONObject json = new JSONObject( );
        buildJsonError( json, strMessage );

        return json;
    }

    /**
     * Builds a json object with the error message.
     * 
     * @param json
     *            the JSON
     * @param strMessage
     *            the error message
     */
    public static void buildJsonError( JSONObject json, String strMessage )
    {
        if ( json != null )
        {
            json.accumulate( JSON_KEY_FORM_ERROR, strMessage );
        }
    }

    /**
     * Build the json form success removing file
     * 
     * @param strIdEntry
     *            the id entry
     * @param strSessionId
     *            the session id
     * @return the json object
     */
    public static JSONObject buildJsonSuccess( String strIdEntry, String strSessionId )
    {
        JSONObject json = new JSONObject( );
        json.accumulateAll( getUploadedFileJSON( DirectoryAsynchronousUploadHandler.getHandler( ).getFileItems( strIdEntry, strSessionId ) ) );
        buildJsonSuccess( DirectoryAsynchronousUploadHandler.getHandler( ).buildFieldName( strIdEntry ), json );

        return json;
    }

    /**
     * Build the json form success removing file
     * 
     * @param strFieldName
     *            the field name (WARNING : it is not the id entry, it is 'directory_<id_entry>', ex: directory_11)
     * @param json
     *            the JSON object
     */
    public static void buildJsonSuccess( String strFieldName, JSONObject json )
    {
        if ( json != null )
        {
            // operation successful
            json.element( JSON_KEY_SUCCESS, JSONUtils.JSON_KEY_SUCCESS );
            json.element( JSON_KEY_FIELD_NAME, strFieldName );
        }
    }

    /**
     * Builds a json object for the file item list. Key is {@link #JSON_KEY_UPLOADED_FILES}, value is the array of uploaded file.
     * 
     * @param listFileItem
     *            the fileItem list
     * @return the json
     */
    public static JSONObject getUploadedFileJSON( List<FileItem> listFileItem )
    {
        JSONObject json = new JSONObject( );

        if ( listFileItem != null )
        {
            for ( FileItem fileItem : listFileItem )
            {
                json.accumulate( JSON_KEY_UPLOADED_FILES, fileItem.getName( ) );
                json.accumulate( JSON_KEY_UPLOADED_FILES_SIZE, fileItem.getSize( ) );
            }

            json.element( JSON_KEY_FILE_COUNT, listFileItem.size( ) );
        }
        else
        {
            // no file
            json.element( JSON_KEY_FILE_COUNT, 0 );
        }

        return json;
    }

    /**
     * Get the user infos. <br />
     * The json must be written with the following format : <br />
     * <code>
     * <br />{ "user-attributes": [
     * <br />{ "user-attribute-key": "user.name.family", "user-attribute-value": "FAMILYNAME" },
     * <br />{ "user-attribute-key": "user.home-info.online.email", "user-attribute-value": "EMAIL@EMAIL.EMAIL"}
     * <br />] }
     * </code>
     * 
     * @param strJSON
     *            the json
     * @return the user attributes
     */
    public static Map<String, String> getUserInfos( String strJSON )
    {
        Map<String, String> userInfos = new HashMap<String, String>( );

        if ( StringUtils.isNotBlank( strJSON ) )
        {
            // Get object "user-attributes"
            JSONObject json = (JSONObject) JSONSerializer.toJSON( strJSON );

            if ( json != null )
            {
                // Get sub-objects of "user-attributes"
                JSONArray arrayUserAttributes = json.getJSONArray( KEY_USER_ATTRIBUTES );

                if ( arrayUserAttributes != null )
                {
                    // Browse each user attribute
                    for ( int i = 0; i < arrayUserAttributes.size( ); i++ )
                    {
                        put( userInfos, arrayUserAttributes.getJSONObject( i ) );
                    }
                }
            }
        }

        return userInfos;
    }

    /**
     * Insert user attribute to the map
     * 
     * @param userInfos
     *            the map
     * @param userAttribute
     *            the user attribute
     */
    private static void put( Map<String, String> userInfos, JSONObject userAttribute )
    {
        if ( userAttribute != null )
        {
            JSONArray listCodes = userAttribute.names( );

            for ( int i = 0; i < listCodes.size( ); i++ )
            {
                String strCode = listCodes.getString( i );
                String strValue = userAttribute.getString( strCode );
                userInfos.put( strCode, strValue );
            }
        }
    }
}

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
package fr.paris.lutece.plugins.directory.web;

import fr.paris.lutece.plugins.directory.service.upload.DirectoryAsynchronousUploadHandler;
import fr.paris.lutece.plugins.directory.utils.JSONUtils;
import fr.paris.lutece.portal.service.i18n.I18nService;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONSerializer;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;


/**
 *
 * This class is called by the directoryupload.js. It is used to
 * remove the file from the asynchronous map store in {@link DirectoryAsynchronousUploadHandler}.
 *
 */
public class DoRemoveAsynchronousFile
{
    private static final String PARAMETER_ID_ENTRY = "id_entry";
    private static final String PARAMETER_FIELD_INDEX = "field_index";
    private static final String PROPERTY_MESSAGE_ERROR_REMOVING_FILE = "directory.message.error.removingFile";

    /**
    * Removes the uploaded fileItem.
    * <br />
    * This method is called by the JSP <b>jsp/site/plugins/directory/DoRemoveFile.jsp</b>
    * because this method is also used in front office in other module (module-workflow-editrecord).
    * @param request the request
    * @category CALLED_BY_JS (directoryupload.js)
    */
    public String doRemoveAsynchronousUploadedFile( HttpServletRequest request )
    {
        String strSessionId = request.getSession(  ).getId(  );
        String strIdEntry = request.getParameter( PARAMETER_ID_ENTRY );
        String strFieldIndex = request.getParameter( PARAMETER_FIELD_INDEX );
        String strErrorMessage = I18nService.getLocalizedString( PROPERTY_MESSAGE_ERROR_REMOVING_FILE,
                request.getLocale(  ) );

        if ( StringUtils.isBlank( strIdEntry ) || StringUtils.isBlank( strFieldIndex ) )
        {
            return JSONUtils.buildJsonError( strErrorMessage ).toString(  );
        }

        // parse json
        JSON jsonFieldIndexes = JSONSerializer.toJSON( strFieldIndex );

        if ( !jsonFieldIndexes.isArray(  ) )
        {
            return JSONUtils.buildJsonError( strErrorMessage ).toString(  );
        }

        JSONArray jsonArrayFieldIndexers = (JSONArray) jsonFieldIndexes;
        int[] tabFieldIndex = new int[jsonArrayFieldIndexers.size(  )];

        for ( int nIndex = 0; nIndex < jsonArrayFieldIndexers.size(  ); nIndex++ )
        {
            try
            {
                tabFieldIndex[nIndex] = Integer.parseInt( jsonArrayFieldIndexers.getString( nIndex ) );
            }
            catch ( NumberFormatException nfe )
            {
                return JSONUtils.buildJsonError( strErrorMessage ).toString(  );
            }
        }

        // inverse order (removing using index - remove greater first to keep order)
        Arrays.sort( tabFieldIndex );
        ArrayUtils.reverse( tabFieldIndex );

        for ( int nFieldIndex : tabFieldIndex )
        {
            DirectoryAsynchronousUploadHandler.getHandler(  ).removeFileItem( strIdEntry, strSessionId, nFieldIndex );
        }

        return JSONUtils.buildJsonSuccess( strIdEntry, strSessionId ).toString(  );
    }
}

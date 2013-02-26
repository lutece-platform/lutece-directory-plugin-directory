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
package fr.paris.lutece.plugins.directory.service.parameter;

import fr.paris.lutece.plugins.directory.business.parameter.DirectoryParameterFilter;
import fr.paris.lutece.plugins.directory.business.parameter.DirectoryParameterHome;
import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;


/**
 *
 * DirectoryParameterService
 *
 */
public final class DirectoryParameterService
{
    private static final String BEAN_DIRECTORY_PARAMETER_SERVICE = "directory.directoryParameterService";

    // PARAMETERS
    private static final String PARAMETER_EXPORT_CSV_ENCODING = "export_csv_encoding";
    private static final String PARAMETER_EXPORT_XML_ENCODING = "export_xml_encoding";

    // PROPERTIES
    private static final String PROPERTY_DEFAULT_EXPORT_ENCODING = "directory.export.encoding.default";

    /**
     * Get the instance of the service
     * @return the instance of the service
     */
    public static DirectoryParameterService getService(  )
    {
        return SpringContextService.getBean( BEAN_DIRECTORY_PARAMETER_SERVICE );
    }

    /**
     * Find all directory parameters
     * @return a {@link ReferenceList}
     */
    public ReferenceList findAll(  )
    {
        return DirectoryParameterHome.findAll( DirectoryUtils.getPlugin(  ) );
    }

    /**
     * Find the default value parameters of the directory
     * @return a {@link ReferenceList}
     */
    public ReferenceList findDefaultValueParameters(  )
    {
        DirectoryParameterFilter filter = new DirectoryParameterFilter(  );
        filter.setExcludeParameterKeys( true );
        filter.addParameterKey( PARAMETER_EXPORT_CSV_ENCODING );
        filter.addParameterKey( PARAMETER_EXPORT_XML_ENCODING );

        return DirectoryParameterHome.findByFilter( filter, DirectoryUtils.getPlugin(  ) );
    }

    /**
     * Find the export parameters
     * @return a {@link ReferenceList}
     */
    public ReferenceList findExportEncodingParameters(  )
    {
        DirectoryParameterFilter filter = new DirectoryParameterFilter(  );
        filter.setExcludeParameterKeys( false );
        filter.addParameterKey( PARAMETER_EXPORT_CSV_ENCODING );
        filter.addParameterKey( PARAMETER_EXPORT_XML_ENCODING );

        return DirectoryParameterHome.findByFilter( filter, DirectoryUtils.getPlugin(  ) );
    }

    /**
    * Load the parameter value
    * @param strParameterKey the parameter key
    * @return The parameter value
    */
    public ReferenceItem findByKey( String strParameterKey )
    {
        return DirectoryParameterHome.findByKey( strParameterKey, DirectoryUtils.getPlugin(  ) );
    }

    /**
     * Update the parameter value
     * @param strParameterKey The parameter key
     * @param strParameterValue The parameter value
     */
    public void update( ReferenceItem param )
    {
        DirectoryParameterHome.update( param, DirectoryUtils.getPlugin(  ) );
    }

    /**
     * Get the encoding for export CSV
     * @return the encoding for export CSV
     */
    public String getExportCSVEncoding(  )
    {
        ReferenceItem param = findByKey( PARAMETER_EXPORT_CSV_ENCODING );

        if ( param == null )
        {
            return AppPropertiesService.getProperty( PROPERTY_DEFAULT_EXPORT_ENCODING );
        }

        return param.getName(  );
    }

    /**
     * Get the encoding for export XML
     * @return the encoding for export XML
     */
    public String getExportXMLEncoding(  )
    {
        ReferenceItem param = findByKey( PARAMETER_EXPORT_XML_ENCODING );

        if ( param == null )
        {
            return AppPropertiesService.getProperty( PROPERTY_DEFAULT_EXPORT_ENCODING );
        }

        return param.getName(  );
    }
}

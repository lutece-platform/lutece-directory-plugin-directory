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
package fr.paris.lutece.plugins.directory.web.action;

import fr.paris.lutece.plugins.directory.business.Category;
import fr.paris.lutece.plugins.directory.business.Directory;
import fr.paris.lutece.plugins.directory.business.DirectoryHome;
import fr.paris.lutece.plugins.directory.business.DirectoryXsl;
import fr.paris.lutece.plugins.directory.business.DirectoryXslFilter;
import fr.paris.lutece.plugins.directory.business.DirectoryXslHome;
import fr.paris.lutece.plugins.directory.business.Entry;
import fr.paris.lutece.plugins.directory.business.EntryFilter;
import fr.paris.lutece.plugins.directory.business.EntryHome;
import fr.paris.lutece.plugins.directory.business.File;
import fr.paris.lutece.plugins.directory.business.FileHome;
import fr.paris.lutece.plugins.directory.business.IEntry;
import fr.paris.lutece.plugins.directory.business.PhysicalFile;
import fr.paris.lutece.plugins.directory.business.PhysicalFileHome;
import fr.paris.lutece.plugins.directory.business.Record;
import fr.paris.lutece.plugins.directory.business.RecordFieldFilter;
import fr.paris.lutece.plugins.directory.business.RecordHome;
import fr.paris.lutece.plugins.directory.service.DirectoryPlugin;
import fr.paris.lutece.plugins.directory.service.DirectoryResourceIdService;
import fr.paris.lutece.plugins.directory.service.parameter.DirectoryParameterService;
import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.business.workflow.State;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.html.XmlTransformerService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
import fr.paris.lutece.portal.web.pluginaction.AbstractPluginAction;
import fr.paris.lutece.portal.web.pluginaction.DefaultPluginActionResult;
import fr.paris.lutece.portal.web.pluginaction.IPluginActionResult;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.UniqueIDGenerator;
import fr.paris.lutece.util.filesystem.FileSystemUtil;
import fr.paris.lutece.util.string.StringUtil;
import fr.paris.lutece.util.xml.XmlUtil;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Exports records (search records or all records)
 *
 */
public class ExportDirectoryAction extends AbstractPluginAction<DirectoryAdminSearchFields> implements IDirectoryAction
{
    private static final String ACTION_NAME = "Export Directory XSL";
    private static final String TEMPLATE_BUTTON = "actions/export.html";
    private static final String PROPERTY_PATH_TMP = "path.tmp";
    private static final String PROPERTY_ENTRY_TYPE_DATE_CREATION_TITLE = "directory.entry_type_date_creation.title";
    private static final String PROPERTY_ENTRY_TYPE_DATE_MODIFICATION_TITLE = "directory.entry_type_date_modification.title";
    private static final String PARAMETER_BUTTON_EXPORT_ALL = "export_search_all.x";
    private static final String PARAMETER_BUTTON_EXPORT_SEARCH = "export_search_result.x";
    private static final String PARAMETER_ID_DIRECTORY = "id_directory";
    private static final String PARAMETER_ID_DIRECTORY_XSL = "id_directory_xsl";
    private static final String TAG_STATUS = "status";
    private static final String TAG_DISPLAY = "display";
    private static final String TAG_YES = "yes";
    private static final String TAG_NO = "no";
    private static final String XSL_UNIQUE_PREFIX_ID = UniqueIDGenerator.getNewId(  ) + "directory-";

    // Export
    private static final int EXPORT_STRINGBUFFER_MAX_CONTENT_SIZE = 500000;
    private static final int EXPORT_STRINGBUFFER_INITIAL_SIZE = 600000;
    private static final int EXPORT_RECORD_STEP = 100;
    private static final String EXPORT_TMPFILE_PREFIX = "exportDirectory";
    private static final String EXPORT_TMPFILE_SUFIX = ".part";
    private static final String EXPORT_XSL_BEGIN_PARTIAL_EXPORT = "<partialexport>";
    private static final String EXPORT_XSL_END_PARTIAL_EXPORT = "</partialexport>";
    private static final String EXPORT_XSL_BEGIN_LIST_RECORD = "<list-record>";
    private static final String EXPORT_XSL_END_LIST_RECORD = "</list-record>";
    private static final String EXPORT_XSL_EMPTY_LIST_RECORD = "<list-record/>";
    private static final String EXPORT_XSL_END_DIRECTORY = "</directory>";
    private static final String EXPORT_XSL_NEW_LINE = "\r\n";
    private static final String EXPORT_CSV_EXT = "csv";
    private static final String CONSTANT_MIME_TYPE_CSV = "application/csv";
    private static final String CONSTANT_MIME_TYPE_OCTETSTREAM = "application/octet-stream";
    private static final String MARK_XSL_EXPORT_LIST = "xsl_export_list";

    public void fillModel( HttpServletRequest request, AdminUser adminUser, Map<String, Object> model )
    {
        //add xslExport
        DirectoryXslFilter directoryXslFilter = new DirectoryXslFilter(  );

        directoryXslFilter.setIdCategory( Category.ID_CATEGORY_EXPORT );

        ReferenceList refListXslExport = DirectoryXslHome.getRefList( directoryXslFilter, getPlugin(  ) );
        model.put( MARK_XSL_EXPORT_LIST, refListXslExport );
    }

    /**
     * {@inheritDoc}
     */
    public String getName(  )
    {
        return ACTION_NAME;
    }

    /**
     * {@inheritDoc}
     */
    public String getButtonTemplate(  )
    {
        return TEMPLATE_BUTTON;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isInvoked( HttpServletRequest request )
    {
        return ( request.getParameter( PARAMETER_BUTTON_EXPORT_SEARCH ) != null ) ||
        ( request.getParameter( PARAMETER_BUTTON_EXPORT_ALL ) != null );
    }

    /**
     * {@inheritDoc}
     */
    public IPluginActionResult process( HttpServletRequest request, HttpServletResponse response, AdminUser adminUser,
        DirectoryAdminSearchFields searchFields ) throws AccessDeniedException
    {
        DefaultPluginActionResult result = new DefaultPluginActionResult(  );

        String strIdDirectory = request.getParameter( PARAMETER_ID_DIRECTORY );
        int nIdDirectory = DirectoryUtils.convertStringToInt( strIdDirectory );
        Directory directory = DirectoryHome.findByPrimaryKey( nIdDirectory, getPlugin(  ) );
        String strIdDirectoryXsl = request.getParameter( PARAMETER_ID_DIRECTORY_XSL );
        int nIdDirectoryXsl = DirectoryUtils.convertStringToInt( strIdDirectoryXsl );
        WorkflowService workflowService = WorkflowService.getInstance(  );
        boolean bWorkflowServiceEnable = workflowService.isAvailable(  );
        String strShotExportFinalOutPut = null;

        // -----------------------------------------------------------------------
        DirectoryXsl directoryXsl = DirectoryXslHome.findByPrimaryKey( nIdDirectoryXsl, getPlugin(  ) );
        String strFileExtension = directoryXsl.getExtension(  );
        String strFileName = directory.getTitle(  ) + "." + strFileExtension;
        strFileName = StringUtil.replaceAccent( strFileName ).replace( " ", "_" );

        boolean bIsCsvExport = strFileExtension.equals( EXPORT_CSV_EXT );
        boolean bDisplayDateCreation = directory.isDateShownInExport(  );
        boolean bDisplayDateModification = directory.isDateModificationShownInExport(  );

        if ( ( directory == null ) || ( directoryXsl == null ) ||
                !RBACService.isAuthorized( Directory.RESOURCE_TYPE, strIdDirectory,
                    DirectoryResourceIdService.PERMISSION_MANAGE_RECORD, adminUser ) )
        {
            throw new AccessDeniedException(  );
        }

        List<Integer> listResultRecordId = new ArrayList<Integer>(  );

        if ( request.getParameter( PARAMETER_BUTTON_EXPORT_SEARCH ) != null )
        {
            // sort order and sort entry are not needed in export
            listResultRecordId = DirectoryUtils.getListResults( request, directory, bWorkflowServiceEnable, true, null,
                    RecordFieldFilter.ORDER_NONE, searchFields, adminUser, adminUser.getLocale(  ) );
        }
        else
        {
            // sort order and sort entry are not needed in export
            listResultRecordId = DirectoryUtils.getListResults( request, directory, bWorkflowServiceEnable, false,
                    null, RecordFieldFilter.ORDER_NONE, searchFields, adminUser, adminUser.getLocale(  ) );
        }

        EntryFilter entryFilter = new EntryFilter(  );
        entryFilter.setIdDirectory( directory.getIdDirectory(  ) );
        entryFilter.setIsGroup( EntryFilter.FILTER_FALSE );
        entryFilter.setIsComment( EntryFilter.FILTER_FALSE );
        entryFilter.setIsShownInExport( EntryFilter.FILTER_TRUE );

        List<IEntry> listEntryResultSearch = EntryHome.getEntryList( entryFilter, getPlugin(  ) );
        StringBuffer strBufferListRecordXml = null;

        java.io.File tmpFile = null;
        BufferedWriter bufferedWriter = null;

        File fileTemplate = null;
        String strFileOutPut = DirectoryUtils.EMPTY_STRING;

        if ( directoryXsl.getFile(  ) != null )
        {
            fileTemplate = FileHome.findByPrimaryKey( directoryXsl.getFile(  ).getIdFile(  ), getPlugin(  ) );
        }

        XmlTransformerService xmlTransformerService = null;
        PhysicalFile physicalFile = null;
        String strXslId = null;

        if ( ( fileTemplate != null ) && ( fileTemplate.getPhysicalFile(  ) != null ) )
        {
            fileTemplate.setPhysicalFile( PhysicalFileHome.findByPrimaryKey( 
                    fileTemplate.getPhysicalFile(  ).getIdPhysicalFile(  ), getPlugin(  ) ) );

            xmlTransformerService = new XmlTransformerService(  );
            physicalFile = fileTemplate.getPhysicalFile(  );
            strXslId = XSL_UNIQUE_PREFIX_ID + physicalFile.getIdPhysicalFile(  );
        }

        int nSize = listResultRecordId.size(  );
        boolean bIsBigExport = ( nSize > EXPORT_RECORD_STEP );

        if ( bIsBigExport )
        {
            try
            {
                String strPath = AppPathService.getWebAppPath(  ) +
                    AppPropertiesService.getProperty( PROPERTY_PATH_TMP );
                java.io.File tmpDir = new java.io.File( strPath );
                tmpFile = java.io.File.createTempFile( EXPORT_TMPFILE_PREFIX, EXPORT_TMPFILE_SUFIX, tmpDir );
            }
            catch ( IOException e )
            {
                AppLogService.error( "Unable to create temp file in webapp tmp dir" );

                try
                {
                    tmpFile = java.io.File.createTempFile( EXPORT_TMPFILE_PREFIX, EXPORT_TMPFILE_SUFIX );
                }
                catch ( IOException e1 )
                {
                    AppLogService.error( e1 );
                }
            }

            try
            {
                tmpFile.deleteOnExit(  );
                bufferedWriter = new BufferedWriter( new FileWriter( tmpFile, true ) );
            }
            catch ( IOException e )
            {
                AppLogService.error( e );
            }
        }

        Plugin plugin = this.getPlugin(  );
        Locale locale = request.getLocale(  );

        // ---------------------------------------------------------------------
        StringBuffer strBufferListEntryXml = new StringBuffer(  );

        if ( bDisplayDateCreation && bIsCsvExport )
        {
            Map<String, String> model = new HashMap<String, String>(  );
            model.put( Entry.ATTRIBUTE_ENTRY_ID, "0" );
            XmlUtil.beginElement( strBufferListEntryXml, Entry.TAG_ENTRY, model );

            String strDateCreation = I18nService.getLocalizedString( PROPERTY_ENTRY_TYPE_DATE_CREATION_TITLE, locale );
            XmlUtil.addElementHtml( strBufferListEntryXml, Entry.TAG_TITLE, strDateCreation );
            XmlUtil.endElement( strBufferListEntryXml, Entry.TAG_ENTRY );
        }

        if ( bDisplayDateModification && bIsCsvExport )
        {
            Map<String, String> model = new HashMap<String, String>(  );
            model.put( Entry.ATTRIBUTE_ENTRY_ID, "0" );
            XmlUtil.beginElement( strBufferListEntryXml, Entry.TAG_ENTRY, model );

            String strDateModification = I18nService.getLocalizedString( PROPERTY_ENTRY_TYPE_DATE_MODIFICATION_TITLE,
                    locale );
            XmlUtil.addElementHtml( strBufferListEntryXml, Entry.TAG_TITLE, strDateModification );
            XmlUtil.endElement( strBufferListEntryXml, Entry.TAG_ENTRY );
        }

        for ( IEntry entry : listEntryResultSearch )
        {
            entry.getXml( plugin, locale, strBufferListEntryXml );
        }

        Map<String, String> model = new HashMap<String, String>(  );

        if ( ( directory.getIdWorkflow(  ) != DirectoryUtils.CONSTANT_ID_NULL ) && bWorkflowServiceEnable )
        {
            model.put( TAG_DISPLAY, TAG_YES );
        }
        else
        {
            model.put( TAG_DISPLAY, TAG_NO );
        }

        XmlUtil.addEmptyElement( strBufferListEntryXml, TAG_STATUS, model );

        StringBuilder strBufferDirectoryXml = new StringBuilder(  );
        strBufferDirectoryXml.append( XmlUtil.getXmlHeader(  ) );

        if ( bIsBigExport )
        {
            strBufferDirectoryXml.append( directory.getXml( plugin, locale, new StringBuffer(  ), strBufferListEntryXml ) );

            strBufferListRecordXml = new StringBuffer( EXPORT_STRINGBUFFER_INITIAL_SIZE );

            strFileOutPut = xmlTransformerService.transformBySourceWithXslCache( strBufferDirectoryXml.toString(  ),
                    physicalFile.getValue(  ), strXslId, null, null );

            String strFinalOutPut = null;

            if ( !bIsCsvExport )
            {
                int pos = strFileOutPut.indexOf( EXPORT_XSL_EMPTY_LIST_RECORD );
                strFinalOutPut = strFileOutPut.substring( 0, pos ) + EXPORT_XSL_BEGIN_LIST_RECORD;
            }
            else
            {
                strFinalOutPut = strFileOutPut;
            }

            try
            {
                bufferedWriter.write( strFinalOutPut );
            }
            catch ( IOException e )
            {
                AppLogService.error( e );
            }
        }
        else
        {
            strBufferListRecordXml = new StringBuffer(  );
        }

        // -----------------------------------------------------------------------
        List<Integer> nTmpListId = new ArrayList<Integer>(  );
        int idWorflow = directory.getIdWorkflow(  );

        if ( bIsBigExport )
        {
            int nXmlHeaderLength = XmlUtil.getXmlHeader(  ).length(  ) - 1;
            int max = nSize / EXPORT_RECORD_STEP;
            int max1 = nSize - EXPORT_RECORD_STEP;

            for ( int i = 0; i < max1; i += EXPORT_RECORD_STEP )
            {
                AppLogService.debug( "Directory export progress : " + ( ( (float) i / nSize ) * 100 ) + "%" );

                nTmpListId = new ArrayList<Integer>(  );

                int k = i + EXPORT_RECORD_STEP;

                for ( int j = i; j < k; j++ )
                {
                    nTmpListId.add( listResultRecordId.get( j ) );
                }

                List<Record> nTmpListRecords = RecordHome.loadListByListId( nTmpListId, plugin );

                for ( Record record : nTmpListRecords )
                {
                    State state = workflowService.getState( record.getIdRecord(  ), Record.WORKFLOW_RESOURCE_TYPE,
                            idWorflow, Integer.valueOf( directory.getIdDirectory(  ) ), null );
                    strBufferListRecordXml.append( record.getXml( plugin, locale, false, state, listEntryResultSearch,
                            false, false, true, bDisplayDateCreation, bDisplayDateModification ) );
                }

                strBufferListRecordXml = this.appendPartialContent( strBufferListRecordXml, bufferedWriter,
                        physicalFile, bIsCsvExport, strXslId, nXmlHeaderLength, xmlTransformerService );
            }

            // -----------------------------------------------------------------------
            int max2 = EXPORT_RECORD_STEP * max;
            nTmpListId = new ArrayList<Integer>(  );

            for ( int i = max2; i < nSize; i++ )
            {
                nTmpListId.add( listResultRecordId.get( ( i ) ) );
            }

            List<Record> nTmpListRecords = RecordHome.loadListByListId( nTmpListId, plugin );

            for ( Record record : nTmpListRecords )
            {
                State state = workflowService.getState( record.getIdRecord(  ), Record.WORKFLOW_RESOURCE_TYPE,
                        idWorflow, Integer.valueOf( directory.getIdDirectory(  ) ), null );

                if ( bIsCsvExport )
                {
                    strBufferListRecordXml.append( record.getXmlForCsvExport( plugin, locale, false, state,
                            listEntryResultSearch, false, false, true, bDisplayDateCreation, bDisplayDateModification ) );
                }
                else
                {
                    strBufferListRecordXml.append( record.getXml( plugin, locale, false, state, listEntryResultSearch,
                            false, false, true, bDisplayDateCreation, bDisplayDateModification ) );
                }
            }

            strBufferListRecordXml = this.appendPartialContent( strBufferListRecordXml, bufferedWriter, physicalFile,
                    bIsCsvExport, strXslId, nXmlHeaderLength, xmlTransformerService );

            strBufferListRecordXml.insert( 0, EXPORT_XSL_BEGIN_PARTIAL_EXPORT );
            strBufferListRecordXml.insert( 0, XmlUtil.getXmlHeader(  ) );
            strBufferListRecordXml.append( EXPORT_XSL_END_PARTIAL_EXPORT );
            strFileOutPut = xmlTransformerService.transformBySourceWithXslCache( strBufferListRecordXml.toString(  ),
                    physicalFile.getValue(  ), strXslId, null, null );

            try
            {
                if ( bIsCsvExport )
                {
                    bufferedWriter.write( strFileOutPut );
                }
                else
                {
                    bufferedWriter.write( strFileOutPut.substring( nXmlHeaderLength ) );
                    bufferedWriter.write( EXPORT_XSL_END_LIST_RECORD + EXPORT_XSL_NEW_LINE + EXPORT_XSL_END_DIRECTORY );
                }

                bufferedWriter.flush(  );
                bufferedWriter.close(  );
            }
            catch ( IOException e )
            {
                AppLogService.error( e );
            }
        }
        else
        {
            List<Record> nTmpListRecords = RecordHome.loadListByListId( listResultRecordId, plugin );

            for ( Record record : nTmpListRecords )
            {
                State state = workflowService.getState( record.getIdRecord(  ), Record.WORKFLOW_RESOURCE_TYPE,
                        idWorflow, Integer.valueOf( directory.getIdDirectory(  ) ), null );

                if ( bIsCsvExport )
                {
                    strBufferListRecordXml.append( record.getXmlForCsvExport( plugin, locale, false, state,
                            listEntryResultSearch, false, false, true, bDisplayDateCreation, bDisplayDateModification ) );
                }
                else
                {
                    strBufferListRecordXml.append( record.getXml( plugin, locale, false, state, listEntryResultSearch,
                            false, false, true, bDisplayDateCreation, bDisplayDateModification ) );
                }
            }

            strBufferDirectoryXml.append( directory.getXml( plugin, locale, strBufferListRecordXml,
                    strBufferListEntryXml ) );
            strShotExportFinalOutPut = xmlTransformerService.transformBySourceWithXslCache( strBufferDirectoryXml.toString(  ),
                    physicalFile.getValue(  ), strXslId, null, null );
        }

        // ----------------------------------------------------------------------- 
        DirectoryUtils.addHeaderResponse( request, response, strFileName );

        if ( bIsCsvExport )
        {
            response.setCharacterEncoding( DirectoryParameterService.getService(  ).getExportCSVEncoding(  ) );
            response.setContentType( CONSTANT_MIME_TYPE_CSV );
        }
        else
        {
            response.setCharacterEncoding( DirectoryParameterService.getService(  ).getExportXMLEncoding(  ) );

            String strMimeType = FileSystemUtil.getMIMEType( strFileName );

            if ( strMimeType != null )
            {
                response.setContentType( strMimeType );
            }
            else
            {
                response.setContentType( CONSTANT_MIME_TYPE_OCTETSTREAM );
            }
        }

        if ( bIsBigExport )
        {
            FileChannel in = null;
            WritableByteChannel writeChannelOut = null;

            try
            {
                in = new FileInputStream( tmpFile ).getChannel(  );
                writeChannelOut = Channels.newChannel( response.getOutputStream(  ) );
                response.setContentLength( Long.valueOf( in.size(  ) ).intValue(  ) );
                in.transferTo( 0, in.size(  ), writeChannelOut );
                response.getOutputStream(  ).close(  );
            }
            catch ( IOException e )
            {
                AppLogService.error( e );
            }
            finally
            {
                if ( in != null )
                {
                    try
                    {
                        in.close(  );
                    }
                    catch ( IOException e )
                    {
                    }
                }

                tmpFile.delete(  );
            }
        }
        else
        {
            PrintWriter out = null;

            try
            {
                out = response.getWriter(  );
                out.print( strShotExportFinalOutPut );
            }
            catch ( IOException e )
            {
                AppLogService.error( e.getMessage(  ), e );
            }
            finally
            {
                if ( out != null )
                {
                    out.flush(  );
                    out.close(  );
                }
            }
        }

        result.setNoop( true );

        return result;
    }

    /**
     * Gets the plugin
     * @return the plugin
     */
    private Plugin getPlugin(  )
    {
        return PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
    }

    /**
     * Append partial export result to temporary file if need
     * @param strBufferListRecordXml The partial XML content
     * @param bufferedWriter The bufferedWriter used to append content to temporary file
     * @param physicalFile The XSL physical File
     * @param bIsCsvExport is CSV export
     * @param strXslId The XSL unique ID
     * @param nXmlHeaderLength XML header length
     * @param xmlTransformerService he XmlTransformer service
     */
    private StringBuffer appendPartialContent( StringBuffer strBufferListRecordXml, BufferedWriter bufferedWriter,
        PhysicalFile physicalFile, boolean bIsCsvExport, String strXslId, int nXmlHeaderLength,
        XmlTransformerService xmlTransformerService )
    {
        if ( strBufferListRecordXml.length(  ) > EXPORT_STRINGBUFFER_MAX_CONTENT_SIZE )
        {
            strBufferListRecordXml.insert( 0, EXPORT_XSL_BEGIN_PARTIAL_EXPORT );
            strBufferListRecordXml.insert( 0, XmlUtil.getXmlHeader(  ) );
            strBufferListRecordXml.append( EXPORT_XSL_END_PARTIAL_EXPORT );

            String strFileOutPut = xmlTransformerService.transformBySourceWithXslCache( strBufferListRecordXml.toString(  ),
                    physicalFile.getValue(  ), strXslId, null, null );

            try
            {
                if ( bIsCsvExport )
                {
                    bufferedWriter.write( strFileOutPut );
                }
                else
                {
                    bufferedWriter.write( strFileOutPut.substring( nXmlHeaderLength ) );
                }
            }
            catch ( IOException e )
            {
                AppLogService.error( e );
            }
            finally
            {
                strBufferListRecordXml = new StringBuffer( EXPORT_STRINGBUFFER_INITIAL_SIZE );
            }
        }

        return strBufferListRecordXml;
    }
}

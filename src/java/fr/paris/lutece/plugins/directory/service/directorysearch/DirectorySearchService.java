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

import fr.paris.lutece.plugins.directory.business.Directory;
import fr.paris.lutece.plugins.directory.business.DirectoryHome;
import fr.paris.lutece.plugins.directory.business.EntryTypeArray;
import fr.paris.lutece.plugins.directory.business.IndexerAction;
import fr.paris.lutece.plugins.directory.business.IndexerActionFilter;
import fr.paris.lutece.plugins.directory.business.IndexerActionHome;
import fr.paris.lutece.plugins.directory.business.Record;
import fr.paris.lutece.plugins.directory.business.RecordField;
import fr.paris.lutece.plugins.directory.business.RecordFieldFilter;
import fr.paris.lutece.plugins.directory.business.RecordFieldHome;
import fr.paris.lutece.plugins.directory.service.record.IRecordService;
import fr.paris.lutece.plugins.directory.service.record.RecordService;
import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

import org.apache.commons.collections.CollectionUtils;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.util.Version;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;


/**
 * DirectorySearchService
 */
public class DirectorySearchService
{
    private static final String BEAN_SEARCH_ENGINE = "directorySearchEngine";
    private static final String PATH_INDEX = "directory.internalIndexer.lucene.indexPath";
    private static final String PROPERTY_WRITER_MERGE_FACTOR = "directory.internalIndexer.lucene.writer.mergeFactor";
    private static final String PROPERTY_WRITER_MAX_FIELD_LENGTH = "directory.internalIndexer.lucene.writer.maxFieldLength";
    private static final String PROPERTY_ANALYSER_CLASS_NAME = "directory.internalIndexer.lucene.analyser.className";
    private static final String PARAMETER_ID_DIRECTORY = "id_directory";
    private static final String PARAMETER_ID_ENTRY = "id_entry";
    private static final String JSON_QUERY_RESULT = "query";
    private static final int DEFAULT_WRITER_MERGE_FACTOR = 20;
    private static final int DEFAULT_WRITER_MAX_FIELD_LENGTH = 1000000;
    private static org.apache.lucene.store.Directory _luceneDirectory;
    private static int _nWriterMergeFactor;
    private static int _nWriterMaxFieldLength;
    private static Analyzer _analyzer;
    private static IndexSearcher _searcher;
    private static IDirectorySearchIndexer _indexer;
    private static final int CONSTANT_TIME_CORRECTION = 3600000 * 12;

    // Constants corresponding to the variables defined in the lutece.properties file
    private static DirectorySearchService _singleton;

    /** Creates a new instance of DirectorySearchService */
    public DirectorySearchService(  )
    {
        // Read configuration properties
        String strIndex = AppPathService.getPath( PATH_INDEX );

        if ( ( strIndex == null ) || ( strIndex.equals( "" ) ) )
        {
            throw new AppException( "Lucene index path not found in directory.properties", null );
        }

        try
        {
            _luceneDirectory = NIOFSDirectory.open( new File( strIndex ) );
        }
        catch ( IOException e1 )
        {
            throw new AppException( "Lucene index path not found in directory.properties", null );
        }

        _nWriterMergeFactor = AppPropertiesService.getPropertyInt( PROPERTY_WRITER_MERGE_FACTOR,
                DEFAULT_WRITER_MERGE_FACTOR );
        _nWriterMaxFieldLength = AppPropertiesService.getPropertyInt( PROPERTY_WRITER_MAX_FIELD_LENGTH,
                DEFAULT_WRITER_MAX_FIELD_LENGTH );

        String strAnalyserClassName = AppPropertiesService.getProperty( PROPERTY_ANALYSER_CLASS_NAME );

        if ( ( strAnalyserClassName == null ) || ( strAnalyserClassName.equals( "" ) ) )
        {
            throw new AppException( "Analyser class name not found in directory.properties", null );
        }

        _indexer = SpringContextService.getBean( "directoryIndexer" );

        try
        {
            _analyzer = (Analyzer) Class.forName( strAnalyserClassName ).newInstance(  );
        }
        catch ( Exception e )
        {
            throw new AppException( "Failed to load Lucene Analyzer class", e );
        }
    }

    /**
     *
     * @return singleton
     */
    public static DirectorySearchService getInstance(  )
    {
        if ( _singleton == null )
        {
            _singleton = new DirectorySearchService(  );
        }

        return _singleton;
    }

    /**
     * Return a list of record key return by the search
     * @param directory the directory
     * @param mapSearch a map which contains for each entry the list of
     *            recorField(value of field search) associate
     * @param dateCreation the creation date
     * @param dateCreationBegin the date begin to search for the creation date
     * @param dateCreationEnd the date end to search for the creation date
     * @param filter the filter
     * @param plugin the plugin
     * @return a list of record key return by the search
     */
    public List<Integer> getSearchResults( Directory directory, HashMap<String, List<RecordField>> mapSearch,
        Date dateCreation, Date dateCreationBegin, Date dateCreationEnd, RecordFieldFilter filter, Plugin plugin )
    {
        return getSearchResults( directory, mapSearch, dateCreationEnd, dateCreationBegin, dateCreationEnd, null, null,
            null, filter, plugin );
    }

    /**
     * Return a list of record key return by the search
     * @param directory the directory
     * @param mapSearch a map which contains for each entry the list of
     *            recorField(value of field search) associate
     * @param dateCreation the creation date
     * @param dateCreationBegin the date begin to search for the creation date
     * @param dateCreationEnd the date end to search for the creation date
     * @param dateModification the modification date
     * @param dateModificationBegin the date begin to search for the
     *            modification date
     * @param dateModificationEnd the date end to search for the modification
     *            date
     * @param filter the filter
     * @param plugin the plugin
     * @return a list of record key return by the search
     */
    public List<Integer> getSearchResults( Directory directory, HashMap<String, List<RecordField>> mapSearch,
        Date dateCreation, Date dateCreationBegin, Date dateCreationEnd, Date dateModification,
        Date dateModificationBegin, Date dateModificationEnd, RecordFieldFilter filter, Plugin plugin )
    {
        List<Integer> listRecordResult = new ArrayList<Integer>(  );

        IRecordService recordService = SpringContextService.getBean( RecordService.BEAN_SERVICE );
        listRecordResult = recordService.getListRecordId( filter, plugin );

        if ( mapSearch != null )
        {
            List<Integer> listRecordResultTmp = null;
            List<RecordField> recordFieldSearch;
            HashMap<String, Object> mapSearchItemEntry;
            boolean bSearchRecordEmpty;
            boolean bSearchEmpty;

            try
            {
                _searcher = new IndexSearcher( DirectoryReader.open( _luceneDirectory ) );

                IDirectorySearchEngine engine = SpringContextService.getBean( BEAN_SEARCH_ENGINE );

                listRecordResultTmp = new ArrayList<Integer>(  );
                bSearchEmpty = true;

                for ( Object entryMapSearch : mapSearch.entrySet(  ) )
                {
                    recordFieldSearch = ( (Entry<String, List<RecordField>>) entryMapSearch ).getValue(  );

                    int nIdEntry = DirectoryUtils.convertStringToInt( ( (Entry<String, List<RecordField>>) entryMapSearch ).getKey(  ) );
                    bSearchRecordEmpty = true;

                    if ( recordFieldSearch != null )
                    {
                        mapSearchItemEntry = new HashMap<String, Object>(  );

                        boolean bIsArray = false;
                        boolean bFirstRecord = true;

                        for ( RecordField recordField : recordFieldSearch )
                        {
                            if ( recordField.getEntry(  ) instanceof EntryTypeArray )
                            {
                                // for array, we do a search on content for each case
                                bIsArray = true;
                                mapSearchItemEntry = new HashMap<String, Object>(  );
                                recordField.getEntry(  ).addSearchCriteria( mapSearchItemEntry, recordField );

                                if ( mapSearchItemEntry.size(  ) > 0 )
                                {
                                    bSearchRecordEmpty = false;
                                    bSearchEmpty = false;
                                    mapSearchItemEntry.put( DirectorySearchItem.FIELD_ID_DIRECTORY,
                                        directory.getIdDirectory(  ) );
                                    mapSearchItemEntry.put( DirectorySearchItem.FIELD_ID_DIRECTORY_ENTRY, nIdEntry );

                                    List<Integer> ids = engine.getSearchResults( mapSearchItemEntry );

                                    if ( CollectionUtils.isEmpty( ids ) )
                                    {
                                        listRecordResultTmp = new ArrayList<Integer>(  );

                                        break;
                                    }
                                    else if ( bFirstRecord )
                                    {
                                        listRecordResultTmp = ids;
                                        bFirstRecord = false;
                                    }
                                    else
                                    {
                                        listRecordResultTmp = (List<Integer>) CollectionUtils.intersection( listRecordResultTmp,
                                                ids );
                                    }
                                }
                            }
                            else
                            {
                                recordField.getEntry(  ).addSearchCriteria( mapSearchItemEntry, recordField );
                            }
                        }

                        if ( !bIsArray && ( mapSearchItemEntry.size(  ) > 0 ) )
                        {
                            bSearchRecordEmpty = false;
                            bSearchEmpty = false;
                            mapSearchItemEntry.put( DirectorySearchItem.FIELD_ID_DIRECTORY, directory.getIdDirectory(  ) );
                            mapSearchItemEntry.put( DirectorySearchItem.FIELD_ID_DIRECTORY_ENTRY, nIdEntry );
                            listRecordResultTmp.addAll( engine.getSearchResults( mapSearchItemEntry ) );
                        }

                        if ( !bSearchRecordEmpty && !directory.isSearchOperatorOr(  ) )
                        {
                            // keeping order is important for display
                            listRecordResult = DirectoryUtils.retainAllIdsKeepingFirstOrder( listRecordResult,
                                    listRecordResultTmp );
                            listRecordResultTmp = new ArrayList<Integer>(  );
                        }
                    }
                }

                if ( directory.isSearchOperatorOr(  ) && !bSearchEmpty )
                {
                    listRecordResult = DirectoryUtils.retainAllIdsKeepingFirstOrder( listRecordResult,
                            listRecordResultTmp );
                }

                //date creation of a record
                if ( dateCreation != null )
                {
                    listRecordResultTmp = new ArrayList<Integer>(  );
                    mapSearchItemEntry = new HashMap<String, Object>(  );
                    mapSearchItemEntry.put( DirectorySearchItem.FIELD_ID_DIRECTORY, directory.getIdDirectory(  ) );
                    dateCreation.setTime( dateCreation.getTime(  ) + CONSTANT_TIME_CORRECTION );

                    mapSearchItemEntry.put( DirectorySearchItem.FIELD_DATE_CREATION, dateCreation );
                    listRecordResultTmp.addAll( engine.getSearchResults( mapSearchItemEntry ) );

                    // keeping order is important for display
                    listRecordResult = DirectoryUtils.retainAllIdsKeepingFirstOrder( listRecordResult,
                            listRecordResultTmp );
                }
                else if ( ( dateCreationBegin != null ) && ( dateCreationEnd != null ) )
                {
                    dateCreationBegin.setTime( dateCreationBegin.getTime(  ) + CONSTANT_TIME_CORRECTION );
                    dateCreationEnd.setTime( dateCreationEnd.getTime(  ) + CONSTANT_TIME_CORRECTION );

                    listRecordResultTmp = new ArrayList<Integer>(  );
                    mapSearchItemEntry = new HashMap<String, Object>(  );
                    mapSearchItemEntry.put( DirectorySearchItem.FIELD_ID_DIRECTORY, directory.getIdDirectory(  ) );
                    mapSearchItemEntry.put( DirectorySearchItem.FIELD_DATE_CREATION_BEGIN, dateCreationBegin );
                    mapSearchItemEntry.put( DirectorySearchItem.FIELD_DATE_CREATION_END, dateCreationEnd );
                    listRecordResultTmp.addAll( engine.getSearchResults( mapSearchItemEntry ) );

                    // keeping order is important for display
                    listRecordResult = DirectoryUtils.retainAllIdsKeepingFirstOrder( listRecordResult,
                            listRecordResultTmp );
                }

                //date modification of a record
                if ( dateModification != null )
                {
                    listRecordResultTmp = new ArrayList<Integer>(  );
                    mapSearchItemEntry = new HashMap<String, Object>(  );
                    mapSearchItemEntry.put( DirectorySearchItem.FIELD_ID_DIRECTORY, directory.getIdDirectory(  ) );
                    dateModification.setTime( dateModification.getTime(  ) + CONSTANT_TIME_CORRECTION );

                    mapSearchItemEntry.put( DirectorySearchItem.FIELD_DATE_MODIFICATION, dateModification );
                    listRecordResultTmp.addAll( engine.getSearchResults( mapSearchItemEntry ) );

                    // keeping order is important for display
                    listRecordResult = DirectoryUtils.retainAllIdsKeepingFirstOrder( listRecordResult,
                            listRecordResultTmp );
                }
                else if ( ( dateModificationBegin != null ) && ( dateModificationEnd != null ) )
                {
                    dateModificationBegin.setTime( dateModificationBegin.getTime(  ) + CONSTANT_TIME_CORRECTION );
                    dateModificationEnd.setTime( dateModificationEnd.getTime(  ) + CONSTANT_TIME_CORRECTION );

                    listRecordResultTmp = new ArrayList<Integer>(  );
                    mapSearchItemEntry = new HashMap<String, Object>(  );
                    mapSearchItemEntry.put( DirectorySearchItem.FIELD_ID_DIRECTORY, directory.getIdDirectory(  ) );
                    mapSearchItemEntry.put( DirectorySearchItem.FIELD_DATE_MODIFICATION_BEGIN, dateModificationBegin );
                    mapSearchItemEntry.put( DirectorySearchItem.FIELD_DATE_MODIFICATION_END, dateModificationEnd );
                    listRecordResultTmp.addAll( engine.getSearchResults( mapSearchItemEntry ) );

                    // keeping order is important for display
                    listRecordResult = DirectoryUtils.retainAllIdsKeepingFirstOrder( listRecordResult,
                            listRecordResultTmp );
                }
            }
            catch ( Exception e )
            {
                AppLogService.error( e.getMessage(  ), e );
                // If an error occurred clean result list
                listRecordResult = new ArrayList<Integer>(  );
            }
        }

        return listRecordResult;
    }

    public String getAutocompleteResult( HttpServletRequest request )
        throws Exception
    {
        String strIdDirectory = request.getParameter( PARAMETER_ID_DIRECTORY );
        String strIdEntry = request.getParameter( PARAMETER_ID_ENTRY );
        Plugin plugin = PluginService.getPlugin( "directory" );
        StringBuffer result = new StringBuffer(  );

        _searcher = new IndexSearcher( DirectoryReader.open( _luceneDirectory ) );

        IDirectorySearchEngine engine = SpringContextService.getBean( BEAN_SEARCH_ENGINE );

        if ( ( ( strIdDirectory != null ) && !strIdDirectory.equals( DirectoryUtils.EMPTY_STRING ) ) &&
                ( ( strIdEntry != null ) && !strIdEntry.equals( DirectoryUtils.EMPTY_STRING ) ) )
        {
            HashMap<String, Object> mapSearchItemEntry = new HashMap<String, Object>(  );
            mapSearchItemEntry.put( DirectorySearchItem.FIELD_ID_DIRECTORY, Integer.parseInt( strIdDirectory ) );
            mapSearchItemEntry.put( DirectorySearchItem.FIELD_ID_DIRECTORY_ENTRY, Integer.parseInt( strIdEntry ) );

            List<Integer> listIntRecordField = engine.getSearchResults( mapSearchItemEntry );
            List<RecordField> listRecordField = new ArrayList<RecordField>(  );

            for ( Integer idDirectoryRecord : listIntRecordField )
            {
                RecordFieldFilter recordFilter = new RecordFieldFilter(  );
                recordFilter.setIdDirectory( Integer.parseInt( strIdDirectory ) );
                recordFilter.setIdEntry( Integer.parseInt( strIdEntry ) );
                recordFilter.setIdRecord( idDirectoryRecord );
                listRecordField.addAll( RecordFieldHome.getRecordFieldList( recordFilter, plugin ) );
            }

            HashMap<String, String> mapResult = new HashMap<String, String>(  );

            for ( RecordField recordField : listRecordField )
            {
                //result.append( recordField.getValue(  ) + "%" );
                mapResult.put( recordField.getValue(  ), recordField.getValue(  ) );
            }

            for ( String key : mapResult.keySet(  ) )
            {
                result.append( key + "%" );
            }
        }

        JSONObject jo = new JSONObject(  );

        try
        {
            jo.put( JSON_QUERY_RESULT, result.toString(  ) );
        }
        catch ( JSONException e )
        {
            AppLogService.error( e.getMessage(  ), e );
        }

        return jo.toString(  );
    }

    /**
     * Process indexing
     * @param bCreate true for start full indexing
     *            false for begin incremental indexing
     * @return the log
     */
    public String processIndexing( boolean bCreate )
    {
        StringBuffer sbLogs = new StringBuffer(  );
        IndexWriter writer = null;
        boolean bCreateIndex = bCreate;

        try
        {
            sbLogs.append( "\r\nIndexing all contents ...\r\n" );

            if ( !DirectoryReader.indexExists( _luceneDirectory ) )
            { //init index
                bCreateIndex = true;
            }

            if ( !bCreateIndex && IndexWriter.isLocked( _luceneDirectory ) )
            {
                IndexWriter.unlock( _luceneDirectory );
            }

            IndexWriterConfig conf = new IndexWriterConfig( Version.LUCENE_46, _analyzer );

            if ( bCreateIndex )
            {
                conf.setOpenMode( OpenMode.CREATE );
            }
            else
            {
                conf.setOpenMode( OpenMode.APPEND );
            }

            writer = new IndexWriter( _luceneDirectory, conf );

            Date start = new Date(  );

            sbLogs.append( "\r\n<strong>Indexer : " );
            sbLogs.append( _indexer.getName(  ) );
            sbLogs.append( " - " );
            sbLogs.append( _indexer.getDescription(  ) );
            sbLogs.append( "</strong>\r\n" );
            _indexer.processIndexing( writer, bCreateIndex, sbLogs );

            Date end = new Date(  );

            sbLogs.append( "Duration of the treatment : " );
            sbLogs.append( end.getTime(  ) - start.getTime(  ) );
            sbLogs.append( " milliseconds\r\n" );
        }
        catch ( Exception e )
        {
            sbLogs.append( " caught a " );
            sbLogs.append( e.getClass(  ) );
            sbLogs.append( "\n with message: " );
            sbLogs.append( e.getMessage(  ) );
            sbLogs.append( "\r\n" );
            AppLogService.error( "Indexing error : " + e.getMessage(  ), e );
        }
        finally
        {
            try
            {
                if ( writer != null )
                {
                    writer.close(  );
                }
            }
            catch ( IOException e )
            {
                AppLogService.error( e.getMessage(  ), e );
            }
        }

        return sbLogs.toString(  );
    }

    /**
     * Add Indexer Action to perform on a record
     * @param nIdRecord the id of the record
     * @param nIdTask the key of the action to do
     * @param plugin the plugin
     */
    public void addIndexerAction( int nIdRecord, int nIdTask, Plugin plugin )
    {
        IRecordService recordService = SpringContextService.getBean( RecordService.BEAN_SERVICE );
        Record record = recordService.findByPrimaryKey( nIdRecord, plugin );

        if ( ( record != null ) && ( record.getDirectory(  ) != null ) )
        {
            int nDirectoryId = record.getDirectory(  ).getIdDirectory(  );
            Directory directory = DirectoryHome.findByPrimaryKey( nDirectoryId, plugin );

            if ( ( directory != null ) && directory.isIndexed(  ) )
            {
                IndexerAction indexerAction = new IndexerAction(  );
                indexerAction.setIdRecord( nIdRecord );
                indexerAction.setIdTask( nIdTask );
                IndexerActionHome.create( indexerAction, plugin );
            }
        }
    }

    /**
     * Remove a Indexer Action
     * @param nIdAction the key of the action to remove
     * @param plugin the plugin
     */
    public void removeIndexerAction( int nIdAction, Plugin plugin )
    {
        IndexerActionHome.remove( nIdAction, plugin );
    }

    /**
     * return a list of IndexerAction by task key
     * @param nIdTask the task kety
     * @param plugin the plugin
     * @return a list of IndexerAction
     */
    public List<IndexerAction> getAllIndexerActionByTask( int nIdTask, Plugin plugin )
    {
        IndexerActionFilter filter = new IndexerActionFilter(  );
        filter.setIdTask( nIdTask );

        return IndexerActionHome.getList( filter, plugin );
    }

    /**
     * return searcher
     * @return searcher
     */
    public IndexSearcher getSearcher(  )
    {
        return _searcher;
    }

    /**
     * set searcher
     * @param searcher searcher
     */
    public void setSearcher( IndexSearcher searcher )
    {
        _searcher = searcher;
    }
}

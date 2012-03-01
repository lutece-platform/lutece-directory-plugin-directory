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
package fr.paris.lutece.plugins.directory.service.directorysearch;

import fr.paris.lutece.plugins.directory.business.Directory;
import fr.paris.lutece.plugins.directory.business.DirectoryFilter;
import fr.paris.lutece.plugins.directory.business.DirectoryHome;
import fr.paris.lutece.plugins.directory.business.IndexerAction;
import fr.paris.lutece.plugins.directory.business.Record;
import fr.paris.lutece.plugins.directory.business.RecordField;
import fr.paris.lutece.plugins.directory.business.RecordFieldFilter;
import fr.paris.lutece.plugins.directory.business.RecordFieldHome;
import fr.paris.lutece.plugins.directory.business.RecordHome;
import fr.paris.lutece.plugins.directory.service.DirectoryPlugin;
import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


/**
 * Directory Indexer
 *
 */
public class DirectoryIndexer implements IDirectorySearchIndexer
{
    private static final int LIST_RECORD_STEP = 50;
    private static final String ENABLE_VALUE_TRUE = "1";
    private static final String PROPERTY_INDEXER_DESCRIPTION = "directory.internalIndexer.description";
    private static final String PROPERTY_INDEXER_NAME = "directory.internalIndexer.name";
    private static final String PROPERTY_INDEXER_VERSION = "directory.internalIndexer.version";
    private static final String PROPERTY_INDEXER_ENABLE = "directory.internalIndexer.enable";
    private static final List<Integer> _lListIdRecordToDelete = new ArrayList<Integer>(  );

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.service.directorysearch.IDirectorySearchIndexer#getDescription()
     */
    public String getDescription(  )
    {
        return AppPropertiesService.getProperty( PROPERTY_INDEXER_DESCRIPTION );
    }

    /**
     * Index given list of record
     * @param indexWriter the indexWriter
     * @param lListId list of id directory / list of id record
     * @param plugin the plugin
     * @return new empty list of id directory / list of id record
     * @throws CorruptIndexException
     * @throws IOException
     * @throws InterruptedException
     */
    private HashMap<Integer, List<Integer>> indexListRecord( IndexWriter indexWriter,
        HashMap<Integer, List<Integer>> lListId, Plugin plugin )
        throws CorruptIndexException, IOException, InterruptedException
    {
        Iterator<Integer> it = lListId.keySet(  ).iterator(  );

        while ( it.hasNext(  ) )
        {
            Integer nDirectoryId = it.next(  );
            Directory directory = DirectoryHome.findByPrimaryKey( nDirectoryId, plugin );
            List<Integer> lListRecordId = lListId.get( nDirectoryId );

            int nListRecordSize = lListRecordId.size(  );

            if ( nListRecordSize > LIST_RECORD_STEP )
            {
                int nIndex = 0;
                int nMax = nListRecordSize - LIST_RECORD_STEP;

                for ( int i = 0; i < nMax; i += LIST_RECORD_STEP )
                {
                    List<RecordField> lListrecordField = RecordFieldHome.getRecordFieldListByRecordIdList( lListRecordId.subList( 
                                i, i + LIST_RECORD_STEP ), plugin );

                    for ( RecordField recordField : lListrecordField )
                    {
                        indexWriter.addDocument( getDocument( recordField, recordField.getRecord(  ), directory ) );
                    }

                    for ( Record record : RecordHome.loadListByListId( lListRecordId.subList( i, i + LIST_RECORD_STEP ),
                            plugin ) )
                    {
                        indexWriter.addDocument( getDocument( record, directory ) );
                    }

                    nIndex = i;
                }

                nIndex += LIST_RECORD_STEP;

                List<RecordField> lListrecordField = RecordFieldHome.getRecordFieldListByRecordIdList( lListRecordId.subList( 
                            nIndex, nListRecordSize ), plugin );

                for ( RecordField recordField : lListrecordField )
                {
                    indexWriter.addDocument( getDocument( recordField, recordField.getRecord(  ), directory ) );
                }

                for ( Record record : RecordHome.loadListByListId( lListRecordId.subList( nIndex, nListRecordSize ),
                        plugin ) )
                {
                    indexWriter.addDocument( getDocument( record, directory ) );
                }
            }
            else
            {
                List<RecordField> lListrecordField = RecordFieldHome.getRecordFieldListByRecordIdList( lListRecordId,
                        plugin );

                for ( RecordField recordField : lListrecordField )
                {
                    indexWriter.addDocument( getDocument( recordField, recordField.getRecord(  ), directory ) );
                }

                for ( Record record : RecordHome.loadListByListId( lListRecordId, plugin ) )
                {
                    indexWriter.addDocument( getDocument( record, directory ) );
                }
            }
        }

        return new HashMap<Integer, List<Integer>>(  );
    }

    /**
     * Append key to list of id directory / list of id record
     * @param nIdDirectory the directory id
     * @param nIdAction the action id
     * @param hm current list of id directory / list of id record
     * @return list of id directory / list of id record
     */
    private HashMap<Integer, List<Integer>> appendKey( Integer nIdDirectory, Integer nIdAction,
        HashMap<Integer, List<Integer>> hm )
    {
        if ( hm.containsKey( nIdDirectory ) )
        {
            hm.get( nIdDirectory ).add( nIdAction );
        }
        else
        {
            List<Integer> lListIdRecord = new ArrayList<Integer>(  );
            lListIdRecord.add( nIdAction );
            hm.put( nIdDirectory, lListIdRecord );
        }

        return hm;
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.service.directorysearch.IDirectorySearchIndexer#processIndexing(org.apache.lucene.index.IndexWriter, boolean, java.lang.StringBuffer)
     */
    public void processIndexing( IndexWriter indexWriter, boolean bCreate, StringBuffer sbLogs )
        throws IOException, InterruptedException, SiteMessageException
    {
        Plugin plugin = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
        RecordFieldFilter recordFieldFilter = new RecordFieldFilter(  );
        HashMap<Integer, List<Integer>> hm = new HashMap<Integer, List<Integer>>(  );

        if ( !bCreate )
        {
            //incremental indexing
            //delete all record which must be delete
            for ( IndexerAction action : DirectorySearchService.getInstance(  )
                                                               .getAllIndexerActionByTask( IndexerAction.TASK_DELETE,
                    plugin ) )
            {
                sbLogRecord( sbLogs, action.getIdRecord(  ), DirectoryUtils.CONSTANT_ID_NULL, IndexerAction.TASK_DELETE );
                indexWriter.deleteDocuments( new Term( DirectorySearchItem.FIELD_ID_DIRECTORY_RECORD,
                        Integer.toString( action.getIdRecord(  ) ) ) );
                DirectorySearchService.getInstance(  ).removeIndexerAction( action.getIdAction(  ), plugin );
            }

            //Hack : see this.appendListRecordToDelete() comments
            for ( Integer nIdRecord : _lListIdRecordToDelete )
            {
                indexWriter.deleteDocuments( new Term( DirectorySearchItem.FIELD_ID_DIRECTORY_RECORD,
                        Integer.toString( nIdRecord ) ) );
            }

            _lListIdRecordToDelete.clear(  );

            //Update all record which must be update
            for ( IndexerAction action : DirectorySearchService.getInstance(  )
                                                               .getAllIndexerActionByTask( IndexerAction.TASK_MODIFY,
                    plugin ) )
            {
                Integer nDirectoryId = RecordHome.getDirectoryIdByRecordId( Integer.valueOf( action.getIdRecord(  ) ),
                        plugin );

                if ( nDirectoryId != null )
                {
                    sbLogRecord( sbLogs, action.getIdRecord(  ), nDirectoryId.intValue(  ), IndexerAction.TASK_MODIFY );

                    indexWriter.deleteDocuments( new Term( DirectorySearchItem.FIELD_ID_DIRECTORY_RECORD,
                            Integer.toString( action.getIdRecord(  ) ) ) );

                    this.appendKey( nDirectoryId, action.getIdRecord(  ), hm );
                }

                DirectorySearchService.getInstance(  ).removeIndexerAction( action.getIdAction(  ), plugin );
            }

            hm = this.indexListRecord( indexWriter, hm, plugin );

            //add all record which must be add
            for ( IndexerAction action : DirectorySearchService.getInstance(  )
                                                               .getAllIndexerActionByTask( IndexerAction.TASK_CREATE,
                    plugin ) )
            {
                Integer nDirectoryId = RecordHome.getDirectoryIdByRecordId( Integer.valueOf( action.getIdRecord(  ) ),
                        plugin );

                if ( nDirectoryId != null )
                {
                    sbLogRecord( sbLogs, action.getIdRecord(  ), nDirectoryId, IndexerAction.TASK_CREATE );

                    this.appendKey( nDirectoryId, action.getIdRecord(  ), hm );
                }

                DirectorySearchService.getInstance(  ).removeIndexerAction( action.getIdAction(  ), plugin );
            }

            hm = this.indexListRecord( indexWriter, hm, plugin );
        }
        else
        {
            // Index only the directories that have the attribute is_indexed as true
            DirectoryFilter filter = new DirectoryFilter(  );
            filter.setIsIndexed( DirectoryFilter.FILTER_TRUE );

            for ( Directory directory : DirectoryHome.getDirectoryList( filter, plugin ) )
            {
                sbLogs.append( "Indexing Directory" );
                sbLogs.append( "\r\n" );
                recordFieldFilter.setIdDirectory( directory.getIdDirectory(  ) );

                for ( Record record : RecordHome.getListRecord( recordFieldFilter, plugin ) )
                {
                    sbLogRecord( sbLogs, record.getIdRecord(  ), record.getDirectory(  ).getIdDirectory(  ),
                        IndexerAction.TASK_CREATE );

                    this.appendKey( directory.getIdDirectory(  ), record.getIdRecord(  ), hm );
                }
            }

            hm = this.indexListRecord( indexWriter, hm, plugin );
        }
    }

    /**
     * Builds a document which will be used by Lucene during the indexing of the directory
     *
     * @param recordField the recordField to index
     * @param record the record associate to the recordField
     * @param directory the directory associate to the recordField
     * @return A Lucene {@link Document} containing QuestionAnswer Data
     * @throws IOException The IO Exception
     * @throws InterruptedException The InterruptedException
     */
    private org.apache.lucene.document.Document getDocument( RecordField recordField, Record record, Directory directory )
        throws IOException, InterruptedException
    {
        // make a new, empty document
        org.apache.lucene.document.Document doc = new org.apache.lucene.document.Document(  );
        HashMap<String, Object> mapSearchItemField;
        doc.add( new Field( DirectorySearchItem.FIELD_ID_DIRECTORY, Integer.toString( directory.getIdDirectory(  ) ),
                Field.Store.YES, Field.Index.NOT_ANALYZED ) );

        doc.add( new Field( DirectorySearchItem.FIELD_ID_DIRECTORY_RECORD, Integer.toString( record.getIdRecord(  ) ),
                Field.Store.YES, Field.Index.NOT_ANALYZED ) );

        doc.add( new Field( DirectorySearchItem.FIELD_ID_DIRECTORY_ENTRY,
                Integer.toString( recordField.getEntry(  ).getIdEntry(  ) ), Field.Store.YES, Field.Index.NOT_ANALYZED ) );

        mapSearchItemField = new HashMap<String, Object>(  );
        recordField.getEntry(  ).addSearchCriteria( mapSearchItemField, recordField );

        if ( mapSearchItemField.containsKey( DirectorySearchItem.FIELD_ID_DIRECTORY_FIELD ) )
        {
            for ( Integer idField : (List<Integer>) mapSearchItemField.get( 
                    DirectorySearchItem.FIELD_ID_DIRECTORY_FIELD ) )
            {
                doc.add( new Field( DirectorySearchItem.FIELD_ID_DIRECTORY_FIELD, Integer.toString( idField ),
                        Field.Store.YES, Field.Index.NOT_ANALYZED ) );
            }
        }

        if ( mapSearchItemField.containsKey( DirectorySearchItem.FIELD_DATE ) )
        {
            String strDate = DateTools.dateToString( (Date) mapSearchItemField.get( DirectorySearchItem.FIELD_DATE ),
                    DateTools.Resolution.DAY );
            doc.add( new Field( DirectorySearchItem.FIELD_DATE, strDate, Field.Store.YES, Field.Index.NOT_ANALYZED ) );
        }

        if ( mapSearchItemField.containsKey( DirectorySearchItem.FIELD_CONTENTS ) )
        {
            doc.add( new Field( DirectorySearchItem.FIELD_CONTENTS,
                    (String) mapSearchItemField.get( DirectorySearchItem.FIELD_CONTENTS ), Field.Store.NO,
                    Field.Index.ANALYZED ) );
        }

        // return the document
        return doc;
    }

    /**
     * Builds a document which will be used by Lucene during the indexing of the directory
     *
     * @param record the record to index
     * @param directory the directory associate to the recordField
     * @return A Lucene {@link Document} containing QuestionAnswer Data
     * @throws IOException The IO Exception
     * @throws InterruptedException The InterruptedException
     */
    private org.apache.lucene.document.Document getDocument( Record record, Directory directory )
        throws IOException, InterruptedException
    {
        // make a new, empty document
        org.apache.lucene.document.Document doc = new org.apache.lucene.document.Document(  );

        if ( ( directory != null ) && ( record != null ) )
        {
            doc.add( new Field( DirectorySearchItem.FIELD_ID_DIRECTORY,
                    Integer.toString( directory.getIdDirectory(  ) ), Field.Store.YES, Field.Index.NOT_ANALYZED ) );

            doc.add( new Field( DirectorySearchItem.FIELD_ID_DIRECTORY_RECORD,
                    Integer.toString( record.getIdRecord(  ) ), Field.Store.YES, Field.Index.NOT_ANALYZED ) );

            if ( record.getWorkgroup(  ) != null )
            {
                doc.add( new Field( DirectorySearchItem.FIELD_WORKGROUP_KEY, record.getWorkgroup(  ), Field.Store.YES,
                        Field.Index.NOT_ANALYZED ) );
            }

            if ( record.getRoleKey(  ) != null )
            {
                doc.add( new Field( DirectorySearchItem.FIELD_ROLE_KEY, record.getRoleKey(  ), Field.Store.YES,
                        Field.Index.NOT_ANALYZED ) );
            }

            String strDate = DateTools.dateToString( record.getDateCreation(  ), DateTools.Resolution.DAY );
            doc.add( new Field( DirectorySearchItem.FIELD_DATE_CREATION, strDate, Field.Store.YES,
                    Field.Index.NOT_ANALYZED ) );

            String strDateModification = DateTools.dateToString( record.getDateModification(  ),
                    DateTools.Resolution.DAY );
            doc.add( new Field( DirectorySearchItem.FIELD_DATE_MODIFICATION, strDateModification, Field.Store.YES,
                    Field.Index.NOT_ANALYZED ) );
        }

        // return the document
        return doc;
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.service.directorysearch.IDirectorySearchIndexer#getName()
     */
    public String getName(  )
    {
        return AppPropertiesService.getProperty( PROPERTY_INDEXER_NAME );
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.service.directorysearch.IDirectorySearchIndexer#getVersion()
     */
    public String getVersion(  )
    {
        return AppPropertiesService.getProperty( PROPERTY_INDEXER_VERSION );
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.directory.service.directorysearch.IDirectorySearchIndexer#isEnable()
     */
    public boolean isEnable(  )
    {
        boolean bReturn = false;
        String strEnable = AppPropertiesService.getProperty( PROPERTY_INDEXER_ENABLE );

        if ( ( strEnable != null ) &&
                ( strEnable.equalsIgnoreCase( Boolean.TRUE.toString(  ) ) || strEnable.equals( ENABLE_VALUE_TRUE ) ) )
        {
            bReturn = true;
        }

        return bReturn;
    }

    /**
     * indexing action performed on the recording
     * @param sbLogs the buffer log
     * @param nIdRecord the id of the record
     * @param nIdDirectory the id of the directory
     * @param nAction the indexer action key performed
     */
    private void sbLogRecord( StringBuffer sbLogs, int nIdRecord, int nIdDirectory, int nAction )
    {
        sbLogs.append( "Indexing Directory record:" );

        switch ( nAction )
        {
            case IndexerAction.TASK_CREATE:
                sbLogs.append( "Insert " );

                break;

            case IndexerAction.TASK_MODIFY:
                sbLogs.append( "Modify " );

                break;

            case IndexerAction.TASK_DELETE:
                sbLogs.append( "Delete " );

                break;

            default:
                break;
        }

        sbLogs.append( "record :" );
        sbLogs.append( "id_record=" );
        sbLogs.append( nIdRecord );

        if ( nIdDirectory != DirectoryUtils.CONSTANT_ID_NULL )
        {
            sbLogs.append( "&" );
            sbLogs.append( "id_directory=" );
            sbLogs.append( nIdDirectory );
        }

        sbLogs.append( "\r\n" );
    }

    /**
     * Append list record id to delete
     * Hack (ugly) to bypass problem of primary key violation on table "directory_indexer_action"
     * when inserting many records
     * @param lListIdRecord List record to delete
     */
    public static void appendListRecordToDelete( List<Integer> lListIdRecord )
    {
        _lListIdRecordToDelete.addAll( lListIdRecord );
    }
}

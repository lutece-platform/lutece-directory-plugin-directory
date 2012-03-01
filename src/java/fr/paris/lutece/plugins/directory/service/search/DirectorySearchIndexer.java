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
package fr.paris.lutece.plugins.directory.service.search;

import fr.paris.lutece.plugins.directory.business.Directory;
import fr.paris.lutece.plugins.directory.business.DirectoryFilter;
import fr.paris.lutece.plugins.directory.business.DirectoryHome;
import fr.paris.lutece.plugins.directory.business.EntryFilter;
import fr.paris.lutece.plugins.directory.business.EntryHome;
import fr.paris.lutece.plugins.directory.business.IEntry;
import fr.paris.lutece.plugins.directory.business.Record;
import fr.paris.lutece.plugins.directory.business.RecordField;
import fr.paris.lutece.plugins.directory.business.RecordFieldFilter;
import fr.paris.lutece.plugins.directory.business.RecordFieldHome;
import fr.paris.lutece.plugins.directory.business.RecordHome;
import fr.paris.lutece.plugins.directory.service.DirectoryPlugin;
import fr.paris.lutece.portal.service.content.XPageAppService;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.search.IndexationService;
import fr.paris.lutece.portal.service.search.SearchIndexer;
import fr.paris.lutece.portal.service.search.SearchItem;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.url.UrlItem;

import org.apache.commons.lang.StringUtils;

import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;


/**
 * Directory global indexer
 */
public class DirectorySearchIndexer implements SearchIndexer
{
    public static final String INDEXER_NAME = "DirectoryIndexer";
    public static final String SHORT_NAME = "dry";
    private static final String DIRECTORY = "directory";
    private static final String INDEXER_DESCRIPTION = "Indexer service for directories";
    private static final String INDEXER_VERSION = "1.0.0";
    private static final String PARAMETER_ID_DIRECTORY_RECORD = "id_directory_record";
    private static final String PARAMETER_VIEW_DIRECTORY_RECORD = "view_directory_record";
    private static final String PROPERTY_INDEXER_ENABLE = "directory.globalIndexer.enable";
    private static final String ROLE_NONE = "none";

    /**
     * {@inheritDoc}
     */
    public String getName(  )
    {
        return INDEXER_NAME;
    }

    /**
     * {@inheritDoc}
     */
    public String getDescription(  )
    {
        return INDEXER_DESCRIPTION;
    }

    /**
     * {@inheritDoc}
     */
    public String getVersion(  )
    {
        return INDEXER_VERSION;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEnable(  )
    {
        String strEnable = AppPropertiesService.getProperty( PROPERTY_INDEXER_ENABLE );

        return ( strEnable.equalsIgnoreCase( "true" ) );
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getListType(  )
    {
        List<String> listType = new ArrayList<String>( 1 );
        listType.add( DIRECTORY );

        return listType;
    }

    /**
     * {@inheritDoc}
     */
    public String getSpecificSearchAppUrl(  )
    {
        UrlItem url = new UrlItem( AppPathService.getPortalUrl(  ) );
        url.addParameter( XPageAppService.PARAM_XPAGE_APP, DIRECTORY );

        return url.getUrl(  );
    }

    /**
     * {@inheritDoc}
     */
    public List<Document> getDocuments( String recordId )
        throws IOException, InterruptedException, SiteMessageException
    {
        Plugin plugin = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );

        int nIdRecord;

        try
        {
            nIdRecord = Integer.parseInt( recordId );
        }
        catch ( NumberFormatException ne )
        {
            AppLogService.error( recordId + " not parseable to an int", ne );

            return new ArrayList<Document>( 0 );
        }

        Record record = RecordHome.findByPrimaryKey( nIdRecord, plugin );
        Directory directory = record.getDirectory(  );

        if ( !record.isEnabled(  ) || !directory.isEnabled(  ) || !directory.isIndexed(  ) )
        {
            return new ArrayList<Document>( 0 );
        }

        int nIdDirectory = directory.getIdDirectory(  );

        //Parse the entries to gather the ones marked as indexed
        EntryFilter entryFilter = new EntryFilter(  );
        entryFilter.setIdDirectory( nIdDirectory );
        entryFilter.setIsIndexed( EntryFilter.FILTER_TRUE );

        List<IEntry> listIndexedEntry = EntryHome.getEntryList( entryFilter, plugin );

        entryFilter = new EntryFilter(  );
        entryFilter.setIdDirectory( nIdDirectory );
        entryFilter.setIsIndexedAsTitle( EntryFilter.FILTER_TRUE );

        List<IEntry> listIndexedAsTitleEntry = EntryHome.getEntryList( entryFilter, plugin );

        entryFilter = new EntryFilter(  );
        entryFilter.setIdDirectory( nIdDirectory );
        entryFilter.setIsIndexedAsSummary( EntryFilter.FILTER_TRUE );

        List<IEntry> listIndexedAsSummaryEntry = EntryHome.getEntryList( entryFilter, plugin );

        Document doc = getDocument( record, listIndexedEntry, listIndexedAsTitleEntry, listIndexedAsSummaryEntry, plugin );

        if ( doc != null )
        {
            List<Document> listDocument = new ArrayList<Document>( 1 );
            listDocument.add( doc );

            return listDocument;
        }
        else
        {
            return new ArrayList<Document>( 0 );
        }
    }

    /**
     * {@inheritDoc}
     */
    public void indexDocuments(  ) throws IOException, InterruptedException, SiteMessageException
    {
        Plugin plugin = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );

        // Index only the directories that have the attribute is_indexed as true
        DirectoryFilter dirFilter = new DirectoryFilter(  );
        dirFilter.setIsIndexed( DirectoryFilter.FILTER_TRUE );
        dirFilter.setIsDisabled( DirectoryFilter.FILTER_TRUE ); //Bad naming: IsDisable( true ) stands for enabled

        for ( Directory directory : DirectoryHome.getDirectoryList( dirFilter, plugin ) )
        {
            int nIdDirectory = directory.getIdDirectory(  );

            //Index only the records that have the attribute is_enable as true
            RecordFieldFilter recFilter = new RecordFieldFilter(  );
            recFilter.setIdDirectory( nIdDirectory );
            recFilter.setIsDisabled( RecordFieldFilter.FILTER_TRUE ); //Bad naming: IsDisable( true ) stands for enabled

            List<Record> listRecord = RecordHome.getListRecord( recFilter, plugin );

            //Keep processing this directory only if there are enabled records
            if ( !listRecord.isEmpty(  ) )
            {
                //Parse the entries to gather the ones marked as indexed
                EntryFilter entryFilter = new EntryFilter(  );
                entryFilter.setIdDirectory( nIdDirectory );
                entryFilter.setIsIndexed( EntryFilter.FILTER_TRUE );

                List<IEntry> listIndexedEntry = EntryHome.getEntryList( entryFilter, plugin );

                entryFilter = new EntryFilter(  );
                entryFilter.setIdDirectory( nIdDirectory );
                entryFilter.setIsIndexedAsTitle( EntryFilter.FILTER_TRUE );

                List<IEntry> listIndexedAsTitleEntry = EntryHome.getEntryList( entryFilter, plugin );

                entryFilter = new EntryFilter(  );
                entryFilter.setIdDirectory( nIdDirectory );
                entryFilter.setIsIndexedAsSummary( EntryFilter.FILTER_TRUE );

                List<IEntry> listIndexedAsSummaryEntry = EntryHome.getEntryList( entryFilter, plugin );

                for ( Record record : listRecord )
                {
                    Document recordDoc = null;

                    try
                    {
                        recordDoc = getDocument( record, listIndexedEntry, listIndexedAsTitleEntry,
                                listIndexedAsSummaryEntry, plugin );
                    }
                    catch ( Exception e )
                    {
                        String strMessage = "Directory ID : " + directory.getIdDirectory(  ) + " - Record ID : " +
                            record.getIdRecord(  );
                        IndexationService.error( this, e, strMessage );
                    }

                    if ( recordDoc != null )
                    {
                        IndexationService.write( recordDoc );
                    }
                }
            }
        }
    }

    /**
     * Builds a document which will be used by Lucene during the indexing of this record
     * @param record the record to convert into a document
     * @param listContentEntry the entries in this record that are marked as is_indexed
     * @param listTitleEntry the entries in this record that are marked as is_indexed_as_title
     * @param listSummaryEntry the entries in this record that are marked as is_indexed_as_summary
     * @param plugin the plugin object
     * @return a lucene document filled with the record data
     */
    public Document getDocument( Record record, List<IEntry> listContentEntry, List<IEntry> listTitleEntry,
        List<IEntry> listSummaryEntry, Plugin plugin )
    {
        Document doc = new Document(  );

        boolean bFallback = false;

        //Fallback if there is no entry marker as indexed_as_title
        //Uses the first indexed field instead
        if ( listTitleEntry.isEmpty(  ) && !listContentEntry.isEmpty(  ) )
        {
            listTitleEntry.add( listContentEntry.get( 0 ) );
            bFallback = true;
        }

        String strTitle = getContentToIndex( record, listTitleEntry, plugin );

        //Fallback if fields were empty
        //Uses the first indexed field instead
        if ( StringUtils.isBlank( strTitle ) && !bFallback && !listContentEntry.isEmpty(  ) )
        {
            listTitleEntry.clear(  );
            listTitleEntry.add( listContentEntry.get( 0 ) );
            strTitle = getContentToIndex( record, listTitleEntry, plugin );
        }

        //No more fallback. Giving up
        if ( StringUtils.isBlank( strTitle ) )
        {
            return null;
        }

        doc.add( new Field( SearchItem.FIELD_TITLE, strTitle, Field.Store.YES, Field.Index.ANALYZED ) );

        if ( !listContentEntry.isEmpty(  ) )
        {
            String strContent = getContentToIndex( record, listContentEntry, plugin );

            if ( StringUtils.isNotBlank( strContent ) )
            {
                doc.add( new Field( SearchItem.FIELD_CONTENTS, strContent, Field.Store.NO, Field.Index.ANALYZED ) );
            }
        }

        if ( !listSummaryEntry.isEmpty(  ) )
        {
            String strSummary = getContentToIndex( record, listSummaryEntry, plugin );

            if ( StringUtils.isNotBlank( strSummary ) )
            {
                doc.add( new Field( SearchItem.FIELD_SUMMARY, strSummary, Field.Store.YES, Field.Index.ANALYZED ) );
            }
        }

        String strRoleKey = record.getRoleKey(  );

        if ( StringUtils.isBlank( strRoleKey ) )
        {
            strRoleKey = ROLE_NONE;
        }

        doc.add( new Field( SearchItem.FIELD_ROLE, strRoleKey, Field.Store.YES, Field.Index.NO ) );

        String strDate = DateTools.dateToString( record.getDateCreation(  ), DateTools.Resolution.DAY );
        doc.add( new Field( SearchItem.FIELD_DATE, strDate, Field.Store.YES, Field.Index.NOT_ANALYZED ) );

        String strDateModification = DateTools.dateToString( record.getDateModification(  ), DateTools.Resolution.DAY );
        doc.add( new Field( SearchItem.FIELD_DATE, strDateModification, Field.Store.YES, Field.Index.NOT_ANALYZED ) );

        doc.add( new Field( SearchItem.FIELD_TYPE, DIRECTORY, Field.Store.YES, Field.Index.NOT_ANALYZED ) );

        UrlItem url = new UrlItem( AppPathService.getPortalUrl(  ) );
        url.addParameter( XPageAppService.PARAM_XPAGE_APP, DIRECTORY );
        url.addParameter( PARAMETER_ID_DIRECTORY_RECORD, record.getIdRecord(  ) );
        url.addParameter( PARAMETER_VIEW_DIRECTORY_RECORD, "" );
        doc.add( new Field( SearchItem.FIELD_URL, url.getUrl(  ), Field.Store.YES, Field.Index.NO ) );

        //Add the uid as a field, so that index can be incrementally maintained.
        // This field is not stored with question/answer, it is indexed, but it is not
        // tokenized prior to indexing.
        String strUID = Integer.toString( record.getIdRecord(  ) ) + "_" + SHORT_NAME;
        doc.add( new Field( SearchItem.FIELD_UID, strUID, Field.Store.NO, Field.Index.NOT_ANALYZED ) );

        return doc;
    }

    /**
     * Concatenates the value of the specified field in this record
     * @param record the record to seek
     * @param listEntry the list of field to concatenate
     * @param plugin the plugin object
     * @return
     */
    private String getContentToIndex( Record record, List<IEntry> listEntry, Plugin plugin )
    {
        List<Integer> listIdEntry = new ArrayList<Integer>( listEntry.size(  ) );

        for ( IEntry entry : listEntry )
        {
            listIdEntry.add( entry.getIdEntry(  ) );
        }

        StringBuffer sb = new StringBuffer(  );

        List<RecordField> listField = RecordFieldHome.getRecordFieldSpecificList( listIdEntry, record.getIdRecord(  ),
                plugin );

        for ( RecordField field : listField )
        {
            sb.append( RecordFieldHome.findByPrimaryKey( field.getIdRecordField(  ), plugin ).getValue(  ) );
            sb.append( " " );
        }

        return sb.toString(  );
    }
}

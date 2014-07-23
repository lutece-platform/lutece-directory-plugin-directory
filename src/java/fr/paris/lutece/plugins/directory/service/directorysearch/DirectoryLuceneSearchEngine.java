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

import fr.paris.lutece.portal.service.search.IndexationService;
import fr.paris.lutece.portal.service.search.LuceneSearchEngine;
import fr.paris.lutece.portal.service.util.AppLogService;

import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.BytesRef;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


/**
 * LuceneSearchEngine
 */
public class DirectoryLuceneSearchEngine implements IDirectorySearchEngine
{
    /**
     * {@inheritDoc}
     */
    @Override
    public List<Integer> getSearchResults( HashMap<String, Object> mapQuery )
    {
        ArrayList<Integer> listResults = new ArrayList<Integer>(  );
        IndexSearcher searcher = null;

        try
        {
            searcher = DirectorySearchService.getInstance(  ).getSearcher(  );

            Collection<String> queries = new ArrayList<String>(  );
            Collection<String> fields = new ArrayList<String>(  );
            Collection<BooleanClause.Occur> flags = new ArrayList<BooleanClause.Occur>(  );

            // contains id directory
            if ( mapQuery.containsKey( DirectorySearchItem.FIELD_ID_DIRECTORY ) )
            {
                Query queryIdDirectory = new TermQuery( new Term( DirectorySearchItem.FIELD_ID_DIRECTORY,
                            Integer.toString( (Integer) mapQuery.get( DirectorySearchItem.FIELD_ID_DIRECTORY ) ) ) );
                queries.add( queryIdDirectory.toString(  ) );
                fields.add( DirectorySearchItem.FIELD_ID_DIRECTORY );
                flags.add( BooleanClause.Occur.MUST );
            }

            if ( mapQuery.containsKey( DirectorySearchItem.FIELD_ID_DIRECTORY_ENTRY ) )
            {
                Query queryIdDirectory = new TermQuery( new Term( DirectorySearchItem.FIELD_ID_DIRECTORY_ENTRY,
                            Integer.toString( (Integer) mapQuery.get( DirectorySearchItem.FIELD_ID_DIRECTORY_ENTRY ) ) ) );
                queries.add( queryIdDirectory.toString(  ) );
                fields.add( DirectorySearchItem.FIELD_ID_DIRECTORY_ENTRY );
                flags.add( BooleanClause.Occur.MUST );
            }

            if ( mapQuery.containsKey( DirectorySearchItem.FIELD_ID_DIRECTORY_FIELD ) )
            {
                Collection<String> queriesIdDirectoryField = new ArrayList<String>(  );
                Collection<String> fieldsIdDirectoryField = new ArrayList<String>(  );
                Collection<BooleanClause.Occur> flagsIdDirectoryField = new ArrayList<BooleanClause.Occur>(  );

                for ( Integer idField : (List<Integer>) mapQuery.get( DirectorySearchItem.FIELD_ID_DIRECTORY_FIELD ) )
                {
                    Query queryIdDirectory = new TermQuery( new Term( DirectorySearchItem.FIELD_ID_DIRECTORY_FIELD,
                                Integer.toString( idField ) ) );
                    queriesIdDirectoryField.add( queryIdDirectory.toString(  ) );
                    fieldsIdDirectoryField.add( DirectorySearchItem.FIELD_ID_DIRECTORY_FIELD );
                    flagsIdDirectoryField.add( BooleanClause.Occur.SHOULD );
                }

                Query queryMultiIdDirectoryField = MultiFieldQueryParser.parse( IndexationService.LUCENE_INDEX_VERSION,
                        queriesIdDirectoryField.toArray( new String[queriesIdDirectoryField.size(  )] ),
                        queriesIdDirectoryField.toArray( new String[fieldsIdDirectoryField.size(  )] ),
                        flagsIdDirectoryField.toArray( new BooleanClause.Occur[flagsIdDirectoryField.size(  )] ),
                        IndexationService.getAnalyser(  ) );

                queries.add( queryMultiIdDirectoryField.toString(  ) );
                fields.add( DirectorySearchItem.FIELD_ID_DIRECTORY_FIELD );
                flags.add( BooleanClause.Occur.MUST );
            }

            //contains content
            if ( mapQuery.containsKey( DirectorySearchItem.FIELD_CONTENTS ) )
            {
                Query queryContent = new TermQuery( new Term( DirectorySearchItem.FIELD_CONTENTS,
                            (String) mapQuery.get( DirectorySearchItem.FIELD_CONTENTS ) ) );
                queries.add( queryContent.toString(  ) );
                fields.add( DirectorySearchItem.FIELD_CONTENTS );
                flags.add( BooleanClause.Occur.MUST );
            }

            //contains date
            if ( mapQuery.containsKey( DirectorySearchItem.FIELD_DATE ) )
            {
                Query queryDate = new TermQuery( new Term( DirectorySearchItem.FIELD_DATE,
                            DateTools.dateToString( (Date) mapQuery.get( DirectorySearchItem.FIELD_DATE ),
                                DateTools.Resolution.DAY ) ) );
                queries.add( queryDate.toString(  ) );
                fields.add( DirectorySearchItem.FIELD_CONTENTS );
                flags.add( BooleanClause.Occur.MUST );
            }

            //contains range date
            if ( mapQuery.containsKey( DirectorySearchItem.FIELD_DATE_BEGIN ) &&
                    mapQuery.containsKey( DirectorySearchItem.FIELD_DATE_END ) )
            {
                BytesRef strLowerTerm = new BytesRef( DateTools.dateToString( 
                            (Date) mapQuery.get( DirectorySearchItem.FIELD_DATE_BEGIN ), DateTools.Resolution.DAY ) );
                BytesRef strUpperTerm = new BytesRef( DateTools.dateToString( 
                            (Date) mapQuery.get( DirectorySearchItem.FIELD_DATE_END ), DateTools.Resolution.DAY ) );
                Query queryRangeDate = new TermRangeQuery( DirectorySearchItem.FIELD_DATE, strLowerTerm, strUpperTerm,
                        true, true );
                queries.add( queryRangeDate.toString(  ) );
                fields.add( DirectorySearchItem.FIELD_DATE );
                flags.add( BooleanClause.Occur.MUST );
            }

            //record date creation
            //contains date creation
            if ( mapQuery.containsKey( DirectorySearchItem.FIELD_DATE_CREATION ) )
            {
                Query queryDate = new TermQuery( new Term( DirectorySearchItem.FIELD_DATE_CREATION,
                            DateTools.dateToString( (Date) mapQuery.get( DirectorySearchItem.FIELD_DATE_CREATION ),
                                DateTools.Resolution.DAY ) ) );
                queries.add( queryDate.toString(  ) );
                fields.add( DirectorySearchItem.FIELD_DATE_CREATION );
                flags.add( BooleanClause.Occur.MUST );
            }

            //contains range date
            if ( mapQuery.containsKey( DirectorySearchItem.FIELD_DATE_CREATION_BEGIN ) &&
                    mapQuery.containsKey( DirectorySearchItem.FIELD_DATE_CREATION_END ) )
            {
                BytesRef strLowerTerm = new BytesRef( DateTools.dateToString( 
                            (Date) mapQuery.get( DirectorySearchItem.FIELD_DATE_CREATION_BEGIN ),
                            DateTools.Resolution.DAY ) );
                BytesRef strUpperTerm = new BytesRef( DateTools.dateToString( 
                            (Date) mapQuery.get( DirectorySearchItem.FIELD_DATE_CREATION_END ), DateTools.Resolution.DAY ) );
                Query queryRangeDate = new TermRangeQuery( DirectorySearchItem.FIELD_DATE_CREATION, strLowerTerm,
                        strUpperTerm, true, true );
                queries.add( queryRangeDate.toString(  ) );
                fields.add( DirectorySearchItem.FIELD_DATE_CREATION );
                flags.add( BooleanClause.Occur.MUST );
            }

            //record date creation
            //contains date creation
            if ( mapQuery.containsKey( DirectorySearchItem.FIELD_DATE_MODIFICATION ) )
            {
                Query queryDate = new TermQuery( new Term( DirectorySearchItem.FIELD_DATE_MODIFICATION,
                            DateTools.dateToString( 
                                (Date) mapQuery.get( DirectorySearchItem.FIELD_DATE_MODIFICATION ),
                                DateTools.Resolution.DAY ) ) );
                queries.add( queryDate.toString(  ) );
                fields.add( DirectorySearchItem.FIELD_DATE_MODIFICATION );
                flags.add( BooleanClause.Occur.MUST );
            }

            //contains range modification date
            if ( mapQuery.containsKey( DirectorySearchItem.FIELD_DATE_MODIFICATION_BEGIN ) &&
                    mapQuery.containsKey( DirectorySearchItem.FIELD_DATE_MODIFICATION_END ) )
            {
                BytesRef strLowerTerm = new BytesRef( DateTools.dateToString( 
                            (Date) mapQuery.get( DirectorySearchItem.FIELD_DATE_MODIFICATION_BEGIN ),
                            DateTools.Resolution.DAY ) );
                BytesRef strUpperTerm = new BytesRef( DateTools.dateToString( 
                            (Date) mapQuery.get( DirectorySearchItem.FIELD_DATE_MODIFICATION_END ),
                            DateTools.Resolution.DAY ) );
                Query queryRangeDate = new TermRangeQuery( DirectorySearchItem.FIELD_DATE_MODIFICATION, strLowerTerm,
                        strUpperTerm, true, true );
                queries.add( queryRangeDate.toString(  ) );
                fields.add( DirectorySearchItem.FIELD_DATE_MODIFICATION );
                flags.add( BooleanClause.Occur.MUST );
            }

            Query queryMulti = MultiFieldQueryParser.parse( IndexationService.LUCENE_INDEX_VERSION,
                    queries.toArray( new String[queries.size(  )] ), fields.toArray( new String[fields.size(  )] ),
                    flags.toArray( new BooleanClause.Occur[flags.size(  )] ), IndexationService.getAnalyser(  ) );

            // Get results documents
            TopDocs topDocs = searcher.search( queryMulti, LuceneSearchEngine.MAX_RESPONSES );
            ScoreDoc[] hits = topDocs.scoreDocs;

            for ( int i = 0; i < hits.length; i++ )
            {
                int docId = hits[i].doc;
                Document document = searcher.doc( docId );
                listResults.add( new DirectorySearchItem( document ).getIdDirectoryRecord(  ) );
            }
        }
        catch ( Exception e )
        {
            AppLogService.error( e.getMessage(  ), e );
        }

        return listResults;
    }
}

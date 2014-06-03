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
package fr.paris.lutece.plugins.directory.business;

import fr.paris.lutece.plugins.directory.utils.DirectoryErrorException;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.web.util.LocalizedPaginator;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.Paginator;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;


/**
 * 
 * IEntry Class
 */
public interface IEntry
{
    /**
     * @return the id of entry
     */
    int getIdEntry( );

    /**
     * set the id of the entry
     * @param idEntry the id of the entry
     */
    void setIdEntry( int idEntry );

    /**
     * 
     * @return the directory associate to the entry
     */
    Directory getDirectory( );

    /**
     * set the directory associate to the entry
     * @param directory the directory associate to the entry
     */
    void setDirectory( Directory directory );

    /**
     * @return title entry
     */
    String getTitle( );

    /**
     * set title entry
     * @param title title
     */
    void setTitle( String title );

    /**
     * @return the entry help message
     */
    String getHelpMessage( );

    /**
     * set entry help message
     * @param helpMessage entry helpMessage
     */
    void setHelpMessage( String helpMessage );

    /**
     * @return the entry help message for search
     */
    String getHelpMessageSearch( );

    /**
     * set the entry help message for search
     * @param helpMessage the entry help message for search
     */
    void setHelpMessageSearch( String helpMessage );

    /**
     * @return the entry comment
     */
    String getComment( );

    /**
     * set entry comment
     * @param comment entry comment
     */
    void setComment( String comment );

    /**
     * @return true if the question is mandatory
     */
    boolean isMandatory( );

    /**
     * set true if the question is mandatory
     * @param mandatory true if the question is mandatory
     */
    void setMandatory( boolean mandatory );

    /**
     * @return true if the field associate must be display in line
     */
    boolean isFieldInLine( );

    /**
     * set true if the field associate must be display in line
     * @param fieldInLine true if the field associate must be display in line
     */
    void setFieldInLine( boolean fieldInLine );

    /**
     * @return true if the field must be shown in advanced search
     */
    boolean isShownInAdvancedSearch( );

    /**
     * @return true if the field must be shown in result list
     */
    boolean isShownInResultList( );

    /**
     * set true if the field must be shown in result list page
     * @param shown true if the entry must be shown in result list page
     */
    void setShownInResultList( boolean shown );

    /**
     * @return true if the field must be shown in result record page
     */
    boolean isShownInResultRecord( );

    /**
     * set true if the field must be shown in result record page
     * @param shown true if the entry must be shown in result record page
     */
    void setShownInResultRecord( boolean shown );

    /**
     * @return true if the field must be shown in result record page
     */
    boolean isShownInHistory( );

    /**
     * set true if the field must be shown in history page
     * @param shown true if the entry must be shown in history page
     */
    void setShownInHistory( boolean shown );

    /**
     * set true if the field must be shown advanced search
     * @param shown true if the entry must be shown in advanced search
     */
    void setShownInAdvancedSearch( boolean shown );

    /**
     * @return true if the field must be shown in data export
     */
    boolean isShownInExport( );

    /**
     * set true if the field must be shown in data export
     * @param shown true if the entry must be shown in data export
     */
    void setShownInExport( boolean shown );

    /**
     * @return true if the field must be shown in record completeness
     */
    boolean isShownInCompleteness( );

    /**
     * set true if the field must be shown record completeness
     * @param shown true if the entry must be shown in record completeness
     */
    void setShownInCompleteness( boolean shown );

    /**
     * @return true if the field must be indexed
     */
    boolean isIndexed( );

    /**
     * set true if the field must be indexed
     * @param indexed true if the field must be indexed
     */
    void setIndexed( boolean indexed );

    /**
     * @return true if the field is (part of) the title of the document in the
     *         global index
     */
    boolean isIndexedAsTitle( );

    /**
     * set true if the field is (part of) the title of the document in the
     * global index
     * @param indexedAsTitle true if the field is (part of) the title of the
     *            document in the global index
     */
    void setIndexedAsTitle( boolean indexedAsTitle );

    /**
     * @return true if the field is (part of) the summary of the document in the
     *         global index
     */
    boolean isIndexedAsSummary( );

    /**
     * set true if the field is (part of) the summary of the document in the
     * global index
     * @param indexedAsSummary true if the field is (part of) the summary of the
     *            document in the global index
     */
    void setIndexedAsSummary( boolean indexedAsSummary );

    /**
     * @return position entry
     */
    int getPosition( );

    /**
     * set position entry
     * @param position position entry
     */
    void setPosition( int position );

    /**
     * @return the type of the entry
     */
    EntryType getEntryType( );

    /**
     * set the type of the entry
     * @param entryType the type of the entry
     */
    void setEntryType( EntryType entryType );

    /**
     * @return the list of field who are associate to the entry
     */
    List<Field> getFields( );

    /**
     * set the list of field who are associate to the entry
     * @param fields the list of field
     */
    void setFields( List<Field> fields );

    /**
     * @return parent entry if the entry is insert in a group
     */
    IEntry getParent( );

    /**
     * set parent entry if the entry is insert in a group
     * @param parent parent entry
     */
    void setParent( IEntry parent );

    /**
     * 
     * @return the list of entry who are insert in the group
     */
    List<IEntry> getChildren( );

    /**
     * set the list of entry who are insert in the group
     * @param children the list of entry
     */
    void setChildren( List<IEntry> children );

    /**
     * @return true if the entry is the last entry of a group or the list of
     *         entry
     */
    boolean isLastInTheList( );

    /**
     * set true if the entry is the last entry of a group or the list of entry
     * @param lastInTheList true if the entry is the last entry of a group or
     *            the list of entry
     */
    void setLastInTheList( boolean lastInTheList );

    /**
     * @return true if the entry is the first entry of a group or the list of
     *         entry
     */
    boolean isFirstInTheList( );

    /**
     * set true if the entry is the first entry of a group or the list of entry
     * @param firstInTheList true if the entry is the last entry of a group or
     *            the list of entry
     */
    void setFirstInTheList( boolean firstInTheList );

    /**
     * 
     * @return the width of the entry
     */
    int getDisplayWidth( );

    /**
     * set the width of the entry
     * @param width width of the entry
     */
    void setDisplayWidth( int width );

    /**
     * 
     * @return the height of the entry
     */
    int getDisplayHeight( );

    /**
     * set the height of the entry
     * @param height the height of the entry
     */
    void setDisplayHeight( int height );

    /**
     * 
     * @return true if a role can be associated with a item
     */
    boolean isRoleAssociated( );

    /**
     * set true if a role can be associated with a item
     * @param bRoleAssociated true if a role can be associated with a item
     */
    void setRoleAssociated( boolean bRoleAssociated );

    /**
     * 
     * @return true if a workgroup can be associated with a item
     */
    boolean isWorkgroupAssociated( );

    /**
     * set true if a workgroup can be associated with a item
     * @param bWorkGroupAssociated true if a workgroup can be associated with a
     *            item
     */
    void setWorkgroupAssociated( boolean bWorkGroupAssociated );

    /**
     * 
     * @return true if the entry is display with Multiple search field
     */
    boolean isMultipleSearchFields( );

    /**
     * set true if the entry is display with Multiple search field
     * @param multipleSearchFields true if the entry is display with Multiple
     *            search field
     */
    void setMultipleSearchFields( boolean multipleSearchFields );

    /**
     * 
     * @return the id entry associed
     */
    int getEntryAssociate( );

    /**
     * set id entry is display with Multiple search field
     * @param idEntryAssociate id entry
     */
    void setEntryAssociate( int idEntryAssociate );

    /**
     * 
     * @return the request SQL
     */
    String getRequestSQL( );

    /**
     * set request SQL
     * @param strRequestSQL request SQL
     */
    void setRequestSQL( String strRequestSQL );

    /**
     * 
     * @return true if add value for all search
     */
    boolean isAddValueAllSearch( );

    /**
     * set the AddValueAllSearch boolean
     * @param bAddValueAllSearch The AddValueAllSearch boolean
     */
    void setAddValueAllSearch( boolean bAddValueAllSearch );

    /**
     * @return true if entry is autocomplete
     */
    boolean isAutocompleteEntry( );

    /**
     * set autocomplete entry type
     * @param bIsAutocompleEntry
     */
    void setAutocompleteEntry( boolean bIsAutocompleEntry );

    /**
     * 
     * @return the label of value for all search
     */
    String getLabelValueAllSearch( );

    /**
     * Set the label value all search
     * @param strLabelValueAllSearch The label value all search
     */
    void setLabelValueAllSearch( String strLabelValueAllSearch );

    /**
     * Get the request data
     * @param request HttpRequest
     * @param locale the locale
     * @return null if all data requiered are in the request else the url of jsp
     *         error
     */
    String getEntryData( HttpServletRequest request, Locale locale );

    /**
     * save in the list of record field the record field associate to the entry
     * @param record the record associated to the record field
     * @param request HttpRequest
     * @param bTestDirectoryError true if the value contains in the request must
     *            be tested
     * @param bAddNewValue
     * @param listRecordField the list of record field associate to the record
     * @param locale the locale
     * @throws DirectoryErrorException If an error occurs
     */
    void getRecordFieldData( Record record, HttpServletRequest request, boolean bTestDirectoryError,
            boolean bAddNewValue, List<RecordField> listRecordField, Locale locale ) throws DirectoryErrorException;

    /**
     * save in the list of record field the record field associate to the entry
     * @param record the record associated to the record field
     * @param listValue the list which contains the string value of the record
     *            field
     * @param bTestDirectoryError true if the value contains in the request must
     *            be tested
     * @param bAddNewValue
     * @param listRecordField the list of record field associate to the record
     * @param locale the locale
     * @throws DirectoryErrorException If an error occurs
     */
    void getRecordFieldData( Record record, List<String> listValue, boolean bTestDirectoryError, boolean bAddNewValue,
            List<RecordField> listRecordField, Locale locale ) throws DirectoryErrorException;

    /**
     * save in the list of record field the record field associate to the entry
     * @param record the record associated to the record field
     * @param strImportValue the import string which contains the string value
     *            of the record field
     * @param bTestDirectoryError true if the value contains in the request must
     *            be tested
     * @param listRecordField the list of record field associate to the record
     * @param locale the locale
     * @throws DirectoryErrorException If an error occurs
     */
    void getImportRecordFieldData( Record record, String strImportValue, boolean bTestDirectoryError,
            List<RecordField> listRecordField, Locale locale ) throws DirectoryErrorException;

    /**
     * Get template create url
     * @return template create url
     */
    String getTemplateCreate( );

    /**
     * Get the template modify url
     * @return template modify url
     */
    String getTemplateModify( );

    /**
     * The paginator who is use in the template modify of the entry
     * @param nItemPerPage Number of items to display per page
     * @param strBaseUrl The base Url for build links on each page link
     * @param strPageIndexParameterName The parameter name for the page index
     * @param strPageIndex The current page index
     * @return the paginator who is use in the template modify of the entry
     */
    Paginator getPaginator( int nItemPerPage, String strBaseUrl, String strPageIndexParameterName, String strPageIndex );

    /**
     * Get the list of regular expression who is use in the template modify
     * @param plugin the plugin
     * @param entry the entry
     * @return the regular expression list who is use in the template modify
     */
    ReferenceList getReferenceListRegularExpression( IEntry entry, Plugin plugin );

    /**
     * Get Html code used in entry form
     * @param locale the locale
     * @param isDisplayFront true if the template front or false if the template
     *            back
     * @return html code
     * 
     * */
    String getHtmlFormEntry( Locale locale, boolean isDisplayFront );

    /**
     * Get Html code used in entry form
     * @param locale the locale
     * @param listRecordField the list of record field associate to the entry
     * @param isDisplayFront true if the template front or false if the template
     *            back
     * @return html code
     * 
     * */
    String getHtmlFormEntry( Locale locale, List<RecordField> listRecordField, boolean isDisplayFront );

    /**
     * Get Html code used in search form
     * @param locale the locale
     * @param isDisplayFront true if the template front or false if the template
     *            back
     * @return html code
     * 
     * */
    String getHtmlFormSearchEntry( Locale locale, boolean isDisplayFront );

    /**
     * Get Html code used in search form
     * @param locale the locale
     * @param listRecordField the list of record field associate to the entry
     * @param isDisplayFront true if the template front or false if the template
     *            back
     * @return html code
     * 
     * */
    String getHtmlFormSearchEntry( Locale locale, List<RecordField> listRecordField, boolean isDisplayFront );

    /**
     * Get the Html code used in display records
     * @param locale the locale
     * @param recordField the record field associate to the entry
     * @param isDisplayFront true if the template front or false if the template
     *            back
     * @return html code
     * 
     * */
    String getHtmlRecordFieldValue( Locale locale, RecordField recordField, boolean isDisplayFront );

    /**
     * convert the value contains in the record field to string
     * @param recordField the recordField
     * @param locale the locale
     * @param bDisplayFront true if display front or false if display back
     * @param bDisplayExport true if display export
     * @return string
     */
    String convertRecordFieldValueToString( RecordField recordField, Locale locale, boolean bDisplayFront,
            boolean bDisplayExport );

    /**
     * convert the title contains in the record field to string
     * @param recordField the recordField
     * @param locale the locale
     * @param bDisplayFront true if display front or false if display back
     * @return string
     */
    String convertRecordFieldTitleToString( RecordField recordField, Locale locale, boolean bDisplayFront );

    /**
     * add in the search map the criteria
     * @param recordField the record field
     * @param mapSearchItem Search map
     * 
     */
    void addSearchCriteria( HashMap<String, Object> mapSearchItem, RecordField recordField );

    /**
     * add The Xml of the entry in the string buffer
     * @param plugin plugin
     * @param locale loclae
     * @param strXml the string buffer
     * 
     */
    void getXml( Plugin plugin, Locale locale, StringBuffer strXml );

    /**
     * @param record The record
     * @param decodedBytes
     * @param nomFile
     * @param b
     * @param listRecordField
     * @param locale
     * @throws DirectoryErrorException
     */
    void getImportRecordFieldData( Record record, byte[] decodedBytes, String nomFile, boolean b,
            List<RecordField> listRecordField, Locale locale ) throws DirectoryErrorException;

    /**
     * Sets the map provider
     * @param mapProvider the map provider
     */
    void setMapProvider( IMapProvider mapProvider );

    /**
     * Gets the map provider
     * @return the map provider
     */
    IMapProvider getMapProvider( );

    /**
     * Check if the entry is sortable
     * @return true if it is sortable, false otherwise
     */
    boolean isSortable( );

    /**
     * The paginator who is use in the template modify of the entry
     * @param nItemPerPage Number of items to display per page
     * @param strBaseUrl The base Url for build links on each page link
     * @param strPageIndexParameterName The parameter name for the page index
     * @param strPageIndex The current page index
     * @param locale Locale
     * @return the paginator who is use in the template modify of the entry
     */
    LocalizedPaginator getPaginator( int nItemPerPage, String strBaseUrl, String strPageIndexParameterName,
            String strPageIndex, Locale locale );

    // SQL PART
    // ADDED TO SORT LIST WITH BETTER PERFORMANCES
    /**
     * Gets the join clause for SQL in case of query order.
     * This function has been added to provide better performances
     * to sort record list.
     * @return join clause (i.e <code>" JOIN some_table ON col1=col2 "</code>)
     * @see #isSortable
     */
    String getSQLJoin( );

    /**
     * Gets the order by clause in case of query order.
     * Does not contain ASC or DESC clause (provided by DAO).
     * This function has been added to provide better performances
     * to sort record list.
     * @return order by clause (i.e. <code>" ORDER BY some_column "</code>)
     * @see #isSortable
     */
    String getSQLOrderBy( );

    /**
     * Use with {@link #getSQLJoin()} and {@link #getSQLOrderBy()} to add
     * parameters values
     * @return an empty list if no parameter needed, parameters values
     *         otherwise.
     */
    List<Object> getSQLParametersValues( );

    /**
     * Check if the file can be uploaded or not.
     * This method will check the size of each file and the number max of files
     * that can be uploaded.
     * @param listUploadedFileItems the list of uploaded files
     * @param listFileItemsToUpload the list of files to upload
     * @param locale the locale
     * @throws DirectoryErrorException exception if there is an error
     */
    void canUploadFiles( List<FileItem> listUploadedFileItems, List<FileItem> listFileItemsToUpload, Locale locale )
            throws DirectoryErrorException;

    /**
     * Check if this entry should be anonymized when a record is anonymized.
     * @return True if this entry should be anonymized, false otherwise.
     */
    boolean getAnonymize( );

    /**
     * Set the anonymize status of an entry.
     * @param bAnonymize True if this entry should be anonymized when a record
     *            is anonymized, false otherwise
     */
    void setAnonymize( boolean bAnonymize );

    /**
     * Check if entries of this type are anonymizable or not.
     * @return True if the entry is anonymizable, false otherwise
     */
    boolean isAnonymizable( );

    /**
     * @return the _nNumberRow
     */
    int getNumberRow( );

    /**
     * @param nNumberRow the _nNumberRow to set
     */
    void setNumberRow( int nNumberRow );

    /**
     * @return the _nNumberColumn
     */
    int getNumberColumn( );

    /**
     * @param nNumberColumn the _nNumberColumn to set
     */
    void setNumberColumn( int nNumberColumn );
}

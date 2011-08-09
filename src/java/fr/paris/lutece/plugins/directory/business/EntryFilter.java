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


/**
 *
 * class EntryFilter
 *
 */
public class EntryFilter
{
    public static final String ALL_STRING = "all";
    public static final int ALL_INT = -1;
    public static final int FILTER_FALSE = 0;
    public static final int FILTER_TRUE = 1;
    private int _nIdDirectory = ALL_INT;
    private int _nIdEntryParent = ALL_INT;
    private int _nIsEntryParentNull = ALL_INT;
    private int _nIsGroup = ALL_INT;
    private int _nIsComment = ALL_INT;
    private int _nIsMyLuteceUser = ALL_INT;
    private int _nIsIndexed = ALL_INT;
    private int _nIsIndexedAsTitle = ALL_INT;
    private int _nIsIndexedAsSummary = ALL_INT;
    private int _nIsShownInResultList = ALL_INT;
    private int _nIsWorkgroupAssociated = ALL_INT;
    private int _nIsRoleAssociated = ALL_INT;
    private int _nIsShownInHistory = ALL_INT;
    private int _nIsAutocompleteEntry = ALL_INT;
    private int _nIdType = ALL_INT;
    private int _nIdEntryAssociate = ALL_INT;
    private int _nPosition = ALL_INT;
    private int _nIsShownInResultRecord = ALL_INT;
    private int _nIsShownInExport = ALL_INT;
    private int _nIsShownInCompleteness = ALL_INT;

    /**
     *
     * @return the position
     */
    public int getPosition(  )
    {
        return _nPosition;
    }

    /**
     * set position
     * @param nPosition the position
     */
    public void setPosition( int nPosition )
    {
        _nPosition = nPosition;
    }

    /**
     *
     * @return true if the filter contain an position
     */
    public boolean containsPosition(  )
    {
        return ( _nPosition != ALL_INT );
    }

    /**
     *
     * @return  the entry associate id
     */
    public int getIdEntryAssociate(  )
    {
        return _nIdEntryAssociate;
    }

    /**
     * set  the entry associate id
     * @param idEntryAssociate the entry associate
     */
    public void setIdEntryAssociate( int idType )
    {
        _nIdEntryAssociate = idType;
    }

    /**
     *
     * @return true if the filter contain an entry associate id
     */
    public boolean containsIdEntryAssociate(  )
    {
        return ( _nIdEntryAssociate != ALL_INT );
    }

    /**
     *
     * @return  the entry type id
     */
    public int getIdType(  )
    {
        return _nIdType;
    }

    /**
     * set  the entry type id
     * @param idType the entry typr
     */
    public void setIdType( int idType )
    {
        _nIdType = idType;
    }

    /**
     *
     * @return true if the filter contain an entry type id
     */
    public boolean containsIdType(  )
    {
        return ( _nIdType != ALL_INT );
    }

    /**
     *
     * @return  the id of the directory insert in the filter
     */
    public int getIdDirectory(  )
    {
        return _nIdDirectory;
    }

    /**
     * set  the id of the directory in the filter
     * @param idDirectory the id of the directory to insert in the filter
     */
    public void setIdDirectory( int idDirectory )
    {
        _nIdDirectory = idDirectory;
    }

    /**
     *
     * @return true if the filter contain an id of form
     */
    public boolean containsIdDirectory(  )
    {
        return ( _nIdDirectory != ALL_INT );
    }

    /**
     *
     * @return  the id of parent entry insert in the filter
     */
    public int getIdEntryParent(  )
    {
        return _nIdEntryParent;
    }

    /**
     * set the id of parent entry
     * @param idEntryParent the id of parent entry to insert in the filter
     */
    public void setIdEntryParent( int idEntryParent )
    {
        _nIdEntryParent = idEntryParent;
    }

    /**
     *
     * @return true if the filter contain an parent id
     */
    public boolean containsIdEntryParent(  )
    {
        return ( _nIdEntryParent != ALL_INT );
    }

    /**
     *
     * @return 1 if the entry parent must be  null,0 if the entry parent must  not be null
     */
    public int getIsEntryParentNull(  )
    {
        return _nIsEntryParentNull;
    }

    /**
     * set 1 if parent entry must be null 0 if parent entry must not be null
     * @param  nEntryParentNull   1 if parent entry must be null 0 if parent entry must not be null
     */
    public void setIsEntryParentNull( int nEntryParentNull )
    {
        _nIsEntryParentNull = nEntryParentNull;
    }

    /**
     *
     * @return true if the filter is initialized
     */
    public boolean containsIsEntryParentNull(  )
    {
        return ( _nIsEntryParentNull != ALL_INT );
    }

    /**
     *
     * @return 1 if the entry must be  a group,0 if the entry must  not be a group
     */
    public int getIsGroup(  )
    {
        return _nIsGroup;
    }

    /**
     * set 1 if the entry must be  a group,0 if the entry must  not a group
     * @param isGroup  1 if the entry must be  a group,0 if the entry must  not be a group
     */
    public void setIsGroup( int isGroup )
    {
        _nIsGroup = isGroup;
    }

    /**
     *
     * @return true  if the filter is initialized
     */
    public boolean containsIsGroup(  )
    {
        return ( _nIsGroup != ALL_INT );
    }

    /**
     *
     * @return  1 if the entry must be a comment,0 if the entry must  not be a comment
     */
    public int getIsComment(  )
    {
        return _nIsComment;
    }

    /**
     * set  1 if the entry must be a comment,0 if the entry must  not be a comment
     * @param isComment 1 if the entry must be a comment,0 if the entry must  not be a comment
     */
    public void setIsComment( int isComment )
    {
        _nIsComment = isComment;
    }

    /**
     *
     * @return true  if the entry must be a comment or must not be a comment
     */
    public boolean containsIsComment(  )
    {
        return ( _nIsComment != ALL_INT );
    }

    /**
    *
    * @return 1 if the entry associate to the record field must be in the result list,
    *                  O if the entry associate to the record field must not  be in the result list
    */
    public int getIsShownInResultList(  )
    {
        return _nIsShownInResultList;
    }

    /**
     * set 1 if the entry associate to the record field must be in the result list,
     *      O if the entry associate to the record field must not  be in the result list
     *  @param nIsShown  1 if the entry associate to the record field must be in the result list
     */
    public void setIsShownInResultList( int nIsShown )
    {
        _nIsShownInResultList = nIsShown;
    }

    /**
    *
    * @return true if the filter is initialized
    */
    public boolean containsIsShownInResultList(  )
    {
        return ( _nIsShownInResultList != ALL_INT );
    }

    /**
    *
    * @return 1 if the entry associate to the record field must be in the result record,
    *         0 if the entry associate to the record field must not be in the result record
    */
    public int getIsShownInResultRecord(  )
    {
        return _nIsShownInResultRecord;
    }

    /**
     * set 1 if the entry associate to the record field must be in the result record,
     *     0 if the entry associate to the record field must not be in the result record
     * @param nIsShownInResultRecord 1 if the entry associate to the record field must be in the result list
     */
    public void setIsShownInResultRecord( int nIsShownInResultRecord )
    {
        _nIsShownInResultRecord = nIsShownInResultRecord;
    }

    /**
    *
    * @return true if the filter is initialized
    */
    public boolean containsIsShownInResultRecord(  )
    {
        return ( _nIsShownInResultRecord != ALL_INT );
    }

    /**
    *
    * @return 1 if the entry associate to the record field must be in the history,
    *                  O if the entry associate to the record field must not  be in the history
    */
    public int getIsShownInHistory(  )
    {
        return _nIsShownInHistory;
    }

    /**
     * set 1 if the entry associate to the record field must be in the history,
    *      O if the entry associate to the record field must not  be in the history
    *  @param nIsShown  1 if the entry associate to the record field must be in the history
     */
    public void setIsShownInHistory( int nIsShown )
    {
        _nIsShownInHistory = nIsShown;
    }

    /**
     *
     * @return true if the filter is initialized
     */
    public boolean containsIsShownInHistory(  )
    {
        return ( _nIsShownInHistory != ALL_INT );
    }

    /**
     *
     * @return 1 if the entry associate to the record field is autocompleted,
     *                  O if the entry associate to the record field is autocompleted
     */
    public int getIsAutocompleteEntry(  )
    {
        return _nIsAutocompleteEntry;
    }

    /**
     * set 1 if the entry associate to the record field is autocompleted,
     *      O if the entry associate to the record field is autocompleted
     *  @param nIsShown  1 if the entry associate to the record field is autocompleted
     */
    public void setIsAutocompleteEntry( int nIsAutocompleteEntry )
    {
        _nIsAutocompleteEntry = nIsAutocompleteEntry;
    }

    /**
     *
     * @return true if the filter is initialized
     */
    public boolean containsIsAutocompleteEntry(  )
    {
        return ( _nIsAutocompleteEntry != ALL_INT );
    }

    /**
    *
    * @return  1 if the entry must be indexed,0 if the entry must  not be indexed
    */
    public int getIsIndexed(  )
    {
        return _nIsIndexed;
    }

    /**
     * set  1 if the entry must be indexed,0 if the entry must  not be a indexed
     * @param isIndexed 1 if the entry must be indexed,0 if the entry must  not be indexed
     */
    public void setIsIndexed( int isIndexed )
    {
        _nIsIndexed = isIndexed;
    }

    /**
     *
     * @return true  if the entry must be indexed or must not be indexed
     */
    public boolean containsIsIndexed(  )
    {
        return ( _nIsIndexed != ALL_INT );
    }
    
    /**
	 *
	 * @return  1 if the entry should be (part of) the title of the document in 
	 * the global index, 0 otherwise
	 */
    public int getIsIndexedAsTitle(  )
    {
        return _nIsIndexedAsTitle;
    }

    /**
     * set  1 if the entry should be (part of) the title of the document in 
     * the global index, 0 otherwise
     * @param isIndexed 1 if the entry should be (part of) the title of the document in 
     * the global index, 0 otherwise
     */
    public void setIsIndexedAsTitle( int isIndexedAsTitle )
    {
        _nIsIndexedAsTitle = isIndexedAsTitle;
    }

    /**
     *
     * @return true  if the entry should be (part of) the title of the document in 
     * the global index, false otherwise
     */
    public boolean containsIsIndexedAsTitle(  )
    {
        return ( _nIsIndexedAsTitle != ALL_INT );
    }
    
    /**
	 *
	 * @return  1 if the entry should be (part of) the summary of the document in 
	 * the global index, 0 otherwise
	 */
   public int getIsIndexedAsSummary(  )
   {
       return _nIsIndexedAsSummary;
   }

   /**
    * set  1 if the entry should be (part of) the summary of the document in 
    * the global index, 0 otherwise
    * @param isIndexed 1 if the entry should be (part of) the summary of the document in 
    * the global index, 0 otherwise
    */
   public void setIsIndexedAsSummary( int isIndexedAsSummary )
   {
       _nIsIndexedAsSummary = isIndexedAsSummary;
   }

   /**
    *
    * @return true  if the entry should be (part of) the summary of the document in 
    * the global index, false otherwise
    */
   public boolean containsIsIndexedAsSummary(  )
   {
       return ( _nIsIndexedAsSummary != ALL_INT );
   }

    /**
    *
    * @return  1 if the entry must be associated with workgroup ,0 if the entry must not be associated with workgroup
    */
    public int getIsWorkgroupAssociated(  )
    {
        return _nIsWorkgroupAssociated;
    }

    /**
     * set   1 if the entry must be associated with workgroup ,0 if the entry must not be associated with workgroup
     * @param isWorkgroupAssociated 1 if the entry must be associated with workgroup ,0 if the entry must not be  associated with workgroup
     */
    public void setIsWorkgroupAssociated( int isWorkgroupAssociated )
    {
        _nIsWorkgroupAssociated = isWorkgroupAssociated;
    }

    /**
     *
     * @return true  if the entry must be associated with workgroup or must not be  associated with workgroup
     */
    public boolean containsIsWorkgroupAssociated(  )
    {
        return ( _nIsWorkgroupAssociated != ALL_INT );
    }

    /**
    *
    * @return  1 if the entry must be associated with role ,0 if the entry must not be associated with role
    */
    public int getIsRoleAssociated(  )
    {
        return _nIsRoleAssociated;
    }

    /**
     * set   1 if the entry must be associated with role,0 if the entry must not be associated with role
     * @param isRoleAssociated 1 if the entry must be associated with role ,0 if the entry must not be  associated with role
     */
    public void setIsRoleAssociated( int isRoleAssociated )
    {
        _nIsRoleAssociated = isRoleAssociated;
    }

    /**
     *
     * @return true  if the entry must be associated with role or must not be  associated with role
     */
    public boolean containsIsRoleAssociated(  )
    {
        return ( _nIsRoleAssociated != ALL_INT );
    }

    /**
     *
     * @return 1 if the entry associate to the record field is shown in export,
     *                  O if the entry associate to the record field is shown in export
     */
    public int getIsShownInExport(  )
    {
        return _nIsShownInExport;
    }

    /**
     * set 1 if the entry associate to the record field is shown in export,
     *      O if the entry associate to the record field is shown in export
     *  @param nIsShown  1 if the entry associate to the record field is shown in export
     */
    public void setIsShownInExport( int nIsShownInExport )
    {
        _nIsShownInExport = nIsShownInExport;
    }

    /**
     *
     * @return true if the filter is initialized
     */
    public boolean containsIsShownInExport(  )
    {
        return ( _nIsShownInExport != ALL_INT );
    }
    
    /**
    *
    * @return 1 if the entry associate to the record field is shown in record completeness,
    *         O if the entry associate to the record field is shown in record completeness
    */
   public int getIsShownInCompleteness(  )
   {
       return _nIsShownInCompleteness;
   }

   /**
    * set 1 if the entry associate to the record field is shown in record completeness,
    *     O if the entry associate to the record field is shown in record completeness
    *  @param nIsShownInCompleteness 1 if the entry associate to the record field is shown in record completeness
    */
   public void setIsShownInCompleteness( int nIsShownInCompleteness )
   {
       _nIsShownInCompleteness = nIsShownInCompleteness;
   }

   /**
    *
    * @return true if the filter is initialized
    */
   public boolean containsIsShownInCompleteness(  )
   {
       return ( _nIsShownInCompleteness != ALL_INT );
   }

    /**
    *
    * @return  1 if the entry must be a MyluteceUser,0 if the entry must  not be a MyluteceUser
    */
    public int getIsMyLuteceUser(  )
    {
        return _nIsMyLuteceUser;
    }

    /**
     * set  1 if the entry must be a MyluteceUser,0 if the entry must  not be a MyluteceUser
     * @param isMyLuteceUser 1 if the entry must be a MyluteceUser,0 if the entry must  not be a MyluteceUser
     */
    public void setIsMyLuteceUser( int isMyLuteceUser )
    {
        _nIsMyLuteceUser = isMyLuteceUser;
    }

    /**
     *
     * @return true  if the entry must be a MyluteceUser or must not be a MyluteceUser
     */
    public boolean containsIsMyLuteceUser(  )
    {
        return ( _nIsMyLuteceUser != ALL_INT );
    }
}

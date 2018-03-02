/*
 * Copyright (c) 2002-2017, Mairie de Paris
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
 * class entryType
 *
 */
public class EntryType
{
    private int _nIdType;
    private String _strTitleI18nKey;
    private String _strClassName;
    private Boolean _bGroup;
    private Boolean _bComment;
    private Boolean _bIsMyLuteceUser;

    /**
     *
     * @return the id of the entry type
     */
    public int getIdType( )
    {
        return _nIdType;
    }

    /**
     * set the id of the entry type
     * 
     * @param idType
     *            the id of the entry type
     */
    public void setIdType( int idType )
    {
        _nIdType = idType;
    }

    /***
     *
     * @return true if the type is a group
     */
    public Boolean getGroup( )
    {
        return _bGroup;
    }

    /**
     * set true if the type is a group
     * 
     * @param isGroup
     *            if the type is a group
     */
    public void setGroup( Boolean isGroup )
    {
        _bGroup = isGroup;
    }

    /**
     *
     * @return the I18n title key of the entry type
     */
    public String getTitleI18nKey( )
    {
        return _strTitleI18nKey;
    }

    /**
     * set the I18n title key of the entry type
     * 
     * @param title
     *            the title of the entry type
     */
    public void setTitleI18nKey( String title )
    {
        _strTitleI18nKey = title;
    }

    /**
     *
     * @return the path for acces to the Classe Entry
     */
    public String getClassName( )
    {
        return _strClassName;
    }

    /**
     * set the path for acces to the Class Entry
     * 
     * @param className
     *            the path for acces to the Class Entry
     */
    public void setClassName( String className )
    {
        _strClassName = className;
    }

    /**
     *
     * @return true if the type is a comment
     */
    public Boolean getComment( )
    {
        return _bComment;
    }

    /**
     *
     * @param isComment
     *            set true if the type is a comment
     */
    public void setComment( Boolean isComment )
    {
        _bComment = isComment;
    }

    /**
     *
     * @return true if the type is a mylutece user
     */
    public Boolean getMyLuteceUser( )
    {
        return _bIsMyLuteceUser;
    }

    /**
     * Set the mylutece user flag
     * 
     * @param isMyLuteceUser
     *            set true if the type is a mylutece user
     */
    public void setMyLuteceUser( Boolean isMyLuteceUser )
    {
        _bIsMyLuteceUser = isMyLuteceUser;
    }
}

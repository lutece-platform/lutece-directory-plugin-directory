/*
 * Copyright (c) 2002-2013, Mairie de Paris
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
 * class Category
 *
 */
public class Category
{
    public static final int ID_CATEGORY_EXPORT = 1;
    public static final int ID_CATEGORY_STYLE_FORM_SEARCH = 2;
    public static final int ID_CATEGORY_STYLE_RESULT_LIST = 3;
    public static final int ID_CATEGORY_STYLE_RESULT_RECORD = 4;
    private int _nIdCategory;
    private String _strTitleI18nKey;

    /**
     *
     * @return the id of the category
     */
    public int getIdCategory(  )
    {
        return _nIdCategory;
    }

    /**
     * set the id of the category
     * @param idCategory the id of the category
     */
    public void setIdCategory( int idCategory )
    {
        _nIdCategory = idCategory;
    }

    /**
     *
     * @return the title key of the category
     */
    public String getTitleI18nKey(  )
    {
        return _strTitleI18nKey;
    }

    /**
     * set the title key of the category
     * @param title the title key of the category
     */
    public void setTitleI18nKey( String title )
    {
        _strTitleI18nKey = title;
    }
}

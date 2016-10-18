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

import fr.paris.lutece.portal.service.plugin.Plugin;

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
import java.util.List;

/**
 * IFormDAO Interface
 */
public interface IFieldDAO
{
    /**
     * Insert a new record in the table.
     *
     * @param field
     *            instance of the Field object to insert
     * @param plugin
     *            the plugin
     * @return the new field create
     */
    int insert( Field field, Plugin plugin );

    /**
     * Update the field in the table
     *
     * @param field
     *            instance of the Field object to update
     * @param plugin
     *            the plugin
     */
    void store( Field field, Plugin plugin );

    /**
     * Delete a record from the table
     *
     * @param nIdField
     *            The identifier of the field
     * @param plugin
     *            the plugin
     */
    void delete( int nIdField, Plugin plugin );

    // /////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Load the data of the Field from the table
     *
     * @param nIdField
     *            The identifier of the field
     * @param plugin
     *            the plugin
     * @return the instance of the Field
     */
    Field load( int nIdField, Plugin plugin );

    /**
     * Load the data of the Field from the table by value
     *
     * @param nIdEntry
     *            the entry id
     * @param strValue
     *            the field value
     * @param plugin
     *            the plugin
     * @return the instance of the Field
     */
    Field loadByValue( int nIdEntry, String strValue, Plugin plugin );

    /**
     * Load the data of all the field of the entry and returns them in a list
     * 
     * @param idEntry
     *            the id of the entry
     * @param plugin
     *            the plugin
     * @return the list of field
     */
    List<Field> selectFieldListByIdEntry( int idEntry, Plugin plugin );

    /**
     * Delete an association between field and a regular expression
     *
     * @param nIdField
     *            The identifier of the field
     * @param nIdExpression
     *            The identifier of the regular expression
     * @param plugin
     *            the plugin
     */
    void deleteVerifyBy( int nIdField, int nIdExpression, Plugin plugin );

    /**
     * insert an association between field and a regular expression
     *
     * @param nIdField
     *            The identifier of the field
     * @param nIdExpression
     *            The identifier of the regular expression
     * @param plugin
     *            the plugin
     */
    void insertVerifyBy( int nIdField, int nIdExpression, Plugin plugin );

    /**
     * Load the key of all the regularExpression associate to the field and returns them in a list
     * 
     * @param nIdField
     *            the id of the field
     * @param plugin
     *            the plugin
     * @return the list of regular expression key
     */
    List<Integer> selectListRegularExpressionKeyByIdField( int nIdField, Plugin plugin );

    /**
     * verify if the regular expresssion is use
     *
     * @param nIdExpression
     *            The identifier of the expression
     * @param plugin
     *            the plugin
     * @return true if the regular expression is use
     */
    boolean isRegularExpressionIsUse( int nIdExpression, Plugin plugin );
}

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

import fr.paris.lutece.portal.business.regularexpression.RegularExpression;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.regularexpression.RegularExpressionService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.ArrayList;
import java.util.List;


/**
 * This class provides instances management methods (create, find, ...) for Field objects
 */
public final class FieldHome
{
    // Static variable pointed at the DAO instance
    private static IFieldDAO _dao = SpringContextService.getBean( "directoryFieldDAO" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private FieldHome(  )
    {
    }

    /**
     * Creation of an instance of field
     *
     * @param field The instance of the Field which contains the informations to store
     * @param plugin the Plugin
     * @return The primary key of the new Field.
     */
    public static int create( Field field, Plugin plugin )
    {
        return _dao.insert( field, plugin );
    }

    /**
     * Copy of an instance of field
     *
     * @param field The instance of the Field who must copy
     * @param plugin the Plugin
     *
     */
    public static void copy( Field field, Plugin plugin )
    {
        Field fieldCopy = field;
        fieldCopy.setIdField( create( field, plugin ) );

        for ( RegularExpression regularExpression : field.getRegularExpressionList(  ) )
        {
            createVerifyBy( fieldCopy.getIdField(  ), regularExpression.getIdExpression(  ), plugin );
        }
    }

    /**
     * Update of the field which is specified in parameter
     *
     * @param field The instance of the Field which contains the informations to update
     * @param plugin the Plugin
     *
     */
    public static void update( Field field, Plugin plugin )
    {
        _dao.store( field, plugin );
    }

    /**
     * Remove the field whose identifier is specified in parameter
     *
     * @param nIdField The field Id
     * @param plugin the Plugin
     */
    public static void remove( int nIdField, Plugin plugin )
    {
        List<Integer> listRegularExpressionKeyEntry = getListRegularExpressionKeyByIdField( nIdField, plugin );

        for ( Integer regularExpressionKey : listRegularExpressionKeyEntry )
        {
            removeVerifyBy( nIdField, regularExpressionKey, plugin );
        }

        _dao.delete( nIdField, plugin );
    }

    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Returns an instance of a Field whose identifier is specified in parameter
     *
     * @param nKey The field primary key
     * @param plugin the Plugin
     * @return an instance of Field
     */
    public static Field findByPrimaryKey( int nKey, Plugin plugin )
    {
        Field field = _dao.load( nKey, plugin );

        if ( field != null )
        {
            List<RegularExpression> listRegularExpression = new ArrayList<RegularExpression>(  );

            if ( RegularExpressionService.getInstance(  ).isAvailable(  ) )
            {
                List<Integer> listRegularExpressionKeyEntry = getListRegularExpressionKeyByIdField( nKey, plugin );

                if ( ( listRegularExpressionKeyEntry != null ) && ( listRegularExpressionKeyEntry.size(  ) != 0 ) )
                {
                    RegularExpression regularExpression = null;

                    for ( Integer regularExpressionKey : listRegularExpressionKeyEntry )
                    {
                        regularExpression = RegularExpressionService.getInstance(  )
                                                                    .getRegularExpressionByKey( regularExpressionKey );

                        if ( regularExpression != null )
                        {
                            listRegularExpression.add( regularExpression );
                        }
                    }
                }
            }

            field.setRegularExpressionList( listRegularExpression );
        }

        return field;
    }

    /**
     * Returns an instance of a Field whose value is specified in parameter
     *
     * @param nIdEntry The entry id
     * @param strValue The value
     * @param plugin the Plugin
     * @return an instance of Field
     */
    public static Field findByValue( int nIdEntry, String strValue, Plugin plugin )
    {
        Field field = _dao.loadByValue( nIdEntry, strValue, plugin );

        if ( field != null )
        {
            List<RegularExpression> listRegularExpression = new ArrayList<RegularExpression>(  );

            if ( RegularExpressionService.getInstance(  ).isAvailable(  ) )
            {
                List<Integer> listRegularExpressionKeyEntry = getListRegularExpressionKeyByIdField( field.getIdField(  ),
                        plugin );

                if ( ( listRegularExpressionKeyEntry != null ) && ( listRegularExpressionKeyEntry.size(  ) != 0 ) )
                {
                    RegularExpression regularExpression = null;

                    for ( Integer regularExpressionKey : listRegularExpressionKeyEntry )
                    {
                        regularExpression = RegularExpressionService.getInstance(  )
                                                                    .getRegularExpressionByKey( regularExpressionKey );

                        if ( regularExpression != null )
                        {
                            listRegularExpression.add( regularExpression );
                        }
                    }
                }
            }

            field.setRegularExpressionList( listRegularExpression );
        }

        return field;
    }

    /**
     * Load the data of all the field of the entry  and returns them in a  list
     * @param nIdEntry the id of the entry
     * @param plugin the plugin
     * @return  the list of field
     */
    public static List<Field> getFieldListByIdEntry( int nIdEntry, Plugin plugin )
    {
        return _dao.selectFieldListByIdEntry( nIdEntry, plugin );
    }

    /**
     * Delete an association between  field and a regular expression
     *
     * @param nIdField The identifier of the field
     *  @param nIdExpression The identifier of the regular expression
     * @param plugin the plugin
     */
    public static void removeVerifyBy( int nIdField, int nIdExpression, Plugin plugin )
    {
        _dao.deleteVerifyBy( nIdField, nIdExpression, plugin );
    }

    /**
     * insert an association between  field and a regular expression
     *
     * @param nIdField The identifier of the field
     * @param nIdExpression The identifier of the regular expression
     * @param plugin the plugin
     */
    public static void createVerifyBy( int nIdField, int nIdExpression, Plugin plugin )
    {
        _dao.insertVerifyBy( nIdField, nIdExpression, plugin );
    }

    /**
         * Load the key  of all the regularExpression associate to the field and returns them in a  list
         * @param nIdField the id of the field
         * @param plugin the plugin
         * @return  the list of  regular expression key
         */
    public static List<Integer> getListRegularExpressionKeyByIdField( int nIdField, Plugin plugin )
    {
        return _dao.selectListRegularExpressionKeyByIdField( nIdField, plugin );
    }

    /**
     * verify if the regular expresssion is use
     *
     * @param nIdExpression The identifier of the regular expression
     * @param plugin the plugin
     * @return true if the regular expression is use
     */
    public static boolean isRegularExpressionIsUse( int nIdExpression, Plugin plugin )
    {
        return _dao.isRegularExpressionIsUse( nIdExpression, plugin );
    }
}

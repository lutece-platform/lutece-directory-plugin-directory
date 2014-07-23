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
package fr.paris.lutece.plugins.directory.business.parameter;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.sql.DAOUtil;


/**
 *
 * DirectoryParameterDAO
 *
 */
public class DirectoryParameterDAO implements IDirectoryParameterDAO
{
    private static final String TRUE = "1";
    private static final String SQL_QUERY_SELECT = " SELECT parameter_value FROM directory_directory_parameter WHERE parameter_key = ? ";
    private static final String SQL_QUERY_UPDATE = " UPDATE directory_directory_parameter SET parameter_value = ? WHERE parameter_key = ? ";
    private static final String SQL_QUERY_SELECT_ALL = " SELECT parameter_key, parameter_value FROM directory_directory_parameter ";
    private static final String SQL_ORDER_BY = " ORDER BY ";
    private static final String SQL_ASC = " ASC ";
    private static final String SQL_PARAMETER_KEY = " parameter_key ";
    private static final String SQL_WHERE = " WHERE ";
    private static final String SQL_IN = " IN ";
    private static final String SQL_NOT = " NOT ";
    private static final String OPEN_BRACKET = " ( ";
    private static final String CLOSED_BRACKET = " ) ";
    private static final String SIMPLE_QUOTE = "'";
    private static final String COMMA = ",";

    /**
     * {@inheritDoc}
     */
    @Override
    public ReferenceList selectAll( Plugin plugin )
    {
        ReferenceList listParams = new ReferenceList(  );
        String strSQL = SQL_QUERY_SELECT_ALL + SQL_ORDER_BY + SQL_PARAMETER_KEY + SQL_ASC;
        DAOUtil daoUtil = new DAOUtil( strSQL, plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            ReferenceItem param = new ReferenceItem(  );
            param.setCode( daoUtil.getString( 1 ) );
            param.setName( daoUtil.getString( 2 ) );
            param.setChecked( TRUE.equals( param.getName(  ) ) ? true : false );
            listParams.add( param );
        }

        daoUtil.free(  );

        return listParams;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReferenceItem load( String strParameterKey, Plugin plugin )
    {
        ReferenceItem param = null;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.setString( 1, strParameterKey );
        daoUtil.executeQuery(  );

        if ( daoUtil.next(  ) )
        {
            param = new ReferenceItem(  );
            param.setCode( strParameterKey );
            param.setName( daoUtil.getString( 1 ) );
            param.setChecked( TRUE.equals( param.getName(  ) ) ? true : false );
        }

        daoUtil.free(  );

        return param;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void store( ReferenceItem param, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );

        daoUtil.setString( 1, param.getName(  ) );
        daoUtil.setString( 2, param.getCode(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReferenceList selectByFilter( DirectoryParameterFilter filter, Plugin plugin )
    {
        // Build SQL query
        StringBuilder sbSQL = new StringBuilder( SQL_QUERY_SELECT_ALL );

        if ( filter.containsListParameterKeys(  ) )
        {
            sbSQL.append( SQL_WHERE + SQL_PARAMETER_KEY );

            if ( filter.excludeParameterKeys(  ) )
            {
                sbSQL.append( SQL_NOT );
            }

            sbSQL.append( SQL_IN );
            sbSQL.append( OPEN_BRACKET );

            for ( int i = 0; i < filter.getListParameterKeys(  ).size(  ); i++ )
            {
                String strParameterKey = filter.getListParameterKeys(  ).get( i );
                sbSQL.append( SIMPLE_QUOTE );
                sbSQL.append( strParameterKey );
                sbSQL.append( SIMPLE_QUOTE );

                if ( i < ( filter.getListParameterKeys(  ).size(  ) - 1 ) )
                {
                    sbSQL.append( COMMA );
                }
            }

            sbSQL.append( CLOSED_BRACKET );
        }

        // Execute SQL query
        ReferenceList listParams = new ReferenceList(  );
        DAOUtil daoUtil = new DAOUtil( sbSQL.toString(  ), plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            ReferenceItem param = new ReferenceItem(  );
            param.setCode( daoUtil.getString( 1 ) );
            param.setName( daoUtil.getString( 2 ) );
            param.setChecked( TRUE.equals( param.getName(  ) ) ? true : false );
            listParams.add( param );
        }

        daoUtil.free(  );

        return listParams;
    }
}

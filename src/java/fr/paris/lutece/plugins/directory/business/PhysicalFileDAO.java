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
import fr.paris.lutece.util.sql.DAOUtil;


/**
 * This class provides Data Access methods for Field objects
 */
public final class PhysicalFileDAO implements IPhysicalFileDAO
{
    // Constants
    private static final String SQL_QUERY_NEW_PK = "SELECT max( id_physical_file ) FROM directory_physical_file";
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = "SELECT id_physical_file,file_value" +
        " FROM directory_physical_file WHERE id_physical_file = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO directory_physical_file(id_physical_file,file_value)" +
        " VALUES(?,?)";
    private static final String SQL_QUERY_DELETE = "DELETE FROM directory_physical_file WHERE id_physical_file = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE  directory_physical_file SET " +
        "id_physical_file=?,file_value=? WHERE id_physical_file = ?";

    /* (non-Javadoc)
         * @see fr.paris.lutece.plugins.directory.business.IPhysicalFile#newPrimaryKey(fr.paris.lutece.portal.service.plugin.Plugin)
         */
    public int newPrimaryKey( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_PK, plugin );
        daoUtil.executeQuery(  );

        int nKey;

        if ( !daoUtil.next(  ) )
        {
            // if the table is empty
            nKey = 1;
        }

        nKey = daoUtil.getInt( 1 ) + 1;
        daoUtil.free(  );

        return nKey;
    }

    /* (non-Javadoc)
         * @see fr.paris.lutece.plugins.directory.business.IPhysicalFile#insert(fr.paris.lutece.plugins.directory.business.PhysicalFile, fr.paris.lutece.portal.service.plugin.Plugin)
         */
    public synchronized int insert( PhysicalFile physicalFile, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );
        daoUtil.setBytes( 2, physicalFile.getValue(  ) );
        physicalFile.setIdPhysicalFile( newPrimaryKey( plugin ) );
        daoUtil.setInt( 1, physicalFile.getIdPhysicalFile(  ) );
        daoUtil.executeUpdate(  );

        daoUtil.free(  );

        return physicalFile.getIdPhysicalFile(  );
    }

    /* (non-Javadoc)
       * @see fr.paris.lutece.plugins.directory.business.IPhysicalFile#load(int, fr.paris.lutece.portal.service.plugin.Plugin)
       */
    public PhysicalFile load( int nId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, plugin );
        daoUtil.setInt( 1, nId );
        daoUtil.executeQuery(  );

        PhysicalFile physicalFile = null;

        if ( daoUtil.next(  ) )
        {
            physicalFile = new PhysicalFile(  );
            physicalFile.setIdPhysicalFile( daoUtil.getInt( 1 ) );
            physicalFile.setValue( daoUtil.getBytes( 2 ) );
        }

        daoUtil.free(  );

        return physicalFile;
    }

    /* (non-Javadoc)
       * @see fr.paris.lutece.plugins.directory.business.IPhysicalFile#delete(int, fr.paris.lutece.portal.service.plugin.Plugin)
       */
    public void delete( int nIdPhysicalFile, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nIdPhysicalFile );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /* (non-Javadoc)
         * @see fr.paris.lutece.plugins.directory.business.IPhysicalFile#store(fr.paris.lutece.plugins.directory.business.PhysicalFile, fr.paris.lutece.portal.service.plugin.Plugin)
         */
    public void store( PhysicalFile physicalFile, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );
        daoUtil.setInt( 1, physicalFile.getIdPhysicalFile(  ) );
        daoUtil.setBytes( 2, physicalFile.getValue(  ) );
        daoUtil.setInt( 3, physicalFile.getIdPhysicalFile(  ) );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }
}

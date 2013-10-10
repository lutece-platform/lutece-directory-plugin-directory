package fr.paris.lutece.plugins.directory.service.record;

import fr.paris.lutece.plugins.directory.business.Record;
import fr.paris.lutece.plugins.directory.business.RecordFieldHome;
import fr.paris.lutece.plugins.directory.business.RecordHome;
import fr.paris.lutece.plugins.directory.service.DirectoryPlugin;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.resource.IExtendableResource;
import fr.paris.lutece.portal.service.resource.IExtendableResourceService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.util.url.UrlItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;


/**
 * Extendable resource service for directory records
 */
public class DirectoryRecordExtendableResourceService implements IExtendableResourceService
{
    private static final String MESSAGE_DIRECTORY_RECORD_RESOURCE_TYPE_DESCRIPTION = "directory.resource.resourceTypeDescription";

    private static final String PARAMETER_PAGE = "page";
    private static final String PARAMETER_ID_DIRECTORY = "id_directory";
    private static final String PARAMETER_ID_DIRECTORY_RECORD = "id_directory_record";
    private static final String CONSTANT_DIRECTORY = "directory";

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isInvoked( String strResourceType )
    {
        return Record.EXTENDABLE_RESOURCE_TYPE.equals( strResourceType );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IExtendableResource getResource( String strIdResource, String strResourceType )
    {
        if ( StringUtils.isNotBlank( strIdResource ) && StringUtils.isNumeric( strIdResource ) )
        {
            int nIdRecord = Integer.parseInt( strIdResource );
            Plugin plugin = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
            Record record = RecordHome.findByPrimaryKey( nIdRecord, plugin );
            List<Integer> listIdRecord = new ArrayList<Integer>( 1 );
            listIdRecord.add( nIdRecord );
            record.setListRecordField( RecordFieldHome.getRecordFieldListByRecordIdList( listIdRecord, plugin ) );
            return record;
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getResourceType( )
    {
        return Record.EXTENDABLE_RESOURCE_TYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getResourceTypeDescription( Locale locale )
    {
        return I18nService.getLocalizedString( MESSAGE_DIRECTORY_RECORD_RESOURCE_TYPE_DESCRIPTION, locale );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getResourceUrl( String strIdResource, String strResourceType )
    {
        if ( StringUtils.isNotBlank( strIdResource ) && StringUtils.isNumeric( strIdResource ) )
        {
            Record record = RecordHome.findByPrimaryKey( Integer.parseInt( strIdResource ),
                    PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME ) );
            UrlItem urlItem = new UrlItem( AppPathService.getPortalUrl( ) );
            urlItem.addParameter( PARAMETER_PAGE, CONSTANT_DIRECTORY );
            urlItem.addParameter( PARAMETER_ID_DIRECTORY, record.getDirectory( ).getIdDirectory( ) );
            urlItem.addParameter( PARAMETER_ID_DIRECTORY_RECORD, strIdResource );
            return urlItem.getUrl( );
        }
        return null;
    }

}

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
package fr.paris.lutece.plugins.directory.web;

import fr.paris.lutece.plugins.directory.business.Category;
import fr.paris.lutece.plugins.directory.business.CategoryHome;
import fr.paris.lutece.plugins.directory.business.DirectoryAction;
import fr.paris.lutece.plugins.directory.business.DirectoryActionHome;
import fr.paris.lutece.plugins.directory.business.DirectoryXsl;
import fr.paris.lutece.plugins.directory.business.DirectoryXslFilter;
import fr.paris.lutece.plugins.directory.business.DirectoryXslHome;
import fr.paris.lutece.plugins.directory.business.File;
import fr.paris.lutece.plugins.directory.business.FileHome;
import fr.paris.lutece.plugins.directory.service.DirectoryXslRemovalListenerService;
import fr.paris.lutece.plugins.directory.service.DirectoryXslResourceIdService;
import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.portal.business.rbac.RBAC;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.web.admin.PluginAdminPageJspBean;
import fr.paris.lutece.portal.web.util.LocalizedPaginator;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.url.UrlItem;

import org.xml.sax.InputSource;

import java.io.ByteArrayInputStream;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


/**
 *
 * class DirectoryXslJspBean
 *
 */
public class DirectoryXslJspBean extends PluginAdminPageJspBean
{
    //	templates
    private static final String TEMPLATE_MANAGE_DIRECTORY_XSL = "admin/plugins/directory/manage_directory_xsl.html";
    private static final String TEMPLATE_CREATE_DIRECTORY_XSL = "admin/plugins/directory/create_directory_xsl.html";
    private static final String TEMPLATE_MODIFY_DIRECTORY_XSL = "admin/plugins/directory/modify_directory_xsl.html";

    //	Markers
    private static final String MARK_DIRECTORY_XSL_LIST = "directory_xsl_list";
    private static final String MARK_CATEGORY_LIST = "category_list";
    private static final String MARK_DIRECTORY_XSL = "directory_xsl";
    private static final String MARK_PAGINATOR = "paginator";
    private static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";
    private static final String MARK_PERMISSION_CREATE = "right_create";

    //	parameters form
    private static final String PARAMETER_ID_DIRECTORY_XSL = "id_directory_xsl";
    private static final String PARAMETER_ID_CATEGORY_XSL = "id_category";
    private static final String PARAMETER_ID_FILE = "id_file";
    private static final String PARAMETER_TITLE = "title";
    private static final String PARAMETER_DESCRIPTION = "description";
    private static final String PARAMETER_EXTENSION = "extension";
    private static final String PARAMETER_PAGE_INDEX = "page_index";

    //	 other constants
    private static final String EMPTY_STRING = "";

    //	message
    private static final String MESSAGE_CONFIRM_REMOVE_DIRECTORY_XSL = "directory.message.confirm_remove_directory_xsl";
    private static final String MESSAGE_MANDATORY_FIELD = "directory.message.mandatory.field";
    private static final String MESSAGE_CAN_NOT_REMOVE_DIRECTORY_XSL = "directory.message.can_not_remove_directory_xsl";
    private static final String FIELD_TITLE = "directory.create_directory_xsl.label_title";
    private static final String FIELD_DESCRIPTION = "directory.create_directory_xsl.label_description";
    private static final String FIELD_EXTENSION = "directory.create_directory_xsl.label_extension";
    private static final String FIELD_FILE = "directory.create_directory_xsl.label_file";
    private static final String MESSAGE_XML_NOT_VALID = "directory.message.xml_not_valid";

    //	properties
    private static final String PROPERTY_ITEM_PER_PAGE = "directory.itemsPerPage";
    private static final String PROPERTY_MANAGE_DIRECTORY_XSL_TITLE = "directory.manage_directory_xsl.page_title";
    private static final String PROPERTY_MODIFY_DIRECTORY_XSL_TITLE = "directory.modify_directory_xsl.title";
    private static final String PROPERTY_CREATE_DIRECTORY_XSL_TITLE = "directory.create_directory_xsl.title";

    //Jsp Definition
    private static final String JSP_MANAGE_DIRECTORY_XSL = "jsp/admin/plugins/directory/ManageDirectoryXsl.jsp";
    private static final String JSP_DO_REMOVE_DIRECTORY_XSL = "jsp/admin/plugins/directory/DoRemoveDirectoryXsl.jsp";

    //	session fields
    private int _nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( PROPERTY_ITEM_PER_PAGE, 15 );
    private String _strCurrentPageIndex;
    private int _nItemsPerPage;

    /**
     * Return management directory xsl ( list of export format )
     * @param request The Http request
     * @return Html management directory
     */
    public String getManageDirectoryXsl( HttpServletRequest request )
    {
        HashMap model = new HashMap(  );
        List<DirectoryXsl> listDirectoryXsl = DirectoryXslHome.getList( new DirectoryXslFilter(  ), getPlugin(  ) );
        _strCurrentPageIndex = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex );
        _nItemsPerPage = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPage,
                _nDefaultItemsPerPage );

        Collection<DirectoryAction> listActions = DirectoryActionHome.selectActionsByDirectoryXsl( getPlugin(  ),
                getLocale(  ) );

        for ( DirectoryXsl directoryXsl : listDirectoryXsl )
        {
            directoryXsl.setActions( (List<DirectoryAction>) RBACService.getAuthorizedActionsCollection( listActions,
                    directoryXsl, getUser(  ) ) );
        }

        model.put( MARK_PERMISSION_CREATE,
            RBACService.isAuthorized( DirectoryXsl.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID,
                DirectoryXslResourceIdService.PERMISSION_CREATE, getUser(  ) ) );

        LocalizedPaginator paginator = new LocalizedPaginator( listDirectoryXsl, _nItemsPerPage,
                getJspManageDirectoryXsl( request ), PARAMETER_PAGE_INDEX, _strCurrentPageIndex, getLocale(  ) );

        model.put( MARK_PAGINATOR, paginator );
        model.put( MARK_NB_ITEMS_PER_PAGE, EMPTY_STRING + _nItemsPerPage );
        model.put( MARK_DIRECTORY_XSL_LIST, paginator.getPageItems(  ) );
        setPageTitleProperty( PROPERTY_MANAGE_DIRECTORY_XSL_TITLE );

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_MANAGE_DIRECTORY_XSL, getLocale(  ), model );

        return getAdminPage( templateList.getHtml(  ) );
    }

    /**
    * Gets the directory xsl creation page
    * @param request The HTTP request
    * @return The directory xsl creation page
    */
    public String getCreateDirectoryXsl( HttpServletRequest request )
    {
        if ( !RBACService.isAuthorized( DirectoryXsl.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID,
                    DirectoryXslResourceIdService.PERMISSION_CREATE, getUser(  ) ) )
        {
            return getJspManageDirectoryXsl( request );
        }

        HashMap model = new HashMap(  );
        model.put( MARK_CATEGORY_LIST, CategoryHome.getList( getPlugin(  ) ) );
        setPageTitleProperty( PROPERTY_CREATE_DIRECTORY_XSL_TITLE );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CREATE_DIRECTORY_XSL, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
    * Perform the directory xsl creation
    * @param request The HTTP request
    * @return The URL to go after performing the action
    */
    public String doCreateDirectoryXsl( HttpServletRequest request )
    {
        if ( !RBACService.isAuthorized( DirectoryXsl.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID,
                    DirectoryXslResourceIdService.PERMISSION_CREATE, getUser(  ) ) )
        {
            return getJspManageDirectoryXsl( request );
        }

        DirectoryXsl directoryXsl = new DirectoryXsl(  );
        String strError = getDirectoryXslData( request, directoryXsl );

        if ( strError != null )
        {
            return strError;
        }

        if ( directoryXsl.getFile(  ) != null )
        {
            directoryXsl.getFile(  ).setIdFile( FileHome.create( directoryXsl.getFile(  ), getPlugin(  ) ) );
        }

        DirectoryXslHome.create( directoryXsl, getPlugin(  ) );

        return getJspManageDirectoryXsl( request );
    }

    /**
    * Gets the export format modification page
    * @param request The HTTP request
    * @throws AccessDeniedException the {@link AccessDeniedException}
    * @return The export format creation page
    */
    public String getModifyDirectoryXsl( HttpServletRequest request )
        throws AccessDeniedException
    {
        DirectoryXsl directoryXsl;
        String strIdDirectoryXsl = request.getParameter( PARAMETER_ID_DIRECTORY_XSL );
        HashMap model = new HashMap(  );
        int nIdDirectoryXsl = DirectoryUtils.convertStringToInt( strIdDirectoryXsl );
        directoryXsl = DirectoryXslHome.findByPrimaryKey( nIdDirectoryXsl, getPlugin(  ) );

        if ( ( directoryXsl == null ) ||
                !RBACService.isAuthorized( DirectoryXsl.RESOURCE_TYPE, strIdDirectoryXsl,
                    DirectoryXslResourceIdService.PERMISSION_MODIFY, getUser(  ) ) )
        {
            throw new AccessDeniedException(  );
        }

        model.put( MARK_DIRECTORY_XSL, directoryXsl );
        model.put( MARK_CATEGORY_LIST, CategoryHome.getList( getPlugin(  ) ) );
        setPageTitleProperty( PROPERTY_MODIFY_DIRECTORY_XSL_TITLE );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MODIFY_DIRECTORY_XSL, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
    * Perform the directory xsl modification
    * @param request The HTTP request
    * @throws AccessDeniedException the {@link AccessDeniedException}
    * @return The URL to go after performing the action
    */
    public String doModifyDirectoryXsl( HttpServletRequest request )
        throws AccessDeniedException
    {
        DirectoryXsl directoryXsl;
        String strIdDirectoryXsl = request.getParameter( PARAMETER_ID_DIRECTORY_XSL );
        int nIdDirectoryXsl = DirectoryUtils.convertStringToInt( strIdDirectoryXsl );
        directoryXsl = DirectoryXslHome.findByPrimaryKey( nIdDirectoryXsl, getPlugin(  ) );

        if ( ( directoryXsl == null ) ||
                !RBACService.isAuthorized( DirectoryXsl.RESOURCE_TYPE, strIdDirectoryXsl,
                    DirectoryXslResourceIdService.PERMISSION_MODIFY, getUser(  ) ) )
        {
            throw new AccessDeniedException(  );
        }

        String strError = getDirectoryXslData( request, directoryXsl );

        if ( strError != null )
        {
            return strError;
        }

        //if directoryXsl
        File fileStore = DirectoryXslHome.findByPrimaryKey( nIdDirectoryXsl, getPlugin(  ) ).getFile(  );

        if ( directoryXsl.getFile(  ) != null )
        {
            //the file has been modified
            File fileSource = directoryXsl.getFile(  );
            //init id file source and id physical file before update
            fileSource.setIdFile( fileStore.getIdFile(  ) );

            if ( fileStore.getPhysicalFile(  ) != null )
            {
                fileSource.getPhysicalFile(  ).setIdPhysicalFile( fileStore.getPhysicalFile(  ).getIdPhysicalFile(  ) );
            }

            FileHome.update( fileSource, getPlugin(  ) );
        }
        else
        {
            directoryXsl.setFile( fileStore );
        }

        DirectoryXslHome.update( directoryXsl, getPlugin(  ) );

        return getJspManageDirectoryXsl( request );
    }

    /**
     * Gets the confirmation page of delete directory xsl
     * @param request The HTTP request
     * @throws AccessDeniedException the {@link AccessDeniedException}
     * @return the confirmation page of delete directory xsl
     */
    public String getConfirmRemoveDirectoryXsl( HttpServletRequest request )
        throws AccessDeniedException
    {
        String strIdDirectoryXsl = request.getParameter( PARAMETER_ID_DIRECTORY_XSL );

        if ( !RBACService.isAuthorized( DirectoryXsl.RESOURCE_TYPE, strIdDirectoryXsl,
                    DirectoryXslResourceIdService.PERMISSION_DELETE, getUser(  ) ) )
        {
            throw new AccessDeniedException(  );
        }

        UrlItem url = new UrlItem( JSP_DO_REMOVE_DIRECTORY_XSL );
        url.addParameter( PARAMETER_ID_DIRECTORY_XSL, strIdDirectoryXsl );

        return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_DIRECTORY_XSL, url.getUrl(  ),
            AdminMessage.TYPE_CONFIRMATION );
    }

    /**
    * Perform the export format supression
    * @param request The HTTP request
    * @throws AccessDeniedException the {@link AccessDeniedException}
    * @return The URL to go after performing the action
    */
    public String doRemoveDirectoryXsl( HttpServletRequest request )
        throws AccessDeniedException
    {
        ArrayList<String> listErrors = new ArrayList<String>(  );
        String strIdDirectoryXsl = request.getParameter( PARAMETER_ID_DIRECTORY_XSL );
        int nIdDirectoryXsl = DirectoryUtils.convertStringToInt( strIdDirectoryXsl );
        DirectoryXsl directoryXsl = DirectoryXslHome.findByPrimaryKey( nIdDirectoryXsl, getPlugin(  ) );

        if ( ( directoryXsl == null ) ||
                !RBACService.isAuthorized( DirectoryXsl.RESOURCE_TYPE, strIdDirectoryXsl,
                    DirectoryXslResourceIdService.PERMISSION_DELETE, getUser(  ) ) )
        {
            throw new AccessDeniedException(  );
        }

        if ( !DirectoryXslRemovalListenerService.getService(  )
                                                    .checkForRemoval( strIdDirectoryXsl, listErrors, getLocale(  ) ) )
        {
            String strCause = AdminMessageService.getFormattedList( listErrors, getLocale(  ) );
            Object[] args = { strCause };

            return AdminMessageService.getMessageUrl( request, MESSAGE_CAN_NOT_REMOVE_DIRECTORY_XSL, args,
                AdminMessage.TYPE_STOP );
        }

        DirectoryXslHome.remove( nIdDirectoryXsl, getPlugin(  ) );

        if ( directoryXsl.getFile(  ) != null )
        {
            FileHome.remove( directoryXsl.getFile(  ).getIdFile(  ), getPlugin(  ) );
        }

        return getJspManageDirectoryXsl( request );
    }

    /**
     * Get the request data and if there is no error insert the data in the exportFormat object specified in parameter.
     * return null if there is no error or else return the error page url
     * @param  request the request
     * @param directoryXsl the exportFormat Object
     * @return null if there is no error or else return the error page url
     */
    private String getDirectoryXslData( HttpServletRequest request, DirectoryXsl directoryXsl )
    {
        String strError = DirectoryUtils.EMPTY_STRING;
        String strTitle = request.getParameter( PARAMETER_TITLE );
        String strDescription = request.getParameter( PARAMETER_DESCRIPTION );
        String strExtension = request.getParameter( PARAMETER_EXTENSION );
        String strIdCategory = request.getParameter( PARAMETER_ID_CATEGORY_XSL );
        int nIdCategory = DirectoryUtils.convertStringToInt( strIdCategory );
        Category category;
        File fileSource = DirectoryUtils.getFileData( PARAMETER_ID_FILE, request );

        if ( ( strTitle == null ) || strTitle.trim(  ).equals( EMPTY_STRING ) )
        {
            strError = FIELD_TITLE;
        }

        else if ( ( strDescription == null ) || strDescription.trim(  ).equals( EMPTY_STRING ) )
        {
            strError = FIELD_DESCRIPTION;
        }

        else if ( ( directoryXsl.getFile(  ) == null ) && ( fileSource == null ) )
        {
            strError = FIELD_FILE;
        }

        //Mandatory fields
        if ( !strError.equals( EMPTY_STRING ) )
        {
            Object[] tabRequiredFields = { I18nService.getLocalizedString( strError, getLocale(  ) ) };

            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields,
                AdminMessage.TYPE_STOP );
        }

        //Check the XML validity of the XSL stylesheet
        if ( fileSource != null )
        {
            strError = isValid( fileSource.getPhysicalFile(  ).getValue(  ) );

            if ( strError != null )
            {
                Object[] args = { strError };

                return AdminMessageService.getMessageUrl( request, MESSAGE_XML_NOT_VALID, args, AdminMessage.TYPE_STOP );
            }
        }

        directoryXsl.setTitle( strTitle );
        directoryXsl.setDescription( strDescription );
        directoryXsl.setExtension( strExtension );

        if ( nIdCategory != DirectoryUtils.CONSTANT_ID_NULL )
        {
            category = new Category(  );
            category.setIdCategory( nIdCategory );
            directoryXsl.setCategory( category );
        }

        directoryXsl.setFile( fileSource );

        return null;
    }

    /**
     *  Use parsing for validate the modify xsl file
     *  @param baXslSource the xsl source
     *  @return the message exception when the validation is false
     */
    private String isValid( byte[] baXslSource )
    {
        String strError = null;

        try
        {
            SAXParserFactory factory = SAXParserFactory.newInstance(  );
            SAXParser analyzer = factory.newSAXParser(  );
            InputSource is = new InputSource( new ByteArrayInputStream( baXslSource ) );
            analyzer.getXMLReader(  ).parse( is );
        }
        catch ( Exception e )
        {
            strError = e.getMessage(  );
        }

        return strError;
    }

    /**
     * return the url of manage export format
     * @param request the request
     * @return the url of manage export format
     */
    private String getJspManageDirectoryXsl( HttpServletRequest request )
    {
        return AppPathService.getBaseUrl( request ) + JSP_MANAGE_DIRECTORY_XSL;
    }
}

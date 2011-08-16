package fr.paris.lutece.plugins.directory.web.action;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.util.url.UrlItem;

/**
 * Redirects to jsp/admin/plugins/directory/MassPrint.jsp
 *
 */
public class MassPrintDirectoryAction implements IDirectoryAction
{
	private static final String ACTION_NAME = "Mass Print Directory";
	private static final String TEMPLATE_BUTTON = "actions/massprint.html";

	private static final String PARAMETER_BUTTON_MASS_PRINT = "massprint";
	
	private static final String JSP_DIRECTORY_MASS_PRINT = "jsp/admin/plugins/directory/MassPrint.jsp";

	public void fillModel(HttpServletRequest request, AdminUser adminUser,
			Map<String, Object> model) {
		// no additionnal data
	}

	/**
	 * {@inheritDoc}
	 */
	public String getButtonTemplate(  )
	{
		return TEMPLATE_BUTTON;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getName(  )
	{
		return ACTION_NAME;
	}

	/**
	 * @see #PARAMETER_BUTTON_MASS_PRINT
	 */
	public boolean isInvoked(HttpServletRequest request) 
	{
		return request.getParameter( PARAMETER_BUTTON_MASS_PRINT ) != null;
	}

	/**
	 * Redirects to {@link #JSP_DIRECTORY_MASS_PRINT}
	 */
	public DirectoryActionResult process(HttpServletRequest request,
			HttpServletResponse response, AdminUser adminUser,
			DirectoryAdminSearchFields sessionFields)
			throws AccessDeniedException 
	{
		DirectoryActionResult result = new DirectoryActionResult(  );
		
		UrlItem urlItem = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_DIRECTORY_MASS_PRINT );
		String strIdDirectory = request.getParameter( DirectoryUtils.PARAMETER_ID_DIRECTORY );
		urlItem.addParameter( DirectoryUtils.PARAMETER_ID_DIRECTORY, strIdDirectory );
		
		result.setRedirect( urlItem.getUrl(  ) );
		return result;
	}

}

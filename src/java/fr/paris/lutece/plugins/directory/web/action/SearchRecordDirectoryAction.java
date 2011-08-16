package fr.paris.lutece.plugins.directory.web.action;

import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.directory.utils.DirectoryErrorException;
import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;

/**
 * Directory record search
 *
 */
public class SearchRecordDirectoryAction implements IDirectoryAction 
{

	private static final String PARAMETER_BUTTON_SEARCH = "search";
	private static final String ACTION_NAME = "Search Directory";
	
    private static final String PARAMETER_DATE_BEGIN_CREATION = "date_begin_creation";
    private static final String PARAMETER_DATE_CREATION = "date_creation";
    private static final String PARAMETER_DATE_END_CREATION = "date_end_creation";
    private static final String PARAMETER_WORKFLOW_STATE_SELECTED = "search_state_workflow";

	public void fillModel(HttpServletRequest request, AdminUser adminUser,
			Map<String, Object> model )
	{
		// nothing to fill, the model is already search friendly
	}

	/**
	 * Returns an empty string - nothing to print
	 */
	public String getButtonTemplate(  )
	{
		return StringUtils.EMPTY;
	}

	/**
	 * 
	 */
	public String getName(  )
	{
		return ACTION_NAME;
	}

	/**
	 * @see #PARAMETER_BUTTON_SEARCH
	 */
	public boolean isInvoked( HttpServletRequest request )
	{
		return request.getParameter( PARAMETER_BUTTON_SEARCH ) != null;
	}

	/**
	 * {@inheritDoc}
	 */
	public DirectoryActionResult process(HttpServletRequest request,
			HttpServletResponse response, AdminUser adminUser,
			DirectoryAdminSearchFields searchFields)
			throws AccessDeniedException 
	{
		DirectoryActionResult result = new DirectoryActionResult(  );
		String strIdDirectory = request.getParameter( DirectoryUtils.PARAMETER_ID_DIRECTORY );
        int nIdDirectory = DirectoryUtils.convertStringToInt( strIdDirectory );
        
        searchFields.setIdWorkflowSate( DirectoryUtils.convertStringToInt( request.getParameter( PARAMETER_WORKFLOW_STATE_SELECTED ) ) );

        try
        {
        	Locale locale = adminUser.getLocale(  );
            //get search filter
        	searchFields.setMapQuery( DirectoryUtils.getSearchRecordData( request, nIdDirectory, DirectoryUtils.getPlugin(  ), locale ) );
        	searchFields.setDateCreationBeginRecord( DirectoryUtils.getSearchRecordDateCreationFromRequest( request,
                    PARAMETER_DATE_BEGIN_CREATION, locale ) );
            searchFields.setDateCreationEndRecord( DirectoryUtils.getSearchRecordDateCreationFromRequest( request,
                    PARAMETER_DATE_END_CREATION, locale ) );
            searchFields.setDateCreationRecord( DirectoryUtils.getSearchRecordDateCreationFromRequest( request,
                    PARAMETER_DATE_CREATION, locale ) );
            
            // build redirect url
            result.setRedirect( DirectoryUtils.getJspManageDirectoryRecord( request, nIdDirectory ) ); // + "&" + PARAMETER_SEARCH + "=" + PARAMETER_SEARCH );
            
        }
        catch ( DirectoryErrorException error )
        {
            String strErrorMessage = DirectoryUtils.EMPTY_STRING;

            if ( error.isMandatoryError(  ) )
            {
                Object[] tabRequiredFields = { error.getTitleField(  ) };
                strErrorMessage = AdminMessageService.getMessageUrl( request, DirectoryUtils.MESSAGE_DIRECTORY_ERROR_MANDATORY_FIELD,
                        tabRequiredFields, AdminMessage.TYPE_STOP );
            }
            else
            {
                Object[] tabRequiredFields = { error.getTitleField(  ), error.getErrorMessage(  ) };
                strErrorMessage = AdminMessageService.getMessageUrl( request, DirectoryUtils.MESSAGE_DIRECTORY_ERROR,
                        tabRequiredFields, AdminMessage.TYPE_STOP );
            }

            result.setRedirect( strErrorMessage );
        }

        return result;
	}

}

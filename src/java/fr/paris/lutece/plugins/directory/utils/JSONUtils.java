package fr.paris.lutece.plugins.directory.utils;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * JSONUtils
 *
 */
public final class JSONUtils
{
	private static final String KEY_USER_ATTRIBUTES = "user-attributes";
	
	/**
	 * Private constructor
	 */
	private JSONUtils(  )
	{
	}
	
	/**
	 * Get the user infos.
	 * <br />
	 * The json must be written with the following format :
	 * <br />
	 * <code>
	 * <br />{ "user-attributes": [
	 * <br />{ "user-attribute-key": "user.name.family", "user-attribute-value": "FAMILYNAME" },
	 * <br />{ "user-attribute-key": "user.home-info.online.email", "user-attribute-value": "EMAIL@EMAIL.EMAIL"}
	 * <br />] }
	 * </code>
	 * @param strJSON the json
	 * @return the user attributes
	 */
	public static Map<String, String> getUserInfos( String strJSON )
	{
		Map<String, String> userInfos = new HashMap<String, String>(  );
		
		if ( StringUtils.isNotBlank( strJSON ) )
		{
			// Get object "user-attributes"
			JSONObject json = (JSONObject) JSONSerializer.toJSON( strJSON );
			if ( json != null )
			{
				// Get sub-objects of "user-attributes"
				JSONArray arrayUserAttributes = json.getJSONArray( KEY_USER_ATTRIBUTES );
				if ( arrayUserAttributes != null )
				{
					// Browse each user attribute
					for ( int i = 0; i < arrayUserAttributes.size(  ); i++ )
					{
						put( userInfos, arrayUserAttributes.getJSONObject( i ) );
					}
				}
			}
		}
		
		return userInfos;
	}

	/**
	 * Insert user attribute to the map
	 * @param userInfos the map
	 * @param userAttribute the user attribute
	 */
	private static void put( Map<String, String> userInfos, JSONObject userAttribute )
	{
		if ( userAttribute != null )
		{
			JSONArray listCodes = userAttribute.names(  );
			for ( int i = 0; i < listCodes.size(  ); i++ )
	        {
				String strCode = listCodes.getString( i );
				String strValue = userAttribute.getString( strCode );
				userInfos.put( strCode, strValue );
	        }
		}
	}
}

<%@page import="fr.paris.lutece.plugins.directory.web.DoDownloadFile"%>
<% 
	 String strResult =  DoDownloadFile.doDownloadFile(request,response);
 	 if (!response.isCommitted())
	{
		  response.sendRedirect(strResult);
	}
%>

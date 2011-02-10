<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:useBean id="directoryDirectory" scope="session" class="fr.paris.lutece.plugins.directory.web.DirectoryJspBean" />
<% 
	directoryDirectory.init( request, fr.paris.lutece.plugins.directory.web.ManageDirectoryJspBean.RIGHT_MANAGE_DIRECTORY);
  	response.sendRedirect( directoryDirectory.doMoveUpEntry( request ) );
%>

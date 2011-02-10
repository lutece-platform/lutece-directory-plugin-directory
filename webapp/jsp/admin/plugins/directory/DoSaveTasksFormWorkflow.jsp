<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:useBean id="directory" scope="session" class="fr.paris.lutece.plugins.directory.web.DirectoryJspBean" />
<% 
	directory.init( request,  fr.paris.lutece.plugins.directory.web.ManageDirectoryJspBean.RIGHT_MANAGE_DIRECTORY); 
	response.sendRedirect( directory.doSaveTasksForm( request ) );
%>
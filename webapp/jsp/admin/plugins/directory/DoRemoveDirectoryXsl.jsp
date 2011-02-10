<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:useBean id="directoryXsl" scope="session" class="fr.paris.lutece.plugins.directory.web.DirectoryXslJspBean" />
<% 
directoryXsl.init( request,fr.paris.lutece.plugins.directory.web.ManageDirectoryJspBean.RIGHT_MANAGE_DIRECTORY);
 response.sendRedirect( directoryXsl.doRemoveDirectoryXsl( request ) );
%>

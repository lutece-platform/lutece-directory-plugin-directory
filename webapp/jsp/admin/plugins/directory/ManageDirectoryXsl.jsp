<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />
<jsp:useBean id="directoryXsl" scope="session" class="fr.paris.lutece.plugins.directory.web.DirectoryXslJspBean" />
<% 
directoryXsl.init( request,fr.paris.lutece.plugins.directory.web.ManageDirectoryJspBean.RIGHT_MANAGE_DIRECTORY);
 %>
<%= directoryXsl.getManageDirectoryXsl( request ) %>
<%@ include file="../../AdminFooter.jsp" %>
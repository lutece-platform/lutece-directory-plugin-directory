<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />
<jsp:useBean id="directory" scope="session" class="fr.paris.lutece.plugins.directory.web.DirectoryJspBean" />
<%
directory.init( request, fr.paris.lutece.plugins.directory.web.ManageDirectoryJspBean.RIGHT_MANAGE_DIRECTORY);
%>
<%= directory.getTasksForm(request) %>
<%@ include file="../../AdminFooter.jsp" %>
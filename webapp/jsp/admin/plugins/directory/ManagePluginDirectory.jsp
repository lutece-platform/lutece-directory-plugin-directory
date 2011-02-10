<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<jsp:useBean id="directoryManageDirectory" scope="session" class="fr.paris.lutece.plugins.directory.web.ManageDirectoryJspBean" />


<%
directoryManageDirectory.init( request, fr.paris.lutece.plugins.directory.web.ManageDirectoryJspBean.RIGHT_MANAGE_DIRECTORY);
 %>
<%= directoryManageDirectory.getManageDirectory( request ) %>

<%@ include file="../../AdminFooter.jsp" %>
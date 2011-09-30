<%@page import="fr.paris.lutece.portal.web.pluginaction.IPluginActionResult"%>
<jsp:useBean id="directoryDirectory" scope="session" class="fr.paris.lutece.plugins.directory.web.DirectoryJspBean" /><%
directoryDirectory.init( request, fr.paris.lutece.plugins.directory.web.ManageDirectoryJspBean.RIGHT_MANAGE_DIRECTORY);
IPluginActionResult result = directoryDirectory.getManageDirectoryRecord( request, response );

if ( result.getRedirect() != null ) {
	response.sendRedirect(result.getRedirect());
} else if ( result.getHtmlContent() != null ) {
	
%>
<jsp:include page="../../AdminHeader.jsp" />
<%= result.getHtmlContent(  ) %>
<%@ include file="../../AdminFooter.jsp" %>
<% } %><%@ page errorPage="../../ErrorPage.jsp" %>
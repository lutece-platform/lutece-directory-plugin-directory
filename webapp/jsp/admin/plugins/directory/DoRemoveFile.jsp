<%@ page errorPage="../../ErrorPage.jsp" %>

<%@page import="fr.paris.lutece.plugins.directory.web.DirectoryJspBean"%>
<jsp:useBean id="directoryDirectory" scope="request" class="fr.paris.lutece.plugins.directory.web.DirectoryJspBean" />

<%
	directoryDirectory.init( request, fr.paris.lutece.plugins.directory.web.ManageDirectoryJspBean.RIGHT_MANAGE_DIRECTORY );
	directoryDirectory.doRemoveAsynchronousUploadedFile( request );
%>
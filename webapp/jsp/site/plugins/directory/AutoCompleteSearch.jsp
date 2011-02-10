<%@ page errorPage="../../ErrorPagePortal.jsp" %>

<jsp:useBean id="directorySearch" scope="session" class="fr.paris.lutece.plugins.directory.service.directorysearch.DirectorySearchService" />
<%= directorySearch.getAutocompleteResult( request )%>
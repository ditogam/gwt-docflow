<%@page import="com.docflow.server.db.DocFlowDocumentGenerator"%>
<%
	new DocFlowDocumentGenerator(request, response,application);
%>
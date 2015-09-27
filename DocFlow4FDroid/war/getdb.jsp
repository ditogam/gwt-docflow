<%@page import="com.docflow.server.DocFlowServiceImpl"%>
<body>
	<%
		DocFlowServiceImpl.flushMakeDB(out, request, response, session);
	%>
</body>

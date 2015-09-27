<%@page import="com.socarmap.server.WSConnection"%>
<%@ page trimDirectiveWhitespaces="true" %>
<body>
	<%
		WSConnection.flushMakeDB(out, request, response, session);
	%>
</body>

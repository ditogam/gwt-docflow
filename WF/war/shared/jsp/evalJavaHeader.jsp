<%-- START /shared/jsp/evalJavaHeader.jsp --%>
<%@page import="com.isomorphic.rpc.ClientMustResubmitException"%>
<%@page import="com.isomorphic.rpc.RPCManager"%>
<%@page import="com.isomorphic.servlet.RequestContext"%>
<%@page import="com.isomorphic.log.Logger"%>
<%@ taglib uri="/WEB-INF/iscTaglib.xml" prefix="isomorphic" %>
<%
    Logger log = new Logger("com.isomorphic.DevConsoleEval"); 
    RequestContext context = RequestContext.instance(request, response);
    RPCManager rpc;
    try {
        rpc = new RPCManager(request, response, out);
    } catch (ClientMustResubmitException e) { 
        return; 
    }
%>
<%-- END /shared/jsp/evalJavaHeader.jsp --%>

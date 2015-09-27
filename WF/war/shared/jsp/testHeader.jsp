<%@page import="com.isomorphic.servlet.RequestContext"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/iscTaglib.xml" prefix="isomorphic" %>
<%@ taglib uri="/WEB-INF/iscTaglib.xml" prefix="isc" %>
<HTML><HEAD><TITLE><%=isc_getShortURI(request)%></TITLE>
<%
    String skin = request.getParameter("skin");
    if (skin == null) skin = "standard";

    String remote = request.getParameter("remote");
    if (remote == null) remote = "false";
    if (remote != "false") {
%>
<script>    
        window.isc_remoteDebug = true;
</script>        
<%        
    }
%>
<isomorphic:loadISC includeModules="FileLoader,Analytics,Drawing,History,Tools" skin="<%=skin%>"/>
</HEAD><BODY>
<%
    RequestContext context = RequestContext.instance(this, request, response, out);        
%>
<SCRIPT>

//--testHeaderEnd-- DO NOT CHANGE OR REMOVE THIS LINE - see TestRunnerFilter.java for comments
// 
// If you want to add code that's visible only to the HTML-framed version of the test file, add it above
// this comment block.  If you want the code to be visible in jsOnly mode _and_ HTML-framed version,
// add it below this comment block.
//------------------------------------------------------------------------------
// START test case
//------------------------------------------------------------------------------

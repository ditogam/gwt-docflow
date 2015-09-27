<!DOCTYPE HTML>
<%@page import="com.isomorphic.log.Logger"%>
<%@page import="com.isomorphic.servlet.RequestContext"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/iscTaglib.xml" prefix="isomorphic" %>
<%@ taglib uri="/WEB-INF/iscTaglib.xml" prefix="isc" %>
<HTML><HEAD><TITLE><%=isc_getShortURI(request)%></TITLE>
<%
    String skin = request.getParameter("skin");
    if (skin == null) skin = "Enterprise";
%>
<isomorphic:loadISC skin="<%=skin%>"/>
</HEAD><BODY>
<%
    RequestContext context = RequestContext.instance(this, request, response, out);        
    Logger log = context.staticLog;
%>


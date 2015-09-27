<%@page import="com.socargass.tabletexporter.GetMap"%>
<%@ page language="java" contentType="image/png"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%
	GetMap.getmap(out, request, response, session);
%>
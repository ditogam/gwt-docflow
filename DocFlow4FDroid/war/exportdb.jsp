<%@page import="com.docflow.server.export.Exporter"%>
<%@ page language="java" contentType="application/octet-stream;"%>
<%
	Exporter.writeResult(request, response);
%>
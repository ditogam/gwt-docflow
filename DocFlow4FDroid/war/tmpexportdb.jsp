<%@page import="com.docflow.shared.DbExpoResult"%>
<%@page import="com.docflow.server.export.Exporter"%>
<%@ page language="java" contentType="application/octet-stream;"%>
<%
	DbExpoResult result = Exporter.createExporterSession(24);
	String _result = "";
	if (result.getException() != null) {
		_result = result.getException().getDetailed();
	} else
		_result = result.getSession_id();
	out.write(_result);
%>
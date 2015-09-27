<%@page import="com.docflow.server.db.DocTypeTree"%>
<%@ page language="java" contentType="text/xml; charset=UTF8"
	pageEncoding="UTF8"%>
<%
	String reqLanguage = request.getParameter("lang");
	int languageid = 1;
	try {
		languageid = Integer.parseInt(reqLanguage.trim());
	} catch (Exception e) {
		languageid = 1;
	}
	String requser_id = request.getParameter("user_id");
	int user_id = -1;
	try {
		user_id = Integer.parseInt(requser_id.trim());
	} catch (Exception e) {
		user_id = -1;
	}
	String reqsystem_id = request.getParameter("system_id");
	int system_id = -1;
	try {
		system_id = Integer.parseInt(reqsystem_id.trim());
	} catch (Exception e) {
		user_id = -1;
	}

	out.print(DocTypeTree.getDocTypeTreeXML(languageid, user_id,system_id));
%>
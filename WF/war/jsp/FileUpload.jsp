
<%@page import="com.workflow.server.utils.UploadUtils"%>
<%@ page language="java" contentType="application/json; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
	UploadUtils.upload(request, response);
%>


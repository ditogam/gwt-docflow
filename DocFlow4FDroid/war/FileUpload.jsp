<%@page import="com.docflow.server.db.MyFileServlet"%>
<%@ page language="java" contentType="application/json; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	MyFileServlet.process(request, response);
%>
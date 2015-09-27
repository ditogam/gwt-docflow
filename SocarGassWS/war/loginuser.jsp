<%@page import="com.socargass.tabletexporter.LoginUser"%>
<%@ page language="java" contentType="text/html; charset=US-ASCII"
    pageEncoding="US-ASCII"%>
<%LoginUser.login(out, request, response, session);%>
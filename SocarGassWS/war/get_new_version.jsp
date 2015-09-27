<%@page import="com.socarmap.server.DBOperations"%>
<%@page import="java.io.OutputStream"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.Statement"%>
<%@page import="java.sql.Connection"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
	String sql = "select program_content from android.android_program where program_id="
			+ request.getParameter("app_id");
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;
	try {
		con = DBOperations.getConnection("docflow");
		stmt = con.createStatement();
		rs = stmt.executeQuery(sql);
		if (rs.next()) {
			OutputStream os = response.getOutputStream();
			os.write(rs.getBytes("program_content"));
			os.flush();
			os.close();
		}
	} catch (Throwable ex) {

	} finally {
		try {
			rs.close();
		} catch (Throwable ex) {

		}
		try {
			stmt.close();
		} catch (Throwable ex) {

		}
		try {
			DBOperations.closeAll(con);
		} catch (Throwable ex) {

		}

	}
%>
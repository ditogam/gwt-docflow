<%@page import="com.common.db.DBConnection"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.Statement"%>
<%@page import="java.sql.Connection"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
	String sql = "select program_md5 from android.android_program where program_id="
			+ request.getParameter("app_id");
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;
	try {
		con = DBConnection.getConnection("DocFlow");
		stmt = con.createStatement();
		rs = stmt.executeQuery(sql);
		if (rs.next())
			out.print(rs.getString("program_md5"));
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
			DBConnection.freeConnection(con);
		} catch (Throwable ex) {

		}

	}
%>
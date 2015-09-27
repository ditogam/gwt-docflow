<%@page import="java.io.PrintWriter"%>
<%@page import="java.io.StringWriter"%>
<%@page import="java.io.OutputStream"%>
<%@page import="com.common.db.DBConnection"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.Statement"%>
<%@page import="java.sql.Connection"%>
<html>
<head>
<title>File Uploading Form</title>
</head>
<body>
	<h3>File Upload:</h3>
	Select a file to upload:
	<br />
	<select name="program_id" form="upload_program_form">
		<%
			String sql = "select program_id,program_name from android.android_program order by program_id desc";
			Connection con = null;
			Statement stmt = null;
			ResultSet rs = null;
			try {
				con = DBConnection.getConnection("DocFlow");
				stmt = con.createStatement();
				rs = stmt.executeQuery(sql);
				while (rs.next()) {
					String program_id = rs.getString("program_id");
					String program_name = rs.getString("program_name");
					out.println("<option value=\"" + program_id + "\">"
							+ program_name + "</option>");
				}
			} catch (Throwable ex) {
				StringWriter sw = new StringWriter();
				ex.printStackTrace(new PrintWriter(sw));
				out.write(sw.toString());
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
	</select>

	<form action=upload_program.jsp method="post"
		enctype="multipart/form-data" id="upload_program_form">
		<input type="file" name="file" /> <br /> <input type="submit"
			value="Upload File" />
	</form>
</body>
</html>
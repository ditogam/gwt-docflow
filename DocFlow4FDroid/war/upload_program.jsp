<%@page import="java.sql.ResultSet"%>
<%@page import="com.common.db.DBConnection"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.sql.Connection"%>
<%@page
	import="org.apache.commons.fileupload.MultipartStream.ItemInputStream"%>
<%@page import="java.io.ByteArrayOutputStream"%>
<%@page import="org.apache.commons.fileupload.util.Streams"%>
<%@page import="org.apache.commons.fileupload.FileItemStream"%>
<%@page import="java.io.PrintWriter"%>
<%@page import="java.io.StringWriter"%>
<%@page import="org.apache.commons.fileupload.FileItemIterator"%>
<%@page import="org.apache.commons.fileupload.servlet.ServletFileUpload"%>

<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
	try {

		ServletFileUpload upload = new ServletFileUpload();
		FileItemIterator iter = upload.getItemIterator(request);
		String program_id = null;
		byte[] arr = null;
		while (iter.hasNext()) {
			FileItemStream item = iter.next();
			String name = item.getFieldName();
			if (name.equals("program_id")) {
				program_id = Streams.asString(item.openStream());
				out.print("ProgramId=" + program_id + "<br>");
			}
			if (!item.isFormField()) {

				ItemInputStream is = (ItemInputStream) item
						.openStream();

				ByteArrayOutputStream buffer = new ByteArrayOutputStream();

				int nRead;
				byte[] data = new byte[16384];

				while ((nRead = is.read(data, 0, data.length)) != -1) {
					buffer.write(data, 0, nRead);
				}

				buffer.flush();
				arr = buffer.toByteArray();
				out.print("byte length==" + arr.length + "<br>");
			}
		}
		if (program_id != null && arr != null) {
			Connection con = null;
			PreparedStatement stmt = null;
			ResultSet rs = null;
			String sql = "update android.android_program set program_content=? where program_id="
					+ program_id;
			try {
				con = DBConnection.getConnection("DocFlow");
				con.setAutoCommit(false);
				stmt = con.prepareStatement(sql);
				stmt.setBytes(1, arr);
				stmt.executeUpdate();
				stmt.close();

				sql = "select program_md5 from android.android_program where program_id="
						+ program_id;
				stmt = con.prepareStatement(sql);
				rs = stmt.executeQuery();
				if (rs.next()) {
					out.print("Succeeded new hash="
							+ rs.getString("program_md5") + "<br>");
				}
				con.commit();

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
		} else {
			out.print("no file or program specified ");
		}
	} catch (Throwable ex) {
		StringWriter sw = new StringWriter();
		ex.printStackTrace(new PrintWriter(sw));
		out.write(sw.toString());
	}
%>

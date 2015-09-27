<%@page import="com.socarmap.server.WSConnection"%>
<%@page import="java.io.ByteArrayOutputStream"%>
<%@page import="org.apache.commons.fileupload.util.Streams"%>
<%@page import="java.io.InputStream"%>
<%@page import="org.apache.commons.fileupload.FileItemStream"%>
<%@page import="org.apache.commons.fileupload.FileItemIterator"%>
<%@page import="org.apache.commons.fileupload.servlet.ServletFileUpload"%>
<%
	ServletFileUpload upload = new ServletFileUpload();
	String uID = request.getParameter("uID");
	System.out.println("uID=========" + uID);
	int count = 0;
	System.out.println("coun111t=========" + count);
	//Parse the request
	try {
		FileItemIterator iter = upload.getItemIterator(request);

		while (iter.hasNext()) {

			FileItemStream item = iter.next();
			String name = item.getFieldName();
			InputStream stream = item.openStream();

			count++;
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();

			int nRead;
			byte[] data = new byte[16384];

			while ((nRead = stream.read(data, 0, data.length)) != -1) {
				buffer.write(data, 0, nRead);
			}

			buffer.flush();
			byte[] bytes = buffer.toByteArray();
			WSConnection.transfer(uID, bytes);
			System.out.println("Form field " + name + " with value "
					+ Streams.asString(stream)
					+ " detected. with length=" + bytes.length);

		}
		System.out.println("count=========" + count);
	} catch (Exception ex) {
		ex.printStackTrace();
	}
%>
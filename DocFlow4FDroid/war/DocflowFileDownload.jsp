<%@page import="com.docflow.server.db.MDBConnection"%>
<%@page import="com.docflow.server.db.SimpleFTPClient"%>
<%@page import="com.docflow.server.ImageData"%>
<%@page import="com.docflow.server.ImageStore"%>
<%@ page import="java.io.*"%>
<%@ page import="java.net.*"%>
<%
	String _id = request.getParameter("id");
	int id = Integer.parseInt(_id);
	ImageData newImage = ImageStore.getImageData(id, null);
	ByteArrayOutputStream bout = new ByteArrayOutputStream();
	SimpleFTPClient client = MDBConnection.getImageFtp(null);
	client.downloadFile("F" + newImage.getId(), bout);
	byte donneeFichier[] = bout.toByteArray();
	bout.close();
	response.setHeader("expires", "0");
	response.setContentType(newImage.getContenttype());

	out.clear();
	OutputStream os = response.getOutputStream();
	os.write(donneeFichier);
	out.flush();
	return;
%>
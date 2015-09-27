<%@page import="com.docflow.server.db.MDBConnection"%>
<%@page import="com.docflow.server.db.SimpleFTPClient"%>
<%@page import="com.docflow.server.ImageData"%>
<%@page import="com.docflow.server.ImageStore"%>
<%@ page import="java.io.*"%>
<%@ page import="java.net.*"%>
<%
	String _id = request.getParameter("id");

	try {
		int id = Integer.parseInt(_id);
		ImageData newImage = ImageStore.getImageData(id, null);
		File file = new File(newImage.getImagename());
		SimpleFTPClient client = MDBConnection.getImageFtp(null);
		client.downloadFile("F" + newImage.getId(), file);
		FileInputStream is = new FileInputStream(file);
		ServletOutputStream out_stream = response.getOutputStream();
		BufferedInputStream bis = new BufferedInputStream(is);

		byte[] buffer = new byte[1024];
		int readCount;
		response.setContentType(newImage.getContenttype());
		while ((readCount = bis.read(buffer)) > 0) {
			out_stream.write(buffer, 0, readCount);
		}
		is.close();
		out.flush();

		file.delete();

	} catch (Exception e) {
		out.println("errr=" + e.getMessage());
	}
	return;
%>
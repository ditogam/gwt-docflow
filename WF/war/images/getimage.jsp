<%@page import="java.io.ByteArrayOutputStream"%>
<%@page import="org.apache.commons.fileupload.util.Streams"%>
<%@page import="com.workflow.server.utils.ImageCache"%>
<%@page import="java.util.List"%>
<%@page import="com.workflow.server.utils.DMIUtils"%>
<%@page import="org.apache.commons.codec.binary.Base64"%>
<%@page import="com.sun.mail.util.BASE64DecoderStream"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.io.ByteArrayInputStream"%>
<%@page import="javax.imageio.ImageIO"%>
<%@page import="java.awt.image.BufferedImage"%>
<%@page contentType="image/png" pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true"%>
<%
	String id = request.getParameter("id");

	ImageCache cache = ImageCache.getInstance();
	byte[] val = cache.get(id);

	if (val == null) {
		Map<?, ?> map = DMIUtils.findRecordById("ImageDataDS", null,
				request.getParameter("id"), "id");
		ByteArrayInputStream str = (ByteArrayInputStream) map
				.get("bdata");
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		Streams.copy(str, bos, true);
		str.close();
		val = bos.toByteArray();
		cache.put(id, val);
	} else {
		val = val;
	}
	BufferedImage image = ImageIO.read(new ByteArrayInputStream(val));
	ImageIO.write(image, "png", response.getOutputStream());
%>
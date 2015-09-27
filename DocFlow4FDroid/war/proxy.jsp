<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"
	import="java.io.BufferedReader,
java.io.InputStreamReader,
java.io.IOException,
java.io.InputStream,
java.net.MalformedURLException,
java.net.URL,
java.net.URLConnection"%>
<%
	String contentURL = null;
	String CONTENT_URL_NAME = "contentURL";
	// get the url through the request:
	if (contentURL == null) {
		contentURL = (String) request.getAttribute(CONTENT_URL_NAME);
		if (contentURL == null)
			contentURL = (String) request
					.getParameter(CONTENT_URL_NAME);
	}
	if (contentURL == null)

		throw new ServletException(
				"A content URL must be provided, as a "
						+ CONTENT_URL_NAME
						+ " request attribute or request parameter.");
	URL url = null;
	System.out.println(contentURL);
	try {
		// get a connection to the content:
		url = new URL(contentURL);
		URLConnection urlConn = url.openConnection();
		// show the client the content type:
		String contentType = urlConn.getContentType();
		response.setContentType(contentType);
		// get the input stream
		InputStream in = urlConn.getInputStream();
		BufferedReader br = new BufferedReader(
				new InputStreamReader(in));
		char[] buffer = new char[1024];
		String contentString = "";
		String tmp = br.readLine();
		do {
			contentString += tmp + "\n";
			tmp = br.readLine();
		} while (tmp != null);
		System.out.println(contentString);
		out.write(contentString.toCharArray());
		out.flush();
		out.close();
	} catch (MalformedURLException me) {
		// on new URL:
		throw new ServletException("URL: '" + contentURL
				+ "' is malformed.");
	} catch (IOException ioe) {
		// on opne connection:
		throw new ServletException("Exception while opening '"
				+ contentURL + "': " + ioe.getMessage());
	} catch (Exception e) {
		// on reading input:
		throw new ServletException("Exception during proxy request: "
				+ e.getMessage());
	}
%>
package com.googlecode.xremoting.core.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import com.googlecode.xremoting.core.spi.Request;

/**
 * {@link Request} implementation for HTTP using {@link HttpURLConnection}.
 * 
 * @author Roman Puchkovskiy
 * @see HttpRequester
 */
public class HttpRequest implements Request {

	private HttpURLConnection connection;

	public HttpRequest(HttpURLConnection connection) {
		this.connection = connection;
	}

	protected void checkStatusCode(HttpURLConnection connection)
			throws IOException {
		if (connection.getResponseCode() != 200) {
			throw new IOException("Wrong status code: 200 expected but got "
					+ connection.getResponseCode() + " ("
					+ connection.getResponseMessage() + ")");
		}
	}

	@Override
	public void commitRequest() throws IOException {
		connection.getOutputStream().flush();
		checkStatusCode(connection);
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return connection.getInputStream();
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return connection.getOutputStream();
	}

	@Override
	public String getResponceType() {

		return connection.getContentType();
	}

	@Override
	public void releaseRequest() {
		connection.disconnect();
	}

}

package com.googlecode.xremoting.core.http;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * {@link HttpConnectionFactory} implementation which uses URL#openConnection().
 * 
 * @author Roman Puchkovskiy
 */
public class DefaultHttpConnectionFactory implements HttpConnectionFactory {

	@Override
	public HttpURLConnection openConnection(String url)
			throws MalformedURLException, IOException {
		int TIMEOUT_VALUE = 3000;
		HttpURLConnection connection = (HttpURLConnection) new URL(url)
				.openConnection();
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setUseCaches(false);
		connection.setChunkedStreamingMode(2048);
		connection.setConnectTimeout(TIMEOUT_VALUE);
		connection.setReadTimeout(TIMEOUT_VALUE);
		return connection;
	}

}

package com.socarmap.helper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;

import com.googlecode.xremoting.core.spi.Request;

/**
 * Implementation of {@link Request} for commons-httpclient.
 * 
 * @author Roman Puchkovskiy
 * @see CommonsHttpClientRequester
 */
public class CommonsHttpClientRequest implements Request {

	private class FunnelOutputStream extends OutputStream {
		@Override
		public void write(byte[] b) throws IOException {
			checkNotCommitted();
			funnel.write(b);
		}

		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			checkNotCommitted();
			funnel.write(b, off, len);
		}

		@Override
		public void write(int b) throws IOException {
			checkNotCommitted();
			funnel.write(b);
		}
	}
	private HttpClient httpClient;
	private HttpPost method;
	private ByteArrayOutputStream funnel = new ByteArrayOutputStream();
	private boolean committed = false;
	private OutputStream os = new FunnelOutputStream();
	private InputStream is;

	private HttpResponse response;

	public CommonsHttpClientRequest(HttpClient httpClient, HttpPost method) {
		super();
		this.httpClient = httpClient;
		this.method = method;
	}

	private void checkCommitted() {
		if (!committed) {
			throw new IllegalStateException("Not yet committed!");
		}
	}

	private void checkNotCommitted() {
		if (committed) {
			throw new IllegalStateException("Already committed!");
		}
	}

	protected void checkStatusCode(HttpPost method) throws IOException {
		if (response.getStatusLine().getStatusCode() != 200) {
			throw new IOException("Wrong status code: 200 expected but got "
					+ response.getStatusLine().getStatusCode() + " ("
					+ response.getStatusLine().getReasonPhrase() + ")");
		}
	}

	@Override
	public void commitRequest() throws IOException {
		checkNotCommitted();
		committed = true;
		method.setEntity(new ByteArrayEntity(funnel.toByteArray()));
		funnel = null;
		try {
			os.close();
		} catch (IOException e) {
			// ignore
		}
		os = null;
		response = httpClient.execute(method);
		checkStatusCode(method);
		is = response.getEntity().getContent();
	}

	@Override
	public InputStream getInputStream() throws IOException {
		checkCommitted();
		return is;
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return os;
	}

	@Override
	public String getResponceType() {

		return response.getEntity().getContentType().getValue();
	}

	@Override
	public void releaseRequest() {
		if (is != null) {
			try {
				is.close();
			} catch (IOException e) {
				// ignore
			}
			is = null;
		}
		if (os != null) {
			try {
				os.close();
			} catch (IOException e) {
				// ignore
			}
			os = null;
		}
		funnel = null;
		if (method != null) {
			method.abort();
		}
	}

}
package com.socarmap.helper;

import java.io.IOException;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;

import com.googlecode.xremoting.core.spi.Request;
import com.googlecode.xremoting.core.spi.Requester;

public class CommonsHttpClientRequester implements Requester {
	private HttpClient httpClient;
	private String url;

	/**
	 * Creates a new CommonsHttpClientRequester instance using a pre-configured
	 * HttpClient and URL of remote service which is accessible using HTTP(s).
	 * 
	 * @param httpClient
	 *            HttpClient instance
	 * @param url
	 *            remote service URL
	 */
	public CommonsHttpClientRequester(HttpClient httpClient, String url) {
		super();
		this.httpClient = httpClient;
		this.url = url;
	}

	protected void configureMethod(HttpPost method) {
		method.setHeader("Content-Type", "application/xml");
	}

	@Override
	public Request createRequest() throws IOException {
		HttpPost method = new HttpPost(url);
		configureMethod(method);
		return new CommonsHttpClientRequest(httpClient, method);
	}

}

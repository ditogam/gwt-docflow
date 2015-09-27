package no.tornado.brap.client;

import java.io.IOException;

public class HttpURLConnectionTransportProvider implements
		TransportProvider<HttpURLTransportSession> {
	@Override
	public HttpURLTransportSession createSession(
			MethodInvocationHandler invocationHandler) {
		return new HttpURLTransportSession(invocationHandler);
	}

	@Override
	public void endSession(HttpURLTransportSession session,
			MethodInvocationHandler invocationHandler) {
		try {
			session.getConn().getInputStream().close();
		} catch (IOException ignored) {
		}
	}
}
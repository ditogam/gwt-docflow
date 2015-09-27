package no.tornado.brap.client;

import java.io.InputStream;
import java.lang.reflect.Method;

import no.tornado.brap.common.InvocationRequest;

public interface TransportSession {
	InputStream sendInvocationRequest(Method method, InvocationRequest request,
			InputStream streamArgument) throws Exception;
}
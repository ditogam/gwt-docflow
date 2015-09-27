package com.googlecode.xremoting.core.invoking;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.googlecode.xremoting.core.XRemotingProxyFactory;
import com.googlecode.xremoting.core.exception.InvokingSideInvocationException;
import com.googlecode.xremoting.core.message.Invocation;
import com.googlecode.xremoting.core.message.Result;
import com.googlecode.xremoting.core.message.Thrown;
import com.googlecode.xremoting.core.spi.Request;
import com.googlecode.xremoting.core.spi.Requester;
import com.googlecode.xremoting.core.spi.SerializationException;
import com.googlecode.xremoting.core.spi.Serializer;

/**
 * {@link InvocationHandler} for XRemoting. Used by
 * {@link XRemotingProxyFactory} on client side.
 * 
 * @author Roman Puchkovskiy
 * @see XRemotingProxyFactory
 */
public class XRemotingInvocationHandler implements InvocationHandler {

	private Serializer serializer;
	private Requester requester;

	public XRemotingInvocationHandler(Serializer serializer, Requester requester) {
		super();
		this.serializer = serializer;
		this.requester = requester;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		boolean ping = method.getName().equalsIgnoreCase("ping");
		Timer.start(method.getName());
		Invocation invocation = new Invocation(method.getName(),
				method.getParameterTypes(), args);

		Request request = null;
		try {
			if (!ping)
				Timer.start("createRequest");
			request = requester.createRequest();
			if (!ping)
				Timer.step("createRequest");
			OutputStream os = request.getOutputStream();
			if (!ping)
				Timer.start("serialize");
			serializer.serialize(invocation, os);
			if (!ping)
				Timer.step("serialize");
			if (!ping)
				Timer.start("commitRequest");
			request.commitRequest();
			if (!ping)
				Timer.step("commitRequest");
			InputStream is = request.getInputStream();
			if (!ping)
				Timer.start("deserialize");
			Object result = serializer.deserialize(is);
			if (!ping)
				Timer.step("deserialize");
			if (result instanceof Result) {
				Result returnValue = (Result) result;
				return returnValue.getObject();
			} else if (result instanceof Thrown) {
				Thrown thrown = (Thrown) result;
				throw thrown.getThrowable();
			} else {
				throw new InvokingSideInvocationException(
						"Instance of unexpected class returned; Result or Thrown expected but got "
								+ result.getClass().getName());
			}
		} catch (IOException e) {
			throw new InvokingSideInvocationException(e);
		} catch (SerializationException e) {
			throw new InvokingSideInvocationException(e);
		} finally {
			if (!ping)
				Timer.step(method.getName());
			if (!ping)
				Timer.printall();
			if (request != null) {
				request.releaseRequest();
			}
		}

	}

}
package com.socarmap.server;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.socarmap.proxy.beans.SocarException;

public class SocarExceptionCriator {

	private static SocarException generateThrow(SocarException th) {
		th.setMyMessage(th.getMessage());
		StringWriter sw = new StringWriter();
		th.printStackTrace(new PrintWriter(sw));
		th.setDetailed(sw.toString());
		return th;
	}

	public static SocarException doThrow() {
		SocarException ex = new SocarException();
		return generateThrow(ex);
	}

	public static SocarException doThrow(String detailMessage,
			Throwable throwable) {
		SocarException ex = new SocarException(detailMessage, throwable);
		return generateThrow(ex);
	}

	public static SocarException doThrow(String detailMessage) {
		SocarException ex = new SocarException(detailMessage);
		return generateThrow(ex);
	}

	public static SocarException doThrow(Throwable th) {
		SocarException ex = new SocarException(th);
		return generateThrow(ex);
	}
}

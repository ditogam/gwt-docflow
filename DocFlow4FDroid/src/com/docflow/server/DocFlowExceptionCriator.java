package com.docflow.server;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.docflow.shared.DocFlowException;

public class DocFlowExceptionCriator {

	private static DocFlowException generateThrow(DocFlowException th) {
		th.setMyMessage(th.getMessage());
		StringWriter sw = new StringWriter();
		th.printStackTrace(new PrintWriter(sw));
		th.setDetailed(sw.toString());
		return th;
	}

	public static DocFlowException doThrow() {
		DocFlowException ex = new DocFlowException();
		return generateThrow(ex);
	}

	public static DocFlowException doThrow(String detailMessage,
			Throwable throwable) {
		DocFlowException ex = new DocFlowException(detailMessage, throwable);
		return generateThrow(ex);
	}

	public static DocFlowException doThrow(String detailMessage) {
		DocFlowException ex = new DocFlowException(detailMessage);
		return generateThrow(ex);
	}

	public static DocFlowException doThrow(Throwable th) {
		DocFlowException ex = new DocFlowException(th);
		return generateThrow(ex);
	}
}

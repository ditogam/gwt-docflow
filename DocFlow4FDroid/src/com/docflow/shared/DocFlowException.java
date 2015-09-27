package com.docflow.shared;

public class DocFlowException extends Exception {

	private String myMessage = null;
	private String detailed = null;
	/**
	 * 
	 */
	private static final long serialVersionUID = 2541258684385393878L;

	public DocFlowException() {
		super();

	}

	public DocFlowException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);

	}

	public DocFlowException(String detailMessage) {
		super(detailMessage);

	}

	public DocFlowException(Throwable throwable) {
		super(throwable);
	}

	public String getMyMessage() {
		return myMessage;
	}

	public void setMyMessage(String myMessage) {
		this.myMessage = myMessage;
	}

	public String getDetailed() {
		return detailed;
	}

	public void setDetailed(String detailed) {
		this.detailed = detailed;
	}

}

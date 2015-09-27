package com.docflow.shared.map;

import java.io.Serializable;

public class MakeDBResponce implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4861085570497998499L;
	private int operationCompleted = 0;
	private boolean completed;
	private int fileSize;

	public int getFileSize() {
		return fileSize;
	}

	public int getOperationCompleted() {
		return operationCompleted;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}

	public void setOperationCompleted(int operationCompleted) {
		this.operationCompleted = operationCompleted;
	}

}

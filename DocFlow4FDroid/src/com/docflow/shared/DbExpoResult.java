package com.docflow.shared;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable;

public class DbExpoResult implements IsSerializable {
	private String session_id;
	private ArrayList<DBData> dbDatas;
	private String tableName;
	private DocFlowException exception;
	private Boolean done;
	private Integer fileSize;

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public DocFlowException getException() {
		return exception;
	}

	public void setException(DocFlowException exception) {
		this.exception = exception;
	}

	public Boolean getDone() {
		return done;
	}

	public void setDone(Boolean done) {
		this.done = done;
	}

	public String getSession_id() {
		return session_id;
	}

	public void setSession_id(String session_id) {
		this.session_id = session_id;
	}

	public ArrayList<DBData> getDbDatas() {
		return dbDatas;
	}

	public void setDbDatas(ArrayList<DBData> dbDatas) {
		this.dbDatas = dbDatas;
	}

	public Integer getFileSize() {
		return fileSize;
	}

	public void setFileSize(Integer fileSize) {
		this.fileSize = fileSize;
	}
}

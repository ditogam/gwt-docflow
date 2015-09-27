package com.socar.map.exporter;

public class SqlLiteCommiter implements Runnable {
	private SqlLiteConnectionNew conn;
	private Thread thread;

	public SqlLiteCommiter(SqlLiteConnectionNew conn) {
		this.conn = conn;
		thread = new Thread(this, "MyName is" + conn.getName());
		thread.start();
	}

	@Override
	public void run() {
		try {
			conn.commit();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public Thread getThread() {
		return thread;
	}

}

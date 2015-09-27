package com.socar.map.exporter.one;

public class SqlLiteCommiterOne implements Runnable {
	private SqlLiteConnectionOne conn;
	private Thread thread;

	public SqlLiteCommiterOne(SqlLiteConnectionOne conn) {
		this.conn = conn;
		thread = new Thread(this, "MyName is" + conn);
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

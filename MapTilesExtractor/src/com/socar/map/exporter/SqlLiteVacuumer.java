package com.socar.map.exporter;

import com.socar.map.downloader.Timer;

public class SqlLiteVacuumer implements Runnable {
	private static final String VACUUM = "VACUUM";
	private SqlLiteConnectionNew conn;
	private Thread thread;

	public SqlLiteVacuumer(SqlLiteConnectionNew conn) {
		this.conn = conn;
//		thread = new Thread(this, "MyName is" + conn.getName());
//		thread.start();
		run();
	}

	@Override
	public void run() {
		try {
			Timer.start(VACUUM);
			conn.getConn().setAutoCommit(false);
//			conn.getConn().setAutoCommit(true);
//			PreparedStatement stmt = conn.getConn().prepareStatement("VACUUM");
//			stmt.execute();
			conn.getConn().commit();
			Timer.step(VACUUM);
			Timer.printall();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.getConn().setAutoCommit(false);
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
	}

	public Thread getThread() {
		return thread;
	}

}

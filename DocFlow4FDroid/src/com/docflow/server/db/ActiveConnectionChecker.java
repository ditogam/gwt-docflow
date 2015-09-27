package com.docflow.server.db;

import java.util.TimerTask;

import com.common.db.DBConnection;

public class ActiveConnectionChecker extends TimerTask {

	@Override
	public void run() {
		DBConnection.checkActiveConnections();
	}

}

package com.docflowdroid.traffic;

import java.io.Serializable;

public class NetTraffic implements Serializable {
	/**
	 * 
	 */

	private static final long serialVersionUID = 8442897337635229546L;
	private long recieved;
	private long sent;

	private long recievedMobile;
	private long sentMobile;

	private long recievedApp;
	private long sentApp;

	public NetTraffic() {
	}

	@Override
	public String toString() {

		long[] data = new long[] { sent, recievedMobile, sentMobile,
				recievedApp, sentApp };
		String ret = recieved + "";
		for (long l : data) {
			ret += "," + l;
		}
		return ret;
	}

	public NetTraffic(String data) {
		try {
			String[] d = data.split(",");
			int index = 0;
			recieved = Long.valueOf(d[index++]);
			sent = Long.valueOf(d[index++]);
			recievedMobile = Long.valueOf(d[index++]);
			sentMobile = Long.valueOf(d[index++]);
			recievedApp = Long.valueOf(d[index++]);
			sentApp = Long.valueOf(d[index++]);

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public long getSent() {
		return sent;
	}

	public void setSent(long sent) {
		this.sent = sent;
	}

	public long getRecieved() {
		return recieved;
	}

	public void setRecieved(long recieved) {
		this.recieved = recieved;
	}

	public long getRecievedMobile() {
		return recievedMobile;
	}

	public void setRecievedMobile(long recievedMobile) {
		this.recievedMobile = recievedMobile;
	}

	public long getSentMobile() {
		return sentMobile;
	}

	public void setSentMobile(long sentMobile) {
		this.sentMobile = sentMobile;
	}

	public long getSentApp() {
		return sentApp;
	}

	public void setSentApp(long sentApp) {
		this.sentApp = sentApp;
	}

	public long getRecievedApp() {
		return recievedApp;
	}

	public void setRecievedApp(long recievedApp) {
		this.recievedApp = recievedApp;
	}

}

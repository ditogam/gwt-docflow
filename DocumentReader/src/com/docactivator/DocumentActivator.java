package com.docactivator;

import java.io.FileReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Properties;

public class DocumentActivator extends Thread {

	private static final int TP_DOC_READER = 1;
	private static final int TP_DOCF_SMSR = 2;

	private int type;

	public DocumentActivator(int type) {
		this.type = type;
	}

	@Override
	public void run() {

		while (true) {
			try {
				if (type == TP_DOC_READER) {
					DBOperations.operate(500);
				}
				if (type == TP_DOCF_SMSR) {
					SMSSenderLog.operate();

				}
				Thread.sleep(20);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public static void main(String[] args) {
		Properties p = new Properties();
		boolean bdocreader = (args == null || args.length == 0);
		int port = 65534 ;
		String host = "docflowdb";
		try {
			SocketAddress sa = null;
			try {
				p.load(new FileReader("DB.properties"));
				port = Integer.parseInt(p.getProperty("port"));
				port = port + (bdocreader ? 0 : 1);
				host = p.getProperty("host");
				sa = new InetSocketAddress(host, port);
			} catch (Exception e) {
				throw new Exception("Cannot read propertie file - " + e.getMessage());
			}

			
			
			
			boolean err = false;
			try {
				Socket s = new Socket();
				s.connect(sa);
				err = true;
				throw new Exception("The program is already running on host=" + host + "port=" + port + "!!!!");

			} catch (Exception e) {
				if (err)
					throw e;
			}
			try {

				ServerSocket s = new ServerSocket();
				s.bind(sa);
			} catch (Exception e) {
				throw new Exception("The program is already running on host=" + host + "port=" + port + "!!!!");
			}

			if (bdocreader)
				new DocumentActivator(TP_DOC_READER).start();
			else {
				new DocumentActivator(TP_DOCF_SMSR).start();
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.exit(0);
		}

	}
}

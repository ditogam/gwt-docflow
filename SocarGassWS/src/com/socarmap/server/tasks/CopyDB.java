package com.socarmap.server.tasks;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.Channels;
import java.nio.channels.Pipe;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;

import com.mindbright.nio.NetworkConnection;
import com.mindbright.nio.NonBlockingOutput;
import com.mindbright.ssh2.SSH2ConsoleRemote;
import com.mindbright.ssh2.SSH2SCP1Client;
import com.mindbright.ssh2.SSH2SimpleClient;
import com.mindbright.ssh2.SSH2Transport;
import com.mindbright.sshcommon.SSHSCP1;
import com.mindbright.util.SecureRandomAndPad;
import com.socarmap.proxy.beans.BuildingUpdate;
import com.socarmap.proxy.beans.MakeDBProcess;
import com.socarmap.server.TileDBCopyProperty;

public class CopyDB {

	public CopyDB(String fileName, int subregion_id, Date lastDownloaded,
			MakeDBProcess process) throws Exception {
		TileDBCopyProperty prop = TileDBCopyProperty.load();
		if (prop == null)
			return;
		if (prop.isScp()) {
			NetworkConnection socket = NetworkConnection.open(
					prop.getHost_name(), prop.getPort());
			SSH2Transport transport = new SSH2Transport(socket,

			createSecureRandom());

			SSH2SimpleClient client = new SSH2SimpleClient(transport,
					prop.getUser_name(), prop.getPassword());
			SSH2ConsoleRemote console = new SSH2ConsoleRemote(
					client.getConnection());
			if (process == null) {
				scp(fileName, subregion_id, lastDownloaded, prop, client,
						console);
			} else {
				String cmdLine = "ls -l "
						+ prop.getRemote_dir()
						+ subregion_id
						+ ".sqlite |awk '{print $5}'&&sqlite3 "
						+ prop.getRemote_dir()
						+ subregion_id
						+ ".sqlite \"SELECT  sum(12+length(file_data))||'_'||count(1) FROM mapfiledatazxy where created_on>=0\"";
				ArrayList<String> list = exucuteConsole(console, cmdLine);
				process.setFilesize(Integer.valueOf(list.get(0)));
				String[] dbCounter = list.get(1).split("_");
				process.setAproximatesize(Integer.valueOf(dbCounter[0]));
				process.setCount(Integer.valueOf(dbCounter[1]));
				process.setShouldCopyTiles(process.getFilesize() < BuildingUpdate.TRANSFER_MAX_SIZE);
			}
			transport.normalDisconnect("User disconnects");
		}

	}

	private void scp(String fileName, int subregion_id, Date lastDownloaded,
			TileDBCopyProperty prop, SSH2SimpleClient client,
			SSH2ConsoleRemote console) throws IOException {
		SSH2SCP1Client scpClient = new SSH2SCP1Client(new File(
				System.getProperty("user.dir")), client.getConnection(),
				System.err, false);
		File srcFile = new File(fileName);
		String dstFile = "/root/" + srcFile.getName() + "." + System.nanoTime();
		dstFile = dstFile.replace(File.separatorChar, '/');
		SSHSCP1 scp = scpClient.scp1();
		scp.copyToRemote(srcFile.getAbsolutePath(), dstFile, false);

		String[] files = { "" + subregion_id };
		for (String f : files) {

			String cmdLine = "sqlite3 "
					+ dstFile
					+ " \"ATTACH DATABASE '"
					+ prop.getRemote_dir()
					+ f
					+ ".sqlite' as candidate; delete from zxy  where exists "

					+ "(select 1 from candidate.mapfiledatazxy k where k.created_on>"
					+ lastDownloaded.getTime()
					+ " and k.zoom=zxy.zoom and k.x=zxy.x and k.y=zxy.y) ;"

					+ "insert into zxy(zoom, x, y,file_data) select zoom,x,y,file_data from candidate.mapfiledatazxy k where k.created_on>"
					+ lastDownloaded.getTime()
					+ ";DETACH DATABASE candidate;\"";

			exucuteConsole(console, cmdLine);
		}
		scp.copyToLocal(srcFile.getAbsolutePath(), dstFile, false);
		exucuteConsole(console, "rm " + dstFile);

		scpClient.close();
	}

	private static ArrayList<String> exucuteConsole(SSH2ConsoleRemote console,
			String cmdLine) throws IOException {

		Pipe pipe = Pipe.open();
		ArrayList<String> result = new ArrayList<String>();
		@SuppressWarnings("unused")
		int exitStatus = -1;

		NonBlockingOutput out = new NonBlockingOutput(pipe);

		if (console.command(cmdLine, null, out, out)) {

			/*
			 * 
			 * Fetch the internal stdout stream and wrap it in a BufferedReader
			 * 
			 * for convenience.
			 */

			BufferedReader stdout = new BufferedReader(new InputStreamReader(

			Channels.newInputStream(pipe.source())));

			/*
			 * 
			 * Read all output sent to stdout (line by line) and print it to our
			 * 
			 * own stdout.
			 */

			String line;

			while ((line = stdout.readLine()) != null) {

				result.add(line);

			}

			/*
			 * 
			 * Retrieve the exit status of the command (from the remote end).
			 */

			exitStatus = console.waitForExitStatus();

		}
		return result;

	}

	private static SecureRandomAndPad createSecureRandom() {
		SecureRandom random = new SecureRandom();
		SecureRandomAndPad secureRandom = new SecureRandomAndPad(random);
		return secureRandom;
	}

	public static void main(String[] args) {
		String line = "130510.sqlite";
		String[] dt = line.trim().split(".sqlite");
		System.out.println(dt[0]);
	}
}

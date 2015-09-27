<%@page import="java.util.zip.GZIPOutputStream"%>
<%@page import="java.io.OutputStreamWriter"%>
<%@page import="java.io.BufferedWriter"%>
<%@page import="java.io.ByteArrayOutputStream"%>
<%@page import="java.io.OutputStream"%>
<%@page import="java.io.FileInputStream"%>
<%@page import="java.io.File"%>
<%@page import="java.util.Collections"%>
<%@page import="java.util.Collection"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.io.InputStreamReader"%>
<%@page import="java.io.BufferedReader"%>
<%@page import="java.io.InputStream"%>
<%@page import="com.jcraft.jsch.ChannelExec"%>
<%@page import="com.jcraft.jsch.ChannelSftp"%>
<%@page import="com.jcraft.jsch.Session"%>
<%@page import="com.jcraft.jsch.JSch"%>
<%@page import="com.socarmap.server.TileDBCopyProperty"%>
<%@ page trimDirectiveWhitespaces="true"%>
<%@ page language="java" contentType="application/octet-stream"
	pageEncoding="US-ASCII"%>
<%
	SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
	TileDBCopyProperty prop = TileDBCopyProperty.load();
	if (prop == null)
		return;
	String fileNames = "";
	String command = "ls -m1 " + prop.getRemote_dir() + "bydate/";
	Session sessions = null;
	ChannelExec channelExec = null;
	Process tr = null;
	InputStream in = null;
	if (prop.isScp()) {
		JSch jsch = new JSch();
		sessions = jsch.getSession(prop.getUser_name(),
				prop.getHost_name(), prop.getPort());
		sessions.setPassword(prop.getPassword());
		sessions.setConfig("StrictHostKeyChecking", "no");
		sessions.connect();
		channelExec = (ChannelExec) sessions.openChannel("exec");
		in = channelExec.getInputStream();
		channelExec.setCommand(command);
		channelExec.connect();
	} else {
		tr = Runtime.getRuntime().exec(command);
		tr.waitFor();
		in = tr.getInputStream();
	}

	BufferedReader reader = new BufferedReader(
			new InputStreamReader(in));
	String line;
	int index = 0;
	ArrayList<Date> dates = new ArrayList<Date>();

	while ((line = reader.readLine()) != null) {
		try {
			String[] dt = line.toUpperCase().trim().split(".sqlite");
			Date d = sdf.parse(dt[0].trim());
			dates.add(d);
		} catch (Exception e) {

		}
	}
	Collections.sort(dates);
	String lasupdated = request.getParameter("lasupdated");
	Date lasupdatedD = new Date(0);
	try {
		lasupdatedD = new Date(Long.valueOf(lasupdated.trim()));
	} catch (Exception e) {

	}
	String fileName = null;
	for (Date dt : dates) {
		if (dt.getTime() >= lasupdatedD.getTime()) {
			fileName = sdf.format(dt) + ".sqlite";
			break;
		}
	}

	if (channelExec != null) {
		channelExec.disconnect();
	} else {
		tr.destroy();
	}
	if (fileName != null) {
		boolean onlySize = request.getParameter("size") != null;
		if (!onlySize) {

			/* GZIPOutputStream gzipStream = new GZIPOutputStream(
					response.getOutputStream()); */
			response.setHeader("Content-Disposition",
					"attachment;filename=" + sdf.format(new Date()));

			if (sessions != null) {
				ChannelSftp channel = (ChannelSftp) sessions
						.openChannel("sftp");
				channel.connect();
				channel.cd(prop.getRemote_dir() + "bydate");
				channel.get(fileName, response.getOutputStream());
				channel.exit();
			} else {
				File file = new File(prop.getRemote_dir() + "bydate",
						fileName);
				FileInputStream fileInputStream = new FileInputStream(
						file);
				OutputStream fileOutputStream = response
						.getOutputStream();
				int bufferSize;
				byte[] bufffer = new byte[512];
				while ((bufferSize = fileInputStream.read(bufffer)) > 0) {
					fileOutputStream.write(bufffer, 0, bufferSize);
				}
				fileInputStream.close();
				fileOutputStream.flush();
				fileOutputStream.close();
			}
		} else {
			response.setContentType("text/html");
			response.setHeader("content-type", "text/html");
			command = "ls -l " + prop.getRemote_dir() + "bydate/"
					+ fileName + "|awk '{print $5}'";
			if (sessions != null) {
				channelExec = (ChannelExec) sessions
						.openChannel("exec");
				in = channelExec.getInputStream();
				channelExec.setCommand(command);
				channelExec.connect();
				reader = new BufferedReader(new InputStreamReader(in));
				line = null;
				index = 0;

				while ((line = reader.readLine()) != null) {
					out.println(line);
				}
				if (channelExec != null) {
					channelExec.disconnect();
				}

			} else {
				File file = new File(prop.getRemote_dir() + "bydate",
						fileName);
				out.println(file.length());
			}

		}
	}
	if (sessions != null) {
		sessions.disconnect();
	}
%>
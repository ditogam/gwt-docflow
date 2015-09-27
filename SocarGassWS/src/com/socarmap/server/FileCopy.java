package com.socarmap.server;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspWriter;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class FileCopy {
	public FileCopy(JspWriter out, HttpServletRequest request,
			HttpServletResponse response, HttpSession httpSession)
			throws Exception {
		int subregionid = 0;
		try {
			subregionid = Integer.parseInt(request.getParameter("subregion"));
		} catch (Exception e) {
			return;
		}
		TileDBCopyProperty prop = TileDBCopyProperty.load();
		if (prop == null)
			return;
		if (prop.isScp()) {
			JSch jsch = new JSch();
			Session session = jsch.getSession(prop.getUser_name(),
					prop.getHost_name(), prop.getPort());
			session.setPassword(prop.getPassword());
			session.setConfig("StrictHostKeyChecking", "no");
			session.connect();

			ChannelSftp sftpChannel = (ChannelSftp) session.openChannel("sftp");
			sftpChannel.connect();
			String filename = subregionid + ".sqlite";
			response.setContentType("application/sqlite");
			response.setHeader("Content-Disposition", "attachment; filename=\""
					+ filename + "\"");
			ServletOutputStream sos = response.getOutputStream();
			String file = prop.getRemote_dir() + filename;
			sftpChannel.get(file, sos);
			sftpChannel.disconnect();
			session.disconnect();

			sos.flush();
		}

	}

}

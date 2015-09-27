package com.docflow.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FileDownload extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8492839294620027863L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		proceed(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		proceed(req, resp);
	}

	private void proceed(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Map<String, String> criteria = new TreeMap<String, String>();
		criteria.put("id", req.getParameter("id"));
		try {
			Map<?, ?> map = DMIUtils.findRecordByCriteria(
					"Demage_Description_FilesDS", null, criteria);
			String act_document_filename = "temp.png";
			int act_document_filesize = Integer.parseInt(map.get(
					"file_data_filesize").toString());
			InputStream is = (InputStream) map.get("file_data");
			String str = URLConnection.guessContentTypeFromStream(is);
			byte[] bt = new byte[act_document_filesize];
			is.read(bt);
			is.close();

			resp.setContentType(str);
			resp.setContentLength(act_document_filesize);
			// resp.setHeader("Content-Disposition", "attachment;filename="
			// + act_document_filename);
			resp.setCharacterEncoding("UTF-8");
			ServletOutputStream os = resp.getOutputStream();
			os.write(bt);
			os.flush();
			os.close();

		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
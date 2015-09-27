package com.docflow.server.db;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.MultipartStream.ItemInputStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.docflow.server.ImageData;
import com.docflow.server.ImageStore;

public class MyFileServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3980893223320835562L;
	private static Log log = LogFactory.getLog(MyFileServlet.class);

	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		process(request, response);
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		process(request, response);
	}

	public static void process(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			if (ServletFileUpload.isMultipartContent(request)) {
				processFiles(request, response);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void processFiles(HttpServletRequest request,
			HttpServletResponse response) {
		HashMap<String, String> args = new HashMap<String, String>();

		ArrayList<ImageData> imageDatas = new ArrayList<ImageData>();

		try {
			if (log.isDebugEnabled())
				log.debug(request.getParameterMap());
			System.out.println(request.getParameterMap());
			ServletFileUpload upload = new ServletFileUpload();
			FileItemIterator iter = upload.getItemIterator(request);
			// pick up parameters first and note actual FileItem
			while (iter.hasNext()) {
				FileItemStream item = iter.next();
				String name = item.getFieldName();
				if (item.isFormField()) {
					args.put(name, Streams.asString(item.openStream()));
				} else {
					InputStream in = null;

					in = item.openStream();
					String fileName = item.getName();
					int slash = fileName.lastIndexOf("/");
					if (slash < 0)
						slash = fileName.lastIndexOf("\\");
					if (slash > 0)
						fileName = fileName.substring(slash + 1);
					if (in.getClass().equals(ItemInputStream.class)) {
						ImageData data = ImageStore.saveFile(
								(ItemInputStream) in, item.getContentType(),
								fileName);
						imageDatas.add(data);
					}
				}
			}

			JSONArray json = new JSONArray();
			for (ImageData imageData : imageDatas) {

				Map obj = new LinkedHashMap();
				obj.put("id", imageData.getId());
				obj.put("file", imageData.getImagename());

				JSONObject jobj = new JSONObject(obj);
				json.add(jobj);
			}
			String resp = json.toString();
			response.setContentType("application/json");
			response.getWriter().write(resp);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

}

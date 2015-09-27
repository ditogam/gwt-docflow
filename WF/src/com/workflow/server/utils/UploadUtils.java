package com.workflow.server.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.MultipartStream.ItemInputStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class UploadUtils {
	@SuppressWarnings("unchecked")
	public static void upload(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		JSONArray json = new JSONArray();

		if (ServletFileUpload.isMultipartContent(request)) {
			ServletFileUpload upload = new ServletFileUpload();
			FileItemIterator iter = upload.getItemIterator(request);
			HashMap<String, String> args = new HashMap<String, String>();
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

						String contentType = item.getContentType();
						ByteArrayOutputStream buffer = new ByteArrayOutputStream();
						Streams.copy(in, buffer, false);
						buffer.flush();
						byte[] arr = buffer.toByteArray();
						buffer.close();
						HashMap<String, Object> data = new HashMap<String, Object>();
						data.put("bdata", new ByteArrayInputStream(arr));
						data.put("bdata_filename", fileName);
						data.put("bdata_filesize", arr.length);
						data.put("bdata_date_created",
								new Timestamp(System.currentTimeMillis()));
						data.put("bdata_contenttype", contentType);
						List<Map<?, ?>> list = DMIUtils.execute("ImageDataDS",
								null, data, "add");
						Map<String, Object> obj = new LinkedHashMap<String, Object>();
						obj.put("id", list.get(0).get("id"));
						obj.put("file", fileName);

						JSONObject jobj = new JSONObject(obj);
						json.add(jobj);

					}
				}
			}

		}
		String resp = json.toString();
		response.setContentType("application/json");
		response.getWriter().write(resp);
	}
}

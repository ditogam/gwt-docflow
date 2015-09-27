package com.docflow.server.db;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.docflow.server.ImageData;
import com.docflow.server.ImageStore;

public class FileDownloader {
	private static String GENERATED_DIR = "tmpfiledir";

	@SuppressWarnings("rawtypes")
	public FileDownloader(HttpServletRequest request,
			HttpServletResponse response, ServletContext context) {
		Map pmap = request.getParameterMap();
		if (pmap.isEmpty())
			return;
		try {
			Object value = pmap.get("id");
			Object hr = pmap.get("hr");
			String sid = ((String[]) value)[0];
			int id = Integer.parseInt(sid);
			ImageData newImage = ImageStore.getImageData(id, hr);
			if (newImage == null)
				return;

			String basePath = request.getServletPath();
			String realpath = context.getRealPath(basePath);
			File jspPath = new File(realpath);
			if (!(jspPath.exists() && jspPath.isFile()))
				return;
			File directory = jspPath.getParentFile();
			File excellDir = new File(directory, GENERATED_DIR);
			if (!excellDir.exists())
				excellDir.mkdir();
			File[] files = excellDir.listFiles();
			String filename = newImage.getImagename();
			for (File file : files) {
				file.delete();
			}
			filename = filename == null ? "Test" : filename;
			File newFile = new File(excellDir, filename);
			copyFile(MDBConnection.getImageFtp(null), "F" + newImage.getId(),
					newFile);
			String redirectUrl = GENERATED_DIR + "/" + filename;
			response.sendRedirect(redirectUrl);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void copyFile(SimpleFTPClient client, String filename,
			File destFile) throws IOException {
		if (destFile.exists()) {
			destFile.delete();
		}

		try {

			client.downloadFile(filename, destFile);
		} finally {

		}
	}

	public static void main(String[] args) throws Exception {
		SimpleFTPClient client = new SimpleFTPClient();
		FileOutputStream fos = null;

		try {
			client.connect("192.168.1.10", 21);
			// client.connect("localhost");
			// client.setCopyStreamListener(createListener());
			// client.addProtocolCommandListener(new PrintCommandListener(
			// new PrintWriter(System.out)));
			client.setRemoteFile("images/aaa.png");
			client.login("docflow", "docflow2012");
			// client.login("docflow", "docflow");
			//
			// The remote filename to be downloaded.
			//

			//
			// Download file from FTP server
			//
			// client.changeWorkingDirectory("images");

			// FTPFile[] files = client.listFiles();
			// System.out.println(files.length);
			// for (FTPFile file : files) {
			// System.out.println(file.getName());
			// }
			//
			// String[] fileNames = client.listNames();
			// if (fileNames != null) {
			// for (String file : fileNames) {
			// System.out.println(file);
			// }
			// }

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
				// client.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}

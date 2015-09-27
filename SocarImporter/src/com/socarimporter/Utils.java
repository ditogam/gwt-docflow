package com.socarimporter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Enumeration;
import java.util.TreeMap;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import nanoxml.XMLElement;

public class Utils {

	private static String MAIN_URL = "http://homepc.homelinux.org:55557/SocarExportWP/";

	public static void init(String url) {
		url = url.trim();
		url = url.endsWith("/") ? url : "/" + url;
		MAIN_URL = url;
	}

	public static void downloadFile(File file, String url) throws Exception {
		FileOutputStream fileOutput = new FileOutputStream(file);
		byte[] buffer = downlodStream(url);
		fileOutput.write(buffer);
		fileOutput.flush();
		fileOutput.close();
	}

	@SuppressWarnings("rawtypes")
	public static void unzip(File file, String dbPath) throws ZipException,
			IOException, FileNotFoundException {
		ZipFile zipFile = new ZipFile(file);
		Enumeration e = zipFile.entries();
		while (e.hasMoreElements()) {
			ZipEntry entry = (ZipEntry) e.nextElement();
			File destinationFilePath = new File(dbPath, entry.getName());
			destinationFilePath.getParentFile().mkdirs();
			BufferedInputStream bis = new BufferedInputStream(
					zipFile.getInputStream(entry));
			int b;
			byte buffer[] = new byte[1024];

			/*
			 * read the current entry from the zip file, extract it and write
			 * the extracted file.
			 */
			FileOutputStream fos = new FileOutputStream(destinationFilePath);
			BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);

			while ((b = bis.read(buffer, 0, 1024)) != -1) {
				bos.write(buffer, 0, b);
			}

			// flush the output stream and close it.
			bos.flush();
			bos.close();

			// close the input stream.
			bis.close();

		}
		zipFile.close();
	}

	@SuppressWarnings("unused")
	public static byte[] downlodStream(String urlStr)
			throws MalformedURLException, IOException, ProtocolException,
			FileNotFoundException {
		URL url = new URL(MAIN_URL + urlStr);

		// create the new connection
		HttpURLConnection urlConnection = (HttpURLConnection) url
				.openConnection();

		// set up some things on the connection
		urlConnection.setRequestMethod("GET");
		urlConnection.setDoOutput(true);

		// and connect!
		urlConnection.connect();

		// set the path where we want to save the file
		// in this case, going to save it on the root directory of the
		// sd card.

		// create a new file, specifying the path, and the filename
		// which we want to save the file as.

		// this will be used to write the downloaded data into the file we
		// created
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		// this will be used in reading the data from the internet
		InputStream inputStream = urlConnection.getInputStream();

		// this is the total size of the file

		int totalSize = urlConnection.getContentLength();
		// variable to store total downloaded bytes
		int downloadedSize = 0;

		// create a buffer...
		byte[] buffer = new byte[1024];
		int bufferLength = 0; // used to store a temporary size of the
								// buffer

		// now, read through the input buffer and write the contents to the
		// file
		while ((bufferLength = inputStream.read(buffer)) > 0) {
			// add the data in the buffer to the file in the file output
			// stream (the file on the sd card
			bos.write(buffer, 0, bufferLength);
			// add up the size so we know how much is downloaded
			downloadedSize += bufferLength;
			// this is where you would do something to report the prgress,
			// like this maybe

		}
		// close the output stream when done
		bos.flush();
		bos.close();
		return bos.toByteArray();
	}

	@SuppressWarnings("unchecked")
	public static TreeMap<Long, String> getMap(String url) {
		TreeMap<Long, String> result = new TreeMap<Long, String>();
		try {
			byte[] bt = downlodStream(url);
			String str = new String(bt, "UTF8");
			str = str.trim();
			XMLElement xml = new XMLElement();
			xml.parseString(str);
			Vector<XMLElement> vec = xml.getChildren();
			for (XMLElement xmlElement : vec) {
				Object value = xmlElement.getAttribute("value");
				Long id = Long.parseLong(xmlElement.getAttribute("id")
						.toString().trim());
				result.put(id, value.toString().trim());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}

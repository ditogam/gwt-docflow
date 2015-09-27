package com.docflowdroid.helper;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

public class Utils {

	private static final int BUFFER_SIZE = 1024 * 4;
	private static final int DEFAULT_SCREEN_WIDTH = 1024;
	private static int SCREEN_WIDTH;
	private static int SCREEN_HEIGHT;

	public static void initScreenResolution(Context context) {
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		SCREEN_WIDTH = size.x;
		SCREEN_HEIGHT = size.y;
	}

	public static int getWidth(int width) {
		double scale = (double) SCREEN_WIDTH / (double) DEFAULT_SCREEN_WIDTH;
		width = (int) ((double) width * scale);
		return width;
	}

	private static char[] hextable = { '0', '1', '2', '3', '4', '5', '6', '7',
			'8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	public static String byteArrayToHex(byte[] array) {
		String s = "";
		for (int i = 0; i < array.length; ++i) {
			int di = (array[i] + 256) & 0xFF; // Make it unsigned
			s = s + hextable[(di >> 4) & 0xF] + hextable[di & 0xF];
		}
		return s;
	}

	public static String digest(String s, String algorithm) {
		MessageDigest m = null;
		try {
			m = MessageDigest.getInstance(algorithm);
		} catch (NoSuchAlgorithmException e) {
			return s;
		}

		m.update(s.getBytes(), 0, s.length());
		return byteArrayToHex(m.digest());
	}

	public static String md5(String s) {
		return digest(s, "MD5");
	}

	public static byte[] read(File file) throws Exception {
		ByteArrayOutputStream ous = null;
		InputStream ios = null;

		try {

			byte[] buffer = new byte[4096];
			ous = new ByteArrayOutputStream();
			ios = new FileInputStream(file);
			int read = 0;
			while ((read = ios.read(buffer)) != -1) {
				ous.write(buffer, 0, read);
			}
		} finally {
			try {
				if (ous != null)
					ous.close();
			} catch (IOException e) {

			}
			try {
				if (ios != null)
					ios.close();
			} catch (IOException e) {
			}
		}

		return ous.toByteArray();

	}

	public static long copyLarge(InputStream input, OutputStream output)
			throws IOException {
		byte[] buffer = new byte[BUFFER_SIZE];
		long count = 0;
		int n = 0;

		while (-1 != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			count += n;
		}
		return count;
	}

	public static Bitmap resizePicture(byte[] bytes, int targetWidth,
			int targetHeight) {
		Bitmap pic;
		// create bitmap options to calculate and use sample size
		BitmapFactory.Options bmpOptions = new BitmapFactory.Options();
		// first decode image dimensions only - not the image bitmap
		// itself
		bmpOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(bytes, 0, bytes.length, bmpOptions);
		// work out what the sample size should be
		// image width and height before sampling
		int currHeight = bmpOptions.outHeight;
		int currWidth = bmpOptions.outWidth;
		// variable to store new sample size
		int sampleSize = 1;
		// calculate the sample size if the existing size is larger
		// than target size
		if (currHeight > targetHeight || currWidth > targetWidth) {
			// use either width or height
			if (currWidth > currHeight)
				sampleSize = Math.round((float) currHeight
						/ (float) targetHeight);
			else
				sampleSize = Math
						.round((float) currWidth / (float) targetWidth);
		}
		// use the new sample size
		bmpOptions.inSampleSize = sampleSize;
		// now decode the bitmap using sample options
		bmpOptions.inJustDecodeBounds = false;
		// get the file as a bitmap
		pic = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, bmpOptions);
		return pic;
	}

	public static byte[] decompress(byte[] compressed) throws IOException {
		ByteArrayInputStream is = new ByteArrayInputStream(compressed);
		GZIPInputStream gis = new GZIPInputStream(is, BUFFER_SIZE);
		byte[] data = new byte[BUFFER_SIZE];
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		int bytesRead;
		while ((bytesRead = gis.read(data)) != -1) {
			os.write(data, 0, bytesRead);
		}
		data = os.toByteArray();
		gis.close();
		is.close();
		return data;
	}

	public static void downlodStream(String urlStr, OutputStream out)
			throws Exception {
		downlodStream(urlStr, null, "", 0, null, null, null, out);
	}

	public static void downlodStream(String urlStr,
			final TextView mDownloadStatusMessageView, final String formattext,
			final int size, final ProgressBar mDownloadProgressView,
			final ProgressDialog progressDialog, Activity act, OutputStream out)
			throws Exception {
		downlodStream(urlStr, mDownloadStatusMessageView, formattext, size,
				mDownloadProgressView, progressDialog, act, out, null);
	}

	public static void downlodCommonStream(String urlStr, OutputStream out)
			throws Exception {
		int TIMEOUT_VALUE = 30000;
		downlodCommonStream(urlStr, out, TIMEOUT_VALUE);
	}

	public static void downlodCommonStream(String urlStr, OutputStream out,
			int timeout) throws Exception {

		URL url = new URL(urlStr);
		// create the new connection
		HttpURLConnection urlConnection = (HttpURLConnection) url
				.openConnection();
		urlConnection.setConnectTimeout(timeout);
		urlConnection.setReadTimeout(timeout);
		// and connect!
		urlConnection.connect();
		InputStream inputStream = urlConnection.getInputStream();
		// this is the total size of the file
		// create a buffer...
		byte[] buffer = new byte[BUFFER_SIZE];
		int bufferLength = 0; // used to store a temporary size of the
		// buffer
		// now, read through the input buffer and write the contents to the
		// file
		while ((bufferLength = inputStream.read(buffer)) > 0) {
			// add the data in the buffer to the file in the file output
			// stream (the file on the sd card
			out.write(buffer, 0, bufferLength);

			// add up the size so we know how much is downloaded

			// this is where you would do something to report the prgress,

			// like this maybe

		}

	}

	private static HttpClient customHttpClient;

	public static synchronized HttpClient getHttpClient() {

		if (customHttpClient != null) {

			return customHttpClient;

		}

		HttpParams params = new BasicHttpParams();

		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);

		HttpProtocolParams.setContentCharset(params,

		HTTP.DEFAULT_CONTENT_CHARSET);

		HttpProtocolParams.setUseExpectContinue(params, true);

		ConnManagerParams.setTimeout(params, 1000);

		HttpConnectionParams.setConnectionTimeout(params, 5000);

		HttpConnectionParams.setSoTimeout(params, 10000);

		SchemeRegistry schReg = new SchemeRegistry();

		schReg.register(new Scheme("http", PlainSocketFactory

		.getSocketFactory(), 80));

		schReg.register(new Scheme("https",

		SSLSocketFactory.getSocketFactory(), 443));

		ClientConnectionManager conMgr = new ThreadSafeClientConnManager(

		params, schReg);

		customHttpClient = new DefaultHttpClient(conMgr, params);

		return customHttpClient;

	}

	public static void downlodStream(String urlStr,

	final TextView mDownloadStatusMessageView, final String formattext,

	final int size, final ProgressBar mDownloadProgressView,

	final ProgressDialog progressDialog, Activity act,

	OutputStream out, StringWriter fileName) throws Exception {

		// variable to store total downloaded bytes

		int downloadedSize = 0;

		if (act != null) {

			act.runOnUiThread(new Runnable() {

				@Override
				public void run() {

					if (mDownloadProgressView != null)

						mDownloadProgressView.setMax(size);

					if (progressDialog != null)

						progressDialog.setMax(size);

				}

			});

			setProcess(mDownloadStatusMessageView, formattext, size,

			mDownloadProgressView, downloadedSize, progressDialog, act);

		}

		try {

			HttpUriRequest request = new HttpGet(urlStr.toString());

			HttpClient httpClient = getHttpClient();

			HttpParams params = new BasicHttpParams();

			HttpConnectionParams.setSoTimeout(params, 60000); // 1 minute

			request.setParams(params);

			HttpResponse response = httpClient.execute(request);

			StatusLine statusLine = response.getStatusLine();

			int statusCode = statusLine.getStatusCode();

			if (statusCode == 200) {

				byte[] image = EntityUtils.toByteArray(response.getEntity());
				out.write(image);

				return;

			} else {

				throw new IOException("Download failed, HTTP response code "

				+ statusCode + " - " + statusLine.getReasonPhrase());

			}

		} finally {

			customHttpClient.getConnectionManager().shutdown();

			customHttpClient = null;

		}

		// int TIMEOUT_VALUE = 30000;

		// URL url = new URL(urlStr);

		// // create the new connection

		// HttpURLConnection urlConnection = (HttpURLConnection) url

		// .openConnection();

		// urlConnection.setConnectTimeout(TIMEOUT_VALUE);

		// urlConnection.setReadTimeout(TIMEOUT_VALUE);

		//

		// // and connect!

		// urlConnection.connect();

		// InputStream inputStream = urlConnection.getInputStream();

		//

		// // this is the total size of the file

		//

		// // create a buffer...

		// byte[] buffer = new byte[BUFFER_SIZE];

		// int bufferLength = 0; // used to store a temporary size of the

		// // buffer

		//

		// // now, read through the input buffer and write the contents to the

		// // file

		// while ((bufferLength = inputStream.read(buffer)) > 0) {

		// // add the data in the buffer to the file in the file output

		// // stream (the file on the sd card

		// out.write(buffer, 0, bufferLength);

		// // add up the size so we know how much is downloaded

		// // this is where you would do something to report the prgress,

		// // like this maybe

		// downloadedSize += bufferLength;

		// // this is where you would do something to report the prgress,

		// // like this maybe

		// if (act != null)

		// setProcess(mDownloadStatusMessageView, formattext, size,

		// mDownloadProgressView, downloadedSize, progressDialog,

		// act);

		//

		// }

		// URL url = new URL(urlStr);

		//

		// // create the new connection

		// HttpURLConnection urlConnection = (HttpURLConnection) url

		// .openConnection();

		//

		// // // set up some things on the connection

		// // urlConnection.setRequestMethod("GET");

		// // urlConnection.setDoOutput(true);

		// urlConnection.setConnectTimeout(TIMEOUT_VALUE);

		// urlConnection.setReadTimeout(TIMEOUT_VALUE);

		//

		// // and connect!

		// urlConnection.connect();

		// if (fileName != null) {

		// String disposition = urlConnection

		// .getHeaderField("Content-Disposition");

		// if (disposition != null) {

		// String fn = "filename=";

		// int index = disposition.indexOf(fn);

		// if (index > 0) {

		// index += fn.length();

		// fn = disposition.substring(index).trim();

		// fileName.append(fn);

		// }

		// }

		// }

		// // set the path where we want to save the file

		// // in this case, going to save it on the root directory of the

		// // sd card.

		//

		// // create a new file, specifying the path, and the filename

		// // which we want to save the file as.

		//

		// // this will be used to write the downloaded data into the file we

		// // created

		//

		// // this will be used in reading the data from the internet

		// InputStream inputStream = urlConnection.getInputStream();

		//

		// // this is the total size of the file

		//

		// // create a buffer...

		// byte[] buffer = new byte[BUFFER_SIZE];

		// int bufferLength = 0; // used to store a temporary size of the

		// // buffer

		//

		// // now, read through the input buffer and write the contents to the

		// // file

		// while ((bufferLength = inputStream.read(buffer)) > 0) {

		// // add the data in the buffer to the file in the file output

		// // stream (the file on the sd card

		// out.write(buffer, 0, bufferLength);

		// // add up the size so we know how much is downloaded

		// downloadedSize += bufferLength;

		// // this is where you would do something to report the prgress,

		// // like this maybe

		// if (act != null)

		// setProcess(mDownloadStatusMessageView, formattext, size,

		// mDownloadProgressView, downloadedSize, progressDialog,

		// act);

		//

		// }

	}

	private static void setProcess(final TextView mDownloadStatusMessageView,

	final String formattext, final int size,

	final ProgressBar mDownloadProgressView, final int downloadedSize,

	final ProgressDialog progressDialog, Activity act) {

		if (act != null) {

			act.runOnUiThread(new Runnable() {

				@Override
				public void run() {

					if (formattext != null) {

						String text = String.format(formattext, size + "",

						downloadedSize + "");

						if (mDownloadStatusMessageView != null) {

							mDownloadStatusMessageView.setText(text);

							mDownloadStatusMessageView.invalidate();

						}

						if (progressDialog != null) {

							progressDialog.setMessage(text);

						}

					}

					if (mDownloadProgressView != null)

						mDownloadProgressView.setProgress(downloadedSize);

					if (progressDialog != null)

						progressDialog.setProgress(downloadedSize);

				}

			});

		}

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
			 * 
			 * read the current entry from the zip file, extract it and write
			 * 
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

}
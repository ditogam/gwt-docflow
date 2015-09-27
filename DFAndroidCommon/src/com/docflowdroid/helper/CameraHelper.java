package com.docflowdroid.helper;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.webkit.MimeTypeMap;

import com.docflowdroid.ActivityHelper;
import com.docflowdroid.DocFlowCommon;
import com.docflowdroid.common.process.IProcess;
import com.docflowdroid.common.process.ProcessExecutor;

public class CameraHelper {

	public final static int CAMERA_PIC_REQUEST = 62535;

	public static void startCameraCapture(final Activity context) {
		context.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Intent cameraIntent = new Intent(
						android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

				try {
					context.startActivityForResult(cameraIntent,
							CAMERA_PIC_REQUEST);
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		});
	}

	private static void addFilePrivate(final Activity context,
			final InputStream stream, final String fileName,
			final ICameraResult cameraResult) {
		try {
			URL url = new URL(DrowableDownloader.getMainURL()
					+ "FileUpload.jsp");
			HttpURLConnection conn = null;
			DataOutputStream dos = null;
			String lineEnd = "\r\n";
			String twoHyphens = "--";
			String boundary = "*****";
			int bytesRead, bytesAvailable, bufferSize;
			byte[] buffer;
			int maxBufferSize = 1 * 1024 * 1024;

			// Open a HTTP connection to the URL
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true); // Allow Inputs
			conn.setDoOutput(true); // Allow Outputs
			conn.setUseCaches(false); // Don't use a Cached Copy
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("ENCTYPE", "multipart/form-data");
			conn.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);
			conn.setRequestProperty("uploaded_file", fileName);

			dos = new DataOutputStream(conn.getOutputStream());

			dos.writeBytes(twoHyphens + boundary + lineEnd);
			dos.writeBytes("Content-Disposition: form-data; name=" + fileName
					+ ";filename=" + fileName + "" + lineEnd);
			dos.writeBytes("Content-Type: application/octet-stream\r\n");
			dos.writeBytes(lineEnd);
			// FileInputStream fileInputStream = new FileInputStream(file);
			// create a buffer of maximum size
			bytesAvailable = stream.available();

			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			buffer = new byte[bufferSize];

			// read file and write it into form...
			bytesRead = stream.read(buffer, 0, bufferSize);

			while (bytesRead > 0) {

				dos.write(buffer, 0, bufferSize);
				bytesAvailable = stream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = stream.read(buffer, 0, bufferSize);

			}

			// send multipart form data necesssary after file data...
			dos.writeBytes(lineEnd);
			dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

			// Responses from the server (code and message)
			int serverResponseCode = conn.getResponseCode();

			if (serverResponseCode == 200) {

				BufferedReader in = new BufferedReader(new InputStreamReader(
						conn.getInputStream()));
				String json = "";

				String inputLine;

				while ((inputLine = in.readLine()) != null)
					json += (inputLine);
				in.close();
				HashMap<Long, String> result = new HashMap<Long, String>();
				JSONArray arr = new JSONArray(json);
				for (int i = 0; i < arr.length(); i++) {
					JSONObject jsonItem = (JSONObject) arr.get(i);
					Long id = Long.parseLong(jsonItem.get("id").toString());
					String file_name = jsonItem.get("file").toString();
					result.put(id, file_name);
				}
				if (cameraResult != null)
					cameraResult.setResult(result);

			}

			// close the streams //
			stream.close();
			dos.flush();
			dos.close();
		} catch (final Exception e) {
			context.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					ActivityHelper.showAlert(context, e);
				}
			});

		}
	}

	public static void addFile(final Activity context,
			final InputStream stream, final String fileName,
			final ICameraResult cameraResult) {

		ProcessExecutor.execute(new IProcess() {

			@Override
			public void execute() throws Exception {
				context.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						addFilePrivate(context, stream, fileName, cameraResult);
					}
				});

			}
		}, context);

	}

	public static final boolean isCameraResult(final Activity context,
			int requestCode, int resultCode) {
		return resultCode == Activity.RESULT_OK
				&& requestCode == CAMERA_PIC_REQUEST;

	}

	public static final boolean executeCameraResult(final Activity context,
			int requestCode, int resultCode, Intent data,
			final ICameraResult cameraResult) {
		if (isCameraResult(context, requestCode, resultCode)) {
			try {
				// the returned picture URI
				Uri pickedUri = data.getData();

				// declare the bitmap
				// declare the path string
				String imgPath = "";

				// retrieve the string using media data
				String[] medData = { MediaStore.Images.Media.DATA };
				// query the data

				Cursor picCursor = context.getContentResolver().query(
						pickedUri, medData, null, null, null);
				if (picCursor != null) {
					// get the path string
					int index = picCursor
							.getColumnIndexOrThrow(MediaColumns.DATA);
					picCursor.moveToFirst();
					imgPath = picCursor.getString(index);
				} else
					imgPath = pickedUri.getPath();

				// if and else handle both choosing from gallery and from file
				// manager

				// if we have a new URI attempt to decode the image bitmap
				if (pickedUri != null) {

					// set the width and height we want to use as maximum
					// display
					// int targetWidth = 600;
					// int targetHeight = 400;
					File file = new File(imgPath);

					Bitmap b = BitmapFactory.decodeFile(imgPath);
					int max_size = 1024;

					int origWidth = b.getWidth();
					int origHeight = b.getHeight();

					int newWidth = origWidth;
					int newHeight = origHeight;

					boolean width_greater = origWidth > origHeight;

					if (width_greater && origWidth > max_size) {
						newWidth = max_size;
						newHeight = origHeight / (origWidth / max_size);
					}

					if (!width_greater && newHeight > max_size) {
						newHeight = max_size;
						newWidth = origWidth / (origWidth / max_size);
					}
					Bitmap b2 = Bitmap.createScaledBitmap(b, newWidth,
							newHeight, false);
					ByteArrayOutputStream outStream = new ByteArrayOutputStream();
					b2.compress(Bitmap.CompressFormat.JPEG, 70, outStream);
					byte[] bt = outStream.toByteArray();
					outStream.close();
					addFile(context, new ByteArrayInputStream(bt),
							file.getName(), cameraResult);
				}
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return true;

		} else
			return false;
	}

	public static final void showFile(final int id, String fileName,
			final Activity context) {
		File tmpDir = new File(context.getString(DocFlowCommon.docflow_dir));
		if (!tmpDir.exists())
			tmpDir.mkdirs();
		tmpDir = new File(tmpDir, "tmp");
		if (!tmpDir.exists())
			tmpDir.mkdirs();
		final File file = new File(tmpDir, fileName);

		ProcessExecutor.execute(new IProcess() {

			@Override
			public void execute() throws Exception {
				try {
					FileOutputStream fos = new FileOutputStream(file);
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					String url = DrowableDownloader.getMainURL()
							+ "DocflowFileDownload.jsp?id=" + id;
					Utils.downlodCommonStream(url, out);
					out.flush();
					byte[] bt = out.toByteArray();
					int len = bt.length;
					System.out.println(len);
					fos.write(bt);
					out.close();
					fos.flush();
					fos.close();
					final Intent intent = new Intent();
					intent.setAction(android.content.Intent.ACTION_VIEW);
					MimeTypeMap mime = MimeTypeMap.getSingleton();
					String ext = file.getName().substring(
							file.getName().indexOf(".") + 1);
					ext = ext.toLowerCase(Locale.ENGLISH);
					String type = mime.getMimeTypeFromExtension(ext);

					intent.setDataAndType(Uri.fromFile(file), type);
					context.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							context.startActivity(intent);
						}
					});

				} catch (final Throwable e) {
					context.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							ActivityHelper.showAlert(context, e);
						}
					});
				}

			}
		}, context);

	}
}

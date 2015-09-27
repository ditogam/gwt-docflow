package com.socarmap.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.socarmap.R;
import com.socarmap.db.SQLiteConnection;
import com.socarmap.proxy.beans.ZXYData;

public class Utils {

	private static final int BUFFER_SIZE = 2048;

	public static byte[] compress(byte bt[]) throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		GZIPOutputStream gos = new GZIPOutputStream(os);
		gos.write(bt);
		gos.close();
		byte[] compressed = os.toByteArray();
		os.close();

		return compressed;
	}

	public static void copyZXYData(File file, final Activity context,
			final ProgressDialog progressDialog) throws Throwable {
		// SocardbTilesFile dd = SocardbTilesFile.instance;
		//
		// if (dd != null && dd.getHelper() != null
		// && dd.getHelper().getZoomConnection() != null) {
		//
		// final int size = dd.getHelper().getStepCount();
		// if (progressDialog != null) {
		// progressDialog.setMax(size);
		// progressDialog.setProgress(0);
		// }
		// Progress pr = new Progress() {
		// int current = 0;
		//
		// @Override
		// public void updateTitle(String title) {
		// setTitleAndProgress(title, current++, progressDialog,
		// context);
		// }
		//
		// @Override
		// public void stepIt() {
		// setTitleAndProgress(null, current, progressDialog, context);
		// }
		//
		// @Override
		// public void reset() {
		// current = 0;
		//
		// }
		// };
		// dd.getHelper().updateAll(file, pr);
		// }
		SQLiteConnection connectionHelper = null;
		SQLiteDatabase database = null;
		String sDir = context.getString(R.string.tiles_dir);
		File dir = new File(sDir);

		try {
			connectionHelper = new SQLiteConnection(context, file);
			database = connectionHelper.getReadableDatabase();
			Cursor mCount = database.rawQuery(
					"select count(*) from mapfiledatazxy", null);
			mCount.moveToFirst();
			int count = mCount.getInt(0);
			mCount.close();
			if (progressDialog != null) {
				progressDialog.setMax(count);
				progressDialog.setProgress(0);
			}
			Cursor cursor = database.query("mapfiledatazxy",
					"zoom,x,y".split(","), null, null, null, null, null);
			cursor.moveToFirst();
			int index = 0;

			while (!cursor.isAfterLast()) {

				ZXYData d = new ZXYData(cursor.getInt(0), cursor.getInt(1),
						cursor.getInt(2));
				setTitleAndProgress("Processing " + d, index, progressDialog,
						context);

				File tileFile = new File(dir, d.getZoom() + "");
				tileFile = new File(tileFile, d.getX() + "");
				tileFile = new File(tileFile, d.getY() + ".png.tile");
				if (tileFile.exists()) {
					setTitleAndProgress("Deleting " + d, index, progressDialog,
							context);
					tileFile.delete();
					// byte[] data = cursor.getBlob(3);
					// setTitleAndProgress("Saving " + d, index, progressDialog,
					// context);
					// saveData(tileFile, data);
				}
				setTitleAndProgress(null, ++index, progressDialog, context);
				cursor.moveToNext();
			}
			// Make sure to close the cursor
			cursor.close();
		} catch (Throwable e) {
			throw e;
		} finally {
			try {
				database.close();
			} catch (Throwable e2) {

			}
			try {
				connectionHelper.close();
			} catch (Throwable e2) {

			}
		}
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

		URL url = new URL(urlStr);
		int TIMEOUT_VALUE = 30000;
		// create the new connection
		HttpURLConnection urlConnection = (HttpURLConnection) url
				.openConnection();

		// set up some things on the connection
		urlConnection.setRequestMethod("GET");
		urlConnection.setDoOutput(true);
		urlConnection.setConnectTimeout(TIMEOUT_VALUE);
		urlConnection.setReadTimeout(TIMEOUT_VALUE);

		// and connect!
		urlConnection.connect();
		if (fileName != null) {
			String disposition = urlConnection
					.getHeaderField("Content-Disposition");
			if (disposition != null) {
				String fn = "filename=";
				int index = disposition.indexOf(fn);
				if (index > 0) {
					index += fn.length();
					fn = disposition.substring(index).trim();
					fileName.append(fn);
				}
			}
		}
		// set the path where we want to save the file
		// in this case, going to save it on the root directory of the
		// sd card.

		// create a new file, specifying the path, and the filename
		// which we want to save the file as.

		// this will be used to write the downloaded data into the file we
		// created

		// this will be used in reading the data from the internet
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
			downloadedSize += bufferLength;
			// this is where you would do something to report the prgress,
			// like this maybe
			if (act != null)
				setProcess(mDownloadStatusMessageView, formattext, size,
						mDownloadProgressView, downloadedSize, progressDialog,
						act);

		}
	}

	public static void proceedZXYData(ArrayList<ZXYData> list, Context context) {
		// String sDir = context.getString(R.string.tiles_dir);
		// File dir = new File(sDir);
		// SocardbTilesFile dd = SocardbTilesFile.instance;
		// for (ZXYData d : list) {
		// File tileFile = new File(dir, d.getZoom() + "");
		// tileFile = new File(tileFile, d.getX() + "");
		// tileFile = new File(tileFile, d.getY() + ".png.tile");
		// tileFile.delete();
		// byte[] data = d.getData();
		// if (data == null && d.getUnique_id() != null) {
		// try {
		// data = ConnectionHelper.getConnection().getData(
		// d.getUnique_id());
		// } catch (Throwable e) {
		// n
		// }
		//
		// }
		// if (data != null) {
		// saveData(tileFile, d.getData());
		// if (dd != null && dd.getHelper() != null
		// && dd.getHelper().getZoomConnection() != null) {
		// ArrayList<ZX1X2> zz = dd.getHelper().findZooms(d.getZoom(),
		// d.getX(), d.getY());
		// if (zz != null) {
		// for (ZX1X2 zx1x2 : zz) {
		// try {
		// dd.getHelper()
		// .getZoomConnection()
		// .updateImageData(zx1x2, d.getZoom(),
		// d.getX(), d.getY(), data);
		// } catch (Throwable e) {
		// e.printStackTrace();
		// }
		// }
		// }
		// }
		// }
		//
		// }

	}

	public static void saveData(File fl, byte[] data) {
		FileOutputStream fos = null;
		try {
			File dir = fl.getParentFile();
			if (!dir.exists())
				return;
			fos = new FileOutputStream(fl);
			data = Utils.decompress(data);
			fos.write(data);
			fos.flush();
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			try {
				fos.close();
			} catch (Throwable e2) {
			}
		}
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

	private static void setTitleAndProgress(final String title,
			final Integer progress, final ProgressDialog progressDialog,
			Activity act) {

		if (act != null) {
			act.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					if (progressDialog != null && title != null) {
						progressDialog.setMessage(title);
					}
					if (progressDialog != null && progress != null)
						progressDialog.setProgress(progress);

				}
			});

		}
	}

	public static void streamCopy(InputStream in, OutputStream out)
			throws IOException {
		byte[] b = new byte[BUFFER_SIZE];
		int read;
		while ((read = in.read(b)) != -1) {
			out.write(b, 0, read);
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

}

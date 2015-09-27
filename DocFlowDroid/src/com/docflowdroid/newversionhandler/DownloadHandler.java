package com.docflowdroid.newversionhandler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicBoolean;

import com.docflowdroid.helper.DrowableDownloader;
import com.docflowdroid.helper.Utils;

// Referenced classes of package com.Updater.android:
//            Updater, HashHandler, ExceptionHandler

class DownloadHandler {

	DownloadHandler() {
	}

	public static File tmp_Apk = null;

	public static void init(File tmp_Apk) {
		DownloadHandler.tmp_Apk = tmp_Apk;
	}

	protected static boolean download(int app_id, boolean check, String device) {
		if (!downloading.getAndSet(true) || !check) {
			String url_check = check ? "get_version_number.jsp"
					: "get_new_version.jsp";
			StringBuilder url = new StringBuilder();
			url.append((new StringBuilder(DrowableDownloader.getMainURL())
					.append(url_check + "?").toString()));
			url.append((new StringBuilder("app_id=")).append(app_id).toString());
			url.append((new StringBuilder("&device=")).append(device)
					.toString());
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			try {
				Utils.downlodStream(url.toString(), bos);
				byte[] bt = bos.toByteArray();

				if (check) {
					final String msg = new String(bt).trim();
					ProgramUpdater.newHash = msg;
					System.err.println("msg=" + msg);
					System.err.println("ProgramUpdater.myHash="
							+ ProgramUpdater.myHash);
					if (!msg.equals(ProgramUpdater.myHash)) {
						try {
							return download(app_id, false, device);
						} finally {
							downloading.set(false);

						}
					}
				} else {
					ByteArrayInputStream bais = new ByteArrayInputStream(bt);
					writeFile(bais, new FileOutputStream(tmp_Apk));
					return true;
				}
				bos.close();

			} catch (Throwable e) {
				e.printStackTrace();

			}
		}
		return false;
	}

	private static void writeFile(InputStream is, OutputStream os)
			throws Throwable {
		int read = 0;
		byte bytes[] = new byte[1024];
		while ((read = is.read(bytes)) != -1)
			os.write(bytes, 0, read);
		is.close();
		os.close();
	}

	private static AtomicBoolean downloading = new AtomicBoolean();;
	protected static boolean isStarted;

}

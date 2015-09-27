package com.socarmap.updater;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.security.auth.x500.X500Principal;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.socarmap.R;
import com.socarmap.ui.process.IProcess;
import com.socarmap.ui.process.ProcessExecutor;

public class ProgramUpdater {
	public static String myHash = null;
	public static String newHash = null;
	static Context ctx;
	public static int program_id;
	public static String device;
	static Observer observer;
	static MyObservable myObservable;

	private static String md5(byte file[]) throws Throwable {
		MessageDigest digest = MessageDigest.getInstance("MD5");
		byte md5sum[] = digest.digest(file);
		BigInteger bigInt = new BigInteger(1, md5sum);
		String newHash;
		for (newHash = bigInt.toString(16); newHash.length() < 32; newHash = "0"
				+ newHash.toString())
			;
		return newHash;
	}

	public static void setCurrentActivity(Activity activity) {
		if (activity == null)
			return;
		ctx = activity;
	}

	private static String getLastInstalledHash(String file_name) {
		FileInputStream fis = null;
		{
			String s;
			try {

				File file = new File(file_name);
				byte fileBytes[] = new byte[(int) file.length()];
				fis = new FileInputStream(file);
				fis.read(fileBytes);
				s = md5(fileBytes);
			} catch (Throwable t) {
				throw new RuntimeException(t);
			}
			try {
				if (fis != null)
					fis.close();
			} catch (IOException ioexception) {
			}
			return s;
		}

	}

	private static ApplicationInfo getApplicationInfo(String packageName,
			Context ctx) {
		List<ApplicationInfo> applicationInfos = ctx.getPackageManager()
				.getInstalledApplications(128);
		if (applicationInfos != null) {
			for (ApplicationInfo applicationInfo : applicationInfos) {
				if (applicationInfo.packageName.equals(packageName))
					return applicationInfo;
			}

		}
		return null;
	}

	private static AtomicBoolean initialized = new AtomicBoolean(false);
	public static boolean debugable;
	private static final X500Principal DEBUG_DN = new X500Principal(
			"CN=Android Debug,O=Android,C=US");

	public static boolean isDebuggable(Context ctx) {
		boolean debuggable = false;

		try {
			PackageInfo pinfo = ctx.getPackageManager().getPackageInfo(
					ctx.getPackageName(), PackageManager.GET_SIGNATURES);
			Signature signatures[] = pinfo.signatures;

			CertificateFactory cf = CertificateFactory.getInstance("X.509");

			for (int i = 0; i < signatures.length; i++) {
				ByteArrayInputStream stream = new ByteArrayInputStream(
						signatures[i].toByteArray());
				X509Certificate cert = (X509Certificate) cf
						.generateCertificate(stream);
				debuggable = cert.getSubjectX500Principal().equals(DEBUG_DN);
				if (debuggable)
					break;
			}
		} catch (NameNotFoundException e) {
			// debuggable variable will remain false
		} catch (CertificateException e) {
			// debuggable variable will remain false
		}
		return debuggable;
	}

	private static ThreadPolicy oldThreadPolicy;

	protected static void setPermissiveThreadPolicy() {
		// set StrictMode to allow network/disk in service
		oldThreadPolicy = StrictMode.getThreadPolicy();
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder(
				oldThreadPolicy).permitNetwork().permitDiskReads()
				.permitDiskWrites().build());
	}

	protected static void resetThreadPolicy() {
		if (oldThreadPolicy != null) {
			// reset to old policy
			StrictMode.setThreadPolicy(oldThreadPolicy);
		}
	}

	public static void init(Context ctx, int program_id, String device,
			Observer observer) {

		String app_info = null;
		ProgramUpdater.debugable = isDebuggable(ctx);
		if (debugable)
			return;
		try {

			DownloadHandler.init(new File(new File(ctx
					.getString(R.string.socar_db)).getParent(), "tmp.apk"));
			setPermissiveThreadPolicy();
			ApplicationInfo applicationInfo = getApplicationInfo(
					ctx.getPackageName(), ctx);
			if (applicationInfo != null) {
				myHash = getLastInstalledHash(applicationInfo.sourceDir);
				app_info = applicationInfo.sourceDir;
			} else
				return;
		} catch (Exception e) {
		} finally {
			resetThreadPolicy();
		}

		Toast.makeText(ctx, "My hash is " + myHash + "\napp_info" + app_info,
				Toast.LENGTH_LONG).show();
		ProgramUpdater.ctx = ctx;
		ProgramUpdater.program_id = program_id;
		ProgramUpdater.device = device;
		ProgramUpdater.observer = observer;

		if (observer != null) {
			myObservable = new MyObservable();
			myObservable.addObserver(observer);
		}
		notifyHandler = new Handler() {
			public void handleMessage(Message msg) {
				try {
					notifyMsg(msg);
				} catch (Throwable t) {
					Log.e("PUSHLINK", "", t);
				}
			}

		};
		initialized.set(true);

	}

	private static void notifyMsg(final Message msg) {
		android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(
				ctx);

		final Intent intent = (Intent) msg.obj;
		Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
		final MediaPlayer mp = MediaPlayer.create(ctx, uri);

		if (mp != null)
			mp.start();

		LayoutInflater inflater = LayoutInflater.from(ctx);
		final View convertView = inflater.inflate(R.layout.new_version_view,
				null);
		setText(convertView, R.id.tv_new_version_avalable,
				ctx.getString(R.string.new_version_avalable));
		((Button) convertView.findViewById(R.id.btn_updt))
				.setOnClickListener(new OnClickListener() {
					public void onClick(View arg0) {
						ctx.startActivity(intent);
						android.os.Process.killProcess(android.os.Process
								.myPid());
					}
				});
		builder.setCancelable(debugable);
		builder.setView(convertView);
		builder.setOnCancelListener(new OnCancelListener() {

			public void onCancel(DialogInterface dialog) {
				mp.stop();

			}
		});
		alertDialog = builder.create();
		alertDialog.show();

		try {
			Thread.sleep(1000);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void setText(View convertView, int id, String text) {
		TextView textView = (TextView) convertView.findViewById(id);
		if (textView != null)
			textView.setText(text);
	}

	static Handler notifyHandler;

	public static void checkNewVersion(final Activity act,
			final IProcess process) {
		if (debugable || !initialized.get())
			return;

		// if (process == null) {
		// AlertDialog alertDialog = new AlertDialog.Builder(act).create();
		// alertDialog.setTitle(act.getString(R.string.update));
		// alertDialog.setMessage(act.getString(R.string.check_new_version));
		// alertDialog.setCancelable(false);
		// alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,
		// act.getString(R.string.update_now),
		// new DialogInterface.OnClickListener() {
		// public void onClick(DialogInterface dialog, int which) {
		// act.finish();
		// android.os.Process.killProcess(android.os.Process
		// .myPid());
		// }
		// });
		// alertDialog.show();
		// return;
		// }
		setCurrentActivity(act);
		privateCheck(act, process);
	}

	public static void privateCheck(final Activity act, final IProcess process) {
		ProcessExecutor.execute(new IProcess() {
			public void execute() throws Throwable {
				System.out.println();
				if (DownloadHandler.download(act, program_id, true, device)) {
					notifyUser();
				} else if (process != null) {
					process.execute();
				}
			}
		}, act);
	}

	private static class MyObservable extends Observable {
		public void setMsg(String msg) {
			setChanged();
			notifyObservers(msg);
		}
	}

	public static void newMessage(String msg) {
		if (observer == null && myObservable == null)
			return;
		myObservable.setMsg(msg);
	}

	public static void downloadAndNotify() {
		if (DownloadHandler.download(ctx,program_id, true, device)) {
			notifyUser();
		}
	}

	private static void notifyUser() {
		// installShortcut();
		Intent installIntent = new Intent("android.intent.action.VIEW");
		java.io.File apkFile = DownloadHandler.tmp_Apk;
		installIntent.setDataAndType(Uri.fromFile(apkFile),
				"application/vnd.android.package-archive");
		installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Message msg = Message.obtain();
		msg.obj = installIntent;
		notifyHandler.sendMessage(msg);
	}

	private static String popUpMessage;
	private static String updateButton;
	private static AlertDialog alertDialog;

	public String getPopUpMessage() {
		return popUpMessage;
	}

	public static void setPopUpMessage(String popUpMessage) {
		ProgramUpdater.popUpMessage = popUpMessage;
	}

	public static String getUpdateButton() {
		return updateButton;
	}

	public static void setUpdateButton(String updateButton) {
		ProgramUpdater.updateButton = updateButton;
	}
}

package com.socarmap.helper;

import java.io.ByteArrayInputStream;
import java.net.URLEncoder;

import no.tornado.brap.client.ServiceProxyFactory;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.StrictMode;
import android.widget.Toast;

import com.googlecode.xremoting.core.XRemotingProxyFactory;
import com.socarmap.R;
import com.socarmap.db.DBSettingsLoader;
import com.socarmap.helper.ConnectivityReceiver.OnNetworkAvailableListener;
import com.socarmap.proxy.IConnection;
import com.socarmap.proxy.beans.UserContext;

public class ConnectionHelper {
	private static IConnection conn = null;
	public static UserContext userContext = null;
	public static Settings settings;
	public static String mUserName;
	public static String mPassword;
	public static Activity activity;
	private static ConnectivityReceiver receiver;

	private static Handler customHandler = new Handler();

	private static long DELAY = 10000;

	private static Runnable updateTimerThread = new Runnable() {

		@Override
		public void run() {
			try {
				if (conn != null) {
					conn.ping();
					synchronized (conn) {
						new UploadProcessor(DBSettingsLoader.getInstance())
								.upload();
					}
				}
			} catch (Exception e) {
				try {
					conn = null;
					getConnection();
				} catch (Throwable e1) {

				}
			}
			customHandler.postDelayed(this, DELAY);
		}
	};
	private static String lastUrl;

	public static IConnection getConnection() throws Throwable {
		customHandler.removeCallbacks(updateTimerThread);

		try {
			int connType = getConnectionType();
			if (connType == R.drawable.offline)
				return null;
			if (conn == null) {
				StrictMode
						.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
								.permitAll().build());

				ServiceProxyFactory.streamBufferSize = 1024;
				String first_conn = connType == R.drawable.mobile_phone ? settings
						.getUrl() : settings.getSecondary_url();
				String second_conn = connType != R.drawable.mobile_phone ? settings
						.getUrl() : settings.getSecondary_url();
				String url = first_conn;
				try {
					conn = tryConnect(url);
					showStatus(R.string.going_online);
				} catch (Throwable e) {
					url = second_conn;
					conn = tryConnect(url);
					showStatus(R.string.going_online);
				}

			} else {
				// conn.ping();
			}
		} catch (Throwable e) {
			if (conn != null)
				showStatus(R.string.going_offline);
			conn = null;
		}
		try {

			if (userContext == null && mUserName != null)
				userContext = conn.loginUser(mUserName, mPassword, 1, 1);
		} catch (Throwable e) {
			if (e.getMessage() != null
					&& e.getMessage().equals(
							UserContext.INVALID_USER_NAME_AND_PWD))
				throw e;
		}
		customHandler.postDelayed(updateTimerThread, DELAY);
		return conn;
	}

	public static int getConnectionType() {
		boolean haveConnectedWifi = false;
		boolean haveConnectedMobile = false;

		ConnectivityManager cm = (ConnectivityManager) activity
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] netInfo = cm.getAllNetworkInfo();
		for (NetworkInfo ni : netInfo) {
			if (ni.getTypeName().equalsIgnoreCase("WIFI"))
				if (ni.isConnected())
					haveConnectedWifi = true;
			if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
				if (ni.isConnected())
					haveConnectedMobile = true;
		}
		if (haveConnectedWifi)
			return R.drawable.wireless;
		if (haveConnectedMobile)
			return R.drawable.mobile_phone;
		return R.drawable.offline;
	}

	public static String getLastUrl() {
		return lastUrl;
	}

	public static ConnectivityReceiver init(Activity activity) {
		ConnectionHelper.activity = activity;
		ProtectedConfig.disconnect();
		receiver = new ConnectivityReceiver(activity);
		receiver.setOnNetworkAvailableListener(new OnNetworkAvailableListener() {
			@Override
			public void onNetworkAvailable() {
				ProtectedConfig.connectSSH();

			}

			@Override
			public void onNetworkUnavailable() {
				ProtectedConfig.disconnect();
			}
		});
		receiver.bind(activity);
		ProtectedConfig.connectSSH();
		try {
			settings = Settings.getInstance(activity);
		} catch (Exception e) {
		}
		try {
			getConnection();
		} catch (Throwable e1) {
			e1.printStackTrace();
		}
		return receiver;
	}

	private static void showStatus(final int text_id) {
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(activity, activity.getString(text_id),
						Toast.LENGTH_SHORT).show();

			}
		});

	}

	private static IConnection tryConnect(String url) throws Exception {
		lastUrl = url;
		String full_url = url += "XSocarConnectorService";
		HttpParams params = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(params, 1000);
		HttpConnectionParams.setSoTimeout(params, 1000);
		HttpConnectionParams.setSocketBufferSize(params, 2048 * 2);
		params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
				HttpVersion.HTTP_1_1);
		HttpClient cl = new DefaultHttpClient(params);

		CommonsHttpClientRequester req = new CommonsHttpClientRequester(cl,
				full_url);
		XRemotingProxyFactory factory = new XRemotingProxyFactory(req);
		conn = (IConnection) factory.create(IConnection.class);
		conn.ping();

		return conn;
	}

	public static void uploadFile(String uId, byte[] data) throws Throwable {
		HttpClient httpClient = new DefaultHttpClient();
		String url = lastUrl;
		if (!url.endsWith("/"))
			url += "/";
		url = url + "upload_data.jsp?uID=" + URLEncoder.encode(uId);
		HttpPost httpPost = new HttpPost(url);
		ByteArrayInputStream bis = new ByteArrayInputStream(data);
		MultipartEntity multipartEntity = new MultipartEntity(
				HttpMultipartMode.BROWSER_COMPATIBLE);

		multipartEntity.addPart("Image", new InputStreamBody(bis, uId));
		httpPost.setEntity(multipartEntity);

		httpClient.execute(httpPost);
	}
}

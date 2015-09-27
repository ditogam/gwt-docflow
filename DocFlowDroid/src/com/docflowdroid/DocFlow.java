package com.docflowdroid;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.TrafficStats;
import android.os.StrictMode;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.view.WindowManager;

import com.common.shared.ClSelectionItem;
import com.common.shared.ds.CDSRequest;
import com.common.shared.ds.CDSResponce;
import com.docflow.and.impl.AndroidDocFlowServiceImpl;
import com.docflow.and.impl.MDBConnection;
import com.docflow.client.DocFlowService;
import com.docflow.shared.DocFlowSerializer;
import com.docflow.shared.StatusObject;
import com.docflow.shared.UserObject;
import com.docflow.shared.docflow.DocType;
import com.docflowdroid.common.MapObjectProcessorContainer;
import com.docflowdroid.conf.Config;
import com.docflowdroid.traffic.NetTraffic;
import com.googlecode.xremoting.core.XRemotingProxyFactory;

import dalvik.system.DexClassLoader;

public class DocFlow extends DocFlowCommon {
	public static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd", Locale.US);
	public static SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd hh:mm:ss", Locale.US);
	public static String mUserName;
	public static String mPassword;

	public static final int PROGRAM_ID = 2;

	public static MapObjectProcessorContainer mapObjectProcessor;
	private static final HashMap<Integer, Integer> status_colors = new HashMap<Integer, Integer>();

	public static String getCaption(int fieldCaptionId, String title) {
		return title;
	}

	public static int getStatusColor(int status) {
		Integer c = status_colors.get(status);
		if (c == null)
			return Color.BLACK;
		return c;
	}

	static {
		init(R.string.login_progress_signing_in, R.id.errDetail,
				R.id.errMessage, R.string.error, R.string.error_locate_failed,
				R.layout.error_detail, R.id.tbDetail, R.string.docflow_dir);
	}

	public static long currenttime = System.currentTimeMillis();

	public static int language_id;
	public static int system;
	public static Class<?> calculator = null;
	public static ArrayList<ClSelectionItem> doc_types;
	public static String device_id;
	public static Long session_id;
	public static int subregion_id;
	public static String android_check_system_ids;
	public static int user_id = 0;

	public static Date getCurrentDate() {
		return new Date(System.currentTimeMillis());
	}

	public static void init(String url, Activity act, int language_id,
			int system, MapObjectProcessorContainer _mapObjectProcessor)
			throws Throwable {
		android_check_system_ids = null;
		session_id = null;
		setActivity(act);
		TelephonyManager tManager = (TelephonyManager) act
				.getSystemService(Context.TELEPHONY_SERVICE);
		device_id = tManager.getDeviceId();
		DocFlow.activity = act;

		String full_url = url;
		mapObjectProcessor = _mapObjectProcessor;

		// DocFlowService conn = new AndroidDocFlowServiceImpl();//
		// init_connection(full_url);
		DocFlowService conn = init_connection(full_url, mWorkingOffline);

		if (mWorkingOffline) {
			MDBConnection.reloadDataSources();
			MDBConnection.reloadsql_custom_functions();
			Map<String, Object> criteria = new HashMap<String, Object>();
			criteria.put("metserial", "25106405");
			CDSRequest req = new CDSRequest("DBCDS_COMMON", criteria,
					"getDublicatedMetters");
			CDSResponce responce = conn.dsFetchData(req.getDsName(), criteria,
					req);
			System.out.println(responce.getResult());
		}

		DocFlow.docFlowService = conn;

		UserObject uo = conn.loginUser(mUserName, mPassword, -1 * language_id,
				system);
		DocFlow.user_obj = uo;
		DocFlow.language_id = language_id;
		DocFlow.system = system;
		DocFlow.user_id = uo.getUser().getUser_id();
		HashMap<Integer, ArrayList<ClSelectionItem>> statuses = uo
				.getStatusTree();
		for (Integer k : statuses.keySet()) {
			ArrayList<ClSelectionItem> items = statuses.get(k);
			for (ClSelectionItem item : items) {
				status_colors.put((int) item.getId(),
						item.getAdditional_value());
			}
		}

		HashMap<Integer, StatusObject> statusObjectTree = uo
				.getStatusObjectTree();

		Set<Integer> _system_ids = statusObjectTree.keySet();
		for (Integer _system_id : _system_ids) {
			StatusObject so = statusObjectTree.get(_system_id);
			if (so.isCheck_for_statuses()) {
				android_check_system_ids = android_check_system_ids == null ? ""
						: android_check_system_ids;
				android_check_system_ids += (android_check_system_ids.isEmpty() ? ""
						: ",")
						+ _system_id;
			}

		}
		subregion_id = uo.getUser().getSubregionid();
		Config c = Config.getInstance();
		c.setLanguageid(language_id);
		c.setSystemid(system);
		c.setUsername(mUserName);
		Config.save();
		createCalculatorClass();
		// calculator=SimpleCalculatorImpl.class;
		// createCalculatorClass(act);
	}

	public static void createCalculatorClass() {
		try {

			final String DEX_FILE_NAME_MYCLASSES = "myclasses.dex";
			final File dexFile = new File(activity.getFilesDir(),
					DEX_FILE_NAME_MYCLASSES);
			ArrayList<String> methodes = user_obj.getMethodes();

			String class_def = methodes.get(0).trim();

			byte[] bt = Base64.decode(class_def.trim().getBytes("UTF8"),
					Base64.DEFAULT);
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(dexFile);
				fos.write(bt);
			} finally {
				try {
					fos.close();
				} catch (Exception e) {
					// TODO: handle exception
				}
			}

			final DexClassLoader dcl = new DexClassLoader(
					dexFile.getAbsolutePath(), activity.getCacheDir()
							.getAbsolutePath(),
					activity.getApplicationInfo().nativeLibraryDir,
					activity.getClassLoader());
			calculator = dcl
					.loadClass("com.docflowdroid.calculatorimpl.SimpleCalculatorImpl");
			System.out.println(calculator);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public static DocFlowService init_connection(String full_url,
			boolean offline) throws Throwable {
		DocFlowService conn = null;
		if (offline) {
			conn = new AndroidDocFlowServiceImpl();
		} else {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
					.permitAll().build());
			HttpParams params = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(params, 10000);
			HttpConnectionParams.setSoTimeout(params, 10000);
			HttpConnectionParams.setSocketBufferSize(params, 2048 * 2);
			params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
					HttpVersion.HTTP_1_1);
			HttpClient cl = new DefaultHttpClient(params);

			CommonsHttpClientRequester req = new CommonsHttpClientRequester(cl,
					full_url);
			DocFlowSerializer ser = new DocFlowSerializer();
			XRemotingProxyFactory factory = new XRemotingProxyFactory(req, ser);
			conn = (DocFlowService) factory.create(DocFlowService.class);
		}
		conn.ping();
		return conn;
	}

	public static void createDocTypeTree() throws Exception {
		doc_types = new ArrayList<ClSelectionItem>();
		HashMap<Integer, ArrayList<DocType>> system_docTypes = user_obj
				.getSystem_docTypes();

		ArrayList<DocType> docTypes = system_docTypes == null
				|| !system_docTypes.containsKey(system) ?

		docFlowService.getDocTypes(DocFlow.language_id, user_obj.getUser()
				.getUser_id(), DocFlow.system) : system_docTypes.get(system);

		int group_id = -1;
		for (DocType docType : docTypes) {

			if (docType.getGroup_id() != group_id) {
				group_id = docType.getGroup_id();
				ClSelectionItem sel = new ClSelectionItem();
				sel.setId(-group_id);
				sel.setValue(docType.getDoctypegroupvalue());
				doc_types.add(sel);
			}
			ClSelectionItem sel = new ClSelectionItem();
			sel.setId(docType.getId());
			sel.setValue("\t\t" + docType.getDoctypevalue());
			sel.setAdditional_value(docType.isApplied_customer() ? 1 : 0);
			doc_types.add(sel);

		}
	}

	public static NetTraffic getTraffic(boolean recreateUsage) {
		NetTraffic net = new NetTraffic();

		int uid = android.os.Process.myUid();

		net.setRecieved(TrafficStats.getTotalRxBytes());
		net.setRecievedApp(TrafficStats.getUidRxBytes(uid));
		net.setRecievedMobile(TrafficStats.getMobileRxBytes());

		uid = android.os.Process.myUid();
		net.setSent(TrafficStats.getTotalTxBytes());
		net.setSentApp(TrafficStats.getUidTxBytes(uid));
		net.setSentMobile(TrafficStats.getMobileTxBytes());

		return net;
	}

	public static void setActivityLandscape(Activity a) {
		a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		a.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		a.getWindow().clearFlags(
				WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
	}

}

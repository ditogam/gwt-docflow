package com.docflowdroid;

import android.app.Activity;

import com.docflow.client.DocFlowService;
import com.docflow.shared.UserObject;

public class DocFlowCommon {
	public static int login_progress_signing_in;
	public static int errDetail;
	public static int errMessage;
	public static int error;
	public static int error_locate_failed;
	public static int error_detail;
	public static int tbDetail;
	public static int docflow_dir;
	public static DocFlowService docFlowService;
	public static boolean mWorkingOffline;

	public static Activity activity;
	public static UserObject user_obj;

	public static boolean hasPermition(String permitionname) {
		return user_obj.getUser().getPermitionNames().contains(permitionname);
	}

	public static boolean hasPermition(int permition_id) {
		return user_obj.getUser().getPermitionIds().contains(permition_id);
	}

	public static void init(int login_progress_signing_in, int errDetail,
			int errMessage, int error, int error_locate_failed,
			int error_detail, int tbDetail, int docflow_dir) {
		DocFlowCommon.login_progress_signing_in = login_progress_signing_in;
		DocFlowCommon.errMessage = errMessage;
		DocFlowCommon.error = error;
		DocFlowCommon.errMessage = errMessage;
		DocFlowCommon.error_locate_failed = error_locate_failed;
		DocFlowCommon.error_detail = error_detail;
		DocFlowCommon.tbDetail = tbDetail;
		DocFlowCommon.errDetail = errDetail;
		DocFlowCommon.docflow_dir = docflow_dir;
	}

	public static void setActivity(Activity activity) {
		DocFlowCommon.activity = activity;
	}

	public static final int getResourceId(String res_name) {
		try {
			String pname = activity.getApplication().getPackageName();
			String res_str = res_name.substring(0, res_name.lastIndexOf("."));
			String res_id = res_name.substring(res_name.lastIndexOf(".") + 1);
			String class_name = pname + ".R$" + res_str;

			Object obj = Class.forName(class_name).getDeclaredField(res_id)
					.get(null);
			return Integer.parseInt(obj.toString());
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return 0;
	}

}

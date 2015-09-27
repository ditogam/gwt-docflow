package com.docflow.and.impl;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

import com.common.shared.ClSelectionItem;
import com.common.shared.usermanager.TransfarableUser;
import com.common.shared.usermanager.User;
import com.docflow.and.impl.db.DBConnectionAnd;
import com.docflow.shared.ClSelection;
import com.docflow.shared.PermissionSystemMap;
import com.docflow.shared.SCSystem;
import com.docflow.shared.StatusObject;
import com.docflow.shared.UserObject;
import com.docflow.shared.docflow.DocType;
import com.docflowdroid.DocFlow;
import com.docflowdroid.R;

public class LoginManager {
	public static UserObject login(String userName, String password,
			int language_id, int system) throws Exception {
		UserObject uo = null;
		Connection conn = null;
		language_id = Math.abs(language_id);
		try {
			conn = DBConnectionAnd.getExportedDB();
			User user = MDBConnection.getUser(userName, password, conn);
			TransfarableUser ts = MDBConnection.getSUser(user, conn);
			try {
				PermissionSystemMap.hasPermition(system, ts);
			} catch (Exception e) {
				throw new Exception(
						DocFlow.activity.getString(R.string.system_perm));
			}
			uo = new UserObject();
			uo.setUser(ts);
			uo.setCaptions(MDBConnection.getClSelectionItems(conn,
					ClSelection.T_CAPTIONS, (long) language_id));
			uo.setServerTime(System.currentTimeMillis());

			int[] status_systems = system == SCSystem.S_CC_AND_ECCIDENT ? new int[] {
					SCSystem.S_CALL_CENTER, SCSystem.S_ECCIDENT_CONTROLL }
					: new int[] { system };
			HashMap<Integer, ArrayList<ClSelectionItem>> statusTree = new HashMap<Integer, ArrayList<ClSelectionItem>>();
			HashMap<Integer, StatusObject> statusObjectTree = new HashMap<Integer, StatusObject>();
			HashMap<Integer, ArrayList<DocType>> system_docTypes = new HashMap<Integer, ArrayList<DocType>>();
			for (int i = 0; i < status_systems.length; i++) {
				try {
					PermissionSystemMap.hasPermition(status_systems[i], ts);
					if (uo.getInitial_system() == null)
						uo.setInitial_system(status_systems[i]);
					statusTree.put(status_systems[i], MDBConnection
							.getStatuses(conn, language_id, status_systems[i]));

					statusObjectTree.put(status_systems[i], MDBConnection
							.getStatusParams(conn, status_systems[i]));
					system_docTypes.put(
							status_systems[i],
							MDBConnection.getDocTypes(language_id,
									ts.getUser_id(), status_systems[i], conn));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			uo.setStatusTree(statusTree);
			uo.setStatusObjectTree(statusObjectTree);
			uo.setSystem_docTypes(system_docTypes);
			MDBConnection.setUserObjectConfig(conn, uo);
		} finally {
			DBConnectionAnd.closeAll(conn);
		}
		return uo;
	}
}

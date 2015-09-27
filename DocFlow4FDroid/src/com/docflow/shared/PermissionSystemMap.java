package com.docflow.shared;

import java.util.Map;
import java.util.TreeMap;

import com.common.shared.usermanager.TransfarableUser;

public class PermissionSystemMap {
	public static final Map<Integer, String> permitionMap = new TreeMap<Integer, String>();

	public static void hasPermition(int system, TransfarableUser user)
			throws Exception {
		if (system == SCSystem.S_PLOMBS)
			return;

		if (system == SCSystem.S_CC_AND_ECCIDENT) {
			if (!(user.getPermitionNames().contains(
					permitionMap.get(SCSystem.S_CALL_CENTER)) || user
					.getPermitionNames().contains(
							permitionMap.get(SCSystem.S_ECCIDENT_CONTROLL))))
				throw new Exception("You cannot access to this system!!!!");
			return;
		}

		if (!user.getPermitionNames().contains(permitionMap.get(system)))
			throw new Exception("You cannot access to this system!!!!");

	}

	public static boolean hasPermition(String permitionName,
			TransfarableUser user) throws Exception {
		return user.getPermitionNames().contains(permitionName);

	}

	static {
		permitionMap.put(SCSystem.S_CALL_CENTER, PermissionNames.CALL_CENTER);
		permitionMap.put(SCSystem.S_DOCFLOW, PermissionNames.DOCFLOW);
		permitionMap.put(SCSystem.S_ECCIDENT_CONTROLL,
				PermissionNames.ECCIDENT_CONTROLL);

		permitionMap.put(SCSystem.S_HR, PermissionNames.HR);
		permitionMap.put(SCSystem.S_MAP, PermissionNames.MAP);
		permitionMap.put(SCSystem.S_USER_MANAGER,
				PermissionNames.CAN_VIEW_USRMANAGER);
		permitionMap.put(SCSystem.S_CORECTOR, PermissionNames.CORECTOR);
		permitionMap.put(SCSystem.S_GASMONITOR, PermissionNames.GASMONITOR);

	}
}

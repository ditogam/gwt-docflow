package com.workflow.server.dmi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;
import com.workflow.server.tags.LoadProjectJavaScriptsTag;
import com.workflow.server.utils.DMIUtils;

public class SystemsDMI {

	private static String concatString(List<String> list) {
		String result = "";
		for (String str : list) {
			if (!result.isEmpty())
				result += ",";
			result += "" + str + "";
		}
		return result;
	}

	public static DSResponse selectModulePath(DSRequest dsRequest)
			throws Exception {

		int id = Integer.parseInt(dsRequest.getCriteriaValue("id").toString());
		Map<?, ?> map = DMIUtils.findRecordById("SystemDS", null, id, "id");
		Map<String, Object> result = new TreeMap<String, Object>();
		result.put("id", id);
		result.put("panel_function", map.get("panel_function"));
		Object load_modules = map.get("load_modules");
		if (!(load_modules == null || load_modules.toString().length() < 2)) {
			ArrayList<String> jsFiles = new ArrayList<String>();
			ArrayList<String> cssFiles = new ArrayList<String>();
			for (String module : load_modules.toString().split(",")) {
				ArrayList<String> files = LoadProjectJavaScriptsTag
						.travelByDir(dsRequest.getServletContext(), module);
				for (String file : files) {
					if (file.toLowerCase().endsWith(".css"))
						cssFiles.add(file);
					else
						jsFiles.add(file);
				}
			}
			String strJs = concatString(jsFiles);
			String strcss = concatString(cssFiles);
			result.put("jsmodules", strJs);
			result.put("cssmodules", strcss);
		}
		DSResponse resp = new DSResponse(result);
		resp.setDropExtraFields(false);
		return resp;
	}
}

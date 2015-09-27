package com.docflow.server.db;

import java.util.ArrayList;

import com.docflow.shared.docflow.DocType;

public class DocTypeTree {
	public static String getDocTypeTreeXML(int language_id, int user_id, int system_id) {
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
		if (language_id <= 0)
			return xml;
		xml = "";
		StringBuilder sb = new StringBuilder(xml + "<DocTypeGroup>");

		try {
			int group_id = -1;
			// sb.append("<DocType><TypeId>0</TypeId><GroupId>-500000033</GroupId><Name>All</Name></DocType>");
			ArrayList<DocType> doctypes = MDBConnection.getDocTypes(language_id, user_id, system_id, null);
			for (DocType docType : doctypes) {
				if (docType.getGroup_id() != group_id) {
					group_id = docType.getGroup_id();
					sb.append("<DocType><TypeId>-" + group_id + "</TypeId><GroupId>0</GroupId><Name>"
							+ docType.getDoctypegroupvalue() + "</Name></DocType>");
				}
				boolean isJsType = docType.isJSType();
				sb.append("<DocType>" + (isJsType ? "<JSType>1</JSType>" : "") + "<TypeId>" + docType.getId()
						+ "</TypeId><GroupId>-" + group_id + "</GroupId><Name>" + docType.getDoctypevalue()
						+ "</Name></DocType>");
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		sb.append("</DocTypeGroup>");
		String ret = sb.toString();
		return ret;
	}
}

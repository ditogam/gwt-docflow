package com.docflow.and.impl;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

import com.common.shared.ClSelectionItem;

public class ClSelectionItemLoader {
	public static ArrayList<ClSelectionItem> getTopType(Connection conn,
			int type) throws Exception {
		return MDBConnection.getClSelectionItems(conn, type);
	}

	public static HashMap<Integer, ArrayList<ClSelectionItem>> getTopTypes(
			Connection conn, int[] types) throws Exception {
		ArrayList<ClSelectionItem> list = MDBConnection
				.getTopTypes(conn, types);
		HashMap<Integer, ArrayList<ClSelectionItem>> map = new HashMap<Integer, ArrayList<ClSelectionItem>>();
		for (ClSelectionItem item : list) {
			ArrayList<ClSelectionItem> items = map
					.get((int) item.getParentId());
			if (items == null) {
				items = new ArrayList<ClSelectionItem>();
				map.put((int) item.getParentId(), items);
			}
			items.add(item);
		}
		return map;
	}

	public static HashMap<Integer, ArrayList<ClSelectionItem>> getAllTopTypes(
			Connection conn) throws Exception {
		return getTopTypes(conn, null);
	}
}

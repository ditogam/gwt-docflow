package com.docflow.server;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import com.common.db.DBConnection;
import com.common.shared.ClSelectionItem;
import com.docflow.server.db.MDBConnection;
import com.docflow.shared.ClSelection;

public class ClSCGenerator {

	public static HashMap<Long, ArrayList<ClSelectionItem>> tmRegions;
	public static HashMap<Long, ArrayList<ClSelectionItem>> tmsubRegions;
	public static HashMap<Long, ArrayList<ClSelectionItem>> tmcities;
	public static ArrayList<ClSelectionItem> regions;

	public static HashMap<Integer, HashMap<String, ArrayList<ClSelectionItem>>> depValues;
	public static HashMap<Integer, HashMap<Long, ClSelectionItem>> values;
	public static HashMap<String, String> strvalues;
	public static HashMap<Integer, ArrayList<ClSelectionItem>> parentItems;

	static {
		try {
			generateValues(null);
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	@SuppressWarnings("rawtypes")
	private static void clearMap(HashMap map) {
		if (map != null)
			map.clear();
	}

	private static void generateValues(Connection connection) throws Exception {

		if (!ClSelection.reloaded)
			MDBConnection.reloadClSelections(connection);

		clearMap(tmRegions);
		clearMap(tmsubRegions);
		clearMap(tmcities);
		clearMap(depValues);
		clearMap(values);
		clearMap(strvalues);
		clearMap(strvalues);
		if (regions != null)
			regions.clear();
		ClSelection[] clSelections = ClSelection.SELECTIONS;

		HashMap<String, String> names = new HashMap<String, String>();

		for (int i = 0; i < clSelections.length; i++) {
			String dbName = clSelections[i].getDbName();
			String name = names.get(dbName);
			if (name == null) {
				names.put(dbName, name);
			}
		}

		values = new HashMap<Integer, HashMap<Long, ClSelectionItem>>();
		strvalues = new HashMap<String, String>();
		HashMap<Integer, ArrayList<ClSelectionItem>> fullValues = new HashMap<Integer, ArrayList<ClSelectionItem>>();
		parentItems = new HashMap<Integer, ArrayList<ClSelectionItem>>();
		int cnt = 1;

		Set<String> nkeys = names.keySet();
		for (String nkey : nkeys) {
			Connection conn = null;
			try {
				conn = DBConnection.getConnection(nkey);

				for (int i = 0; i < clSelections.length; i++) {
					ClSelection ci = clSelections[i];
					if (!ci.getDbName().equals(nkey))
						continue;

					System.err.println(cnt + " out of " + clSelections.length
							+ ";Attempting ClSelection on " + ci.getDbName()
							+ " sql = " + ci.getSql());

					ArrayList<ClSelectionItem> items = DBConnection
							.getSelectionItems(ci.getDbName(), ci.getSql(),
									ci.getParenttype() != ClSelection.T_NONE,
									conn);

					// Collections.sort(items, new Comparator<ClSelectionItem>()
					// {
					// @Override
					// public int compare(ClSelectionItem item1,
					// ClSelectionItem item2) {
					// return item1.getValue().compareTo(item1.getValue());
					// }
					// });

					System.err.println(cnt + " out of " + clSelections.length
							+ ";Done " + items.size() + " items");
					fullValues.put(ci.getType(), items);
					values.put(ci.getType(),
							new HashMap<Long, ClSelectionItem>());
					if (ci.getParenttype() == ClSelection.T_NONE)
						parentItems.put(ci.getType(), items);
					cnt++;
				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					DBConnection.freeConnection(conn);
				} catch (Exception e2) {
					// TODO: handle exception
				}
			}
		}

		Set<Integer> keys = fullValues.keySet();

		depValues = new HashMap<Integer, HashMap<String, ArrayList<ClSelectionItem>>>();

		for (Integer key : keys) {
			ArrayList<ClSelectionItem> items = fullValues.get(key);
			HashMap<Long, ClSelectionItem> valueItems = values.get(key);
			for (ClSelectionItem _item : items) {
				valueItems.put(_item.getId(), _item);
				strvalues.put(key + "_" + _item.getId(), _item.getValue());
			}

			ArrayList<ClSelection> k = getCilds(key, clSelections);
			if (k == null)
				continue;
			HashMap<String, ArrayList<ClSelectionItem>> map = new HashMap<String, ArrayList<ClSelectionItem>>();
			depValues.put(key, map);
			for (ClSelection clSelection : k) {
				ArrayList<ClSelectionItem> _items = fullValues.get(clSelection
						.getType());
				if (_items == null)
					continue;
				for (ClSelectionItem _item : items) {
					map.put(clSelection.getType() + "_" + _item.getId(),
							getSubItems(_item, _items));
				}
			}

		}
	}

	private static ArrayList<ClSelection> getCilds(int parent,
			ClSelection[] clSelections) {
		ArrayList<ClSelection> ret = new ArrayList<ClSelection>();
		for (ClSelection clSelection : clSelections) {
			if (clSelection.getParenttype() == parent)
				ret.add(clSelection);
		}
		return ret;
	}

	public static ArrayList<ClSelectionItem> getCities(long subRegionId) {
		return getDepValues(ClSelection.T_SUBREGION, ClSelection.T_CITY,
				subRegionId);

	}

	public static ArrayList<ClSelectionItem> getDepValues(int ptype,
			int subtype, long parentId) {
		HashMap<String, ArrayList<ClSelectionItem>> items = depValues
				.get(ptype);
		if (items == null)
			return null;
		String key = subtype + "_" + parentId;
		return items.get(key);
	}

	public static ArrayList<ClSelectionItem> getRegions() {
		return getToptValues(ClSelection.T_REGION);
	}

	public static ArrayList<ClSelectionItem> getStreets(long cityId) {
		return getDepValues(ClSelection.T_CITY, ClSelection.T_STREET, cityId);
	}

	private static ArrayList<ClSelectionItem> getSubItems(
			ClSelectionItem clSelectionItem, ArrayList<ClSelectionItem> items) {
		ArrayList<ClSelectionItem> result = new ArrayList<ClSelectionItem>();
		for (ClSelectionItem item : items) {
			if (item.getParentId() == clSelectionItem.getId()) {
				item.setParentValue(clSelectionItem);
				clSelectionItem.getSubItems().add(item);
				result.add(item);
			}
		}

		// Collections.sort(result, new Comparator<ClSelectionItem>() {
		// @Override
		// public int compare(ClSelectionItem item1, ClSelectionItem item2) {
		//
		// return item1.getValue().compareTo(item1.getValue());
		// }
		// });
		return result;
	}

	public static ArrayList<ClSelectionItem> getSubRegions(long regionId) {
		return getDepValues(ClSelection.T_REGION, ClSelection.T_SUBREGION,
				regionId);
	}

	public static ArrayList<ClSelectionItem> getToptValues(int topType) {
		return parentItems.get(topType);
	}

	public static ClSelectionItem getValue(int type, long id) {
		HashMap<Long, ClSelectionItem> items = values.get(type);
		if (items == null)
			return null;
		return items.get(id);
	}

	public static String getValueForTypeAndId(int type_id, long id) {
		return strvalues.get(type_id + "_" + id);
	}

	public static void reloadParams() {
		try {
			DocFlowServiceImpl.gisMaps = null;
			DocFlowServiceImpl.statuses = null;
			CreateCustomDatasource.dsNames = null;
			ClSelection.reloaded = false;
			generateValues(null);
		} catch (Exception e) {

		}
	}
}

package com.docflow.server.db.map;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.common.db.DBConnection;
import com.docflow.server.DMIUtils;
import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

public class BuildingCustomersDMI {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public DSResponse fetchBuildCustomersResult(DSRequest req) throws Exception {
		Map criteria = req.getCriteria();
		criteria.put("only_building_ids", 1);
		criteria.put("for_building", 1);
		Map<?, ?> customers = DMIUtils.findRecordByCriteria("BuildingsDS",
				"getCustomersForSelection", criteria);
		if (customers == null)
			return new DSResponse(new ArrayList<Map<String, Object>>());
		Object cus_ids = customers.get("feature_text");
		if (cus_ids == null)
			return new DSResponse(new ArrayList<Map<String, Object>>());
		cus_ids = cus_ids.toString().trim();
		if (cus_ids.toString().length() == 0)
			return new DSResponse(new ArrayList<Map<String, Object>>());

		Map newCriteria = new TreeMap();
		puttoMap(criteria, newCriteria, "pmeter_cusid");
		puttoMap(criteria, newCriteria, "subregionid");
		puttoMap(criteria, newCriteria, "mstatusid");
		puttoMap(criteria, newCriteria, "has_no_parent");
		newCriteria.put("cus_ids", cus_ids);

		List<Map<?, ?>> result = DMIUtils.findRecordsByCriteria(
				"CustShortMeterDS", null, newCriteria);
		return new DSResponse(result);

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void puttoMap(Map criteria, Map newCriteria, String key) {
		newCriteria.put(key, criteria.get(key));
	}

	@SuppressWarnings("rawtypes")
	public DSResponse fetchMeterBuildins(DSRequest req) throws Exception {
		Map criteria = req.getCriteria();
		Object uuid = criteria.get("uuid");
		Map<?, ?> result = DMIUtils.findRecordByCriteria("CustShortMeterDS",
				"getBuildingsFromCustomers", criteria);
		Object building_ids = result.get("cusname");
		if (building_ids == null) {
			building_ids = "";// return new DSResponse(new ArrayList<Map<String,
								// Object>>());
		}
		Object srid = criteria.get("srid");
		Connection connMaps = null;
		try {
			connMaps = DBConnection.getConnection("MAP");
			String feature_text = BuildingDMI.saveSearchResult(uuid.toString(),
					building_ids.toString(), new Integer(srid.toString()),
					connMaps);
			Map<String, Object> rs = new TreeMap<String, Object>();
			rs.put("feature_text", feature_text);
			ArrayList<Map<String, Object>> res = new ArrayList<Map<String, Object>>();
			res.add(rs);
			connMaps.commit();
			DSResponse mres = new DSResponse(res);
			mres.setDropExtraFields(false);
			return mres;
		} catch (Exception e) {

			try {
				connMaps.rollback();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			throw new Exception(e.toString());
		} finally {

			try {
				DBConnection.freeConnection(connMaps);
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
	}
}

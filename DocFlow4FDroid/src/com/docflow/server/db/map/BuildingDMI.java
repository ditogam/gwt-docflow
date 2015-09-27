package com.docflow.server.db.map;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.common.db.DBConnection;
import com.docflow.server.DMIUtils;
import com.docflow.shared.MapObjectTypes;
import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

public class BuildingDMI {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public DSResponse fetchCustSearchResult(DSRequest req) throws Exception {
		Map criteria = req.getCriteria();
		criteria.put("only_building_ids", 1);
		criteria.put("for_building", 1);
		Object uuid = criteria.get("uuid");
		Map<?, ?> result = DMIUtils.findRecordByCriteria("CustomerDS", null,
				criteria);
		Object building_ids = result.get("cusname");
		if (building_ids == null) {
			building_ids = "";// return new DSResponse(new ArrayList<Map<String,
								// Object>>());
		}
		Object srid = criteria.get("srid");
		Connection connMaps = null;
		try {
			connMaps = DBConnection.getConnection("MAP");
			String feature_text = saveSearchResult(uuid.toString(),
					building_ids.toString(), new Integer(srid.toString()),
					connMaps);
			Map<String, Object> rs = new TreeMap<String, Object>();
			rs.put("feature_text", feature_text);
			ArrayList<Map<String, Object>> res = new ArrayList<Map<String, Object>>();
			res.add(rs);
			connMaps.commit();
			return new DSResponse(res);
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

	public static String saveSearchResult(String uuid, String building_ids,
			int srid, Connection conn) throws Exception {
		PreparedStatement psDelete = null;
		PreparedStatement psAdd = null;
		PreparedStatement psGet = null;
		ResultSet rs = null;
		try {
			psDelete = conn
					.prepareStatement("delete from cust_search_result where uu_id=? or create_time<now() - cast('1 day' as interval)");
			psDelete.setString(1, uuid);
			psDelete.executeUpdate();
			if (building_ids.length() > 0) {

				psAdd = conn
						.prepareStatement("insert into cust_search_result (buid,uu_id) select distinct building_id,? from maps.building_to_customers "
								+ "where building_id = ANY(string_to_array(?,',')::int[])");

				psAdd.setString(1, uuid);
				psAdd.setString(2, building_ids);
				psAdd.executeUpdate();
				psGet = conn
						.prepareStatement("select ST_AsText(ST_transform(ST_Buffer(ST_Envelope (ST_Collect(the_geom)),100),?)) from buildings b"
								+ " where buid= ANY(string_to_array(?,',')::int[])");

				psGet.setInt(1, srid);
				psGet.setString(2, building_ids);
				rs = psGet.executeQuery();
				if (rs.next()) {
					String res = rs.getString(1);
					return res;
				}

			}
			return "";

		} finally {
			try {
				psDelete.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			try {
				psAdd.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			try {
				psGet.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			try {
				rs.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}

	}

	public DSResponse add(DSRequest req) throws Exception {
		Connection connBilling = null;
		Connection connMaps = null;
		try {
			Map<?, ?> map = req.getValues();
			int building_id = new Integer(map.get("buid").toString());
			String customers = map.get("feature_text") == null ? "" : map.get(
					"feature_text").toString();
			connBilling = DBConnection.getConnection("Gass");
			connMaps = DBConnection.getConnection("MAP");
			customers = customers.trim();
			saveBuildings(building_id, customers, connBilling);
			saveBuildings(building_id, customers, connMaps);
			saveHasCustomers(building_id, customers.length() > 0 ? 1 : 0,
					connMaps);

			connBilling.commit();
			connMaps.commit();
			return new DSResponse(map);

		} catch (Exception e) {
			try {
				connBilling.rollback();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			try {
				connMaps.rollback();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			throw new Exception(e.toString());
		} finally {
			try {

				DBConnection.freeConnection(connBilling);
			} catch (Exception e2) {
				// TODO: handle exception
			}
			try {
				DBConnection.freeConnection(connMaps);
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
	}

	private void saveBuildings(int building_id, String customers,
			Connection conn) throws Exception {
		PreparedStatement psDelete = null;
		PreparedStatement psAdd = null;
		try {
			psDelete = conn
					.prepareStatement("delete from maps.building_to_customers where building_id=?");
			psDelete.setInt(1, building_id);
			psDelete.executeUpdate();
			if (customers.length() > 0) {
				String cusIds[] = customers.split(",");
				psAdd = conn
						.prepareStatement("insert into maps.building_to_customers (building_id,cusid) values (?,?)");

				for (String cusId : cusIds) {
					psAdd.setInt(1, building_id);
					psAdd.setInt(2, new Integer(cusId));
					psAdd.addBatch();
				}
				psAdd.executeBatch();
			}

		} finally {
			try {
				psDelete.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			try {
				psAdd.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}

	}

	private void saveHasCustomers(int building_id, int hascust, Connection conn)
			throws Exception {
		PreparedStatement psUpdate = null;

		try {
			psUpdate = conn
					.prepareStatement("update buildings set has_customers=? where buid=?");
			psUpdate.setInt(1, hascust);
			psUpdate.setInt(2, building_id);
			psUpdate.executeUpdate();

		} finally {
			try {
				psUpdate.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}

		}

	}

	private void setPSValue(int index, PreparedStatement ps, Object value,
			int object_type) throws Exception {
		if (value == null)
			ps.setNull(index, object_type);
		else
			ps.setObject(index, value, object_type);
	}

	private void saveMapObject(Connection conn, int map_object_type,
			Map<String, Object> map) throws Exception {
		PreparedStatement psUpdate = null;
		String statement = DBMapObjectTypes.getSQLForUpdate(map_object_type);
		if (statement == null)
			return;
		DBMapObjectTypes.SQLTypes[] mapping = DBMapObjectTypes
				.getSQLTypes(map_object_type);
		if (mapping == null)
			return;
		try {
			psUpdate = conn.prepareStatement(statement);
			for (int i = 0; i < mapping.length; i++) {
				setPSValue(i + 1, psUpdate, map.get(mapping[i].field_name),
						mapping[i].field_sql_type);
			}
			psUpdate.executeUpdate();

		} finally {

			try {
				psUpdate.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
	}

	private void saveDistrict_Meter_mapping(int map_point_id, int cusid,
			Connection conn) throws Exception {
		PreparedStatement psAdd = null;
		try {

			removeDistrict_Meter_mapping(cusid, conn);
			psAdd = conn
					.prepareStatement("insert into maps.district_meter_mapping (cusid,meter_point_id) values (?,?)");

			psAdd.setInt(1, cusid);
			psAdd.setInt(2, map_point_id);

			psAdd.executeUpdate();

		} finally {
			try {
				psAdd.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}

	}

	private void removeDistrict_Meter_mapping(int cusid, Connection conn)
			throws Exception {
		PreparedStatement psDelete = null;
		try {
			psDelete = conn
					.prepareStatement("delete from maps.district_meter_mapping where cusid=?");
			psDelete.setInt(1, cusid);

			psDelete.executeUpdate();

		} finally {
			try {
				psDelete.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}

	}

	@SuppressWarnings("unchecked")
	public DSResponse remove(DSRequest req) throws Exception {
		Map<String, Object> map = req.getOldValues();
		DSResponse responce = new DSResponse(map);
		Connection connMaps = null;
		Connection connBilling = null;
		PreparedStatement psDelete = null;

		try {
			connMaps = DBConnection.getConnection("MAP");
			connBilling = DBConnection.getConnection("Gass");
			int cusid = new Integer(map.get("buid").toString());
			psDelete = connMaps
					.prepareStatement("delete from district_meters where cusid=?");
			psDelete.setInt(1, cusid);
			psDelete.executeUpdate();
			removeDistrict_Meter_mapping(cusid, connMaps);
			removeDistrict_Meter_mapping(cusid, connBilling);
			connMaps.commit();
			connBilling.commit();
			return responce;
		} catch (Exception e) {
			e.printStackTrace();
			try {
				connMaps.rollback();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			try {
				connBilling.rollback();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			throw new Exception(e.toString());
		} finally {
			try {
				psDelete.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			try {
				DBConnection.freeConnection(connBilling);
			} catch (Exception e2) {
				// TODO: handle exception
			}
			try {
				DBConnection.freeConnection(connMaps);
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}

	}

	public static String getCenterCoordinates(Integer customer_id,
			String subregion_id, String to_srid) throws Exception {
		Connection connMaps = null;
		Connection connBilling = null;
		ResultSet rsBilling = null;
		ResultSet rsMap = null;
		Integer iSubregion_id = null;
		Long lBuid = null;
		try {
			if (customer_id != null) {
				connBilling = DBConnection.getConnection("Gass");
				String sql = "select subregionid,building_id from v_customer_full where cusid="
						+ customer_id;
				rsBilling = connBilling.createStatement().executeQuery(sql);
				if (rsBilling.next()) {
					Object obj = rsBilling.getObject("subregionid");
					if (obj != null) {
						try {
							iSubregion_id = Integer.parseInt(obj.toString());
						} catch (Exception e) {
							// TODO: handle exception
						}
					}
					obj = rsBilling.getObject("building_id");
					if (obj != null) {
						try {
							lBuid = Long.parseLong(obj.toString());
						} catch (Exception e) {
							// TODO: handle exception
						}
					}
				}

			} else {
				try {
					iSubregion_id = Integer.parseInt(subregion_id.toString());
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
			String sql = null;
			if (lBuid != null) {
				sql = "select astext(transform(globals.st_pointonsurface(the_geom),"
						+ to_srid + ")) wkt from buildings where buid=" + lBuid;
			} else if (iSubregion_id != null) {
				sql = "select astext(transform(COALESCE(globals.st_subregion_centroid(subregion_id),globals.st_pointonsurface(the_geom)),"
						+ to_srid
						+ ")) wkt from subregions where the_geom is not null and subregion_id ="
						+ iSubregion_id;
			}
			if (sql != null) {
				connMaps = DBConnection.getConnection("MAP");
				rsMap = connMaps.createStatement().executeQuery(sql);
				if (rsMap.next()) {
					String wkt = rsMap.getString("wkt");
					return wkt;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			// throw new Exception(e.toString());
		} finally {
			try {
				rsBilling.getStatement().close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			try {
				rsBilling.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			try {
				rsMap.getStatement().close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			try {
				rsMap.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			try {
				DBConnection.freeConnection(connBilling);
			} catch (Exception e2) {
				// TODO: handle exception
			}
			try {
				DBConnection.freeConnection(connMaps);
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
		return null;
	}

	public Map<String, Object> update(Map<String, Object> map) throws Exception {

		Connection connMaps = null;
		Connection connBilling = null;
		PreparedStatement psUpdate = null;
		ResultSet rs = null;
		try {
			int building_id = new Integer(map.get("buid").toString());
			String feature_text = map.get("feature_text").toString();
			int srid = new Integer(map.get("srid").toString());
			int to_srid = new Integer(map.get("to_srid").toString());
			int regid = new Integer(map.get("regid").toString());
			int raiid = new Integer(map.get("raiid").toString());
			int map_object_type = new Integer(map.get("map_object_type")
					.toString());
			String table_name = MapObjectTypes
					.getMapObjectTypeTblName(map_object_type);

			Map<String, Object> criteria = new TreeMap<String, Object>();
			criteria.putAll(map);
			criteria.put("to_srid", srid);
			criteria.put("from_srid", to_srid);
			criteria.put("buffer_size", 0.5);
			criteria.put("point", feature_text);

			List<Map<?, ?>> list = DMIUtils.findRecordsByCriteria(
					"BuildingsDS", "getBufferedPoligon", criteria);
			if (list == null || list.size() == 0)
				throw new Exception("Cannot find subregion!!!");
			if (list.size() > 1)
				throw new Exception(
						"This object intersects several subregion!!!");
			Map<?, ?> res = list.get(0);
			int p_regid = new Integer(res.get("regid").toString());
			int p_raiid = new Integer(res.get("raiid").toString());
			int p_corector_id = -1;
			try {
				p_corector_id = new Integer(map.get("corector_id").toString());
			} catch (Exception e) {
				// TODO: handle exception
			}

			if (p_regid != regid)
				throw new Exception("This object belongs to other region!!!");
			if (p_raiid != raiid)
				throw new Exception("This object belongs to other subregion!!!");

			connMaps = DBConnection.getConnection("MAP");
			connBilling = DBConnection.getConnection("Gass");
			// object_type character varying, object_id integer, p_regid
			// integer, p_raiid integer,srid integer, to_srid integer,
			// feature_text character varying
			psUpdate = connMaps
					.prepareStatement("select globals.saveMapObject(?, ?, ?, ?,?, ?, ?, ?) buid");
			int index = 1;
			psUpdate.setString(index++, table_name);
			psUpdate.setInt(index++, building_id);
			psUpdate.setInt(index++, regid);
			psUpdate.setInt(index++, raiid);
			psUpdate.setInt(index++, p_corector_id);
			psUpdate.setInt(index++, srid);
			psUpdate.setInt(index++, to_srid);
			psUpdate.setString(index++, feature_text);
			rs = psUpdate.executeQuery();
			rs.next();

			building_id = rs.getInt(1);
			map.put("buid", building_id);
			if (map_object_type == MapObjectTypes.MO_DISTRICT_METER_TYPE) {
				int cusid = new Integer(map.get("cusid").toString());
				saveDistrict_Meter_mapping(building_id, cusid, connBilling);
				saveDistrict_Meter_mapping(building_id, cusid, connMaps);
			}
			saveMapObject(connMaps, map_object_type, map);
			connMaps.commit();
			connBilling.commit();
			return map;

		} catch (Exception e) {
			e.printStackTrace();
			try {
				connMaps.rollback();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			try {
				connBilling.rollback();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			throw new Exception(e.toString());
		} finally {
			try {
				rs.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			try {
				psUpdate.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			try {
				DBConnection.freeConnection(connBilling);
			} catch (Exception e2) {
				// TODO: handle exception
			}
			try {
				DBConnection.freeConnection(connMaps);
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
	}
}

package com.workflow.server.utils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;
import com.isomorphic.datasource.DataSource;
import com.isomorphic.util.DataTools;

public class DMIUtils {

	@SuppressWarnings({ "unchecked" })
	public static final List<Map<?, ?>> execute(String dsName,
			String operationId, Map<?, ?> data, String operationType)
			throws Exception {

		@SuppressWarnings("rawtypes")
		DSRequest request = new DSRequest(data == null ? new HashMap() : data);
		if (operationId != null)
			request.setOperationId(operationId);
		request.setDataSourceName(dsName);
		if (data != null)
			if (operationType == null ||DataSource.isFetch(operationType))
				request.setCriteria(data);
			else
				request.setValues(data);
		request.setOperationType(operationType);
//		request.setFreeOnExecute(true);
		DSResponse resp = request.execute();
		request.freeAllResources();
		List<Map<?, ?>> result = resp.getDataList();
		
		return result;
	}

	public static final List<Map<?, ?>> findRecordsByCriteria(String dsName,
			String operationId, Map<?, ?> criteria) throws Exception {
		return execute(dsName, operationId, criteria, "fetch");
	}

	@SuppressWarnings({ "rawtypes" })
	public static final Map<?, ?> findRecordByCriteria(String dsName,
			String operationId, Map<?, ?> criteria) throws Exception {
		List<Map<?, ?>> list = findRecordsByCriteria(dsName, operationId,
				criteria);
		Map result = new TreeMap();
		if (list != null && !list.isEmpty())
			result = list.get(0);
		return result;

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static final Map<?, ?> findRecordById(String dsName,
			String operationId, Object id, String idName) throws Exception {
		Map criteria = new TreeMap();
		criteria.put(idName, id);
		List<Map<?, ?>> list = findRecordsByCriteria(dsName, operationId,
				criteria);
		Map result = new TreeMap();
		if (list != null && !list.isEmpty())
			result = list.get(0);
		return result;

	}

	public static final void findRecordById(String dsName, String operationId,
			long id, String idName, Object bean) throws Exception {
		DataTools.setProperties(
				findRecordById(dsName, operationId, id, idName), bean);
	}

	@SuppressWarnings("rawtypes")
	public static final <D> List<D> findObjectsdByCriteria(String dsName,
			String operationId, Map<?, ?> criteria, Class<D> clazz)
			throws Exception {
		List<Map<?, ?>> list = findRecordsByCriteria(dsName, operationId,
				criteria);
		if (list == null)
			list = new ArrayList<Map<?, ?>>();

		List<D> result = new ArrayList<D>();
		for (Map map : list) {
			D o = clazz.newInstance();
			DataTools.setProperties(map, o);
			result.add(o);
		}
		return result;
	}

	public static String getRowValueSt(Object val) {
		return val == null ? null : val.toString();
	}

	public static Long getRowValueLong(Object val) {
		return val == null ? null : new Long(val.toString().trim());
	}

	public static Boolean getRowValueBoolean(Object val) {
		return val == null ? null : (val instanceof Boolean ? (Boolean) val
				: new Long(val.toString().trim()).longValue() == 1);
	}

	public static Double getRowValueDouble(Object val) {
		return val == null ? null : new Double(val.toString().trim());
	}

	public static Timestamp getRowValueDateTime(Object val) {
		return val == null ? null : (Timestamp) val;
	}

}

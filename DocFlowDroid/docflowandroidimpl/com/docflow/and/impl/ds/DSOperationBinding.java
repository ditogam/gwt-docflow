package com.docflow.and.impl.ds;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("operationBinding")
public class DSOperationBinding {
	@XStreamAsAttribute
	private String operationId;
	@XStreamAsAttribute
	private String operationType;

	private String selectClause;
	private String tableClause;
	private String whereClause;
	private String orderClause;
	private String customSQL;
	private String groupClause;

	private transient DataSource ds = null;
	private transient String selectStatement;

	public DSOperationBinding() {
	}

	public String getOperationId() {
		return detectNull(operationId);
	}

	public void setOperationId(String operationId) {
		this.operationId = detectNull(operationId);
	}

	public String getOperationType() {
		return detectNull(operationType);
	}

	public void setOperationType(String operationType) {
		this.operationType = detectNull(operationType);
	}

	public String getSelectClause() {
		return detectNull(selectClause);
	}

	public void setSelectClause(String selectClause) {
		this.selectClause = detectNull(selectClause);
	}

	public String getTableClause() {
		return detectNull(tableClause);
	}

	public void setTableClause(String tableClause) {
		this.tableClause = detectNull(tableClause);
	}

	public String getWhereClause() {
		return detectNull(whereClause);
	}

	public void setWhereClause(String whereClause) {
		this.whereClause = detectNull(whereClause);
	}

	public String getOrderClause() {
		return detectNull(orderClause);
	}

	public void setOrderClause(String orderClause) {
		this.orderClause = detectNull(orderClause);
	}

	public String getCustomSQL() {
		return detectNull(customSQL);
	}

	public void setCustomSQL(String customSQL) {
		this.customSQL = detectNull(customSQL);
	}

	public String getGroupClause() {
		return detectNull(groupClause);
	}

	public void setGroupClause(String groupClause) {
		this.groupClause = detectNull(groupClause);
	}

	private String detectNull(String str) {
		if (str != null && str.trim().isEmpty())
			str = null;
		return str == null ? null : str.trim();
	}

	private String setDefaults(String sql) {
		String result = sql;
		if (ds == null)
			return result;

		String tableName = getTableName();
		result = result.replaceAll("$defaultTableClause", tableName);

		String fields = "";
		ArrayList<DSField> dsfields = ds.getFields();
		if (dsfields == null)
			dsfields = new ArrayList<DSField>();
		for (DSField dsField : dsfields) {
			if (!fields.isEmpty())
				fields += ",";
			fields = tableName + ".\"" + dsField.getName() + "\"";
		}
		result = result.replaceAll("$defaultSelectClause", fields);
		return result;
	}

	private String getTableName() {
		String tableName = "\"" + ds.getTableName() + "\"";
		return tableName;
	}

	private String getValue(Object o) {
		if (o == null)
			return null;
		try {
			if (o instanceof Number)
				return o + "";
		} catch (Exception e) {
			// TODO: handle exception
		}

		if (o instanceof Date)
			return (((Date) o).getTime()) + "";

		return "'" + o + "'";
	}

	private String checkValueCriteria(String val) {
		return (val.contains("%") ? " like " : "=") + val;
	}

	public String generateSql(Map<String, Object> map) {

		if (operationType.equals("fetch")) {
			String result = generateSelect();
			String defaultWhereClause = "$defaultWhereClause";
			String where_criteria = "";
			if (ds != null && result.contains(defaultWhereClause)) {
				ArrayList<DSField> dsfields = ds.getFields();
				if (dsfields == null)
					dsfields = new ArrayList<DSField>();
				String tableName = tableClause == null ? (getTableName() + ".")
						: "";

				for (DSField dsField : dsfields) {
					Object o = map.get(dsField.getName());
					String val = getValue(o);
					if (val == null)
						continue;
					if (!where_criteria.isEmpty())
						where_criteria += " and ";
					where_criteria += tableName + "\"" + dsField.getName()
							+ "\"" + checkValueCriteria(val);
				}
				if (!where_criteria.isEmpty())
					where_criteria = "(" + where_criteria + ")";
			}
			Map<String, String> criteria = new HashMap<String, String>();
			Set<String> keys = map.keySet();
			for (String key : keys) {
				String val = getValue(map.get(key));
				if (val != null)
					criteria.put(key, val);
			}
			VelocityContext context = new VelocityContext();
			context.put("criteria", criteria);
			context.put(defaultWhereClause, where_criteria);
			StringWriter sw = new StringWriter();
			StringReader sr = new StringReader(result);
			Velocity.evaluate(context, sw, "", sr);
			result = sw.toString();

			return result;
		}
		return null;
	}

	public String generateSelect() {
		if (selectStatement != null)
			return selectStatement;
		getCustomSQL();
		getGroupClause();
		getOrderClause();
		getSelectClause();
		getTableClause();
		getWhereClause();
		String result = "";
		if (customSQL != null)
			return customSQL;

		result = " select ";
		if (selectClause != null)
			result += selectClause;
		else {
			result += "$defaultSelectClause";
		}
		result += "\nfrom\t";

		if (tableClause != null)
			result += tableClause;
		else {
			result += "$defaultTableClause";
		}

		if (whereClause != null)
			result += "\nwhere (" + whereClause + ")\t";
		else {
			result += "\n$defaultWhereClause";
		}

		if (groupClause != null)
			result += "\ngroup by (" + groupClause + ")\t";
		else {
			// result += "$defaultGroupClause";
		}

		if (orderClause != null)
			result += "\norder by (" + orderClause + ")\t";
		else {
			// result += "$defaultOrderClause";
		}
		result = setDefaults(result);
		return result;
	}

	public void setDs(DataSource ds) {
		this.ds = ds;
		selectStatement = generateSelect();
	}

}

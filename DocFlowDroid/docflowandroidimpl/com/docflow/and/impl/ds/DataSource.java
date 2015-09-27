package com.docflow.and.impl.ds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.velocity.app.Velocity;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.mapper.CannotResolveClassException;
import com.thoughtworks.xstream.mapper.MapperWrapper;

@XStreamAlias("DataSource")
public class DataSource {
	@XStreamAsAttribute
	@XStreamAlias("ID")
	private String id;
	@XStreamAsAttribute
	private String tableName;

	private ArrayList<DSField> fields;
	private ArrayList<DSOperationBinding> operationBindings;
	public static Map<String, DataSource> datasources = new HashMap<String, DataSource>();
	private Map<String, Map<String, DSOperationBinding>> mOperationBindings = new HashMap<String, Map<String, DSOperationBinding>>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ArrayList<DSField> getFields() {
		return fields;
	}

	public void setFields(ArrayList<DSField> fields) {
		this.fields = fields;
	}

	public ArrayList<DSOperationBinding> getOperationBindings() {
		return operationBindings;
	}

	public void setOperationBindings(
			ArrayList<DSOperationBinding> operationBindings) {
		this.operationBindings = operationBindings;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public void generate() {
		mOperationBindings.put("fetch",
				new HashMap<String, DSOperationBinding>());
		mOperationBindings
				.put("add", new HashMap<String, DSOperationBinding>());
		mOperationBindings.put("update",
				new HashMap<String, DSOperationBinding>());
		mOperationBindings.put("remove",
				new HashMap<String, DSOperationBinding>());
		mOperationBindings
				.put("add", new HashMap<String, DSOperationBinding>());
		for (DSOperationBinding ob : operationBindings) {
			ob.setDs(this);
			Map<String, DSOperationBinding> mp = mOperationBindings.get(ob
					.getOperationType());
			if (mp == null)
				continue;

			if (ob.getOperationId() == null
					|| ob.getOperationId().trim().length() == 0)
				ob.setOperationId("");
			mp.put(ob.getOperationId(), ob);
		}

		Set<String> keys = mOperationBindings.keySet();
		for (String key : keys) {
			Map<String, DSOperationBinding> mp = mOperationBindings.get(key);
			if (!mp.containsKey(""))
				mp.put("", new DSOperationBinding());
		}

	}

	private static XStream xStream = null;

	public static String getRequestSql(String dsName, String operationType,
			String operationId, Map<String, Object> map) {
		String result = null;
		DataSource ds = datasources.get(dsName);
		if (ds == null)
			return null;
		Map<String, DSOperationBinding> mp = ds.mOperationBindings
				.get(operationType);
		if (mp == null)
			return null;

		if (operationId == null || operationId.trim().length() == 0)
			operationId = "";
		DSOperationBinding op = mp.get(operationId);
		if (op == null)
			return null;
		result = op.generateSql(map);
		return result;
	}

	public static DataSource createInstance(String content) throws Exception {
		if (xStream == null) {
			xStream = new XStream() {
				protected MapperWrapper wrapMapper(MapperWrapper next) {
					return new MapperWrapper(next) {
						@SuppressWarnings("rawtypes")
						public boolean shouldSerializeMember(Class definedIn,
								String fieldName) {
							try {
								return definedIn != Object.class
										|| realClass(fieldName) != null;
							} catch (CannotResolveClassException cnrce) {
								return false;
							}
						}
					};
				}
			};
			xStream.alias("DataSource", DataSource.class);
			xStream.alias("field", DSField.class);
			xStream.alias("operationBinding", DSOperationBinding.class);
			xStream.autodetectAnnotations(true);
		}

		DataSource result = (DataSource) xStream.fromXML(content);
		result.generate();
		return result;

	}

	static {
		Velocity.setProperty(Velocity.RUNTIME_LOG_LOGSYSTEM_CLASS,
				"com.docflow.and.impl.ds.velocity.VelocityLogger");
		Velocity.init();
	}

	public static void putAll(ArrayList<DataSource> list) {
		for (DataSource ds : list) {
			DataSource.datasources.put(ds.getId(), ds);
		}

	}
}

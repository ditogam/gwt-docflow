package com.docflow.client.components.map;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.TreeMap;

import com.docflow.shared.IMonitorChartType;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.form.fields.SelectItem;

public class MeterChartData extends Record {

	public static final TreeMap<Integer, String> mpDeviceTypes = new TreeMap<Integer, String>();
	public static final TreeMap<Integer, ArrayList<Record>> mpDeviceTypesA = new TreeMap<Integer, ArrayList<Record>>();

	private static String getTypeText(int typeid) {
		String ret = mpDeviceTypes.get(typeid);
		return ret == null ? "Unknown" : ret;
	}

	public static void setTypes(Record[] records) {
		if (records == null)
			return;
		for (Record record : records) {
			try {
				Integer typeId = record.getAttributeAsInt("val_type");
				ArrayList<Record> list = mpDeviceTypesA.get(typeId);
				if (list == null) {
					list = new ArrayList<Record>();
					mpDeviceTypesA.put(typeId, list);
				}
				list.add(record);
				mpDeviceTypes.put(record.getAttributeAsInt("id"),
						record.getAttribute("values"));
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	public static void setComboValues(int type, SelectItem siCombo) {
		if (siCombo.getMultiple() == null || !siCombo.getMultiple())
			siCombo.setMultiple(true);
		ArrayList<Record> types = mpDeviceTypesA.get(type);
		if (types == null)
			types = new ArrayList<Record>();
		LinkedHashMap<String, String> hashMap = new LinkedHashMap<String, String>();
		ArrayList<String> selections = new ArrayList<String>();
		for (Record record : types) {
			hashMap.put(record.getAttribute("id"),
					record.getAttribute("values"));
			try {
				if (record.getAttributeAsBoolean("default_val"))
					selections.add(record.getAttribute("id"));
			} catch (Exception e) {
				// TODO: handle exception
			}

		}
		siCombo.setValueMap(hashMap);
		if (selections.isEmpty())
			siCombo.setDefaultToFirstOption(true);
		else
			siCombo.setValues(selections.toArray(new String[] {}));
	}

	public MeterChartData() {

	}

	private static void createData(ArrayList<MeterChartData> datas,
			Record record, int type) {
		DateTimeFormat df = type == IMonitorChartType.DT_HOUR ? DateTimeFormat
				.getFormat("HH:mm") : DateTimeFormat.getFormat("dd/MM/yy");
		Date data_date = record.getAttributeAsDate("data_date");

		String values = record.getAttribute("values");
		if (data_date == null || values == null)
			return;
		String data_dates = df.format(data_date);
		String vArray[] = values.split("&");
		for (String st : vArray) {
			String v[] = st.split("::");
			try {
				int i = Integer.parseInt(v[0]);
				MeterChartData md = new MeterChartData();
				String value = v[1];
				md.setAttribute("data_date", data_dates);
				md.setAttribute("type", getTypeText(i));
				md.setAttribute("value", value);
				datas.add(md);
			} catch (Exception e) {
				// TODO: handle exception
			}

		}
	}

	public static MeterChartData[] getData(int type, Record[] records) {

		ArrayList<MeterChartData> datas = new ArrayList<MeterChartData>();
		if (records != null) {
			for (Record record : records) {
				createData(datas, record, type);
			}
		}
		return datas.toArray(new MeterChartData[] {});
	}

}
package com.docflow.client.components.map;

import java.util.Map;
import java.util.TreeMap;

import com.docflow.client.ClientUtils;
import com.docflow.client.DocFlow;
import com.docflow.client.FormItemDescr;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;

public class PDistrictMeterForm extends DynamicForm {

	private SelectItem siMeters;
	private StaticTextItem stiZone;
	private SelectItem siCorector;

	public PDistrictMeterForm(Record record, boolean editable,
			int num_of_columns) {
		setNumCols(num_of_columns);
		Integer subregionid = record.getAttributeAsInt("raiid");
		Integer cusid = record.getAttributeAsInt("cusid");

		Map<String, Object> meterCrit = new TreeMap<String, Object>();
		meterCrit.put("parent_metters", 1);
		meterCrit.put("subregionid", subregionid);

		if (cusid != null && cusid > 0)
			meterCrit.put("ccusid", cusid);
		siMeters = new SelectItem("cusid", "საუბნო მრიცხველი");
		siMeters.setRequired(true);
		ClientUtils.fillCombo(siMeters, "CustomerDS", null, "cusid", "cusname",
				meterCrit);
		
		siMeters.setAutoFetchData(true);
		siMeters.setColSpan(2);
		siMeters.setWidth(400);
		siMeters.setCanEdit(editable);
		setTitleOrientation(TitleOrientation.TOP);

		stiZone = new StaticTextItem("zone", "ზონა");

		ClientUtils.makeDependancy(siMeters, true, new FormItemDescr(stiZone,
				"zone", "zone"));
		stiZone.setVisible(editable);
		siCorector = new SelectItem("corector_id", "Corector");
		siCorector.setCanEdit(editable);
		meterCrit = new TreeMap<String, Object>();
		meterCrit.put("subregion_id", subregionid);
		ClientUtils.fillCombo(siCorector, "MeterDeviceDS", null, "id", "name",
				meterCrit);
		setFields(siMeters, stiZone, siCorector);
		setValues(record.toMap());
		if (cusid != null && cusid > 1) {
			Criteria cr = new Criteria();
			cr.setAttribute("cusid", cusid);
			cr.setAttribute("parent_metters", 1);
			DocFlow.getDataSource("CustomerDS").fetchData(cr, new DSCallback() {

				@Override
				public void execute(DSResponse response, Object rawData,
						DSRequest request) {
					if (response.getData() == null
							|| response.getData().length == 0)
						return;
					setValues(response.getData()[0].toMap());

				}
			});
		}

	}
}

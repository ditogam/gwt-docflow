package com.docflow.client.components.docflow;

import java.util.Map;
import java.util.TreeMap;

import com.docflow.client.ClientUtils;
import com.docflow.client.DocFlow;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.EnterKeyEditAction;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.IntegerItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.form.fields.events.KeyPressEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.SummaryFunction;
import com.smartgwt.client.widgets.grid.events.ChangeEvent;
import com.smartgwt.client.widgets.grid.events.ChangeHandler;
import com.smartgwt.client.widgets.grid.events.ChangedEvent;
import com.smartgwt.client.widgets.grid.events.ChangedHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

public class PReaderList extends VLayout {
	private DynamicForm dfMain;
	private IntegerItem iiListNum;
	private ListGrid lgReaderList;

	public PReaderList() {
		dfMain = new DynamicForm();
		dfMain.setTitleOrientation(TitleOrientation.TOP);
		iiListNum = new IntegerItem("iiListNum", "წამკითხველის ბარათი");
		ButtonItem biFind = new ButtonItem("biFind", "Find");
		biFind.setStartRow(false);
		biFind.setEndRow(false);

		iiListNum.addKeyPressHandler(new KeyPressHandler() {

			@Override
			public void onKeyPress(KeyPressEvent event) {
				if (event.getKeyName().equalsIgnoreCase("enter")) {
					find();
				}

			}
		});

		dfMain.setFields(iiListNum, biFind, new StaticTextItem("pcityname",
				"რაიონი"), new StaticTextItem("cityname", "ქალაქი/სოფელი"),
				new StaticTextItem("zone", "ჩაბარების თარიღი"),
				new StaticTextItem("readername", "წამკითხველი"),
				new StaticTextItem("username", "ოპერატორი"),
				new StaticTextItem("custypename", "ტიპი"), new StaticTextItem(
						"chdate", "ჩაბარების თარიღი"));
		dfMain.setHeight("20%");
		dfMain.setWidth100();

		lgReaderList = new ListGrid() {
			@Override
			protected String getBaseStyle(ListGridRecord record, int rowNum,
					int colNum) {
				if (getFieldName(colNum).equals("oldval")
						|| getFieldName(colNum).equals("newval")) {
					Double oldval = getValue(record, "oldval");
					Double newval = getValue(record, "newval");
					if (oldval != null && newval != null
							&& newval.doubleValue() < oldval.doubleValue())
						return "myHighGridCell";
				}
				if (getFieldName(colNum).equals("temp_k")) {
					Double temp_k = getValue(record, "temp_k");
					if (temp_k != null && temp_k.doubleValue() > 10.0)
						return "myLowGridCell";
				}
				return super.getBaseStyle(record, rowNum, colNum);
			}
		};
		lgReaderList.setAutoFetchData(false);
		lgReaderList.setCanEdit(true);
		lgReaderList.setDataSource(DocFlow.getDataSource("ReaderListValsDS"));
		lgReaderList.setShowAllRecords(true);
		lgReaderList.setShowGridSummary(true);
		lgReaderList
				.setFields(
						new ListGridField("id", "ID"),
						new ListGridField("streetname", "ქუჩა", 120),
						new ListGridField("home", "სახლი"),
						new ListGridField("flat", "ბინა"),
						new ListGridField("cusname", "გვარი", 120),
						new ListGridField("cusid", "cusid"),
						new ListGridField("oldloan", "დარიცხ. ვალი"),
						new ListGridField("oldval", "ძვ.ჩვ"),
						new ListGridField("newval", "ახ.ჩვ"),
						new ListGridField("temp_k", "კოეფიც."),
						new ListGridField("m3k", "m3"),
						// new ListGridField("temp_k", "K"),
						new ListGridField("startamount", "თანხა"),
						/* new ListGridField("oldcusid", "ძვ. cusid"), */new ListGridField(
								"newdate", "თარიღი"), new ListGridField(
								"meterid", "meterid"), new ListGridField(
								"metserial", "metserial", 100),
						new ListGridField("mtypename", "ტიპი", 100),
						new ListGridField("metermax", "metermax"));
		ListGridField[] fields = lgReaderList.getFields();
		for (ListGridField listGridField : fields) {
			listGridField.setCanEdit(false);
			listGridField.setShowGridSummary(false);

		}

		lgReaderList.setEnterKeyEditAction(EnterKeyEditAction.NEXTROW);
		ChangeHandler chh = new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				int row = event.getRowNum();
				ListGridRecord rec = lgReaderList.getRecord(row);
				if (rec == null)
					return;
				rec.setAttribute("userid", DocFlow.user_id);

			}
		};

		ListGridField listGridField = lgReaderList.getField("m3k");

		listGridField.setSummaryFunction(new SummaryFunction() {
			public Object getSummaryValue(Record[] records, ListGridField field) {
				Double m3 = 0.0;
				for (Record listGridField : records) {
					Double d = getValue(listGridField, "m3k");
					if (d != null)
						m3 += d;
				}
				NumberFormat nf = NumberFormat.getFormat("###0.##");
				return nf.format(m3) + " m3";
			}
		});

		listGridField = lgReaderList.getField("m3k");

		listGridField.setSummaryFunction(new SummaryFunction() {
			public Object getSummaryValue(Record[] records, ListGridField field) {
				Double m3 = 0.0;
				for (Record listGridField : records) {
					Double d = getValue(listGridField, "m3k");
					if (d != null)
						m3 += d;
				}
				NumberFormat nf = NumberFormat.getFormat("#,##0.##");
				return nf.format(m3) + " m3";
			}
		});

		// listGridField
		// .setRecordSummaryFunction(RecordSummaryFunctionType.MULTIPLIER);
		// listGridField.setSummaryFunction(SummaryFunctionType.SUM);
		listGridField.setShowGridSummary(true);

		listGridField = lgReaderList.getField("newval");

		listGridField.setSummaryFunction(new SummaryFunction() {
			public Object getSummaryValue(Record[] records, ListGridField field) {
				Double val = 0.0;
				for (Record listGridField : records) {
					Double newval = getValue(listGridField, "newval");
					Double oldval = getValue(listGridField, "oldval");
					if (oldval != null && newval != null)
						val += newval.doubleValue() - oldval.doubleValue();
				}
				NumberFormat nf = NumberFormat.getFormat("###0.##");
				return nf.format(val);
			}
		});
		// listGridField
		// .setRecordSummaryFunction(RecordSummaryFunctionType.MULTIPLIER);
		// listGridField.setSummaryFunction(SummaryFunctionType.SUM);
		listGridField.setShowGridSummary(true);

		ChangedHandler chgedh = new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				int row = event.getRowNum();
				ListGridRecord rec = lgReaderList.getRecord(row);
				if (rec == null)
					return;
				rec.setAttribute("userid", DocFlow.user_id);
				Double oldval = getValue(rec, "oldval");
				Double newval = getValue(rec, "newval");
				Double temp_k = getValue(rec, "temp_k");
				if (event.getColNum() == lgReaderList.getFieldNum("oldval"))
					oldval = getValue(event.getValue());
				if (event.getColNum() == lgReaderList.getFieldNum("newval"))
					newval = getValue(event.getValue());
				if (event.getColNum() == lgReaderList.getFieldNum("temp_k"))
					temp_k = getValue(event.getValue());
				if (oldval == null || newval == null || temp_k == null)
					return;
				Double m3 = (newval - oldval) * temp_k;
				NumberFormat twoDForm = NumberFormat.getFormat("#.##");
				m3 = twoDForm.parse(twoDForm.format(m3));
				rec.setAttribute("m3k", m3);
			}
		};
		lgReaderList.getField("oldval").addChangedHandler(chgedh);
		lgReaderList.getField("newval").addChangedHandler(chgedh);
		lgReaderList.getField("temp_k").addChangedHandler(chgedh);

		String[] coef_editable_columns = DocFlow.user_obj
				.getZoneConfiguration().getCoef_editable_columns();
		if (coef_editable_columns == null)
			coef_editable_columns = new String[] {};
		for (String coef_editable_column : coef_editable_columns) {
			setFieldEditable(chh, coef_editable_column.trim());
		}

		// // setFieldEditable(chh, "oldval");
		// setFieldEditable(chh, "newval");
		// setFieldEditable(chh, "m3k");
		// setFieldEditable(chh, "startamount");
		// // setFieldEditable(chh, "oldcusid");
		// setFieldEditable(chh, "temp_k");
		// setFieldEditable(chh, "oldval");

		// lgReaderList.getField("oldloan").setHidden(true);
		lgReaderList.getField("startamount").setHidden(true);
		lgReaderList.setHeight("80%");
		lgReaderList.setWidth100();
		lgReaderList.setAutoSaveEdits(false);
		HLayout hl = new HLayout();
		hl.setWidth100();
		hl.setHeight(30);
		IButton b = new IButton("Save");
		hl.setAlign(Alignment.RIGHT);
		HTMLPane pane = new HTMLPane();
		String cont = "<table><tr>";

		cont += "<td width=\"20\" bgcolor=\"" + "#FFC0C0"
				+ "\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td><td>" + "წინსწრება"
				+ "</td>";
		cont += "<td width=\"20\" bgcolor=\"" + "#C0FFC0"
				+ "\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td><td>"
				+ "დიდი კოეფიციენტი" + "</td>";

		cont += "</tr></table>";
		pane.setContents(cont);

		hl.addMembers(pane, b);

		b.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {

			@Override
			public void onClick(
					com.smartgwt.client.widgets.events.ClickEvent event) {
				try {
					lgReaderList.saveAllEdits();
				} catch (Exception e) {
					SC.warn(e.getMessage());
				}

			}
		});

		this.addMembers(dfMain, hl, lgReaderList);

		biFind.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				find();

			}
		});

	}

	private Double getValue(Object fieldVal) {
		try {
			return Double.parseDouble(fieldVal.toString().trim());
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	private Double getValue(Record record, String fieldName) {
		try {
			return Double.parseDouble(record.getAttribute(fieldName).trim());
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	private void setFieldEditable(ChangeHandler chh, String fieldName) {
		if (fieldName == null)
			return;
		ListGridField field = lgReaderList.getField(fieldName);
		if (field == null)
			return;
		field.setCanEdit(true);
		field.addChangeHandler(chh);
	}

	protected void find() {
		final Criteria criteria = new Criteria();
		try {
			lgReaderList.cancelEditing();
			final Integer listnum = Integer.parseInt(iiListNum.getValue()
					.toString().trim());
			criteria.setAttribute("listnum", listnum);
			if (DocFlow.user_obj.getUser().getRegionid() >= 0)
				criteria.setAttribute("ppcityid", DocFlow.user_obj.getUser()
						.getRegionid());
			if (DocFlow.user_obj.getUser().getSubregionid() >= 0)
				criteria.setAttribute("pcityid", DocFlow.user_obj.getUser()
						.getSubregionid());
			criteria.setAttribute("_UUUUUUUIDUUU", HTMLPanel.createUniqueId());
			ClientUtils.fetchData(criteria, new DSCallback() {

				@Override
				public void execute(DSResponse response, Object rawData,
						DSRequest request) {
					Record[] records = response.getData();
					Map<?, ?> map = null;
					if (records == null || records.length < 1)
						map = new TreeMap<String, String>();
					else
						map = records[0].toMap();

					dfMain.setValues(map);
					iiListNum.setValue(listnum);

				}
			}, "ReaderListDescDS", null);
			lgReaderList.setShowAllRecords(true);
			lgReaderList.filterData(criteria);

		} catch (Exception e) {
			SC.warn(e.getMessage());
		}
	}
}

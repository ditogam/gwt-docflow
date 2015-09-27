package com.rdcommon.client.components.ds;

import java.util.ArrayList;
import java.util.TreeMap;

import com.rdcommon.client.ClientUtils;
import com.rdcommon.client.CommonDialog;
import com.rdcommon.client.DSClientSearchForm;
import com.rdcommon.shared.ds.DSDefinition;
import com.rdcommon.shared.ds.DSField;
import com.rdcommon.shared.ds.DSFormDefinition;
import com.rdcommon.shared.props.PropertyNames;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.SelectionType;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.VisibilityChangedEvent;
import com.smartgwt.client.widgets.events.VisibilityChangedHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.BooleanItem;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

public class PDSDefinition extends VLayout {
	private DSDefinition ds;

	private DynamicForm dmMain;

	private TextItem tiDsName;
	private TextItem tiTableName;
	private BooleanItem biDropExtraFields;
	private TextItem tiServerObjectClassName;

	private GRDSForm searchForms;
	private GRDSForm editForms;
	private GRDSForm gridForms;

	private GROperationBindings operationBindings;
	private GRDSFields fields;

	public PDSDefinition(final DSDefinition ds) {
		this.ds = ds;
		dmMain = new DynamicForm();
		dmMain.setTitleOrientation(TitleOrientation.TOP);
		dmMain.setWidth100();
		dmMain.setHeight("30%");
		this.addMember(dmMain);
		tiDsName = new TextItem("dsName", "DS Name");
		tiTableName = new TextItem("tableName", "Table Name");
		biDropExtraFields = new BooleanItem("dropExtraFields",
				"dropExtraFields");
		tiServerObjectClassName = new TextItem("serverObjectClassName",
				"ServerObject ClassName");
		ButtonItem biSHowAdditional = new ButtonItem("kkk",
				"Show add properties");
		biSHowAdditional.setStartRow(false);
		biSHowAdditional.setEndRow(false);
		dmMain.setFields(tiDsName, tiTableName, biDropExtraFields,
				tiServerObjectClassName, biSHowAdditional);
		setData(ds);
		biSHowAdditional.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				new CommonDialog(new PropsDlgPanel(PDSDefinition.this.ds,
						PropertyNames.PT_DATASOURCE), true, 300, 400,
						"Additional properties").show();
			}
		});

		TabSet ts = new TabSet();
		ts.setWidth100();
		ts.setHeight("70%");
		Tab tOperationBinding = new Tab("Operation bindings");
		String[] bindings = null;
		if (ds != null && ds.getOperationBindings() != null)
			bindings = ds.getOperationBindings().toArray(new String[] {});
		operationBindings = new GROperationBindings(bindings);
		tOperationBinding.setPane(operationBindings);

		Tab tFields = new Tab("Fields");
		DSField[] fields = null;
		if (ds != null && ds.getDsFields() != null)
			fields = ds.getDsFields().toArray(new DSField[] {});
		this.fields = new GRDSFields(fields);
		tFields.setPane(this.fields);

		ts.addTab(tFields);
		ts.addTab(tOperationBinding);
		DataSource dsFields = this.fields.getDataSource();
		searchForms = addTab(ts, "Search forms", PropertyNames.PT_FORMITEM,
				(ds != null && ds.getSearchForms() != null) ? ds
						.getSearchForms().toArray(new DSFormDefinition[] {})
						: null, dsFields);

		editForms = addTab(ts, "Edit forms", PropertyNames.PT_FORMITEM,
				(ds != null && ds.getInputForms() != null) ? ds.getInputForms()
						.toArray(new DSFormDefinition[] {}) : null, dsFields);

		gridForms = addTab(
				ts,
				"Grids",
				PropertyNames.PT_GRID_FIELD,
				(ds != null && ds.getGrids() != null) ? ds.getGrids().toArray(
						new DSFormDefinition[] {}) : null, dsFields);

		this.addMember(ts);
	}

	private GRDSForm addTab(TabSet ts, String title, int field_type,
			DSFormDefinition[] formDefinitions, DataSource dsFields) {
		Tab tab = new Tab(title);
		final GRDSForm ret = new GRDSForm(formDefinitions, title, field_type,
				dsFields);

		com.smartgwt.client.widgets.events.ClickHandler tsbStateHandler = new com.smartgwt.client.widgets.events.ClickHandler() {

			@Override
			public void onClick(
					com.smartgwt.client.widgets.events.ClickEvent event) {
				Record rec = ret.listGrid.getSelectedRecord();

				if (rec == null)
					return;
				DSFormDefinition f = (DSFormDefinition) rec
						.getAttributeAsObject("_obj");
				if (f == null)
					return;
				saveData();
				final Window win = new Window();
//				win.setHeight(500);
//				win.setWidth(500);
				win.setTitle("test");

				win.addItem(new DSClientSearchForm(ds, f.getName()));
				win.setCanDragResize(true);
				win.setIsModal(true);
				ClientUtils.setValues(f, win);
				win.centerInPage();
				win.show();
				
				win.addVisibilityChangedHandler(new VisibilityChangedHandler() {

					@Override
					public void onVisibilityChanged(VisibilityChangedEvent event) {
						if (!event.getIsVisible())
							win.destroy();

					}
				});
			}
		};
		ret.toolStrip.addSeparator();
		ToolStripButton tsbPreview = ClientUtils.createTSButton(
				"[SKIN]/pickers/date_picker.png", tsbStateHandler,
				SelectionType.BUTTON, "Preview", "selection", ret.toolStrip);

		tab.setPane(ret);
		ts.addTab(tab);
		return ret;
	}

	public DynamicForm getDmMain() {
		return dmMain;
	}

	public void setData(DSDefinition ds) {
		this.ds = ds;
		if (ds != null) {
			tiDsName.setValue(ds.getDsName());
			tiTableName.setValue(ds.getTableName());
			biDropExtraFields.setValue(ds.getDropExtraFields());
			tiServerObjectClassName.setValue(ds.getServerObjectClassName());

		} else {
			dmMain.setValues(new TreeMap<String, Object>());
		}
	}

	public void saveData() {
		if (ds == null)
			ds = new DSDefinition();
		ds.setDsName(tiDsName.getValueAsString());
		ds.setTableName(tiTableName.getValueAsString());
		ds.setDropExtraFields(ClientUtils.getBooleanValue(tiTableName));
		ds.setServerObjectClassName(tiServerObjectClassName.getValueAsString());
		ArrayList<DSField> fds = new ArrayList<DSField>();
		ListGridRecord[] records = fields.listGrid.getRecords();
		for (ListGridRecord listGridRecord : records) {
			DSField f = (DSField) listGridRecord.getAttributeAsObject("_obj");
			if (f == null)
				continue;
			fds.add(f);
		}
		ds.setDsFields(fds);
		ArrayList<DSFormDefinition> sfms = new ArrayList<DSFormDefinition>();
		records = searchForms.listGrid.getRecords();
		for (ListGridRecord listGridRecord : records) {
			DSFormDefinition f = (DSFormDefinition) listGridRecord
					.getAttributeAsObject("_obj");
			if (f == null)
				continue;
			sfms.add(f);
		}
		ds.setSearchForms(sfms);

	}
}

package com.rdcommon.client.components.ds;

import com.rdcommon.client.CommonSavePanel;
import com.rdcommon.shared.ds.DSField;
import com.rdcommon.shared.ds.DSFormDefinition;
import com.rdcommon.shared.ds.DSGroup;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;

public class PDSFormSave extends CommonSavePanel {

	private DataSource dsDataSourceFields;
	private int field_type;
	private GRDSForm form;
	private DSFormDefinition fd;

	private TextItem tiName;
	private DynamicForm dmMain;

	private PDSComponent component;

	private PGroupEdit groupEdit;
	private boolean insert;
	private Record record;

	public PDSFormSave(GRDSForm form, DSFormDefinition fd, int field_type,
			DataSource dsDataSourceFields, Record record) {
		insert = record == null;
		this.dsDataSourceFields = dsDataSourceFields;
		this.record = record;
		this.form = form;
		this.field_type = field_type;

		dmMain = new DynamicForm();
		dmMain.setTitleOrientation(TitleOrientation.TOP);
		dmMain.setWidth100();
		dmMain.setHeight("5%");
		this.addMember(dmMain);

		tiName = new TextItem("name", "Name");
		tiName.setRequired(true);
		dmMain.setFields(tiName);
		if (fd != null) {
			tiName.setValue(fd.getName());
			DSFormDefinition copy = new DSFormDefinition();
			copy.mergeProps(fd, new DSFormDefinition());
			fd = copy;

		} else
			fd = new DSFormDefinition();
		this.fd = fd;

		component = new PDSComponent(fd, field_type, true);
		component.setHeight("30%");
		this.addMember(component);
		TabSet ts = new TabSet();
		ts.setWidth100();
		Tab tGroups = new Tab("Groups");
		groupEdit = new PGroupEdit(fd.getDsGroups() == null ? null : fd
				.getDsGroups().toArray(new DSGroup[] {}));
		tGroups.setPane(groupEdit);
		groupEdit.setWidth100();
		ts.addTab(tGroups);
		this.addMember(ts);
	}

	@Override
	public void saveData(Window win) throws Exception {
		if (!dmMain.validate())
			throw new Exception("Set name");
		fd.setName(tiName.getValueAsString());
		component.saveData();
		fd.setDsGroups(groupEdit.getData());
		Record r = record;
		boolean insert = r == null;
		if (r == null)
			r = new Record();
		form.setDataValue(fd, r);

		if (insert)
			form.listGrid.addData(r);
		else
			form.listGrid.refreshRow(form.listGrid.getRecordIndex(r));
	}
}

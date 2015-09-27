package com.rdcommon.client.components.ds;

import com.rdcommon.client.ClientUtils;
import com.rdcommon.client.CommonDialog;
import com.rdcommon.shared.ds.DSClientFieldDef;
import com.rdcommon.shared.ds.DSField;
import com.rdcommon.shared.props.PropertyNames;
import com.smartgwt.client.types.SelectionType;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.BooleanItem;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

public class PDSField extends VLayout {
	private DSField field;
	private DynamicForm dmMain;
	private TextItem tiName;
	private TextItem tiTitle;
	private BooleanItem biPrimaryKey;

	private PDSClientField searchProps;
	private PDSClientField formProps;
	private PDSClientField gridProps;
	private PDSClientField exportProps;

	private DSClientFieldDef memory;

	private ToolStripButton tsbCopy;
	private ToolStripButton tsbPaste;

	public PDSField(final DSField field) {
		this.field = field;
		if (field == null)
			this.field = new DSField();
		dmMain = new DynamicForm();
		dmMain.setTitleOrientation(TitleOrientation.TOP);
		dmMain.setWidth100();
		dmMain.setHeight100();
		this.addMember(dmMain);
		tiName = new TextItem("fName", "Name");
		tiName.setRequired(true);
		tiTitle = new TextItem("fTitle", "Title");
		biPrimaryKey = new BooleanItem("primaryKey", "Is primery key");
		ButtonItem biSHowAdditional = new ButtonItem("kkk",
				"Show add properties");
		biSHowAdditional.setStartRow(false);
		biSHowAdditional.setEndRow(false);
		dmMain.setFields(tiName, tiTitle, biPrimaryKey, biSHowAdditional);
		if (field != null) {
			tiName.setValue(field.getfName());
			tiTitle.setValue(field.getfTitle());
			biPrimaryKey.setValue(field.getPrimaryKey());
		}
		biSHowAdditional.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				new CommonDialog(new PropsDlgPanel(PDSField.this.field,
						PropertyNames.PT_DSFIELD), true, 300, 400,
						"Additional properties").show();
			}
		});
		ToolStrip toolStrip = new ToolStrip();
		final TabSet ts = new TabSet();
		com.smartgwt.client.widgets.events.ClickHandler tsbStateHandler = new com.smartgwt.client.widgets.events.ClickHandler() {

			@Override
			public void onClick(
					com.smartgwt.client.widgets.events.ClickEvent event) {
				Object objectSource = event.getSource();
				ttsbuttonClick(ts, objectSource);

			}
		};

		tsbCopy = ClientUtils.createTSButton("[SKIN]/RichTextEditor/copy.png",
				tsbStateHandler, SelectionType.BUTTON, "Copy", "selection",
				toolStrip);
		tsbCopy.setSelected(false);

		tsbPaste = ClientUtils.createTSButton(
				"[SKIN]/RichTextEditor/paste.png", tsbStateHandler,
				SelectionType.BUTTON, "Paste", "selection", toolStrip);
		tsbPaste.setSelected(false);
		tsbPaste.setDisabled(true);

		this.addMember(toolStrip);

		ts.setHeight("350");
		searchProps = addClientDef(ts, this.field.getSearchProps(),
				PropertyNames.PT_FORMITEM, "Search Field def values");
		formProps = addClientDef(ts, this.field.getFormProps(),
				PropertyNames.PT_FORMITEM, "Edit Field def values");
		gridProps = addClientDef(ts, this.field.getGridProps(),
				PropertyNames.PT_GRID_FIELD, "Grid Field def values");
		exportProps = addClientDef(ts, this.field.getExportProps(),
				PropertyNames.PT_GRID_FIELD, "Export Field def values");
		this.addMember(ts);

	}

	protected void ttsbuttonClick(TabSet ts, Object objectSource) {
		PDSClientField clientField = (PDSClientField) ts.getSelectedTab()
				.getPane();
		if (objectSource.equals(tsbCopy)) {
			tsbPaste.setDisabled(false);
			memory = clientField.cloneData();
		}
		if (objectSource.equals(tsbPaste)) {
			tsbPaste.setDisabled(true);
			clientField.setFieldDef(memory);
			memory = null;
		}

	}

	private PDSClientField addClientDef(TabSet ts, DSClientFieldDef def,
			int type, String title) {
		Tab tab = new Tab(title);
		PDSClientField ret = new PDSClientField(def, type);
		tab.setPane(ret);
		ts.addTab(tab);
		return ret;
	}

	public DSField saveData() throws Exception {
		if (!dmMain.validate())
			throw new Exception("Set name");
		field.setfName(tiName.getValueAsString());
		field.setfTitle(tiTitle.getValueAsString());
		field.setPrimaryKey(ClientUtils.getBooleanValue(biPrimaryKey));

		field.setSearchProps(searchProps.getFieldDef());
		if (field.getSearchProps() != null)
			field.getSearchProps().setName(field.getfName());

		field.setFormProps(formProps.getFieldDef());
		if (field.getFormProps() != null)
			field.getFormProps().setName(field.getfName());

		field.setGridProps(gridProps.getFieldDef());
		if (field.getGridProps() != null)
			field.getGridProps().setName(field.getfName());

		field.setExportProps(exportProps.getFieldDef());
		if (field.getExportProps() != null)
			field.getExportProps().setName(field.getfName());

		return field;
	}
}

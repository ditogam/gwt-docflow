package com.rdcommon.client.components.ds;

import java.util.TreeMap;

import com.rdcommon.client.ClientUtils;
import com.rdcommon.shared.ds.DSComponent;
import com.rdcommon.shared.ds.DSGroup;
import com.rdcommon.shared.props.PropertyNames;
import com.smartgwt.client.types.SelectionType;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.BooleanItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

public class PDSGroupData extends VLayout {
	private ToolStripButton tsbSave;

	private DSGroup group;
	private PDSComponent pComp;
	private PDSComponent pDynamicFormProps;
	private DynamicForm dmMain;

	private BooleanItem biVertical;
	private BooleanItem biDinamycFormProps;

	public PDSGroupData() {

		com.smartgwt.client.widgets.events.ClickHandler tsbStateHandler = new com.smartgwt.client.widgets.events.ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Object objectSource = event.getSource();
				ttsbuttonClick(objectSource);
			}

		};
		ToolStrip toolStrip = new ToolStrip();

		tsbSave = createTSButton("[SKIN]/actions/save.png", tsbStateHandler,
				SelectionType.BUTTON, "Add Object", "selection", toolStrip);
		this.addMember(toolStrip);
		tsbSave.setSelected(false);
		dmMain = new DynamicForm();
		dmMain.setTitleOrientation(TitleOrientation.TOP);
		this.addMember(dmMain);
		biVertical = new BooleanItem("vertical", "Is component vertical");
		biDinamycFormProps = new BooleanItem("biDinamycFormProps",
				"Set dynamic form props");
		biDinamycFormProps.addChangedHandler(new ChangedHandler() {

			@Override
			public void onChanged(ChangedEvent event) {
				enebleDynamicForm();

			}
		});
		dmMain.setFields(biVertical, biDinamycFormProps);
		pComp = new PDSComponent(null, PropertyNames.PT_PANLE_GROUP, true);
		this.addMember(pComp);
		pDynamicFormProps = new PDSComponent(null,
				PropertyNames.PT_DYNAMICFORM, true);
		pDynamicFormProps.setGroupTitle("Dynamic form properties");
		pDynamicFormProps.setIsGroup(true);
		this.addMember(pDynamicFormProps);
		pDynamicFormProps.setDisabled(true);
		setDisabled(true);

	}

	protected void ttsbuttonClick(Object objectSource) {
		saveGroup();
	}

	private ToolStripButton createTSButton(String icon,
			com.smartgwt.client.widgets.events.ClickHandler tsbStateHandler,
			SelectionType actionType, String toolTip, String group_id,
			ToolStrip toolStrip) {
		ToolStripButton tsbButton = new ToolStripButton();
		tsbButton.setIcon(icon);
		tsbButton.addClickHandler(tsbStateHandler);
		tsbButton.setTooltip(toolTip);
		tsbButton.setActionType(actionType);
		tsbButton.setRadioGroup(group_id);
		toolStrip.addButton(tsbButton);
		return tsbButton;
	}

	public void saveGroup() {

		group.setVertical(ClientUtils.getBooleanValue(biVertical));
		pComp.saveData();
		if (!pDynamicFormProps.isDisabled())
			pDynamicFormProps.saveData();
		else
			group.setDynamicFieldProps(null);
	}

	public void setGroup(DSGroup group) {
		this.group = group;
		if (group == null) {
			biDinamycFormProps.setValue(false);
			pDynamicFormProps.setData(null);
			pComp.setData(null);
			enebleDynamicForm();
			dmMain.setValues(new TreeMap<String, Object>());
			setDisabled(true);
			return;
		}
		setDisabled(false);
		biVertical.setValue(group.getVertical());
		pComp.setData(group);
		biDinamycFormProps.setValue(group.getDynamicFieldProps() != null);
		pDynamicFormProps.setData(group.getDynamicFieldProps());
		enebleDynamicForm();

	}

	public void enebleDynamicForm() {
		Boolean checked = ClientUtils.getBooleanValue(biDinamycFormProps);
		pDynamicFormProps.setDisabled(checked == null || !checked);
		if (checked != null && checked && group != null) {
			if (group.getDynamicFieldProps() == null)
				group.setDynamicFieldProps(new DSComponent());

		} else {
			pDynamicFormProps.setData(group == null ? null : group
					.getDynamicFieldProps());
		}

	}
}

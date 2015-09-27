package com.rdcommon.client.components.ds;

import java.util.TreeMap;

import com.rdcommon.client.ClientUtils;
import com.rdcommon.client.CommonDialog;
import com.rdcommon.shared.ds.DSComponent;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.BooleanItem;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.layout.VLayout;

public class PDSComponent extends VLayout {
	private int type;
	private DSComponent comp;
	private DynamicForm dmMain;

	private TextItem tiTitle;
	private BooleanItem biHidden;
	private BooleanItem biReadOnly;

	public PDSComponent(final DSComponent comp, final int type,
			boolean additionalProps) {
		this.comp = comp;
		this.type = type;
		dmMain = new DynamicForm();
		dmMain.setTitleOrientation(TitleOrientation.TOP);
		dmMain.setWidth100();
		dmMain.setHeight100();
		this.addMember(dmMain);
		tiTitle = new TextItem("title", "Title");
		biHidden = new BooleanItem("hidden", "Is component hidden");
		biReadOnly = new BooleanItem("readOnly",
				"Is component readOnly(disabled)");
		ButtonItem biSHowAdditional = new ButtonItem("kkk",
				"Show add properties");
		biSHowAdditional.setVisible(additionalProps);
		biSHowAdditional.setStartRow(false);
		biSHowAdditional.setEndRow(false);
		dmMain.setFields(tiTitle, biHidden, biReadOnly, biSHowAdditional);
		setData(comp);
		biSHowAdditional.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				new CommonDialog(
						new PropsDlgPanel(PDSComponent.this.comp, type), true,
						300, 400, "Additional properties").show();
			}
		});
	}

	public DynamicForm getDmMain() {
		return dmMain;
	}

	public void setData(DSComponent comp) {
		this.comp = comp;
		if (comp != null) {
			tiTitle.setValue(comp.getTitle());
			biHidden.setValue(comp.getHidden());
			biReadOnly.setValue(comp.getReadOnly());
		} else {
			dmMain.setValues(new TreeMap<String, Object>());
		}
	}

	public void saveData() {
		if (comp != null) {
			comp.setHidden(ClientUtils.getBooleanValue(biHidden));
			comp.setReadOnly(ClientUtils.getBooleanValue(biReadOnly));
			comp.setTitle(tiTitle.getValueAsString());
		}
	}

}

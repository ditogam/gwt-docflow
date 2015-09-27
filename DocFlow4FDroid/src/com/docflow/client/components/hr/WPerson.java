package com.docflow.client.components.hr;

import com.common.client.WindowResultObject;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;

public class WPerson extends Window implements WindowResultObject {

	public static void showForm(Integer id, WindowResultObject valueSet) {
		WPerson c = new WPerson(id, valueSet);
		c.show();
	}

	private PersonItem personItem;
	private WindowResultObject valueSet;

	public WPerson(Integer id, WindowResultObject valueSet) {
		super();
		this.valueSet = valueSet;
		setMembersMargin(5);
		personItem = new PersonItem();
		personItem.setPersonId(id);
		this.addItem(personItem);
		HLayout hl = new HLayout();
		hl.setAlign(Alignment.RIGHT);
		IButton bSave = new IButton("Save", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				saveData();
			}

		});
		IButton bCancel = new IButton("Cancel");
		bCancel.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				destroy();
			}
		});
		hl.addMember(bSave);
		hl.addMember(bCancel);
		hl.setWidth100();
		hl.setMembersMargin(10);
		hl.setHeight("30");
		Label l = new Label();
		l.setTitle("");
		l.setContents("");
		l.setWidth(0);
		hl.addMember(l);
		this.addItem(hl);
		setHeight(600);
		setWidth(700);
		this.setCanDragResize(true);
		this.setShowMinimizeButton(false);

		this.setIsModal(true);
		this.setShowFooter(true);

		this.centerInPage();
		// createKeyPressElement(this);
	}

	protected void saveData() {
		personItem.saveData(this);
	}

	@Override
	public void setResult(Object obj) {
		valueSet.setResult(obj);

	}

}

package com.docflow.client.components.docflow;

import com.docflow.client.DocFlow;
import com.docflow.shared.common.FormDefinition;
import com.docflow.shared.docflow.DocType;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.PickerIcon;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.FormItemClickHandler;
import com.smartgwt.client.widgets.form.fields.events.FormItemIconClickEvent;
import com.smartgwt.client.widgets.layout.HLayout;

public class WDocType extends Window {

	public static void showIt(final DocType dt) {
		WDocType w = new WDocType(dt);
		w.show();

	}

	public TextAreaItem taiXML;
	private TextAreaItem taiCustSQL;

	private TextItem tiCustSelectionFields;
	private TextItem tiReader;

	private CPDocType cp;

	private DocType dt;

	public WDocType(DocType dt) {

		super();
		this.dt = dt;
		setMembersMargin(5);
		setTitle("დოკუმენტის ტიპი");

		setWidth(500);
		StaticTextItem stTextItem = new StaticTextItem("stTextItem",
				"დოკუმენტის ტიპი:");
		stTextItem.setValue(dt.getId() + "--" + dt.getDoctypegroupvalue());
		taiXML = new TextAreaItem("taiXML", "XML");
		taiXML.setValue(dt.getDoc_template());
		taiXML.setWidth("100%");
		taiXML.setHeight("300");
		cp = CPDocType.getCustomType(dt);
		if (cp == null) {
			PickerIcon editPicker = new PickerIcon(PickerIcon.SEARCH,
					new FormItemClickHandler() {
						public void onFormItemClick(FormItemIconClickEvent event) {
							String xml = taiXML.getValueAsString();
							try {
								FormDefinition fd = new FormDefinition();
								fd.setXml(xml);
								WDocTypeXMLCaptions.showIt(xml, WDocType.this);
							} catch (Exception e) {
								setResult(e);
								return;
							}

						}
					});
			taiXML.setIcons(editPicker);
		}
		taiCustSQL = new TextAreaItem("taiCustSQL", "CustSelectXML");
		taiCustSQL.setWidth("100%");
		taiCustSQL.setHeight("200");
		taiCustSQL.setCanEdit(cp == null);
		taiCustSQL.setValue(dt.getCust_sql());
		tiCustSelectionFields = new TextItem("tiCustSelectionFields",
				"CustSelectFields");
		tiCustSelectionFields.setWidth("100%");
		tiCustSelectionFields.setValue(dt.getCust_selectfields());
		tiReader = new TextItem("tiReader", "Reader");
		tiReader.setWidth("100%");
		tiReader.setValue(dt.getRealdoctypeid());

		DynamicForm df = new DynamicForm();
		df.setAlign(Alignment.LEFT);
		df.setTitleOrientation(TitleOrientation.TOP);
		df.setWidth100();
		df.setNumCols(1);
		df.setItems(stTextItem, taiXML, taiCustSQL, tiCustSelectionFields,
				tiReader);
		df.setAutoHeight();

		this.addItem(df);
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
		setHeight(800);
		setWidth(800);
		this.setCanDragResize(true);
		this.setShowMinimizeButton(false);
		// this.setShowCloseButton(false);
		this.setIsModal(true);
		this.setShowFooter(true);
		// this.setShowModalMask(true);
		this.centerInPage();
	}

	private void saveData() {

		String xml = taiXML.getValueAsString();
		if (cp == null)
			try {
				FormDefinition fd = new FormDefinition();
				fd.setXml(xml);
			} catch (Exception e) {
				setResult(e);
				return;
			}
		dt.setDoc_template(xml);
		dt.setCust_sql(taiCustSQL.getValueAsString());
		dt.setCust_selectfields(tiCustSelectionFields.getValueAsString());
		dt.setRealdoctypeid(tiReader.getValueAsString());
		DocFlow.docFlowService.saveDocType(dt, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				setResult(caught);

			}

			@Override
			public void onSuccess(Void result) {
				setResult(null);

			}
		});
	}

	private void setResult(Throwable caught) {
		if (caught != null) {
			SC.say("შეცდომა", caught.getMessage(), new BooleanCallback() {
				@Override
				public void execute(Boolean value) {

				}
			});
			return;
		} else {
			if (cp != null)
				CustomPanel.destroyAndRecreate(dt.getId());
			destroy();
		}
	}
}

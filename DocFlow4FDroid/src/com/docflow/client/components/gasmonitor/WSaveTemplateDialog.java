package com.docflow.client.components.gasmonitor;

import com.docflow.client.DocFlow;
import com.docflow.client.components.SavePanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.VisibilityChangedEvent;
import com.smartgwt.client.widgets.events.VisibilityChangedHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.layout.VLayout;

public class WSaveTemplateDialog extends Window {

	private DynamicForm dfMapObject;
	private TextItem tiTemplateName;
	private SelectItem cbTemplates;
	private String data;

	public WSaveTemplateDialog(String data, SelectItem cbTemplates) {
		this.data = data;
		this.cbTemplates = cbTemplates;
		VLayout vlMain = new VLayout();

		dfMapObject = new DynamicForm();
		if (dfMapObject != null)
			vlMain.addMember(dfMapObject);
		dfMapObject.setTitleOrientation(TitleOrientation.TOP);
		dfMapObject.setNumCols(1);

		tiTemplateName = new TextItem("tiTemplateName", "Template name");
		tiTemplateName.setRequired(true);
		tiTemplateName.setWidth(250);
		dfMapObject.setFields(tiTemplateName);
		vlMain.setHeight100();
		vlMain.setWidth100();

		this.addItem(vlMain);

		SavePanel savePanel = new SavePanel("Add", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				saveData();
			}

		}, "Close", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}

		});
		vlMain.addMember(savePanel);
		this.setHeight(120);
		this.setWidth(300);
		this.setCanDragResize(true);
		this.setShowMinimizeButton(false);
		this.setIsModal(true);
		this.setShowFooter(true);
		this.centerInPage();
		this.setTitle("შაბლონის შენახვა");
		show();
		addVisibilityChangedHandler(new VisibilityChangedHandler() {

			@Override
			public void onVisibilityChanged(VisibilityChangedEvent event) {
				if (!event.getIsVisible())
					destroy();
			}
		});
	}

	protected void saveData() {
		if (!tiTemplateName.validate())
			return;
		String value = tiTemplateName.getValueAsString();
		value = value.trim();
		if (value.length() == 0) {
			tiTemplateName.setValue((String) null);
			tiTemplateName.validate();
			return;
		}
		destroy();
		try {
			Record rec = new Record();
			rec.setAttribute("mtemplate_name", value);
			rec.setAttribute("mtemplatedata", data);
			rec.setAttribute("user_id", DocFlow.user_id);
			DocFlow.getDataSource("GasMonitorTemplateDS").addData(rec,
					new DSCallback() {

						@Override
						public void execute(DSResponse response,
								Object rawData, DSRequest request) {
							DSRequest req = new DSRequest();
							req.setAttribute("_UUUUUUUIDUUU",
									HTMLPanel.createUniqueId());
							cbTemplates.fetchData(new DSCallback() {

								@Override
								public void execute(DSResponse response,
										Object rawData, DSRequest request) {
									// TODO Auto-generated method stub

								}
							}, req);
							if (response.getData() != null
									&& response.getData().length != 0) {
								try {
									int id = Integer.parseInt(response
											.getData()[0].getAttribute(
											"mtemplate_id").trim());
									cbTemplates.setValue(id);
								} catch (Exception e) {
									// TODO: handle exception
								}
							}
						}
					});

		} catch (Exception e) {
			SC.warn(e.getMessage());
		}
	}
}

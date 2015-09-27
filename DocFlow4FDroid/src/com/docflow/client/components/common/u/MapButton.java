package com.docflow.client.components.common.u;

import java.util.HashMap;

import org.gwtopenmaps.openlayers.client.format.WKT;

import com.docflow.client.DocFlow;
import com.docflow.client.components.common.FieldDefinitionItem;
import com.docflow.client.components.common.FormDefinitionPanel;
import com.docflow.client.components.common.SplashDialog;
import com.docflow.client.components.docflow.DocumentDetailTabPane;
import com.docflow.client.components.map.Constants;
import com.docflow.shared.common.FieldDefinition;
import com.docflow.shared.docflow.DocType;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;

public class MapButton extends ButtonItem {
	private FormDefinitionPanel panel;
	private FieldDefinition field;
	private String displayIcon;

	public MapButton(FormDefinitionPanel panel, FieldDefinition field) {
		super();
		this.panel = panel;
		this.field = field;
		setStartRow(false);
		setEndRow(false);
		setShowTitle(false);
		setTitle("map/google_earth.png");
		addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				showMap();

			}

		});
	}

	public String getDisplayIcon() {
		return displayIcon;
	}

	@Override
	public void setTitle(String title) {
		super.setTitle("");
		String images = "images/";
		if (title.startsWith(images))
			title = title.substring(images.length());

		displayIcon = title;
		setIcon(displayIcon);
		if (!displayIcon.startsWith("http")
				|| !displayIcon.startsWith("images/"))
			displayIcon = "images/" + displayIcon;

	}

	@Override
	public String getDisplayValue() {
		return field.getDisplayValue();
	}

	@Override
	public void setValue(String value) {
		super.setValue(value);
		value = value == null ? "" : value;
		value = value.trim();
		if (value.isEmpty())
			super.setTitle("");
		else
			super.setTitle(getDisplayValue());
	}

	@Override
	public void setValue(Object value) {
		setValue(value == null ? (String) null : value.toString().trim());
	}

	private void showMap() {

		try {
			WKT kml = new WKT();
			kml.read(getValue().toString())[0].getCenterLonLat();
			WMapDialog.showMapDialog(MapButton.this);
			return;

		} catch (Exception e) {
			// TODO: handle exception
		}

		final DocumentDetailTabPane pane = DocumentDetailTabPane.documentDetails;
		Integer customer_id = pane.seletedCustomer;
		String _subregion_id = null;
		if (customer_id == null) {
			DocType dt = pane.getDocType();
			if (dt.isApplied_customer()) {
				SC.say("Please select customer!!!", new BooleanCallback() {
					@Override
					public void execute(Boolean value) {
						pane.iiCustomer.focusInItem();
					}
				});
				return;
			} else {
				HashMap<String, FieldDefinitionItem> map = panel
						.getFormitemMap();
				FieldDefinitionItem sri = null;
				for (String key : map.keySet()) {
					FieldDefinitionItem item = map.get(key);
					if ((item.getFieldDef().getDefaultValue() != null && item
							.getFieldDef().getDefaultValue().trim()
							.equals("$subregionId"))
							|| key.toLowerCase().equals(
									"subregionId".toLowerCase())
							|| key.toLowerCase().equals(
									"subregion_Id".toLowerCase())) {
						sri = map.get(key);
						break;
					}
				}
				if (sri != null) {
					Object subregion_id = sri.getFormItem().getValue();
					if (subregion_id == null
							|| subregion_id.toString().trim().isEmpty()
							|| subregion_id.toString().trim().equals("-1")) {
						final FormItem fi = sri.getFormItem();
						SC.say("Please select subregion!!!",
								new BooleanCallback() {
									@Override
									public void execute(Boolean value) {
										fi.focusInItem();
									}
								});
						return;
					}
					_subregion_id = subregion_id.toString();
				}
			}

		}
		SplashDialog.showSplash();
		DocFlow.docFlowService.getCenterCoordinates(customer_id, _subregion_id,
				Constants.GOOGLE_SRID + "", new AsyncCallback<String>() {

					@Override
					public void onSuccess(String result) {
						setValue(result);
						SplashDialog.hideSplash();
						WMapDialog.showMapDialog(MapButton.this);
					}

					@Override
					public void onFailure(Throwable caught) {
						SplashDialog.hideSplash();

					}
				});

	}
}

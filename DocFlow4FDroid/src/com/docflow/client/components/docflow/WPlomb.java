package com.docflow.client.components.docflow;

import java.util.Map;

import com.docflow.client.ClientUtils;
import com.docflow.client.components.SavePanel;
import com.docflow.shared.ClSelection;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.VisibilityChangedEvent;
import com.smartgwt.client.widgets.events.VisibilityChangedHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.IntegerItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.layout.VLayout;

public class WPlomb extends Window {
	private DynamicForm dfPlomb;

	private Record record;
	ListGrid lg;
	private Integer user_id;

	public WPlomb(Record record, ListGrid lg, Integer user_id) {
		this.record = record;
		this.lg = lg;
		this.user_id = user_id;
		SelectItem siColor = new SelectItem("color_id", "Color");
		ClientUtils.fillSelectionCombo(siColor, ClSelection.T_PLOMB_COLOR);
		siColor.setRequired(true);
		SelectItem sDistributor = new SelectItem("distributor_id",
				"Distributor");
		ClientUtils.fillSelectionCombo(sDistributor,
				ClSelection.T_PLOMB_DISTRIBUTOR);
		sDistributor.setRequired(true);

		IntegerItem iiMinValue = new IntegerItem("min_value", "Min value");
		iiMinValue.setRequired(true);
		IntegerItem iiMaxValue = new IntegerItem("max_value", "Max value");
		iiMaxValue.setRequired(true);

		dfPlomb = new DynamicForm();
		dfPlomb.setNumCols(4);
		dfPlomb.setFields(siColor, sDistributor, iiMinValue, iiMaxValue);

		SavePanel savePanel = new SavePanel("Save", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				saveData();
			}

		}, "Close", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				destroy();
			}

		});
		VLayout vlMain = new VLayout();
		vlMain.setHeight100();
		vlMain.setWidth100();
		VLayout hlMain = new VLayout();
		hlMain.setHeight100();
		hlMain.setWidth100();
		hlMain.addMember(dfPlomb);

		vlMain.addMember(hlMain);
		this.setHeight(130);
		this.setWidth(520);
		this.setTitle("Add/Edit Plomb");
		this.setCanDragResize(true);
		this.setShowMinimizeButton(false);
		this.setIsModal(true);
		this.setShowFooter(true);
		this.centerInPage();

		vlMain.addMember(savePanel);
		this.addItem(vlMain);
		if (record != null)
			dfPlomb.setValues(record.toMap());
		addVisibilityChangedHandler(new VisibilityChangedHandler() {
			@Override
			public void onVisibilityChanged(VisibilityChangedEvent event) {
				if (!event.getIsVisible()) {
					destroy();
				}

			}
		});
	}

	@SuppressWarnings("unchecked")
	protected void saveData() {
		if (!dfPlomb.validate())
			return;

		if (record == null)
			record = new Record();
		Map<String, Object> map = ClientUtils.fillMapFromForm(record.toMap(),
				dfPlomb);
		map.put("userid", user_id);
		DSCallback cb = new DSCallback() {

			@Override
			public void execute(DSResponse response, Object rawData,
					DSRequest request) {
				destroy();

			}
		};

		try {
			if (map.containsKey("id"))
				lg.updateData(new Record(map), cb);
			else
				lg.addData(new Record(map), cb);
		} catch (Exception e) {
			SC.warn(e.getMessage());
		}
	}
}

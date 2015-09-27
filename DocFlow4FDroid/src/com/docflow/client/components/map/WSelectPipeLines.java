package com.docflow.client.components.map;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import com.docflow.client.ClientUtils;
import com.docflow.client.DocFlow;
import com.docflow.client.FormItemDescr;
import com.docflow.client.components.SavePanel;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.SelectionType;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.VisibilityChangedEvent;
import com.smartgwt.client.widgets.events.VisibilityChangedHandler;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.form.fields.PickerIcon;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.events.FormItemClickHandler;
import com.smartgwt.client.widgets.form.fields.events.FormItemIconClickEvent;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

public class WSelectPipeLines extends Window {

	private ListGrid lgListGrid;

	public WSelectPipeLines(final PPipeLine mp, Record[] records) {
		ToolStrip tsMain = new ToolStrip();
		this.setTitle("მილების გაერთიანება");
		lgListGrid = new ListGrid();
		PPipeLine.createListGrid(lgListGrid);
		if (records != null) {
			final ToolStripButton tsbRemove = new ToolStripButton();
			com.smartgwt.client.widgets.events.ClickHandler tsbStateHandler = new com.smartgwt.client.widgets.events.ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					if (event.getSource().equals(tsbRemove))
						mp.removeData(lgListGrid, new ArrayList<String>(), false);
				}
			};

			tsbRemove.setIcon("[SKIN]/actions/remove.png");
			tsbRemove.addClickHandler(tsbStateHandler);
			tsbRemove.setTooltip("Remove data");
			tsbRemove.setActionType(SelectionType.BUTTON);
			tsbRemove.setSelected(false);
			tsMain.addButton(tsbRemove);
			this.addItem(tsMain);
		}

		lgListGrid.setHeight100();

		if (records == null) {

			Record record = new Record();
			if (DocFlow.user_obj.getUser().getRegionid() >= 1)
				record.setAttribute("regid", DocFlow.user_obj.getUser().getRegionid());

			if (DocFlow.user_obj.getUser().getSubregionid() >= 1)
				record.setAttribute("raiid", DocFlow.user_obj.getUser().getSubregionid());

			Integer subregionid = record.getAttributeAsInt("raiid");
			final PBuildingsForm form = new PBuildingsForm(record, null, false);

			Map<String, Object> subRegionCrit = new TreeMap<String, Object>();
			if (subregionid != null) {
				subRegionCrit.put("parentId", subregionid);
			}
			final ComboBoxItem siGroupNames = new ComboBoxItem("siGroupNames", "Group names");
			ClientUtils.fillCombo(siGroupNames, "PipeLineDS", "getGroupNames", "group_id", "group_name", subRegionCrit);
			ClientUtils.makeDependancy(form.siSubregion, true, new FormItemDescr(siGroupNames));
			form.setNumCols(6);
			siGroupNames.setColSpan(6);
			siGroupNames.setWidth("100%");
			siGroupNames.setAddUnknownValues(false);

			PickerIcon searchPicker = new PickerIcon(PickerIcon.SEARCH, new FormItemClickHandler() {
				public void onFormItemClick(FormItemIconClickEvent event) {
					Record rec = siGroupNames.getSelectedRecord();
					if (rec == null)
						return;
					Integer group_id = rec.getAttributeAsInt("group_id");
					lgListGrid.selectAllRecords();
					lgListGrid.removeSelectedData();
					Criteria cr = new Criteria();
					if (group_id != null)
						cr.setAttribute("group_id", group_id);
					cr.setAttribute("to_srid", Constants.GOOGLE_SRID);
					cr.setAttribute("id", -1);
					DSCallback cb = new DSCallback() {

						@Override
						public void execute(DSResponse response, Object rawData, DSRequest request) {
							form.setValue("full_length", PPipeLine.addData(lgListGrid, response.getData(), true, null));

						}
					};
					ClientUtils.fetchData(cr, cb, "PipeLineDS", "getPipeLinesById");
				}
			});
			siGroupNames.setIcons(searchPicker);
			form.setFields(form.getField("regid"), form.siSubregion, new StaticTextItem("full_length","FullLength"), siGroupNames);
			this.addItem(form);
		}
		this.addItem(lgListGrid);
		if (records != null) {
			SavePanel savePanel = new SavePanel("Save", new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					saveData(mp);
				}

			}, "Close", new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					destroy();
				}

			});
			this.addItem(savePanel);
		}
		this.setHeight(700);
		this.setWidth(800);
		this.setCanDragResize(true);
		this.setShowMinimizeButton(false);
		this.setIsModal(true);
		this.setShowFooter(true);
		this.centerInPage();

		addVisibilityChangedHandler(new VisibilityChangedHandler() {
			@Override
			public void onVisibilityChanged(VisibilityChangedEvent event) {
				if (!event.getIsVisible()) {
					destroy();
				}

			}
		});
		if (records != null)
			mp.addData(lgListGrid, records, false, null);
	}

	protected void saveData(final PPipeLine mp) {
		try {
			lgListGrid.clearCriteria();
		} catch (Exception e) {
			// TODO: handle exception
		}
		mp.addData(mp.lgListGrid, lgListGrid.getRecords(), true, null);
		destroy();
	}

}

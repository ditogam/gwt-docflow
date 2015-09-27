package com.docflow.client.components.hr;

import java.util.HashMap;
import java.util.Map;

import com.docflow.client.DocFlow;
import com.docflow.client.components.CardLayoutCanvas;
import com.docflow.shared.hr.Captions;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.events.DrawEvent;
import com.smartgwt.client.widgets.events.DrawHandler;
import com.smartgwt.client.widgets.grid.CellFormatter;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.RecordClickEvent;
import com.smartgwt.client.widgets.grid.events.RecordClickHandler;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.ClickHandler;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeGridField;
import com.smartgwt.client.widgets.tree.TreeNode;
import com.smartgwt.client.widgets.tree.events.FolderDropEvent;
import com.smartgwt.client.widgets.tree.events.FolderDropHandler;

public class HRStructureTree extends TreeGrid implements LanguageValueSet {
	private boolean openonce = false;
	private MenuItem miAddStructure;
	private MenuItem miAddPerson;

	public HRStructureTree() {
		DataSource dsHRStructureDS = DocFlow.getDataSource("HRStructureDS");
		this.setDataSource(dsHRStructureDS);
		this.setCriteria(new Criteria("language_id", DocFlow.language_id + ""));
		setFolderIcon(null);
		TreeGridField nameField = new TreeGridField("item_name", "სტრუქტურა");
		nameField.setCellFormatter(new CellFormatter() {

			@Override
			public String format(Object value, ListGridRecord record,
					int rowNum, int colNum) {
				Integer item_type_id = record.getAttributeAsInt("item_type_id");
				if (value == null)
					value = "";
				String iconname = item_type_id == 1 ? "folder_out.png"
						: "person_open.png";
				value = "<img src=\"/images/icons/16/" + iconname
						+ "\" alt=\"logo\">" + value;
				return value.toString();
			}
		});
		this.setFields(nameField);
		setCanReorderRecords(true);
		this.addRecordClickHandler(new RecordClickHandler() {

			@Override
			public void onRecordClick(RecordClickEvent event) {
				recordSelected(event.getRecord());
			}
		});
		addDrawHandler(new DrawHandler() {
			@Override
			public void onDraw(DrawEvent event) {
				fetchDataR();
			}
		});

		setCanEdit(false);
		setAutoSaveEdits(false);
		Menu m = new Menu();
		miAddStructure = new MenuItem("სტრუქტურის დამატება");
		miAddStructure.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(MenuItemClickEvent event) {
				addItem(true, 1);

			}
		});
		miAddPerson = new MenuItem("პიროვნების დამატება");
		miAddPerson.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(MenuItemClickEvent event) {
				addItem(true, 2);

			}
		});
		// MenuItem miEdit = new MenuItem("შეცვლა");
		// miEdit.addClickHandler(new ClickHandler() {
		//
		// @Override
		// public void onClick(MenuItemClickEvent event) {
		// addItem(false, 0);
		//
		// }
		// });
		m.setItems(miAddStructure, miAddPerson/* , miEdit */);
		this.setContextMenu(m);
		addFolderDropHandler(new FolderDropHandler() {

			@Override
			public void onFolderDrop(FolderDropEvent event) {
				System.out.println(event.getSource());

			}
		});

	}

	private int parentid;
	private Record rec;
	private int type;

	public void addItem(boolean add, int type) {
		rec = getSelectedRecord();
		if (rec == null)
			return;
		Integer item_type = rec.getAttributeAsInt("item_type_id");
		if (item_type == null)
			return;
		if (item_type.intValue() != 1 && type == 1)
			return;
		this.type = type;
		parentid = rec.getAttributeAsInt("item_id");
		Tree tree = getData();
		TreeNode node = (TreeNode) getSelectedRecord();
		tree.openFolder(node);
		// WAddEditStructure.showWindow(add, parentid, type, this);
		//
		WCaptions.showForm(true, null, "", HRStructureTree.this,
				new HashMap<Integer, Captions>());

	}

	private void fetchDataR() {
		openonce = false;
		Criteria cr = getCriteria();
		if (cr == null)
			cr = new Criteria("language_id", DocFlow.language_id + "");
		fetchData(cr, new DSCallback() {
			@Override
			public void execute(DSResponse response, Object rawData,
					DSRequest request) {
				if (!openonce) {
					openonce = true;
					getData().openAll();
				}
			}
		});
	}

	private void recordSelected(Record rec) {
		int id = -1;
		try {
			id = rec.getAttributeAsInt("object_id");
		} catch (Exception e) {
			// TODO: handle exception
		}
		if (rec.getAttributeAsInt("item_type_id") == 1) {
			miAddStructure.setEnabled(true);
			miAddPerson.setEnabled(true);
			PStructure.structure.setId(id, this, rec);
			DocFlow.docFlow.card.showCard(CardLayoutCanvas.DEPARTMENT_PANEL);
		} else {
			miAddStructure.setEnabled(false);
			miAddPerson.setEnabled(false);
			PJob_Position.job_Position.setId(id, this, rec);
			DocFlow.docFlow.card.showCard(CardLayoutCanvas.JOB_POSSITION_PANEL);
		}

	}

	@Override
	public void setValue(Long id, String value) {
		Map<Object, Object> map = new HashMap<Object, Object>();
		map.put("item_parent_id", parentid);
		map.put("item_type_id", type);
		map.put("item_name", value);
		map.put("item_name_id", id);
		map.put("language_id", DocFlow.language_id);
		this.startEditingNew(map);
		this.saveAllEdits();

	}
}

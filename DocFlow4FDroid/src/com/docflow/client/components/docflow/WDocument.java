package com.docflow.client.components.docflow;

import com.docflow.client.components.map.PParentMeter;
import com.docflow.client.components.map.WSelectParentMeters;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.SelectionType;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

public class WDocument extends Window {
	private DocumentDetailTabPane panel;

	public WDocument(DocumentDetailTabPane panel) {
		this.panel = panel;
		setTitle("Document");
		setWidth100();
		setHeight100();
		setShowMinimizeButton(false);
		setShowCloseButton(false);
		setCanDragReposition(false);
		setCanDragResize(false);
		setShowShadow(false);
		this.setIsModal(true);
		ToolStrip tsMain = new ToolStrip();
		tsMain.setWidth100();
		final ToolStripButton tsbSave = new ToolStripButton("Save");
		final ToolStripButton tsbCancel = new ToolStripButton("Close");

		com.smartgwt.client.widgets.events.ClickHandler tsbStateHandler = new com.smartgwt.client.widgets.events.ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {

				if (event.getSource().equals(tsbSave)) {
					WDocument.this.panel.saveDocument();
				}

				if (event.getSource().equals(tsbCancel)) {
					close();
				}

			}
		};

		tsbSave.setIcon("[SKIN]/actions/save.png");
		tsbSave.addClickHandler(tsbStateHandler);
		tsbSave.setTooltip("Save");
		tsbSave.setActionType(SelectionType.BUTTON);
		tsbSave.setSelected(false);
		tsMain.addButton(tsbSave);

		tsbCancel.setIcon("[SKIN]/actions/close.png");
		tsbCancel.addClickHandler(tsbStateHandler);
		tsbCancel.setTooltip("Close");
		tsbCancel.setActionType(SelectionType.BUTTON);
		tsbCancel.setSelected(false);
		tsMain.addButton(tsbCancel);
		addItem(tsMain);
	}

	private boolean closeEvent = false;

	public void setCloseEvent(CloseClickHandler event) {
		if (closeEvent)
			return;
		closeEvent = true;
		addCloseClickHandler(event);
	}

	public void setPane(Canvas canvas) {
		canvas.setWidth100();
		canvas.setHeight("90%");
		addItem(canvas);
	}
}

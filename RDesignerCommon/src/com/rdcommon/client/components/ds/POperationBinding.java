package com.rdcommon.client.components.ds;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.XMLParser;
import com.rdcommon.client.CommonSavePanel;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;

public class POperationBinding extends CommonSavePanel {
	private TextAreaItem taiOpBinding;
	private DynamicForm dmMain;
	private GROperationBindings bindings;
	private Record record;

	public POperationBinding(GROperationBindings bindings, Record record,
			Object obj) {
		this.bindings = bindings;
		this.record = record;
		taiOpBinding = new TextAreaItem("taiOpBinding", "XML text");
		taiOpBinding.setRequired(true);
		taiOpBinding.setValue(obj);
		taiOpBinding.setWidth("519");
		taiOpBinding.setHeight("400");

		dmMain = new DynamicForm();
		dmMain.setTitleOrientation(TitleOrientation.TOP);
		dmMain.setFields(taiOpBinding);
		dmMain.setWidth100();
		dmMain.setHeight100();
		this.addMember(dmMain);
	}

	@Override
	public void saveData(Window win) throws Exception {
		if (!dmMain.validate()) {
			throw new Exception("Empty xml");
		}

		String xml = taiOpBinding.getValueAsString();
		Document doc = XMLParser.parse(xml);

		Node rootElem = doc.getChildNodes().item(0);
		// NodeList nodeList = rootElem.getChildNodes();

		String operationType = rootElem.getAttributes()
				.getNamedItem("operationType").getNodeValue();
		Node nOperationType = rootElem.getAttributes().getNamedItem(
				"operationId");

		if (nOperationType != null)
			operationType += " - " + nOperationType.getNodeValue();
		boolean insert = record == null;
		Record r = record;
		if (r == null)
			r = new Record();
		bindings.setDataValue(xml, r);
		r.setAttribute("operationBind", operationType);
		if (insert)
			bindings.listGrid.addData(r);
		else
			bindings.listGrid.refreshRow(bindings.listGrid.getRecordIndex(r));
	}

}

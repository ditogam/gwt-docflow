package com.docflow.client.components.docflow;

import java.util.ArrayList;

import com.docflow.shared.common.FieldDefinition;
import com.docflow.shared.common.FormDefinition;
import com.docflow.shared.common.FormGroup;
import com.smartgwt.client.widgets.layout.VLayout;

public class TestPanel extends VLayout {
	public TestPanel() {
		super();
		this.setWidth100();
		this.setHeight100();
		ArrayList<FieldDefinition> fields = new ArrayList<FieldDefinition>();
		FormDefinition formd = new FormDefinition();
		formd.setFormGroups(new ArrayList<FormGroup>());
		FormGroup fg = new FormGroup();
		fg.setFieldDefinitions(fields);
		formd.getFormGroups().add(fg);

		FieldDefinition fd = new FieldDefinition();
		fd.setFieldCaption("field1");
		fd.setFieldType(FieldDefinition.FT_STRING);
		fields.add(fd);
		fd = new FieldDefinition();
		fd.setFieldCaption("field2");
		fd.setFieldType(FieldDefinition.FT_INTEGER);
		fields.add(fd);
		fd = new FieldDefinition();
		fd.setFieldCaption("field3");
		fd.setFieldType(FieldDefinition.FT_STRING);
		fd.setFieldReadOnly(true);
		fields.add(fd);

		fields = new ArrayList<FieldDefinition>();
		fg = new FormGroup();
		fg.setFieldDefinitions(fields);
		formd.getFormGroups().add(fg);

		fd = new FieldDefinition();
		fd.setFieldCaption("field4");
		fd.setFieldType(FieldDefinition.FT_TEXTAREA);
		fd.setFieldReadOnly(true);
		fields.add(fd);
		fd = new FieldDefinition();
		fd.setFieldCaption("field5");
		fd.setFieldType(FieldDefinition.FT_SELECTION);
		fd.setFieldReadOnly(true);
		fields.add(fd);

		fields = new ArrayList<FieldDefinition>();
		fg = new FormGroup();
		fg.setFieldDefinitions(fields);
		formd.getFormGroups().add(fg);

		fd = new FieldDefinition();
		fd.setFieldCaption("region");
		fd.setFieldType(FieldDefinition.FT_SELECTION);
		fd.setFieldReadOnly(true);
		fields.add(fd);
		fd = new FieldDefinition();
		fd.setFieldCaption("district");
		fd.setFieldType(FieldDefinition.FT_SELECTION);
		fd.setFieldReadOnly(true);
		fields.add(fd);
		fd = new FieldDefinition();
		fd.setFieldCaption("city");
		fd.setFieldType(FieldDefinition.FT_SELECTION);
		fd.setFieldReadOnly(true);
		fields.add(fd);
		fd = new FieldDefinition();
		fd.setFieldCaption("street");
		fd.setFieldType(FieldDefinition.FT_SELECTION);
		fd.setFieldReadOnly(true);
		fields.add(fd);
		try {
			String str = formd.toXmlString();
			formd.setXml(str);
			str = formd.toXmlString();
			System.out.println(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// this.addMember(new FormDefinitionPanel(formd,
		// new SimpleFieldDefinitionListValue()));
	}
}

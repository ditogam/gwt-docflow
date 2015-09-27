package com.docflow.client.components.hr;

import com.docflow.client.DocFlow;
import com.docflow.shared.hr.Responcibility_types;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.form.DynamicForm;

public class Presponcibility_Type extends DynamicForm {
	private LanguageItem resp_type_name;
	private Responcibility_types rt;

	public Presponcibility_Type(Responcibility_types rt) {
		this.rt = rt;
		setTitleOrientation(TitleOrientation.TOP);
		resp_type_name = new LanguageItem("resp_type_name", DocFlow.getCaption(
				-50, "უფლებამოსილების ტიპი"));
		setFields(resp_type_name);
		if (rt != null) {
			resp_type_name.setValue(rt.getResp_type_name_id(),
					rt.getResp_type_name());
		}
	}

	public Responcibility_types getRt() throws Exception {
		if (resp_type_name.getId() == null
				|| resp_type_name.getId().intValue() <= 0)
			throw new Exception(DocFlow.getCaption(-50,
					"შეიყვანეთ უფლებამოსილების ტიპი!!!"));
		if (rt == null)
			rt = new Responcibility_types();
		rt.setResp_type_name(resp_type_name.getValueAsString());
		rt.setResp_type_name_id(resp_type_name.getId());
		return rt;
	}

}

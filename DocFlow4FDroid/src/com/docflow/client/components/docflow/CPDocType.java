package com.docflow.client.components.docflow;

import com.docflow.shared.docflow.DocType;
import com.smartgwt.client.widgets.Canvas;

public class CPDocType {

	private Canvas canvas;
	private int id;
	private DocType dt;
	private String[] jsElements;
	private String mainFunction;
	private String content;

	// private

	public static CPDocType getCustomType(DocType dt) {
		CPDocType cp = null;
		try {
			if (!dt.isJSType())
				return cp;
			cp = new CPDocType();
			cp.setDt(dt);
			cp.setId(dt.getId());
			cp.jsElements = dt.getRealdoctypeid().trim().split(",");
			cp.setContent(dt.getDoc_template().trim());
			cp.setMainFunction(cp.jsElements[0]);

		} catch (Exception e) {
			cp = null;
		}
		return cp;
	}

	public String[] getJsElements() {
		return jsElements;
	}

	public void setJsElements(String[] jsElements) {
		this.jsElements = jsElements;
	}

	public DocType getDt() {
		return dt;
	}

	public void setDt(DocType dt) {
		this.dt = dt;
	}

	public String getMainFunction() {
		return mainFunction;
	}

	public void setMainFunction(String mainFunction) {
		this.mainFunction = mainFunction;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Canvas getCanvas() {
		return canvas;
	}

	public void setCanvas(Canvas canvas) {
		this.canvas = canvas;
	}
}

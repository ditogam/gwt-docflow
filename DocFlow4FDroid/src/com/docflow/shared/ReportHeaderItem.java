package com.docflow.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ReportHeaderItem implements IsSerializable {

	/**
	 * 
	 */

	public static final int LEFT = 1;
	public static final int CENTER = 2;
	public static final int RIGHT = 3;
	private String title;
	private int orientation;
	private int cellorientation;
	private int width;

	public int getCellorientation() {
		return cellorientation;
	}

	public int getOrientation() {
		return orientation;
	}

	public String getTitle() {
		return title;
	}

	public int getWidth() {
		return width;
	}

	public void setCellorientation(int cellorientation) {
		this.cellorientation = cellorientation;
	}

	public void setOrientation(int orientation) {
		this.orientation = orientation;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setWidth(int width) {
		this.width = width;
	}

}

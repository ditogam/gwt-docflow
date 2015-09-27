package com.docflowdroid.comp;

public enum TitleOrientation implements ValueEnum {
	LEFT("left"), TOP("top"), RIGHT("right");
	private String value;

	TitleOrientation(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}
}
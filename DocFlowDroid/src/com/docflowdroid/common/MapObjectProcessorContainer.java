package com.docflowdroid.common;

public class MapObjectProcessorContainer {
	public IMapObjectProcessor mapObjectProcessor;
	public int image_id;
	public int text;

	public MapObjectProcessorContainer(IMapObjectProcessor mapObjectProcessor,
			int image_id, int text) {
		super();
		this.mapObjectProcessor = mapObjectProcessor;
		this.image_id = image_id;
		this.text = text;
	}

}

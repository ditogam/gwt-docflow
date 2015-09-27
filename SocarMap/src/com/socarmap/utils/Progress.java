package com.socarmap.utils;

public interface Progress {
	public void reset();

	public void stepIt();

	public void updateTitle(String title);
}

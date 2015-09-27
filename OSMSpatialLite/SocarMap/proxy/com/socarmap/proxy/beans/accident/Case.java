package com.socarmap.proxy.beans.accident;

import java.io.Serializable;
import java.util.ArrayList;

public class Case extends Accident_Default implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3686010431414898487L;

	private int iniciator_type;
	private ArrayList<Step> steps;

	public Case() {
	}

	public int getIniciator_type() {
		return iniciator_type;
	}

	public ArrayList<Step> getSteps() {
		return steps;
	}

	public void setIniciator_type(int iniciator_type) {
		this.iniciator_type = iniciator_type;
	}

	public void setSteps(ArrayList<Step> steps) {
		this.steps = steps;
	}

}

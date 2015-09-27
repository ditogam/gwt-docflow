package com.socarmap.proxy.beans;

import java.io.Serializable;
import java.util.HashMap;

public class Classifiers implements Serializable {
	private static final long serialVersionUID = 7329469926534925909L;
	private HashMap<Long, String> demage_types = new HashMap<Long, String>();
	private HashMap<Long, String> regions = new HashMap<Long, String>();
	private HashMap<Long, HashMap<Long, String>> subregions = new HashMap<Long, HashMap<Long, String>>();
	private HashMap<Long, HashMap<Long, String>> zones = new HashMap<Long, HashMap<Long, String>>();

	public HashMap<Long, String> getDemage_types() {
		return demage_types;
	}

	public Classifiers getForUser(UserContext uc) {
		if (uc.getSubregion_id() < 0 && uc.getRegion_id() < 0)
			return this;
		Classifiers r = new Classifiers();
		Long region_id = null;
		Long subregion_id = null;
		r.demage_types = demage_types;
		if (uc.getSubregion_id() >= 0) {

			for (Long key : subregions.keySet()) {
				HashMap<Long, String> mpSubregions = subregions.get(key);
				for (Long key1 : mpSubregions.keySet()) {
					if (key1.longValue() == uc.getSubregion_id()) {
						region_id = key;
						break;
					}
				}
				if (region_id != null)
					break;
			}

		} else if (uc.getRegion_id() >= 0) {
			region_id = (long) uc.getRegion_id();
		}
		if (region_id != null) {
			r.regions.put(region_id, regions.get(region_id));
			if (uc.getSubregion_id() >= 0) {
				HashMap<Long, String> mpSubregions = subregions.get(region_id);
				r.subregions.put(region_id, new HashMap<Long, String>());
				subregion_id = (long) uc.getSubregion_id();
				if (mpSubregions != null) {
					r.subregions.get(region_id).put(subregion_id,
							mpSubregions.get(subregion_id));
				}
			} else
				r.subregions.put(region_id, subregions.get(region_id));
			for (Long key : r.subregions.keySet()) {
				HashMap<Long, String> mpSubregions = r.subregions.get(key);
				for (Long key1 : mpSubregions.keySet()) {
					r.zones.put(key1, zones.get(key1));
				}
			}
		}
		return r;
	}

	public HashMap<Long, String> getRegions() {
		return regions;
	}

	public HashMap<Long, HashMap<Long, String>> getSubregions() {
		return subregions;
	}

	public HashMap<Long, HashMap<Long, String>> getZones() {
		return zones;
	}

	public void setDemage_types(HashMap<Long, String> demage_types) {
		this.demage_types = demage_types;
	}

	public void setRegions(HashMap<Long, String> regions) {
		this.regions = regions;
	}

	public void setSubregions(HashMap<Long, HashMap<Long, String>> subregions) {
		this.subregions = subregions;
	}

	public void setZones(HashMap<Long, HashMap<Long, String>> zones) {
		this.zones = zones;
	}
}

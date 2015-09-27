package com.rdcommon.shared.ds;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.google.gwt.user.client.rpc.IsSerializable;

public class DSCProp implements IsSerializable {

	protected Map<String, String> additionalProps;

	public Map<String, String> getAdditionalProps() {
		return additionalProps;
	}

	public void setAdditionalProps(Map<String, String> additionalProps) {
		this.additionalProps = additionalProps;
	}

	public void setValue(String key, String value) {
		checkAFExistance();
		additionalProps.put(key, value);
	}

	public void setValue(String key, String value, boolean exitIfExists) {
		checkAFExistance();
		if (exitIfExists && additionalProps.containsKey(key))
			return;
		setValue(key, value);
	}

	public void checkAFExistance() {
		if (additionalProps == null)
			additionalProps = new TreeMap<String, String>();
	}

	public void mergeProps(DSCProp p, DSCProp o) {
		DSCProp no = this;
		Map<String, String> pAdditionalProps = p.additionalProps;
		no.additionalProps = pAdditionalProps;
		Map<String, String> oAdditionalProps = o.additionalProps;
		if (pAdditionalProps == null || pAdditionalProps.isEmpty()) {
			no.additionalProps = oAdditionalProps;
		} else {
			if (oAdditionalProps != null && !oAdditionalProps.isEmpty()) {
				Set<String> keys = oAdditionalProps.keySet();
				for (String key : keys) {
					pAdditionalProps.put(key, oAdditionalProps.get(key));
				}
				no.additionalProps = pAdditionalProps;
			}
		}
	}

}

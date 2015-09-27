package com.docflow.client.components.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.common.shared.ClSelectionItem;
import com.docflow.client.DocFlow;
import com.docflow.shared.ClSelection;
import com.docflow.shared.PermissionNames;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;

public class AddressComponent {
	private class AddressSelectItem extends SelectItem {

		private int type;
		private AddressSelectItem[] subitems;

		public AddressSelectItem(String name, final int type,
				final AddressSelectItem[] subitems) {
			super(name);
			this.type = type;
			this.subitems = subitems;
			if (subitems != null && subitems.length > 0)
				this.addChangedHandler(new ChangedHandler() {

					@Override
					public void onChanged(ChangedEvent event) {

						Object currentvalue = getValue();
						if (!(currentvalue == null || currentvalue.toString()
								.equals("-1"))) {
							String currentId = currentvalue.toString();

							for (AddressSelectItem addressSelectItem : subitems) {
								String value = addressSelectItem.type + "_"
										+ type + "_" + currentId;
								HashMap<String, String> listSqls = new HashMap<String, String>();
								listSqls.put(addressSelectItem.type + "", value);
								DocFlow.docFlowService
										.getListTypesForDocument(
												listSqls,
												-1,
												new AsyncCallback<HashMap<String, ArrayList<ClSelectionItem>>>() {

													@Override
													public void onFailure(
															Throwable caught) {
														// TODO Auto-generated
														// method stub

													}

													@Override
													public void onSuccess(
															HashMap<String, ArrayList<ClSelectionItem>> result) {
														addSubItemValues(result);
													}
												});
							}
						} else {
							addSubItemValues(null);
						}
					}
				});
		}

		private void addSubItemValues(
				HashMap<String, ArrayList<ClSelectionItem>> values) {
			if (values == null) {
				values = new HashMap<String, ArrayList<ClSelectionItem>>();
			}
			for (AddressSelectItem addressSelectItem : subitems) {
				setSelectItems(addressSelectItem,
						values.get(addressSelectItem.type + ""));
			}

		}

	}

	private AddressSelectItem siRegion;
	private AddressSelectItem siSubregion;
	private AddressSelectItem siCity;

	private AddressSelectItem siStreet;

	public AddressComponent(boolean needStreet, boolean needCity,
			ArrayList<ClSelectionItem> regions) {

		if (needCity && needStreet)
			siStreet = new AddressSelectItem("siStreet", ClSelection.T_STREET,
					null);
		if (needCity)
			siCity = new AddressSelectItem("siCity", ClSelection.T_CITY,
					needStreet ? new AddressSelectItem[] { siStreet } : null);
		siSubregion = new AddressSelectItem("siSubregion",
				ClSelection.T_SUBREGION,
				needCity ? new AddressSelectItem[] { siCity } : null);
		siRegion = new AddressSelectItem("siRegion", ClSelection.T_REGION,
				new AddressSelectItem[] { siSubregion });
		if (regions == null) {
			HashMap<String, String> listSqls = new HashMap<String, String>();
			listSqls.put("" + ClSelection.T_REGION, "" + ClSelection.T_REGION);

			DocFlow.docFlowService
					.getListTypesForDocument(
							listSqls,
							-1,
							new AsyncCallback<HashMap<String, ArrayList<ClSelectionItem>>>() {

								@Override
								public void onFailure(Throwable caught) {

								}

								@Override
								public void onSuccess(
										HashMap<String, ArrayList<ClSelectionItem>> result) {
									setSelectItems(
											siRegion,
											result.get(""
													+ ClSelection.T_REGION));
									if (DocFlow.user_obj.getUser()
											.getRegionid() >= 0) {
										siRegion.setValue(""
												+ DocFlow.user_obj.getUser()
														.getRegionid());
										if (!DocFlow
												.hasPermition("CAN_VIEW_ALL_REGIONS")) {
											siRegion.setDisabled(true);

										}
									}
								}
							});

		} else
			setSelectItems(siRegion, regions);
		if (DocFlow.user_obj.getUser().getRegionid() >= 0) {
			siRegion.setValue("" + DocFlow.user_obj.getUser().getRegionid());
			if (!DocFlow.hasPermition("CAN_VIEW_ALL_REGIONS")) {
				siRegion.setDisabled(true);
				siSubregion.setDisabled(!DocFlow
						.hasPermition(PermissionNames.CAN_VIEW_ALL_SUBREGIONS));
			}

			HashMap<String, String> listSqls = new HashMap<String, String>();
			String value = ClSelection.T_SUBREGION + "_" + ClSelection.T_REGION
					+ "_" + DocFlow.user_obj.getUser().getRegionid();
			listSqls.put(ClSelection.T_SUBREGION + "", value);

			DocFlow.docFlowService
					.getListTypesForDocument(
							listSqls,
							-1,
							new AsyncCallback<HashMap<String, ArrayList<ClSelectionItem>>>() {

								@Override
								public void onFailure(Throwable caught) {
									// TODO Auto-generated method stub

								}

								@Override
								public void onSuccess(
										HashMap<String, ArrayList<ClSelectionItem>> result) {
									setSelectItems(
											siSubregion,
											result.get(ClSelection.T_SUBREGION
													+ ""));
									if (DocFlow.user_obj.getUser()
											.getSubregionid() >= 0) {
										siSubregion.setValue(""
												+ DocFlow.user_obj.getUser()
														.getSubregionid());
									}
								}
							});

		} else
			setSelectItems(siSubregion, null);
		if (needCity)
			setSelectItems(siCity, null);
		if (needStreet)
			setSelectItems(siStreet, null);

	}

	public SelectItem getSiCity() {
		return siCity;
	}

	public SelectItem getSiRegion() {
		return siRegion;
	}

	public SelectItem getSiStreet() {
		return siStreet;
	}

	public SelectItem getSiSubregion() {
		return siSubregion;
	}

	public void setSelectItems(FormItem si, ArrayList<ClSelectionItem> items) {
		if (items == null) {
			items = new ArrayList<ClSelectionItem>();
		}
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("-1", "---");
		for (ClSelectionItem item : items) {
			map.put(item.getId() + "", item.getValue());
		}
		si.setValueMap(map);
		si.setValue("-1");
	}

}

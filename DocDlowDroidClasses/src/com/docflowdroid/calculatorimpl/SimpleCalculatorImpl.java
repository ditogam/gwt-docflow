package com.docflowdroid.calculatorimpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.ImageButton;

import com.common.shared.ds.CDSRequest;
import com.common.shared.ds.CDSResponce;
import com.docflowdroid.ASimpleCalculator;
import com.docflowdroid.ActivityHelper;
import com.docflowdroid.DocFlowCommon;
import com.docflowdroid.common.BooleanCallback;
import com.docflowdroid.common.FieldDefinitionItem;
import com.docflowdroid.common.SC;
import com.docflowdroid.common.comp.IFormDefinitionPanel;
import com.docflowdroid.common.ds.DsOperationResult;

public class SimpleCalculatorImpl extends ASimpleCalculator {

	public SimpleCalculatorImpl(
			HashMap<String, FieldDefinitionItem> formitemMap,
			IFormDefinitionPanel definitionPanel) {
		super(formitemMap, definitionPanel);
	}

	public void check_private_number(FieldDefinitionItem current)
			throws Exception {
		if (current.getFormItem().getIcons() != null)
			return;
		ImageButton imgb = new ImageButton(current.getFormItem().getContext());
		imgb.setImageResource(DocFlowCommon.getResourceId("drawable.approved"));
		imgb.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					java.lang.Object cusname = getValue((FieldDefinitionItem) formitemMap
							.get("cusname"));
					java.lang.Object private_number = getValue((FieldDefinitionItem) formitemMap
							.get("private_number"));
					java.lang.String cusName = cusname.toString().trim()
							.split(" ")[0];
					java.lang.String url = "http://voters.cec.gov.ge/korp3TIM.php?pn="
							+ private_number
							+ "&gv="
							+ java.net.URLEncoder.encode(cusName, "UTF8");

					Map<String, Object> criteria = new HashMap<String, Object>();
					criteria.put("url", (Object) url);
					executeDs(new CDSRequest("GetHttpReqvestDS", criteria,
							"execHttpGet"), new DsOperationResult() {

						@Override
						public void operationResult(CDSResponce responce) {
							if (responce == null)
								return;
							if (responce.getTotalRows() < 1)
								return;
							if (responce.getResult() == null)
								return;
							if (responce.getResult().isEmpty())
								return;
							final Object content = responce.getResult().get(0)
									.get("content");
							if (content == null
									|| content.toString().trim().isEmpty())
								return;
							definitionPanel.getContextActivity().runOnUiThread(
									new Runnable() {
										@Override
										public void run() {
											ActivityHelper.showAlert(
													definitionPanel
															.getContextActivity(),
													content.toString(), true);

										}
									});

						}
					});

				} catch (java.lang.Throwable e) {
					ActivityHelper.showError(v.getContext(), e);
				}

			}
		});

		current.getFormItem().setIcons(new ImageButton[] { imgb });
	}

	public static String getMyName() {
		return "AVOEEEEEE";
	}

	public void mettercorrectionCombo(FieldDefinitionItem item) {
		String mserial = null;
		Object metterValue = getItem("meterid").getFormItem().getDisplayValue();
		try {
			mserial = metterValue.toString().split(":")[1];
		} catch (Exception e) {
			// Handle errors here
		}
		try {
			getItem("mserial").getFormItem().setValue(mserial);
		} catch (Exception err) {
			// Handle errors here
		}

		double meterid = getDoubleValue(getItem("meterid"));
		Map<String, Object> criteria = new HashMap<String, Object>();
		criteria.put("metterid", meterid);

		CDSRequest request = new CDSRequest("DBCDS_COMMON", criteria,
				"getMetterValue");

		DsOperationResult result = new DsOperationResult() {

			@Override
			public void operationResult(CDSResponce responce) {
				setMeterValues(responce.getResult());

			}
		};

		executeDs(request, result);

	}

	public void setMeterValues(List<Map<String, Object>> result) {
		if (result.size() < 1)
			return;
		Map<String, Object> rec = result.get(0);

		setFieldValue("moldvalue", rec.get("mettervalue"));
		setFieldValue("mnewvalue", rec.get("mettervalue"));
		setFieldValue("emetserial", rec.get("metserial"));
		setFieldValue("emettertype", rec.get("mtypeid"));

		setFieldValue("expensem3", 0);

		FieldDefinitionItem oldplombs = getItem("oldplombs");
		boolean withplombs = false;
		if (oldplombs != null)
			withplombs = true;
		if (withplombs) {
			double meterid = getDoubleValue(getItem("meterid"));
			Map<String, Object> criteria = new HashMap<String, Object>();
			criteria.put("metterid", meterid);

			CDSRequest request = new CDSRequest("DBCDS_COMMON", criteria,
					"getMetterPlombs");

			DsOperationResult rs = new DsOperationResult() {

				@Override
				public void operationResult(CDSResponce responce) {
					setPlombs(responce.getResult());

				}
			};

			executeDs(request, rs);

		}
	}

	public void setPlombs(List<Map<String, Object>> result) {
		if (result.size() < 1)
			result = new ArrayList<Map<String, Object>>();

		FieldDefinitionItem mnewvalue = getItem("oldplombs");
		if (mnewvalue == null)
			return;
		HashMap<Long, String> map = new HashMap<Long, String>();
		map.put(-1L, "---NEW----");
		for (int i = 0; i < result.size(); i++) {
			Map<String, Object> obj = result.get(i);
			map.put(Long.valueOf(obj.get("plombid").toString().trim()),
					obj.get("plombname") + ":" + obj.get("place") + ":"
							+ obj.get("status"));
		}

		mnewvalue.getFormItem().setValueMap(map);
		plombCombo(mnewvalue);

	}

	public void metterserialcheckself(FieldDefinitionItem item) {
		Object metserial = getFieldValue(item.getFieldDef().getFieldName());
		Map<String, Object> criteria = new HashMap<String, Object>();
		criteria.put("metserial", metserial);
		criteria.put("cusid", -1);

		CDSRequest request = new CDSRequest("DBCDS_COMMON", criteria,
				"getDublicatedMetters");

		DsOperationResult rs = new DsOperationResult() {

			@Override
			public void operationResult(CDSResponce responce) {
				setDublicated(responce.getResult());

			}
		};

		executeDs(request, rs);

	}

	protected void setDublicated(List<Map<String, Object>> result) {
		if (result.size() < 1)
			result = new ArrayList<Map<String, Object>>();

		String cdublicated = "";
		String dublicatedresult = "";
		if (result.size() >= 1) {
			cdublicated = "DUBLICATED";
			Map<String, Object> obj = result.get(0);
			String val = (String) obj.get("dublicated");
			Object cnt = (val.split("<tr>").length - 1);

			final String dubl = "ნაპოვნია " + cnt
					+ " მრიცხველი ქარხნული ნომრით:" + obj.get("metserial");
			definitionPanel.getContextActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {
					SC.say(definitionPanel.getContextActivity(), dubl,
							new BooleanCallback() {

								@Override
								public void execute(Boolean value) {

									definitionPanel.getContextActivity()
											.runOnUiThread(new Runnable() {

												@Override
												public void run() {
													FieldDefinitionItem item = getItem("metserial");
													if (item != null)
														item.getFormItem()
																.requestFocusSelf();

												}
											});

								}
							});

				}
			});

			dublicatedresult = val;
		}
		setFieldValue("cdublicated", cdublicated);
		dublicatedresult = Html.fromHtml(dublicatedresult).toString();
		setFieldValue("dublicatedresult", dublicatedresult);

	}

	public void metterserialcheck(final FieldDefinitionItem item) {
		metterserialcheckself(item);
		setOnFocusChangeValue(item, new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus)
					return;
				metterserialcheckself(item);
			}
		});

	}

	public void metterstateCombo(FieldDefinitionItem item) {
		String[] oldmaetterFields = { "meterid", "moldvalue", "mnewvalue",
				"expensem3", "inddaricx" };
		String[] oldmaetterNotReqFields = { "moldvalue", "mnewvalue",
				"expensem3", "inddaricx" };
		String[] newmaetterFields = { "metserial", "mettertype", "start_index",
				"cortypeid", "corserial", "montagedate", "corvalue" };
		String[] newmaetterNotReqFields = { "cortypeid", "corserial",
				"corvalue" };

		double state = getDoubleValue(item);

		try {
			setEditableAndRequered(oldmaetterFields,
					(state == 1.0 || state == 3.0), oldmaetterNotReqFields);

			setEditableAndRequered(newmaetterFields,
					(state == 1.0 || state == 2.0), newmaetterNotReqFields);
		} catch (Throwable ex) {
			ActivityHelper.showAlert(item.getFormItem().getContext(), ex);
		}
	}

	public void mettercorrectionReq(FieldDefinitionItem item) {
		calculateMetterInd(item);
	}

	public void calculateMetterInd(FieldDefinitionItem item) {
		boolean oldmetterenabled = false;
		try {
			FieldDefinitionItem formItem = getItem("meterid");
			boolean disabled = formItem.getFormItem().isDisabled();

			if (!disabled)
				oldmetterenabled = true;
			else
				oldmetterenabled = !disabled;
		} catch (Throwable ex) {
			ActivityHelper.showAlert(item.getFormItem().getContext(), ex);
		}

		boolean selected = oldmetterenabled;
		boolean field_isSelected = getBooleanValue(item);
		selected = selected && field_isSelected;
		String[] oldmaetterFields = { "mnewvalue", "expensem3" };

		String[] notrequered = {};
		setEditableAndRequered(oldmaetterFields, selected, notrequered);

	}

	public void showMetterM3(FieldDefinitionItem item) {
		final FieldDefinitionItem mnewvalue = getItem("mnewvalue");
		try {
			setOnFocusChangeValue(mnewvalue, new OnFocusChangeListener() {
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (!hasFocus) {
						double m3 = getDoubleValue(getItem("expensem3"));
						if (m3 < 0) {
							SC.say(mnewvalue.getFormItem().getContext(),
									"Negative", new BooleanCallback() {

										@Override
										public void execute(Boolean value) {
											if (mnewvalue != null) {

												mnewvalue.getFormItem()
														.requestFocusSelf();
											}
										}
									});
						}
					}

				}
			});

		} finally {
			double oldValue = getDoubleValue(getItem("moldvalue"));
			double newValue = getDoubleValue(getItem("mnewvalue"));
			Double m3 = newValue - oldValue;
			setFieldValue("expensem3", m3.intValue());
		}
	}

	public void ntwpriceAmountCalculate(final FieldDefinitionItem item) {
		setOnFocusChangeValue(item, new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				double value = getDoubleValue(item);
				if (value <= 0)
					item.getFormItem().setValue(null);
				ntwpricecalculatentwAmount();
			}
		});
		ntwpricecalculatentwAmount();
	}

	public void ntwpricecalculatentwAmount() {
		FieldDefinitionItem cntItem = getItem("cnt");
		FieldDefinitionItem amountItem = getItem("amount");

		if (cntItem == null || amountItem == null)
			return;
		int cnt = (int) getDoubleValue(cntItem);
		double amount = 0.0;
		for (int i = 0; i < cnt; i++) {
			String itemName = "nic" + (i + 1);

			FieldDefinitionItem valueItemS = getItem(itemName);
			itemName = "nip" + (i + 1);
			FieldDefinitionItem priceItemS = getItem(itemName);
			itemName = "nia" + (i + 1);
			FieldDefinitionItem amountItemS = getItem(itemName);
			double amounts = getDoubleValue(valueItemS)
					* getDoubleValue(priceItemS);
			if (amounts <= 0)
				amountItemS.getFormItem().setValue(null);
			else
				amountItemS.getFormItem().setValue(formatDouble(amounts));
			if (valueItemS != null)
				amount += amounts;
		}
		if (amount == 0.0)
			amountItem.getFormItem().setValue(null);
		else
			amountItem.getFormItem().setValue(formatDouble(amount));
	}

	public void mettercorrectionValue(FieldDefinitionItem item) {
		showMetterM3(item);
	}

	public void ntwpriceAmountSet(final FieldDefinitionItem item) {
		FieldDefinitionItem pricesItem = getItem("prices");
		FieldDefinitionItem cntItem = getItem("cnt");
		if (pricesItem == null)
			return;
		if (cntItem == null)
			return;

		Object pricesItemO = getValue(pricesItem);
		if (pricesItemO == null || pricesItemO.toString().length() == 0)
			return;
		String pricesItemS = pricesItemO.toString().trim();
		String priceitems[] = pricesItemS.split(",");
		int cnt = 0;
		for (String s : priceitems) {
			String[] items = s.split(";");
			if (items.length != 4)
				continue;
			String itemName = "nip" + items[0];
			FieldDefinitionItem priceItemS = getItem(itemName);
			if (priceItemS != null) {
				String title = Html.fromHtml(
						convertHexToString(items[1]) + " / "
								+ convertHexToString(items[2])).toString();
				priceItemS.getFormItem().setTitle(title);
				priceItemS.getFormItem().setValue(formatDouble(items[3]));
			}
			cnt++;
		}
		cntItem.getFormItem().setValue(cnt);
	}

	private void disableField(String field_name) {
		FieldDefinitionItem field = formitemMap.get(field_name);
		// SC.say(definitionPanel.getContextActivity(), "field name=="
		// + field_name + " item=" + field);
		if (field == null)
			return;

		field.getFormItem().setDisabled(true);
	}

	public void setAddressFieldsDisabled(FieldDefinitionItem current) {
		int region_id = DocFlowCommon.user_obj.getUser().getRegionid();
		int subregion_id = DocFlowCommon.user_obj.getUser().getSubregionid();
		if (region_id != -1)
			disableField("regionId");
		if (subregion_id != -1)
			disableField("subregionId");
		// SC.say(definitionPanel.getContextActivity(), "region_id=" + region_id
		// + " item=" + subregion_id);

	}

	public void plombCombo(FieldDefinitionItem current) {
		double value = getIntValue(current);

		boolean enable = value == -1 || value == 0;
		String fn = "plombnum";
		FieldDefinitionItem formItem = getItem(fn);

		if (formItem != null) {
			formItem.getFormItem().setDisabled(!enable);
			formItem.getFieldDef().setRequaiered(enable);
			if (!enable)
				setFieldValue(fn, current.getFormItem().getDisplayValue());
		}

	}

}

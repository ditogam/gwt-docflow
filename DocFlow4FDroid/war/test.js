function formatNumber(numb) {
	var nn = parseFloat(new String(numb));
	if (isNaN(nn))
		nn = 0.0;
	nn = nn.toFixed(2);
	var snum = new String(nn);
	var sec = snum.split('.');
	var whole = sec[0];
	var dec = sec[1];

	if (dec == '00')
		dec = '';
	if (dec.length > 0 && dec.charAt(dec.length - 1) == '0')
		dec = dec.charAt(0);
	var ret = whole;
	if (dec.length > 0)
		ret += '.' + dec;
	nn = parseFloat(new String(ret));
	if (isNaN(nn))
		nn = 0.0;
	return nn + "";
}

function checkNegative(fieldName) {
	var currentValue = getDoubleValue(calculatorCallBackGetValue(fieldName));
	var mnewvalue = calculatorCallBackGetField(fieldName);

	if (currentValue < 0) {
		isc.say("Negative", function(value) {
			mnewvalue.focusInItem();
		});
		return false;
	}

	if ((mnewvalue)) {
		var attrName = "focusechangedlisteneradded";
		var focList = mnewvalue.getProperty(attrName);
		if (focList)
			return true;
		mnewvalue.setProperty(attrName, "1");
		mnewvalue.blur = function(form, item) {
			var val = getDoubleValue(calculatorCallBackGetValue(fieldName));

			if (val < 0) {
				isc.say("Negative", function(value) {
					item.focusInItem();
				});
			}

		}
	}
	return true;
}

function calculateFinanceAbstactR(fieldName, source_loan, source_cred,
		dest_loan, dest_cred, pos, can_be_grater) {

	try {
		var currentValue = getDoubleValue(calculatorCallBackGetValue(fieldName));
		if (!checkNegative(fieldName))
			return;
		var cloan = getDoubleValue(calculatorCallBackGetValue(source_loan));
		var credit = getDoubleValue(calculatorCallBackGetValue(source_cred));

		var value = (cloan - credit) - (currentValue * pos);
		var nloan = 0.0;
		var ncredit = 0.0;

		nloan = value > 0 ? value : 0;
		ncredit = value < 0 ? Math.abs(value) : 0;
		if (can_be_grater != 1) {
			if ((pos == 1 && ncredit > 0) || (pos != 1 && nloan > 0)) {
				var dloan = getDoubleValue(calculatorCallBackGetValue(dest_loan));
				var dredit = getDoubleValue(calculatorCallBackGetValue(dest_cred));
				var value = Math.max((cloan - dloan), (credit - dredit));
				calculatorCallBackSetValue(fieldName, formatNumber(value));
				var nloan = dloan;
				var ncredit = dredit;
				isc.say((pos == 1) ? "არ შეიძლება კრედიტში გადასვლა!!!"
						: "არ შეიძლება ვალის გაზრდა!!!");
			}
		}

		calculatorCallBackSetValue(dest_loan, formatNumber(nloan));
		calculatorCallBackSetValue(dest_cred, formatNumber(ncredit));
		var result = new Object;
		result[dest_loan] = nloan;
		result[dest_cred] = ncredit;
		return result;

	} catch (err) {

		var vDebug = "";
		// for ( var prop in err) {
		// vDebug += "property: " + prop + " value: [" + err[prop] + "]\n";
		// }
		vDebug += "toString(): " + " value: [" + err.toString() + "]";

		alert(vDebug);
	}

}

function calculateFinanceAbstact(fieldName, source_loan, source_cred,
		dest_loan, dest_cred, pos) {
	return calculateFinanceAbstactR(fieldName, source_loan, source_cred,
			dest_loan, dest_cred, pos, 1);
}

function setFieldLoan(value, loan, cerdit, dest_field) {
	var nloan = getDoubleValue(value[loan]);
	var ncredit = getDoubleValue(value[cerdit]);
	var result = '';
	if (ncredit > 0)
		result = formatNumber(ncredit) + '(კრედიტი)';
	else
		result = formatNumber(nloan) + '(ვალი)';

	calculatorCallBackSetValue(dest_field, result);
}

function calculateFinanceDual(fieldName) {
	var source = calculateFinanceAbstact(fieldName, "cloan_source",
			"credit_source", "nloan_source", "ncredit_source", -1);
	setFieldLoan(source, "nloan_source", "ncredit_source", "result_source");

	var dest = calculateFinanceAbstact(fieldName, "cloan_dest", "credit_dest",
			"nloan_dest", "ncredit_dest", 1);
	setFieldLoan(dest, "nloan_dest", "ncredit_dest", "result_dest");
}

function calculateFinance(fieldName, pos) {
	calculateFinanceAbstact(fieldName, "cloan", "credit", "nloan", "ncredit",
			pos);
}

function finantialCalculatorPosChk(fieldName) {
	calculateFinanceAbstactR(fieldName, "cloan", "credit", "nloan", "ncredit",
			1, 0);
}

function finantialCalculatorPos(fieldName) {
	calculateFinance(fieldName, 1);

}

function finantialCalculatorNeg(fieldName) {
	calculateFinance(fieldName, -1);
}

function showMetterM3(fieldName) {
	try {
		var mnewvalue = calculatorCallBackGetField("mnewvalue");

		if ((mnewvalue)) {
			var attrName = "focusechangedlisteneradded";
			var focList = mnewvalue.getProperty(attrName);
			if (focList)
				return;
			mnewvalue.setProperty(attrName, "1");
			mnewvalue.blur = function(form, item) {
				var m3 = getDoubleValue(calculatorCallBackGetValue("expensem3"));

				if (m3 < 0) {
					isc.say("Negative", function(value) {
						item.focusInItem();
					});
				}

			}
		}
	} finally {
		var oldValue = getDoubleValue(calculatorCallBackGetValue("moldvalue"));
		var newValue = getDoubleValue(calculatorCallBackGetValue("mnewvalue"));
		var m3 = newValue - oldValue;
		calculatorCallBackSetValue("expensem3", formatNumber(m3));
	}
}

function setMeterValues(result) {
	if (result.length < 1)
		return;
	var rec = result[0];
	calculatorCallBackSetValue("moldvalue", rec["mettervalue"]);
	calculatorCallBackSetValue("mnewvalue", rec["mettervalue"]);
	calculatorCallBackSetValue("emetserial", rec["metserial"]);
	calculatorCallBackSetValue("emettertype", rec["mtypeid"]);

	showMetterM3("");

	var oldplombs = calculatorCallBackGetField("oldplombs");
	var withplombs = false;
	if (oldplombs)
		withplombs = true;
	if (withplombs) {
		var meterid = getDoubleValue(calculatorCallBackGetValue("meterid"));
		var criteria = {};
		criteria["metterid"] = meterid;
		calculatorCallBackExecDS("DBCDS_COMMON", "getMetterPlombs",
				"setPlombs", criteria);
	}
}
function calculateMetterInd(fieldName) {
	var oldmetterenabled = false;
	try {
		var formItem = calculatorCallBackGetField("meterid");
		var disabled = formItem.getProperty("disabled");

		if (!disabled)
			oldmetterenabled = true;
		else
			oldmetterenabled = !disabled;
	} catch (err) {
		// Handle errors here
	}

	var selected = oldmetterenabled;
	var field_isSelected = getBooleanFieldValue(fieldName);
	selected = selected && field_isSelected;
	var oldmaetterFields = [];
	oldmaetterFields.push("mnewvalue");
	oldmaetterFields.push("expensem3");
	var notrequered = [];
	setEditableAndRequered(oldmaetterFields, selected, notrequered);

}

function mettercorrectionReq(fieldName) {
	calculateMetterInd(fieldName);
}

function showNegative(fieldName) {
	var formItem = calculatorCallBackGetField("mnewvalue");
	if (!formItem)
		return;

	var attrName = "fclist";
	var focList = formItem.getProperty(attrName);
	if (focList)
		return;
	formItem.setProperty(attrName, "1");
	formItem.blur = function(form, item) {
		var oldvalue = getDoubleValue(calculatorCallBackGetValue("moldvalue"));
		var newvalue = getDoubleValue(calculatorCallBackGetValue("mnewvalue"));

		if (newvalue < oldvalue) {
			isc.say("Negative", function(value) {
				item.focusInItem();
			});
		}

	}

}

function mettercorrectionCombo(fieldName) {
	var mserial = null;
	metterValue = calculatorCallBackGetDisplayValue("meterid");
	try {
		mserial = metterValue.split(":")[1];
	} catch (err) {
		// Handle errors here
	}
	try {
		calculatorCallBackSetValue("mserial", mserial);
	} catch (err) {
		// Handle errors here
	}

	var meterid = getDoubleValue(calculatorCallBackGetValue("meterid"));

	var criteria = {};
	criteria["metterid"] = meterid;

	try {
		calculatorCallBackExecDS("DBCDS_COMMON", "getMetterValue",
				"setMeterValues", criteria);
	} catch (err) {
		alert("err=" + err.stack);
	}

}

function setPlombs(result) {
	if (result.length < 1)
		result = [];

	var mnewvalue = calculatorCallBackGetField("oldplombs");
	if (!(mnewvalue))
		return;
	var map = new Object;
	map["-1"] = "---NEW----";
	for (i = 0; i < result.length; i++) {
		var obj = result[i];
		map[obj["plombid"]] = obj["plombname"] + ":" + obj["place"] + ":"
				+ obj["status"];
	}
	mnewvalue.setValueMap(map);

}

function mettercorrectionValue(fieldName) {
	showMetterM3(fieldName);
}

function plombCombo(fieldName) {
	var value = getDoubleValue(calculatorCallBackGetValue(fieldName));

	var enable = value == -1;
	var fn = "plombnum";
	var formItem = calculatorCallBackGetField(fn);

	formItem.setProperty("disabled", !enable);
	calculatorCallBackFieldDefRequaiered(fn, enable);
	if (!enable)
		calculatorCallBackSetValue(fn,
				calculatorCallBackGetDisplayValue(fieldName));
}

function generateProportions(cloan, monthcount, type, count) {
	var proportions = [];
	for (i = 0; i < count; i++) {
		proportions[i] = "";
	}
	if (type == 1) {
		var portion = cloan / getDoubleValue(monthcount);
		var proportionInt = getIntValue(portion);
		var first = cloan - getDoubleValue(proportionInt) * (monthcount - 1);

		proportions[0] = formatNumber(first) + "";
		for (i = 1; i < monthcount; i++) {
			proportions[i] = proportionInt + "";
		}
	}
	if (type == 2) {
		var first = (cloan / 2.0);
		var portion = first / getDoubleValue(monthcount - 1);
		var proportionInt = getIntValue(portion);
		first = cloan
				- (getDoubleValue(monthcount - 1) * getDoubleValue(proportionInt));

		proportions[0] = formatNumber(first) + "";
		for (i = 1; i < monthcount; i++) {
			proportions[i] = proportionInt + "";
		}
	}

	if (type == 3) {
		var S = cloan;
		var Sx = (getIntValue(S / 2)) * 2;
		var delta = S - Sx;
		var q = 0.5;

		var first = Sx / 2.0;
		var sum = 0.0;
		for (i = 1; i < monthcount; i++) {
			var pow = Math.pow(q, i);
			var val = first * pow;
			sum += val;
			proportions[i] = formatNumber(val) + "";
		}
		proportions[0] = formatNumber(first) + "";
	}
	return proportions;
}

function portionCalculatorLinarFunc(fieldName, type, count) {
	try {
		var cloan = getDoubleValue(calculatorCallBackGetValue("cloan"));
		var monthcount = getIntValue(calculatorCallBackGetValue("monthcount"));

		var proportions = generateProportions(cloan, monthcount, type, count);
		for (i = 0; i < proportions.length; i++) {
			calculatorCallBackSetValue("m" + (i + 1), proportions[i] + "");
		}

	} catch (err) {
		isc.say(err);
	}
}

function portionCalculatorLinar(fieldName) {
	portionCalculatorLinarFunc(fieldName, 1, 12);
}

function portionCalculatorLinar24(fieldName) {
	portionCalculatorLinarFunc(fieldName, 1, 24);
}

function portionCalculatorHalf(fieldName) {
	portionCalculatorLinarFunc(fieldName, 2, 12);
}

function metterstateCombo(fieldName) {
	var oldmaetterFields = [];
	oldmaetterFields.push("meterid");
	oldmaetterFields.push("moldvalue");
	oldmaetterFields.push("mnewvalue");
	oldmaetterFields.push("expensem3");
	oldmaetterFields.push("inddaricx");

	var oldmaetterNotReqFields = [];
	oldmaetterNotReqFields.push("moldvalue");
	oldmaetterNotReqFields.push("mnewvalue");
	oldmaetterNotReqFields.push("expensem3");
	oldmaetterNotReqFields.push("inddaricx");

	var newmaetterFields = [];
	newmaetterFields.push("metserial");
	newmaetterFields.push("mettertype");
	newmaetterFields.push("start_index");
	newmaetterFields.push("cortypeid");
	newmaetterFields.push("corserial");
	newmaetterFields.push("montagedate");
	newmaetterFields.push("corvalue");

	var newmaetterNotReqFields = [];
	newmaetterNotReqFields.push("cortypeid");
	newmaetterNotReqFields.push("corserial");
	newmaetterNotReqFields.push("corvalue");

	var state = getDoubleValue(calculatorCallBackGetValue(fieldName));

	try {
		setEditableAndRequered(oldmaetterFields,
				(state == 1.0 || state == 3.0), oldmaetterNotReqFields);

		setEditableAndRequered(newmaetterFields,
				(state == 1.0 || state == 2.0), newmaetterNotReqFields);
	} catch (err) {
		alert(err);
	}

}

function mettervalueComboNegative(fieldName) {
	mettercorrectionCombo(fieldName);
	showNegative(fieldName);
}

function setDublicated(result) {
	if (result.length < 1)
		result = [];

	var cdublicated = "";
	var dublicatedresult = "";
	if (result.length >= 1) {

		cdublicated = "DUBLICATED";
		var obj = result[0];
		var val = obj["dublicated"];
		var cnt = (val.split("<tr>").length - 1);
		isc.say("ნაპოვნია " + cnt + " კორექტორი ქარხნული ნომრით:"
				+ obj["metserial"]);
		dublicatedresult = val;
	}
	calculatorCallBackSetValue("cdublicated", cdublicated);
	calculatorCallBackSetValue("dublicatedresult", dublicatedresult);

}

function metterserialcheckself(fieldName) {
	var metserial = calculatorCallBackGetValue(fieldName);
	var criteria = {};
	criteria["metserial"] = metserial;
	criteria["cusid"] = -1;
	calculatorCallBackExecDS("DBCDS_COMMON", "getDublicatedMetters",
			"setDublicated", criteria);

}

function metterserialcheck(fieldName) {
	var mnewvalue = calculatorCallBackGetField(fieldName);
	metterserialcheckself(fieldName);
	if ((mnewvalue)) {
		var attrName = "focusechangedlisteneradded";
		var focList = mnewvalue.getProperty(attrName);
		if (focList)
			return true;
		mnewvalue.setProperty(attrName, "1");
		mnewvalue.blur = function(form, item) {
			metterserialcheckself(fieldName);
		}
	}
}

function checkFor_has_to_be(fieldName, fieldName_which, checkfor, alert_msg) {
	var fieldName_which_val = calculatorCallBackGetValue(fieldName_which);
	var ck = checkfor.split(',');
	var done;
	for (i = 0; i < ck.length; i++) {
		if (ck[i] == fieldName_which_val) {
			done = 1;
		}
	}
	if (!done) {
		var mnewvalue = calculatorCallBackGetField(fieldName);
		isc.say(alert_msg);
		calculatorCallBackSetValue(fieldName, '');
	}
}

function checkFor_has_to_be_list(fieldName, fieldName_which, checkfor,
		alert_msg) {
	var mnewvalue = calculatorCallBackGetField(fieldName);
	if ((mnewvalue)) {
		var attrName = "focusechangedlisteneradded";
		var focList = mnewvalue.getProperty(attrName);
		if (focList)
			return true;
		mnewvalue.setProperty(attrName, "1");
		mnewvalue.blur = function(form, item) {
			checkFor_has_to_be(fieldName, fieldName_which, checkfor, alert_msg);
		}
	}
}

function checkFor7_8_hastobe_7(fieldName) {
	checkFor_has_to_be_list(fieldName, 'classid', '7,8',
			'აბონენტი უნდა იყოს "სოკარის განვადებით" ან "სოკარის განვადებით პლიუსი"');
	finantialCalculatorNeg(fieldName);
}
function checkFor8_hastobe_8(fieldName) {
	checkFor_has_to_be_list(fieldName, 'classid', '8',
			'აბონენტი უნდა იყოს "სოკარის განვადებით პლიუსი"');
}
function check_private_number(fieldName) {
	var private_number = calculatorCallBackGetField("private_number");
	if (private_number["icons"])
		return;

	try {
		var _icons = [ {
			src : "[SKIN]/actions/accept.png",
			click : function(form, item) {
				var _private_number = calculatorCallBackGetValue("private_number");
				var _cusname = calculatorCallBackGetValue("cusname");
				var criteria = {};
				criteria["url"] = "http://voters.cec.gov.ge/korp3TIM.php?pn="
						+ _private_number + "&gv="
						+ encodeURIComponent(_cusname.split(' ')[0]);

				calculatorCallBackExecDS("GetHttpReqvestDS", "execHttpGet",
						"setprivatenumberresult", criteria);
			}

		} ];
		private_number.setProperty("icons", _icons);

	} catch (err) {
		alert(err);
	}

}
function setprivatenumberresult(result) {
	if (result.length < 1)
		result = [];
	var mnewvalue = result[0]["content"];
	mnewvalue = mnewvalue.replace("DISPLAY: none", "");
	isc.say(mnewvalue);

}
function ntwpriceAmountSet(fieldName) {
	var pricesItem = calculatorCallBackGetField("prices");
	var cntItem = calculatorCallBackGetField("cnt");

	if (!pricesItem)
		return;
	if (!cntItem)
		return;
	var pricesItemO = calculatorCallBackGetValue("prices");
	if (!pricesItemO || pricesItemO.trim().length == 0)
		return;
	var pricesItemS = pricesItemO.trim();

	var priceitems = pricesItemS.split(",");
	cnt = 0;

	for (i = 0; i < priceitems.length; i++) {
		var items = priceitems[i].split(";");
		if (items.length != 4)
			continue;
		String
		itemName = "nip" + items[0];
		var priceItemS = calculatorCallBackGetField(itemName);
		if (!priceItemS)
			continue;
		title = Base64.decode(items[1]) + " / " + Base64.decode(items[2]);
		priceItemS.setProperty("title", title);
		priceItemS.redraw();
		// calculatorCallBackSetFieldProperty(itemName, "title", title);
		var val = items[3];

		var dec = getDoubleValue(val);
		dec = dec.toFixed(2);
		calculatorCallBackSetValue(itemName, dec + "");
		cnt++;
	}
	calculatorCallBackSetValue("cnt", cnt + "");
}
function ntwpricecalculatentwAmount() {
	try {
		var cnt = getIntValue(calculatorCallBackGetValue("cnt"));
		var amount = 0.0;
		for (i = 0; i < cnt; i++) {
			var itemName = "nic" + (i + 1);
			var amounts = 0.0;
			var v = calculatorCallBackGetValue(itemName);
			amounts = getDoubleValue(v);
			itemName = "nip" + (i + 1);
			v = calculatorCallBackGetValue(itemName);
			amounts = amounts * getDoubleValue(v);
			itemName = "nia" + (i + 1);
			if (amounts <= 0)
				calculatorCallBackSetValue(itemName, "");
			else
				calculatorCallBackSetValue(itemName, amounts.toFixed(2) + "");
			amount += amounts;
		}
		if (amount <= 0)
			calculatorCallBackSetValue("amount", "");
		else
			calculatorCallBackSetValue("amount", amount.toFixed(2) + "");

	} catch (e) {
		isc.say(e);
	}

}
function ntwpriceAmountCalculate(fieldName) {
	try {
		var mnewvalue = calculatorCallBackGetField(fieldName);

		if ((mnewvalue)) {
			var attrName = "focusechangedlisteneradded";
			var focList = mnewvalue.getProperty(attrName);
			if (focList)
				return;
			mnewvalue.setProperty(attrName, "1");
			mnewvalue.blur = function(form, item) {
				var val = calculatorCallBackGetValue(fieldName);
				if (val) {
					var value = getDoubleValue(val);
					if (value <= 0)
						calculatorCallBackSetValue(fieldName, "");

				}
				try {
					ntwpricecalculatentwAmount();
				} catch (e) {
				}
			}

		}

	} catch (e) {
		alert(e);
	} finally {
		ntwpricecalculatentwAmount();
	}
}

function createmapfeaturestyle(layer_id, filter_id) {
	var mystyle;
	if (layer_id == 8) {
		var cl = "#CC9900";
		var stl = 'solid';
		switch (filter_id) {
		case 11:
			cl = "#FF3333";
			break;
		case 12:
			cl = "#FF3333";
			stl = 'dashdot';
			break;
		case 21:
			cl = "#CC9900";
			break;
		case 22:
			cl = "#CC9900";
			stl = 'dashdot';
			break;
		case 31:
			cl = "#999999";
			break;
		case 32:
			cl = "#999999";
			stl = 'dashdot';
			break;
		}
		;

		cl = shadeColor(cl, -40);
		mystyle = {
			strokeColor : cl,
			strokeOpacity : 0.5,
			strokeDashstyle : stl,
			strokeWidth : 5
		};
		// mystyle = null;

	}
	if (layer_id == 9 || layer_id == 11 || layer_id == 12) {
		var dir = "images/map/";
		if (layer_id == 9)
			dir += "eq/";
		if (layer_id == 11)
			dir += "neq/";
		if (layer_id == 12)
			dir += "tr/";
		var url = dir + filter_id + ".png";
		var _widh = (layer_id == 11 && filter_id == 2) ? 100 : 50;
		mystyle = {
			pointRadius : 20,
			graphicHeight : 50,
			graphicWidth : _widh,
			externalGraphic : url
		};
	}

	if (layer_id == 6) {
		var url = "images/map/district_meter.png";
		mystyle = {
			pointRadius : 20,
			graphicHeight : 25,
			graphicWidth : 25,
			graphicOpacity : 0.7,
			externalGraphic : url
		};
	}

	return mystyle;

}

function proceedmapfeaturetypeandrotation(_tp_field, rfield_name, layer_field) {

	if (_editor_style && _editor_feature && _editor_modifyVector
			&& _editor_drawFeature) {
		var _tp = getIntValue(calculatorCallBackGetValue(_tp_field));
		var _rotation = getIntValue(calculatorCallBackGetValue(rfield_name));
		if (_rotation < 0)
			_rotation = 0;
		if (_rotation > 360)
			_rotation = 360;
		var layer_id = getIntValue(calculatorCallBackGetValue(layer_field));
		calculatorCallBackSetValue(rfield_name, "" + _rotation);
		var tmp_style = createmapfeaturestyle(layer_id, _tp);
		if (tmp_style) {
			if (tmp_style.externalGraphic)
				_editor_style.externalGraphic = tmp_style.externalGraphic;
			_editor_style.rotation = _rotation;
			_editor_feature.style = _editor_style;
			_editor_modifyVector.removeFeatures(_editor_feature);
			_editor_modifyVector.addFeatures(_editor_feature);
			_editor_drawFeature.selectControl.select(_editor_feature);
		}
	}
}

function mapfeaturetypeandrotationchanged(fieldName) {
	proceedmapfeaturetypeandrotation("type", "rotation", "layer_id");
}

function netquipmentfeaturetypeandrotationchanged(fieldName) {
	var _tp_field = "type";
	proceedmapfeaturetypeandrotation(_tp_field, "rotation", "layer_id");
	var _tp = getIntValue(calculatorCallBackGetValue(_tp_field));

	var _cus_id_field = calculatorCallBackGetField("cusid");
	var _cus_name_field = calculatorCallBackGetField("cusname");
	if (_cus_id_field)
		if (_tp == 3)
			_cus_id_field.hide();
		else
			_cus_id_field.show();
	if (_cus_name_field)
		if (_tp == 3)
			_cus_name_field.hide();
		else
			_cus_name_field.show();
}

function customers_search(fieldName) {
	try {
		var mnewvalue = calculatorCallBackGetField(fieldName);

		if ((mnewvalue)) {
			var attrName = "addCanvasItem";
			var attrval = mnewvalue.getProperty(attrName);
			if (attrval)
				return;

			var _defaultDistance = 200;

			mnewvalue.showTitle = false;

			var _goto = {
				_constructor : "Button",
				title : ">>",
				click : function() {
					var rec = this.parentElement.record;
					if (!rec)
						return;
					var _my_map;
					if (_editor_drawFeature)
						_my_map = _editor_drawFeature.map;
					if (rec && _my_map) {
						try {
							var wktStr = rec["lcentroid"];
							var fea = new OpenLayers.Format.WKT().read(wktStr);
							var cen = fea.geometry.getBounds()
									.getCenterLonLat();
							_my_map.setCenter(cen);
						} catch (e) {
							alert('Err read data' + e);
						}
					}
				},
				height : 20,
				width : 20
			};

			var _removeBtn = {
				_constructor : "Button",
				title : "-",
				click : function() {
					var rec = this.parentElement.record;
					if (!rec)
						return;
					isc.ask("Do you want to delete record?", function(value) {

						try {
							if (!rec)
								return;
							lg.selectRecord(rec);
							lg.removeSelectedData();
							ts.computeData('');
						} catch (e) {
							alert('err' + e + ' ' + ts);
						}
					});
				},
				height : 20,
				width : 20
			};

			var lg = isc.ListGrid.create({
				autoDraw : false,
				// showHeader : false,
				showRollOverCanvas : true,
				canSelectText : true,
				canDragSelectText : true,
				// showFilterEditor : true,
				showRowNumbers : true,
				fields : [ {
					name : "cusid",
					title : "ID"
				}, {
					name : "cusname",
					title : "Name",
					width : "*"
				}, {
					name : "zone",
					title : "Zone",
					width : "*"
				}, {
					name : "distance",
					title : "Distance",
					width : "100"
				} ],

				cellDoubleClick : function() {

				},
				// groupStartOpen : "all",
				// groupByField : 'buid',
				autoFetchData : true,
				rollOverCanvasConstructor : isc.HLayout,
				rollOverCanvasProperties : {
					snapTo : "TR",
					height : 20,
					width : 40,
					members : [ _goto, _removeBtn ]
				}
			});

			mnewvalue.myListGrid = lg;

			var ts = isc.ToolStripButton
					.create({
						autoDraw : false,
						icon : "[SKIN]actions/add.png",
						title : "",
						dsResponce : function(_1, _2, _3) {
							// alert(" _1=" + _1 + " _2.length=" + _2.length
							// + " _3=" + _3);
							if (_2 && _2.length && _2.length > 0) {
								var _data = _2[0];
								ts.buids = _data["buids"];
								ts.cusids = _data["cusids"];
								ts.builds_collection = _data["builds_collection"];
								var d;
								var _mycusids = ts.cusids;

								// ts._wms_layer1 = ts
								// .addWms(_mycusids, 'polygon');
								ts._wms_layer2 = ts.addWms('0', d);
							}
						},
						replaceParams : function(_mycusids) {
							return _mycusids.split(',').join('-');
						},
						merge_params : function(_wms_layer, _cus_ids) {

							if (_wms_layer) {
								if (!_cus_ids || _cus_ids.length == 0)
									_cus_ids = '0';
								_cus_ids = this.replaceParams(_cus_ids);
								var neParams = {
									viewparams : 'cusids:' + _cus_ids
								};
								_wms_layer.mergeNewParams(neParams);
								_wms_layer.redraw(true);
							}
						},
						computeData : function(_tt) {
							try {
								var _cus_ids = mnewvalue.getValue();
								if (!_cus_ids || _cus_ids.length == 0)
									_cus_ids = '0';
								this.merge_params(this._wms_layer2, _cus_ids);

							} catch (e) {
								alert('computeGetValue err' + e);
							}
						},

						addWms : function(_mycusids, _my_style) {
							try {
								var _my_map;
								// alert('_mycusids error' + _mycusids
								// + ' _my_style=' + _my_style);
								if (_editor_drawFeature)
									_my_map = _editor_drawFeature.map;
								if (_my_map) {

									var layers = _my_map.layers;
									if (layers.constructor != Array) {
										layers = [ layers ];
									}
									var _newLayer;
									var _url;
									for ( var i = 0; i < layers.length; ++i) {
										try {
											_url = layers[i].url;
											if (_url) {
												_newLayer = layers[i].clone();
												break;
											}
										} catch (e) {
											alert('layerrsss error' + e);
										}
									}
									if (_url && _newLayer) {
										var _param = this
												.replaceParams(_mycusids);
										var neParams = {
											layers : "buildings_for_customer",
											viewparams : 'cusids:' + _param
										};
										if (_my_style)
											neParams["STYLES"] = _my_style;
										_newLayer.mergeNewParams(neParams);
									}
									_my_map.addLayer(_newLayer);
									return _newLayer;
								}
							} catch (e) {
								alert('fwatures error' + e);
							}
						},
						click : function() {
							try {
								var ds_cus_short = isc.DataSource
										.get("CustomerShortDS");
								
								var _raiid = getIntValue(calculatorCallBackGetValue("raiid"));
								var _cus_ids = this.cusids;
								var _already_customers = mnewvalue.getValue();
								
								var _init_criteria = {
									raiid : _raiid,
									notcusids : _already_customers,
									buid : 1
								};
								var _zone_filter = {
									_constructor : "ComboBoxItem",
									optionDataSource : ds_cus_short,
									valueField : "zone",
									displayField : "zone",
									optionOperationId : "getZones",
									textMatchStyle : "startsWith",
									addUnknownValues : true,
									autoFetchData : true,
									// filterLocally : true,
									fetchMissingValues : true,
									optionCriteria : _init_criteria,
									title : "Zone",
									name : "zone",
									visible : false

								};
								var _df = isc.DynamicForm.create({
									numCols : 2,
									autoDraw : false,
									addFilter : function(item) {
										var _val = item.getValue();
										if (!_val)
											isc.say('Invalid distance !!!!');
										try {
											var _intVal = getIntValue(_val);
											if ((_intVal + '') != _val) {
												isc.say('Invalid distance '
														+ _val + '!!!!');
												return;
											}
											if (_intVal > 10000) {
												isc.say('Too far ' + _val
														+ '!!!!');
												return;
											}
											lg_customers.setDistance(_intVal);
										} catch (e) {
											alert('Error int' + e)
										}

									},

									fields : [ {
										name : "distance",
										title : "Distance (in M)",
										_constructor : "IntegerItem",
										defaultValue : _defaultDistance,
										icons : [ {
											src : "[SKIN]/actions/accept.png",
											click : function(form, item) {
												form.addFilter(item);
											}
										}, {
											src : "eraser.png",
											click : function(form, item) {
												lg_customers.clearDistance();
											}

										} ],
										keyPress : function(item, form, key) {
											if (key && key == 'Enter') {
												form.addFilter(item);
											}
										}
									} ]
								});
								var lg_customers = isc.ListGrid
										.create({
											clearDistance : function(_distance) {
												var _localdistance = this.initialCriteria["distance"];

												if (!_localdistance)
													return;
												try {
													var in_crt = this.initialCriteria;
													delete in_crt["distance"];
													delete in_crt["cusids"];
													this.initialCriteria = in_crt;
													lg_customers
															.fetchData(in_crt);
												} catch (e) {
													alert('clear error' + e);
												}

											},
											setDistance : function(_distance) {
												var _localdistance = this.initialCriteria["distance"];
												if (_localdistance
														&& _localdistance == _distance)
													return;
												this.initialCriteria["distance"] = _distance;
												var criteria = mnewvalue
														.getDataCriteria(false);
												criteria["distance"] = _distance;

												var _ds = isc.DataSource
														.get('BuildingsDS');
												_ds
														.fetchData(
																criteria,
																this.dsResponce,
																{
																	operationId : "getbuildingsindistance"
																});

											},
											dsResponce : function(_1, _2, _3) {

												if (_2 && _2.length
														&& _2.length > 0) {
													var _data = _2[0];
													var in_crt = [];
													for ( var p in lg_customers.initialCriteria) {
														in_crt[p] = lg_customers.initialCriteria[p];
													}
													in_crt["cusids"] = _data["cusids"];
													lg_customers.initialCriteria = in_crt;
													lg_customers
															.fetchData(in_crt);
												}

											},
											autoDraw : false,
											listEndEditAction : "next",
											width : "100%",
											height : 300,
											showFilterEditor : true,
											autoFetchData : false,
											canSelectText : true,
											canDragSelectText : true,
											alternateRecordStyles : true,
											dataSource : ds_cus_short,
											autoFetchData : true,
											initialCriteria : _init_criteria,
											criteria : _init_criteria,
											showRowNumbers : true,
											fields : [ {
												name : "cusid",
												title : "ID",
												width : 50
											}, {
												name : "cusname",
												title : "Name",
												width : "*"
											}, {
												name : "zone",
												title : "Zone",
												width : "*"
											// ,
											// editorType : "ComboBoxItem",
											// filterEditorProperties :
											// _zone_filter
											} ],

											showRollOverCanvas : true,
											rollOverCanvasConstructor : isc.HLayout,
											rollOverCanvasProperties : {
												snapTo : "TR",
												height : 20,
												width : 20,
												members : [ {
													_constructor : "Button",
													title : "+",
													click : function() {
														try {

															var _rec = this.parentElement.record;
															if (_rec && lg) {
																var newRec = [];
																for ( var _prop in _rec) {
																	newRec[_prop] = _rec[_prop];
																}
																_rec["enabled"] = false;
																newRec["buid"] = 1;
																lg_customers
																		.redraw();
																lg
																		.addData(newRec);
																lg.redraw();

																mnewvalue
																		.setValuesLocal(mnewvalue
																				.getValue());

															}
														} catch (e) {
															alert(e);
														}
													},
													height : 20,
													width : 20
												} ]
											}
										});

								var _tsAdd = isc.ToolStripButton
										.create({
											autoDraw : false,
											icon : "[SKIN]actions/approve.png",
											title : "",
											click : function() {
												try {

													var _recs = lg_customers
															.getSelectedRecords();
													if (_recs
															&& _recs.length > 0
															&& lg) {
														var _data = lg.data;
														if (!_data)
															_data = [];

														try {
															for ( var i = 0; i < _recs.length; i++) {
																_recs[i]["buid"] = 1;
																lg
																		.addData(_recs[i]);
															}
															lg.fetchData({});
															lg.redraw();
														} catch (e) {
															alert(lg
																	+ 'Full addDataValues _tsAdd '
																	+ e);
														}

														mnewvalue
																.setValuesLocal(mnewvalue
																		.getValue());
														this.myWindow.destroy();
													}

												} catch (e) {
													alert('Full errr _tsAdd '
															+ e);
												}
											}
										});
								var _win = isc.Window.create({
									autoDraw : false,
									autoCenter : true,
									dismissOnOutsideClick : true,
									title : "Select customers",
									autoSize : true,
									isModal : true,
									width : 500,
									height : 300,
									items : [ isc.ToolStrip.create({
										autoDraw : false,
										width : "100%",
										members : [ _tsAdd, _df ],
										visibilityMode : "multiple"
									}), lg_customers ],
									showShadow : false
								});
								_tsAdd.myWindow = _win;
								_win.show();

							} catch (e) {
								alert(e);
							}
						}
					});
			mnewvalue.myTs = ts;
			var _tsDelete = isc.ToolStripButton.create({
				autoDraw : false,
				icon : "[SKIN]actions/remove.png",
				title : "",
				click : function() {
					isc.ask("Do you want to delete records?", function(value) {
						lg.removeSelectedData();
						ts.computeData('');
					});
				}
			});

			mnewvalue.setCanvas(isc.VLayout.create({
				autoDraw : false,

				members : [ isc.ToolStrip.create({
					autoDraw : false,
					members : [ ts, _tsDelete ],
					visibilityMode : "multiple"
				}), lg ],
				showEdges : true
			}));

			var _beforevalue = calculatorCallBackGetValue(fieldName);
			mnewvalue
					.addProperties({
						getValue : function() {
							if (!this.myListGrid) {
								var _unk;
								return _unk;
							}
							var _data = this.myListGrid.data;
							var _cus_ids;
							for ( var i = 0; i < _data.length; i++) {
								if (!_cus_ids)
									_cus_ids = '';
								if (_cus_ids.length > 0)
									_cus_ids += ',';
								_cus_ids += _data[i]["cusid"];
							}
							return _cus_ids;
						},
						getDataCriteria : function(_zoom_map) {
							try {
								var _raiid = getIntValue(calculatorCallBackGetValue("raiid"));
								var _my_map;
								if (_editor_drawFeature)
									_my_map = _editor_drawFeature.map;
								var clonlat;
								if (_editor_feature) {
									clonlat = _editor_feature.geometry
											.getBounds().getCenterLonLat();
									_view_point = 'POINT(' + clonlat.lon + ' '
											+ clonlat.lat + ')';

								}
								if (_zoom_map && _my_map) {
									_my_map.zoomOut();
									_my_map.zoomOut();
									_my_map.zoomOut();
									if (clonlat)
										_my_map.setCenter(clonlat);
									mnewvalue.myMap = _my_map;
								}
								var criteria = {};
								criteria["subregionid"] = _raiid;
								criteria["point"] = _view_point;
								criteria["from_srid"] = 900913;
								criteria["to_srid"] = 32638;
								criteria["distance"] = 200;
								return criteria;
							} catch (e) {
								alert("errr===" + e);
							}
						},
						generateData : function() {
							try {

								var criteria = this.getDataCriteria(true);
								var _ds = isc.DataSource.get('BuildingsDS');
								// alert('fetching');
								var fnc = this.myTs.dsResponce;
								_ds.fetchData(criteria, fnc, {
									operationId : "getbuildingsindistance"
								});
							} catch (e) {
								alert("errr===" + e);
							}
						},
						setValuesLocal : function(_value) {
							if (!this.myListGrid) {
								return;
							}
							var und;
							this.oldValue = und;
							this.myListGrid.data = [];
							if (!this._wms_layer && this.myTs) {
								var _v = _value;
								if (!_v)
									_v = '0';
								this._wms_layer = this.myTs.addWms(_v, '');
							}
							mnewvalue.myListGrid.redraw();
							if (!_value) {
								return;
							}

							var ds_cus_short = isc.DataSource
									.get("CustomerShortDS");
							var formatDistance = function(_1) {
								// alert(_1);
								var dist;
								var _distM = _1;
								if (_distM) {
									var _dots = (_distM + '').split('.');
									_distM = _dots[0];
									if (_dots.length > 1 && _dots[1])
										_distM += '.'
												+ _dots[1].substring(0, 2);

									dist = _distM + 'm';
									if (_distM > 1000) {
										var km = _distM / 1000.0;

										_distM = getDoubleValue(
												km + (_distM - km * 1000.0))
												.toPrecision(4);

										dist = _distM + 'km';
									}
								}
								return dist;
							}
							var _fun_Resp = function(_1, _2, _3) {
								if (_2 && _2.length && _2.length > 0) {
									var str_conflict;
									var _data = mnewvalue.localData;
									var _id = getIntValue(calculatorCallBackGetValue("id"));
									var _newRecs = [];
									for ( var i = 0; i < _data.length; i++) {
										var _rRec = _data[i];
										var _cusid = _rRec["cusid"];
										var _cusname = _rRec["cusname"];

										var _newRecord;
										var found = false;

										for ( var j = 0; j < _2.length; j++) {
											var _respRec = _2[j];

											var _resp_cusid = _respRec["cusid"];
											if (_resp_cusid == _cusid) {
												found = true;
												var _resp_id = _respRec["mobject_id"];
												if (!_resp_id
														|| _resp_id == _id) {
													_newRecord = _rRec;

													var dist = formatDistance(_respRec["distance"]);

													if (dist)
														_newRecord["distance"] = dist;
													_newRecord["lcentroid"] = _respRec["lcentroid"];
													_newRecord["buid"] = _respRec["buid"];
													break;
												} else {
													if (!str_conflict)
														str_conflict = 'Conflicts:';
													str_conflict += '<br>'
															+ _cusid + ':'
															+ _cusname;
												}
											}

										}
										if (!found) {
											if (!str_conflict)
												str_conflict = 'Conflicts:';
											str_conflict += '<br>' + _cusid
													+ ':' + _cusname
													+ ' <b>NOT FOUND!!!</b>';
										}
										if (_newRecord)
											mnewvalue.myListGrid
													.addData(_newRecord);
									}
									mnewvalue.myListGrid.fetchData();

									mnewvalue.myListGrid.redraw();
									ts.computeData('');
									if (str_conflict)
										isc.say(str_conflict);

								} else {
									isc.say('No data found!!!');
								}
							};

							var _funResp = function(_1, _2, _3) {
								// alert('bbb');
								// this.myListGrid.data = _2;
								if (_2 && _2.length && _2.length > 0) {

									var criteria = mnewvalue
											.getDataCriteria(false);
									criteria["cusids"] = _value;
									var _ds = isc.DataSource.get('BuildingsDS');
									mnewvalue.localData = _2;
									_ds.fetchData(criteria, _fun_Resp, {
										operationId : "getcustomerdistances"
									});

								}

							};
							ds_cus_short.fetchData({
								cusids : _value
							}, _funResp);
						},
						setValue : function(_value) {
							this.generateData();
							this.setValuesLocal(_value);
						}
					});
			// mnewvalue.setHeight("300");

			mnewvalue.setValue(_beforevalue);
			mnewvalue.setProperty(attrName, "1");

		}

	} catch (e) {
		alert(e);
	}
}
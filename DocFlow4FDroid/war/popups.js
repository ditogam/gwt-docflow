var controller;

var _myclosePopupWindow;
var _select_map_object;

var _editor_modifyVector;
var _editor_drawFeature;
var _editor_feature;
var _editor_style;

function callFunction(id) {
	controller(id);
}

function myclosePopupWindow(id) {
	_myclosePopupWindow(id);
}

function select_map_object(geom_text, srid) {
	_select_map_object(geom_text, srid);
}

function shadeColor(color, percent) {

	var R = parseInt(color.substring(1, 3), 16);
	var G = parseInt(color.substring(3, 5), 16);
	var B = parseInt(color.substring(5, 7), 16);

	R = parseInt(R * (100 + percent) / 100);
	G = parseInt(G * (100 + percent) / 100);
	B = parseInt(B * (100 + percent) / 100);

	R = (R < 255) ? R : 255;
	G = (G < 255) ? G : 255;
	B = (B < 255) ? B : 255;

	var RR = ((R.toString(16).length == 1) ? "0" + R.toString(16) : R
			.toString(16));
	var GG = ((G.toString(16).length == 1) ? "0" + G.toString(16) : G
			.toString(16));
	var BB = ((B.toString(16).length == 1) ? "0" + B.toString(16) : B
			.toString(16));

	return "#" + RR + GG + BB;
}

function createmapobjectstyle(layer_id, filter_id) {
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
	if (layer_id == 9) {
		var url = "images/map/eq/" + filter_id + ".png";
		mystyle = {
			pointRadius : 20,
			graphicHeight : 25,
			graphicWidth : 25,
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

function gassequipmenttypeandrotationchanged(fieldName) {
	if (_editor_style && _editor_feature && _editor_modifyVector
			&& _editor_drawFeature) {
		var _tp = getIntValue(calculatorCallBackGetValue("type"));
		var _rotation = getIntValue(calculatorCallBackGetValue("rotation"));
		if (_rotation < 0)
			_rotation = 0;
		if (_rotation > 360)
			_rotation = 360;
		calculatorCallBackSetValue("rotation", "" + _rotation);
		var tmp_style = createmapobjectstyle(9, _tp);
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

function setMeterDeviceCustomer(result) {
	if (!result || result.length < 1)
		result = [];

	var mvalue = calculatorCallBackGetField("cusid");
	if (!(mvalue))
		return;
	var map = new Object;
	for (i = 0; i < result.length; i++) {
		var obj = result[i];
		map[obj["cusid"]] = obj["cusname"];
	}
	mvalue.setValueMap(map);
}

function setMeterDevice(result) {
	if (!result || result.length < 1)
		result = [];

	var mvalue = calculatorCallBackGetField("corector_id");
	if (!(mvalue))
		return;
	var map = new Object;
	for (i = 0; i < result.length; i++) {
		var obj = result[i];
		map[obj["id"]] = obj["name"];
	}
	mvalue.setValueMap(map);
}

function meretdeviceproceedcombo(fieldName) {
	var _subregionId = getIntValue(calculatorCallBackGetValue("subregionId"));
	var _cusid = getIntValue(calculatorCallBackGetValue("cusid"));
	var criteria = {};
	criteria["parent_metters"] = 1;
	criteria["subregionid"] = _subregionId;
	if (_cusid && _cusid > 0)
		criteria["ccusid"] = _cusid;
	try {
		calculatorCallBackExecDS("CustomerDS", "", "setMeterDeviceCustomer",
				criteria);
	} catch (err) {
		alert("err=" + err.stack);
	}

	criteria = {};
	criteria["subregion_id"] = _subregionId;

	try {
		calculatorCallBackExecDS("MeterDeviceDS", "", "setMeterDevice",
				criteria);
	} catch (err) {
		alert("err=" + err.stack);
	}

}
function pipelinetypechanged(fieldName) {
	if (_editor_style && _editor_feature && _editor_modifyVector
			&& _editor_drawFeature) {
		var _tp = getIntValue(calculatorCallBackGetValue("type"));
		var tmp_style = createmapobjectstyle(8, _tp);
		if (tmp_style) {

			_editor_style.strokeColor = tmp_style.strokeColor;
			_editor_style.strokeDashstyle = tmp_style.strokeDashstyle;
			_editor_feature.style = _editor_style;
			_editor_modifyVector.removeFeatures(_editor_feature);
			_editor_modifyVector.addFeatures(_editor_feature);
			_editor_drawFeature.selectControl.select(_editor_feature);
		}
	}
}

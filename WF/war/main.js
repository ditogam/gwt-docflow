function setCookie(cname, cvalue, exdays) {
	var d = new Date();
	d.setTime(d.getTime() + (exdays * 24 * 60 * 60 * 1000));
	var expires = "expires=" + d.toUTCString();
	document.cookie = cname + "=" + cvalue + "; " + expires;
}
function saveCookie(cname, cvalue) {
	setCookie(cname, cvalue, 100);
}
function getCookie(cname) {
	var name = cname + "=";
	var ca = document.cookie.split(';');
	for (var i = 0; i < ca.length; i++) {
		var c = ca[i];
		while (c.charAt(0) == ' ')
			c = c.substring(1);
		if (c.indexOf(name) != -1)
			return c.substring(name.length, c.length);
	}
	return "";
}

function getIntValue(value) {
	var val = parseInt(value);
	if (isNaN(val))
		val = 0;
	return val;

}

function removeOfflineSaved(_name) {
	var _8 = isc.Offline.get(_name);
	if (_8)
		isc.Offline.remove(_name);
}

function getLanguageId() {
	var lng = getCookie(LANGUAGE_ID_NAME);
	if (!lng) {
		lng = 1;
	}
	setCookie(LANGUAGE_ID_NAME, lng, 100);
	return lng;
}

function makeFieldsBasics(_dsName, _fieldNames) {
	if (!_dsName || !_fieldNames)
		return;
	var _ds = isc.DataSource.get(_dsName);
	if (!_ds)
		return;
	for (var i = 0; i < _fieldNames.length; i++) {
		var _field = _ds.getField(_fieldNames[i]);
		if (_field) {
			_field.group = "basics";
			_field.xmlAttribute = true;
			_field.basic = "true";
		}
	}
}

function saveCreatorProps(_creator, _props) {
	if (!_creator || !_creator)
		return;
	for ( var key in _props) {
		var _value = _props[key];
		var _field = _creator.getField(key);
		if (_field) {
			_field.setValue(_value);
			_field.$48z = true;
		} else
			_creator.setValue(key, _value);
	}
	_creator.save();
}

function getValueId(_name, _default) {
	var vl = getCookie(_name);
	if (!vl && _default) {
		vl = _default;
	}
	if (vl)
		setCookie(_name, vl, 100);
	return vl;
}

var LANGUAGE_ID_NAME = "LANGUAGE_ID_NAME";
var LANGUAGE_ID = getValueId(LANGUAGE_ID_NAME, 1);
var SYSTEM_ID_NAME = "SYSTEM_ID_NAME";
var SYSTEM_ID = getValueId(SYSTEM_ID_NAME, 1);
var USER_NAME_C = "USER_NAME_C";
var USER_NAME = getValueId(USER_NAME_C);

function saveSystemCookies() {
	saveCookie(USER_NAME_C, USER_NAME);
	saveCookie(SYSTEM_ID_NAME, SYSTEM_ID);
	saveCookie(LANGUAGE_ID_NAME, LANGUAGE_ID);
}

var LANGUAGES = [];
var ALL_DATASOURCES = [];
var ALL_DEPENDENCY = [];

var MAIN_PANEL;
var SYSTEM_MODULES = {};

function getTopLevelWidget(globals) {

	if (!globals) {
		return null;
	}

	var globalKeys = isc.isAn.Array(globals) ? globals : isc.getKeys(globals)
	// find the last top-level Canvas in the globals and return it
	//
	// Note: globalEvalWithCapture return globalIDs in the order they were
	// created.
	// Typically the top-level container is declared last since it incorporates
	// other
	// Canvii declared before it, so we count down from the last created Canvas
	// here.
	var _screen;
	for (var i = 0; i < globalKeys.length; i++) {
		var global = globalKeys[i];
		var obj = window[global]; // globals are IDs, dereference

		if (obj && isc.isA.Canvas(obj) && !obj.destroyed
				&& obj.parentElement == null && obj.masterElement == null) {
			_screen = obj;
		}
	}
	return _screen;
}

function makefunct() {
	alert(arguments.length);
}

function setCookie(cname, cvalue, exdays) {
	var d = new Date();
	d.setTime(d.getTime() + (exdays * 24 * 60 * 60 * 1000));
	var expires = "expires=" + d.toUTCString();
	document.cookie = cname + "=" + cvalue + "; " + expires;
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

var LANGUAGE_ID_NAME = "LANGUAGE_ID_NAME";

function getLanguageId() {
	var lng = getCookie(LANGUAGE_ID_NAME);
	if (!lng) {
		lng = 1;
	}
	setCookie(LANGUAGE_ID_NAME, lng, 100);
	return lng;
}

var LANGUAGE_ID = getLanguageId();
var LANGUAGES=[];

function getIntValue(value) {
	var val = parseInt(value);
	if (isNaN(val))
		val = 0;
	return val;

}
function fileUpload(form, action_url, _field, callfunction) {
	// Create the iframe...
	var iframe = document.createElement("iframe");
	iframe.setAttribute("id", "upload_iframe");
	iframe.setAttribute("name", "upload_iframe");
	iframe.setAttribute("width", "0");
	iframe.setAttribute("height", "0");
	iframe.setAttribute("border", "0");
	iframe.setAttribute("style", "width: 0; height: 0; border: none;");

	// Add to document...
	form.parentNode.appendChild(iframe);
	window.frames['upload_iframe'].name = "upload_iframe";

	iframeId = document.getElementById("upload_iframe");

	// Add event...
	var eventHandler = function() {

		if (iframeId.detachEvent)
			iframeId.detachEvent("onload", eventHandler);
		else
			iframeId.removeEventListener("load", eventHandler, false);

		// Message from server...
		if (iframeId.contentDocument) {
			content = iframeId.contentDocument.body.innerHTML;
		} else if (iframeId.contentWindow) {
			content = iframeId.contentWindow.document.body.innerHTML;
		} else if (iframeId.document) {
			content = iframeId.document.body.innerHTML;
		}
		try {
			content = content.replace('<pre>', '');
			content = content.replace('</pre>', '');
			content = content.trim();
			var _index = content.indexOf(">");
			var _len = content.length;
			if (_index >= 0)
				content = content.substr(_index + 1, _len - _index);

		} catch (err) {

			var vDebug = "";
			// for ( var prop in err) {
			// vDebug += "property: " + prop + " value: [" + err[prop] + "]\n";
			// }
			vDebug += "sssssstoString(): " + " value: [" + err.toString() + "]";

			alert(vDebug);
		}
		try {

			var res = JSON.parse(content, true);
			var funct = window[callfunction];
			funct(_field, res);

		} catch (err) {

			var vDebug = "";
			// for ( var prop in err) {
			// vDebug += "property: " + prop + " value: [" + err[prop] + "]\n";
			// }
			vDebug += "cccctoString(): " + " value: [" + err.toString() + "]";

			alert(vDebug);
		}

	}

	if (iframeId.addEventListener)
		iframeId.addEventListener("load", eventHandler, true);
	if (iframeId.attachEvent)
		iframeId.attachEvent("onload", eventHandler);

	// Set properties of form...
	form.setAttribute("target", "upload_iframe");
	form.setAttribute("action", action_url);
	form.setAttribute("method", "post");
	form.setAttribute("enctype", "multipart/form-data");
	form.setAttribute("encoding", "multipart/form-data");

	form.submit();

}

function post_upload_file(field, actionUrl, callfunction) {
	// The rest of this code assumes you are not using a library.
	// It can be made less wordy if you use one.
	var _form = document.createElement("form");
	_form.setAttribute("style", "display:none");

	var hiddenField = document.createElement("input");
	hiddenField.setAttribute("type", "file");
	hiddenField.setAttribute("name", "TMPPPP");

	_form.appendChild(hiddenField);
	document.body.appendChild(_form);
	hiddenField.click();

	hiddenField.onchange = function() {
		if (upload_started)
			upload_started('1');
		upload_started = null;
		fileUpload(_form, actionUrl, field, callfunction);
	};

}

function removeOfflineSaved(_name) {
	var _8 = isc.Offline.get(_name);
	if (_8)
		isc.Offline.remove(_name);
}

function addContactOffline(_cId, _contact_name, _key_name) {
	var _json_text = isc.Offline.get(_key_name);
	if (!_json_text) {
		_json_text = '{}';
	}

	var _json = JSON.parse(_json_text, true);
	var _obj_record = JSON.parse("{}", true);
	_obj_record["cId"] = _cId;
	_obj_record["contact_name"] = _contact_name;
	var _contacts = _json['contacts'];
	if (!_contacts) {
		_contacts = [];
		_json['contacts'] = _contacts;
	}
	_contacts.push(_obj_record);
	_json_text = JSON.stringify(_json);
	isc.Offline.put(_key_name, _json_text);
}

function addContact(_ds, _cId, _contact_name, _key_name) {
	try {
		var _record = [];
		_record["cId"] = getIntValue(_cId);
		_record["contact_name"] = _contact_name;
		_ds.addData(_record);
		if (_key_name) {
			addContactOffline(_cId, _contact_name, _key_name);

		}
	} catch (err) {
		var vDebug = "";
		// for ( var prop in err) {
		// vDebug += "property: " + prop + " value: [" + err[prop] + "]\n";
		// }
		vDebug += "cccctoString(): " + " value: [" + err.toString() + "]";

		alert(vDebug);
	}
}

function refillOfflineContacts(_key_name) {
	for (var int = 0; int < 100; int++) {
		var c_id = int + 1;
		addContactOffline(c_id, c_id + "_data", _key_name)
	}
}

function refillContacts(_ds, _key_name) {
	// removeOfflineSaved(_key_name);
	// alert(_ds);
	var _json_text = isc.Offline.get(_key_name);
	if (!_json_text)
		return;
	try {

		var _json = JSON.parse(_json_text, true);
		for (var i = 0; i < _json.contacts.length; i++) {
			var _contact = _json.contacts[i];

			var _cId = _contact.cId;
			var _contact_name = _contact.contact_name;
			addContact(_ds, _cId, _contact_name);
		}
	} catch (err) {
		var vDebug = "";
		// for ( var prop in err) {
		// vDebug += "property: " + prop + " value: [" + err[prop] + "]\n";
		// }
		vDebug += "cccctoString(): " + " value: [" + err.toString() + "]";

		alert(vDebug);
	}
}
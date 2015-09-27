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
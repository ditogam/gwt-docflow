var calculatorCallBackConstants, calculatorCallBackGetValue, calculatorCallBackSetValue, 
calculatorCallBackSetFieldProperty, calculatorCallBackGetFieldProperty, calculatorCallBackgenerateProportions, 
calculatorCallBackGetField, calculatorCallBackGetDisplayValue, calculatorCallBackExecDS, calculatorCallBackFieldDefRequaiered, calculatorCallBackSetFieldListValues;
var image_functions, upload_started;

function getDoubleValue(value) {
	var val = parseFloat(value);
	if (isNaN(val))
		val = 0.0;
	return val;
}

function getIntValue(value) {
	var val = parseInt(value);
	if (isNaN(val))
		val = 0;
	return val;

}

function getBooleanFieldValue(fieldName) {
	var val = calculatorCallBackGetValue(fieldName);
	if (!val)
		val = "false";
	val += "";
	val = val.trim();
	return val == ("true") ? true : false;
}

function setEditableAndRequered(fields, enable, notrequered) {
	for (i = 0; i < fields.length; i++) {
		var fieldName = fields[i];
		var fdi = calculatorCallBackGetField(fieldName);
		if (!fdi)
			continue;
		fdi.setProperty("disabled", !enable);
		calculatorCallBackFieldDefRequaiered(fieldName, enable);
		for (j = 0; j < notrequered.length; j++) {
			var fieldNameN = notrequered[j];
			if (new String(fieldNameN).valueOf() == new String(fieldName)
					.valueOf()) {
				calculatorCallBackFieldDefRequaiered(fieldName, false);
			}
		}
	}
}

function generateTest(fieldName) {
	var map = new Object;
	var mapSelect = new Object;
	mapSelect["infot_type"] = "select id, ipname cvalue from info_problem_types where ip_type=2 order by 1";
	map["DocFlow"] = mapSelect;
	calculatorCallBackSetFieldListValues(map);
}

function fileUpload(form, action_url, fieldName, callfunction) {
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
			funct(fieldName, res);

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

function post_upload_file(fieldName, callfunction) {
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
		fileUpload(_form, 'FileUpload.jsp', fieldName, callfunction);
	};

}

function proceedfileupload(fieldName) {
	var file_item = calculatorCallBackGetField(fieldName);
	if (file_item["icons"])
		return;

	var _icons = [
			{
				src : "",
				click : function(form, item) {
					var _val = calculatorCallBackGetValue(fieldName);

					if (!_val)
						post_upload_file(fieldName, 'setfileuploadresult');
					else
						window
								.open("FileDownload.jsp?id=" + _val,
										"yourWindowName",
										"location=yes,resizable=yes,scrollbars=yess,status=yes");
				}
			}, {
				src : "eraser.png",
				click : function(form, item) {
					setfileitemvalue(fieldName, null);
				}

			} ];
	file_item.setProperty("icons", _icons);
	var _val = calculatorCallBackGetValue(fieldName);
	setfileitemvalue(fieldName, _val);

}

function setfileitemvalue(fieldName, _value) {
	var file_item = calculatorCallBackGetField(fieldName);
	var icon = "[SKIN]/pickers/search_picker.png";
	if (_value)
		icon = "[SKIN]/pickers/date_picker.png";
	calculatorCallBackSetValue(fieldName, _value);
}

function setfileuploadresult(fieldName, result) {
	setfileitemvalue(fieldName, result[0].id + "");
}

var Base64 = {

	// private property
	_keyStr : "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=",

	// public method for encoding
	encode : function(input) {
		var output = "";
		var chr1, chr2, chr3, enc1, enc2, enc3, enc4;
		var i = 0;

		input = Base64._utf8_encode(input);

		while (i < input.length) {

			chr1 = input.charCodeAt(i++);
			chr2 = input.charCodeAt(i++);
			chr3 = input.charCodeAt(i++);

			enc1 = chr1 >> 2;
			enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
			enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
			enc4 = chr3 & 63;

			if (isNaN(chr2)) {
				enc3 = enc4 = 64;
			} else if (isNaN(chr3)) {
				enc4 = 64;
			}

			output = output + this._keyStr.charAt(enc1)
					+ this._keyStr.charAt(enc2) + this._keyStr.charAt(enc3)
					+ this._keyStr.charAt(enc4);

		}

		return output;
	},

	// public method for decoding
	decode : function(input) {
		var output = "";
		var chr1, chr2, chr3;
		var enc1, enc2, enc3, enc4;
		var i = 0;

		input = input.replace(/[^A-Za-z0-9\+\/\=]/g, "");

		while (i < input.length) {

			enc1 = this._keyStr.indexOf(input.charAt(i++));
			enc2 = this._keyStr.indexOf(input.charAt(i++));
			enc3 = this._keyStr.indexOf(input.charAt(i++));
			enc4 = this._keyStr.indexOf(input.charAt(i++));

			chr1 = (enc1 << 2) | (enc2 >> 4);
			chr2 = ((enc2 & 15) << 4) | (enc3 >> 2);
			chr3 = ((enc3 & 3) << 6) | enc4;

			output = output + String.fromCharCode(chr1);

			if (enc3 != 64) {
				output = output + String.fromCharCode(chr2);
			}
			if (enc4 != 64) {
				output = output + String.fromCharCode(chr3);
			}

		}

		output = Base64._utf8_decode(output);

		return output;

	},

	// private method for UTF-8 encoding
	_utf8_encode : function(string) {
		string = string.replace(/\r\n/g, "\n");
		var utftext = "";

		for ( var n = 0; n < string.length; n++) {

			var c = string.charCodeAt(n);

			if (c < 128) {
				utftext += String.fromCharCode(c);
			} else if ((c > 127) && (c < 2048)) {
				utftext += String.fromCharCode((c >> 6) | 192);
				utftext += String.fromCharCode((c & 63) | 128);
			} else {
				utftext += String.fromCharCode((c >> 12) | 224);
				utftext += String.fromCharCode(((c >> 6) & 63) | 128);
				utftext += String.fromCharCode((c & 63) | 128);
			}

		}

		return utftext;
	},

	// private method for UTF-8 decoding
	_utf8_decode : function(utftext) {
		var string = "";
		var i = 0;
		var c = c1 = c2 = 0;

		while (i < utftext.length) {

			c = utftext.charCodeAt(i);

			if (c < 128) {
				string += String.fromCharCode(c);
				i++;
			} else if ((c > 191) && (c < 224)) {
				c2 = utftext.charCodeAt(i + 1);
				string += String.fromCharCode(((c & 31) << 6) | (c2 & 63));
				i += 2;
			} else {
				c2 = utftext.charCodeAt(i + 1);
				c3 = utftext.charCodeAt(i + 2);
				string += String.fromCharCode(((c & 15) << 12)
						| ((c2 & 63) << 6) | (c3 & 63));
				i += 3;
			}

		}

		return string;
	}

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
	alert(amounts);
	if (amounts <= 0)
		calculatorCallBackSetValue("amount", "");
	else
		calculatorCallBackSetValue("amount", amount.toFixed(2) + "");
}
function ntwpriceAmountCalculate(fieldName) {
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
				alert(value);
			}
			try {
				ntwpricecalculatentwAmount();
			} catch (e) {
				alert(e);
			}
		}
	}
	ntwpricecalculatentwAmount();
}

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
			vDebug += "Errorr cccctoString(): " + " value: [" + err.toString()
					+ "]";

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

function post_upload_file(field, actionUrl, callfunction, types) {
	// The rest of this code assumes you are not using a library.
	// It can be made less wordy if you use one.
	var _form = document.createElement("form");
	_form.setAttribute("style", "display:none");

	var hiddenField = document.createElement("input");
	hiddenField.setAttribute("type", "file");
	hiddenField.setAttribute("name", "TMPPPP");
	if (types)
		hiddenField.setAttribute("accept", types);

	_form.appendChild(hiddenField);
	document.body.appendChild(_form);
	hiddenField.click();

	hiddenField.onchange = function() {
		if (upload_started) {
			upload_started('1');
		}
		upload_started = null;
		fileUpload(_form, actionUrl, field, callfunction);
	};
}
if (window.isc && !window.isc.module_ImageUpload) {
	isc.module_ImageUpload = 1;
	// alert('module_ImageUpload=' + LANGUAGES + " LANGUAGE_ID=" + LANGUAGE_ID);
	isc.ClassFactory.defineClass("ImageUploadItem", "CanvasItem");
	isc.ImageUploadItem
			.addProperties({
				width : 200,
				_constructor : ImageUploadItem,
				actionUrl : "jsp/FileUpload.jsp",
				showTitle : false,
				init : function() {
					this.icons = [ {
						src : "display_image.png",
						click : function(form, item) {
							var url = item.actionUrl;
							window["upload_started"] = item.beginUpload;
							window["callfunction"] = function(_field, _result) {
								try {
									_field.setValue(_result[0].id);
								} catch (e) {
									alert("errorrr" + e);
								}
							};
							post_upload_file(item, url, "callfunction",
									item.file_types);

						}
					} ];
					this.canvas = isc.DynamicForm.create({
						numCols : 2,
						fields : [
								{
									name : "img_id",
									title : "",
									showTitle : false,
									width : 20,
									height : 20,
									// visible : false,
									_constructor : "CanvasItem",
									canvas : isc.Img.create({
										imageType : "center",
										width : 20,
										height : 20
									}),
									setValue : function(value) {
										if (!value)
											this.canvas.setSrc('empty.png');
										else {
											this.canvas
													.setSrc('getimage.jsp?id='
															+ value);
										}
									}
								}, {
									name : "img",
									title : "",
									showTitle : false,
									_constructor : "StaticTextItem"
								} ]
					});
					return this.Super('init', arguments);
				},
				setActionUrl : function(_1) {
					this.actionUrl = _1;
				},
				getActionUrl : function() {
					return this.actionUrl;
				},
				getValue : function() {
					var _val = this.Super("getValue", arguments);
					return _val;
				},
				setValue : function(value) {
					this.Super("setValue", arguments);
					var undef;
					this.canvas.setValues({
						img_id : value
					});
					if (value) {
						var cr = {
							id : value
						};
						var _editor = this;
						isc.DataSource.get("ImageDataDS").fetchData(cr,
								function(dsResponse, data) {
									if (data && data.length > 0) {
										_editor.canvas.setValues({
											img_id : data[0]["id"],
											img : data[0]["bdata_filename"]
										});

									} else
										_editor.canvas.canvas.setValues({});
								});
					}
				}

			});
}
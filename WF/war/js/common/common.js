function addCaptionToObj(_obj, _captionids) {
	if (_obj.caption_id)
		_captionids.push({
			id : _obj.caption_id,
			obj : _obj
		});
}

function addChildRec(_children, _arr) {
	for (var i = 0; i < _arr.length; i++) {
		if (!_arr[i])
			continue;
		var _found;
		for (var k = 0; k < _arr[i].length; k++) {
			var toAdd = _arr[i][k];
			if (!toAdd)
				continue;
			for (var j = 0; j < _children.length; j++) {
				if (!_children[j])
					continue;
				if (_children[j] == toAdd) {
					_found = 1;
					break;
				}
			}
			if (!_found)
				_children.push(toAdd);
		}

	}
}

function copyArray(src, dest, startPos) {
	startPos = startPos || 0;
	for (var i = 0; i < src.length; i++) {
		dest.set(startPos + i, src.get(i));
	}
}

function travelandchangeids(_canvas, _map, _captionids) {
	addCaptionToObj(_canvas, _captionids);
	if (isc.isA.FormItem(_canvas) || isc.isA.StretchImgButton(_canvas)) {
		return;
	}
	_canvas.donnnn = true;
	var _id = _canvas.ID;
	var _new_id = isc.ClassFactory.getNextGlobalID(_canvas);
	isc.ClassFactory.addGlobalID(_canvas, _new_id);
	_map.push({
		id : _id,
		newid : _new_id
	});

	var _children = [];
	if (isc.isA.DynamicForm(_canvas)) {
		var _fields = _canvas.getFields();
		if (_fields)
			for (var i = 0; i < _fields.length; i++) {
				addCaptionToObj(_fields[i], _captionids);
				if (_fields[i].canvas)
					_children.push(_fields[i].canvas);
			}
	} else {
		if (isc.isA.TabSet(_canvas)) {
			var _tabs = _canvas.tabs;
			if (_tabs)
				for (var i = 0; i < _tabs.length; i++) {
					addCaptionToObj(_tabs[i], _captionids);
					if (_tabs[i].pane)
						_children.push(_tabs[i].pane);
				}
		}
	}
	addChildRec(_children, [ _canvas.members, _canvas.items, _canvas.children,
			_canvas.sections ]);

	for (var i = 0; i < _children.length; i++) {
		travelandchangeids(_children[i], _map, _captionids);
	}
}

function createComponentFromJS(_jstext, _callback) {
	var origAutoDraw = isc.Canvas.getInstanceProperty("autoDraw"), suppressAutoDraw = true;
	if (suppressAutoDraw)
		isc.Canvas.setInstanceProperty("autoDraw", false);
	isc.setAutoDraw(false);
	var tmp = isc.VLayout.create({
		width : "0",
		height : "0",
		autoDraw : false,
		visible : false
	});
	// var hiddenDiv = document.getElementById("divHidden");
	var result = isc.Class.evalWithVars(_jstext, null, null);
	tmp.addMember(result);
	var _map = [];
	var _captionids = [];
	travelandchangeids(result, _map, _captionids);
	if (_callback)
		_callback(result, _map);
	return result;
}

function initMainPanel(_rec) {

	var _js = _rec.panel_function;
	var _cssmodules = _rec.cssmodules;
	var _jsmodules = _rec.jsmodules;
	var _callback = function() {
		initMainPanelLoaded(_js);
	}
	loadSystemModules(_cssmodules, _jsmodules, _callback);
}

function initMainPanelLoaded(_js) {
	var _member = isc.Class.evalWithVars(_js, null, null);
	if (!MAIN_PANEL)
		MAIN_PANEL = isc.VLayout.create({
			width : "100%",
			height : "100%",
			showEdges : true,
			members : [ _member ]
		});
	else {
		MAIN_PANEL.destroy();
		MAIN_PANEL = null;
		initMainPanel(_js);
	}
}

function loadjscssfile(filename, filetype) {
	if (filetype == "js") { // if filename is a external JavaScript file
		var fileref = document.createElement('script')
		fileref.setAttribute("type", "text/javascript")
		fileref.setAttribute("src", filename)
	} else if (filetype == "css") { // if filename is an external CSS file
		var fileref = document.createElement("link")
		fileref.setAttribute("rel", "stylesheet")
		fileref.setAttribute("type", "text/css")
		fileref.setAttribute("href", filename)
	}
	if (typeof fileref != "undefined")
		document.getElementsByTagName("head")[0].appendChild(fileref)
}

function loadSystemModules(_cssmodules, _jsmodules, _callback) {
	if (_cssmodules) {
		_cssmodules = _cssmodules.split(',');
		for (var i = 0; i < _cssmodules.length; i++) {
			var cssm = _cssmodules[i];
			loadjscssfile(cssm, "css");
		}
	}
	if (!_jsmodules || _jsmodules.length == 0)
		_callback('loaded');
	else {
		_jsmodules = _jsmodules.split(',');
		addModules(_jsmodules, _callback);
	}
}

function addModules(modules, _callback) {
	$.getScripts({
		urls : modules,
		cache : true, // Default
		async : false, // Default
		success : function(response) {
			if (_callback)
				_callback(response)
		},
		error : function(response) {
			if (callback)
				_callback(response)
		}
	});
}

function getTextDiff(viewType, base, newtxt) {
	newtxt = newtxt ? newtxt : '';
	base = base ? base : '';
	base = difflib.stringAsLines(base.trim());
	newtxt = difflib.stringAsLines(newtxt.trim());
	viewType = parseInt(viewType);
	var sm = new difflib.SequenceMatcher(base, newtxt);
	var opcodes = sm.get_opcodes();
	var contextSize;
	var diff = diffview.buildView({
		baseTextLines : base,
		newTextLines : newtxt,
		opcodes : opcodes,
		baseTextName : "Base Text",
		newTextName : "New Text",
		contextSize : contextSize,
		viewType : viewType
	});
	return '<table class="diff">' + diff.innerHTML + '</table>';
}

function updateLanguageSession(_callback) {
	isc.DataSource.get("LanguageDS").fetchData({
		language_id : LANGUAGE_ID,
		id : 1
	}, _callback, {
		operationId : "updatelanguageid"
	});
}

function temp() {
	var df = isc.DynamicForm.create({
		ID : "pickerForm",
		values : {
			startMode : "simple",
			position : "auto"
		},
		numCols : 3,
		titleOrientation : "top",
		width : '100%',
		fields : [ {
			name : "style",
			title : "Initially show ColorPicker as",
			width : 200,
			type : "radioGroup",
			vertically : "false",
			valueMap : {
				"0" : "Side by Side Diff",
				"1" : "Inline Diff"
			},
			value : "0",
			changed : function(form, item, value) {
				var viewType = parseInt(value);
				var base = difflib.stringAsLines(form.getValue("curren"));
				var newtxt = difflib.stringAsLines(form.getValue("old"));
				var sm = new difflib.SequenceMatcher(base, newtxt);
				var opcodes = sm.get_opcodes();
				var contextSize;
				var diff = diffview.buildView({
					baseTextLines : base,
					newTextLines : newtxt,
					opcodes : opcodes,
					baseTextName : "Base Text",
					newTextName : "New Text",
					contextSize : contextSize,
					viewType : viewType
				});

				var tdata = document.createElement("tdata");
				tdata.appendChild(diff);
				var _val = tdata.innerHTML;
				form.setValue("diff", _val);
				form.setValue("diff1", _val)
			}
		}, {
			name : "curren",
			title : "Base Text",
			width : '*',
			type : "textArea"
		}, {
			name : "old",
			title : "Old Text",
			width : '*',
			type : "textArea"
		}, {
			name : "diff",
			title : "Difference",
			width : '100%',
			type : "textArea",
			colSpan : 3
		}, {
			name : "diff1",
			title : "Difference",
			width : '100%',
			type : "staticText",
			colSpan : 3
		} ]
	});
}
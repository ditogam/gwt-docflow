function loginWF() {
	var valueIconMap = {};

	for (var i = 0; i < LANGUAGES.length; i++) {
		valueIconMap[LANGUAGES[i].id + ''] = "getimage.jsp?id="
				+ LANGUAGES[i].img_id + '';
	}

	var lgFields = [ {
		name : "username",
		title : "Username",
		type : "text",
		required : true,
		defaultValue : USER_NAME
	}/*
		 * , { name : "password", title : "Password", required : true, type :
		 * "password" }
		 */, {
		name : "language",
		title : "Language",
		required : true,
		type : "select",
		optionDataSource : "LanguageDS",
		valueField : "id",
		displayField : "language_name",
		addUnknownValues : false,
		valueIcons : valueIconMap,
		value : LANGUAGE_ID
	}, {
		name : "system",
		title : "System",
		required : true,
		type : "select",
		optionDataSource : "SystemDS",
		valueField : "id",
		displayField : "system_name",
		addUnknownValues : false,
		value : SYSTEM_ID
	} ];
	var _df = isc.DynamicForm.create({
		dataSource : this.optionDataSource,
		width : 1,
		height : 1,
		autoDraw : false,
		fields : lgFields
	});

	var init_system = function(dsResponse, data) {
		var _id = _df.getField("system").getValue();

		var _callback = function(dsResponse, data, dsRequest) {
			if (!data)
				return;
			initMainPanel(data);
			_w.destroy();
		}

		isc.DataSource.get("SystemDS").fetchData({
			id : _id
		}, _callback, {
			operationId : "selectModulePath"
		});

	}

	var _login_click = function() {
		if (!_df.validate())
			return;
		LANGUAGE_ID = _df.getValue("language");
		SYSTEM_ID = _df.getValue("system");
		USER_NAME = _df.getValue("username");
		saveSystemCookies();
		updateLanguageSession(init_system);
	}
	var _w = isc.Window.create({
		title : "Login",
		autoSize : true,
		autoCenter : true,
		isModal : true,
		showModalMask : true,
		autoDraw : false,
		showCloseButton : false,
		showMinimizeButton : false,
		canDragResize : false,
		canDragReposition : false,
		items : [ isc.VLayout.create({
			autoDraw : false,
			height : 1,
			width : 1,
			members : [ _df, isc.HLayout.create({
				autoDraw : false,
				height : 1,
				width : "*",
				members : [ isc.IButton.create({
					autoDraw : false,
					title : "Login",
					click : _login_click
				}) ],
				layoutAlign : "left"
			}) ]
		}) ]
	});
	_w.show();

}

if (window.isc && !window.isc.module_Captions) {
	isc.module_Captions = 1;

	isc.DataSource.create({
		allowAdvancedCriteria : true,
		ID : "CaptionEditorSelect",
		Constructor : "SelectItem",
		fields : [],
		inheritsFrom : "SelectItem"
	});

	isc.DataSource.create({
		allowAdvancedCriteria : true,
		ID : "CaptionEditorCombo",
		Constructor : "ComboBoxItem",
		fields : [],
		inheritsFrom : "ComboBoxItem"
	});

	var _lngProps = {
		init : function() {
			this.Super('init', arguments);
			this.initWidget(this);
		},
		initWidget : function() {
			var lgFields = [];
			for (var i = 0; i < LANGUAGES.length; i++) {
				lgField = {
					name : "language_" + LANGUAGES[i].id,
					// width : "33%",
					title : LANGUAGES[i].name
				};
				lgFields.push(lgField);
			}
			this.pickListProperties = {
				showFilterEditor : true
			};
			this.width = "*";
			this.fetchMissingValues = true;
			this.optionDataSource = isc.DataSource.get("CaptionsDS");
			this.displayField = "language_" + LANGUAGE_ID;
			this.valueField = "id";
			this.addUnknownValues = false;
			this.pickListFields = lgFields;

			this._action_icons = [ {
				src : "[SKIN]actions/add.png",
				form_item : this,
				click : function(form, item) {
					this.form_item.addCaption();
				}
			}, {
				src : "[SKIN]actions/accept.png",
				form_item : this,
				click : function(form, item) {
					this.form_item.editCaption();
				}
			} ];
			this.setEditShowIcons(this.showEditIcons);

		},
		setEditShowIcons : function(_show) {
			var und;
			if (_show == und)
				_show = true;
			this.showEditIcons = _show;
			this.setIcons(_show ? this._action_icons : []);
			// this.redraw();
		},
		addCaption : function() {
			this.addEditCaption();
		},
		setValueToForm : function(_form, _name, _value, _liveObj) {

			var _item = _form.getItem(_name);
			if (_item) {
				_item.setValue(_value);
				_item.$48z = true;
			} else
				_form.setValue(_name, _value);
			if (_liveObj && _liveObj.isA(isc.DynamicForm)) {
				_liveObj.setGroupTitle(_value);
				_liveObj.isGroup = true;
			}
		},
		changed : function() {
			this.Super('changed', arguments);
			var liveComp;
			var liveObj;

			if (this.creator && this.creator.currentComponent
					&& this.creator.currentComponent.liveObject) {
				liveComp = this.creator.currentComponent;
				liveObj = liveComp.liveObject;
				var _value = this.getDisplayValue();
				var props = {};
				if (liveObj && liveObj.isA(isc.DynamicForm)) {
					props.groupTitle = _value;
					props.isGroup = true;
				} else
					props.title = _value;
				saveCreatorProps(this.creator, props);

			}

			// this.setValueToForm(this.form, 'groupTitle',
			// this.getDisplayValue(), liveObj);
			// this.setValueToForm(this.form, 'isGroup', true, liveObj);

		},
		editCaption : function() {
			var _editor = this;
			if (this.getValue())
				this.optionDataSource.fetchData({
					id : this.getValue()
				}, function(dsResponse, data) {
					if (data && data.length > 0) {
						_editor.addEditCaption(data[0]);
					}
				});
		},
		addEditCaption : function(record) {
			var lgFields = [];
			for (var i = 0; i < LANGUAGES.length; i++) {
				lgField = {
					name : "language_" + LANGUAGES[i].id,
					// width : "33%",
					title : LANGUAGES[i].name,
					required : true,
					icons : [ {
						src : 'getimage.jsp?id=' + LANGUAGES[i].img_id
					} ]
				};
				lgFields.push(lgField);
			}
			lgFields.push({
				name : "id",
				// width : "33%",
				visible : false
			});
			var _df = isc.DynamicForm.create({
				dataSource : this.optionDataSource,
				width : 1,
				height : 1,
				fields : lgFields,
				fetchOperation : "fetchAddEditCaption"
			});
			if (!record)
				record = {
					id : -1
				};
			_df.setValues(record);
			var _field = this;
			var _save_click = function() {
				var _validate = _df.validate(false);
				if (!_validate)
					return;
				var _f = function(dsResponse, data) {
					if (data && data.length > 0) {
						_field.setValue((data[0]).id);
						_field.fetchData();
						if (_field.languageWindow)
							_field.languageWindow.destroy();
					}
				};
				_df.dataSource.fetchData(_df.getValuesAsCriteria(), _f, {
					operationId : "fetchAddEditCaption"
				});

			};

			var _w = isc.Window.create({
				title : "Captions",
				autoSize : true,
				autoCenter : true,
				isModal : true,
				showModalMask : true,
				autoDraw : false,
				visibilityChanged : function(isVisible) {
					if (!isVisible)
						this.destroy();
				},
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
							title : "Save",
							click : _save_click
						}), isc.IButton.create({
							autoDraw : false,
							title : "Close",
							click : function() {
								_w.destroy();
							}
						}) ],
						layoutAlign : "left"
					}) ]
				}) ]
			});
			this.languageWindow = _w;
			_w.show();
		}
	};

	isc.ClassFactory.defineInterface("CaptionEditorInterface");
	isc.ClassFactory.defineClass("CaptionEditorSelect", "SelectItem",
			"CaptionEditorInterface");

	isc.CaptionEditorSelect.addProperties(_lngProps);

	isc.ClassFactory.defineClass("CaptionEditorCombo", "ComboBoxItem",
			"CaptionEditorInterface");

	isc.CaptionEditorCombo.addProperties(_lngProps);
	isc.ClassFactory.defineClass("CaptionEditor", "CaptionEditorSelect");

}
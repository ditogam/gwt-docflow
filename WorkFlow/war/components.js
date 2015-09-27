var custom_class_inited;
var contacts_df;

if (!custom_class_inited) {
	custom_class_inited = 1;
	isc.DataSource.create({
		allowAdvancedCriteria : true,
		ID : "CaptionEditor",
		Constructor : "SelectItem",
		fields : [],
		inheritsFrom : "SelectItem"
	});

	// var _tab_cls = isc.Canvas;
	// alert(_tab_cls);
	// _tab_cls.addProperties({
	// setCaption_id : function(_caption_id) {
	// this.caption_id = _caption_id;
	// this.title = "sdfjk";
	// }
	// });

	isc.ClassFactory.defineClass("CaptionEditor", "SelectItem");

	isc.CaptionEditor.addProperties({
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
			this.optionDataSource = isc.DataSource.get("CaptionsDS");
			this.displayField = "language_" + LANGUAGE_ID;
			this.valueField = "id";

			this.pickListFields = lgFields;
			var cm = isc.Menu.create({
				autoDraw : false,
				form_item : this,
				data : [ {
					title : "Add caption",
					icon : "actions/add.png",
					form_item : this,
					click : function(_1, _2, _3, _4) {
						this.form_item.addCaption();
					}
				}, {
					title : "Edit caption",
					icon : "actions/accept.png",
					form_item : this,
					click : function(_1, _2, _3, _4) {
						this.form_item.editCaption();
					}
				} ]
			});
			this.form.contextMenu = cm;
		},
		addCaption : function() {
			this.addEditCaption();
		},
		changed : function() {
			this.Super('changed', arguments);
			var _titleItem = this.form.getItem('title');
			alert(this.form.getChangedValues().length);
			if (_titleItem) {
				_titleItem.setValue(this.getDisplayValue());
				_titleItem.$48z = true;
			} else
				this.form.setValue('title', this.getDisplayValue());
			alert(this.form.getChangedValues().length);
		},
		editCaption : function() {
			var _editor = this;
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
					required : true
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
			_w.show();
		}
	});

	// for (var i = 0; i < add_caption_ids.length; i++) {
	// var ds = isc.DataSource.get(add_caption_ids[i]);
	// if (!ds)
	// continue;
	// var _field = ds.getField("title");
	// if (_field) {
	// _field.type = "integer";
	// _field.editorType = "CaptionEditor";
	// }
	// }

	isc.DataSource.create({
		allowAdvancedCriteria : true,
		ID : "ImageUploadItem",
		Constructor : "StaticTextItem",
		fields : [ {
			title : "Action Url",
			name : "actionUrl",
			xmlAttribute : true,
			type : "text",
			defaultValue : "FileUpload.jsp",
			basic : "true",
			group : "basics"
		} ],
		inheritsFrom : "FormItem"
	});

	isc.DataSource.create({
		allowAdvancedCriteria : true,
		ID : "ContactItem",
		Constructor : "StaticTextItem",
		fields : [],
		inheritsFrom : "StaticTextItem"
	});

	var _contacts_ds = isc.DataSource.create({
		allowAdvancedCriteria : true,
		ID : "Contacts",
		clientOnly : true,
		fields : [ {
			name : "cid",
			type : "integer",
			primaryKey : true,
			title : "ID",
			hidden : true
		}, {
			name : "contact_name",
			title : "Contact",
			type : "text"
		} ]
	});
	var _contacts_key = "DF_CONTACTS";
	// removeOfflineSaved(_contacts_key);
	// refillOfflineContacts(_contacts_key);
	refillContacts(_contacts_ds, _contacts_key);
	// for ( var i = 4; i < 100; i++) {
	// addContact(_contacts_ds, i, i + "_sdfsdf", _contacts_key);
	// }

	isc.ClassFactory.defineClass("ContractFormItem", "CanvasItem");

	isc.ContractFormItem.addClassProperties({
		_VALUE_NAME : "_VALUE_NAME",
		_DISPLAY_VALUE_NAME : "_DISPLAY_VALUE_NAME"
	});

	isc.ContractFormItem
			.addProperties({
				showTitle : false,
				height : "20",
				width : "*",
				init : function() {
					this.Super('init', arguments);
					this.canvas = isc.VLayout.create({});
					this.initWidget(this);
				},
				initWidget : function() {
					this.Super('initWidget', arguments);
					var _lbl = isc.Label.create({
						autoDraw : false,
						height : "16",
						width : 1,
						styleName : "staticTextItem",
					});
					var _closeBtn = isc.ImgButton.create({
						autoDraw : false,
						height : "16",
						width : "16",
						src : "close.png",
						setFieldAndForm : function(_field, _form) {
							this.field = _field;
							this.form = _form;

						},
						click : function() {
							this.form.removeContact(this.field);
						}

					});
					var _canvas = isc.HLayout.create({
						autoDraw : false,
						height : "1",
						width : "1",
						members : [ _lbl, _closeBtn ],
						setField : function(_field) {
							this.field = _field;
						}
					});

					this.label = _lbl;
					this.closeBtn = _closeBtn;
					_closeBtn.setFieldAndForm(this, this.form);
					this.setCanvas(_canvas);
				},
				setDisplayValue : function(p_value) {
					this.label.contents = p_value;
					this.label.redraw();
				},
				getDisplayValue : function() {
					return this.label.contents;
				},
				getCloseBtn : function() {
					return this.closeBtn;
				},
				getDisplayLbl : function() {
					return this.label;
				},
				getAllValues : function() {
					var _value = [];
					_value[isc.ContractFormItem._VALUE_NAME] = this.getValue();
					_value[isc.ContractFormItem._DISPLAY_VALUE_NAME] = this
							.getDisplayValue();
					return _value;
				},
				setAllValues : function(_value) {
					if (!_value)
						_value = [];
					this.setValue(_value[isc.ContractFormItem._VALUE_NAME]);
					this
							.setDisplayValue(_value[isc.ContractFormItem._DISPLAY_VALUE_NAME]);
				}
			});

	isc.ClassFactory.defineClass("ContractForm", "VLayout");
	isc.DataSource.create({
		allowAdvancedCriteria : true,
		ID : "ContractForm",
		Constructor : "Canvas",
		fields : [],
		inheritsFrom : "Canvas"
	});

	isc.ContractForm.addClassProperties({
		cb_item_name : "cb_new_item"
	});

	isc.ContractForm
			.addProperties({
				width : 1,
				height : 1,
				initWidget : function() {
					this.Super("initWidget", arguments);
					this.createNewForm();
				},
				createNewFieldClassConstructor : function() {
					return "ContractFormItem";
				},
				createNewCombo : function() {
					return {
						name : isc.ContractForm.cb_item_name,
						showTitle : false,
						textBoxStyle : "staticTextItem",
						showPickerIcon : false,
						_constructor : "ComboBoxItem",
						displayField : "contact_name",
						changeOnKeypress : true,
						valueField : "cid",
						optionDataSource : _contacts_ds,
						addUnknownValues : false,
						allowEmptyValue : false,
						completeOnTab : true,
						formatOnBlur : true,
						keyPress : function(item, form, key) {
							if (key && key == 'Enter') {

								form.addContact(this);
							}
						},
						onblur : function() {
							alert(this.getValue() + " "
									+ this.getDisplayValue());
						}
					};
				},
				createNewForm : function(_values) {
					var new_fields = [];

					if (this.formItem) {
						this.removeMember(this.formItem);
						this.formItem.destroy();
					}
					var _other_fields = [];
					if (_values)
						for (var i = 0; i < _values.length; i++) {
							_value = _values[i];
							var _newClassConstructor = this
									.createNewFieldClassConstructor();
							var _field = {
								name : "contact_" + (i + 1),
								_constructor : _newClassConstructor
							};
							// _field.setAllValues(_value);
							new_fields.push(_field);
							_other_fields.push(_field.name);
						}

					new_fields.push(this.createNewCombo());

					var _form = isc.DynamicForm
							.create({
								autoDraw : false,
								numCols : 10,
								getAllValues : function(_except) {
									var _fields = this.getFields();
									var _newfields = [];
									var _field;
									for (var i = 0; i < _fields.length - 1; i++) {
										if (_except
												&& _except.name == _fields[i].name)
											continue;
										_newfields.push(_fields[i]
												.getAllValues());
									}
									return _newfields;
								},
								addContact : function(item) {
									var _cid = item.getValue();
									var _contact_name = item.getDisplayValue();
									if (!_cid || !_contact_name) {
										item.setValue(null);
										item.focusInItem();
										return;
									}
									var _newfields = this.getAllValues();
									var _newVal = [];

									_newVal[isc.ContractFormItem._VALUE_NAME] = _cid;
									_newVal[isc.ContractFormItem._DISPLAY_VALUE_NAME] = _contact_name;
									_newfields.push(_newVal);
									this.creatorLayer.createNewForm(_newfields);

								},
								removeContact : function(item) {
									var _newfields = this.getAllValues(item);
									this.creatorLayer.createNewForm(_newfields);
								}

							});
					_form.setFields(new_fields);
					for (var i = 0; i < _other_fields.length; i++) {
						var _name = _other_fields[i];
						var _field = _form.getField(_name);
						var _value = _values[i];
						_field.setAllValues(_value);
					}
					_form.creatorLayer = this;
					this.formItem = _form;
					this.addMember(_form);
					var _field = _form.getField(isc.ContractForm.cb_item_name);
					_field.focusInItem();
					return _form;
				}

			});

	isc.ClassFactory.defineClass("ImageUploadItem", "StaticTextItem");

	isc.ImageUploadItem.addProperties({
		width : 200,
		actionUrl : "FileUpload.jsp",
		showTitle : false,
		init : function() {

			this.icons = [ {
				src : "display_image.png",
				click : function(form, item) {

					// addContact(_contacts_ds, 1, "1sdfsdf", _contacts_key);
					// addContact(_contacts_ds, 2, "2sdfsdf", _contacts_key);
					// addContact(_contacts_ds, 3, "3sdfsdf", _contacts_key);

					var url = item.actionUrl;
					window["upload_started"] = function(txt) {
						alert(txt);
					};
					window["callfunction"] = function(_field, _result) {
						alert(txt);
					};
					post_upload_file(item, url, "callfunction");

				}
			} ];
			return this.Super('init', arguments);
		},
		setDefaultValue : function(_1) {
			this.Super("setDefaultValue", arguments);
		},
		setActionUrl : function(_1) {
			this.actionUrl = _1;
		},
		getetActionUrl : function() {
			return this.actionUrl;
		},
		setShowTitle : function(_1) {
		}

	});

}
if (window.isc && !window.isc.module_Dependency) {
	isc.module_Dependency = 1;

	var _props = {
		init : function() {
			this.Super('init', arguments);
			this.initWidget(arguments);
		},
		initWidget : function() {
			this.Super('initWidget', arguments);
			if (this.dependency)
				this.setDependency(this.dependency);
		},
		setDependency : function() {
			this.dependency = arguments[0];
			if (!this.dependency)
				return;

			var _json;
			try {
				_json = JSON.parse(this.dependency);
			} catch (e) {
				alert(e);
				return;
			}
			var optionDataSource = _json["optionDataSource"];
			var valueField = _json["valueField"];
			var displayField = _json["displayField"];
			var dependency_fields = _json["dependency_fields"];
			if (valueField)
				this.valueField = (valueField);
			if (displayField)
				this.displayField = (displayField);
			if (optionDataSource)
				this.setOptionDataSource(optionDataSource);
			if (dependency_fields) {
				this.dependency_fields = dependency_fields;
				this.change = function() {
					alert('changeee' + this);
				}
			}
		}
	};

	isc.ClassFactory.defineInterface("DependencyInterface");
	isc.ClassFactory.defineClass("DependencySelectItem", "SelectItem",
			"DependencyInterface");

	isc.DependencySelectItem.addProperties(_props);

	isc.ClassFactory.defineClass("DependencyComboBoxItem", "ComboBoxItem",
			"DependencyInterface");

	isc.DependencyComboBoxItem.addProperties(_props);

	isc.DataSource.create({
		allowAdvancedCriteria : true,
		ID : "DependencyEditor",
		Constructor : "CanvasItem",
		fields : [],
		inheritsFrom : "CanvasItem"
	});

	var _depProps = {
		init : function() {
			this.Super('init', arguments);
		},
		createCanvas : function() {
			this.width = "*";
			var dFields = [ {
				name : "optionDataSource",
				title : "DataSource",
				_constructor : "ComboBoxItem",
				valueMap : ALL_DATASOURCES
			}, , {
				name : "valueField",
				title : "Value Field",
				type : "string"
			}, {
				name : "displayField",
				title : "Display Field",
				type : "string"
			}, {
				name : "dependency_fields",
				title : "DependencyFields",
				type : "string",
				width : "150"
			} ];

			this.value_editor = isc.DynamicForm.create({
				width : "*",
				height : 1,
				fields : dFields,
				titleOrientation : "top",
				numCols : 1
			});
			this.value_editor.setValues(this.form.getValues());
			this._action_icons = [ {
				src : "[SKIN]actions/accept.png",
				form_item : this,
				click : function(form, item) {
					this.form_item.saveDataValue();
				}
			} ];
			this.setIcons(this._action_icons);
			var fields = this.value_editor.getFields();
			for (var i = 0; i < fields.length; i++) {
				var field = fields[i];
				field.creator = this;
				field.keyPress = function(_1, _2, _3) {
					if (_3 == "Enter")
						_1.creator.saveDataValue();
				}
			}
			return this.value_editor;

		},
		changed : function() {
			this.Super('changed', arguments);
			alert('changed');
		},
		saveDataValue : function() {
			var fields = this.value_editor.getFields();
			var _val = {};
			for (var i = 0; i < fields.length; i++) {
				var field = fields[i];
				_val[field.name] = field.getValue();
			}
			var _newval = JSON.stringify(_val);
			_val.dependency = _newval;
			saveCreatorProps(this.creator, _val);
		},
		setValue : function(value) {
			this.Super('setValue', arguments);
			this.value_editor.clearValues();
			if (!value)
				return;
			var _json;
			try {
				_json = JSON.parse(value);
			} catch (e) {
				return;
			}
			if (!_json)
				return;

			for ( var key in _json) {
				this.value_editor.setValue(key, _json[key]);
			}
		}
	};

	isc.ClassFactory.defineClass("DependencyEditor", "CanvasItem");

	isc.DependencyEditor.addProperties(_depProps);

}
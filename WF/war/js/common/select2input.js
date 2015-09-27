if (window.isc && !window.isc.module_Select2) {
	isc.module_Select2 = 1;

	isc.DataSource.create({
		allowAdvancedCriteria : true,
		ID : "Select2Input",
		Constructor : "Select2Item",
		fields : [],
		inheritsFrom : "TextAreaItem"
	});
	var const_container = 's2containerid_';
	var const_choices = 's2choicesid_';
	var const_choice = 's2choicesid_';

	var _tiProps = {

		// write out a textarea with a known ID
		init : function() {
			this.Super('init', arguments);
			this.initWidget(arguments);
		},
		initWidget : function() {
			this.Super('initWidget', arguments);
			if (!this.noChooserIcon) {
				this._action_icons = [ {
					src : "[SKIN]actions/view.png",
					form_item : this,
					click : function(form, item) {
						item.chooseItems();
					}
				} ];
				this.setIcons(this._action_icons);
			}
		},
		chooseItems : function() {
			var ht = isc.HirarchyTree.create({
				type_id : 2,
				recordDoubleClick : function() {
					this.df.getField('items').addDataItem(
							this.getSelectedRecord());
				}
			});
			var lgFields = [ {
				name : "items",
				type : "select2",
				noChooserIcon : true,
				showTitle : false,
				width : "*"
			} ];
			var _width = 500;
			var _df = isc.DynamicForm.create({
				showEdges : true,
				width : _width,
				height : 1,
				numCols : 1,
				fields : lgFields,
				showTitle : false
			});
			ht.df = _df;

			var clickH = function() {
				_w.chooserItem.setValue(_df.getField("items").getDataValue());
				_w.destroy();
			}
			var _w = isc.Window.create({
				title : this.getTitle(),
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
					height : 500,
					width : _width,
					members : [ ht, isc.VLayout.create({
						autoDraw : false,
						height : 1,
						width : 1,
						members : [ ht, _df, isc.HLayout.create({
							autoDraw : false,
							height : 1,
							width : "*",
							members : [ isc.IButton.create({
								autoDraw : false,
								title : "Save",
								click : clickH
							}), isc.IButton.create({
								autoDraw : false,
								title : "Close",
								click : function() {
									_w.destroy();
								}
							}) ],
							layoutAlign : "center"
						}) ]
					}) ]
				}) ]
			});
			this.languageWindow = _w;
			_w.chooserItem = this;
			_w.show();
			_df.getField('items').setValue(this.localdata);
		},
		addDataList : function(_vals) {
			if (!isc.isAn.Array(_vals))
				_vals = [ _vals ];
			for (var i = 0; i < _vals.length; i++) {
				this.localdata.push(_vals[i]);
			}
		},

		getCompID : function() {
			return this.getDataElementId();
		},
		compareTwoValues : function(_new, _old) {
			if (!_new && _old)
				return false;
			if (_new && !_old)
				return false;
			if (!_new && !_old)
				return true;
			return _new == _old;
		},
		addDataItem : function(_new) {
			if (!_new || !_new.id)
				return;
			if (!this.localdata)
				this.localdata = [];
			for (var i = 0; i < this.localdata.length; i++) {
				var ld = this.localdata[i];
				if (ld.id == _new.id)
					return;
			}
			this.localdata.push(_new);
			this.setValue(this.localdata);
		},
		setValue : function(_new) {
			if (!_new)
				_new = this.tmp_val;
			if (this.compareTwoValues(_new, this.tmp_val))
				return;
			this.tmp_val = _new;
			this.localdata = [];
			if (_new) {
				if (isc.isAn.Array(_new))
					for (var i = 0; i < _new.length; i++) {
						var _obj = _new[i];
						if (!_obj)
							continue;
						if (!_obj.id)
							continue;
						if (isc.isAn.Object(_obj))
							this.localdata.push(_obj);
						else
							this.localdata.push({
								id : _obj
							});
					}
				else {
					var split = (_new + '').split(',');
					for (var i = 0; i < split.length; i++) {
						this.localdata.push({
							id : split[i]
						});
					}
				}
			}
			var _ids = "";
			for (var i = 0; i < this.localdata.length; i++) {
				var ld = this.localdata[i];
				if (!ld.name) {
					if (_ids.length > 0)
						_ids += ",";
					_ids += ld.id;
				}
			}
			var _localdata = this.localdata;
			var _this = this;
			if (_ids.length > 0) {
				var cr = {
					ids : _ids
				};
				isc.DataSource
						.get("HierarchyDS")
						.fetchData(
								cr,
								function(dsResponse, data) {
									if (data) {
										for (var i = 0; i < data.length; i++) {
											var dat = data[i];
											for (var j = 0; j < _localdata.length; j++) {
												var ld = _localdata[j];
												if (ld.id == dat.id) {
													ld.name = dat.name;
													ld.icon = dat.icon;
													break;
												}
											}
										}
									}
									for (var j = 0; j < _localdata.length; j++) {
										var ld = _localdata[j];
										if (!ld.name) {
											ld.name = ld.id + '';
										}
									}
									_this.localdata = _localdata;
									_this.setDataValues();
								}, {
									operationId : "fetchWithIds"
								});
			} else {
				this.setDataValues();
			}
		},
		doubleClick : function(form, item) {
			alert(item)
		},
		getValue : function() {
			var ret = '';
			if (this.localdata)
				for (var i = 0; i < this.localdata.length; i++) {
					if (ret.length > 0)
						ret += ',';
					ret += this.localdata[i].id;
				}
			return ret;
		},
		getDataValue : function() {
			return this.localdata;
		},
		setDataValues : function() {
			// if(this.compl)
			// return;
			// this.compl=1;
			// var id = const_choices + this.getDataElementId();
			// var ul = document.getElementById(id);
			// var li = document.createElement("li");
			// var children = ul.children.length + 1 + id
			// li.setAttribute("id", "element" + children)
			// // li.appendChild(document.createTextNode("Element " +
			// children));
			// ul.appendChild(li)
			this.redraw();

		},
		deleteData : function(id) {
			var tmp = [];
			if (this.localdata)
				for (var i = 0; i < this.localdata.length; i++) {
					var ld = this.localdata[i];
					if (id != ld.id)
						tmp.push(ld);
				}
			this.localdata = tmp;
			this.redraw();
		},
		getElementHTML : function(value) {
			// return this.Super('getElementHTML', arguments);
			var id = this.getDataElementId();
			superhtml = '<div class="select2-container select2-container-multi" id="'
					+ const_container
					+ id
					+ '" style="width: 100%;"><ul class="select2-choices" id="'
					+ const_choices + id + '"> ';
			if (this.localdata)
				for (var i = 0; i < this.localdata.length; i++) {
					var ld = this.localdata[i];
					var colid = const_choice + id + "_" + i;
					var img = (ld.icon ? '<img src="images/' + ld.icon
							+ '" alt="Pipelines">' : "");
					img = '';
					superhtml += '\n<li class="select2-search-choice" id="'
							+ id
							+ '_c_11"    value="'
							+ ld.id
							+ '">\n'
							+ '<div style="cursor:pointer" onclick="alert(this);">'
							+ img
							+ ld.name
							+ '</div> \n'
							+ '<a href="#" class="select2-search-choice-close" tabindex="-1" onclick="'
							+ this.getID() + '.deleteData(' + ld.id
							+ ')"></a>\n' + '</li>\n';
				}
			superhtml += '</ul></div>';
			return superhtml;
		},
		redrawOnResize : false
	};

	isc.ClassFactory.defineClass("Select2Item", "TextAreaItem");

	isc.Select2Item.addProperties(_tiProps);

}
function removeSaved(_name) {
	var _8 = isc.Offline.get(_name);
	if (_8)
		isc.Offline.remove(_name);
}

function setupNewVisualBuilder(vbrecord) {
	removeSaved("VB_AUTOSAVE_PROJECT");
	removeSaved("VB_SINGLE_SCREEN");

	// isc.Page.setAppImgDir(jsvisualbuilder_path + "graphics/");

	var _changed_funct = function(form, item, value) {
		var _src_id = form.getValue("fromSrouce");
		var _to_id = form.getValue("toSrouce");
		var _diffStyle = form.getValue("diffStyle");
		var _which_field = form.getValue("which_field");
		var src_text = form.getField("fromSrouce").getSelectedRecord()[_which_field];
		var to_text = form.getField("toSrouce").getSelectedRecord()[_which_field];
		var _diff = getTextDiff(_diffStyle, src_text, to_text);
		form.htmlDiff.setContents(_diff);
	}

	isc.overwriteClass("TTextAreaItem", "LanguageEditorItem");
	var _doc_template_id = vbrecord && vbrecord.id ? vbrecord.id : -1;
	var _dfDifference = isc.DynamicForm
			.create({
				showEdges : true,
				autoDraw : false,
				height : 1,
				numCols : 4,
				width : "100%",
				fields : [
						{
							dependency : "{\"optionDataSource\":\"DocumentTemplateHistDS\",\"valueField\":\"id\",\"displayField\":\"time_creation\"}",
							name : "fromSrouce",
							title : "From",
							width : "*",
							_constructor : "DependencySelectItem",
							changed : _changed_funct,
							optionCriteria : {
								doc_template_id : _doc_template_id
							}
						},
						{
							dependency : "{\"optionDataSource\":\"DocumentTemplateHistDS\",\"valueField\":\"id\",\"displayField\":\"time_creation\"}",
							name : "toSrouce",
							title : "To",
							width : "*",
							_constructor : "DependencySelectItem",
							separateSpecialValues : false,
							changed : _changed_funct,
							optionCriteria : {
								doc_template_id : _doc_template_id
							}
						}, {
							name : "diffStyle",
							title : "Diff style",
							width : "*",
							valueMap : {
								"0" : "Side by Side",
								"1" : "Inline"
							},
							vertical : false,
							defaultValue : "0",
							_constructor : "RadioGroupItem",
							changed : _changed_funct
						}, {
							name : "which_field",
							title : "Which field",
							width : "*",
							valueMap : {
								template_name : "Template name",
								js_code : "JS Code",
								xml_code : "XML Code",
								custom_js_code : "Custom JavaScript",
								description : "description"
							},
							defaultValue : "js_code",
							_constructor : "SelectItem",
							changed : _changed_funct
						} ],
				titleOrientation : "left",
				wrapItemTitles : true
			});
	var _htmlDiff = isc.HTMLPane.create({
		showEdges : true,
		autoDraw : false,
		height : "100%",
		width : "100%"
	});
	_dfDifference.htmlDiff = _htmlDiff;

	var vb = isc.VisualBuilder.create({
		width : "100%",
		height : "100%",
		vbrecord : vbrecord,
		autoDraw : false,
		modulesDir : 'modules/',
		saveFileBuiltinIsEnabled : false,
		loadFileBuiltinIsEnabled : false,
		filesystemDataSourceEnabled : false,
		skin : "Enterprise",
		defaultApplicationMode : "edit",
		showModeSwitcher : true,
		showScreenMenu : false,
		singleScreenMode : true,
		canAddRootComponents : false,
		initialComponent : {
			type : "DataView",
			defaults : {
				autoDraw : true,
				modulesDir : 'modules/',
				overflow : "hidden",
				width : "100%",
				height : "100%",
				minMemberSize : 18
			}
		},
		doCode : function(_run) {
			var tab = this.codePane.getSelectedTab();
			var text = tab.pane.getField('codeField').getValue();
			var index = vb.codePane.getSelectedTabNumber();
			var comps = vb.projectComponents;

			// if (index < 2)
			// vb.projectComponents.destroyAll();
			var requestProperties = {};
			if (index == 0) {
				vb.projectComponents.destroyAll();
				comps.addPaletteNodesFromXML(text);
			}
			if (index == 1) {
				var _callBack = function() {
					comps.destroyAll();
					comps.addPaletteNodesFromJS(text);
				}
				isc.SaveWindow.createWindowWithItem(
						createComponentFromJS(text), _callBack);
			}
		},
		updateSource : function() {
			this.Super('updateSource', arguments);
			if (!this.languages_styles_updated) {
				try {
					this.codePreview.getField("codeField").setLanguageName(
							"xml");
				} catch (e) {
					// TODO: handle exception
				}
				this.languages_styles_updated = 1;
			}
			// alert(this.codePreview.getField("codeField").getLanguageName());
			var _val = this.jsCodePreview.getField("codeField").getValue();
			_val = !_val ? "" : _val;
			if (_val.trim().length == 0) {
				var _val1 = this.getUpdatedSource();
				if (_val1.trim().length > 0) {
					this.jsCodePreview.getField("codeField").setValue(_val1);
				}

			}

		},
		finishInitWidget : function() {
			this.Super('finishInitWidget', arguments);
			if (this.vbrecord) {
				var comps = this.projectComponents;
				comps.destroyAll();
				comps.addPaletteNodesFromJS(this.vbrecord.js_code);
			}
		},

		addChildren : function() {
			this.Super('addChildren', arguments);

			this.descriptionform = isc.DynamicForm.create({
				autoDraw : false,
				height : "100%",
				numCols : 2,
				width : "100%",
				fields : [ {
					colSpan : 2,
					height : "100%",
					name : "description",
					title : "Description",
					width : "*",
					_constructor : "TextAreaItem"
				} ],
				titleOrientation : "top"
			});

			this.additionaljsform = isc.DynamicForm.create({
				autoDraw : false,
				height : "100%",
				numCols : 1,
				width : "100%",
				fields : [ {
					height : "100%",
					name : "codeField",
					showTitle : false,
					width : "*",
					editorType : "TTextAreaItem"
				} ],
				titleOrientation : "top"
			});

			this.main.addTab({
				title : "Description",
				pane : this.descriptionform
			});
			this.main.addTab({
				title : "Difference",
				pane : isc.VLayout.create({
					autoDraw : false,
					members : [ _dfDifference, _htmlDiff ]
				})
			});
			this.codePane.addTab({
				title : "Custom JavaScript",
				pane : this.additionaljsform
			});

			this.codePane.addProperties({
				tabBarControls : [ isc.IButton.create({
					icon : "[SKIN]actions/accept.png",
					title : "Change VB",
					layoutAlign : "center",
					click : this.getID() + ".doCode(0);"
				}), isc.IButton.create({
					icon : "icon_run.png",
					title : "Run it",
					layoutAlign : "center",
					click : this.getID() + ".doCode(1);"
				}), "tabScroller", "tabPicker" ]
			});
			if (this.vbrecord) {
				this.descriptionform.setValue("description",
						this.vbrecord.description);

				this.additionaljsform.setValue("codeField",
						this.vbrecord.custom_js_code);
			}

		},
		getCustomComponentsURL : function() {
			return "customcomponents/customComponents.xml";
		},
		saveData : function(_call_back) {
			if (!this.vbrecord) {
				this.vbrecord = {};
			}
			this.vbrecord.template_name = this.dm_descr
					.getValue("template_name");
			this.vbrecord.description = this.descriptionform
					.getValue("description");
			this.vbrecord.custom_js_code = this.additionaljsform
					.getValue("codeField");
			this.vbrecord.xml_code = this.getUpdatedSource();
			this.vbrecord.time_creation = new Date();
			this.vbrecord.last_modification_time = new Date();
			var _this = this;
			isc.xml.toJSCode(this.vbrecord.xml_code, function(_1) {
				_this.vbrecord.js_code = _1.data;
				if (_this.vbrecord.id)
					isc.DataSource.get("DocumentTemplateDS").updateData(
							_this.vbrecord, _call_back);
				else
					isc.DataSource.get("DocumentTemplateDS").addData(
							_this.vbrecord, _call_back);
			})
		}

	});

	var dm_descr = isc.DynamicForm
			.create({
				autoDraw : false,
				showEdges : true,
				width : "100%",
				numCols : 4,
				fields : [
						{
							name : "template_name",
							title : "TEMPLATE NAME",
							width : "*",
							wrapTitle : false,
							requered : true,
							_constructor : "TextItem"
						},
						{
							dependency : "{\"optionDataSource\":\"DocumentTemplateDS\",\"valueField\":\"id\",\"displayField\":\"template_name\"}",
							name : "copy_from",
							title : "Copy from",
							width : "*",
							wrapTitle : false,
							_constructor : "DependencySelectItem",
							pickListFields : [ {
								name : "template_name",
								title : "Name"
							}, {
								name : "time_creation",
								title : "Criation time"
							} ],
							pickListProperties : {
								showFilterEditor : true
							},
							icons : [ {
								src : "[SKIN]actions/accept.png"
							} ]
						} ]
			});

	if (vbrecord)
		dm_descr.setValue("template_name", vbrecord.template_name);
	vb.dm_descr = dm_descr;
	var vl_main = isc.VLayout.create({
		autoDraw : false,
		dm_descr : dm_descr,
		vb : vb,
		members : [ dm_descr, vb ],
		saveData : function(_call_back) {
			if (!this.dm_descr.validate())
				return;
			this.vb.saveData(_call_back);
		}
	})
	return vl_main;

}

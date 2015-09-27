/*

 SmartClient Ajax RIA system
 Version v10.0p_2014-09-18/EVAL Development Only (2014-09-18)

 Copyright 2000 and beyond Isomorphic Software, Inc. All rights reserved.
 "SmartClient" is a trademark of Isomorphic Software, Inc.

 LICENSE NOTICE
 INSTALLATION OR USE OF THIS SOFTWARE INDICATES YOUR ACCEPTANCE OF
 ISOMORPHIC SOFTWARE LICENSE TERMS. If you have received this file
 without an accompanying Isomorphic Software license file, please
 contact licensing@isomorphic.com for details. Unauthorized copying and
 use of this software is a violation of international copyright law.

 DEVELOPMENT ONLY - DO NOT DEPLOY
 This software is provided for evaluation, training, and development
 purposes only. It may include supplementary components that are not
 licensed for deployment. The separate DEPLOY package for this release
 contains SmartClient components that are licensed for deployment.

 PROPRIETARY & PROTECTED MATERIAL
 This software contains proprietary materials that are protected by
 contract and intellectual property law. You are expressly prohibited
 from attempting to reverse engineer this software or modify this
 software for human readability.

 CONTACT ISOMORPHIC
 For more information regarding license rights and restrictions, or to
 report possible license violations, please contact Isomorphic Software
 by email (licensing@isomorphic.com) or web (www.isomorphic.com).

 */

if (window.isc && window.isc.module_Core && !window.isc.module_VisualBuilder) {
	isc.module_VisualBuilder = 1;
	isc._moduleStart = isc._VisualBuilder_start = (isc.timestamp ? isc
			.timestamp() : new Date().getTime());
	if (isc._moduleEnd
			&& (!isc.Log || (isc.Log && isc.Log.logIsDebugEnabled('loadTime')))) {
		isc._pTM = {
			message : 'VisualBuilder load/parse time: '
					+ (isc._moduleStart - isc._moduleEnd) + 'ms',
			category : 'loadTime'
		};
		if (isc.Log && isc.Log.logDebug)
			isc.Log.logDebug(isc._pTM.message, 'loadTime');
		else if (isc._preLog)
			isc._preLog[isc._preLog.length] = isc._pTM;
		else
			isc._preLog = [ isc._pTM ]
	}
	isc.definingFramework = true;
	isc.ClassFactory.defineClass("MockupContainer", "Canvas");
	isc.A = isc.MockupContainer.getPrototype();
	isc.A.autoMaskChildren = true;
	isc.A.editProxyProperties = {
		childrenSnapToGrid : true,
		persistCoordinates : true
	};
	isc.ClassFactory.defineClass("Project");
	isc.A = isc.Project;
	isc.A.AUTOSAVE = "VB_AUTOSAVE_PROJECT";
	isc.A.AUTOSAVE_SINGLE_SCREEN = "VB_SINGLE_SCREEN";
	isc.A = isc.Project.getPrototype();
	isc.B = isc._allFuncs;
	isc.C = isc.B._maxIndex;
	isc.D = isc._funcClasses;
	isc.D[isc.C] = isc.A.Class;
	isc.A.screensDefaults = {
		_constructor : "Tree",
		openProperty : "_isOpen_",
		parentProperty : "_parent_"
	};
	isc.A.autoSavePause = 500;
	isc.B.push(isc.A.setID = function isc_Project_setID(_1) {
		this.ID = _1
	}, isc.A.setName = function isc_Project_setName(_1) {
		this.name = _1
	}, isc.A.addDatasource = function isc_Project_addDatasource(_1, _2) {
		var _3 = this.datasources.findIndex("dsName", _1);
		if (_3 == -1) {
			this.datasources.addAt({
				dsName : _1,
				dsType : _2
			}, 0);
			this.autoSaveSoon()
		}
	}, isc.A.removeDatasource = function isc_Project_removeDatasource(_1) {
		var _2 = this.datasources.findIndex("dsName", _1);
		if (_2 >= 0) {
			this.datasources.removeAt(_2);
			this.autoSaveSoon()
		}
	}, isc.A.setCurrentScreenID = function isc_Project_setCurrentScreenID(_1) {
		if (this.currentScreenID !== _1) {
			this.currentScreenID = _1;
			this.autoSaveSoon()
		}
	}, isc.A.init = function isc_Project_init() {
		this.Super("init", arguments);
		if (!this.datasources)
			this.datasources = [];
		this.addAutoChild("screens");
		this.observe(this.screens, "dataChanged", "observer.autoSaveSoon();")
	}, isc.A.destroy = function isc_Project_destroy() {
		this.ignore(this.screens, "dataChanged");
		this.Super("destroy", arguments)
	}, isc.A.isEmpty = function isc_Project_isEmpty() {
		return this.screens.getLength() == 0
	}, isc.A.addScreen = function isc_Project_addScreen(_1, _2, _3, _4, _5) {
		if (!_1)
			_1 = this.screens.getRoot();
		if (!_2)
			_2 = "";
		var _6 = this.findScreen(_2);
		if (_6) {
			if (_2) {
				if (!_5) {
					return _6
				}
			} else {
				if (!this.builder || !this.builder.singleScreenMode) {
					_6 = null
				}
			}
		}
		var _7 = this.screens.add({
			screenID : _2,
			title : _3,
			contents : _4,
			isFolder : false
		}, _1);
		if (_6)
			this.removeScreen(_6);
		this.autoSaveSoon();
		return _7
	}, isc.A.removeScreen = function isc_Project_removeScreen(_1) {
		if (_1)
			this.screens.remove(_1)
	}, isc.A.addGroup = function isc_Project_addGroup(_1, _2) {
		if (!_1)
			_1 = this.screens.getRoot();
		return this.screens.add({
			title : _2,
			isFolder : true
		}, _1)
	}, isc.A.removeGroup = function isc_Project_removeGroup(_1) {
		if (_1)
			this.screens.remove(_1)
	}, isc.A.findScreen = function isc_Project_findScreen(_1) {
		if (!_1)
			_1 = "";
		return this.screens.find({
			screenID : _1,
			isFolder : false
		})
	}, isc.A.firstScreen = function isc_Project_firstScreen() {
		return this.screens.find({
			isFolder : false
		})
	}, isc.A.untitledScreen = function isc_Project_untitledScreen() {
		return this.findScreen(null)
	}, isc.A.setScreenProperties = function isc_Project_setScreenProperties(_1,
			_2) {
		isc.addProperties(_1, _2);
		this.autoSaveSoon();
		return _1
	}, isc.A.setScreenDirty = function isc_Project_setScreenDirty(_1, _2) {
		var _3 = _2 ? new Date() : null;
		if (_3)
			this.screenDirty = _3;
		this.setScreenProperties(_1, {
			dirty : _3
		})
	}, isc.A.saveScreenContents = function isc_Project_saveScreenContents(_1) {
		if (_1) {
			var _2 = _1.dirty;
			var _3 = this;
			if (_1.screenID) {
				this.screenFileSource.saveFile(_1.screenID, _1.title,
						_1.contents, function() {
							if (_2 == _1.dirty)
								_3.setScreenDirty(_1, false)
						})
			} else {
				this.screenFileSource.showSaveFileUI(_1.contents, function(_5,
						_6, _7) {
					if (_2 == _1.dirty)
						_3.setScreenDirty(_1, false);
					if (!isc.isAn.Array(_6))
						_6 = [ _6 ];
					if (_6[0].id) {
						var _4 = _3.findScreen(_6[0].id);
						if (_4)
							_3.removeScreen(_4)
					}
					_3.setScreenProperties(_1, {
						screenID : _6[0].id,
						title : _6[0].name
					})
				})
			}
		}
	}, isc.A.saveScreenAs = function isc_Project_saveScreenAs(_1, _2) {
		var _3 = this;
		this.screenFileSource.showSaveFileUI(_1.contents, function(_5, _6, _7) {
			if (!isc.isAn.Array(_6))
				_6 = [ _6 ];
			var _4 = _3.addScreen(_3.screens.getParent(_1), _6[0].id,
					_6[0].name, _1.contents, true);
			_3.fireCallback(_2, "screen", [ _4 ])
		})
	}, isc.A.fetchScreenContents = function isc_Project_fetchScreenContents(_1,
			_2) {
		if (_1) {
			if (_1.contents || !_1.screenID) {
				this.fireCallback(_2, "contents", [ _1.contents ])
			} else {
				var _3 = this;
				this.screenFileSource.loadFile(_1.screenID,
						function(_4, _5, _6) {
							if (_4.status < 0) {
								_3.fireCallback(_2, "contents", [ null ])
							} else {
								_1.contents = _5[0].contents;
								_3.setScreenDirty(_1, false);
								_3.fireCallback(_2, "contents",
										[ _5[0].contents ])
							}
						}, {
							willHandleError : true
						})
			}
		} else {
			this.fireCallback(_2, "contents", [ null ])
		}
	}, isc.A.xmlSerialize = function isc_Project_xmlSerialize() {
		var _1 = {
			screens : this.createAutoChild("screens"),
			currentScreenID : this.currentScreenID,
			datasources : this.datasources
		};
		_1.screens.setRoot(this.screens.getCleanNodeData(
				this.screens.getRoot(), true, true, true));
		_1.screens.getAllNodes().map(function(_3) {
			if (!_3.dirty) {
				delete _3.contents
			}
			delete _3.dirty;
			delete _3.name;
			delete _3.id;
			delete _3.parentId;
			if (_3.children && _3.children.length == 0)
				delete _3.children
		});
		var _2 = isc.DS.get("Project").xmlSerialize(_1);
		_1.screens.destroy();
		return _2
	}, isc.A.autoSaveSoon = function isc_Project_autoSaveSoon() {
		this.fireOnPause("autoSave", "autoSave", this.autoSavePause)
	}, isc.A.autoSave = function isc_Project_autoSave(_1) {
		if (this.ID) {
			this.save(_1, {
				showPrompt : false
			})
		} else {
			var _2 = isc.Project.AUTOSAVE;
			if (this.builder) {
				this.builder.cacheCurrentScreenContents();
				if (this.builder.singleScreenMode)
					_2 = isc.Project.AUTOSAVE_SINGLE_SCREEN
			}
			isc.Offline.put(_2, this.xmlSerialize());
			this.screenDirty = null;
			this.fireCallback(_1)
		}
	}, isc.A.save = function isc_Project_save(_1, _2) {
		if (this.ID) {
			if (this.builder)
				this.builder.cacheCurrentScreenContents();
			var _3 = this;
			var _4 = this.screenDirty;
			this.fileSource.saveFile(this.ID, this.name, this.xmlSerialize(),
					function() {
						if (_4 == _3.screenDirty)
							_3.screenDirty = null;
						this.fireCallback(_1)
					}, _2)
		} else {
			this.saveAs(_1)
		}
	}, isc.A.saveAs = function isc_Project_saveAs(_1) {
		if (this.builder)
			this.builder.cacheCurrentScreenContents();
		var _2 = this;
		var _3 = this.screenDirty;
		this.fileSource.showSaveFileUI(this.xmlSerialize(),
				function(_4, _5, _6) {
					isc.Offline.remove(isc.Project.AUTOSAVE);
					if (_3 == _2.screenDirty)
						_2.screenDirty = null;
					if (!isc.isAn.Array(_5))
						_5 = [ _5 ];
					_2.setID(_5[0].id);
					_2.setName(_5[0].name);
					this.fireCallback(_1)
				})
	});
	isc.B._maxIndex = isc.C + 25;
	isc.ClassFactory.defineClass("VisualBuilder", "VLayout");
	isc.A = isc.VisualBuilder;
	isc.A.titleEditEvent = "doubleClick";
	isc.A = isc.VisualBuilder.getPrototype();
	isc.B = isc._allFuncs;
	isc.C = isc.B._maxIndex;
	isc.D = isc._funcClasses;
	isc.D[isc.C] = isc.A.Class;
	isc.A.saveFileBuiltinIsEnabled = false;
	isc.A.loadFileBuiltinIsEnabled = false;
	isc.A.filesystemDataSourceEnabled = false;
	isc.A.canAddRootComponents = false;
	isc.A.offlineStorageKey = "VisualBuilder-savedSettings";
	isc.A.settingsFile = "[VBWORKSPACE]/vb.settings.xml";
	isc.A.settingsFileSourceDefaults = {
		_constructor : "FileSource",
		webrootOnly : false
	};
	isc.A.projectFileSourceDefaults = {
		_constructor : "FileSource",
		defaultPath : "[VBWORKSPACE]",
		webrootOnly : false,
		saveWindowProperties : {
			title : "Save Project",
			actionButtonTitle : "Save Project",
			webrootOnly : false,
			fileFilters : [ {
				filterName : "Project XML Files",
				filterExpressions : [ /\.proj\.xml$/i ]
			} ],
			directoryListingProperties : {
				canEdit : false
			},
			getFileName : function(_1) {
				var _2 = _1.toLowerCase();
				if (_2.endsWith(".proj.xml")) {
					return _1
				} else if (_2.endsWith(".xml")) {
					return _1.slice(0, -4) + ".proj.xml"
				} else {
					return _1 + ".proj.xml"
				}
			}
		},
		loadWindowProperties : {
			title : "Load Project",
			actionButtonTitle : "Load Project",
			fileFilters : [ {
				filterName : "Project XML Files",
				filterExpressions : [ /\.proj\.xml$/i ]
			} ],
			directoryListingProperties : {
				canEdit : false
			}
		}
	};
	isc.A.recentProjectsCount = 5;
	isc.A.screenFileSourceDefaults = {
		_constructor : "FileSource",
		defaultPath : "[VBWORKSPACE]",
		webrootOnly : false,
		saveWindowProperties : {
			title : "Save Screen",
			actionButtonTitle : "Save Screen",
			webrootOnly : false,
			fileFilters : [ {
				filterName : "XML Files",
				filterExpressions : [ /^(?!.*\.proj\.xml$).*\.xml$/i ]
			} ],
			directoryListingProperties : {
				canEdit : false
			},
			getFileName : function(_1) {
				if (_1.toLowerCase().endsWith(".xml")) {
					return _1
				} else {
					return _1 + ".xml"
				}
			}
		},
		loadWindowProperties : {
			title : "Load Screen",
			actionButtonTitle : "Load Screen",
			fileFilters : [ {
				filterName : "XML Files",
				filterExpressions : [ /^(?!.*\.proj\.xml$).*\.xml$/i ]
			} ],
			directoryListingProperties : {
				canEdit : false
			}
		}
	};
	isc.A.jspFileSourceDefaults = {
		_constructor : "FileSource",
		defaultPath : "[VBWORKSPACE]",
		webrootOnly : false,
		saveWindowProperties : {
			title : "Export JSP",
			actionButtonTitle : "Export JSP",
			webrootOnly : false,
			fileFilters : [ {
				filterName : "JSP Files",
				filterExpressions : [ /.*\.jsp$/i ]
			} ],
			directoryListingProperties : {
				canEdit : false
			},
			getFileName : function(_1) {
				if (_1.toLowerCase().endsWith(".jsp")) {
					return _1
				} else {
					return _1 + ".jsp"
				}
			}
		}
	};
	isc.A.singleScreenMode = true;
	isc.A.singleScreenModeProjectID = "[VBWORKSPACE]/vb.singleScreen.proj.xml";
	isc.A.vertical = true;
	isc.A.sControlIsomorphicDir = "http://www.isomorphic.com/isomorphic/";
	isc.A.sControlSkin = "SmartClient";
	isc.A.workspacePath = "[VBWORKSPACE]";
	isc.A.workspaceURL = "workspace/";
	isc.A.basePathRelWorkspace = "..";
	isc.A.webRootRelWorkspace = "../../..";
	isc.A.useFieldMapper = false;
	isc.A.helpPaneProperties = {
		headerTitle : "About Visual Builder",
		contentsURL : "visualBuilderHelp.html"
	};
	isc.A.canEditExpressions = true;
	isc.A.typeCount = {};
	isc.A.disableDirtyTracking = 0;
	isc.A.workspaceDefaults = {
		_constructor : "TLayout",
		vertical : false,
		autoDraw : false,
		backgroundColor : isc.nativeSkin ? null : "black"
	};
	isc.A.leftStackDefaults = {
		_constructor : "TSectionStack",
		autoDraw : false,
		width : 320,
		showResizeBar : true,
		visibilityMode : "multiple"
	};
	isc.A.middleStackDefaults = {
		_constructor : "TSectionStack",
		autoDraw : false,
		showResizeBar : true,
		resizeBarTarget : "next",
		visibilityMode : "multiple",
		styleName : "pageBackground"
	};
	isc.A.modeSwitcherDefaults = {
		_constructor : "TDynamicForm",
		autoDraw : false,
		autoParent : "middleStack",
		numCols : 7,
		initWidget : function() {
			this.Super("initWidget", arguments);
			this.setValue("skin", this.creator.skin);
			if (!this.creator.hostedMode)
				this.showItem("useToolSkin")
		},
		setNativeSkin : function(_1) {
			this.creator.doAutoSave(this.getID() + ".doSetNativeSkin('" + _1
					+ "')")
		},
		doSetNativeSkin : function(_1) {
			var _2 = isc.clone(isc.params);
			_2.useNativeSkin = _1 == "useNativeSkin" ? 1 : 0;
			var _3 = location.href;
			if (_3.contains("?"))
				_3 = _3.substring(0, _3.indexOf("?"));
			_3 += "?";
			for ( var _4 in _2) {
				_3 += encodeURIComponent(_4) + "=" + encodeURIComponent(_2[_4])
						+ "&"
			}
			_3 = _3.substring(0, _3.length - 1);
			isc.Cookie.set(this.creator.loadAutoSaveCookie, "true");
			location.replace(_3)
		},
		setSkin : function(_1) {
			this.creator.doAutoSave(this.getID() + ".doSetSkin('" + _1 + "')")
		},
		doSetSkin : function(_1) {
			var _2 = isc.clone(isc.params);
			_2.skin = _1;
			var _3 = location.href;
			if (_3.contains("?"))
				_3 = _3.substring(0, _3.indexOf("?"));
			_3 += "?";
			for ( var _4 in _2) {
				_3 += encodeURIComponent(_4) + "=" + encodeURIComponent(_2[_4])
						+ "&"
			}
			_3 = _3.substring(0, _3.length - 1);
			isc.Cookie.set(this.creator.loadAutoSaveCookie, "true");
			location.replace(_3)
		},
		setResolution : function(_1) {
			var s = _1.split("x");
			var _3 = parseInt(s[0].trim());
			var _4 = parseInt(s[1].trim());
			this.creator.middleStack.setWidth(_3);
			this.creator.rootLiveObject.setHeight(_4)
		},
		fields : [
				{
					name : "skin",
					editorType : "TSelectItem",
					width : 100,
					titleAlign : "top",
					wrapTitle : false,
					valueMap : {
						Enterprise : "Enterprise",
						EnterpriseBlue : "Enterprise Blue",
						Graphite : "Graphite",
						Simplicity : "Simplicity",
						fleet : "Fleet",
						TreeFrog : "TreeFrog",
						SilverWave : "SilverWave",
						BlackOps : "Black Ops"
					},
					title : "Skin",
					change : "form.setSkin(value)"
				},
				{
					name : "useToolSkin",
					editorType : "TSelectItem",
					width : 160,
					showTitle : false,
					visible : false,
					titleAlign : "top",
					valueMap : {
						useToolSkin : "Use high contrast tool skin",
						useNativeSkin : "Use app skin for tools "
					},
					defaultValue : isc.nativeSkin ? "useNativeSkin"
							: "useToolSkin",
					changed : "form.setNativeSkin(value)"
				},
				{
					name : "resolution",
					editorType : "TSelectItem",
					width : 100,
					valueMap : [ "1024x768", "1280x1024" ],
					title : "Resolution",
					change : "form.setResolution(value)"
				},
				{
					name : "switcher",
					showTitle : false,
					title : "Component mode",
					valueMap : [ "Live", "Edit" ],
					vertical : false,
					editorType : "TRadioGroupItem",
					wrapTitle : false,
					changed : function(_1, _2, _3) {
						var _4 = (_3 == "Edit"), _5 = _1.creator, _6 = _5.currentScreen, _7 = _5
								.getScreenMockupMode(_6);
						if (_4 && _7) {
							_5.withoutDirtyTracking(function() {
								_5.projectComponents.destroyAll()
							});
							_5.setScreenContents(_6.contents, _7)
						} else {
							var _8 = _5.projectComponents.data;
							for (var i = 0; i < _8.getLength(); i++) {
								var _10 = _8.get(i);
								var _11 = _10.liveObject;
								if (_11.setEditMode) {
									_11.setEditMode(_4, _5.projectComponents,
											_10)
								} else {
									_11.editingOn = _4
								}
							}
						}
						_5.editingOn = _4;
						_5.projectComponents.switchEditMode(_4)
					}
				} ]
	};
	isc.A.rightStackDefaults = {
		_constructor : "TSectionStack",
		autoDraw : false,
		width : 200,
		visibilityMode : "multiple"
	};
	isc.A.canvasItemWrapperConstructor = "CanvasItem";
	isc.A.canvasItemWrapperDefaults = {
		showTitle : false,
		colSpan : "*",
		width : "*"
	};
	isc.A.simpleTypeNodeConstructor = "FormItem";
	isc.A.simpleTypeNodeDefaults = {
		isGroup : true,
		cellPadding : 5,
		showComplexFields : false,
		doNotUseDefaultBinding : true
	};
	isc.A.complexTypeNodeConstructor = "DynamicForm";
	isc.A.complexTypeNodeDefaults = {
		isGroup : true,
		cellPadding : 5,
		showComplexFields : false,
		doNotUseDefaultBinding : true
	};
	isc.A.repeatingComplexTypeNodeDefaults = {
		autoFitData : "vertical",
		leaveScrollbarGap : false
	};
	isc.A.paletteNodeDSDefaults = {
		_constructor : "DataSource",
		ID : "paletteNode",
		recordXPath : "/PaletteNodes/PaletteNode",
		fields : {
			name : {
				name : "name",
				type : "text",
				length : 8,
				required : true
			},
			title : {
				name : "title",
				type : "text",
				title : "Title",
				length : 128,
				required : true
			},
			type : {
				name : "type",
				type : "text",
				title : "Type",
				length : 128,
				required : true
			},
			icon : {
				name : "icon",
				type : "image",
				title : "Icon Filename",
				length : 128
			},
			iconWidth : {
				name : "iconWidth",
				type : "number",
				title : "Icon Width"
			},
			iconHeight : {
				name : "iconHeight",
				type : "number",
				title : "Icon Height"
			},
			iconSize : {
				name : "iconSize",
				type : "number",
				title : "Icon Size"
			},
			showDropIcon : {
				name : "showDropIcon",
				type : "boolean",
				title : "Show Drop Icon"
			},
			defaults : {
				name : "defaults",
				type : "Canvas",
				propertiesOnly : true
			},
			children : {
				name : "children",
				type : "paletteNode",
				multiple : true
			}
		}
	};
	isc.A.paletteDSDefaults = {
		_constructor : "DataSource",
		ID : "paletteDS",
		clientOnly : true,
		fields : [ {
			name : "id",
			type : "integer",
			primaryKey : true
		}, {
			name : "parentId",
			type : "integer"
		}, {
			name : "title",
			title : "Component",
			type : "text"
		}, {
			name : "description",
			type : "text"
		}, {
			name : "isFolder",
			type : "boolean"
		}, {
			name : "type",
			type : "text"
		}, {
			name : "children"
		} ],
		performDSOperation : function(_1, _2, _3, _4) {
			if (this.$149o == this.mockupMode && this.getCacheData() != null) {
				return this.Super("performDSOperation", arguments)
			}
			this.$149o = this.mockupMode;
			this.setCacheData(null);
			this.$149p = null;
			this.$149q = 2;
			this.paletteNodeDS.dataURL = this.customComponentsURL;
			this.paletteNodeDS.fetchData({}, this.getID()
					+ ".fetchComponentsReply(dsResponse.clientContext,data)", {
				clientContext : {
					operationType : _1,
					data : _2,
					callback : _3,
					requestProperties : _4
				}
			});
			this.paletteNodeDS.dataURL = (this.mockupMode ? "defaultMockupComponents.xml"
					: "defaultComponents.xml");
			this.paletteNodeDS.fetchData({}, this.getID()
					+ ".fetchComponentsReply(dsResponse.clientContext,data)", {
				clientContext : {
					operationType : _1,
					data : _2,
					callback : _3,
					requestProperties : _4
				}
			})
		},
		fetchComponentsReply : function(_1, _2) {
			if (!this.$149p)
				this.$149p = _2;
			else
				this.$149p.addList(_2);
			if (--this.$149q == 0) {
				_2 = this.flattenTree(this.$149p);
				this.setCacheData(_2);
				this.$149p = null;
				this.Super("performDSOperation", [ _1.operationType, _1.data,
						_1.callback, _1.requestProperties ])
			}
		},
		assignIds : function(_1, _2) {
			if (_2 == null)
				this.$149r = 0;
			for (var i = 0; i < _1.length; i++) {
				var _4 = _1[i];
				_4.id = this.$149r++;
				if (_2 != null)
					_4.parentId = _2;
				if (_4.children)
					this.assignIds(_4.children, _4.id);
				else
					_4.isFolder = false
			}
		},
		flattenTree : function(_1, _2, _3) {
			if (_2 == null)
				this.$149r = 0;
			if (!_3)
				_3 = [];
			for (var i = 0; i < _1.length; i++) {
				var _5 = _1[i];
				_5.id = this.$149r++;
				if (_2 != null)
					_5.parentId = _2;
				if (_5.children) {
					this.flattenTree(_5.children, _5.id, _3);
					delete _5.children
				} else {
					_5.isFolder = false
				}
				_3.add(_5)
			}
			return _3
		}
	};
	isc.A.libraryComponentsDefaults = {
		_constructor : "TTreePalette",
		autoShowParents : true,
		autoDraw : false,
		dataSource : "paletteDS",
		loadDataOnDemand : false,
		cellHeight : 22,
		showRoot : false,
		showHeader : false,
		selectionType : Selection.SINGLE,
		treeFieldTitle : "Form Items",
		canDragRecordsOut : true,
		canAcceptDroppedRecords : false,
		dragDataAction : isc.TreeViewer.COPY,
		iconSize : 16,
		folderOpenImage : "cubes_blue.gif",
		folderClosedImage : "cubes_blue.gif",
		folderDropImage : "cubes_green.gif",
		fileImage : "cube_blue.gif"
	};
	isc.A.screenListToolbarDefaults = {
		_constructor : "TLayout",
		vertical : false,
		autoDraw : true,
		membersMargin : 10,
		height : 20,
		autoParent : "screenPane"
	};
	isc.A.screenAddButtonDefaults = {
		_constructor : "TMenuButton",
		autoDraw : false,
		title : "Add...",
		showMenuBelow : false,
		width : 80,
		autoParent : "screenListToolbar"
	};
	isc.A.dataSourceListDefaults = {
		_constructor : "TListPalette",
		showHeaderMenuButton : false,
		autoDraw : false,
		height : "40%",
		selectionType : "single",
		canDragRecordsOut : true,
		emptyMessage : "<span style='color: red'>Use the 'Add...' button below to add DataSources.</span>",
		editSelectedDataSource : function() {
			var _1 = this.getSelectedRecord();
			if (_1)
				isc.DS.get(_1.ID, this.creator.getID() + ".showDSEditor(ds)")
		},
		doubleClick : function() {
			this.editSelectedDataSource()
		},
		selectionChanged : function() {
			this.creator.dsEditButton
					.setDisabled(this.getSelectedRecord() == null)
		},
		fields : [ {
			name : "ID",
			width : "*"
		}, {
			name : "dsType",
			title : "Type",
			width : 65
		}, {
			name : "download",
			showTitle : false,
			width : 22
		} ],
		formatCellValue : function(_1, _2, _3, _4) {
			var _5 = this.getField(_4);
			if (_5.name == "download") {
				return this.imgHTML("[SKINIMG]/actions/download.png", null,
						null, null, null, this.widgetImgDir)
			} else
				return _1
		},
		cellClick : function(_1, _2, _3) {
			var _4 = this.getField(_3);
			if (_4.name == "download") {
				isc.DS.get(_1.ID, this.creator.getID()
						+ ".downloadDataSource(ds)")
			} else
				return this.Super("cellClick", arguments)
		},
		dsContextMenuDefaults : {
			_constructor : "Menu",
			autoDraw : false,
			showIcon : false,
			showMenuFor : function(_1, _2) {
				this.$109l = _1;
				this.$109m = _2;
				this.showContextMenu()
			},
			data : [
					{
						title : "Edit...",
						click : function(_1, _2, _3) {
							isc.DS.get(_3.$109l.ID, _3.creator.creator.getID()
									+ ".showDSEditor(ds)")
						}
					},
					{
						title : "Remove from project",
						click : function(_1, _2, _3) {
							_3.creator.creator.project
									.removeDatasource(_3.$109l.ID)
						}
					} ]
		},
		rowContextClick : function(_1, _2, _3) {
			this.dsContextMenu.showMenuFor(_1, _2);
			return false
		},
		initWidget : function() {
			this.Super("initWidget", arguments);
			this.dsContextMenu = this.createAutoChild("dsContextMenu")
		},
		autoParent : "dataSourcePane"
	};
	isc.A.dataSourceListToolbarDefaults = {
		_constructor : "TLayout",
		vertical : false,
		autoDraw : true,
		membersMargin : 10,
		margin : 2,
		height : 20,
		autoParent : "dataSourcePane"
	};
	isc.A.dsNewButtonDefaults = {
		_constructor : "TMenuButton",
		autoDraw : false,
		title : "Add...",
		showMenuBelow : false,
		width : 70,
		autoParent : "dataSourceListToolbar"
	};
	isc.A.dsNewButtonMenuDefaults = {
		_constructor : "Menu",
		data : [ {
			title : "New DataSource...",
			click : function(_1, _2, _3) {
				_3.creator.showDSWizard()
			}
		}, {
			title : "Existing DataSource...",
			click : function(_1, _2, _3) {
				_3.creator.showDSRepoLoadUI()
			}
		} ]
	};
	isc.A.dsEditButtonDefaults = {
		_constructor : "TButton",
		autoDraw : false,
		disabled : true,
		title : "Edit...",
		width : 70,
		click : "this.creator.dataSourceList.editSelectedDataSource()",
		autoParent : "dataSourceListToolbar"
	};
	isc.A.projectComponentsMenuDefaults = {
		_constructor : "Menu",
		autoDraw : false,
		showIcon : false,
		enableIf : function(_1, _2) {
			var _3 = _1 ? _1.getSelection() : null, _4 = _3 ? _3[0] : null, _5 = true;
			if (!_1.creator.canAddRootComponents) {
				var _6 = _1.data, _7 = (_3 == null ? 0 : _3.length);
				for (var i = 0; i < _7; ++i) {
					if (_6.isRoot(_6.getParent(_3[i])))
						_5 = false
				}
			}
			return {
				selection : _3,
				node : _4,
				removeOK : _5
			}
		},
		data : [
				{
					title : "Remove",
					enableIf : "node != null && removeOK",
					click : function(_1) {
						var _2 = _1.getSelection(), _3 = (_2 == null ? 0
								: _2.length);
						for (var i = 0; i < _3; ++i) {
							var _5 = _2[i];
							_1.destroyNode(_5)
						}
						_1.data.removeList(_2)
					}
				},
				{
					title : "Edit",
					enableIf : "node != null",
					click : function(_1, _2, _3) {
						var _4 = _1.getSelection(), _5 = (_4 ? _4[0]
								: undefined);
						_3.creator.editComponent(_5,
								_3.creator.projectComponents.getLiveObject(_5))
					}
				} ]
	};
	isc.A.projectComponentsDefaults = {
		_constructor : "TEditTree",
		showHeaderMenuButton : false,
		editContextDefaults : {
			persistCoordinates : null,
			selectedAppearance : "outlineEdges",
			canGroupSelect : false,
			enableInlineEdit : true,
			isVisualBuilder : true,
			addNode : function(_1, _2, _3, _4, _5, _6) {
				var _7 = this.creator;
				var _8 = isc.ClassFactory.getClass(_1.type);
				if (_8 && _8.isA(isc.DataSource)) {
					if (_1.defaults == null) {
						var _9 = this;
						_1.loadData(_1, function() {
							_9.addNode(_1, _2, _3, _4, _5)
						});
						return

						

												

						

																		

						

												

						

					} else if (_6 == null) {
						var _9 = this;
						var _10 = _7.showFieldMapper(_2.liveObject, _1, _2,
								function() {
									_9.addNode(_1, _2, _3, _4, _5, true)
								});
						if (_10) {
							return

							

														

							

																					

							

														

							

						}
					}
				}
				_1 = this.Super("addNode", arguments);
				if (!_1)
					return;
				if (!_1.dropped || (_1.loadData != null && !_1.isLoaded)) {
					_7.observeNodeDragResized(_1, _2);
					_7.creator.componentAdded();
					return _1
				}
				var _8 = isc.ClassFactory.getClass(_1.type);
				if (_8 != null && _8.isA(isc.DataSource)) {
					var _11 = _1.liveObject, _12 = _11.serverType || _11.dsType
							|| _11.dataSourceType, _13 = _2, _14 = _2.liveObject;
					if ((isc.isA.ListGrid(_14) || isc.isA.TileGrid(_14))
							&& (_12 == "sql" || _12 == "hibernate"
									|| _11.dataURL != null || _11.clientOnly || _11.serviceNamespace != null)
							&& !_11.noAutoFetch && _14.autoFetchData != false) {
						_13.defaults.autoFetchData = true;
						if (isc.SForce && isc.isA.SFDataSource(_11)
								&& !isc.SForce.sessionId) {
							isc.SForce.ensureLoggedIn(function() {
								_14.fetchData()
							}, true)
						} else {
							_14.fetchData()
						}
					}
				}
				var _15 = this.getLiveObject(_1);
				if (!_15.getEditableProperties) {
					_7.creator.componentAdded();
					return _1
				}
				_7.observeNodeDragResized(_1, _2);
				if (_15.setEditableProperties) {
					_15.setEditableProperties({});
					if (_15.markForRedraw)
						_15.markForRedraw();
					else if (_15.redraw)
						_15.redraw()
				}
				_7.delayCall("hiliteSelected", [ true ]);
				_7.creator.componentAdded();
				return _1
			}
		},
		shouldShowDragLineForRecord : function() {
			if (this.Super("shouldShowDragLineForRecord", arguments)) {
				return !!this.willAcceptDrop()
			}
			return false
		},
		autoDraw : false,
		canSort : false,
		leaveScrollbarGap : false,
		selectionUpdated : function(_1) {
			if (_1)
				this.creator.editComponent(_1, this.editContext
						.getLiveObject(_1));
			else
				this.creator.clearComponent()
		},
		hiliteSelected : function() {
			var _1 = this.getSelectedRecord();
			while (_1) {
				var _2 = _1 ? _1.liveObject : null;
				if ((isc.isA.Canvas(_2) || isc.isA.FormItem(_2))
						&& _2.isDrawn() && _2.isVisible()) {
					isc.EditContext.selectCanvasOrFormItem(_2);
					break
				}
				_1 = this.data.getParent(_1)
			}
		},
		canRemoveRecords : true,
		removeRecordClick : function(_1) {
			var _2 = this.getRecord(_1), _3 = (_2 ? _2.liveObject : null);
			if (_3 && _3.editContext) {
				_3.editContext.destroyComponent(_2)
			}
		},
		autoShowParents : true,
		observeNodeDragResized : function(_1, _2) {
			if (_2 == null)
				_2 = this.editContext.getDefaultParent(_1);
			var _3 = this.editContext.getLiveObject(_2);
			if (_3 && isc.isA.Layout(_3) && !isc.isA.ListGrid(_3)) {
				var _4 = _1.liveObject;
				if (_4.dragResized && !this.isObserving(_4, "dragResized")) {
					this.observe(_4, "dragResized",
							"observer.liveObjectDragResized(observed)")
				}
			}
		},
		liveObjectDragResized : function(_1) {
			var _2 = _1.parentElement;
			if (_2) {
				var _3 = _1.editNode;
				if (_2.vertical) {
					var _4 = _1.getHeight();
					this.creator.projectComponents.setNodeProperties(_3, {
						height : _4
					});
					this.creator.componentAttributeEditor
							.setValue("height", _4)
				} else {
					var _5 = _1.getWidth();
					this.creator.projectComponents.setNodeProperties(_3, {
						width : _5
					});
					this.creator.componentAttributeEditor.setValue("width", _5)
				}
			}
		},
		showFieldMapper : function(_1, _2, _3, _4) {
			if (!isc.isA.ListGrid(_1))
				return;
			var _5 = this;
			var _6 = _1.getDataSource();
			var _7 = _6 && _6.isA("MockDataSource");
			if (!_7 && this.creator.useFieldMapper) {
				if (_6 && _6.isA("DataSource")) {
					_7 = true
				} else if (_1.fields && _1.fields.length > 0) {
					_7 = true;
					var _8 = {};
					for (var i = 0; i < _1.fields.length; i++) {
						_8[_1.fields[i].name] = _1.fields[i]
					}
					_6 = {
						fields : _8,
						getFields : function() {
							return this.fields
						}
					}
				}
			}
			if (_7) {
				var _10 = _2.liveObject;
				if (isc.isA.MockDataSource(_2.liveObject))
					return false;
				var _11 = isc.FieldMapper.create({
					width : 780,
					height : 380,
					mockDataSource : _6,
					targetDataSource : _10
				});
				var _12 = isc.Window
						.create({
							items : [
									_11,
									{
										_constructor : "DynamicForm",
										width : "100%",
										colWidths : "*, 50, 120",
										numCols : 3,
										padding : 5,
										items : [
												{
													_constructor : "ButtonItem",
													title : "OK",
													endRow : false,
													width : 50,
													align : "right",
													click : function() {
														for (var i = _3.children.length - 1; i >= 0; i--) {
															if (_3.children[i].type == "ListGridField") {
																_5.editContext
																		.removeNode(_3.children[i])
															}
														}
														var _8 = _11
																.getMappedFields();
														for (var i = 0; i < _8.length; i++) {
															var _13 = _1
																	.getFieldEditNode(
																			_8[i],
																			_10);
															var _14 = _5.editContext
																	.makeEditNode(_13);
															_5.editContext
																	.addNode(
																			_14,
																			_3,
																			null,
																			null,
																			true)
														}
														_4();
														_1.setFields(_8);
														_1.fetchData();
														_12.hide()
													}
												},
												{
													_constructor : "ButtonItem",
													title : "Cancel",
													width : 50,
													endRow : false,
													startRow : false,
													click : function() {
														_12.hide()
													}
												},
												{
													_constructor : "ButtonItem",
													title : "Drop Existing Fields",
													width : 120,
													startRow : false,
													click : function() {
														isc
																.confirm(
																		"Drop existing fields and use default DataSource binding?",
																		function(
																				_15) {
																			if (_15) {
																				_11
																						.setDefaultData()
																			}
																		})
													}
												} ]
									} ],
							width : 800,
							height : 470,
							title : "Fields mapping",
							autoCenter : true,
							isModal : true
						})
			}
			return _7
		},
		removeNode : function(_1, _2, _3, _4, _5) {
			var _6 = _1.liveObject;
			if (_6 && this.isObserving(_6, "dragResized")) {
				this.ignore(_6, "dragResized")
			}
			this.Super("removeNode", [ _1, _2, _3, _4, _5 ], arguments);
			this.creator.componentRemoved(_1)
		},
		destroyNode : function(_1, _2, _3, _4, _5) {
			var _6 = _1.liveObject;
			if (_6 && this.isObserving(_6, "dragResized")) {
				this.ignore(_6, "dragResized")
			}
			this.Super("destroyNode", [ _1, _2, _3, _4, _5 ], arguments);
			this.creator.componentRemoved(_1)
		},
		removeAll : function() {
			this.creator.clearComponent();
			return this.Super("removeAll", arguments)
		},
		destroyAll : function() {
			this.creator.clearComponent();
			return this.Super("destroyAll", arguments)
		},
		folderOpenImage : "cubes_blue.gif",
		folderClosedImage : "cubes_blue.gif",
		folderDropImage : "cubes_green.gif",
		fileImage : "cube_blue.gif",
		hasComponents : function() {
			var _1 = this.getData();
			var _2 = _1.getLength();
			return _2 > 1 || (_2 == 1 && _1.get(0).type != "DataView")
		}
	};
	isc.A.mockupExtraPalettesDefaults = {
		_constructor : "HiddenPalette",
		data : [ {
			title : "Tab",
			type : "Tab"
		} ]
	};
	isc.A.codePreviewDefaults = {
		_constructor : "DynamicForm",
		autoDraw : false,
		overflow : "auto",
		browserSpellCheck : false,
		items : [ {
			name : "codeField",
			editorType : "TTextAreaItem",
			showTitle : false,
			colSpan : "*",
			width : "*",
			height : "*"
		} ],
		saveToSalesForce : function() {
			if (!this.$48y) {
				this.$48y = true;
				var _1 = this;
				isc
						.say(
								"This feature will save your application to your SalesForce account as an 'SControl', which can be shown in a Custom Tab via the customization interfaces within SalesForce.<P>In order to be successfully deployed to SalesForce, an application must consist  strictly of SalesForce DataSources, 'clientOnly' DataSources and XJSONDataSources.<P>The deployed application does not require Java or other external server functionality, instead accessing SalesForce APIs via SOAP, and loading the SmartClient framework itself as static web assets (from SmartClient.com by default - see the visualBuilder/index.jsp 'builderConfig' block to customize).",
								function() {
									_1.saveSControl()
								})
			} else {
				this.saveSControl()
			}
		},
		saveSControl : function(_1) {
			if (!_1) {
				var _2 = this;
				isc.SForce.ensureLoggedIn(function() {
					_2.saveSControl(true)
				}, true);
				return

				

								

				

												

				

								

				

			}
			var _3 = this.builder.projectComponents.serializeAllEditNodes(true);
			var _4 = this.creator;
			isc.xml.toJSCode(_3, function(_6, _7) {
				isc.askForValue("Name your SControl :", function(_8) {
					if (_8 == null)
						return;
					var _5 = isc.WebService.get("urn:partner.soap.sforce.com");
					_5.controlIsomorphicDir = _4.sControlIsomorphicDir;
					_5.controlSkin = _4.sControlSkin;
					_5.deploySControl(_8, _7)
				}, {
					defaultValue : "ISC"
				})
			})
		},
		hasChanged : function() {
			return this.valuesHaveChanged()
		},
		discardChanges : function() {
			this.resetValues()
		},
		saveChanges : function() {
			var _1 = this.getValue("codeField");
			var _2 = this.creator, _3 = _2.currentScreen;
			_2.loadViewFromXML(_3, _1);
			this.resetValues()
		}
	};
	isc.A.jsCodePreviewDefaults = {
		_constructor : "DynamicForm",
		autoDraw : false,
		overflow : "auto",
		browserSpellCheck : false,
		items : [ {
			name : "codeField",
			editorType : "TTextAreaItem",
			showTitle : false,
			colSpan : "*",
			width : "*",
			height : "*"
		} ],
		setContents : function(_1) {
			this.setValue("codeField", _1)
		}
	};
	isc.A.codePaneDefaults = {
		_constructor : "TTabSet",
		autoDraw : false,
		height : "35%",
		tabSelected : function(_1) {
			this.creator.updateSource()
		},
		hasChanged : function() {
			var _1 = this.getTabPane(0);
			if (_1) {
				return _1.valuesHaveChanged()
			}
			return false
		},
		discardChanges : function() {
			var _1 = this.getTabPane(0);
			if (_1) {
				_1.resetValues()
			}
		},
		saveChanges : function() {
			var _1 = this.getTabPane(0);
			if (_1) {
				var _2 = _1.getValue("codeField");
				var _3 = this.creator, _4 = _3.currentScreen;
				_3.loadViewFromXML(_4, _2);
				_1.resetValues()
			}
		}
	};
	isc.A.componentAttributeEditorDefaults = {
		_constructor : "TComponentEditor",
		autoDraw : false,
		autoFocus : false,
		overflow : "auto",
		alwaysShowVScrollbar : true,
		showAttributes : true,
		showMethods : false,
		backgroundColor : isc.nativeSkin ? null : "black",
		basicMode : true
	};
	isc.A.componentMethodEditorDefaults = {
		_constructor : "TComponentEditor",
		sortFields : true,
		autoDraw : false,
		autoFocus : false,
		overflow : "auto",
		alwaysShowVScrollbar : true,
		showAttributes : false,
		showMethods : true,
		backgroundColor : isc.nativeSkin ? null : "black",
		basicMode : true
	};
	isc.A.editorPaneDefaults = {
		_constructor : "TTabSet",
		autoDraw : false,
		paneContainerProperties : {
			customEdges : [ "T" ]
		},
		tabBarProperties : {
			baseLineCapSize : 0
		},
		tabBarControls : [ isc.Img.create({
			src : "[SKIN]/../../ToolSkin/images/actions/remove.png",
			autoDraw : false,
			width : 16,
			height : 16,
			layoutAlign : "center",
			cursor : "pointer",
			canHover : true,
			showHover : true,
			prompt : "Delete current component",
			click : function() {
				var _1 = isc.SelectionOutline.getSelectedObject();
				if (_1 && _1.editContext) {
					_1.editContext.destroyComponent(_1.editNode);
					isc.SelectionOutline.deselect()
				}
			}
		}), isc.LayoutSpacer.create({
			width : 10
		}), "tabScroller", "tabPicker" ],
		tabDeselected : function(_1, _2, _3, _4) {
			this.$710 = _2.ID
		},
		tabSelected : function(_1, _2, _3, _4) {
			if (!this.$710)
				return;
			var _5 = this.creator.getCurrentComponent(), _6 = _5 ? _5[this.$710
					+ "BasicMode"] : null, _7 = _5 ? _5[_2.ID + "BasicMode"]
					: null;
			if (_6 != _7) {
				this.creator.editComponent(_5, _5.liveObject)
			} else {
				this.creator.applyBasicModeSettings()
			}
		},
		selectedEditorName : function() {
			var _1 = this.getTabObject(this.selectedTab);
			if (_1 && _1.title)
				return _1.title.toLowerCase();
			return null
		},
		PROPERTIES : "properties",
		EVENTS : "events"
	};
	isc.A.applyButtonDefaults = {
		_constructor : "TButton",
		resizeable : false,
		autoDraw : false,
		title : "Apply",
		click : "this.creator.saveComponentEditors();",
		disabled : true,
		height : 20
	};
	isc.A.advancedButtonDefaults = {
		_constructor : "TButton",
		resizeable : false,
		autoDraw : false,
		click : function() {
			var _1 = this.creator.getCurrentComponent();
			this.creator.toggleBasicMode(_1);
			this.creator.editComponent(_1, _1.liveObject)
		},
		disabled : true,
		height : 20
	};
	isc.A.helpPaneDefaults = {
		_constructor : "THTMLFlow",
		padding : 10,
		autoDraw : false,
		overflow : "auto"
	};
	isc.A.projectPaneDefaults = {
		_constructor : "TTabSet",
		autoDraw : false
	};
	isc.A.projectMenuButtonDefaults = {
		_constructor : "TMenuButton",
		autoDraw : false,
		width : "80%",
		height : 28,
		margin : 4,
		layoutAlign : "center"
	};
	isc.A.recentProjectsMenuDefaults = {
		_constructor : "Menu",
		width : 100,
		itemClick : function(_1) {
			var _2 = this.creator;
			_2.confirmDropProject(function() {
				_2.loadProject(_1.projectID)
			})
		}
	};
	isc.A.screenMenuButtonDefaults = {
		_constructor : "TMenuButton",
		autoDraw : false,
		title : "Screen",
		height : 20,
		width : 80
	};
	isc.A.removeButtonDefaults = {
		_constructor : "ImgButton",
		autoDraw : false,
		src : "[SKIN]/../../ToolSkin/images/actions/remove.png",
		width : 16,
		height : 16,
		showRollOver : false,
		showDown : false,
		prompt : "Remove",
		visibility : "hidden",
		click : function() {
			var _1 = this.creator.projectComponents.getEditContext(), _2 = _1
					.getSelectedEditNodes();
			for (var i = 0; i < _2.length; i++) {
				_1.destroyNode(_2[i])
			}
		}
	};
	isc.A.bringToFrontButtonDefaults = {
		_constructor : "TButton",
		autoDraw : false,
		title : "Bring to front",
		height : 20,
		width : 80,
		visibility : "hidden",
		click : function() {
			var _1 = this.creator.projectComponents.getEditContext(), _2 = _1
					.getSelectedEditNodes();
			for (var i = 0; i < _2.length; i++) {
				_2[i].liveObject.bringToFront()
			}
		}
	};
	isc.A.sendToBackButtonDefaults = {
		_constructor : "TButton",
		autoDraw : false,
		title : "Send to back",
		height : 20,
		width : 80,
		visibility : "hidden",
		click : function() {
			var _1 = this.creator.projectComponents.getEditContext(), _2 = _1
					.getSelectedEditNodes();
			for (var i = 0; i < _2.length; i++) {
				_2[i].liveObject.sendToBack()
			}
		}
	};
	isc.A.mainDefaults = {
		_constructor : "TTabSet",
		width : "100%",
		height : "100%",
		backgroundColor : isc.nativeSkin ? null : "black",
		tabSelected : function(_1, _2, _3, _4) {
			if (_1 == 1) {
				isc.SelectionOutline.hideOutline();
				this.creator.updateSource()
			} else if (_1 == 0) {
				isc.SelectionOutline.showOutline();
				isc.SelectionOutline.showDragHandle()
			}
		},
		tabDeselected : function(_1, _2, _3, _4, _5) {
			if (_1 == 1) {
				if (_2.hasChanged && _2.hasChanged()) {
					var _6 = this;
					var _7 = isc.Dialog
							.create({
								message : "Code changes have been made to the generated XML. Should these code changes be saved to the current screen definition?",
								icon : "[SKIN]ask.png",
								buttons : [ isc.Button.create({
									title : "Save",
									click : function() {
										_2.saveChanges();
										_6.selectTab(0);
										this.topElement.cancelClick()
									}
								}), isc.Button.create({
									title : "Discard",
									click : function() {
										_2.discardChanges();
										_6.selectTab(0);
										this.topElement.cancelClick()
									}
								}), isc.Button.create({
									title : "Cancel",
									click : function() {
										this.topElement.cancelClick()
									}
								}) ]
							});
					_7.show();
					return false
				}
			}
			return true
		}
	};
	isc.A.operationsPaletteDefaults = {
		_constructor : isc.TTreePalette,
		getIcon : function(_1) {
			var _2 = this.creator.getServiceElementIcon(_1);
			if (_2)
				return _2;
			return this.Super("getIcon", arguments)
		}
	};
	isc.A.schemaViewerDefaults = {
		_constructor : isc.TTreeGrid,
		autoDraw : false,
		recordDoubleClick : "this.creator.operationSelected()",
		fields : [ {
			name : "name",
			title : "Service/PortType/Operation",
			treeField : true
		}, {
			name : "serviceType",
			title : "Type"
		} ],
		getIcon : function(_1) {
			var _2 = this.creator.getServiceElementIcon(_1);
			if (_2)
				return _2;
			return this.Super("getIcon", arguments)
		}
	};
	isc.A.schemaViewerSelectButtonDefaults = {
		_constructor : isc.TButton,
		autoDraw : false,
		title : "Select",
		click : "this.creator.operationSelected()"
	};
	isc.A.commonEditorFunctions = {
		itemChange : function(_1, _2, _3) {
			this.logInfo("itemChange on: " + _1 + ", value now: " + _2,
					"editing");
			if (_1.name == "classSwitcher") {
				this.builder.switchComponentClass(_2);
				return true
			}
			if (this.immediateSave || isc.isA.ExpressionItem(_1)
					|| isc.isA.ActionMenuItem(_1) || isc.isA.CheckboxItem(_1)) {
				this.saveItem(_1, _2);
				this.builder.updateSource();
				return true
			} else {
				_1.$48z = true;
				return true
			}
		},
		itemKeyPress : function(_1, _2) {
			if (_2 == "Enter")
				this.save()
		},
		saveItem : function(_1, _2) {
			return this.saveItems([ _1 ], [ _2 ])
		},
		save : function() {
			if (!this.validate())
				return;
			var _1 = [], _2 = [];
			for (var i = 0; i < this.items.length; i++) {
				var _4 = this.items[i];
				if (_4.$48z) {
					_1.add(_4);
					_2.add(this.getValue(_4.name));
					_4.$48z = false
				}
			}
			var _5 = this.saveItems(_1, _2);
			this.builder.updateSource();
			return _5
		},
		saveItems : function(_1, _2) {
			if (!_1)
				return true;
			if (_1.length > 0)
				this.builder.markDirty();
			var _3 = this.currentComponent, _4 = {};
			for (var i = 0; i < _1.length; i++) {
				var _6 = _1[i], _7 = _2[i];
				_4[_6.name] = _7
			}
			return this.saveProperties(_4, _3)
		},
		saveProperties : function(_1, _2) {
			var _3 = _2.liveObject
					|| this.builder.projectComponents.getLiveObject(_2);
			this.logInfo("applying changed properties: " + this.echo(_1)
					+ " to: " + this.echoLeaf(_3), "editing");

			this.builder.projectComponents.setNodeProperties(_2, _1);
			var _4 = this.builder.getCurrentlyVisibleEditor(), _5 = _2[_4.ID
					+ "BasicMode"];
			if (_5 == false) {
				_2.$711 = true
			}
			return true
		}
	};
	isc.A.rootComponentDefaults = {
		_constructor : "Canvas",
		border : "6px groove #666666",
		getObjectField : function(_1) {
			var _2 = this.creator
					.getScreenMockupMode(this.creator.currentScreen);
			if (!_2)
				return this.Super("getObjectField", arguments);
			var _3 = isc.ClassFactory.getClass(_1);
			if (isc.isA.Canvas(_3)) {
				return "children"
			} else {
				return null
			}
		}
	};
	isc.A.librarySearchDefaults = {
		_constructor : "DynamicForm",
		height : 20,
		numCols : 1,
		selectOnFocus : true,
		quickAddDefaults : {
			editorType : "ComboBoxItem",
			optionDataSource : "paletteDS",
			valueField : "id",
			displayField : "title",
			optionCriteria : {
				_constructor : "AdvancedCriteria",
				operator : "and",
				criteria : [ {
					fieldName : "type",
					operator : "notNull"
				} ]
			},
			completeOnTab : true,
			hint : "Quick Add..",
			showHintInField : true,
			textMatchStyle : "substring",
			loadDataOnDemand : false,
			useClientFiltering : false,
			changed : function(_1, _2, _3) {
				var _4 = _2.getSelectedRecord();
				if (_4) {
					_1.addNode(_4)
				}
			}
		},
		initWidget : function() {
			this.fields = [ this.getQuickAddField("quickAdd", false),
					this.getQuickAddField("mockupQuickAdd", true) ];
			this.Super("initWidget", arguments)
		},
		getQuickAddField : function(_1, _2) {
			var _2 = this.creator
					.getScreenMockupMode(this.creator.currentScreen), _3 = {};
			if ((_2 && this.creator.useQuickAddDescriptionField != false)
					|| this.creator.useQuickAddDescriptionField) {
				_3 = isc.addProperties(_3, {
					pickListFields : [ {
						name : "title"
					}, {
						name : "description"
					} ],
					pickListWidth : 350,
					filterFields : [ "title", "description" ]
				})
			}
			return isc.addProperties({
				name : _1,
				showTitle : false,
				width : "*",
				showIf : "false"
			}, this.quickAddDefaults, this.quickAddProperties, _3)
		},
		refresh : function() {
			var _1 = this.creator
					.getScreenMockupMode(this.creator.currentScreen);
			if (_1) {
				this.hideItem("quickAdd");
				this.showItem("mockupQuickAdd")
			} else {
				this.showItem("quickAdd");
				this.hideItem("mockupQuickAdd")
			}
		},
		addNode : function(_1) {
			var _2 = this.creator
					.getScreenMockupMode(this.creator.currentScreen), _3 = _1.type
					|| _1.className, _4 = isc.ClassFactory.getClass(_3), _5 = this.creator.projectComponents
					.getEditContext(), _6 = _5.makeEditNode(_1), _7 = this.creator.projectComponents
					.getDefaultParent(_6, true);
			if (!_7)
				return;
			if (_4 && _4.isA("FormItem")) {
				_6 = _5.addWithWrapper(_6, _7)
			} else {
				_6 = _5.addNode(_6, _7)
			}
			if (_2)
				_6.liveObject.moveTo(20, 20)
		}
	};
	isc.A.projectComponentsSearchDefaults = {
		_constructor : "GridSearch",
		searchProperty : "title",
		searchProperty : "name",
		hint : "Find Live Component By ID..."
	};
	isc.A.dataSourceListSearchDefaults = {
		_constructor : "GridSearch",
		searchProperty : "title",
		hint : "Find DataSource...",
		autoParent : "dataSourcePane"
	};
	isc.A.dataSourcePaneDefaults = {
		_constructor : "VLayout"
	};
	isc.A.screenPaneDefaults = {
		_constructor : "VLayout"
	};
	isc.A.downloadDataSourceDialogTitle = "Download DataSource [\${dsID}]";
	isc.A.downloadDataSourceDialogPrompt = "Choose the format in which to export this DataSource definition.  If you're making use of server capabilities, you should export to XML.";
	isc.A.downloadDataSourceDialogButtonTitle = "Download";
	isc.B
			.push(isc.A.$132q = function isc_VisualBuilder__updateEditComponentRemovability(
					_1) {
				if (_1 == null)
					_1 = this.$132r;
				else
					this.$132r = _1;
				var _2 = this.projectComponents.data, _3 = this.canAddRootComponents
						|| !_2.isRoot(_2.getParent(_1));
				this.editorPane.tabBarControls[0].setVisibility(_3)
			});
	isc.evalBoundary;
	isc.B
			.push(
					isc.A.setCanAddRootComponents = function isc_VisualBuilder_setCanAddRootComponents(
							_1) {
						this.canAddRootComponents = _1;
						this.projectComponents.setProperty("canDropRootNodes",
								_1);
						this.$132q()
					},
					isc.A.autoSaveCurrentSettings = function isc_VisualBuilder_autoSaveCurrentSettings() {
						this.fireOnPause("saveCurrentSettings",
								"saveCurrentSettings")
					},
					isc.A.saveCurrentSettings = function isc_VisualBuilder_saveCurrentSettings() {
						if (!this.filesystemDataSourceEnabled) {
							isc.Offline.put(this.offlineStorageKey, isc.JSON
									.encode(this.currentSettings, {
										strictQuoting : true
									}))
						} else {
							var _1 = isc.DS.get("VisualBuilder").xmlSerialize(
									this.currentSettings);
							this.settingsFileSource.saveFile(this.settingsFile,
									null, _1, null, {
										showPrompt : false
									})
						}
					},
					isc.A.$110h = function isc_VisualBuilder__restoreSettings(
							_1) {
						this.currentSettings = isc.JSON.decode(_1);
						this.setProperties(this.currentSettings)
					},
					isc.A.loadCurrentSettings = function isc_VisualBuilder_loadCurrentSettings(
							_1) {
						var _2 = this;
						if (!this.filesystemDataSourceEnabled) {
							try {
								var _3 = isc.Offline
										.get(this.offlineStorageKey);
								if (_3)
									this.$110h(_3)
							} finally {
								this.fireCallback(_1)
							}
						} else {
							this.settingsFileSource.loadFile(this.settingsFile,
									function(_4, _5, _6) {
										if (_4.status >= 0) {
											isc.DMI.callBuiltin({
												methodName : "xmlToJS",
												arguments : [ _5[0].contents ],
												requestParams : {
													willHandleError : true,
													timeout : 6000
												},
												callback : function(_7, _5) {
													try {
														if (_7.status >= 0) {
															_2.$110h(_5)
														}
													} finally {
														_2.fireCallback(_1)
													}
												}
											})
										} else {
											_2.fireCallback(_1)
										}
									}, {
										willHandleError : true,
										timeout : 6000
									})
						}
					},
					isc.A.getProjectID = function isc_VisualBuilder_getProjectID() {
						return this.project ? this.project.ID : null
					},
					isc.A.getProjectDisplayName = function isc_VisualBuilder_getProjectDisplayName() {
						var _1 = this.project ? this.project.name : null;
						if (!_1)
							return "Untitled Project";
						if (_1.endsWith(".proj.xml")) {
							return _1.slice(0, -9)
						} else if (_1.endsWith(".xml")) {
							return _1.slice(0, -4)
						} else {
							return _1
						}
					},
					isc.A.setProject = function isc_VisualBuilder_setProject(_1) {
						if (_1 == this.project)
							return;
						if (this.project) {
							this.ignore(this.project, "setID");
							this.ignore(this.project, "setName");
							this.ignore(this.project, "setScreenProperties");
							this.ignore(this.project, "removeScreen");
							this.ignore(this.project, "removeGroup");
							if (this.dataSourceList)
								this.ignore(this.project.datasources,
										"dataChanged");
							this.project.builder = null
						}
						this.project = _1;
						if (_1) {
							_1.fileSource = this.projectFileSource;
							_1.screenFileSource = this.screenFileSource;
							this.observe(_1, "setID",
									"observer.updateProjectID();");
							this.observe(_1, "setName",
									"observer.updateProjectName();");
							this
									.observe(_1, "setScreenProperties",
											"observer.updateScreenProperties(returnVal);");
							this.observe(_1, "removeScreen",
									"observer.checkCurrentScreen();");
							this.observe(_1, "removeGroup",
									"observer.checkCurrentScreen();");
							if (this.dataSourceList)
								this.observe(_1.datasources, "dataChanged",
										"observer.updateDataSourceList();");
							_1.builder = this
						}
						this.updateProjectID();
						this.updateProjectName();
						if (this.screenList) {
							this.screenList.setData(_1 ? _1.screens : isc.Tree
									.create())
						}
						if (this.dataSourceList)
							this.updateDataSourceList();
						if (this.project.currentScreenID) {
							var _2 = this.project
									.findScreen(this.project.currentScreenID);
							if (_2) {
								this.setCurrentScreen(_2)
							} else {
								this.openDefaultScreen()
							}
						} else {
							this.openDefaultScreen()
						}
					},
					isc.A.updateDataSourceList = function isc_VisualBuilder_updateDataSourceList() {
						var _1 = this;
						var _2 = this.project.datasources
								.map(function(_4) {
									var _3 = _1.dataSourceList.data.find("ID",
											_4.dsName);
									if (_3) {
										return _3
									} else {
										return _1.projectComponents.editContext
												.makeDSPaletteNode(_4.dsName,
														_4.dsType)
									}
								});
						this.dataSourceList.setData(_2)
					},
					isc.A.checkCurrentScreen = function isc_VisualBuilder_checkCurrentScreen() {
						if (this.currentScreen) {
							var _1 = this.project
									.findScreen(this.currentScreen.screenID);
							if (!_1)
								this.openDefaultScreen()
						}
					},
					isc.A.openDefaultScreen = function isc_VisualBuilder_openDefaultScreen() {
						var _1;
						if (this.singleScreenMode) {
							_1 = this.project.untitledScreen()
						} else {
							_1 = this.project.firstScreen()
						}
						if (!_1) {
							_1 = this.project.addScreen(null, null,
									"Untitled Screen");
							_1.mockupMode = this.mockupMode
						}
						this.setCurrentScreen(_1)
					},
					isc.A.updateProjectID = function isc_VisualBuilder_updateProjectID() {
						if (this.singleScreenMode)
							return;
						this.currentSettings.projectID = this.project.ID;
						this.autoSaveCurrentSettings();
						this.updateRecentProjects()
					},
					isc.A.updateProjectName = function isc_VisualBuilder_updateProjectName() {
						var _1 = this.getProjectDisplayName();
						if (this.projectMenuButton)
							this.projectMenuButton.setTitle(_1);
						this.updateRecentProjects()
					},
					isc.A.confirmDropScreen = function isc_VisualBuilder_confirmDropScreen(
							_1) {
						if (this.singleScreenMode && this.currentScreen
								&& this.currentScreen.dirty) {
							var _2 = this;
							isc.confirm("Save current project?", function(_3) {
								if (_3 == true) {
									_2.saveScreenAs(_2.currentScreen, _1)
								} else if (_3 == false) {
									_2.fireCallback(_1)
								}
							}, {
								buttons : [ isc.Dialog.YES, isc.Dialog.NO,
										isc.Dialog.CANCEL ]
							})
						} else {
							this.fireCallback(_1)
						}
					},
					isc.A.confirmDropProject = function isc_VisualBuilder_confirmDropProject(
							_1) {
						if (!this.project || this.project.isEmpty()
								|| this.getProjectID()) {
							this.fireCallback(_1)
						} else {
							var _2 = this;
							isc.confirm("Save current project?", function(_3) {
								if (_3 == true) {
									_2.saveScreenAs(_2.currentScreen, _1)
								} else if (_3 == false) {
									_2.fireCallback(_1)
								}
							}, {
								buttons : [ isc.Dialog.YES, isc.Dialog.NO,
										isc.Dialog.CANCEL ]
							})
						}
					},
					isc.A.makeNewProject = function isc_VisualBuilder_makeNewProject() {
						var _1 = isc.Project.create();
						this.setProject(_1)
					},
					isc.A.showLoadProjectUI = function isc_VisualBuilder_showLoadProjectUI() {
						this.projectFileSource.showLoadFileUI({
							target : this,
							methodName : "loadProjectReply"
						})
					},
					isc.A.loadProjectReply = function isc_VisualBuilder_loadProjectReply(
							_1, _2, _3) {
						var _4 = this;
						isc.DMI.callBuiltin({
							methodName : "xmlToJS",
							arguments : [ _2[0].contents ],
							callback : function(_6, _7) {
								var _5 = isc.eval(_7);
								if (_5.screens) {
									_5.screens.getAllNodes().map(function(_8) {
										if (_8.contents)
											_8.dirty = new Date()
									})
								}
								_5.setID(_2[0].id);
								_5.setName(_2[0].name);
								_4.setProject(_5)
							}
						})
					},
					isc.A.loadProject = function isc_VisualBuilder_loadProject(
							_1) {
						if (!_1) {
							this.logWarn("Tried to loadProject without an ID");
							return

							

														

							

																					

							

														

							

						}
						this.projectFileSource.loadFile(_1, {
							target : this,
							methodName : "loadProjectReply"
						}, {
							willHandleError : true
						})
					},
					isc.A.getRecentProjects = function isc_VisualBuilder_getRecentProjects() {
						if (!this.recentProjects)
							this.recentProjects = [];
						return this.recentProjects
					},
					isc.A.setRecentProjects = function isc_VisualBuilder_setRecentProjects(
							_1) {
						var _2 = this.getRecentProjects();
						_2.setLength(0);
						_2.addList(_1)
					},
					isc.A.updateRecentProjects = function isc_VisualBuilder_updateRecentProjects() {
						var _1 = this.getProjectID();
						if (!_1 || _1 == this.singleScreenModeProjectID)
							return;
						var _2 = this.getRecentProjects();
						var _3 = _2.findIndex("projectID", _1);
						if (_3 != -1)
							_2.removeAt(_3);
						_2.addAt({
							projectID : _1,
							title : this.getProjectDisplayName()
						}, 0);
						if (_2.getLength() > this.recentProjectsCount) {
							_2.setLength(this.recentProjectsCount)
						}
						this.currentSettings.recentProjects = _2;
						this.autoSaveCurrentSettings()
					},
					isc.A.showAddScreenGroupUI = function isc_VisualBuilder_showAddScreenGroupUI(
							_1) {
						var _2 = this.project.addGroup(_1, "New Group");
						var _3 = this.screenList.getRecordIndex(_2);
						this.screenList.delayCall("startEditing", [ _3, 0 ])
					},
					isc.A.showAddScreenUI = function isc_VisualBuilder_showAddScreenUI(
							_1) {
						var _2 = this;
						this.screenFileSource.showLoadFileUI(function(_4, _5,
								_6) {
							if (isc.isAn.Array(_5))
								_5 = _5[0];
							var _3 = _2.project.addScreen(_1, _5.id, _5.name,
									_5.contents, false);
							_2.setCurrentScreen(_3)
						})
					},
					isc.A.deleteScreen = function isc_VisualBuilder_deleteScreen(
							_1) {
						var _2 = this;
						this.screenFileSource.removeFile(_1.screenID,
								function() {
									_2.project.removeScreen(_1)
								})
					},
					isc.A.cacheCurrentScreenContents = function isc_VisualBuilder_cacheCurrentScreenContents() {
						if (this.currentScreen == null)
							return;
						this.currentScreen.contents = this.getUpdatedSource()
					},
					isc.A.setCurrentScreen = function isc_VisualBuilder_setCurrentScreen(
							_1) {
						var _2 = this.currentScreen, _3 = (_2 ? this.currentScreen.mockupMode
								: null);
						if (_2 == _1)
							return;
						this.cacheCurrentScreenContents();
						this.currentScreen = _1;
						if (!this.projectComponentsMenu)
							this.addChildren();
						if (_1) {
							if (_1.contents) {
								this.withoutDirtyTracking(function() {
									this.projectComponents.destroyAll()
								});
								this.setScreenContents(_1.contents, _3)
							} else {
								this.withoutDirtyTracking(function() {
									this.clearScreenUI()
								});
								var _4 = this;
								this.project.fetchScreenContents(_1, function(
										_5) {
									if (_5) {
										_4.withoutDirtyTracking(function() {
											_4.projectComponents.destroyAll()
										});
										_4.setScreenContents(_5, _3)
									} else {
										_4.updateScreenTitle();
										_4.showScreenUI();
										_4.refreshLibraryComponents()
									}
								})
							}
						} else {
							this.show();
							this.updateScreenTitle()
						}
						this.project.setCurrentScreenID(_1.screenID);
						if (this.screenList)
							this.screenList.selectSingleRecord(_1)
					},
					isc.A.setScreenContents = function isc_VisualBuilder_setScreenContents(
							_1, _2) {
						var _3 = this;
						this.projectComponents
								.getPaletteNodesFromXML(
										_1,
										function(_13) {
											var _4 = (_13 && _13.length > 0 ? _13[0]
													: null), _5 = (_13
													&& _13.length > 0 ? _13[_13.length - 1]
													: null);
											if ((_4 && _4.type == "MockupContainer")
													|| (_5 && _5.type == "MockupContainer")) {
												var _6 = (_4.type == "MockupContainer" ? _4
														: _5), _7 = (_6.defaults.children ? _6.defaults.children
														: _13), _8 = [];
												for (var i = 0; i < _7.length; i++) {
													var _10 = _7[i], _11 = _10.type
															|| _10._constructor;
													if (_11
															&& _11 != "MockupContainer") {
														var _12 = (_10.defaults ? _10.defaults
																: isc
																		.addProperties(
																				{},
																				_10));
														_8.add({
															type : _11,
															defaults : _12
														})
													}
												}
												_3.currentScreen.mockupMode = true;
												_3
														.refreshLibraryComponents(function() {
															_3
																	.updateScreenTitle();
															if (_2 != _3.currentScreen.mockupMode) {
																_3
																		.showScreenUI()
															}
															if (_8)
																_3.projectComponents
																		.addFromPaletteNodes(_8);
															_3
																	.updateSelectionActionButtons()
														})
											} else {
												_3.currentScreen.mockupMode = false;
												_3
														.refreshLibraryComponents(function() {
															_3
																	.updateScreenTitle();
															if (_2 != _3.currentScreen.mockupMode) {
																_3
																		.showScreenUI()
															}
															if (_13)
																_3.projectComponents
																		.addFromPaletteNodes(_13);
															_3
																	.updateSelectionActionButtons()
														})
											}
										})
					},
					isc.A.updateScreenProperties = function isc_VisualBuilder_updateScreenProperties(
							_1) {
						if (this.screenList) {
							var _2 = this.screenList.getRecordIndex(_1);
							this.screenList.refreshRow(_2);
							this.screenList.applyHilites()
						}
						this.updateScreenTitle()
					},
					isc.A.getCurrentScreenTitle = function isc_VisualBuilder_getCurrentScreenTitle() {
						var _1 = "Untitled screen";
						if (this.currentScreen && this.currentScreen.title) {
							var _1 = this.currentScreen.title;
							if (_1.endsWith(".xml"))
								_1 = _1.slice(0, -4)
						}
						return _1
					},
					isc.A.updateScreenTitle = function isc_VisualBuilder_updateScreenTitle() {
						if (this.middleStack) {
							var _1 = this
									.getScreenMockupMode(this.currentScreen), _2 = (_1
									&& this.singleScreenMode ? "Mockup" : this
									.getCurrentScreenTitle()), _3 = (!_1 || !this.singleScreenMode);
							var _4 = this.middleStack
									.getSectionNumber("applicationSection");
							if (_3 != this.middleStack.sections[_4].canCollapse
									&& this.middleStack.isDrawn()) {
								var _5 = this.middleStack.getSectionHeader(_4), _6 = this.middleStack.sections[_4], _7 = _5.controlsLayout
										.getMembers(), _8 = _6.items[0], _9 = [];
								for (var i = _7.length - 1; i >= 0; i--) {
									_9.addAt(_7[i], 0);
									_5.controlsLayout.removeMember(_7[i])
								}
								_6.items = [];
								this.middleStack.removeSection(_6);
								this.middleStack.addSection({
									title : _2,
									autoShow : true,
									ID : "applicationSection",
									canCollapse : _3,
									items : [ _8 ],
									controls : _9,
									setExpanded : function(_11) {
										this.Super("setExpanded", arguments);
										if (!_11)
											isc.SelectionOutline.hideOutline()
									}
								}, 0)
							} else {
								this.middleStack.setSectionTitle(
										"applicationSection", _2);
								this.middleStack.getSections()[0].canCollapse = _3
							}
						}
					},
					isc.A.getScreenMockupMode = function isc_VisualBuilder_getScreenMockupMode(
							_1) {
						if (!_1)
							return this.mockupMode;
						var _2 = (_1.mockupMode != null ? _1.mockupMode
								: this.mockupMode);
						if (isc.isA.String(_2))
							_2 = (_2 == "true");
						return _2
					},
					isc.A.saveScreenAs = function isc_VisualBuilder_saveScreenAs(
							_1, _2) {
						this.cacheCurrentScreenContents();
						var _3 = this;
						this.project.saveScreenAs(_1, function(_4) {
							if (_2) {
								_3.fireCallback(_2)
							} else if (_1 == _3.currentScreen) {
								_3.setCurrentScreen(_4)
							}
						})
					},
					isc.A.revertScreen = function isc_VisualBuilder_revertScreen(
							_1) {
						if (_1 == this.currentScreen)
							this.cacheCurrentScreenContents();
						var _2 = _1.contents;
						delete _1.contents;
						var _3 = this;
						this.project.fetchScreenContents(_1, function(_4) {
							if (_4) {
								if (_1 == _3.currentScreen) {
									_3.withoutDirtyTracking(function() {
										_3.projectComponents.destroyAll()
									});
									_3.projectComponents
											.addPaletteNodesFromXML(_4)
								}
							} else {
								isc.say("Reversion failed.");
								_1.contents = _2
							}
						})
					},
					isc.A.loadViewFromXML = function isc_VisualBuilder_loadViewFromXML(
							_1, _2) {
						this.cacheCurrentScreenContents();
						_1.contents = _2;
						this.project.setScreenDirty(_1, false);
						var _3 = this;
						this.withoutDirtyTracking(function() {
							_3.projectComponents.destroyAll()
						});
						var _4 = (this.currentScreen ? this.currentScreen.mockupMode
								: null);
						this.setScreenContents(_1.contents, _4)
					},
					isc.A.exportScreenAsJSP = function isc_VisualBuilder_exportScreenAsJSP(
							_1) {
						var _2 = this;
						var _3 = "Drawing,Analytics,DocViewer,VisualBuilder";
						if (_1 == this.currentScreen)
							this.cacheCurrentScreenContents();
						var _4 = '<%@ page contentType="text/html; charset=UTF-8"%>\n<%@ taglib uri="/WEB-INF/iscTaglib.xml" prefix="isomorphic" %>\n<HTML><HEAD><TITLE>'
								+ _1.title
								+ '</TITLE>\n<isomorphic:loadISC skin="'
								+ _2.skin
								+ '"'
								+ (_2.modulesDir ? ' modulesDir="'
										+ _2.modulesDir + '"' : "")
								+ (_3 ? (' includeModules="' + _3 + '"') : "")
								+ '/>\n </HEAD><BODY>\n';
						for (var i = 0; i < _2.globalDependencies.deps.length; i++) {
							var _6 = _2.globalDependencies.deps[i];
							if (_6.type == "js") {
								_4 += '<SCRIPT SRC='
										+ (_6.url.startsWith("/") ? _2.webRootRelWorkspace
												: _2.basePathRelWorkspace + "/")
										+ _6.url + '></SCRIPT>\n'
							} else if (_6.type == "schema") {
								_4 += '<SCRIPT>\n<isomorphic:loadDS name="'
										+ _6.id + '"/></SCRIPT>\n'
							} else if (_6.type == "ui") {
								_4 += '<SCRIPT>\n<isomorphic:loadUI name="'
										+ _6.id + '"/></SCRIPT>\n'
							} else if (_6.type == "css") {
								_4 += '<LINK REL="stylesheet" TYPE="text/css" HREF='
										+ (_6.url.startsWith("/") ? _2.webRootRelWorkspace
												: _2.basePathRelWorkspace + "/")
										+ _6.url + '>\n'
							}
						}
						_4 += '<SCRIPT>\nisc.Page.setAppImgDir("'
								+ _2.basePathRelWorkspace
								+ '/graphics/");\n<isomorphic:XML>\n'
								+ _1.contents
								+ '\n</isomorphic:XML></SCRIPT>\n</BODY></HTML>';
						this.addAutoChild("jspFileSource");
						this.jspFileSource
								.showSaveFileUI(
										_4,
										function(_8, _9, _10) {
											if (!isc.isAn.Array(_9))
												_9 = [ _9 ];
											var _7 = window.location.href;
											if (_7.indexOf("?") > 0)
												_7 = _7.substring(0, _7
														.indexOf("?"));
											_7 = _7.substring(0, _7
													.lastIndexOf("/"));
											_7 += (_7.endsWith("/") ? "" : "/")
													+ _2.workspaceURL
													+ _9[0].name;
											isc
													.say("Your screen can be accessed at:<P><a target=_blank href='"
															+ _7
															+ "'>"
															+ _7
															+ "</a>")
										})
					},
					isc.A.markDirty = function isc_VisualBuilder_markDirty() {
						if (!this.disableDirtyTracking && !isc.$75u
								&& this.project) {
							this.project.setScreenDirty(this.currentScreen,
									true)
						}
					},
					isc.A.withoutDirtyTracking = function isc_VisualBuilder_withoutDirtyTracking(
							_1) {
						try {
							this.disableDirtyTracking += 1;
							this.fireCallback(_1)
						} finally {
							this.disableDirtyTracking -= 1
						}
					},
					isc.A.previewAreaResized = function isc_VisualBuilder_previewAreaResized() {
						if (!this.modeSwitcher)
							return;
						var _1 = this.rootLiveObject.getVisibleWidth();
						var _2 = this.rootLiveObject.getVisibleHeight();
						this.modeSwitcher.setValue("resolution", _1 + "x" + _2)
					},
					isc.A.showImportDialog = function isc_VisualBuilder_showImportDialog(
							_1) {
						if (!this.$482) {
							this.$482 = isc.LoadFileDialog
									.create({
										actionStripControls : [ "spacer:10",
												"pathLabel",
												"previousFolderButton",
												"spacer:10",
												"upOneLevelButton",
												"spacer:10", "refreshButton",
												"spacer:2" ],
										directoryListingProperties : {
											canEdit : false
										},
										title : "Import File",
										rootDir : "/",
										initialDir : "[VBWORKSPACE]",
										webrootOnly : false,
										loadFile : function(_4) {
											if (_4.match(/\.(xml|js)$/i) == null) {
												isc
														.say("Only JS or XML files may be imported (must end with .js or .xml");
												return

												

																								

												

																																				

												

																								

												

											}
											var _2 = this;
											isc.DMI
													.callBuiltin({
														methodName : "loadFile",
														arguments : [ this.currentDir
																+ "/" + _4 ],
														callback : function(_5) {
															if (_4
																	.match(/\.xml$/i) != null) {
																isc.DMI
																		.callBuiltin({
																			methodName : "xmlToJS",
																			arguments : _5.data,
																			callback : function(
																					_5) {
																				_2
																						.fileLoaded(_5.data)
																			}
																		})
															} else {
																_2
																		.fileLoaded(_5.data)
															}
														}
													})
										},
										fileLoaded : function(_4) {
											var _3 = _1.creator.project
													.addScreen(null, null,
															"Imported Screen");
											_1.creator.setCurrentScreen(_3);
											_1.creator.projectComponents
													.destroyAll();
											_1.creator.projectComponents
													.addPaletteNodesFromJS(_4);
											this.hide()
										}
									})
						} else {
							this.$482.directoryListing.data.invalidateCache()
						}
						this.$482.show()
					},
					isc.A.loadBMMLMockup = function isc_VisualBuilder_loadBMMLMockup(
							_1, _2, _3, _4, _5) {
						var _6 = this;
						isc.DMI
								.callBuiltin({
									methodName : "loadFile",
									arguments : [ _1 ],
									callback : function(_11) {
										var _7 = isc.MockupImporter
												.create({
													dropMarkup : _2 == null ? true
															: _2,
													trimSpace : _3 == null ? true
															: _3,
													fillSpace : _4 == null ? true
															: _4,
													mockupPath : _1,
													fieldNamingConvention : _5
												});
										_7
												.bmmlToXml(
														_11.data,
														function(_12) {
															if (_12) {
																var _8 = _6.project
																		.addScreen(
																				null,
																				null,
																				"Imported BMML");
																_6
																		.setCurrentScreen(_8);
																_6.projectComponents
																		.destroyAll();
																_6.projectComponents
																		.getPaletteNodesFromXML(
																				_12,
																				function(
																						_13) {
																					for (var i = 0; i < _13.length; i++) {
																						var _10 = _13[i];
																						if (_10.autoDraw !== false
																								&& _10.component
																								&& _10.component.defaults) {
																							delete _10.component.defaults.autoDraw
																						}
																					}
																					_6.projectComponents
																							.addFromPaletteNodes(_13)
																				})
															}
														})
									}
								})
					},
					isc.A.getServiceElementIcon = function isc_VisualBuilder_getServiceElementIcon(
							_1) {
						var _2 = _1.serviceType;
						if (_2 == "service" || _2 == "categoryProject")
							return "service.png";
						else if (_2 == "portType")
							return "portType.png";
						else if (_2 == "operation")
							return "operation.png";
						else if (_2 == "message_in")
							return "email_in.png";
						else if (_2 == "message_out")
							return "email_out.png";
						else if (_2 == "simpleType")
							return "page_single.png";
						else if (_2 == "complexType")
							return "page_multiple.png";
						return null
					},
					isc.A.keyPress = function isc_VisualBuilder_keyPress() {
						if (isc.EH.getKey() == "Delete") {
							if (!this.editingOn)
								return

							

														

							

																					

							

														

							

																												

							

														

							

																					

							

														

							

							var _1 = isc.SelectionOutline.getSelectedObject();
							if (_1 && _1.editContext) {
								_1.editContext.destroyComponent(_1.editNode);
								isc.SelectionOutline.deselect();
								return false
							}
						}
					},
					isc.A.destroy = function isc_VisualBuilder_destroy() {
						this.setProject(null);
						this
								.ignore(this.projectComponents,
										"setNodeProperties");
						this.Super("destroy", arguments)
					},
					isc.A.init = function isc_VisualBuilder_init() {
						this.screenMenuDefaults = {
							_constructor : "Menu",
							title : "Screen",
							width : 60,
							data : [
									{
										title : "New screen",
										click : function(_6, _7, _8) {
											var _1 = _8.creator;
											_1.confirmDropScreen(function() {
												var _2 = _1.project.addScreen(
														null, null,
														"Untitled Screen");
												_2.mockupMode = _1.mockupMode;
												_1.setCurrentScreen(_2)
											})
										}
									},
									{
										title : "Open screen...",
										enabled : this.filesystemDataSourceEnabled,
										click : function(_6, _7, _8) {
											_8.creator
													.confirmDropScreen(function() {
														_8.creator
																.showAddScreenUI()
													})
										}
									},
									{
										title : "Import screen...",
										enabled : this.filesystemDataSourceEnabled,
										click : function(_6, _7, _8) {
											_8.creator
													.confirmDropScreen(function() {
														var _3 = "../../isomorphic/system/reference/SmartClient_Reference.html#group..visualBuilder";
														isc
																.ask(
																		"This feature allows you to import externally edited XML or JS code. The Visual Builder cannot fully capture all externally edited files. For more information, see the <a target=_blank href='"
																				+ _3
																				+ "'>Visual Builder Docs</a><br><br>Proceed with Import?",
																		function(
																				_9) {
																			if (_9)
																				_8.creator
																						.showImportDialog(_8)
																		})
													})
										}
									},
									{
										title : "Import from Balsamiq...",
										click : function(_6, _7, _8) {
											_8.creator
													.confirmDropScreen(function() {
														isc.BMMLImportDialog
																.create({
																	showFileNameField : _8.creator.loadFileBuiltinIsEnabled,
																	showAssetsNameField : _8.creator.saveFileBuiltinIsEnabled,
																	showOutputField : _8.creator.saveFileBuiltinIsEnabled,
																	showSkinSelector : false,
																	submit : function(
																			_9,
																			_10,
																			_11,
																			_12,
																			_13,
																			_14,
																			_15,
																			_16) {
																		_8.creator
																				.loadBMMLMockup(
																						_9,
																						_13,
																						_14,
																						_15,
																						_16);
																		this
																				.markForDestroy()
																	}
																})
													})
										}
									},
									{
										dynamicTitle : function(_6, _7, _8) {
											return "Save '"
													+ _7.creator
															.getCurrentScreenTitle()
													+ "'"
										},
										enabled : this.filesystemDataSourceEnabled,
										click : function(_6, _7, _8) {
											_8.creator
													.cacheCurrentScreenContents();
											_8.creator.project
													.saveScreenContents(_8.creator.currentScreen)
										}
									},
									{
										title : "Save as ...",
										enabled : this.filesystemDataSourceEnabled,
										click : function(_6, _7, _8) {
											_8.creator
													.saveScreenAs(_8.creator.currentScreen)
										}
									},
									{
										title : "Export as JSP ...",
										enabled : this.filesystemDataSourceEnabled,
										click : function(_6, _7, _8) {
											_8.creator
													.exportScreenAsJSP(_8.creator.currentScreen)
										}
									},
									{
										title : "Revert",
										enabled : this.filesystemDataSourceEnabled ? null
												: false,
										enableIf : function(_6, _7, _8) {
											if (_8.enabled === false)
												return false;
											return _7.creator.currentScreen.screenID
										},
										click : function(_6, _7, _8) {
											isc
													.confirm(
															"Revert screen to saved version?",
															function(_9) {
																if (_9) {
																	_8.creator
																			.revertScreen(_8.creator.currentScreen)
																}
															})
										}
									},
									{
										title : "Remove from project",
										removeInSingleScreenMode : true,
										click : function(_6, _7, _8) {
											_8.creator.project
													.removeScreen(_8.creator.currentScreen)
										}
									},
									{
										dynamicTitle : function(_6, _7, _8) {
											return "Delete '"
													+ _7.creator
															.getCurrentScreenTitle()
													+ "'"
										},
										enabled : this.filesystemDataSourceEnabled ? null
												: false,
										enableIf : function(_6, _7, _8) {
											if (_8.enabled === false)
												return false;
											return _7.creator.currentScreen.screenID
										},
										click : function(_6, _7, _8) {
											var _4 = "Delete screen '"
													+ _8.creator.currentScreen.title
													+ "' from server? This operation cannot be undone.";
											isc
													.confirm(
															_4,
															function(_9) {
																if (_9)
																	_8.creator
																			.deleteScreen(_8.creator.currentScreen)
															})
										}
									} ]
						};
						if (this.singleScreenMode) {
							var _5 = this.screenMenuDefaults.data;
							_5.removeList(_5.findAll(
									"removeInSingleScreenMode", true))
						}
						this.projectMenuDefaults = {
							_constructor : "Menu",
							title : "Project",
							width : 100,
							data : [
									{
										title : "New project ...",
										click : function(_6, _7, _8) {
											_8.creator
													.confirmDropProject("this.makeNewProject()")
										}
									},
									{
										title : "Load project ...",
										enabled : this.filesystemDataSourceEnabled,
										click : function(_6, _7, _8) {
											_8.creator
													.confirmDropProject("this.showLoadProjectUI();")
										}
									},
									{
										title : "Save project",
										enabled : this.filesystemDataSourceEnabled,
										click : function(_6, _7, _8) {
											_8.creator.project.save()
										}
									},
									{
										title : "Save project as ...",
										enabled : this.filesystemDataSourceEnabled,
										click : function(_6, _7, _8) {
											_8.creator.project.saveAs()
										}
									},
									{
										title : "Recent projects",
										forceDisabled : !this.filesystemDataSourceEnabled,
										enableIf : function(_6, _7, _8) {
											if (_8.forceDisabled)
												return false;
											if (!_8.submenu)
												_8.submenu = _7.creator.recentProjectsMenu;
											return _8.submenu
													&& _8.submenu.data
															.getLength() > 0
										}
									} ]
						};
						this.screenListDefaults = {
							_constructor : "TTreeGrid",
							autoParent : "screenPane",
							canReparentNodes : true,
							canReorderRecords : true,
							canEdit : true,
							editEvent : "none",
							showHeader : false,
							hilites : [ {
								criteria : {
									_constructor : "AdvancedCriteria",
									fieldName : "dirty",
									operator : "notNull"
								},
								cssText : "color: red;"
							} ],
							fields : [ {
								name : "title",
								treeField : true,
								formatCellValue : function(_6, _7, _8, _9, _10) {
									return _6.endsWith(".xml") ? _6
											.slice(0, -4) : _6
								}
							} ],
							screenContextMenuDefaults : {
								_constructor : "Menu",
								autoDraw : false,
								showIcon : false,
								showMenuFor : function(_6, _7) {
									this.$109l = _6;
									this.$109m = _7;
									this.showContextMenu()
								},
								data : [
										{
											title : "Save",
											enabled : this.filesystemDataSourceEnabled,
											click : function(_6, _7, _8) {
												_8.creator.creator
														.cacheCurrentScreenContents();
												_8.creator.creator.project
														.saveScreenContents(_8.$109l)
											}
										},
										{
											title : "Save as ...",
											enabled : this.filesystemDataSourceEnabled,
											click : function(_6, _7, _8) {
												_8.creator.creator
														.saveScreenAs(_8.$109l)
											}
										},
										{
											title : "Export as JSP ...",
											enabled : this.filesystemDataSourceEnabled,
											click : function(_6, _7, _8) {
												_8.creator.creator
														.exportScreenAsJSP(_8.$109l)
											}
										},
										{
											title : "Revert",
											enabled : this.filesystemDataSourceEnabled ? null
													: false,
											enableIf : function(_6, _7, _8) {
												if (_8.enabled === false)
													return false;
												return _7.$109l.screenID
											},
											click : function(_6, _7, _8) {
												isc
														.confirm(
																"Revert screen to saved version?",
																function(_9) {
																	if (_9) {
																		_8.creator.creator
																				.revertScreen(_8.$109l)
																	}
																})
											}
										},
										{
											title : "Remove from project",
											click : function(_6, _7, _8) {
												_8.creator.creator.project
														.removeScreen(_8.$109l)
											}
										},
										{
											title : "Delete on server",
											enabled : this.filesystemDataSourceEnabled ? null
													: false,
											enableIf : function(_6, _7, _8) {
												if (_8.enabled === false)
													return false;
												return _7.$109l.screenID
											},
											click : function(_6, _7, _8) {
												var _4 = "Delete screen '"
														+ _8.$109l.title
														+ "' on the server? This operation cannot be undone.";
												isc
														.confirm(
																_4,
																function(_9) {
																	if (_9)
																		_8.creator.creator
																				.deleteScreen(_8.$109l)
																})
											}
										} ]
							},
							groupContextMenuDefaults : {
								_constructor : "Menu",
								autoDraw : false,
								showIcon : false,
								showMenuFor : function(_6, _7) {
									this.$109l = _6;
									this.$109m = _7;
									this.showContextMenu()
								},
								data : [
										{
											title : "Remove from project",
											click : function(_6, _7, _8) {
												_8.creator.creator.project
														.removeGroup(_8.$109l)
											}
										},
										{
											title : "Rename",
											click : function(_6, _7, _8) {
												_8.creator.startEditing(
														_8.$109m, 0)
											}
										} ]
							},
							initWidget : function() {
								this.Super("initWidget", arguments);
								this.screenContextMenu = this
										.createAutoChild("screenContextMenu");
								this.groupContextMenu = this
										.createAutoChild("groupContextMenu")
							},
							folderContextClick : function(_6, _7, _8) {
								this.groupContextMenu.showMenuFor(_7, _8);
								return false
							},
							leafContextClick : function(_6, _7, _8) {
								this.screenContextMenu.showMenuFor(_7, _8);
								return false
							},
							selectionStyle : "single",
							selectionChanged : function(_6, _7) {
								if (_7 && !_6.isFolder)
									this.creator.setCurrentScreen(_6)
							}
						};
						this.screenAddButtonMenuDefaults = {
							_constructor : "Menu",
							data : [
									{
										title : "Add saved screen...",
										enabled : this.filesystemDataSourceEnabled,
										click : function(_6, _7, _8) {
											_8.creator.showAddScreenUI()
										}
									},
									{
										title : "New screen",
										click : function(_6, _7, _8) {
											var _2 = _8.creator.project
													.addScreen(null, null,
															"Untitled Screen");
											_2.mockupMode = _8.creator.mockupMode;
											_8.creator.setCurrentScreen(_2)
										}
									},
									{
										title : "New group",
										click : function(_6, _7, _8) {
											_8.creator.showAddScreenGroupUI()
										}
									},
									{
										title : "Import screen ...",
										enabled : this.filesystemDataSourceEnabled,
										click : function(_6, _7, _8) {
											var _3 = "../../isomorphic/system/reference/SmartClient_Reference.html#group..visualBuilder";
											isc
													.ask(
															"This feature allows you to import externally edited XML or JS code. The Visual Builder cannot fully capture all externally edited files. For more information, see the <a target=_blank href='"
																	+ _3
																	+ "'>Visual Builder Docs</a><br><br>Proceed with Import?",
															function(_9) {
																if (_9)
																	_8.creator
																			.showImportDialog(_8)
															})
										}
									},
									{
										title : "Import from Balsamiq...",
										click : function(_6, _7, _8) {
											isc.BMMLImportDialog
													.create({
														showFileNameField : _8.creator.loadFileBuiltinIsEnabled,
														showAssetsNameField : _8.creator.saveFileBuiltinIsEnabled,
														showOutputField : _8.creator.saveFileBuiltinIsEnabled,
														showSkinSelector : false,
														submit : function(_9,
																_10, _11, _12,
																_13, _14, _15,
																_16) {
															_8.creator
																	.loadBMMLMockup(
																			_9,
																			_13,
																			_14,
																			_15,
																			_16);
															this
																	.markForDestroy()
														}
													})
										}
									} ]
						};
						this.Super("init", arguments)
					});
	isc.evalBoundary;
	isc.B
			.push(
					isc.A.initWidget = function isc_VisualBuilder_initWidget() {
						this.Super('initWidget', arguments);
						isc.addProperties(this.projectComponentsDefaults, {
							canDropRootNodes : this.canAddRootComponents
						});
						isc.designTime = true;
						if (this.filesystemDataSourceEnabled) {
							this.addAutoChild("settingsFileSource");
							this.addAutoChild("projectFileSource");
							this.addAutoChild("screenFileSource")
						}
						this.loadCurrentSettings({
							target : this,
							methodName : "finishInitWidget"
						})
					},
					isc.A.finishInitWidget = function isc_VisualBuilder_finishInitWidget() {
						if (!this.currentSettings)
							this.currentSettings = {};
						isc.Page.setEvent("mouseDown", function() {
							var _1 = isc.EditContext.titleEditor;
							if (_1) {
								var x = isc.EH.getX(), y = isc.EH.getY();
								var _4 = _1.getPageRect();
								if (x >= _4[0] && x <= _4[0] + _4[2]
										&& y >= _4[1] && y <= _4[1] + _4[3]) {
								} else {
									_1.blur(_1, _1.getItem("title"))
								}
							}
						});
						if (this.defaultApplicationMode) {
							this.editingOn = this.defaultApplicationMode
									.toLowerCase() == "edit"
						} else {
							this.editingOn = false
						}
						this.paletteNodeDS = this
								.createAutoChild("paletteNodeDS");
						this.paletteDS = this.createAutoChild("paletteDS", {
							paletteNodeDS : this.paletteNodeDS,
							customComponentsURL : this.getCustomComponentsURL()
						});
						var _5 = this;
						this.rootLiveObject = this.createAutoChild(
								"rootComponent", {
									autoDraw : false,
									editorRoot : true,
									canFocus : true,
									width : "100%",
									height : "50%",
									resized : function() {
										_5.previewAreaResized()
									}
								});
						this.previewArea = this.rootLiveObject;
						var _5 = this;
						this.globalDependencies = isc.DataSource.create({
							dataURL : "globalDependencies.xml",
							recordXPath : "//dependency",
							fields : [ {
								name : "type"
							} ],
							loadDependencies : function(_10) {
								this.deps = _10;
								for (var i = 0; i < _10.length; i++) {
									var _7 = _10[i];
									if (_7.type == "js" || _7.type == "css") {
										if (_7.url.startsWith("/")) {
											isc.FileLoader.loadFile("../.."
													+ _7.url)
										} else {
											isc.FileLoader.loadFile(_7.url)
										}
									} else if (_7.type == "schema") {
										isc.DataSource.get(_7.id, function() {
										})
									} else if (_7.type == "ui") {
									}
								}
							}
						});
						this.globalDependencies.fetchData(null, this.getID()
								+ ".globalDependencies.loadDependencies(data)");
						if (this.singleScreenMode) {
							this.showScreenList = false;
							this.showProjectMenu = false
						}
						if (this.singleScreenMode) {
							var _8 = isc.Offline
									.get(isc.Project.AUTOSAVE_SINGLE_SCREEN);
							if (_8) {
								this.loadProjectReply(null, [ {
									id : null,
									name : null,
									contents : _8
								} ], null)
							} else {
								this.makeNewProject()
							}
						} else if (this.project) {
							var _9 = this.project;
							this.project = null;
							this.setProject(_9)
						} else if (this.projectID) {
							this.loadProject(this.projectID);
							this.makeNewProject()
						} else {
							var _8 = isc.Offline.get(isc.Project.AUTOSAVE);
							if (_8) {
								this.loadProjectReply(null, [ {
									id : null,
									name : null,
									contents : _8
								} ], null)
							} else {
								this.makeNewProject()
							}
						}
					},
					isc.A.doAutoSave = function isc_VisualBuilder_doAutoSave(_1) {
						if (this.project) {
							this.project.autoSave(_1)
						}
					},
					isc.A.hide = function isc_VisualBuilder_hide() {
						isc.SelectionOutline.deselect();
						this.Super("hide", arguments)
					},
					isc.A.clear = function isc_VisualBuilder_clear() {
						isc.SelectionOutline.deselect();
						this.Super("clear", arguments)
					},
					isc.A.showScreenUI = function isc_VisualBuilder_showScreenUI() {
						var _1 = this.getScreenMockupMode(this.currentScreen);
						if (_1) {
							if (this.leftStack
									.getSectionNumber("componentProperties") >= 0)
								this.leftStack
										.hideSection("componentProperties");
							if (this.leftStack.getSectionNumber("helpPane") >= 0)
								this.leftStack.hideSection("helpPane");
							if (this.leftStack
									.getSectionNumber("componentLibrary") < 0) {
								var _2 = this.rightStack
										.sectionForItem(this.librarySearch);
								this.librarySearch.deparent();
								this.libraryComponents.deparent();
								_2.items = [];
								this.rightStack.removeSection(_2);
								this.leftStack.addSection({
									title : "Component Library",
									ID : "componentLibrary",
									autoShow : true,
									items : [ this.librarySearch,
											this.libraryComponents ]
								}, 0)
							}
							this.middleStack.hideSection("componentTree");
							this.middleStack.setShowResizeBar(false);
							this.rightStack.hide();
							this.removeButton.show();
							this.bringToFrontButton.show();
							this.sendToBackButton.show();
							this.screenMenuButton.hide();
							this.main.setTabPane("generatedCodeTab",
									this.codePreview);
							var _3 = this.projectComponents.getEditContext();
							_3.isVisualBuilder = false;
							if (!this.mockupExtraPalettes)
								this.mockupExtraPalettes = this
										.createAutoChild("mockupExtraPalettes");
							_3.extraPalettes = this.mockupExtraPalettes;
							_3.allowNestedDrops = false;
							_3.selectedAppearance = "outlineMask";
							_3.showSelectedLabel = false;
							this.rootLiveObject.autoMaskChildren = true;
							this.rootLiveObject.childrenSnapToGrid = true;
							if (this.rootLiveObject.editProxy) {
								this.rootLiveObject.editProxy.canSelectChildren = true;
								this.rootLiveObject.editProxy.persistCoordinates = true
							} else {
								if (this.rootLiveObject.editProxyProperties) {
									isc
											.addProperties(
													this.rootLiveObject,
													{
														editProxyProperties : isc
																.addProperties(
																		{},
																		this.rootLiveObject.editProxyProperties,
																		{
																			canSelectChildren : true,
																			persistCoordinates : true
																		})
													})
								} else {
									this.rootLiveObject.editProxyProperties = {
										canSelectChildren : true,
										persistCoordinates : true
									}
								}
							}
							this.rootLiveObject.setEditMode(true,
									this.projectComponents.getEditContext());
							this.enableKeyHandler(true)
						} else {
							if (this.leftStack
									.getSectionNumber("componentProperties") >= 0)
								this.leftStack
										.showSection("componentProperties");
							if (this.leftStack.getSectionNumber("helpPane") >= 0)
								this.leftStack.showSection("helpPane");
							this.middleStack.showSection("componentTree");
							this.middleStack.setShowResizeBar(true);
							if (this.rightStack
									.getSectionNumber("componentLibrary") < 0) {
								var _2 = this.leftStack
										.sectionForItem(this.librarySearch);
								this.librarySearch.deparent();
								this.libraryComponents.deparent();
								_2.items = [];
								this.leftStack.removeSection(_2);
								this.rightStack.addSection({
									title : "Component Library",
									ID : "componentLibrary",
									autoShow : true,
									items : [ this.librarySearch,
											this.libraryComponents ]
								}, 0);
								this.rightStack.show()
							}
							this.removeButton.hide();
							this.bringToFrontButton.hide();
							this.sendToBackButton.hide();
							if (this.previewArea.isVisible()) {
								this.screenMenuButton.show()
							}
							if (this.main.getTabPane("generatedCodeTab") != this.codePane) {
								this.main.setTabPane("generatedCodeTab",
										this.codePane)
							}
							;
							var _3 = this.projectComponents.getEditContext();
							_3.isVisualBuilder = true;
							_3.allowNestedDrops = true;
							_3.selectedAppearance = "outlineEdges";
							_3.showSelectedLabel = true;
							_3.extraPalettes = null;
							this.rootLiveObject.autoMaskChildren = false;
							this.rootLiveObject.childrenSnapToGrid = false;
							if (this.rootLiveObject.editProxy)
								this.rootLiveObject.editProxy.persistCoordinates = false;
							this.enableKeyHandler(false)
						}
						this.show()
					},
					isc.A.updateSelectionActionButtons = function isc_VisualBuilder_updateSelectionActionButtons() {
						var _1 = this.projectComponents.getEditContext()
								.getSelectedEditNodes();
						if (_1.length == 0) {
							this.removeButton.disable();
							this.sendToBackButton.disable();
							this.bringToFrontButton.disable()
						} else {
							this.removeButton.enable();
							this.sendToBackButton.enable();
							this.bringToFrontButton.enable()
						}
					},
					isc.A.enableKeyHandler = function isc_VisualBuilder_enableKeyHandler(
							_1) {
						if (_1) {
							if (!this.$1367) {
								this.$1367 = isc.Page
										.setEvent("keyPress", this)
							}
						} else {
							if (this.$1367) {
								isc.Page.clearEvent("keyPress", this.$1367);
								delete this.$1367
							}
						}
					},
					isc.A.pageKeyPress = function isc_VisualBuilder_pageKeyPress(
							_1, _2) {
						var _3 = isc.EH.getKeyEventCharacter(), _4 = this.projectComponents
								.getEditContext().getSelectedEditNodes();
						if (_4.length == 0)
							return;
						if (!this.previewArea.containsFocus())
							return;
						var _3 = isc.EH.getKey();
						if (_3 == "Delete" || _3 == "Backspace") {
							var _5 = this.projectComponents.getEditContext();
							for (var i = 0; i < _4.length; i++) {
								_5.removeNode(_4[i])
							}
							return false
						}
					},
					isc.A.addChildren = function isc_VisualBuilder_addChildren() {
						this.hide();
						var _1 = this;
						if (this.showBuilderOnly)
							this.showCodePane = false;
						this.projectComponentsMenu = this
								.createAutoChild("projectComponentsMenu");
						this.addAutoChild("libraryComponents");
						this.addAutoChild("librarySearch", {
							grid : this.libraryComponents
						});
						this
								.addAutoChild(
										"projectComponents",
										{
											contextMenu : this.projectComponentsMenu,
											rootComponent : this.rootComponent,
											rootLiveObject : this.rootLiveObject,
											defaultPalette : this.libraryComponents,
											editContextProperties : {
												showSelectedLabelOnSelect : (this.hideLabelWhenSelecting != true),
												canSelectChildren : true,
												selectedEditNotesUpdated : function() {
													_1
															.updateSelectionActionButtons()
												}
											}
										});
						this.libraryComponents.defaultEditContext = this.projectComponents.editContext;
						this.observe(this.projectComponents.editContext,
								"setNodeProperties", "observer.markDirty();");
						this.projectTree = this.projectComponents.data;
						this.projectTree.observe(this.projectTree,
								"dataChanged", function() {
									_1.updateSource()
								});
						if (this.showCodePane != false) {
							this.addAutoChild("codePane");
							this.addAutoChildren([ "codePreview",
									"jsCodePreview" ]);
							if (this.showCodePreview != false)
								this.codePane.addTab({
									title : "XML Code",
									pane : this.codePreview,
									width : 150,
									click : this.getID() + ".updateSource();"
								});
							if (this.showJsCodePreview != false)
								this.codePane.addTab({
									title : "JS Code",
									pane : this.jsCodePreview,
									width : 150,
									click : this.getID() + ".updateSource();"
								})
						}
						this.addAutoChild("componentAttributeEditor", isc
								.addProperties(this.commonEditorFunctions, {
									builder : this
								}));
						this
								.addAutoChild(
										"componentMethodEditor",
										isc
												.addProperties(
														this.commonEditorFunctions,
														{
															canEditExpressions : this.canEditExpressions,
															builder : this
														}));
						this.addAutoChild("editorPane");
						if (this.showComponentAttributeEditor != false)
							this.editorPane.addTab({
								title : "Properties",
								pane : this.componentAttributeEditor
							});
						if (this.showComponentMethodEditor != false)
							this.editorPane.addTab({
								title : "Events",
								pane : this.componentMethodEditor
							});
						this.applyButton = this.createAutoChild("applyButton");
						if (this.showHelpPane != false) {
							this.helpPane = this
									.createAutoChild(
											"helpPane",
											{
												contentsURL : this.helpPaneProperties.contentsURL
											})
						}
						if (this.showLeftStack != false) {
							this.addAutoChild("leftStack");
							if (this.showEditorPane != false) {
								this.editorPaneButtonBar = isc.HStack.create({
									membersMargin : 10,
									height : this.applyButton.height,
									members : [ this.applyButton ]
								});
								if (this.showAdvancedButton != false) {
									this.advancedButton = this
											.createAutoChild("advancedButton");
									this.advancedButton
											.setTitle(this.componentAttributeEditor.basicMode ? this.componentAttributeEditor.moreTitle
													: this.componentAttributeEditor.lessTitle);
									this.editorPaneButtonBar
											.addMember(this.advancedButton)
								}
								this.leftStack.addSection({
									title : "Component Properties",
									ID : "componentProperties",
									autoShow : true,
									items : [ this.editorPane,
											this.editorPaneButtonBar ]
								})
							}
							if (this.showHelpPane != false) {
								this.leftStack
										.addSection({
											title : this.helpPaneProperties.headerTitle,
											ID : "helpPane",
											autoShow : false,
											items : [ this.helpPane ]
										})
							}
						}
						this.showMiddleStack = (this.showPreviewArea != false || this.showProjectComponents != false);
						var _2 = [];
						this.removeButton = this
								.createAutoChild("removeButton");
						_2.add(this.removeButton);
						this.bringToFrontButton = this
								.createAutoChild("bringToFrontButton");
						_2.add(this.bringToFrontButton);
						this.sendToBackButton = this
								.createAutoChild("sendToBackButton");
						_2.add(this.sendToBackButton);
						if (this.showScreenMenu != false) {
							this.screenMenu = this
									.createAutoChild("screenMenu");
							this.screenMenuButton = this.createAutoChild(
									"screenMenuButton", {
										menu : this.screenMenu
									});
							_2.add(this.screenMenuButton);
							_2.add(isc.LayoutSpacer.create({
								width : 10
							}))
						}
						if (this.showModeSwitcher != false) {
							var _3 = this.modeSwitcher = this
									.createAutoChild("modeSwitcher");
							_3.getField("switcher").setValue(
									this.editingOn ? "Edit" : "Live");
							_2.add(_3)
						}
						if (this.showMiddleStack != false) {
							this.addAutoChild("middleStack");
							if (this.showPreviewArea != false) {
								this.middleStack.addSection({
									title : "Application",
									autoShow : true,
									ID : "applicationSection",
									items : [ this.previewArea ],
									controls : _2,
									setExpanded : function(_5) {
										this.Super("setExpanded", arguments);
										if (!_5)
											isc.SelectionOutline.hideOutline()
									}
								})
							}
							if (this.showProjectComponents != false) {
								this.projectComponentsSearch = this
										.createAutoChild(
												"projectComponentsSearch",
												{
													grid : this.projectComponents
												});
								this.middleStack.addSection({
									height : 24,
									name : "componentTree",
									title : "Component Tree",
									autoShow : true,
									items : [ this.projectComponents ],
									controls : [ this.projectComponentsSearch ]
								})
							}
						}
						if (this.collapseComponentTree == true)
							this.middleStack.collapseSection(1);
						this.showRightStack = (this.showLibraryComponents != false
								|| this.showScreenList != false || this.showDataSourceList != false);
						if (this.showRightStack != false) {
							this.addAutoChild("rightStack");
							if (this.showLibraryComponents != false) {
								this.rightStack.addSection({
									title : "Component Library",
									ID : "componentLibrary",
									autoShow : true,
									items : [ this.librarySearch,
											this.libraryComponents ]
								})
							}
							if (this.showProjectMenu != false) {
								this.recentProjectsMenu = this.createAutoChild(
										"recentProjectsMenu", {
											data : this.getRecentProjects()
										});
								this.projectMenu = this
										.createAutoChild("projectMenu");
								this.projectMenuButton = this.createAutoChild(
										"projectMenuButton", {
											menu : this.projectMenu
										})
							}
							if (this.showScreenList != false
									|| this.showDataSourceList != false) {
								var _4 = this.showScreenList != false
										&& this.showDataSourceList != false;
								if (_4) {
									this.projectPane = this
											.createAutoChild("projectPane");
									this.rightStack.addSection({
										name : "project",
										title : "Project",
										autoShow : true,
										items : [ this.projectMenuButton,
												this.projectPane ]
									})
								}
								if (this.showScreenList != false) {
									this.screenPane = this
											.createAutoChild("screenPane");
									this.addAutoChild("screenList");
									this.addAutoChild("screenListToolbar");
									this.screenAddButtonMenu = this
											.createAutoChild("screenAddButtonMenu");
									this.addAutoChild("screenAddButton", {
										menu : this.screenAddButtonMenu
									});
									if (this.projectPane) {
										this.projectPane.addTab({
											title : "Screens",
											pane : this.screenPane
										})
									} else {
										this.rightStack.addSection({
											title : "Screens",
											autoShow : true,
											items : [ this.screenPane ]
										})
									}
								}
								if (this.showDataSourceList != false) {
									this.dataSourcePane = this
											.createAutoChild("dataSourcePane");
									this.addAutoChildren([ "dataSourceList",
											"dataSourceListToolbar",
											"dsNewButton", "dsEditButton" ]);
									this.dsNewButton.menu = this
											.createAutoChild("dsNewButtonMenu");
									this.dataSourceListSearch = this
											.createAutoChild(
													"dataSourceListSearch",
													{
														grid : this.dataSourceList
													});
									if (this.projectComponents)
										this.projectComponents.extraPalettes = [ this.dataSourceList ];
									if (this.projectPane) {
										this.projectPane.addTab({
											title : "DataSources",
											pane : this.dataSourcePane
										})
									} else {
										this.rightStack.addSection({
											name : "dataSources",
											title : "DataSources",
											autoShow : true,
											items : [ this.dataSourcePane ]
										})
									}
									if (this.project) {
										this
												.observe(
														this.project.datasources,
														"dataChanged",
														"observer.updateDataSourceList();");
										this.updateDataSourceList()
									}
								}
							}
						}
						this.addAutoChild("workspace");
						if (this.showLeftStack != false)
							this.workspace.addMember(this.leftStack);
						if (this.showMiddleStack != false)
							this.workspace.addMember(this.middleStack);
						if (this.showRightStack != false)
							this.workspace.addMember(this.rightStack);
						if (this.showCodePane != false) {
							this.addAutoChild("main", {
								tabs : [ {
									title : "Build",
									pane : this.workspace
								}, {
									title : "Code",
									ID : "generatedCodeTab",
									pane : this.codePane
								} ]
							})
						}
					},
					isc.A.editComponent = function isc_VisualBuilder_editComponent(
							_1, _2) {
						if (isc.isA.DataSource(_2))
							return;
						if (_1 != null)
							this.$132q(_1);
						this.setBasicMode(_1);
						if (this.showComponentAttributeEditor != false) {
							this.componentAttributeEditor.editComponent(_1, _2)
						}
						if (this.showComponentMethodEditor != false)
							this.componentMethodEditor.editComponent(_1, _2);
						if (this.showComponentAttributeEditor != false
								|| this.showComponentMethodEditor != false) {
							this.applyBasicModeSettings(_1)
						}
						if (isc.Browser.isIE && this.editorPane.paneContainer
								&& this.editorPane.paneContainer.isVisible()) {
							this.editorPane.paneContainer.hide();
							this.editorPane.paneContainer.show()
						}
						if (this.leftStack) {
							var _3 = _2;
							if (!_3._constructor)
								_3 = _1;
							this.leftStack.setSectionTitle(
									"componentProperties", isc.DS.getAutoId(_3)
											+ " Properties")
						}
						this.setComponentList()
					},
					isc.A.setBasicMode = function isc_VisualBuilder_setBasicMode(
							_1) {
						if (!_1)
							return;
						var _2 = this.getCurrentlyVisibleEditor(), _3 = _2.ID
								+ "BasicMode";
						if (_1[_3] == null)
							_1[_3] = _2.basicMode;
						_2.$694 = _1[_3]
					},
					isc.A.toggleBasicMode = function isc_VisualBuilder_toggleBasicMode(
							_1) {
						if (!_1)
							return;
						var _2 = this.getCurrentlyVisibleEditor();
						_2.$694 = _2.$694 == null ? !_2.basicMode : !_2.$694;
						_1[_2.ID + "BasicMode"] = _2.$694
					},
					isc.A.applyBasicModeSettings = function isc_VisualBuilder_applyBasicModeSettings(
							_1) {
						if (!_1)
							return;
						var _2 = this.getCurrentlyVisibleEditor();
						this.setComponentEditorButtonState(_1);
						this.setClassSwitcherState(_1)
					},
					isc.A.setComponentEditorButtonState = function isc_VisualBuilder_setComponentEditorButtonState(
							_1) {
						if (!_1)
							return;
						if (this.showAdvancedButton != false) {
							var _2 = this.getCurrentlyVisibleEditor(), _3 = _1[_2.ID
									+ "BasicMode"];
							if (_2.showMethods) {
								var _4 = _2.dataSource ? isc.DS
										.get(_2.dataSource) : null;
								var _5 = false;
								if (_4.methods) {
									for (var i = 0; i < _4.methods.length; i++) {
										if (_4.methods[i].basic) {
											_5 = true;
											break
										}
									}
								}
								if (!_5) {
									_2.basicMode = false;
									this.advancedButton.setDisabled(true);
									return

									

																		

									

																											

									

																		

									

								}
							}
							if (_3) {
								this.advancedButton.setTitle(_2.moreTitle)
							} else {
								this.advancedButton.setTitle(_2.lessTitle)
							}
							this.advancedButton.setDisabled(false)
						}
						this.applyButton.setDisabled(false)
					},
					isc.A.setClassSwitcherState = function isc_VisualBuilder_setClassSwitcherState(
							_1) {
						if (this.getCurrentlyVisibleEditor() != this.componentAttributeEditor)
							return;
						if (!this.componentAttributeEditor.canSwitchClass)
							return;
						if (!this.componentAttributeEditor
								.getField("classSwitcher"))
							return;
						if (!this.componentAttributeEditor.$694 || _1.$711) {
							this.componentAttributeEditor.getField(
									"classSwitcher").setDisabled(true)
						} else {
							this.componentAttributeEditor.getField(
									"classSwitcher").setDisabled(false)
						}
					},
					isc.A.getCurrentlyVisibleEditor = function isc_VisualBuilder_getCurrentlyVisibleEditor() {
						if (this.editorPane.selectedEditorName() == this.editorPane.PROPERTIES) {
							return this.componentAttributeEditor
						}
						return this.componentMethodEditor
					},
					isc.A.saveComponentEditors = function isc_VisualBuilder_saveComponentEditors() {
						if (this.componentMethodEditor)
							this.componentMethodEditor.save();
						if (this.componentAttributeEditor)
							this.componentAttributeEditor.save()
					},
					isc.A.getCurrentComponent = function isc_VisualBuilder_getCurrentComponent() {
						return this.componentAttributeEditor ? this.componentAttributeEditor.currentComponent
								: this.componentMethodEditor ? this.componentMethodEditor.currentComponent
										: null
					},
					isc.A.setComponentList = function isc_VisualBuilder_setComponentList() {
						var _1 = this.projectComponents, _2 = _1.data
								.getDescendants(_1.data.getRoot());
						if (this.componentMethodEditor)
							this.componentMethodEditor.allComponents = _2;
						if (this.componentAttributeEditor)
							this.componentAttributeEditor.allComponents = _2
					},
					isc.A.componentAdded = function isc_VisualBuilder_componentAdded() {
						this.setComponentList();
						this.markDirty()
					},
					isc.A.componentRemoved = function isc_VisualBuilder_componentRemoved(
							_1) {
						var _2 = this.getCurrentComponent();
						if (_2 == _1)
							this.clearComponent();
						this.setComponentList();
						this.markDirty()
					},
					isc.A.clearComponent = function isc_VisualBuilder_clearComponent() {
						if (this.componentAttributeEditor)
							this.componentAttributeEditor.clearComponent();
						if (this.componentMethodEditor)
							this.componentMethodEditor.clearComponent();
						if (this.leftStack) {
							this.leftStack.setSectionTitle(
									"componentProperties",
									"Component Properties");
							if (this.applyButton)
								this.applyButton.setDisabled(true);
							if (this.advancedButton)
								this.advancedButton.setDisabled(true)
						}
					},
					isc.A.switchComponentClass = function isc_VisualBuilder_switchComponentClass(
							_1) {
						var _2 = this.getCurrentComponent(), _3 = this.projectComponents.data, _4 = _3
								.getParent(_2), _5 = _3.getChildren(_4)
								.indexOf(_2);
						var _6 = this.projectComponents.makeEditNode({
							type : _1,
							defaults : _2.defaults
						});
						this.projectComponents.removeComponent(_2);
						_6 = this.projectComponents.addComponent(_6, _4, _5);
						if (_6 && _6.liveObject) {
							isc.EditContext.selectCanvasOrFormItem(
									_6.liveObject, true)
						}
					},
					isc.A.getCustomComponentsURL = function isc_VisualBuilder_getCustomComponentsURL() {
						return "customComponents.xml"
					},
					isc.A.refreshLibraryComponents = function isc_VisualBuilder_refreshLibraryComponents(
							_1) {
						var _2 = this.getScreenMockupMode(this.currentScreen);
						if (this.paletteDS.mockupMode != null
								&& _2 == this.paletteDS.mockupMode) {
							if (_1)
								_1();
							return

							

														

							

																					

							

														

							

						}
						this.paletteDS.mockupMode = _2;
						var _3 = this;
						this.libraryComponents.invalidateCache();
						if (!this.libraryComponents.willFetchData({}) && _1) {
							_1()
						}
						this.libraryComponents.fetchData({}, function() {
							var _4 = _3.libraryComponents.getData();
							_4.openFolders(_4.getChildren(_4.getRoot()));
							var _5 = _4.findAll("isClosed", "true");
							if (_5 && _5.length)
								_4.closeFolders(_5);
							if (_1)
								_1()
						});
						this.librarySearch.refresh()
					},
					isc.A.showDSRepoLoadUI = function isc_VisualBuilder_showDSRepoLoadUI() {
						if (!this.$113f)
							this.$113f = isc.DSRepo.create();
						var _1 = this;
						this.$113f.showLoadUI(null, function(_4) {
							if (_4 && _4.length > 0) {
								for (var i = 0; i < _4.length; i++) {
									var _3 = _4[i];
									if (_3 && _3.dsName) {
										_1.project.addDatasource(_3.dsName,
												_3.dsType)
									}
								}
							}
						})
					},
					isc.A.addDataSource = function isc_VisualBuilder_addDataSource(
							_1) {
						if (this.dsEditorWindow) {
							this.dsEditorWindow.hide();
							if (_1.serverType == "sql"
									|| _1.serverType == "hibernate") {
								if (this.dsWizard != null) {
									var _2 = this.dsWizard.dsTypeRecord, _3 = _2.wizardDefaults, _4 = _3 ? _3.existingTable == "true"
											: false;
									if (!_4) {
										var _5 = "http://"
												+ window.location.host
												+ "/tools/adminConsole.jsp";
										isc
												.say("To generate or regenerate SQL tables for this DataSource, use the <a target=_blank href='"
														+ _5
														+ "'>Admin Console</a> or the <i>DataSources</i> tab in the Developer Console")
									}
								}
							}
						}
						var _6 = _1.serviceNamespace ? "webService"
								: _1.serverType || _1.dataFormat;
						this.project.addDatasource(_1.ID, _6)
					},
					isc.A.clearScreenUI = function isc_VisualBuilder_clearScreenUI() {
						if (this.projectComponents) {
							this.projectComponents.destroyAll();
							if (!this.currentScreen.mockupMode
									&& this.initialComponent) {
								var _1 = this.projectComponents
										.makeEditNode(this.initialComponent);
								this.projectComponents.addNode(_1)
							}
						}
					},
					isc.A.updateSource = function isc_VisualBuilder_updateSource(
							_1) {
						if (this.showCodePane == false || !this.main)
							return;
						if (!_1 && this.main.getSelectedTabNumber() != 1)
							return;
						var _2 = this.getUpdatedSource();
						if (!_2)
							return;
						var _3 = new RegExp("(\\r\\n|[\\r\\n])", "g");
						_2 = _2.replace(_3, "\n");
						if (this.codePreview) {
							this.codePreview.setValues({
								codeField : _2
							})
						}
						if (this.jsCodePreview
								&& this.codePane.isDrawn()
								&& this.codePane.getSelectedTab().pane == this.jsCodePreview) {
							isc.xml.toJSCode(_2, this.getID()
									+ ".jsCodePreview.setContents(data)")
						}
					},
					isc.A.getUpdatedJSSource = function isc_VisualBuilder_getUpdatedJSSource(
							_1) {
						isc.xml.toJSCode(this.getUpdatedSource(), _1)
					},
					isc.A.getUpdatedSource = function isc_VisualBuilder_getUpdatedSource() {
						var _1 = this.getScreenMockupMode(this.currentScreen), _2 = {
							outputComponentsIndividually : !_1
						}, _3 = this.projectComponents
								.serializeAllEditNodes(_2);
						if (_1)
							_3 = "<MockupContainer>" + _3
									+ "</MockupContainer>";
						return _3
					},
					isc.A.downloadDataSource = function isc_VisualBuilder_downloadDataSource(
							_1) {
						var _2 = this;
						var _3 = this.downloadDataSourceDialogTitle
								.evalDynamicString(this, {
									dsID : _1.ID
								}), _4 = this.downloadDataSourceDialogPrompt, _5 = this.downloadDataSourceDialogButtonTitle;
						this.downloadDataSourceDialog = isc.TWindow
								.create({
									title : _3,
									width : 300,
									height : 160,
									isModal : true,
									showModalMask : true,
									autoCenter : true,
									padding : 8,
									items : [
											isc.Label.create({
												width : "100%",
												padding : 8,
												contents : _4
											}),
											isc.DynamicForm
													.create({
														width : "100%",
														numCols : 3,
														items : [
																{
																	name : "formatType",
																	title : "Format",
																	type : "select",
																	width : "*",
																	defaultValue : "XML",
																	valueMap : [
																			"XML",
																			"JavaScript" ]
																},
																{
																	name : "downloadButton",
																	title : _5,
																	type : "button",
																	startRow : false,
																	click : function() {
																		_2
																				.continueDSDownload(
																						_1,
																						this.form
																								.getValue("formatType"))
																	}
																} ]
													}) ]
								});
						this.downloadDataSourceDialog.show()
					},
					isc.A.continueDSDownload = function isc_VisualBuilder_continueDSDownload(
							_1, _2) {
						this.downloadDataSourceDialog.hide();
						this.downloadDataSourceDialog.markForDestroy();
						var _3 = this, _4 = _1.getClassName(), _5;
						if (isc.DS.isRegistered(_4)) {
							_5 = isc.DS.get(_4)
						} else {
							_5 = isc.DS.get("DataSource");
							_1._constructor = _4
						}
						var _6 = _5.xmlSerialize(_1);
						if (_2 == "XML") {
							_3.downloadDataSourceReply(_1, _2, _6)
						} else {
							isc.XMLTools.toJSCode(_6, function(_7) {
								_3.downloadDataSourceReply(_1, _2, _7.data)
							})
						}
					},
					isc.A.downloadDataSourceReply = function isc_VisualBuilder_downloadDataSourceReply(
							_1, _2, _3) {
						var _4 = _1.ID + ".ds." + (_2 == "XML" ? "xml" : "js"), _5 = (_2 == "XML" ? "text/xml"
								: "application/json");
						isc.DMI.callBuiltin({
							methodName : "downloadClientContent",
							arguments : [ _3, _4, _5 ],
							requestParams : {
								showPrompt : false,
								useXmlHttpRequest : false,
								timeout : 0
							}
						})
					},
					isc.A.showDSWizard = function isc_VisualBuilder_showDSWizard() {
						if (this.wizardWindow)
							return this.wizardWindow.show();
						var _1 = this;
						this.wizardWindow = isc.TWindow.create({
							title : "DataSource Wizard",
							autoCenter : true,
							width : "85%",
							height : "85%",
							builder : _1,
							closeClick : function() {
								this.Super("closeClick", arguments);
								_1.dsWizard.resetWizard()
							},
							items : [ _1.dsWizard = isc.DSWizard.create({
								callingBuilder : _1
							}) ]
						})
					},
					isc.A.showDSEditor = function isc_VisualBuilder_showDSEditor(
							_1, _2, _3) {
						var _4 = this, _5 = {
							target : _4,
							methodName : "addDataSource"
						}
						this.makeDSEditor();
						if (_2)
							this.dsEditor.editNew(_1, _5, _3);
						else
							this.dsEditor.editSaved(_1, _5, _3);
						this.dsEditor.knownDataSources = this.dataSourceList.data;
						this.dsEditorWindow.show()
					},
					isc.A.makeDSEditor = function isc_VisualBuilder_makeDSEditor() {
						if (this.dsEditorWindow)
							return;
						var _1 = this;
						if (!this.dsEditor) {
							this.dsEditor = isc.DataSourceEditor.create({
								dataSource : "DataSource",
								width : "100%",
								height : "80%",
								autoDraw : false,
								canAddChildSchema : true,
								canEditChildSchema : true,
								builder : _1,
								mainStackProperties : {
									_constructor : "TSectionStack"
								},
								instructionsProperties : {
									_constructor : "THTMLFlow"
								},
								mainEditorProperties : {
									_constructor : "TComponentEditor",
									formConstructor : isc.TComponentEditor
								},
								fieldLayoutProperties : {
									_constructor : "TLayout"
								},
								getUniqueDataSourceID : function() {
									var _2, i = 0;
									while (_2 == null) {
										_2 = "dataSource" + i;
										if (_1.dataSourceList.data.find("ID",
												_2)) {
											_2 = null;
											i++
										}
									}
									return _2
								}
							})
						}
						this.dsEditorWindow = isc.Window.create({
							title : "DataSource Editor",
							autoDraw : true,
							builder : this,
							isModal : true,
							autoCenter : true,
							width : "85%",
							height : "85%",
							canDragResize : true,
							items : [ this.dsEditor ]
						})
					},
					isc.A.hideRightMostResizeBar = function isc_VisualBuilder_hideRightMostResizeBar() {
						this.workspace
								.getMember(this.workspace.getMembers().length - 1).showResizeBar = false
					},
					isc.A.addOperation = function isc_VisualBuilder_addOperation() {
						if (!this.schemaWindow) {
							this.schemaWindow = isc.TWindow
									.create({
										title : this.schemaWindowTitle
												|| "Webservice Operations",
										autoCenter : true,
										autoDraw : false,
										width : Math.round(this.width * .6),
										height : Math.round(this.height * .9),
										items : [
												this
														.addAutoChild("schemaViewer"),
												this
														.addAutoChild("schemaViewerSelectButton") ]
									})
						}
						this.getOperationsTreeData(this.getID()
								+ ".addOperationReply(data)")
					},
					isc.A.addOperationReply = function isc_VisualBuilder_addOperationReply(
							_1) {
						this.schemaViewer.setData(isc.Tree.create({
							modelType : "children",
							root : _1,
							nameProperty : "name",
							childrenProperty : "children"
						}));
						this.schemaViewer.getData().openAll();
						this.schemaWindow.show()
					},
					isc.A.operationSelected = function isc_VisualBuilder_operationSelected() {
						var _1 = this.schemaViewer.data, _2 = this.schemaViewer
								.getSelectedRecord();
						if (_2 != null) {
							if (_2.serviceType == "service")
								_2 = _1.getChildren(_2)[0];
							if (_2.serviceType == "portType")
								_2 = _1.getChildren(_2)[0];
							var _3 = _1.getParent(_2);
							var _4 = _1.getParent(_3);
							var _5 = _2.location || _3.location || _4.location;
							var _6 = this.projectComponents.data;
							_5 = this.getOperationWSDLLocation(_5);
							var _7 = this;
							this.loadWebService(_5, _4.name, _3.name, _2.name)
						}
					},
					isc.A.getOperationWSDLLocation = function isc_VisualBuilder_getOperationWSDLLocation(
							_1) {
						return _1
					},
					isc.A.loadWebService = function isc_VisualBuilder_loadWebService(
							_1, _2, _3, _4) {
						var _5 = this;
						isc.xml.loadWSDL(_1, function(_6) {
							_5.newServiceLoaded(_6, _2, _3, _4, _1)
						}, null, true)
					},
					isc.A.newServiceLoaded = function isc_VisualBuilder_newServiceLoaded(
							_1, _2, _3, _4, _5) {
						var _6 = {
							operationName : _4,
							serviceNamespace : _1.serviceNamespace,
							serviceName : _1.name,
							serviceDescription : _2,
							portTypeName : _3,
							location : _5
						}
						this.addWebService(_1, _6);
						this.schemaWindow.hide()
					},
					isc.A.getOperationsTreeData = function isc_VisualBuilder_getOperationsTreeData() {
						var _1 = this.operationsPalette, _2 = _1 ? _1.data
								: null, _3 = _2 ? _2.getChildren(_2.getRoot())
								: null;
						return _3
					},
					isc.A.trimOperationsTreeData = function isc_VisualBuilder_trimOperationsTreeData(
							_1, _2) {
						if (!_1)
							return null;
						var _3 = _2 ? "message_in" : "message_out", _4 = isc
								.addProperties({}, _1), _5 = false;
						while (!_5) {
							var _6 = _4.find("serviceType", _3);
							if (_6) {
								_4.remove(_6)
							} else
								_5 = true
						}
						return _4
					},
					isc.A.addWebService = function isc_VisualBuilder_addWebService(
							_1, _2) {
						var _3 = {};
						_3.webService = _1;
						_3.inputSchema = _1.getRequestMessage(_2.operationName);
						_3.outputSchema = _1
								.getResponseMessage(_2.operationName);
						_2.inputSchema = _3.inputSchema;
						_2.outputSchema = _3.outputSchema;
						var _4 = this.addServiceOperation(_2);
						var _5 = this.getComplexOperationsPaletteTreeData(), _6 = "|"
								+ _2.serviceDescription
								+ "|"
								+ _2.portTypeName
								+ "|" + _2.operationName, _7;
						if (this.operationsPalette.getData()) {
							_7 = isc.Tree.create({
								root : this.operationsPalette.getData()
										.getRoot()
							})
						} else {
							_7 = isc.Tree.create({})
						}
						_5.pathDelim = "|";
						_7.pathDelim = "|";
						if (_7.find(_6)) {
							this
									.logWarn("Attempting to add webservice operation that is already in the tree");
							this.schemaWindow.hide();
							return

							

														

							

																					

							

														

							

						}
						var _8;
						if (_4) {
							_8 = {
								name : _2.operationName,
								serviceType : "operation",
								type : "IButton",
								defaults : {
									title : "Invoke " + _2.operationName,
									autoFit : true,
									click : {
										target : _4.liveObject.ID,
										name : "invoke",
										title : "Invoke " + _2.operationName
									}
								}
							}
						} else {
							_8 = {
								name : _2.operationName,
								serviceType : "operation",
								canDrag : false
							}
						}
						_6 = "|" + _2.serviceDescription + "|"
								+ _2.portTypeName;
						var _9 = _7.find(_6)
						var _10 = false;
						if (_9) {
							_7.add(_8, _9);
							_10 = true
						} else {
							_6 = "|" + _2.serviceDescription
							var _9 = _7.find(_6)
							var _10 = false;
							if (_9) {
								_7.add({
									name : _2.portTypeName,
									serviceType : "portType",
									canDrag : false,
									children : [ _8 ]
								}, _9);
								_10 = true
							} else {
								var _11 = {
									name : _2.serviceDescription,
									serviceType : "service",
									canDrag : false,
									children : [ {
										name : _2.portTypeName,
										serviceType : "portType",
										canDrag : false,
										children : [ _8 ]
									} ]
								}
							}
							_5.children.add(_11);
							this.operationsPalette.setData(isc.Tree.create({
								modelType : "children",
								root : _5,
								nameProperty : "name",
								childrenProperty : "children"
							}))
						}
						this.operationsPalette.setData(_7);
						var _6 = "|" + _2.serviceDescription + "|"
								+ _2.portTypeName + "|" + _2.operationName, _9 = _7
								.find(_6);
						var _12 = {
							palette : this.operationsPalette,
							isOutput : false,
							operation : _2.operationName,
							serviceName : _2.serviceName,
							serviceNamespace : _2.serviceNamespace
						};
						if (_3.inputSchema) {
							this.setSchema(_3.inputSchema, _9, null, "", _12)
						}
						if (_3.outputSchema) {
							_12.isOutput = true;
							this.setSchema(_3.outputSchema, _9, null, "", _12)
						}
						this.operationsPalette.getData().openAll()
					});
	isc.evalBoundary;
	isc.B
			.push(
					isc.A.addServiceOperation = function isc_VisualBuilder_addServiceOperation(
							_1) {
						var _2, _3;
						if (_1.inputSchema) {
							_2 = {
								type : "ValuesManager",
								defaults : {
									parentProperty : "inputVM",
									dataSource : _1.inputSchema.ID,
									serviceName : _1.serviceName,
									serviceNamespace : _1.serviceNamespace
								}
							}
						}
						if (_1.outputSchema) {
							_3 = {
								type : "ValuesManager",
								defaults : {
									parentProperty : "outputVM",
									dataSource : _1.outputSchema.ID,
									serviceName : _1.serviceName,
									serviceNamespace : _1.serviceNamespace
								}
							}
						}
						var _4 = {
							type : "ServiceOperation",
							defaults : {
								operationName : _1.operationName,
								serviceNamespace : _1.serviceNamespace,
								serviceName : _1.serviceName,
								serviceDescription : _1.serviceDescription,
								portTypeName : _1.portTypeName,
								location : _1.location
							}
						};
						var _5 = this.projectComponents;
						var _6 = _5.makeEditNode(_4);
						_5.addComponent(_6);
						if (_2)
							_5.addComponent(_5.makeEditNode(_2), _6, null,
									"inputVM");
						if (_3)
							_5.addComponent(_5.makeEditNode(_3), _6, null,
									"outputVM");
						return _6
					},
					isc.A.shouldShowDataPathFields = function isc_VisualBuilder_shouldShowDataPathFields() {
						return this.operationsPalette ? true : false
					},
					isc.A.getComplexOperationsPaletteTreeData = function isc_VisualBuilder_getComplexOperationsPaletteTreeData() {
						if (!this.operationsPalette
								|| !this.operationsPalette.data)
							return {
								children : []
							};
						var _1 = this.operationsPalette.data, _2 = _1
								.getChildren(_1.getRoot());
						return {
							children : _2
						}
					},
					isc.A.setSchema = function isc_VisualBuilder_setSchema(_1,
							_2, _3, _4, _5) {
						var _6 = _5.populateTarget
								&& this.targetContext != null;
						var _7 = _1.getFieldNames();
						for (var i = 0; i < _7.length; i++) {
							var _9 = _7[i], _10 = _1.getField(_9), _11 = _1
									.fieldIsComplexType(_9), _12;
							var _13 = this.getFieldPaletteNode(_1, _9, _4, _5);
							var _14 = _5.palette.data;
							_14.add(_13, _2 || _14.getRoot());
							if (_6
									&& _3
									&& _3.type == this.complexTypeNodeConstructor) {
								var _15 = null;
								if (_11) {
									var _16 = {};
									isc.addProperties(_16,
											this.canvasItemWrapperDefaults);
									isc.addProperties(_16,
											this.canvasItemWrapperProperties);
									_15 = _5.palette
											.makeEditNode({
												type : this.canvasItemWrapperConstructor,
												defaults : _16
											});
									this.targetContext.addNode(_15, _3)
								}
								var _17 = _5.palette.makeEditNode(_13);
								this.targetContext.addNode(_17, _15 || _3);
								if (isc.EditContext)
									isc.EditContext.clearSchemaProperties(_17)
							}
							if (_11) {
								var _18 = _1.getSchema(_10.type);
								this.setSchema(_18, _13, _17, (_4 ? _4 + "/"
										: "")
										+ _9, _5)
							}
						}
					},
					isc.A.getFieldPaletteNode = function isc_VisualBuilder_getFieldPaletteNode(
							_1, _2, _3, _4) {
						var _5 = _1.fieldIsComplexType(_2), _6 = _1
								.getField(_2), _7 = _4.isOutput, _8 = _4.displayOnly != null ? _4.displayOnly
								: _7, _9 = this.getSchemaDataSourceIDs(
								_4.operation, _4.serviceName,
								_4.serviceNamespace), _10 = {
							schemaDataSource : _7 ? _9.output : _9.input,
							serviceNamespace : _4.serviceNamespace,
							serviceName : _4.serviceName,
							isComplexType : _5,
							type : _5 ? "complexType" : "simpleType"
						}, _11 = isc.DataSource.getAutoTitle(_2), _12 = {
							name : _2,
							defaults : _10
						}, _13 = (_3 ? _3 + "/" : "") + _2;
						_10.dataPath = _13;
						if (_8) {
							_10.inputDataPath = _13;
							_10.inputSchemaDataSource = _10.schemaDataSource;
							_10.inputServiceNamespace = _10.serviceNamespace;
							_10.inputServiceName = _10.serviceName
						}
						if (_8)
							_10.canEdit = false;
						var _14 = _6.xmlMaxOccurs;
						if (_14 == "unbounded")
							_14 = 1000;
						if (!_5) {
							_12 = this.getSimpleTypeNode(_12, _8, _11)
						} else {
							_10.dataSource = _10.schemaDataSource;
							delete _10.schemaDataSource;
							if (_14 == null || _14 <= 1) {
								_12 = this.getComplexTypeNode(_12, _8, _11)
							} else {
								_12 = this.getRepeatingComplexTypeNode(_12,
										_14, _8, _1, _6.type, _11)
							}
						}
						_12.title = _10.title;
						_12.type = _10.type;
						return _12
					},
					isc.A.getSimpleTypeNode = function isc_VisualBuilder_getSimpleTypeNode(
							_1, _2, _3) {
						_1.type = this.simpleTypeNodeConstructor;
						_1.defaults.title = _3;
						isc.addProperties(_1.defaults,
								this.simpleTypeNodeDefaults);
						isc.addProperties(_1.defaults,
								this.simpleTypeNodeProperties);
						return _1
					},
					isc.A.getComplexTypeNode = function isc_VisualBuilder_getComplexTypeNode(
							_1, _2, _3) {
						_1.type = this.complexTypeNodeConstructor;
						delete _1.defaults.dataPath;
						delete _1.defaults.inputDataPath;
						_1.defaults.groupTitle = _3;
						isc.addProperties(_1.defaults,
								this.complexTypeNodeDefaults);
						isc.addProperties(_1.defaults,
								this.complexTypeNodeProperties);
						return _1
					},
					isc.A.getRepeatingComplexTypeNode = function isc_VisualBuilder_getRepeatingComplexTypeNode(
							_1, _2, _3, _4, _5, _6) {
						if (_2 < 5 && _3) {
							_1.type = "DetailViewer"
						} else {
							_1.type = (_3 ? "ListGrid" : "LineEditor")
						}
						var _7 = _4.getSchema(_5);
						var _8 = _3 ? "inputDataPath" : "dataPath";
						if (_3) {
							_1.defaults.height = 80;
							_1.defaults.autoFitMaxRecords = 10;
							_1.defaults.canDragSelectText = true
						} else {
							_1.defaults.saveLocally = true
						}
						var _9 = _7.getFlattenedFields(null,
								_1.defaults.dataPath, _8);
						_9 = isc.getValues(_9);
						_9 = isc.applyMask(_9, [ "name", "title", "dataPath",
								"inputDataPath" ]);
						_1.defaults.defaultFields = _9;
						isc.addProperties(_1.defaults,
								this.repeatingComplexTypeNodeDefaults);
						isc.addProperties(_1.defaults,
								this.repeatingComplexTypeNodeProperties);
						return _1
					},
					isc.A.getSchemaDataSourceIDs = function isc_VisualBuilder_getSchemaDataSourceIDs(
							_1, _2, _3) {
						var _4 = {};
						var _5 = isc.ServiceOperation.getServiceOperation(_1,
								_2, _3);
						if (_5) {
							if (_5.inputVM) {
								_4.input = isc.DataSource
										.get(_5.inputVM.dataSource).ID
							}
							if (_5.outputVM) {
								_4.output = isc.DataSource
										.get(_5.outputVM.dataSource).ID
							}
						}
						return _4
					});
	isc.B._maxIndex = isc.C + 103;
	isc.defineClass("ActionMenu", "Menu");
	isc.A = isc.ActionMenu.getPrototype();
	isc.B = isc._allFuncs;
	isc.C = isc.B._maxIndex;
	isc.D = isc._funcClasses;
	isc.D[isc.C] = isc.A.Class;
	isc.A.$50a = [ "string", "number", "boolean", "object", "array" ];
	isc.B
			.push(
					isc.A.initWidget = function isc_ActionMenu_initWidget() {
						this.setComponents(this.components);
						this.Super("initWidget", arguments)
					},
					isc.A.draw = function isc_ActionMenu_draw() {
						if (!this.$126w) {
							isc.SelectionOutline.hideDragHandle();
							this.$126w = isc.SelectionOutline
									.getSelectedObject()
						}
						return this.Super("draw", arguments)
					},
					isc.A.hide = function isc_ActionMenu_hide() {
						if (this.$126w) {
							isc.SelectionOutline.select(this.$126w);
							isc.SelectionOutline.showDragHandle();
							delete this.$126w
						}
						return this.Super("hide", arguments)
					},
					isc.A.setComponents = function isc_ActionMenu_setComponents(
							_1) {
						var _2 = [];
						if (!_1)
							_1 = [];
						for (var i = 0; i < _1.length; i++) {
							var _4 = _1[i], _5 = {
								component : _4,
								icon : _4.icon,
								title : _4.liveObject.getActionTargetTitle ? _4.liveObject
										.getActionTargetTitle()
										: _4.ID + " (" + _4.type + ")"
							};
							var _6 = isc.jsdoc.getActions(_4.type);
							if (_6) {
								_5.submenu = this.getActionsMenu(_4, _6);
								_2.add(_5)
							}
						}
						_2.add({
							title : "[None]",
							click : this.getID() + ".clearAction()",
							icon : "[SKINIMG]/actions/cancel.png"
						})
						this.setData(_2)
					},
					isc.A.rowOver = function isc_ActionMenu_rowOver(_1) {
						this.Super("rowOver", arguments);
						var _2 = _1.component;
						if (_2 && _2.liveObject)
							isc.SelectionOutline.select(_2.liveObject);
						else
							isc.SelectionOutline.deselect();
						this.bringToFront()
					},
					isc.A.getActionsMenu = function isc_ActionMenu_getActionsMenu(
							_1, _2) {
						var _3 = [];
						for (var i = 0; i < _2.length; i++) {
							var _5 = _2[i], _6 = {
								title : _5.title,
								icon : _5.icon,
								component : _1,
								targetAction : _5,
								click : this.getID()
										+ ".bindAction(item.component, item.targetAction)"
							}
							_3.add(_6)
						}
						return _3
					},
					isc.A.getInheritedMethod = function isc_ActionMenu_getInheritedMethod(
							_1, _2) {
						while (_1) {
							var _3 = isc.jsdoc.getDocItem("method:" + _1 + "."
									+ _2);
							if (_3 != null)
								return _3;
							var _4 = isc.DS.get(_1);
							if (_4 && _4.methods) {
								var _5 = _4.methods.find("name", _2);
								if (_5)
									return _5
							}
							var _6 = isc.ClassFactory.getClass(_1);
							if (_6 == null)
								return null;
							_6 = _6.getSuperClass();
							if (_6 == null)
								return null;
							_1 = _6.getClassName()
						}
					},
					isc.A.bindAction = function isc_ActionMenu_bindAction(_1,
							_2) {
						var _3 = this.sourceComponent, _4 = this
								.getInheritedMethod(_3.type, this.sourceMethod), _5 = isc.isAn
								.XMLNode(_4) ? isc.jsdoc.toJS(_4) : _4;
						if (this.logIsDebugEnabled("actionBinding")) {
							this.logDebug("bindAction: component " + _1.ID
									+ ", sourceMethod: " + this.echoFull(_5)
									+ ", action method: " + this.echoFull(_2),
									"actionBinding")
						}
						var _6 = {
							title : _2.title,
							target : _1.ID,
							name : _2.name
						};
						var _7;
						if (_2.params) {
							var _8 = [], _9 = false;
							_7 = _5.params;
							if (!_7)
								_7 = [];
							else if (!isc.isAn.Array(_7))
								_7 = [ _7 ];
							else
								_7 = _7.duplicate();
							_7.add({
								name : "this",
								type : this.sourceComponent.type
							});
							for (var i = 0; i < _2.params.length; i++) {
								var _11 = _2.params[i];
								this.logInfo("considering actionMethod "
										+ _2.name + " param: " + _11.name
										+ " of type " + _11.type,
										"actionBinding");
								var _12 = _11.optional != null
										&& _11.optional.toString() != "false";
								if (!_12
										|| _11.type != null
										&& !this.$50a.contains(_11.type
												.toLowerCase())) {
									var _13 = this.getMatchingSourceParam(_11,
											_7);
									if (_13 != null) {
										_8[i] = _13.name;
										_13.$480 = true;
										_9 = true;
										continue
									} else if (!_12) {
										this
												.logInfo(
														"action binding failed, actionMethod param "
																+ _11.name
																+ " of type "
																+ _11.type
																+ " couldn't be fulfilled",
														"actionBinding");
										isc
												.say("Visual Builder couldn't find an automatic binding from event "
														+ _5.name
														+ " to action "
														+ (_2.title || _2.name));
										return null
									}
								}
								_8[i] = "null"
							}
							if (_9)
								_6.mapping = _8
						}
						if (this.logIsInfoEnabled("actionBinding")) {
							this.logWarn("generated binding: "
									+ this.echoFull(_6), "actionBinding")
						}
						if (_7)
							_7.setProperty("$480", null);
						this.bindingComplete(_6)
					},
					isc.A.bindingComplete = function isc_ActionMenu_bindingComplete(
							_1) {
					},
					isc.A.clearAction = function isc_ActionMenu_clearAction() {
						var _1 = null;
						this.bindingComplete(_1)
					},
					isc.A.getMatchingSourceParam = function isc_ActionMenu_getMatchingSourceParam(
							_1, _2) {
						var _3 = this.getFirstType(_1.type);
						var _4 = isc.DS.get(_3);
						this.logInfo("selected type " + _3 + " has schema: "
								+ _4, "actionBinding");
						for (var i = 0; i < _2.length; i++) {
							var _6 = _2[i];
							if (_6.$480)
								continue;
							this.logDebug("considering source param: "
									+ _6.name + " of type " + _6.type,
									"actionBinding");
							var _7 = this.getFirstType(_6.type);
							var _8 = isc.DS.get(_7);
							if (!_8) {
								if (_3 == _7)
									return _6;
								continue
							}
							if (_8.inheritsSchema(_4)) {
								return _6
							}
						}
					},
					isc.A.getFirstType = function isc_ActionMenu_getFirstType(
							_1) {
						_1 = _1.split(/[ \t]+/)[0];
						_1 = _1.substring(0, 1).toUpperCase() + _1.substring(1);
						return _1
					});
	isc.B._maxIndex = isc.C + 12;
	isc.A = isc.jsdoc;
	isc.B = isc._allFuncs;
	isc.C = isc.B._maxIndex;
	isc.D = isc._funcClasses;
	isc.D[isc.C] = isc.A.Class;
	isc.B.push(isc.A.getActions = function isc_c_jsdoc_getActions(_1) {
		var _2 = isc.DS.get(_1);
		if (_2 == null)
			return null;
		var _3;
		while (_2 != null) {
			var _4 = _2.methods ? _2.methods.findAll("action", true) : null;
			if (_4 == null) {
				if (_2.showSuperClassActions == false)
					break;
				_2 = _2.superDS();
				continue
			}
			for (var i = 0; i < _4.length; i++) {
				var _6 = _4[i], _7 = isc.jsdoc.getDocItem("method:" + _2.ID
						+ "." + _6.name), _8 = _7 ? isc.jsdoc.toJS(_7) : _6;
				if (_3 == null)
					_3 = [];
				_3.add(isc.addProperties({}, _8, _6));
				var _9 = _3[i].params;
				if (_9 != null && !isc.isAn.Array(_9))
					_3[i].params = [ _9 ]
			}
			if (_2.showSuperClassActions == false)
				break;
			_2 = _2.superDS()
		}
		return _3
	});
	isc.B._maxIndex = isc.C + 1;
	isc.defineClass("GridSearch", "DynamicForm");
	isc.A = isc.GridSearch.getPrototype();
	isc.B = isc._allFuncs;
	isc.C = isc.B._maxIndex;
	isc.D = isc._funcClasses;
	isc.D[isc.C] = isc.A.Class;
	isc.A.browserSpellCheck = false;
	isc.A.height = 20;
	isc.A.numCols = 2;
	isc.A.cellPadding = 0;
	isc.A.colWidths = [ 46, 200 ];
	isc.A.titleSuffix = ":&nbsp;";
	isc.A.showSearchTitle = false;
	isc.A.wrapItemTitles = false;
	isc.A.selectOnFocus = true;
	isc.A.hint = "Find...";
	isc.A.searchTitle = "<span style='color:#FFFFFF'>Search</span>";
	isc.B
			.push(
					isc.A.initWidget = function isc_GridSearch_initWidget() {
						this.items = [ isc.addProperties({
							name : "search",
							width : "*",
							colSpan : "*",
							showTitle : this.showSearchTitle,
							editorType : "TTextItem",
							selectOnFocus : true,
							title : this.searchTitle,
							showHintInField : true,
							hint : this.hint,
							changed : "form.findNode()",
							keyPress : function(_1, _2, _3) {
								if (_3 == "Enter")
									_2.findNode();
								if (_3 == "Escape") {
									_2.revertState();
									return false
								}
							}
						}, this.searchItemProperties) ];
						this.Super("initWidget", arguments);
						if (this.grid)
							this.setGrid(this.grid)
					},
					isc.A.setGrid = function isc_GridSearch_setGrid(_1) {
						this.grid = _1;
						this.defaultSearchProperty();
						if (isc.isA.TreeGrid(_1)) {
							if (_1.$84w)
								_1.getNodeTitle = _1.$84w;
							_1.$84w = _1.getNodeTitle;
							_1.getNodeTitle = function(_5, _6, _7) {
								var _2 = _1.$84w(_5, _6, _7);
								if (_5.$826) {
									var _3, _4;
									if (_2.match(/<.*>/)) {
										_4 = new RegExp("(^|>)([^<]*?)("
												+ _5.$826 + ")", "ig");
										_3 = _2
												.replace(_4,
														"$1$2<span style='background-color:#FF0000;'>$3</span>")
									} else {
										_4 = new RegExp("(" + _5.$826 + ")",
												"ig");
										_3 = _2
												.replace(_4,
														"<span style='background-color:#FF0000;'>$1</span>")
									}
									_2 = _3
								}
								return _2
							}
						} else {
							if (_1.$84x)
								_1.formatCellValue = _1.$84x;
							_1.formatCellValue = function(_2, _5, _6, _7) {
								if (_1.$84x) {
									_2 = _1.$84x(_2, _5, _6, _7)
								}
								if (_2 != null && _5.$826) {
									var _3, _4;
									if (_2.match(/<.*>/)) {
										_4 = new RegExp("(^|>)([^<]*?)("
												+ _5.$826 + ")", "ig");
										_3 = _2
												.replace(_4,
														"$1$2<span style='background-color:#FF0000;'>$3</span>")
									} else {
										_4 = new RegExp("(" + _5.$826 + ")",
												"ig");
										_3 = _2
												.replace(_4,
														"<span style='background-color:#FF0000;'>$1</span>")
									}
									_2 = _3
								}
								return _2
							}
						}
					},
					isc.A.defaultSearchProperty = function isc_GridSearch_defaultSearchProperty() {
						if (!this.searchProperty && this.grid) {
							if (isc.isA.TreeGrid(this.grid)) {
								this.searchProperty = this.grid.getTitleField()
							} else {
								this.searchProperty = this.grid.getFieldName(0)
							}
						}
					},
					isc.A.revertState = function isc_GridSearch_revertState() {
						var _1 = this.grid;
						if (this.$49d) {
							delete this.$49d.$826;
							_1.refreshRow(_1.getRecordIndex(this.$49d))
						}
						this.$49c = this.$49d = null;
						if (this.$827) {
							for (var i = 0; i < this.$827.length; i++)
								_1.data.closeFolder(this.$827[i])
						}
						this.$827 = null;
						this.clearValue("search")
					},
					isc.A.findNode = function isc_GridSearch_findNode() {
						if (!this.grid || !this.grid.getData())
							return;
						var _1 = this.getValue("search");
						if (_1 == null) {
							this.revertState();
							return

							

														

							

																					

							

														

							

						}
						_1 = _1.toLowerCase();
						var _2 = this.$49c == _1 && this.$49d;
						this.$49c = _1;
						var _3 = this.grid;
						var _4 = isc.isA.TreeGrid(_3) ? _3.data.getAllNodes()
								: _3.getData();
						var _5 = this.$49d ? _4.indexOf(this.$49d) : 0;
						if (_2)
							_5++;
						if (this.$49d) {
							delete this.$49d.$826;
							_3.refreshRow(_3.getRecordIndex(this.$49d));
							this.$49d = null
						}
						var _6 = this.findNext(_4, _5, _1);
						if (!_6)
							_6 = this.findNext(_4, 0, _1);
						if (_6) {
							this.$49d = _6;
							_6.$826 = _1;
							if (this.$827) {
								for (var i = 0; i < this.$827.length; i++)
									_3.data.closeFolder(this.$827[i])
							}
							this.$827 = null;
							if (isc.isA.TreeGrid(_3)) {
								var _8 = _3.data.getParents(_6);
								this.$827 = [];
								for (var i = 0; i < _8.length; i++) {
									var _9 = _8[i];
									if (!_3.data.isOpen(_9)) {
										this.$827.add(_9);
										_3.data.openFolder(_9)
									}
								}
								if (_3.data.isFolder(_6) && !_3.data.isOpen(_6)) {
									_3.data.openFolder(_6);
									this.$827.add(_6)
								}
							}
							var _10 = _3.getRecordIndex(_6);
							_3.refreshRow(_10)
							_3.scrollRecordIntoView(_10)
						}
					}, isc.A.findNext = function isc_GridSearch_findNext(_1,
							_2, _3) {
						for (var i = _2; i < _1.getLength(); i++) {
							var _5 = _1.get(i);
							if (_5[this.searchProperty]
									&& _5[this.searchProperty].toLowerCase()
											.contains(_3)) {
								return _5
							}
						}
					});
	isc.B._maxIndex = isc.C + 6;
	isc.DataSource.create({
		allowAdvancedCriteria : true,
		ID : "SCUploadSaveFile",
		addGlobalId : false,
		operationBindings : [ {
			operationType : "custom",
			operationId : "checkUploadFeature"
		} ],
		fields : [ {
			hidden : true,
			primaryKey : true,
			name : "path"
		}, {
			hidden : true,
			name : "lastChangeDate"
		}, {
			name : "file",
			type : "binary"
		} ]
	})
	isc.defineClass("MockupImporter");
	isc.A = isc.MockupImporter;
	isc.B = isc._allFuncs;
	isc.C = isc.B._maxIndex;
	isc.D = isc._funcClasses;
	isc.D[isc.C] = isc.A.Class;
	isc.B.push(isc.A.$970 = function isc_c_MockupImporter__isValidID(_1) {
		return (_1.match(isc.MockupImporter.$971) != null)
	}, isc.A.$972 = function isc_c_MockupImporter__createStrRegexp(_1) {
		return _1 + "(?:[^\\" + _1 + "]|\\.)*?" + _1
	}, isc.A.$973 = function isc_c_MockupImporter__createKeyValueRegexp(_1, _2,
			_3) {
		return "\\s*" + _1 + "\\s*" + _2 + "\\s*" + _3 + "\\s*"
	}, isc.A.$974 = function isc_c_MockupImporter__createEntryRegexp(_1) {
		var _2 = "(" + isc.MockupImporter.$972("'") + ")", _3 = "("
				+ isc.MockupImporter.$972("\"") + ")", _4 = "([^;\\s" + _1
				+ "](?:[^;" + _1 + "]*[^;\\s" + _1 + "])?)", _5 = "(?:" + _2
				+ "|" + _3 + "|" + _4 + ")", _6 = isc.MockupImporter.$973(_5,
				_1, _5), _7 = "^(?:(?:" + _6 + ")|([^;]*))";
		return _7
	}, isc.A.$975 = function isc_c_MockupImporter__parseCustomProperties(_1) {
		var _2 = function(_1, _15) {
			var _3 = [], _4 = [], _5 = [];
			for (var j = 0, _7 = _1.length; j < _7; ++j) {
				var s = _1.substring(j);
				var _9 = s.match(_15);
				if (_9[7] != null) {
					var _10 = _9[7].toString().trim();
					if (_10 != "") {
						_5.push(_10)
					}
				} else {
					var _11, _12;
					if (_9[1] != null)
						_11 = eval(_9[1].toString());
					else if (_9[2] != null)
						_11 = eval(_9[2].toString());
					else
						_11 = _9[3].toString();
					if (_9[4] != null)
						_12 = eval(_9[4].toString());
					else if (_9[5] != null)
						_12 = eval(_9[5].toString());
					else
						_12 = _9[6].toString();
					_3.push(_11);
					_4.push(_12)
				}
				j += _9[0].toString().length
			}
			return {
				keys : _3,
				values : _4,
				errors : _5
			}
		};
		var _13 = _2(_1, isc.MockupImporter.$976), _14 = _2(_1,
				isc.MockupImporter.$977);
		return (_13.errors.length <= _14.errors.length ? _13 : _14)
	});
	isc.B._maxIndex = isc.C + 5;
	isc.A = isc.MockupImporter;
	isc.A.$971 = new RegExp("^[a-zA-Z_$][a-zA-Z0-9_$]*$");
	isc.A.$976 = new RegExp(isc.MockupImporter.$974(":"));
	isc.A.$977 = new RegExp(isc.MockupImporter.$974("="));
	isc.A = isc.MockupImporter.getPrototype();
	isc.A.transformRules = isc.Page.combineURLs(isc.Page.getIsomorphicDir(),
			"../tools/visualBuilder/balsamiqTransformRules.js");
	isc.A.useLayoutHeuristics = true;
	isc.A.sloppyEdgeControlOverflow = 10;
	isc.A.maxControlOverlap = 20;
	isc.A.stackContainerFillInset = 20;
	isc.A.labelMaxOffset = 10;
	isc.A.dropExtraProperties = true;
	isc.A.allowedExtraProperties = [];
	isc.A.tallFormItems = [ "TextAreaItem", "RadioGroupItem", "SpacerItem",
			"ButtonItem" ];
	isc.A.ignoreWidthFormItems = [ "DateItem", "StaticTextItem" ];
	isc.A.dropMarkup = true;
	isc.A.trimSpace = true;
	isc.A.fillSpace = true;
	isc.A.trimWhitespace = true;
	isc.A.formsGridCellWidth = 5;
	isc.A.formsGridCellHeight = 22;
	isc.A.maxOuterControlsDistance = 50;
	isc.A.stackFlexMaxSizeMatch = 10;
	isc.A.formExtraSpaceThreshold = 15;
	isc.A.formExtraWidthThreshold = 30;
	isc.A.defaultButtonSize = 27;
	isc.A.buttonMinimumChangeSize = 3;
	isc.A.$87q = [ "HStack", "HLayout", "VStack", "VLayout" ];
	isc.A.$87t = [ "ButtonItem", "CheckboxItem", "RadioItem" ];
	isc.A.$878 = {};
	isc.A.$90r = [];
	isc.A.fieldNamingConvention = "camelCaps";
	isc.A.warnings = "";
	isc.A = isc.MockupImporter.getPrototype();
	isc.B = isc._allFuncs;
	isc.C = isc.B._maxIndex;
	isc.D = isc._funcClasses;
	isc.D[isc.C] = isc.A.Class;
	isc.A.$85p = [];
	isc.A.$z = false;
	isc.B
			.push(
					isc.A.init = function isc_MockupImporter_init() {
						var _1 = this;
						isc.FL
								.loadJSFiles(
										this.transformRules,
										function() {
											_1.$z = true;
											var _2 = window.transformRules;
											if (_2 == null) {
												isc
														.logWarn("The MockupImporter could not find window.transformRules.");
												_2 = {
													classTranslations : {},
													propertyTranslations : {},
													formItems : [],
													markupItems : [],
													widgetPropertyTranslations : {}
												}
											}
											_1.$98p = _2;
											_2.mockupImporter = _1;
											for (var i = 0; i < _1.$85p.length; i++) {
												var _4 = _1.$85p[i];
												if (_4.xml)
													_1.bmmlToXml(_4.xml,
															_4.callback);
												else
													_1.reifyComponentXml(
															_4.componentXml,
															_4.callback)
											}
											;
											_1.$85p.clear();
											delete _1.$85p
										})
					},
					isc.A.bmmlToXml = function isc_MockupImporter_bmmlToXml(_1,
							_2) {
						if (!this.$z) {
							this.$85p.add({
								xml : _1,
								callback : _2
							})
						} else {
							this.warnings = "";
							this.$98y(_1, _2)
						}
					},
					isc.A.$98y = function isc_MockupImporter__bmmlToXml(_1, _2,
							_3) {
						var _4 = _3;
						if (_4 == null) {
							_4 = this.mockupPath
						} else {
							this.dropMarkup = true
						}
						if (this.$878[_4] == null) {
							this.$878[_4] = {
								widgets : []
							}
						}
						var _5 = isc.XMLTools.toJS(isc.XMLTools.parseXML(_1));
						var _6 = this.$873(_5, _4);
						if (_4 == this.mockupPath && !this.dropMarkup) {
							this.$978 = []
						}
						for (var i = 0; i < _6.length; i++) {
							if (_6[i]._constructor == "MockupElement"
									&& _6[i].controlName != "com.balsamiq.mockups::HSplitter"
									&& _6[i].controlName != "com.balsamiq.mockups::VSplitter") {
								if (_4 == this.mockupPath && !this.dropMarkup) {
									this.$978.add(isc.clone(_6[i]))
								}
								_6.removeAt(i);
								i--
							} else if (this.dropMarkup
									&& _6[i].specialProperties
									&& _6[i].specialProperties.markup) {
								_6.removeAt(i);
								i--
							}
						}
						var _8 = this;
						this.$874(_6, function(_11) {
							var _9 = _8.$98p;
							if (_11 == null) {
								_2(null);
								return

								

																

								

																								

								

																

								

							}
							_8.adjustLayoutPosition(_11);
							if (_4 == _8.mockupPath && _8.$978) {
								var _10 = _8.$979(_11);
								if (_10.length == 0) {
									_11.addList(_8.$978);
									delete _8.$978
								}
							}
							if (_8.useLayoutHeuristics) {
								_11 = _8.processHeuristics(_11)
							}
							_8.postProcessLayout(_11);
							_8.$878[_4].layout = _11;
							_8.$879(_11, _3, _2)
						})
					},
					isc.A.reifyComponentXml = function isc_MockupImporter_reifyComponentXml(
							_1, _2) {
						if (!this.$z) {
							this.$85p.add({
								componentXml : _1,
								callback : _2
							})
						} else {
							this.warnings = "";
							var _3 = this;
							isc.DMI.callBuiltin({
								methodName : "xmlToJS",
								arguments : [ _1 ],
								callback : function(_4, _5) {
									_3.$150t(_5, _2)
								}
							})
						}
					},
					isc.A.$150t = function isc_MockupImporter__reifyComponentXml(
							_1, _2) {
						var _3 = this, _4 = isc.EditContext.create();
						_4
								.getPaletteNodesFromJS(
										_1,
										function(_14) {
											var _5 = (_14.length == 1
													&& _14[0].defaults.children ? _14[0].defaults.children
													: _14);
											var _6 = _3.$98p, _7 = _6.classTranslations;
											for (var i = 0; i < _5.length; i++) {
												var _9 = _5[i];
												if (_9._constructor == "DynamicForm"
														&& _9.fields.length == 1) {
													var _10 = _9.fields[0], _11 = _3
															.getBalsamiqControlNameForSCControl(_10._constructor);
													if (!_9.specialProperties)
														_9.specialProperties = {};
													_9.specialProperties.controlName = _11;
													_9.specialProperties.markup = false;
													_9.fields[0].specialProperties = isc
															.shallowClone(_9.specialProperties)
												}
												if (!_9.specialProperties
														|| !_9.specialProperties.controlName) {
													var _11 = _3
															.getBalsamiqControlNameForSCControl(_9._constructor);
													if (_11) {
														if (!_9.specialProperties)
															_9.specialProperties = {};
														_9.specialProperties.controlName = _11
													}
												}
												if (_9.zIndex == null)
													_9.zIndex = 100000
											}
											var _12 = _5;
											_3.adjustLayoutPosition(_12);
											if (_3.useLayoutHeuristics) {
												_12 = _3.processHeuristics(_12)
											}
											_3.postProcessLayout(_12);
											if (_2) {
												var _13 = isc.EditContext
														.serializeDefaults(_3
																.$98a(_12));
												_2(_13.replace(/\r/g, "\n"),
														[], _3.$134x(_12))
											}
										}, [ isc.RPC.ALL_GLOBALS ])
					},
					isc.A.adjustLayoutPosition = function isc_MockupImporter_adjustLayoutPosition(
							_1) {
						if (this.trimSpace) {
							var _2 = 10000;
							var _3 = 10000;
							for (var i = 0; i < _1.length; i++) {
								if (_1[i].left != null && _1[i].top != null) {
									_2 = Math.min(_2, _1[i].left);
									_3 = Math.min(_3, _1[i].top)
								}
							}
							for (var i = 0; i < _1.length; i++) {
								if (_1[i].left != null && _1[i].top != null) {
									_1[i].left -= _2;
									_1[i].top -= _3
								}
							}
						}
					},
					isc.A.postProcessLayout = function isc_MockupImporter_postProcessLayout(
							_1) {
						var _2 = this.$98p;
						for (var i = 0; i < _1.length; i++) {
							var _4 = _1[i];
							var _5 = _4.specialProperties;
							if (_5 != null
									&& (_5.overrideWidth || _5.overrideHeight)) {
								if (_5.overrideWidth) {
									if (_4._constructor == "DynamicForm") {
										_4.width = "100%"
									} else {
										_4.width = "*"
									}
								}
								if (_5.overrideHeight) {
									if (_4._constructor == "DynamicForm") {
										_4.height = "100%"
									} else {
										_4.height = "*"
									}
								}
							}
							if (_5 != null && (_5.fullWidth || _5.fullHeight)
									&& _4._constructor != "FacetChart") {
								if (_5.containerName == "TabSet"
										|| _5.containerName == "Window"
										|| _5.containerName == "SectionStack"
										|| _5.containerName == "HStack"
										|| _5.containerName == "HLayout"
										|| _5.containerName == "VLayout") {
									if (_5.fullWidth) {
										delete _4.width
									}
									if (_5.fullHeight) {
										delete _4.height
									}
								} else if (_5.containerName == "VStack"
										|| _5.controlName == "com.balsamiq.mockups::FieldSet"
										|| _5.controlName == "com.balsamiq.mockups::Canvas"
										|| _5.controlName == "com.balsamiq.mockups::TabBar") {
									if (_5.fullWidth) {
										_4.width = "100%"
									}
									if (_5.fullHeight) {
										_4.height = "100%"
									}
								}
							}
							delete _4.absX;
							delete _4.absY;
							if (_4._constructor == "DynamicForm"
									&& _4.isGroup != true && _4.height != "*") {
								if (_4.specialProperties.calculatedHeight != null) {
									_4.height = _4.specialProperties.calculatedHeight
								}
							}
							if (_4.specialProperties) {
								var _6 = _4.specialProperties.controlName;
								var _7 = _2.widgetPropertyTranslations[_6];
								if (_7 && _7.onProcessFinished) {
									_7.onProcessFinished(_4)
								}
							}
							if (_4._constructor == "DynamicForm") {
								var _8 = _4.items || _4.fields;
								if (_8) {
									for (var j = 0; j < _8.length; j++) {
										var _10 = _8[j].specialProperties;
										if (_10) {
											var _6 = _10.controlName;
											var _7 = _2.widgetPropertyTranslations[_6];
											if (_7 && _7.onProcessFinished) {
												_7.onProcessFinished(_8[j])
											}
										}
									}
								}
							}
						}
					},
					isc.A.getBalsamiqControlNameForSCControl = function isc_MockupImporter_getBalsamiqControlNameForSCControl(
							_1) {
						var _2 = this.$98p, _3 = _2.classTranslations, _4;
						for ( var _5 in _3) {
							if (_3[_5] == _1) {
								_4 = _5;
								break
							}
						}
						return _4
					},
					isc.A.$879 = function isc_MockupImporter__processLinks(_1,
							_2, _3) {
						if (this.$88a == null) {
							this.$88a = 0
						}
						var _4 = this;
						var _5 = 0;
						for (var i = 0; i < _1.length; i++) {
							var _7 = [ _1[i] ];
							if (_1[i]._constructor == "DynamicForm"
									&& _1[i].items) {
								_7 = _1[i].items
							}
							if (_1[i]._constructor == "SectionStack"
									&& _1[i].specialProperties.widgets) {
								_7 = _1[i].specialProperties.widgets;
								_7.add(_1[i])
							}
							for (var _8 = 0; _8 < _7.length; _8++) {
								var _9 = _7[_8];
								if (_9.specialProperties == null
										|| (_9.specialProperties.hrefs == null && _9.specialProperties.href == null))
									continue;
								var _10 = null;
								if (_9.specialProperties.hrefs) {
									if (isc.isA
											.String(_9.specialProperties.hrefs)) {
										_10 = _9.specialProperties.hrefs
												.split(",")
									} else {
									}
								} else if (_9.specialProperties.href) {
									_10 = [ _9.specialProperties.href ]
								}
								if (_10 == null)
									continue;
								var _11 = this.mockupPath.substring(0,
										this.mockupPath.lastIndexOf("/"));
								_9.specialProperties.links = [];
								for (var j = 0; j < _10.length; j++) {
									var _13 = _10[j].split("&bm;");
									var _14 = _13[1];
									if (_14 == null) {
										_9.specialProperties.links.add(null);
										continue
									}
									_14 = _11 + "/" + _14;
									_9.specialProperties.links.add({
										fileName : _14,
										name : _13[0]
									});
									if (this.$878[_14]) {
										this.$878[_14].widgets.add(_9)
									} else {
										this.$878[_14] = {
											widgets : [ _9 ],
											fileName : _14,
											layoutName : _13[0]
										};
										_5++;
										var _15 = function(_17) {
											_5--;
											if (_5 == 0) {
												if (_17 == null) {
													_3(null)
												} else if (_2) {
													_3(_4.$360(_1))
												} else {
													_1 = _4.$88b(_1);
													if (_4.$978) {
														_1.addList(_4.$978)
													}
													var _16 = isc.EditContext
															.serializeDefaults(_4
																	.$98a(_1));
													_3(
															_16.replace(/\r/g,
																	"\n"), [],
															_4.$134x(_1));
													if (_4.$90r.length > 0) {
														_4
																.logWarn("During import these custom components were not found: "
																		+ _4.$90r)
													}
												}
											}
										};
										this.$1335(_14, _15)
									}
								}
							}
						}
						if (_5 == 0) {
							if (_2) {
								_3(this.$360(_1))
							} else if (this.$88a == 0) {
								if (this.$978) {
									_1.addList(this.$978)
								}
								var _16 = isc.EditContext
										.serializeDefaults(this.$98a(_1));
								_3(_16.replace(/\r/g, "\n"), _1);
								if (this.$90r.length > 0) {
									this
											.logWarn("During import these custom components were not found: "
													+ this.$90r)
								}
							}
						}
					},
					isc.A.$134x = function isc_MockupImporter__getLayoutIds(_1) {
						var _2 = [];
						for (var i = 0; i < _1.length; i++) {
							if (_1[i].ID)
								_2.add(_1[i].ID)
						}
						return _2
					},
					isc.A.$1335 = function isc_MockupImporter__loadLinkedLayout(
							_1, _2, _3) {
						var _4 = this;
						isc.DMI
								.callBuiltin({
									methodName : "loadFile",
									arguments : [ _1 ],
									callback : function(_13) {
										var _5 = _3 || _1;
										if (_13.status == isc.RPCResponse.STATUS_FAILURE) {
											var _6 = _1.lastIndexOf("/");
											var _7 = _1.substring(0, _6);
											var _8 = _1.substring(_6 + 1);
											if (!_7.endsWith("/assets")) {
												var _9 = _7 + "/assets/" + _8;
												_4.$1335(_9, _2, _5);
												return

												

																								

												

																																				

												

																								

												

											}
											if (_4.$90q == null) {
												_4.$90q = []
											}
											_4.$90q.add(_5);
											var _10 = "Unable to import this mockup. Missing resources:<br/>";
											for (var _11 = 0; _11 < _4.$90q.length; _11++) {
												_10 += _4.$90q[_11];
												if (_11 != (_4.$90q.length - 1)) {
													_10 += "<br/>"
												}
											}
											_4.logWarn(_10);
											isc
													.ask(
															_10
																	+ " Do you want to abort import or continue without these resources? You will be able to upload these resources by using add asset button and import this mockup again.",
															function() {
															},
															{
																buttons : [
																		isc.Button
																				.create({
																					title : "Abort",
																					click : function() {
																						this.topElement
																								.hide();
																						_2(null)
																					}
																				}),
																		isc.Button
																				.create({
																					title : "Continue",
																					click : function() {
																						this.topElement
																								.hide();
																						for (var i = 0; i < _4.$90q.length; i++) {
																							_4.$878[_4.$90q[i]].layout = [];
																							_2(_5)
																						}
																					}
																				}) ]
															});
											return

											

																						

											

																																	

											

																						

											

										}
										_4.$98y(_13.data, _2, _5)
									},
									requestParams : {
										willHandleError : true
									}
								})
					},
					isc.A.$98a = function isc_MockupImporter__cleanLayout(_1) {
						for (var _2 = 0; _2 < _1.length; _2++) {
							var _3 = _1[_2];
							delete _3.specialProperties;
							if (_3._constructor == "DynamicForm") {
								var _4 = _3.items || _3.fields;
								if (_4) {
									for (var i = 0; i < _4.length; i++) {
										var _6 = _4[i];
										if (this.ignoreWidthFormItems
												.contains(_6._constructor)) {
											delete _6.width
										} else if (isc.isA.Number(_6.width)) {
											var _7 = isc[_6._constructor]
													.getInstanceProperty("width");
											if (Math.abs(_6.width - _7) < this.formExtraWidthThreshold) {
												delete _6.width
											}
										}
										_6.$97p = _6._constructor;
										delete _6._constructor;
										if (_6.showTitle == true)
											delete _6.showTitle
									}
								}
							}
							if (_3._constructor == "TabSet"
									&& _3.selectedTab == 0) {
								delete _3.selectedTab
							}
							var _8 = _3.items || _3.fields || _3.members;
							if (_8)
								this.$98a(_8)
						}
						return _1
					},
					isc.A.$979 = function isc_MockupImporter__getLinks(_1) {
						var _2 = [];
						for (var i = 0; i < _1.length; i++) {
							var _4 = [ _1[i] ];
							if (_1[i]._constructor == "DynamicForm"
									&& (_1[i].items || _1[i].fields)) {
								_4 = _1[i].items || _1[i].fields
							}
							if (_1[i]._constructor == "SectionStack"
									&& _1[i].specialProperties.widgets) {
								_4 = _1[i].specialProperties.widgets;
								_4.add(_1[i])
							}
							for (var _5 = 0; _5 < _4.length; _5++) {
								var _6 = _4[_5];
								if (_6.specialProperties == null
										|| (_6.specialProperties.hrefs == null && _6.specialProperties.href == null))
									continue;
								if (_6.specialProperties.hrefs) {
									if (isc.isA
											.String(_6.specialProperties.hrefs)) {
										_2.addAll(_6.specialProperties.hrefs
												.split(","))
									} else {
									}
								} else if (_6.specialProperties.href) {
									_2.add(_6.specialProperties.href)
								}
							}
						}
						return _2
					},
					isc.A.$88b = function isc_MockupImporter__mergeLinksLayout(
							_1) {
						for ( var _2 in this.$878) {
							var _3 = this.$878[_2];
							if (_2 == this.mockupPath) {
								_3.prefix = "";
								_3.processed = true
							} else {
								_3.prefix = _3.layoutName.replace(
										/[^a-zA-Z0-9_]/g, "_")
										+ "_"
							}
							_3.topLevelElements = this.$88q(_3.layout)
						}
						var _4 = this.mockupPath.substring(0, this.mockupPath
								.lastIndexOf("/"));
						var _2 = this.mockupPath;
						var _5 = [];
						do {
							var _3 = this.$878[_2];
							if (_3.layout.length > 0) {
								if (_3.prefix != "") {
									this.$88s(_3.layout, _3.prefix)
								}
								this.$88r(_3);
								if (_3.prefix != "") {
									this.$88s(_3.layout, _3.prefix);
									for (var i = 0; i < _3.topLevelElements.length; i++) {
										_3.topLevelElements[i].autoDraw = false
									}
								}
								_5.addList(_3.layout)
							}
							_2 = null;
							for ( var _7 in this.$878) {
								var _3 = this.$878[_7];
								if (_3.processed != true) {
									_2 = _7;
									_3.processed = true;
									break
								}
							}
						} while (_2);
						for ( var _7 in this.$878) {
							var _3 = this.$878[_7];
							if (_3.activateCode == null) {
								_3.activateCode = this.$88t(_3.layout)
							}
							_3.showCode = this.$88u(_3);
							_3.hideCode = this.$88v(_3)
						}
						for ( var _2 in this.$878) {
							if (this.$878[_2].mergedWith)
								continue;
							_1 = this.$878[_2].layout
							for (var i = 0; i < _1.length; i++) {
								var _8 = _1[i];
								if (_8.specialProperties
										&& _8.specialProperties.links
										&& (_8._constructor != "TabSet" && _8._constructor != "SectionStack")) {
									var _9 = _8.specialProperties.links;
									for (var j = 0; j < _9.length; j++) {
										if (_9[j] == null)
											continue;
										var _11 = _9[j].fileName;
										var _12 = this.$90s(_11, _2,
												_8.customData, _5);
										if (_8._constructor == "TreeGrid"
												|| _8._constructor == "ListGrid") {
											if (_8.selectionChanged == null) {
												_8.selectionChanged = ""
											}
											_8.selectionChanged += "if (this.getRecordIndex(record) == "
													+ (j - 1)
													+ ") {"
													+ _12
													+ "}"
										} else {
											_8.click = _12
										}
									}
								} else if (_8._constructor == "DynamicForm") {
									var _13 = _8.items || _8.fields;
									for (var j = 0; j < _13.length; j++) {
										if (_13[j].specialProperties
												&& _13[j].specialProperties.links) {
											var _9 = _13[j].specialProperties.links;
											for (var k = 0; k < _9.length; k++) {
												if (_9[k] == null)
													continue;
												var _11 = _9[k].fileName;
												_8.items[j].click = this.$90s(
														_11, _2,
														_13[j].customData, _5)
											}
										}
									}
								} else if (_8._constructor == "SectionStack"
										&& _8.specialProperties.widgets) {
									var _13 = _8.specialProperties.widgets;
									for (var j = 0; j < _13.length; j++) {
										if (_8 != _13[j]
												&& _13[j].specialProperties
												&& _13[j].specialProperties.links) {
											var _9 = _13[j].specialProperties.links;
											for (var k = 0; k < _9.length; k++) {
												if (_9[k] == null)
													continue;
												var _11 = _9[k].fileName;
												_13[j].click = this.$90s(_11,
														_2, _13[j].customData,
														_5)
											}
										}
									}
								} else if (_8._constructor == "TabSet"
										&& _8.specialProperties.links) {
									var _15 = _8.tabs;
									for (var j = 0; j < _15.length; j++) {
										var _16 = _8.specialProperties.links[j];
										if (_16) {
											var _11 = _16.fileName;
											_15[j].click = this.$90s(_11, _2,
													_15[j].customData, _5)
										}
									}
								}
							}
						}
						return _5
					},
					isc.A.$90s = function isc_MockupImporter__constructActivateCode(
							_1, _2, _3, _4) {
						var _5 = this.$878[_1];
						var _6 = "";
						if (_3) {
							var _3 = decodeURIComponent(_3);
							var _7 = _3.indexOf("linkTarget=");
							if (_7 >= 0) {
								var _8 = _3
										.substring(_7 + "linkTarget=".length)
										.trim();
								if (_8.contains("\n")) {
									_8 = _8.substring(0, _8.indexOf("\n"))
											.trim()
								}
								_8 = _8.replace(/['"]/g, "");
								var _9 = null;
								for (var k = 0; k < _4.length; k++) {
									if (_4[k].customID == _8) {
										_9 = _4[k];
										break
									}
								}
								if (_9 == null) {
									this.$90r.add(_8);
									return _6 + _5.activateCode
								}
								if (_9._constructor == "Window") {
									_6 = "for (var i = 0; i < " + _9.ID
											+ ".items.length; i++) { " + _9.ID
											+ ".items[i].hide()}\n";
									for (var k = 0; k < _5.topLevelElements.length; k++) {
										if (_9.ID == _5.topLevelElements[k].ID) {
											for (var l = 0; l < _9.items.length; l++) {
												_6 += _9.ID + ".addItem("
														+ _9.items[l].ref
														+ ");";
												_6 += _9.items[l].ref
														+ ".show();"
											}
										} else {
											_6 += _9.ID + ".addItem("
													+ _5.topLevelElements[k].ID
													+ ");";
											_6 += _5.topLevelElements[k].ID
													+ ".show();"
										}
									}
								} else if (_9._constructor == "TabSet") {
									var _12 = _9.selectedTab == null ? 0
											: _9.selectedTab;
									if (_12 >= _9.tabs.length)
										_12 = _9.tabs.length - 1;
									var _13 = _9.ID + ".tabs[" + _12 + "].pane";
									_6 = "for (var i = 0; i < " + _13
											+ ".members.length; i++) { " + _13
											+ ".members[i].hide()}\n";
									for (var k = 0; k < _5.topLevelElements.length; k++) {
										if (_9.ID == _5.topLevelElements[k].ID) {
											var _14 = _9.specialProperties.innerItems;
											for (var l = 0; l < _14.length; l++) {
												_6 += _13 + ".addMember("
														+ _14[l].ID + ");";
												_6 += _14[l].ID + ".show();"
											}
										} else {
											_6 += _13 + ".addMember("
													+ _5.topLevelElements[k].ID
													+ ");";
											_6 += _5.topLevelElements[k].ID
													+ ".show();"
										}
									}
								} else if (_9._constructor == "SectionStack") {
									var _15 = _9.ID + ".sections[" + _9.$88w
											+ "]";
									_6 = "for (var i = "
											+ _15
											+ ".items.length - 1; i >= 0; i--) { "
											+ _15 + ".items[i].hide();\n"
											+ _9.ID + ".removeItem(" + _9.$88w
											+ "," + _15 + ".items[i])}\n";
									for (var k = 0; k < _5.topLevelElements.length; k++) {
										if (_9.ID == _5.topLevelElements[k].ID) {
											var _14 = _9.specialProperties.innerItems;
											for (var l = 0; l < _14.length; l++) {
												_6 += _9.ID + ".addItem("
														+ _9.$88w + ","
														+ _14[l].ID + ", " + k
														+ ");";
												_6 += _14[l].ID + ".show();\n"
											}
										} else {
											_6 += _9.ID + ".addItem(" + _9.$88w
													+ ","
													+ _5.topLevelElements[k].ID
													+ ", " + k + ");";
											_6 += _5.topLevelElements[k].ID
													+ ".show();\n"
										}
									}
								} else {
									_6 = "for (var i = 0; i < " + _9.ID
											+ ".members.length; i++) { "
											+ _9.ID + ".members[i].hide()}\n";
									for (var k = 0; k < _5.topLevelElements.length; k++) {
										if (_9.ID == _5.topLevelElements[k].ID) {
											var _14 = _9.specialProperties.innerItems;
											for (var l = 0; l < _14.length; l++) {
												_6 += _9.ID + ".addMember("
														+ _14[l].ID + ");";
												_6 += _14[l].ID + ".show();"
											}
										} else {
											_6 += _9.ID + ".addMember("
													+ _5.topLevelElements[k].ID
													+ ");";
											_6 += _5.topLevelElements[k].ID
													+ ".show();"
										}
									}
								}
							}
							return _6 + _5.activateCode
						} else {
							if (_5.prefix != this.$878[_2].prefix) {
								var _16 = this.$878[_2];
								if (_16.mergedWith != null) {
									if (_16.mergedWith == "") {
										_16 = this.$878[this.mockupPath]
									} else {
										_16 = this.$878[_16.mergedWith]
									}
								}
								_6 += _16.hideCode;
								_6 += _5.showCode
							}
							_6 += _5.activateCode;
							return _6
						}
					},
					isc.A.$88r = function isc_MockupImporter__mergeLinksLayoutProcessTabsAndStacks(
							_1) {
						var _2 = _1.layout;
						do {
							var _3 = false;
							for (var i = 0; i < _2.length; i++) {
								var _5 = _2[i];
								if (_5.specialProperties
										&& _5.specialProperties.links
										&& (_5._constructor == "TabSet" || _5._constructor == "SectionStack")) {
									var _6 = _5.specialProperties.links;
									for (var j = 0; j < _6.length; j++) {
										if (_6[j] == null)
											continue;
										var _8 = this.$878[_6[j].fileName];
										if (!_8.processed
												&& _8.layout.length > 0
												&& this.$88c(_2, _8, _5)) {
											var _9 = null;
											if (_5._constructor == "TabSet") {
												_9 = this.$88d(j, _5, _8,
														_8.prefix)
											} else {
												_9 = this.$88e(j, _5, _8,
														_8.prefix)
											}
											var _10 = _9.layout;
											if (_10) {
												_2.addListAt(_10, _2
														.indexOf(_5) - 1);
												_3 = true;
												_8.processed = true;
												_8.mergedWith = _1.prefix;
												_8.topLevelElements = _1.topLevelElements;
												_9.widget.ID = _5.ID;
												_8.activateCode = _5.ID
														+ ".showRecursively();\n"
														+ this
																.$88t([ _9.widget ])
														+ this.$88t(_10)
											}
										}
									}
								}
							}
						} while (_3)
					},
					isc.A.$88t = function isc_MockupImporter__getActivateLayoutCode(
							_1) {
						var _2 = "";
						for (var i = 0; i < _1.length; i++) {
							if (_1[i]._constructor == "TabSet") {
								_2 = _1[i].ID + ".selectTab("
										+ _1[i].selectedTab + ");\n" + _2
							} else if (_1[i]._constructor == "SectionStack") {
								for (var j = 0; j < _1[i].sections.length; j++) {
									if (_1[i].sections[j].expanded) {
										_2 = _1[i].ID + ".expandSection(" + j
												+ ");\n" + _2
									} else if (_1[i].sections[j].items) {
										_2 = _1[i].ID + ".collapseSection(" + j
												+ ");\n" + _2
									}
								}
							}
						}
						;
						return _2
					},
					isc.A.$88u = function isc_MockupImporter__getShowLayoutCode(
							_1) {
						var _2 = "";
						for (var k = 0; k < _1.topLevelElements.length; k++) {
							_2 += _1.topLevelElements[k].ID + ".show();\n"
						}
						;
						return _2
					},
					isc.A.$88v = function isc_MockupImporter__getHideLayoutCode(
							_1) {
						var _2 = "";
						for (var k = 0; k < _1.topLevelElements.length; k++) {
							_2 += _1.topLevelElements[k].ID + ".hide();\n"
						}
						;
						return _2
					},
					isc.A.$88q = function isc_MockupImporter__getLayoutTopLevelElements(
							_1) {
						var _2 = [];
						for (var i = 0; i < _1.length; i++) {
							if (_1[i]._constructor != "MockDataSource"
									&& _1[i]._constructor != "ValuesManager"
									&& this.$88g(_1, _1[i]) == null) {
								_2.add(_1[i])
							}
						}
						;
						return _2
					},
					isc.A.$88d = function isc_MockupImporter__mergeTabLayout(
							_1, _2, _3, _4) {
						if (_2.tabs[_1].pane)
							return null;
						var _5 = this.$88f(_3, _2);
						if (_5 == null)
							return null;
						var _6 = _5.layout;
						this.$88s(_6, _4);
						_2.specialProperties.innerItems.addList(_6);
						for (var i = 0; i < _5.widget.tabs.length; i++) {
							var _8 = _5.widget.tabs[i].pane;
							if (_8 == null)
								continue;
							if (isc.isA.String(_8)) {
								_2.tabs[i].pane = _4 + _8
							} else {
								for (var j = 0; j < _8.VStack.members.length; j++) {
									_8.VStack.members[j] = _4
											+ _8.VStack.members[j]
								}
								;
								_2.tabs[i].pane = _8
							}
							break
						}
						;
						return _5
					},
					isc.A.$88e = function isc_MockupImporter__mergeSectionStackLayout(
							_1, _2, _3, _4) {
						for (var _5 = 0; _5 < _2.sections.length; _5++) {
							if (_2.sections[_5].$76w != _1)
								continue;
							var _6 = this.$88f(_3, _2);
							if (_6 == null)
								return;
							var _7 = _6.layout;
							this.$88s(_7, _4);
							_2.specialProperties.innerItems.addList(_7);
							_2.sections[_5].items = _6.widget.sections[_5].items;
							return _6
						}
					},
					isc.A.$88s = function isc_MockupImporter__addPrefixToIds(
							_1, _2) {
						for (var i = 0; i < _1.length; i++) {
							var _4 = _1[i];
							if (_4.ID && !_4.ID.startsWith(_2)) {
								_4.ID = _2 + _4.ID
							}
							if (_4.dataSource && !_4.dataSource.startsWith(_2)) {
								_4.dataSource = _2 + _4.dataSource
							}
							if (_4.valuesManager
									&& !_4.valuesManager.startsWith(_2)) {
								_4.valuesManager = _2 + _4.valuesManager
							}
							if (_4.specialProperties) {
								var _5 = _4.specialProperties.refs;
								if (_5) {
									for (var j = 0; j < _5.length; j++) {
										_5[j].ref = _4.ID
									}
								}
							}
							if (_4._constructor == "TabSet") {
								for (var j = 0; j < _4.tabs.length; j++) {
									if (_4.tabs[j].pane == null)
										continue;
									if (isc.isA.String(_4.tabs[j].pane)
											&& !_4.tabs[j].pane.startsWith(_2)) {
										_4.tabs[j].pane = _2 + _4.tabs[j].pane
									} else {
										var _7 = _4.tabs[j].pane;
										if (_7.VStack) {
											for (var k = 0; k < _7.VStack.members.length; k++) {
												if (!_7.VStack.members[k]
														.startsWith(_2)) {
													_7.VStack.members[k] = _2
															+ _7.VStack.members[k]
												}
											}
										} else if (_7.children) {
											for (var k = 0; k < _7.children.length; k++) {
												if (!_7.children[k].ref
														.startsWith(_2)) {
													_7.children[k].ref = _2
															+ _7.children.members[k].ref
												}
											}
										}
									}
								}
							}
						}
					},
					isc.A.$88c = function isc_MockupImporter__compareParentLayout(
							_1, _2, _3) {
						var _4 = this.$360(_1);
						var _5 = this.$360(_2.layout);
						var _6 = this.$88f({
							layout : _1
						}, _3);
						var _7 = this.$88f(_2, _3);
						if (_7 == null)
							return false;
						for (var i = 0; i < _6.layout.length; i++) {
							for (var j = 0; j < _4.length; j++) {
								if (_6.layout[i].ID == _4[j].ID) {
									_4.removeAt(j);
									break
								}
							}
						}
						;
						for (var i = 0; i < _7.layout.length; i++) {
							for (var j = 0; j < _5.length; j++) {
								if (_7.layout[i].ID == _5[j].ID) {
									_5.removeAt(j);
									break
								}
							}
						}
						;
						var _10 = _6.widget;
						var _11 = _7.widget;
						do {
							_10 = this.$88g(_4, _10);
							_11 = this.$88g(_5, _11);
							if (_10 == null || _11 == null)
								break;
							if (_10._constructor == "TabSet"
									&& _11._constructor == "TabSet") {
								delete _11.layoutLeftMargin;
								delete _11.layoutTopMargin;
								delete _11.layoutRightMargin;
								delete _11.layoutBottomMargin;
								delete _10.layoutLeftMargin;
								delete _10.layoutTopMargin;
								delete _10.layoutRightMargin;
								delete _10.layoutBottomMargin;
								var _12 = _10.specialProperties.innerItems;
								for (var i = 0; i < _11.tabs.length; i++) {
									if (_11.tabs[i].pane != null
											|| _10.tabs[i].pane == null) {
										continue
									}
									var _13 = _10.tabs[i].pane;
									if (isc.isA.String(_13)) {
										for (var k = 0; k < _12.length; k++) {
											var _15 = _12[k]
											if (_15.ID != _13)
												continue;
											this.$88h(_4, _15)
										}
									} else {
										for (var j = 0; j < _13.VStack.members.length; j++) {
											for (var k = 0; k < _12.length; k++) {
												var _15 = _12[k]
												if (_15.ID != _13.VStack.members[j])
													continue;
												this.$88h(_4, _15)
											}
										}
									}
								}
							} else if (_10._constructor == "SectionStack"
									&& _11._constructor == "SectionStack") {
								var _12 = _10.specialProperties.innerItems;
								for (var i = 0; i < _11.sections.length; i++) {
									if (_11.sections[i].items != null
											|| _10.sections[i].items == null) {
										continue
									}
									_10.sections[i].expanded = _11.sections[i].expanded;
									for (var j = 0; j < _10.sections[i].items.length; j++) {
										for (var k = 0; k < _12.length; k++) {
											var _15 = _12[k]
											if (_15.ID != _10.sections[i].items[j].ref)
												continue;
											this.$88h(_4, _15)
										}
									}
									;
									delete _10.sections[i].items
								}
							}
						} while (true);
						for (var j = 0; j < _4.length; j++) {
							if (_6.widget.ID == _4[j].ID) {
								_4.removeAt(j);
								break
							}
						}
						for (var j = 0; j < _5.length; j++) {
							if (_7.widget.ID == _5[j].ID) {
								_5.removeAt(j);
								break
							}
						}
						this.$88i(_4);
						this.$88i(_5);
						var _16 = function(_19, _20) {
							return isc.echoAll(_19) < isc.echoAll(_20)
						};
						_4.sort(_16);
						_5.sort(_16);
						var _17 = isc.JSON.encode(_4);
						var _18 = isc.JSON.encode(_5);
						return _17 === _18
					});
	isc.evalBoundary;
	isc.B
			.push(
					isc.A.$88h = function isc_MockupImporter__removeItemWithChildItemsFromLayout(
							_1, _2) {
						for (var l = 0; l < _1.length; l++) {
							if (_1[l].ID == _2.ID) {
								_1.removeAt(l);
								break
							}
						}
						;
						var _4 = this.$88j(_2);
						for (var m = 0; m < _4.length; m++) {
							for (var l = 0; l < _1.length; l++) {
								if (_1[l].ID == _4[m].ID) {
									_1.removeAt(l);
									break
								}
							}
						}
						;
						var _6 = _2.specialProperties.additionalElements;
						if (_6) {
							for (var m = 0; m < _6.length; m++) {
								for (var l = 0; l < _1.length; l++) {
									if (_1[l].ID == _6[m].ID) {
										_1.removeAt(l);
										break
									}
								}
							}
						}
					},
					isc.A.$88g = function isc_MockupImporter__findParentWidget(
							_1, _2) {
						for (var i = 0; i < _1.length; i++) {
							if (_1[i].specialProperties
									&& _1[i].specialProperties.innerItems) {
								for (var j = 0; j < _1[i].specialProperties.innerItems.length; j++) {
									if (_1[i].specialProperties.innerItems[j].ID == _2.ID) {
										return _1[i]
									}
								}
							}
						}
					},
					isc.A.$88i = function isc_MockupImporter__cleanObjects(_1) {
						if (this.$88k(_1)) {
						} else if (isc.isA.Array(_1)) {
							for (var i = 0; i < _1.length; i++) {
								if (!this.$88k(_1[i])) {
									this.$88i(_1[i])
								}
							}
						} else {
							for ( var _3 in _1) {
								if (_3 == "ID" || _3 == "ref"
										|| _3 == "specialProperties"
										|| _3 == "pane" || _3 == "selectedTab"
										|| _3 == "zIndex" || _3 == "expanded"
										|| _3 == "$88w") {
									delete _1[_3]
								} else if (!this.$88k(_1[_3])) {
									this.$88i(_1[_3])
								}
							}
						}
					},
					isc.A.$360 = function isc_MockupImporter__clone(_1, _2) {
						if (_2 == null)
							_2 = 10;
						var _3 = null;
						if (this.$88k(_1)) {
							_3 = this.$88l(_1)
						} else if (isc.isA.Array(_1)) {
							_3 = [];
							for (var i = 0; i < _1.length; i++) {
								var _5 = _1[i];
								if (this.$88k(_5)) {
									_3.add(this.$88l(_5))
								} else if (_2 == 0) {
									_3.add(isc.isA.Array(_5) ? [] : {})
								} else {
									_3.add(this.$360(_5, _2 - 1))
								}
							}
						} else {
							_3 = {};
							for ( var _6 in _1) {
								var _5 = _1[_6];
								if (this.$88k(_5)) {
									_3[_6] = this.$88l(_5)
								} else if (_2 == 0) {
									_3[_6] = (isc.isA.Array(_5) ? [] : {})
								} else {
									_3[_6] = this.$360(_5, _2 - 1)
								}
							}
						}
						return _3
					},
					isc.A.$88k = function isc_MockupImporter__isPlainObject(_1) {
						var _2;
						return (_1 === _2) || (_1 == null)
								|| isc.isA.String(_1) || isc.isA.Boolean(_1)
								|| isc.isA.Number(_1) || isc.isA.Function(_1)
								|| isc.isA.Date(_1)
					},
					isc.A.$88l = function isc_MockupImporter__clonePlainObject(
							_1) {
						var _2;
						if (_1 === _2)
							return _2;
						if (_1 == null)
							return null;
						if (isc.isA.String(_1) || isc.isA.Boolean(_1)
								|| isc.isA.Number(_1) || isc.isA.Function(_1))
							return _1;
						if (isc.isA.Date(_1))
							return _1.duplicate();
						return null
					},
					isc.A.$88f = function isc_MockupImporter__getWidgetContentLayout(
							_1, _2) {
						if (_2._constructor == "TabSet") {
							var _3 = this.$88m(_1.layout, _2.tabs);
							if (_3) {
								return {
									layout : this.$88j(_3),
									widget : _3
								}
							}
						} else if (_2._constructor == "SectionStack") {
							var _4 = this.$88n(_1.layout, _2.sections);
							if (_4) {
								return {
									layout : this.$88j(_4),
									widget : _4
								}
							}
						}
					},
					isc.A.$88m = function isc_MockupImporter__findSameTabSet(
							_1, _2) {
						for (var i = 0; i < _1.length; i++) {
							var _4 = _1[i];
							if (_4._constructor == "TabSet"
									&& _4.tabs.length == _2.length) {
								var _5 = _4;
								var _6 = true;
								for (var j = 0; j < _2.length; j++) {
									if (_2[j].title != _4.tabs[j].title) {
										_6 = false;
										break
									}
								}
								;
								if (_6) {
									return _5
								}
							}
						}
						;
						return null
					},
					isc.A.$88n = function isc_MockupImporter__findSameSectionStack(
							_1, _2) {
						for (var i = 0; i < _1.length; i++) {
							var _4 = _1[i];
							if (_4._constructor == "SectionStack"
									&& _4.sections.length == _2.length) {
								var _5 = _4;
								var _6 = true;
								for (var j = 0; j < _2.length; j++) {
									if (_2[j].title != _4.sections[j].title) {
										_6 = false;
										break
									}
								}
								;
								if (_6) {
									return _5
								}
							}
						}
						;
						return null
					},
					isc.A.$88j = function isc_MockupImporter__getInnerComponents(
							_1) {
						var _2 = this.$88o(_1);
						for (var i = _2.length - 1; i >= 1; i--) {
							for (var j = i - 1; j >= 0; j--) {
								if (_2[i].ID == _2[j].ID) {
									_2.removeAt(i);
									i--;
									break
								}
							}
						}
						;
						return _2
					},
					isc.A.$88o = function isc_MockupImporter__getInnerComponentsRecursive(
							_1) {
						var _2 = [];
						if (_1.specialProperties.innerItems == null)
							return [];
						for (var i = 0; i < _1.specialProperties.innerItems.length; i++) {
							var _4 = _1.specialProperties.innerItems[i];
							if (_4.specialProperties
									&& _4.specialProperties.innerItems) {
								_2.addList(this.$88o(_4))
							}
							if (_4.specialProperties
									&& _4.specialProperties.additionalElements) {
								_2
										.addList(_4.specialProperties.additionalElements)
							}
							_2.add(_4)
						}
						;
						return _2
					},
					isc.A.$873 = function isc_MockupImporter__convertBMMLWidgetsToISCWidgets(
							_1, _2) {
						var _3 = [];
						var _4;
						if (_1 && _1.controls) {
							_4 = _1.controls.control
						} else {
							this.logWarn("The data is not in BMML format"
									+ (_2 != null ? ":  " + _2 : "."));
							isc.warn("The file is not in BMML format.", {
								target : this,
								methodName : "bmmlImportFailed"
							});
							_4 = []
						}
						if (!isc.isAn.Array(_4)) {
							_4 = [ _4 ]
						}
						for (var i = 0; i < _4.length; i++) {
							if ("__group__" == _4[i].controlTypeID) {
								_3.addList(this.convertGroup(_4[i]))
							} else {
								_3.addList(this.convertControl(_4[i]))
							}
						}
						;
						return _3
					},
					isc.A.$874 = function isc_MockupImporter__loadSymbolsAssets(
							_1, _2) {
						if (this.mockupPath) {
							var _3 = this.mockupPath.substring(0,
									this.mockupPath.lastIndexOf("/"));
							var _4 = [];
							var _5 = [];
							for (var i = 0; i < _1.length; i++) {
								if (_1[i]._constructor == "Symbol") {
									var _7 = _1[i].symbolPath;
									if (_7.startsWith("./")) {
										_7 = _7.substring(2)
									}
									_7 = _3 + "/" + _7;
									var _8 = _7.indexOf("#");
									var _9 = null;
									if (_8 > 0) {
										_9 = _7.substring(_7.indexOf("#") + 1);
										_7 = _7.substring(0, _7.indexOf("#"))
									}
									_4.add({
										symbol : _1[i],
										path : _7,
										symbolName : _9
									})
									if (!_5.contains(_7)) {
										_5.add(_7)
									}
								}
							}
							;
							var _10 = 0;
							var _11 = this;
							for (var i = 0; i < _5.length; i++) {
								var _12 = _5[i];
								isc.DMI
										.callBuiltin({
											methodName : "loadFile",
											arguments : [ _12 ],
											callback : function(_23) {
												var _13 = _23.context.data.arguments[0];
												if (_23.status == isc.RPCResponse.STATUS_FAILURE) {
													var _14 = function() {
														for (var j = 0; j < _4.length; j++) {
															if (_13 != _4[j].path)
																continue;
															var _16 = _4[j].symbol;
															var _17 = {
																_constructor : "Label",
																ID : "symbol_"
																		+ j,
																contents : _4[j].symbolName,
																left : _16.left,
																top : _16.top,
																width : _16.width,
																height : _16.height,
																border : "1px solid black",
																align : "center",
																zIndex : _16.zIndex,
																specialProperties : {
																	controlName : "com.balsamiq.mockups::Label"
																}
															};
															_1
																	.addAt(
																			_17,
																			_1
																					.indexOf(_16));
															_1.remove(_16)
														}
														;
														_10++;
														if (_10 == _5.length) {
															_2(_1)
														}
														this.topElement.hide()
													}
													isc
															.ask(
																	"Unable to import this mockup. Asset "
																			+ _13
																			+ " is missing. Do you want to abort import or continue with placeholders. You will be able to upload this asset and import this mockup again.",
																	function() {
																	},
																	{
																		buttons : [
																				isc.Button
																						.create({
																							title : "Abort",
																							click : function() {
																								this.topElement
																										.hide();
																								_2(null)
																							}
																						}),
																				isc.Button
																						.create({
																							title : "Continue",
																							click : _14
																						}) ]
																	});
													return

													

																										

													

																																							

													

																										

													

												}
												for (var j = 0; j < _4.length; j++) {
													if (_13 != _4[j].path)
														continue;
													var _18 = isc.XMLTools
															.toJS(isc.XMLTools
																	.parseXML(_23.data));
													var _17;
													var _19 = _18.controls ? _18.controls.control
															: [];
													if (_4[j].symbolName == null) {
														_17 = {
															groupChildrenDescriptors : {
																control : _19
															},
															zOrder : 0,
															width : _4[j].symbol.width,
															height : _4[j].symbol.height,
															controlTypeID : "__group__",
															measuredW : _4[j].symbol.width,
															measuredH : _4[j].symbol.height
														}
													} else {
														for (var k = 0; k < _19.length; k++) {
															var _21 = _19[k].controlProperties.controlName;
															if (unescape(_21) == _4[j].symbolName) {
																_17 = _19[k];
																break
															}
														}
													}
													_17.x = 0;
													_17.y = 0;
													_17 = _11.$875(_17,
															_4[j].symbol);
													_11.$132v(_17);
													var _22 = _11.$873({
														controls : {
															control : [ _17 ]
														}
													});
													_1
															.addListAt(
																	_22,
																	_1
																			.indexOf(_4[j].symbol));
													_1.remove(_4[j].symbol)
												}
												;
												_10++;
												if (_10 == _5.length) {
													_2(_1)
												}
											},
											requestParams : {
												willHandleError : true
											}
										})
							}
							;
							if (_5.length == 0) {
								_2(_1)
							}
						} else {
							_2(_1)
						}
					},
					isc.A.$875 = function isc_MockupImporter__handleSymbolOverride(
							_1, _2) {
						if (_1.controlID) {
							_1.controlID = _2.ID + "$876" + _1.controlID
						} else {
							_1.controlID = _2.ID
						}
						_1.x += _2.left;
						_1.y += _2.top;
						_1.zOrder = parseInt(_1.zOrder) + _2.zIndex - 1000000;
						if (_2.override) {
							var _3 = _2.override.controlID.split(":");
							var _4 = _1;
							for (var i = 0; i < _3.length; i++) {
								var _6 = _4.groupChildrenDescriptors.control;
								for (var j = 0; j < _6.length; j++) {
									if (_6[j].controlID == _3[i]) {
										_4 = _6[j];
										break
									}
								}
							}
							;
							for ( var _8 in _2.override) {
								for ( var _9 in _4) {
									if (_8 != "controlID" && _8 == _9) {
										_4[_9] = _2.override[_8]
									}
								}
								for ( var _9 in _4.controlProperties) {
									if (_8 == _9) {
										_4.controlProperties[_9] = _2.override[_8]
									}
								}
							}
						}
						return _1
					},
					isc.A.$132v = function isc_MockupImporter__realignControlsOfSymbol(
							_1) {
						var _2 = _1.groupChildrenDescriptors.control;
						var _3 = _2[0].x;
						var _4 = _2[0].y;
						for (var i = 1; i < _2.length; i++) {
							if (_2[i].x < _3)
								_3 = _2[i].x;
							if (_2[i].y < _4)
								_4 = _2[i].y
						}
						for (var i = 0; i < _2.length; i++) {
							_2[i].x -= _3;
							_2[i].y -= _4
						}
					},
					isc.A.convertGroup = function isc_MockupImporter_convertGroup(
							_1) {
						var _2 = this.$98p;
						var _3 = [];
						var _4 = _1.groupChildrenDescriptors.control;
						if (!isc.isA.Array(_4)) {
							_4 = [ _4 ]
						}
						for (var i = 0; i < _4.length; i++) {
							var _6 = _4[i];
							var _7;
							if ("__group__" == _6.controlTypeID) {
								_7 = this.convertGroup(_6)
							} else {
								_7 = this.convertControl(_6)
							}
							for (var j = 0; j < _7.length; j++) {
								var _9 = _2.propertyTranslations;
								if (_7[j][_9.x] != null) {
									_7[j][_9.x] = parseInt(_7[j][_9.x])
											+ parseInt(_1.x)
								}
								if (_7[j][_9.y] != null) {
									_7[j][_9.y] = parseInt(_7[j][_9.y])
											+ parseInt(_1.y)
								}
								if (_7[j][_9.zOrder] != null) {
									_7[j][_9.zOrder] = parseInt(_7[j][_9.zOrder])
											+ parseInt(_1.zOrder)
								}
								_7[j].ID = "group_" + _1.controlID + "_"
										+ _7[j].ID;
								if (_7[j].dataSource != null) {
									_7[j].dataSource = "group_" + _1.controlID
											+ "_" + _7[j].dataSource
								}
							}
							_3.addList(_7)
						}
						return _3
					},
					isc.A.convertControl = function isc_MockupImporter_convertControl(
							_1) {
						var _2 = this.$98p;
						var _3 = this.getSCClass(_1.controlTypeID);
						var _4 = {
							ID : "control" + _1.controlID,
							_constructor : _3,
							specialProperties : {
								controlName : _1.controlTypeID
							}
						};
						if (_3 == null) {
							_3 = "MockupElement";
							_4._constructor = _3;
							_4.controlName = _1.controlTypeID
						}
						for ( var _5 in _1) {
							if (_5 != "controlProperties"
									&& _5 != "controlTypeID") {
								var _6 = _1[_5];
								var _7 = this.getSCPropertyName(
										_1.controlTypeID, _5, _6);
								if (_7 != null) {
									_4[_7] = _6
								} else {
									if (!this.dropExtraProperties
											|| this.allowedExtraProperties
													.contains(_5)) {
										_4[_5] = _6
									} else {
										_4.specialProperties[_5] = _6
									}
								}
							}
						}
						var _8 = undefined;
						if (_1.controlProperties != null) {
							for ( var _5 in _1.controlProperties) {
								var _6 = _1.controlProperties[_5];
								if (typeof _6 == "string")
									_6 = unescape(_6);
								if (_8 === undefined && _5 == "markup") {
									_8 = (_6 == "true")
								}
								if (_5 == "customID") {
									var _9 = decodeURIComponent(_6);
									if (isc.MockupImporter.$970(_9)) {
										_4.ID = _9
									} else {
										this
												.logWarn("Ignoring invalid customID \""
														+ _9 + "\".")
									}
								} else if (_5 == "customData") {
									var _10 = decodeURIComponent(_6), _11 = isc.MockupImporter
											.$975(_10), _12 = _11.keys, _13 = _11.values, _14 = _11.errors, i, _16;
									if (!_14.isEmpty()) {
										var _17 = isc.StringBuffer.create();
										_17
												.append("Ignoring invalid customData configurations:  ");
										for (i = 0, _16 = _14.length; i < _16; ++i) {
											if (i > 0)
												_17.append(", ");
											_17.append("\"", _14[i], "\"")
										}
										this.logWarn(_17.release(false))
									}
									var _18 = isc.MockupImporter.$970;
									for (i = 0, _16 = _12.length; i < _16; ++i) {
										var _19 = _12[i], _6 = _13[i];
										if (_18(_19)) {
											if (_19 == "schemaName") {
												_4.$97p = _6;
												delete _4._constructor;
												_3 = null
											} else if (_19 == "constructor") {
												_3 = _4._constructor = _6
											} else {
												_4[_19] = _6
											}
										} else {
											this
													.logWarn("Ignoring customData for invalid property name \""
															+ _19 + "\".")
										}
									}
								} else {
									var _7 = this.getSCPropertyName(
											_1.controlTypeID, _5, _6);
									_6 = this.getSCPropertyValue(
											_1.controlTypeID, _5, _6);
									if (_7 != null) {
										_4[_7] = _6
									} else {
										if (!this.dropExtraProperties
												|| this.allowedExtraProperties
														.contains(_5)) {
											_4[_5] = _6
										} else {
											_4.specialProperties[_5] = _6
										}
									}
								}
							}
						}
						var _20 = _4.specialProperties.controlName, _21 = _2.markupItems
								.contains(_20);
						_4.specialProperties.markup = _8
								|| (_8 === undefined && _21);
						_4 = this.afterConvert(_1.controlTypeID, _3, _4);
						var _11 = [ _4 ];
						var _22 = this.getAdditionalElements(_1.controlTypeID,
								_3, _4);
						if (_22 != null) {
							if (_4.specialProperties == null) {
								_4.specialProperties = {}
							}
							_4.specialProperties.additionalElements = [];
							_4.specialProperties.additionalElements.addAll(_22);
							_22.add(_4);
							_11 = _22
						}
						return _11
					},
					isc.A.getSCClass = function isc_MockupImporter_getSCClass(
							_1) {
						return this.$98p.classTranslations[_1]
					},
					isc.A.getSCPropertyName = function isc_MockupImporter_getSCPropertyName(
							_1, _2, _3) {
						var _4 = this.$98p;
						var _5 = _4.widgetPropertyTranslations[_1];
						if (_5 != null) {
							var _6 = _5[_2];
							if (_6 != null) {
								return _6
							}
						}
						return _4.propertyTranslations[_2]
					},
					isc.A.getSCPropertyValue = function isc_MockupImporter_getSCPropertyValue(
							_1, _2, _3) {
						var _4 = this.$98p.widgetPropertyTranslations[_1];
						if (_4 != null && _4.controlPropertiesParser != null) {
							var _5 = _4.controlPropertiesParser(_2, _3);
							if (_5 != null) {
								if (this.trimWhitespace
										&& _5.MockDataSource
										&& _5.MockDataSource.mockDataType != "tree") {
									var _6 = _5.MockDataSource.mockData;
									var _7 = isc.SB.create();
									var _8 = _6.split("\n");
									for (var i = 0; i < _8.length; i++) {
										var _10 = "";
										var d = _8[i].split(",");
										for (var j = 0; j < d.length; j++) {
											_7.append(d[j].trim());
											if (j + 1 < d.length) {
												_7.append(",")
											} else if (i + 1 < _8.length) {
												_7.append("\n")
											}
										}
									}
									_5.MockDataSource.mockData = _7
											.release(false)
								}
								return _5
							}
						}
						return _3
					},
					isc.A.afterConvert = function isc_MockupImporter_afterConvert(
							_1, _2, _3) {
						var _4 = this.$98p;
						if (_3.zIndex != null) {
							_3.zIndex = 1000000 + parseInt(_3.zIndex)
						}
						if (_3.width == null || _3.width == '-1') {
							if (_3.measuredW) {
								_3.width = _3.measuredW
							} else {
								_3.width = _3.specialProperties.measuredW
							}
						}
						if (_3.height == null || _3.height == '-1') {
							if (_3.measuredH) {
								_3.height = _3.measuredH
							} else {
								_3.height = _3.specialProperties.measuredH
							}
						}
						var _5 = _4.widgetPropertyTranslations[_1];
						if (_5 && _5.afterInit) {
							_5.afterInit(_2, _3)
						}
						if (_3.height)
							_3.height = parseInt(_3.height);
						if (_3.top)
							_3.top = parseInt(_3.top);
						if (_3.left)
							_3.left = parseInt(_3.left);
						if (_3.width)
							_3.width = parseInt(_3.width);
						if (_4.formItems.contains(_2)) {
							_3.showTitle = false;
							var _6 = {
								_constructor : 'DynamicForm',
								ID : _3.ID,
								height : _3.height,
								top : _3.top,
								left : _3.left,
								width : _3.width,
								zIndex : _3.zIndex,
								title : _3.title,
								items : [ _3 ],
								specialProperties : _3.specialProperties
							};
							if (_3.title == null) {
								delete _6.title;
								_6.numCols = 1
							}
							if (_6.height < _3.height) {
								_3.height = _6.height
							}
							delete _3.ID;
							delete _3.zIndex;
							delete _3.left;
							delete _3.top;
							_3 = _6
						}
						return _3
					},
					isc.A.getAdditionalElements = function isc_MockupImporter_getAdditionalElements(
							_1, _2, _3) {
						var _4 = this.$98p.widgetPropertyTranslations[_1];
						if (_4 && _4.getAdditionalElements) {
							return _4.getAdditionalElements(_2, _3)
						}
						return null
					},
					isc.A.processHeuristics = function isc_MockupImporter_processHeuristics(
							_1) {
						var _2 = [], _3 = this.$98p;
						for (var i = 0; i < _1.length; i++) {
							if (_1[i].specialProperties) {
								var _5 = _1[i].specialProperties.controlName;
								var _6 = _3.widgetPropertyTranslations[_5];
								if (_6 && _6.addChild) {
									_2.add(_1[i]);
									_1[i].contained = [];
									_1[i].headerContained = [];
									_1[i].markupContained = []
								}
								if (_1[i].members) {
									_1[i].contained = []
								}
							}
						}
						_1 = this.processContainersHeuristic(_1, _2);
						_1 = this.processStackHeuristic(_1, _2);
						_1 = this.processFormsHeuristic(_1, _2);
						_1 = this.removeExtraContainers(_1, _2);
						_1 = this.processValuesManagers(_1, _2);
						_1 = this.processVLayoutForms(_1, _2);
						_1 = this.processAddingToContainersHeuristic(_1, _2);
						if (this.fillSpace) {
							_1 = this.processFluidLayoutHeuristic(_1, _2)
						}
						return _1
					},
					isc.A.processVLayoutForms = function isc_MockupImporter_processVLayoutForms(
							_1, _2) {
						for (var i = 0; i < _2.length; i++) {
							var _4 = _2[i];
							if (_4._constructor == "VLayout"
									|| _4._constructor == "VStack") {
								for (var j = 0; j < _4.contained.length; j++) {
									var _6 = _4.contained[j];
									if (_6._constructor == "DynamicForm"
											&& (_6.items || _6.fields)) {
										var _7 = _6.items || _6.fields;
										for (var k = 0; k < _7.length; k++) {
											var _9 = _7[k];
											if (_9._constructor == "TextAreaItem") {
												_9.height = "*"
											}
										}
									}
								}
							}
							var _10 = _4._constructor == "HStack"
									|| _4._constructor == "HLayout";
							var _11 = _4._constructor == "VStack"
									|| _4._constructor == "VLayout";
							if (_10 || _11) {
								if ((_10 && (_4.specialProperties.overrideHeight || _4.specialProperties.fullHeight))
										|| (_11 && (_4.specialProperties.overrideWidth || _4.specialProperties.fullWidth))) {
									for (var j = 0; j < _4.contained.length; j++) {
										var _6 = _4.contained[j];
										if (_6.showResizeBar)
											_6.resizeBarTarget = "next"
									}
								}
							}
						}
						return _1
					},
					isc.A.processContainersHeuristic = function isc_MockupImporter_processContainersHeuristic(
							_1, _2) {
						var _3 = this.$98p;
						var _4 = [];
						var _5 = [];
						for (var i = 0; i < _1.length; i++) {
							if (_1[i].left != null) {
								_1[i].absX = _1[i].left
							}
							if (_1[i].top != null) {
								_1[i].absY = _1[i].top
							}
						}
						for (var i = 0; i < _1.length; i++) {
							var _7 = _1[i];
							if (_7._constructor == "MockDataSource") {
								_5.addAt(_7, 0)
							} else {
								var _8 = this.findBestContainer(_2, _7);
								if (_8 != null) {
									var _9 = _8.specialProperties.controlName;
									var _10 = _7.specialProperties.controlName;
									var _11 = _7.specialProperties.markup;
									var _12 = _3.widgetPropertyTranslations[_9];
									_7.top -= _8.absY;
									_7.left -= _8.absX;
									_7.autoDraw = false;
									if (_11) {
										_8.markupContained.add(_7);
										_7.top -= _12.getTopMargin(_8);
										_7.left -= _12.getLeftMargin(_8)
									} else {
										var _13 = null;
										if (_12.getControlAreaName) {
											_13 = _12
													.getControlAreaName(_8, _7)
										}
										if (_13) {
											_8.headerContained.add({
												controlAreaName : _13,
												control : _7
											})
										} else {
											_8.contained.add(_7);
											_7.top -= _12.getTopMargin(_8);
											_7.left -= _12.getLeftMargin(_8);
											var _14 = _3.widgetPropertyTranslations[_10];
											if (_14 && _14.sloppyEdgeControl) {
												if ((_7.left + _7.width) > _8.width) {
													_7.width = _8.width
															- _7.left
												}
												if ((_7.top + _7.height) > _8.height) {
													_7.height = _8.height
															- _7.top
												}
											}
										}
									}
									_7.top = Math.max(0, _7.top);
									_7.left = Math.max(0, _7.left)
								}
								_4.add(_7)
							}
						}
						var _15 = [];
						for (var i = 0; i < _4.length; i++) {
							var _7 = _4[i];
							var _16 = this.getAllChildItems(_7);
							if (_16.length == 0) {
								_15.add(_7, 0)
							} else {
								var _17 = -1;
								for (var j = 0; j < _16.length; j++) {
									var _19 = _15.indexOf(_16[j]);
									if (_19 >= 0 && _17 < _19) {
										_17 = _19
									}
								}
								if (_17 >= 0) {
									_15.add(_7, _17 + 1)
								} else {
									_15.add(_7, 0)
								}
							}
						}
						var _20 = [];
						for (var i = 0; i < _15.length; i++) {
							var _7 = _15[i];
							if (_2.contains(_7))
								continue;
							var _21 = false;
							for (var j = 0; j < _2.length; j++) {
								var _8 = _2[j];
								if ((_8.contained && _8.contained.contains(_7))
										|| (_8.markupContained && _8.markupContained
												.contains(_7))) {
									_21 = true;
									break
								}
								if (_8.headerContained) {
									for (var k = 0; k < _8.headerContained.length; k++) {
										if (_8.headerContained[k].control == _7) {
											_21 = true;
											break
										}
									}
									if (_21) {
										break
									}
								}
							}
							if (!_21) {
								_20.add(_7)
							}
						}
						if (_20.length > 0) {
							for (var j = 0; j < _20.length - 1; j++) {
								_20[j].autoDraw = false;
								var _23 = {
									ID : "outer_" + j,
									_constructor : "VStack",
									fake : true,
									contained : [ _20[j] ],
									markupContained : [],
									specialProperties : {
										controlName : "Stack"
									},
									top : _20[j].top,
									left : _20[j].left,
									width : _20[j].width,
									height : _20[j].height
								};
								var _24;
								do {
									_24 = false;
									for (var k = j + 1; k < _20.length; k++) {
										var _25 = _20[k];
										var _26 = _25.left;
										var _27 = _25.left + _25.width;
										var _28 = _25.top;
										var _29 = _25.top + _25.height;
										var _30 = _23.left;
										var _31 = _23.left + _23.width;
										var _32 = _23.top;
										var _33 = _23.top + _23.height;
										var _34 = _30
												- this.maxOuterControlsDistance;
										var _35 = _31
												+ this.maxOuterControlsDistance;
										var _36 = _32
												- this.maxOuterControlsDistance;
										var _37 = _33
												+ this.maxOuterControlsDistance;
										var _38 = _26 < _35 && _27 > _34
												&& _28 < _37 && _29 > _36;
										if (_38) {
											_23.contained.add(_25);
											_25.autoDraw = false;
											var _39 = Math.max(_33, _29);
											var _40 = Math.max(_31, _27);
											_23.top = Math.min(_32, _28);
											_23.left = Math.min(_30, _26);
											_23.height = _39 - _23.top;
											_23.width = _40 - _23.left;
											_20.removeAt(k);
											k--;
											_24 = true
										}
									}
								} while (_24);
								for (var k = 0; k < _23.contained.length; k++) {
									_23.contained[k].left -= _23.left;
									_23.contained[k].top -= _23.top;
									var _41 = _23.contained[k].specialProperties;
									if (_41.markup) {
										_23.markupContained
												.add(_23.contained[k]);
										_23.contained.removeAt(k);
										k--
									}
								}
								_23.absX = _23.left, _23.absY = _23.top, _2
										.add(_23);
								_15.add(_23)
							}
						}
						_15.addListAt(_5, 0);
						return _15
					},
					isc.A.getAllChildItems = function isc_MockupImporter_getAllChildItems(
							_1, _2) {
						if (_1.contained == null) {
							return []
						}
						var _3 = [];
						_3.addList(_1.contained);
						if (_2 != true) {
							if (_1.markupContained) {
								_3.addList(_1.markupContained)
							}
							if (_1.headerContained) {
								for (var i = 0; i < _1.headerContained.length; i++) {
									_3.add(_1.headerContained[i].control)
								}
							}
						}
						for (var i = 0; i < _1.contained.length; i++) {
							if (_1.contained[i].contained
									&& _1.contained[i].contained.length > 0) {
								_3.addList(this
										.getAllChildItems(_1.contained[i]))
							}
						}
						return _3
					},
					isc.A.findBestContainer = function isc_MockupImporter_findBestContainer(
							_1, _2) {
						var _3 = this.$98p;
						var _4 = _2.absX + (_2.width == null ? 0 : _2.width);
						var _5 = _2.absY + (_2.height == null ? 0 : _2.height);
						var _6 = [];
						for (var i = 0; i < _1.length; i++) {
							var _8 = _1[i];
							if (_8 == _2) {
								continue
							} else if (_8.specialProperties.markup) {
								continue
							}
							var _9 = _8.absX - 2;
							var _10 = _8.absX + _8.width + 2;
							var _11 = _8.absY - 2;
							var _12 = _8.absY + _8.height + 2;
							if (_9 <= _2.absX && _11 <= _2.absY
									&& _2.zIndex >= _8.zIndex) {
								var _13 = _3.widgetPropertyTranslations[_2.specialProperties.controlName];
								if (_13 != null && _13.sloppyEdgeControl) {
									if ((_10 + this.sloppyEdgeControlOverflow) >= _4
											&& (_12 + this.sloppyEdgeControlOverflow) >= _5) {
										_6.add(_8)
									}
								} else {
									if (_10 >= _4 && _12 >= _5) {
										_6.add(_8)
									}
								}
							}
						}
						if (_6.length > 0) {
							var _8 = _6[0];
							for (var i = 1; i < _6.length; i++) {
								if (_8.width > _6[i].width
										|| _8.height > _6[i].height) {
									_8 = _6[i]
								}
							}
							return _8
						} else {
							return null
						}
					},
					isc.A.processStackHeuristic = function isc_MockupImporter_processStackHeuristic(
							_1, _2) {
						var _3 = this.$98p;
						for (var i = 0; i < _2.length; i++) {
							var _5 = _2[i];
							var _6 = _5._constructor == "HStack"
									|| _5._constructor == "HLayout";
							for (var _7 = 0; _7 < _5.contained.length; _7++) {
								var _8 = _5.contained[_7];
								if (_8._constructor == "Scrollbar") {
									var _9 = _8.specialProperties.controlName;
									var _10 = _5.specialProperties.controlName;
									var _11 = _3.widgetPropertyTranslations[_10];
									var _12 = false;
									if (_9 == "com.balsamiq.mockups::VerticalScrollBar") {
										var _13 = _8.left + _8.width;
										var _14 = _5.width
												- _11.getRightMargin(_5);
										_12 = Math.abs(_14 - _13) < 10
									} else {
										var _15 = _8.top + _8.height;
										var _16 = _5.height
												- _11.getBottomMargin(_5);
										_12 = Math.abs(_16 - _15) < 10
									}
									if (_12) {
										_5.overflow = "auto";
										_1.remove(_8);
										_5.contained.removeAt(_7);
										_7--
									}
								}
							}
							_5.contained.sort(function(_34, _35) {
								if (_34.top == _35.top) {
									return _34.left - _35.left
								}
								return _34.top - _35.top
							});
							this.handleElementsOverlap(_5.contained);
							this.addLabelsToFormItems(_1, _5);
							var _17 = this.splitElementsByContainers(
									_5.contained, "top", "height");
							var _18 = this.splitElementsByContainers(
									_5.contained, "left", "width");
							var _19;
							if (_18.size() > 1
									&& _18.size() < 5
									&& (_17.size() < 2 || _18.size() < _17
											.size())
									&& _5._constructor != "HStack"
									&& _5._constructor != "HLayout") {
								_19 = this.processStacksRecursively(_5,
										"root_horizontal");
								var _20 = {
									_constructor : "HStack",
									ID : _5.ID + "_root",
									contained : _5.contained,
									specialProperties : {
										controlName : "Stack",
										containerName : "HStack",
										fullWidth : true,
										fullHeight : true
									}
								};
								var _21 = 1000000;
								var _22 = 0;
								for (var j = 0; j < _20.contained.length; j++) {
									if (_20.contained[j].zIndex) {
										_21 = Math.max(_21,
												_20.contained[j].zIndex)
									}
									if (_20.contained[j].height) {
										_22 = Math.max(_22,
												_20.contained[j].height)
									}
								}
								;
								_20.zIndex = _21;
								_20.height = _22;
								_5.contained = [ _20 ];
								_19.add(_20)
							} else {
								_19 = this.processStacksRecursively(_5,
										"root_vertical")
							}
							_2.addListAt(_19, i);
							var _24 = _1.indexOf(_5);
							_1.addListAt(_19, _24);
							i += _19.length
						}
						for (var i = 0; i < _2.length; i++) {
							var _5 = _2[i];
							var _6 = _5._constructor == "HStack"
									|| _5._constructor == "HLayout";
							var _25 = 0;
							for (var j = 1; j < _5.contained.length; j++) {
								var _26 = _5.contained[j];
								var _27 = _5.contained[j - 1];
								var _28 = _5.contained[j + 1];
								var _9 = _26.specialProperties.controlName;
								if (_9 == "com.balsamiq.mockups::HSplitter"
										|| _9 == "com.balsamiq.mockups::HRule"
										|| _9 == "com.balsamiq.mockups::VSplitter"
										|| _9 == "com.balsamiq.mockups::VRule") {
									_27.showResizeBar = true;
									if (_28 && _28.specialProperties) {
										_28.specialProperties.overrideWidth = "*";
										if (_5._constructor == "HStack") {
											_5._constructor = "HLayout"
										}
									}
									_5.overflow = "auto";
									_1.remove(_5.contained[j]);
									_5.contained.removeAt(j);
									j--;
									continue
								}
								if ((_6 && _9 == "com.balsamiq.mockups::VerticalScrollBar")
										|| (!_6 && _9 == "com.balsamiq.mockups::HorizontalScrollBar")) {
									_5.overflow = "auto";
									if (_28 && _28.specialProperties) {
										_28.specialProperties.overrideHeight = "*";
										if (_5._constructor == "VStack") {
											_5._constructor = "VLayout"
										}
									}
									if (_6) {
										_27.width += _26.width
									} else {
										_27.height += _26.height
									}
									_1.remove(_26);
									_5.contained.removeAt(j);
									j--;
									continue
								}
								var _29;
								if (_6) {
									_29 = _26.left - _27.left - _27.width
								} else {
									_29 = _26.top - _27.top - _27.height
								}
								if (_25 == 0) {
									_25 = _29
								} else if (Math.abs(_25 - _29) > 5) {
									_25 = 0;
									break
								}
							}
							var _10 = _5.specialProperties.controlName;
							var _30 = _3.widgetPropertyTranslations[_10];
							if (_30.canUseMargin == null || _30.canUseMargin) {
								if (_25 > 0) {
									for (var j = 0; j < _5.contained.length - 1; j++) {
										if (_5.contained[j].showResizeBar != true) {
											_5.membersMargin = _25;
											break
										}
									}
								}
							} else {
								_25 = 0
							}
							for (var j = 1; j < _5.contained.length; j++) {
								var _26 = _5.contained[j];
								var _27 = _5.contained[j - 1];
								var _31 = 0;
								if (_6) {
									if (_26.absX != null && _27.absX != null) {
										_31 = _26.absX - _25
												- (_27.absX + _27.width)
									} else {
										_31 = _26.left - _25
												- (_27.left + _27.width)
									}
								} else {
									if (_26.absY != null && _27.absY != null) {
										_31 = _26.absY - _25
												- (_27.absY + _27.height)
									} else {
										_31 = _26.top - _25
												- (_27.top + _27.height)
									}
								}
								if (_27.showResizeBar) {
									var _32 = Math.round((_31 + _25 - 4) / 2);
									if (_6) {
										_27.width += _32;
										_26.width += _32;
										_26.left -= _32;
										if (_26.contained) {
											for (var k = 0; k < _26.contained.length; k++) {
												_26.contained[k].specialProperties.left = _26.contained[k].left;
												_26.contained[k].left += _32
											}
										}
									} else {
										_27.height += _32;
										_26.height += _32;
										_26.top -= _32;
										if (_26.contained) {
											for (var k = 0; k < _26.contained.length; k++) {
												_26.contained[k].specialProperties.top = _26.contained[k].top;
												_26.contained[k].top += _32
											}
										}
									}
								} else if (_31 > 0) {
									_27.extraSpace = _31
								}
							}
						}
						return _1
					});
	isc.evalBoundary;
	isc.B
			.push(
					isc.A.addLabelsToFormItems = function isc_MockupImporter_addLabelsToFormItems(
							_1, _2) {
						for (var i = 0; i < _2.contained.length; i++) {
							var _4 = _2.contained[i];
							if (_4._constructor != "Label")
								continue;
							for (var j = 0; j < _2.contained.length; j++) {
								var _6 = _2.contained[j], _7 = _6.items
										|| _6.fields;
								if (_6._constructor != "DynamicForm"
										|| _7 == null) {
									continue
								}
								var _8 = (_6.top > _4.top)
										&& (_6.top - (_4.top + _4.height) < this.labelMaxOffset)
										&& ((Math.abs(_6.left - _4.left) < this.labelMaxOffset) || (_4.left <= _6.left && (_4.left + _4.width) >= (_6.left + _6.width)));
								var _9 = (_6.left > _4.left)
										&& (_6.left - (_4.left + _4.width) < 2 * this.labelMaxOffset)
										&& (_4.top + this.labelMaxOffset > _6.top)
										&& (_4.top + _4.height
												- this.labelMaxOffset < _6.top
												+ _6.height);
								if (_8 || _9) {
									if (_7[0].title) {
										if (_8) {
											var _10 = false;
											for (var l = 0; l < _2.contained.length; l++) {
												var _12 = _2.contained[l];
												if (i != l
														&& _12._constructor == "Label") {
													var _13 = (_4.top > _12.top && (_4.top - (_12.top + _12.height)) < this.labelMaxOffset)
															&& (Math
																	.abs(_4.left
																			- _12.left) < this.labelMaxOffset);
													if (_13) {
														_10 = true;
														break
													}
												}
											}
											if (_10) {
												continue
											}
										}
									} else {
										_7[0].showTitle = true;
										if (_4.contents.endsWith(":")) {
											_4.contents = _4.contents
													.substring(
															0,
															_4.contents.length - 1)
										}
										_7[0].title = _4.contents;
										if (_8) {
											_6.numCols = 1;
											_7[0].titleOrientation = "top";
											var _14 = 17;
											_6.height += _14;
											_6.top = Math.max(0, _6.top - _14);
											_6.absY -= _14
										} else {
											var _15 = _6.width;
											_6.numCols = 2;
											_6.width = _6.left + _6.width
													- _4.left;
											_6.left = _4.left;
											_6.absX = _4.absX;
											if (_6.height > _4.height * 2) {
												var _16 = Math.abs(_4.top
														- _6.top);
												var _17 = Math.abs(_6.top
														+ _6.height / 2
														- _4.top - _4.height
														/ 2);
												var _18 = Math.abs(_6.height
														- _4.top - _4.height);
												if (_16 < _17 && _16 < _18) {
													_7[0].titleVAlign = "top"
												} else if (_18 < _17
														&& _18 < _16) {
													_7[0].titleVAlign = "bottom"
												}
											}
											if (isc.isA.String(_7[0].width)) {
												_6.titleWidth = _6.width - _15
														+ 1
											} else {
												_6.titleWidth = _6.width
														- _7[0].width + 1
											}
										}
										_2.contained.removeAt(i);
										_1.remove(_4);
										i--;
										break
									}
								}
							}
						}
					},
					isc.A.processStacksRecursively = function isc_MockupImporter_processStacksRecursively(
							_1, _2) {
						_1.contained.sort(function(_17, _18) {
							if (_17.top == _18.top) {
								return _17.left - _18.left
							}
							return _17.top - _18.top
						});
						var _3 = _1.contained;
						if (_2 == "vertical" || _2 == "root_vertical") {
							var _4 = this.splitElementsByContainers(_3, "top",
									"height");
							if (_2 == "vertical" && _4.length == 1) {
								return []
							}
							_4.sort(function(_17, _18) {
								return _17.top - _18.top
							});
							_1.contained = [];
							var _5 = [];
							for (var i = 0; i < _4.length; i++) {
								var _7 = _4[i];
								if (_7.children.length == 1) {
									_1.contained.add(_7.children[0])
								} else {
									var _8 = {
										_constructor : "HStack",
										ID : _1.ID + "$852" + i,
										contained : _7.children,
										top : _7.top,
										height : _7.height,
										absY : _7.children[0].absY,
										absX : _7.children[0].absX,
										specialProperties : {
											controlName : "Stack"
										}
									};
									var _9 = 1000000;
									var _10 = 1000000;
									var _11 = 0;
									for (var j = 0; j < _7.children.length; j++) {
										var _13 = _7.children[j];
										_13.top -= _8.top;
										if (_13.top < 0)
											_13.top = 0;
										if (_13.zIndex) {
											_9 = Math.max(_9, _13.zIndex)
										}
										_10 = Math.min(_10, _13.left);
										_11 = Math.max(_11, _13.left
												+ _13.width)
									}
									_8.zIndex = _9;
									_8.width = _11 - _10;
									_8.left = _10;
									for (var j = 0; j < _7.children.length; j++) {
										_7.children[j].left -= _8.left
									}
									var _14 = this.processStacksRecursively(_8,
											"horizontal");
									_1.contained.add(_8);
									if (_14.length != 0) {
										_5.addList(_14)
									}
									_5.add(_8)
								}
							}
							return _5
						} else {
							var _4 = this.splitElementsByContainers(_3, "left",
									"width");
							if (_2 == "horizontal" && _4.length == 1) {
								return []
							}
							_4.sort(function(_17, _18) {
								return _17.left - _18.left
							});
							_1.contained = [];
							var _5 = [];
							for (var i = 0; i < _4.length; i++) {
								var _7 = _4[i];
								if (_7.children.length == 1) {
									_1.contained.add(_7.children[0])
								} else {
									var _8 = {
										_constructor : "VStack",
										ID : _1.ID + "$853" + i,
										contained : _7.children,
										left : _7.left,
										width : _7.width,
										absX : _7.children[0].absX,
										absY : _7.children[0].absY,
										autoDraw : false,
										specialProperties : {
											controlName : "Stack"
										}
									};
									var _9 = 1000000;
									var _15 = 1000000;
									var _16 = 0;
									for (var j = 0; j < _7.children.length; j++) {
										var _13 = _7.children[j];
										_13.left -= _8.left;
										if (_13.left < 0) {
											_13.left = 0
										}
										if (_13.zIndex) {
											_9 = Math.max(_9, _13.zIndex)
										}
										_15 = Math.min(_15, _13.top);
										_16 = Math.max(_16, _13.top
												+ _13.height)
									}
									_8.zIndex = _9;
									_8.height = _16 - _15;
									_8.top = _15;
									for (var j = 0; j < _7.children.length; j++) {
										_7.children[j].top -= _8.top
									}
									var _14 = this.processStacksRecursively(_8,
											"vertical");
									_1.contained.add(_8);
									if (_14.length != 0) {
										_5.addList(_14)
									}
									_5.add(_8)
								}
							}
							return _5
						}
					},
					isc.A.splitElementsByContainers = function isc_MockupImporter_splitElementsByContainers(
							_1, _2, _3) {
						var _4 = [], _5 = this.$98p;
						for (var i = 0; i < _1.length; i++) {
							var _7 = _1[i];
							var _8 = _7[_3];
							var _9 = _7.specialProperties.controlName;
							var _10 = _5.widgetPropertyTranslations[_9];
							if (_10 && _10.sloppyEdgeControl
									&& _10.estimateControlSize) {
								_8 = _10.estimateControlSize(_7)[_3];
								_7[_3] = _8
							}
							var _11 = null;
							for (var j = 0; j < _4.length; j++) {
								var _13 = _4[j];
								var _14 = _8 / 4;
								var _15 = _13[_3];
								_15 /= 4;
								var _16 = Math.min(_14, _15);
								if ((_7[_2] + _16 >= _13[_2])
										&& (_7[_2] + _16 < _13[_2] + _13[_3])) {
									_11 = _13;
									break
								}
							}
							if (_11 != null) {
								_11.children.add(_7);
								if (_7[_2] < _11[_2]
										|| (_7[_2] + _8 > _11[_2] + _11[_3])) {
									var _17 = _11[_2] + _11[_3];
									var _18 = _7[_2] + _7[_3];
									_11[_2] = Math.min(_7[_2], _11[_2]);
									_11[_3] = Math.max(_17, _18) - _11[_2];
									for (var j = 0; j < _4.length; j++) {
										var _13 = _4[j];
										var _14 = _8 / 4;
										var _15 = _13[_3];
										_15 /= 4;
										var _16 = Math.min(_14, _15);
										if (_13 != _11
												&& (_11[_2] + _16) < (_13[_2] + _13[_3])
												&& (_11[_2] + _11[_3]) > (_13[_2] + _16)) {
											_11.children.addList(_13.children);
											if (_11[_2] > _13[_2]) {
												_11[_2] = _13[_2]
											}
											if (_11[_2] + _11[_3] < _13[_2]
													+ _13[_3]) {
												_11[_3] = _13[_2] + _13[_3]
														- _11[_2]
											}
											_4.removeAt(j);
											j--
										}
									}
								}
							} else {
								var _19 = {
									children : [ _7 ]
								};
								_19[_2] = _7[_2];
								_19[_3] = _8;
								_4.add(_19)
							}
						}
						return _4
					},
					isc.A.handleElementsOverlap = function isc_MockupImporter_handleElementsOverlap(
							_1) {
						for (var i = 0; i < _1.length - 1; i++) {
							for (var j = i + 1; j < _1.length; j++) {
								var _4 = _1[i];
								var _5 = _1[j];
								var _6 = _4.left + _4.width;
								var _7 = _4.top + _4.height;
								var _8 = _5.left + _5.width;
								var _9 = _5.top + _5.height;
								if (_4.left < _8 && _6 > _5.left && _4.top < _9
										&& _7 > _5.top) {
									var _10 = Math.abs(_7 - _5.top);
									var _11 = Math.abs(_6 - _5.left);
									if (_10 > 0 && _10 < this.maxControlOverlap) {
										_4.height -= _10 + 1;
										if (_4._constructor == "DynamicForm"
												&& (_4.items || _4.fields)) {
											var _12 = _4.items || _4.fields;
											for (var k = 0; k < _12.length; k++) {
												_12[k].height = Math.min(
														_12[k].height,
														_4.height - 2)
											}
										}
									}
									if (_11 > 0 && _11 < this.maxControlOverlap) {
										_4.width -= _11 + 1;
										if (_4._constructor == "DynamicForm"
												&& (_4.items || _4.fields)) {
											var _12 = _4.items || _4.fields;
											for (var k = 0; k < _12.length; k++) {
												_12[k].width = Math.min(
														_12[k].width,
														_4.width - 2)
											}
										}
									}
								}
							}
						}
					},
					isc.A.processFormsHeuristic = function isc_MockupImporter_processFormsHeuristic(
							_1, _2) {
						var _3 = this.$98p;
						var _4 = [];
						for (var i = 0; i < _2.length; i++) {
							var _6 = _2[i];
							if (this.isFormsOnlyContainer(_6)) {
								_4.add(_6)
							}
						}
						var _7 = [];
						for (var i = 0; i < _2.length; i++) {
							var _6 = _2[i];
							if (_4.contains(_6))
								continue;
							var _8 = -1;
							var _9 = -1;
							for (var j = 0; j < _6.contained.length; j++) {
								var _11 = _6.contained[j];
								var _12 = (j == (_6.contained.length - 1));
								if ((_11._constructor == "DynamicForm" && (_11.items != null || _11.fields != null))
										|| (_11._constructor != "DynamicForm"
												&& _4.contains(_11) && this.$87q
												.contains(_11._constructor))) {
									if (_8 < 0)
										_8 = j;
									_9 = j
								} else {
									_12 = true
								}
								if (_12 && _8 >= 0) {
									if (_8 != _9) {
										_7.add({
											container : _6,
											startInd : _8,
											endInd : _9
										});
										for (var k = _8; k <= _9; k++) {
											_4.remove(_6.contained[k]);
											var _14 = this.getAllChildItems(
													_6.contained[k], true);
											for (var _15 = 0; _15 < _14.length; _15++) {
												if (_14[_15].contained)
													_4.remove(_14[_15])
											}
										}
									}
									_8 = -1;
									_9 = -1
								}
							}
						}
						for (var i = 0; i < _4.length; i++) {
							for (var j = 0; j < _4.length; j++) {
								if (_4[j].contained.contains(_4[i])) {
									_4.removeAt(i);
									i--;
									break
								}
							}
						}
						for (var i = 0; i < _4.length; i++) {
							var _16 = _4[i];
							var _17 = this.getAllChildItems(_16, true)
							if (_17.length <= 1)
								continue;
							var _18 = this.combineItemsIntoAForm(_17);
							var _19 = _16.specialProperties.controlName;
							var _20 = _3.widgetPropertyTranslations[_19];
							_18.left = Math.max(0, _18.absX - _16.absX
									- _20.getLeftMargin(_16));
							_18.top = Math.max(0, _18.absY - _16.absY
									- _20.getTopMargin(_16));
							_18.specialProperties = {};
							_18.specialProperties.calculatedHeight = _18.calculatedHeight;
							delete _18.calculatedHeight;
							_18.ID = "f_" + i;
							_16.contained = [ _18 ];
							delete _18.additionalExtraSpace;
							_1.addAt(_18, _1.indexOf(_16));
							for (var j = 0; j < _17.length; j++) {
								_1.remove(_17[j]);
								_2.remove(_17[j])
							}
						}
						for (var i = 0; i < _7.length; i++) {
							var _21 = _7[i];
							var _22 = _21.container;
							var _17 = [];
							for (var j = _21.startInd; j <= _21.endInd; j++) {
								var _23 = _22.contained[j];
								if (_23.contained) {
									_17.addList(this
											.getAllChildItems(_23, true))
								} else {
									_17.add(_23)
								}
							}
							if (_17.length <= 1)
								continue;
							var _18 = this.combineItemsIntoAForm(_17);
							_18.left = _22.contained[_21.startInd].left;
							_18.top = _22.contained[_21.startInd].top;
							_18.specialProperties = {};
							_18.specialProperties.calculatedHeight = _18.calculatedHeight;
							delete _18.calculatedHeight;
							_18.ID = "pf_" + i;
							var _24 = _22.contained[_21.endInd]
							if (_24.extraSpace) {
								_18.extraSpace = _24.extraSpace
							}
							for (var j = _21.endInd; j >= _21.startInd; j--) {
								_1.remove(_22.contained[j]);
								_2.remove(_22.contained[j]);
								_22.contained.removeAt(j)
							}
							;
							_22.contained.addAt(_18, _21.startInd);
							_1.addAt(_18, _1.indexOf(_22));
							for (var j = 0; j < _17.length; j++) {
								_1.remove(_17[j]);
								_2.remove(_17[j])
							}
						}
						for (var i = 0; i < _1.length; i++) {
							var _11 = _1[i], _25 = _11.items || _11.fields;
							if (_11._constructor == "DynamicForm" && _25
									&& _25.length == 1
									&& _25[0]._constructor == "ButtonItem") {
								var _26 = _11.extraSpace;
								var _27 = _25[0];
								if (_11.extraSpace)
									_27.extraSpace = _11.extraSpace;
								_27.left = _11.left;
								_27.top = _11.top;
								_27._constructor = "Button";
								delete _27.startRow;
								delete _27.endRow;
								for ( var _28 in _11) {
									if (_28 != "ID"
											&& _28 != "specialProperties") {
										delete _11[_28]
									}
								}
								for ( var _28 in _27) {
									_11[_28] = _27[_28]
								}
							} else if (_11._constructor == "DynamicForm" && _25) {
								var _18 = _11;
								var _29 = _25;
								var _30 = _18.numCols || 2;
								var _31 = 0;
								for (var j = 0; j < _29.length; j++) {
									var _11 = _29[j];
									var _32 = null;
									var _33 = _11.colSpan
											|| (_11.showTitle ? 2 : 1);
									var _34 = [ _11 ];
									var _35 = [ _11.title ];
									var _36 = [];
									var _37 = null;
									if (_11.value == true) {
										_37 = _11.title
									}
									var _38 = _30 > _33 && _11.endRow != true;
									_31 += _33;
									if (_11._constructor == "RadioItem") {
										for (var k = j + 1; k < _29.length; k++) {
											_32 = _29[k];
											if (_32._constructor != "RadioItem")
												break;
											if (_32.value == true) {
												if (_37 != null)
													break;
												_37 = _32.title
											}
											_34.add(_32);
											_35.add(_32.title);
											if (_32.disabled) {
												_36.add(_32.title)
											}
											var _39 = _32.colSpan
													|| (_32.showTitle ? 2 : 1);
											_31 += _39;
											if (_32.endRow || _31 == _30) {
												if (_38)
													break;
												_31 = 0
											}
										}
									}
									if (_34.length > 1) {
										_29.removeList(_34);
										var _40 = {
											_constructor : "RadioGroupItem",
											type : "radioGroup",
											showTitle : false,
											valueMap : _35,
											value : _11.title
										};
										if (_34[0].cellHeight) {
											_40.cellHeight = _34[0].cellHeight
										}
										if (_36.length > 0) {
											_40.disabledValues = _36
										}
										if (_38) {
											_40.vertical = false;
											_40.colSpan = _31;
											if (_32.endRow) {
												_40.endRow = true
											}
										}
										_29.addAt(_40, j)
									}
									if (_11.endRow || _31 == _30
											|| (_32 && _32.endRow)) {
										_31 = 0
									}
								}
								var _24 = _29[_29.length - 1];
								var _26 = 0;
								if (_18.extraSpace) {
									_26 = _18.extraSpace
								}
								if (_24.extraSpace) {
									_26 += _24.extraSpace
								}
								if (_18.additionalExtraSpace) {
									_26 += _18.additionalExtraSpace;
									_18.height -= _18.additionalExtraSpace;
									delete _18.additionalExtraSpace
								}
								if (_26 > 0) {
									_18.extraSpace = _26
								}
							}
						}
						return _1
					},
					isc.A.isFormsOnlyContainer = function isc_MockupImporter_isFormsOnlyContainer(
							_1) {
						var _2 = this.getAllChildItems(_1, true)
						if (_2.length == 0)
							return false;
						var _3 = true;
						for (var j = 0; j < _2.length; j++) {
							var _5 = _2[j]._constructor == "DynamicForm";
							var _6 = _2[j]._constructor == "Label"
									&& _2[j].icon == null;
							var _7 = this.$87q.contains(_2[j]._constructor);
							if (!_5 && !_6 && !_7)
								return false;
							if (_5 && _2[j].items == null
									&& _2[j].fields == null)
								return false;
							_3 = _3 && (_6 || _7)
						}
						return !_3
					},
					isc.A.combineItemsIntoAForm = function isc_MockupImporter_combineItemsIntoAForm(
							_1) {
						var _2 = [];
						for (var i = 0; i < _1.length; i++) {
							if (_1[i]._constructor == "Label") {
								var _4 = _1[i];
								_4._constructor = "DynamicForm";
								_4.items = [ {
									_constructor : "StaticTextItem",
									showTitle : false,
									width : _4.width,
									value : _4.contents
								} ]
							}
						}
						for (var j = 0; j < _1.length; j++) {
							if (_1[j]._constructor != "DynamicForm"
									|| (_1[j].items == null && _1[j].fields == null))
								continue;
							var x = _1[j].absX;
							var y = _1[j].absY;
							var _8 = _1[j].orientation == "horizontal";
							var _9 = _1[j].items || _1[j].fields;
							for (var k = 0; k < _9.length; k++) {
								var _11 = _9[k];
								_11.$86q = {
									x : x,
									y : y,
									width : _11.width ? _11.width : _1[j].width,
									height : _11.height ? _11.height
											: this.formsGridCellHeight
								};
								if (_11.title
										&& !this.$87t
												.contains(_11._constructor)) {
									if (_11.titleOrientation == "top") {
										_11.$86q.height += 17
									} else if (_1[j].titleWidth) {
										_11.$86q.x += _1[j].titleWidth;
										_11.titleWidth = _1[j].titleWidth
									}
								}
								if (_8) {
									x += _11.$86q.width
								} else {
									y += _11.$86q.height
								}
								_2.add(_11)
							}
						}
						var _12 = {
							_constructor : "DynamicForm",
							items : []
						}
						var _13 = 10000;
						var _14 = 10000;
						for (var j = 0; j < _2.length; j++) {
							_13 = Math.min(_13, _2[j].$86q.x);
							_14 = Math.min(_14, _2[j].$86q.y)
						}
						_12.absX = _13;
						_12.absY = _14;
						var _15 = 0;
						var _16 = 0;
						var _17 = [];
						var _18 = [];
						for (var j = 0; j < _2.length; j++) {
							_2[j].$86q.x -= _13;
							_2[j].$86q.y -= _14;
							_15 = Math
									.max(_15, _2[j].$86q.x + _2[j].$86q.width);
							_16 = Math.max(_16, _2[j].$86q.y
									+ _2[j].$86q.height);
							if (!_17.contains(_2[j].$86q.x)) {
								_17.add(_2[j].$86q.x)
							}
							if (!_18.contains(_2[j].$86q.y)) {
								_18.add(_2[j].$86q.y)
							}
						}
						_17.sort(function(_51, _52) {
							return _51 - _52
						});
						_18.sort(function(_51, _52) {
							return _51 - _52
						});
						for (var i = 0; i < _17.length - 1; i++) {
							if (_17[i + 1] - _17[i] < this.formsGridCellWidth) {
								for (var j = 0; j < _2.length; j++) {
									if (_2[j].$86q.x == _17[i + 1]) {
										_2[j].$86q.width += (_17[i + 1] - _17[i]);
										_2[j].$86q.x = _17[i]
									}
								}
								_17.removeAt(i + 1);
								i--
							}
						}
						for (var i = 0; i < _18.length - 1; i++) {
							if (_18[i + 1] - _18[i] < this.formsGridCellHeight * 2 / 3) {
								for (var j = 0; j < _2.length; j++) {
									if (_2[j].$86q.y == _18[i + 1]) {
										_2[j].$86q.height += (_18[i + 1] - _18[i]);
										_2[j].$86q.y = _18[i]
									}
								}
								_18.removeAt(i + 1);
								i--
							}
						}
						var _19 = [];
						for (var _20 = 0; _20 < _18.length; _20++) {
							var _21 = [];
							_19.add(_21);
							for (var _22 = 0; _22 < _17.length; _22++) {
								_21.add(null)
							}
						}
						for (var j = 0; j < _2.length; j++) {
							var _11 = _2[j];
							var _23 = 0;
							var _24 = 0;
							for (var _20 = 0; _20 < _18.length; _20++) {
								if (_11.$86q.y >= _18[_20]) {
									_23 = _20;
									_24 = _20
								}
								if ((_11.$86q.y + _11.$86q.height) <= _18[_20]) {
									break
								}
								_24 = _20
							}
							var _25 = 0;
							var _26 = 0;
							for (var _22 = 0; _22 < _17.length; _22++) {
								if (_11.$86q.x >= _17[_22]) {
									_25 = _22;
									_26 = _22
								}
								if ((_11.$86q.x + _11.$86q.width) <= _17[_22]) {
									break
								}
								_26 = _22
							}
							if (_26 - _25 >= 1) {
								_11.colSpan = _26 - _25 + 1
							}
							if (_24 - _23 >= 1) {
								_11.rowSpan = _24 - _23 + 1
							}
							delete _11.$86q;
							for (var _22 = _25; _22 <= _26; _22++) {
								for (var _20 = _23; _20 <= _24; _20++) {
									var _27 = _19[_20][_22];
									if (_27) {
										if (_22 > 0 && _27 == _19[_20][_22 - 1]) {
											if (_27.colSpan) {
												_27.colSpan--;
												if (_27.colSpan == 1)
													delete _27.colSpan
											} else if (_27.titleColSpan) {
												_27.titleColSpan--;
												if (_27.titleColSpan == 1)
													delete _27.titleColSpan
											}
										} else if (_20 > 0
												&& _27 == _19[_20 - 1][_22]) {
											_27.rowSpan--;
											for (var i = _22 + 1; i < _19[_20].length; i++) {
												if (_19[_20][i] == _27) {
													_19[_20][i] = null
												}
											}
											if (_27.rowSpan == 1)
												delete _27.rowSpan
										}
									}
									_19[_20][_22] = _11;
									if (_11.titleWidth != null) {
										var _28 = _22 - 1;
										var _29 = 0;
										while (_28 >= 0
												&& _19[_20][_28] == null) {
											_19[_20][_28] = _11;
											_29++;
											_28--
										}
										if (_29 > 1)
											_11.titleColSpan = _29;
										if (_29 > 0)
											delete _11.titleWidth
									}
								}
							}
						}
						for (var _30 = 0; _30 < _19.length; _30++) {
							for (var _31 = 0; _31 < _19[_30].length - 1; _31++) {
								if (_19[_30][_31]
										&& _19[_30][_31 + 1]
										&& _19[_30][_31]._constructor == "StaticTextItem"
										&& _19[_30][_31 + 1]._constructor != "StaticTextItem"
										&& _19[_30][_31 + 1]._constructor != "SpacerItem"
										&& _19[_30][_31 + 1]._constructor != "ButtonItem"
										&& _19[_30][_31 + 1].showTitle == false) {
									if (_19[_30][_31 + 1].rowSpan) {
										var c = false;
										for (var _20 = 0; _20 < _19.length; _20++) {
											if (_19[_20][_31 + 1] == _19[_30][_31 + 1]) {
												if (_19[_20][_31] != _19[_30][_31]
														&& _19[_20][_31] != null) {
													c = true;
													break
												}
												if (_19[_20][_31] == null) {
													_19[_20][_31] = _19[_30][_31 + 1]
												}
											}
										}
										if (c) {
											continue
										}
									}
									var _33 = _19[_30][_31];
									_19[_30][_31 + 1].title = _19[_30][_31].value;
									_19[_30][_31 + 1].width += _19[_30][_31].width;
									_19[_30][_31 + 1].showTitle = true;
									_19[_30][_31] = _19[_30][_31 + 1];
									var _34 = _31 - 1;
									if (_34 >= 0 && _19[_30][_34] == _33) {
										while (_34 >= 0 && _19[_30][_34] == _33) {
											_19[_30][_34] = _19[_30][_31 + 1];
											if (_19[_30][_31 + 1].titleColSpan) {
												_19[_30][_31 + 1].titleColSpan++
											} else {
												_19[_30][_31 + 1].titleColSpan = 1
											}
											_34--
										}
									} else {
										_19[_30][_31 + 1].width -= _33.width
									}
								}
							}
						}
						var _35 = 0;
						var _36 = false;
						var _37 = isc.ListGrid
								.getInstanceProperty("cellPadding");
						var _38 = isc.TextItem.getInstanceProperty("height");
						for (var i = 0; i < _19.length; i++) {
							var _11 = _19[i][0];
							if (_11 == null)
								continue;
							if (i > 0 && _11 == _19[i - 1][0])
								continue;
							var _39 = 0;
							if (_11._constructor == "TextAreaItem") {
								_36 = true;
								_39 = _11.height
							} else if (_11._constructor == "SpacerItem") {
								for (var j = 0; j < _19[i].length; j++) {
									if (_19[i][j]._constructor != "SpacerItem") {
										_11 = _19[i][j];
										if (_11._constructor == "TextAreaItem") {
											_36 = true;
											_39 = _11.height
										} else {
											if (isc[_11._constructor])
												_39 = isc[_11._constructor]
														.getInstanceProperty("height")
										}
										break
									}
								}
							} else {
								if (isc[_11._constructor])
									_39 = isc[_11._constructor]
											.getInstanceProperty("height")
							}
							if (_39 == null || _39 == 0)
								_39 = _38;
							_35 += _37 + _39;
							if (_11.showTitle && _11.titleOrientation == "top") {
								_35 += 17
							}
						}
						if (_36) {
							_12.calculatedHeight = _35
						}
						var _40 = 0;
						for (var _20 = 0; _20 < _18.length; _20++) {
							for (var _22 = 0; _22 < _17.length; _22++) {
								var _41 = _19[_20][_22];
								if (_41 == null) {
									if (_20 != 0
											&& _19[_20 - 1][_22]._constructor == "SpacerItem") {
										_19[_20][_22] = _19[_20 - 1][_22];
										if (_19[_20][_22].rowSpan == null) {
											_19[_20][_22].rowSpan = 2
										} else {
											_19[_20][_22].rowSpan++
										}
									} else {
										if (_22 > 0
												&& _19[_20][_22 - 1]._constructor == "SpacerItem") {
											_19[_20][_22] = _19[_20][_22 - 1];
											if (_19[_20][_22].colSpan == null) {
												_19[_20][_22].colSpan = 2
											} else {
												_19[_20][_22].colSpan++
											}
										} else {
											_19[_20][_22] = {
												_constructor : "SpacerItem"
											};
											_12.items.add(_19[_20][_22])
										}
									}
								} else if (!_12.items.contains(_41)) {
									_12.items.add(_41)
								}
								_41 = _19[_20][_22];
								if (_41.rowSpan == null
										&& _20 < (_18.length - 1)) {
									var _37 = isc.ListGrid
											.getInstanceProperty("cellPadding");
									_41.cellHeight = _18[_20 + 1] - _18[_20]
											- _37;
									var _38 = isc.TextItem
											.getInstanceProperty("height");
									_38 += _37;
									if (Math.abs(_38 - _41.cellHeight) <= this.formExtraSpaceThreshold) {
										_40 += Math.abs(_38 - _41.cellHeight);
										delete _41.cellHeight
									}
									if (_41.cellHeight >= 3 * _38) {
										_41.vAlign = "top";
										_41.titleVAlign = "top"
									}
									if (_41._constructor == "ButtonItem") {
										delete _41.cellHeight
									}
								}
							}
							for (var i = _12.items.length - 1; i >= 0; i--) {
								if (_12.items[i]._constructor == "SpacerItem") {
									_12.items.removeAt(i);
									if (_20 != (_18.length - 1)) {
										_12.items[_12.items.length - 1].endRow = true
									}
								} else {
									break
								}
							}
						}
						var _42 = [];
						for (var _22 = 0; _22 < _17.length; _22++) {
							var _43 = 0;
							for (var _20 = 0; _20 < _18.length; _20++) {
								if (_19[_20][_22].titleWidth != null) {
									if (_22 == 0) {
										_43 = Math.max(_43,
												_19[_20][_22].titleWidth)
									} else {
										var _44 = _17[_22] - _17[_22 - 1]
												- _19[_20][_22 - 1].width;
										if (_43 == 0) {
											_43 = _44
										} else {
											_43 = Math.min(_43, _44)
										}
									}
								}
							}
							if (_43 > 0) {
								if (_22 != 0) {
									_42[_42.length - 1] -= _43
								} else {
									_12.absX -= _43
								}
								for (var _20 = 0; _20 < _18.length; _20++) {
									if (_19[_20][_22].titleWidth == null) {
										if (_19[_20][_22]._constructor == "ButtonItem") {
											var _31 = _12.items
													.indexOf(_19[_20][_22]);
											var _45 = _12.items[_31 - 1];
											if (_22 > 0
													&& _45._constructor == "SpacerItem") {
												if (_45.colSpan == null) {
													_45.colSpan = 2
												} else {
													_45.colSpan++
												}
											} else {
												_12.items.addAt({
													_constructor : "SpacerItem"
												}, _31)
											}
										} else {
											if (_19[_20][_22].colSpan == null) {
												_19[_20][_22].colSpan = 2
											} else {
												_19[_20][_22].colSpan++
											}
										}
									}
								}
								_42.add(_43)
							}
							if (_22 == _17.length - 1) {
								_42.add(_15 - _17[_22])
							} else {
								_42.add(_17[_22 + 1] - _17[_22])
							}
						}
						for (var i = 0; i < _2.length; i++) {
							delete _2[i].titleWidth
						}
						var _46 = "";
						_12.width = 0;
						for (var j = 0; j < _42.length; j++) {
							if (j != _42.length - 1) {
								_46 += _42[j];
								_46 += ","
							} else {
								_46 += "*"
							}
							_12.width += _42[j]
						}
						for (var j = 0; j < _19.length; j++) {
							for (var k = 0; k < _19[j].length; k++) {
								var _47 = _19[j][k];
								if (_47.width == null || _47.width == "*")
									continue;
								var _48 = _42[k];
								var _49 = _47.colSpan | _47.titleColSpan;
								if (_49) {
									for (var _50 = 1; _50 < _49; _50++) {
										_48 += _42[j + _50]
									}
									k += _49
								}
								if (Math.abs(_48 - _47.width) < 2) {
									_47.width = "*"
								}
							}
						}
						for (var i = 0; i < _12.items.length; i++) {
							var _11 = _12.items[i];
							if (_11._constructor == "TextAreaItem") {
								if (_11.rowSpan > 1
										&& _11.rowSpan != _19.length) {
									_11.height = "*"
								}
								if ((_11.colSpan || 1) != _19[0].length) {
									_11.width = "*"
								}
							}
						}
						_12.colWidths = _46;
						_12.numCols = _42.length;
						_12.height = _16;
						if (_40 > 0) {
							_12.additionalExtraSpace = _40
						}
						return _12
					});
	isc.evalBoundary;
	isc.B
			.push(
					isc.A.removeExtraContainers = function isc_MockupImporter_removeExtraContainers(
							_1, _2) {
						var _3;
						do {
							_3 = false;
							for (var i = 0; i < _2.length; i++) {
								var _5 = _2[i];
								if (_5.contained.length == 1
										&& (this.$87q.contains(_5._constructor) || "Canvas" == _5._constructor
												&& _5.contained[0]._constructor == "DynamicForm")
										&& !(_5.overflow && _5.contained[0]._constructor != "ListGrid")) {
									var _6 = _5.contained[0];
									for (var j = 0; j < _2.length; j++) {
										var _8 = _2[j].contained.indexOf(_5);
										if (_8 >= 0) {
											var _9 = _2[j].contained[_8];
											var _10 = _6.left;
											var _11 = _6.top;
											var _12 = _5.height - _11
													- _6.height;
											var _13 = _5.width - _10 - _6.width;
											var _14 = _6.specialProperties;
											_14.lm = (_14.lm || 0) + _10;
											_14.tm = (_14.tm || 0) + _11;
											_14.bm = (_14.bm || 0) + _12;
											_14.rm = (_14.rm || 0) + _13;
											var _15 = _6._constructor == "HStack"
													|| _6._constructor == "HLayout";
											var _16 = _9._constructor == "HStack"
													|| _9._constructor == "HLayout";
											var _17 = _5._constructor == "HStack"
													|| _5._constructor == "HLayout";
											if (_15 && _16 && !_17
													|| (!_15 && !_16)) {
												_6.extraSpace = (_6.extraSpace || 0)
														+ (_2[j].contained[_8].extraSpace || 0)
											} else {
												_6.extraSpace = _2[j].contained[_8].extraSpace || 0
											}
											if (_6.extraSpace == 0) {
												delete _6.extraSpace
											}
											var _18 = [ "border", "width",
													"height", "top", "left",
													"isGroup", "groupTitle",
													"markupContained" ];
											var _19 = [ "measuredW",
													"measuredH",
													"overrideWidth", "top",
													"left" ];
											for (var _20 = 0; _20 < _18.length; _20++) {
												if (_5[_18[_20]]) {
													_6[_18[_20]] = _5[_18[_20]]
												}
											}
											if (_5.specialProperties
													&& _5.contained[0].specialProperties) {
												if ((_5.specialProperties.controlName == "com.balsamiq.mockups::Canvas" || _5.specialProperties.controlName == "com.balsamiq.mockups::FieldSet")
														&& _6._constructor == "DynamicForm") {
													_19.add("controlName")
												}
												for (var _20 = 0; _20 < _19.length; _20++) {
													_6.specialProperties[_19[_20]] = _5.specialProperties[_19[_20]]
												}
											}
											_2[j].contained[_8] = _5.contained[0];
											_1.remove(_5);
											if (_6.markupContained
													&& _5.contained[0].markupContained.length > 0) {
												_6.contained = [];
												_2.set(i, _5.contained[0])
											} else {
												_2.removeAt(i);
												i--
											}
											_3 = true
											break
										}
									}
									if (!_3
											&& _5.specialProperties.controlName != "com.balsamiq.mockups::BrowserWindow") {
										if (_5.top)
											_6.top = (_6.top || 0) + _5.top;
										if (_5.left)
											_6.left = (_6.left || 0) + _5.left;
										if (_5.width)
											_6.width = _5.width;
										if (_5.height)
											_6.height = _5.height;
										if (_5.border)
											_6.border = _5.border;
										if (_5.zIndex)
											_6.zIndex = _5.zIndex;
										if (_5.isGroup)
											_6.isGroup = _5.isGroup;
										if (_5.groupTitle)
											_6.groupTitle = _5.groupTitle;
										if (_5.markupContained)
											_6.markupContained = _5.markupContained;
										_6.specialProperties.fullHeight = _5.specialProperties.fullHeight;
										_6.specialProperties.fullWidth = _5.specialProperties.fullWidth;
										delete _6.extraSpace;
										delete _6.autoDraw;
										_1.remove(_5);
										if (_6.markupContained
												&& _6.markupContained.length > 0) {
											_6.contained = [];
											_2.set(i, _6)
										} else {
											_2.removeAt(i);
											i--
										}
										_3 = true
									}
								}
							}
						} while (_3);
						return _1
					},
					isc.A.processValuesManagers = function isc_MockupImporter_processValuesManagers(
							_1, _2) {
						for (var i = 0; i < _2.length; i++) {
							if (_2[i].specialProperties != null
									&& _2[i].specialProperties.controlName != null
									&& _2[i].specialProperties.controlName
											.startsWith("com.balsamiq.mockups::")
									&& _2[i]._constructor != "DynamicForm") {
								var _4 = this
										.findDynamicFormsRecursively(_2[i]);
								if (_4.length > 1) {
									var _5 = {
										_constructor : "ValuesManager",
										ID : "vm_" + i
									};
									for (var j = 0; j < _4.length; j++) {
										_4[j].valuesManager = _5.ID;
										if (_4[j].specialProperties.additionalElements == null) {
											_4[j].specialProperties.additionalElements = []
										}
										_4[j].specialProperties.additionalElements
												.add(_5)
									}
									_1.addAt(_5, 0)
								}
								var _7 = [];
								for (var j = 0; j < _4.length; j++) {
									_7.addList(_4[j].items)
								}
								for (var j = 0; j < _7.length; j++) {
									var _8 = _7[j];
									if (_8._constructor == "SpacerItem")
										continue;
									var _9 = _8.title || _8.defaultValue
											|| _8._constructor;
									var _10 = isc.MockDataSource
											.convertTitleToName(_9,
													this.fieldNamingConvention);
									var _11 = _10;
									var _12 = 0;
									do {
										var _13 = false;
										for (var k = 0; k < _7.length; k++) {
											if (_7[k].name == _11) {
												_12++;
												_11 = _10 + _12;
												_13 = true;
												break
											}
										}
									} while (_13);
									_8.name = _11;
									if (_8._constructor == "CheckboxItem"
											&& _8.showTitle == false
											&& _8.colSpan == 2) {
										delete _8.showTitle;
										delete _8.colSpan
									}
								}
							}
						}
						return _1
					},
					isc.A.findDynamicFormsRecursively = function isc_MockupImporter_findDynamicFormsRecursively(
							_1) {
						var _2 = [];
						for (var i = 0; i < _1.contained.length; i++) {
							var _4 = _1.contained[i];
							if (_4._constructor == "DynamicForm"
									&& (_4.items != null || _4.fields != null)) {
								_2.add(_4)
							}
							if (this.$87q.contains(_4._constructor)
									|| (_4._constructor == "DynamicForm"
											&& _4.items == null && _4.fields == null)) {
								_2.addAll(this.findDynamicFormsRecursively(_4))
							}
						}
						return _2
					},
					isc.A.processAddingToContainersHeuristic = function isc_MockupImporter_processAddingToContainersHeuristic(
							_1, _2) {
						var _3 = this.$98p;
						this.cleanZIndexParam(_1, _2);
						for (var i = 0; i < _2.length; i++) {
							this.processRemoveWidths(_1, _2, _2[i]);
							var _5 = _3.widgetPropertyTranslations[_2[i].specialProperties.controlName];
							var _6 = null;
							if (_2[i].markupContained != null
									&& _2[i].markupContained.length > 0) {
								_6 = {
									_constructor : "VStack",
									ID : "widgets_container_" + i,
									position : "absolute",
									top : 0,
									autoDraw : false,
									width : "100%",
									height : "100%",
									zIndex : _2[i].zIndex,
									members : []
								};
								var _7 = {
									_constructor : "Canvas",
									height : "100%",
									width : "100%",
									autoDraw : false,
									children : [ this.$88p(_6) ]
								}
								for (var j = 0; j < _2[i].markupContained.length; j++) {
									_2[i].markupContained[j].position = "absolute";
									_7.children.add(this
											.$88p(_2[i].markupContained[j]))
								}
								_5.addChild(_2[i], _7, _1);
								_1.addAt(_6, _1.indexOf(_2[i]))
							}
							var _9 = _2[i];
							this.processLayoutMargin(_1, _2, _9, _6);
							if (_9.contained.length == 1
									&& (_9._constructor == "TabSet" || _9._constructor == "SectionStack")
									&& _9.verticalScrollBar != null
									&& _9.contained[0]._constructor == "VStack") {
								_9.contained[0]._constructor = "VLayout";
								_9.contained[0].overflow = "auto";
								delete _9.verticalScrollBar
							}
							for (var j = 0; j < _9.contained.length; j++) {
								var _10 = _9.contained[j];
								_10.autoDraw = "false";
								if (_6 != null) {
									_6.members.add(this.$88p(_10))
								} else {
									_5.addChild(_9, this.$88p(_10), _1)
								}
								if (_10._constructor == "DynamicForm"
										&& (_10.items != null || _10.fields != null)) {
									var _11 = _10.items || _10.fields;
									for (var k = 0; k < _11.length; k++) {
										var _13 = _11[k];
										delete _13.left;
										delete _13.top;
										if (!this.tallFormItems
												.contains(_13._constructor)
												&& !this.tallFormItems
														.contains(_13.type)
												&& ("SelectItem" != _13._constructor || _13.multipleAppearance != "grid")) {
											delete _13.height
										} else if (_13._constructor == "ButtonItem") {
											if (Math.abs(_13.height
													- this.defaultButtonSize) <= this.buttonMinimumChangeSize) {
												delete _13.height
											}
										}
									}
								}
							}
							if (_9.headerContained != null) {
								_9.headerContained.sort(function(_18, _19) {
									return _18.control.left - _19.control.left
								});
								for (var j = 0; j < _9.headerContained.length; j++) {
									if (j > 0
											&& _9.headerContained[j].control.specialProperties.controlName == "com.balsamiq.mockups::VSplitter"
											|| _9.headerContained[j].control.specialProperties.controlName == "com.balsamiq.mockups::VRule") {
										_9.headerContained[j - 1].control.showResizeBar = true;
										_9.overflow = "auto";
										_1
												.remove(_9.headerContained[j].control);
										_9.headerContained.removeAt(j);
										j--;
										continue
									}
									delete _9.headerContained[j].control.height;
									delete _9.headerContained[j].control.zIndex;
									delete _9.headerContained[j].control.top;
									delete _9.headerContained[j].control.left;
									_9.headerContained[j].control.autoDraw = "false";
									if (_5.addControl) {
										_5
												.addControl(
														_9,
														{
															controlAreaName : _9.headerContained[j].controlAreaName,
															control : this
																	.$88p(_9.headerContained[j].control)
														})
									} else {
										isc
												.logWarn("no add control method for "
														+ _2[i].specialProperties.controlName
														+ " unable to add "
														+ isc
																.echoAll(_9.headerContained[j].control))
									}
								}
							}
							if (_9._constructor != "DynamicForm"
									&& _9._constructor != "Canvas"
									&& (_9.markupContained == null || _9.markupContained.length == 0)) {
								for (var j = 0; j < _9.contained.length; j++) {
									var _10 = _9.contained[j];
									if (_10._constructor == "Label") {
										var _14 = _9._constructor == "HStack"
												|| _9._constructor == "HLayout";
										if (!_14
												&& _10.left > this.stackContainerFillInset) {
											_10.width += _10.left;
											_10.align = "right"
										}
										if (_14
												&& _10.top > this.stackContainerFillInset) {
											_10.height += _10.top;
											_10.valign = "bottom"
										}
									}
									delete _10.left;
									delete _10.top
								}
							}
							if (_9.fake) {
								if (_9.layoutLeftMargin == null) {
									_9.layoutLeftMargin = 0
								}
								if (_9.layoutTopMargin == null) {
									_9.layoutTopMargin = 0
								}
								if (_9.contained.length == 1
										&& _9.markupContained == 0) {
									_9.contained[0].left = _9.left
											+ _9.layoutLeftMargin;
									_9.contained[0].top = _9.top
											+ _9.layoutTopMargin;
									if (_9.contained[0].specialProperties) {
										delete _9.contained[0].specialProperties.fullWidth;
										delete _9.contained[0].specialProperties.fullHeight
									}
									delete _9.contained[0].autoDraw;
									_1.remove(_9)
								} else {
									_9.left = _9.left + _9.layoutLeftMargin;
									_9.top = _9.top + _9.layoutTopMargin;
									delete _9.layoutLeftMargin;
									delete _9.layoutTopMargin;
									delete _9.fake
								}
							}
						}
						if (this.fillSpace) {
							var _15 = null;
							for (var i = 0; i < _2.length; i++) {
								var _16 = true;
								for (var j = 0; j < _2.length; j++) {
									if (_2[j].contained.contains(_2[i])) {
										_16 = false;
										break
									}
								}
								if (_16) {
									for (var j = 0; j < _1.length; j++) {
										if (_2.contains(_1[j]))
											continue;
										var _17 = _1[j].specialProperties;
										if (_17) {
											if (_17.refs == null
													|| _17.refs.length == 0) {
												_16 = false
											}
										}
									}
								}
								if (_16) {
									if (_15 != null) {
										_15 = null;
										break
									} else {
										_15 = _2[i]
									}
								}
							}
							if (_15 != null
									&& ((this.$87q.contains(_15._constructor)
											|| "SectionStack" == _15._constructor || ((_15._constructor == "Window" || _15._constructor == "Canvas")
											&& _15.contained.length == 1
											&& this.$87q
													.contains(_15.contained[0]._constructor) && ((_15.contained[0].width == null || _15.contained[0].width == "100%") && (_15.contained[0].height == null || _15.contained[0].height == "100%")))) || (_15._constructor == "TabSet"))) {
								_15.width = "100%";
								_15.height = "100%";
								delete _15.left;
								delete _15.top
							}
						}
						for (var i = 0; i < _2.length; i++) {
							_2[i].specialProperties.innerItems = [];
							_2[i].specialProperties.innerItems
									.addList(_2[i].contained);
							_2[i].specialProperties.innerItems
									.addList(_2[i].headerContained);
							_2[i].specialProperties.innerItems
									.addList(_2[i].markupContained);
							delete _2[i].contained;
							delete _2[i].headerContained;
							delete _2[i].markupContained
						}
						return _1
					},
					isc.A.$88p = function isc_MockupImporter__getRefCanvas(_1) {
						var _2 = {
							_constructor : "Canvas",
							ref : _1.ID
						};
						if (_1.specialProperties == null) {
							_1.specialProperties = {}
						}
						if (_1.specialProperties.refs == null) {
							_1.specialProperties.refs = []
						}
						_1.specialProperties.refs.add(_2);
						return _2
					},
					isc.A.cleanZIndexParam = function isc_MockupImporter_cleanZIndexParam(
							_1, _2) {
						var _3 = this;
						var _4 = function(_10, _9) {
							var _11 = _3.getAllChildItems(_10);
							for (var j = 0; j < _11.length; j++) {
								var _12 = _11[j];
								var _13 = _12.width;
								if (_13 == null
										&& _12.specialProperties != null) {
									_13 = _12.specialProperties.measuredWidth
								}
								var _14 = _12.height;
								if (_14 == null
										&& _12.specialProperties != null) {
									_14 = _12.specialProperties.measuredHeight
								}
								if (_14 != null && _13 != null
										&& _12.absX != null && _12.absY != null
										&& _9.absX < (_12.absX + _13)
										&& (_9.absX + _9.width) > _12.absX
										&& _9.absY < (_12.absY + _14)
										&& (_9.absY + _9.height) > _12.absY) {
									_9.doNotRemoveIndex = true
								}
							}
							if (_9.doNotRemoveIndex) {
								delete _9.doNotRemoveIndex
							} else {
								delete _9.zIndex
							}
						}
						for (var i = 0; i < _2.length; i++) {
							var _6 = true;
							for (var j = 0; j < _2.length; j++) {
								if (_2[j].contained != null
										&& _2[j].contained.contains(_2[i])) {
									_6 = false;
									break
								}
							}
							if (_6)
								delete _2[i].zIndex;
							if (_2[i].markupContained != null) {
								for (var k = 0; k < _2[i].markupContained.length; k++) {
									var _9 = _2[i].markupContained[k];
									_4(_2[i], _9)
								}
							}
							for (var j = 0; j < _2[i].contained.length; j++) {
								if (_2[i].children == null) {
									delete _2[i].contained[j].zIndex
								} else {
									_4(_2[i], _2[i].contained[j])
								}
							}
						}
					},
					isc.A.processLayoutMargin = function isc_MockupImporter_processLayoutMargin(
							_1, _2, _3, _4) {
						var _5 = 10000;
						var _6 = _5 + 1;
						var _7 = _5 + 1;
						var _8 = _5 + 1;
						var _9 = _5 + 1;
						var _10 = _3._constructor == "HStack"
								|| _3._constructor == "HLayout";
						var _11 = this.getControlHeightUsingItsParents(_2, _3);
						var _12 = this.getControlWidthUsingItsParents(_2, _3);
						for (var j = 0; j < _3.contained.length; j++) {
							var c = _3.contained[j];
							if (_10) {
								if (j == 0 && c.left)
									_6 = Math.min(_6, c.left);
								if (c.top != null) {
									_7 = Math.min(_7, c.top);
									if (c.height && _11) {
										_9 = Math.min(_9, _11 - c.top
												- c.height - 1)
									}
								}
								if (j == (_3.contained.length - 1) && c.left
										&& _12 && c.width) {
									_8 = Math.min(_8,
											(_12 - c.left - c.width - 1))
								}
							} else {
								if (j == 0 && c.top)
									_7 = Math.min(_7, c.top);
								if (c.left != null) {
									_6 = Math.min(_6, c.left);
									if (_12 && c.width) {
										_8 = Math.min(_8, (_12 - c.left
												- c.width - 1))
									}
								}
								if (j == (_3.contained.length - 1) && c.top
										&& _11 && c.height) {
									_9 = Math.min(_9,
											(_11 - c.top - c.height - 1))
								}
							}
						}
						for (var j = 0; j < _3.contained.length; j++) {
							var c = _3.contained[j];
							if (this.$87q.contains(c._constructor)
									|| "DynamicForm" == c._constructor) {
								var _15 = 0;
								var _16 = 0;
								var _17 = 0;
								if (_10) {
									if (j == 0 && c.left != null) {
										_15 = c.left - _6
									} else if (j != 0
											&& _3.contained[j - 1].showResizeBar
											&& _3.contained[j - 1].extraSpace) {
										_15 = _3.contained[j - 1].extraSpace - 12;
										delete _3.contained[j - 1].extraSpace
									}
									if (c.top != null) {
										_16 = c.top - _7
									}
									if (j == (_3.contained.length - 1)
											&& c.left != null && _12 != null
											&& c.width != null) {
										_17 = _12 - c.left - c.width - _8
									}
								} else {
									if (c.left != null) {
										_15 = c.left - _6
									}
									if (j == 0 && c.top != null) {
										_16 = c.top - _7
									} else if (j != 0
											&& _3.contained[j - 1].showResizeBar
											&& _3.contained[j - 1].extraSpace) {
										_16 = _3.contained[j - 1].extraSpace - 12;
										delete _3.contained[j - 1].extraSpace
									}
									if (c.left != null && _12 != null
											&& c.width != null) {
										_17 = _12 - c.left - c.width - _8
									}
								}
								_15 += c.specialProperties.lm || 0;
								_17 += c.specialProperties.rm || 0;
								_16 += c.specialProperties.tm || 0;
								if ("DynamicForm" == c._constructor) {
									var _18 = Math.min(_15, Math.min(_17, _16));
									if (_18 > 0) {
										c.padding = _18;
										_15 -= _18;
										_16 -= _18;
										_17 -= _18
									}
									var _19 = _3._constructor == "HStack"
											|| _3._constructor == "HLayout";
									if (!_19 && j > 0 && _16 > 0) {
										_3.contained[j - 1].extraSpace = (_3.contained[j - 1].extraSpace || 0)
												+ _16
									} else {
										var _20 = isc.ListGrid
												.getInstanceProperty("cellPadding");
										_16 -= _20;
										if (c.items && _16 > 3) {
											c.items.addAt({
												type : "SpacerItem",
												height : _16,
												colSpan : "*"
											}, 0)
										}
									}
								} else {
									if (_15 > 0 && c.layoutLeftMargin == null)
										c.layoutLeftMargin = _15;
									if (_16 > 0 && c.layoutTopMargin == null)
										c.layoutTopMargin = _16;
									if (_17 > 0 && c.layoutRightMargin == null)
										c.layoutRightMargin = _17
								}
							}
						}
						if (_3._constructor == "SectionStack"
								&& ((_6 > 0 && _6 <= _5)
										|| (_7 > 0 && _7 <= _5)
										|| (_8 > 0 && _8 <= _5) || (_3.membersMargin != null))) {
							var c = {
								ID : _3.ID + "_root",
								_constructor : "VStack",
								autoDraw : false,
								contained : _3.contained,
								specialProperties : {
									controlName : "Stack"
								}
							};
							_2.addAt(c, _2.indexOf(_3) + 1);
							_1.addAt(c, _4 ? _1.indexOf(_4) : _1.indexOf(_3));
							if (_3.membersMargin) {
								c.membersMargin = _3.membersMargin;
								delete _3.membersMargin
							}
							_3.contained = [ c ]
						}
						if ((_6 > 0 && _6 <= _5) || (_7 > 0 && _7 <= _5)
								|| (_8 > 0 && _8 <= _5) || (_9 > 0 && _9 <= _5)) {
							var c = null;
							if (_4 != null) {
								c = _4
							} else if (_3._constructor == "Window") {
								c = {};
								_3.bodyDefaults = c
							} else if (_3._constructor == "SectionStack") {
								c = _3.contained[0]
							} else {
								c = _3
							}
							if (c != null) {
								if (_3.specialProperties == null) {
									_3.specialProperties = {}
								}
								_3.specialProperties.layoutContainer = c;
								if (c._constructor == "DynamicForm") {
									var _21 = Math.round((_6 + _7) / 2);
									var _22 = 10;
									if (Math.abs(_7 - _21) < _22
											&& Math.abs(_6 - _21) < _22
											&& (_21 - _8) < _22) {
										c.padding = _21
									}
								} else {
									if (_6 > 0 && _6 <= _5) {
										c.layoutLeftMargin = _6
									}
									if (_7 > 0 && _7 <= _5) {
										c.layoutTopMargin = _7
									}
									if (_8 > 0 && _8 <= _5) {
										c.layoutRightMargin = _8
									}
									if (_9 > 0 && _9 <= _5) {
										c.layoutBottomMargin = _9
									}
								}
							}
						}
					},
					isc.A.processRemoveWidths = function isc_MockupImporter_processRemoveWidths(
							_1, _2, _3) {
						var _4 = this.$98p;
						var _5 = _4.widgetPropertyTranslations[_3.specialProperties.controlName];
						for (var j = 0; j < _3.contained.length; j++) {
							var _7 = _3.contained[j];
							var _8 = null;
							var _9 = null;
							var _10 = this.getParent(_1, _3);
							var _11 = this.getControlWidthUsingItsParents(_1,
									_3);
							var _12 = 0;
							var _13 = 0;
							var _14 = 0;
							var _15 = 0;
							if (_7.specialProperties.controlName) {
								var _16 = _4.widgetPropertyTranslations[_7.specialProperties.controlName];
								if (_16) {
									var _12 = _16.getLeftMargin ? _16
											.getLeftMargin(_7) : 0;
									var _13 = _16.getRightMargin ? _16
											.getRightMargin(_7) : 0;
									var _14 = _16.getTopMargin ? _16
											.getTopMargin(_7) : 0;
									var _15 = _16.getLeftMargin ? _16
											.getLeftMargin(_7) : 0
								}
							}
							var _17 = _7.specialProperties.left || _7.left;
							var _18 = _7.specialProperties.top || _7.top;
							if (_17 <= (this.stackContainerFillInset + _12)
									&& (_7.left + _7.width) >= (_11 - (this.stackContainerFillInset + _13))
									|| ((_3._constructor == "VStack" || _3._constructor == "VLayout") && (_7._constructor == "HStack" || _7._constructor == "HLayout"))) {
								_7.specialProperties.fullWidth = true;
								_7.specialProperties.containerName = _3._constructor;
								_8 = true
							}
							var _19 = this.getControlHeightUsingItsParents(_1,
									_3);
							if ((_18 <= (this.stackContainerFillInset + _14) && ((_7.top + _7.height) >= (_19 - (this.stackContainerFillInset + _15))))
									|| ((_3._constructor == "HStack" || _3._constructor == "HLayout") && (_7._constructor == "VStack" || _7._constructor == "VLayout"))) {
								_7.specialProperties.fullHeight = true;
								_7.specialProperties.containerName = _3._constructor;
								_9 = true
							}
							if ((_3._constructor == "TabSet"
									|| _3._constructor == "Window"
									|| _3._constructor == "SectionStack"
									|| _3._constructor == "VStack" || _3._constructor == "VLayout")
									&& _7.width != null
									&& _7.specialProperties.overrideWidth == null) {
								if (_7.left <= this.stackContainerFillInset) {
									_7.layoutAlign = "left"
								} else if (_7.left + _7.width >= (_3.width
										- this.stackContainerFillInset
										- _5.getLeftMargin(_3) - _5
										.getRightMargin(_3))) {
									_7.layoutAlign = "right"
								} else if (Math.abs(_7.left
										+ _7.width
										/ 2
										- (_3.width - _5.getLeftMargin(_3) - _5
												.getRightMargin(_3)) / 2) <= this.stackContainerFillInset) {
									_7.layoutAlign = "center"
								} else if (_7._constructor == "Label") {
									var _20 = _7.left - _3.left
											+ _5.getLeftMargin(_3);
									if (_20 > this.stackContainerFillInset / 2) {
										_7.align = "right"
									}
								}
							} else if ((_3._constructor == "HStack" || _3._constructor == "HLayout")
									&& _7.height != null) {
								if (_7.top <= this.stackContainerFillInset) {
									_7.layoutAlign = "top"
								} else if (_7.top + _7.height >= (_3.height
										- this.stackContainerFillInset
										- _5.getTopMargin(_3) - _5
										.getBottomMargin(_3))) {
									_7.layoutAlign = "bottom"
								} else if (Math.abs(_7.top
										+ _7.height
										/ 2
										- (_3.height - _5.getTopMargin(_3) - _5
												.getBottomMargin(_3)) / 2) < this.stackContainerFillInset) {
									_7.layoutAlign = "center"
								}
								if (_7._constructor == "Label"
										&& _7.layoutAlign
										&& _7.layoutAlign != "center") {
								}
							}
							if (_8 && _9 && _3.contained.length > 1) {
								this.processSnapToHeuristic(_1, _3, _7);
								break
							}
						}
					},
					isc.A.processSnapToHeuristic = function isc_MockupImporter_processSnapToHeuristic(
							_1, _2, _3) {
						var _4 = 5;
						for (var i = 0; i < _2.contained.length; i++) {
							var _6 = _2.contained[i];
							if (_6 != _3) {
								if (_6.width != null) {
									var _7 = this
											.getControlWidthUsingItsParents(_1,
													_2);
									if (Math.abs(_7 - _6.width) <= _4 * 2) {
									} else if (Math.abs(_7
											- (_6.left + _6.width)) <= _4) {
										_6.snapToHor = "R";
										delete _6.left
									} else if (Math.abs(_7 / 2
											- (_6.left + _6.width / 2)) <= _4) {
										_6.snapToHor = "C";
										delete _6.left
									} else if (_6.left <= _4) {
										_6.snapToHor = "L";
										delete _6.left
									}
								}
								if (_6.height != null) {
									var _8 = this
											.getControlHeightUsingItsParents(
													_1, _2);
									if (Math.abs(_8 - _6.height) <= _4 * 2) {
									} else if (Math.abs(_8
											- (_6.top + _6.height)) <= _4) {
										_6.snapToVer = "B";
										delete _6.top
									} else if (Math.abs(_8 / 2
											- (_6.top + _6.height / 2)) <= _4) {
										_6.snapToVer = "C";
										delete _6.top
									} else if (_6.top <= _4) {
										_6.snapToVer = "T";
										delete _6.top
									}
								}
								var _9 = "";
								if (_6.snapToVer != null) {
									_9 += _6.snapToVer;
									delete _6.snapToVer;
									delete _6.layoutTopMargin
								}
								if (_6.snapToHor != null) {
									if (_6.snapToVer == "C") {
										_9 = _6.snapToHor
									} else {
										_9 += _6.snapToHor
									}
									delete _6.snapToHor;
									delete _6.layoutLeftMargin
								}
								if (_2.markupContained == null) {
									_2.markupContained = []
								}
								if (_9 != null && _9 != "") {
									_6.snapTo = _9;
									_2.markupContained.add(_6);
									_2.contained.removeAt(i);
									i--
								} else {
									_2.markupContained.add(_6);
									_2.contained.removeAt(i);
									i--
								}
							}
						}
						return _1
					},
					isc.A.getControlHeightUsingItsParents = function isc_MockupImporter_getControlHeightUsingItsParents(
							_1, _2) {
						var _3 = _2.height, _4 = this.$98p;
						if (_3 == null) {
							var _5 = this.getParent(_1, _2);
							while (_5 != null && _5.height == null) {
								_5 = this.getParent(_1, _5)
							}
							if (_5 != null) {
								var _6 = _4.widgetPropertyTranslations[_5.specialProperties.controlName];
								_3 = _5.height - _6.getTopMargin(_5)
										- _6.getBottomMargin(_5)
							}
						} else {
							var _6 = _4.widgetPropertyTranslations[_2.specialProperties.controlName];
							if (_6.getTopMargin != null
									&& _6.getBottomMargin != null) {
								_3 = _2.height - _6.getTopMargin(_2)
										- _6.getBottomMargin(_2)
							}
						}
						return _3
					});
	isc.evalBoundary;
	isc.B
			.push(
					isc.A.getControlWidthUsingItsParents = function isc_MockupImporter_getControlWidthUsingItsParents(
							_1, _2) {
						var _3 = _2.width, _4 = this.$98p;
						if (_3 == null) {
							var _5 = this.getParent(_1, _2);
							while (_5 != null && _5.width == null) {
								_5 = this.getParent(_1, _5)
							}
							if (_5 != null) {
								var _6 = _4.widgetPropertyTranslations[_5.specialProperties.controlName];
								_3 = _5.width - _6.getLeftMargin(_5)
										- _6.getRightMargin(_5)
							}
						} else {
							var _6 = _4.widgetPropertyTranslations[_2.specialProperties.controlName];
							if (_6.getLeftMargin != null
									&& _6.getRightMargin != null) {
								_3 = _2.width - _6.getLeftMargin(_2)
										- _6.getRightMargin(_2)
							}
						}
						return _3
					},
					isc.A.getParent = function isc_MockupImporter_getParent(_1,
							_2) {
						for (var i = 0; i < _1.length; i++) {
							if (_1[i].contained != null
									&& _1[i].contained.contains(_2)) {
								return _1[i]
							}
							if (_1[i].children != null
									&& _1[i].children.contains(_2)) {
								return _1[i]
							}
						}
						return null
					},
					isc.A.processFluidLayoutHeuristic = function isc_MockupImporter_processFluidLayoutHeuristic(
							_1, _2) {
						for (var i = 0; i < _2.length; i++) {
							var _4 = _2[i];
							var _5 = _4.specialProperties;
							if (_4._constructor == "TabSet") {
								var _6 = _4.tabs[_4.selectedTab].pane;
								if (_6 && _6.VStack) {
									_4 = _6.VStack;
									_4._constructor = "VStack"
								}
							}
							if ((_5 && (_5.fullWidth || _5.fullHeight))
									|| _4.height == null || _4.width == null) {
								if (_4._constructor == "HStack"
										|| _4._constructor == "VStack") {
									var _7 = [];
									var _8 = 0;
									for (var j = 0; j < _5.innerItems.length; j++) {
										var _10 = _5.innerItems[j];
										if (_7.isEmpty()
												&& ((_10._constructor == "HLayout" && _4._constructor == "HStack") || (_10._constructor == "VLayout" && _4._constructor == "VStack"))) {
											if (_8 != 1) {
												_7.clear();
												_8 = 1
											}
											_7.add(_10)
										}
										if (_10._constructor == "ListGrid"
												&& (_7.isEmpty() || _8 >= 2)) {
											if (_8 != 2) {
												_7.clear();
												_8 = 2
											}
											_7.add(_10)
										}
										if (_10._constructor == "DynamicForm"
												&& _4._constructor == "VStack"
												&& (_7.isEmpty() || _8 >= 3)) {
											if (_10.items != null
													|| _10.fields != null) {
												var _11 = _10.items
														|| _10.fields;
												for (var k = 0; k < _11.length; k++) {
													var _13 = _11[k];
													if (_13._constructor == "TextAreaItem") {
														if (_8 != 3) {
															_7.clear();
															_8 = 3
														}
														_7.add(_10);
														break
													}
												}
											}
										}
										if (_10._constructor == "TabSet"
												&& (_7.isEmpty() || _8 >= 4)) {
											if (_8 != 4) {
												_7.clear();
												_8 = 4
											}
											_7.add(_10)
										}
										if (_10.specialProperties
												&& _10.specialProperties.innerItems
												&& (_7.isEmpty() || _8 >= 5)) {
											var _14 = _10.specialProperties.innerItems, _15 = (_14.length == 1 && _14[0]), _16 = (_15
													&& _15.specialProperties != null && _15.specialProperties.controlName), _17 = (_16 == "com.balsamiq.mockups::TagCloud");
											if (!_17) {
												if (_8 != 5) {
													_7.clear();
													_8 = 5
												}
												_7.add(_10)
											}
										}
									}
									if (!_7.isEmpty()) {
										if (_7.length > 1) {
											var _18 = (_4._constructor == "HStack") ? "width"
													: "height";
											var _19 = _7[0][_18];
											var _20 = _7[0][_18];
											var _21 = _7[0][_18];
											for (var _22 = 1; _22 < _7.length; _22++) {
												var _23 = _7[_22];
												var _24 = _23[_18];
												if (_24 < _19) {
													_19 = _24
												} else if (_24 > _20) {
													_20 = _24;
													_21 = _23
												}
											}
											if (_20 - _19 < this.stackFlexMaxSizeMatch) {
												for (var _22 = 0; _22 < _7.length; _22++) {
													this
															.removeWidgetSizeProperty(
																	_7[_22],
																	_4._constructor == "HStack")
												}
											} else {
												this
														.removeWidgetSizeProperty(
																_21,
																_4._constructor == "HStack")
											}
										} else {
											this
													.removeWidgetSizeProperty(
															_7[0],
															_4._constructor == "HStack")
										}
										if (_4._constructor == "HStack") {
											_4._constructor = "HLayout"
										} else {
											_4._constructor = "VLayout"
										}
									}
								}
							}
						}
						return _1
					},
					isc.A.removeWidgetSizeProperty = function isc_MockupImporter_removeWidgetSizeProperty(
							_1, _2) {
						if (_2) {
							delete _1.width;
							if (_1._constructor == "DynamicForm") {
								_1.width = "*";
								var _3 = _1.items || _1.fields;
								if (_3) {
									for (var i = 0; i < _3.length; i++) {
										if (_3[i]._constructor == "TextAreaItem") {
											_3[i].width = "*"
										}
									}
								}
							}
						} else {
							delete _1.height;
							if (_1._constructor == "DynamicForm") {
								_1.height = "*";
								var _3 = _1.items || _1.fields;
								if (_3) {
									for (var i = 0; i < _3.length; i++) {
										if (_3[i]._constructor == "TextAreaItem") {
											_3[i].height = "*"
										}
									}
								}
							}
						}
					}, isc.A.logWarn = function isc_MockupImporter_logWarn(_1,
							_2) {
						this.Super("logWarn", arguments);
						this.warnings += "\n" + _1
					});
	isc.B._maxIndex = isc.C + 74;
	isc.ClassFactory.defineClass("BMMLImportDialog", isc.TWindow || isc.Dialog);
	isc.A = isc.BMMLImportDialog.getPrototype();
	isc.B = isc._allFuncs;
	isc.C = isc.B._maxIndex;
	isc.D = isc._funcClasses;
	isc.D[isc.C] = isc.A.Class;
	isc.A.showFileNameField = true;
	isc.A.showAssetsNameField = true;
	isc.A.showOutputField = true;
	isc.A.showSkinSelector = true;
	isc.A.autoSize = true;
	isc.A.autoCenter = true;
	isc.A.showMinimize = false;
	isc.A.isModal = true;
	isc.A.title = "Import Balsamiq File";
	isc.A.showToolbar = false;
	isc.A.skin = "Enterprise";
	isc.A.importFormDefaults = {
		_constructor : "DynamicForm",
		cellPadding : 6,
		autoDraw : false,
		saveOnEnter : true,
		wrapItemTitles : false,
		colWidths : [ 140, "*" ],
		width : "100%",
		useAllDataSourceFields : false,
		padding : 10,
		isGroup : true,
		groupTitle : "Mockup File Location",
		submit : function() {
			var _1 = this.getItem("fileName"), _2 = this.getValue("fileName");
			if (!_2 || isc.isAn.emptyString(_2) || _2 == _1.defaultValue) {
				isc.say("Please select a file to import.");
				return

				

								

				

												

				

								

				

			}
			this.Super("submit", arguments)
		},
		uploadCallback : function(_1, _2) {
			var _3 = _2.filePath;
			var _4 = _2.fileName;
			var v = this.valuesToSend;
			this.creator.submit(_3, v.outputFileName, _2.fileContent, v.skin,
					v.dropMarkup, v.trimSpace, v.fillSpace,
					v.fieldNamingConvention, false, true)
		},
		uploadAssetCallback : function(_1, _2, _3) {
			var _4 = this;
			var _5 = _2.fileName;
			if (_4.assetFiles == null) {
				_4.assetFiles = []
			}
			if (!_4.assetFiles.contains(_5)) {
				_4.assetFiles.add(_5)
			}
			if (_4.assetFiles.length == 1) {
				_4.setValue("assetsName", _4.assetFiles[0]
						+ " (click to upload more)")
			} else {
				_4.setValue("assetsName", _4.assetFiles.length
						+ " assets (click to upload more)")
			}
			if (_3) {
				_4.getField("file").clearValue()
			}
		}
	};
	isc.B
			.push(
					isc.A.initWidget = function isc_BMMLImportDialog_initWidget() {
						this.Super("initWidget", arguments);
						var _1 = [];
						var _2 = this.showFileNameField;
						if (_2) {
							_1
									.addList([
											{
												name : "fileName",
												title : "Select file from local disk",
												editorType : isc.TLinkItem
														|| isc.LinkItem,
												target : "javascript",
												defaultValue : "select file",
												canEdit : false,
												width : "*",
												colSpan : "*",
												prompt : "Click to select the BMML file you want to import.",
												click : function(_15, _17) {
													var _3 = _15;
													var _4 = isc.LoadFileDialog
															.create({
																actionStripControls : [
																		"spacer:10",
																		"pathLabel",
																		"previousFolderButton",
																		"spacer:10",
																		"upOneLevelButton",
																		"spacer:10",
																		"refreshButton",
																		"spacer:2" ],
																directoryListingProperties : {
																	canEdit : false
																},
																title : "Import Balsamiq File",
																initialDir : "[VBWORKSPACE]",
																rootDir : "/",
																webrootOnly : false,
																width : "100%",
																showModalMask : true,
																isModal : true,
																fileFilters : [ {
																	filterName : "BMML Files",
																	filterExpressions : [ new RegExp(
																			"\\.bmml$") ]
																} ],
																loadFile : function(
																		_9) {
																	if (_9
																			.match(/\.(bmml)$/i) == null) {
																		isc
																				.say("Only BMML files may be imported (must end with .bmml)");
																		return

																		

																																				

																		

																																																						

																		

																																				

																		

																	}
																	var _5 = this;
																	_3
																			.setValue(
																					"fileName",
																					this.currentDir
																							+ "/"
																							+ _9);
																	var _6 = _3
																			.getFields();
																	for (var i = 1; i < _6.length; i++) {
																		_6[i]
																				.disable()
																	}
																	_4.hide()
																}
															});
													_4.show()
												}
											}, {
												name : "fileOr",
												value : "OR",
												colSpan : 1,
												align : "right",
												visible : false,
												type : "blurb"
											} ])
						}
						_1.push({
							name : "file",
							title : "Upload file",
							editorType : isc.TUploadItem || isc.UploadItem,
							width : 200,
							hoverWidth : 200,
							width : "*",
							colSpan : "*",
							visible : false,
							startRow : true,
							itemHoverHTML : function() {
								return "Upload file to server and proceed."
							},
							titleHoverHTML : function() {
								return this.itemHoverHTML()
							},
							change : function(_15, _17, _18, _19) {
								if (_2) {
									var _6 = _15.getFields();
									for (var i = 0; i < _6.length; i++) {
										if (i != 2 && i != 3)
											_6[i].disable()
									}
								}
								return true
							}
						});
						if (this.$98b && this.fileName) {
							var j = this.fileName.lastIndexOf("/"), _9 = this.fileName
									.substring(j + 1), _10 = "Currently using uploaded file: &nbsp;"
									+ _9;
							_1.push({
								type : "canvas",
								hint : "<span style=\"white-space:nowrap\">"
										+ _10 + "</span>",
								width : 1,
								canvas : isc.HTMLFlow.create({
									autoDraw : false,
									contents : ""
								})
							})
						}
						var _11 = this.showAssetsNameField;
						if (_11) {
							_1
									.push({
										name : "assetsName",
										title : "Upload assets",
										editorType : isc.TLinkItem
												|| isc.LinkItem,
										target : "javascript",
										defaultValue : "upload an asset file",
										width : "*",
										colSpan : "*",
										startRow : true,
										canEdit : false,
										visible : false,
										click : function(_15, _17) {
											var _12 = isc.TWindow || isc.Dialog;
											var _13 = _12.create({
												title : "Load asset",
												height : 140,
												width : 400,
												showToolbar : false,
												autoCenter : true
											});
											var _3 = _15;
											var _14 = isc.DynamicForm
													.create({
														dataSource : _15.dataSource,
														numCols : 3,
														cellPadding : 5,
														colWidths : "140, 180, *",
														autoDraw : false,
														fields : [
																{
																	name : "file",
																	editorType : isc.TFileItem
																			|| isc.FileItem,
																	title : "Asset file",
																	colSpan : 3,
																	endRow : true
																},
																{
																	type : "SpacerItem"
																},
																{
																	name : "submitButton",
																	title : "Load",
																	editorType : isc.TButtonItem
																			|| isc.ButtonItem,
																	endRow : false,
																	startRow : false,
																	align : "right",
																	click : function(
																			_18,
																			_17) {
																		if (_18
																				.getValues().file != null) {
																			_18
																					.getValues().file_filepath = "[VBWORKSPACE]/assets";
																			_13
																					.hide();
																			_18
																					.saveData(_15
																							.getID()
																							+ ".uploadAssetCallback(dsResponse, data)");
																			if (_2) {
																				var _6 = _3
																						.getFields();
																				for (var i = 0; i < _6.length; i++) {
																					if (i != 2
																							&& i != 3)
																						_6[i]
																								.disable()
																				}
																			}
																		} else {
																			isc
																					.warn("Select asset to upload")
																		}
																	}
																},
																{
																	name : "cancelButton",
																	title : "Cancel",
																	align : "right",
																	editorType : isc.TButtonItem
																			|| isc.ButtonItem,
																	startRow : false,
																	click : function(
																			_15,
																			_17) {
																		_13
																				.hide()
																	}
																} ]
													});
											_13.addItem(_14);
											_13.show()
										}
									})
						}
						_1.addList([ {
							value : "OR",
							colSpan : 1,
							align : "right",
							type : "blurb"
						}, {
							name : "uploadFromURL",
							type : "text",
							width : 450,
							title : "Fetch file from URL",
							startRow : true
						} ]);
						this.vm = isc.ValuesManager.create();
						this.vm.setValues({
							uploadFromURL : this.uploadFromURL,
							outputFileName : this.outputFileName,
							skin : this.skin,
							dropMarkup : this.dropMarkup,
							trimSpace : this.trimSpace,
							fillSpace : this.fillSpace,
							fieldNamingConvention : this.fieldNamingConvention
						});
						if (this.$98b != null && !this.$98b) {
							this.vm.getValues().fileName = this.fileName
						}
						var _15 = this.importForm = this.createAutoChild(
								"importForm", {
									valuesManager : this.vm,
									fields : _1
								});
						var _16 = isc.DataSource.get("SCUploadSaveFile");
						_15.setDataSource(_16, _15.fields);
						this.addItem(_15);
						this.addItem(this.$98c());
						this.addItem(this.$98d());
						this.addItem(this.$98e());
						_16
								.performCustomOperation(
										"checkUploadFeature",
										null,
										function(_17, _18) {
											if (_17.status == isc.RPCResponse.STATUS_SUCCESS) {
												if (_2)
													_15.getField("fileOr")
															.show();
												_15.getField("file").show();
												if (_11)
													_15.getField("assetsName")
															.show()
											}
										}, {
											willHandleError : true
										})
					},
					isc.A.submit = function isc_BMMLImportDialog_submit(_1, _2,
							_3, _4, _5, _6, _7, _8, _9, _10) {
					},
					isc.A.$98e = function isc_BMMLImportDialog__createActionsForm() {
						var _1 = this;
						var _2 = [ {
							name : "fieldNamingConvention",
							editorType : isc.TSelectItem || isc.SelectItem,
							width : 175,
							title : "Field Naming Convention",
							defaultValue : "camelCaps",
							hint : "Advanced&nbsp;setting",
							startRow : false,
							endRow : true,
							valueMap : {
								camelCaps : "camelCaps",
								underscores : "Underscores",
								underscoresAllCaps : "Underscores All Caps",
								underscoresKeepCase : "Underscores Keep Case"
							},
							hoverWidth : 200,
							titleHoverHTML : function(_17, _18) {
								return "Naming convention used when translating grid column labels and input field labels to DataSource field identifiers.  This does not affect the appearance or behavior of the imported mockup, just the identifiers used when connecting your imported mockup to real data.<P>Choose a naming convention that is similar to how your Java code or database columns are named - hover options in the drop-down list for details.<P>If unsure, keep the default of \"camelCaps\"."
							},
							itemHoverHTML : function(_17, _18) {
								var _3 = _17.getValue();
								if (_3 == "camelCaps") {
									return "For example, \"First Name\" becomes \"firstName\".  Best setting for binding to Java Beans (including Hibernate and JPA) and databases where columns names have no underscores, for example, \"FIRSTNAME\"."
								} else if (_3 == "underscores") {
									return "For example, \"First Name\" becomes \"first_name\".  Best setting for databases that have underscores in column names, for example, \"FIRST_NAME\"."
								} else if (_3 == "underscoresAllCaps") {
									return "For example, \"First Name\" becomes \"FIRST_NAME\".  Alternative to \"Underscores\" for developers who prefer field identifiers to be all caps."
								} else if (_3 == "underscoresKeepCase") {
									return "For example, \"First Name\" becomes \"First_Name\".  Alternative to \"Underscores\" for developers who prefer field identifiers to be mixed case."
								}
							}
						} ];
						if (this.showOutputField) {
							_2
									.add({
										name : "outputFileName",
										title : "Output File Name",
										type : isc.TTextItem || isc.TextItem,
										width : 450,
										hoverWidth : 200,
										hint : "Optional",
										itemHoverHTML : function() {
											return "Writes the source code of the imported screen to the specified path.  If the specified path is relative (does not start with a slash), it is assumed to be relative to webroot/tools.  If the file name ends in 'js' the output is JavaScript, otherwise it's XML."
										},
										titleHoverHTML : function() {
											return this.itemHoverHTML()
										}
									})
						}
						_2
								.add({
									name : "submitButton",
									title : "Import",
									type : isc.TButtonItem || isc.ButtonItem,
									width : 100,
									colSpan : "3",
									align : "right",
									click : function(_17, _18) {
										var _4 = _1.vm.getValues();
										var _5 = _4.uploadFromURL;
										if (_4.fileName == 'select file'
												&& _4.file == null
												&& _5 == null
												&& !(_1.$98b && _1.fileName != null)) {
											isc
													.warn("Select a file to process.")
										} else {
											if (_4.file != null) {
												_4.file_filepath = "[VBWORKSPACE]";
												if (_1.showAssetsNameField
														&& (_4.file == "symbols.bmml" || _4.file
																.endsWith("\symbols.bmml"))) {
													isc
															.ask(
																	"The file 'symbols.bmml' looks like an asset file. Do you want to convert it or add to assets?",
																	function(_3) {
																	},
																	{
																		buttons : [
																				isc.Button
																						.create({
																							title : "Convert",
																							click : function() {
																								this.topElement
																										.hide();
																								if (_1.importForm.creator)
																									_1.importForm.creator
																											.hide();
																								_4.file_filepath = "[VBWORKSPACE]";
																								_1.importForm.valuesToSend = isc
																										.clone(_4);
																								_1.importForm
																										.saveData(_1.importForm
																												.getID()
																												+ ".uploadCallback(dsResponse, data)")
																							}
																						}),
																				isc.Button
																						.create({
																							title : "Add to assets",
																							click : function() {
																								this.topElement
																										.hide();
																								_4.file_filepath = "[VBWORKSPACE]/assets";
																								_1.importForm.valuesToSend = isc
																										.clone(_4);
																								_1.importForm
																										.saveData(_1.importForm
																												.getID()
																												+ ".uploadAssetCallback(dsResponse, data, true)")
																							}
																						}) ]
																	})
												} else {
													if (_1.importForm.creator)
														_1.importForm.creator
																.hide();
													_4.file_filepath = "[VBWORKSPACE]";
													_1.importForm.valuesToSend = isc
															.clone(_4);
													_1.importForm
															.saveData(_1.importForm
																	.getID()
																	+ ".uploadCallback(dsResponse, data)")
												}
											} else {
												if (_5 != null) {
													if (!_5.startsWith("http")) {
														_5 = "http://" + _5
													}
													var _6 = new RegExp(
															"^http(?:s)?://([^/:]+)(?::[0-9]+)?(/.*)?$"), _7 = _5
															.match(_6), _8 = (_7 != null
															&& _7[1] != null ? _7[1]
															.toString()
															: ""), _9 = _8 == "mybalsamiq.com"
															|| _8
																	.endsWith(".mybalsamiq.com");
													if (_9) {
														var _10 = (_7 != null
																&& _7[2] != null ? _7[2]
																.toString()
																: ""), _11 = _5.length
																- _10.length;
														if (_10 != ""
																&& _10 != "/"
																&& !_5
																		.endsWith(".bmml")) {
															_5 += ".bmml"
														}
														var j = -1;
														while ((j = _10
																.indexOf(
																		"/edit/",
																		j + 1)) != -1) {
															var k = j + _11;
															_5 = _5.substring(
																	0, k)
																	+ _5
																			.substring(k + 5);
															_11 = _11 - 5
														}
													}
												}
												var _14 = (_5 != null), _15 = _1.$98b, _16 = (_15
														&& _4.fileName == "select file"
														&& !_4.uploadFromURL && _1.fileName)
														|| _5 || _4.fileName;
												if (_1.importForm.creator)
													_1.importForm.creator
															.hide();
												_1
														.submit(
																_16,
																_4.outputFileName,
																null,
																_4.skin,
																_4.dropMarkup,
																_4.trimSpace,
																_4.fillSpace,
																_4.fieldNamingConvention,
																_14, _15)
											}
										}
									}
								});
						return isc.DynamicForm.create({
							rightPadding : this.importForm.rightPadding
									|| this.importForm.padding,
							leftPadding : this.importForm.leftPadding
									|| this.importForm.padding,
							cellPadding : this.importForm.cellPadding,
							autoDraw : false,
							saveOnEnter : true,
							numCols : this.importForm.numCols,
							wrapItemTitles : false,
							width : "100%",
							colWidths : this.importForm.colWidths,
							useAllDataSourceFields : false,
							valuesManager : this.vm,
							fields : _2,
							isGroup : true,
							showGroupLabel : false,
							groupBorderCSS : "1px solid transparent"
						})
					},
					isc.A.$98d = function isc_BMMLImportDialog__createFlagsForm() {
						var _1 = [];
						_1
								.addList([
										{
											name : "dropMarkup",
											title : "Drop Markup",
											editorType : isc.TCheckboxItem
													|| isc.CheckboxItem,
											labelAsTitle : true,
											defaultValue : true,
											hoverWidth : 200,
											width : "*",
											startRow : true,
											itemHoverHTML : function() {
												return "If enabled, markup elements such as sticky notes and strikethroughs are dropped during import."
											},
											titleHoverHTML : function() {
												return this.itemHoverHTML()
											}
										},
										{
											name : "trimSpace",
											title : "Trim Space",
											editorType : isc.TCheckboxItem
													|| isc.CheckboxItem,
											labelAsTitle : true,
											defaultValue : true,
											hoverWidth : 200,
											width : "*",
											itemHoverHTML : function() {
												return "If enabled, and there is empty space to the left/top of the mockup, the mockup is moved to 0,0 instead.  In combination with dropMarkup, this also means that a mockup with only markup elements to the left/top of it will be moved to 0,0 as part of importing."
											},
											titleHoverHTML : function() {
												return this.itemHoverHTML()
											}
										},
										{
											name : "fillSpace",
											title : "Fill Space",
											editorType : isc.TCheckboxItem
													|| isc.CheckboxItem,
											labelAsTitle : true,
											defaultValue : true,
											hoverWidth : 200,
											width : "*",
											itemHoverHTML : function() {
												return "If enabled and a mockup-import results in a single layout or single top-level container with a single layout within it, the top-level widget will be set to 100% width and height so that it fills available space."
											},
											titleHoverHTML : function() {
												return this.itemHoverHTML()
											}
										}, {
											editorType : "SpacerItem"
										} ]);
						return isc.DynamicForm.create({
							rightPadding : this.importForm.rightPadding
									|| this.importForm.padding,
							leftPadding : this.importForm.leftPadding
									|| this.importForm.padding,
							cellPadding : this.importForm.cellPadding,
							autoDraw : false,
							saveOnEnter : true,
							numCols : 7,
							colWidths : [ this.importForm.colWidths[0], 50,
									this.importForm.colWidths[0], 50,
									this.importForm.colWidths[0], 50, "*" ],
							width : "100%",
							wrapItemTitles : false,
							useAllDataSourceFields : false,
							valuesManager : this.vm,
							fields : _1,
							isGroup : true,
							showGroupLabel : false,
							groupBorderCSS : "1px solid transparent"
						})
					},
					isc.A.$98c = function isc_BMMLImportDialog__createHeadForm() {
						var _1 = [];
						if (this.showSkinSelector) {
							_1.add({
								name : "skin",
								editorType : isc.TSelectItem || isc.SelectItem,
								width : 175,
								title : "Skin",
								defaultValue : this.skin,
								endRow : false,
								valueMap : {
									Enterprise : "Enterprise",
									EnterpriseBlue : "Enterprise Blue",
									Graphite : "Graphite",
									Simplicity : "Simplicity",
									fleet : "Fleet",
									TreeFrog : "TreeFrog",
									SilverWave : "SilverWave",
									BlackOps : "Black Ops",
									SmartClient : "Stone",
									Cupertino : "Cupertino",
									standard : "Basic"
								},
								changed : function(_3, _4, _5) {
									var _2 = isc.URIBuilder
											.create(window.location);
									_2.setQueryParam("skin", _5);
									window.location.replace(_2.uri)
								}
							})
						}
						return isc.DynamicForm.create({
							rightPadding : this.importForm.rightPadding
									|| this.importForm.padding,
							leftPadding : this.importForm.leftPadding
									|| this.importForm.padding,
							cellPadding : this.importForm.cellPadding,
							autoDraw : false,
							saveOnEnter : true,
							numCols : this.importForm.numCols,
							wrapItemTitles : false,
							width : "100%",
							colWidths : this.importForm.colWidths,
							useAllDataSourceFields : false,
							valuesManager : this.vm,
							fields : _1,
							isGroup : true,
							showGroupLabel : false,
							groupBorderCSS : "1px solid transparent"
						})
					});
	isc.B._maxIndex = isc.C + 5;
	isc.BMMLImportDialog.changeDefaults("bodyDefaults", {
		width : "100%"
	});
	isc.defineClass("FieldMapper", "HStack");
	isc.A = isc.FieldMapper.getPrototype();
	isc.B = isc._allFuncs;
	isc.C = isc.B._maxIndex;
	isc.D = isc._funcClasses;
	isc.D[isc.C] = isc.A.Class;
	isc.A.membersMargin = 10;
	isc.A.padding = 10;
	isc.A.width = 1020;
	isc.A.height = 260;
	isc.B.push(isc.A.initWidget = function isc_FieldMapper_initWidget() {
		this.Super("initWidget", arguments);
		var _1 = this;
		this.targetDataSourceList = isc.ListGrid.create({
			width : (this.width - 20) / 2,
			height : "100%",
			alternateRecordStyles : true,
			fields : [ {
				name : "name",
				title : "Name"
			}, {
				name : "title",
				title : "Title"
			}, {
				name : "type",
				title : "Type"
			}, {
				name : "inUse",
				title : "In use",
				type : "boolean"
			} ],
			canDragRecordsOut : true,
			dragDataAction : "copy",
			autoDraw : false,
			updateInUseFields : function(_6, _7) {
				if (_7) {
					this.setValue(_7, false)
				}
				this.setValue(_6, true);
				var _2 = _1.targetDataSource.getFields();
				var _3 = [];
				for (var i = 0; i < this.data.length; i++) {
					if (!this.data[i].inUse) {
						_3.add(this.data[i].name)
					}
				}
				_1.$87z.getField("mappedTo").valueMap = _3
			},
			setValue : function(_6, _7) {
				for (var i = 0; i < this.data.length; i++) {
					if (this.data[i].name == _6) {
						this.data[i].inUse = _7;
						this.redraw();
						return

						

												

						

																		

						

												

						

					}
				}
			}
		});
		this.$87z = isc.ListGrid.create({
			width : (this.width - 20) / 2,
			height : "100%",
			alternateRecordStyles : true,
			fields : [ {
				name : "name",
				title : "Name"
			}, {
				name : "title",
				title : "Title"
			}, {
				name : "mappedTo",
				title : "Mapped To",
				type : "SelectItem",
				change : function(_6, _7, _8, _9) {
					_1.targetDataSourceList.updateInUseFields(_8, _9)
				}
			} ],
			canReorderRecords : true,
			canAcceptDroppedRecords : true,
			canRemoveRecords : true,
			recordDrop : function(_6, _7, _8, _9) {
				if (this == _9) {
					this.Super("recordDrop", arguments);
					return

					

										

					

															

					

										

					

				}
				for (var i = 0; i < this.data.length; i++) {
					if (this.data[i].mappedTo == _6[0].name) {
						delete this.data[i].mappedTo;
						break
					}
				}
				;
				if (this.hilitedRecord == null) {
					var _5 = isc.clone(_6[0]);
					_5.mappedTo = _5.name;
					this.data.add(_5);
					_1.targetDataSourceList.updateInUseFields(_5.name);
					return

					

										

					

															

					

										

					

				}
				_7 = this.getRecord(this.hilitedRecord);
				_1.targetDataSourceList.updateInUseFields(_6[0].name,
						_7.mappedTo);
				_7.mappedTo = _6[0].name;
				this.redraw()
			},
			dropMove : function() {
				this.hilitedRecord = this.getEventRecordNum();
				if (this.hilitedRecord == -2) {
					delete this.hilitedRecord;
					this.clearLastHilite()
				} else {
					this.$88(this.hilitedRecord)
				}
			},
			canEdit : true,
			autoDraw : false
		});
		this.setMembers([ isc.VStack.create({
			members : [ {
				_constructor : "Label",
				contents : "Existing Fields",
				height : 1,
				width : this.$87z.width,
				baseStyle : "headerItem"
			}, this.$87z ]
		}), isc.VStack.create({
			members : [ {
				_constructor : "Label",
				contents : "Fields from new DataSource",
				height : 1,
				width : this.targetDataSourceList.width,
				baseStyle : "headerItem"
			}, this.targetDataSourceList ]
		}) ]);
		if (this.mockDataSource == null) {
			isc.logWarn("MockDataSource should be set")
		} else if (this.targetDataSource == null) {
			isc.logWarn("TargetDataSource should be set")
		} else {
			this.setDefaultData()
		}
	}, isc.A.setDefaultData = function isc_FieldMapper_setDefaultData() {
		var _1 = this.mockDataSource.getFields();
		var _2 = [];
		for ( var _3 in _1) {
			_2.add({
				name : _3,
				title : _1[_3].title
			})
		}
		;
		this.$870(_2);
		this.$87z.setData(_2);
		var _4 = {};
		for (var i = 0; i < _2.length; i++) {
			if (_2[i].mappedTo)
				_4[_2[i].mappedTo] = _2[i].name
		}
		_1 = this.targetDataSource.getFields();
		_2 = [];
		var _6 = [];
		for ( var _3 in _1) {
			_2.add({
				name : _3,
				title : _1[_3].title,
				type : _1[_3].type,
				inUse : _4[_3] != null
			});
			if (_4[_3] == null) {
				_6.add(_3)
			}
		}
		;
		this.targetDataSourceList.setData(_2);
		this.$87z.getField("mappedTo").valueMap = _6
	}, isc.A.$870 = function isc_FieldMapper__automaticDefaultMapping(_1) {
		var _2 = [];
		var _3 = isc.shallowClone(this.targetDataSource);
		_3.autoDeriveTitles = true;
		var _4 = _3.getFields();
		for ( var _5 in _4) {
			_2.add({
				name : _5,
				splittedTitle : _4[_5].title.toLowerCase().split(" ")
			})
		}
		;
		for (var i = 0; i < _1.length; i++) {
			if (_1[i].title == null) {
				continue
			}
			var _7 = _1[i].title.toLowerCase().split(" ");
			var _8 = null;
			var _9 = 0;
			for (var j = 0; j < _2.length; j++) {
				if (_2[j].occupied)
					continue;
				var _11 = 0;
				var _12 = _2[j].splittedTitle;
				for (var _13 = 0; _13 < _12.length; _13++) {
					for (var _14 = 0; _14 < _7.length; _14++) {
						if (_7[_14] == _12[_13]) {
							_11++
						}
					}
				}
				if (_11 > _9) {
					_9 = _11;
					_8 = _2[j]
				}
			}
			;
			if (_8) {
				_8.occupied = true;
				_1[i].mappedTo = _8.name
			}
		}
	}, isc.A.getMappedFields = function isc_FieldMapper_getMappedFields() {
		var _1 = this.$87z.getData();
		var _2 = [];
		for (var i = 0; i < _1.length; i++) {
			var _4 = {
				name : _1[i].mappedTo,
				title : _1[i].title
			};
			_2.add(_4)
		}
		;
		return _2
	});
	isc.B._maxIndex = isc.C + 4;
	isc._nonDebugModules = (isc._nonDebugModules != null ? isc._nonDebugModules
			: []);
	isc._nonDebugModules.push('VisualBuilder');
	isc.checkForDebugAndNonDebugModules();
	isc._moduleEnd = isc._VisualBuilder_end = (isc.timestamp ? isc.timestamp()
			: new Date().getTime());
	if (isc.Log && isc.Log.logIsInfoEnabled('loadTime'))
		isc.Log.logInfo('VisualBuilder module init time: '
				+ (isc._moduleEnd - isc._moduleStart) + 'ms', 'loadTime');
	delete isc.definingFramework;
	if (isc.Page)
		isc.Page.handleEvent(null, "moduleLoaded", {
			moduleName : 'VisualBuilder',
			loadTime : (isc._moduleEnd - isc._moduleStart)
		});
} else {
	if (window.isc && isc.Log && isc.Log.logWarn)
		isc.Log.logWarn("Duplicate load of module 'VisualBuilder'.");
}

/*
 * 
 * SmartClient Ajax RIA system Version v10.0p_2014-09-18/EVAL Development Only
 * (2014-09-18)
 * 
 * Copyright 2000 and beyond Isomorphic Software, Inc. All rights reserved.
 * "SmartClient" is a trademark of Isomorphic Software, Inc.
 * 
 * LICENSE NOTICE INSTALLATION OR USE OF THIS SOFTWARE INDICATES YOUR ACCEPTANCE
 * OF ISOMORPHIC SOFTWARE LICENSE TERMS. If you have received this file without
 * an accompanying Isomorphic Software license file, please contact
 * licensing@isomorphic.com for details. Unauthorized copying and use of this
 * software is a violation of international copyright law.
 * 
 * DEVELOPMENT ONLY - DO NOT DEPLOY This software is provided for evaluation,
 * training, and development purposes only. It may include supplementary
 * components that are not licensed for deployment. The separate DEPLOY package
 * for this release contains SmartClient components that are licensed for
 * deployment.
 * 
 * PROPRIETARY & PROTECTED MATERIAL This software contains proprietary materials
 * that are protected by contract and intellectual property law. You are
 * expressly prohibited from attempting to reverse engineer this software or
 * modify this software for human readability.
 * 
 * CONTACT ISOMORPHIC For more information regarding license rights and
 * restrictions, or to report possible license violations, please contact
 * Isomorphic Software by email (licensing@isomorphic.com) or web
 * (www.isomorphic.com).
 * 
 */


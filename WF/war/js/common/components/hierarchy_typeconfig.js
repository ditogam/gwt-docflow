function getHierarchy_typeConfig() {

	var ToolStrip0 = isc.ToolStrip.create({
		autoDraw : false,
		members : [ isc.ToolStripButton.create({
			autoDraw : false,
			icon : "[SKIN]actions/add.png",
			click : function() {
				lgHierarchy_type.startEditingNew();
			}
		}), isc.ToolStripButton.create({
			autoDraw : false,
			icon : "[SKIN]actions/refresh.png",
			click : function() {
				lgHierarchy_type.invalidateCache();
			}
		}) ],
		visibilityMode : "multiple"
	});
	var lgHierarchy_type = isc.ListGrid.create({
		autoDraw : false,
		height : "100%",
		width : "100%",
		dataSource : 'Hierarchy_TypeDS',
		autoFetchData : true,
		showResizeBar : true,
		// canEdit : true,
		dataArrived : function(startRow, endRow) {
			this.selectRange(startRow, startRow + 1);
		},
		selectionChanged : function() {
			var record = lgHierarchy_type.getSelectedRecord();
			if (!record)
				return;
			var _id = record["id"];
			if (!_id)
				return;
			var cr = {
				hierarchy_type_id : _id
			};
			lgHierarchy.setTypeId(_id);

			lgHierarchy_sub_type.fetchData(cr);
		}
	});

	var ToolStrip1 = isc.ToolStrip.create({
		autoDraw : false,
		members : [ isc.ToolStripButton.create({
			autoDraw : false,
			icon : "[SKIN]actions/add.png",
			click : function() {
				var _id = lgHierarchy_type.getSelectedRecord()["id"];
				lgHierarchy_sub_type.startEditingNew({
					hierarchy_type_id : _id
				});
			}
		}), isc.ToolStripButton.create({
			autoDraw : false,
			icon : "[SKIN]actions/refresh.png",
			click : function() {
				lgHierarchy_sub_type.invalidateCache();
			}
		}) ],
		visibilityMode : "multiple"
	});
	var lgHierarchy_sub_type = isc.ListGrid
			.create({
				autoDraw : false,
				height : "100%",
				width : "100%",
				dataSource : 'Hierarchy_Sub_TypeDS',
				autoFetchData : false,
				showResizeBar : true,
				// canEdit : true,
				showRollOverCanvas : true,
				rollOverCanvasConstructor : isc.HLayout,
				rollOverCanvasProperties : {
					snapTo : "TR",
					height : 22,
					width : 22,
					members : [ {
						_constructor : "IButton",
						showDownIcon : false,
						showRollOverIcon : false,
						icon : "[SKIN]actions/edit.png",
						click : "isc.say('Expanded record:' + this.echo(this.parentElement.record))",
						height : 21,
						width : 21
					} ]
				}
			});
	var VLayout1 = isc.VLayout.create({
		autoDraw : false,
		showResizeBar : true,
		members : [ ToolStrip0, lgHierarchy_type, ToolStrip1,
				lgHierarchy_sub_type ]
	});

	var ToolStrip2 = isc.ToolStrip.create({
		autoDraw : false,
		members : [ isc.ToolStripButton.create({
			autoDraw : false,
			icon : "[SKIN]actions/add.png",
			click : function() {
				var _id = lgHierarchy_type.getSelectedRecord()["id"];
				lgHierarchy_sub_type.startEditingNew({
					hierarchy_type_id : _id
				});
				dfLanguages.focusInItem("language_name");
			}
		}), isc.ToolStripButton.create({
			autoDraw : false,
			icon : "[SKIN]actions/refresh.png",
			click : function() {
				lgHierarchy.invalidateCache();
			}
		}) ],
		visibilityMode : "multiple"
	});

	var lgHierarchy = isc.HirarchyTree
			.create({

				canReorderRecords : true,
				canAcceptDroppedRecords : true,
				showRollOverCanvas : true,
				rollOverCanvasConstructor : isc.HLayout,
				rollOverCanvasProperties : {
					snapTo : "TR",
					height : 16,
					width : 38,
					members : [
							{
								_constructor : "IButton",
								showDownIcon : false,
								showRollOverIcon : false,
								icon : "[SKIN]actions/edit.png",
								click : "isc.say('Expanded record:' + this.echo(this.parentElement.record))",
								height : 16,
								width : 16
							},
							{
								_constructor : "ImgButton",
								showDownIcon : false,
								showRollOverIcon : false,
								src : "[SKIN]actions/view.png",
								click : "isc.say('Expanded record:' + this.echo(this.parentElement.record))",
								height : 16,
								width : 16
							} ]
				}

			});

	var VLayout3 = isc.VLayout.create({
		autoDraw : false,
		members : [ ToolStrip2, lgHierarchy ]
	});
	var HLayout1 = isc.HLayout.create({
		autoDraw : false,
		members : [ VLayout1, VLayout3 ]
	})

	return HLayout1;
}




function getLanguageConfig() {

	var ToolStrip0 = isc.ToolStrip.create({
		autoDraw : false,
		members : [ isc.ToolStripButton.create({
			autoDraw : false,
			icon : "[SKIN]actions/add.png",
			click : function() {
				dfLanguages.editNewRecord();
				dfLanguages.focusInItem("language_name");
			}
		}), isc.ToolStripButton.create({
			autoDraw : false,
			icon : "[SKIN]actions/refresh.png",
			click : function() {
				lgLanguages.invalidateCache();
			}
		}) ],
		visibilityMode : "multiple"
	});
	var lgLanguages = isc.ListGrid.create({
		autoDraw : false,
		height : "100%",
		width : "10%",
		dataSource : 'LanguageDS',
		autoFetchData : true,
		showResizeBar : true,
		fields : [ {
			name : "id",
			title : "ID",
			width : 50
		}, {
			name : "language_name",
			title : "Language"
		}, {
			name : "img_Id",
			title : "IMG",
			width : 50,
			type : "image",
			imageURLPrefix : "getimage.jsp?id=",
			imageURLSuffix : "",
			align : "center"
		} ],
		dataArrived : function(startRow, endRow) {
			this.selectRange(startRow, startRow + 1);
		},
		selectionChanged : function() {
			dfLanguages.editRecord(this.getSelectedRecord());
		}
	});

	var dfLanguages = isc.DynamicForm.create({
		autoDraw : false,
		height : "100%",
		numCols : 1,
		width : "*",
		dataSource : lgLanguages.dataSource,
		fields : [
				{
					name : "id",
					_constructor : "HiddenItem"
				},
				{
					name : "language_name",
					title : "Language Name",
					defaultValue : "",
					required : true,
					_constructor : "TextItem",

				},
				{
					name : "img_Id",
					title : "Image",
					type : "integer",
					required : true,
					file_types : "image/*",
					_constructor : "ImageUploadItem"
				},
				{
					name : "btnSave",
					title : "Save",
					_constructor : "ButtonItem",
					click : function() {
						if (!this.form.validate())
							return;
						var _id = this.form.getValue("id");
						if (_id)
							lgLanguages.updateData(this.form.getValues());
						else
							lgLanguages.addData(this.form.getValues(),
									function(dsResponse, data, dsRequest) {
										lgLanguages.deselectAllRecords();
										lgLanguages.selectRecord(data);
									});
					}
				} ],
		titleOrientation : "top"
	});

	var HLayout0 = isc.HLayout.create({
		autoDraw : false,
		members : [ lgLanguages, dfLanguages ]
	});

	var VLayout1 = isc.VLayout.create({
		autoDraw : false,
		members : [ ToolStrip0, HLayout0 ]
	});
	return VLayout1;
}
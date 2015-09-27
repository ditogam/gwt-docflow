function getTemplateConfig() {

	var ToolStrip0 = isc.ToolStrip.create({
		autoDraw : false,
		members : [ isc.ToolStripButton.create({
			autoDraw : false,
			icon : "[SKIN]actions/add.png",
			click : function() {
				lgDocTemplates.addEditRecord();
			}
		}), isc.ToolStripButton.create({
			autoDraw : false,
			icon : "[SKIN]actions/edit.png",
			click : function() {
				var _rec = lgDocTemplates.getSelectedRecord();
				if (!_rec)
					return;
				lgDocTemplates.addEditRecord(_rec);
			}
		}), isc.ToolStripButton.create({
			autoDraw : false,
			icon : "[SKIN]actions/refresh.png",
			click : function() {
				lgDocTemplates.invalidateCache();
			}
		}) ],
		visibilityMode : "multiple"
	});
	var lgDocTemplates = isc.ListGrid.create({
		autoDraw : false,
		height : "100%",
		width : "100%",
		dataSource : 'DocumentTemplateDS',
		autoFetchData : true,
		addEditRecord : function(_rec) {
			var _vb = setupNewVisualBuilder(_rec);
			var _this = this;
			var _callBack = function() {
				_vb.destroy();
				if (arguments && arguments.length > 0)
					_this.invalidateCache();
			}
			isc.SaveWindow.createWindowWithItem(_vb, _callBack);
		}
	});

	var VLayout1 = isc.VLayout.create({
		autoDraw : false,
		members : [ ToolStrip0, lgDocTemplates ]
	});

	return VLayout1;
}
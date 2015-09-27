if (window.isc && !window.isc.module_SaveWindow) {
	isc.module_SaveWindow = 1;
	isc.ClassFactory.defineClass("SaveWindow", "Window");
	var _props = {
		autoDraw : false,
		height : "100%",
		width : "100%",
		autoCenter : true,
		isModal : true,
		title : "Window0",
		showMinimizeButton : false,
		showShadow : false,
		canDragReposition : false,
		canDragResize : false,
		init : function() {
			this.Super('init', arguments);
			// this.initWidget(this);
		},
		closeAndDestroy : function() {
			this.destroy();
			if (this.callback) {
				this.callback(arguments);
			}
		},
		saveAndClose : function() {
			try {
				if (this._form) {
					var _this = this;
					var _callBack = function() {
						_this.closeAndDestroy(1);
					}
					this._form.saveData(_callBack);
				}
			} catch (e) {
				// TODO: handle exception
			}
		},
		visibilityChanged : function(isVisible) {
			this.Super('visibilityChanged', arguments);
			if (!isVisible)
				this.closeAndDestroy();
		},
		initWidget : function() {
			this.Super('initWidget', arguments);
			this.toolstrip = isc.ToolStrip.create({
				autoDraw : false,
				members : [ isc.ToolStripButton.create({
					autoDraw : false,
					icon : "[SKIN]/actions/save.png",
					title : "",
					wnd : this,
					click : "this.wnd.saveAndClose();"
				}), isc.ToolStripButton.create({
					autoDraw : false,
					icon : "[SKIN]/actions/close.png",
					title : "",
					wnd : this,
					click : "this.wnd.closeAndDestroy();"
				}) ],
				visibilityMode : "multiple"
			});
			this.addItem(this.toolstrip);
		}
	};
	isc.SaveWindow.addProperties(_props);
	_props = {
		createWindowWithJs : function() {
			isc.SaveWindow.createWindowWithItem();
		},
		createWindowWithItem : function() {
			var _window = isc.SaveWindow.create();

			if (arguments && arguments.length > 0) {
				_window._form = arguments[0];
				_window.addItem(_window._form);
				if (arguments.length > 1) {
					_window.callback = arguments[1];
				}
			}
			_window.show();
			return _window;
		}
	};
	isc.SaveWindow.addClassProperties(_props);
}

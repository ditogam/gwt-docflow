if (window.isc && !window.isc.module_LanguageEditor) {
	isc.module_LanguageEditor = 1;

	isc.DataSource.create({
		allowAdvancedCriteria : true,
		ID : "LanguageEditor",
		Constructor : "LanguageEditor",
		fields : [],
		inheritsFrom : "Canvas"
	});
	isc.DataSource.create({
		allowAdvancedCriteria : true,
		ID : "LanguageEditorItem",
		Constructor : "LanguageEditorItem",
		fields : [],
		inheritsFrom : "CanvasItem"
	});
	var _tiProps = {
		getComponentId : function() {
			return this.getID() + "_aceEditor";
		},
		getAutoCompleteId : function() {
			return this.getComponentId() + "_auto_complete";
		},
		getLanguageName : function() {
			if (this.editor)
				return this.editor.session.getMode();
			return null;
		},
		setLanguageName : function(language_name) {
			this.language_name = language_name;
			if (!this.language_name)
				this.language_name = "javascript";
			if (this.editor)
				this.editor.session.setMode("ace/mode/" + this.language_name);
		},
		init : function() {
			this.Super('init', arguments);
			ace.require("ace/ext/language_tools");
			this.setLanguageName(this.language_name);

		},
		getInnerHTML : function() {
			var _und;
			if (this.editor)
				this.editor.destroy();
			this.editor = _und;
			return "<pre style='width:100%;height:95%' ID='"
					+ this.getComponentId() + "'></pre>";
		},
		changedEditor : function(value) {
			var _value = this.editor.getValue();
			if (this.change_hendlers)
				for (var i = 0; i < this.change_hendlers.length; i++) {
					var h = this.change_hendlers[i];
					try {
						h.editorValueChanged(_value);
					} catch (e) {
						// TODO: handle exception
					}

				}
		},
		destroy : function() {
			if (this.editor)
				this.editor.destroy();
			this.Super("destroy", arguments);
		},
		// call superclass method to draw, then have CKEditor replace the
		// textarea we
		// wrote out with the CKEditor widget
		draw : function() {
			if (!this.readyToDraw())
				return this;
			this.Super("draw", arguments);
			if (this.editor)
				return this;
			var _id = this.getComponentId();
			this.setLanguageName(this.language_name);
			this.editor = ace.edit(_id);

			this.editor.session.setMode("ace/mode/" + this.language_name);
			this.editor.container.class = "LanguageEditor";
			this.editor.setTheme("ace/theme/tomorrow");
			this.editor.setFontSize(11);
			var _this = this;
			this.editor.on("change", function(e) {
				_this.changedEditor(e.data);
			});
			this.editor.setOptions({
				enableBasicAutocompletion : true,
				enableSnippets : true,
				enableLiveAutocompletion : false
			});

			var cls_name = ".ace_autocomplete";
			this.editor.execCommand("startAutocomplete");
			var k = $(cls_name);
			var el;
			for (var i = 0; i < k.length; i++) {
				if (!k[i].id) {
					el = k[i];
					el.id = this.getAutoCompleteId();
					$(el).prependTo(this.editor.container.parentNode);
					el.style.display = "none";
					break;
				}
			}
			this.editor.setValue(this.defaultValue);
			return this;
		},
		addChangeHendler : function(_h) {
			if (!this.change_hendlers)
				this.change_hendlers = [];
			this.change_hendlers.push(_h);
		},
		redrawOnResize : false
	// see next section
	};

	isc.ClassFactory.defineClass("LanguageEditor", "Canvas");

	isc.LanguageEditor.addProperties(_tiProps);

	var _eii = {
		init : function() {
			this.Super('init', arguments);
		},
		editorValueChanged : function(newValue) {
			var _visible = this.isVisible();
			if (_visible) {
				this.Super('setValue', newValue);
				this.tmp_val = newValue;
			}
		},
		createCanvas : function() {
			var e = isc.LanguageEditor.create({
				width : "100%",
				height : "100%",
				autDraw : false,
				language_name : this.language_name,
				defaultValue : this.getValue()
			});
			e.addChangeHendler(this);
			var _this = this;
			this.form.addProperties({
				draw : function() {
					this.Super("draw", arguments)
					_this.setValue(_this.getValue())
				}
			});
			return e;
		},
		setValue : function(newValue) {
			if (this.canvas && this.canvas.editor) {
				this.canvas.editor.setValue(newValue, -1);
			} else
				this.tmp_val = newValue;
			this.Super('setValue', arguments);
		},
		setLanguageName : function(language_name) {
			this.language_name = language_name;
			if (this.canvas && this.canvas.editor)
				return this.canvas.setLanguageName(language_name);
		},
		getLanguageName : function() {
			if (this.canvas && this.canvas.editor)
				return this.canvas.getLanguageName();
			return null;
		}
	};
	isc.ClassFactory.defineClass("LanguageEditorItem", "CanvasItem");
	isc.LanguageEditorItem.addProperties(_eii);

}
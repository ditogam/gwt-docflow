if (window.isc && !window.isc.module_TokenInput) {
	isc.module_TokenInput = 1;

	isc.DataSource.create({
		allowAdvancedCriteria : true,
		ID : "TokenInput",
		Constructor : "TokenInput",
		fields : [],
		inheritsFrom : "Canvas"
	});

	var _tiProps = {
		showEdges : true,
		// write out a textarea with a known ID
		initWidget : function() {
			this.Super('initWidget', arguments);
			this.localdata = [];
		},
		addDataaa : function(_val) {
			this.localdata.push(_val);
		},
		getCompID : function() {
			return this.getID() + "_TokenInput";
		},
		getInnerHTML : function() {
			var str = this.getCompID();
			var ret = "    <div style='width=100%;height=100%'>\n"
					+ "        <input type='text' id='" + str + "'  />\n" + ""
					+ "\n" + "    </div>";
			return ret;
		},
		draw : function() {
			if (!this.readyToDraw())
				return this;
			this.Super("draw", arguments);
			var str = this.getCompID();
			var component = $("#" + str + "");
			component.tokenInput(this.localdata);
			return this;
		},
		redrawOnResize : false
	// see next section
	};

	isc.ClassFactory.defineClass("TokenInput", "Canvas");

	isc.TokenInput.addProperties(_tiProps);

}
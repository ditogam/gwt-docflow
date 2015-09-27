function getTestCNF() {
	var lbl = isc.Label.create({});
	var lg = isc.ListGrid.create({
		autoDraw : false,
		height : "100%",
		width : "100%",
		autoFetchData : true,
		dataSource : isc.DataSource.get('HierarchyDS'),
		showRowNumbers : true,
		fields : [ {
			name : "id",
			title : "ID",
			width : 50
		}, {
			name : "name",
			title : "Language"
		} ]
	});
	var VLayout1 = isc.VLayout.create({
		autoDraw : false,
		members : [ lbl, isc.IButton.create({
			left : 20,
			top : 20,
			width : 150,
			height : 25,
			title : "Refresh",
			click : function() {
				lg.refreshCurrent(function() {
					isc.say("done");
				})
			}
		}), lg ]
	});
	return VLayout1;

}
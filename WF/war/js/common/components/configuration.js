function createConfiguration() {
	var TabSet0 = isc.TabSet.create({
		ID : "TabSet0",
		autoDraw : false,
		height : "100%",
		overflow : "hidden",
		width : "100%",
		tabs : [ {
			title : "Languages",
			pane : getLanguageConfig(),
		}, {
			title : "Hierarchy",
			pane : getHierarchy_typeConfig(),
		}, {
			title : "Templates",
			pane : getTemplateConfig(),
		} ],
		destroyPanes : false
	});
	return TabSet0;
}

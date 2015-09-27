if (window.isc && !window.isc.module_HirarchyTree) {
	isc.module_HirarchyTree = 1;

	isc.DataSource.create({
		allowAdvancedCriteria : true,
		ID : "HirarchyTree",
		Constructor : "HirarchyTree",
		fields : [],
		inheritsFrom : "TreeGrid"
	});

	var _lngProps = {
		height : "100%",
		width : "100%",
		dataSource : "HierarchyDS",
		showFilterEditor : true,
		autoFetchAsFilter : true,
		fields : [ {
			name : "name",
			canFilter : true,
			filterOnKeypress : true
		} ],
		initWidget : function() {
			this.Super('initWidget', arguments);
			if(this.type_id){
				this.setTypeId(this.type_id)
			}
		},
		setTypeId : function(_type_id) {
			var cr = {
				hierarchy_type_id : _type_id
			};
			this.fetchData(cr);
		},
		dataProperties : {
			nameProperty : "caption_id",
			idField : "id",
			parentIdField : "parent_id",
			iconField : "icon",
			dataArrived : function(parentNode) {
				var criteria = this.criteria;
				if (criteria && criteria.name && criteria.name.length > 3) {
					this.openAll();
				}
			}
		}

	};

	isc.ClassFactory.defineClass("HirarchyTree", "TreeGrid");

	isc.HirarchyTree.addProperties(_lngProps);

}
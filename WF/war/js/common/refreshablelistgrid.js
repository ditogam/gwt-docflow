if (window.isc && !window.isc.module_RefreshableGrid) {
	isc.module_RefreshableGrid = 1;

	var _tiProps = {
		refreshCurrent : function(_callback) {
			var dataSource = this.getDataSource();
			var visibleRows = this.getVisibleRows();
			var startRow = visibleRows[0] - this.data.resultSize, endRow = visibleRows[1]
					+ this.data.resultSize;

			if (startRow < 0) {
				startRow = 0;
			}

			var request = {
				startRow : startRow,
				endRow : endRow,
				sortBy : this.getSort(),
				showPrompt : false
			};
			var _this = this;
			var callback = function(dsResponse, data, dsRequest) {

				var result = dsResponse.data, initialData = [];

				// correctly position the result in the resultset's cache
				initialData.length = dsResponse.totalRows;

				copyArray(result, initialData, dsResponse.startRow);

				var resultSet = isc.ResultSet.create({
					dataSource : _this.getDataSource(),
					initialLength : dsResponse.totalRows,
					initialData : initialData,
					sortSpecifiers : _this.getSort(),
					criteria : _this.getCriteria()
				});

				_this.setData(resultSet);
				if (_callback)
					_callback(dsResponse, data, dsRequest);
			};
			dataSource.fetchData(this.getCriteria(), callback, request);
		}

	};



	isc.ListGrid.addProperties(_tiProps);

}
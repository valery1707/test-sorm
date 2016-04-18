angular.module('app').
service('gridHelper', [function () {
	return function ($scope, service, paginationOptions, gridExt) {
		$scope.loadPage = function () {
			$scope.isLoading = true;

			var pageable = {
				size: paginationOptions.pageSize,
				page: (paginationOptions.pageNumber - 1),
				sort: paginationOptions.sort
			};

			service.filter(pageable, $scope.filterModel, function (data) {
				//console.log(data);
				$scope.gridOptions.totalItems = data.totalElements;
				$scope.gridOptions.data = data.content;
				$scope.isLoading = false;
			});
		};
		$scope.gridOptions = {
			paginationPageSizes: [25, 50, 75],
			paginationPageSize: paginationOptions.pageSize,
			useExternalPagination: true,
			useExternalSorting: true,
			enableFiltering: true,
			useExternalFiltering: true,
			onRegisterApi: function (gridApi) {
				$scope.gridApi = gridApi;
				$scope.gridApi.core.on.sortChanged($scope, function (grid, sortColumns) {
					if (sortColumns.length == 0) {
						paginationOptions.sort = null;
					} else {
						paginationOptions.sort = [];
						for (var i = 0; i < sortColumns.length; i++) {
							var col = sortColumns[i];
							paginationOptions.sort.push(col.field + ',' + col.sort.direction.toUpperCase())
						}
					}
					$scope.loadPage();
				});
				gridApi.pagination.on.paginationChanged($scope, function (newPage, pageSize) {
					paginationOptions.pageNumber = newPage;
					paginationOptions.pageSize = pageSize;
					$scope.loadPage();
				});
				$scope.gridApi.core.on.filterChanged($scope, function () {
					var grid = this.grid;
					for (var i = 0; i < grid.columns.length; i++) {
						var col = grid.columns[i];
						var mapper = col.colDef.filterTermMapper;
						var getter = function (filter, mapper) {
							var term = filter.term;
							var type = typeof term;
							if (type != 'boolean' && type != 'number') {
								if (!term) {
									return undefined;
								}
							}
							mapper = filter.filterTermMapper || mapper;
							if (mapper) {
								term = mapper(term);
							}
							return term;
						};
						if (col.filters.length == 1) {
							$scope.filterModel[col.field] = getter(col.filters[0], mapper);
						} else {
							$scope.filterModel[col.field] = [];
							for (var t = 0; t < col.filters.length; t++) {
								$scope.filterModel[col.field].push(getter(col.filters[t], mapper))
							}
						}
					}
					$scope.loadPage();
				});
			}
		};
		$scope.gridOptions = jQuery.extend($scope.gridOptions, gridExt);
		return $scope.gridOptions;
	}
}])
;

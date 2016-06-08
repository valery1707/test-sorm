angular.module('app').
service('gridHelper', [function () {
	return function ($scope, service, paginationOptions, gridExt) {

		//region Action column
		var actionColWidth = 34;
		if ($scope.principal && $scope.actions) {
			var colActions = $scope.actions.filter(function (action) {
				return action != undefined
					   && 'perRow' in action && action.perRow
					   && $scope.principal.hasAnyPermission(action.permissions)
			});
			actionColWidth = (colActions.length * (actionColWidth - 10)) + 10;
		}
		var actionCol = gridExt.columnDefs.find(function (col) {
			return col.field == '_actions';
		});
		if (actionCol) {
			if (actionColWidth <= 10) {
				//Столбец есть, а действий нет
				gridExt.columnDefs.remove(gridExt.columnDefs.indexOf(actionCol));
			} else {
				//Добавляем поля по-умолчанию
				jQuery.extend(actionCol, jQuery.extend({
					name: '',
					enableFiltering: false,
					enableSorting: false,
					enableColumnMenu: false,
					cellTemplate: 'view/common/grid/row-actions.html',
					width: actionColWidth,
					maxWidth: actionColWidth,
					minWidth: actionColWidth
				}, actionCol));
			}
		}
		//endregion

		//region Filter columns by permission
		if ($scope.principal) {
			gridExt.columnDefs
					.filter(function (col) {
						return col != undefined && 'permissions' in col && !$scope.principal.hasAnyPermission(col.permissions);
					})
					.forEach(function (col) {
						gridExt.columnDefs.remove(gridExt.columnDefs.indexOf(col));
					});
		}
		//endregion

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
			//minRowsToShow: 10,
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
}]).
service('formBuilder', [function () {
	return function ($scope, $state, $stateParams, service, toastr, prefix) {
		$scope.model = {};
		$scope.serverErrors = {};
		$scope.serverErrorValidator = function (field) {
			const fieldDef = $scope.entityForm[field];
			const fieldValue = fieldDef.$viewValue;
			var errors = [];
			const errorsDef = $scope.serverErrors[field];
			if (errorsDef) {//Массив с ошибками
				for (var errorI in errorsDef) {//Отдельная ошибка
					if (errorsDef.hasOwnProperty(errorI)) {
						const errorDef = errorsDef[errorI];
						if (errorDef.rejectedValue == fieldValue) {
							errors.push(errorDef.defaultMessage);
						}
					}
				}
			}
			return {isValid: errors.length == 0, message: errors.join('\r\n<br/>')};
		};
		if ($stateParams.id) {
			service.get($stateParams,
					function (data) {
						$scope.model = data;
					},
					function () {
						$scope.cancel();
					});
		}
		$scope.cancel = function () {
			$state.go(prefix + '.list');
		};
		$scope.save = function () {
			service.save($scope.model,
					function (data) {
						$state.go(prefix + '.list');
					},
					function (error) {
						if (error && error.status === 400 && error.data) {
							const errorsDef = error.data;
							$scope.serverErrors = errorsDef;
							for (var field in errorsDef) {
								if (errorsDef.hasOwnProperty(field)) {
									$scope.$broadcast('angularValidation.revalidate', field);
								}
							}
						} else {
							toastr.error('Unknown server error', 'Error');
						}
					});
		};
	};
}])
;

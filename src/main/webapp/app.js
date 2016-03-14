'use strict';

var apiBaseUrl = '';

// Declare app level module which depends on views, and components
angular.module('app', [
	'ngRoute',
	'ngResource'
	, 'ngTouch'
	, 'ui.grid', 'ui.grid.pagination', 'ui.grid.resizeColumns'
	, 'ghiscoding.validation'
	, 'pascalprecht.translate'
]).

factory('dataService', ['$resource', function ($resource) {
	return $resource(apiBaseUrl + '/data', {}, {
		query: {
			method: 'GET',
			params: {},
			isArray: false,
			cache: false
		},
		filter: {
			method: 'POST',
			isArray: false,
			cache: false
		}
	});
}]).
controller('dataCtrl', ['$scope', 'dataService', 'uiGridConstants', function ($scope, service, uiGridConstants) {
	$scope.filterModel = {};

	var paginationOptions = {
		pageNumber: 1,
		pageSize: 25,
		sort: ["dateTime,ASC"]
	};

	$scope.gridOptions = {
		paginationPageSizes: [25, 50, 75],
		paginationPageSize: 25,
		useExternalPagination: true,
		useExternalSorting: true,
		enableFiltering: true,
		useExternalFiltering: true,
		columnDefs: [
			{field: 'dateTime', sort: {direction: uiGridConstants.ASC, priority: 0}, enableFiltering: false},
			{field: 'protocol'},
			{field: 'srcIp', filter: {placeholder: 'IP/CIDR'}},
			{field: 'srcPort', enableFiltering: false},
			{field: 'dstIp', filter: {placeholder: 'IP/CIDR'}},
			{field: 'dstPort', enableFiltering: false},
		],
		onRegisterApi: function(gridApi) {
			$scope.gridApi = gridApi;
			$scope.gridApi.core.on.sortChanged($scope, function(grid, sortColumns) {
				if (sortColumns.length == 0) {
					paginationOptions.sort = null;
				} else {
					paginationOptions.sort = [];
					for (var i = 0; i < sortColumns.length; i++) {
						var col = sortColumns[i];
						paginationOptions.sort.push(col.field + ',' + col.sort.direction.toUpperCase())
					}
				}
				getPage();
			});
			gridApi.pagination.on.paginationChanged($scope, function (newPage, pageSize) {
				paginationOptions.pageNumber = newPage;
				paginationOptions.pageSize = pageSize;
				getPage();
			});
			$scope.gridApi.core.on.filterChanged($scope, function () {
				var grid = this.grid;
				for (var i = 0; i < grid.columns.length; i++) {
					var col = grid.columns[i];
					var term = col.filters[0].term;
					$scope.filterModel[col.field] = term;
				}
				getPage();
			});
		}
	};

	var getPage = function () {
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

	getPage();
}])
;

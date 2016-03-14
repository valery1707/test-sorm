'use strict';

var apiBaseUrl = '';

// Declare app level module which depends on views, and components
angular.module('app', [
	'ngRoute',
	'ngResource'
	, 'ngTouch'
	, 'ui.grid', 'ui.grid.pagination'
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
		columnDefs: [
			{field: 'dateTime', sort: {direction: uiGridConstants.ASC, priority: 0}},
			{field: 'protocol'},
			{field: 'srcIp'},
			{field: 'srcPort'},
			{field: 'dstIp'},
			{field: 'dstPort'},
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
		}
	};

	var getPage = function () {
		$scope.isLoading = true;

		var pageable = {
			size: paginationOptions.pageSize,
			page: (paginationOptions.pageNumber - 1),
			sort: paginationOptions.sort
		};

		service.filter(pageable, null, function (data) {
			//console.log(data);
			$scope.gridOptions.totalItems = data.totalElements;
			$scope.gridOptions.data = data.content;
			$scope.isLoading = false;
		});
	};

	getPage();
}])
;

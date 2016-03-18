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

directive('dateTimePicker', function(){
	return {
		restrict : "A",
		require: 'ngModel',
		link : function(scope, element, attrs, ngModel){
			$(function(){
				$(element).datetimepicker({
					format: 'Y-m-d H:i',
					step: 15,
					lang: 'ru',
					dayOfWeekStart: 1,
					onChangeDateTime: function (dp, $input) {
						scope.$apply(function () {
							ngModel.$setViewValue($input.val());
						});
					},
					onClose: function (dp, $input) {
						scope.$apply(function () {
							ngModel.$setViewValue($input.val());
						});
					}
				});
			})
		}
	}
}).

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
			{
				field: 'dateTime',
				sort: {direction: uiGridConstants.ASC, priority: 0},
				filterHeaderTemplate: '<div' +
									  ' class="ui-grid-filter-container"' +
									  ' ng-repeat="colFilter in col.filters"' +
									  ' ng-class="{\'ui-grid-filter-cancel-button-hidden\' : colFilter.disableCancelFilterButton === true }">' +
									  '  <input' +
									  '   type="text"' +
									  '   style="display:inline; width:100%"' +
									  '   class="ui-grid-filter-input ui-grid-filter-input-{{$index}}"' +
									  '   date-time-picker' +
									  '   ng-model="col.filters[$index].term"' +
									  '   ng-attr-placeholder="{{colFilter.placeholder || \'\'}}"' +
									  '   aria-label="{{colFilter.ariaLabel || aria.defaultFilterLabel}}">' +
									  '  <div' +
									  '   role="button"' +
									  '   class="ui-grid-filter-button"' +
									  '   ng-click="removeFilter(colFilter, $index)"' +
									  '   ng-if="!colFilter.disableCancelFilterButton"' +
									  '   ng-disabled="colFilter.term === undefined || colFilter.term === null || colFilter.term === \'\'"' +
									  '   ng-show="colFilter.term !== undefined && colFilter.term !== null && colFilter.term !== \'\'">' +
									  '    <i' +
									  '     class="ui-grid-icon-cancel"' +
									  '     ui-grid-one-bind-aria-label="aria.removeFilter">' +
									  '      &nbsp;' +
									  '    </i>' +
									  '  </div>' +
									  '</div>',
				filters: [{placeholder: 'from'}, {placeholder: 'to'}],
				filterTermMapper: function(value) {
					return moment(value).format('YYYY-MM-DD[T]HH:mm:ss.SSSZ');
				}
			},
			{field: 'protocol'},
			{field: 'srcIp', filter: {placeholder: 'IP/CIDR'}},
			{field: 'srcPort', filter: {placeholder: '<, <=, =, >, >=, ...'}},
			{field: 'dstIp', filter: {placeholder: 'IP/CIDR'}},
			{field: 'dstPort', filter: {placeholder: '<, <=, =, >, >=, ... '}},
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

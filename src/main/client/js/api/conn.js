angular.module('app').
factory('connService', ['$resource', function ($resource) {
	return $resource(apiBaseUrl + '/conn', {}, serviceCommonConfig);
}]).
controller('connCtrl', ['$scope', 'connService', 'uiGridConstants', 'gridHelper', function ($scope, service, uiGridConstants, gridHelper) {
	$scope.filterModel = {};

	var paginationOptions = {
		pageNumber: 1,
		pageSize: 25,
		sort: ["ts,ASC"]
	};

	gridHelper($scope, service, paginationOptions, {
		columnDefs: [
			{
				field: 'ts',
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
			{field: 'proto'},
			{field: 'idOrigHost', filter: {placeholder: 'IP/CIDR'}},
			{field: 'idOrigPort', filter: {placeholder: '<, <=, =, >, >=, ...'}},
			{field: 'idRespHost', filter: {placeholder: 'IP/CIDR'}},
			{field: 'idRespPort', filter: {placeholder: '<, <=, =, >, >=, ... '}},
		],
	});

	$scope.loadPage();
}])
;

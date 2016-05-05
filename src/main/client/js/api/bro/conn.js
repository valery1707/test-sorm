angular.module('app').
config(['$stateProvider', function ($stateProvider) {
	$stateProvider
			.state('bro.conn', {
				url: "/conn",
				templateUrl: 'view/bro/conn.html',
				controller: 'broConnCtrl'
			});
}]).
factory('broConnService', ['$resource', function ($resource) {
	return $resource(apiBaseUrl + '/bro/conn', {}, serviceCommonConfig);
}]).
controller('broConnCtrl', ['$scope', 'broConnService', 'uiGridConstants', 'gridHelper', 'dateTimePickerFilterTemplate', '$stateParams', function ($scope, service, uiGridConstants, gridHelper, filterTemplate, $stateParams) {
	$scope.filterModel = {
		taskId: $stateParams.id
	};

	var paginationOptions = {
		pageNumber: 1,
		pageSize: 25,
		sort: ["ts,DESC"]
	};

	gridHelper($scope, service, paginationOptions, {
		columnDefs: [
			{
				field: 'ts',
				sort: {direction: uiGridConstants.DESC, priority: 0},
				filterHeaderTemplate: filterTemplate(),
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

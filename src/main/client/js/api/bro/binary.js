angular.module('app').
config(['$stateProvider', function ($stateProvider) {
	$stateProvider
			.state('bro.binary', {
				url: "/binary",
				templateUrl: 'view/common/grid/grid.html',
				controller: 'broBinaryCtrl'
			});
}]).
factory('broBinaryService', ['$resource', function ($resource) {
	return $resource(apiBaseUrl + '/bro/binary', {}, serviceCommonConfig);
}]).
controller('broBinaryCtrl', ['$scope', 'broBinaryService', 'uiGridConstants', 'gridHelper', '$stateParams', function ($scope, service, uiGridConstants, gridHelper, $stateParams) {
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
				filterHeaderTemplate: 'view/common/grid/filter/dateTime.html',
				filters: [{placeholder: 'from'}, {placeholder: 'to'}],
				filterTermMapper: function(value) {
					return moment(value).format('YYYY-MM-DD[T]HH:mm:ss.SSSZ');
				}
			},
			{field: 'idOrigHost', filter: {placeholder: 'IP/CIDR'}},
			{field: 'idOrigPort', filter: {placeholder: '<, <=, =, >, >=, ...'}},
			{field: 'idRespHost', filter: {placeholder: 'IP/CIDR'}},
			{field: 'idRespPort', filter: {placeholder: '<, <=, =, >, >=, ... '}},
			{field: 'protocol'},
		],
	});

	$scope.loadPage();
}])
;

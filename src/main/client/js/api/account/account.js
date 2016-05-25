angular.module('app').
config(['$stateProvider', function ($stateProvider) {
	$stateProvider
			.state('account', {
				abstract: true,
				data: {
					permissions: {
						only: ['SUPER']
					}
				},
				url: '/account',
				templateUrl: 'view/common/parent.html'
			})
			.state('account.list', {
				url: "/",
				templateUrl: 'view/common/grid/grid.html',
				controller: 'accountCtrl'
			});
}]).
factory('accountService', ['$resource', function ($resource) {
	return $resource(apiBaseUrl + '/account', {}, serviceCommonConfig);
}]).
controller('accountCtrl', ['$scope', 'accountService', 'uiGridConstants', 'gridHelper', 'dateTimePickerFilterTemplate', '$state', 'principal', function ($scope, service, uiGridConstants, gridHelper, filterTemplate, $state, principal) {
	$scope.filterModel = {};

	var paginationOptions = {
		pageNumber: 1,
		pageSize: 25,
		sort: ["id,ASC"]
	};

	gridHelper($scope, service, paginationOptions, {
		columnDefs: [
			{
				field: 'id',
				sort: {direction: uiGridConstants.DESC, priority: 0}
			},
		],
	});

	$scope.loadPage();
}])
;
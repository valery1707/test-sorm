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
controller('accountCtrl', ['$scope', 'accountService', 'uiGridConstants', 'gridHelper', '$state', 'principal', function ($scope, service, uiGridConstants, gridHelper, $state, principal) {
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
			{field: 'username'},
			{
				field: 'active',
				filter: {
					type: uiGridConstants.filter.SELECT,
					selectOptions: [
						{value: true, label: 'True'},
						{value: false, label: 'False'}
					]
				}
			},
			{
				field: 'role',
				filter: {
					type: uiGridConstants.filter.SELECT,
					selectOptions: [
						{value: 'SUPER', label: 'SUPER'},
						{value: 'ADMIN', label: 'ADMIN'},
						{value: 'OPERATOR', label: 'OPERATOR'},
						{value: 'SUPERVISOR', label: 'SUPERVISOR'}
					]
				}
			},
			{
				field: 'activeUntil',
				filterHeaderTemplate: 'view/common/grid/filter/dateTime.html',
				filters: [{placeholder: 'from'}, {placeholder: 'to'}],
				filterTermMapper: function (value) {
					return moment(value).format('YYYY-MM-DD');
				}
			},
			{field: 'agency'},
		],
	});

	$scope.loadPage();
}])
;
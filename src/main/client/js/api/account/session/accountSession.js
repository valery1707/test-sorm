angular.module('app').
config(['$stateProvider', function ($stateProvider) {
	$stateProvider
			.state('accountSession', {
				abstract: true,
				data: {
					permissions: {
						only: ['SUPER']
					}
				},
				url: '/accountSession',
				templateUrl: 'view/common/parent.html'
			})
			.state('accountSession.list', {
				url: "/",
				templateUrl: 'view/common/grid/grid.html',
				controller: 'accountSessionCtrl'
			});
}]).
factory('accountSessionService', ['$resource', function ($resource) {
	return $resource(apiBaseUrl + '/account/session', {}, serviceCommonConfig);
}]).
controller('accountSessionCtrl', ['$scope', 'accountSessionService', 'uiGridConstants', 'gridHelper', 'dateTimePickerFilterTemplate', '$state', 'principal', function ($scope, service, uiGridConstants, gridHelper, filterTemplate, $state, principal) {
	$scope.filterModel = {};

	var paginationOptions = {
		pageNumber: 1,
		pageSize: 25,
		sort: ["loginAt,DESC"]
	};

	gridHelper($scope, service, paginationOptions, {
		columnDefs: [
			{
				field: 'id',
			},
			{field: 'accountUsername', enableFiltering: true},
			{
				field: 'loginAt',
				sort: {direction: uiGridConstants.DESC, priority: 0},
				filterHeaderTemplate: filterTemplate(),
				filters: [{placeholder: 'from'}, {placeholder: 'to'}],
				filterTermMapper: function (value) {
					return moment(value).format('YYYY-MM-DD[T]HH:mm:ss.SSSZ');
				}
			},
			{
				field: 'loginAs',
				filter: {
					type: uiGridConstants.filter.SELECT,
					selectOptions: [
						{value: 'MANUAL', label: 'MANUAL'},
						{value: 'INTERACTIVE', label: 'INTERACTIVE'},
						{value: 'REMEMBER_ME', label: 'REMEMBER_ME'}
					]
				}
			},
			{field: 'sessionId'},
			{field: 'details', visible: false},
			{
				field: 'logoutAt',
				filterHeaderTemplate: filterTemplate(),
				filters: [{placeholder: 'from'}, {placeholder: 'to'}],
				filterTermMapper: function (value) {
					return moment(value).format('YYYY-MM-DD[T]HH:mm:ss.SSSZ');
				}
			},
			{
				field: 'logoutAs',
				filter: {
					type: uiGridConstants.filter.SELECT,
					selectOptions: [
						{value: 'MANUAL', label: 'MANUAL'},
						{value: 'TIMEOUT', label: 'TIMEOUT'},
						{value: 'SERVER_RESTART', label: 'SERVER_RESTART'}
					]
				}
			},
			{
				field: 'duration',
				enableFiltering: false,
				cellFilter: 'secondToPeriod'
			},
		],
	});

	$scope.loadPage();
}])
;
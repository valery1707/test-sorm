angular.module('app').
config(['$stateProvider', function ($stateProvider) {
	$stateProvider
			.state('task', {
				abstract: true,
				data: {
					permissions: {
						only: ['ADMIN', 'OPERATOR']
					}
				},
				url: '/task',
				templateUrl: 'view/common/parent.html'
			})
			.state('task.list', {
				url: "/",
				templateUrl: 'view/task/list.html',
				controller: 'taskCtrl'
			})
			.state('task.view', {
				data: {
					permissions: {
						only: ['task.view']
					}
				},
				url: '/:id/view',
				templateUrl: 'view/task/view.html',
				controller: 'taskViewCtrl'
			})
			.state('task.view.conn', {
				url: '/conn',
				templateUrl: 'view/common/grid/grid.html',
				controller: 'broConnCtrl'
			})
			.state('task.view.http', {
				url: '/http',
				templateUrl: 'view/common/grid/grid.html',
				controller: 'broHttpCtrl'
			})
			.state('task.view.files', {
				url: '/files',
				templateUrl: 'view/common/grid/grid.html',
				controller: 'broFilesCtrl'
			})
			.state('task.view.smtp', {
				url: '/smtp',
				templateUrl: 'view/common/grid/grid.html',
				controller: 'broSmtpCtrl'
			});
}]).
factory('taskService', ['$resource', function ($resource) {
	return $resource(apiBaseUrl + '/task', {}, serviceCommonConfig);
}]).
controller('taskCtrl', ['$scope', 'taskService', 'uiGridConstants', 'gridHelper', '$state', 'principal', function ($scope, service, uiGridConstants, gridHelper, $state, principal) {
	$scope.principal = principal;

	$scope.actions = [
		{
			perRow: true,
			permissions: ['task.view'],
			icon: 'eye-open',
			action: function (grid, row) {
				$state.go('task.view.conn', {id: row.entity.id});
			}
		}
	];

	$scope.filterModel = {};

	var paginationOptions = {
		pageNumber: 1,
		pageSize: 25,
		sort: ["id,ASC"]
	};

	gridHelper($scope, service, paginationOptions, {
		columnDefs: [
			{
				field: '_actions',
				name: '',
				enableFiltering: false,
				enableSorting: false,
				enableColumnMenu: false,
				cellTemplate: 'view/common/grid/row-actions.html',
				width: 34, maxWidth: 34, minWidth: 34
			},
			{
				field: 'id',
				sort: {direction: uiGridConstants.ASC, priority: 0}
			},
			{field: 'agency'},
			{field: 'clientAlias'},
			{
				field: 'periodStart',
				filterHeaderTemplate: 'view/common/grid/filter/dateTime.html',
				filters: [{placeholder: 'from'}, {placeholder: 'to'}],
				filterTermMapper: function(value) {
					return moment(value).format('YYYY-MM-DD[T]HH:mm:ss.SSSZ');
				}
			},
			{
				field: 'periodFinish',
				filterHeaderTemplate: 'view/common/grid/filter/dateTime.html',
				filters: [{placeholder: 'from'}, {placeholder: 'to'}],
				filterTermMapper: function(value) {
					return moment(value).format('YYYY-MM-DD[T]HH:mm:ss.SSSZ');
				}
			},
			{field: 'note'},
		],
	});

	$scope.loadPage();
}]).controller('taskViewCtrl', ['$scope', '$state', function ($scope, $state) {
	$scope.tabs = [
		{active: false, route: 'task.view.conn', name: 'Conn'},
		{active: false, route: 'task.view.http', name: 'Http'},
		{active: false, route: 'task.view.files', name: 'Files'},
		{active: false, route: 'task.view.smtp', name: 'SMTP'},
	];

	$scope.go = function (route) {
		$state.go(route);
	};

	$scope.isActive = function (route) {
		return $state.is(route);
	};

	$scope.$on("$stateChangeSuccess", function () {
		$scope.tabs.forEach(function (tab) {
			tab.active = $scope.isActive(tab.route);
		});
	});
}])
;
angular.module('app').
config(['$stateProvider', function ($stateProvider) {
	$stateProvider
			.state('task', {
				abstract: true,
				url: '/task',
				templateUrl: 'view/common/parent.html'
			})
			.state('task.list', {
				url: "/",
				templateUrl: 'view/task/list.html',
				controller: 'taskCtrl'
			});
}]).
factory('taskService', ['$resource', function ($resource) {
	return $resource(apiBaseUrl + '/task', {}, serviceCommonConfig);
}]).
controller('taskCtrl', ['$scope', 'taskService', 'uiGridConstants', 'gridHelper', 'dateTimePickerFilterTemplate', function ($scope, service, uiGridConstants, gridHelper, filterTemplate) {
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
				sort: {direction: uiGridConstants.ASC, priority: 0}
			},
			{field: 'agency'},
			{field: 'clientAlias'},
			{
				field: 'periodStart',
				filterHeaderTemplate: filterTemplate(),
				filters: [{placeholder: 'from'}, {placeholder: 'to'}],
				filterTermMapper: function(value) {
					return moment(value).format('YYYY-MM-DD[T]HH:mm:ss.SSSZ');
				}
			},
			{
				field: 'periodFinish',
				filterHeaderTemplate: filterTemplate(),
				filters: [{placeholder: 'from'}, {placeholder: 'to'}],
				filterTermMapper: function(value) {
					return moment(value).format('YYYY-MM-DD[T]HH:mm:ss.SSSZ');
				}
			},
			{field: 'note'},
		],
	});

	$scope.loadPage();
}])
;
angular.module('app').
config(['$stateProvider', function ($stateProvider) {
	$stateProvider
			.state('serverStatus', {
				abstract: true,
				data: {
					permissions: {
						only: ['ADMIN']
					}
				},
				url: '/serverStatus',
				templateUrl: 'view/common/parent.html'
			})
			.state('serverStatus.list', {
				url: "/",
				templateUrl: 'view/common/grid/grid.html',
				controller: 'serverStatusCtrl'
			})
	;
}]).
factory('serverStatusService', ['$resource', function ($resource) {
	const url = apiBaseUrl + '/server/status';
	return $resource(url, {}, jQuery.extend({}, serviceCommonConfig, {
		refresh: {
			url: url + '/refresh',
			method: 'GET',
			isArray: false
		}
	}));
}]).
controller('serverStatusCtrl', ['$scope', 'serverStatusService', 'uiGridConstants', 'gridHelper', '$state', 'dialogs', 'toastr', 'principal', function ($scope, service, uiGridConstants, gridHelper, $state, dialogs, toastr, principal) {
	$scope.principal = principal;

	$scope.actions = [
		{
			permissions: ['serverStatus.refresh'],
			icon: 'flash',
			action: function (grid, row) {
				service.refresh(
						function (data) {
							$scope.loadPage();
						},
						function (error) {
							toastr.error(error.statusText, 'Error');
						});
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
				field: '_actions'
			},
			{
				field: 'id',
				visible: false,
				sort: {direction: uiGridConstants.DESC, priority: 0}
			},
			{
				field: 'serverName',
				name: 'Имя'
			},
			{
				field: 'modifiedAt',
				name: 'Данные на момент',
				filterHeaderTemplate: 'view/common/grid/filter/dateTime.html',
				filters: [{placeholder: 'from', picker: {onlyDate: true}}, {placeholder: 'to', picker: {onlyDate: true}}],
				filterTermMapper: function (value) {
					return moment(value).format('YYYY-MM-DD');
				}
			},
			{
				field: 'hostStatus',
				name: 'Сервер доступен',
				cellFilter: 'booleanToString',
				filter: {
					type: uiGridConstants.filter.SELECT,
					selectOptions: [
						{value: true, label: 'ДА'},
						{value: false, label: 'нет'}
					]
				}
			},
			{
				field: 'dbStatus',
				name: 'БД доступна',
				cellFilter: 'booleanToString',
				filter: {
					type: uiGridConstants.filter.SELECT,
					selectOptions: [
						{value: true, label: 'ДА'},
						{value: false, label: 'нет'}
					]
				}
			},
		],
	});

	$scope.loadPage();
}])
;
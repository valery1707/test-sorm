const Task_Mode = {
	FULL: 'Полный контроль',
	STATISTIC: 'Статистический контроль',
};
angular.module('app').
config(['$stateProvider', function ($stateProvider) {
	$stateProvider
			.state('task', {
				abstract: true,
				data: {
					permissions: {
						only: ['ADMIN', 'OPERATOR', 'SUPERVISOR']
					}
				},
				url: '/task',
				templateUrl: 'view/common/parent.html'
			})
			.state('task.list', {
				url: "/",
				templateUrl: 'view/common/grid/grid.html',
				controller: 'taskCtrl'
			})
			.state('task.item', {
				abstract: true,
				url: "/{id:[0-9]+}",
				template: '<ui-view></ui-view>'
			})
			.state('task.item.update', {
				url: "/edit",
				templateUrl: 'view/task/item.edit.html',
				controller: 'taskCtrlEdit'
			})
			.state('task.create', {
				url: "/new",
				templateUrl: 'view/task/item.edit.html',
				controller: 'taskCtrlEdit'
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
			})
			.state('task.view.binary', {
				url: '/binary',
				templateUrl: 'view/common/grid/grid.html',
				controller: 'broBinaryCtrl'
			});
}]).
factory('taskService', ['$resource', function ($resource) {
	const url = apiBaseUrl + '/task';
	return $resource(url, {}, jQuery.extend({}, serviceCommonConfig, {
		comboAgency: {
			url: url + '/comboAgency',
			method: 'GET',
			isArray: true
		}
	}));
}]).
controller('taskCtrl', ['$scope', 'taskService', 'uiGridConstants', 'gridHelper', '$state', 'dialogs', 'toastr', 'principal', function ($scope, service, uiGridConstants, gridHelper, $state, dialogs, toastr, principal) {
	$scope.principal = principal;

	$scope.actions = [
		{
			permissions: ['task.create'],
			icon: 'plus',
			action: function (grid, row) {
				$state.go('task.create');
			}
		},
		{
			type: 'refresh',
			permissions: ['task.list']
		},
		{
			perRow: true,
			permissions: ['task.delete'],
			icon: 'minus',
			action: function (grid, row) {
				dialogs.confirm('Удалить задание?', 'Подтверждение').result.then(
						function (yes) {
							service.delete({id: row.entity.id},
									function (success) {
										toastr.info('Задание удалено', 'Успех');
										$scope.loadPage();
									},
									function (error) {
										const msg = error.statusText ? error.statusText : error;
										toastr.error(msg, 'Ошибка сервера');
									});
						},
						function (no) {
						}
				);
			}
		},
		{
			perRow: true,
			permissions: ['task.update'],
			icon: 'edit',
			action: function (grid, row) {
				$state.go('task.item.update', {id: row.entity.id});
			}
		},
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
		sort: ["createdAt,ASC"]
	};

	gridHelper($scope, service, paginationOptions, {
		columnDefs: [
			{
				field: '_actions'
			},
			{
				field: 'number',
				name: 'Номер задания'
			},
			{
				field: 'createdAt',
				name: 'Дата и время постановки задания',
				sort: {direction: uiGridConstants.ASC, priority: 0}
			},
			{
				field: 'mode',
				name: 'Признак собираемых сведений',
				cellFilter: 'Task_Mode',
				filter: {
					type: uiGridConstants.filter.SELECT,
					selectOptions: enumToFilterValues(Task_Mode)
				}
			},
			{
				field: 'agencyName',
				name: 'Орган, осуществляющий проведение ОРМ или надзор'
			},
			{
				field: 'clientAlias',
				name: 'Псевдоним абонента'
			},
			{
				field: 'periodStart',
				name: 'Период перехвата. Начало',
				filterHeaderTemplate: 'view/common/grid/filter/dateTime.html',
				filters: [{placeholder: 'с'}, {placeholder: 'по'}],
				filterTermMapper: function (value) {
					return moment(value).format('YYYY-MM-DD[T]HH:mm:ss.SSSZ');
				}
			},
			{
				field: 'periodFinish',
				name: 'Период перехвата. Конец',
				filterHeaderTemplate: 'view/common/grid/filter/dateTime.html',
				filters: [{placeholder: 'с'}, {placeholder: 'по'}],
				filterTermMapper: function (value) {
					return moment(value).format('YYYY-MM-DD[T]HH:mm:ss.SSSZ');
				}
			},
			{
				field: 'active',
				name: 'Активен',
				cellFilter: 'booleanToString',
				permissions: ['task.list.active']
			},
			{
				field: 'note',
				name: 'Примечание'
			},
		],
	});

	$scope.loadPage();
}]).
controller('taskCtrlEdit', ['$scope', '$state', '$stateParams', '$translate', 'taskService', 'toastr', 'formBuilder', function ($scope, $state, $stateParams, $translate, service, toastr, formBuilder) {
	$scope.selectTagSimple = function (tag) {
		return tag.replace(',', '').trim();
	};
	$scope.optSelect = function (item, field) {
		$scope.model[field] = item.id;
	};
	service.comboAgency(
			function (data) {
				$scope.optAgency = data;
			},
			function (error) {
				toastr.error(error.statusText, 'Error');
			});
	formBuilder($scope, $state, $stateParams, $translate, service, toastr, 'task', {
		filter: {
			email: [],
			ip: []
		}
	});
}]).
controller('taskViewCtrl', ['$scope', '$state', function ($scope, $state) {
	$scope.tabs = [
		{active: false, route: 'task.view.conn', name: 'Conn', visible: true},
		{active: false, route: 'task.view.http', name: 'Http', visible: true},
		{active: false, route: 'task.view.files', name: 'Files', visible: false},
		{active: false, route: 'task.view.smtp', name: 'SMTP', visible: true},
		{active: false, route: 'task.view.binary', name: 'Binary', visible: true},
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
}]).
filter('Task_Mode', function () {
	return function (val) {
		return enumToTranslate(Task_Mode, val);
	}
})
;
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
controller('accountSessionCtrl', ['$scope', 'accountSessionService', 'uiGridConstants', 'gridHelper', '$state', 'principal', function ($scope, service, uiGridConstants, gridHelper, $state, principal) {
	$scope.actions = [
		{
			type: 'refresh',
			permissions: ['accountSession.list']
		}
	];

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
			{
				field: 'accountUsername',
				name: 'Имя пользователя'
			},
			{
				field: 'loginAt',
				name: 'Время входа',
				sort: {direction: uiGridConstants.DESC, priority: 0},
				filterHeaderTemplate: 'view/common/grid/filter/dateTime.html',
				filters: [{placeholder: 'с'}, {placeholder: 'по'}],
				filterTermMapper: function (value) {
					return moment(value).format('YYYY-MM-DD[T]HH:mm:ss.SSSZ');
				}
			},
			{
				field: 'loginAs',
				name: 'Тип входа',
				cellFilter: 'AccountSession_Login',
				filter: {
					type: uiGridConstants.filter.SELECT,
					selectOptions: [
						{value: 'MANUAL', label: 'Ручной'},
						//{value: 'INTERACTIVE', label: 'Интерактивный'},
						{value: 'REMEMBER_ME', label: 'Запомнить меня'}
					]
				}
			},
			{field: 'sessionId', visible: false},
			{field: 'details', visible: false},
			{
				field: 'logoutAt',
				name: 'Время выхода',
				filterHeaderTemplate: 'view/common/grid/filter/dateTime.html',
				filters: [{placeholder: 'с'}, {placeholder: 'по'}],
				filterTermMapper: function (value) {
					return moment(value).format('YYYY-MM-DD[T]HH:mm:ss.SSSZ');
				}
			},
			{
				field: 'logoutAs',
				name: 'Тип выхода',
				cellFilter: 'AccountSession_Logout',
				filter: {
					type: uiGridConstants.filter.SELECT,
					selectOptions: [
						{value: 'MANUAL', label: 'Ручной'},
						{value: 'TIMEOUT', label: 'Таймаут'},
						{value: 'SERVER_RESTART', label: 'Перезапуск сервера'}
					]
				}
			},
			{
				field: 'duration',
				name: 'Продолжительность',
				cellFilter: 'secondToPeriod'
			},
		],
	});

	$scope.loadPage();
}]).
filter('AccountSession_Login', function () {
	return function (val) {
		switch (val) {
			case 'MANUAL': return 'Ручной';
			case 'INTERACTIVE': return 'Интерактивный';
			case 'REMEMBER_ME': return 'Запомнить меня';
			default: return val;
		}
	}
}).
filter('AccountSession_Logout', function () {
	return function (val) {
		switch (val) {
			case 'MANUAL': return 'Ручной';
			case 'TIMEOUT': return 'Таймаут';
			case 'SERVER_RESTART': return 'Перезапуск сервера';
			default: return val;
		}
	}
})
;
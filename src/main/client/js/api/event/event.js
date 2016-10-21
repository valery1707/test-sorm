const Event_EventType = {
	ADMIN_ACCOUNT_CREATE: '1.1: Добавление учётной записи',
	ADMIN_ACCOUNT_UPDATE: '1.2: Модификация параметров учётной записи',
	ADMIN_ACCOUNT_DELETE: '1.3: Удаление учётной записи',
	ADMIN_ACCOUNT_LIST: '1.4: Просмотр списка учётных записей',
	ADMIN_TASK_CREATE: '1.5: Задание на перехват сообщений абонента',
	ADMIN_TASK_DELETE: '1.7: Удаление заданий на перехват сообщений абонента',
	ADMIN_TASK_LIST: '1.6: Просмотр списка заданий на перехват сообщений абонента',
	ADMIN_TASK_PERMIT_CREATE: '1.8: Выдача санкий на обработку перехваченных сообщений абонента',
	ADMIN_TASK_PERMIT_DELETE: '1.10: Удаление санкции на обработку перехваченных сообщений абонента',
	ADMIN_TASK_PERMIT_LIST: '1.9: Просмотр списка выданных санкций на обработку перехваченных сообщений абонента',
	SERVER_STATUS: '1.14: Проверка технического состояния оборудования',//todo
	ADMIN_CHANGE_PASSWORD: '1.15: Изменение пароля администратора',

	OPERATOR_TASK_LIST: '2.1: Просмотр списка санкционированных заданий на перехват сообщений абонента',
	OPERATOR_TASK_VIEW: '2.3: Получение результатов перехвата сообщений абонента',
	OPERATOR_CHANGE_PASSWORD: '2.6: Изменение пароля обработчика',

	SUPERVISOR_ACCOUNT_LIST: '3.1: Просмотр сисков учётных записей',
	SUPERVISOR_TASK_LIST: '3.2: Просмотр списка заданий на перехват сообщений абонента',
	SUPERVISOR_CHANGE_PASSWORD: '3.4: Изменение пароля надзорщика',
};
angular.module('app').
config(['$stateProvider', function ($stateProvider) {
	$stateProvider
			.state('event', {
				abstract: true,
				data: {
					permissions: {
						only: ['ADMIN']
					}
				},
				url: '/event',
				templateUrl: 'view/common/parent.html'
			})
			.state('event.list', {
				url: "/",
				templateUrl: 'view/common/grid/grid.html',
				controller: 'eventCtrl'
			});
}]).
factory('eventService', ['$resource', function ($resource) {
	return $resource(apiBaseUrl + '/event', {}, serviceCommonConfig);
}]).
controller('eventCtrl', ['$scope', 'eventService', 'uiGridConstants', 'gridHelper', '$state', 'principal', function ($scope, service, uiGridConstants, gridHelper, $state, principal) {
	$scope.actions = [
		{
			type: 'refresh',
			permissions: ['event.list']
		}
	];

	$scope.filterModel = {};

	var paginationOptions = {
		pageNumber: 1,
		pageSize: 25,
		sort: ["createdAt,DESC"]
	};

	gridHelper($scope, service, paginationOptions, {
		columnDefs: [
			{
				field: 'id',
			},
			{
				field: 'username',
				name: 'Имя пользователя'
			},
			{
				field: 'createdAt',
				name: 'Время собятия',
				sort: {direction: uiGridConstants.DESC, priority: 0},
				filterHeaderTemplate: 'view/common/grid/filter/dateTime.html',
				filters: [{placeholder: 'from'}, {placeholder: 'to'}],
				filterTermMapper: function (value) {
					return moment(value).format('YYYY-MM-DD[T]HH:mm:ss.SSSZ');
				}
			},
			{
				field: 'type',
				name: 'Событие',
				cellFilter: 'Event_Type',
				filter: {
					type: uiGridConstants.filter.SELECT,
					selectOptions: enumToFilterValues(Event_EventType)
				}
			},
			{
				field: 'success',
				name: 'Успешно',
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
				field: 'ip',
				name: 'IP'
			},
			{
				field: 'ext',
				name: 'Дополнительно'
			},
		],
	});

	$scope.loadPage();
}]).
filter('Event_Type', function () {
	return function (val) {
		return enumToTranslate(Event_EventType, val);
	}
})
;
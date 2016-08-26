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
			})
			.state('account.item', {
				abstract: true,
				url: "/{id:[0-9]+}",
				template: '<ui-view></ui-view>'
			})
			.state('account.item.update', {
				url: "/edit",
				templateUrl: 'view/account/item.edit.html',
				controller: 'accountCtrlEdit'
			})
			.state('account.create', {
				url: "/new",
				templateUrl: 'view/account/item.edit.html',
				controller: 'accountCtrlEdit'
			})
	;
}]).
factory('accountService', ['$resource', function ($resource) {
	const url = apiBaseUrl + '/account';
	return $resource(url, {}, jQuery.extend({}, serviceCommonConfig, {
		comboAgency: {
			url: url + '/comboAgency',
			method: 'GET',
			isArray: true
		}
	}));
}]).
controller('accountCtrl', ['$scope', 'accountService', 'uiGridConstants', 'gridHelper', '$state', 'dialogs', 'toastr', 'principal', function ($scope, service, uiGridConstants, gridHelper, $state, dialogs, toastr, principal) {
	$scope.principal = principal;

	$scope.actions = [
		{
			permissions: ['account.create'],
			icon: 'plus',
			action: function (grid, row) {
				$state.go('account.create');
			}
		},
		{
			perRow: true,
			permissions: ['account.delete'],
			icon: 'minus',
			action: function (grid, row) {
				dialogs.confirm('Удалить аккаунт?', 'Подтверждение').result.then(
						function (yes) {
							service.delete({id: row.entity.id},
									function (success) {
										toastr.info('Аккаунт удалён', 'Успех');
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
			permissions: ['account.update'],
			icon: 'edit',
			action: function (grid, row) {
				$state.go('account.item.update', {id: row.entity.id});
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
				sort: {direction: uiGridConstants.DESC, priority: 0}
			},
			{
				field: 'username',
				name: 'Имя пользователя'
			},
			{
				field: 'active',
				name: 'Активен',
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
				field: 'role',
				name: 'Роль',
				cellFilter: 'Account_Role',
				filter: {
					type: uiGridConstants.filter.SELECT,
					selectOptions: [
						{value: 'SUPER', label: 'Сисадмин'},
						{value: 'ADMIN', label: 'Администратор'},
						{value: 'OPERATOR', label: 'Обработчик'},
						{value: 'SUPERVISOR', label: 'Надзорщик'}
					]
				}
			},
			{
				field: 'activeUntil',
				name: 'Дата деактивации',
				filterHeaderTemplate: 'view/common/grid/filter/dateTime.html',
				filters: [{placeholder: 'from', picker: {onlyDate: true}}, {placeholder: 'to', picker: {onlyDate: true}}],
				filterTermMapper: function (value) {
					return moment(value).format('YYYY-MM-DD');
				}
			},
			{
				field: 'agencyName',
				name: 'Орган, осуществляющий проведение ОРМ или надзор'
			},
		],
	});

	$scope.loadPage();
}]).
controller('accountCtrlEdit', ['$scope', '$state', '$stateParams', 'accountService', 'toastr', 'formBuilder', function ($scope, $state, $stateParams, service, toastr, formBuilder) {
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
	formBuilder($scope, $state, $stateParams, service, toastr, 'account');
}]).
filter('Account_Role', function () {
	return function (val) {
		switch (val) {
			case 'SUPER': return 'Сисадмин';
			case 'ADMIN': return 'Администратор';
			case 'OPERATOR': return 'Обработчик';
			case 'SUPERVISOR': return 'Надзорщик';
			default: return val;
		}
	}
})
;
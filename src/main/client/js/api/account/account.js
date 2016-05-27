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
	return $resource(apiBaseUrl + '/account', {}, serviceCommonConfig);
}]).
controller('accountCtrl', ['$scope', 'accountService', 'uiGridConstants', 'gridHelper', '$state', 'principal', function ($scope, service, uiGridConstants, gridHelper, $state, principal) {
	$scope.principal = principal;
	$scope.filterModel = {};

	var paginationOptions = {
		pageNumber: 1,
		pageSize: 25,
		sort: ["id,ASC"]
	};

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
			permissions: ['account.update'],
			icon: 'edit',
			action: function (grid, row) {
				$state.go('account.item.update', {id: row.entity.id});
			}
		}
	];

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
				filters: [{placeholder: 'from', picker: {onlyDate: true}}, {placeholder: 'to', picker: {onlyDate: true}}],
				filterTermMapper: function (value) {
					return moment(value).format('YYYY-MM-DD');
				}
			},
			{field: 'agency'},
		],
	});

	$scope.loadPage();
}]).
controller('accountCtrlEdit', ['$scope', '$state', '$stateParams', 'accountService', 'toastr', function ($scope, $state, $stateParams, service, toastr) {
	$scope.model = {};
	$scope.serverErrors = {};
	$scope.serverErrorValidator = function (field) {
		const fieldDef = $scope.entityForm[field];
		const fieldValue = fieldDef.$viewValue;
		var errors = [];
		const errorsDef = $scope.serverErrors[field];
		if (errorsDef) {//Массив с ошибками
			for (var errorI in errorsDef) {//Отдельная ошибка
				if (errorsDef.hasOwnProperty(errorI)) {
					const errorDef = errorsDef[errorI];
					if (errorDef.rejectedValue == fieldValue) {
						errors.push(errorDef.defaultMessage);
					}
				}
			}
		}
		return {isValid: errors.length == 0, message: errors.join('\r\n<br/>')};
	};
	if ($stateParams.id) {
		service.get($stateParams,
				function (data) {
					$scope.model = data;
				},
				function () {
					$scope.cancel();
				});
	}
	$scope.cancel = function () {
		$state.go('account.list');
	};
	$scope.save = function () {
		service.save($scope.model,
				function (data) {
					$state.go('account.list');
				},
				function (error) {
					if (error && error.status === 400 && error.data) {
						const errorsDef = error.data;
						$scope.serverErrors = errorsDef;
						for (var field in errorsDef) {
							if (errorsDef.hasOwnProperty(field)) {
								$scope.$broadcast('angularValidation.revalidate', field);
							}
						}
					} else {
						toastr.error('Unknown server error', 'Error');
					}
				});
	};
}])
;
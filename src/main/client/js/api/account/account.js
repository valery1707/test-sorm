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
			.state('account.item.edit', {
				url: "/edit",
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
			permissions: ['account.item.edit'],
			icon: 'edit',
			action: function (grid, row) {
				$state.go('account.item.edit', {id: row.entity.id});
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
				cellTemplate: 'view/common/grid/actions.html',
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
controller('accountCtrlEdit', ['$scope', '$state', '$stateParams', 'accountService', function ($scope, $state, $stateParams, service) {
	$scope.model = {};
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
					//todo Parse error
					console.log('error', error);
				});
	};
}])
;
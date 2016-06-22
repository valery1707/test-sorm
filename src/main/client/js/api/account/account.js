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
				dialogs.confirm('Remove account?', 'Remove account?').result.then(
						function (yes) {
							service.delete({id: row.entity.id},
									function (success) {
										toastr.info('Account deleted', 'Success');
										$scope.loadPage();
									},
									function (error) {
										const msg = error.statusText ? error.statusText : error;
										toastr.error(msg, 'Server error');
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
controller('accountCtrlEdit', ['$scope', '$state', '$stateParams', 'accountService', 'toastr', 'formBuilder', function ($scope, $state, $stateParams, service, toastr, formBuilder) {
	formBuilder($scope, $state, $stateParams, service, toastr, 'account');
}])
;
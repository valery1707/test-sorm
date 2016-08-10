angular.module('app').
config(['$stateProvider', function ($stateProvider) {
	$stateProvider
			.state('taskPermit', {
				abstract: true,
				data: {
					permissions: {
						only: ['ADMIN']
					}
				},
				url: '/taskPermit',
				templateUrl: 'view/common/parent.html'
			})
			.state('taskPermit.list', {
				url: "/",
				templateUrl: 'view/common/grid/grid.html',
				controller: 'taskPermitCtrl'
			})
			.state('taskPermit.create', {
				url: "/new",
				templateUrl: 'view/task/permit/edit.html',
				controller: 'taskPermitCtrlEdit'
			});
}]).
factory('taskPermitService', ['$resource', function ($resource) {
	const url = apiBaseUrl + '/task/permit';
	return $resource(url, {}, jQuery.extend({}, serviceCommonConfig, {
		comboTask: {
			url: url + '/comboTask',
			method: 'GET',
			isArray: true
		},
		comboAccount: {
			url: url + '/comboAccount',
			method: 'GET',
			isArray: true
		},
		comboAgency: {
			url: url + '/comboAgency',
			method: 'GET',
			isArray: true
		}
	}));
}]).
controller('taskPermitCtrl', ['$scope', 'taskPermitService', 'uiGridConstants', 'gridHelper', '$state', 'dialogs', 'toastr', 'principal', function ($scope, service, uiGridConstants, gridHelper, $state, dialogs, toastr, principal) {
	$scope.principal = principal;

	$scope.actions = [
		{
			permissions: ['taskPermit.create'],
			icon: 'plus',
			action: function (grid, row) {
				$state.go('taskPermit.create');
			}
		},
		{
			perRow: true,
			permissions: ['taskPermit.delete'],
			icon: 'minus',
			action: function (grid, row) {
				dialogs.confirm('Remove task permit?', 'Confirm').result.then(
						function (yes) {
							service.delete({id: row.entity.id},
									function (success) {
										toastr.info('Task permit deleted', 'Success');
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
				sort: {direction: uiGridConstants.ASC, priority: 0}
			},
			{field: 'agencyName'},
			{field: 'taskId'},
			{field: 'accountName'},
			{
				field: 'periodStart',
				filterHeaderTemplate: 'view/common/grid/filter/dateTime.html',
				filters: [{placeholder: 'from'}, {placeholder: 'to'}],
				filterTermMapper: function (value) {
					return moment(value).format('YYYY-MM-DD[T]HH:mm:ss.SSSZ');
				}
			},
			{
				field: 'periodFinish',
				filterHeaderTemplate: 'view/common/grid/filter/dateTime.html',
				filters: [{placeholder: 'from'}, {placeholder: 'to'}],
				filterTermMapper: function (value) {
					return moment(value).format('YYYY-MM-DD[T]HH:mm:ss.SSSZ');
				}
			},
		],
	});

	$scope.loadPage();
}]).
controller('taskPermitCtrlEdit', ['$scope', '$state', '$stateParams', 'taskPermitService', 'toastr', 'formBuilder', function ($scope, $state, $stateParams, service, toastr, formBuilder) {
	$scope.optSelect = function (item, field) {
		$scope.model[field] = item.id;
	};
	service.comboTask(
			function (data) {
				$scope.optTask = data;
			},
			function (error) {
				toastr.error(error.statusText, 'Error');
			});
	$scope.optTaskGroup = function (item) {
		return 'Agency: ' + item.agency;
	};
	service.comboAccount(
			function (data) {
				$scope.optAccount = data;
			},
			function (error) {
				toastr.error(error.statusText, 'Error');
			});
	service.comboAgency(
			function (data) {
				$scope.optAgency = data;
			},
			function (error) {
				toastr.error(error.statusText, 'Error');
			});
	formBuilder($scope, $state, $stateParams, service, toastr, 'taskPermit');
}])
;
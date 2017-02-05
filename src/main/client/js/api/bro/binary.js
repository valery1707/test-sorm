angular.module('app').
config(['$stateProvider', function ($stateProvider) {
	$stateProvider
			.state('bro.binary', {
				url: "/binary",
				templateUrl: 'view/common/grid/grid.html',
				controller: 'broBinaryCtrl'
			});
}]).
factory('broBinaryService', ['$resource', function ($resource) {
	const url = apiBaseUrl + '/bro/binary';
	return $resource(url, {}, jQuery.extend({}, serviceCommonConfig, {
		files: {
			url: url + '/files',
			method: 'GET',
			isArray: true,
			cache: false
		}
	}));
}]).
controller('broBinaryCtrl', ['$scope', 'broBinaryService', 'uiGridConstants', 'gridHelper', '$stateParams', 'dialogs', 'toastr', 'principal', function ($scope, service, uiGridConstants, gridHelper, $stateParams, dialogs, toastr, principal) {
	$scope.principal = principal;

	$scope.actions = [
		{
			perRow: true,
			permissions: ['task.view'],
			icon: 'eye-open',
			action: function (grid, row) {
				service.get(row.entity,
						function (success) {
							dialogs.create('view/bro/binary.detail.html', 'broCommonDetailCtrl', {entity: success, service: service}, {keyboard: true});
						},
						function (error) {
							const msg = error.statusText ? error.statusText : error;
							toastr.error(msg, 'Server error');
						});
			}
		}
	];

	$scope.filterModel = {
		taskId: $stateParams.id
	};

	var paginationOptions = {
		pageNumber: 1,
		pageSize: 25,
		sort: ["ts,DESC"]
	};

	gridHelper($scope, service, paginationOptions, {
		columnDefs: [
			{
				field: '_actions'
			},
			{
				field: 'ts',
				sort: {direction: uiGridConstants.DESC, priority: 0},
				filterHeaderTemplate: 'view/common/grid/filter/dateTime.html',
				filters: [{placeholder: 'с'}, {placeholder: 'по'}],
				filterTermMapper: function(value) {
					return moment(value).format('YYYY-MM-DD[T]HH:mm:ss.SSSZ');
				}
			},
			{field: 'idOrigHost', filter: {placeholder: 'IP/CIDR'}},
			{field: 'idOrigPort', filter: {placeholder: '<, <=, =, >, >=, ...'}},
			{field: 'idRespHost', filter: {placeholder: 'IP/CIDR'}},
			{field: 'idRespPort', filter: {placeholder: '<, <=, =, >, >=, ... '}},
			{field: 'protocol'},
		],
	});

	$scope.loadPage();
}])
;

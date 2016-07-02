angular.module('app').
config(['$stateProvider', function ($stateProvider) {
	$stateProvider
			.state('bro.http', {
				url: "/http",
				templateUrl: 'view/common/grid/grid.html',
				controller: 'broHttpCtrl'
			});
}]).
factory('broHttpService', ['$resource', function ($resource) {
	const url = apiBaseUrl + '/bro/http';
	return $resource(url, {}, jQuery.extend({}, serviceCommonConfig, {
		files: {
			url: url + '/files',
			method: 'GET',
			isArray: true,
			cache: false
		}
	}));
}]).
controller('broHttpCtrl', ['$scope', 'broHttpService', 'uiGridConstants', 'gridHelper', '$stateParams', 'dialogs', 'toastr', 'principal', function ($scope, service, uiGridConstants, gridHelper, $stateParams, dialogs, toastr, principal) {
	$scope.principal = principal;

	$scope.actions = [
		{
			perRow: true,
			permissions: ['task.view'],
			icon: 'eye-open',
			action: function (grid, row) {
				service.get(row.entity,
						function (success) {
							dialogs.create('view/bro/http.detail.html', 'broCommonDetailCtrl', {entity: success, service: service}, {keyboard: true});
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
				filters: [{placeholder: 'from'}, {placeholder: 'to'}],
				filterTermMapper: function (value) {
					return moment(value).format('YYYY-MM-DD[T]HH:mm:ss.SSSZ');
				}
			},
			{field: 'idOrigHost', filter: {placeholder: 'IP/CIDR'}},
			{field: 'idOrigPort', filter: {placeholder: '<, <=, =, >, >=, ...'}},
			{field: 'idRespHost', filter: {placeholder: 'IP/CIDR'}},
			{field: 'idRespPort', filter: {placeholder: '<, <=, =, >, >=, ... '}},
			{field: 'method'},
			{field: 'host'},
			{field: 'uri'},
			{field: 'referrer'},
			{field: 'userAgent'},
			{field: 'requestBodyLen', cellFilter: 'formatBytes:1024:2'},
			{field: 'responseBodyLen', cellFilter: 'formatBytes:1024:2'},
			{field: 'statusCode'},
			{
				field: 'hasFiles',
				enableSorting: false,
				filter: {
					type: uiGridConstants.filter.SELECT,
					selectOptions: [
						{value: true, label: 'True'},
						{value: false, label: 'False'}
					]
				}
			},
		],
	});

	$scope.loadPage();
}])
;

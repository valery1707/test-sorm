angular.module('app').
config(['$stateProvider', function ($stateProvider) {
	$stateProvider
			.state('bro.http', {
				url: "/http",
				templateUrl: 'view/bro/http.html',
				controller: 'broHttpCtrl'
			});
}]).
factory('broHttpService', ['$resource', function ($resource) {
	return $resource(apiBaseUrl + '/bro/http', {}, serviceCommonConfig);
}]).
controller('broHttpCtrl', ['$scope', 'broHttpService', 'uiGridConstants', 'gridHelper', 'dateTimePickerFilterTemplate', '$stateParams', function ($scope, service, uiGridConstants, gridHelper, filterTemplate, $stateParams) {
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
				field: 'ts',
				sort: {direction: uiGridConstants.DESC, priority: 0},
				filterHeaderTemplate: filterTemplate(),
				filters: [{placeholder: 'from'}, {placeholder: 'to'}],
				filterTermMapper: function(value) {
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
			{field: 'hasFiles', enableFiltering: false},
		],
	});

	$scope.loadPage();
}])
;

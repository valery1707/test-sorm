angular.module('app').
config(['$routeProvider', function ($routeProvider) {
	$routeProvider.when('/http', {
		templateUrl: 'view/http.html',
		controller: 'httpCtrl'
	});
}]).
factory('httpService', ['$resource', function ($resource) {
	return $resource(apiBaseUrl + '/http', {}, serviceCommonConfig);
}]).
controller('httpCtrl', ['$scope', 'httpService', 'uiGridConstants', 'gridHelper', 'dateTimePickerFilterTemplate', function ($scope, service, uiGridConstants, gridHelper, filterTemplate) {
	$scope.filterModel = {};

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
			{field: 'requestBodyLen'},
			{field: 'responseBodyLen'},
			{field: 'statusCode'},
			{field: 'hasFiles'},
		],
	});

	$scope.loadPage();
}])
;

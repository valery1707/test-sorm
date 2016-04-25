angular.module('app').
config(['$routeProvider', function ($routeProvider) {
	$routeProvider.when('/files', {
		templateUrl: 'view/files.html',
		controller: 'filesCtrl'
	});
}]).
factory('filesService', ['$resource', function ($resource) {
	return $resource(apiBaseUrl + '/files', {}, serviceCommonConfig);
}]).
controller('filesCtrl', ['$scope', 'filesService', 'uiGridConstants', 'gridHelper', 'dateTimePickerFilterTemplate', function ($scope, service, uiGridConstants, gridHelper, filterTemplate) {
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
				, enableFiltering: false
			},
			{field: 'mimeType'},
			{field: 'filename'},
			{field: 'size', cellFilter: 'formatBytes:1024:2'},
			{field: 'extracted'},
		],
	});

	$scope.loadPage();
}])
;

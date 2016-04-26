angular.module('app').
config(['$routeProvider', function ($routeProvider) {
	$routeProvider.when('/files', {
		templateUrl: 'view/files.html',
		controller: 'filesCtrl'
	});
}]).
factory('filesService', ['$resource', function ($resource) {
	const url = apiBaseUrl + '/files';
	return $resource(url, {}, jQuery.extend({}, serviceCommonConfig, {
		download: {
			url: url + '/download',
			method: 'POST',
			isArray: false,
			cache: false,
			responseType: 'blob',
			transformResponse: function (data, headers) {
				//console.log('Headers: ', headers);
				return {
					value: data,
					contentTypeFull: headers('Content-Type'),
					contentType: headers('Content-Type').split(';')[0],
					filename: headers('X-Filename')
				};
			}
		}
	}));
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
			{
				field: '_download',
				enableFiltering: false,
				enableSorting: false,
				cellTemplate: '<div class="ngCellText" ng-class="col.colIndex()"><button class="btn primary" ng-click="grid.appScope.download(row.entity[\'extracted\'])">Download</button></div>'},
		],
	});
	$scope.download = function(arg1) {
		//console.log('Download request:', arg1);
		service.download(arg1, function(data) {
			//console.log('Download response:', data);
			saveAs(data.value, data.filename, true);
		});
	};

	$scope.loadPage();
}])
;

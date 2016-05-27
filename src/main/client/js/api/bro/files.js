angular.module('app').
config(['$stateProvider', function ($stateProvider) {
	$stateProvider
			.state('bro.files', {
				url: "/files",
				templateUrl: 'view/common/grid/grid.html',
				controller: 'broFilesCtrl'
			});
}]).
factory('broFilesService', ['$resource', function ($resource) {
	const url = apiBaseUrl + '/bro/files';
	return $resource(url, {}, jQuery.extend({}, serviceCommonConfig, {
		download: {
			url: url + '/download',
			method: 'POST',
			isArray: false,
			cache: false,
			responseType: 'blob',
			transformResponse: function (data, headers) {
				//console.log('Response:', data);
				//console.log('Headers: ', headers);
				const contentType = headers('Content-Type');
				return {
					value: data,
					contentTypeFull: contentType,
					contentType: contentType ? contentType.split(';')[0] : null,
					filename: headers('X-Filename')
				};
			}
		}
	}));
}]).
controller('broFilesCtrl', ['$scope', 'broFilesService', 'uiGridConstants', 'gridHelper', '$stateParams', 'toastr', function ($scope, service, uiGridConstants, gridHelper, $stateParams, toastr) {
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
				filterHeaderTemplate: 'view/common/grid/filter/dateTime.html',
				filters: [{placeholder: 'from'}, {placeholder: 'to'}],
				filterTermMapper: function(value) {
					return moment(value).format('YYYY-MM-DD[T]HH:mm:ss.SSSZ');
				}
			},
			{field: 'mimeType'},
			{field: 'filename'},
			{field: 'seenBytes', cellFilter: 'formatBytes:1024:2'},
			{field: 'totalBytes', cellFilter: 'formatBytes:1024:2'},
			{field: 'missingBytes', cellFilter: 'formatBytes:1024:2'},
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
			if (data.value.size > 0) {
				saveAs(data.value, data.filename, true);
			} else {
				toastr.error('File not found on server', 'Error');
			}
		});
	};

	$scope.loadPage();
}])
;

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
					filename: decodeURIComponent(headers('X-Filename'))
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
				field: '_actions',
				name: '',
				enableFiltering: false,
				enableSorting: false,
				enableColumnMenu: false,
				width: 34, maxWidth: 34, minWidth: 34,
				cellTemplate: '<div class="ui-grid-cell-contents" ng-class="col.colIndex()">' +
							  '  <button class="btn btn-xs btn-default" ng-click="grid.appScope.download(row.entity[\'extracted\'])">' +
							  '    <span class="glyphicon glyphicon-download" aria-hidden="true"></span>' +
							  '  </button>' +
							  '</div>'
			},
			{
				field: 'ts',
				sort: {direction: uiGridConstants.DESC, priority: 0},
				filterHeaderTemplate: 'view/common/grid/filter/dateTime.html',
				filters: [{placeholder: 'from'}, {placeholder: 'to'}],
				filterTermMapper: function(value) {
					return moment(value).format('YYYY-MM-DD[T]HH:mm:ss.SSSZ');
				}
			},
			{field: 'source'},
			{field: 'mimeType'},
			{field: 'filename'},
			{field: 'seenBytes', cellFilter: 'formatBytes:1024:2'},
			{field: 'totalBytes', cellFilter: 'formatBytes:1024:2'},
			{field: 'missingBytes', cellFilter: 'formatBytes:1024:2'},
			{field: 'extracted'},
		],
	});
	$scope.download = function(arg1) {
		if (arg1) {
			service.download(arg1, function(data) {
				if (data.value.size > 0) {
					saveAs(data.value, data.filename, true);
				} else {
					toastr.error('File not found on server', 'Error');
				}
			});
		} else {
			toastr.error('File not found on server', 'Error');
		}
	};

	$scope.loadPage();
}]).
controller('broCommonDetailCtrl', ['$scope', 'broFilesService', 'toastr', function ($scope, broFilesService, toastr) {
	const service = $scope.$resolve.data.service;
	$scope.model = $scope.$resolve.data.entity;

	$scope.download = function (arg1) {
		if (arg1) {
			broFilesService.download(arg1, function (data) {
				if (data.value.size > 0) {
					saveAs(data.value, data.filename, true);
				} else {
					toastr.error('File not found on server', 'Error');
				}
			});
		} else {
			toastr.error('File not found on server', 'Error');
		}
	};

	const tooltip = function (row, col) {
		return row.entity[col.field];
	};

	$scope.gridOptions = {
		enableFiltering: true,
		columnDefs: [
			{
				field: '_download',
				name: '',
				enableFiltering: false,
				enableSorting: false,
				enableColumnMenu: false,
				width: 34, maxWidth: 34, minWidth: 34,
				cellTemplate: '<div class="ui-grid-cell-contents" ng-class="col.colIndex()">' +
							  '  <button class="btn btn-xs btn-default" ng-click="grid.appScope.download(row.entity[\'extracted\'])">' +
							  '    <span class="glyphicon glyphicon-download" aria-hidden="true"></span>' +
							  '  </button>' +
							  '</div>'
			},
			{field: 'filename', cellTooltip: tooltip},
			{field: 'mimeType', cellTooltip: tooltip},
			{field: 'totalBytes'},
			{field: 'seenBytes'},
			{field: 'overflowBytes'},
			{field: 'missingBytes'},
			{field: 'md5', cellTooltip: tooltip},
			{field: 'sha1', cellTooltip: tooltip}
		]
	};

	$scope.images = [];
	service.files($scope.model,
			function (data) {
				$scope.gridOptions.data = data;
				const images = data.filter(function (file) {
					return file.mimeType && file.mimeType.startsWith("image/") && file.extracted;
				});
				Array.prototype.push.apply($scope.images, images);
			},
			function (error) {
				const msg = error.statusText ? error.statusText : error;
				toastr.error(msg, 'Server error');
			});

	$scope.cancel = function () {
		$scope.$dismiss('Canceled');
	};
}])
;

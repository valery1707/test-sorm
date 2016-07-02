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
							dialogs.create('view/bro/http.detail.html', 'broHttpDetailCtrl', {entity: success}, {keyboard: true});
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
}]).
controller('broHttpDetailCtrl', ['$scope', 'broHttpService', 'broFilesService', 'toastr', function ($scope, service, broFilesService, toastr) {
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

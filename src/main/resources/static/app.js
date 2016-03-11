'use strict';

var apiBaseUrl = '';

// Declare app level module which depends on views, and components
angular.module('app', [
	'ngRoute',
	'ngResource'
	, 'smart-table'
	, 'ghiscoding.validation'
	, 'pascalprecht.translate'
]).

factory('dataService', ['$resource', function ($resource) {
	return $resource(apiBaseUrl + '/data', {}, {
		query: {
			method: 'GET',
			params: {},
			isArray: false,
			cache: false
		},
		filter: {
			method: 'POST',
			isArray: false,
			cache: false
		}
	});
}]).
controller('dataCtrl', ['$scope', 'dataService', function ($scope, service) {
	$scope.filterModel = {};
	$scope.displayed = [];
	$scope.callServer = function (tableState) {
		$scope.isLoading = true;

		//console.log(tableState);

		var pagination = tableState.pagination;
		var pageStartItemIndex = pagination.start || 0;     // This is NOT the page number, but the index of item in the list that you want to use to display the table.
		var pageSize = pagination.number || 10;  // Number of entries showed per page.
		var pageIndex = pageStartItemIndex / pageSize;

		service.filter({size: pageSize, page: pageIndex}, tableState.search.predicateObject, function (data) {
			//console.log(data);
			$scope.displayed = data.content;
			//set the number of pages so the pagination can update
			tableState.pagination.numberOfPages = data.totalPages;
			$scope.isLoading = false;
		});
	};
}])
;

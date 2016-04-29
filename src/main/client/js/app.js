'use strict';

// Declare app level module which depends on views, and components
angular.module('app', [
	'ngRoute',
	'ngResource'
	, 'ngTouch'
	, 'ui.grid', 'ui.grid.pagination', 'ui.grid.resizeColumns'
	, 'ghiscoding.validation'
	, 'pascalprecht.translate'
	, 'ngLoadingSpinner'
	, 'angularSpinner'
]).
config(['$routeProvider', function ($routeProvider) {
	//$routeProvider.otherwise({redirectTo: '/main'});//todo dashboard?
}])
;

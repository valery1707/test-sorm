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
	, 'ui.router'
]).
config(['$routeProvider', function ($routeProvider) {
	//$routeProvider.otherwise({redirectTo: '/main'});//todo dashboard?
}]).
config(['$stateProvider', function ($stateProvider) {
	$stateProvider
			.state('bro', {
				abstract: true,
				url: '/bro',
				templateUrl: 'view/common/parent.html'
			});
}]).
run(['$rootScope', '$state', '$stateParams', function ($rootScope, $state, $stateParams) {
	// It's very handy to add references to $state and $stateParams to the $rootScope
	// so that you can access them from any scope within your applications. For example,
	// <li ng-class="{ active: $state.includes('contacts.list') }"> will set the <li>
	// to active whenever 'contacts.list' or one of its decendents is active.
	$rootScope.$state = $state;
	$rootScope.$stateParams = $stateParams;
}])
;

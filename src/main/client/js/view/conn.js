angular.module('app').
config(['$routeProvider', function ($routeProvider) {
	$routeProvider.when('/conn', {
		templateUrl: 'view/conn.html',
		controller: 'connCtrl'
	});
}])
;

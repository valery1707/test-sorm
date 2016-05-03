angular.module('app').
config(['$stateProvider', function ($stateProvider) {
	$stateProvider
			.state('todo', {
				url: "/todo",
				templateUrl: 'view/todo.html',
			});
}]);

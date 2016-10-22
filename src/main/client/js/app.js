'use strict';

// Declare app level module which depends on views, and components
angular.module('app', [
	'ngRoute',
	'ngResource'
	, 'ngTouch'
	, 'ui.grid', 'ui.grid.pagination', 'ui.grid.resizeColumns'
	//https://github.com/angular-ui/ui-select
	, 'ui.select'
	, 'ngSanitize'
	//https://github.com/ghiscoding/angular-validation/wiki
	, 'ghiscoding.validation'
	//https://github.com/angular-translate/angular-translate
	, 'pascalprecht.translate'
	, 'ngLoadingSpinner'
	, 'angularSpinner'
	, 'ui.router'
	//https://github.com/Narzerus/angular-permission
	, 'permission', 'permission.ui'
	//https://github.com/Foxandxss/angular-toastr
	, 'ngAnimate', 'toastr'
	, 'ui.bootstrap'
	//https://github.com/m-e-conroy/angular-dialog-service
	, 'dialogs.main', 'dialogs.default-translations'
]).
config(['$routeProvider', function ($routeProvider) {
	//$routeProvider.otherwise({redirectTo: '/main'});//todo dashboard?
}]).
config(['$stateProvider', function ($stateProvider) {
	$stateProvider
			.state('bro', {//todo Move to './api/bro' or remove
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
}]).
config(['$translateProvider', function ($translateProvider) {
	//Dialogs
	$translateProvider.translations('ru-RU', {
		DIALOGS_ERROR: "Ошибка",
		DIALOGS_ERROR_MSG: "Возникла неизвестная ошибка.",
		DIALOGS_CLOSE: "Закрыть",
		DIALOGS_PLEASE_WAIT: "Подождите",
		DIALOGS_PLEASE_WAIT_ELIPS: "Подождите...",
		DIALOGS_PLEASE_WAIT_MSG: "Ожидаю выполнения операции.",
		DIALOGS_PERCENT_COMPLETE: "% Завершено",
		DIALOGS_NOTIFICATION: "Уведомление",
		DIALOGS_NOTIFICATION_MSG: "Неизвестное уведомление.",
		DIALOGS_CONFIRMATION: "Подтверждение",
		DIALOGS_CONFIRMATION_MSG: "Требуется подтверждение.",
		DIALOGS_OK: "OK",
		DIALOGS_YES: "Да",
		DIALOGS_NO: "Нет"
	});
	$translateProvider.useStaticFilesLoader({
		files: [
			{
				prefix: 'locale/validation/',
				suffix: '.json'
			}
		]
	});

	//$translateProvider.useSanitizeValueStrategy('sanitize');

	// define translation maps you want to use on startup
	$translateProvider.preferredLanguage('ru-RU');
	$translateProvider.fallbackLanguage('ru');
}]).
config(['uiSelectConfig', function (uiSelectConfig) {
	uiSelectConfig.theme = 'bootstrap';
}])
;

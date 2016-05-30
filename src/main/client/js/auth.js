angular.module('app').
config(['$urlRouterProvider', function ($urlRouterProvider) {
	$urlRouterProvider.otherwise(function ($injector) {
		var $state = $injector.get("$state");
		$state.go('home');//todo URL?
	});
}]).
run(['$q', 'PermissionStore', 'RoleStore', 'principal', function ($q, PermissionStore, RoleStore, principal) {
	var checkPermission = function (permissionName) {
		return principal.identity().then(function () {
			const hasPermission = principal.hasPermission(permissionName);
			if (hasPermission) {
				return true;
			} else {
				return $q.reject(permissionName);
			}
		});
	};
	PermissionStore.definePermission('seeDashboard', function () {
		//todo remove?
		return true;
	});
	PermissionStore.definePermission('isAuthenticated', function () {
		return principal.identity().then(function () {
			const authenticated = principal.isAuthenticated();
			if (authenticated) {
				return true;
			} else {
				return $q.reject('isAuthenticated');
			}
		});
	});
	var common = ['isAuthenticated'];
	var roles = {
		//Суперадминистратор
		SUPER: [
			, "account.list"
			, "account.create"
			, "account.update"
			, "accountSession.list"
		],
		//Администратор
		ADMIN: [
			, "task.list"
			, "task.create"
			, "task.update"
		],
		//Оператор/Обработчик
		OPERATOR: [
			, "task.list"
			, "task.view"
		],
		//Надзор
		SUPERVISOR: [
			, "unknown"
		]
	};
	for (var role in roles) {
		var permissions = roles[role];
		if (permissions.length > 0) {
			PermissionStore.defineManyPermissions(permissions, checkPermission);
			RoleStore.defineRole(role, common.concat(permissions));
		}
	}
}]).
factory('principal', ['$q', '$resource', function ($q, $resource) {
	var _identity = undefined;
	var _authenticated = false;
	return {
		isIdentityResolved: function () {
			return angular.isDefined(_identity);
		},
		isAuthenticated: function () {
			return _authenticated;
		},
		account: function () {
			return _identity;
		},
		hasPermission: function (permission) {
			if (!_authenticated || !_identity.permission) {
				return false;
			}

			return _identity.permission.indexOf(permission) != -1;
		},
		hasAnyPermission: function (permissions) {
			if (!_authenticated || !_identity.permission) {
				return false;
			}

			for (var i = 0; i < permissions.length; i++) {
				if (this.hasPermission(permissions[i])) {
					return true;
				}
			}

			return false;
		},
		//Сохранение данных об авторизации
		authenticate: function (identity) {
			_identity = identity;
			_authenticated = identity != null;
		},
		//Получение данных о пользователе с сервера
		identity: function (force) {
			var deferred = $q.defer();

			if (force === true) {
				_identity = undefined;
			}

			// check and see if we have retrieved the identity data from the server. if we have, reuse it by immediately resolving
			if (this.isIdentityResolved()) {
				deferred.resolve(_identity);

				return deferred.promise;
			}

			var self = this;
			$resource(apiBaseUrl + '/auth').get({},
					function (data) {
						if (data && data.permission) {
							self.authenticate(data);
							deferred.resolve(_identity);
						} else {
							deferred.reject(_identity);
						}
					},
					function () {
						self.authenticate(null);
						deferred.reject(_identity);
					}
			);

			return deferred.promise;
		},
		login: function (username, password, rememberMe) {
			var deferred = $q.defer();

			const account = {
				username: username,
				password: password
			};
			var self = this;
			const headers = {
				'X-Requested-With': 'XMLHttpRequest',
				'Authorization': "Basic " + btoa(username + ":" + password)
			};
			const params = {rememberMe: rememberMe};
			$resource(apiBaseUrl + '/auth', {}, {
				auth: {
					method: 'POST',
					isArray: false,
					headers: headers
				}
			}).auth(params, account,
					function (data) {
						if (data && data.permission) {
							self.authenticate(data);
							deferred.resolve(_identity);
						} else {
							deferred.reject(_identity);
						}
					},
					function (error) {
						self.authenticate(null);
						deferred.reject(error);
					}
			);

			return deferred.promise;
		},
		logout: function () {
			var deferred = $q.defer();

			var self = this;
			$resource(apiBaseUrl + '/auth/logout').save({},
					function (data) {
						self.authenticate(null);
						deferred.resolve(data);
					},
					function (error) {
						self.authenticate(null);
						deferred.resolve(error);
					}
			);

			return deferred.promise;
		},
		changePassword: function (oldPassword, newPassword) {
			var deferred = $q.defer();

			const pass = {
				old: oldPassword,
				new: newPassword
			};
			$resource(apiBaseUrl + '/auth', {}, {
				pass: {
					method: 'PATCH',
					isArray: false
				}
			}).pass({}, pass,
					function (data) {
						if (data && data.permission) {
							deferred.resolve(data);
						} else {
							deferred.reject(data);
						}
					},
					function (error) {
						deferred.reject(error);
					}
			);

			return deferred.promise;
		}
	};
}]).
controller('authController', ['$scope', '$state', 'principal', 'toastr', 'dialogs', function ($scope, $state, principal, toastr, dialogs) {
	$scope.principal = principal;
	principal.identity();//Запрос данных с сервера
	$scope.logout = function () {
		principal.logout()
				.then(function () {
					$state.go('login');
				});
	};
	$scope.model = {};
	$scope.login = function () {
		principal.login($scope.model.username, $scope.model.password, $scope.model.rememberMe)
				.then(function () {
					$state.go('home');
				}, function (error) {
					if (error && error.status && error.status == 401) {
						toastr.error('Invalid username or password', 'Authorization error');
					} else if (error && error.status) {
						toastr.error('Error ' + error.status + ': ' + error.statusText, 'Unknown error');
					} else {
						toastr.error('Error: ' + error, 'Unknown error');
					}
				});
	};
	$scope.changePasswordForm = function () {
		dialogs.create('view/common/changePassword.html', 'changePasswordController', {test: 123}, {keyboard: true});
	};
}]).
controller('changePasswordController', ['$scope', 'principal', 'toastr', function ($scope, principal, toastr) {
	$scope.model = {};
	$scope.cancel = function () {
		$scope.$dismiss('Canceled');
	};
	$scope.save = function () {
		principal.changePassword($scope.model.oldPassword, $scope.model.newPassword)
				.then(function () {
					$scope.$close();
					toastr.success('Password changed', 'Change password');
				}, function (error) {
					toastr.error(error.statusText, 'Error');
				});
	};
	$scope.hitEnter = function (evt) {
		const isNullOrEmpty = function (value) {
			return angular.equals(value, null) || angular.equals(value, '');
		};
		const notNullOrEmpty = function (value) {
			return !isNullOrEmpty(value);
		};
		if (angular.equals(evt.keyCode, 13) && notNullOrEmpty($scope.model.oldPassword) && notNullOrEmpty($scope.model.newPassword)) {
			$scope.save();
		}
	};
}]).
config(['$stateProvider', function ($stateProvider) {
	var redirectTo = function (rejectedPermission, transitionProperties) {
		if (!rejectedPermission || rejectedPermission == 'isAuthenticated') {
			return 'login';//todo Const?
		} else {
			return 'accessDenied';//todo Const?
		}
	};
	$stateProvider
			.decorator('data', function (state, parent) {
				var result = parent(state);
				//Копирование данных из родителя
				if (state.parent && state.parent.data) {
					result = jQuery.extend({}, result, state.parent.data, state.self.data);
				}
				//Добавление функции для редиректа
				if (result && result.permissions && !result.permissions.redirectTo) {
					result.permissions.redirectTo = redirectTo;
				}
				state.data = result;
				state.self.data = result;
				return result;
			})
			.state('home', {
				url: '/',
				templateUrl: 'view/home.html',
				data: {
					permissions: {
						only: ['isAuthenticated']
					}
				}
			})
			.state('accessDenied', {
				url: '/accessDenied',
				templateUrl: 'view/common/accessDenied.html'
			})
			.state('login', {
				url: '/login',
				templateUrl: 'view/common/login.html'
			})
	;
}])
;

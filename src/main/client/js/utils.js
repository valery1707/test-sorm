/**
 * Format bytes to string
 * @param bytes Bytes count
 * @param decimals Count decimals after dot
 * @param k Coefficient
 * @param sizes Sizes
 * @returns string
 * @see https://stackoverflow.com/a/18650828
 */
function formatBytes(bytes, decimals, k, sizes) {
	if (bytes == 0) {
		return '0 Byte';
	}
	var dm = decimals + 0 || 2;
	var i = Math.floor(Math.log(bytes) / Math.log(k));
	return (bytes / Math.pow(k, i)).toFixed(dm) + ' ' + sizes[i];
}
/**
 * Format bytes to string in 1024 mode
 * @param bytes Bytes count
 * @param decimals Count decimals after dot
 * @returns string
 * @see https://stackoverflow.com/a/18650828
 */
function formatBytes_1024(bytes, decimals) {
	return formatBytes(bytes, decimals, 1024, ['Bytes', 'KiB', 'MiB', 'GiB', 'TiB', 'PiB', 'EiB', 'ZiB', 'YiB']);
}
/**
 * Format bytes to string in 1000 mode
 * @param bytes Bytes count
 * @param decimals Count decimals after dot
 * @returns string
 * @see https://stackoverflow.com/a/18650828
 */
function formatBytes_1000(bytes, decimals) {
	return formatBytes(bytes, decimals, 1000, ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB']);
}

/**
 * Array Remove - By John Resig (MIT Licensed)
 * @see https://stackoverflow.com/a/9815010/1263442
 */
Array.prototype.remove = function (from, to) {
	var rest = this.slice((to || from) + 1 || this.length);
	this.length = from < 0 ? this.length + from : from;
	return this.push.apply(this, rest);
};

/**
 * todo https://github.com/ReactiveSets/toubkal/blob/master/lib/util/value_equals.js
 * (при выполнении нужно проверить работу валидации для фильтра по email/IP в задачах)
 * @param a
 * @param b
 */
function object_equals(a, b) {
	return (a === b) || (JSON.stringify(a) === JSON.stringify(b));
}

function object_remove_empty_array_fields(object) {
	if (object instanceof Object) {
		for (var p in object) {
			if (object.hasOwnProperty(p)) {
				const v = object[p];
				if (v instanceof Array && v.length == 0) {
					object[p] = undefined;
				}
			}
		}
	}
}

angular.module('app').
filter('formatBytes', function () {
	return function (bytes, k, decimals) {
		if (bytes == null) {
			return '';
		}
		if (k == 1024) {
			return formatBytes_1024(bytes, decimals);
		} else {
			return formatBytes_1000(bytes, decimals);
		}
	}
}).filter('secondToPeriod', function () {
	return function (seconds) {
		if (seconds == null) {
			return '';
		}
		return moment.duration(seconds, 'seconds').humanize();
	}
}).
//@see https://docs.angularjs.org/api/ng/directive/select#binding-select-to-a-non-string-value-via-ngmodel-parsing-formatting
directive('convertToDate', function () {
	return {
		require: 'ngModel',
		link: function (scope, element, attrs, ngModel) {
			ngModel.$parsers.push(function (val) {
				return val ? moment(val).format('YYYY-MM-DD') : null;
			});
			ngModel.$formatters.push(function (val) {
				return val ? moment(val).format('YYYY-MM-DD') : null;
			});
		}
	};
}).
directive('convertToDateTime', function () {
	return {
		require: 'ngModel',
		link: function (scope, element, attrs, ngModel) {
			ngModel.$parsers.push(function (val) {
				return val ? moment(val).format('YYYY-MM-DDTHH:mm:ss') : null;
			});
			ngModel.$formatters.push(function (val) {
				return val ? moment(val).format('YYYY-MM-DD HH:mm') : null;
			});
		}
	};
}).
directive('convertOptionToValue', function () {
	return {
		require: 'ngModel',
		link: function (scope, element, attrs, ngModel) {
			ngModel.$parsers.push(function (val) {
				return val ? val.value : null;
			});
			ngModel.$formatters.push(function (val) {
				return val;
			});
		}
	};
}).
directive('navDropdownHover', function () {
	return {
		restrict: "A",
		link: function (scope, element, attrs) {
			$(function () {
				const opt = attrs.navDropdownHover ? JSON.parse(attrs.navDropdownHover) : {};
				jQuery.extend(opt, {
					delay: 500,
					instantlyCloseOthers: true,
					hoverDelay: 0
				});
				$(element).dropdownHover(opt);
			})
		}
	};
})
;

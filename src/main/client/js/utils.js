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
})
;

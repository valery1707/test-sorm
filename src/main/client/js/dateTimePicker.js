angular.module('app').
directive('dateTimePicker', function(){
	return {
		restrict : "A",
		require: 'ngModel',
		link : function(scope, element, attrs, ngModel){
			$(function(){
				$(element).datetimepicker({
					format: 'Y-m-d H:i',
					step: 15,
					lang: 'ru',
					dayOfWeekStart: 1,
					onChangeDateTime: function (dp, $input) {
						scope.$apply(function () {
							ngModel.$setViewValue($input.val());
						});
					},
					onClose: function (dp, $input) {
						scope.$apply(function () {
							ngModel.$setViewValue($input.val());
						});
					}
				});
			})
		}
	}
})
;

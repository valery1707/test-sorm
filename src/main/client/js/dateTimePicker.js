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
}).
service('dateTimePickerFilterTemplate', [function () {
	return function () {
		return '<div' +
			   ' class="ui-grid-filter-container"' +
			   ' ng-repeat="colFilter in col.filters"' +
			   ' ng-class="{\'ui-grid-filter-cancel-button-hidden\' : colFilter.disableCancelFilterButton === true }">' +
			   '  <input' +
			   '   type="text"' +
			   '   style="display:inline; width:100%"' +
			   '   class="ui-grid-filter-input ui-grid-filter-input-{{$index}}"' +
			   '   date-time-picker' +
			   '   ng-model="col.filters[$index].term"' +
			   '   ng-attr-placeholder="{{colFilter.placeholder || \'\'}}"' +
			   '   aria-label="{{colFilter.ariaLabel || aria.defaultFilterLabel}}">' +
			   '  <div' +
			   '   role="button"' +
			   '   class="ui-grid-filter-button"' +
			   '   ng-click="removeFilter(colFilter, $index)"' +
			   '   ng-if="!colFilter.disableCancelFilterButton"' +
			   '   ng-disabled="colFilter.term === undefined || colFilter.term === null || colFilter.term === \'\'"' +
			   '   ng-show="colFilter.term !== undefined && colFilter.term !== null && colFilter.term !== \'\'">' +
			   '    <i' +
			   '     class="ui-grid-icon-cancel"' +
			   '     ui-grid-one-bind-aria-label="aria.removeFilter">' +
			   '      &nbsp;' +
			   '    </i>' +
			   '  </div>' +
			   '</div>';
	};
}])
;

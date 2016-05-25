package name.valery1707.megatel.sorm.db.filter;

import name.valery1707.megatel.sorm.db.SingularExpressionGetter;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

public class DateFilter<D, F> extends BaseBetweenFilter<D, F, LocalDate, LocalDate> {
	public DateFilter(SingularExpressionGetter<D, LocalDate> field, Function<F, List<LocalDate>> getter) {
		super(field, getter, v -> v);
	}
}

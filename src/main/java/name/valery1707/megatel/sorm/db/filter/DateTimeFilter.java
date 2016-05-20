package name.valery1707.megatel.sorm.db.filter;

import name.valery1707.megatel.sorm.db.SingularAttributeGetter;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.Function;

public class DateTimeFilter<D, F> extends BaseBetweenFilter<D, F, ZonedDateTime, ZonedDateTime> {
	public DateTimeFilter(SingularAttributeGetter<D, ZonedDateTime> field, Function<F, List<ZonedDateTime>> getter) {
		super(field, getter, v -> v);
	}
}

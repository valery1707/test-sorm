package name.valery1707.megatel.sorm.db.filter;

import name.valery1707.megatel.sorm.DateUtils;
import name.valery1707.megatel.sorm.db.SingularAttributeGetter;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.Function;

public class DateTimeFilterDecimal<D, F> extends BaseBetweenFilter<D, F, BigDecimal, ZonedDateTime> {
	public DateTimeFilterDecimal(SingularAttributeGetter<D, BigDecimal> field, Function<F, List<ZonedDateTime>> value) {
		super(field, value, DateUtils::zonedDateTime2BigDecimal);
	}
}

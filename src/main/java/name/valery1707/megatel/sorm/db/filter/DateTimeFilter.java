package name.valery1707.megatel.sorm.db.filter;

import javax.persistence.metamodel.SingularAttribute;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.Function;

public class DateTimeFilter<D, F> extends BaseBetweenFilter<D, F, ZonedDateTime, ZonedDateTime> {
	public DateTimeFilter(SingularAttribute<D, ZonedDateTime> field, Function<F, List<ZonedDateTime>> getter) {
		super(field, getter, v -> v);
	}
}

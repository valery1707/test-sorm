package name.valery1707.megatel.sorm.db.filter;

import javax.annotation.Nonnull;
import javax.persistence.criteria.*;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class DateTimeFilter<D, F> extends BaseFilter<D, F, List<ZonedDateTime>> {
	public DateTimeFilter(String field, Function<F, List<ZonedDateTime>> getter) {
		super(field, getter);
	}

	@Override
	protected boolean nonEmpty(List<ZonedDateTime> value) {
		return super.nonEmpty(value)
			   && value.stream().anyMatch(Objects::nonNull)
			   && value.size() == 2;
	}

	@Override
	protected Predicate toPredicateImpl(Root<D> root, CriteriaQuery<?> query, CriteriaBuilder cb, @Nonnull List<ZonedDateTime> filter) {
		Expression<ZonedDateTime> field = root.get(field());
		ZonedDateTime start = filter.get(0);
		ZonedDateTime finish = filter.get(1);
		if (start == null) {
			return cb.lessThanOrEqualTo(field, finish);
		} else if (finish == null) {
			return cb.greaterThanOrEqualTo(field, start);
		} else {
			return cb.between(field, start, finish);
		}
	}
}

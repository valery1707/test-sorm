package name.valery1707.megatel.sorm.db;

import name.valery1707.megatel.sorm.db.filter.*;
import org.springframework.data.jpa.domain.Specification;

import javax.annotation.Nonnull;
import javax.persistence.criteria.Predicate;
import javax.persistence.metamodel.SingularAttribute;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static name.valery1707.megatel.sorm.db.SingularAttributeGetter.field;

public class SpecificationBuilder<D, F> {

	private List<Filter<? super D, F>> filters = new ArrayList<>();
	private final SpecificationMode mode;

	public SpecificationBuilder(SpecificationMode mode) {
		this.mode = mode;
	}

	public <X> SpecificationBuilder<D, F> withEquals(Function<F, X> value, SingularExpressionGetter<D, X> fieldGetter) {
		filters.add(new EqualsFilter<>(fieldGetter, value));
		return this;
	}

	public <X> SpecificationBuilder<D, F> withEquals(Function<F, X> value, SingularAttribute<D, X> field) {
		return withEquals(value, field(field));
	}

	public SpecificationBuilder<D, F> withString(Function<F, String> value, SingularExpressionGetter<? super D, String> fieldGetter) {
		filters.add(new StringFilter<>(fieldGetter, value));
		return this;
	}

	public SpecificationBuilder<D, F> withString(Function<F, String> value, SingularAttribute<? super D, String> field) {
		return withString(value, field(field));
	}

	public <F1> SpecificationBuilder<D, F> withString(Function<F, String> value, SingularAttribute<? super D, F1> field1, SingularAttribute<? super F1, String> field2) {
		return withString(value, field(field1).nest(field2));
	}

	public SpecificationBuilder<D, F> withIp(Function<F, String> value, SingularExpressionGetter<? super D, BigInteger> fieldGetter) {
		filters.add(new IpFilter<>(fieldGetter, value));
		return this;
	}

	public SpecificationBuilder<D, F> withIp(Function<F, String> value, SingularAttribute<? super D, BigInteger> field) {
		return withIp(value, field(field));
	}

	public <M extends Number & Comparable<M>> SpecificationBuilder<D, F> withNumber(Function<F, String> value, SingularExpressionGetter<? super D, M> fieldGetter) {
		filters.add(new NumberFilter<>(fieldGetter, value));
		return this;
	}

	public <M extends Number & Comparable<M>> SpecificationBuilder<D, F> withNumber(Function<F, String> value, SingularAttribute<? super D, M> field) {
		return withNumber(value, field(field));
	}

	public SpecificationBuilder<D, F> withDateTime(Function<F, List<ZonedDateTime>> value, SingularExpressionGetter<? super D, ZonedDateTime> fieldGetter) {
		filters.add(new DateTimeFilter<>(fieldGetter, value));
		return this;
	}

	public SpecificationBuilder<D, F> withDateTime(Function<F, List<ZonedDateTime>> value, SingularAttribute<? super D, ZonedDateTime> field) {
		return withDateTime(value, field(field));
	}

	public SpecificationBuilder<D, F> withDate(Function<F, List<LocalDate>> value, SingularExpressionGetter<? super D, LocalDate> fieldGetter) {
		filters.add(new DateFilter<>(fieldGetter, value));
		return this;
	}

	public SpecificationBuilder<D, F> withDate(Function<F, List<LocalDate>> value, SingularAttribute<? super D, LocalDate> fieldGetter) {
		return withDate(value, field(fieldGetter));
	}

	public SpecificationBuilder<D, F> withDateTimeDecimal(Function<F, List<ZonedDateTime>> value, SingularExpressionGetter<? super D, BigDecimal> fieldGetter) {
		filters.add(new DateTimeFilterDecimal<>(fieldGetter, value));
		return this;
	}

	public SpecificationBuilder<D, F> withDateTimeDecimal(Function<F, List<ZonedDateTime>> value, SingularAttribute<? super D, BigDecimal> field) {
		return withDateTimeDecimal(value, field(field));
	}

	public Specification<D> build(F filter) {
		if (filter == null) {
			return null;
		}
		return (root, query, cb) -> {
			List<Predicate> predicates = filters.stream()
					.map(SpecificationBuilder::cast)
					.map(f -> f.toPredicate(root, query, cb, filter))
					.filter(Objects::nonNull)
					.collect(Collectors.toList());
			if (predicates.isEmpty()) {
				return null;
			} else {
				Predicate[] array = predicates.toArray(new Predicate[predicates.size()]);
				return mode.mapper().apply(cb, array);
			}
		};
	}

	@Nonnull
	@SuppressWarnings("unchecked")
	private static <X, Y> Filter<X, Y> cast(Filter<? super X, Y> src) {
		return (Filter<X, Y>) src;
	}
}

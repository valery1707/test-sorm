package name.valery1707.megatel.sorm.db;

import javaslang.collection.List;
import name.valery1707.megatel.sorm.db.filter.*;
import org.springframework.data.jpa.domain.Specification;

import javax.annotation.Nonnull;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.metamodel.SingularAttribute;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

import static name.valery1707.megatel.sorm.db.SingularAttributeGetter.field;

/**
 * @param <D> Domain-class
 * @param <F> Filter-class
 */
public class SpecificationBuilder<D, F> {

	private List<Filter<? super D, F>> filters = List.empty();
	private final SpecificationMode mode;

	public SpecificationBuilder(SpecificationMode mode) {
		this.mode = mode;
	}

	public SpecificationBuilder<D, F> with(Filter<? super D, F> filter) {
		filters = filters.append(filter);
		return this;
	}

	public <X> SpecificationBuilder<D, F> withCustomSimple(Function<F, X> value, SingularExpressionGetter<? super D, X> fieldGetter, Function<CriteriaBuilder, BiFunction<Expression<X>, X, Predicate>> criteriaBuilder) {
		return with(new BaseSimpleFilter<>(fieldGetter, value, criteriaBuilder));
	}

	public <X> SpecificationBuilder<D, F> withCustomSimple(Function<F, X> value, SingularAttribute<? super D, X> field, Function<CriteriaBuilder, BiFunction<Expression<X>, X, Predicate>> criteriaBuilder) {
		return withCustomSimple(value, field(field), criteriaBuilder);
	}

	public <X> SpecificationBuilder<D, F> withEquals(Function<F, X> value, SingularExpressionGetter<D, X> fieldGetter) {
		return with(new EqualsFilter<>(fieldGetter, value));
	}

	public <X> SpecificationBuilder<D, F> withEquals(Function<F, X> value, SingularAttribute<D, X> field) {
		return withEquals(value, field(field));
	}

	public SpecificationBuilder<D, F> withString(Function<F, String> value, SingularExpressionGetter<? super D, String> fieldGetter) {
		return with(new StringFilter<>(fieldGetter, value));
	}

	public SpecificationBuilder<D, F> withString(Function<F, String> value, SingularAttribute<? super D, String> field) {
		return withString(value, field(field));
	}

	public <F1> SpecificationBuilder<D, F> withString(Function<F, String> value, SingularAttribute<? super D, F1> field1, SingularAttribute<? super F1, String> field2) {
		return withString(value, field(field1).nest(field2));
	}

	public SpecificationBuilder<D, F> withIp(Function<F, String> value, SingularExpressionGetter<? super D, BigInteger> fieldGetter) {
		return with(new IpFilter<>(fieldGetter, value));
	}

	public SpecificationBuilder<D, F> withIp(Function<F, String> value, SingularAttribute<? super D, BigInteger> field) {
		return withIp(value, field(field));
	}

	public <M extends Number & Comparable<M>> SpecificationBuilder<D, F> withNumber(Function<F, String> value, SingularExpressionGetter<? super D, M> fieldGetter) {
		return with(new NumberFilter<>(fieldGetter, value));
	}

	public <M extends Number & Comparable<M>> SpecificationBuilder<D, F> withNumber(Function<F, String> value, SingularAttribute<? super D, M> field) {
		return withNumber(value, field(field));
	}

	public SpecificationBuilder<D, F> withDateTime(Function<F, java.util.List<ZonedDateTime>> value, SingularExpressionGetter<? super D, ZonedDateTime> fieldGetter) {
		return with(new DateTimeFilter<>(fieldGetter, value));
	}

	public SpecificationBuilder<D, F> withDateTime(Function<F, java.util.List<ZonedDateTime>> value, SingularAttribute<? super D, ZonedDateTime> field) {
		return withDateTime(value, field(field));
	}

	public SpecificationBuilder<D, F> withDate(Function<F, java.util.List<LocalDate>> value, SingularExpressionGetter<? super D, LocalDate> fieldGetter) {
		return with(new DateFilter<>(fieldGetter, value));
	}

	public SpecificationBuilder<D, F> withDate(Function<F, java.util.List<LocalDate>> value, SingularAttribute<? super D, LocalDate> fieldGetter) {
		return withDate(value, field(fieldGetter));
	}

	public SpecificationBuilder<D, F> withDateTimeDecimal(Function<F, java.util.List<ZonedDateTime>> value, SingularExpressionGetter<? super D, BigDecimal> fieldGetter) {
		return with(new DateTimeFilterDecimal<>(fieldGetter, value));
	}

	public SpecificationBuilder<D, F> withDateTimeDecimal(Function<F, java.util.List<ZonedDateTime>> value, SingularAttribute<? super D, BigDecimal> field) {
		return withDateTimeDecimal(value, field(field));
	}

	public Specification<D> build(F filter) {
		if (filter == null) {
			return null;
		}
		return (root, query, cb) -> {
			List<Predicate> predicates = filters
					.map(SpecificationBuilder::cast)
					.map(f -> f.toPredicate(root, query, cb, filter))
					.filter(Objects::nonNull);
			if (predicates.isEmpty()) {
				return null;
			} else {
				Predicate[] array = predicates.toJavaArray(Predicate.class);
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

package name.valery1707.megatel.sorm.db;

import name.valery1707.megatel.sorm.db.filter.*;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import javax.persistence.metamodel.SingularAttribute;
import java.math.BigDecimal;
import java.math.BigInteger;
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

	public SpecificationBuilder<D, F> withString(Function<F, String> value, SingularAttributeGetter<? super D, String> field) {
		filters.add(new StringFilter<>(field, value));
		return this;
	}

	@Deprecated
	public SpecificationBuilder<D, F> withString(SingularAttribute<? super D, String> field, Function<F, String> value) {
		return withString(value, field(field));
	}

	public SpecificationBuilder<D, F> withString(Function<F, String> value, SingularAttribute<? super D, String> field) {
		return withString(value, field(field));
	}

	public <F1> SpecificationBuilder<D, F> withString(Function<F, String> value, SingularAttribute<? super D, F1> field1, SingularAttribute<? super F1, String> field2) {
		return withString(value, field(field1).nest(field2));
	}

	public SpecificationBuilder<D, F> withIp(SingularAttribute<? super D, BigInteger> field, Function<F, String> value) {
		filters.add(new IpFilter<>(field(field), value));
		return this;
	}

	public <M extends Number & Comparable<M>> SpecificationBuilder<D, F> withNumber(SingularAttribute<? super D, M> field, Function<F, String> value) {
		filters.add(new NumberFilter<>(field(field), value));
		return this;
	}

	public SpecificationBuilder<D, F> withDateTime(SingularAttribute<? super D, ZonedDateTime> field, Function<F, List<ZonedDateTime>> value) {
		filters.add(new DateTimeFilter<>(field(field), value));
		return this;
	}

	public SpecificationBuilder<D, F> withDateTimeDecimal(SingularAttribute<? super D, BigDecimal> field, Function<F, List<ZonedDateTime>> value) {
		filters.add(new DateTimeFilterDecimal<>(field(field), value));
		return this;
	}

	public Specification<D> build(F filter) {
		if (filter == null) {
			return null;
		}
		return (root, query, cb) -> {
			List<Predicate> predicates = filters.stream()
					.map(f -> (Filter<D, F>) f)
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
}

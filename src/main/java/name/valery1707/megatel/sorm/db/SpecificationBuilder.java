package name.valery1707.megatel.sorm.db;

import name.valery1707.megatel.sorm.db.filter.*;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import javax.persistence.metamodel.SingularAttribute;
import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SpecificationBuilder<D, F> {

	private List<Filter<D, F>> filters = new ArrayList<>();
	private final SpecificationMode mode;

	public SpecificationBuilder(SpecificationMode mode) {
		this.mode = mode;
	}

	public SpecificationBuilder<D, F> withString(SingularAttribute<D, String> field, Function<F, String> value) {
		filters.add(new StringFilter<>(field, value));
		return this;
	}

	public SpecificationBuilder<D, F> withIp(SingularAttribute<D, BigInteger> field, Function<F, String> value) {
		filters.add(new IpFilter<>(field, value));
		return this;
	}

	public <M extends Number & Comparable<M>> SpecificationBuilder<D, F> withNumber(SingularAttribute<D, M> field, Function<F, String> value) {
		filters.add(new NumberFilter<>(field, value));
		return this;
	}

	public SpecificationBuilder<D, F> withDateTime(SingularAttribute<D, ZonedDateTime> field, Function<F, List<ZonedDateTime>> value) {
		filters.add(new DateTimeFilter<>(field, value));
		return this;
	}

	public Specification<D> build(F filter) {
		if (filter == null) {
			return null;
		}
		return (root, query, cb) -> {
			List<Predicate> predicates = filters.stream()
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

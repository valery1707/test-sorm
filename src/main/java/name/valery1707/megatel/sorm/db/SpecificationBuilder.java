package name.valery1707.megatel.sorm.db;

import name.valery1707.megatel.sorm.db.filter.*;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
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

	public SpecificationBuilder<D, F> withString(String field, Function<F, String> value) {
		//todo Передавать не строку, а SingularAttribute<D, String>
		filters.add(new StringFilter<>(field, value));
		return this;
	}

	public SpecificationBuilder<D, F> withIp(String field, Function<F, String> value) {
		//todo Передавать не строку, а SingularAttribute<D, BigInteger>
		filters.add(new IpFilter<>(field, value));
		return this;
	}

	public SpecificationBuilder<D, F> withNumber(String field, Function<F, String> value) {
		//todo Передавать не строку, а SingularAttribute<D, Integer>
		filters.add(new NumberFilter<>(field, value));
		return this;
	}

	public SpecificationBuilder<D, F> withDateTime(String field, Function<F, List<ZonedDateTime>> value) {
		//todo Передавать не строку, а SingularAttribute<D, ZonedDateTime>
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

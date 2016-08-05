package name.valery1707.core.db;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import java.util.function.BiFunction;

public enum SpecificationMode {
	OR(CriteriaBuilder::or),
	AND(CriteriaBuilder::and);

	private final BiFunction<CriteriaBuilder, Predicate[], Predicate> mapper;

	SpecificationMode(BiFunction<CriteriaBuilder, Predicate[], Predicate> mapper) {
		this.mapper = mapper;
	}

	public BiFunction<CriteriaBuilder, Predicate[], Predicate> mapper() {
		return mapper;
	}
}

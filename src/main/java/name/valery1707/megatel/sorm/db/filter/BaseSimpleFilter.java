package name.valery1707.megatel.sorm.db.filter;


import name.valery1707.megatel.sorm.db.SingularAttributeGetter;

import javax.annotation.Nonnull;
import javax.persistence.criteria.*;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class BaseSimpleFilter<D, F, X> extends BaseFilter<D, F, X, X> {

	private final Function<CriteriaBuilder, BiFunction<Path<X>, X, Predicate>> criteriaBuilder;

	public BaseSimpleFilter(SingularAttributeGetter<D, X> field, Function<F, X> getter, Function<CriteriaBuilder, BiFunction<Path<X>, X, Predicate>> criteriaBuilder) {
		super(field, getter);
		this.criteriaBuilder = criteriaBuilder;
	}

	@Override
	protected Predicate toPredicateImpl(Root<D> root, CriteriaQuery<?> query, CriteriaBuilder cb, @Nonnull X filter) {
		return criteriaBuilder.apply(cb).apply(field(root), filter);
	}
}

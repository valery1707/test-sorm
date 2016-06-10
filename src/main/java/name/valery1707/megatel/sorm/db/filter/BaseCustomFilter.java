package name.valery1707.megatel.sorm.db.filter;

import name.valery1707.megatel.sorm.db.SingularExpressionGetter;

import javax.annotation.Nonnull;
import javax.persistence.criteria.*;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @param <D> Domain class
 * @param <F> Filter class
 * @param <X> Filter field class
 * @param <Y> Domain field class
 */
public class BaseCustomFilter<D, F, X, Y> extends BaseFilter<D, F, Y, X> {
	private final Function<CriteriaBuilder, BiFunction<Expression<Y>, X, Predicate>> criteriaBuilder;

	public BaseCustomFilter(SingularExpressionGetter<D, Y> field, Function<F, X> getter, Function<CriteriaBuilder, BiFunction<Expression<Y>, X, Predicate>> criteriaBuilder) {
		super(field, getter);
		this.criteriaBuilder = criteriaBuilder;
	}

	@Override
	protected Predicate toPredicateImpl(Root<D> root, CriteriaQuery<?> query, CriteriaBuilder cb, @Nonnull X filter) {
		return criteriaBuilder.apply(cb).apply(field(root, cb), filter);
	}
}

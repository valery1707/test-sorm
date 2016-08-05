package name.valery1707.core.db.filter;


import name.valery1707.core.db.SingularExpressionGetter;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import java.util.function.BiFunction;
import java.util.function.Function;

public class BaseSimpleFilter<D, F, X> extends BaseCustomFilter<D, F, X, X> {

	public BaseSimpleFilter(SingularExpressionGetter<D, X> field, Function<F, X> getter, Function<CriteriaBuilder, BiFunction<Expression<X>, X, Predicate>> criteriaBuilder) {
		super(field, getter, criteriaBuilder);
	}
}

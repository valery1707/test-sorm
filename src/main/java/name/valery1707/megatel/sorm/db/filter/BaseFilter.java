package name.valery1707.megatel.sorm.db.filter;

import name.valery1707.megatel.sorm.db.SingularExpressionGetter;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.persistence.criteria.*;
import java.util.function.Function;

/**
 * @param <D> Domain class
 * @param <F> Filter class
 * @param <M> Domain field class
 * @param <V> Filter field class
 */
public abstract class BaseFilter<D, F, M, V> implements Filter<D, F> {
	private final SingularExpressionGetter<D, M> field;
	private final Function<F, V> getter;

	public BaseFilter(SingularExpressionGetter<D, M> field, Function<F, V> getter) {
		this.field = field;
		this.getter = getter;
	}

	protected Expression<M> field(Root<D> root, CriteriaBuilder cb) {
		return field.apply(root, cb);
	}

	protected V value(F filter) {
		return getter.apply(filter);
	}

	@Override
	public Predicate toPredicate(Root<D> root, CriteriaQuery<?> query, CriteriaBuilder cb, @NotNull F filter) {
		V value = value(filter);
		if (nonEmpty(value)) {
			return toPredicateImpl(root, query, cb, value);
		} else {
			return null;
		}
	}

	protected boolean nonEmpty(V value) {
		return value != null;
	}

	protected abstract Predicate toPredicateImpl(Root<D> root, CriteriaQuery<?> query, CriteriaBuilder cb, @Nonnull V filter);
}

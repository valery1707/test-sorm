package name.valery1707.megatel.sorm.db.filter;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;
import java.util.function.Function;

/**
 * @param <D> Domain class
 * @param <F> Filter class
 * @param <M> Domain field class
 * @param <V> Filter field class
 */
public abstract class BaseFilter<D, F, M, V> implements Filter<D, F> {
	private final SingularAttribute<D, M> field;
	private final Function<F, V> getter;

	public BaseFilter(SingularAttribute<D, M> field, Function<F, V> getter) {
		this.field = field;
		this.getter = getter;
	}

	protected SingularAttribute<D, M> field() {
		return field;
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

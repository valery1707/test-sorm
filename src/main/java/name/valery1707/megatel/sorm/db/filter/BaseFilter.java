package name.valery1707.megatel.sorm.db.filter;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.function.Function;

public abstract class BaseFilter<D, F, V> implements Filter<D, F> {
	private final String field;
	private final Function<F, V> getter;

	public BaseFilter(String field, Function<F, V> getter) {
		this.field = field;
		this.getter = getter;
	}

	protected String field() {
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

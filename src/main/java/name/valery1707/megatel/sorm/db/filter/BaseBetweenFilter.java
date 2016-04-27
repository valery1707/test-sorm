package name.valery1707.megatel.sorm.db.filter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.SingularAttribute;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public abstract class BaseBetweenFilter<D, F, M extends Comparable<? super M>, V> extends BaseFilter<D, F, M, List<V>> {
	private final Function<V, M> toDb;

	public BaseBetweenFilter(SingularAttribute<D, M> field, Function<F, List<V>> getter, Function<V, M> toDb) {
		super(field, getter);
		this.toDb = toDb;
	}

	public BaseBetweenFilter(SingularAttribute<D, M> field, Function<F, List<V>> getter) {
		this(field, getter, null);
	}

	@Override
	protected boolean nonEmpty(List<V> value) {
		return super.nonEmpty(value)
			   && value.stream().anyMatch(Objects::nonNull)
			   && value.size() == 2;
	}

	@Override
	protected Predicate toPredicateImpl(Root<D> root, CriteriaQuery<?> query, CriteriaBuilder cb, @Nonnull List<V> filter) {
		Expression<M> field = root.get(field());
		V startRaw = filter.get(0);
		V finishRaw = filter.get(1);
		M start = startRaw != null ? toDb(startRaw) : null;
		M finish = finishRaw != null ? toDb(finishRaw) : null;
		if (start == null) {
			return cb.lessThanOrEqualTo(field, finish);
		} else if (finish == null) {
			return cb.greaterThanOrEqualTo(field, start);
		} else {
			return cb.between(field, start, finish);
		}
	}

	@Nullable
	protected M toDb(@Nonnull V value) {
		return toDb.apply(value);
	}
}

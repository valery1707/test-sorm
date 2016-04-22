package name.valery1707.megatel.sorm.db.filter;

import org.jetbrains.annotations.NotNull;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;
import java.util.function.Function;

public class StringFilter<D, F> extends BaseFilter<D, F, String, String> {
	public StringFilter(SingularAttribute<D, String> field, Function<F, String> getter) {
		super(field, getter);
	}

	@Override
	protected Predicate toPredicateImpl(Root<D> root, CriteriaQuery<?> query, CriteriaBuilder cb, @NotNull String filter) {
		return cb.like(cb.lower(root.get(field())), "%" + filter.toLowerCase() + "%");
	}
}

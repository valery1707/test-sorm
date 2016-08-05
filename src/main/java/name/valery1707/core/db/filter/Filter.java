package name.valery1707.core.db.filter;

import javax.annotation.Nonnull;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * @param <D> Domain class
 * @param <F> Filter class
 */
public interface Filter<D, F> {
	Predicate toPredicate(Root<D> root, CriteriaQuery<?> query, CriteriaBuilder cb, @Nonnull F filter);
}

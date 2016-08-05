package name.valery1707.core.db;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;

@FunctionalInterface
public interface SingularExpressionGetter<D, F> {
	Expression<F> apply(Path<D> root, CriteriaBuilder cb);
}

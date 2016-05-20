package name.valery1707.megatel.sorm.db;

import javax.persistence.criteria.Path;
import javax.persistence.metamodel.SingularAttribute;

/**
 * @param <D> Domain class
 * @param <F> Field class
 */
@FunctionalInterface
public interface SingularAttributeGetter<D, F> {
	Path<F> apply(Path<D> root);

	static <X, Y> DirectSingularAttributeGetter<X, Y> direct(SingularAttribute<X, Y> singularAttribute) {
		return new DirectSingularAttributeGetter<>(singularAttribute);
	}

	class DirectSingularAttributeGetter<D, F> implements SingularAttributeGetter<D, F> {
		private final SingularAttribute<D, F> singularAttribute;

		public DirectSingularAttributeGetter(SingularAttribute<D, F> singularAttribute) {
			this.singularAttribute = singularAttribute;
		}

		@Override
		public Path<F> apply(Path<D> root) {
			return root.get(singularAttribute);
		}
	}

	static <X, Y> NestedSingularAttributeGetter<X, Y> field(SingularAttribute<X, Y> singularAttribute) {
		return new NestedSingularAttributeGetter<>(direct(singularAttribute));
	}

	class NestedSingularAttributeGetter<D, F> implements SingularAttributeGetter<D, F> {
		private final SingularAttributeGetter<D, F> singularAttribute;

		public NestedSingularAttributeGetter(SingularAttributeGetter<D, F> singularAttribute) {
			this.singularAttribute = singularAttribute;
		}

		@Override
		public Path<F> apply(Path<D> root) {
			return singularAttribute.apply(root);
		}

		public <X> NestedSingularAttributeGetter<D, X> nest(SingularAttribute<? super F, X> singularAttribute) {
			NestedSingularAttributeGetter<D, F> self = this;
			return new NestedSingularAttributeGetter<>((SingularAttributeGetter<D, X>) root -> self.apply(root).get(singularAttribute));
		}
	}
}

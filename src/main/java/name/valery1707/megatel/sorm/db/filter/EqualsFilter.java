package name.valery1707.megatel.sorm.db.filter;

import name.valery1707.megatel.sorm.db.SingularAttributeGetter;

import java.util.function.Function;

public class EqualsFilter<D, F, X> extends BaseSimpleFilter<D, F, X> {
	public EqualsFilter(SingularAttributeGetter<D, X> fieldGetter, Function<F, X> getter) {
		super(fieldGetter, getter, cb -> cb::equal);
	}
}

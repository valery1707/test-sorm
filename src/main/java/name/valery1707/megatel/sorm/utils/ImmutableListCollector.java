package name.valery1707.megatel.sorm.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class ImmutableListCollector {
	public static <T, A extends List<T>> Collector<T, A, List<T>> toImmutableList(Supplier<A> collectionFactory) {
		return Collector.of(collectionFactory, List::add, (left, right) -> {
			left.addAll(right);
			return left;
		}, Collections::unmodifiableList);
	}

	public static <t> Collector<t, List<t>, List<t>> toImmutableList() {
		return toImmutableList(ArrayList::new);
	}
}

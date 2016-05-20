package name.valery1707.megatel.sorm.db.filter;

import name.valery1707.megatel.sorm.db.SingularAttributeGetter;

import javax.annotation.Nonnull;
import javax.persistence.criteria.*;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class NumberFilter<D, F, M extends Number & Comparable<M>> extends BaseFilter<D, F, M, String> {
	private static final Pattern PORT_FILTER_PATTERN = Pattern.compile("^([>=<]?[=]?\\d+)|(\\d+\\.\\.\\d+)$");

	public NumberFilter(SingularAttributeGetter<D, M> field, Function<F, String> getter) {
		super(field, getter);
	}

	@SuppressWarnings("unchecked")
	private M val(String str) {
		return (M) Integer.valueOf(str);
	}

	@Override
	protected Predicate toPredicateImpl(Root<D> root, CriteriaQuery<?> query, CriteriaBuilder cb, @Nonnull String filter) {
		Expression<M> field = field(root);
		List<Predicate> predicates = Stream.of(filter.split(","))
				.filter(Objects::nonNull)
				.map(String::trim)
				.filter(s -> !s.isEmpty() && PORT_FILTER_PATTERN.matcher(s).matches())
				.map(s -> {
					if (s.contains("..")) {
						int i1 = s.indexOf("..");
						int i2 = s.lastIndexOf("..");
						String s1 = s.substring(0, i1);
						String s2 = s.substring(i2 + 2);
						return cb.between(field, val(s1), val(s2));
					} else {
						String mode = (s.length() > 1 && s.charAt(1) == '=') ? s.substring(0, 2)
								: (s.charAt(0) == '>' || s.charAt(0) == '=' || s.charAt(0) == '<' ? s.substring(0, 1) : "");
						String s1 = s.substring(mode.length());
						M value = val(s1);
						switch (mode) {
							case ">":
								return cb.greaterThan(field, value);
							case ">=":
								return cb.greaterThanOrEqualTo(field, value);
							case "<":
								return cb.lessThan(field, value);
							case "<=":
								return cb.lessThanOrEqualTo(field, value);
							case "=":
							case "==":
							default://implicit equal
								return cb.equal(field, value);
						}
					}
				})
				.filter(Objects::nonNull)
				.collect(toList());

		if (predicates.isEmpty()) {
			return null;
		} else {
			return cb.or(predicates.toArray(new Predicate[predicates.size()]));
		}
	}
}

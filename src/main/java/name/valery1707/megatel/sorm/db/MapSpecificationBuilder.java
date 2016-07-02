package name.valery1707.megatel.sorm.db;

import javaslang.collection.LinkedHashMap;
import javaslang.collection.List;
import javaslang.collection.Stream;
import name.valery1707.megatel.sorm.DateUtils;
import org.hibernate.jpa.criteria.path.SingularAttributePath;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.metamodel.Attribute;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

public class MapSpecificationBuilder {
	public static <T> Specification<T> buildSpecification(Map<String, String> filter) {
		return (root, query, cb) -> {
			//region Prepare filter
			Set<String> entityFields = Stream.ofAll(root.getModel().getSingularAttributes()).map(Attribute::getName).toJavaSet();
			filter.entrySet().removeIf(t ->
					t.getKey() == null
					|| t.getKey().contains(".")
					|| !entityFields.contains(t.getKey()) ||
					t.getValue() == null);
			//endregion

			//region Predicates
			List<Predicate> predicates = LinkedHashMap.ofAll(filter)
					.toList()
					.map(t -> {
						Path<Object> field = root.get(t._1);
						return cb.equal(field, fixType(t._2, field));
					});
			//endregion

			if (predicates.nonEmpty()) {
				return cb.and(predicates.toJavaArray(Predicate.class));
			} else {
				Path<?> field = root.get(root.getModel().getSingularAttributes().iterator().next());
				return cb.notEqual(field, field);
			}
		};
	}

	private static <T> Object fixType(String value, Path<T> field) {
		if (field instanceof SingularAttributePath) {
			Class javaType = ((SingularAttributePath) field).getJavaType();
			if (BigDecimal.class.isAssignableFrom(javaType)) {
				return DateUtils.zonedDateTime2BigDecimal(DateUtils.parseDateTime(value));
			}
			if (Boolean.class.isAssignableFrom(javaType)) {
				return "true".equalsIgnoreCase(value);
			}
			return value;
		} else {
			return null;
		}
	}
}

package name.valery1707.megatel.sorm.api;

import name.valery1707.megatel.sorm.domain.Data;
import name.valery1707.megatel.sorm.dto.DataDto;
import name.valery1707.megatel.sorm.dto.DataFilter;
import org.apache.commons.net.util.SubnetUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.persistence.criteria.*;
import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static name.valery1707.megatel.sorm.IpUtils.buildSubnet;
import static name.valery1707.megatel.sorm.IpUtils.ipToNumber;

@RestController
@RequestMapping("/data")
public class DataController {
	@Inject
	private DataRepo repo;

	@RequestMapping(method = RequestMethod.GET)
	public Page<DataDto> findAll(
			@PageableDefault(size = 20) @SortDefault("dateTime") Pageable pageable
	) {
		return findByFilter(pageable, null);
	}

	@RequestMapping(method = RequestMethod.POST)
	public Page<DataDto> findByFilter(
			@PageableDefault(size = 20) @SortDefault("dateTime") Pageable pageable,
			@RequestBody(required = false) DataFilter filter
	) {
		Specification<Data> spec = null;
		if (filter != null) {
			spec = new Specification<Data>() {
				@Override
				public Predicate toPredicate(Root<Data> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
					List<Predicate> predicates = new ArrayList<>();

					if (filter.getProtocol() != null) {
						predicates.add(
								cb.like(cb.lower(root.get("protocol")), "%" + filter.getProtocol().toLowerCase() + "%")
						);
					}

					predicates.add(buildIpFilter(root.get("srcIp"), cb, filter.getSrcIp()));
					predicates.add(buildIpFilter(root.get("dstIp"), cb, filter.getDstIp()));

					List<ZonedDateTime> times = filter.getDateTime();
					if (times != null && times.stream().anyMatch(Objects::nonNull) && times.size() == 2) {
						predicates.add(buildTimeFilter(root.get("dateTime"), cb::greaterThanOrEqualTo, times.get(0)));
						predicates.add(buildTimeFilter(root.get("dateTime"), cb::lessThanOrEqualTo, times.get(1)));
					}

					predicates.add(buildPortFilter(root.get("srcPort"), cb, filter.getSrcPort()));
					predicates.add(buildPortFilter(root.get("dstPort"), cb, filter.getDstPort()));

					predicates.removeIf(Objects::isNull);
					if (predicates.isEmpty()) {
						return null;
					} else {
						return cb.and(predicates.toArray(new Predicate[predicates.size()]));
					}
				}
			};
		}
		return repo.findAll(spec, pageable)
				.map(DataDto::fromEntity);
	}

	private static final Pattern PORT_FILTER_PATTERN = Pattern.compile("^([>=<]?[=]?\\d+)|(\\d+\\.\\.\\d+)$");

	private Predicate buildPortFilter(Expression<Integer> field, CriteriaBuilder cb, String portsRaw) {
		if (portsRaw == null) {
			return null;
		}
		List<Predicate> predicates = Stream.of(portsRaw.split(","))
				.filter(Objects::nonNull)
				.map(String::trim)
				.filter(s -> !s.isEmpty() && PORT_FILTER_PATTERN.matcher(s).matches())
				.map(s -> {
					if (s.contains("..")) {
						int i1 = s.indexOf("..");
						int i2 = s.lastIndexOf("..");
						String s1 = s.substring(0, i1);
						String s2 = s.substring(i2 + 2);
						return cb.between(field, Integer.valueOf(s1), Integer.valueOf(s2));
					} else {
						String mode = (s.length() > 1 && s.charAt(1) == '=') ? s.substring(0, 2)
								: (s.charAt(0) == '>' || s.charAt(0) == '=' || s.charAt(0) == '<' ? s.substring(0, 1) : "");
						String s1 = s.substring(mode.length());
						int value = Integer.valueOf(s1);
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
				.collect(toList());

		predicates.removeIf(Objects::isNull);
		if (predicates.isEmpty()) {
			return null;
		} else {
			return cb.or(predicates.toArray(new Predicate[predicates.size()]));
		}
	}

	private <F extends ZonedDateTime> Predicate buildTimeFilter(Expression<F> field, BiFunction<Expression<F>, F, Predicate> filter, @Nullable F time) {
		if (time == null) {
			return null;
		}
		return filter.apply(field, time);
	}

	private <F extends BigInteger> Predicate buildIpFilter(Expression<F> field, CriteriaBuilder cb, String ip) {
		if (ip == null) {
			return null;
		}
		SubnetUtils subnet = buildSubnet(ip);
		if (subnet != null) {
			return cb.between(field, ipToNumber(subnet.getInfo().getLowAddress()), ipToNumber(subnet.getInfo().getHighAddress()));
		}
		return null;
	}
}

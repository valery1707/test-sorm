package name.valery1707.megatel.sorm.api;

import name.valery1707.megatel.sorm.domain.Data;
import name.valery1707.megatel.sorm.dto.DataDto;
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

import javax.inject.Inject;
import javax.persistence.criteria.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
			@RequestBody(required = false) DataDto filter
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

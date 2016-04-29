package name.valery1707.megatel.sorm.api.bro.conn;

import name.valery1707.megatel.sorm.db.SpecificationBuilder;
import name.valery1707.megatel.sorm.db.SpecificationMode;
import name.valery1707.megatel.sorm.domain.BroConn;
import name.valery1707.megatel.sorm.domain.BroConn_;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

@RestController
@RequestMapping("/api/bro/conn")
public class BroConnController {
	@Inject
	private BroConnRepo repo;

	private SpecificationBuilder<BroConn, BroConnFilter> specificationBuilder;

	@PostConstruct
	public void init() {
		specificationBuilder = new SpecificationBuilder<BroConn, BroConnFilter>(SpecificationMode.AND)
				.withDateTimeDecimal(BroConn_.ts, BroConnFilter::getTs)
				.withIp(BroConn_.idOrigIp, BroConnFilter::getIdOrigHost)
				.withIp(BroConn_.idRespIp, BroConnFilter::getIdRespHost)
				.withNumber(BroConn_.idOrigPort, BroConnFilter::getIdOrigPort)
				.withNumber(BroConn_.idRespPort, BroConnFilter::getIdRespPort)
				.withString(BroConn_.proto, BroConnFilter::getProto)
		;
	}

	@RequestMapping(method = RequestMethod.POST)
	public Page<BroConnDto> findByFilter(
			@PageableDefault(size = 20) @SortDefault("ts") Pageable pageable,
			@RequestBody(required = false) BroConnFilter filter
	) {
		Specification<BroConn> spec = specificationBuilder.build(filter);
		return repo.findAll(spec, pageable)
				.map(BroConnDto::new);
	}
}

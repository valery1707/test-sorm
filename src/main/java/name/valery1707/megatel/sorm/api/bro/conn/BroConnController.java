package name.valery1707.megatel.sorm.api.bro.conn;

import name.valery1707.megatel.sorm.api.auth.AccountService;
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

	@Inject
	private AccountService accountService;

	private SpecificationBuilder<BroConn, BroConnFilter> specificationBuilder;

	@PostConstruct
	public void init() {
		specificationBuilder = new SpecificationBuilder<BroConn, BroConnFilter>(SpecificationMode.AND)
				.withDateTimeDecimal(BroConnFilter::getTs, BroConn_.ts)
				.withIp(BroConnFilter::getIdOrigHost, BroConn_.idOrigIp)
				.withIp(BroConnFilter::getIdRespHost, BroConn_.idRespIp)
				.withNumber(BroConnFilter::getIdOrigPort, BroConn_.idOrigPort)
				.withNumber(BroConnFilter::getIdRespPort, BroConn_.idRespPort)
				.withString(BroConnFilter::getProto, BroConn_.proto)
		;
	}

	@RequestMapping(method = RequestMethod.POST)
	public Page<BroConnDto> findByFilter(
			@PageableDefault(size = 20) @SortDefault("ts") Pageable pageable,
			@RequestBody(required = false) BroConnFilter filter
	) {
		accountService.requireAnyRight("task.view");
		Specification<BroConn> spec = specificationBuilder.build(filter);
		return repo.findAll(spec, pageable)
				.map(BroConnDto::new);
	}
}

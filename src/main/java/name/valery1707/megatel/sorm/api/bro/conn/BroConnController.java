package name.valery1707.megatel.sorm.api.bro.conn;

import name.valery1707.core.app.AccountService;
import name.valery1707.core.db.SpecificationBuilder;
import name.valery1707.core.db.SpecificationMode;
import name.valery1707.megatel.sorm.api.task.permit.TaskPermitRepo;
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
import java.time.ZonedDateTime;

@RestController
@RequestMapping("/api/bro/conn")
public class BroConnController {
	@Inject
	private BroConnRepo repo;

	@Inject
	private TaskPermitRepo taskPermitRepo;

	@Inject
	private AccountService accountService;

	private SpecificationBuilder<BroConn, BroConnFilter> specificationBuilder;

	@PostConstruct
	public void init() {
		specificationBuilder = new SpecificationBuilder<BroConn, BroConnFilter>(SpecificationMode.AND)
				.withCustom(BroConnFilter::getTaskId, BroConn_.amtTasksList, cb -> (field, id) -> cb.like(field, "%," + id + ",%"))
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
		if (!taskPermitRepo.isAllowedTask(accountService.getCurrentAuditor(), ZonedDateTime.now(), filter.getTaskId())) {
			filter.setTaskId(-1L);
		}
		Specification<BroConn> spec = specificationBuilder.build(filter);
		return repo.findAll(spec, pageable)
				.map(BroConnDto::new);
	}
}

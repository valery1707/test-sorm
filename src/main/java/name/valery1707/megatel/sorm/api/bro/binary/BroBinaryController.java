package name.valery1707.megatel.sorm.api.bro.binary;

import name.valery1707.core.app.AccountService;
import name.valery1707.core.db.SpecificationBuilder;
import name.valery1707.core.db.SpecificationMode;
import name.valery1707.megatel.sorm.api.task.permit.TaskPermitRepo;
import name.valery1707.megatel.sorm.domain.BroBinary;
import name.valery1707.megatel.sorm.domain.BroBinary_;
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
@RequestMapping("/api/bro/binary")
public class BroBinaryController {
	@Inject
	@SuppressWarnings("SpringJavaAutowiringInspection")
	private BroBinaryRepo repo;

	@Inject
	@SuppressWarnings("SpringJavaAutowiringInspection")
	private TaskPermitRepo taskPermitRepo;

	@Inject
	private AccountService accountService;

	private SpecificationBuilder<BroBinary, BroBinaryFilter> specificationBuilder;

	@PostConstruct
	public void init() {
		specificationBuilder = new SpecificationBuilder<BroBinary, BroBinaryFilter>(SpecificationMode.AND)
				.withCustom(BroBinaryFilter::getTaskId, BroBinary_.amtTasksList, cb -> (field, id) -> cb.like(field, "%," + id + ",%"))
				.withDateTimeDecimal(BroBinaryFilter::getTs, BroBinary_.ts)
				.withIp(BroBinaryFilter::getIdOrigHost, BroBinary_.idOrigIp)
				.withIp(BroBinaryFilter::getIdRespHost, BroBinary_.idRespIp)
				.withNumber(BroBinaryFilter::getIdOrigPort, BroBinary_.idOrigPort)
				.withNumber(BroBinaryFilter::getIdRespPort, BroBinary_.idRespPort)
				.withString(BroBinaryFilter::getProtocol, BroBinary_.protocol)
		;
	}

	@RequestMapping(method = RequestMethod.POST)
	public Page<BroBinaryDto> findByFilter(
			@PageableDefault(size = 20) @SortDefault("ts") Pageable pageable,
			@RequestBody(required = false) BroBinaryFilter filter
	) {
		accountService.requireAnyRight("task.view");
		if (!taskPermitRepo.isAllowedTask(accountService.getCurrentAuditor(), ZonedDateTime.now(), filter.getTaskId())) {
			filter.setTaskId(-1L);
		}
		Specification<BroBinary> spec = specificationBuilder.build(filter);
		return repo.findAll(spec, pageable)
				.map(BroBinaryDto::new);
	}
}

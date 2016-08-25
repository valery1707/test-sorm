package name.valery1707.megatel.sorm.api.bro.binary;

import javaslang.control.Option;
import name.valery1707.core.app.AccountService;
import name.valery1707.core.db.MapSpecificationBuilder;
import name.valery1707.core.db.SpecificationBuilder;
import name.valery1707.core.db.SpecificationMode;
import name.valery1707.megatel.sorm.api.bro.files.BroFilesRepo;
import name.valery1707.megatel.sorm.api.bro.files.BroFilesService;
import name.valery1707.megatel.sorm.api.task.permit.TaskPermitRepo;
import name.valery1707.megatel.sorm.domain.BroBinary;
import name.valery1707.megatel.sorm.domain.BroBinary_;
import name.valery1707.megatel.sorm.domain.BroFiles;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

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

	private Option<BroBinary> findByExample(Map<String, String> filter) {
		Specification<BroBinary> specification = MapSpecificationBuilder.buildSpecification(filter);
		Page<BroBinary> all = repo.findAll(specification, new PageRequest(0, 1));
		if (all.getContent().isEmpty()) {
			return Option.none();
		}
		return Option.of(all.getContent().get(0));
	}

	@RequestMapping(method = RequestMethod.GET)
	public BroBinaryDto get(@RequestParam Map<String, String> dtoFields) {
		return findByExample(dtoFields)
				.map(BroBinaryDto::new)
				.getOrElseThrow(() -> new AccessDeniedException(String.format("Entity '%s' not found", "BroBinary")));
	}

	@Inject
	@SuppressWarnings("SpringJavaAutowiringInspection")
	private BroFilesRepo filesRepo;

	@Inject
	private BroFilesService filesService;

	@RequestMapping(path = "files", method = RequestMethod.GET)
	public List<BroFiles> files(@RequestParam Map<String, String> dtoFields) {
		return findByExample(dtoFields)
				.iterator()
				.filter(binary -> binary.getUid() != null)
				.map(BroBinary::getUid)
				.distinct()
				.flatMap(filesRepo::findByConnUid)
				.map(filesService::fixFilename)
				.toJavaList();
	}
}

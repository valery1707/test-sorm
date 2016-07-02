package name.valery1707.megatel.sorm.api.bro.smtp;

import javaslang.control.Option;
import name.valery1707.megatel.sorm.api.bro.files.BroFilesRepo;
import name.valery1707.megatel.sorm.api.bro.files.BroFilesService;
import name.valery1707.megatel.sorm.app.AccountService;
import name.valery1707.megatel.sorm.db.MapSpecificationBuilder;
import name.valery1707.megatel.sorm.db.SpecificationBuilder;
import name.valery1707.megatel.sorm.db.SpecificationMode;
import name.valery1707.megatel.sorm.domain.BroFiles;
import name.valery1707.megatel.sorm.domain.BroSmtp;
import name.valery1707.megatel.sorm.domain.BroSmtp_;
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
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bro/smtp")
public class BroSmtpController {
	@Inject
	private BroSmtpRepo repo;

	@Inject
	private AccountService accountService;

	private SpecificationBuilder<BroSmtp, BroSmtpFilter> specificationBuilder;

	@PostConstruct
	public void init() {
		specificationBuilder = new SpecificationBuilder<BroSmtp, BroSmtpFilter>(SpecificationMode.AND)
				.withDateTimeDecimal(BroSmtpFilter::getTs, BroSmtp_.ts)
				.withIp(BroSmtpFilter::getIdOrigHost, BroSmtp_.idOrigIp)
				.withIp(BroSmtpFilter::getIdRespHost, BroSmtp_.idRespIp)
				.withNumber(BroSmtpFilter::getIdOrigPort, BroSmtp_.idOrigPort)
				.withNumber(BroSmtpFilter::getIdRespPort, BroSmtp_.idRespPort)
				.withString(BroSmtpFilter::getFrom, BroSmtp_.from)
				.withString(BroSmtpFilter::getTo, BroSmtp_.to)
				.withString(BroSmtpFilter::getSubject, BroSmtp_.subject)
				.withString(BroSmtpFilter::getUserAgent, BroSmtp_.userAgent)
				.withString(BroSmtpFilter::getFuids, BroSmtp_.fuids)
		;
	}

	@RequestMapping(method = RequestMethod.POST)
	public Page<BroSmtpDto> findByFilter(
			@PageableDefault(size = 20) @SortDefault("ts") Pageable pageable,
			@RequestBody(required = false) BroSmtpFilter filter
	) {
		accountService.requireAnyRight("task.view");
		//todo Фильтрация по выбранной задаче
		//todo Исправить сортировку по полю `isWebmail`
		Specification<BroSmtp> spec = specificationBuilder.build(filter);
		return repo.findAll(spec, pageable)
				.map(BroSmtpDto::new);
	}

	private Option<BroSmtp> findByExample(Map<String, String> filter) {
		Specification<BroSmtp> specification = MapSpecificationBuilder.buildSpecification(filter);
		Page<BroSmtp> all = repo.findAll(specification, new PageRequest(0, 1));
		if (all.getContent().isEmpty()) {
			return Option.none();
		}
		return Option.of(all.getContent().get(0));
	}

	@RequestMapping(method = RequestMethod.GET)
	public BroSmtpDto get(@RequestParam Map<String, String> dtoFields) {
		return findByExample(dtoFields)
				.map(BroSmtpDto::new)
				.getOrElseThrow(() -> new AccessDeniedException(String.format("Entity '%s' not found", "BroSmtp")));
	}

	@Inject
	private BroFilesRepo filesRepo;

	@Inject
	private BroFilesService filesService;

	@RequestMapping(path = "files", method = RequestMethod.GET)
	public List<BroFiles> files(@RequestParam Map<String, String> dtoFields) {
		return findByExample(dtoFields)
				.iterator()
				.filter(smtp -> smtp.getFuids() != null)
				.map(BroSmtp::getFuids)
				.flatMap(s -> Arrays.asList(s.split(",")))
				.distinct()
				.flatMap(filesRepo::findByFuid)
				.map(filesService::fixFilename)
				.toJavaList();
	}
}

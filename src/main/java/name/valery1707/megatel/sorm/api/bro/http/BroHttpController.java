package name.valery1707.megatel.sorm.api.bro.http;

import javaslang.collection.Stream;
import javaslang.control.Option;
import name.valery1707.megatel.sorm.api.bro.files.BroFilesRepo;
import name.valery1707.megatel.sorm.api.bro.files.BroFilesService;
import name.valery1707.megatel.sorm.api.task.permit.TaskPermitRepo;
import name.valery1707.megatel.sorm.app.AccountService;
import name.valery1707.megatel.sorm.db.MapSpecificationBuilder;
import name.valery1707.megatel.sorm.db.SpecificationBuilder;
import name.valery1707.megatel.sorm.db.SpecificationMode;
import name.valery1707.megatel.sorm.domain.BroFiles;
import name.valery1707.megatel.sorm.domain.BroHttp;
import name.valery1707.megatel.sorm.domain.BroHttp_;
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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/bro/http")
public class BroHttpController {
	@Inject
	private BroHttpRepo repo;

	@Inject
	private TaskPermitRepo taskPermitRepo;

	@Inject
	private AccountService accountService;

	private SpecificationBuilder<BroHttp, BroHttpFilter> specificationBuilder;

	@PostConstruct
	public void init() {
		specificationBuilder = new SpecificationBuilder<BroHttp, BroHttpFilter>(SpecificationMode.AND)
				.withCustom(BroHttpFilter::getTaskId, BroHttp_.amtTasksList, cb -> (field, id) -> cb.like(field, "%," + id + ",%"))
				.withDateTimeDecimal(BroHttpFilter::getTs, BroHttp_.ts)
				.withIp(BroHttpFilter::getIdOrigHost, BroHttp_.idOrigIp)
				.withIp(BroHttpFilter::getIdRespHost, BroHttp_.idRespIp)
				.withNumber(BroHttpFilter::getIdOrigPort, BroHttp_.idOrigPort)
				.withNumber(BroHttpFilter::getIdRespPort, BroHttp_.idRespPort)
				.withString(BroHttpFilter::getMethod, BroHttp_.method)
				.withString(BroHttpFilter::getHost, BroHttp_.host)
				.withString(BroHttpFilter::getUri, BroHttp_.uri)
				.withString(BroHttpFilter::getReferrer, BroHttp_.referrer)
				.withString(BroHttpFilter::getUserAgent, BroHttp_.userAgent)
				.withNumber(BroHttpFilter::getRequestBodyLen, BroHttp_.requestBodyLen)
				.withNumber(BroHttpFilter::getResponseBodyLen, BroHttp_.responseBodyLen)
				.withNumber(BroHttpFilter::getStatusCode, BroHttp_.statusCode)
				.withCustom(BroHttpFilter::getHasFiles, BroHttp_.respFuids, cb -> (field, hasFiles) -> hasFiles ? cb.isNotNull(field) : cb.isNull(field))
		;
	}

	@RequestMapping(method = RequestMethod.POST)
	public Page<BroHttpDto> findByFilter(
			@PageableDefault(size = 20) @SortDefault("ts") Pageable pageable,
			@RequestBody(required = false) BroHttpFilter filter
	) {
		accountService.requireAnyRight("task.view");
		if (!taskPermitRepo.isAllowedTask(accountService.getCurrentAuditor(), ZonedDateTime.now(), filter.getTaskId())) {
			filter.setTaskId(-1L);
		}
		Specification<BroHttp> spec = specificationBuilder.build(filter);
		return repo.findAll(spec, pageable)
				.map(BroHttpDto::new);
	}

	private Option<BroHttp> findByExample(Map<String, String> filter) {
		Specification<BroHttp> specification = MapSpecificationBuilder.buildSpecification(filter);
		Page<BroHttp> all = repo.findAll(specification, new PageRequest(0, 1));
		if (all.getContent().isEmpty()) {
			return Option.none();
		}
		return Option.of(all.getContent().get(0));
	}

	@RequestMapping(method = RequestMethod.GET)
	public BroHttpDto get(@RequestParam Map<String, String> dtoFields) {
		return findByExample(dtoFields)
				.map(BroHttpDto::new)
				.getOrElseThrow(() -> new AccessDeniedException(String.format("Entity '%s' not found", "BroHttp")));
	}

	@Inject
	private BroFilesRepo filesRepo;

	@Inject
	private BroFilesService filesService;

	@RequestMapping(path = "files", method = RequestMethod.GET)
	public List<BroFiles> files(@RequestParam Map<String, String> dtoFields) {
		return findByExample(dtoFields)
				.iterator()
				.filter(http -> http.getOrigFuids() != null || http.getRespFuids() != null)
				.map(http -> Stream.of(http.getOrigFuids(), http.getRespFuids()).filter(Objects::nonNull).mkString(","))
				.flatMap(s -> Arrays.asList(s.split(",")))
				.distinct()
				.flatMap(filesRepo::findByFuid)
				.map(filesService::fixFilename)
				.toJavaList();
	}
}

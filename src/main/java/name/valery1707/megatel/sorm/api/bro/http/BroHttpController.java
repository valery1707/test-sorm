package name.valery1707.megatel.sorm.api.bro.http;

import javaslang.collection.Stream;
import javaslang.control.Option;
import name.valery1707.megatel.sorm.api.bro.files.BroFilesRepo;
import name.valery1707.megatel.sorm.app.AccountService;
import name.valery1707.megatel.sorm.db.MapSpecificationBuilder;
import name.valery1707.megatel.sorm.db.SpecificationBuilder;
import name.valery1707.megatel.sorm.db.SpecificationMode;
import name.valery1707.megatel.sorm.domain.BroFiles;
import name.valery1707.megatel.sorm.domain.BroHttp;
import name.valery1707.megatel.sorm.domain.BroHttp_;
import org.springframework.data.domain.Page;
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
import java.util.Objects;

@RestController
@RequestMapping("/api/bro/http")
public class BroHttpController {
	@Inject
	private BroHttpRepo repo;

	@Inject
	private AccountService accountService;

	private SpecificationBuilder<BroHttp, BroHttpFilter> specificationBuilder;

	@PostConstruct
	public void init() {
		specificationBuilder = new SpecificationBuilder<BroHttp, BroHttpFilter>(SpecificationMode.AND)
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
		//todo Фильтрация по выбранной задаче
		Specification<BroHttp> spec = specificationBuilder.build(filter);
		return repo.findAll(spec, pageable)
				.map(BroHttpDto::new);
	}

	private Option<BroHttp> findByExample(Map<String, String> filter) {
		Specification<BroHttp> specification = MapSpecificationBuilder.buildSpecification(filter);
		java.util.List<BroHttp> all = repo.findAll(specification);
		if (all.isEmpty()) {
			return Option.none();
		}
		return Option.of(all.get(0));
	}

	@RequestMapping(path = "temp", method = RequestMethod.GET)
	public BroHttpDto get(@RequestParam Map<String, String> dtoFields) {
		return findByExample(dtoFields)
				.map(BroHttpDto::new)
				.getOrElseThrow(() -> new AccessDeniedException(String.format("Entity '%s' not found", "BroHttp")));
	}

	@Inject
	private BroFilesRepo filesRepo;

	@RequestMapping(path = "", method = RequestMethod.GET)
	public List<BroFiles> files(@RequestParam Map<String, String> dtoFields) {
		return findByExample(dtoFields)
				.iterator()
				.filter(http -> http.getOrigFuids() != null || http.getRespFuids() != null)
				.map(http -> Stream.of(http.getOrigFuids(), http.getRespFuids()).filter(Objects::nonNull).mkString(","))
				.flatMap(s -> Arrays.asList(s.split(",")))
				.distinct()
				.map(filesRepo::getByFuid)
				.toJavaList();
	}
}

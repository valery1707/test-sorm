package name.valery1707.megatel.sorm.api.bro.http;

import name.valery1707.megatel.sorm.app.AccountService;
import name.valery1707.megatel.sorm.db.SpecificationBuilder;
import name.valery1707.megatel.sorm.db.SpecificationMode;
import name.valery1707.megatel.sorm.domain.BroHttp;
import name.valery1707.megatel.sorm.domain.BroHttp_;
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
}

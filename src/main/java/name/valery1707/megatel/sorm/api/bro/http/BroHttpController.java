package name.valery1707.megatel.sorm.api.bro.http;

import name.valery1707.megatel.sorm.api.auth.AccountService;
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
				.withDateTimeDecimal(BroHttp_.ts, BroHttpFilter::getTs)
				.withIp(BroHttp_.idOrigIp, BroHttpFilter::getIdOrigHost)
				.withIp(BroHttp_.idRespIp, BroHttpFilter::getIdRespHost)
				.withNumber(BroHttp_.idOrigPort, BroHttpFilter::getIdOrigPort)
				.withNumber(BroHttp_.idRespPort, BroHttpFilter::getIdRespPort)
				.withString(BroHttp_.method, BroHttpFilter::getMethod)
				.withString(BroHttp_.host, BroHttpFilter::getHost)
				.withString(BroHttp_.uri, BroHttpFilter::getUri)
				.withString(BroHttp_.referrer, BroHttpFilter::getReferrer)
				.withString(BroHttp_.userAgent, BroHttpFilter::getUserAgent)
				.withNumber(BroHttp_.requestBodyLen, BroHttpFilter::getRequestBodyLen)
				.withNumber(BroHttp_.responseBodyLen, BroHttpFilter::getResponseBodyLen)
				.withNumber(BroHttp_.statusCode, BroHttpFilter::getStatusCode)
		;
	}

	@RequestMapping(method = RequestMethod.POST)
	public Page<BroHttpDto> findByFilter(
			@PageableDefault(size = 20) @SortDefault("ts") Pageable pageable,
			@RequestBody(required = false) BroHttpFilter filter
	) {
		accountService.requireAnyRight("task.view");
		Specification<BroHttp> spec = specificationBuilder.build(filter);
		return repo.findAll(spec, pageable)
				.map(BroHttpDto::new);
	}
}

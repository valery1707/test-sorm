package name.valery1707.megatel.sorm.api.http;

import name.valery1707.megatel.sorm.db.SpecificationBuilder;
import name.valery1707.megatel.sorm.db.SpecificationMode;
import name.valery1707.megatel.sorm.domain.Http;
import name.valery1707.megatel.sorm.domain.Http_;
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
@RequestMapping("/api/http")
public class HttpController {
	@Inject
	private HttpRepo repo;

	private SpecificationBuilder<Http, HttpFilter> specificationBuilder;

	@PostConstruct
	public void init() {
		specificationBuilder = new SpecificationBuilder<Http, HttpFilter>(SpecificationMode.AND)
				.withDateTimeDecimal(Http_.ts, HttpFilter::getTs)
				.withIp(Http_.idOrigIp, HttpFilter::getIdOrigHost)
				.withIp(Http_.idRespIp, HttpFilter::getIdRespHost)
				.withNumber(Http_.idOrigPort, HttpFilter::getIdOrigPort)
				.withNumber(Http_.idRespPort, HttpFilter::getIdRespPort)
				.withString(Http_.method, HttpFilter::getMethod)
				.withString(Http_.host, HttpFilter::getHost)
				.withString(Http_.uri, HttpFilter::getUri)
				.withString(Http_.referrer, HttpFilter::getReferrer)
				.withString(Http_.userAgent, HttpFilter::getUserAgent)
				.withNumber(Http_.requestBodyLen, HttpFilter::getRequestBodyLen)
				.withNumber(Http_.responseBodyLen, HttpFilter::getResponseBodyLen)
				.withNumber(Http_.statusCode, HttpFilter::getStatusCode)
		;
	}

	@RequestMapping(method = RequestMethod.POST)
	public Page<HttpDto> findByFilter(
			@PageableDefault(size = 20) @SortDefault("ts") Pageable pageable,
			@RequestBody(required = false) HttpFilter filter
	) {
		Specification<Http> spec = specificationBuilder.build(filter);
		return repo.findAll(spec, pageable)
				.map(HttpDto::new);
	}
}
